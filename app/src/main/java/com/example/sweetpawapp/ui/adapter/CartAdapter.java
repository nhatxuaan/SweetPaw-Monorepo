package com.example.sweetpawapp.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.cart.CartItem;
import com.example.sweetpawapp.ui.viewmodel.CartViewModel;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartList;
    private OnCartChangeListener listener;
    private CartViewModel cartViewModel;


    public interface OnCartChangeListener {
        void onCartUpdated();
        void onItemDeleted(String productId);
    }

    public CartAdapter(List<CartItem> cartList, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }
    public CartAdapter(List<CartItem> cartList, CartViewModel cartViewModel, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.cartViewModel = cartViewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDesc.setText(item.getDescription());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        holder.tvPrice.setText(decimalFormat.format(item.getPrice()) + "đ");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        Log.d("Stock", item.getStock() + "");
        //Load ảnh từ URL bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl()) //
                .placeholder(R.drawable.anh) // ảnh tạm khi đang tải
                .error(R.drawable.anh) // ảnh hiển thị khi lỗi
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgProduct);


        holder.imgCong.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onCartUpdated();
            // Gọi API cập nhật server
            // Gọi API cập nhật server
            cartViewModel.updateQuantity(item.getProductId(), +1, (LifecycleOwner) listener);
        });

        holder.imgTru.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                listener.onCartUpdated();
                // Gọi API cập nhật server
                cartViewModel.updateQuantity(item.getProductId(), -1, (LifecycleOwner) listener);
            }
        });
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(item.isSelected());
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            listener.onCartUpdated();

        });

        holder.imgDelete.setOnClickListener(v -> {
            listener.onItemDeleted(item.getProductId()); // báo về Fragment
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView imgProduct, imgTru, imgCong, imgDelete;
        TextView tvName, tvDesc, tvPrice, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgTru = itemView.findViewById(R.id.imgTru);
            imgCong = itemView.findViewById(R.id.imgCong);
            imgDelete = itemView.findViewById(R.id.btnDelete);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
    public void selectAll(boolean selectAll) {
        for (CartItem item : cartList) {
            item.setSelected(selectAll);
        }
        notifyDataSetChanged();
        listener.onCartUpdated();
    }
}