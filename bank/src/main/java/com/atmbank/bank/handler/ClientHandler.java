package com.atmbank.bank.handler;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.atmbank.bank.model.Account;
import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.bank.security.SecurityContext;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;
import com.atmbank.common.message.request.CreateAccountRequest;
import com.atmbank.common.message.request.DepositRequest;
import com.atmbank.common.message.request.GetBalanceRequest;
import com.atmbank.common.message.request.WithdrawRequest;
import com.atmbank.common.message.response.AuthenticateResponse;
import com.atmbank.common.message.response.CreateAccountResponse;
import com.atmbank.common.message.response.DepositResponse;
import com.atmbank.common.message.response.ErrorResponse;
import com.atmbank.common.message.response.GetBalanceResponse;
import com.atmbank.common.message.response.ResponseStatus;
import com.atmbank.common.message.response.WithdrawResponse;

public class ClientHandler {
    private static final Logger logger = new ConsoleLogger(); // TODO: Change to NullLogger before delivery

    private final Socket clientSocket;
    private final AccountRepository accountRepository;
    private final SecurityContext securityContext;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket, AccountRepository accountRepository, SecurityContext securityContext) {
        this.clientSocket = socket;
        this.accountRepository = accountRepository;
        this.securityContext = securityContext;
    }

    public void handle() {
        try {
            setupStreams();
            handleHandshake();
            handleClientRequests();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error handling client: %s", e);
        } finally {
            closeConnection();
        }
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void handleHandshake() {
        // TODO: Implement handshake logic
    }

    private void handleClientRequests() throws IOException, ClassNotFoundException {
        Message request;
        boolean authenticated = false;

        try {
            while ((request = (Message) in.readObject()) != null) {
                if (request.getType() == MessageType.AUTHENTICATE) {
                    authenticated = true;
                    sendResponse(new AuthenticateResponse(true));
                } else if (request.getType() == MessageType.CREATE_ACCOUNT) {
                    handleCreateAccountRequest((CreateAccountRequest) request);
                } else if (authenticated) {
                    handleAuthenticatedRequest(request);
                } else {
                    sendResponse(new ErrorResponse("AUTHENTICATION_REQUIRED"));
                }
            }
        } catch (EOFException e) {
            logger.info("Client disconnected %s", getConnectionIdentifier());
        }
    }

    private void handleAuthenticatedRequest(Message request) throws IOException {
        switch (request.getType()) {
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
                logger.info("%s sent unknown request: %s", getConnectionIdentifier(), request);
                break;
        }
    }

    private void handleCreateAccountRequest(CreateAccountRequest request) throws IOException {
        String accountNumber = request.getAccountNumber();
        double initialBalance = request.getInitialBalance();

        if (initialBalance < 0) {
            sendResponse(new CreateAccountResponse(ResponseStatus.NEGATIVE_AMOUNT));
            logger.info("%s tried to create account with negative balance: %s", getConnectionIdentifier(), request);
            return;
        }

        Account existingAccount = accountRepository.findById(accountNumber);
        if (existingAccount != null) {
            sendResponse(new CreateAccountResponse(ResponseStatus.ACCOUNT_EXISTS));
            logger.info("%s tried to create existing account: %s", getConnectionIdentifier(), request);
            return;
        }

        Account newAccount = new Account(accountNumber, initialBalance);
        accountRepository.save(newAccount);
        sendResponse(new CreateAccountResponse(ResponseStatus.SUCCESS));
        logger.info("%s created a new account: %s", getConnectionIdentifier(), request);
    }

    private void handleGetBalanceRequest(GetBalanceRequest request) throws IOException {
        String accountNumber = request.getAccountNumber();
        Account account = accountRepository.findById(accountNumber);

        if (account == null) {
            sendResponse(new GetBalanceResponse(ResponseStatus.ACCOUNT_NOT_FOUND));
            logger.info("%s tried to get balance for nonexistent account: %s", getConnectionIdentifier(), request);
            return;
        }

        sendResponse(new GetBalanceResponse(account.getBalance()));
        logger.info("%s checked balance for account: %s", getConnectionIdentifier(), request);
    }

    private void handleWithdrawRequest(WithdrawRequest request) throws IOException {
        String accountNumber = request.getAccountNumber();
        double amount = request.getAmount();

        if (amount <= 0) {
            sendResponse(new WithdrawResponse(ResponseStatus.NEGATIVE_OR_ZERO_AMOUNT));
            logger.info("%s tried to withdraw an invalid amount from account: %s", getConnectionIdentifier(), request);
            return;
        }

        Account account = accountRepository.findById(accountNumber);
        if (account == null) {
            sendResponse(new WithdrawResponse(ResponseStatus.ACCOUNT_NOT_FOUND));
            logger.info("%s tried to withdraw from nonexistent account: %s", getConnectionIdentifier(), request);
            return;
        }

        try {
            account.withdraw(amount);
            accountRepository.updateBalance(accountNumber, account.getBalance());
            sendResponse(new WithdrawResponse(ResponseStatus.SUCCESS));
            logger.info("%s withdrawn from account: %s", getConnectionIdentifier(), request);
        } catch (IllegalArgumentException e) {
            sendResponse(new WithdrawResponse(ResponseStatus.INSUFFICIENT_BALANCE));
            logger.info("%s tried to withdraw from account with insufficient balance: %s", getConnectionIdentifier(),
                    request);
        }
    }

    private void handleDepositRequest(DepositRequest request) throws IOException {
        String accountNumber = request.getAccountNumber();
        double amount = request.getAmount();

        if (amount <= 0) {
            sendResponse(new DepositResponse(ResponseStatus.NEGATIVE_OR_ZERO_AMOUNT));
            logger.info("%s tried to deposit an invalid amount to account: %s", getConnectionIdentifier(), request);
            return;
        }

        Account account = accountRepository.findById(accountNumber);
        if (account == null) {
            sendResponse(new DepositResponse(ResponseStatus.ACCOUNT_NOT_FOUND));
            logger.info("%s tried to deposit to nonexistent account: %s", getConnectionIdentifier(), request);
            return;
        }

        try {
            account.deposit(amount);
            accountRepository.updateBalance(accountNumber, account.getBalance());
            sendResponse(new DepositResponse(ResponseStatus.SUCCESS));
            logger.info("%s deposited to account: %s", getConnectionIdentifier(), request);
        } catch (IllegalArgumentException e) {
            sendResponse(new DepositResponse(ResponseStatus.ERROR));
            logger.info("%s tried to deposit an invalid amount to account: %s", getConnectionIdentifier(), request);
        }
    }

    private void sendResponse(Message response) throws IOException {
        out.writeObject(response);
        out.flush();
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
                logger.info("Closed connection with client: %s", clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            logger.error("Error closing client connection: %s", e.getMessage());
        }
    }
}
