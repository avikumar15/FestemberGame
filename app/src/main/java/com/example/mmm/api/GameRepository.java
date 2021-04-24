package com.example.mmm.api;

import androidx.annotation.NonNull;

import com.example.mmm.model.User;
import com.example.mmm.viewmodel.onDataRetrieved;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// Singleton Class
public class GameRepository {

    public final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users");
    public onDataRetrieved retrievedInterface;

    private static GameRepository repository = null;

    ValueEventListener eventListener;

    private GameRepository(onDataRetrieved retrievedInterface) {
        this.retrievedInterface = retrievedInterface;
    }

    public static GameRepository getInstance(onDataRetrieved retrievedInterface) {
        if(repository==null)
            repository = new GameRepository(retrievedInterface);

        repository.eventListener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<User> users = new ArrayList<>();

                for(DataSnapshot snapshot1:snapshot.getChildren()) {
                    users.add(snapshot1.getValue(User.class));
                }

                retrievedInterface.notifyDataRetrieved(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return repository;
    }

    public void retrieveData() {
        ref.addValueEventListener(eventListener);
    }

    public void removeListener() {
        ref.removeEventListener(eventListener);
    }

    public void addUser(List<User> user) {
        ref.setValue(user);
    }

}
