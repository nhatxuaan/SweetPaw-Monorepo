package com.example.sweetpawapp.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.ui.viewmodel.UserViewModel;

public class AccountActivity extends AppCompatActivity {
    private EditText edtName, edtST, edtEmail;
    private Button btnXacNhan;
    private UserViewModel userViewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initUI();
        setupEvents();

        // Lấy dữ liệu Intent (có thể null)
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");

        String phone = getIntent().getStringExtra("phone");

        Log.d("User", "edtName=" + edtName + ", edtEmail=" + edtEmail + ", edtST=" + edtST);

        // Cập nhật UI: nếu null thì hiển thị rỗng
        if (edtST != null) edtST.setText(phone != null ? phone : "");
        if (edtName != null) edtName.setText(name != null ? name : "");
        if (edtEmail != null) edtEmail.setText(email != null ? email : "");

    }
    private void initUI(){
        edtName = findViewById(R.id.edtName);
        edtST = findViewById(R.id.edtST);
        edtEmail = findViewById(R.id.edtEmail); edtEmail.setEnabled(false);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        progressBar = findViewById(R.id.progressBar);


    }
    private void setupEvents(){
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
        //Cập nhật thông tin
        btnXacNhan.setOnClickListener(v ->{
            String fullName = edtName.getText().toString();
            String email = edtEmail.getText().toString();
            String phone = edtST.getText().toString();
            progressBar.setVisibility(View.GONE);
            userViewModel.updateProfile(fullName, email, phone)
                    .observe(this, user -> {
                        if (user != null) {
                            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            Log.d("Profile", "User mới: " + user.getHoTen());
                            // báo cho ProfileFragment biết là đã cập nhật thành công
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });


        });

    }
}
