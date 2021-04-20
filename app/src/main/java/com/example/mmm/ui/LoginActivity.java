package com.example.mmm.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mmm.R;

public class LoginActivity extends AppCompatActivity {

    ConstraintLayout cl;
    float screenWidth, screenHeight;
    ObjectAnimator animation;
    int animationDuration = 1000;

    ImageView ivRegister;
    ImageView ivLeaderboard;

    Intent intent;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();
        animateCl();
        Log.i("LoginActivity", "Height and width: "+screenHeight+", "+screenWidth);

        setListeners();
    }

    private void setListeners() {

        ivRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        ivLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, LeaderboardActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {
        intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }

    private void animateCl() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        screenWidth = metrics.widthPixels*1.0f;
        screenHeight = metrics.heightPixels*1.0f;

        animation = ObjectAnimator.ofFloat(cl, "translationY", -screenHeight/2-100*metrics.density);
        animation.setDuration(animationDuration);

        animation.start();

        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ivLeaderboard.setVisibility(View.VISIBLE);
                ivRegister.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void bindViews() {
        cl = findViewById(R.id.cl_login);
        ivLeaderboard = findViewById(R.id.iv_lead);
        ivRegister = findViewById(R.id.iv_reg);
        btnLogin = findViewById(R.id.btn_login);
    }
}