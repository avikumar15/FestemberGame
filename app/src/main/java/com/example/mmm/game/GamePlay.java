package com.example.mmm.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.mmm.R;
import com.example.mmm.game.obstacles.CrossRotatingObstacle;
import com.example.mmm.game.obstacles.HorizontalObstacle;
import com.example.mmm.game.obstacles.HorizontalObstacleSet;
import com.example.mmm.game.obstacles.LayeredHorizontalObjects;
import com.example.mmm.game.obstacles.MutuallyAttractedObstacles;
import com.example.mmm.game.obstacles.Obstacle;
import com.example.mmm.game.obstacles.RotatingObstacle;
import com.example.mmm.game.powerups.DisableCollisionsPowerup;
import com.example.mmm.game.powerups.Powerup;
import com.example.mmm.game.utils.FrameRect;
import com.example.mmm.ui.GameActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.mmm.game.utils.GameUtils.EXT_PADDING;
import static com.example.mmm.game.utils.GameUtils.GAP_LAYERED_OBSTACLE;
import static com.example.mmm.game.utils.GameUtils.POINTER_RADIUS;
import static com.example.mmm.game.utils.GameUtils.POWERUP_BAR_HEIGHT;
import static com.example.mmm.game.obstacles.Obstacle.CROSS_ROTATING_OBSTACLE;
import static com.example.mmm.game.obstacles.Obstacle.HORIZONTAL_LAYERED_OBSTACLE;
import static com.example.mmm.game.obstacles.Obstacle.HORIZONTAL_OBSTACLE;
import static com.example.mmm.game.obstacles.Obstacle.HORIZONTAL_OBSTACLE_SET;
import static com.example.mmm.game.obstacles.Obstacle.MUTUALLY_ATTRACTED_OBSTACLE;
import static com.example.mmm.game.utils.GameUtils.TRANSITION_TIME_INVISIBILITY;

public class GamePlay extends View {

    // TAG for logs
    private final static String TAG = "GamePlay";

    public int maxFrames = 4;  // Increase this to make size of each background bitmap to reduce
    public float frameHeight;
    float fingerX, fingerY;
    Context mContext;

    private Paint obstaclePaint = new Paint(), brush = new Paint(), paint = new Paint(), backgroundPaint = new Paint(), textPaint = new Paint(), powerupPaint = new Paint();
    private Paint powerupBarFillPaint = new Paint(), powerupBarStrokePaint = new Paint();

    private float height, width;
    private Path trianglePath;
    private boolean started = false, ended = false, gameOver = false;

    private Bitmap startScreen, gameOverScreen, startScreenResized, gameOverScreenResized;
    private Game game;

    private Drawable frameDrawable;
    private List<FrameRect> frameRects = new ArrayList<>();
    private Canvas tempCanvas;
    private Bitmap tempBitmap;
    private int hitObstacleIndex, hitX, hitY;
    private int transitionTime;


    /**
     * Sets paint for various objects.
     * @param context
     * @param width
     * @param height
     */
    public GamePlay(Context context, float width, float height) {
        super(context);

        this.width = width;
        this.height = height;

        if (maxFrames > 0) {
            frameHeight = height / (maxFrames - 1);
        } else {
            frameHeight = height;
        }

        frameDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tile2);

        brush.setStyle(Paint.Style.FILL_AND_STROKE);

        // todo: replace with assets instead.
        obstaclePaint.setColor(getResources().getColor(R.color.colorAccent));
        powerupPaint.setColor(getResources().getColor(R.color.colorPrimary));
        textPaint.setColor(getResources().getColor(R.color.white));
        powerupBarFillPaint.setColor(getResources().getColor(R.color.white));
        powerupBarStrokePaint.setColor(getResources().getColor(R.color.white));
        textPaint.setTextSize(80.0f);

        powerupBarStrokePaint.setStyle(Paint.Style.STROKE);
        powerupBarStrokePaint.setStrokeWidth(10.0f);

        startScreen = BitmapFactory.decodeResource(getResources(), R.drawable.start_screen_min);
        gameOverScreen = BitmapFactory.decodeResource(getResources(), R.drawable.game_over_screen_min);

        startScreenResized = Bitmap.createScaledBitmap(startScreen, (int) width, (int) height, false);
        gameOverScreenResized = Bitmap.createScaledBitmap(gameOverScreen, (int) width, (int) height, false);

