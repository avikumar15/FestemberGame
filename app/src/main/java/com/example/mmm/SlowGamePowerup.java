package com.example.mmm;

import static com.example.mmm.GameUtils.FRAME_SPEED_RATE;
import static com.example.mmm.GameUtils.INITIAL_FRAME_RECT_SPEED;

public class SlowGamePowerup implements Powerup {
    private final static float TIME_DURATION = 100f;
    private final static float height = 200f;
    private float cx, cy;
    private boolean isPicked, canPick;
    private Game game;

    public SlowGamePowerup(float cx, float cy, Game game){
        this.cx = cx;
        this.cy = cy;
        this.game = game;

        isPicked = false;
        canPick = true;
    }

    @Override
    public void affectGameSpeed() {
        if (game.moveDownSpeed >= INITIAL_FRAME_RECT_SPEED + FRAME_SPEED_RATE){
            game.moveDownSpeed -= FRAME_SPEED_RATE;
        }
    }

    @Override
    public void affectRateOfObstacles() { }

    @Override
    public boolean disableCollisions() { return false; }

    @Override
    public boolean isPicked() { return isPicked; }

    @Override
    public boolean canPick() { return canPick; }

    @Override
    public float getTimeDuration() { return TIME_DURATION; }

    @Override
    public float getCx() { return cx; }

    @Override
    public float getCy() { return cy; }

    @Override
    public float getTop() { return cy - height / 2; }

    @Override
    public float getBottom() { return cy + height / 2; }
}
