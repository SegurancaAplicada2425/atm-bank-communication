package com.atmbank.bank;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;

import org.apache.commons.cli.ParseException;

import com.atmbank.bank.config.CommandLineConfig;
import com.atmbank.bank.exception.AuthFileAlreadyExistsException;
import com.atmbank.bank.repository.AccountRepository;
import com.atmbank.bank.repository.InMemoryAccountRepository;
import com.atmbank.bank.security.AuthFileGenerator;
import com.atmbank.bank.security.SecurityContext;
import com.atmbank.bank.server.BankServer;
import com.atmbank.common.logger.ConsoleLogger;
import com.atmbank.common.logger.Logger;
import com.atmbank.common.security.AESUtils;
import com.atmbank.common.security.AuthFileData;
import com.atmbank.common.security.ByteArrayGenerator;
import com.atmbank.common.security.RSAUtils;

public class Main {
    private static final int ERROR_EXIT_CODE = 255;

    private static final Logger logger = new ConsoleLogger(); // TODO: Change to NullLogger before delivery

    public static void main(String[] args) {
        try {
            CommandLineConfig config = new CommandLineConfig(args);
            AccountRepository accountRepository = new InMemoryAccountRepository();

            byte[] secret = ByteArrayGenerator.generate(32);
            Key key = AESUtils.generateKey();
            KeyPair keyPair = RSAUtils.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();

            AuthFileData authFileData = new AuthFileData(secret, key, publicKey);
            AuthFileGenerator authFileGenerator = new AuthFileGenerator(authFileData);
            authFileGenerator.generate(config.getAuthFile());

            SecurityContext securityContext = new SecurityContext(secret, key, keyPair);

            BankServer server = new BankServer(config.getPort(), accountRepository, securityContext);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                server.stop();
            }));

            server.start();
        } catch (ParseException e) {
            logger.error("Error parsing command line arguments: %s", e.getMessage());
            System.exit(ERROR_EXIT_CODE);
        } catch (AuthFileAlreadyExistsException e) {
            logger.error("Auth file already exists: %s", e.getMessage());
            System.exit(ERROR_EXIT_CODE);
        } catch (IOException e) {
            logger.error("Server error: %s", e.getMessage());
            System.exit(ERROR_EXIT_CODE);
        } catch (Exception e) {
            logger.error("Unexpected error: %s", e.getMessage());
            System.exit(ERROR_EXIT_CODE);
        }
    }
}
