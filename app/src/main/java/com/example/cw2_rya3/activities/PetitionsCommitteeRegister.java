package com.example.cw2_rya3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cw2_rya3.R;

public class PetitionsCommitteeRegister extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button registerButton;

    // Predefined credentials for the Committee
    private static final String COMMITTEE_EMAIL = "admin@petition.parliament.sr";
    private static final String COMMITTEE_PASSWORD = "2025%shangrila";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petitions_committee_register);

        usernameInput = findViewById(R.id.editTextText2);
        passwordInput = findViewById(R.id.editTextTextPassword);
        registerButton = findViewById(R.id.PCOregisterButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCommitteeLogin();
            }
        });
    }

    private void handleCommitteeLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check Committee member credentials are valid
        if (username.equals(COMMITTEE_EMAIL) && password.equals(COMMITTEE_PASSWORD)) {
            // Save session
            saveCommitteeSession();

            // Navigate to Committee Dashboard
            Toast.makeText(this, "Welcome, Committee Member!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PetitionsCommitteeRegister.this, PetitionCommitteeDashboard.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCommitteeSession() {
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_email", COMMITTEE_EMAIL);
        editor.putString("user_role", "Committee"); // Save role as "Committee"
        editor.apply();
    }
}
