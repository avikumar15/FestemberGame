package com.example.mmm;

import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE;
import static com.example.mmm.Obstacle.ROTATING_OBSTACLE;

public class GameUtils {
    public static boolean getRandomSign() { return Math.random() < 0.5; }
    public static String getRandomObstacleType(){
        int i = (int) Math.floor(Math.random()*2);
        switch (i) {
            case 0: return ROTATING_OBSTACLE;
            case 1: return HORIZONTAL_OBSTACLE;
            default: return HORIZONTAL_OBSTACLE;
        }
    }
}
