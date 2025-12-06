package com.example.sweetpawapp.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.notification.DateSection;
import com.example.sweetpawapp.data.model.notification.NotificationByDate;
import com.example.sweetpawapp.data.model.notification.NotificationItem;
import com.example.sweetpawapp.ui.adapter.NotificationDateAdapter;
import com.example.sweetpawapp.ui.viewmodel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "THÔNG BÁO";

    private ImageView imgBack;
    private TextView tabAll, tabUnread;
    private RecyclerView rvDate;

    private NotificationViewModel notificationViewModel;
    private NotificationDateAdapter adapter;
    private PreferencesManager pref;

    private boolean isAllTabSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongbao_activity);

        Log.d(TAG, "onCreate: Bắt đầu khởi tạo Activity thông báo");

        pref = PreferencesManager.getInstance(this);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        initUI();
        setupRecycler();
        setupEvents();

        selectTab(true);
        loadNotifications(true);
    }

    private void initUI() {
        imgBack = findViewById(R.id.imgBack);
        tabAll = findViewById(R.id.tabAll);
        tabUnread = findViewById(R.id.tabUnread);
        rvDate = findViewById(R.id.rvDate);
    }

    private void setupRecycler() {
        rvDate.setLayoutManager(new LinearLayoutManager(this));
        createAdapter();
    }

    private void createAdapter() {
        String token = "Bearer " + pref.getToken();
        adapter = new NotificationDateAdapter(new ArrayList<>(), notificationViewModel, token, isAllTabSelected);
        rvDate.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {

            Log.d(TAG, "Nhấn vào thông báo: " + item.getTitle() + " | Đã đọc=" + item.isRead());

            if (!item.isRead()) {
                item.setRead(true);
                adapter.updateNotificationItem(item);

                notificationViewModel.markAsRead(token, item.getNotificationId(),isAllTabSelected );

                Log.d(TAG, "Đánh dấu đã đọc: " + item.getNotificationId());

                if (!isAllTabSelected) {
                    adapter.removeNotificationItem(item);

                    Log.d(TAG, "Đã loại bỏ khỏi tab Chưa đọc");

                }
            }
        });
    }

    private void setupEvents() {
        imgBack.setOnClickListener(v -> finish());

        tabAll.setOnClickListener(v -> {
            if (!isAllTabSelected) {
                isAllTabSelected = true;

                Log.d(TAG, "Chuyển sang tab Tất cả");

                selectTab(true);
                createAdapter();
                loadNotifications(true);
            }
        });

        tabUnread.setOnClickListener(v -> {
            if (isAllTabSelected) {
                isAllTabSelected = false;

                Log.d(TAG, "Chuyển sang tab Chưa đọc");

                selectTab(false);
                createAdapter();
                loadNotifications(false);
            }
        });
    }

    private void selectTab(boolean isAllSelected) {
        if (isAllSelected) {
            tabAll.setBackgroundResource(R.drawable.bg_tab_selected);
            tabAll.setTextColor(getColor(R.color.pinkDark));

            tabUnread.setBackgroundResource(R.drawable.bg_tab_unselected);
            tabUnread.setTextColor(Color.parseColor("#757575"));
        } else {
            tabUnread.setBackgroundResource(R.drawable.bg_tab_selected);
            tabUnread.setTextColor(getColor(R.color.pinkDark));

            tabAll.setBackgroundResource(R.drawable.bg_tab_unselected);
            tabAll.setTextColor(Color.parseColor("#757575"));
        }
    }

    private void loadNotifications(boolean isAll) {
        String token = "Bearer " + pref.getToken();

        // Remove ALL observers trước khi gắn lại
        notificationViewModel.getAllNotificationsLiveData().removeObservers(this);
        notificationViewModel.getUnreadNotificationsLiveData().removeObservers(this);

        if (isAll) {
            notificationViewModel.fetchAllNotifications(token);
            notificationViewModel.getAllNotificationsLiveData().observe(this, data -> {
                updateAdapterData(data);
            });
        } else {
            notificationViewModel.fetchUnreadNotifications(token);
            notificationViewModel.getUnreadNotificationsLiveData().observe(this, data -> {
                updateAdapterData(data);
            });
        }
    }


    private void updateAdapterData(NotificationByDate notificationByDate) {
        if (notificationByDate == null || notificationByDate.getNotificationsByDate() == null) {
            adapter.updateData(new ArrayList<>());
            return;
        }

        List<DateSection> sections = new ArrayList<>();
        for (Map.Entry<String, List<NotificationItem>> entry : notificationByDate.getNotificationsByDate().entrySet()) {
            sections.add(new DateSection(entry.getKey(), new ArrayList<>(entry.getValue())));
        }

        adapter.updateData(sections);
    }

}
