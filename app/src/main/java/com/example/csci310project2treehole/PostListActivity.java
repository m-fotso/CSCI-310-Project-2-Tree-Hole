package com.example.csci310project2treehole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton profileButton;
    private TextView toolbarTitle;
    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView postsListView;
    private FloatingActionButton fabNewPost;
    private Chip subscriptionChip;
    private View emptyStateView;
    private View mainContentView;

    private FirebaseAuth mAuth;
    private String userId;
    private String category;
    private DatabaseReference postsRef;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        category = getIntent().getStringExtra("category");
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        subscriptionManager = new SubscriptionManager(this);

        // Initialize UI components
        initializeViews();
        setupToolbar();
        setupPostsList();
        setupClickListeners();
        setupBottomNavigation();

        // Load initial data
        loadPosts();
        checkSubscriptionStatus();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        profileButton = findViewById(R.id.profile_button);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        postsListView = findViewById(R.id.posts_list_view);
        fabNewPost = findViewById(R.id.fab_new_post);
        subscriptionChip = findViewById(R.id.subscription_chip);
        emptyStateView = findViewById(R.id.empty_state_layout);
        mainContentView = findViewById(R.id.main_content);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarTitle.setText(category != null ? "USC Tree Hole - " + category : "USC Tree Hole");
    }

    private void setupPostsList() {
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        postsListView.setAdapter(postAdapter);

        // Set item click listener for posts
        postsListView.setOnItemClickListener((parent, view, position, id) -> {
            Post post = postList.get(position);
            Intent intent = new Intent(PostListActivity.this, PostDetailActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("postId", post.getPostId());
            startActivity(intent);
        });

        // Setup swipe refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);
    }

    private void setupClickListeners() {
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(PostListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        fabNewPost.setOnClickListener(v -> {
            Intent intent = new Intent(PostListActivity.this, NewPostActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });

        subscriptionChip.setOnClickListener(v -> toggleSubscription());

        if (emptyStateView != null) {
            View createFirstPostButton = emptyStateView.findViewById(R.id.create_first_post_button);
            if (createFirstPostButton != null) {
                createFirstPostButton.setOnClickListener(v -> {
                    Intent intent = new Intent(PostListActivity.this, NewPostActivity.class);
                    intent.putExtra("category", category);
                    startActivity(intent);
                });
            }
        }
    }

    private void setupBottomNavigation() {
        // Set initial selection
        int selectedItemId;
        if (category == null) {
            selectedItemId = R.id.nav_home;
        } else {
            switch (category) {
                case "Academic":
                    selectedItemId = R.id.nav_academic;
                    break;
                case "Life":
                    selectedItemId = R.id.nav_life;
                    break;
                case "Events":
                    selectedItemId = R.id.nav_events;
                    break;
                default:
                    selectedItemId = R.id.nav_home;
                    break;
            }
        }
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            String newCategory = null;  // Default to home
            int itemId = item.getItemId();

            if (itemId == R.id.nav_academic) {
                newCategory = "Academic";
            } else if (itemId == R.id.nav_life) {
                newCategory = "Life";
            } else if (itemId == R.id.nav_events) {
                newCategory = "Events";
            }
            // nav_home case stays as null

            // Don't reload if we're already on this category
            if ((category == null && newCategory == null) ||
                    (category != null && category.equals(newCategory))) {
                return true;
            }

            Intent intent = new Intent(PostListActivity.this, PostListActivity.class);
            if (newCategory != null) {
                intent.putExtra("category", newCategory);
            }
            startActivity(intent);
            finish();
            return true;
        });
    }

    private void loadPosts() {
        DatabaseReference ref = category != null ?
                postsRef.child(category) :
                postsRef;  // For home feed, look at all categories

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }

                // Sort posts by timestamp (newest first)
                Collections.sort(postList, (p1, p2) ->
                        Long.compare(p2.getTimestamp(), p1.getTimestamp()));

                // Update UI based on whether there are posts
                updateEmptyState();

                postAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PostListActivity.this,
                        "Failed to load posts: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        if (postList.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            mainContentView.setVisibility(View.GONE);

            // Update empty state message based on category
            TextView emptyStateText = emptyStateView.findViewById(R.id.empty_state_text);
            if (emptyStateText != null) {
                if (category != null) {
                    emptyStateText.setText("No posts yet in " + category);
                } else {
                    emptyStateText.setText("No posts in your subscribed categories");
                }
            }
        } else {
            emptyStateView.setVisibility(View.GONE);
            mainContentView.setVisibility(View.VISIBLE);
        }
    }

    private void checkSubscriptionStatus() {
        if (category != null) {  // Only show subscription chip for specific categories
            subscriptionChip.setVisibility(View.VISIBLE);
            subscriptionManager.checkSubscription(category, new SubscriptionManager.SubscriptionCallback() {
                @Override
                public void onSuccess(boolean isSubscribed) {
                    subscriptionChip.setChecked(isSubscribed);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PostListActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            subscriptionChip.setVisibility(View.GONE);
        }
    }

    private void toggleSubscription() {
        if (category == null) return;

        subscriptionManager.toggleSubscription(category, new SubscriptionManager.SubscriptionCallback() {
            @Override
            public void onSuccess(boolean result) {
                subscriptionChip.setChecked(result);
                String message = result ?
                        "Subscribed to " + category :
                        "Unsubscribed from " + category;
                Toast.makeText(PostListActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PostListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts();  // Refresh posts when returning to the activity
    }
}
