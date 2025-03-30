package com.atmbank.atm.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.atmbank.atm.security.SecurityContext;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.message.Message;
import com.atmbank.common.message.request.AuthenticateRequest;
import com.atmbank.common.message.request.CreateAccountRequest;
import com.atmbank.common.message.request.DepositRequest;
import com.atmbank.common.message.request.GetBalanceRequest;
import com.atmbank.common.message.request.WithdrawRequest;
import com.atmbank.common.message.response.AuthenticateResponse;
import com.atmbank.common.message.response.CreateAccountResponse;
import com.atmbank.common.message.response.DepositResponse;
import com.atmbank.common.message.response.GetBalanceResponse;
import com.atmbank.common.message.response.WithdrawResponse;

public class BankClient {
    private static final Logger logger = new ConsoleLogger(); // TODO: Change to NullLogger before delivery

    private final String serverAddress;
    private final int serverPort;
    private final SecurityContext securityContext;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected;
    private boolean authenticated;

    public BankClient(String serverAddress, int serverPort, String authFile, SecurityContext securityContext) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.securityContext = securityContext;
        this.connected = false;
        this.authenticated = false;
    }

    public void connect() throws IOException {
        try {
            socket = new Socket(serverAddress, serverPort);
            setupStreams();
            connected = true;

            performHandshake();
        } catch (IOException e) {
            disconnect();
            throw new IOException("Failed to connect to the bank server: " + e.getMessage(), e);
        }
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void performHandshake() {
        // TODO: Implement handshake logic
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
            logger.error("Error closing connection: %s", e.getMessage());
        }
    }

    public void createAccount(String accountNumber, double initialBalance) throws IOException, ClassNotFoundException {
        checkConnection();

        sendMessage(new CreateAccountRequest(accountNumber, initialBalance));
        Message response = receiveResponse();

        if (response instanceof CreateAccountResponse createAccountResponse) {
            if (createAccountResponse.isSuccess()) {
                logger.info("Account creation successful");
            } else {
                logger.error("Account creation failed: %s", createAccountResponse.getStatus());
            }
        } else {
            logger.error("Unexpected response type");
        }
    }

    public void deposit(String accountNumber, double amount) throws IOException, ClassNotFoundException {
        authenticate();

        checkConnectionAndAuthentication();

        sendMessage(new DepositRequest(accountNumber, amount));
        Message response = receiveResponse();

        if (response instanceof DepositResponse depositResponse) {
            if (depositResponse.isSuccess()) {
                logger.info("Deposit successful");
            } else {
                logger.error("Deposit failed: %s", depositResponse.getStatus());
            }
        } else {
            logger.error("Unexpected response type");
        }
    }

    public void withdraw(String accountNumber, double amount) throws IOException, ClassNotFoundException {
        authenticate();

        checkConnectionAndAuthentication();

        sendMessage(new WithdrawRequest(accountNumber, amount));
        Message response = receiveResponse();

        if (response instanceof WithdrawResponse withdrawResponse) {
            if (withdrawResponse.isSuccess()) {
                logger.info("Withdrawal successful");
            } else {
                logger.error("Withdrawal failed: %s", withdrawResponse.getStatus());
            }
        } else {
            logger.error("Unexpected response type");
        }
    }

    public double getBalance(String accountNumber) throws IOException, ClassNotFoundException {
        authenticate();

        checkConnectionAndAuthentication();

        sendMessage(new GetBalanceRequest(accountNumber));
        Message response = receiveResponse();

        if (response instanceof GetBalanceResponse balanceResponse) {
            if (!balanceResponse.isSuccess()) {
                logger.error("Balance inquiry failed: %s", balanceResponse.getStatus());
                return 0.0;
            }
            Double balance = balanceResponse.getBalance();
            if (balance != null) {
                return balance;
            }
        }

        logger.error("Invalid balance response");
        return 0.0;
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

    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    private Message receiveResponse() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    private void checkConnection() throws IOException {
        if (!connected) {
            throw new IOException("Not connected to the bank server");
        }
    }

    private void checkAuthentication() throws IOException {
        if (!authenticated) {
            throw new IOException("Not authenticated with the bank server");
        }
    }

    private void checkConnectionAndAuthentication() throws IOException {
        checkConnection();
        checkAuthentication();
    }
}
