package com.vasili_zlobin.lesson2;

import java.util.Arrays;

public class MainLesson2 {
    private static final int ARRAY_SIZE = 4;

    public static void main(String[] args) {
        String[][] table = new String[ARRAY_SIZE][ARRAY_SIZE];
        for (String[] strings : table) {
            Arrays.fill(strings, "-10");
        }
        tryRunMethod(table);
        table[2][3] = "1.3";
        tryRunMethod(table);
        table[3] = new String[3];
        tryRunMethod(table);

    }

    private static void tryRunMethod(String[][] array) {
        try {
            System.out.printf("Сумма преобразованных значений: %d%n", sumStringTable(array));
        } catch (MyArraySizeException | MyArrayDataException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int sumStringTable(String[][] array) {
        int result = 0;
        if (!checkArraySize(array)) {
            throw new MyArraySizeException(ARRAY_SIZE);
        }
        for (int i = 0; i < ARRAY_SIZE; i++) {
            for (int j = 0; j < ARRAY_SIZE; j++) {
                try {
                    result += Integer.parseInt(array[i][j]);
                } catch (NumberFormatException e) {
                    throw new MyArrayDataException(i, j, array[i][j]);
                }
            }
        }
        return result;
    }

    private static boolean checkArraySize(String array[][]) {
        boolean result = array.length == ARRAY_SIZE;
        if (result) {
            for (String[] strings : array) {
                if (strings.length != ARRAY_SIZE) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
}
