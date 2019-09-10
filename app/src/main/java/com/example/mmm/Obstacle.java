package com.example.mmm;

public interface Obstacle {
    public final static String ROTATING_OBSTACLE = "RotatingObstacle", HORIZONTAL_OBSTACLE = "HorizontalObstacle", CROSS_ROTATING_OBSTACLE = "CrossRotatingObstacle", HORIZONTAL_OBSTACLE_SET = "HorizontalObstacleSet";
    public String getObstacleType();
    public float getCx();
    public float getCy();
    public float getTop();
    public float getBottom();

    /**
     * Increases the y coordinate by move down rate.
     * This is same for all the types of obstacles.
     */
    public void moveDown();

    /**
     * Requires modification of logic for each type of obstacle implemented
     * @param x x coordinate of the pointer
     * @param y y coordinate of the pointer
     * @return Checks whether the pointer lies inside the obstacle.
     */
    public boolean isInside(float x, float y);
    public boolean isAlive();

    /**
     * This depends on the logic of moving the obstacle.
     * eg. Circular motion logic for rotating pair, Changing cx for horizontal movement etc.
     */
    public void update();
}
