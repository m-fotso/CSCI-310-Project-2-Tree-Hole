package com.example.csci310project2treehole;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.csci310project2treehole.R;
import com.example.csci310project2treehole.Reply;
import java.util.List;

public class ReplyAdapter extends ArrayAdapter<Reply> {

    private Context context;
    private List<Reply> replyList;

    public ReplyAdapter(Context context, List<Reply> replyList) {
        super(context, R.layout.item_reply, replyList);
        this.context = context;
        this.replyList = replyList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Reply reply = replyList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        }

        TextView contentTextView = convertView.findViewById(R.id.reply_content_textview);
        TextView authorTextView = convertView.findViewById(R.id.reply_author_textview);
        TextView timestampTextView = convertView.findViewById(R.id.reply_timestamp_textview);

        contentTextView.setText(reply.getContent());
        authorTextView.setText(reply.getAuthorName());
        timestampTextView.setText(android.text.format.DateFormat.format("MMM dd, yyyy hh:mm a", reply.getTimestamp()));

        return convertView;
    }
}
