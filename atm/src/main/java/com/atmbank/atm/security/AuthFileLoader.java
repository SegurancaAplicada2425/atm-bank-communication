package com.atmbank.atm.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.PublicKey;

import org.apache.commons.codec.binary.Hex;

import com.atmbank.atm.exception.AuthFileFormatException;
import com.atmbank.atm.exception.AuthFileNotFoundException;
import com.atmbank.common.security.AESUtils;
import com.atmbank.common.security.AuthFileData;
import com.atmbank.common.security.RSAUtils;

public class AuthFileLoader {
    private AuthFileData data;

    public void load(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            throw new AuthFileNotFoundException();
        }
        byte[] content = Files.readAllBytes(path);

        String[] lines = new String(content).split("\n");
        if (lines.length != 3) {
            throw new AuthFileFormatException();
        }

        byte[] secret = Hex.decodeHex(lines[0].trim());
        byte[] keyBytes = Hex.decodeHex(lines[1].trim());
        byte[] bankPublicKeyBytes = Hex.decodeHex(lines[2].trim());

        Key key = AESUtils.getKeyFromBytes(keyBytes);
        PublicKey bankPublicKey = RSAUtils.getPublicKeyFromBytes(bankPublicKeyBytes);

        data = new AuthFileData(secret, key, bankPublicKey);
    }

    public AuthFileData getData() {
        return data;
    }
}
