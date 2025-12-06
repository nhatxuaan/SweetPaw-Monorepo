package com.example.sweetpawapp.ui.viewmodel;


import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MediatorLiveData; //cập nhật gradle lại

import com.example.sweetpawapp.App;
import com.example.sweetpawapp.data.model.cart.CartItem;
import com.example.sweetpawapp.data.repository.CartRepository;


import java.util.List;

public class CartViewModel extends ViewModel {

    private final CartRepository cartRepository;
    private final MediatorLiveData<List<CartItem>> cartItems = new MediatorLiveData<>();

    public CartViewModel() {
        cartRepository = new CartRepository();
        loadCartItems(); // gọi khi khởi tạo ViewModel
    }
    //Trả về LiveData chứa danh sách sản phẩm trong giỏ hàng.
    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    //Gọi Repository để tải lại giỏ hàng từ server.
    public void loadCartItems() {
        LiveData<List<CartItem>> repoLiveData = cartRepository.getCartItems();

        // KHÔNG removeSource ngay lập tức — vì cần nhận cả giá trị rỗng
        cartItems.addSource(repoLiveData, items -> {
            cartItems.setValue(items);

            //Khi server trả về (dù rỗng) thì removeSource
            if (items != null) {
                cartItems.removeSource(repoLiveData);
            }
        });
    }

    //Làm mới lại giỏ hàng (gọi API lại)
    public void refreshCart() {
        loadCartItems();
    }
    // xóa 1 sản phẩm
    public void removeItem(String productId) {
        cartRepository.removeItemFromCart(productId).observeForever(success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(App.getAppContext(), "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show();
                refreshCart(); // gọi lại API để cập nhật danh sách
            } else {
                Toast.makeText(App.getAppContext(), "Xoá thất bại", Toast.LENGTH_SHORT).show();
            }
        });

    }
    // xóa sản phẩm được chọn
    public void removeSelectedItems(List<String> selectedIds) {
        cartRepository.removeSelectedItems(selectedIds).observeForever(success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(App.getAppContext(), "Đã xoá sản phẩm được chọn", Toast.LENGTH_SHORT).show();
                refreshCart();
            } else {
                Toast.makeText(App.getAppContext(), "Xoá thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Gọi API cập nhật số lượng
    // LifecycleOwner cung cấp thông tin trạng thái vòng đời
    public void updateQuantity(String productId, int change, LifecycleOwner owner) {
        cartRepository.updateCartQuantity(productId, change).observe(owner, success -> {
            if (Boolean.TRUE.equals(success)) {
               // Toast.makeText(App.getAppContext(), "Cập nhật số lượng thành công", Toast.LENGTH_SHORT).show();
                refreshCart(); // hiển thị giỏ hàng mới
            } else {
                Toast.makeText(App.getAppContext(), "Không thể cập nhật số lượng", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        cartItems.setValue(null);
    }
}
