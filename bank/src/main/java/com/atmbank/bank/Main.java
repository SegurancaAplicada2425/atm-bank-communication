package com.atmbank.bank;

import com.atmbank.bank.config.CommandLineConfig;
import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.bank.repository.InMemoryAccountRepository;
import com.atmbank.bank.security.SecurityContext;
import com.atmbank.bank.security.authfile.AuthFileGenerator;
import com.atmbank.bank.server.BankServer;
import com.atmbank.common.config.Constants;
import com.atmbank.common.display.ConsoleDisplay;
import com.atmbank.common.display.Display;
import com.atmbank.common.logger.ConditionalLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.security.authfile.AuthFileData;
import com.atmbank.common.security.utils.RSAUtils;

import java.security.KeyPair;

public class Main {
    private static final int SUCCESS_CODE = Constants.SUCCESS_CODE;
    private static final int GENERAL_ERROR_CODE = Constants.GENERAL_ERROR_CODE;

    private static final Display display = new ConsoleDisplay();
    private static final Logger debugLogger = new ConditionalLogger(Constants.DEBUG_MODE);

    public static void main(String[] args) {
        try {
            CommandLineConfig config = new CommandLineConfig(args);
            AccountRepository accountRepository = new InMemoryAccountRepository();

            KeyPair keyPair = RSAUtils.generateKeyPair();
            byte[] privateKey = RSAUtils.getPrivateKeyBytes(keyPair);
            byte[] publicKey = RSAUtils.getPublicKeyBytes(keyPair);

            AuthFileData authFileData = new AuthFileData(publicKey);
            AuthFileGenerator authFileGenerator = new AuthFileGenerator(authFileData);
            authFileGenerator.generate(config.getAuthFile());
            display.display("created");

            SecurityContext securityContext = new SecurityContext(privateKey, publicKey);

            BankServer server = new BankServer(config.getPort(), accountRepository, securityContext);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                debugLogger.info("Shutting down server...");
                server.stop();
            }));

            server.start();
        } catch (Exception e) {
            debugLogger.error("Unexpected error: %s", e.getMessage());
            System.exit(GENERAL_ERROR_CODE);
        }

        System.exit(SUCCESS_CODE);
    }
}
