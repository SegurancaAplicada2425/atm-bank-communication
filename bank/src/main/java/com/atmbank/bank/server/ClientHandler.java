package com.atmbank.bank.server;

import com.atmbank.bank.model.Account;
import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.bank.security.SecurityContext;
import com.atmbank.bank.security.protocol.ProtocolHandler;
import com.atmbank.common.config.Constants;
import com.atmbank.common.display.ConsoleDisplay;
import com.atmbank.common.display.Display;
import com.atmbank.common.logger.ConditionalLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.message.request.*;
import com.atmbank.common.message.response.*;
import com.atmbank.common.security.protocol.ProtocolException;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private static final Display display = new ConsoleDisplay();
    private static final Logger debugLogger = new ConditionalLogger(Constants.DEBUG_MODE);

    private final Socket clientSocket;
    private final AccountRepository accountRepository;
    private final ProtocolHandler protocolHandler;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, AccountRepository accountRepository, SecurityContext securityContext) throws Exception {
        this.clientSocket = socket;
        this.accountRepository = accountRepository;
        this.protocolHandler = new ProtocolHandler(securityContext);
    }

    public void handle() {
        try {
            setupStreams();
            performHandshake();
            handleRequests();
        } catch (SocketTimeoutException e) {
            display.display("protocol_error");
        } catch (ProtocolException e) {
            display.display("protocol_error");
            debugLogger.error("Protocol error: %s", e.getMessage());
        } catch (Exception e) {
            debugLogger.error("Unexpected error: %s", e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void setupStreams() throws IOException {
        clientSocket.setSoTimeout(Constants.CONNECTION_TIMEOUT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    private void performHandshake() throws ProtocolException, IOException {
        try {
            String handshakeRequest = in.readLine();
            if (handshakeRequest == null) {
                throw new IOException("Handshake request is null");
            }
            protocolHandler.processHandshakeRequest(handshakeRequest);

            String serverExchangeMessage = protocolHandler.getServerExchangeMessage();
            out.println(serverExchangeMessage);

            String clientExchangeMessage = in.readLine();
            if (clientExchangeMessage == null) {
                throw new IOException("Client exchange message is null");
            }
            protocolHandler.processClientExchangeMessage(clientExchangeMessage);

            String handshakeResponse = protocolHandler.getHandshakeResponse();
            out.println(handshakeResponse);

            debugLogger.info("Handshake completed with client: %s", getConnectionIdentifier());
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ProtocolException("Failed to perform handshake: " + e.getMessage(), e);
        }
    }

    private void handleRequests() throws IOException, ProtocolException {
        try {
            Request request;
            while ((request = receiveRequest()) != null) {
                handleRequest(request);
            }
        } catch (EOFException e) {
            debugLogger.info("Client disconnected %s", getConnectionIdentifier());
        }
    }

    private void handleRequest(Request request) throws ProtocolException {
        switch (request.getType()) {
            case CREATE_ACCOUNT:
                handleCreateAccountRequest((CreateAccountRequest) request);
                break;
            case DEPOSIT:
                handleDepositRequest((DepositRequest) request);
                break;
            case WITHDRAW:
                handleWithdrawRequest((WithdrawRequest) request);
                break;
            case GET_BALANCE:
                handleGetBalanceRequest((GetBalanceRequest) request);
                break;
            default:
                sendResponse(new ErrorResponse("UNKNOWN_REQUEST"));
                debugLogger.info("%s sent unknown request: %s", getConnectionIdentifier(), request);
                break;
        }
    }

    private void handleCreateAccountRequest(CreateAccountRequest request) throws ProtocolException {
        String accountNumber = request.getAccountNumber();
        String accountPin = request.getAccountPin();
        double initialBalance = request.getInitialBalance();

        try {
            boolean success = accountRepository.createAccount(accountNumber, accountPin, initialBalance);
            if (!success) {
                sendResponse(new CreateAccountResponse(ResponseStatus.ERROR));
                debugLogger.info("Account creation failed for account: %s", accountNumber);
                return;
            }
            display.display("{\"account:\"%s\",\"initial_balance\":%.2f}", accountNumber, initialBalance);
            sendResponse(new CreateAccountResponse(ResponseStatus.SUCCESS));
        } catch (IllegalArgumentException e) {
            sendResponse(new CreateAccountResponse(ResponseStatus.INVALID_AMOUNT));
            debugLogger.info("%s tried to create an invalid account: %s", getConnectionIdentifier(), request);
        }
    }

    private void handleDepositRequest(DepositRequest request) throws ProtocolException {
        String accountNumber = request.getAccountNumber();
        String accountPin = request.getAccountPin();
        double amount = request.getAmount();

        try {
            boolean success = accountRepository.deposit(accountNumber, accountPin, amount);
            if (!success) {
                sendResponse(new DepositResponse(ResponseStatus.ERROR));
                debugLogger.info("Deposit failed for account: %s", accountNumber);
                return;
            }
            display.display("{\"account:\"%s\",\"deposit\":%.2f}", accountNumber, amount);
            sendResponse(new DepositResponse(ResponseStatus.SUCCESS));
        } catch (IllegalArgumentException e) {
            sendResponse(new DepositResponse(ResponseStatus.INVALID_AMOUNT));
            debugLogger.info("%s tried to deposit an invalid amount to account: %s", getConnectionIdentifier(), request);
        }
    }

    private void handleWithdrawRequest(WithdrawRequest request) throws ProtocolException {
        String accountNumber = request.getAccountNumber();
        String accountPin = request.getAccountPin();
        double amount = request.getAmount();

        try {
            boolean success = accountRepository.withdraw(accountNumber, accountPin, amount);
            if (!success) {
                sendResponse(new WithdrawResponse(ResponseStatus.ERROR));
                debugLogger.info("Withdraw failed for account: %s", accountNumber);
                return;
            }
            display.display("{\"account:\"%s\",\"withdraw\":%.2f}", accountNumber, amount);
            sendResponse(new WithdrawResponse(ResponseStatus.SUCCESS));
        } catch (IllegalArgumentException e) {
            sendResponse(new WithdrawResponse(ResponseStatus.INVALID_AMOUNT));
            debugLogger.info("%s tried to withdraw an invalid amount from account: %s", getConnectionIdentifier(), request);
        }
    }

    private void handleGetBalanceRequest(GetBalanceRequest request) throws ProtocolException {
        String accountNumber = request.getAccountNumber();
        String accountPin = request.getAccountPin();

        Account account = accountRepository.getAccount(accountNumber, accountPin);
        if (account == null) {
            sendResponse(new GetBalanceResponse(ResponseStatus.ACCOUNT_NOT_FOUND));
            debugLogger.info("%s tried to get balance for an invalid account: %s", getConnectionIdentifier(), request);
            return;
        }

        display.display("{\"account:\"%s\",\"balance\":%.2f}", accountNumber, account.getBalance());
        sendResponse(new GetBalanceResponse(account.getBalance()));
    }

    private Request receiveRequest() throws IOException, ProtocolException {
        String requestStr = in.readLine();
        if (requestStr == null) {
            throw new EOFException("End of stream reached");
        }
        return protocolHandler.processSessionRequest(requestStr);
    }

    private void sendResponse(Response response) throws ProtocolException {
        String responseStr = protocolHandler.getSessionResponse(response);
        out.println(responseStr);
    }

    private String getConnectionIdentifier() {
        return String.format("%s:%d", clientSocket.getInetAddress(), clientSocket.getPort());
    }

    private void closeConnection() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                debugLogger.info("Closed connection with client: %s", clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            debugLogger.error("Error closing client connection: %s", e.getMessage());
        }
    }
}
