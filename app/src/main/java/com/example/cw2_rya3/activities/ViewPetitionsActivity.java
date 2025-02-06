package com.example.cw2_rya3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.cw2_rya3.R;
import com.example.cw2_rya3.adapters.PetitionsAdapter;
import com.example.cw2_rya3.database.DBConnection;
import com.example.cw2_rya3.models.Petition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ViewPetitionsActivity extends AppCompatActivity {

    private RecyclerView petitionsRecyclerView;
    private PetitionsAdapter petitionsAdapter;
    private List<Petition> petitionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_petitions);


        petitionsRecyclerView = findViewById(R.id.petitionsRecyclerView);
        petitionsList = new ArrayList<>();
        petitionsAdapter = new PetitionsAdapter(petitionsList, this::signPetition);

        petitionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        petitionsRecyclerView.setAdapter(petitionsAdapter);


        fetchPetitionsFromDatabase();
    }

    private void fetchPetitionsFromDatabase() {
        new Thread(() -> {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "SELECT p.id, p.title, p.content, p.status, u.full_name AS petitioner_name " +
                        "FROM petitions p JOIN users u ON p.petitioner_id = u.id";
                PreparedStatement stmt = conn.prepareStatement(query);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Petition petition = new Petition(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("status"),
                            rs.getString("petitioner_name")
                    );
                    petitionsList.add(petition);
                }

                runOnUiThread(() -> petitionsAdapter.notifyDataSetChanged());

                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch petitions. Please try again.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void signPetition(Petition petition) {
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userEmail = preferences.getString("user_email", null);

        if (userEmail == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                Connection conn = DBConnection.getConnection();

                // Check if the user already signed the petition
                String checkQuery = "SELECT COUNT(*) FROM signatures WHERE petitioner_id = (SELECT id FROM users WHERE email = ?) AND petition_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, userEmail);
                checkStmt.setInt(2, petition.getId());

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    runOnUiThread(() -> Toast.makeText(this, "You have already signed this petition.", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Insert signature
                String insertQuery = "INSERT INTO signatures (petitioner_id, petition_id) VALUES ((SELECT id FROM users WHERE email = ?), ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, userEmail);
                insertStmt.setInt(2, petition.getId());

                int rowsInserted = insertStmt.executeUpdate();
                runOnUiThread(() -> {
                    if (rowsInserted > 0) {
                        Toast.makeText(this, "Petition signed successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to sign petition. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

                rs.close();
                checkStmt.close();
                insertStmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

