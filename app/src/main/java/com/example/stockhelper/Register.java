package com.example.stockhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity implements View.OnClickListener {

    public EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRepeatedPassword;
    private ProgressBar registerProgressBar;
    private Button registryButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextUsername = findViewById(R.id.registerUsername);
        editTextEmail = findViewById(R.id.registerEmail);
        editTextPassword = findViewById(R.id.registerPassword);
        editTextRepeatedPassword = findViewById(R.id.registerRepeatedPassword);
        registerProgressBar = findViewById(R.id.registerProgressBar);

        registryButton = findViewById(R.id.registerButton);
        registryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                RegisterUser();
                break;
        }
    }

    public void RegisterUser() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repeatedPassword = editTextRepeatedPassword.getText().toString().trim();
        boolean validUsername = false;
        boolean validEmail = false;
        boolean validPassword = false;

        switch (ValidateUsername(username)) {
            case 0:
                validUsername = true;
                break;
            case 1:
                editTextUsername.setError("This field is required!");
                editTextUsername.requestFocus();
                break;
            case 2:
                editTextUsername.setError("Username must be at least 6 characters!");
                editTextUsername.requestFocus();
                break;
            case 3:
                editTextUsername.setError("Username cannot be longer than 16 characters!");
                editTextUsername.requestFocus();
                break;
        }

        switch (ValidateEmail(email)) {
            case 0:
                validEmail = true;
                break;
            case 1:
                editTextEmail.setError("This field is required!");
                editTextEmail.requestFocus();
                break;
            case 2:
                editTextEmail.setError("Please provide valid email!");
                editTextEmail.requestFocus();
                break;
            case 3:
                editTextEmail.setError("Email must be at least 8 characters!");
                editTextEmail.requestFocus();
                break;
            case 4:
                editTextEmail.setError("Email cannot be longer than 40 characters!");
                editTextEmail.requestFocus();
                break;
        }

        switch (ValidatePassword(password, repeatedPassword)) {
            case 0:
                validPassword = true;
                break;
            case 1:
                editTextPassword.setError("This field is required!");
                editTextPassword.requestFocus();
                break;
            case 2:
                editTextRepeatedPassword.setError("This field is required!");
                editTextRepeatedPassword.requestFocus();
                break;
            case 3:
                editTextRepeatedPassword.setError("Passwords don't match!");
                editTextRepeatedPassword.requestFocus();
                break;
            case 4:
                editTextPassword.setError("Password must be at least 6 characters!");
                editTextPassword.requestFocus();
                break;
            case 5:
                editTextPassword.setError("Password cannot be longer than 40 characters!");
                editTextPassword.requestFocus();
                break;
        }

        if (validEmail && validUsername && validPassword) {
            registerProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(username, email);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Register.this, "You have registered successfully!", Toast.LENGTH_LONG).show();
                                            registerProgressBar.setVisibility(View.GONE);
                                            ReturnToLoginPage();
                                        } else {
                                            Toast.makeText(Register.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
                                            registerProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Register.this, "Something gone wrong! Try again!", Toast.LENGTH_LONG).show();
                                registerProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void ReturnToLoginPage() {
        startActivity(new Intent(this, Login.class));
    }

    public int ValidateUsername(String username) {
        if (username.isEmpty()) {
            return 1;
        }
        if (username.length() < 6) {
            return 2;
        }
        if (username.length() > 16) {
            return 3;
        }
        return 0;
    }

    public int ValidateEmail(String email) {
        if (email.isEmpty()) {
            return 1;
        }
        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            return 2;
        }
        if (email.length() < 8) {
            return 3;
        }
        if (email.length() > 40) {
            return 4;
        }
        return 0;
    }

    public int ValidatePassword(String password, String repeatedPassword) {
        if (password.isEmpty()) {
            return 1;
        }
        if (repeatedPassword.isEmpty()) {
            return 2;
        }
        if (!password.equals(repeatedPassword)) {
            return 3;
        }
        if (password.length() < 6) {
            return 4;
        }
        if (password.length() > 40) {
            return 5;
        }
        return 0;
    }

}