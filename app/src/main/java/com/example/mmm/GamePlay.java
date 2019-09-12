package com.example.mmm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static com.example.mmm.GameUtils.EXT_PADDING;
import static com.example.mmm.GameUtils.GAP_LAYERED_OBSTACLE;
import static com.example.mmm.GameUtils.POINTER_RADIUS;
import static com.example.mmm.Obstacle.CROSS_ROTATING_OBSTACLE;
import static com.example.mmm.Obstacle.HORIZONTAL_LAYERED_OBSTACLE;
import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE;
import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE_SET;

public class GamePlay extends View {

    private final static String TAG = "GamePlay";
    public int maxFrames = 4;  // Increase this to make size of each background bitmap to reduce
    public float frameHeight;
    float fingerX, fingerY;
    Context mContext;
    private Paint obstaclePaint = new Paint(), brush = new Paint(), paint = new Paint(), backgroundPaint = new Paint(), textPaint = new Paint();
    private float height, width;
    private Path gameplayPath;
    private boolean started = false, ended = false, gameOver = false;
    private Bitmap startScreen, gameOverScreen, startScreenResized, gameOverScreenResized;
    private Game game;
    // ie. make it look like cropped and decrease to achieve the opposite.
    private Drawable frameDrawable;
    private List<FrameRect> frameRects = new ArrayList<>();
    private Canvas tempCanvas;
    private Bitmap tempBitmap;

    public GamePlay(Context context, float width, float height) {
        super(context);

        this.width = width;
        this.height = height;

        if (maxFrames > 0) {
            frameHeight = height / (maxFrames - 1);
        } else {
            frameHeight = height;
        }

        obstaclePaint.setColor(getResources().getColor(R.color.colorAccent));
        //     brush.setColor(getResources().getColor(R.color.ball_color));
        textPaint.setColor(getResources().getColor(R.color.white));
        textPaint.setTextSize(80.0f);

        startScreen = BitmapFactory.decodeResource(getResources(), R.drawable.start_screen_min);
        gameOverScreen = BitmapFactory.decodeResource(getResources(), R.drawable.game_over_screen_min);

        startScreenResized = Bitmap.createScaledBitmap(startScreen, (int) width, (int) height, false);
        gameOverScreenResized = Bitmap.createScaledBitmap(gameOverScreen, (int) width, (int) height, false);

        mContext = context;
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

        tempBitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        // Contents of temp canvas are drawn in drawObstacles on canvas using temp bitmap.
        tempCanvas = new Canvas(tempBitmap);

        for (int i = maxFrames - 2; i >= 0; --i){
            frameRects.add(new FrameRect(frameHeight * i, frameHeight * (i + 1), game));
        }

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
        //   Intent intent = new Intent(mContext,MainActivity.class);
        //   mContext.startActivity(intent);
        //   ((Activity)mContext).finish();
        //   ((Activity)mContext).overridePendingTransition(0,0);

        Log.d(TAG, "Game Over");

    }

    private void showStartingScreen(Canvas canvas) {
        canvas.drawBitmap(startScreenResized, 0, 0, paint);
        invalidate();
    }

    private void showGameOverScreen(Canvas canvas) {
        canvas.drawBitmap(gameOverScreenResized, 0, 0, paint);
        invalidate();
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
     * The main function that draws all the obstacles and the pointer.
     * The drawing method varies from obstacle to obstacle.
     * If the pointer is inside any obstacle, it calls the setGameOver function.
     *
     * @param canvas The canvas on which the obstacles and pointer is to be drawn
     */
    private void drawObstacles(Canvas canvas) {
        List<Obstacle> obstacles = game.getObstacles();
        tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Obstacle obstacle : obstacles) {
            // If pointer is inside a obstacle, game is over.
            if (obstacle.isInside(fingerX, fingerY)) {
                setGameOver();
                invalidate();
                break;
            }
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
        // Drawing the contents of temp canvas on original canvas by using temp bitmap.
        canvas.drawBitmap(tempBitmap, 0, 0, backgroundPaint);
        game.update();
        invalidate();
    }

    /**
     * Draws all the images (frameRects) on the canvas.
     *
     * @param canvas The canvas on which the background images are to be drawn.
     */
    private void drawFrameRect(Canvas canvas){
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
                frameDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tile2);
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
     * If the game is running action move will move the pointer
     * If the game is in starting screen, action move will start the game using start function
     * If the game is in game over screen, action down will go the starting screen.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        float cx = event.getX();
        float cy = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // If the game is in game over screen, action down will go the starting screen.
                if (!started && ended) {
                    ended = false;
                    invalidate();
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
