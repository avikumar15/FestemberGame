package com.example.mmm.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmm.R;
import com.example.mmm.Utils;
import com.example.mmm.adapters.LeaderboardRecyclerAdapter;
import com.example.mmm.model.User;
import com.example.mmm.viewmodel.GameViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.mmm.Utils.SP_KEY;

public class LeaderboardActivity extends AppCompatActivity {

    List<User> users = new ArrayList<>();
    RecyclerView recyclerView;
    LeaderboardRecyclerAdapter adapter;
    GameViewModel model;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        adapter = new LeaderboardRecyclerAdapter(users);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        model = (new ViewModelProvider(this)).get(GameViewModel.class);

        model.getUsers().observe(LeaderboardActivity.this, u -> {
            users.clear();
            users.addAll(u);
            sort(users);
            adapter.notifyDataSetChanged();
        });

        sharedPref = getSharedPreferences(
                SP_KEY, Context.MODE_PRIVATE);

    }

    private void sort(List<User> users) {
        Collections.sort(users, (user, t1) -> (int) (t1.Score - user.Score));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        model.removeListener();

        Intent intent;

        if("".equals(sharedPref.getString(Utils.USER_KEY,""))) {
            intent = new Intent(LeaderboardActivity.this, LoginActivity.class);
        } else {
            intent = new Intent(LeaderboardActivity.this, GameActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(0,0);

        finish();
    }

}
