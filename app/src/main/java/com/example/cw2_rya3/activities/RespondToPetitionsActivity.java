package com.example.cw2_rya3.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.cw2_rya3.R;
import com.example.cw2_rya3.adapters.RespondPetitionsAdapter;
import com.example.cw2_rya3.database.DBConnection;
import com.example.cw2_rya3.models.Petition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RespondToPetitionsActivity extends AppCompatActivity {

    private RecyclerView respondRecyclerView;
    private RespondPetitionsAdapter adapter;
    private List<Petition> petitionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_to_petitions);

        respondRecyclerView = findViewById(R.id.respondRecyclerView);
        petitionsList = new ArrayList<>();
        adapter = new RespondPetitionsAdapter(petitionsList, this::respondToPetition);

        respondRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        respondRecyclerView.setAdapter(adapter);

        // Fetch petitions that meet the threshold
        fetchThresholdMetPetitions();
    }

    private void fetchThresholdMetPetitions() {
        new Thread(() -> {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "SELECT p.id, p.title, p.content, p.signatures_count " +
                        "FROM petitions p " +
                        "JOIN settings s " +
                        "WHERE p.signatures_count >= s.signature_threshold AND p.status = 'open'";
                PreparedStatement stmt = conn.prepareStatement(query);

                ResultSet rs = stmt.executeQuery();

                // Populate petitionsList with petitions that meet the threshold
                while (rs.next()) {
                    Petition petition = new Petition(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            "open",
                            rs.getInt("signatures_count")
                    );
                    petitionsList.add(petition);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error fetching petitions.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void respondToPetition(Petition petition, String response) {
        new Thread(() -> {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "UPDATE petitions SET response = ?, status = 'closed' WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, response);
                stmt.setInt(2, petition.getId());

                int rowsUpdated = stmt.executeUpdate();

                runOnUiThread(() -> {
                    if (rowsUpdated > 0) {
                        Toast.makeText(this, "Response added and petition closed.", Toast.LENGTH_SHORT).show();
                        petitionsList.remove(petition); // Remove closed petition
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to update petition.", Toast.LENGTH_SHORT).show();
                    }
                });

                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error responding to petition.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}

