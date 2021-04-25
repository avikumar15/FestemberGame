package com.example.mmm.viewmodel;

import com.example.mmm.model.User;

import java.util.List;

public interface DataRetrievedInterface {
    public void notifyDataRetrieved(List<User> result);
}
