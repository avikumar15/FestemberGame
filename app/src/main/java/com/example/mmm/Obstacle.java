package com.example.mmm;

public interface Obstacle {
    public final static String ROTATING_OBSTACLE = "RotatingObstacle", HORIZONTAL_OBSTACLE = "HorizontalObstacle";
    public String getObstacleType();
    public float getCx();
    public float getCy();
    public float getTop();
    public float getBottom();
    public void moveDown();
    public boolean isInside(float x, float y);
    public boolean isAlive();
    public void update();
}
