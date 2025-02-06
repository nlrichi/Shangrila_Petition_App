package com.example.cw2_rya3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cw2_rya3.R;
import com.example.cw2_rya3.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PetitionCommitteeDashboard extends AppCompatActivity {

    private SeekBar signatureThresholdSeekBar;
    private TextView thresholdValue;
    private Button applyThresholdButton;
    private Button viewAllPetitionsButton;
    private Button respondToPetitionsButton;
    private Button logoutButton;

    private int currentThreshold = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petition_committee_dashboard);
        signatureThresholdSeekBar = findViewById(R.id.signatureThresholdSeekBar);
        thresholdValue = findViewById(R.id.thresholdValue);
        applyThresholdButton = findViewById(R.id.applyThresholdButton);
        viewAllPetitionsButton = findViewById(R.id.viewAllPetitionsButton);
        respondToPetitionsButton = findViewById(R.id.respondToPetitionsButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Update threshold value on SeekBar change
        signatureThresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentThreshold = progress;
                thresholdValue.setText("Current Threshold: " + currentThreshold);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Apply new threshold
        applyThresholdButton.setOnClickListener(v -> applyThreshold());

        // Navigate to view all petitions
        viewAllPetitionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(PetitionCommitteeDashboard.this, ViewPetitionsActivity.class);
            startActivity(intent);
        });

        // Navigate to respond to petitions
        respondToPetitionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(PetitionCommitteeDashboard.this, RespondToPetitionsActivity.class);
            startActivity(intent);
        });

        // Logout
        logoutButton.setOnClickListener(v -> logout());
    }

    private void applyThreshold() {
        new Thread(() -> {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "UPDATE settings SET signature_threshold = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, currentThreshold);

                int rowsUpdated = stmt.executeUpdate();
                runOnUiThread(() -> {
                    if (rowsUpdated > 0) {
                        Toast.makeText(this, "Threshold updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update threshold.", Toast.LENGTH_SHORT).show();
                    }
                });

                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error updating threshold.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void logout() {
        Intent intent = new Intent(PetitionCommitteeDashboard.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

