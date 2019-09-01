package com.example.mmm;

import android.support.annotation.NonNull;

import static com.example.mmm.Game.EXT_PADDING;
import static com.example.mmm.Game.MOVE_DOWN_RATE;
import static com.example.mmm.GameUtils.getRandomSign;

public class RotatingObstacle implements Obstacle {
    private final float obstacleRadius = 85.0f, THETA_RATE = (float) Math.PI / 50.0f;
    private float cx, cy, orbitRadius, theta;
    private boolean isClockwise, isAlive = false;
    private Game game;

    public RotatingObstacle(float cx, float cy, float orbitRadius, Game game){
        this.cx = cx;
        this.cy = cy;
        this.orbitRadius = orbitRadius;
        this.game = game;

        isAlive = true;
        theta = (float) Math.PI / 2;
        isClockwise = getRandomSign();

    }

    public float getObstacleCx1() { return cx + (float) (orbitRadius * Math.sin(theta)); }

    public float getObstacleCy1() { return cy + (float) (orbitRadius * Math.cos(theta)); }

    public float getObstacleCx2() { return cx - (float) (orbitRadius * Math.sin(theta)); }

    public float getObstacleCy2() { return cy - (float) (orbitRadius * Math.cos(theta)); }

    public float getObstacleRadius() { return obstacleRadius; }

    public float getOrbitRadius() { return orbitRadius; }

    @Override
    public void update() {
        if (isClockwise){
            theta += THETA_RATE;
        } else {
            theta -= THETA_RATE;
        }
    }

    @Override
    public boolean isInside(float x, float y){
        if (x >= getObstacleCx1() - getObstacleRadius() && x <= getObstacleCx1() + getObstacleRadius() && y >= getObstacleCy1() - getObstacleRadius() && y <= getObstacleCy1() + getObstacleRadius()){
            return true;
        }
        else if (x >= getObstacleCx2() - getObstacleRadius() && x <= getObstacleCx2() + getObstacleRadius() && y >= getObstacleCy2() - getObstacleRadius() && y <= getObstacleCy2() + getObstacleRadius()){
            return true;
        }
        return false;
    }

    @Override
    public void moveDown(){
        cy += MOVE_DOWN_RATE;
        if (getTop() >= game.getHeight() - EXT_PADDING){
            isAlive = false;
        }
    }

    @Override
    public boolean isAlive() { return isAlive; }

    @Override
    public float getCx() { return cx; }

    @Override
    public float getCy() { return cy; }

    @Override
    public float getTop() {
        return cy - orbitRadius - obstacleRadius;
    }

    @Override
    public float getBottom() {
        return cy + orbitRadius + obstacleRadius;
    }

    @Override
    public String getObstacleType() { return ROTATING_OBSTACLE; }

    @NonNull
    @Override
    public String toString() {
        return "Rotating Obstacle at cx = " + cx + ", cy = " + cy;
    }
}
