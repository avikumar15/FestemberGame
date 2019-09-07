package com.example.mmm;

import android.support.annotation.NonNull;

import static com.example.mmm.GameUtils.EXT_PADDING;
import static com.example.mmm.Game.MOVE_DOWN_RATE;
import static com.example.mmm.GameUtils.POINTER_RADIUS;
import static com.example.mmm.GameUtils.getRandomSign;

public class HorizontalObstacleSet implements Obstacle {
    private Game game;
    private float cx, cy;
    private boolean isAlive, isMovingRight;
    final static float HORIZONTAL_MOVE_RATE = 14.0f;
    final static float obstacleHeight = 300.0f;
    float obstacleWidth;

    public HorizontalObstacleSet(float cx, float cy, Game game){
        this.cx = cx;
        this.cy = cy;
        this.game = game;

        obstacleWidth = (game.getWidth() - 8 * POINTER_RADIUS) / 4;

        isAlive = true;
        isMovingRight = getRandomSign();
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
        if (isMovingRight){
            cx += HORIZONTAL_MOVE_RATE;
        } else{
            cx -= HORIZONTAL_MOVE_RATE;
        }

        if (cx <= 2 * obstacleWidth){
            isMovingRight = true;
        }
        else if (cx >= game.getWidth() - 2 * obstacleWidth){
            isMovingRight = false;
        }
    }

    @Override
    public boolean isInside(float x, float y) {
        return (
                x >= cx - obstacleWidth - POINTER_RADIUS &&
                x <= cx + obstacleWidth + POINTER_RADIUS &&
                y >= cy - obstacleHeight / 2 - POINTER_RADIUS &&
                y <= cy + obstacleHeight / 2 + POINTER_RADIUS
        ) || (
                x >= 0 &&
                x <= obstacleWidth + POINTER_RADIUS &&
                y >= cy - obstacleHeight / 2 - POINTER_RADIUS &&
                y <= cy + obstacleHeight / 2 + POINTER_RADIUS
        ) || (
                x >= game.getWidth() - obstacleWidth - POINTER_RADIUS &&
                x <= game.getWidth() &&
                y >= cy - obstacleHeight / 2 - POINTER_RADIUS &&
                y <= cy + obstacleHeight / 2 + POINTER_RADIUS
        );
    }

    @Override
    public float getCx() { return cx; }

    @Override
    public float getCy() { return cy; }

    @Override
    public float getTop() { return cy - obstacleHeight / 2; }

    public float getLeft() { return cx - obstacleWidth; }

    public float getRight() { return cx + obstacleWidth; }

    @Override
    public float getBottom() { return cy + obstacleHeight / 2; }

    public float getObstacleWidth() { return obstacleWidth; }

    @Override
    public boolean isAlive() { return isAlive; }

    @Override
    public String getObstacleType() { return HORIZONTAL_OBSTACLE_SET; }

    @NonNull
    @Override
    public String toString() {
        return "Horizontal Obstacle Set at cx = " + cx + ", cy = " + cy;
    }
}
