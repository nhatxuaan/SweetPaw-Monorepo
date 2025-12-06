package com.example.sweetpawapp.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.ui.viewmodel.UserViewModel;

public class OptActivity extends AppCompatActivity {
    EditText[] otpInputs;
    Button btnTT;
    private ProgressBar progressBar;
    private UserViewModel userViewModel;
    private String email;
    private static final String TAG = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opt_activity);

        initUI();
        setupEvents();

    }

    private void initUI(){
        // Anh xa
        otpInputs = new EditText[]{
                findViewById(R.id.otp1),
                findViewById(R.id.otp2),
                findViewById(R.id.otp3),
                findViewById(R.id.otp4),
                findViewById(R.id.otp5)
        };
        btnTT = findViewById(R.id.btnTT);
        progressBar = findViewById(R.id.progressBar);
        btnTT.setEnabled(false);

        // Nhận email từ màn hình trước
        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không nhận được email!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "Email nhận từ intent: " + email);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }
    private void setupEvents(){
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                    checkOtpFilled();
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        btnTT.setOnClickListener(v -> verifyOtp());
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
    }
    private void checkOtpFilled() {
        boolean filled = true;
        for (EditText editText : otpInputs) {
            if (editText.getText().toString().isEmpty()) {
                filled = false;
                break;
            }
        }
        btnTT.setEnabled(filled);
        btnTT.setAlpha(filled ? 1f : 0.5f);
        // Đổi màu
        int color = filled
                ? ContextCompat.getColor(this, R.color.pinkDark)
                : ContextCompat.getColor(this, R.color.pinkLight);
        btnTT.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void verifyOtp() {
        // Gộp OTP lại
        StringBuilder otpBuilder = new StringBuilder();
        for (EditText input : otpInputs) {
            otpBuilder.append(input.getText().toString().trim());
        }
        String otp = otpBuilder.toString();

        if (otp.length() < 5) {
            Toast.makeText(this, "Vui lòng nhập đủ 5 số OTP!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Gửi yêu cầu verify OTP cho email: " + email + " - OTP: " + otp);

        progressBar.setVisibility(View.VISIBLE);
        btnTT.setEnabled(false);

        userViewModel.verifyOtp(email, otp).observe(this, response -> {
            progressBar.setVisibility(View.GONE);
            btnTT.setEnabled(true);

            if (response != null) {
                Log.d(TAG, "Phản hồi từ server: success=" + response.isSuccess());

                if (response.isSuccess()) {
                    Toast.makeText(this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(OptActivity.this, UpdatepassActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Xác thực không thành công", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e(TAG, "Phản hồi null từ server!");
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_LONG).show();
            }
        });
    }
}