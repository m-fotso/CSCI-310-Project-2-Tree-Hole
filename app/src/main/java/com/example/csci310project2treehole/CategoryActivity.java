package com.example.csci310project2treehole;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity {

    private ListView categoryListView;
    private String[] categories = {"Academic", "Life", "Event"};
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryListView = findViewById(R.id.category_list_view);
        backButton = findViewById(R.id.back_button);

        // Set up the adapter to display categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categories);
        categoryListView.setAdapter(adapter);

        // Set item click listener to navigate to PostListActivity
        categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = categories[position];
            Intent intent = new Intent(CategoryActivity.this, PostListActivity.class);
            intent.putExtra("category", selectedCategory);
            startActivity(intent);
        });

        // Handle back button click
        backButton.setOnClickListener(v -> {
            // Navigate back to MainActivity
            startActivity(new Intent(CategoryActivity.this, MainActivity.class));
            finish();
        });
    }
}
