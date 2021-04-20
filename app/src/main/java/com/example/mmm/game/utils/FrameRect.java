package com.example.mmm.game.utils;

import com.example.mmm.game.Game;

public class FrameRect {
    private float top, bottom;
    private boolean inScreen;
    private Game game;

    public FrameRect(float top, float bottom, Game game){
        this.top = top;
        this.bottom = bottom;
        this.game = game;
        inScreen = true;
    }

    public void update(){
        top += game.frameRectSpeed;
        bottom += game.frameRectSpeed;
        if (top >= game.getHeight()){
            inScreen = false;
        }
    }

    public boolean isInScreen() { return inScreen; }

    public float getTop() { return top; }

    public float getBottom() { return bottom; }
}
