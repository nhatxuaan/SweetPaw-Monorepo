package com.example.sweetpawapp.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.notification.NotificationItem;
import com.example.sweetpawapp.ui.viewmodel.NotificationViewModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "THÔNG BÁO";

    private List<NotificationItem> list;
    private NotificationViewModel notificationViewModel;
    private String token;
    private OnItemClickListener onItemClickListener;
    private boolean isAllTab;

    public NotificationAdapter(List<NotificationItem> list, NotificationViewModel viewModel, String token, boolean isAllTab) {
        this.list = list;
        this.notificationViewModel = viewModel;
        this.token = token;
        this.isAllTab = isAllTab;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = list.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getBody());
        holder.tvAction.setText("SweetPaw");

        float alpha = item.isRead() ? 0.6f : 1f;
        holder.tvTitle.setAlpha(alpha);
        holder.tvContent.setAlpha(alpha);

        holder.itemView.setOnClickListener(v -> {

            Log.d(TAG, "NotificationAdapter: Nhấn vào thông báo: " + item.getTitle() + " | Đã đọc=" + item.isRead());


            if (!item.isRead()) {
                item.setRead(true);                // cập nhật local UI trước
                notifyItemChanged(position);

                Log.d(TAG, "NotificationAdapter: Cập nhật UI local - đã đọc=true");

                // Gọi ViewModel để mark-as-read
                if (notificationViewModel != null && token != null) {

                    Log.d(TAG, "NotificationAdapter: Gọi ViewModel.markAsRead với notificationId=" + item.getNotificationId());

                    notificationViewModel.markAsRead(token, item.getNotificationId(), isAllTab);
                }

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(item);
                }
            }
        });

    }

    public interface OnItemClickListener {
        void onItemClicked(NotificationItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvAction;
        ImageView imgIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvAction = itemView.findViewById(R.id.tvAction);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }
}
