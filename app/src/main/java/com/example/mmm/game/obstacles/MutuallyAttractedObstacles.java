package com.example.mmm.game.obstacles;


import androidx.annotation.NonNull;

import com.example.mmm.game.Game;

import static com.example.mmm.game.utils.GameUtils.EXT_PADDING;
import static com.example.mmm.game.utils.GameUtils.POINTER_RADIUS;
import static com.example.mmm.game.utils.GameUtils.getRandomSign;

public class MutuallyAttractedObstacles implements Obstacle{

    private float cx, cy;
    private float acceleartionFactor;
    private Game game;
    private boolean isAlive;
    private boolean isMovingRight;
    static float HORIZONTAL_MOVE_RATE = 5f;
    public final static float obstacleHeight = 250.0f, obstacleWidth = 250.0f;
    private boolean isInvisible = false;

    public MutuallyAttractedObstacles(float cx, float cy, Game game){
        this.cx = cx;
        this.cy = cy;
        this.game = game;
        acceleartionFactor=0;
        /* Initially the obstacle is alive.
           This goes false as soon as obstacle crosses the screen in y direction.
        */
        isAlive = true;
        isMovingRight = getRandomSign();
    }

    @Override
    public String getObstacleType() {
        return MUTUALLY_ATTRACTED_OBSTACLE;
    }

    @Override
    public float getCx() { return cx; }

    @Override
    public float getCy() { return cy; }

    public float getLeft() { return cx - obstacleWidth / 2.0f; }

    public float getRight() { return cx + obstacleWidth / 2.0f; }

    @Override
    public float getTop() { return cy - obstacleHeight / 2.0f; }

    @Override
    public float getBottom() { return cy + obstacleHeight / 2.0f; }

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
        if (isMovingRight){
            cx += HORIZONTAL_MOVE_RATE + acceleartionFactor++;
        } else{
            cx -= HORIZONTAL_MOVE_RATE*4;
        }

        // Horizontal bounce action if pointer goes to either end, by changing isMovingRight
        if (cx <= EXT_PADDING + obstacleWidth / 2.0f){
            isMovingRight = true;
        }
        else if (cx >= game.getWidth()/2f - EXT_PADDING - obstacleWidth / 2.0f){
            isMovingRight = false;
            acceleartionFactor=0;
        }
    }

    @Override
    public boolean isInside(float x, float y) {
        return (
                (x >= cx - obstacleWidth/2f - POINTER_RADIUS &&
                        x <= cx + obstacleWidth/2f + POINTER_RADIUS &&
                        y >= cy - obstacleHeight/2f - POINTER_RADIUS &&
                        y <= cy + obstacleHeight/2f + POINTER_RADIUS) ||
                        (x >= cx - obstacleWidth/2f - POINTER_RADIUS + game.getWidth()-2*cx &&
                        x <= cx + obstacleWidth/2f + POINTER_RADIUS + game.getWidth()-2*cx &&
                        y >= cy - obstacleHeight/2f - POINTER_RADIUS &&
                        y <= cy + obstacleHeight/2f + POINTER_RADIUS)
        );
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

    @NonNull
    @Override
    public String toString() {
        return "Horizontal Obstacle at cx = " + cx + ", cy = " + cy;
    }
}
