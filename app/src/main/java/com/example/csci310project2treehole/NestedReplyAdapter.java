package com.example.csci310project2treehole;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NestedReplyAdapter extends RecyclerView.Adapter<NestedReplyAdapter.ReplyViewHolder> {
    private final Context context;
    private final List<Reply> replies;
    private final Map<String, List<Reply>> nestedReplies;
    private final OnReplyClickListener replyClickListener;
    private final String currentUserId;
    private final SimpleDateFormat dateFormat;
    private final DatabaseReference postRef;

    public interface OnReplyClickListener {
        void onReplyClick(Reply reply);
    }

    public NestedReplyAdapter(Context context, List<Reply> replies,
                              Map<String, List<Reply>> nestedReplies,
                              OnReplyClickListener replyClickListener,
                              DatabaseReference postRef) {
        this.context = context;
        this.replies = replies;
        this.nestedReplies = nestedReplies;
        this.replyClickListener = replyClickListener;
        this.postRef = postRef;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Reply reply = replies.get(position);
        holder.bind(reply, 0); // 0 is the initial indentation level
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {
        private final View indentationSpace;
        private final TextView authorTextView;
        private final TextView contentTextView;
        private final TextView timestampTextView;
        private final TextView showRepliesText;
        private final Button replyButton;
        private final ImageButton editButton;
        private final ImageButton deleteButton;
        private boolean isExpanded = false;

        ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            indentationSpace = itemView.findViewById(R.id.indentation_space);
            authorTextView = itemView.findViewById(R.id.reply_author_textview);
            contentTextView = itemView.findViewById(R.id.reply_content_textview);
            timestampTextView = itemView.findViewById(R.id.reply_timestamp_textview);
            showRepliesText = itemView.findViewById(R.id.show_replies_text);
            replyButton = itemView.findViewById(R.id.reply_button);
            editButton = itemView.findViewById(R.id.edit_reply_button);
            deleteButton = itemView.findViewById(R.id.delete_reply_button);
        }

        void bind(Reply reply, int indentationLevel) {
            // Set indentation width based on level
            ViewGroup.LayoutParams params = indentationSpace.getLayoutParams();
            params.width = indentationLevel * context.getResources()
                    .getDimensionPixelSize(R.dimen.reply_indentation_width);
            indentationSpace.setLayoutParams(params);
            indentationSpace.setVisibility(indentationLevel > 0 ? View.VISIBLE : View.GONE);

            // Set reply content
            authorTextView.setText(reply.getDisplayName());
            contentTextView.setText(reply.getContent());
            timestampTextView.setText(dateFormat.format(new Date(reply.getTimestamp())));

            // Show edit/delete buttons for user's own replies
            boolean isAuthor = reply.getAuthorId().equals(currentUserId);
            editButton.setVisibility(isAuthor ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(isAuthor ? View.VISIBLE : View.GONE);

            // Setup nested replies
            List<Reply> nested = nestedReplies.get(reply.getReplyId());
            if (nested != null && !nested.isEmpty()) {
                showRepliesText.setVisibility(View.VISIBLE);
                updateRepliesCount(nested.size());

                showRepliesText.setOnClickListener(v -> {
                    isExpanded = !isExpanded;
                    updateRepliesCount(nested.size());
                    if (isExpanded) {
                        // Show nested replies
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            for (int i = 0; i < nested.size(); i++) {
                                replies.add(position + 1 + i, nested.get(i));
                            }
                            notifyItemRangeInserted(position + 1, nested.size());
                        }
                    } else {
                        // Hide nested replies
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            for (int i = 0; i < nested.size(); i++) {
                                replies.remove(position + 1);
                            }
                            notifyItemRangeRemoved(position + 1, nested.size());
                        }
                    }
                });
            } else {
                showRepliesText.setVisibility(View.GONE);
            }

            // Click listeners
            replyButton.setOnClickListener(v -> {
                if (replyClickListener != null) {
                    replyClickListener.onReplyClick(reply);
                }
            });

            editButton.setOnClickListener(v -> showEditDialog(reply));
            deleteButton.setOnClickListener(v -> showDeleteDialog(reply));
        }

        private void updateRepliesCount(int count) {
            String text = count + " " + (count == 1 ? "reply" : "replies");
            if (isExpanded) {
                text = "Hide " + text.toLowerCase();
            } else {
                text = "Show " + text.toLowerCase();
            }
            showRepliesText.setText(text);
        }

        private void showEditDialog(Reply reply) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_edit_reply, null);

            EditText editContent = dialogView.findViewById(R.id.edit_reply_content);
            editContent.setText(reply.getContent());

            builder.setView(dialogView)
                    .setTitle("Edit Reply")
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newContent = editContent.getText().toString().trim();
                        if (!newContent.isEmpty()) {
                            updateReply(reply, newContent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void showDeleteDialog(Reply reply) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Reply")
                    .setMessage("Are you sure you want to delete this reply?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteReply(reply))
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void updateReply(Reply reply, String newContent) {
            DatabaseReference replyRef = postRef.child("replies").child(reply.getReplyId());
            replyRef.child("content").setValue(newContent)
                    .addOnSuccessListener(aVoid -> {
                        showToast("Reply updated successfully");
                        reply.setContent(newContent);
                        notifyItemChanged(getAdapterPosition());
                    })
                    .addOnFailureListener(e -> showToast("Failed to update reply"));
        }

        private void deleteReply(Reply reply) {
            DatabaseReference repliesRef = postRef.child("replies");
            // First, recursively delete all nested replies
            deleteNestedReplies(reply.getReplyId(), () -> {
                // Then delete the reply itself
                repliesRef.child(reply.getReplyId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                replies.remove(position);
                                notifyItemRemoved(position);
                                showToast("Reply deleted successfully");
                            }
                        })
                        .addOnFailureListener(e -> showToast("Failed to delete reply"));
            });
        }

        private void deleteNestedReplies(String parentReplyId, Runnable onComplete) {
            List<Reply> nested = nestedReplies.get(parentReplyId);
            if (nested == null || nested.isEmpty()) {
                onComplete.run();
                return;
            }

            final int[] remainingDeletions = {nested.size()};

            for (Reply nestedReply : nested) {
                deleteNestedReplies(nestedReply.getReplyId(), () -> {
                    postRef.child("replies").child(nestedReply.getReplyId()).removeValue()
                            .addOnCompleteListener(task -> {
                                remainingDeletions[0]--;
                                if (remainingDeletions[0] == 0) {
                                    onComplete.run();
                                }
                            });
                });
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}