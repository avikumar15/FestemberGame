package com.example.mmm.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.mmm.R;
import com.example.mmm.Utils;
import com.example.mmm.viewmodel.GameViewModel;
import com.example.mmm.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.example.mmm.Utils.SP_KEY;
import static com.example.mmm.Utils.USER_KEY;
import static com.example.mmm.Utils.USER_SCORE;

public class LoginActivity extends AppCompatActivity {

    ConstraintLayout cl;
    float screenWidth, screenHeight;
    ObjectAnimator animation;
    int animationDuration = 1000;

    ImageView ivRegister;
    ImageView ivLeaderboard;

    Intent intent;
    Button btnLogin;

    EditText etUsername;
    EditText etPass;

    GameViewModel model;
    List<User> userList = new ArrayList<>();

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = getSharedPreferences(
                SP_KEY, Context.MODE_PRIVATE);

        bindViews();
        animateCl();
        Log.i("LoginActivity", "Height and width: "+screenHeight+", "+screenWidth);

        setListeners();

        model = (new ViewModelProvider(this)).get(GameViewModel.class);

        model.getUsers().observe(this, u -> {
            userList = u;
        });

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

        String uname = etUsername.getText().toString();
        String pass = etPass.getText().toString();

        if(uname.equals("") || pass.equals("")) {
            Toast.makeText(this, "Field can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String passSHA = "";
        Long score=0L;

        for(int i = 0; i<userList.size(); i++) {
            if(uname.equals(userList.get(i).Username)) {
                passSHA = userList.get(i).Password;
                score = userList.get(i).Score;
                break;
            }
        }

        if(Utils.sha256(pass).equals(passSHA)) {
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString(USER_KEY,uname);
            editor.putLong(USER_SCORE,score);

            editor.apply();

            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        } else if(passSHA.equals("")) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username or Password wrong!", Toast.LENGTH_SHORT).show();
        }

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
        etUsername = findViewById(R.id.etUsername);
        etPass = findViewById(R.id.etPassword);
    }
}