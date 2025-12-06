package com.example.sweetpawapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.sweetpawapp.data.local.entity.ProductEntity;

import java.util.List;

// ProductDao giúp thao tác CRUD với bảng "products" trong SQLite
@Dao
public interface ProductDao {

    // Thêm sản phẩm mới (nếu trùng _id thì ghi đè)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(ProductEntity product);

    // Thêm danh sách sản phẩm (ví dụ: khi fetch từ API)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllProducts(List<ProductEntity> productList);

    // Lấy tất cả sản phẩm trong DB
    @Query("SELECT * FROM products")
    List<ProductEntity> getAllProducts();

    // Lấy 1 sản phẩm theo MongoDB _id
    @Query("SELECT * FROM products WHERE _id = :id LIMIT 1")
    ProductEntity getProductById(String id);

    // Cập nhật thông tin sản phẩm
    @Update
    void updateProduct(ProductEntity product);

    // Xóa 1 sản phẩm
    @Delete
    void deleteProduct(ProductEntity product);

    // Xóa toàn bộ bảng (ví dụ: refresh khi sync lại từ server)
    @Query("DELETE FROM products")
    void deleteAllProducts();
}
