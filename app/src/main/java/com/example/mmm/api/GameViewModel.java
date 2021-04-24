package com.example.mmm.api;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mmm.model.User;

import java.util.List;

public class GameViewModel extends ViewModel implements onDataRetrieved {

    private MutableLiveData<List<User>> users;
    private GameRepository repository = GameRepository.getInstance(this);

    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<>();
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        repository.retrieveData();
    }

    private void loadUsers(List<User> result) {
        users.setValue(result);
    }

    public void addUser(User user) {
        List<User> us = users.getValue();
        assert us != null;
        us.add(user);
        repository.addUser(us);
        users.postValue(us);
    }

    @Override
    public void notifyDataRetrieved(List<User> result) {
        loadUsers(result);
    }
}
