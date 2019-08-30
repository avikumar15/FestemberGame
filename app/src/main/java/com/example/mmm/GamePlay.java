package com.example.mmm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class GamePlay extends View {

    Paint OffWhite;
    float x,y;
    float halfSideLength;
    float incrementVariable=0;
    public Paint brush;
    public Path gameplayPath;
    public float fingerX;
    public float fingerY;
    public Context mContext;

    public GamePlay(Context context, float xCenter, float yCenter) {
        super(context);

        OffWhite = new Paint();
        OffWhite.setColor(Color.parseColor("#DCDCDC"));
        brush = new Paint();
        brush.setColor(Color.parseColor("#60B8B894"));

        gameplayPath = new Path();

        x=xCenter;
        y=yCenter;

        mContext = context;
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        halfSideLength = getHeight()/30f;
    //    canvas.drawPath(gameplayPath,brush);
        canvas.drawCircle(fingerX,fingerY,60,brush);
        canvas.drawRect(x-halfSideLength+incrementVariable,y-halfSideLength+incrementVariable,x+halfSideLength+incrementVariable,y+halfSideLength+incrementVariable,OffWhite);

        if(fingerX>=x-halfSideLength+incrementVariable && fingerX<=x+halfSideLength+incrementVariable && fingerY>=y-halfSideLength+incrementVariable && fingerY<=y+halfSideLength+incrementVariable)
        {
            Toast.makeText(mContext,"Game Over",Toast.LENGTH_SHORT).show();
        }

        if(x+halfSideLength+incrementVariable<=3*getWidth()/4f)
        incrementVariable+=5;
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
            float cx = event.getX();
            float cy = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN : {
                fingerX=cx;
                fingerY=cy;
                return true;
            }case MotionEvent.ACTION_MOVE:
            {
                fingerX=cx;
                fingerY=cy;
                break;
            }
            default:
            {
                // add game over code here
                Toast.makeText(mContext,"Game Over",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        postInvalidate();
        return false;
    }
}