package com.vasili_zlobin.java3lesson4;

public class Homework4_1 {
    private static final int ITERATIONS = 5;
    private static final String[] arrayStrings = new String[]{"A", "B", "C"};
    private static final Object monitor = new Object();
    private static int currentIndex;

    public static void main(String[] args) {
        currentIndex = 0;
        for (int i = 0; i < arrayStrings.length; i++) {
            PrintStringThread thread = new PrintStringThread(i);
            thread.start();
        }
    }

    public static void concurrentPrint(int nextIndex) throws InterruptedException {
        synchronized (monitor) {
            for (int i = 0; i < ITERATIONS; i++) {
                while (currentIndex != nextIndex) {
                    monitor.wait();
                }
                System.out.print(arrayStrings[nextIndex]);
                currentIndex++;
                if (currentIndex >= arrayStrings.length) {
                    currentIndex = 0;
                }
                monitor.notifyAll();
            }
        }
    }
}