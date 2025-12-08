package com.example.sweetpawapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweetpawapp.data.model.recommend.CartRecommendResponse;
import com.example.sweetpawapp.data.model.recommend.RecommendHomeResponse;
import com.example.sweetpawapp.data.repository.RecommendRepository;

import java.util.List;

public class RecommendViewModel extends ViewModel {
    private RecommendRepository repository;
    private MutableLiveData<RecommendHomeResponse> recommendLiveData = new MutableLiveData<>();
    private MutableLiveData<CartRecommendResponse> cartRecommendLiveData = new MutableLiveData<>();

    public RecommendViewModel() {
        repository = new RecommendRepository();
    }

    public LiveData<RecommendHomeResponse> loadRecommend() {
        recommendLiveData = (MutableLiveData<RecommendHomeResponse>) repository.getHomeRecommend();
        return recommendLiveData;
    }

    public LiveData<RecommendHomeResponse> getRecommendData() {
        return recommendLiveData;
    }
    public LiveData<CartRecommendResponse> getCartRecommend(List<String> ids) {
        cartRecommendLiveData = (MutableLiveData<CartRecommendResponse>)
                repository.getRecommendFromCart(ids);
        return cartRecommendLiveData;
    }
    public LiveData<CartRecommendResponse> observeCartRecommend() {
        return cartRecommendLiveData;
    }

}
//public class RecommendViewModel extends ViewModel {
//    private RecommendRepository repository;
//
//    private MutableLiveData<CartRecommendResponse> cartRecommendLiveData = new MutableLiveData<>();
//
//    public RecommendViewModel() {
//        repository = new RecommendRepository();
//    }
//
//    public LiveData<CartRecommendResponse> getCartRecommend(List<String> ids) {
//        cartRecommendLiveData = (MutableLiveData<CartRecommendResponse>)
//                repository.getRecommendFromCart(ids);
//        return cartRecommendLiveData;
//    }
//
//    public LiveData<CartRecommendResponse> observeCartRecommend() {
//        return cartRecommendLiveData;
//    }
//}
