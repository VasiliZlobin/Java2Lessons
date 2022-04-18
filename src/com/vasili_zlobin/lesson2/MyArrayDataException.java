package com.vasili_zlobin.lesson2;

public class MyArrayDataException extends NumberFormatException {
    public MyArrayDataException(int x, int y, String value) {
        super(String.format("Ошибка. Не удалось преобразовать строку \"%s\" в целое число. Ячейка [%d][%d].", value, x, y));
    }
}
