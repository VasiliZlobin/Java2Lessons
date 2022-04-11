package com.vasili_zlobin.lesson1;

public class Cat implements CanRunAndJump {
    private String nick;
    private int canRun;
    private double canJump;

    public Cat(String nick, int canRun, double canJump) {
        this.nick = nick;
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
        return "Кот " + nick;
    }
}
