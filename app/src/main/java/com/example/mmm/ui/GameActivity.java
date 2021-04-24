package com.example.mmm.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mmm.game.GameStatusInterface;
import com.example.mmm.Utils;
import com.example.mmm.game.GamePlay;
import com.example.mmm.R;

import static com.example.mmm.Utils.SP_KEY;

public class GameActivity extends AppCompatActivity implements GameStatusInterface {

    GamePlay gamePlay;
    FrameLayout frameLayout;

    final float screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    final float screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels ;

    private final static String TAG = "MainActivity";

    Button btnLeader;
    Button btnLogOut;

    Button tv;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        gamePlay = new GamePlay(this, screenWidth, screenHeight);

        frameLayout = findViewById(R.id.GameBackground);

        btnLeader = findViewById(R.id.btnLeaderGame);
        btnLogOut = findViewById(R.id.btnLogoutGame);
        tv = findViewById(R.id.tvHelpText);

        sharedPref = getSharedPreferences(SP_KEY, Context.MODE_PRIVATE);

        frameLayout.addView(gamePlay);

        btnLogOut.setOnClickListener(view -> {
            Intent intent = new Intent(GameActivity.this, LoginActivity.class);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Utils.USER_KEY, "");
            editor.apply();
            startActivity(intent);
            finish();
            overridePendingTransition(0,0);
        });
    }

    @Override
    public void onGameStarted() {
        tv.setVisibility(View.INVISIBLE);
        btnLogOut.setVisibility(View.INVISIBLE);
        btnLeader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNewGame() {
        tv.setVisibility(View.VISIBLE);
    }

}
