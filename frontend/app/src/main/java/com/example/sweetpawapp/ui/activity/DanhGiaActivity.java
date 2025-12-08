    package com.example.sweetpawapp.ui.activity;
    
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.lifecycle.Observer;
    import androidx.lifecycle.ViewModelProvider;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    
    import com.example.sweetpawapp.R;
    import com.example.sweetpawapp.data.model.order.OrderDetailItem;
    import com.example.sweetpawapp.data.model.review.ReviewOrder;
    import com.example.sweetpawapp.data.repository.ReviewRepository;
    import com.example.sweetpawapp.ui.adapter.DanhGiaAdapter;
    import com.example.sweetpawapp.ui.viewmodel.ReviewViewModel;

    import java.util.ArrayList;
    import java.util.List;

    public class DanhGiaActivity extends AppCompatActivity {
        private RecyclerView rvProduct;
        private TextView tvMadonhang, tvThoigian;
        private ImageView imgBack;

        private DanhGiaAdapter adapter;
        private ReviewRepository reviewRepository;

        private String orderId, maDon, tgDat;
        ArrayList<OrderDetailItem> productList;
        ReviewViewModel viewModel;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.danhgia_activity);

            reviewRepository = new ReviewRepository();

            // orderId lấy từ intent
            orderId = getIntent().getStringExtra("orderId");
            maDon = getIntent().getStringExtra("tvMaDon");
            tgDat = getIntent().getStringExtra("tvTgDat");
            productList = (ArrayList<OrderDetailItem>) getIntent().getSerializableExtra("productList");
            viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);


            initUI();
            loadReviews();
            observeReviewResponse();
            imgBack.setOnClickListener(v -> finish());
        }
        private void initUI(){
            rvProduct = findViewById(R.id.rvProduct);
            tvMadonhang = findViewById(R.id.tvMadonhang);
            tvThoigian = findViewById(R.id.tvThoigian);
            imgBack = findViewById(R.id.imgBack);
        }

        private void setupRecyclerView() {
            adapter = new DanhGiaAdapter(this, null, viewModel);
            rvProduct.setLayoutManager(new LinearLayoutManager(this));
            rvProduct.setAdapter(adapter);
        }

        private void observeReviewResponse(){
            viewModel.getReviewLiveData().observe(this, response -> {
                if (response != null) {
                    ReviewOrder review = response.getData();
                    // cập nhật vào adapter để gọi api cập nhật đánh giá
                    adapter.updateRatingIdForCreatedItem(review);
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Thêm đánh giá thất bại!", Toast.LENGTH_SHORT).show();
                }
            });

            viewModel.getUpdateReviewLiveData().observe(this, response -> {
                if (response != null) {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cập nhật đánh giá thất bại!", Toast.LENGTH_SHORT).show();
                }
            });

            viewModel.getDeleteReviewLiveData().observe(this, response -> {
                if (response != null) {
                    Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Xóa đánh giá thất bại!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        private void loadReviews() {
            tvMadonhang.setText(maDon);
            tvThoigian.setText(tgDat);

            if (productList != null) {
                List<ReviewOrder> reviewsToShow = new ArrayList<>();

                for (OrderDetailItem item : productList) {
                    ReviewOrder review = new ReviewOrder();
                    review.setProductName(item.getName());
                    review.setProductId(item.getProductId());
                    review.setOrderId(orderId);  // gán orderId cho dễ dùng

                    reviewsToShow.add(review);
                }

                // Setup adapter 1 lần duy nhất
                if (adapter == null) {
                    adapter = new DanhGiaAdapter(this, reviewsToShow, viewModel);
                    rvProduct.setLayoutManager(new LinearLayoutManager(this));
                    rvProduct.setAdapter(adapter);
                } else {
                    adapter.setReviewList(reviewsToShow);
                }

                // Nếu muốn lấy review cũ từ server, cập nhật từng ReviewOrder:
                for (ReviewOrder review : reviewsToShow) {
                    reviewRepository.getReviewsByProduct(review.getProductId()).observe(this, reviewList -> {
                        if (reviewList != null) {
                            for (ReviewOrder r : reviewList) {
                                if (r.getOrderId().equals(orderId)) {
                                    review.setStars(r.getStars());
                                    review.setComment(r.getComment());
                                    review.setRatingId(r.getRatingId());
                                    adapter.notifyDataSetChanged(); // cập nhật từng item
                                }
                            }
                        }
                    });
                }
            }
        }

    }
