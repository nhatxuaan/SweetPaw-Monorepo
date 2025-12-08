package com.example.sweetpawapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.sweetpawapp.data.local.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    // Thêm user mới hoặc cập nhật nếu trùng id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    // Lấy toàn bộ user trong bảng (trong trường hợp app đăng nhập nhiều acc)
    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> getAllUsers();

    // Lấy 1 user theo id (ví dụ: id = _id của MongoDB)
    @Query("SELECT * FROM users WHERE _id = :id LIMIT 1")
    LiveData<UserEntity> getUserById(String id);

    // Lấy user hiện tại (nếu chỉ có 1 người đăng nhập)
    @Query("SELECT * FROM users LIMIT 1")
    LiveData<UserEntity> getCurrentUser();

    // Cập nhật thông tin user (họ tên, sdt, ...)
    @Update
    void updateUser(UserEntity user);

    // Xóa 1 user cụ thể
    @Delete
    void deleteUser(UserEntity user);

    // Xóa toàn bộ user trong bảng (ví dụ: khi logout tất cả acc)
    @Query("DELETE FROM users")
    void deleteAllUsers();
}
