package com.atmbank.atm.security.cardfile;

import com.atmbank.common.utils.ConversionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CardFileGenerator {
    private final CardFileData data;

    public CardFileGenerator(CardFileData data) {
        this.data = data;
    }

    public void generate(String filePath) throws IOException, CardFileAlreadyExistsException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            throw new CardFileAlreadyExistsException();
        }
        byte[] content = ConversionUtils.toBytes(data.toString());
        Files.write(path, content);
    }

    public static void delete(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
}
