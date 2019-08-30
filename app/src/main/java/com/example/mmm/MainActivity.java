package com.example.mmm;

import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    GamePlay gamePlay;
    FrameLayout frameLayout;

    final float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    final float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gamePlay = new GamePlay(this, 0,screenHeight/2f);
        frameLayout = new FrameLayout(this);
        frameLayout.addView(gamePlay);
        setContentView(frameLayout);
    }
}
