package com.vasili_zlobin.lesson3;

import java.util.*;

public class Phonebook {
    private static Phonebook phonebook;
    private Map<String, Set<String>> dataTable;

    public static Phonebook getPhonebook() {
        if (phonebook == null) {
            phonebook = new Phonebook();
        }
        return phonebook;
    }

    private Phonebook() {
        dataTable = new TreeMap<>();
    }

    public void add(String lastName, String number) {
        Set<String> numbers;
        if (dataTable.containsKey(lastName)) {
            numbers = dataTable.get(lastName);
        } else {
            numbers = new TreeSet<>();
            dataTable.put(lastName, numbers);
        }
        numbers.add(number);
    }

    public Set<String> get(String lastName) {
        return dataTable.get(lastName);
    }
}
