package com.example.cw2_rya3.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2_rya3.R;
import com.example.cw2_rya3.activities.MainActivity;
import com.example.cw2_rya3.activities.PetitionCommitteeDashboard;
import com.example.cw2_rya3.database.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;

public class Login extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView newUserTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.loginID);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        newUserTextView = findViewById(R.id.newUserTextView);

        loadLastLoggedInEmail();

        loginButton.setOnClickListener(v -> handleLogin());

        // Handle Redirect to Registration Activity
        newUserTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    private void loadLastLoggedInEmail() {
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String lastEmail = preferences.getString("user_email", null);
        if (lastEmail != null) {
            emailInput.setText(lastEmail);
        }
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for hardcoded admin credentials
        String adminEmail = "admin@petition.parliament.sr";
        String adminPassword = "2025%shangrila";

        if (email.equals(adminEmail) && password.equals(adminPassword)) {
            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, PetitionCommitteeDashboard.class);
            startActivity(intent);
            finish();
            return;
        }

        // Proceed with database authentication
        new Thread(() -> {
            try {
                // Query to validate user credentials
                String query = "SELECT role FROM users WHERE email = '" + email + "' AND password_hash = '" + hashPassword(password) + "'";
                ResultSet rs = DBConnection.executeSelectQuery(query);

                if (rs.next()) {
                    String role = rs.getString("role");
                    saveUserSession(email, role);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Redirect based on role
                        Intent intent = role.equals("Petitioner") ? new Intent(Login.this, MainActivity.class)
                                : new Intent(Login.this, PetitionCommitteeDashboard.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show());
                }

                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void saveUserSession(String email, String role) {
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_email", email);
        editor.putString("user_role", role); // Save the user's role (e.g., 'Petitioner' or 'Committee')
        editor.apply();
    }

    private String hashPassword(String password) {
        // Implement SHA-256 hashing
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