        mContext = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!started && !ended) {
            showStartingScreen(canvas);
        } else if (ended) {
            showGameOverScreen(canvas);
            textPaint.setTextSize(100);
        } else {
            drawFrameRect(canvas);

            canvas.drawCircle(fingerX, fingerY, POINTER_RADIUS, brush);
            drawPointer(canvas);
            drawObstacles(canvas);

            // Avoiding game null pointer exception
            try {
                canvas.drawText("Score: " + game.getScoreInt(), width / 6f, height / 10f, textPaint);      // Change the dimensions, if required.
            } catch (Exception e) {
                Log.d(TAG, "Exception caught", e);
            }
        }
        invalidate();
    }

    /**
     * If the game is running action move will move the pointer
     * If the game is in starting screen, action move will start the game using start function
     * If the game is in game over screen, action down will go the starting screen.
     */

//    If you return true from an ACTION_DOWN event you are interested in the rest of the
//    events in that gesture. A "gesture" in this case means all events until the final
//    ACTION_UP or ACTION_CANCEL. Returning false from an ACTION_DOWN means you do not
//    want the event and other views will have the opportunity to handle it. If you have
//    overlapping views this can be a sibling view. If not it will bubble up to the parent.

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        float cx = event.getX();
        float cy = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // If the game is in game over screen, action down will go the starting screen.
                if (!started && ended) {
                    Intent intent = new Intent(mContext, GameActivity.class);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                    ((Activity)mContext).overridePendingTransition(0,0);
                    return false;
                }
                fingerX = cx;
                fingerY = cy;
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                // If the game is in starting screen, action move will start the game using start function
                if (!started && !ended) {
                    start();
                    invalidate();
                    return true;
                }
                fingerX = cx;
                fingerY = cy;
                break;
            }
            default: {
                if (!started && !ended) {
                    return true;
                }
                // If the game is running and the player does anything other than action move, it will result in game over.
                if (!gameOver) {
                    setGameOver();
                    invalidate();
                    return false;
                }
            }
        }

        invalidate();
        return false;
    }



    /**
     * Called as soon the game starts from the starting screen.
     * Add game start code here
     * It is better to add initialization of variables related to Game class in Game constructor.
     */
    public void start(){
        started = true;
        ended = false;
        gameOver = false;
        game = new Game(width, height);

        trianglePath = new Path();

        tempBitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        // Contents of temp canvas are drawn in drawObstacles on canvas using temp bitmap.
        tempCanvas = new Canvas(tempBitmap);

        for (int i = maxFrames - 2; i >= 0; --i){
            frameRects.add(new FrameRect(frameHeight * i, frameHeight * (i + 1), game));
        }

        hitObstacleIndex = -1;
        transitionTime = 0;

        Log.d(TAG, "Started!");
        invalidate();
    }

    /**
     * Called as soon as the game is over
     */
    private void setGameOver() {
        gameOver = true;
        ended = true;
        started = false;

        // free canvas objects

        Log.d(TAG, "Game Over");

    }

    private void showStartingScreen(Canvas canvas) {
        canvas.drawBitmap(startScreenResized, 0, 0, paint);
    }

    private void showGameOverScreen(Canvas canvas) {
        canvas.drawBitmap(gameOverScreenResized, 0, 0, paint);
    }

    /**
     * The main function that draws all the obstacles an power-ups.
     * The drawing method varies from obstacle to obstacle.
     * If the pointer is inside any obstacle, it calls the setGameOver function.
     *
     * @param canvas The canvas on which the obstacles and pointer is to be drawn
     */
    private void drawObstacles(Canvas canvas) {
        List<Obstacle> obstacles = game.getObstacles();
        tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (int i = 0; i < obstacles.size(); ++i) {
            Obstacle obstacle = obstacles.get(i);
            // If pointer is inside a obstacle, game is over.
            if (obstacle.isInside(fingerX, fingerY) && !obstacle.isInvisible()) {
                if (game.isDisableCollisions()) {
                    obstacle.setInvisible();
                    hitObstacleIndex = i;
                    hitX = (int) obstacle.getCx();
                    hitY = (int) obstacle.getCy();
                } else {
                    setGameOver();
                    invalidate();
                    break;
                }
            }
            if (!obstacle.isInvisible()) {
                drawObstacle(canvas, obstacle);
            }
            obstacle.update();
            obstacle.moveDown();
        }
        if (hitObstacleIndex != -1 && hitObstacleIndex < obstacles.size()) {
            Obstacle hitObstacle = obstacles.get(hitObstacleIndex);
            ++transitionTime;
            if (transitionTime < TRANSITION_TIME_INVISIBILITY) {
                int alpha = (int) ((1 - transitionTime * 1.0f / TRANSITION_TIME_INVISIBILITY) * 255);
                obstaclePaint.setAlpha(alpha);
                drawObstacle(canvas, hitObstacle);
                obstaclePaint.setAlpha(255);
            } else {
                hitObstacleIndex = -1;
                transitionTime = 0;
            }
        }
        int nonActivePowerups = 0;
        for (Powerup powerup : game.getPowerups()){
            if (!powerup.isPicked()) {
                if (powerup.isInside(fingerX, fingerY)) {
                    Log.d(TAG, "Inside powerup");
                    powerup.setPicked();
                    invalidate();
                }

                DisableCollisionsPowerup disableCollisionsPowerup = (DisableCollisionsPowerup) powerup;
                canvas.drawRect(
                        disableCollisionsPowerup.getLeft(),
                        disableCollisionsPowerup.getTop(),
                        disableCollisionsPowerup.getRight(),
                        disableCollisionsPowerup.getBottom(),
                        powerupPaint
                );
            }

            powerup.moveDown();
            if (powerup.isActive() && powerup.isPicked()){
                powerup.updateTimePicked();
                if (powerup.disableCollisions() && (DisableCollisionsPowerup.TIME_DURATION - powerup.getTimePicked() < game.getDisableCollisionsTime() || game.getDisableCollisionsTime() == 0)){
                    game.setDisableCollisionsTime(DisableCollisionsPowerup.TIME_DURATION - powerup.getTimePicked());
                }
//                Log.d(TAG, "Powerup time: " + powerup.getTimePicked());
            }
            if (!game.isDisableCollisions() && powerup.disableCollisions() && powerup.isActive() && powerup.isPicked()){
                Log.d(TAG, "Can disable collisions");
                game.setDisableCollisions(true);
            }
            else if (game.isDisableCollisions() && powerup.disableCollisions() && (!powerup.isPicked() || !powerup.isActive())){
                ++nonActivePowerups;
            }
        }
        if (game.isDisableCollisions() && nonActivePowerups == game.getPowerups().size()){
            Log.d(TAG, "Enabling collisions");
            game.setDisableCollisions(false);
            game.setDisableCollisionsTime(0);
        }
        // Drawing the contents of temp canvas on original canvas by using temp bitmap.
        canvas.drawBitmap(tempBitmap, 0, 0, backgroundPaint);

        if (game.isDisableCollisions()){
//            canvas.drawRect(
//                    EXT_PADDING * 1.5f,
//                    EXT_PADDING * 1.5f,
//                    width - EXT_PADDING * 1.5f,
//                    EXT_PADDING * 1.5f + POWERUP_BAR_HEIGHT,
//                    backgroundPaint
//            );
            canvas.drawRect(
                    EXT_PADDING * 1.5f,
                    EXT_PADDING * 1.5f,
                    width - EXT_PADDING * 1.5f,
                    EXT_PADDING * 1.5f + POWERUP_BAR_HEIGHT,
                    powerupBarStrokePaint
            );
            canvas.drawRect(
                    EXT_PADDING * 1.5f,
                    EXT_PADDING * 1.5f,
                    EXT_PADDING * 1.5f + (width - 2 * EXT_PADDING * 1.5f) * game.getDisableCollisionsTime() * 1.0f / DisableCollisionsPowerup.TIME_DURATION,
                    EXT_PADDING * 1.5f + POWERUP_BAR_HEIGHT,
                    powerupBarFillPaint
            );
        }

        game.update();
        invalidate();
    }

    private void drawObstacle(Canvas canvas, Obstacle obstacle){
        if (obstacle.getObstacleType().equals(Obstacle.ROTATING_OBSTACLE)) {
            RotatingObstacle rotatingObstacle = (RotatingObstacle) obstacle;
            // Drawing two circles.
            canvas.drawCircle(rotatingObstacle.getObstacleCx1(), rotatingObstacle.getObstacleCy1(), rotatingObstacle.getObstacleRadius(), obstaclePaint);
            canvas.drawCircle(rotatingObstacle.getObstacleCx2(), rotatingObstacle.getObstacleCy2(), rotatingObstacle.getObstacleRadius(), obstaclePaint);
        }// New Obstacle, horizontal obstacle stacked up in 3 lines with one space in the middle, which continues translating horizontally.
        else if (obstacle.getObstacleType().equals(HORIZONTAL_LAYERED_OBSTACLE)) {
            LayeredHorizontalObjects LhorizontalObstacle = (LayeredHorizontalObjects) obstacle;

            //making layers of obstacles
            //layer02
            {
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() + 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() + 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() + 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() + 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() + 11 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);

                canvas.drawRect(LhorizontalObstacle.getCx() - 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() - 1 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() - 3 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() - 5 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() - 7 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 11 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop(), LhorizontalObstacle.getCx() - 9 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getBottom() - GAP_LAYERED_OBSTACLE, obstaclePaint);
            }
            //layer01
            {
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 11 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getBottom(), obstaclePaint);

                canvas.drawRect(LhorizontalObstacle.getCx() - 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 1 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 3 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 5 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 7 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 11 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() - LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 9 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() - GAP_LAYERED_OBSTACLE, obstaclePaint);
            }
            //layer03
            {
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(GAP_LAYERED_OBSTACLE + LhorizontalObstacle.getCx() + 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() + 11 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);

                canvas.drawRect(LhorizontalObstacle.getCx() - 3 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 1 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 5 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 3 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 7 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 5 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 9 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 7 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);
                canvas.drawRect(LhorizontalObstacle.getCx() - 11 * LhorizontalObstacle.getObstacleWidth() / 2f, LhorizontalObstacle.getTop() + LhorizontalObstacle.getObstacleHeight(), LhorizontalObstacle.getCx() - 9 * LhorizontalObstacle.getObstacleWidth() / 2f - GAP_LAYERED_OBSTACLE, LhorizontalObstacle.getTop() + 2 * LhorizontalObstacle.getObstacleHeight() - GAP_LAYERED_OBSTACLE, obstaclePaint);

            }

        } else if (obstacle.getObstacleType().equals(HORIZONTAL_OBSTACLE)) {
            HorizontalObstacle horizontalObstacle = (HorizontalObstacle) obstacle;
            canvas.drawRect(horizontalObstacle.getLeft(), horizontalObstacle.getTop(), horizontalObstacle.getRight(), horizontalObstacle.getBottom(), obstaclePaint);

        } else if (obstacle.getObstacleType().equals(HORIZONTAL_OBSTACLE_SET)) {
            HorizontalObstacleSet horizontalObstacleSet = (HorizontalObstacleSet) obstacle;
            // Drawing a rectangle on the left end with width = obstacleWidth, height = obstacleHeight
            canvas.drawRect(
                    EXT_PADDING,
                    horizontalObstacleSet.getTop(),
                    horizontalObstacleSet.getObstacleWidth(),
                    horizontalObstacleSet.getBottom(),
                    obstaclePaint
            );
            // Drawing a rectangle on the right end with width = obstacleWidth, height = obstacleHeight
            canvas.drawRect(
                    width - horizontalObstacleSet.getObstacleWidth(),
                    horizontalObstacleSet.getTop(),
                    width - EXT_PADDING,
                    horizontalObstacleSet.getBottom(),
                    obstaclePaint
            );
            // Drawing a rectangle on the moving part with width = 2 * obstacleWidth, height = obstacleHeight
            canvas.drawRect(
                    horizontalObstacleSet.getLeft(),
                    horizontalObstacleSet.getTop(),
                    horizontalObstacleSet.getRight(),
                    horizontalObstacleSet.getBottom(),
                    obstaclePaint
            );
        } else if (obstacle.getObstacleType().equals(MUTUALLY_ATTRACTED_OBSTACLE)) {
            MutuallyAttractedObstacles mutuallyAttractedObstacles = (MutuallyAttractedObstacles) obstacle;
            canvas.drawRect(mutuallyAttractedObstacles.getLeft(), mutuallyAttractedObstacles.getTop(), mutuallyAttractedObstacles.getRight(), mutuallyAttractedObstacles.getBottom(), obstaclePaint);
            canvas.drawRect(mutuallyAttractedObstacles.getLeft() + getWidth() - 2 * mutuallyAttractedObstacles.getCx(), mutuallyAttractedObstacles.getTop(), mutuallyAttractedObstacles.getRight() + getWidth() - 2 * mutuallyAttractedObstacles.getCx(), mutuallyAttractedObstacles.getBottom(), obstaclePaint);
        } else if (obstacle.getObstacleType().equals(CROSS_ROTATING_OBSTACLE)) {
            CrossRotatingObstacle crossRotatingObstacle = (CrossRotatingObstacle) obstacle;
            // We draw a cross (or line if hasDoubleLines is false) in temp canvas and rotate it by an angle theta.
            tempCanvas.save();
            tempCanvas.rotate(
                    crossRotatingObstacle.getTheta() * (float) (180 / Math.PI),
                    crossRotatingObstacle.getCx(),
                    crossRotatingObstacle.getCy()
            );
            tempCanvas.drawRect(
                    crossRotatingObstacle.getLeft(),
                    crossRotatingObstacle.getCy() - crossRotatingObstacle.getObstacleThickness() / 2,
                    crossRotatingObstacle.getRight(),
                    crossRotatingObstacle.getCy() + crossRotatingObstacle.getObstacleThickness() / 2,
                    obstaclePaint
            );
            if (crossRotatingObstacle.hasDoubleLines()) {
                tempCanvas.drawRect(
                        crossRotatingObstacle.getCx() - crossRotatingObstacle.getObstacleThickness() / 2,
                        crossRotatingObstacle.getTop(),
                        crossRotatingObstacle.getCx() + crossRotatingObstacle.getObstacleThickness() / 2,
                        crossRotatingObstacle.getBottom(),
                        obstaclePaint
                );
            }
            // Drawing a small circle at the center of the obstacle.
            tempCanvas.drawCircle(crossRotatingObstacle.getCx(), crossRotatingObstacle.getCy(), crossRotatingObstacle.getObstacleCenterRadius(), obstaclePaint);
            tempCanvas.restore();
        }
    }

    // If the player has superpower, draw the circle with spikes, else normal circle
    private void drawPointer(Canvas canvas){
        if (!game.isDisableCollisions()){
            canvas.drawCircle(
                    fingerX,
                    fingerY,
                    POINTER_RADIUS,
                    brush
            );
        }
        else {
            float expandedPointerRadius = POINTER_RADIUS * 1.3f;
            canvas.drawCircle(
                    fingerX,
                    fingerY,
                    expandedPointerRadius,
                    brush
            );
            float x, y;
            trianglePath.reset();

            // draw spikes
            for (float theta = 0; theta < 2 * (float) Math.PI; theta += (float) Math.PI / 4){
                float theta2 = theta + (float) Math.PI / 10;
                x = fingerX + expandedPointerRadius * 1.4f * (float) (Math.cos(theta) + Math.cos(theta2)) / 2;
                y = fingerY + expandedPointerRadius * 1.4f * (float) (Math.sin(theta) + Math.sin(theta2)) / 2;

                trianglePath.moveTo(fingerX + expandedPointerRadius * (float) Math.cos(theta),
                        fingerY + expandedPointerRadius * (float) Math.sin(theta));

                trianglePath.lineTo(x, y);

                trianglePath.lineTo(fingerX + expandedPointerRadius * (float) Math.cos(theta2),
                        fingerY + expandedPointerRadius * (float) Math.sin(theta2));

                trianglePath.close();

                canvas.drawPath(trianglePath, brush);
            }
        }
    }

    /**
     * Draws all the images (frameRects) on the canvas.
     *
     * @param canvas The canvas on which the background images are to be drawn.
     */
    private void drawFrameRect(Canvas canvas){
        Log.i(TAG, "Number of frameRects: "+ frameRects.size());
        if (frameRects == null || frameRects.size() == 0){
            return;
        }
        float frameTop = frameRects.get(frameRects.size() - 1).getTop();
        if (frameTop > 0){
            frameRects.add(new FrameRect(frameTop - frameHeight, frameTop, game));
        }
        if (!frameRects.get(0).isInScreen()){
            frameRects.remove(0);
        }
        for (FrameRect frameRect : frameRects){
            try {
                frameDrawable.setBounds(0, (int) frameRect.getTop(), (int) width, (int) frameRect.getBottom());
                frameDrawable.draw(canvas);
                frameDrawable.invalidateSelf();
            }
            catch (Exception e){
                Log.d(TAG, "Exception caught", e);
            }
            frameRect.update();
        }
        invalidate();
    }

    /**
     * @return Whether or not the game is in either the starting screen or game over screen, so that determining we can quit the app if back is pressed.
     */
    public boolean canExit() {
        return gameOver || (!started && !ended) || (ended);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
