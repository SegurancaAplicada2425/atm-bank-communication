package com.atmbank.bank.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.atmbank.bank.exception.AuthFileAlreadyExistsException;
import com.atmbank.common.security.AuthFileData;

public class AuthFileGenerator {
    private final AuthFileData data;

    public AuthFileGenerator(AuthFileData data) {
        this.data = data;
    }

    public void generate(String filePath) throws IOException, AuthFileAlreadyExistsException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            throw new AuthFileAlreadyExistsException();
        }
        byte[] content = data.toString().getBytes();
        Files.write(path, content);
    }
}
