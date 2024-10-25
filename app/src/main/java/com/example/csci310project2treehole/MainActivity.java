package com.example.csci310project2treehole;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Write data to Firebase
        writeMessageToFirebase();

        // Read data from Firebase
        readMessageFromFirebase();
    }

    private void writeMessageToFirebase() {
        // Writing a simple message to Firebase
        databaseReference.child("message").setValue("Hello, USC Tree Hole!")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Data written successfully.");
                    } else {
                        Log.e(TAG, "Failed to write data.", task.getException());
                    }
                });
    }

    private void readMessageFromFirebase() {
        // Reading the value from Firebase
        databaseReference.child("message").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message = dataSnapshot.getValue(String.class);
                if (message != null) {
                    Log.d(TAG, "Data read from Firebase: " + message);
                } else {
                    Log.e(TAG, "No data found in Firebase.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read data.", error.toException());
            }
        });
    }
}
