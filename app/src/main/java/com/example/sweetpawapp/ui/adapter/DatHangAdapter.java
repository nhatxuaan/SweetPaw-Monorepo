package com.example.sweetpawapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.cart.CartItem;

import java.text.DecimalFormat;
import java.util.List;

public class DatHangAdapter extends RecyclerView.Adapter<DatHangAdapter.DatHangViewHolder> {

    private final List<CartItem> cartList;

    public DatHangAdapter(List<CartItem> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public DatHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dathang, parent, false);
        return new DatHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DatHangViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        if (item == null) return;

        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDescription());

        DecimalFormat df = new DecimalFormat("#,### đ");
        holder.tvPrice.setText(df.format(item.getPrice()));

        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        // Load ảnh từ URL bằng Glide (kiểm tra null/empty)
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.imgProduct.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.anh)    // ảnh đang load
                    .error(R.drawable.anh)      // ảnh khi lỗi
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.banh);
        }

    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class DatHangViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvDesc, tvPrice, tvQuantity;

        public DatHangViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}