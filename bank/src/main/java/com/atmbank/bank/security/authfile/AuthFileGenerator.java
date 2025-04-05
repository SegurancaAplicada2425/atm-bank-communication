package com.atmbank.bank.security.authfile;

import com.atmbank.common.security.authfile.AuthFileData;
import com.atmbank.common.utils.ConversionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        byte[] content = ConversionUtils.toBytes(data.toString());
        Files.write(path, content);
    }
}
