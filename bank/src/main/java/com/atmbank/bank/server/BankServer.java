package com.atmbank.bank.server;

import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.bank.security.SecurityContext;
import com.atmbank.common.config.Constants;
import com.atmbank.common.logger.ConditionalLogger;
import com.atmbank.common.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankServer {
    private static final Logger debugLogger = new ConditionalLogger(Constants.DEBUG_MODE);

    private static final int MAX_CONNECTIONS = Constants.MAX_CONNECTIONS;

    private final int port;
    private final AccountRepository accountRepository;
    private final SecurityContext securityContext;
    private final ExecutorService executorService;
    private ServerSocket serverSocket;
    private boolean running;

    public BankServer(int port, AccountRepository accountRepository, SecurityContext securityContext) {
        this.port = port;
        this.accountRepository = accountRepository;
        this.securityContext = securityContext;
        this.executorService = Executors.newFixedThreadPool(MAX_CONNECTIONS);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        debugLogger.info("Bank server started on port %d", port);

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                debugLogger.info("New client connected: %s:%d", clientSocket.getInetAddress(), clientSocket.getPort());

                ClientHandler clientHandler = new ClientHandler(clientSocket, accountRepository, securityContext);
                executorService.submit(clientHandler::handle);
            } catch (Exception e) {
                if (running) {
                    debugLogger.error("Error accepting client connection: %s", e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;

        if (executorService != null) {
            executorService.shutdown();
        }

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                debugLogger.error("Error closing server socket: %s", e.getMessage());
            }
        }

        debugLogger.info("Bank server stopped");
    }
}
