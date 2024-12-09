package com.example.csci310project2treehole;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private List<Notification> notifications;
    private SimpleDateFormat dateFormat;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.titleText.setText(notification.getTitle());
        holder.contentText.setText(notification.getMessage());
        holder.timeText.setText(dateFormat.format(new Date(notification.getTimestamp())));

        holder.itemView.setOnClickListener(v -> {
            if (notification.getPostId() != null && notification.getCategory() != null) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", notification.getPostId());
                intent.putExtra("category", notification.getCategory());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView titleText;
        TextView contentText;
        TextView timeText;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.notification_icon);
            titleText = itemView.findViewById(R.id.notification_title);
            contentText = itemView.findViewById(R.id.notification_text);
            timeText = itemView.findViewById(R.id.notification_time);
        }
    }
}