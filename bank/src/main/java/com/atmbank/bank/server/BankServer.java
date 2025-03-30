package com.atmbank.bank.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.atmbank.bank.handler.ClientHandler;
import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.bank.security.SecurityContext;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;

public class BankServer {
    private static final Logger logger = new ConsoleLogger(); // TODO: Change to NullLogger before delivery

    private final int port;
    private final AccountRepository accountRepository;
    private final SecurityContext securityContext;
    private ServerSocket serverSocket;
    private boolean running;

    public BankServer(int port, AccountRepository accountRepository, SecurityContext securityContext) {
        this.port = port;
        this.accountRepository = accountRepository;
        this.securityContext = securityContext;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        logger.info("Bank server started on port %d", port);

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: %s:%d", clientSocket.getInetAddress(), clientSocket.getPort());
                ClientHandler clientHandler = new ClientHandler(clientSocket, accountRepository, securityContext);
                clientHandler.handle();
            } catch (IOException e) {
                if (running) {
                    logger.error("Error accepting client connection: %s", e.getMessage());
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
                logger.error("Error closing server socket: %s", e.getMessage());
            }
        }

        logger.info("Bank server stopped");
    }
}
