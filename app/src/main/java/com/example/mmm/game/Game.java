package com.example.mmm.game;

import android.util.Log;

import com.example.mmm.game.obstacles.CrossRotatingObstacle;
import com.example.mmm.game.obstacles.HorizontalObstacle;
import com.example.mmm.game.obstacles.HorizontalObstacleSet;
import com.example.mmm.game.obstacles.LayeredHorizontalObjects;
import com.example.mmm.game.obstacles.MutuallyAttractedObstacles;
import com.example.mmm.game.obstacles.Obstacle;
import com.example.mmm.game.obstacles.RotatingObstacle;
import com.example.mmm.game.powerups.DisableCollisionsPowerup;
import com.example.mmm.game.powerups.Powerup;
import com.example.mmm.game.powerups.SlowGamePowerup;

import java.util.ArrayList;
import java.util.List;

import static com.example.mmm.game.utils.GameUtils.EXT_PADDING;
import static com.example.mmm.game.utils.GameUtils.FRAME_SPEED_RATE;
import static com.example.mmm.game.utils.GameUtils.INITIAL_FRAME_RECT_SPEED;
import static com.example.mmm.game.utils.GameUtils.MAX_SPEED;
import static com.example.mmm.game.utils.GameUtils.SCORE_EACH_OBSTACLE;
import static com.example.mmm.game.utils.GameUtils.SCORE_INCREASE_RATE;
import static com.example.mmm.game.utils.GameUtils.getRandomObstacleType;
import static com.example.mmm.game.utils.GameUtils.getRandomSignProbability;
import static com.example.mmm.game.obstacles.Obstacle.CROSS_ROTATING_OBSTACLE;
import static com.example.mmm.game.obstacles.Obstacle.HORIZONTAL_LAYERED_OBSTACLE;
import static com.example.mmm.game.obstacles.Obstacle.HORIZONTAL_OBSTACLE_SET;
import static com.example.mmm.game.obstacles.Obstacle.MUTUALLY_ATTRACTED_OBSTACLE;
import static com.example.mmm.game.obstacles.Obstacle.ROTATING_OBSTACLE;
import static com.example.mmm.game.powerups.Powerup.DISABLE_COLLISIONS_POWERUP;
import static com.example.mmm.game.powerups.Powerup.SLOW_GAME_POWERUP;

public class Game {
    private float width, height, minDimension, score;
    public float thresholdHeight;
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Powerup> powerups = new ArrayList<>();
    private final static String TAG = "Game";
    public float moveDownSpeed;
    public float frameRectSpeed;  // Change this to adjust moving speed of background. This is just for the background
    private boolean disableCollisions;
    private float powerUpProbability = 0.012f; // Probability of getting a powerup randomly in the game.
    private int maxPowerupsRate = 1;
    private int disableCollisionsTime = 0;

