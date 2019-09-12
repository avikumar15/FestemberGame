package com.example.mmm;

import android.support.annotation.NonNull;

import static com.example.mmm.Game.MOVE_DOWN_RATE;
import static com.example.mmm.GameUtils.EXT_PADDING;
import static com.example.mmm.GameUtils.POINTER_RADIUS;
import static com.example.mmm.GameUtils.distance;
import static com.example.mmm.GameUtils.getRandomSign;

public class RotatingObstacle implements Obstacle {
    private final float obstacleRadius = 85.0f, THETA_RATE = (float) Math.PI / 50.0f;
    private float cx, cy, orbitRadius, theta;
    private boolean isClockwise, isAlive = false;
    private Game game;
    // obstacleRadius -> Radius of each of the two rotating circles.
    // orbitRadius -> Radius of the circle around which the two circles orbit.

    public RotatingObstacle(float cx, float cy, float orbitRadius, Game game){
        this.cx = cx;
        this.cy = cy;
        this.orbitRadius = orbitRadius;
        this.game = game;

        /* Initially the obstacle is alive.
           This goes false as soon as obstacle crosses the screen in y direction.
        */
        isAlive = true;
        theta = (float) Math.PI / 2;
        isClockwise = getRandomSign();

    }

    // These functions return the center coordinates of the two circles (rotating pair)
    public float getObstacleCx1() { return cx + (float) (orbitRadius * Math.sin(theta)); }

    public float getObstacleCy1() { return cy + (float) (orbitRadius * Math.cos(theta)); }

    public float getObstacleCx2() { return cx - (float) (orbitRadius * Math.sin(theta)); }

    public float getObstacleCy2() { return cy - (float) (orbitRadius * Math.cos(theta)); }

    public float getObstacleRadius() { return obstacleRadius; }

    public float getOrbitRadius() { return orbitRadius; }

    @Override
    public void update() {
        // Rotational Motion
        if (isClockwise){
            theta += THETA_RATE;
        } else {
            theta -= THETA_RATE;
        }
    }

    @Override
    public boolean isInside(float x, float y){
        return (
                distance(getObstacleCx1(), getObstacleCy1(), x, y) <= obstacleRadius + POINTER_RADIUS ||
                        distance(getObstacleCx2(), getObstacleCy2(), x, y) <= obstacleRadius + POINTER_RADIUS
        );
    }

    @Override
    public void moveDown(){
        cy += game.moveDownSpeed;
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
