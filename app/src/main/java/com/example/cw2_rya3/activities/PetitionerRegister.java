package com.example.cw2_rya3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cw2_rya3.R;
import com.example.cw2_rya3.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PetitionerRegister extends AppCompatActivity {

    private EditText emailInput, fullNameInput, dobInput, passwordInput, confirmPasswordInput, bioIDInput;
    private Button registerButton, scanQRButton;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petitioner_register);

        emailInput = findViewById(R.id.loginID);
        fullNameInput = findViewById(R.id.FullName);
        dobInput = findViewById(R.id.Dob);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.password);
        bioIDInput = findViewById(R.id.BioID);
        registerButton = findViewById(R.id.petitionsCommitteeRegisterButton);
        scanQRButton = findViewById(R.id.button4);
        progressBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences("SLPP_Preferences", MODE_PRIVATE);
        progressBar.setVisibility(View.INVISIBLE);

        registerButton.setOnClickListener(v -> registerPetitioner());

        scanQRButton.setOnClickListener(v -> Toast.makeText(this, "QR Scan Feature Not Yet Implemented", Toast.LENGTH_SHORT).show());
    }

    private void registerPetitioner() {
        String email = emailInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String bioID = bioIDInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(dob) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(bioID)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bioID.length() != 10) {
            Toast.makeText(this, "BioID must be 10 characters long!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                // Check if BioID exists and is not used
                String bioIDCheckQuery = "SELECT used FROM bioid WHERE code = '" + bioID + "'";
                ResultSet bioIDResult = DBConnection.executeSelectQuery(bioIDCheckQuery);

                if (bioIDResult.next()) {
                    if (bioIDResult.getInt("used") == 1) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "BioID is already used!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        });
                        return;
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Invalid BioID!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                    return;
                }

                // Check if email already exists
                String emailCheckQuery = "SELECT COUNT(*) FROM users WHERE email = '" + email + "'";
                ResultSet emailResult = DBConnection.executeSelectQuery(emailCheckQuery);

                if (emailResult.next() && emailResult.getInt(1) > 0) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                    return;
                }

                // Insert user data
                String hashedPassword = hashPassword(password);
                String insertQuery = "INSERT INTO users (email, full_name, dob, password_hash, bioid, role) " +
                        "VALUES ('" + email + "', '" + fullName + "', '" + dob + "', '" + hashedPassword + "', '" + bioID + "', 'Petitioner')";
                DBConnection.executeQuery(insertQuery);

                // Mark BioID as used
                String updateBioIDQuery = "UPDATE bioid SET used = 1 WHERE code = '" + bioID + "'";
                DBConnection.executeQuery(updateBioIDQuery);

                // Save email in SharedPreferences
                sharedPreferences.edit().putString("lastLoginEmail", email).apply();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PetitionerRegister.this, Login.class);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error occurred during registration.", Toast.LENGTH_SHORT).show());
            } finally {
                runOnUiThread(() -> progressBar.setVisibility(View.INVISIBLE));
            }
        }).start();
    }



    private String hashPassword(String password) {
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


