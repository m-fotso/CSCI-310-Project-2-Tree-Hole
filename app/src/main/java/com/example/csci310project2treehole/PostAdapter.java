package com.example.csci310project2treehole;

import android.content.Context;
import android.view.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;

import java.util.List;
import java.util.Date;

public class PostAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> postList;
    private DatabaseReference usersRef;

    public PostAdapter(Context context, List<Post> postList) {
        super(context, R.layout.item_post, postList);
        this.context = context;
        this.postList = postList;
        this.usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = postList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.post_title_textview);
        TextView authorTextView = convertView.findViewById(R.id.post_author_textview);
        TextView timestampTextView = convertView.findViewById(R.id.post_timestamp_textview);
        TextView contentPreviewTextView = convertView.findViewById(R.id.post_content_preview);
        ImageView authorImageView = convertView.findViewById(R.id.post_author_image);

        titleTextView.setText(post.getTitle());

        // Handle null author name
        String authorDisplay = post.isAnonymous() ? "Anonymous" :
                (post.getAuthorName() != null ? post.getAuthorName() : "Unknown User");
        authorTextView.setText("By: " + authorDisplay);

        // Handle timestamp
        if (post.getTimestamp() > 0) {
            timestampTextView.setText(android.text.format.DateFormat.format(
                    "MMM dd, yyyy hh:mm a", new Date(post.getTimestamp())));
        } else {
            timestampTextView.setText("Just now");
        }

        // Add content preview
        if (post.getContent() != null && !post.getContent().isEmpty()) {
            String preview = post.getContent().length() > 100 ?
                    post.getContent().substring(0, 97) + "..." :
                    post.getContent();
            contentPreviewTextView.setText(preview);
            contentPreviewTextView.setVisibility(View.VISIBLE);
        } else {
            contentPreviewTextView.setVisibility(View.GONE);
        }

        // Load profile image
        if (post.isAnonymous()) {
            authorImageView.setImageResource(R.drawable.ic_default_profile);
        } else {
            loadProfileImage(post.getAuthorId(), authorImageView);
        }

        return convertView;
    }

    private void loadProfileImage(String userId, final ImageView imageView) {
        usersRef.child(userId).child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageUrl = snapshot.getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("default_profile_image_url")) {
                        Glide.with(context)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_default_profile)
                                .error(R.drawable.ic_default_profile)
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.ic_default_profile);
                    }
                } else {
                    imageView.setImageResource(R.drawable.ic_default_profile);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                imageView.setImageResource(R.drawable.ic_default_profile);
            }
        });
    }
}