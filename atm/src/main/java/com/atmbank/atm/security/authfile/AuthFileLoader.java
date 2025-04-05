package com.atmbank.atm.security.authfile;

import com.atmbank.common.security.authfile.AuthFileData;
import com.atmbank.common.utils.ConversionUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AuthFileLoader {
    private AuthFileData data;

    public void load(String filePath) throws Exception {
        Path path = Paths.get(filePath);

        if (Files.notExists(path)) {
            throw new AuthFileNotFoundException();
        }
        byte[] contentBytes = Files.readAllBytes(path);

        String content = new String(contentBytes);

        byte[] bankPublicKey = ConversionUtils.toBytes(content.trim(), true);

        data = new AuthFileData(bankPublicKey);
    }

    public AuthFileData getData() {
        return data;
    }
}
