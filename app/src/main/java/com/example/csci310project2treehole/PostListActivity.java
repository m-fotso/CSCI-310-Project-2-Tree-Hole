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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton profileButton, notificationButton;
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
    private Map<String, Boolean> userSubscriptions;

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

        // Load user subscriptions first, then posts
        loadUserSubscriptions();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        profileButton = findViewById(R.id.profile_button);
        notificationButton = findViewById(R.id.notification_button);
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
        String titleText = category != null ? "USC Tree Hole - " + category : "USC Tree Hole - Home";
        toolbarTitle.setText(titleText);

        // Setup notification button
        notificationButton.setOnClickListener(v -> {
            Intent notificationIntent = new Intent(PostListActivity.this, NotificationsActivity.class);
            startActivity(notificationIntent);
        });

        // Setup profile button
        profileButton.setOnClickListener(v -> {
            Intent profileIntent = new Intent(PostListActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });
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
            } else if (itemId == R.id.nav_categories) {
                newCategory = "Extra";
            }
            // nav_home case stays as null

            // Don't reload if we're already on this category
            if ((category == null && newCategory == null) ||
                    (category != null && category.equals(newCategory))) {
                return true;
            }

            if(newCategory != null) {
                if (newCategory.equals("Extra")) {
                    Intent intent = new Intent(PostListActivity.this, CategoryActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
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

    private void setupPostsList() {
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        postsListView.setAdapter(postAdapter);

        // Add animation to list items
        android.view.animation.LayoutAnimationController animation =
                android.view.animation.AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        postsListView.setLayoutAnimation(animation);

        postsListView.setOnItemClickListener((parent, view, position, id) -> {
            Post post = postList.get(position);
            if (post != null && post.getPostId() != null) {
                AnimUtils.buttonClickAnimation(view, () -> {
                    Intent intent = new Intent(PostListActivity.this, PostDetailActivity.class);
                    intent.putExtra("category", category != null ? category :
                            getCategoryForPost(post.getPostId()));
                    intent.putExtra("postId", post.getPostId());
                    startActivity(intent);
                });
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this::refreshPosts);
    }

    private String getCategoryForPost(String postId) {
        for (Post post : postList) {
            if (post.getPostId().equals(postId)) {
                return post.getCategory();
            }
        }
        return null;
    }

    private void setupClickListeners() {
        profileButton.setOnClickListener(v ->
                startActivity(new Intent(PostListActivity.this, ProfileActivity.class)));

        fabNewPost.setOnClickListener(v -> {
            Intent intent = new Intent(PostListActivity.this, NewPostActivity.class);
            if (category != null) {
                intent.putExtra("category", category);
            }
            startActivity(intent);
        });

        subscriptionChip.setOnClickListener(v -> toggleSubscription());

        if (emptyStateView != null) {
            View createFirstPostButton = emptyStateView.findViewById(R.id.create_first_post_button);
            if (createFirstPostButton != null) {
                createFirstPostButton.setOnClickListener(v -> {
                    Intent intent = new Intent(PostListActivity.this, NewPostActivity.class);
                    if (category != null) {
                        intent.putExtra("category", category);
                    }
                    startActivity(intent);
                });
            }
        }
    }

    private void loadUserSubscriptions() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId);

        userRef.child("subscriptions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userSubscriptions = new HashMap<>();
                for (DataSnapshot subSnapshot : snapshot.getChildren()) {
                    userSubscriptions.put(subSnapshot.getKey(),
                            subSnapshot.getValue(Boolean.class));
                }
                loadPosts();
                checkSubscriptionStatus();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PostListActivity.this,
                        "Failed to load subscriptions", Toast.LENGTH_SHORT).show();
                userSubscriptions = new HashMap<>();
                loadPosts();
            }
        });
    }

    private void loadPosts() {
        if (category != null) {
            loadCategoryPosts();
        } else {
            loadHomePosts();
        }
    }

    private void loadCategoryPosts() {
        postsRef.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(postSnapshot.getKey());
                        post.setCategory(category);
                        postList.add(post);
                    }
                }
                finalizePosts();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleLoadError(error);
            }
        });
    }

    private void loadHomePosts() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId);

        userRef.child("subscriptions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot subscriptionSnapshot) {
                Map<String, Boolean> subscriptions = new HashMap<>();
                for (DataSnapshot subSnapshot : subscriptionSnapshot.getChildren()) {
                    subscriptions.put(subSnapshot.getKey(),
                            subSnapshot.getValue(Boolean.class));
                }

                // Check if user has any active subscriptions
                boolean hasActiveSubscriptions = subscriptions.values().stream()
                        .anyMatch(value -> value != null && value);

                postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        postList.clear();
                        for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                            String categoryName = categorySnapshot.getKey();
                            Boolean isSubscribed = subscriptions.get(categoryName);

                            // Add posts if either:
                            // 1. User has no active subscriptions (show all posts)
                            // 2. User is subscribed to this category
                            if (!hasActiveSubscriptions ||
                                    (isSubscribed != null && isSubscribed)) {
                                for (DataSnapshot postSnapshot : categorySnapshot.getChildren()) {
                                    Post post = postSnapshot.getValue(Post.class);
                                    if (post != null) {
                                        post.setPostId(postSnapshot.getKey());
                                        post.setCategory(categoryName);
                                        postList.add(post);
                                    }
                                }
                            }
                        }
                        finalizePosts();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        handleLoadError(error);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleLoadError(error);
            }
        });
    }

    private void finalizePosts() {
        Collections.sort(postList, (p1, p2) ->
                Long.compare(p2.getTimestamp(), p1.getTimestamp()));
        updateEmptyState();
        AnimUtils.fadeIn(postsListView, 300);
        postAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void handleLoadError(DatabaseError error) {
        Toast.makeText(PostListActivity.this,
                "Failed to load posts: " + error.getMessage(),
                Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
        updateEmptyState();
    }

    private void refreshPosts() {
        AnimUtils.fadeOut(postsListView, 300, () -> {
            swipeRefreshLayout.setRefreshing(true);
            loadUserSubscriptions();
        });
    }

    private void updateEmptyState() {
        if (postList.isEmpty()) {
            AnimUtils.fadeIn(emptyStateView, 300);
            AnimUtils.fadeOut(mainContentView, 300, null);
            emptyStateView.setVisibility(View.VISIBLE);
            mainContentView.setVisibility(View.GONE);

            TextView emptyStateText = emptyStateView.findViewById(R.id.empty_state_text);
            if (emptyStateText != null) {
                if (category != null) {
                    emptyStateText.setText("No posts in " + category);
                } else {
                    emptyStateText.setText("No posts in your feed");
                }
            }
        } else {
            AnimUtils.fadeOut(emptyStateView, 300, null);
            AnimUtils.fadeIn(mainContentView, 300);
            emptyStateView.setVisibility(View.GONE);
            mainContentView.setVisibility(View.VISIBLE);
        }
    }

    private void checkSubscriptionStatus() {
        if (category != null) {
            subscriptionChip.setVisibility(View.VISIBLE);
            Boolean isSubscribed = userSubscriptions.get(category);
            subscriptionChip.setChecked(Boolean.TRUE.equals(isSubscribed));
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
                if (userSubscriptions != null) {
                    userSubscriptions.put(category, result);
                }
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
        refreshPosts();
    }
}