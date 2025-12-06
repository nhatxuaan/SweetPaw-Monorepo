package com.example.sweetpawapp.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.notification.DateSection;
import com.example.sweetpawapp.data.model.notification.NotificationItem;
import com.example.sweetpawapp.ui.viewmodel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationDateAdapter extends RecyclerView.Adapter<NotificationDateAdapter.DateViewHolder> {

    private static final String TAG = "THÔNG BÁO";

    private List<DateSection> dateList;
    private NotificationViewModel notificationViewModel;
    private String token;
    private OnItemClickListener onItemClickListener;
    private boolean isAllTab;

    public NotificationDateAdapter(List<DateSection> dateList, NotificationViewModel viewModel, String token, boolean isAllTab) {
        this.dateList = dateList;
        this.notificationViewModel = viewModel;
        this.token = token;
        this.isAllTab = isAllTab;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateSection section = dateList.get(position);
        holder.tvDate.setText(section.getDate());

        holder.rvItem.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        NotificationAdapter childAdapter = new NotificationAdapter(section.getList(), notificationViewModel, token, isAllTab);

        childAdapter.setOnItemClickListener(item -> {

            Log.d(TAG, "Nhấn vào thông báo con: " + item.getTitle());

            if (onItemClickListener != null) {
                onItemClickListener.onItemClicked(item);
            }
        });

        holder.rvItem.setAdapter(childAdapter);
    }

    public void updateData(List<DateSection> newSections) {

        Log.d(TAG, "updateData: Cập nhật adapter, số section mới=" + newSections.size());

        this.dateList.clear();
        this.dateList.addAll(newSections);
        notifyDataSetChanged();
    }

    public void updateNotificationItem(NotificationItem item) {

        Log.d(TAG, "updateNotificationItem: " + item.getTitle());


        for (DateSection section : dateList) {
            List<NotificationItem> list = section.getList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getNotificationId().equals(item.getNotificationId())) {
                    list.set(i, item);
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    public void removeNotificationItem(NotificationItem item) {

        Log.d(TAG, "removeNotificationItem: " + item.getTitle());

        List<DateSection> emptySections = new ArrayList<>();
        for (DateSection section : dateList) {
            section.getList().removeIf(noti -> noti.getNotificationId().equals(item.getNotificationId()));
            if (section.getList().isEmpty()) {
                emptySections.add(section);
            }
        }
        dateList.removeAll(emptySections);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClicked(NotificationItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        RecyclerView rvItem;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            rvItem = itemView.findViewById(R.id.rvItem);
        }
    }
}
