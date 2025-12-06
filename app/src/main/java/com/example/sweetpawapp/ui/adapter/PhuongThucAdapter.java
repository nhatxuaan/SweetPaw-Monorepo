package com.example.sweetpawapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sweetpawapp.R;

public class PhuongThucAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] phuongThuc;
    private int[] icons;

    public PhuongThucAdapter(Context context, String[] phuongThuc, int[] icons) {
        super(context, R.layout.item_phuong_thuc, phuongThuc);
        this.context = context;
        this.phuongThuc = phuongThuc;
        this.icons = icons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_phuong_thuc, parent, false);
        }

        ImageView imgIcon = convertView.findViewById(R.id.imgIcon);
        TextView tvPhuongThuc = convertView.findViewById(R.id.tvPhuongThuc);

        imgIcon.setImageResource(icons[position]);
        tvPhuongThuc.setText(phuongThuc[position]);

        return convertView;
    }
}
