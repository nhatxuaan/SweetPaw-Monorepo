package com.example.sweetpawapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweetpawapp.data.model.order.OrderDetail;
import com.example.sweetpawapp.data.model.order.OrderRequest;
import com.example.sweetpawapp.data.model.order.OrderResponse;
import com.example.sweetpawapp.data.model.order.Orders;
import com.example.sweetpawapp.data.model.order.PreviewOrderRequest;
import com.example.sweetpawapp.data.model.order.PreviewOrderResponse;
import com.example.sweetpawapp.data.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private MutableLiveData<PreviewOrderResponse> previewOrderResponse;
    private final MutableLiveData<OrderResponse> orderResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Orders>> userOrdersLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderDetail> orderDetailLiveData = new MutableLiveData<>();

    public OrderViewModel() {
        orderRepository = new OrderRepository();
        previewOrderResponse = new MutableLiveData<>();

    }

    // Hàm gọi API preview order
    public void previewOrder(PreviewOrderRequest request) {
        LiveData<PreviewOrderResponse> response = orderRepository.preview(request);
        response.observeForever(previewOrderResponse::setValue);
    }

    //  Cho phép Activity/Fragment quan sát dữ liệu trả về
    public LiveData<PreviewOrderResponse> getPreviewOrderResponse() {
        return previewOrderResponse;
    }

    public void orderRepository(OrderRequest request) {
        LiveData<OrderResponse> responseLiveData = orderRepository.order(request);
        responseLiveData.observeForever(orderResponseLiveData::setValue);
    }

    // Cho phép Activity/Fragment quan sát dữ liệu trả về
    public LiveData<OrderResponse> getOrderResponse() {
        return orderResponseLiveData;
    }


    public LiveData<List<Orders>> getUserOrders() {
        return userOrdersLiveData;
    }

    public void fetchUserOrders() {
        orderRepository.getUserOrders(new OrderRepository.OnOrdersFetchedListener() {
            @Override
            public void onFetched(List<Orders> orders) {
                userOrdersLiveData.postValue(orders);
            }

            @Override
            public void onError() {
                userOrdersLiveData.postValue(new ArrayList<>());
            }
        });
    }


    public LiveData<OrderDetail> getOrderDetailLiveData() {
        return orderDetailLiveData;
    }

    public void fetchOrderDetail(String orderId) {
        orderRepository.getOrderDetail(orderId).observeForever(orderDetailLiveData::setValue);
    }



}
