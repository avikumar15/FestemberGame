package com.example.mmm;

import java.util.ArrayList;
import java.util.List;

import static com.example.mmm.GameUtils.EXT_PADDING;
import static com.example.mmm.Game.MOVE_DOWN_RATE;
import static com.example.mmm.GameUtils.POINTER_RADIUS;
import static com.example.mmm.GameUtils.distance;
import static com.example.mmm.GameUtils.getRandomSign;
import static com.example.mmm.GameUtils.getRandomSignProbability;

public class CrossRotatingObstacle implements Obstacle {

    private float cx, cy, theta;
    final static float obstacleThickness = 80.0f, orbitRadius = 300.0f, obstacleCenterRadius = 90.0f;
    final static float HORIZONTAL_MOVE_RATE = 10.0f, THETA_RATE = (float) Math.PI / 45.0f;
    private boolean isAlive, isMovingRight, isClockwise, hasDoubleLines, isTranslating;
    private Game game;
    private float rotatedX, rotatedY, rotatedAngle, distance;

    public CrossRotatingObstacle(float cx, float cy, Game game){
        this.cx = cx;
        this.cy = cy;
        this.game = game;

        isAlive = true;
        isClockwise = true;
        hasDoubleLines = getRandomSign();
        isTranslating = getRandomSignProbability(0.75);
        if (isTranslating){
            isMovingRight = getRandomSign();
        }
        theta = 0f;

    }

    @Override
    public void moveDown() {
        cy += MOVE_DOWN_RATE;
        if (getTop() >= game.getHeight() - EXT_PADDING){
            isAlive = false;
        }
    }

    @Override
    public void update() {
        if (isTranslating) {
            if (isMovingRight) {
                cx += HORIZONTAL_MOVE_RATE;
            } else {
                cx -= HORIZONTAL_MOVE_RATE;
            }
        }

        if (getLeft() <= EXT_PADDING){
            isMovingRight = true;
            isClockwise = !isClockwise;
        }
        else if (getRight() >= game.getWidth() - EXT_PADDING){
            isMovingRight = false;
            isClockwise = !isClockwise;
        }

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

    @Override
    public boolean isInside(float x, float y) {
        distance = distance(x, y, cx, cy);
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
        if (!hasDoubleLines) {
            return (
                    rotatedX >= cx - orbitRadius - POINTER_RADIUS &&
                    rotatedX <= cx + orbitRadius + POINTER_RADIUS &&
                    rotatedY >= cy - obstacleThickness / 2 - POINTER_RADIUS &&
                    rotatedY <= cy + obstacleThickness / 2 + POINTER_RADIUS
            );
        }
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

    public boolean hasDoubleLines() { return hasDoubleLines; }

    public float getTheta() { return theta; }

    @Override
    public float getCx() { return cx; }

    @Override
    public float getCy() { return cy; }

    public float getObstacleCenterRadius() { return obstacleCenterRadius; }

    public float getObstacleThickness() { return obstacleThickness; }

    public float getOrbitRadius() { return orbitRadius; }

//    public float getRotatedAngle() { return rotatedAngle; }
//
//    public float getRotatedX() { return rotatedX; }
//
//    public float getRotatedY() { return rotatedY; }

    @Override
    public String getObstacleType() { return CROSS_ROTATING_OBSTACLE; }
}
