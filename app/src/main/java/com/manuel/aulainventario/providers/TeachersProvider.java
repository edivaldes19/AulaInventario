package com.manuel.aulainventario.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.manuel.aulainventario.models.Teacher;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TeachersProvider {
    CollectionReference mCollection;

    public TeachersProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Teachers");
    }

    public Task<DocumentSnapshot> getTeacher(String id) {
        return mCollection.document(id).get();
    }

    public Task<Void> create(Teacher teacher) {
        return mCollection.document(teacher.getId()).set(teacher);
    }

    public Task<Void> update(Teacher teacher) {
        Map<String, Object> map = new HashMap<>();
        map.put("idKinder", teacher.getIdKinder());
        map.put("teachername", teacher.getTeachername());
        map.put("phone", teacher.getPhone());
        map.put("turn", teacher.getTurn());
        map.put("grade", teacher.getGrade());
        map.put("group", teacher.getGroup());
        map.put("timestamp", new Date().getTime());
        return mCollection.document(teacher.getId()).update(map);
    }

    public Task<QuerySnapshot> getAllTeacherDocuments() {
        return mCollection.get();
    }
}