package com.atmbank.atm;

import java.io.IOException;

import org.apache.commons.cli.ParseException;

import com.atmbank.atm.client.BankClient;
import com.atmbank.atm.config.CommandLineConfig;
import com.atmbank.atm.security.AuthFileLoader;
import com.atmbank.atm.security.SecurityContext;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.security.AuthFileData;

public class Main {
    private static final int CONNECTION_ERROR_CODE = 63;
    private static final int GENERAL_ERROR_CODE = 1;

    private static final Logger logger = new ConsoleLogger(); // TODO: Change to NullLogger before delivery

    public static void main(String[] args) {
        try {
            CommandLineConfig config = new CommandLineConfig(args);

            AuthFileLoader authFileLoader = new AuthFileLoader();
            authFileLoader.load(config.getAuthFile());

            AuthFileData authFileData = authFileLoader.getData();

            SecurityContext securityContext = new SecurityContext(
                    authFileData.getSecret(),
                    authFileData.getKey(),
                    authFileData.getBankPublicKey());

            BankClient client = new BankClient(config.getIpAddress(), config.getPort(), config.getAuthFile(),
                    securityContext);

            executeOperation(client, config);
        } catch (ParseException e) {
            logger.error("Error parsing command line arguments: %s", e.getMessage());
            System.exit(GENERAL_ERROR_CODE);
        } catch (IOException e) {
            logger.error("Connection error: %s", e.getMessage());
            System.exit(CONNECTION_ERROR_CODE);
        } catch (Exception e) {
            logger.error("Unexpected error: %s", e.getMessage());
            System.exit(GENERAL_ERROR_CODE);
        }
    }

    private static void executeOperation(BankClient client, CommandLineConfig config)
            throws IOException, ClassNotFoundException {
        try {
            client.connect();

            if (config.isCreateAccountOperation()) {
                logger.info("Creating account %s with initial balance %.2f", config.getAccount(), config.getBalance());
                client.createAccount(config.getAccount(), config.getBalance());
            } else if (config.isDepositOperation()) {
                logger.info("Depositing %.2f to account %s", config.getAmount(), config.getAccount());
                client.deposit(config.getAccount(), config.getAmount());
            } else if (config.isWithdrawOperation()) {
                logger.info("Withdrawing %.2f from account %s", config.getAmount(), config.getAccount());
                client.withdraw(config.getAccount(), config.getAmount());
            } else if (config.isGetBalanceOperation()) {
                logger.info("Getting balance for account %s", config.getAccount());
                double balance = client.getBalance(config.getAccount());
                logger.info("Balance: %.2f", balance);
            }
        } finally {
            client.disconnect();
        }
    }
}
