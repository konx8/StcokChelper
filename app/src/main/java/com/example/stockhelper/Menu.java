package com.example.stockhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity implements View.OnClickListener {

    private TextView welcomeText;
    private Button searchButton;
    private Button favoriteButton;
    private Button profileButton;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        username = getIntent().getExtras().getString("username");


        welcomeText = findViewById(R.id.menuWelcomeText);
        searchButton = findViewById(R.id.menuSearchButton);
        favoriteButton = findViewById(R.id.menuFavoriteButton);
        profileButton = findViewById(R.id.menuProfileButton);
        logoutButton = findViewById(R.id.menuLogoutButton);

        searchButton.setOnClickListener(this);
        favoriteButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        welcomeText.setText("Welcome " + username + "!");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.menuSearchButton:
                openSearchActivity();
                break;
            case R.id.menuFavoriteButton:
                openFavActivity();
                break;
            case R.id.menuProfileButton:
                openProfileActivity();
                break;
            case R.id.menuLogoutButton:
                Toast.makeText(Menu.this, "Logged out", Toast.LENGTH_LONG).show();
                firebaseLogout();
                break;
    }
}

    public void openSearchActivity(){
        Intent searchIntent = new Intent(this, Search.class);
        startActivity(searchIntent);
    }
    public void openProfileActivity() {
        Intent profileIntent = new Intent(this,Profile.class);
        startActivity(profileIntent);
    }

    public void openFavActivity() {
        Intent favIntent = new Intent(this,Favorite.class);
        startActivity(favIntent);
    }

    private void firebaseLogout() {
        mAuth.signOut();
        startActivity(new Intent(this, Login.class));
    }
}