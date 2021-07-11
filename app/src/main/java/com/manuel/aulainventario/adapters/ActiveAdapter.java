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
import com.manuel.aulainventario.activities.ActiveFormActivity;
import com.manuel.aulainventario.models.Active;
import com.manuel.aulainventario.providers.ActiveProvider;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.utils.RelativeTime;

public class ActiveAdapter extends FirestoreRecyclerAdapter<Active, ActiveAdapter.ViewHolder> {
    Context context;
    AuthProvider mAuthProvider;
    ActiveProvider mActiveProvider;

    public ActiveAdapter(@NonNull FirestoreRecyclerOptions<Active> options, Context context) {
        super(options);
        this.context = context;
        mAuthProvider = new AuthProvider();
        mActiveProvider = new ActiveProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ActiveAdapter.ViewHolder holder, int position, @NonNull Active active) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        String activeId = snapshot.getId();
        holder.materialTextViewNumber.setText(String.valueOf(active.getNumber()));
        holder.materialTextViewKey.setText(active.getKey());
        holder.materialTextViewDescription.setText(active.getDescription());
        holder.materialTextViewAmount.setText(String.valueOf(active.getAmount()));
        holder.materialTextViewPrice.setText(String.valueOf(active.getPrice()));
        holder.materialTextViewTotal.setText(String.valueOf(active.getTotal()));
        holder.materialTextViewCondition.setText(active.getCondition());
        String time = RelativeTime.getTimeAgo(active.getTimestamp());
        holder.materialTextViewDate.setText(time);
        if (active.getIdTeacher().equals(mAuthProvider.getUid())) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewDelete.setVisibility(View.GONE);
        }
        holder.cardViewActive.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActiveFormActivity.class);
            intent.putExtra("idActiveUpdate", activeId);
            intent.putExtra("activeTitle", String.valueOf(active.getNumber()));
            intent.putExtra("activeSelect", true);
            context.startActivity(intent);
        });
        holder.imageViewDelete.setOnClickListener(v -> mActiveProvider.delete(activeId).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Snackbar.make(holder.itemView, "Error al eliminar el registro", Snackbar.LENGTH_SHORT).show();
            }
        }));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_active, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardViewActive;
        MaterialTextView materialTextViewNumber, materialTextViewKey, materialTextViewDescription, materialTextViewAmount, materialTextViewPrice, materialTextViewTotal, materialTextViewCondition, materialTextViewDate;
        ShapeableImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewActive = itemView.findViewById(R.id.cardViewActive);
            materialTextViewNumber = itemView.findViewById(R.id.textViewActiveNumber);
            materialTextViewKey = itemView.findViewById(R.id.textViewActiveKey);
            materialTextViewDescription = itemView.findViewById(R.id.textViewActiveDescription);
            materialTextViewAmount = itemView.findViewById(R.id.textViewActiveAmount);
            materialTextViewPrice = itemView.findViewById(R.id.textViewActivePrice);
            materialTextViewTotal = itemView.findViewById(R.id.textViewActiveTotal);
            materialTextViewCondition = itemView.findViewById(R.id.textViewActiveCondition);
            materialTextViewDate = itemView.findViewById(R.id.textViewDateActive);
            imageViewDelete = itemView.findViewById(R.id.imageViewDeleteActive);
        }
    }
}