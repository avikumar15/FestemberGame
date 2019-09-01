package com.example.mmm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class GamePlay extends View {

    private Paint obstaclePaint = new Paint(), brush = new Paint(), paint = new Paint(), backgroundPaint = new Paint(), textPaint = new Paint();
    private float x,y;
    private float height, width;
    float halfSideLength;
    float incrementVariable=0;
    private Path gameplayPath;
    float fingerX, fingerY;
    private boolean started = false, ended = false, gameOver = false;
    Context mContext;
    private Bitmap startScreen, gameOverScreen, startScreenResized, gameOverScreenResized;
    private Game game;

    private final static String TAG = "GamePlay";

    public GamePlay(Context context, float width, float height) {
        super(context);

        this.width = width;
        this.height = height;

        obstaclePaint.setColor(getResources().getColor(R.color.colorAccent));
        brush.setColor(getResources().getColor(R.color.ball_color));
        backgroundPaint.setColor(getResources().getColor(R.color.background_black));
        textPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
        textPaint.setTextSize(10.0f);

        startScreen = BitmapFactory.decodeResource(getResources(), R.drawable.start_screen);
        gameOverScreen = BitmapFactory.decodeResource(getResources(), R.drawable.game_over_screen);

        startScreenResized = Bitmap.createScaledBitmap(startScreen, (int) width, (int) height, false);
        gameOverScreenResized = Bitmap.createScaledBitmap(gameOverScreen, (int) width, (int) height, false);

        x = 0;
        y = height/2;

        Log.d(TAG, "Height = " + height + ", width = " + width);

        mContext = context;
    }

    public void start(){
        started = true;
        ended = false;
        gameOver = false;
        game = new Game(width, height);
        gameplayPath = new Path();
        Log.d(TAG, "Started!");
        invalidate();
    }

    private void setGameOver(){
        gameOver = true;
        ended = true;
        started = false;
        Log.d(TAG, "Game Over");
//        Toast.makeText(mContext,"Game Over",Toast.LENGTH_SHORT).show();
    }

    private void showStartingScreen(Canvas canvas){
        canvas.drawBitmap(startScreenResized, 0, 0, paint);
//        canvas.drawText("Touch anywhere to start", 100, 100, textPaint);
        invalidate();
    }

    private void showGameOverScreen(Canvas canvas){
//        canvas.drawBitmap(gameOverScreenResized, 0, 0, paint);
//        canvas.drawRect(0, 0, width, height, backgroundPaint);
        canvas.drawBitmap(gameOverScreenResized, 0, 0, paint);
//        canvas.drawText("Touch anywhere to start", 100, 100, textPaint);
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
        }
        else {
            canvas.drawRect(0, 0, width, height, backgroundPaint);
//            halfSideLength = getHeight() / 30f;
            //    canvas.drawPath(gameplayPath,brush);
            canvas.drawCircle(fingerX, fingerY, 60, brush);
            drawObstacles(canvas);

//            canvas.drawRect(x - halfSideLength + incrementVariable, y - halfSideLength + incrementVariable, x + halfSideLength + incrementVariable, y + halfSideLength + incrementVariable, obstaclePaint);

//            if (fingerX >= x - halfSideLength + incrementVariable && fingerX <= x + halfSideLength + incrementVariable && fingerY >= y - halfSideLength + incrementVariable && fingerY <= y + halfSideLength + incrementVariable) {
//                Toast.makeText(mContext, "Game Over", Toast.LENGTH_SHORT).show();
//            }
//
//            if (x + halfSideLength + incrementVariable <= 3 * getWidth() / 4f) {
//                incrementVariable += 5;
//            }
        }

        invalidate();
    }

    private void drawObstacles(Canvas canvas){
        List<Obstacle> obstacles = game.getObstacles();
        for (Obstacle obstacle : obstacles){
            if (obstacle.getObstacleType().equals(Obstacle.ROTATING_OBSTACLE)){
                RotatingObstacle rotatingObstacle = (RotatingObstacle)obstacle;
                canvas.drawCircle(rotatingObstacle.getObstacleCx1(), rotatingObstacle.getObstacleCy1(), rotatingObstacle.getObstacleRadius(), obstaclePaint);
                canvas.drawCircle(rotatingObstacle.getObstacleCx2(), rotatingObstacle.getObstacleCy2(), rotatingObstacle.getObstacleRadius(), obstaclePaint);
                if (rotatingObstacle.isInside(fingerX, fingerY)){
                    Log.d(TAG, "Game Over! Ball inside obstacle: " + rotatingObstacle);
                    setGameOver();
                    invalidate();
                    break;
                }
            }
            obstacle.update();
            obstacle.moveDown();
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
                    Log.d(TAG, "Going to starting screen");
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
                    Log.d(TAG, "Action Move");
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
                    // add game over code here
                    setGameOver();
                    invalidate();
                    return false;
                }
            }
        }

        invalidate();
        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}