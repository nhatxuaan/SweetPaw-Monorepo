package com.example.sweetpawapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.user.Address;
import com.example.sweetpawapp.data.model.user.User;
import com.example.sweetpawapp.ui.adapter.AddressUserAdapter;
import com.example.sweetpawapp.ui.viewmodel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.example.sweetpawapp.data.model.user.AddAddressResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddressUserActivity extends AppCompatActivity {
    private static final String TAG = "UserAddress";
    private RecyclerView rcvDiaChi;
    private TextView imgThemDC;
    private LinearLayout layoutDCMoi;
    private EditText edtTenDC, edtSoNha, edtTenDuong, edtXa, edtHuyen, edtTinh;
    private MaterialButton btnHuy, btnLuu;
    private List<Address> diaChiList;
    private AddressUserAdapter adapter;

    private UserViewModel viewModel;
    private ActivityResultLauncher<Intent> detailAddressLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_user_activity);

        initUI();
        setupRecyclerView();
        setupViewModel();
        setupEvents();

        // Load danh sách địa chỉ lần đầu
        viewModel.fetchAddressList();
    }
    private void initUI(){
        rcvDiaChi = findViewById(R.id.rcvDiaChi);
        imgThemDC = findViewById(R.id.imgThemDC);
        layoutDCMoi = findViewById(R.id.layoutDCMoi);
        edtTenDC = findViewById(R.id.edtTenDC);
        edtSoNha = findViewById(R.id.edtSoNha);
        edtTenDuong = findViewById(R.id.edtTenDuong);
        edtXa = findViewById(R.id.edtXa);
        edtHuyen = findViewById(R.id.edtHuyen);
        edtTinh = findViewById(R.id.edtTinh);
        btnHuy = findViewById(R.id.btnHuy);
        btnLuu = findViewById(R.id.btnLuu);

    }

    private void setupRecyclerView() {
        diaChiList = new ArrayList<>();
        adapter = new AddressUserAdapter(this, diaChiList);
        rcvDiaChi.setLayoutManager(new LinearLayoutManager(this));
        rcvDiaChi.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Lấy JSON từ Intent
        Intent intent = getIntent();
        String diaChiJson = intent != null ? intent.getStringExtra("addresses_json") : null;

        Log.d(TAG, "JSON nhận được từ ProfileFragment: " + diaChiJson);

        if (diaChiJson != null && !diaChiJson.isEmpty()) {
            try {
                Type listType = new TypeToken<ArrayList<User.DiaChi>>() {}.getType();
                List<User.DiaChi> userDiaChiList = new Gson().fromJson(diaChiJson, listType);

                if (userDiaChiList != null && !userDiaChiList.isEmpty()) {
                    Log.d(TAG, "Danh sách địa chỉ nhận được: " + userDiaChiList.size() + " địa chỉ");

                    // Chuyển từng User.DiaChi sang Address
                    for (User.DiaChi uc : userDiaChiList) {
                        Address addr = new Address(
                                uc.getTenDiaChi(),
                                uc.getSoNha(),
                                uc.getTenDuong(),
                                uc.getPhuongXa(),
                                uc.getQuanHuyen(),
                                uc.getThanhPho(),
                                uc.isMacDinh()
                        );
                        Log.d(TAG, "Thêm địa chỉ đầy đủ: " + addr.getFullDiaChi());
                        diaChiList.add(addr);
                    }
                    adapter.setDiaChiList(diaChiList);
                } else {
                    Log.d(TAG, "Danh sách địa chỉ trống hoặc null");
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi parse addresses_json", e);
            }
        } else {
            Log.d(TAG, "Không nhận được dữ liệu addresses_json, danh sách rỗng");
        }

        // Quan sát kết quả API khi thêm địa chỉ mới
        viewModel.getAddAddressResult().observe(this, (AddAddressResponse response) -> {
            if (response != null) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                if (response.getDiaChi() != null) {
                    diaChiList.clear();
                    for (User.DiaChi dc : response.getDiaChi()) {
                        diaChiList.add(new Address(
                                dc.getTenDiaChi(),
                                dc.getSoNha(),
                                dc.getTenDuong(),
                                dc.getPhuongXa(),
                                dc.getQuanHuyen(),
                                dc.getThanhPho(),
                                dc.isMacDinh()
                        ));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    // Nếu API trả null thì fetch lại danh sách
                    viewModel.fetchAddressList();
                }

                // Ẩn layout thêm địa chỉ và reset EditText
                layoutDCMoi.setVisibility(LinearLayout.GONE);
                edtTenDC.setText("");
                edtSoNha.setText("");
                edtTenDuong.setText("");
                edtXa.setText("");
                edtHuyen.setText("");
                edtTinh.setText("");
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát danh sách địa chỉ
        viewModel.getAddressList().observe(this, list -> {
            if (list != null) {
                diaChiList.clear();
                for (User.DiaChi dc : list) {
                    diaChiList.add(new Address(
                            dc.getTenDiaChi(),
                            dc.getSoNha(),
                            dc.getTenDuong(),
                            dc.getPhuongXa(),
                            dc.getQuanHuyen(),
                            dc.getThanhPho(),
                            dc.isMacDinh()
                    ));
                }
                adapter.setDiaChiList(diaChiList);
            }
        });
    }

    private void setupEvents(){
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());

        // Bấm vào "Thêm địa chỉ"
        imgThemDC.setOnClickListener(v -> {
            layoutDCMoi.setVisibility(LinearLayout.VISIBLE);
        });

        // Hủy thêm địa chỉ
        btnHuy.setOnClickListener(v -> {
            layoutDCMoi.setVisibility(LinearLayout.GONE);
        });

        // Lưu địa chỉ mới
        btnLuu.setOnClickListener(v -> {
            String ten = edtTenDC.getText().toString().trim();
            String soNha = edtSoNha.getText().toString().trim();
            String duong = edtTenDuong.getText().toString().trim();
            String xa = edtXa.getText().toString().trim();
            String huyen = edtHuyen.getText().toString().trim();
            String tinh = edtTinh.getText().toString().trim();

            Log.d("User", "===== [CLICK LƯU ĐỊA CHỈ] =====");
            Log.d("User", "Dữ liệu nhập: " + ten + " - " + soNha + " - " + duong);

            if (!ten.isEmpty() && !soNha.isEmpty()) {
                User.DiaChi newAddress = new User.DiaChi (ten, soNha, duong, xa, huyen, tinh, false);

                // Lấy userId từ PreferencesManager
                PreferencesManager prefs = PreferencesManager.getInstance(this);
                String userId = prefs.getUserId();

                Log.d("User", "UserID từ Preferences: " + userId);

                // Gọi API qua ViewModel
                viewModel.addAddress(userId, newAddress);
                layoutDCMoi.setVisibility(LinearLayout.GONE);
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tên và số nhà", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            int pos = data.getIntExtra("update_position", -1);
            Log.e(TAG, "pos: " + pos);
            if(pos != -1){
                Log.d(TAG, "Load lại trang");
                viewModel.fetchAddressList();
            }

            int deletePos = data.getIntExtra("delete_position", -1);
            if (deletePos != -1) {
                diaChiList.remove(deletePos);
                adapter.notifyItemRemoved(deletePos);
                Log.d(TAG, "Cập nhật danh sách sau xóa");
                return;  // Không cần xử lý tiếp
            }

            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                // Bỏ mặc định tất cả địa chỉ khác
                for (Address addr : diaChiList) {
                    addr.setMacDinh(false);
                }
                // Gán địa chỉ được chọn là mặc định
                diaChiList.get(position).setMacDinh(true);

            //    adapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Load lại trang");
        viewModel.fetchAddressList();
    }


}
