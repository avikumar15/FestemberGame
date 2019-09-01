package com.example.mmm;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private float width, height, minDimension;
    public final static float EXT_PADDING = 40.0f;
    public final static float MOVE_DOWN_RATE = 10.0f;
    private List<Obstacle> obstacles = new ArrayList<>();

    public Game(float width, float height){
        this.width = width;
        this.height = height;
        minDimension = Math.min(width, height);
        obstacles.add(new RotatingObstacle(width/2, EXT_PADDING, minDimension * 0.4f, Game.this));
    }

    public List<Obstacle> getObstacles(){
        return obstacles;
    }

    public void moveDown(){
        List<Obstacle> tempObstacles = new ArrayList<>();
        for (Obstacle obstacle : obstacles){
            obstacle.moveDown();
            if (obstacle.isAlive()){
                tempObstacles.add(obstacle);
            }
        }
        obstacles.clear();
        obstacles.addAll(tempObstacles);
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
