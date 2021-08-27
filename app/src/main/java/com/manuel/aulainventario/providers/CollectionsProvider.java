package com.manuel.aulainventario.providers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class CollectionsProvider {
    CollectionReference mCollection;
    Context mContext;
    String mNameCollection;

    public CollectionsProvider(Context context, String collection) {
        this.mContext = context;
        this.mNameCollection = collection;
        mCollection = FirebaseFirestore.getInstance().collection(collection);
    }

    public void getAllTheDocumentsInACollectionAndSetTheAdapter(CoordinatorLayout coordinatorLayout, ArrayList<String> stringList, String field, Spinner spinner, String firstValue, MaterialTextView textView, String error) {
        mCollection.orderBy(field, Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains(field)) {
                            String values = snapshot.getString(field);
                            stringList.add(values);
                        }
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, stringList);
                spinner.setAdapter(arrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String valueSelected = parent.getItemAtPosition(position).toString().trim();
                        textView.setText(firstValue + valueSelected);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else {
                Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void getNumbersByTeacher(String idTeacher, CoordinatorLayout coordinatorLayout, ArrayList<Long> longs) {
        mCollection.whereEqualTo("idTeacher", idTeacher).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains("number")) {
                            long allFields = snapshot.getLong("number");
                            longs.add(allFields);
                        }
                    }
                }
            } else {
                Snackbar.make(coordinatorLayout, "Error al obtener los números", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}