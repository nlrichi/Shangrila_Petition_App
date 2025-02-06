package com.example.cw2_rya3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw2_rya3.R;
import com.example.cw2_rya3.models.Petition;

import java.util.List;

public class RespondPetitionsAdapter extends RecyclerView.Adapter<RespondPetitionsAdapter.RespondPetitionViewHolder> {

    private List<Petition> petitionsList;
    private RespondListener respondListener;

    public interface RespondListener {
        void onRespond(Petition petition, String response);
    }

    public RespondPetitionsAdapter(List<Petition> petitionsList, RespondListener respondListener) {
        this.petitionsList = petitionsList;
        this.respondListener = respondListener;
    }

    @NonNull
    @Override
    public RespondPetitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.respond_petition_item, parent, false);
        return new RespondPetitionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RespondPetitionViewHolder holder, int position) {
        Petition petition = petitionsList.get(position);

        holder.titleTextView.setText(petition.getTitle());
        holder.contentTextView.setText(petition.getContent());
        holder.signaturesTextView.setText("Signatures: " + petition.getSignaturesCount());
        holder.statusTextView.setText("Status: " + petition.getStatus());

        // Handle response submission
        holder.respondButton.setOnClickListener(v -> {
            String responseText = holder.responseEditText.getText().toString().trim();
            if (!responseText.isEmpty()) {
                respondListener.onRespond(petition, responseText);
                holder.responseEditText.setText(""); // Clear input field after response
            } else {
                holder.responseEditText.setError("Response cannot be empty");
            }
        });
    }

    @Override
    public int getItemCount() {
        return petitionsList.size();
    }

    public static class RespondPetitionViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, signaturesTextView, statusTextView;
        EditText responseEditText;
        Button respondButton;

        public RespondPetitionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.petitionTitleTextView);
            contentTextView = itemView.findViewById(R.id.petitionContentTextView);
            signaturesTextView = itemView.findViewById(R.id.petitionSignaturesTextView);
            statusTextView = itemView.findViewById(R.id.petitionStatusTextView);
            responseEditText = itemView.findViewById(R.id.responseEditText);
            respondButton = itemView.findViewById(R.id.respondButton);
        }
    }
}

