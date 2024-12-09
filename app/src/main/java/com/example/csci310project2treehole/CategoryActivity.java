package com.example.csci310project2treehole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyStateView;
    private Button backButton;
    private FloatingActionButton fabNewCategory;

    private DatabaseReference categoryRef;
    private CategoryAdapter categoryAdapter;
    private ArrayList<String> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Initialize Views
        initializeViews();

        // Set up RecyclerView
        setupRecyclerView();

        // Firebase Initialization
        categoryRef = FirebaseDatabase.getInstance().getReference("posts");

        // Fetch Categories
        fetchCategories();

        // Swipe to Refresh Listener
        swipeRefreshLayout.setOnRefreshListener(this::fetchCategories);

        // Floating Button for Adding New Category
        fabNewCategory.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, CategoryDetailActivity.class);
            startActivity(intent);
        });

        // Back Button Listener
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, PostListActivity.class);
            startActivity(intent);
        });
    }

    private void initializeViews() {
        categoryRecyclerView = findViewById(R.id.category_list_view);
        swipeRefreshLayout = findViewById(R.id.category_swipe_refresh);
        emptyStateView = findViewById(R.id.category_empty_state_layout);
        backButton = findViewById(R.id.back_button);
        fabNewCategory = findViewById(R.id.fab_new_category);
    }

    private void setupRecyclerView() {
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            // Handle Category Click
            Intent intent = new Intent(CategoryActivity.this, PostListActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void fetchCategories() {
        swipeRefreshLayout.setRefreshing(true);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();
                    if (categoryName != null) {
                        categoryList.add(categoryName);
                    }
                }

                updateUI();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryActivity.this, "Error loading categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateUI() {
        if (categoryList.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            categoryRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
        }
        categoryAdapter.notifyDataSetChanged();
    }
}
