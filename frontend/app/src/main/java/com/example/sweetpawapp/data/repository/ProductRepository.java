package com.example.sweetpawapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sweetpawapp.App;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.product.FavoriteResponse;
import com.example.sweetpawapp.data.model.product.GetProductDetailResponse;
import com.example.sweetpawapp.data.model.product.Product;
import com.example.sweetpawapp.data.model.product.FilterProductRequest;
import com.example.sweetpawapp.data.model.product.FilterProductResponse;
import com.example.sweetpawapp.data.model.product.ProductDetail;
import com.example.sweetpawapp.data.model.product.ProductResponse;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteRequest;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ProductRepository {

    private static final String TAG = "PRODUCT";
    private final ApiService apiService;

    public ProductRepository() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void getProductsByCategory(String categoryName, final ProductCallback callback) {
        Call<ProductResponse> call = apiService.getProductsByCategory(categoryName);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    if (productResponse.getData() != null) {
                        callback.onSuccess(productResponse.getData());
                    } else {
                        callback.onError("Không có dữ liệu trong phản hồi API");
                    }
                } else {
                    callback.onError("Lỗi khi lấy sản phẩm: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi gọi API " + categoryName + ": " + t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    public interface ProductCallback {
        void onSuccess(List<Product> products);
        void onError(String errorMessage);
    }

    public LiveData<FilterProductResponse> filterProducts(FilterProductRequest request) {
        MutableLiveData<FilterProductResponse> data = new MutableLiveData<>();

        Log.d(TAG, "Gửi request lọc sản phẩm:");
        Log.d(TAG, "PriceRange: " + (request.getPriceRange() != null ? request.getPriceRange().toString() : "null"));
        Log.d(TAG, "Rating: " + request.getRating());
        Log.d(TAG, "Keyword: " + request.getKeyword());

        apiService.filterProducts(request).enqueue(new Callback<FilterProductResponse>() {
            @Override
            public void onResponse(Call<FilterProductResponse> call, Response<FilterProductResponse> response) {
                Log.d(TAG, "API phản hồi filterProducts: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    FilterProductResponse result = response.body();

                    Log.d(TAG, "Lọc thành công: " + result.getMessage());
                    Log.d(TAG, "Tổng sản phẩm tìm được: " + (result.getData() != null ? result.getData().size() : 0));

                    if (result.getData() != null) {
                        for (Product p : result.getData()) {
                            Log.d(TAG, "   ➜ " + p.getName() + " | Giá: " + p.getPrice() + " | " + p.getRatingAvg());
                        }
                    }

                    data.postValue(result);

                } else {
                    Log.w(TAG, "API trả về lỗi hoặc body null. Code=" + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Chi tiết lỗi: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi đọc errorBody", e);
                        }
                    }
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<FilterProductResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi khi gọi API filterProducts: " + t.getMessage(), t);
                data.postValue(null);
            }
        });

        return data;
    }

    //Lấy chi tiết sản phẩm
    public LiveData<GetProductDetailResponse> getProductDetail(String productId) {
        MutableLiveData<GetProductDetailResponse> data = new MutableLiveData<>();
        //Không cần token
        Log.d(TAG, "Gọi API lấy chi tiết sản phẩm: " + productId);
        apiService.getProductDetail(productId).enqueue(new Callback<GetProductDetailResponse>() {
            @Override
            public void onResponse(Call<GetProductDetailResponse> call, Response<GetProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Lấy chi tiết sản phẩm thành công");
                    data.postValue(response.body());
                } else {
                    Log.d(TAG, "Lấy chi tiết sản phẩm thât bai");
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<GetProductDetailResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng khi gọi api lấy chi tiết sp: " + t.getMessage(), t);
                data.postValue(null);
            }
        });

        return data;
    }

    //Sản phẩm yêu thích
    public void toggleFavorite(MutableLiveData<ToggleFavoriteResponse> liveData, ToggleFavoriteRequest request) {
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        Log.d(TAG, "Gọi API sản phẩm yêu thích: " + request.getProductId());
        apiService.toggleFavorite("Bearer " + token, request).enqueue(new Callback<ToggleFavoriteResponse>() {
                    @Override
                    public void onResponse(Call<ToggleFavoriteResponse> call, Response<ToggleFavoriteResponse> response) {
                        Log.e("RAW_JSON", new Gson().toJson(response.body()));
                        if (response.isSuccessful()) {
                            liveData.setValue(response.body());
                            Log.d(TAG, "Gọi API sản phẩm yêu thích THÀNH CÔNG: " + request.getProductId());
                        } else {
                            liveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ToggleFavoriteResponse> call, Throwable t) {
                        liveData.setValue(null);
                    }
                });
    }

    //Lấy danh sách sản phẩm yêu thích
    public LiveData<FavoriteResponse> getFavorite() {
        MutableLiveData<FavoriteResponse> data = new MutableLiveData<>();
        PreferencesManager prefs = PreferencesManager.getInstance(App.getAppContext());
        String token = prefs.getToken();

        Log.d(TAG, "Gọi API lấy danh sách sản phẩm yêu thích ");
        apiService.getFavorites("Bearer " + token).enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Lấy danh sách yêu thích thành công");
                    data.postValue(response.body());
                } else {
                    Log.e(TAG, "Lấy danh sách yêu thích thât bai");
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi mạng khi gọi api lấy ds yêu thích" + t.getMessage(), t);
                data.postValue(null);
            }
        });

        return data;
    }



}
