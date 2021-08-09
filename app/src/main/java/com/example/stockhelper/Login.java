package com.example.stockhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private String email, password;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView registerText;
    private ProgressBar loginProgressBar;
    private Button submitButton;
    private FirebaseAuth mAuth;
    private String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        registerText = (TextView) findViewById(R.id.registerText);
        submitButton = (Button) findViewById((R.id.login_button));
        loginProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);

        registerText.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            loginProgressBar.setVisibility(View.VISIBLE);
            DataBaseRequest();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerText:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.login_button:
                AppLogin();
        }
    }

    private void DataBaseRequest(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child("Users").child(mAuth.getCurrentUser().getUid()).child("username").getValue(String.class);
                ChangeActivity(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loginProgressBar.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Couldn't login!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ChangeActivity(String username){
        loginProgressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, Menu.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void AppLogin() {
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        if (email.isEmpty()) {
            emailInput.setError("This field is required!");
            emailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please provide valid email");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("This field is required!");
            passwordInput.requestFocus();
            return;
        }

        loginProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DataBaseRequest();
                        } else {
                            loginProgressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Wrong email or password!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}