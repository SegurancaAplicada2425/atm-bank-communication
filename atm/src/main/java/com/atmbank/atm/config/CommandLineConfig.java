package com.atmbank.atm.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineConfig {
    private static final String AUTH_FILE_OPTION = "s";
    private static final String IP_ADDRESS_OPTION = "i";
    private static final String PORT_OPTION = "p";
    private static final String CARD_FILE_OPTION = "c";
    private static final String ACCOUNT_OPTION = "a";
    private static final String CREATE_ACCOUNT_OPTION = "n";
    private static final String DEPOSIT_OPTION = "d";
    private static final String WITHDRAW_OPTION = "w";
    private static final String GET_BALANCE_OPTION = "g";

    private static final String DEFAULT_AUTH_FILE = "bank.auth";
    private static final String DEFAULT_IP_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 3000;
    private static final double DEFAULT_BALANCE = 0.0;
    private static final double DEFAULT_AMOUNT = 0.0;

    private final String authFile;
    private final String ipAddress;
    private final int port;
    private final String cardFile;
    private final String account;
    private final boolean createAccountOperation;
    private final boolean depositOperation;
    private final boolean withdrawOperation;
    private final boolean getBalanceOperation;
    private final double balance;
    private final double amount;

    public CommandLineConfig(String[] args) throws ParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        validateNoDuplicateOptions(cmd);
        validateRequiredOptions(cmd);

        this.authFile = parseAuthFile(cmd);
        this.ipAddress = parseIpAddress(cmd);
        this.port = parsePort(cmd);
        this.cardFile = parseCardFile(cmd);
        this.account = cmd.getOptionValue(ACCOUNT_OPTION);

        this.createAccountOperation = cmd.hasOption(CREATE_ACCOUNT_OPTION);
        this.depositOperation = cmd.hasOption(DEPOSIT_OPTION);
        this.withdrawOperation = cmd.hasOption(WITHDRAW_OPTION);
        this.getBalanceOperation = cmd.hasOption(GET_BALANCE_OPTION);

        validateOperationType();

        this.balance = parseDoubleOption(cmd, CREATE_ACCOUNT_OPTION, DEFAULT_BALANCE);
        this.amount = parseDoubleOption(cmd, depositOperation ? DEPOSIT_OPTION : WITHDRAW_OPTION, DEFAULT_AMOUNT);
    }

    private Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder(AUTH_FILE_OPTION)
                .hasArg()
                .desc("Authentication file path")
                .build());

        options.addOption(Option.builder(IP_ADDRESS_OPTION)
                .hasArg()
                .desc("Bank server IP address")
                .build());

        options.addOption(Option.builder(PORT_OPTION)
                .hasArg()
                .desc("Bank server port")
                .build());

        options.addOption(Option.builder(CARD_FILE_OPTION)
                .hasArg()
                .desc("Card file path")
                .build());

        options.addOption(Option.builder(ACCOUNT_OPTION)
                .hasArg()
                .desc("Account number")
                .required()
                .build());

        options.addOption(Option.builder(CREATE_ACCOUNT_OPTION)
                .hasArg()
                .desc("Create new account with the specified balance")
                .build());

        options.addOption(Option.builder(DEPOSIT_OPTION)
                .hasArg()
                .desc("Deposit the specified amount")
                .build());

        options.addOption(Option.builder(WITHDRAW_OPTION)
                .hasArg()
                .desc("Withdraw the specified amount")
                .build());

        options.addOption(Option.builder(GET_BALANCE_OPTION)
                .desc("Get account balance")
                .build());

        return options;
    }

    private void validateNoDuplicateOptions(CommandLine cmd) throws ParseException {
        for (Option option : cmd.getOptions()) {
            if (cmd.getOptionValues(option.getOpt()) != null && cmd.getOptionValues(option.getOpt()).length > 1) {
                throw new ParseException("Option '" + option.getOpt() + "' specified multiple times");
            }
        }
    }

    private void validateRequiredOptions(CommandLine cmd) throws ParseException {
        if (!cmd.hasOption(ACCOUNT_OPTION)) {
            throw new ParseException("Account option is required");
        }
    }

    private void validateOperationType() throws ParseException {
        int operationCount = 0;
        if (createAccountOperation)
            operationCount++;
        if (depositOperation)
            operationCount++;
        if (withdrawOperation)
            operationCount++;
        if (getBalanceOperation)
            operationCount++;

        if (operationCount != 1) {
            throw new ParseException("Exactly one operation type (-n, -d, -w, -g) must be specified");
        }
    }

    private String parseAuthFile(CommandLine cmd) {
        return cmd.hasOption(AUTH_FILE_OPTION)
                ? cmd.getOptionValue(AUTH_FILE_OPTION)
                : DEFAULT_AUTH_FILE;
    }

    private String parseIpAddress(CommandLine cmd) {
        return cmd.hasOption(IP_ADDRESS_OPTION)
                ? cmd.getOptionValue(IP_ADDRESS_OPTION)
                : DEFAULT_IP_ADDRESS;
    }

    private int parsePort(CommandLine cmd) throws ParseException {
        if (cmd.hasOption(PORT_OPTION)) {
            try {
                return Integer.parseInt(cmd.getOptionValue(PORT_OPTION));
            } catch (NumberFormatException e) {
                throw new ParseException("Port must be a valid number");
            }
        }
        return DEFAULT_PORT;
    }

    private String parseCardFile(CommandLine cmd) {
        return cmd.hasOption(CARD_FILE_OPTION)
                ? cmd.getOptionValue(CARD_FILE_OPTION)
                : account + ".card";
    }

    private double parseDoubleOption(CommandLine cmd, String option, double defaultValue) throws ParseException {
        if (cmd.hasOption(option)) {
            try {
                return Double.parseDouble(cmd.getOptionValue(option));
            } catch (NumberFormatException e) {
                throw new ParseException("Option " + option + " must be a valid number");
            }
        }
        return defaultValue;
    }

    public String getAuthFile() {
        return authFile;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getCardFile() {
        return cardFile;
    }

    public String getAccount() {
        return account;
    }

    public boolean isCreateAccountOperation() {
        return createAccountOperation;
    }

    public boolean isDepositOperation() {
        return depositOperation;
    }

    public boolean isWithdrawOperation() {
        return withdrawOperation;
    }

    public boolean isGetBalanceOperation() {
        return getBalanceOperation;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalance() {
        return balance;
    }
}
