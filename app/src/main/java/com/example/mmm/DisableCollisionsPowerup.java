package com.example.mmm;

import static com.example.mmm.GameUtils.EXT_PADDING;
import static com.example.mmm.GameUtils.POINTER_RADIUS;

public class DisableCollisionsPowerup implements Powerup {
    private final static float TIME_DURATION = 1000f;
    private final static float powerupHeight = 200f, powerupWidth = 200f;
    private float timePicked;
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
        cy += game.moveDownSpeed;
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
    public float getTimePicked() {
        return timePicked;
    }

    @Override
    public void updateTimePicked() {
        ++timePicked;
        if (timePicked > TIME_DURATION){
            isActive = false;
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
    public float getTimeDuration() { return TIME_DURATION; }

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
