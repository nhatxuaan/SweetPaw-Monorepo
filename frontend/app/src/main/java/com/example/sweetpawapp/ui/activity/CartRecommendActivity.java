package com.example.sweetpawapp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.cart.CartItem;
import com.example.sweetpawapp.data.model.product.Product;
import com.example.sweetpawapp.ui.adapter.FeatureAdapter;
import com.example.sweetpawapp.ui.fragment.HomeFragment;
import com.example.sweetpawapp.ui.viewmodel.ProductViewModel;
import com.example.sweetpawapp.ui.viewmodel.RecommendViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartRecommendActivity extends AppCompatActivity {
    private ArrayList<CartItem> selectedItems;
    private ActivityResultLauncher<Intent> detailLauncher;
    private ImageView imgBack;
    private RecyclerView recyclerProduct;
    private ProgressBar progressBar;
    private RecommendViewModel viewModel;
    private ProductViewModel productViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_recommend_activity);
        // Lấy dữ liệu từ Intent
        selectedItems = getIntent().getParcelableArrayListExtra("selectedItems");
        viewModel = new ViewModelProvider(this).get(RecommendViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        initUI();
        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String productId = data.getStringExtra("productId");
                            boolean isFavorite = data.getBooleanExtra("isFavorite", false);

                            // Cập nhật lại danh sách trong adapter
                            updateFavoriteInAllAdapters(productId, isFavorite);
                        }
                    }
                }
        );
        loadCartRecommend();
        imgBack.setOnClickListener(v -> finish());

    }
    private void initUI(){
        imgBack = findViewById(R.id.imgBack);
        recyclerProduct = findViewById(R.id.recyclerProduct);
        recyclerProduct.setLayoutManager(new GridLayoutManager(this, 2));
        progressBar = findViewById(R.id.progressBar);


    }

    private void loadCartRecommend(){
        // Khi bắt đầu gọi API
        progressBar.setVisibility(View.VISIBLE);
        // Ẩn sau 2 giây
        new android.os.Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
        }, 2000);

        if (selectedItems == null || selectedItems.isEmpty()) {
            Log.e("CartRecommend", "selectedItems NULL hoặc EMPTY!");
            return;
        }

        // Lấy list productIds
        ArrayList<String> ids = new ArrayList<>();
        for (CartItem item : selectedItems) {
            ids.add(item.getProductId());
        }

        Log.d("CartRecommend", "Gửi productIds lên API: " + ids);

        // Gọi API
        viewModel.getCartRecommend(ids);

        // Quan sát kết quả
        viewModel.observeCartRecommend().observe(this, response -> {

            Log.d("CartRecommend", "API response nhận về: " + response);

            if (response != null && response.isSuccess()) {

                Log.d("CartRecommend", "Số sản phẩm gợi ý: " + response.getProducts().size());

                // Đánh dấu sản phẩm yêu thích
                getFavoriteProducts(favorites -> {

                    Log.d("CartRecommend", "Danh sách favorite từ server: " + favorites.size());

                    markFavoriteProducts(response.getProducts(), favorites);

                    FeatureAdapter adapter = new FeatureAdapter(response.getProducts());

                    // click vào item -> mở detail
                    adapter.setOnItemClickListener(product -> {
                        Log.d("CartRecommend", "Click item: " + product.getName());

                        Intent intent = new Intent(this, DetailProductActivity.class);
                        intent.putExtra("id", product.getMongoId());
                        intent.putExtra("name", product.getName());
                        intent.putExtra("price", product.getPrice());
                        intent.putExtra("description", product.getDescript());
                        intent.putExtra("imageUrl", product.getImageUrl());
                        intent.putExtra("quantity", product.getQuantity());
                        intent.putExtra("isFavorite", product.isFavorite());

                        detailLauncher.launch(intent);
                    });

                    // click like
                    adapter.setOnItemLikeClickListener((product, position) -> {

                        Log.d("CartRecommend", "Click LIKE: " + product.getMongoId());

                        productViewModel.toggleFavorite(product.getMongoId())
                                .observe(this, favResponse -> {

                                    Log.d("CartRecommend", "Kết quả toggleFavorite: " + favResponse);

                                    if (favResponse != null && favResponse.getData() != null) {
                                        boolean isFavorite = favResponse.getData()
                                                .getProducts()
                                                .contains(product.getMongoId());

                                        Log.d("CartRecommend", "isFavorite mới = " + isFavorite);

                                        product.setFavorite(isFavorite);
                                        adapter.notifyItemChanged(position);

                                        String message = favResponse.getMessage();
                                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });

                    recyclerProduct.setAdapter(adapter);
                });

            } else {
                Log.e("CartRecommend", "Không lấy được gợi ý hoặc response null!");
            }
        });
    }

    private void getFavoriteProducts(HomeFragment.FavoriteCallback callback) {
        productViewModel.getFavorite().observe(this, response -> {

            Log.d("CartRecommend", "Favorite API trả về: " + response);

            if (response == null || response.getData() == null) {
                Log.e("CartRecommend", "Favorite LIST = null, trả về list rỗng");
                callback.onLoaded(new ArrayList<>());
                return;
            }

            Log.d("CartRecommend", "Favorite LIST size = " + response.getData().size());

            callback.onLoaded(response.getData());
        });
    }

    private void markFavoriteProducts(List<Product> productList, List<Product> favoriteList) {
        Log.d("CartRecommend", "Đánh dấu favorite: productList=" + productList.size()
                + " favoriteList=" + favoriteList.size());

        if (favoriteList == null || productList == null) return;

        for (Product p : productList) {
            for (Product fav : favoriteList) {
                if (p.getMongoId().equals(fav.getMongoId())) {
                    Log.d("CartRecommend", "Đánh dấu yêu thích cho: " + p.getName());
                    p.setFavorite(true);
                    break;
                }
            }
        }
    }
    private void updateFavoriteInAllAdapters(String productId, boolean isFavorite) {
        RecyclerView.Adapter adapter = recyclerProduct.getAdapter();
        if (!(adapter instanceof FeatureAdapter)) return;

        FeatureAdapter featureAdapter = (FeatureAdapter) adapter;
        List<Product> list = featureAdapter.getProductList();

        if (list == null) return;

        for (int i = 0; i < list.size(); i++) {
            Product p = list.get(i);
            if (p.getMongoId().equals(productId)) {
                p.setFavorite(isFavorite);
                featureAdapter.notifyItemChanged(i);
                break;
            }
        }
    }


}
