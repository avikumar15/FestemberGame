package com.example.mmm.viewmodel;

import android.widget.ListView;

import com.example.mmm.model.User;

import java.util.List;

public interface onDataRetrieved {
    public void notifyDataRetrieved(List<User> result);
}
