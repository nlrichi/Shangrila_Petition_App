package com.example.cw2_rya3.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cw2_rya3.R;

public class Register extends AppCompatActivity {

    private Button petitionerRegisterButton, committeeRegisterButton;
    private TextView loginLink;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        petitionerRegisterButton = findViewById(R.id.petitionerRegisterButton);
        committeeRegisterButton = findViewById(R.id.petitionsCommitteeRegisterButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        // Handle Petitioner Registration Navigation
        petitionerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, PetitionerRegister.class);
                startActivity(intent);
            }
        });

        committeeRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, PetitionsCommitteeRegister.class);
                startActivity(intent);
            }
        });


        // Navigate back to Login
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
