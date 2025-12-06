package com.example.sweetpawapp.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweetpawapp.data.model.notification.NotificationByDate;
import com.example.sweetpawapp.data.model.notification.NotificationItem;
import com.example.sweetpawapp.data.repository.NotificationRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationViewModel extends ViewModel {

    private static final String TAG = "THÔNG BÁO";

    private NotificationRepository repository;
    private MutableLiveData<NotificationByDate> allNotificationsLiveData;
    private MutableLiveData<NotificationByDate> unreadNotificationsLiveData;

    public NotificationViewModel() {
        repository = new NotificationRepository();
        allNotificationsLiveData = new MutableLiveData<>();
        unreadNotificationsLiveData = new MutableLiveData<>();
    }

    public LiveData<NotificationByDate> getAllNotificationsLiveData() {
        return allNotificationsLiveData;
    }

    public LiveData<NotificationByDate> getUnreadNotificationsLiveData() {
        return unreadNotificationsLiveData;
    }

    public void fetchAllNotifications(String token) {
        repository.getAllNotifications(token, allNotificationsLiveData);
    }

    public void fetchUnreadNotifications(String token) {
        repository.getUnreadNotifications(token, unreadNotificationsLiveData);
    }

    // LiveData cho item vừa đánh dấu đã đọc
    private MutableLiveData<NotificationItem> markReadLiveData = new MutableLiveData<>();
    public LiveData<NotificationItem> getMarkReadLiveData() {
        return markReadLiveData;
    }

    // ==================== MARK AS READ ====================
    public void markAsRead(String token, String notificationId, boolean isAllTab){

        Log.d(TAG, "NotificationViewModel: markAsRead called, notificationId=" + notificationId);

        repository.markNotificationAsRead(token, notificationId).observeForever(item -> {
            if(item != null){

                Log.d(TAG, "NotificationViewModel: markAsRead - nhận dữ liệu server, notificationId=" + item.getNotificationId() + ", isRead=" + item.isRead());

                markReadLiveData.setValue(item); // LiveData cho item vừa đánh dấu
                // Cập nhật danh sách allNotificationsLiveData
                if(isAllTab && allNotificationsLiveData.getValue() != null){
                    updateNotificationInByDate(allNotificationsLiveData.getValue(), item);
                    allNotificationsLiveData.setValue(allNotificationsLiveData.getValue());

                    Log.d(TAG, "NotificationViewModel: Cập nhật allNotificationsLiveData local");
                }

                if(!isAllTab && unreadNotificationsLiveData.getValue() != null){
                    removeNotificationFromUnread(allNotificationsLiveData.getValue(), item);
                    unreadNotificationsLiveData.setValue(unreadNotificationsLiveData.getValue());

                    Log.d(TAG, "NotificationViewModel: Cập nhật unreadNotificationsLiveData local");
                }
            } else {
                Log.e(TAG, "NotificationViewModel: markAsRead - Không nhận được phản hồi server");
            }
        });
    }


    // ==================== HELPER METHODS ====================
    private void updateNotificationInByDate(NotificationByDate notificationByDate, NotificationItem item){
        if(notificationByDate == null || notificationByDate.getNotificationsByDate() == null) return;

        for(String dateKey : notificationByDate.getNotificationsByDate().keySet()){
            for(NotificationItem n : notificationByDate.getNotificationsByDate().get(dateKey)){
                if(n.getNotificationId().equals(item.getNotificationId())){
                    n.setRead(true); // đánh dấu đã đọc local
                    return;
                }
            }
        }
    }

    private void removeNotificationFromUnread(NotificationByDate notificationByDate, NotificationItem item){
        if(notificationByDate == null || notificationByDate.getNotificationsByDate() == null) return;

        for(String dateKey : notificationByDate.getNotificationsByDate().keySet()){
            notificationByDate.getNotificationsByDate().get(dateKey).removeIf(n -> n.getNotificationId().equals(item.getNotificationId()));
        }
    }
}

