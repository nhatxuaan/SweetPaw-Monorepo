package com.example.sweetpawapp.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.product.Product;
import com.example.sweetpawapp.data.model.product.FilterProductRequest;
import com.example.sweetpawapp.data.model.product.SearchProductResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;
import com.example.sweetpawapp.ui.adapter.FeatureAdapter;
import com.example.sweetpawapp.ui.fragment.HomeFragment;
import com.example.sweetpawapp.ui.viewmodel.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "PRODUCT";
    private ImageView imgFilter, chatBtn, notificationBtn;
    private TextView tvthongBao, tvClose;
    private LinearLayout layoutKQ, layoutFilter;
    private RecyclerView recycler_kq;
    private EditText edtTimKiem;
    private Button btnApDung;
    private FeatureAdapter adapter;

    private ProductViewModel productViewModel;
    private ActivityResultLauncher<Intent> detailLauncher;



    private RadioButton selectedPrice = null;
    private RadioButton selectedRating = null;

    private boolean priceSelected = false;
    private boolean ratingSelected = false;

    private RadioGroup rgGia1, rgGia2, rgRating1, rgRating2;
    private RadioButton rbDuoi100, rb100_200, rb200_300, rb300_400, rbTren400;
    private RadioButton rbStar5, rbStar4, rbStar3, rbStar2, rbStar1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        initUI();
        setupEvents();

        boolean showFilter = getIntent().getBooleanExtra("showFilter", false);

        if (showFilter) {
            // Khi mở từ HomeFragment (ấn imgFilter)
            layoutFilter.setVisibility(View.VISIBLE);
        } else {
            // Khi mở bình thường (ấn vào ô tìm kiếm)
            layoutFilter.setVisibility(View.GONE);
            // Tự động focus và bật bàn phím
            edtTimKiem.requestFocus();
            edtTimKiem.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(edtTimKiem, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 200);
        }
        // Gọi hàm xử lý search khi nhấn Enter
        edtTimKiem.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = edtTimKiem.getText().toString().trim();
            if (!keyword.isEmpty()) {
                hideKeyboard();
                searchProducts(keyword);
            }
            return true;
        });
        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String productId = data.getStringExtra("productId");
                            boolean isFavorite = data.getBooleanExtra("isFavorite", false);

                            updateFavoriteInAdapter(productId, isFavorite);
                        }
                    }
                }
        );


    }
    private void initUI(){
        chatBtn = findViewById(R.id.chat);
        notificationBtn = findViewById(R.id.notification);
        imgFilter = findViewById(R.id.imgFilter);
        tvthongBao = findViewById(R.id.tvthongBao);
        layoutKQ = findViewById(R.id.layoutKQ);
        recycler_kq = findViewById(R.id.recycler_kq);
        edtTimKiem = findViewById(R.id.edtTimKiem);
        layoutFilter = findViewById(R.id.layoutFilter);
        tvClose = findViewById(R.id.tvClose);
        btnApDung = findViewById(R.id.btnApDung);
        progressBar = findViewById(R.id.progressBar);

        // Price filter
        rgGia1 = findViewById(R.id.rgGia1);
        rgGia2 = findViewById(R.id.rgGia2);
        rbDuoi100 = findViewById(R.id.rbDuoi100);
        rb100_200 = findViewById(R.id.rb100_200);
        rb200_300 = findViewById(R.id.rb200_300);
        rb300_400 = findViewById(R.id.rb300_400);
        rbTren400 = findViewById(R.id.rbTren400);

        // Rating filter
        rgRating1 = findViewById(R.id.rgRating1);
        rgRating2 = findViewById(R.id.rgRating2);
        rbStar5 = findViewById(R.id.rbStar5);
        rbStar4 = findViewById(R.id.rbStar4);
        rbStar3 = findViewById(R.id.rbStar3);
        rbStar2 = findViewById(R.id.rbStar2);
        rbStar1 = findViewById(R.id.rbStar1);

        // Reset tag ban đầu cho tất cả radio
        rbDuoi100.setTag(false);
        rb100_200.setTag(false);
        rb200_300.setTag(false);
        rb300_400.setTag(false);
        rbTren400.setTag(false);

        rbStar5.setTag(false);
        rbStar4.setTag(false);
        rbStar3.setTag(false);
        rbStar2.setTag(false);
        rbStar1.setTag(false);

        setupToggleRadio(rbDuoi100, "price");
        setupToggleRadio(rb100_200, "price");
        setupToggleRadio(rb200_300, "price");
        setupToggleRadio(rb300_400, "price");
        setupToggleRadio(rbTren400, "price");

        setupToggleRadio(rbStar5, "rating");
        setupToggleRadio(rbStar4, "rating");
        setupToggleRadio(rbStar3, "rating");
        setupToggleRadio(rbStar2, "rating");
        setupToggleRadio(rbStar1, "rating");

    }

    private void setupEvents(){
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
        tvClose.setOnClickListener(v ->{
            layoutFilter.setVisibility(View.GONE);
        });
        imgFilter.setOnClickListener(v ->{
            layoutFilter.setVisibility(View.VISIBLE);
        });
        //Chuyển sang nhắn tin
        chatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, MessageActivity.class);
            startActivity(intent);
        });

        //Chuyển sang thông báo
        notificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
        btnApDung.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            // Ẩn sau 1 giây
            new android.os.Handler().postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
            }, 1000);

            int minPrice = 0;
            int maxPrice = 0;
            int rating = 0;
            String keyword = edtTimKiem.getText().toString().trim();

            if (rbDuoi100.isChecked()) { minPrice = 0; maxPrice = 100; }
            else if (rb100_200.isChecked()) { minPrice = 100; maxPrice = 200; }
            else if (rb200_300.isChecked()) { minPrice = 200; maxPrice = 300; }
            else if (rb300_400.isChecked()) { minPrice = 300; maxPrice = 400; }
            else if (rbTren400.isChecked()) { minPrice = 400; maxPrice = 999999; }

            if (rbStar5.isChecked()) rating = 5;
            else if (rbStar4.isChecked()) rating = 4;
            else if (rbStar3.isChecked()) rating = 3;
            else if (rbStar2.isChecked()) rating = 2;
            else if (rbStar1.isChecked()) rating = 1;
            Log.d("RatingFilter", rating  +" được chọn");

            List<String> priceRange = new ArrayList<>();
            if (selectedPrice != null) {
                priceRange.add(minPrice + "-" + maxPrice);
            }

            FilterProductRequest request = new FilterProductRequest(
                    priceRange.isEmpty() ? null : priceRange,
                    rating,
                    keyword.isEmpty() ? null : keyword
            );

            productViewModel.filterProducts(request).observe(this, response -> {
                if (response == null || response.getData() == null || response.getData().isEmpty()) {
                    tvthongBao.setVisibility(View.VISIBLE);
                    tvthongBao.setText("Không tìm thấy sản phẩm nào!");
                    layoutKQ.setVisibility(View.GONE);
                } else {
                    tvthongBao.setVisibility(View.GONE);
                    showProducts(response.getData());
                }
            });

            layoutFilter.setVisibility(View.GONE);
        });


    }
    // Hàm gọi API tìm kiếm sản phẩm
    private void searchProducts(String keyword) {
        tvthongBao.setVisibility(View.GONE);
        layoutKQ.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.searchProducts(keyword).enqueue(new Callback<SearchProductResponse>() {
            @Override
            public void onResponse(Call<SearchProductResponse> call, Response<SearchProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SearchProductResponse result = response.body();
                    if (response.body() != null && response.body().getData() != null) {
                        showProducts(result.getData());
                    } else {
                        tvthongBao.setVisibility(View.VISIBLE);
                        tvthongBao.setText("Không tìm thấy sản phẩm nào!");
                    }
                } else {
                    tvthongBao.setVisibility(View.VISIBLE);
                    tvthongBao.setText("Lỗi phản hồi từ server!");
                }
            }

            @Override
            public void onFailure(Call<SearchProductResponse> call, Throwable t) {
                tvthongBao.setVisibility(View.VISIBLE);
                tvthongBao.setText("Không thể kết nối đến server!");
            }
        });
    }

    // Hiển thị danh sách sản phẩm
    private void showProducts(List<Product> products) {
        layoutKQ.setVisibility(View.VISIBLE);
        recycler_kq.setLayoutManager(new GridLayoutManager(this, 2));

        getFavoriteProducts(favorites -> {
            // Đánh dấu sản phẩm yêu thích
            markFavoriteProducts(products, favorites);

            adapter = new FeatureAdapter(products);

            adapter.setOnItemClickListener(product -> {
                Intent intent = new Intent(SearchActivity.this, DetailProductActivity.class);
                intent.putExtra("id", product.getMongoId());
                intent.putExtra("name", product.getName());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("description", product.getDescript());
                intent.putExtra("imageUrl", product.getImageUrl());
                intent.putExtra("quantity", product.getQuantity());
                intent.putExtra("isFavorite", product.isFavorite());
                //startActivity(intent);
                detailLauncher.launch(intent);
            });
            adapter.setOnItemLikeClickListener((product, position) -> {
                productViewModel.toggleFavorite(product.getMongoId())
                        .observe(this, responseFavorite -> {

                            if (responseFavorite != null) {
                                String message = responseFavorite.getMessage();
                                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();

                                // Cập nhật lại trạng thái yêu thích
                                if (responseFavorite.getData() != null
                                        && responseFavorite.getData().getProducts() != null) {

                                    boolean isFavorite = responseFavorite.getData()
                                            .getProducts()
                                            .contains(product.getMongoId());

                                    product.setFavorite(isFavorite);
                                }

                                adapter.notifyItemChanged(position);

                            } else {
                                Toast.makeText(SearchActivity.this,
                                        "Không thể cập nhật sản phẩm yêu thích",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
            });

            recycler_kq.setAdapter(adapter);
        });
    }

    // Ẩn bàn phím sau khi nhấn Enter
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setupToggleRadio(RadioButton radio, String type) {
        radio.setOnClickListener(v -> {

            if (type.equals("price")) {

                if (selectedPrice == radio && priceSelected) {
                    // Nếu ấn lại chính nó → tắt
                    radio.setChecked(false);
                    selectedPrice = null;
                    priceSelected = false;
                } else {
                    // Chọn radio mới
                    if (selectedPrice != null) selectedPrice.setChecked(false);
                    radio.setChecked(true);
                    selectedPrice = radio;
                    priceSelected = true;
                }

            } else if (type.equals("rating")) {

                if (selectedRating == radio && ratingSelected) {
                    // Nếu ấn lại chính nó → tắt
                    radio.setChecked(false);
                    selectedRating = null;
                    ratingSelected = false;
                } else {
                    if (selectedRating != null) selectedRating.setChecked(false);
                    radio.setChecked(true);
                    selectedRating = radio;
                    ratingSelected = true;
                }
            }
        });
    }
    private void markFavoriteProducts(List<Product> productList, List<Product> favoriteList) {
        if (favoriteList == null || productList == null) return;
        for (Product p : productList) {
            for (Product fav : favoriteList) {
                if (p.getMongoId().equals(fav.getMongoId())) {
                    p.setFavorite(true);  //đánh dấu yêu thích
                    break;
                }
            }
        }
    }
    private void getFavoriteProducts(FavoriteCallback callback) {
        productViewModel.getFavorite().observe(this, response -> {
            if (response == null || response.getData() == null) {
                callback.onLoaded(new ArrayList<>()); // trả list trống
                return;
            }

            // Trả danh sách sản phẩm yêu thích về callback
            callback.onLoaded(response.getData());
        });
    }
    public interface FavoriteCallback {
        void onLoaded(List<Product> favorites);
    }

    private void updateFavoriteInAdapter(String productId, boolean isFavorite) {
        if (adapter == null) return;

        List<Product> list = adapter.getProductList();
        if (list == null) return;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMongoId().equals(productId)) {
                list.get(i).setFavorite(isFavorite);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }






}
