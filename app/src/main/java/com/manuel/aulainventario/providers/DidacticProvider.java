package com.manuel.aulainventario.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.manuel.aulainventario.models.Didactic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DidacticProvider {
    CollectionReference mCollection;

    public DidacticProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Didactic");
    }

    public Task<Void> save(Didactic didactic) {
        String id = mCollection.document().getId();
        didactic.setId(id);
        return mCollection.document(id).set(didactic);
    }

    public Task<Void> update(Didactic didactic) {
        Map<String, Object> map = new HashMap<>();
        map.put("number", didactic.getNumber());
        map.put("description", didactic.getDescription());
        map.put("amount", didactic.getAmount());
        map.put("condition", didactic.getCondition());
        map.put("timestamp", new Date().getTime());
        return mCollection.document(didactic.getId()).update(map);
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

    public Query getDidacticByDescription(String description) {
        return mCollection.orderBy("description").startAt(description).endAt(description + '\uf8ff');
    }

    public Query getDidacticByTeacher(String idTeacher) {
        return mCollection.whereEqualTo("idTeacher", idTeacher).orderBy("number", Query.Direction.ASCENDING);
    }

    public Task<DocumentSnapshot> getDidacticById(String id) {
        return mCollection.document(id).get();
    }

    public Task<QuerySnapshot> getDidacticNumbers(String idTeacher) {
        return mCollection.whereEqualTo("idTeacher", idTeacher).get();
    }
}