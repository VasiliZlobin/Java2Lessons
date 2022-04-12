package com.vasili_zlobin.lesson1;

public class Wall implements Obstacle {
    private static final double MIN_HEIGHT = 0.1;
    private double height;

    public Wall(double height) {
        this.height = Math.max(height, MIN_HEIGHT);
    }

    @Override
    public boolean isSuccess(Member member) {
        boolean success = member.jump() >= height;
        String prefix = success ? "" : "не ";
        System.out.printf("%s %sсмог перепрыгнуть %s%n", member.getInfo(), prefix, height);
        return success;
    }
}
