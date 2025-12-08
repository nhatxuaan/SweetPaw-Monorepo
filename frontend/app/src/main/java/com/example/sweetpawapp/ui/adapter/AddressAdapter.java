package com.example.sweetpawapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.user.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.DiaChiViewHolder> {

    private Context context;
    private List<Address> diaChiList;
    private int selectedPosition = -1;
    private Address selectedAddress; // địa chỉ được chọn hiện tại

    public AddressAdapter(Context context, List<Address> diaChiList) {
        this.context = context;
        setDiaChiList(diaChiList);

    }

    @NonNull
    @Override
    public DiaChiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diachi, parent, false);
        return new DiaChiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaChiViewHolder holder, int position) {
        Address diaChi = diaChiList.get(position);
        holder.tvNamDC.setText(diaChi.getTenLoai());
        holder.tvND.setText(diaChi.getFullDiaChi());

        holder.rbNhaRieng.setChecked(position == selectedPosition);

        // Khi click chọn địa chỉ
        View.OnClickListener listener = v -> {
            int previousPosition = selectedPosition;
            int newPosition = holder.getAdapterPosition();

            if (newPosition != RecyclerView.NO_POSITION) {
                selectedPosition = newPosition;
                selectedAddress = diaChiList.get(newPosition);
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
            }
        };

        holder.itemView.setOnClickListener(listener);
        holder.rbNhaRieng.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return diaChiList != null ? diaChiList.size() : 0;
    }

    public static class DiaChiViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamDC, tvND;
        RadioButton rbNhaRieng;

        public DiaChiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamDC = itemView.findViewById(R.id.tvNamDC);
            tvND = itemView.findViewById(R.id.tvND);
            rbNhaRieng = itemView.findViewById(R.id.rbNhaRieng);
        }
    }
    // Trả ra địa chỉ hiện đang được chọn
    public Address getSelectedAddress() {
        return selectedAddress;
    }

    // Trả ra địa chỉ đang chọn
    public int getSelectedPosition() {
        return selectedPosition;
    }

    // Set danh sách mới và tự chọn địa chỉ mặc định
    public void setDiaChiList(List<Address> list) {
        this.diaChiList = list;
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

    // Dùng khi bạn muốn set sẵn địa chỉ (ví dụ sau khi người dùng quay lại từ trang đặt hàng)
    public void setSelectedAddress(Address address) {
        if (address == null|| diaChiList == null) return;
        for (int i = 0; i < diaChiList.size(); i++) {
            if (diaChiList.get(i).equals(address)) {
                selectedPosition = i;
                selectedAddress = address;
                notifyDataSetChanged();
                return;
            }
        }
    }
}