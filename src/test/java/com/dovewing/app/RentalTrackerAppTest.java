package com.dovewing.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class RentalTrackerAppTest {

    private static final Path RECORDED_OUTPUT = Path.of("docs", "rebuilt-output.txt");

    @Test
    void outputMatchesTheRecordedRebuiltOutput() throws IOException {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(captured, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            RentalTrackerApp.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        String expected = Files.readString(RECORDED_OUTPUT, StandardCharsets.UTF_8);
        assertEquals(normaliseLineEndings(expected),
                normaliseLineEndings(captured.toString(StandardCharsets.UTF_8)));
    }

    private static String normaliseLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }
}
