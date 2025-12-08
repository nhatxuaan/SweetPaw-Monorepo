package com.example.sweetpawapp.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.sweetpawapp.R;
import com.example.sweetpawapp.ui.viewmodel.PaymentViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PaymentActivity extends AppCompatActivity {
    private Runnable autoCloseRunnable;
    private ImageView id_qr, imgBack;
    private TextView id_sotien;
    private LinearLayout btnSaveImage;
    private String orderId;
    private String tienTT;
    private PaymentViewModel paymentViewModel;
    private Handler handler = new Handler();
    private Runnable checkPaymentRunnable;
    private Boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thanh_toan_activity);
        orderId = getIntent().getStringExtra("orderId");
        tienTT = getIntent().getStringExtra("tienTT");

        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        initUI();
        setupEvents();
        callCreatePaymentAPI();

        checkPaymentLoop();

        // Auto close sau 3 phút
//        handler.postDelayed(() -> {
//            if(isSuccess) return;
//            Toast.makeText(PaymentActivity.this, "Hết thời gian thanh toán!", Toast.LENGTH_SHORT).show();
//            handler.removeCallbacks(checkPaymentRunnable); // ngừng loop check
//            finish();
//        }, 180000);
        autoCloseRunnable = () -> {
            if (isSuccess) return;
            Toast.makeText(PaymentActivity.this, "Hết thời gian thanh toán!", Toast.LENGTH_SHORT).show();
            handler.removeCallbacks(checkPaymentRunnable);
            finish();
        };

        handler.postDelayed(autoCloseRunnable, 180000);


    }
    private void initUI(){
        id_qr = findViewById(R.id.id_qr);
        imgBack = findViewById(R.id.imgBack);
        id_sotien = findViewById(R.id.id_sotien);
        btnSaveImage = findViewById(R.id.btnSaveImage);

    }

    private void setupEvents(){
        imgBack.setOnClickListener(v -> finish());
        btnSaveImage.setOnClickListener(v -> saveImageToGallery());

    }
    private void callCreatePaymentAPI(){
        id_sotien.setText(tienTT);
        if (orderId == null) {
            Log.e("Payment", "orderId NULL");
            return;
        }
        Log.e("Payment", orderId);

        paymentViewModel.createPayment(orderId);

        paymentViewModel.getPaymentLiveData().observe(this, response -> {
            if (response == null || response.getData() == null) {
                Log.e("Payment", "Lỗi tạo QR hoặc response null");
                return;
            }

            String qrUrl = response.getData().getQrUrl();
            Log.d("Payment", "QR URL: " + qrUrl);

            // Load QR vào ImageView bằng Glide
            Glide.with(this).load(qrUrl).into(id_qr);
        });
    }


    private void checkPaymentLoop() {

        checkPaymentRunnable = new Runnable() {
            @Override
            public void run() {
                paymentViewModel.getPaymentStatus(orderId).observe(PaymentActivity.this, response -> {
                    if (response != null) {
                        String status = response.getData().getPaymentStatus();

                        Log.d("PAYMENT", "Trạng thái: " + status);

                        if ("SUCCESS".equals(status)) {
                            isSuccess = true;
                            Toast.makeText(PaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                            handler.removeCallbacks(checkPaymentRunnable);

                            // TRẢ KẾT QUẢ CHO ACTIVITY TRƯỚC
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("paymentSuccess", true);
                            setResult(RESULT_OK, resultIntent);

                            finish();
                        }
                    }
                });

                handler.postDelayed(this, 3000); // check lại sau 3 giây
            }
        };

        handler.post(checkPaymentRunnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkPaymentRunnable);
        handler.removeCallbacks(autoCloseRunnable);
    }

    private void saveImageToGallery() {
        id_qr.setDrawingCacheEnabled(true);
        id_qr.buildDrawingCache();
        Bitmap bitmap = id_qr.getDrawingCache();

        if (bitmap == null) {
            Toast.makeText(this, "Không thể lưu ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "QR_" + System.currentTimeMillis() + ".png";

        OutputStream fos;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Android 10+
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/SweetPaw");

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = getContentResolver().openOutputStream(uri);
            } else {
                // Android 9 trở xuống
                String imagesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ).toString() + "/SweetPaw";

                File dir = new File(imagesDir);
                if (!dir.exists()) dir.mkdirs();

                File imageFile = new File(dir, fileName);
                fos = new FileOutputStream(imageFile);
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (fos != null) fos.close();

            Toast.makeText(this, "Đã lưu vào thư viện!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
        }

        id_qr.setDrawingCacheEnabled(false);
    }



}
