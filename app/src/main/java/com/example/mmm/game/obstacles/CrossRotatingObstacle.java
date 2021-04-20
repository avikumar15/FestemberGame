package com.example.mmm.game.obstacles;

import com.example.mmm.game.Game;

import static com.example.mmm.game.utils.GameUtils.EXT_PADDING;
import static com.example.mmm.game.utils.GameUtils.POINTER_RADIUS;
import static com.example.mmm.game.utils.GameUtils.distance;
import static com.example.mmm.game.utils.GameUtils.getRandomSign;
import static com.example.mmm.game.utils.GameUtils.getRandomSignProbability;

public class CrossRotatingObstacle implements Obstacle {

    private float cx, cy, theta;
    public final static float obstacleThickness = 80.0f, orbitRadius = 300.0f, obstacleCenterRadius = 90.0f;
    public final static float HORIZONTAL_MOVE_RATE = 10.0f, THETA_RATE = (float) Math.PI / 45.0f;
    private boolean isAlive, isMovingRight, isClockwise, hasDoubleLines, isTranslating;
    private boolean isInvisible = false;
    // hasDoubleLines -> Whether or not the obstacle has double lines (cross shape) or not (Single line).
    // isTranslating -> The obstacle will also move horizontally if this is set to true.

    private Game game;
    private float rotatedX, rotatedY, rotatedAngle, distance;

    /**
     * @param cx x coordinate of center of the obstacle
     * @param cy y coordinate of center of the obstacle
     * @param game The game instance for getting width and height
     */
    public CrossRotatingObstacle(float cx, float cy, Game game){
        this.cx = cx;
        this.cy = cy;
        this.game = game;

        /* Initially the obstacle is alive.
           This goes false as soon as obstacle crosses the screen in y direction.
        */
        isAlive = true;
        isClockwise = true;
        hasDoubleLines = getRandomSign();
        isTranslating = getRandomSignProbability(0.75); // 75% probability for translation in x-axis.
        if (isTranslating){
            isMovingRight = getRandomSign(); // If it translating, 50% probability for initially moving to the right side.
        }
        theta = 0f;

    }

    @Override
    public void moveDown() {
        cy += game.moveDownSpeed;
        if (getTop() >= game.getHeight() - EXT_PADDING){
            isAlive = false;
        }
    }

    @Override
    public void update() {
        // Horizontal translation motion
        if (isTranslating) {
            if (isMovingRight) {
                cx += HORIZONTAL_MOVE_RATE;
            } else {
                cx -= HORIZONTAL_MOVE_RATE;
            }
        }

        // Horizontal Bounce action if pointer goes to either end, by changing isMovingRight
        if (getLeft() <= EXT_PADDING){
            isMovingRight = true;
            isClockwise = !isClockwise;
        }
        else if (getRight() >= game.getWidth() - EXT_PADDING){
            isMovingRight = false;
            isClockwise = !isClockwise;
        }

        // Rotational Motion
        if (isClockwise){
            theta += THETA_RATE;
        } else {
            theta -= THETA_RATE;
        }
    }

    @Override
    public float getTop() {
        return cy - orbitRadius;
    }

    @Override
    public float getBottom() {
        return cy + orbitRadius;
    }

    public float getLeft(){
        return cx - orbitRadius;
    }
    public float getRight(){
        return cx + orbitRadius;
    }

    /**
     * This rotates the axes and each points in the current coordinate system by angle theta in the anticlockwise direction.
     * After rotation of axes, it is just a matter of checking whether the point lies inside the rectangle(s).
     * @param x x coordinate of the pointer
     * @param y y coordinate of the pointer
     * @return Checks whether the pointer lies inside the obstacle (rotating cross).
     */
    @Override
    public boolean isInside(float x, float y) {
        distance = distance(x, y, cx, cy);
        // To check whether the pointer lies inside a small circle at the center.
        if (distance <= obstacleCenterRadius + POINTER_RADIUS){
            return true;
        }
        if (x == cx){
            rotatedAngle = (float) Math.PI / 2;
        }
        else {
            rotatedAngle = (float) Math.atan((- cy + y) / (cx - x));
        }
        if (x <= cx && y >= cy){
            rotatedAngle -= Math.PI;
        }
        else if (x <= cx && y < cy){
            rotatedAngle += Math.PI;
        }
        rotatedX = cx + distance * (float) Math.cos(rotatedAngle + theta);
        rotatedY = cy - distance * (float) Math.sin(rotatedAngle + theta);
        // rotatedX -> x coordinate in the new coordinate system of x after rotating the actual (previous) axes by angle theta.
        // rotatedY -> y coordinate in the new coordinate system of y after rotating the actual (previous) axes by angle theta.

        // If there is only one rectangle.
        if (!hasDoubleLines) {
            return (
                    rotatedX >= cx - orbitRadius - POINTER_RADIUS &&
                    rotatedX <= cx + orbitRadius + POINTER_RADIUS &&
                    rotatedY >= cy - obstacleThickness / 2 - POINTER_RADIUS &&
                    rotatedY <= cy + obstacleThickness / 2 + POINTER_RADIUS
            );
        }
        // If hasDoubleLines is true, then it has two rectangles. So checks are performed for two rectangles.
        else {
            return (
                    rotatedX >= cx - orbitRadius - POINTER_RADIUS &&
                    rotatedX <= cx + orbitRadius + POINTER_RADIUS &&
                    rotatedY >= cy - obstacleThickness / 2 - POINTER_RADIUS &&
                    rotatedY <= cy + obstacleThickness / 2 + POINTER_RADIUS
            ) || (
                    rotatedX >= cx - obstacleThickness / 2 - POINTER_RADIUS &&
                    rotatedX <= cx + obstacleThickness / 2 + POINTER_RADIUS &&
                    rotatedY >= cy - orbitRadius - POINTER_RADIUS &&
                    rotatedY <= cy + orbitRadius + POINTER_RADIUS
            );
        }

    }

    @Override
    public boolean isAlive() { return isAlive; }

    @Override
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    @Override
    public boolean isInvisible() {
        return isInvisible;
    }

    @Override
    public void setInvisible() {
        isInvisible = true;
    }

    public boolean hasDoubleLines() { return hasDoubleLines; }

    public float getTheta() { return theta; }

    @Override
    public float getCx() { return cx; }

    @Override
    public float getCy() { return cy; }

    public float getObstacleCenterRadius() { return obstacleCenterRadius; }

    public float getObstacleThickness() { return obstacleThickness; }

    public float getOrbitRadius() { return orbitRadius; }

    @Override
    public String getObstacleType() { return CROSS_ROTATING_OBSTACLE; }
}
