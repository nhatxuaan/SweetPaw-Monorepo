package com.example.sweetpawapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sweetpawapp.data.local.dao.UserDao;
import com.example.sweetpawapp.data.local.dao.ProductDao;
import com.example.sweetpawapp.data.local.entity.ProductEntity;
import com.example.sweetpawapp.data.local.entity.UserEntity;

@Database(
        entities = {
                UserEntity.class,
                ProductEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // Khai báo DAO
    public abstract UserDao userDao();
    public abstract ProductDao productDao();

    // Singleton để tạo 1 instance duy nhất
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "sweetpaw_db" // Tên file SQLite trong máy
                            )
                            .fallbackToDestructiveMigration() // Xóa DB nếu version thay đổi
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
