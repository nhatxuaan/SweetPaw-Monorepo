package com.example.sweetpawapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.review.Review;
import com.example.sweetpawapp.data.model.product.ProductDetail;
import com.example.sweetpawapp.data.model.product.RatingItem;
import com.example.sweetpawapp.data.repository.CartRepository;
import com.example.sweetpawapp.ui.adapter.ReviewAdapter;
import com.example.sweetpawapp.ui.viewmodel.CartViewModel;
import com.example.sweetpawapp.ui.viewmodel.ProductViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailProductActivity extends AppCompatActivity {
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private String id;
    private int stock;
    private boolean isFavorite;

    private TextView tvName,tvPrice,tvDesc, tvSL, tvSoDG, tvSoLuongTon;
    private ImageView imgProduct, imgTruSL, imgCongSL, imgAddCart, imgBack, imgLike;
    private Button btnMuaNgay;
    private RecyclerView recyclerView;
    private CartViewModel cartViewModel;
    private ProductViewModel productViewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_product_activity);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        //nhận dữ liệu
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        price = intent.getDoubleExtra("price", 0);
        description = intent.getStringExtra("description");
        imageUrl = intent.getStringExtra("imageUrl");
        id = intent.getStringExtra("id");
        stock = intent.getIntExtra("quantity", 0);
        isFavorite = intent.getBooleanExtra("isFavorite", false);

        initUI();
        setupEvent();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("productId", id);
                resultIntent.putExtra("isFavorite", isFavorite);
                setResult(RESULT_OK, resultIntent);

                finish(); // thay cho super.onBackPressed()
            }
        });

    }
    private void initUI(){
        // Liên kết View trong layout
        tvName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvProductPrice);
        tvSoLuongTon = findViewById(R.id.tvSoLuong);
        tvDesc = findViewById(R.id.tvProductDescription);
        imgProduct = findViewById(R.id.imgProductDetail);
        imgTruSL = findViewById(R.id.imgTruSL);
        imgCongSL = findViewById(R.id.imgCongSL);
        imgAddCart = findViewById(R.id.imgAddCart);
        btnMuaNgay = findViewById(R.id.btnMuaNgay);
        tvSL = findViewById(R.id.tvsoLuong);
        tvSoDG = findViewById(R.id.soDG);
        imgBack = findViewById(R.id.imgBack);
        imgLike = findViewById(R.id.imgLike);
        recyclerView = findViewById(R.id.rvReviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Hiển thị dữ liệu
        tvName.setText(name);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        tvPrice.setText(decimalFormat.format(price));
        tvDesc.setText(description);
        tvSoLuongTon.setText(stock +"");

        if (isFavorite) {
            imgLike.setImageResource(R.drawable.liked_heart); //icon yêu thích
        } else {
            imgLike.setImageResource(R.drawable.heart_white); //icon chưa yêu thích
        }


        // Load ảnh từ URL bằng Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.anh) // ảnh hiển thị trong lúc chờ load
                .error(R.drawable.anh)   // ảnh hiển thị nếu load lỗi
                .into(imgProduct);

        productViewModel.getProductDetail(id).observe(this, res -> {
            if (res == null || res.getData() == null) {
                Log.d("ChiTiet", id  +"Không tải được dữ liệu");
                return;
            }

            ProductDetail p = res.getData();
            List<Review> reviews = new ArrayList<>();

            for (RatingItem r : p.getRatings()) {
                reviews.add(new Review(
                        r.getUser().getNameUser(),
                        r.getStars(),
                        r.getComment(),
                        r.getCreatedAt()
                ));
            }

            tvSoDG.setText(reviews.size() + "");
            ReviewAdapter adapter = new ReviewAdapter(this, reviews);
            recyclerView.setAdapter(adapter);
        });

    }
    private void setupEvent(){
        // Gán sự kiện cho nút cộng/trừ
        imgCongSL.setOnClickListener(v -> {
            int current = Integer.parseInt(tvSL.getText().toString());
            tvSL.setText(String.valueOf(current + 1));
        });

        imgTruSL.setOnClickListener(v -> {
            int current = Integer.parseInt(tvSL.getText().toString());
            if (current > 1) {
                tvSL.setText(String.valueOf(current - 1));
            }
        });
        // Nút quay lại
        imgBack.setOnClickListener(v -> finish());
        // Xử lý thêm vào giỏ hàng
        imgAddCart.setOnClickListener(v -> addToCart());
        // Nút mua ngay
//        btnMuaNgay.setOnClickListener(v -> {
//            Toast.makeText(this, "Chức năng đang được phát triển!", Toast.LENGTH_SHORT).show();
//        });


        imgLike.setOnClickListener(v->{
            productViewModel.toggleFavorite(id)
                    .observe(this, responseFavorite -> {
                        if (responseFavorite != null) {
                            // Hiện message từ server
                            Toast.makeText(this, responseFavorite.getMessage(), Toast.LENGTH_SHORT).show();

                            // Đảo trạng thái yêu thích local
                            isFavorite = !isFavorite;

                            // Cập nhật icon
                            if (isFavorite) {
                                imgLike.setImageResource(R.drawable.liked_heart);
                            } else {
                                imgLike.setImageResource(R.drawable.heart_white);
                            }
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("productId", id);
                            resultIntent.putExtra("isFavorite", isFavorite);
                            setResult(RESULT_OK, resultIntent);

                        } else {
                            Toast.makeText(this, "Không thể cập nhật sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }
    private void addToCart() {
        if(stock <= 0){
            Toast.makeText(this, "Sản phẩm hiện đã hết. Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity = Integer.parseInt(tvSL.getText().toString());
        if(quantity > stock){
            Toast.makeText(this, "Không đủ số lượng có sẵn. Vui lòng chọn lại!", Toast.LENGTH_SHORT).show();
            return;
        }
        CartRepository repo = new CartRepository();

        repo.addToCart(id, quantity).observe(this, success -> {
            Log.d("Đã chọn", quantity +"");
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không thể thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

