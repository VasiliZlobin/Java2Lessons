package com.vasili_zlobin.lesson1;

public class Human implements Member {
    private String name;
    private double canJump;
    private int canRun;

    public Human(String name, int canRun, double canJump) {
        this.name = name;
        this.canJump = Math.max(canJump, 0);
        this.canRun = Math.max(canRun, 0);
    }

    public String getInfo() {
        return "Человек " + name;
    }

    @Override
    public int run() {
        return canRun;
    }

    @Override
    public double jump() {
        return canJump;
    }
}
