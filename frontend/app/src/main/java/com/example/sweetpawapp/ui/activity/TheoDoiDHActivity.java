package com.example.sweetpawapp.ui.activity;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.order.OrderDetail;
import com.example.sweetpawapp.data.model.order.OrderDetailItem;
import com.example.sweetpawapp.data.model.order.OrderItem;
import com.example.sweetpawapp.ui.adapter.TheoDoiAdapter;
import com.example.sweetpawapp.ui.viewmodel.OrderViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TheoDoiDHActivity extends AppCompatActivity {
    private TextView tvMaDon, tvTgDat, tvDanhGia, tvNguoiNhan, tvSDT, tvDiaChi, tvHTThanhToan, tvTrangThaiDH, tvGhiChu, tvTienHang, tvPhiGH, tvGiamGia, tvTienTT, tvStatusPayment, tvThanhToan;
    private ImageView tvXemTT1, tvXemTT2, iconDangXL, iconGH, iconGiaoTC;
    private RecyclerView rvProduct;
    private LinearLayout layoutTrangThai;
    List<OrderDetailItem> items;

    private OrderViewModel orderViewModel;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.td_donhang_activity);

        initUI();
        setupEvents();

        // Lấy orderId từ Intent
        orderId = getIntent().getStringExtra("orderId");


        if (orderId != null) {
            setupViewModel();
            fetchOrderDetail();
        }


    }
    private void initUI(){
        tvMaDon = findViewById(R.id.tvMaDon);
        tvTgDat = findViewById(R.id.tvTgDat);
        tvDanhGia = findViewById(R.id.tvDanhGia);
        tvNguoiNhan = findViewById(R.id.tvNguoiNhan);
        tvSDT = findViewById(R.id.tvSDT);
        tvDiaChi = findViewById(R.id.tvDiaChi);
        tvHTThanhToan = findViewById(R.id.tvHTThanhToan);
        tvTrangThaiDH = findViewById(R.id.tvTrangThaiDH);
        tvGhiChu = findViewById(R.id.tvGhiChu);
        tvTienHang = findViewById(R.id.tvTienHang);
        tvPhiGH = findViewById(R.id.tvPhiGH);
        tvGiamGia = findViewById(R.id.tvGiamGia);
        tvTienTT = findViewById(R.id.tvTienTT);
        iconDangXL =  findViewById(R.id.iconDangXL);
        iconGH = findViewById(R.id.iconGH);
        tvXemTT1 = findViewById(R.id.tvXemTT1);
        tvXemTT2 = findViewById(R.id.tvXemTT2);
        iconGiaoTC = findViewById(R.id.iconGiaoTC);
        rvProduct = findViewById(R.id.rvProduct);
        layoutTrangThai = findViewById(R.id.layoutTrangThai);
        iconDangXL.setSelected(true);
        tvDanhGia = findViewById(R.id.tvDanhGia);
        tvStatusPayment = findViewById(R.id.tvStatusPayment);
        tvThanhToan = findViewById(R.id.tvThanhToan);

    }
    private void setupViewModel() {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Observe dữ liệu chi tiết đơn hàng
        orderViewModel.getOrderDetailLiveData().observe(this, orderDetail -> {
            if (orderDetail != null) {
                bindDataToUI(orderDetail);
            }
        });
    }

    private void fetchOrderDetail() {
        if (orderId != null) {
            orderViewModel.fetchOrderDetail(orderId);
        }
    }


    private void bindDataToUI(OrderDetail orderDetail) {
        if (orderDetail == null) {
            Log.d("Order", "OrderDetail null");
            return;
        }

        items = orderDetail.getItems();
        // Set dữ liệu chi tiết
        tvMaDon.setText(orderDetail.getOrderCode());

        //Chuyển đổi thời gian
        Log.d("Giaohang", tvTgDat.getText().toString());
        try {
            String utcTime = orderDetail.getCreatedAt();

            // Định dạng của chuỗi từ server (ISO-8601)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Giờ gốc là UTC

            java.util.Date date = sdf.parse(utcTime);

            // Đổi sang giờ Việt Nam (UTC+7)
            java.text.SimpleDateFormat vnFormat = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            vnFormat.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

            String formattedDate = vnFormat.format(date);

            tvTgDat.setText(formattedDate);
            Log.d("TgDat", "Giờ VN: " + formattedDate);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TgDat", "Parse lỗi: " + e.getMessage());
            tvTgDat.setText(orderDetail.getCreatedAt());
        }


        tvNguoiNhan.setText(orderDetail.getToName());
        tvSDT.setText(orderDetail.getToPhone());
        tvDiaChi.setText(orderDetail.getToAddress());
        tvHTThanhToan.setText(orderDetail.getPaymentMethod());
        tvTrangThaiDH.setText(orderDetail.getDisplayStatus());

        Log.d("Order", orderDetail.getPayment_status()+"");
        String statusPay = orderDetail.getPayment_status();
        if(statusPay.equals("SUCCESS")){
            statusPay = "Thanh toán thành công";
        }
        else{
            statusPay = "Chưa thanh toán";
            if(orderDetail.getPaymentMethod().equals("Chuyển khoản")){
                tvThanhToan.setVisibility(View.VISIBLE);
            }
        }
        tvStatusPayment.setText(statusPay);

        //Cập nhật icon trạng thái
        String status = tvTrangThaiDH.getText().toString().trim();
        Log.d("Giaohang", status);
        if(status.equals("Đã giao thành công")){
            iconGiaoTC.setSelected(true);
            iconGH.setSelected(true);
            tvDanhGia.setVisibility(View.VISIBLE);
        }
        else if(status.equals("Đang giao hàng")){
            iconGH.setSelected(true);
        }

        tvGhiChu.setText(orderDetail.getNote());

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        tvTienHang.setText(decimalFormat.format(orderDetail.getSubtotal()) + " đ");
        tvPhiGH.setText(decimalFormat.format(orderDetail.getShippingFee()) + " đ");
        tvGiamGia.setText(decimalFormat.format(orderDetail.getDiscount()) + " đ");
        tvTienTT.setText(decimalFormat.format(orderDetail.getTotalPrice()) + " đ");

        if (orderDetail.getItems() != null) {
            TheoDoiAdapter<OrderDetailItem> adapterOrder = new TheoDoiAdapter<>(this, orderDetail.getItems(),
                    (holder, item) -> TheoDoiAdapter.bindWithoutImage(holder, item.getName(), item.getQuantity(), item.getPrice(),item.getImage()));
            rvProduct.setLayoutManager(new LinearLayoutManager(this));
            rvProduct.setAdapter(adapterOrder);
        }
    }


    private void setupEvents(){
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
        //Xem trạng thái
        tvXemTT1.setOnClickListener(v ->{
            layoutTrangThai.setVisibility(View.VISIBLE);
            tvXemTT1.setVisibility(View.GONE);
            tvXemTT2.setVisibility(View.VISIBLE);
        });
        tvXemTT2.setOnClickListener(v ->{
            layoutTrangThai.setVisibility(View.GONE);
            tvXemTT1.setVisibility(View.VISIBLE);
            tvXemTT2.setVisibility(View.GONE);
        });

        tvDanhGia.setOnClickListener(v ->{
            // Truyền danh sách sản phẩm
            if (items != null && !items.isEmpty()) {
                Intent intent = new Intent(this, DanhGiaActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("tvMaDon", tvMaDon.getText());
                intent.putExtra("tvTgDat", tvTgDat.getText());
                intent.putExtra("productList", new ArrayList<>(items));  // truyền trực tiếp
                startActivity(intent);
            }
        });

        tvThanhToan.setOnClickListener(v->{
            Log.d("Payment", "order" + orderId);
            Intent intent = new Intent(TheoDoiDHActivity.this, PaymentActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("tienTT", tvTienTT.getText());
          //  startActivity(intent);
            startActivityForResult(intent, 123);  // CHỜ KẾT QUẢ

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK) {
            boolean paySuccess = data.getBooleanExtra("paymentSuccess", false);

            if (paySuccess) {
                // Gọi lại API để refresh giao diện
                fetchOrderDetail();
                tvThanhToan.setVisibility(View.INVISIBLE);
            }
        }
    }


}
