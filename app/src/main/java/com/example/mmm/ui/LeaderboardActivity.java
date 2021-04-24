package com.example.mmm.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmm.R;
import com.example.mmm.adapters.LeaderboardRecyclerAdapter;
import com.example.mmm.viewmodel.GameViewModel;
import com.example.mmm.model.User;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    List<User> users = new ArrayList<>();
    RecyclerView recyclerView;
    LeaderboardRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        adapter = new LeaderboardRecyclerAdapter(users);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        GameViewModel model = (new ViewModelProvider(this)).get(GameViewModel.class);

        model.getUsers().observe(this, u -> {

            users.clear();
            users.addAll(u);

            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Added new user!", Toast.LENGTH_SHORT).show();
        });

    }
}
