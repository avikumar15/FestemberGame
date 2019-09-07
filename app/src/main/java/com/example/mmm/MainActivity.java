package com.example.mmm;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    GamePlay gamePlay;
    FrameLayout frameLayout;
    LinearLayoutManager layoutManager;


    final float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    final float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels ;

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gamePlay = new GamePlay(this, screenWidth, screenHeight);
        frameLayout = (FrameLayout) findViewById(R.id.GameBackground);
        frameLayout.addView(gamePlay);
    }
}
