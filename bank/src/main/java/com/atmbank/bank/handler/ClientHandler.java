package com.atmbank.bank.handler;

import com.atmbank.bank.model.Account;
import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.message.Message;
import com.atmbank.common.message.MessageType;
import com.atmbank.common.message.request.CreateAccountRequest;
import com.atmbank.common.message.request.DepositRequest;
import com.atmbank.common.message.request.GetBalanceRequest;
import com.atmbank.common.message.request.WithdrawRequest;
import com.atmbank.common.message.response.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {
    private static final Logger LOGGER = new ConsoleLogger();

    private final Socket clientSocket;
    private final AccountRepository accountRepository;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket, AccountRepository accountRepository) {
        this.clientSocket = socket;
        this.accountRepository = accountRepository;
    }

    public void handle() {
        try {
            setupStreams();
            handleClientRequests();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Error handling client: %s", e);
        } finally {
            closeConnection();
        }
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    private void handleClientRequests() throws IOException, ClassNotFoundException {
        Message request;
        boolean authenticated = false;

        try {
            while ((request = (Message) in.readObject()) != null) {
                if (request.getType() == MessageType.AUTHENTICATE) {
                    authenticated = true;
                    sendResponse(new AuthenticateResponse(true));
                } else if (authenticated) {
                    handleAuthenticatedRequest(request);
                } else {
                    sendResponse(new ErrorResponse("AUTHENTICATION_REQUIRED"));
                }
            }
        } catch (EOFException e) {
            LOGGER.info("Client disconnected %s", getConnectionIdentifier());
        }
    }

    private void handleAuthenticatedRequest(Message request) throws IOException {
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
                LOGGER.info("%s sent unknown request: %s", getConnectionIdentifier(), request);
                break;
        }
    }

    private void handleCreateAccountRequest(CreateAccountRequest request) throws IOException {
        String accountNumber = request.getAccountNumber();
        double initialBalance = request.getInitialBalance();

        if (initialBalance < 0) {
            sendResponse(new CreateAccountResponse(ResponseStatus.NEGATIVE_AMOUNT));
            LOGGER.info("%s tried to create account with negative balance: %s", getConnectionIdentifier(), request);
            return;
        }

        Account existingAccount = accountRepository.findById(accountNumber);
        if (existingAccount != null) {
            sendResponse(new CreateAccountResponse(ResponseStatus.ACCOUNT_EXISTS));
            LOGGER.info("%s tried to create existing account: %s", getConnectionIdentifier(), request);
            return;
        }

        Account newAccount = new Account(accountNumber, initialBalance);
        accountRepository.save(newAccount);
        sendResponse(new CreateAccountResponse(ResponseStatus.SUCCESS));
        LOGGER.info("%s created a new account: %s", getConnectionIdentifier(), request);
    }

    private void handleGetBalanceRequest(GetBalanceRequest request) throws IOException {
        String accountNumber = request.getAccountNumber();
        Account account = accountRepository.findById(accountNumber);

        if (account == null) {
            sendResponse(new GetBalanceResponse(ResponseStatus.ACCOUNT_NOT_FOUND));
            LOGGER.info("%s tried to get balance for nonexistent account: %s", getConnectionIdentifier(), request);
            return;
        }

        sendResponse(new GetBalanceResponse(account.getBalance()));
        LOGGER.info("%s checked balance for account: %s", getConnectionIdentifier(), request);
    }

    private void handleWithdrawRequest(WithdrawRequest request) throws IOException {
        String accountNumber = request.getAccountNumber();
        double amount = request.getAmount();

        if (amount <= 0) {
            sendResponse(new WithdrawResponse(ResponseStatus.NEGATIVE_OR_ZERO_AMOUNT));
            LOGGER.info("%s tried to withdraw an invalid amount from account: %s", getConnectionIdentifier(), request);
            return;
        }

        Account account = accountRepository.findById(accountNumber);
        if (account == null) {
            sendResponse(new WithdrawResponse(ResponseStatus.ACCOUNT_NOT_FOUND));
            LOGGER.info("%s tried to withdraw from nonexistent account: %s", getConnectionIdentifier(), request);
            return;
        }

        try {
            account.withdraw(amount);
            accountRepository.updateBalance(accountNumber, account.getBalance());
            sendResponse(new WithdrawResponse(ResponseStatus.SUCCESS));
            LOGGER.info("%s withdrawn from account: %s", getConnectionIdentifier(), request);
        } catch (IllegalArgumentException e) {
            sendResponse(new WithdrawResponse(ResponseStatus.INSUFFICIENT_BALANCE));
            LOGGER.info("%s tried to withdraw from account with insufficient balance: %s", getConnectionIdentifier(), request);
        }
    }

    private void handleDepositRequest(DepositRequest request) throws IOException {
        String accountNumber = request.getAccountNumber();
        double amount = request.getAmount();

        if (amount <= 0) {
            sendResponse(new DepositResponse(ResponseStatus.NEGATIVE_OR_ZERO_AMOUNT));
            LOGGER.info("%s tried to deposit an invalid amount to account: %s", getConnectionIdentifier(), request);
            return;
        }

        Account account = accountRepository.findById(accountNumber);
        if (account == null) {
            sendResponse(new DepositResponse(ResponseStatus.ACCOUNT_NOT_FOUND));
            LOGGER.info("%s tried to deposit to nonexistent account: %s", getConnectionIdentifier(), request);
            return;
        }

        try {
            account.deposit(amount);
            accountRepository.updateBalance(accountNumber, account.getBalance());
            sendResponse(new DepositResponse(ResponseStatus.SUCCESS));
            LOGGER.info("%s deposited to account: %s", getConnectionIdentifier(), request);
        } catch (IllegalArgumentException e) {
            sendResponse(new DepositResponse(ResponseStatus.ERROR));
            LOGGER.info("%s tried to deposit an invalid amount to account: %s", getConnectionIdentifier(), request);
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
                LOGGER.info("Closed connection with client: %s", clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            LOGGER.error("Error closing client connection: %s", e.getMessage());
        }
    }
}
