package com.example.sweetpawapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sweetpawapp.ui.viewmodel.AuthViewModel;

import com.example.sweetpawapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.util.Log;


public class SignupActivity extends AppCompatActivity {
    private TextInputEditText editHoTen, editEmail, editMK, editMKConform;
    private Button btnDK;
    private MaterialButton btnGoogle;

    private static final String TAG = "SignupActivity";
    private static final int RC_SIGN_IN = 9002;

    private int mauMacDinh;
    private int mauActive;

    private ProgressBar progressBar;

    private AuthViewModel authViewModel;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity); // layout màn hình đăng ký
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initUI();
        setupGoogleSignIn();
        setupEvents();
    }
    private void initUI(){
        //anh xa
        editHoTen = findViewById(R.id.editHoTen);
        editEmail = findViewById(R.id.editEmail);
        editMK = findViewById(R.id.editMK);
        editMKConform = findViewById(R.id.editMK2);
        btnDK = findViewById(R.id.btnDN);
        btnDK.setEnabled(false);
        btnGoogle = findViewById(R.id.btnGoogle);
        // Màu mặc định và màu khi sẵn sàng đăng nhập
        mauMacDinh = getColor(R.color.pinkLight);
        mauActive = getColor(R.color.pinkDark);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupGoogleSignIn() {
        String webClientId = getString(R.string.default_web_client_id); // Web Client ID
        Log.d(TAG, "Web Client ID đang dùng để requestIdToken: " + webClientId); // 🔹 log ở đây

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id)) // lấy từ google-services.json
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void setupEvents(){
        // Theo dõi thay đổi nội dung ô nhập
        editHoTen.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kiemTraNhapDayDu();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

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
        editMKConform.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                kiemTraNhapDayDu();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        btnDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                }, 2000);


                String hoTen = editHoTen.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String matKhau = editMK.getText().toString().trim();
                String matKhau2 = editMKConform.getText().toString().trim();

                Log.d(TAG, "HoTen: " + hoTen + ", Email: " + email + ", MatKhau: " + matKhau + ", XacNhan: " + matKhau2);


                if (!matKhau.equals(matKhau2)) {
                    Toast.makeText(SignupActivity.this, "Mật khẩu xác nhận không trùng khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "Gọi API register với dữ liệu: hoTen=" + hoTen + ", email=" + email + ", MatKhau: " + matKhau);
                authViewModel.register(hoTen, email, matKhau).observe(SignupActivity.this, success -> {
                    if (Boolean.TRUE.equals(success)) {
                        Log.d(TAG, "Phản hồi API: Đăng ký thành công");
                        Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.d(TAG, "Phản hồi API: Đăng ký thất bại");
                        Toast.makeText(SignupActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
    }


    // 🔹 Xử lý kết quả đăng ký bằng Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    Log.d(TAG, "Google ID Token nhận được: " + idToken);

                    // 🔹 Gọi ViewModel để đăng ký
                    authViewModel.registerWithGoogle(idToken).observe(this, response -> {
                        Log.d(TAG, "Phản hồi server: " + (response != null ? response.toString() : "null"));
                        if (response != null && response.isSuccess()) {
                            Toast.makeText(this, "Đăng ký Google thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Đăng ký Google thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                Log.e(TAG, "Đăng ký Google thất bại: " + e.getMessage(), e);
                Toast.makeText(this, "Đăng ký Google thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void kiemTraNhapDayDu() {
        String hoTen = editHoTen.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String matKhau = editMK.getText().toString().trim();
        String matKhau2 = editMKConform.getText().toString().trim();

        if (!hoTen.isEmpty() && !email.isEmpty() && !matKhau.isEmpty() && !matKhau2.isEmpty()) {
            btnDK.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.pinkDark)));
            btnDK.setEnabled(true);
        } else {
            btnDK.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.pinkLight)));
            btnDK.setEnabled(false);
        }
    }
}
