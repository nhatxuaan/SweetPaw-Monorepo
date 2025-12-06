package com.example.sweetpawapp.ui.adapter;

import android.content.Context;
import android.util.Log;
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
import com.example.sweetpawapp.data.model.order.OrderItem;
import com.example.sweetpawapp.data.model.order.Orders;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final Context context;
    private List<Orders> orderList;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(String orderId);
    }



    public OrderAdapter(Context context, List<Orders> orderList, OnOrderClickListener listener) {
        this.context = context;
        if (orderList != null) this.orderList = orderList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donhang, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders order = orderList.get(position);
        holder.txtMaDonHang.setText("#" + order.getGhnOrderCode());
        Log.d("Order", "MaDonHang=" + order.getGhnOrderCode() + ", ThoiGian=" + order.getCreatedAt());
        //Chuyển đổi thời gian
        try {
            String utcTime = order.getCreatedAt();

            // Định dạng của chuỗi từ server (ISO-8601)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Giờ gốc là UTC

            java.util.Date date = sdf.parse(utcTime);

            // Đổi sang giờ Việt Nam (UTC+7)
            java.text.SimpleDateFormat vnFormat = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            vnFormat.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

            String formattedDate = vnFormat.format(date);

            holder.txtThoiGian.setText(formattedDate);
            Log.d("TgDat", "Giờ VN: " + formattedDate);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TgDat", "Parse lỗi: " + e.getMessage());
            holder.txtThoiGian.setText(order.getCreatedAt());
        }




        holder.tvTrangThai.setText(order.getStatus());
        String imageUrl = order.getThumbnail_url();

        // Load ảnh sản phẩm đầu tiên bằng Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.imgProduct.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.anh)   // ảnh đang load (tuỳ bạn đổi)
                    .error(R.drawable.anh)     // ảnh lỗi (tuỳ bạn đổi)
                    .centerCrop()
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.anh);
        }

        // Xem chi tiết đơn hàng
        holder.txtXemChiTiet.setOnClickListener(v ->
                listener.onOrderClick(order.getId())   // hoặc getOrderId()
        );

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }



    /** Cập nhật danh sách dữ liệu mới */
    public void updateData(List<Orders> newList) {
        if (newList != null) {
            this.orderList = new ArrayList<>(newList);
        } else {
            this.orderList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }


    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtMaDonHang, txtThoiGian, txtXemChiTiet, tvTrangThai;
        ImageView imgProduct;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMaDonHang = itemView.findViewById(R.id.id_madonhang);
            txtThoiGian = itemView.findViewById(R.id.id_thoigian);
            txtXemChiTiet = itemView.findViewById(R.id.id_xemchitiet);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
        }
    }

}
