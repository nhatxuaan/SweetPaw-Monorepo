package com.example.sweetpawapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sweetpawapp.data.model.product.FavoriteResponse;
import com.example.sweetpawapp.data.model.product.FilterProductRequest;
import com.example.sweetpawapp.data.model.product.FilterProductResponse;
import com.example.sweetpawapp.data.model.product.GetProductDetailResponse;
import com.example.sweetpawapp.data.model.product.ProductDetail;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteRequest;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteResponse;
import com.example.sweetpawapp.data.repository.ProductRepository;

public class ProductViewModel extends ViewModel {
    private final ProductRepository productRepository;

    public ProductViewModel() {
        productRepository = new ProductRepository();
    }

    public LiveData<FilterProductResponse> filterProducts(FilterProductRequest request) {
        return productRepository.filterProducts(request);
    }
    public LiveData<GetProductDetailResponse> getProductDetail(String id) {
        return productRepository.getProductDetail(id);
    }
    public LiveData<FavoriteResponse> getFavorite() {
        return productRepository.getFavorite();
    }
    // Trả về LiveData trực tiếp để observe
    public LiveData<ToggleFavoriteResponse> toggleFavorite(String productId) {
        MutableLiveData<ToggleFavoriteResponse> liveData = new MutableLiveData<>();
        ToggleFavoriteRequest req = new ToggleFavoriteRequest(productId);
        productRepository.toggleFavorite(liveData, req);
        return liveData;
    }

}
