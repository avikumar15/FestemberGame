package com.example.mmm.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmm.R;
import com.example.mmm.model.User;
import com.example.mmm.api.GameViewModel;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    List<User> users;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        GameViewModel model = (new ViewModelProvider(this)).get(GameViewModel.class);

        model.getUsers().observe(this, users -> {
            this.users = users;
        });

        recyclerView = findViewById(R.id.rv);

    }
}
