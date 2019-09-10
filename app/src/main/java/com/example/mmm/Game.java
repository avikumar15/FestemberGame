package com.example.mmm;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.mmm.GameUtils.EXT_PADDING;
import static com.example.mmm.GameUtils.FRAME_RECT_SPEED;
import static com.example.mmm.GameUtils.FRAME_SPEED_RATE;
import static com.example.mmm.GameUtils.MAX_SPEED;
import static com.example.mmm.GameUtils.SCORE_EACH_OBSTACLE;
import static com.example.mmm.GameUtils.SCORE_INCREASE_RATE;
import static com.example.mmm.GameUtils.getRandomObstacleType;
import static com.example.mmm.Obstacle.CROSS_ROTATING_OBSTACLE;
import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE;
import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE_SET;
import static com.example.mmm.Obstacle.ROTATING_OBSTACLE;

public class Game {
    private float width, height, minDimension, score;
    public float thresholdHeight;
    private List<Obstacle> obstacles = new ArrayList<>();
    private final static String TAG = "Game";
    public static float MOVE_DOWN_RATE;

    public Game(float width, float height){
        this.width = width;
        this.height = height;
        thresholdHeight = height * 0.5f; // If the topmost obstacle crosses this specified height, an obstacle would be generated.
        minDimension = Math.min(width, height);
        score = 0;
        FRAME_RECT_SPEED = 11.0f;
        MOVE_DOWN_RATE = FRAME_RECT_SPEED;
        addObstacle();
    }

    /**
     * Adds an obstacle randomly in the game
     */
    public void addObstacle(){
        String obstacleType = getRandomObstacleType();
        Obstacle obstacle;
        float cx;
        switch (obstacleType){
            case ROTATING_OBSTACLE:
                obstacle = new RotatingObstacle(width/2, EXT_PADDING, minDimension * 0.33f, Game.this);
                break;
            case HORIZONTAL_OBSTACLE:
                cx = EXT_PADDING + HorizontalObstacle.obstacleWidth + (width - 2 * EXT_PADDING - HorizontalObstacle.obstacleWidth) * (float) Math.random();
                // cx -> random value from padding+obstacleWidth to width-padding-obstacleWidth
                obstacle = new HorizontalObstacle(cx, EXT_PADDING, Game.this);
                break;
            case CROSS_ROTATING_OBSTACLE:
                obstacle = new CrossRotatingObstacle(width / 2, EXT_PADDING, Game.this);
                break;
            case HORIZONTAL_OBSTACLE_SET:
                obstacle = new HorizontalObstacleSet(width/2, EXT_PADDING, Game.this);
                break;
            default:
                cx = EXT_PADDING + HorizontalObstacle.obstacleWidth + (width - 2 * EXT_PADDING - HorizontalObstacle.obstacleWidth) * (float) Math.random();
                // cx -> random value from padding+obstacleWidth to width-padding-obstacleWidth
                obstacle = new HorizontalObstacle(cx, EXT_PADDING, Game.this);
                break;
        }
        obstacles.add(obstacle);
    }

    public List<Obstacle> getObstacles(){
        return obstacles;
    }

    /**
     * Updates the position of the obstacles in the game and removes it if goes beyond the screen.
     * Calls addObstacle if the topmost obstacle crosses the threshold height.
     */
    public void update(){
       if(FRAME_RECT_SPEED <= MAX_SPEED) {
           FRAME_RECT_SPEED += FRAME_SPEED_RATE;
           MOVE_DOWN_RATE += FRAME_SPEED_RATE;
       }
        score += SCORE_INCREASE_RATE;
        for (Obstacle obstacle : obstacles){
            obstacle.update();
            obstacle.moveDown();
        }
        if (obstacles.size() == 0){
            addObstacle();
        }
        else if (obstacles.get(obstacles.size()-1).getTop() >= thresholdHeight){
            // Last obstacle crossed threshold. Can generate obstacle
            addObstacle();
        }
        if (!obstacles.get(0).isAlive()) {
            // First obstacle crossed boundary (can be removed)
            score += SCORE_EACH_OBSTACLE;
            obstacles.remove(0);
        }

    }

    /**
     * This function was not used. Instead directly the check was performed in drawObstacles in GamePlay.
     * @param x x coordinate of the pointer
     * @param y y coordinate of the pointer
     * @return Returns whether the game is over or not
     */
    public boolean checkGameOver(float x, float y){
        if (obstacles == null){
            return true;
        }
        else if (obstacles.size() == 0) {
            return false;
        }
        else {
            for (Obstacle obstacle : obstacles) {
                if (obstacle.isInside(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public float getScore() { return score; }

    public int getScoreInt() { return (int) Math.floor(score); }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
}
