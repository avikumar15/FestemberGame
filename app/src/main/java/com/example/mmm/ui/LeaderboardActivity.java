package com.example.mmm.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mmm.R;
import com.example.mmm.viewmodel.LeaderboardViewModel;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        LeaderboardViewModel model = (new ViewModelProvider(this)).get(LeaderboardViewModel.class);
        model.getUsers().observe(this, users -> {
            if(users.size()>0) {
                Log.i("LeaderboardActivity", users.get(0).toString());
            }
        });

    }
}
