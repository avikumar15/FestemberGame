package com.example.mmm;

public class DisableCollisionsPowerup implements Powerup {
    private final static float TIME_DURATION = 100f;
    private final static float height = 200f;
    private float cx, cy;
    private boolean isPicked, canPick;
    private Game game;

    public DisableCollisionsPowerup(float cx, float cy, Game game){
        this.cx = cx;
        this.cy = cy;
        this.game = game;

        isPicked = false;
        canPick = true;
    }

    @Override
    public boolean disableCollisions() { return true; }

    @Override
    public float getTimeDuration() { return TIME_DURATION; }

    @Override
    public void affectGameSpeed() { }

    @Override
    public void affectRateOfObstacles() { }

    @Override
    public boolean isPicked() { return isPicked; }

    @Override
    public boolean canPick() { return canPick; }

    @Override
    public float getCx() { return cx; }

    @Override
    public float getCy() { return cy; }

    @Override
    public float getTop() { return cy - height / 2; }

    @Override
    public float getBottom() { return cy + height / 2; }
}
