package com.example.cw2_rya3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw2_rya3.R;
import com.example.cw2_rya3.models.Petition;

import java.util.List;

public class PetitionsAdapter extends RecyclerView.Adapter<PetitionsAdapter.PetitionViewHolder> {

    private final List<Petition> petitionsList;
    private final OnPetitionActionListener actionListener;

    // Interface for handling actions on petitions
    public interface OnPetitionActionListener {
        void onSignPetition(Petition petition);
    }

    public PetitionsAdapter(List<Petition> petitionsList, OnPetitionActionListener actionListener) {
        this.petitionsList = petitionsList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public PetitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.petition_item, parent, false);
        return new PetitionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetitionViewHolder holder, int position) {
        Petition petition = petitionsList.get(position);

        holder.titleTextView.setText(petition.getTitle());
        holder.statusTextView.setText("Status: " + petition.getStatus());
        holder.petitionerNameTextView.setText("By: " + petition.getPetitionerName());

        // Handle click event for signing petitions
        holder.signPetitionButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onSignPetition(petition);
            }
        });

        // Disable the button if the petition is closed
        holder.signPetitionButton.setEnabled("open".equalsIgnoreCase(petition.getStatus()));
    }

    @Override
    public int getItemCount() {
        return petitionsList.size();
    }

    public static class PetitionViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, statusTextView, petitionerNameTextView;
        Button signPetitionButton;

        public PetitionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.petitionTitleTextView);
            statusTextView = itemView.findViewById(R.id.petitionStatusTextView);
            petitionerNameTextView = itemView.findViewById(R.id.petitionerNameTextView);
            signPetitionButton = itemView.findViewById(R.id.signPetitionButton);
        }
    }
}


