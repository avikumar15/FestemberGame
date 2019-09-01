package com.example.mmm;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.HolderClass>
{

    @NonNull
    @Override
    public HolderClass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = (View) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout, viewGroup, false);

        HolderClass vh = new HolderClass(v);		//Return theme views
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderClass holderClass, int i) {

    }

    @Override
    public int getItemCount() {
        return 10000000;                    //look for better solution
    }

    public static class HolderClass extends RecyclerView.ViewHolder {

        public HolderClass(@NonNull View itemView) {
            super(itemView);
        }
    }
}
