package com.example.mmm;

public interface Powerup {
    public final static String SLOW_GAME_POWERUP = "SlowGamePowerup", DISABLE_COLLISIONS_POWERUP = "DisableCollisionsPowerup";

    /**
     * @return Time duration of effect of the powerup if picked.
     */
    public float getTimeDuration();

    /**
     * Effect on power-up on game speed.
     * Can be left blank, if power-up has no effect on game speed.
     */
    public void affectGameSpeed();

    /**
     * Effect on power-up on rate of obstacles.
     * Can be left blank, if power-up has no effect on rate of obstacles.
     */
    public void affectRateOfObstacles();

    /**
     * @return True if power-up disables collisions with obstacles.
     * False in all other cases.
     */
    public boolean disableCollisions();

    /**
     * @return True if picked and if time elapsed from picking is less than time duration. False in all other cases.
     */
    public boolean isPicked();

    /**
     * @return Whether or not power-up can be picked.
     */
    public boolean canPick();

    /**
     * @return x coordinate of centre point of the power-up.
     */
    public float getCx();

    /**
     * @return y coordinate of centre point of the power-up.
     */
    public float getCy();

    /**
     * @return y coordinate of the top-most point of the power-up.
     */
    public float getTop();

    /**
     * @return y coordinate of the bottom-most point of the power-up.
     */
    public float getBottom();
}
