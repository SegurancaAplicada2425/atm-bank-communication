package com.atmbank.atm.security.cardfile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CardFileLoader {
    private CardFileData data;

    public void load(String filePath) throws Exception {
        Path path = Paths.get(filePath);

        if (Files.notExists(path)) {
            throw new CardFileNotFoundException();
        }
        byte[] contentBytes = Files.readAllBytes(path);

        String content = new String(contentBytes);

        data = new CardFileData(content);
    }

    public CardFileData getData() {
        return data;
    }
}
