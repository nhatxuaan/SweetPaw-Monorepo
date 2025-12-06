package com.example.sweetpawapp.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.user.User;
import com.example.sweetpawapp.ui.activity.AccountActivity;
import com.example.sweetpawapp.ui.activity.AddressUserActivity;
import com.example.sweetpawapp.ui.activity.ChangePasswordActivity;
import com.example.sweetpawapp.ui.activity.DiaChiActivity;
import com.example.sweetpawapp.ui.activity.FeedbackActivity;
import com.example.sweetpawapp.ui.activity.MainActivity;
import com.example.sweetpawapp.ui.activity.SettingActivity;
import com.example.sweetpawapp.ui.viewmodel.UserViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private User currentUser;
    private static final String TAG = "User";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private TextView tvNameUser, dangXuat;
    private LinearLayout TTinUSer, DiaChi, guiGopY, setting, DoiMK;

    private UserViewModel userViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //Ánh xạ các view
        tvNameUser = view.findViewById(R.id.tvNameUser);
        dangXuat = view.findViewById(R.id.dangXuat);
        TTinUSer = view.findViewById(R.id.TTinUSer);
        DiaChi = view.findViewById(R.id.DiaChi);
        guiGopY = view.findViewById(R.id.guiGopY);
        setting = view.findViewById(R.id.setting);
        DoiMK = view.findViewById(R.id.DoiMK);

        observeUserData();

        setEvent();

        return view;
    }

    private void observeUserData() {
        userViewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                tvNameUser.setText(user.getHoTen());
                Log.d(TAG, "Nhận dữ liệu user: " + user.getHoTen());
            } else {
                Log.w(TAG, "User null, chưa load được");
            }
        });
    }
    private void setEvent(){
        // Click TTinUSer
        TTinUSer.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            intent.putExtra("name", currentUser != null ? currentUser.getHoTen() : null);
            intent.putExtra("email", currentUser != null ? currentUser.getEmail() : null);
            intent.putExtra("phone", currentUser != null ? currentUser.getSoDienThoai() : null);
            editProfileLauncher.launch(intent);
        });

        // Click DiaChi
        DiaChi.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddressUserActivity.class);
            String diaChiJson = currentUser != null ? new Gson().toJson(currentUser.getDiaChiList()) : null;
            intent.putExtra("addresses_json", diaChiJson);
            startActivity(intent);
        });
        guiGopY.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
            startActivity(intent);
        });
        setting.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        });
        DoiMK.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        dangXuat.setOnClickListener(v -> {
            //Dialog xác nhận đăng xuất
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        PreferencesManager prefs = PreferencesManager.getInstance(getContext());

                        // Xóa token & trạng thái đăng nhập
                        prefs.logoutSession();
                        Log.d(TAG, "Đã đăng xuất (xóa token, giữ email và password)");

                        // Thông báo
                        Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                        // Quay lại màn login
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        requireActivity().finish();
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Đã hủy đăng xuất", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });
    }
    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: Reloading user profile...");

        if (userViewModel != null) {
            PreferencesManager prefs = PreferencesManager.getInstance(requireContext());
            String token = prefs.getToken();

            if (token != null && !token.isEmpty()) {
                userViewModel.fetchUserProfile(token);
            } else {
                Log.w(TAG, "Token null hoặc rỗng, không thể load profile");
            }
        } else {
            Log.w(TAG, "userViewModel null trong onResume!");
        }
        observeUserData();
    }

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Khi AccountActivity báo cập nhật thành công → reload lại user profile
                    PreferencesManager prefs = PreferencesManager.getInstance(requireContext());
                    String token = prefs.getToken();
                    if (token != null && !token.isEmpty()) {
                        userViewModel.fetchUserProfile(token);
                    }
                }
            });


}