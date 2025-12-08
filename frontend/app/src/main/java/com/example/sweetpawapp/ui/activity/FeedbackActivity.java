package com.example.sweetpawapp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sweetpawapp.R;

public class FeedbackActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText edtND;
    private Button btnGui;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);

        initUI();
        setupEvents();
    }
    private void initUI(){
        ratingBar = findViewById(R.id.ratingBar);
        edtND = findViewById(R.id.edtND);
        btnGui = findViewById(R.id.btnGui);
    }
    private void setupEvents(){
        btnGui.setOnClickListener(v -> {
            //Xu ly gui
            //Gửi xong thông báo rùi thoát ra
            finish();
        });
        // Nút back
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
    }
}
