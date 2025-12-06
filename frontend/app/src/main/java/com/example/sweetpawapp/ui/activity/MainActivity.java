package com.example.sweetpawapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.ui.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView txtCreate, txtQuenMK;
    private TextInputEditText editTenDN, editMK;
    private Button btnDN;
    private MaterialButton btnGoogle;
    private ProgressBar progressBar;

    private int mauMacDinh;
    private int mauActive;

    private AuthViewModel authViewModel;

    // [GOOGLE LOGIN] - Thêm client & request code
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Xin quyền POST_NOTIFICATIONS nếu Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        101); // request code tuỳ ý
            }
        }

        PreferencesManager prefs = PreferencesManager.getInstance(this);
        if (prefs.isLoggedIn() && prefs.getToken() != null) {
            Log.d("MainActivity", "Đã đăng nhập sẵn, chuyển sang HomeActivity");
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish(); // đóng màn đăng nhập để không quay lại được
            return; // không chạy tiếp nữa
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        initUI();


        editTenDN.setText(prefs.getLastEmail());
        editMK.setText(prefs.getLastPassword());


        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        setupEvents();

        kiemTraNhapDayDu();

        //  [GOOGLE LOGIN] - Cấu hình đăng nhập Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Lấy từ google-services.json
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initUI() {
        txtCreate = findViewById(R.id.txtCreate);
        txtQuenMK = findViewById(R.id.txtQuenMK);
        editTenDN = findViewById(R.id.editTenDN);
        editMK = findViewById(R.id.editMK);
        btnDN = findViewById(R.id.btnDN);
        btnGoogle = findViewById(R.id.btnGoogle);

        btnDN.setEnabled(false);
        mauMacDinh = getColor(R.color.pinkLight);
        mauActive = getColor(R.color.pinkDark);
        progressBar = findViewById(R.id.progressBar);

    }


    private void setupEvents() {
        // Theo dõi thay đổi nội dung 2 ô nhập
        editTenDN.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { kiemTraNhapDayDu(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        editMK.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { kiemTraNhapDayDu(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // Chuyển sang trang đăng ký
        txtCreate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Chuyển sang quên mật khẩu
        txtQuenMK.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgetpassActivity.class);
            startActivity(intent);
        });

        // Đăng nhập thường
        btnDN.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
            }, 2000);

            String email = editTenDN.getText().toString().trim();
            String matKhau = editMK.getText().toString().trim();

            if (email.isEmpty() || matKhau.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy token Firebase async
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Lỗi lấy token Firebase!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fcmToken = task.getResult();
                String subscribedTopics = "allUsers";
                Log.d(TAG, "Token lúc đăng nhập: " + fcmToken);

                // Subscribe topic async
                FirebaseMessaging.getInstance().subscribeToTopic("allUsers").addOnCompleteListener(subTask -> {
                    if (subTask.isSuccessful()) {
                        Log.d(TAG, "Đăng ký topic allUsers thành công");
                    } else {
                        Log.d(TAG, "Đăng ký topic allUsers thất bại");
                    }

                    // Gọi ViewModel login trên main thread
                    authViewModel.login(email, matKhau, fcmToken, subscribedTopics).observe(MainActivity.this, loginResponse -> {
                        progressBar.setVisibility(View.GONE);
                        if (loginResponse != null && loginResponse.isSuccess() && loginResponse.getToken() != null) {
                            PreferencesManager prefs = PreferencesManager.getInstance(MainActivity.this);
                            prefs.saveToken(loginResponse.getToken());
                            prefs.setLoggedIn(true);
                            prefs.saveLastLoginInfo(email, matKhau, loginResponse.getToken());

                            Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });
        });

        // Bắt sự kiện nút Google
        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    // Nhận kết quả từ Google Sign-In
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = signInTask.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    String subscribedTopics = "allUsers";
                    Log.d(TAG, "Google ID Token: " + idToken);

                    // Lấy token Firebase async
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {
                        if (!tokenTask.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Lỗi lấy token Firebase!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String fcmToken = tokenTask.getResult();
                        Log.d(TAG, "Token lúc đăng nhập Google: " + fcmToken);

                        // Subscribe topic async
                        FirebaseMessaging.getInstance().subscribeToTopic("allUsers").addOnCompleteListener(subTask -> {
                            if (subTask.isSuccessful()) {
                                Log.d(TAG, "Đăng ký topic allUsers thành công");
                            } else {
                                Log.d(TAG, "Đăng ký topic allUsers thất bại");
                            }

                            // Gọi ViewModel loginWithGoogle trên main thread
                            authViewModel.loginWithGoogle(idToken, fcmToken, subscribedTopics).observe(MainActivity.this, response -> {
                                progressBar.setVisibility(View.GONE);
                                if (response != null && response.isBoolean()) {
                                    Toast.makeText(MainActivity.this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    });
                }
            } catch (ApiException e) {
                Log.e(TAG, "Lỗi đăng nhập Google: " + e.getStatusCode(), e);
                Toast.makeText(this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void kiemTraNhapDayDu() {
        String tenDN = editTenDN.getText().toString().trim();
        String matKhau = editMK.getText().toString().trim();

        if (!tenDN.isEmpty() && !matKhau.isEmpty()) {
            btnDN.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.pinkDark)));
            btnDN.setEnabled(true);
        } else {
            btnDN.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.pinkLight)));
            btnDN.setEnabled(false);
        }
    }

    // Callback khi người dùng phản hồi quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Người dùng đã cấp quyền POST_NOTIFICATIONS");
            } else {
                Log.w(TAG, "Người dùng từ chối quyền POST_NOTIFICATIONS");
            }
        }
    }
}
