package com.example.mmm;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.mmm.GameUtils.getRandomObstacleType;
import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE;
import static com.example.mmm.Obstacle.ROTATING_OBSTACLE;

public class Game {
    private float width, height, minDimension;
    public final static float EXT_PADDING = 25.0f;
    public final static float MOVE_DOWN_RATE = 7.0f;
    public float thresholdHeight;
    private List<Obstacle> obstacles = new ArrayList<>();
    private final static String TAG = "Game";

    public Game(float width, float height){
        this.width = width;
        this.height = height;
        thresholdHeight = height * 0.5f;
        minDimension = Math.min(width, height);
        addObstacle();
    }

    public void addObstacle(){
        String obstacleType = getRandomObstacleType();
        Obstacle obstacle;
        float cx;
        switch (obstacleType){
            case ROTATING_OBSTACLE:
                obstacle = new RotatingObstacle(width/2, EXT_PADDING, minDimension * 0.37f, Game.this);
                break;
            case HORIZONTAL_OBSTACLE:
                cx = EXT_PADDING + HorizontalObstacle.obstacleWidth + (width - 2 * EXT_PADDING - HorizontalObstacle.obstacleWidth) * (float) Math.random();
                obstacle = new HorizontalObstacle(cx, EXT_PADDING, Game.this);
                break;
            default:
                cx = EXT_PADDING + HorizontalObstacle.obstacleWidth + (width - 2 * EXT_PADDING - HorizontalObstacle.obstacleWidth) * (float) Math.random();
                obstacle = new HorizontalObstacle(cx, EXT_PADDING, Game.this);
                break;
        }
        obstacles.add(obstacle);
    }

    public List<Obstacle> getObstacles(){
        return obstacles;
    }

    public void update(){

        for (Obstacle obstacle : obstacles){
            obstacle.update();
            obstacle.moveDown();
        }
        if (obstacles.size() == 0){
            Log.d(TAG, "No Obstacles now");
            addObstacle();
        }
        else if (obstacles.get(obstacles.size()-1).getTop() >= thresholdHeight){
            Log.d(TAG, "Last obstacle crossed threshold. Can generate obstacle");
            addObstacle();
        }
        if (!obstacles.get(0).isAlive()){
            Log.d(TAG, "First obstacle crossed boundary.");
            obstacles.remove(0);
        }
    }

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

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }
}
