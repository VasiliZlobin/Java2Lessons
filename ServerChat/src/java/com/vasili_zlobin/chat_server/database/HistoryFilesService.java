package com.vasili_zlobin.chat_server.database;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class HistoryFilesService {
    private static final int MAX_ROWS = 100;

    public static boolean saveHistory(String login, String sender, String message) {
        boolean result = true;
        File history = new File("history_" + login + ".txt");
        if (!history.exists()) {
            try {
                result = history.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        }
        if (result) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new PrintWriter(new FileWriter(history, true)))) {
                bufferedWriter.write(String.format("%n%s: %s", sender, message));
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    public static String loadHistory(String login) {
        StringBuilder sb = new StringBuilder();
        List<String> listRows = new LinkedList<>();
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File("history_" + login + ".txt"), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null && listRows.size() < MAX_ROWS) {
                listRows.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int maxRows = Math.min(MAX_ROWS, listRows.size());
        for (String row : listRows) {
            sb.insert(0, String.format("%d) %s%n", maxRows--, row));
        }
        return sb.toString();
    }
}
