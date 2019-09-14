package com.example.mmm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class PointerDrawable extends View {

    private float radius, fingerX, fingerY;
    Paint pointerCirclePaint;
    Paint pointerExtensionPaint;
    Path PointerPath;


    public PointerDrawable(Context context, float X, float Y, float rad, Paint pointerP) {
        super(context);
        fingerX=X;
        fingerY=Y;
        radius=rad;
        PointerPath=new Path();

        PointerPath.moveTo(X-rad,fingerY);
        PointerPath.lineTo(X+rad,fingerY);
        PointerPath.lineTo(fingerX,fingerY+220);
        PointerPath.lineTo(X-rad,fingerY);
        PointerPath.close();

        pointerCirclePaint=pointerP;

        pointerExtensionPaint=pointerP;

    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        pointerExtensionPaint.setAlpha(10);
        pointerCirclePaint.setAlpha(255);

        canvas.drawCircle(fingerX,fingerY,radius,pointerCirclePaint);
        canvas.drawPath(PointerPath,pointerExtensionPaint);

    }
}
