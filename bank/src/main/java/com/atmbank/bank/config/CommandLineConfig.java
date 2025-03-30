package com.atmbank.bank.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineConfig {
    private static final String PORT_OPTION = "p";
    private static final String AUTH_FILE_OPTION = "s";
    private static final int DEFAULT_PORT = 3000;
    private static final String DEFAULT_AUTH_FILE = "bank.auth";

    private final int port;
    private final String authFile;

    public CommandLineConfig(String[] args) throws ParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        validateNoDuplicateOptions(cmd);

        this.port = parsePort(cmd);
        this.authFile = parseAuthFile(cmd);
    }

    private Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder(PORT_OPTION)
                .hasArg()
                .desc("Port number for the bank server")
                .build());

        options.addOption(Option.builder(AUTH_FILE_OPTION)
                .hasArg()
                .desc("Authentication file path")
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

    private String parseAuthFile(CommandLine cmd) {
        return cmd.hasOption(AUTH_FILE_OPTION)
                ? cmd.getOptionValue(AUTH_FILE_OPTION)
                : DEFAULT_AUTH_FILE;
    }

    public int getPort() {
        return port;
    }

    public String getAuthFile() {
        return authFile;
    }
}
