package com.atmbank.bank;

import com.atmbank.bank.config.CommandLineConfig;
import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.bank.repository.InMemoryAccountRepository;
import com.atmbank.bank.server.BankServer;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class Main {
    private static final Logger LOGGER = new ConsoleLogger(); // TODO: Change to NullLogger before delivery
    private static final int ERROR_EXIT_CODE = 255;

    public static void main(String[] args) {
        try {
            CommandLineConfig config = new CommandLineConfig(args);
            AccountRepository accountRepository = new InMemoryAccountRepository();
            BankServer server = new BankServer(config.getPort(), accountRepository);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutting down server...");
                server.stop();
            }));

            server.start();
        } catch (ParseException e) {
            LOGGER.error("Error parsing command line arguments: %s", e.getMessage());
            System.exit(ERROR_EXIT_CODE);
        } catch (IOException e) {
            LOGGER.error("Server error: %s", e.getMessage());
            System.exit(ERROR_EXIT_CODE);
        } catch (Exception e) {
            LOGGER.error("Unexpected error: %s", e.getMessage());
            System.exit(ERROR_EXIT_CODE);
        }
    }
}
