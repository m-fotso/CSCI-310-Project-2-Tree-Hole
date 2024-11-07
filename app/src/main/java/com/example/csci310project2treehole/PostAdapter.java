package com.example.csci310project2treehole;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.List;
import java.util.Date;

public class PostAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        super(context, R.layout.item_post, postList);
        this.context = context;
        this.postList = postList;
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

        return convertView;
    }
}