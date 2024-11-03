package com.example.csci310project2treehole;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.csci310project2treehole.R;
import com.example.csci310project2treehole.Post;
import java.util.List;

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

        titleTextView.setText(post.getTitle());
        authorTextView.setText("By: " + post.getAuthorName());
        timestampTextView.setText(android.text.format.DateFormat.format("MMM dd, yyyy hh:mm a", post.getTimestamp()));

        return convertView;
    }
}
