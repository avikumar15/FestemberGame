package com.example.mmm;

import static com.example.mmm.Obstacle.CROSS_ROTATING_OBSTACLE;
import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE;
import static com.example.mmm.Obstacle.ROTATING_OBSTACLE;

public class GameUtils {
    public final static float FRAME_RECT_SPEED = 11.0f;  // Change this to adjust background moving speed. This is just for the background
    public final static float EXT_PADDING = 25.0f;
    public final static float MOVE_DOWN_RATE = 7.0f; // This is the vertical speed of obstacles.
    public static final float POINTER_RADIUS = 60;
    public final static int TYPES_OF_OBSTACLES = 3;
    public final static float SCORE_INCREASE_RATE = 0.05f;
    public final static float SCORE_EACH_OBSTACLE = 1;
    public static boolean getRandomSign() { return Math.random() < 0.5; }
    public static boolean getRandomSignProbability(double probability){ return Math.random() < probability; }
    public static String getRandomObstacleType(){
        int i = (int) Math.floor(Math.random()*TYPES_OF_OBSTACLES);
        switch (i) {
            case 0: return ROTATING_OBSTACLE;
            case 1: return HORIZONTAL_OBSTACLE;
            case 2: return CROSS_ROTATING_OBSTACLE;
            default: return HORIZONTAL_OBSTACLE;
        }
    }
    public static float distance(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
