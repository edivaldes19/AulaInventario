package com.manuel.aulainventario.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.activities.PedagogicalFormActivity;
import com.manuel.aulainventario.models.Pedagogical;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.PedagogicalProvider;
import com.manuel.aulainventario.utils.RelativeTime;

public class PedagogicalAdapter extends FirestoreRecyclerAdapter<Pedagogical, PedagogicalAdapter.ViewHolder> {
    Context context;
    AuthProvider mAuthProvider;
    PedagogicalProvider mPedagogicalProvider;

    public PedagogicalAdapter(@NonNull FirestoreRecyclerOptions<Pedagogical> options, Context context) {
        super(options);
        this.context = context;
        mAuthProvider = new AuthProvider();
        mPedagogicalProvider = new PedagogicalProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull PedagogicalAdapter.ViewHolder holder, int position, @NonNull Pedagogical pedagogical) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        String pedagogicalId = snapshot.getId();
        holder.materialTextViewNumber.setText(String.valueOf(pedagogical.getNumber()));
        holder.materialTextViewDescription.setText(pedagogical.getDescription());
        holder.materialTextViewAmount.setText(String.valueOf(pedagogical.getAmount()));
        holder.materialTextViewCondition.setText(pedagogical.getCondition());
        String time = RelativeTime.getTimeAgo(pedagogical.getTimestamp());
        holder.materialTextViewDate.setText(time);
        if (pedagogical.getIdTeacher().equals(mAuthProvider.getUid())) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewDelete.setVisibility(View.GONE);
        }
        holder.cardViewPedagogical.setOnClickListener(v -> {
            Intent intent = new Intent(context, PedagogicalFormActivity.class);
            intent.putExtra("idPedagogicalUpdate", pedagogicalId);
            intent.putExtra("pedagogicalTitle", String.valueOf(pedagogical.getNumber()));
            intent.putExtra("pedagogicalSelect", true);
            context.startActivity(intent);
        });
        holder.imageViewDelete.setOnClickListener(v -> mPedagogicalProvider.delete(pedagogicalId).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Snackbar.make(holder.itemView, "Error al eliminar el registro", Snackbar.LENGTH_SHORT).show();
            }
        }));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pedagogical, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardViewPedagogical;
        MaterialTextView materialTextViewNumber, materialTextViewDescription, materialTextViewAmount, materialTextViewCondition, materialTextViewDate;
        ShapeableImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewPedagogical = itemView.findViewById(R.id.cardViewPedagogical);
            materialTextViewNumber = itemView.findViewById(R.id.textViewPedagogicalNumber);
            materialTextViewDescription = itemView.findViewById(R.id.textViewPedagogicalDescription);
            materialTextViewAmount = itemView.findViewById(R.id.textViewPedagogicalAmount);
            materialTextViewCondition = itemView.findViewById(R.id.textViewPedagogicalCondition);
            materialTextViewDate = itemView.findViewById(R.id.textViewDatePedagogical);
            imageViewDelete = itemView.findViewById(R.id.imageViewDeletePedagogical);
        }
    }
}