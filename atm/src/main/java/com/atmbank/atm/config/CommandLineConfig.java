package com.atmbank.atm.config;

import org.apache.commons.cli.*;

import java.util.regex.Pattern;

public class CommandLineConfig {
    private static final double MIN_AMOUNT = 0.00;
    private static final double MAX_AMOUNT = 4294967295.99;
    private static final String[] FILE_NAME_BLACKLIST = {".", ".."};
    private static final String[] ACCOUNT_NAME_WHITELIST = FILE_NAME_BLACKLIST;
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(0|[1-9][0-9]*)");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("(0|[1-9][0-9]*)\\.([0-9]{2})");
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("[_\\-.0-9a-z]{1,127}");
    private static final Pattern ACCOUNT_NAME_PATTERN = Pattern.compile("[_\\-.0-9a-z]{1,122}");
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

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

        this.authFile = parseAndValidateAuthFile(cmd);
        this.ipAddress = parseAndValidateIpAddress(cmd);
        this.port = parseAndValidatePort(cmd);
        this.account = parseAndValidateAccount(cmd);
        this.cardFile = parseAndValidateCardFile(cmd);

        this.createAccountOperation = cmd.hasOption(CREATE_ACCOUNT_OPTION);
        this.depositOperation = cmd.hasOption(DEPOSIT_OPTION);
        this.withdrawOperation = cmd.hasOption(WITHDRAW_OPTION);
        this.getBalanceOperation = cmd.hasOption(GET_BALANCE_OPTION);

        validateOperationType();

        this.balance = parseDouble(cmd, CREATE_ACCOUNT_OPTION, DEFAULT_BALANCE);
        this.amount = parseDouble(cmd, depositOperation ? DEPOSIT_OPTION : WITHDRAW_OPTION, DEFAULT_AMOUNT);
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

    private String parseAndValidateAuthFile(CommandLine cmd) throws ParseException {
        String authFile = cmd.hasOption(AUTH_FILE_OPTION)
                ? cmd.getOptionValue(AUTH_FILE_OPTION)
                : DEFAULT_AUTH_FILE;

        validateFileName(authFile, "auth file");
        return authFile;
    }

    private String parseAndValidateIpAddress(CommandLine cmd) throws ParseException {
        String ipAddress = cmd.hasOption(IP_ADDRESS_OPTION)
                ? cmd.getOptionValue(IP_ADDRESS_OPTION)
                : DEFAULT_IP_ADDRESS;

        if (!IP_ADDRESS_PATTERN.matcher(ipAddress).matches()) {
            throw new ParseException("Invalid IP address format");
        }
        return ipAddress;
    }

    private int parseAndValidatePort(CommandLine cmd) throws ParseException {
        if (cmd.hasOption(PORT_OPTION)) {
            String portStr = cmd.getOptionValue(PORT_OPTION);

            if (!NUMBER_PATTERN.matcher(portStr).matches()) {
                throw new ParseException("Port must be a valid number");
            }

            try {
                int port = Integer.parseInt(portStr);
                if (port < MIN_PORT || port > MAX_PORT) {
                    throw new ParseException("Port must be between " + MIN_PORT + " and " + MAX_PORT);
                }
                return port;
            } catch (NumberFormatException e) {
                throw new ParseException("Port must be a valid number");
            }
        }
        return DEFAULT_PORT;
    }

    private String parseAndValidateAccount(CommandLine cmd) throws ParseException {
        String account = cmd.getOptionValue(ACCOUNT_OPTION);

        for (String whitelistedName : ACCOUNT_NAME_WHITELIST) {
            if (account.equals(whitelistedName)) {
                return account;
            }
        }

        if (!ACCOUNT_NAME_PATTERN.matcher(account).matches()) {
            throw new ParseException("Invalid account name");
        }
        return account;
    }

    private String parseAndValidateCardFile(CommandLine cmd) throws ParseException {
        String cardFile = cmd.hasOption(CARD_FILE_OPTION)
                ? cmd.getOptionValue(CARD_FILE_OPTION)
                : account + ".card";

        validateFileName(cardFile, "card file");
        return cardFile;
    }

    private void validateFileName(String fileName, String fileDescription) throws ParseException {
        for (String blacklistedName : FILE_NAME_BLACKLIST) {
            if (fileName.equals(blacklistedName)) {
                throw new ParseException(fileDescription + " name cannot be '" + blacklistedName + "'");
            }
        }

        if (!FILE_NAME_PATTERN.matcher(fileName).matches()) {
            throw new ParseException("Invalid " + fileDescription + " name");
        }
    }

    private double parseDouble(CommandLine cmd, String option, double defaultValue) throws ParseException {
        if (cmd.hasOption(option)) {
            String amountStr = cmd.getOptionValue(option);

            if (!DECIMAL_PATTERN.matcher(amountStr).matches()) {
                throw new ParseException("Option " + option + " must be a valid decimal number");
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
                    throw new ParseException("Option " + option + " must be between " + MIN_AMOUNT + " and " + MAX_AMOUNT);
                }
                return amount;
            } catch (NumberFormatException e) {
                throw new ParseException("Option " + option + " must be a valid decimal number");
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
