package com.example.csci310project2treehole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText, uscidEditText;
    private Button signupButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        // Connect UI components
        emailEditText = findViewById(R.id.semail);
        passwordEditText = findViewById(R.id.spassword);
        uscidEditText = findViewById(R.id.sid);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                addUser(email, password);
            }
        });
    }

    private void addUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Navigate to the main feed
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Login failed
                        Toast.makeText(SignUpActivity.this, "Sign Up failed. Please check all fields.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

