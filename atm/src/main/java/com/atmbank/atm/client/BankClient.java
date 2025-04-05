package com.atmbank.atm.client;

import com.atmbank.atm.security.SecurityContext;
import com.atmbank.atm.security.protocol.ProtocolHandler;
import com.atmbank.common.config.Constants;
import com.atmbank.common.display.ConsoleDisplay;
import com.atmbank.common.display.Display;
import com.atmbank.common.logger.ConditionalLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.message.request.*;
import com.atmbank.common.message.response.*;
import com.atmbank.common.security.protocol.ProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BankClient {
    private static final Display display = new ConsoleDisplay();
    private static final Logger debugLogger = new ConditionalLogger(Constants.DEBUG_MODE);

    private final String serverAddress;
    private final int serverPort;
    private final ProtocolHandler protocolHandler;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected;

    public BankClient(String serverAddress, int serverPort, SecurityContext securityContext) throws Exception {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.protocolHandler = new ProtocolHandler(securityContext);
        this.connected = false;
    }

    public void connect() throws Exception {
        try {
            socket = new Socket(serverAddress, serverPort);
            setupStreams();
            connected = true;

            performHandshake();
        } catch (Exception e) {
            disconnect();
            throw new ProtocolException("Failed to connect to server: " + e.getMessage(), e);
        }
    }

    private void setupStreams() throws IOException {
        socket.setSoTimeout(Constants.CONNECTION_TIMEOUT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void performHandshake() throws ProtocolException, IOException {
        try {
            String handshakeRequest = protocolHandler.getHandshakeRequest();
            out.println(handshakeRequest);

            String serverExchangeMessage = in.readLine();
            if (serverExchangeMessage == null) {
                throw new IOException("Server closed the connection during handshake");
            }
            protocolHandler.processServerExchangeMessage(serverExchangeMessage);

            String clientExchangeMessage = protocolHandler.getClientExchangeMessage();
            out.println(clientExchangeMessage);

            String handshakeResponse = in.readLine();
            if (handshakeResponse == null) {
                throw new IOException("Server closed the connection during handshake");
            }
            protocolHandler.processHandshakeResponse(handshakeResponse);

            debugLogger.info("Handshake completed with server: %s", socket.getRemoteSocketAddress());
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ProtocolException("Failed to perform handshake: " + e.getMessage(), e);
        }
    }

    public void disconnect() {
        connected = false;

        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            debugLogger.error("Error closing connection: %s", e.getMessage());
        }
    }

    public void createAccount(String accountNumber, String accountPin, double initialBalance) throws IOException, ProtocolException {
        checkConnection();

        sendRequest(new CreateAccountRequest(accountNumber, accountPin, initialBalance));
        Response response = receiveResponse();

        if (response instanceof CreateAccountResponse createAccountResponse) {
            if (createAccountResponse.isSuccess()) {
                display.display("{\"account:\"%s\",\"initial_balance\":%.2f}", accountNumber, initialBalance);
            } else {
                debugLogger.error("Account creation failed: %s", createAccountResponse.getStatus());
            }
        } else {
            debugLogger.error("Unexpected response type");
        }
    }

    public void deposit(String accountNumber, String accountPin, double amount) throws IOException, ProtocolException {
        checkConnection();

        sendRequest(new DepositRequest(accountNumber, accountPin, amount));
        Response response = receiveResponse();

        if (response instanceof DepositResponse depositResponse) {
            if (depositResponse.isSuccess()) {
                display.display("{\"account:\"%s\",\"deposit\":%.2f}", accountNumber, amount);
            } else {
                debugLogger.error("Deposit failed: %s", depositResponse.getStatus());
            }
        } else {
            debugLogger.error("Unexpected response type");
        }
    }

    public void withdraw(String accountNumber, String accountPin, double amount) throws IOException, ProtocolException {
        checkConnection();

        sendRequest(new WithdrawRequest(accountNumber, accountPin, amount));
        Response response = receiveResponse();

        if (response instanceof WithdrawResponse withdrawResponse) {
            if (withdrawResponse.isSuccess()) {
                display.display("{\"account:\"%s\",\"withdraw\":%.2f}", accountNumber, amount);
            } else {
                debugLogger.error("Withdrawal failed: %s", withdrawResponse.getStatus());
            }
        } else {
            debugLogger.error("Unexpected response type");
        }
    }

    public void displayBalance(String accountNumber, String accountPin) throws IOException, ProtocolException {
        checkConnection();

        sendRequest(new GetBalanceRequest(accountNumber, accountPin));
        Response response = receiveResponse();

        if (response instanceof GetBalanceResponse balanceResponse) {
            if (balanceResponse.isSuccess()) {
                display.display("{\"account:\"%s\",\"balance\":%.2f}", accountNumber, balanceResponse.getBalance());
            } else {
                debugLogger.error("Balance retrieval failed: %s", balanceResponse.getStatus());
            }
        } else {
            debugLogger.error("Unexpected response type");
        }
    }

    private void sendRequest(Request request) throws ProtocolException {
        String requestStr = protocolHandler.getSessionRequest(request);
        out.println(requestStr);
    }

    private Response receiveResponse() throws IOException, ProtocolException {
        String responseStr = in.readLine();
        if (responseStr == null) {
            throw new IOException("Server closed the connection");
        }
        return protocolHandler.processSessionResponse(responseStr);
    }

    private void checkConnection() throws IOException {
        if (!connected) {
            throw new IOException("Not connected to the bank server");
        }
    }
}
