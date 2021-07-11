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
import com.manuel.aulainventario.activities.DidacticFormActivity;
import com.manuel.aulainventario.models.Didactic;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.DidacticProvider;
import com.manuel.aulainventario.utils.RelativeTime;

public class DidacticAdapter extends FirestoreRecyclerAdapter<Didactic, DidacticAdapter.ViewHolder> {
    Context context;
    AuthProvider mAuthProvider;
    DidacticProvider mDidacticProvider;

    public DidacticAdapter(@NonNull FirestoreRecyclerOptions<Didactic> options, Context context) {
        super(options);
        this.context = context;
        mAuthProvider = new AuthProvider();
        mDidacticProvider = new DidacticProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull DidacticAdapter.ViewHolder holder, int position, @NonNull Didactic didactic) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        String didacticId = snapshot.getId();
        holder.materialTextViewNumber.setText(String.valueOf(didactic.getNumber()));
        holder.materialTextViewDescription.setText(didactic.getDescription());
        holder.materialTextViewAmount.setText(String.valueOf(didactic.getAmount()));
        holder.materialTextViewCondition.setText(didactic.getCondition());
        String time = RelativeTime.getTimeAgo(didactic.getTimestamp());
        holder.materialTextViewDate.setText(time);
        if (didactic.getIdTeacher().equals(mAuthProvider.getUid())) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewDelete.setVisibility(View.GONE);
        }
        holder.cardViewDidactic.setOnClickListener(v -> {
            Intent intent = new Intent(context, DidacticFormActivity.class);
            intent.putExtra("idDidacticUpdate", didacticId);
            intent.putExtra("didacticTitle", String.valueOf(didactic.getNumber()));
            intent.putExtra("didacticSelect", true);
            context.startActivity(intent);
        });
        holder.imageViewDelete.setOnClickListener(v -> mDidacticProvider.delete(didacticId).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Snackbar.make(holder.itemView, "Error al eliminar el registro", Snackbar.LENGTH_SHORT).show();
            }
        }));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_didactic, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardViewDidactic;
        MaterialTextView materialTextViewNumber, materialTextViewDescription, materialTextViewAmount, materialTextViewCondition, materialTextViewDate;
        ShapeableImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewDidactic = itemView.findViewById(R.id.cardViewDidactic);
            materialTextViewNumber = itemView.findViewById(R.id.textViewDidacticNumber);
            materialTextViewDescription = itemView.findViewById(R.id.textViewDidacticDescription);
            materialTextViewAmount = itemView.findViewById(R.id.textViewDidacticAmount);
            materialTextViewCondition = itemView.findViewById(R.id.textViewDidacticCondition);
            materialTextViewDate = itemView.findViewById(R.id.textViewDateDidactic);
            imageViewDelete = itemView.findViewById(R.id.imageViewDeleteDidactic);
        }
    }
}