package com.vasili_zlobin.lesson1;

public class TestLesson1 {
    public static void main(String[] args) {
        CanRunAndJump[] members = new CanRunAndJump[3];
        members[0] = new Cat("Барсик", 700, 0.5);
        members[1] = new Human("Атлет", 1000, 2.1);
        members[2] = new Robot("01", 500, 1);

        CheckSuccess[] obstacles = new CheckSuccess[4];
        obstacles[0] = new Treadmill(650);
        obstacles[1] = new Wall(0.45);
        obstacles[2] = new Treadmill(1000);
        obstacles[3] = new Wall(2);

        for (CanRunAndJump member : members) {
            for (CheckSuccess obstacle : obstacles) {
                if (!obstacle.isSuccess(member)) {
                    System.out.printf("%s сошел с дистанции!%n", member.getInfo());
                    break;
                }
            }
        }
    }
}
