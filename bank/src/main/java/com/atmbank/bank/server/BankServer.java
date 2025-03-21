package com.atmbank.bank.server;

import com.atmbank.bank.handler.ClientHandler;
import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {
    private static final Logger LOGGER = new ConsoleLogger();

    private final int port;
    private final AccountRepository accountRepository;
    private ServerSocket serverSocket;
    private boolean running;

    public BankServer(int port, AccountRepository accountRepository) {
        this.port = port;
        this.accountRepository = accountRepository;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        LOGGER.info("Bank server started on port %d", port);

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("New client connected: %s:%d", clientSocket.getInetAddress(), clientSocket.getPort());
                ClientHandler clientHandler = new ClientHandler(clientSocket, accountRepository); // TODO: Check if this needs thread pool
                clientHandler.handle();
            } catch (IOException e) {
                if (running) {
                    LOGGER.error("Error accepting client connection: %s", e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error("Error closing server socket: %s", e.getMessage());
            }
        }

        LOGGER.info("Bank server stopped");
    }
}
