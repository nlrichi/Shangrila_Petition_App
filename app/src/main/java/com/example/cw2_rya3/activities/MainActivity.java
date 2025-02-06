package com.example.cw2_rya3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cw2_rya3.R;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button petitionerDashboardButton, committeeDashboardButton, logoutButton;
    private String userEmail, userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if a user session exists
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userEmail = preferences.getString("user_email", null);
        userRole = preferences.getString("user_role", null);

        if (userEmail == null || userRole == null) {
            // If no session exists, redirect to the Login activity
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialize views
        welcomeTextView = findViewById(R.id.welcomeTextView);
        petitionerDashboardButton = findViewById(R.id.petitionerDashboardButton);
        committeeDashboardButton = findViewById(R.id.committeeDashboardButton);
        logoutButton = findViewById(R.id.logoutButton);

        welcomeTextView.setText("Welcome to Shangri-La Petition Platform\n" + userEmail);

        // Show appropriate buttons based on user role
        if ("Petitioner".equals(userRole)) {
            petitionerDashboardButton.setVisibility(View.VISIBLE);
        } else if ("Committee".equals(userRole)) {
            committeeDashboardButton.setVisibility(View.VISIBLE);
        }

        petitionerDashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PetitionersDashboard.class);
            startActivity(intent);
        });

        committeeDashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PetitionCommitteeDashboard.class);
            startActivity(intent);
        });

        // Handle Logout
        logoutButton.setOnClickListener(v -> logout());
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void logout() {
        // Clear user session
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to Login Activity
        redirectToLogin();
    }
}
