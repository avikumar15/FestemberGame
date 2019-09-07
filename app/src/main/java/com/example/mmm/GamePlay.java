package com.example.mmm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.mmm.GameUtils.FRAME_RECT_SPEED;
import static com.example.mmm.GameUtils.EXT_PADDING;
import static com.example.mmm.GameUtils.POINTER_RADIUS;
import static com.example.mmm.Obstacle.CROSS_ROTATING_OBSTACLE;
import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE;
import static com.example.mmm.Obstacle.HORIZONTAL_OBSTACLE_SET;

public class GamePlay extends View {

    private Paint obstaclePaint = new Paint(), brush = new Paint(), paint = new Paint(), backgroundPaint = new Paint(), textPaint = new Paint();
    private float height, width;

    private Path gameplayPath;
    float fingerX, fingerY;
    private boolean started = false, ended = false, gameOver = false;
    Context mContext;
    private Bitmap startScreen, gameOverScreen, startScreenResized, gameOverScreenResized;
    private Game game;

    public int maxFrames = 4;  // Increase this to make size of each background bitmap to reduce
                               // ie. make it look like cropped and decrease to achieve the opposite.
    private Drawable frameDrawable;
    public float frameHeight;
    private List<FrameRect> frameRects = new ArrayList<>();

    private Canvas tempCanvas;
    private Bitmap tempBitmap;

    private final static String TAG = "GamePlay";

    public GamePlay(Context context, float width, float height) {
        super(context);

        this.width = width;
        this.height = height;

        if (maxFrames > 0) {
            frameHeight = height / (maxFrames - 1);
        }
        else{
            frameHeight = height;
        }

        obstaclePaint.setColor(getResources().getColor(R.color.colorAccent));
   //     brush.setColor(getResources().getColor(R.color.ball_color));
        textPaint.setColor(getResources().getColor(R.color.white));
        textPaint.setTextSize(80.0f);

        startScreen = BitmapFactory.decodeResource(getResources(), R.drawable.start_screen);
        gameOverScreen = BitmapFactory.decodeResource(getResources(), R.drawable.game_over_screen);

        startScreenResized = Bitmap.createScaledBitmap(startScreen, (int) width, (int) height, false);
        gameOverScreenResized = Bitmap.createScaledBitmap(gameOverScreen, (int) width, (int) height, false);

//        Log.d(TAG, "Height = " + height + ", width = " + width);

        mContext = context;
    }

    // Add Game start code here
    // It is better to add initialization of variables related to Game class in Game constructor.
    public void start(){
        started = true;
        ended = false;
        gameOver = false;
        game = new Game(width, height);

        tempBitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);

        for (int i = maxFrames - 2; i >= 0; --i){
            frameRects.add(new FrameRect(frameHeight * i, frameHeight * (i + 1), height));
        }

