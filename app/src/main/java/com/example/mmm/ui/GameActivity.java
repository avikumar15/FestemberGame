package com.example.mmm.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mmm.game.GamePlay;
import com.example.mmm.R;

public class GameActivity extends AppCompatActivity {

    GamePlay gamePlay;
    FrameLayout frameLayout;

    final float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    final float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels ;

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        gamePlay = new GamePlay(this, screenWidth, screenHeight);
        frameLayout = findViewById(R.id.GameBackground);

        frameLayout.addView(gamePlay);
    }
}
