package com.example.sweetpawapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweetpawapp.data.model.order.PaymentResponse;
import com.example.sweetpawapp.data.model.order.PaymentStatusResponse;
import com.example.sweetpawapp.data.repository.OrderRepository;

public class PaymentViewModel extends ViewModel {
    private OrderRepository repository = new OrderRepository();
    private MutableLiveData<PaymentResponse> paymentLiveData = new MutableLiveData<>();

    public MutableLiveData<PaymentResponse> getPaymentLiveData() {
        return paymentLiveData;
    }

    public void createPayment (String orderId) {
        repository.createPayment(orderId).observeForever(paymentLiveData::setValue);
    }
    public LiveData<PaymentStatusResponse> getPaymentStatus(String orderId) {
        return repository.getPaymentStatus(orderId);
    }

}
