package com.vasili_zlobin.lesson2;

public class MyArraySizeException extends RuntimeException {
    public MyArraySizeException(int size) {
        super(String.format("Ошибка. Переданный массив не соответствует размерам %dX%d.", size, size));
    }
}
