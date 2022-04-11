package com.vasili_zlobin.lesson1;

public class Robot implements CanRunAndJump {
    private String model;
    private int canRun;
    private double canJump;

    public Robot(String model, int canRun, double canJump) {
        this.model = model;
        this.canRun = Math.max(canRun, 0);
        this.canJump = Math.max(canJump, 0);
    }

    @Override
    public int run() {
        return canRun;
    }

    @Override
    public double jump() {
        return canJump;
    }

    @Override
    public String getInfo() {
        return "Робот " + model;
    }
}
