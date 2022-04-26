package com.vasili_zlobin.lesson5;

public class EvaluateArrayThread extends Thread {
    private final float[] array;

    public EvaluateArrayThread(float[] array) {
        super();
        this.array = array;
    }

    @Override
    public void run() {
        MainLesson5.evaluateArrayValues(array);
    }
}
