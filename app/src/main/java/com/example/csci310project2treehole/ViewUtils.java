package com.example.csci310project2treehole;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ViewUtils {

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // Pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredItemCount = Math.min(listAdapter.getCount(), 5); // Display at least 5 items
        for (int i = 0; i < desiredItemCount; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (desiredItemCount - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}