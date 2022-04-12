package com.vasili_zlobin.lesson1;

public class Treadmill implements Obstacle {
    private static final int MIN_LENGTH = 10;
    private int length;

    public Treadmill(int length) {
        this.length = Math.max(length, MIN_LENGTH);
    }

    @Override
    public boolean isSuccess(Member member) {
        boolean success = member.run() >= length;
        String prefix = success ? "" : "не ";
        System.out.printf("%s %sсмог пробежать %d%n", member.getInfo(), prefix, length);
        return success;
    }
}
