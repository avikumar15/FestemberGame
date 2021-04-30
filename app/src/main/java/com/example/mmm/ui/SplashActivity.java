package com.example.mmm.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.mmm.R;
import com.example.mmm.Utils;
import com.example.mmm.viewmodel.GameViewModel;

import static com.example.mmm.Utils.SP_KEY;

public class SplashActivity extends AppCompatActivity {

    Intent intent;
    ConstraintLayout cl;
    ImageView iv;
    DisplayMetrics metrics;
    ObjectAnimator animation;
    Float screenHeight, screenWidth;
    int animationDuration = 1500;

    SharedPreferences sharedPref;

    GameViewModel model;
    Boolean isDataReady = false;
    Boolean isAnimationDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPref = getSharedPreferences(
                SP_KEY, Context.MODE_PRIVATE);

        model = (new ViewModelProvider(this)).get(GameViewModel.class);

        model.getUsers().observe(this, u -> {
            isDataReady = true;
            if(isAnimationDone) {
                model.removeListener();
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        initViewsAndVars();
        Log.i("SplashActivity", "Height and width: "+screenHeight+", "+screenWidth);
        setHandler();
    }

    private void setHandler() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation.start();
                crossfade();
            }
        }, 500);
    }

    private void initViewsAndVars() {
        intent = new Intent(this, LoginActivity.class);

        cl = findViewById(R.id.cl_splash);
        iv = findViewById(R.id.iv_splash);

        metrics = getResources().getDisplayMetrics();

        screenWidth = metrics.widthPixels*1.0f;
        screenHeight = metrics.heightPixels*1.0f;

        animation = ObjectAnimator.ofFloat(iv, "translationY", -screenHeight/2-100*metrics.density);
        animation.setDuration(animationDuration);

        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if("".equals(sharedPref.getString(Utils.USER_KEY,""))) {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, GameActivity.class);
                }

                isAnimationDone=true;

                if(isDataReady) {
                    model.removeListener();
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                } else {
                    Toast.makeText(SplashActivity.this, "Internet slow!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void crossfade() {
        cl.setAlpha(1.0f);
        cl.setVisibility(View.VISIBLE);

        cl.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(null);
    }

}
