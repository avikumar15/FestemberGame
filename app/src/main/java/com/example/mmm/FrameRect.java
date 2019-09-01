package com.example.mmm;

import static com.example.mmm.Game.EXT_PADDING;
import static com.example.mmm.GameUtils.FRAME_RECT_SPEED;

public class FrameRect {
    private float top, bottom, height;
    private boolean inScreen;

    public FrameRect(float top, float bottom, float height){
        this.top = top;
        this.bottom = bottom;
        this.height = height;
        inScreen = true;
    }

    public void update(){
        top += FRAME_RECT_SPEED;
        bottom += FRAME_RECT_SPEED;
        if (top >= height){
            inScreen = false;
        }
    }

    public boolean isInScreen() { return inScreen; }

    public float getTop() { return top; }

    public float getBottom() { return bottom; }
}
