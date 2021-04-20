package com.example.mmm.firebase;

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
public class LeaderboardRepository {

    public final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users");
    public onDataRetrieved retrievedInterface;

    private static LeaderboardRepository repository = null;

    private LeaderboardRepository(onDataRetrieved retrievedInterface) {
        this.retrievedInterface = retrievedInterface;
    }

    public static LeaderboardRepository getInstance(onDataRetrieved retrievedInterface) {
        if(repository==null)
            repository = new LeaderboardRepository(retrievedInterface);

        return repository;
    }

    public void retrieveData() {
        ref.addValueEventListener(new ValueEventListener() {
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
    }

}
