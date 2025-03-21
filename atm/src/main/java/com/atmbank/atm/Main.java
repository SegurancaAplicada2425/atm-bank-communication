package com.atmbank.atm;

import com.atmbank.atm.client.BankClient;
import com.atmbank.atm.config.CommandLineConfig;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class Main {
    private static final Logger LOGGER = new ConsoleLogger();
    private static final int CONNECTION_ERROR_CODE = 63;
    private static final int GENERAL_ERROR_CODE = 1;

    public static void main(String[] args) {
        try {
            CommandLineConfig config = new CommandLineConfig(args);
            BankClient client = new BankClient(config.getIpAddress(), config.getPort(), config.getAuthFile());

            executeOperation(client, config);
        } catch (ParseException e) {
            LOGGER.error("Error parsing command line arguments: %s", e.getMessage());
            System.exit(GENERAL_ERROR_CODE);
        } catch (IOException e) {
            LOGGER.error("Connection error: %s", e.getMessage());
            System.exit(CONNECTION_ERROR_CODE);
        } catch (Exception e) {
            LOGGER.error("Unexpected error: %s", e.getMessage());
            System.exit(GENERAL_ERROR_CODE);
        }
    }

    private static void executeOperation(BankClient client, CommandLineConfig config) throws IOException, ClassNotFoundException {
        try {
            client.connect();

            if (config.isCreateAccountOperation()) {
                LOGGER.info("Creating account %s with initial balance %.2f", config.getAccount(), config.getBalance());
                client.createAccount(config.getAccount(), config.getBalance());
            } else if (config.isDepositOperation()) {
                LOGGER.info("Depositing %.2f to account %s", config.getAmount(), config.getAccount());
                client.deposit(config.getAccount(), config.getAmount());
            } else if (config.isWithdrawOperation()) {
                LOGGER.info("Withdrawing %.2f from account %s", config.getAmount(), config.getAccount());
                client.withdraw(config.getAccount(), config.getAmount());
            } else if (config.isGetBalanceOperation()) {
                LOGGER.info("Getting balance for account %s", config.getAccount());
                double balance = client.getBalance(config.getAccount());
                LOGGER.info("Balance: %.2f", balance);
            }
        } finally {
            client.disconnect();
        }
    }
}
