package com.manuel.aulainventario.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class KinderProvider {
    CollectionReference mCollection;

    public KinderProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Kindergartens");
    }

    public Task<DocumentSnapshot> getKindergartens(String id) {
        return mCollection.document(id).get();
    }

    public Task<QuerySnapshot> getAllDocuments() {
        return mCollection.orderBy("name", Query.Direction.ASCENDING).get();
    }
}