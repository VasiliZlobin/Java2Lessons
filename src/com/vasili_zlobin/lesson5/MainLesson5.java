package com.vasili_zlobin.lesson5;

import java.util.Arrays;

public class MainLesson5 {
    private static final int SIZE = 10_000_000;
    private static final int HALF = SIZE / 2;
    private static final float[] array = new float[SIZE];
    private static long currentTimeMs;

    public static void main(String[] args) {
        firstMethod();
        System.out.println();
        try {
            secondMethod();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void firstMethod() {
        Arrays.fill(array, 1);
        System.out.println("Старт первого метода");
        currentTimeMs = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            evaluateArrayValue(array, i);
        }
        getPeriodWithPrint("Первый метод отработал за %d мс%n");
    }

    private static void secondMethod() throws InterruptedException {
        Arrays.fill(array, 1);
        System.out.println("Старт второго метода");
        long allTime = 0;
        currentTimeMs = System.currentTimeMillis();
        float[] leftArray = new float[HALF];
        float[] rightArray = new float[HALF];
        System.arraycopy(array, 0, leftArray, 0, HALF);
        System.arraycopy(array, HALF, rightArray, 0, HALF);
        allTime += getPeriodWithPrint("Разбили массив за %d мс%n");
        EvaluateArrayThread thread1 = new EvaluateArrayThread(leftArray);
        EvaluateArrayThread thread2 = new EvaluateArrayThread(rightArray);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        allTime += getPeriodWithPrint("Выполнили вычисления за %d мс%n");
        System.arraycopy(leftArray, 0, array, 0, HALF);
        System.arraycopy(rightArray, 0, array, HALF, HALF);
        allTime += getPeriodWithPrint("Выполнили слияние за %d мс%n");
        System.out.printf("Общее время работы второго метода: %d мс%n", allTime);
    }

    private static long getPeriodWithPrint(String message) {
        long period = System.currentTimeMillis() - currentTimeMs;
        System.out.printf(message, period);
        currentTimeMs = System.currentTimeMillis();
        return period;
    }

    static void evaluateArrayValue(float[] arr, int i) {
        arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5.0f) * Math.cos(0.2f + i / 5.0f) * Math.cos(0.4f + i / 2.0f));
    }
}
