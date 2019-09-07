package com.example.mmm;

import android.support.annotation.NonNull;

import static com.example.mmm.GameUtils.EXT_PADDING;
import static com.example.mmm.Game.MOVE_DOWN_RATE;
import static com.example.mmm.GameUtils.POINTER_RADIUS;
import static com.example.mmm.GameUtils.getRandomSign;

public class HorizontalObstacle implements Obstacle {
    private float cx, cy;
    private Game game;
    private boolean isAlive;
    private boolean isMovingRight;
     static float HORIZONTAL_MOVE_RATE = 20f;
    final static float obstacleHeight = 250.0f, obstacleWidth = 250.0f;

    public HorizontalObstacle(float cx, float cy, Game game){
        this.cx = cx;
        this.cy = cy;
        this.game = game;

        isAlive = true;
        isMovingRight = getRandomSign();
    }

    @Override
    public String getObstacleType() {
        return HORIZONTAL_OBSTACLE;
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
        cy += MOVE_DOWN_RATE;
        if (getTop() >= game.getHeight() - EXT_PADDING){
            isAlive = false;
        }
    }

    @Override
    public void update() {
        if (isMovingRight){
            cx += HORIZONTAL_MOVE_RATE;
        } else{
            cx -= HORIZONTAL_MOVE_RATE;
        }

        if (cx <= EXT_PADDING + obstacleWidth / 2.0f){
            isMovingRight = true;
        }
        else if (cx >= game.getWidth() - EXT_PADDING - obstacleWidth / 2.0f){
            isMovingRight = false;
        }
    }

    @Override
    public boolean isInside(float x, float y) {
        return (
                x >= cx - obstacleWidth/2f - POINTER_RADIUS &&
                x <= cx + obstacleWidth/2f + POINTER_RADIUS &&
                y >= cy - obstacleHeight/2f - POINTER_RADIUS &&
                y <= cy + obstacleHeight + POINTER_RADIUS
        );
    }

    @Override
    public boolean isAlive() { return isAlive; }

    @NonNull
    @Override
    public String toString() {
        return "Horizontal Obstacle at cx = " + cx + ", cy = " + cy;
    }
}
