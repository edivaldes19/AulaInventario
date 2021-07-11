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
import com.manuel.aulainventario.activities.ConsumptionFormActivity;
import com.manuel.aulainventario.models.Consumption;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.ConsumptionProvider;
import com.manuel.aulainventario.utils.RelativeTime;

public class ConsumptionAdapter extends FirestoreRecyclerAdapter<Consumption, ConsumptionAdapter.ViewHolder> {
    Context context;
    AuthProvider mAuthProvider;
    ConsumptionProvider mConsumptionProvider;

    public ConsumptionAdapter(@NonNull FirestoreRecyclerOptions<Consumption> options, Context context) {
        super(options);
        this.context = context;
        mAuthProvider = new AuthProvider();
        mConsumptionProvider = new ConsumptionProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ConsumptionAdapter.ViewHolder holder, int position, @NonNull Consumption consumption) {
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
        String consumptionId = snapshot.getId();
        holder.materialTextViewNumber.setText(String.valueOf(consumption.getNumber()));
        holder.materialTextViewDescription.setText(consumption.getDescription());
        holder.materialTextViewAmount.setText(String.valueOf(consumption.getAmount()));
        String time = RelativeTime.getTimeAgo(consumption.getTimestamp());
        holder.materialTextViewDate.setText(time);
        if (consumption.getIdTeacher().equals(mAuthProvider.getUid())) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewDelete.setVisibility(View.GONE);
        }
        holder.cardViewConsumption.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConsumptionFormActivity.class);
            intent.putExtra("idConsumptionUpdate", consumptionId);
            intent.putExtra("consumptionTitle", String.valueOf(consumption.getNumber()));
            intent.putExtra("consumptionSelect", true);
            context.startActivity(intent);
        });
        holder.imageViewDelete.setOnClickListener(v -> mConsumptionProvider.delete(consumptionId).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Snackbar.make(holder.itemView, "Error al eliminar el registro", Snackbar.LENGTH_SHORT).show();
            }
        }));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_consumption, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardViewConsumption;
        MaterialTextView materialTextViewNumber, materialTextViewDescription, materialTextViewAmount, materialTextViewDate;
        ShapeableImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewConsumption = itemView.findViewById(R.id.cardViewConsumption);
            materialTextViewNumber = itemView.findViewById(R.id.textViewConsumptionNumber);
            materialTextViewDescription = itemView.findViewById(R.id.textViewConsumptionDescription);
            materialTextViewAmount = itemView.findViewById(R.id.textViewConsumptionAmount);
            materialTextViewDate = itemView.findViewById(R.id.textViewDateConsumption);
            imageViewDelete = itemView.findViewById(R.id.imageViewDeleteConsumption);
        }
    }
}