package com.example.csci310project2treehole;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference databaseReference;
    protected FirebaseStorage firebaseStorage;
    protected StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Initialize Firebase Database
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();

            // Initialize Firebase Storage with specific bucket
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();

            // Verify storage bucket
            String bucket = firebaseStorage.getReference().getBucket();
            Log.d(TAG, "Firebase Storage initialized with bucket: " + bucket);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
        }
    }
}