        Log.d(TAG, "Started!");
        invalidate();
    }

    // Add Game Over code here
    private void setGameOver(){
        gameOver = true;
        ended = true;
        started = false;
        Log.d(TAG, "Game Over");

    }

    private void showStartingScreen(Canvas canvas){
        canvas.drawBitmap(startScreenResized, 0, 0, paint);
        invalidate();
    }

    private void showGameOverScreen(Canvas canvas){
        canvas.drawBitmap(gameOverScreenResized, 0, 0, paint);
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (!started && !ended){
            showStartingScreen(canvas);
        }
        else if (ended){
            showGameOverScreen(canvas);
            textPaint.setTextSize(100);
        }
        else {
            drawFrameRect(canvas);
            canvas.drawCircle(fingerX, fingerY, POINTER_RADIUS, brush);
            drawObstacles(canvas);

            // Avoiding game null pointer exception
            try {
                canvas.drawText("Score: " + game.getScoreInt(), width / 6f, height / 10f, textPaint);      // Change the dimensions, if required.
            }
            catch (Exception e){
                Log.d(TAG, "Exception caught", e);
            }
        }
        invalidate();
    }

    private void drawObstacles(Canvas canvas){
        List<Obstacle> obstacles = game.getObstacles();
        tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Obstacle obstacle : obstacles){
            if (obstacle.isInside(fingerX, fingerY)){
//                Log.d(TAG, "Game Over! Ball inside obstacle: " + obstacle);
//                setGameOver();
                invalidate();
                break;
            }
            if (obstacle.getObstacleType().equals(Obstacle.ROTATING_OBSTACLE)){
                RotatingObstacle rotatingObstacle = (RotatingObstacle) obstacle;
                canvas.drawCircle(rotatingObstacle.getObstacleCx1(), rotatingObstacle.getObstacleCy1(), rotatingObstacle.getObstacleRadius(), obstaclePaint);
                canvas.drawCircle(rotatingObstacle.getObstacleCx2(), rotatingObstacle.getObstacleCy2(), rotatingObstacle.getObstacleRadius(), obstaclePaint);
            }
            else if (obstacle.getObstacleType().equals(HORIZONTAL_OBSTACLE)){
                HorizontalObstacle horizontalObstacle = (HorizontalObstacle) obstacle;
                canvas.drawRect(horizontalObstacle.getLeft(), horizontalObstacle.getTop(), horizontalObstacle.getRight(), horizontalObstacle.getBottom(), obstaclePaint);
            }
            else if (obstacle.getObstacleType().equals(HORIZONTAL_OBSTACLE_SET)){
                HorizontalObstacleSet horizontalObstacleSet = (HorizontalObstacleSet) obstacle;
                canvas.drawRect(
                        EXT_PADDING,
                        horizontalObstacleSet.getTop(),
                        horizontalObstacleSet.getObstacleWidth(),
                        horizontalObstacleSet.getBottom(),
                        obstaclePaint
                );
                canvas.drawRect(
                        width - horizontalObstacleSet.getObstacleWidth(),
                        horizontalObstacleSet.getTop(),
                        width - EXT_PADDING,
                        horizontalObstacleSet.getBottom(),
                        obstaclePaint
                );
                canvas.drawRect(
                        horizontalObstacleSet.getLeft(),
                        horizontalObstacleSet.getTop(),
                        horizontalObstacleSet.getRight(),
                        horizontalObstacleSet.getBottom(),
                        obstaclePaint
                );
            }
            else if (obstacle.getObstacleType().equals(CROSS_ROTATING_OBSTACLE)){
                CrossRotatingObstacle crossRotatingObstacle = (CrossRotatingObstacle) obstacle;
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
                tempCanvas.drawCircle(crossRotatingObstacle.getCx(), crossRotatingObstacle.getCy(), crossRotatingObstacle.getObstacleCenterRadius(), obstaclePaint);
                tempCanvas.restore();
            }
        }
        canvas.drawBitmap(tempBitmap, 0, 0, backgroundPaint);
        game.update();
        invalidate();
    }

    private void drawFrameRect(Canvas canvas){
        if (frameRects == null || frameRects.size() == 0){
            return;
        }
        float frameTop = frameRects.get(frameRects.size() - 1).getTop();
        if (frameTop > 0){
            frameRects.add(new FrameRect(frameTop - frameHeight, frameTop, height));
        }
        if (!frameRects.get(0).isInScreen()){
            frameRects.remove(0);
        }
        for (FrameRect frameRect : frameRects){
            try {
                frameDrawable = ContextCompat.getDrawable(getContext(), R.drawable.tile);
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

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        performClick();
        float cx = event.getX();
        float cy = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN : {
                if (!started && ended){
                    ended = false;
                    invalidate();
                    return false;
                }
                fingerX=cx;
                fingerY=cy;
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!started && !ended){
                    start();
                    invalidate();
                    return true;
                }
                fingerX=cx;
                fingerY=cy;
                break;
            }
            default: {
                if (!started && !ended){
                    return true;
                }
                if (!gameOver) {
//                    setGameOver();
                    invalidate();
                    return false;
                }
            }
        }

        invalidate();
        return false;
    }

    public boolean canExit(){
        return gameOver || (!started && !ended) || (ended);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}