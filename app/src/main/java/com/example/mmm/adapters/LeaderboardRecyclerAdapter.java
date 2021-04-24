package com.example.mmm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mmm.R;
import com.example.mmm.model.User;

import java.util.List;

public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.ViewHolder> {

    List<User> users;

    private LeaderboardRecyclerAdapter() {

    }

    public LeaderboardRecyclerAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvRank.setText((position+1)+"");
        holder.tvUname.setText(users.get(position).Username);
        holder.tvScore.setText(users.get(position).Score+"");
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRank, tvUname, tvScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRank = itemView.findViewById(R.id.tvRank);
            tvUname = itemView.findViewById(R.id.tvUserName);
            tvScore = itemView.findViewById(R.id.tvScore);

        }

    }
}
