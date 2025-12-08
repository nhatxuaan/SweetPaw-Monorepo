package com.example.sweetpawapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sweetpawapp.App;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.cart.CartItem;
import com.example.sweetpawapp.data.model.cart.AddToCartRequest;
import com.example.sweetpawapp.data.model.cart.AddToCartResponse;
import com.example.sweetpawapp.data.model.cart.GetCartResponse;
import com.example.sweetpawapp.data.model.cart.RemoveCartRequest;
import com.example.sweetpawapp.data.model.cart.RemoveCartResponse;
import com.example.sweetpawapp.data.model.cart.UpdateCartQuantityRequest;
import com.example.sweetpawapp.data.model.cart.UpdateCartQuantityResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CartRepository {
    private static final String TAG = "CartRepository";
    private final ApiService apiService;

    public CartRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public LiveData<List<CartItem>> getCartItems() {
        MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>();

        // --- Lấy token và userId từ SharedPreferences ---
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        String userId = prefs.getUserId();
        Log.e(TAG, "Token "+ token);
        Log.e(TAG, "userId "+ userId);

        if (token == null || token.isEmpty() || userId == null || userId.isEmpty()) {
            Log.e(TAG, "Token hoặc userId null — chưa đăng nhập");
            cartItemsLiveData.setValue(null);
            return cartItemsLiveData;
        }

        Log.d(TAG, "Token: " + token.substring(0, Math.min(10, token.length())) + "...");
        Log.d(TAG, "UserId: " + userId);

        // --- Gọi API lấy giỏ hàng ---
        apiService.getCart("Bearer " + token, userId).enqueue(new Callback<GetCartResponse>() {
            @Override
            public void onResponse(Call<GetCartResponse> call, Response<GetCartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetCartResponse cartResponse = response.body();

                    if (cartResponse.getData() != null && cartResponse.getData().getItems() != null) {
                        List<CartItem> items = cartResponse.getData().getItems();
                        cartItemsLiveData.setValue(items);
                        Log.d(TAG, "Lấy giỏ hàng thành công: " + items.size() + " sản phẩm");
                    } else {
                        Log.w(TAG, "Giỏ hàng trống hoặc data null");
                        cartItemsLiveData.setValue(new ArrayList<>());
                    }
                } else {
                    Log.e(TAG, "Lỗi khi lấy giỏ hàng: code " + response.code());
                    cartItemsLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GetCartResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API giỏ hàng: " + t.getMessage(), t);
                cartItemsLiveData.setValue(null);
            }
        });

        return cartItemsLiveData;
    }

    //Thêm sản phẩm vào giỏ hàng
    public LiveData<Boolean> addToCart(String productId, int quantity) {
        MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        String userId = prefs.getUserId();
        Log.e(TAG, "Token " + token);
        Log.e(TAG, "userId " + userId);

        if (token == null || userId == null || token.isEmpty() || userId.isEmpty()) {
            Log.e(TAG, "Token hoặc userId trống — chưa đăng nhập");
            successLiveData.setValue(false);
            return successLiveData;
        }

        AddToCartRequest request = new AddToCartRequest(userId, productId, quantity);
        Log.d(TAG, "Gọi API thêm giỏ hàng: " + request.getProductId() + " x" + request.getQuantity());

        apiService.addToCart("Bearer " + token, request).enqueue(new Callback<AddToCartResponse>() {
            @Override
            public void onResponse(Call<AddToCartResponse> call, Response<AddToCartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AddToCartResponse body = response.body();
                    Log.d(TAG, "Thêm giỏ hàng: " + body.getMessage());

                    if (body.getStatus() == 200) {
                        successLiveData.setValue(true);
                    } else {
                        successLiveData.setValue(false);
                    }
                } else {
                    Log.e(TAG, "API lỗi code: " + response.code() +
                            " body: " + response.errorBody().toString());
                    successLiveData.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<AddToCartResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng khi thêm giỏ hàng: " + t.getMessage());
                successLiveData.setValue(false);
            }
        });

        return successLiveData;
    }
    // Xóa 1 sản phẩm khỏi giỏ hàng
    public LiveData<Boolean> removeItemFromCart(String productId) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token trống — chưa đăng nhập");
            resultLiveData.setValue(false);
            return resultLiveData;
        }

        Log.d(TAG, "Gọi API xóa sản phẩm khỏi giỏ: " + productId);

        // Gọi API xóa
        apiService.removeItemFromCart("Bearer " + token, productId)
                .enqueue(new Callback<RemoveCartResponse>() {
                    @Override
                    public void onResponse(Call<RemoveCartResponse> call, Response<RemoveCartResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RemoveCartResponse body = response.body();
                            Log.d(TAG, "Phản hồi server: " + body.getMessage());

                            if (body.getStatus() == 200) {
                                resultLiveData.setValue(true);
                            } else {
                                Log.e(TAG, "Lỗi logic API: " + body.getMessage());
                                resultLiveData.setValue(false);
                            }
                        } else {
                            Log.e(TAG, "Lỗi khi xóa sản phẩm: code " + response.code());
                            resultLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<RemoveCartResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng khi xóa sản phẩm: " + t.getMessage());
                        resultLiveData.setValue(false);
                    }
                });

        return resultLiveData;
    }
    //Hàm xóa sản phẩm được chọn
    public LiveData<Boolean> removeSelectedItems(List<String> selectedProductIds) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token trống — chưa đăng nhập");
            resultLiveData.setValue(false);
            return resultLiveData;
        }

        // Log danh sách sản phẩm cần xoá
        Log.d(TAG, "Gọi API xoá sản phẩm: " + selectedProductIds);

        // Tạo request body
        RemoveCartRequest request = new RemoveCartRequest(selectedProductIds);

        // Gọi API xoá
        apiService.removeSelectedItems("Bearer " + token, request)
                .enqueue(new Callback<RemoveCartResponse>() {
                    @Override
                    public void onResponse(Call<RemoveCartResponse> call, Response<RemoveCartResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RemoveCartResponse body = response.body();
                            Log.d(TAG, "Xoá sản phẩm thành công: " + body.getMessage());
                            resultLiveData.setValue(body.getStatus() == 200);
                        } else {
                            Log.e(TAG, "Lỗi khi xoá sản phẩm: code " + response.code());
                            resultLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<RemoveCartResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng khi xoá sản phẩm: " + t.getMessage(), t);
                        resultLiveData.setValue(false);
                    }
                });

        return resultLiveData;
    }

    // Hàm cập nhật số lượng sản phẩm
    public LiveData<Boolean> updateCartQuantity(String productId, int change) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();

        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token trống — chưa đăng nhập");
            resultLiveData.setValue(false);
            return resultLiveData;
        }

        UpdateCartQuantityRequest request = new UpdateCartQuantityRequest(productId, change);
        Log.d(TAG, "Gọi API cập nhật số lượng: " + productId + " | thay đổi = " + change);

        apiService.updateCartQuantity("Bearer " + token, request)
                .enqueue(new Callback<UpdateCartQuantityResponse>() {
                    @Override
                    public void onResponse(Call<UpdateCartQuantityResponse> call, Response<UpdateCartQuantityResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UpdateCartQuantityResponse body = response.body();
                            Log.d(TAG, "Phản hồi API cập nhật số lượng: " + body.getMessage());

                            if (body.getStatus() == 200) {
                                resultLiveData.setValue(true);
                            } else {
                                Log.e(TAG, "Cập nhật thất bại: " + body.getMessage());
                                resultLiveData.setValue(false);
                            }
                        } else {
                            Log.e(TAG, "Lỗi API khi cập nhật số lượng: code " + response.code());
                            resultLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateCartQuantityResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi mạng khi cập nhật số lượng: " + t.getMessage(), t);
                        resultLiveData.setValue(false);
                    }
                });

        return resultLiveData;
    }






}
