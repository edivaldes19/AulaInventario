package com.manuel.aulainventario.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ConditionsProvider {
    private final CollectionReference mCollection;

    public ConditionsProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Conditions");
    }

    public Task<QuerySnapshot> getAllDocuments() {
        return mCollection.orderBy("condition", Query.Direction.ASCENDING).get();
    }
}