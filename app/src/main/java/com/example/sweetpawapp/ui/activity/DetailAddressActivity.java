    package com.example.sweetpawapp.ui.activity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.ProgressBar;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.lifecycle.ViewModelProvider;

    import com.example.sweetpawapp.R;
    import com.example.sweetpawapp.data.local.PreferencesManager;
    import com.example.sweetpawapp.data.model.user.User;
    import com.example.sweetpawapp.ui.viewmodel.UserViewModel;
    import com.google.android.material.button.MaterialButton;

    public class DetailAddressActivity extends AppCompatActivity {
        private static final String TAG = "UpdateAddress";
        private EditText edtTenDC, edtSoNha, edtTenDuong, edtXa, edtHuyen, edtTinh;
        private MaterialButton btnChinhSua, btnHuy, btnLuu, btnXoa;
        private LinearLayout layoutTwoOption;
        private Button btnMacDinh;
        private ProgressBar progressBar;
        private UserViewModel viewModel;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.detail_address_activity);

            initUI();
            setupViewModel();
            displayData();
            setupEvents();
        }
        private void initUI(){
            edtTenDC = findViewById(R.id.edtTenDC); edtTenDC.setEnabled(false);
            edtSoNha = findViewById(R.id. edtSoNha); edtSoNha.setEnabled(false);
            edtTenDuong = findViewById(R.id.edtTenDuong); edtTenDuong.setEnabled(false);
            edtXa = findViewById(R.id.edtXa);  edtXa.setEnabled(false);
            edtHuyen = findViewById(R.id.edtHuyen); edtHuyen.setEnabled(false);
            edtTinh = findViewById(R.id.edtTinh); edtTinh.setEnabled(false);
            btnChinhSua = findViewById(R.id.btnChinhSua);
            layoutTwoOption = findViewById(R.id.layoutTwoOption);
            btnMacDinh = findViewById(R.id.btnMacDinh);
            btnHuy = findViewById(R.id.btnHuy);
            btnLuu = findViewById(R.id.btnLuu);
            btnXoa = findViewById(R.id.btnXoa);
            progressBar = findViewById(R.id.progressBar);
        }
        public void setupViewModel(){
            viewModel = new ViewModelProvider(this).get(UserViewModel.class);
            viewModel.getSetDefaultResult().observe(this, response -> {
                if (response != null) {
                    Toast.makeText(this, "Cập nhật địa chỉ mặc định thành công!", Toast.LENGTH_SHORT).show();

                    // Trả position về cho AddressUserActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Không thể cập nhật địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                }
            });

            viewModel.getDeleteAddressResult().observe(this, response -> {
                if (response != null) {
                    Toast.makeText(this, "Xóa địa chỉ thành công!", Toast.LENGTH_SHORT).show();

                    // Gửi position về để AddressUserActivity cập nhật RecyclerView
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("delete_position", getIntent().getIntExtra("position", -1));
                    setResult(RESULT_OK, resultIntent);

                    finish();
                } else {
                    Toast.makeText(this, "Không thể xóa địa chỉ", Toast.LENGTH_SHORT).show();
                }
            });

            viewModel.getUpdateAddressResult().observe(this, response -> {
                Log.e(TAG, "update thanh cong");
                if (response != null) {
                    Toast.makeText(this, "Cập nhật địa chỉ thành công!", Toast.LENGTH_SHORT).show();

                    // Trả position về AddressUserActivity để cập nhật RecyclerView
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("update_position", getIntent().getIntExtra("position", -1));
                    setResult(RESULT_OK, resultIntent);

                } else {
                    Toast.makeText(this, "Cập nhật địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }

            });



        }
        private void displayData(){
            //Lấy dữ liệu từ Intent
            Intent intent = getIntent();
            String tenLoai = intent.getStringExtra("tenLoai");
            String soNha = intent.getStringExtra("soNha");
            String tenDuong = intent.getStringExtra("tenDuong");
            String xa = intent.getStringExtra("xa");
            String huyen = intent.getStringExtra("huyen");
            String tinh = intent.getStringExtra("tinh");
            String fullDiaChi = intent.getStringExtra("fullDiaChi");
            boolean macDinh = intent.getBooleanExtra("macDinh", false);

            edtTenDC.setText(tenLoai);
            edtSoNha.setText(soNha);
            edtTenDuong.setText(tenDuong);
            edtXa.setText(xa);
            edtHuyen.setText(huyen);
            edtTinh.setText(tinh);


        }

        private void setupEvents(){
            // Nút back
            findViewById(R.id.imgBack).setOnClickListener(v -> finish());
            btnMacDinh.setOnClickListener(v ->{
                progressBar = findViewById(R.id.progressBar);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                setResult(RESULT_OK, resultIntent);

              //  progressBar.setVisibility(View.VISIBLE);

                int position = getIntent().getIntExtra("position", -1);
                String userId = PreferencesManager.getInstance(this).getUserId();

                if (position != -1) {
                    viewModel.setDefaultAddress(userId, position);
                }

                finish(); // quay lại màn hình trước
            });



            btnChinhSua.setOnClickListener(v ->{
                setUpEnable(true);
                layoutTwoOption.setVisibility(View.VISIBLE);
                btnChinhSua.setVisibility(View.GONE);
            });
            btnHuy.setOnClickListener(v ->{
                setUpEnable(false);
                layoutTwoOption.setVisibility(View.GONE);
                btnChinhSua.setVisibility(View.VISIBLE);
            });
            btnLuu.setOnClickListener(v ->{
                // Lấy dữ liệu từ EditText
                String tenDC = edtTenDC.getText().toString().trim();
                String soNha = edtSoNha.getText().toString().trim();
                String tenDuong = edtTenDuong.getText().toString().trim();
                String xa = edtXa.getText().toString().trim();
                String huyen = edtHuyen.getText().toString().trim();
                String tinh = edtTinh.getText().toString().trim();


                User.DiaChi updatedAddress = new User.DiaChi();
                updatedAddress.setTenDiaChi(tenDC);
                updatedAddress.setSoNha(soNha);
                updatedAddress.setTenDuong(tenDuong);
                updatedAddress.setPhuongXa(xa);
                updatedAddress.setQuanHuyen(huyen);
                updatedAddress.setThanhPho(tinh);

                int position = getIntent().getIntExtra("position", -1);
                String userId = PreferencesManager.getInstance(this).getUserId();

                if (position != -1) {
                    viewModel.updateAddress(userId, position, updatedAddress);
                    Log.e(TAG, "update thanh cong");
                }

                //Sau khi lưu xong thì đóng ô luu và hủy
                layoutTwoOption.setVisibility(View.GONE);
                btnChinhSua.setVisibility(View.VISIBLE);
                setUpEnable(false);

            });

            btnXoa.setOnClickListener(v -> {
                int position = getIntent().getIntExtra("position", -1);
                String userId = PreferencesManager.getInstance(this).getUserId();

                if (position == -1) {
                    Toast.makeText(this, "Không thể xác định địa chỉ để xóa", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gọi API xoá địa chỉ
                viewModel.deleteAddress(userId, position);

            });

        }
        private void setUpEnable(boolean status){
            edtTenDC.setEnabled(status);
            edtSoNha.setEnabled(status);
            edtTenDuong.setEnabled(status);
            edtXa.setEnabled(status);
            edtHuyen.setEnabled(status);
            edtTinh.setEnabled(status);
        }

    }
