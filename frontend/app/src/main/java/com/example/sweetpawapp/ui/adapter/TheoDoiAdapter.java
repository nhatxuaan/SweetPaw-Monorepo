package com.example.sweetpawapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sweetpawapp.R;

import java.text.DecimalFormat;
import java.util.List;

public class TheoDoiAdapter<T> extends RecyclerView.Adapter<TheoDoiAdapter.ViewHolder> {

    private final Context context;
    private final List<T> itemList;
    private final Binder<T> binder;

    // Interface để bind dữ liệu
    public interface Binder<T> {
        void bind(ViewHolder holder, T item);
    }

    public TheoDoiAdapter(Context context, List<T> itemList, Binder<T> binder) {
        this.context = context;
        this.itemList = itemList;
        this.binder = binder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trangthai, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        binder.bind(holder, itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameProduct, tvSoLuong, tvGia;
        ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameProduct = itemView.findViewById(R.id.tvNameProduct);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvGia = itemView.findViewById(R.id.tvGia);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }

    // Helper method load dữ liệu chung
    public static void bindCommonData(ViewHolder holder, String name, int quantity, int price, String imageUrl) {
        holder.tvNameProduct.setText(name);
        holder.tvSoLuong.setText(String.valueOf(quantity));
        holder.tvGia.setText(new DecimalFormat("#,###").format(price) + "đ");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.imgProduct.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.banh)
                    .error(R.drawable.banh_tho)
                    .centerCrop()
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.banh); // default image
        }
    }

    // Helper method cho item không có ảnh (OrderDetailItem)
    public static void bindWithoutImage(ViewHolder holder, String name, int quantity, int price, String imageUrl) {
        holder.tvNameProduct.setText(name);
        holder.tvSoLuong.setText(String.valueOf(quantity));
        holder.tvGia.setText(new DecimalFormat("#,###").format(price) + "đ");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.imgProduct.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.banh)
                    .error(R.drawable.banh_tho)
                    .centerCrop()
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.banh); // ảnh mặc định
        }
    }
}
