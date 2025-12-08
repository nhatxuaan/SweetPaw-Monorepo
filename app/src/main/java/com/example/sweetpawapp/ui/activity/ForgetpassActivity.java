package com.example.sweetpawapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.ui.viewmodel.UserViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class ForgetpassActivity extends AppCompatActivity {
    TextInputEditText editEmail;
    Button btnTT;

    private int mauMacDinh;
    private int mauActive;
    private UserViewModel userViewModel;
    private ProgressBar progressBar;

    private static final String TAG = "User";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgetpass_activity);

        initUI();
        setupEvent();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }
    private void initUI(){
        //anh xa
        editEmail = findViewById(R.id.editEmail);
        btnTT = findViewById(R.id.btnTT);
        btnTT.setEnabled(false);
        progressBar = findViewById(R.id.progressBar);
        // Màu mặc định và màu khi sẵn sàng đăng nhập
        mauMacDinh = getColor(R.color.pinkLight);
        mauActive = getColor(R.color.pinkDark);
    }
    private  void setupEvent(){
        // Theo dõi thay đổi nội dung 2 ô nhập
        editEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kiemTraNhapDayDu();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        // Xử lý khi nhấn Tiếp tục
        btnTT.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnTT.setEnabled(false);

            Log.d(TAG, "Gửi yêu cầu check email: " + email);

            // Gọi API check email + gửi OTP
            userViewModel.checkEmailAndSendOtp(email).observe(this, response -> {
                progressBar.setVisibility(View.GONE);
                btnTT.setEnabled(true);

                if (response != null) {
                    Log.d(TAG, "Nhận phản hồi: success=" + response.isSuccess() +
                            ", expiresIn=" + response.getExpiresIn());
                } else {
                    Log.e(TAG, "Response null!");
                }

                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "OTP đã được gửi! Hết hạn sau: " + response.getExpiresIn(), Toast.LENGTH_LONG).show();

                    // chuyển sang màn hình nhập OTP
                    Intent intent = new Intent(ForgetpassActivity.this, OptActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Email không tồn tại hoặc gửi OTP thất bại!", Toast.LENGTH_LONG).show();
                }
            });
        });
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
    }
    private void kiemTraNhapDayDu() {
        String email = editEmail.getText().toString().trim();
        if (!email.isEmpty()) {
            btnTT.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.pinkDark)));
            btnTT.setEnabled(true);
        } else {
            btnTT.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.pinkLight)));
            btnTT.setEnabled(false);
        }
    }
}