    public Game(float width, float height){
        this.width = width;
        this.height = height;

        thresholdHeight = height * 0.75f; // If the topmost obstacle crosses this specified height, an obstacle would be generated.
        minDimension = Math.min(width, height);
        score = 0;

        frameRectSpeed = INITIAL_FRAME_RECT_SPEED;
        moveDownSpeed = INITIAL_FRAME_RECT_SPEED;

        disableCollisions = false;

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
            // cx -> random value from padding+obstacleWidth to width-padding-obstacleWidth
            case CROSS_ROTATING_OBSTACLE:
                obstacle = new CrossRotatingObstacle(width / 2, EXT_PADDING, Game.this);
                break;
            case HORIZONTAL_OBSTACLE_SET:
                obstacle = new HorizontalObstacleSet(width/2, EXT_PADDING, Game.this);
                break;
            case HORIZONTAL_LAYERED_OBSTACLE :
                cx = EXT_PADDING + LayeredHorizontalObjects.obstacleWidth + (width - 2 * EXT_PADDING - LayeredHorizontalObjects.obstacleWidth) * (float) Math.random();
                obstacle = new LayeredHorizontalObjects(cx,EXT_PADDING,Game.this);
                break;

            case MUTUALLY_ATTRACTED_OBSTACLE :
                obstacle = new MutuallyAttractedObstacles(width/2 - MutuallyAttractedObstacles.obstacleWidth/2f, EXT_PADDING, Game.this);
                break;

            // also HORIZONTAL_OBSTACLE.
            default:
                cx = EXT_PADDING + HorizontalObstacle.obstacleWidth + (width - 2 * EXT_PADDING - HorizontalObstacle.obstacleWidth) * (float) Math.random();
                // cx -> random value from padding+obstacleWidth to width-padding-obstacleWidth
                obstacle = new HorizontalObstacle(cx, EXT_PADDING, Game.this);
                break;
        }
        obstacles.add(obstacle);
    }

    /**
     * Adds a power-up randomly in the game
     */
    public void addPowerup(){
        String powerupType = DISABLE_COLLISIONS_POWERUP;
        float cx;
        Powerup powerup;
        switch (powerupType){
            case DISABLE_COLLISIONS_POWERUP:
                cx = EXT_PADDING + HorizontalObstacle.obstacleWidth + (width - 2 * EXT_PADDING - HorizontalObstacle.obstacleWidth) * (float) Math.random();
                // cx -> random value from padding+obstacleWidth to width-padding-obstacleWidth
                powerup = new DisableCollisionsPowerup(cx, EXT_PADDING - DisableCollisionsPowerup.powerupHeight / 2, Game.this);
                break;
            case SLOW_GAME_POWERUP:
                cx = EXT_PADDING + HorizontalObstacle.obstacleWidth + (width - 2 * EXT_PADDING - HorizontalObstacle.obstacleWidth) * (float) Math.random();
                // cx -> random value from padding+obstacleWidth to width-padding-obstacleWidth
                powerup = new SlowGamePowerup(cx, EXT_PADDING - SlowGamePowerup.powerupHeight / 2, Game.this);
                break;
            default:
                cx = EXT_PADDING + HorizontalObstacle.obstacleWidth + (width - 2 * EXT_PADDING - HorizontalObstacle.obstacleWidth) * (float) Math.random();
                // cx -> random value from padding+obstacleWidth to width-padding-obstacleWidth
                powerup = new SlowGamePowerup(cx, EXT_PADDING - DisableCollisionsPowerup.powerupHeight / 2, Game.this);
                break;
        }
        powerups.add(powerup);
    }

    public List<Obstacle> getObstacles(){
        return obstacles;
    }

    public List<Powerup> getPowerups() { return powerups; }

    /**
     * Updates the position of the obstacles in the game and removes it if goes beyond the screen.
     * Calls addObstacle if the topmost obstacle crosses the threshold height.
     */
    public void update(){
        if(frameRectSpeed <= MAX_SPEED) {
            frameRectSpeed += FRAME_SPEED_RATE;
            moveDownSpeed += FRAME_SPEED_RATE;
        }
        score += SCORE_INCREASE_RATE;
//        for (Obstacle obstacle : obstacles){
//            obstacle.update();
//            obstacle.moveDown();
//        }
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

        if (getRandomSignProbability(powerUpProbability) && powerups.size() < maxPowerupsRate){
            Log.d(TAG, "Powerups size: " + powerups.size() + ", can generate powerups");
            addPowerup();
        }
        if (powerups.size() > 0 && !powerups.get(0).canPick() && (!powerups.get(0).isPicked() || powerups.get(0).isPicked() && !powerups.get(0).isActive())){
            powerups.remove(0);
        }
    }

    public boolean isDisableCollisions() { return disableCollisions; }

    public void setDisableCollisions(boolean disableCollisions) { this.disableCollisions = disableCollisions; }

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

    public void setDisableCollisionsTime(int disableCollisionsTime) { this.disableCollisionsTime = disableCollisionsTime; }

    public int getDisableCollisionsTime() { return disableCollisionsTime; }
}