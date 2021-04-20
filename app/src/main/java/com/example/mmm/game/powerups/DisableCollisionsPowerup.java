package com.example.mmm.game.powerups;

import com.example.mmm.game.Game;

import static com.example.mmm.game.utils.GameUtils.EXT_PADDING;
import static com.example.mmm.game.utils.GameUtils.POINTER_RADIUS;
import static com.example.mmm.game.utils.GameUtils.POWERUP_SPEED_OBSTACLE_SPEED_RATIO;

public class DisableCollisionsPowerup implements Powerup {
    public final static int TIME_DURATION = 250;
    public final static float powerupHeight = 200f, powerupWidth = 200f;
    private int timePicked;
    private float cx, cy;
    private boolean canPick, isActive, isPicked;
    private Game game;

    public DisableCollisionsPowerup(float cx, float cy, Game game){
        this.cx = cx;
        this.cy = cy;
        this.game = game;

        canPick = true;
        isActive = false;

        timePicked = 0;
    }

    @Override
    public void moveDown() {
        cy += game.moveDownSpeed * POWERUP_SPEED_OBSTACLE_SPEED_RATIO;
        if (getTop() >= game.getHeight() - EXT_PADDING){
            canPick = false;
        }
    }

    @Override
    public boolean isInside(float x, float y) {
        return (
                x >= cx - powerupWidth/2f - POINTER_RADIUS &&
                        x <= cx + powerupWidth/2f + POINTER_RADIUS &&
                        y >= cy - powerupHeight/2f - POINTER_RADIUS &&
                        y <= cy + powerupHeight/2f + POINTER_RADIUS
        );
    }

    @Override
    public int getTimePicked() {
        return timePicked;
    }

    @Override
    public void updateTimePicked() {
        ++timePicked;
        if (timePicked > TIME_DURATION){
            isActive = false;
            isPicked = false;
        }
    }

    @Override
    public void setActive() {
        isActive = true;
    }

    @Override
    public void setPicked() {
        isPicked = true;
        isActive = true;
    }

    @Override
    public boolean disableCollisions() { return true; }

    @Override
    public int getTimeDuration() { return TIME_DURATION; }

    @Override
    public void affectGameSpeed() { }

    @Override
    public void affectRateOfObstacles() { }

    public boolean isPicked() { return isPicked; }

    @Override
    public boolean isActive() { return isActive; }

    @Override
    public boolean canPick() { return canPick; }

    @Override
    public float getCx() { return cx; }

    @Override
    public float getCy() { return cy; }

    @Override
    public float getTop() { return cy - powerupHeight / 2; }

    @Override
    public float getBottom() { return cy + powerupHeight / 2; }

    public float getLeft() { return cx - powerupWidth / 2; }

    public float getRight() { return cx + powerupWidth / 2; }

    @Override
    public String getPowerupType() {
        return DISABLE_COLLISIONS_POWERUP;
    }
}
