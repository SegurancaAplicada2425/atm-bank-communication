package com.atmbank.atm;

import com.atmbank.atm.client.BankClient;
import com.atmbank.atm.config.CommandLineConfig;
import com.atmbank.atm.security.SecurityContext;
import com.atmbank.atm.security.authfile.AuthFileLoader;
import com.atmbank.atm.security.cardfile.CardFileData;
import com.atmbank.atm.security.cardfile.CardFileGenerator;
import com.atmbank.atm.security.cardfile.CardFileLoader;
import com.atmbank.common.config.Constants;
import com.atmbank.common.logger.ConditionalLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.security.authfile.AuthFileData;
import com.atmbank.common.security.protocol.ProtocolException;
import com.atmbank.common.security.utils.AESUtils;
import com.atmbank.common.utils.ByteArrayGenerator;
import com.atmbank.common.utils.ConversionUtils;

import java.net.SocketTimeoutException;
import java.security.Key;

public class Main {
    private static final int SUCCESS_CODE = Constants.SUCCESS_CODE;
    private static final int PROTOCOL_ERROR_CODE = Constants.PROTOCOL_ERROR_CODE;
    private static final int GENERAL_ERROR_CODE = Constants.GENERAL_ERROR_CODE;

    private static final Logger debugLogger = new ConditionalLogger(Constants.DEBUG_MODE);

    public static void main(String[] args) {
        try {
            CommandLineConfig config = new CommandLineConfig(args);

            AuthFileLoader authFileLoader = new AuthFileLoader();
            authFileLoader.load(config.getAuthFile());

            AuthFileData authFileData = authFileLoader.getData();

            Key key = AESUtils.generateKey();
            byte[] keyBytes = AESUtils.getKeyBytes(key);

            SecurityContext securityContext = new SecurityContext(keyBytes, authFileData.getBankPublicKey());

            BankClient client = new BankClient(config.getIpAddress(), config.getPort(), securityContext);

            executeOperation(config, client);
        } catch (SocketTimeoutException e) {
            debugLogger.error("Connection timed out: %s", e.getMessage());
            System.exit(PROTOCOL_ERROR_CODE);
        } catch (ProtocolException e) {
            debugLogger.error("Protocol error: %s", e.getMessage());
            System.exit(PROTOCOL_ERROR_CODE);
        } catch (Exception e) {
            debugLogger.error("Unexpected error: %s", e.getMessage());
            System.exit(GENERAL_ERROR_CODE);
        }

        System.exit(SUCCESS_CODE);
    }

    private static void executeOperation(CommandLineConfig config, BankClient client) throws Exception {
        client.connect();

        String cardFile = config.getCardFile();

        String accountPin;
        if (!config.isCreateAccountOperation()) {
            CardFileLoader cardFileLoader = new CardFileLoader();
            cardFileLoader.load(cardFile);
            CardFileData cardFileData = cardFileLoader.getData();
            accountPin = cardFileData.getAccountPin();
        } else {
            byte[] accountPinBytes = ByteArrayGenerator.generate(Constants.PIN_SIZE / 8);
            accountPin = ConversionUtils.toHexString(accountPinBytes);
            CardFileData data = new CardFileData(accountPin);
            CardFileGenerator cardFileGenerator = new CardFileGenerator(data);
            cardFileGenerator.generate(config.getCardFile());
        }

        if (config.isCreateAccountOperation()) {
            try {
                debugLogger.info("Creating account %s with initial balance %.2f", config.getAccount(), config.getBalance());
                client.createAccount(config.getAccount(), accountPin, config.getBalance());
            } catch (Exception e) {
                CardFileGenerator.delete(config.getCardFile());
                throw e;
            }
        } else if (config.isDepositOperation()) {
            debugLogger.info("Depositing %.2f to account %s", config.getAmount(), config.getAccount());
            client.deposit(config.getAccount(), accountPin, config.getAmount());
        } else if (config.isWithdrawOperation()) {
            debugLogger.info("Withdrawing %.2f from account %s", config.getAmount(), config.getAccount());
            client.withdraw(config.getAccount(), accountPin, config.getAmount());
        } else if (config.isGetBalanceOperation()) {
            debugLogger.info("Getting balance for account %s", config.getAccount());
            client.displayBalance(config.getAccount(), accountPin);
        }

        client.disconnect();
    }
}
