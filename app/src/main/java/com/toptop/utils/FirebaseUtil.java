package com.toptop.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {
    private final String FIREBASE_URL = "https://toptop-4d369-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private String tableName;

    public FirebaseUtil(String tableName) {
        this.tableName = tableName;
    }

    public DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference(tableName);
    }
}
