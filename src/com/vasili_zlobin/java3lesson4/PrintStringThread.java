package com.vasili_zlobin.java3lesson4;

public class PrintStringThread extends Thread {
    private final int waitIndex;

    public PrintStringThread(int waitIndex) {
        super();
        this.waitIndex = waitIndex;
    }

    @Override
    public void run() {
        try {
            Homework4_1.concurrentPrint(waitIndex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
