package com.atmbank.atm.client;

import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.message.Message;
import com.atmbank.common.message.request.*;
import com.atmbank.common.message.response.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BankClient {
    private static final Logger LOGGER = new ConsoleLogger();

    private final String serverAddress;
    private final int serverPort;
    private final String authFile;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected;
    private boolean authenticated;

    public BankClient(String serverAddress, int serverPort, String authFile) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.authFile = authFile;
        this.connected = false;
        this.authenticated = false;
    }

    public void connect() throws IOException {
        try {
            socket = new Socket(serverAddress, serverPort);
            setupStreams();
            connected = true;

            authenticate();
        } catch (IOException | ClassNotFoundException e) {
            disconnect();
            throw new IOException("Failed to connect to the bank server: " + e.getMessage(), e);
        }
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void authenticate() throws IOException, ClassNotFoundException {
        sendMessage(new AuthenticateRequest("client"));
        Message response = receiveResponse();

        if (response instanceof AuthenticateResponse authenticateResponse && authenticateResponse.isAuthenticated()) {
            authenticated = true;
        } else {
            throw new IOException("Authentication failed");
        }
    }

    public void disconnect() {
        connected = false;
        authenticated = false;

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
            LOGGER.error("Error closing connection: %s", e.getMessage());
        }
    }

    public void createAccount(String accountNumber, double initialBalance) throws IOException, ClassNotFoundException {
        checkConnectionAndAuthentication();

        sendMessage(new CreateAccountRequest(accountNumber, initialBalance));
        Message response = receiveResponse();

        if (response instanceof CreateAccountResponse createAccountResponse) {
            if (createAccountResponse.isSuccess()) {
                LOGGER.info("Account creation successful");
            } else {
                LOGGER.error("Account creation failed: %s", createAccountResponse.getStatus());
            }
        } else {
            LOGGER.error("Unexpected response type");
        }
    }

    public void deposit(String accountNumber, double amount) throws IOException, ClassNotFoundException {
        checkConnectionAndAuthentication();

        sendMessage(new DepositRequest(accountNumber, amount));
        Message response = receiveResponse();

        if (response instanceof DepositResponse depositResponse) {
            if (depositResponse.isSuccess()) {
                LOGGER.info("Deposit successful");
            } else {
                LOGGER.error("Deposit failed: %s", depositResponse.getStatus());
            }
        } else {
            LOGGER.error("Unexpected response type");
        }
    }

    public void withdraw(String accountNumber, double amount) throws IOException, ClassNotFoundException {
        checkConnectionAndAuthentication();

        sendMessage(new WithdrawRequest(accountNumber, amount));
        Message response = receiveResponse();

        if (response instanceof WithdrawResponse withdrawResponse) {
            if (withdrawResponse.isSuccess()) {
                LOGGER.info("Withdrawal successful");
            } else {
                LOGGER.error("Withdrawal failed: %s", withdrawResponse.getStatus());
            }
        } else {
            LOGGER.error("Unexpected response type");
        }
    }

    public double getBalance(String accountNumber) throws IOException, ClassNotFoundException {
        checkConnectionAndAuthentication();

        sendMessage(new GetBalanceRequest(accountNumber));
        Message response = receiveResponse();

        if (response instanceof GetBalanceResponse balanceResponse) {
            if (!balanceResponse.isSuccess()) {
                LOGGER.error("Balance inquiry failed: %s", balanceResponse.getStatus());
                return 0.0;
            }
            Double balance = balanceResponse.getBalance();
            if (balance != null) {
                return balance;
            }
        }

        LOGGER.error("Invalid balance response");
        return 0.0;
    }

    private void checkConnectionAndAuthentication() throws IOException {
        if (!connected) {
            throw new IOException("Not connected to the bank server");
        }

        if (!authenticated) {
            throw new IOException("Not authenticated with the bank server");
        }
    }

    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    private Message receiveResponse() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }
}
