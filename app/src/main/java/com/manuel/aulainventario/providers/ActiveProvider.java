package com.manuel.aulainventario.providers;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manuel.aulainventario.models.Active;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActiveProvider {
    CollectionReference mCollection;

    public ActiveProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Active");
    }

    public Task<Void> save(Active active) {
        String id = mCollection.document().getId();
        active.setId(id);
        return mCollection.document(id).set(active);
    }

    public Task<Void> update(Active active) {
        Map<String, Object> map = new HashMap<>();
        map.put("number", active.getNumber());
        map.put("key", active.getKey());
        map.put("description", active.getDescription());
        map.put("amount", active.getAmount());
        map.put("price", active.getPrice());
        map.put("total", active.getTotal());
        map.put("condition", active.getCondition());
        map.put("timestamp", new Date().getTime());
        return mCollection.document(active.getId()).update(map);
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

    public Query getActiveByDescription(String description) {
        return mCollection.orderBy("description").startAt(description).endAt(description + '\uf8ff');
    }

    public Query getActiveByTeacher(String idTeacher) {
        return mCollection.whereEqualTo("idTeacher", idTeacher).orderBy("number", Query.Direction.ASCENDING);
    }

    public Task<DocumentSnapshot> getActiveById(String id) {
        return mCollection.document(id).get();
    }

    public void getKeysByTeacher(AuthProvider authProvider, CoordinatorLayout coordinatorLayout, ArrayList<String> strings) {
        mCollection.whereEqualTo("idTeacher", authProvider.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains("key")) {
                            String allFields = snapshot.getString("key");
                            strings.add(allFields);
                        }
                    }
                }
            } else {
                Snackbar.make(coordinatorLayout, "Error al obtener las claves", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}