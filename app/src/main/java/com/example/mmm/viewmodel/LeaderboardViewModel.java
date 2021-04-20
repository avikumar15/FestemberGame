package com.example.mmm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mmm.firebase.LeaderboardRepository;
import com.example.mmm.model.User;

import java.util.List;

public class LeaderboardViewModel extends ViewModel implements onDataRetrieved {

    private MutableLiveData<List<User>> users;
    private LeaderboardRepository repository = LeaderboardRepository.getInstance(this);

    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<>();
            loadArticles();
        }
        return users;
    }

    private void loadArticles() {
        repository.retrieveData();
    }

    private void loadArticles(List<User> result) {
        users.setValue(result);
    }

    @Override
    public void notifyDataRetrieved(List<User> result) {
        loadArticles(result);
    }
}
