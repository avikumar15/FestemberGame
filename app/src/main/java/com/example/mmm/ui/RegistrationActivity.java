package com.example.mmm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mmm.R;
import com.example.mmm.Utils;
import com.example.mmm.model.User;
import com.example.mmm.viewmodel.GameViewModel;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    EditText etName, etUsername, etPass, etEmail;
    Button btnRegister;
    List<User> users;
    GameViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        bindViews();

        model = (new ViewModelProvider(this)).get(GameViewModel.class);
        users = model.getUsers().getValue();

        btnRegister.setOnClickListener(view -> register());
    }

    private void register() {

        String name = etName.getText().toString();
        String uname = etUsername.getText().toString();
        String pass = etPass.getText().toString();
        String email = etEmail.getText().toString();

        if(name.equals("") || uname.equals("") || pass.equals("") || email.equals("")) {
            Toast.makeText(this, "Input field cant be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<User> users = model.getUsers().getValue();

        boolean isUserAlreadyReg = false;

        assert users != null;
        for(int i = 0; i<users.size(); i++) {
            if(uname.equals(users.get(i).Username)) {
                isUserAlreadyReg = true;
                break;
            }
        }

        if(isUserAlreadyReg) {
            Toast.makeText(this, "User already registered! Aborting...", Toast.LENGTH_SHORT).show();
        } else {
            model.addUser(new User(uname, email, Utils.sha256(pass), name, (long) 0));
            Toast.makeText(this, "User Registered!", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindViews() {
        etName = findViewById(R.id.etName_Reg);
        etUsername = findViewById(R.id.etUserName_Reg);
        etPass = findViewById(R.id.etPassword_Reg);
        etEmail = findViewById(R.id.etEmail_Reg);
        btnRegister = findViewById(R.id.btn_Register);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        model.removeListener();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}