package com.example.cw2_rya3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.cw2_rya3.R;
import com.example.cw2_rya3.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PetitionersDashboard extends AppCompatActivity {

    private EditText petitionTitleInput;
    private EditText petitionContentInput;
    private Button createPetitionButton;
    private Switch viewPetitionsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petitioners_dashboard);

        petitionTitleInput = findViewById(R.id.petitionTitle);
        petitionContentInput = findViewById(R.id.petitionContent);
        createPetitionButton = findViewById(R.id.createPetitionButton);
        viewPetitionsSwitch = findViewById(R.id.viewPetitionsSwitch);

        // Handle Create Petition
        createPetitionButton.setOnClickListener(v -> createPetition());

        // Handle View All Petitions Switch
        viewPetitionsSwitch.setOnClickListener(v -> {
            if (viewPetitionsSwitch.isChecked()) {
                Intent intent = new Intent(PetitionersDashboard.this, ViewPetitionsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createPetition() {
        String title = petitionTitleInput.getText().toString().trim();
        String content = petitionContentInput.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please fill in both title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.length() > 100) {
            Toast.makeText(this, "Title must be 100 characters or less.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get logged-in user's email from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userEmail = preferences.getString("user_email", null);

        if (userEmail == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert petition into database
        new Thread(() -> {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "INSERT INTO petitions (title, content, petitioner_id, status) " +
                        "VALUES (?, ?, (SELECT id FROM users WHERE email = ?), 'open')";
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setString(1, title);
                stmt.setString(2, content);
                stmt.setString(3, userEmail); // Use the logged-in user's email

                int rowsInserted = stmt.executeUpdate();
                runOnUiThread(() -> {
                    if (rowsInserted > 0) {
                        Toast.makeText(this, "Petition created successfully!", Toast.LENGTH_SHORT).show();
                        petitionTitleInput.setText("");
                        petitionContentInput.setText("");
                    } else {
                        Toast.makeText(this, "Failed to create petition. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

