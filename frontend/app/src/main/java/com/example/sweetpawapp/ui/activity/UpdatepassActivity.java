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

public class UpdatepassActivity extends AppCompatActivity {
    TextInputEditText editMK, editMK2;
    Button btnCN;
    private ProgressBar progressBar;
    private UserViewModel userViewModel;

    private int mauMacDinh;
    private int mauActive;
    private String email;

    private static final String TAG = "User";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatepass_activity);

        email = getIntent().getStringExtra("email");
        Log.d(TAG, "Email nhận từ intent: " + email);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initUI();
        setupEvents();

    }

    private void initUI(){
        //anh xa
        editMK = findViewById(R.id.editUpMK);
        editMK2 = findViewById(R.id.editUpMK2);
        btnCN = findViewById(R.id.btnCN);
        progressBar = findViewById(R.id.progressBar);
        btnCN.setEnabled(false);

        // Màu mặc định và màu khi sẵn sàng đăng nhập
        mauMacDinh = getColor(R.color.pinkLight);
        mauActive = getColor(R.color.pinkDark);
    }

    private void setupEvents(){
        // Theo dõi thay đổi nội dung 2 ô nhập
        editMK.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kiemTraNhapDayDu();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        editMK2.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kiemTraNhapDayDu();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        btnCN.setOnClickListener(v -> {
            String newPass = editMK.getText().toString().trim();
            String confirmPass = editMK2.getText().toString().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnCN.setEnabled(false);

            Log.d(TAG, "Gửi request resetPassword cho email: " + email);

            userViewModel.resetPassword(email, newPass, confirmPass).observe(this, response -> {
                progressBar.setVisibility(View.GONE);
                btnCN.setEnabled(true);

                if (response != null && response.isSuccess()) {
                    Log.d(TAG, "Reset password thành công!");
                    Toast.makeText(this, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    //Trở về màn hình đăng nhập
                    Intent intent = new Intent(UpdatepassActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Trở về đăng nhập
                } else {
                    Log.e(TAG, "Reset password thất bại!");
                    Toast.makeText(this, "Cập nhật thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        });


        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
    }


    private void kiemTraNhapDayDu() {
        String mk = editMK.getText().toString().trim();
        String mk2 = editMK2.getText().toString().trim();
        if (!mk.isEmpty() && !mk2.isEmpty()) {
            btnCN.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.pinkDark)));
            btnCN.setEnabled(true);
        } else {
            btnCN.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.pinkLight)));
            btnCN.setEnabled(false);
        }
    }
}
