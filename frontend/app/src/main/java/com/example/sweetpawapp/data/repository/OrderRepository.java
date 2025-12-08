package com.example.sweetpawapp.data.repository;

import android.content.SharedPreferences;
import android.util.Log;
import com.example.sweetpawapp.App;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.order.OrderDetail;
import com.example.sweetpawapp.data.model.order.OrderDetailResponse;
import com.example.sweetpawapp.data.model.order.OrderRequest;
import com.example.sweetpawapp.data.model.order.OrderResponse;
import com.example.sweetpawapp.data.model.order.Orders;
import com.example.sweetpawapp.data.model.order.OrdersListResponse;
import com.example.sweetpawapp.data.model.order.PaymentResponse;
import com.example.sweetpawapp.data.model.order.PaymentStatusResponse;
import com.example.sweetpawapp.data.model.order.PreviewOrderRequest;
import com.example.sweetpawapp.data.model.order.PreviewOrderResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;
import com.google.gson.Gson;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class OrderRepository {
    private static final String TAG = "Order";
    private ApiService apiService;
    private PreferencesManager prefs;


    public OrderRepository() {
        prefs = PreferencesManager.getInstance(App.getAppContext());
        apiService = RetrofitClient.getClient().create(ApiService.class);
        Log.d(TAG, "RetrofitClient initialized thành công: " + apiService);
    }

    public LiveData<PreviewOrderResponse> preview(PreviewOrderRequest request) {
        Log.d(String.valueOf(TAG).toString(), "Bắt đầu gọi API preview order: " + request);
        MutableLiveData<PreviewOrderResponse> result = new MutableLiveData<>();
        String token = prefs.getToken();
        apiService.previewOrder("Bearer " + token, request).enqueue(new Callback<PreviewOrderResponse>() {
            @Override
            public void onResponse(Call<PreviewOrderResponse> call, Response<PreviewOrderResponse> response) {
                Log.d("OrderRepository", "onResponse: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    PreviewOrderResponse previewResponse = response.body();
                    Log.d("OrderRepository", "Tính toán đơn hàng thành công: " + previewResponse.getData().getTotal());
                    result.setValue(previewResponse);
                } else {
                    Log.e("OrderRepository", "Lỗi phản hồi: " + response.code());
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PreviewOrderResponse> call, Throwable t) {
                Log.e("OrderRepository", "Lỗi kết nối: " + t.getMessage(), t);
                result.setValue(null);
            }

        });

        return result;
    }

    public LiveData<OrderResponse> order(OrderRequest request) {
        MutableLiveData<OrderResponse> data = new MutableLiveData<>();
        String token = prefs.getToken();

        Log.d(TAG, "Gửi yêu cầu tạo đơn hàng với token: " + token);

        apiService.order("Bearer " + token, request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                Log.e("RAW_JSON", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API trả về thành công: code=" + response.body().getData().getCode());
                    data.setValue(response.body());
                    Log.e(TAG, "Response " + response.body().getData().getOrderId());
                } else {
                    Log.e(TAG, "Lỗi phản hồi HTTP: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    public interface OnOrdersFetchedListener {
        void onFetched(List<Orders> orders);
        void onError();
    }

    public void getUserOrders(OnOrdersFetchedListener listener) {
        String token = prefs.getToken();
        apiService.getUserOrders("Bearer " + token).enqueue(new Callback<OrdersListResponse>() {
            @Override
            public void onResponse(Call<OrdersListResponse> call, Response<OrdersListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    listener.onFetched(response.body().getData());
                } else {
                    listener.onError();
                }
            }

            @Override
            public void onFailure(Call<OrdersListResponse> call, Throwable t) {
                listener.onError();
            }
        });
    }

    public LiveData<OrderDetail> getOrderDetail(String orderId) {
        MutableLiveData<OrderDetail> data = new MutableLiveData<>();
        String token = prefs.getToken();

        Log.d(TAG, "Gọi API getOrderDetail - orderId: " + orderId);
        Log.d(TAG, "Token: " + token);

        apiService.getOrderDetail("Bearer " + token, orderId)
                .enqueue(new Callback<OrderDetailResponse>() {
                    @Override
                    public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                        Log.e("OrderDetail", new Gson().toJson(response.body()));
                        Log.d(TAG, "HTTP Code: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            OrderDetailResponse body = response.body();

                            if (body.isSuccess() && body.getData() != null) {
                                Log.d(TAG, "Lấy chi tiết đơn hàng thành công!");
                                data.setValue(body.getData());
                            } else {
                                Log.e(TAG, "API trả lỗi: " + body.getMessage());
                                data.setValue(null);
                            }

                        } else {
                            Log.e(TAG, "Response lỗi hoặc null");
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                        Log.e(TAG, "Lỗi gọi API: " + t.getMessage(), t);
                        data.setValue(null);
                    }
                });

        return data;
    }

    //payment
    public LiveData<PaymentResponse> createPayment(String orderId) {
        MutableLiveData<PaymentResponse> data = new MutableLiveData<>();
        String token = prefs.getToken();
        Log.d(TAG, "Gọi API createPayment với orderId: " + orderId);

        apiService.createPayment("Bearer "+ token, orderId).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                Log.d(TAG, "HTTP Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Tạo QR thành công: " + response.body().getData().getQrUrl());
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Lỗi API createPayment: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API createPayment: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<PaymentStatusResponse> getPaymentStatus(String orderId) {
        MutableLiveData<PaymentStatusResponse> result = new MutableLiveData<>();
        String token = prefs.getToken();

        apiService.getPaymentStatus("Bearer " + token, orderId)
                .enqueue(new Callback<PaymentStatusResponse>() {
                    @Override
                    public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                        Log.e("Payment", "GọiAPI: Status Payment ");
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(response.body());
                        } else {
                            result.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                        result.setValue(null);
                    }
                });

        return result;
    }

}
