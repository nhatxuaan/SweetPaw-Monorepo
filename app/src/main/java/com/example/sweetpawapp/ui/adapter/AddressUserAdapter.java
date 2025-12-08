package com.example.sweetpawapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.user.Address;
import com.example.sweetpawapp.ui.activity.DetailAddressActivity;

import java.util.List;
public class AddressUserAdapter extends RecyclerView.Adapter<AddressUserAdapter.AddressViewHolder> {
    private final Context context;
    private List<Address> addressList;
    private int selectedPosition = -1;
    private Address selectedAddress; // địa chỉ được chọn hiện tại
    public AddressUserAdapter(Context context, List<Address> addressList){
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diachi_user, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);

        // Tên địa chỉ
        holder.tvNamDC.setText(address.getTenLoai());

        // Gộp chuỗi địa chỉ
        String fullAddress = address.getFullDiaChi();
        holder.tvND.setText(fullAddress);

        // Hiển thị "Mặc định" nếu có
        if (address.isMacDinh()) {
            holder.tvMacDinh.setVisibility(View.VISIBLE);
        } else {
            holder.tvMacDinh.setVisibility(View.GONE);
        }
        // Khi người dùng nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailAddressActivity.class);
            intent.putExtra("position", position); // gửi thêm vị trí trong danh sách
            intent.putExtra("tenLoai", address.getTenLoai());
            intent.putExtra("soNha", address.getSoNha());
            intent.putExtra("tenDuong", address.getTenDuong());
            intent.putExtra("xa", address.getPhuongXa());
            intent.putExtra("huyen", address.getQuanHuyen());
            intent.putExtra("tinh", address.getThanhPho());
            intent.putExtra("fullDiaChi", address.getFullDiaChi());
            intent.putExtra("macDinh", address.isMacDinh());

            ((Activity) context).startActivityForResult(intent, 100);
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamDC, tvND, tvMacDinh;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamDC = itemView.findViewById(R.id.tvNamDC);
            tvND = itemView.findViewById(R.id.tvND);
            tvMacDinh = itemView.findViewById(R.id.tvMacDinh);
        }
    }
    public void setDiaChiList(List<Address> list) {
        this.addressList = list;
        selectedPosition = -1;
        selectedAddress = null;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isMacDinh()) {
                    selectedPosition = i;
                    selectedAddress = list.get(i);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }



}
