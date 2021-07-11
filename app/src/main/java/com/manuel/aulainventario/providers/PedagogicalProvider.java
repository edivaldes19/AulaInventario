package com.manuel.aulainventario.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.manuel.aulainventario.models.Pedagogical;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PedagogicalProvider {
    CollectionReference mCollection;

    public PedagogicalProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Pedagogical");
    }

    public Task<Void> save(Pedagogical pedagogical) {
        String id = mCollection.document().getId();
        pedagogical.setId(id);
        return mCollection.document(id).set(pedagogical);
    }

    public Task<Void> update(Pedagogical pedagogical) {
        Map<String, Object> map = new HashMap<>();
        map.put("number", pedagogical.getNumber());
        map.put("description", pedagogical.getDescription());
        map.put("amount", pedagogical.getAmount());
        map.put("condition", pedagogical.getCondition());
        map.put("timestamp", new Date().getTime());
        return mCollection.document(pedagogical.getId()).update(map);
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

    public Query getPedagogicalByDescription(String description) {
        return mCollection.orderBy("description").startAt(description).endAt(description + '\uf8ff');
    }

    public Query getPedagogicalByTeacher(String idTeacher) {
        return mCollection.whereEqualTo("idTeacher", idTeacher).orderBy("number", Query.Direction.ASCENDING);
    }

    public Task<DocumentSnapshot> getPedagogicalById(String id) {
        return mCollection.document(id).get();
    }
}