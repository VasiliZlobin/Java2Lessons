package com.vasili_zlobin.lesson3;

import java.util.*;

public class MainLesson3 {
    public static void main(String[] args) {
        testFirstTask();
        System.out.println();
        testSecondTask();
    }

    private static void testFirstTask() {
        List<String> arrayString = getStringList();
        Map<String, Integer> countStrings = new LinkedHashMap<>();
        for (String word : arrayString) {
            int count = 1;
            if (countStrings.containsKey(word)) {
                count = countStrings.get(word) + 1;
            }
            countStrings.put(word, count);
        }
        for (Map.Entry<String, Integer> entry : countStrings.entrySet()) {
            System.out.printf("Слово \"%s\" встречается раз: %d%n", entry.getKey(), entry.getValue());
        }
    }

    private static List<String> getStringList() {
        String[] array = {"one", "five", "two", "four", "three", "four", "two", "three", "four", "five", "two"
                , "three", "four", "two", "three", "four"};
        return new ArrayList<>(Arrays.asList(array));
    }

    private static void testSecondTask() {
        Phonebook book = Phonebook.getPhonebook();
        addRecords(book);
        showNumber(book, "Петров");
        showNumber(book, "Уваров");
        showNumber(book, "Сидоров");
        showNumber(book, "Иванов");
    }

    private static void addRecords(Phonebook book) {
        book.add("Иванов", "89254445511");
        book.add("Иванов", "89254445522");
        book.add("Иванов", "89254445511");
        book.add("Петров", "89254444433");
        book.add("Петров", "89254445533");
        book.add("Петров", "89254345511");
        book.add("Сидоров", "89263345511");
        book.add("Петров", "89254345511");
    }

    private static void showNumber(Phonebook book, String lastName) {
        Set<String> record = book.get(lastName);
        if (record == null) {
            System.out.printf("%s. Отсутствуют известные телефонные номера.%n", lastName);
        } else {
            System.out.printf("%s. Номера телефонов: %s%n", lastName, record.toString());
        }
    }
}
