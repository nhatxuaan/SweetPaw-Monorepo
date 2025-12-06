package com.example.sweetpawapp.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.ui.adapter.PhuongThucAdapter;

public class SettingActivity extends AppCompatActivity {
    private ImageView imgBack;
    private Switch switchOrder, switchSales, switchMessgage;
    private Spinner spnPhuongThuc;
    private Button btnLuu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        initUI();
        setupEvents();
    }
    private void initUI(){
        imgBack = findViewById(R.id.imgBack);
        switchOrder = findViewById(R.id.switchOrder); setupSwitch(switchOrder);
        switchSales = findViewById(R.id.switchSales); setupSwitch(switchSales);
        switchMessgage = findViewById(R.id.switchMessgage); setupSwitch(switchMessgage);

        spnPhuongThuc = findViewById(R.id.spnPhuongThuc);
        btnLuu = findViewById(R.id.btnLuu);
        // Dữ liệu cho spinner
        String[] phuongThuc = {"Tiếng Việt", "English"};

        int[] icons = {
                R.drawable.vietnamese,
                R.drawable.english
        };
        // Adapter
        PhuongThucAdapter adapterPT = new PhuongThucAdapter(this, phuongThuc, icons);
        spnPhuongThuc.setAdapter(adapterPT);
        spnPhuongThuc.setPopupBackgroundResource(R.drawable.bg_edittext_border);


    }
    private void setupEvents(){
        imgBack.setOnClickListener(v -> {

        });
        btnLuu.setOnClickListener(v -> {

        });
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
    }
    private void setupSwitch(Switch sw) {
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int thumbColor = getColor(isChecked ? R.color.pinkDark : R.color.grey);
            int trackColor = getColor(isChecked ? R.color.pinkLight : R.color.lighttext);

            sw.getThumbDrawable().setTint(thumbColor);
            sw.getTrackDrawable().setTint(trackColor);
        });

        // Gọi 1 lần ban đầu để tô màu đúng trạng thái hiện tại
        int thumbColor = getColor(sw.isChecked() ? R.color.pinkDark : R.color.grey);
        int trackColor = getColor(sw.isChecked() ? R.color.pinkLight : R.color.lighttext);
        sw.getThumbDrawable().setTint(thumbColor);
        sw.getTrackDrawable().setTint(trackColor);
    }
}
