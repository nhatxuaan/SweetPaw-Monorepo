package com.example.sweetpawapp.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.user.ChangePasswordRequest;
import com.example.sweetpawapp.data.model.user.ChangePasswordResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtMKCu, edtMKMoi1, edtMKMoi2;
    private Button btnXacNhan;
    ImageView btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);

        edtMKCu = findViewById(R.id.edtMKCu);
        edtMKMoi1 = findViewById(R.id.edtMKMoi1);
        edtMKMoi2 = findViewById(R.id.edtMKMoi2);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        progressBar = findViewById(R.id.progressBar);

        btnBack = findViewById(R.id.imgBack);

        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


        btnXacNhan.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
            }, 1000);

            String oldPassword = edtMKCu.getText().toString().trim();
            String newPassword = edtMKMoi1.getText().toString().trim();
            String confirmPassword = edtMKMoi2.getText().toString().trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            changePassword(oldPassword, newPassword);
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        //Lấy token đã lưu sau khi đăng nhập
        PreferencesManager prefs = PreferencesManager.getInstance(this);
        String token = prefs.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);

        Call<ChangePasswordResponse> call = apiService.changePassword("Bearer " + token, request);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    // Xóa dữ liệu nhập
                    edtMKCu.setText("");
                    edtMKMoi1.setText("");
                    edtMKMoi2.setText("");
                    finish();
                } else {
                    String errMsg = "Đổi mật khẩu thất bại";
                    if (response.errorBody() != null) {
                        errMsg += ": kiểm tra mật khẩu cũ";
                    }
                    Toast.makeText(ChangePasswordActivity.this, errMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
