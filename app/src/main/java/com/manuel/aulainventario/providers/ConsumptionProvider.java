package com.manuel.aulainventario.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.manuel.aulainventario.models.Consumption;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConsumptionProvider {
    CollectionReference mCollection;

    public ConsumptionProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Consumption");
    }

    public Task<Void> save(Consumption consumption) {
        String id = mCollection.document().getId();
        consumption.setId(id);
        return mCollection.document(id).set(consumption);
    }

    public Task<Void> update(Consumption consumption) {
        Map<String, Object> map = new HashMap<>();
        map.put("number", consumption.getNumber());
        map.put("description", consumption.getDescription());
        map.put("amount", consumption.getAmount());
        map.put("timestamp", new Date().getTime());
        return mCollection.document(consumption.getId()).update(map);
    }

    public Task<Void> delete(String id) {
        return mCollection.document(id).delete();
    }

    public Query getConsumptionByDescription(String description) {
        return mCollection.orderBy("description").startAt(description).endAt(description + '\uf8ff');
    }

    public Query getConsumptionByTeacher(String idTeacher) {
        return mCollection.whereEqualTo("idTeacher", idTeacher).orderBy("number", Query.Direction.ASCENDING);
    }

    public Task<DocumentSnapshot> getConsumptionById(String id) {
        return mCollection.document(id).get();
    }
}