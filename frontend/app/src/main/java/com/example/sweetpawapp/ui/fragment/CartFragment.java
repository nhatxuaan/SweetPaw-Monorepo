package com.example.sweetpawapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.cart.CartItem;
import com.example.sweetpawapp.ui.activity.CartRecommendActivity;
import com.example.sweetpawapp.ui.activity.DatHangActivity;
import com.example.sweetpawapp.ui.adapter.CartAdapter;
import com.example.sweetpawapp.ui.viewmodel.CartViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment implements CartAdapter.OnCartChangeListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private static final String TAG = "CartFragment";

    private RecyclerView rcvCart;
    private TextView tvTongTien, tvDatHang, tvChonTB, tvXoa;
    private LinearLayout layOutTT, layOut, layOutGY;
    private RelativeLayout layOutTrong, option;
    private CartAdapter adapter;

    private CartViewModel cartViewModel;
    private List<CartItem> cartList = new ArrayList<>();

    public CartFragment() {
        // Required empty public constructor
    }


    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        rcvCart = view.findViewById(R.id.rcvCart);
        tvTongTien = view.findViewById(R.id.tvTongTien);
        tvDatHang = view.findViewById(R.id.tvDatHang);
        layOutTT = view.findViewById(R.id.layOutTT);
        layOut = view.findViewById(R.id.layOut);
        layOutGY = view.findViewById(R.id.layOutGY);
        tvChonTB = view.findViewById(R.id.tvChonTB);
        tvXoa = view.findViewById(R.id.tvXoa);
        layOutTrong = view.findViewById(R.id.layOutTrong);
        option = view.findViewById(R.id.option);

        // --- Gọi API lấy giỏ hàng ---
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        observeCartData();

        //setup RecyclerView
        adapter = new CartAdapter(cartList, cartViewModel,this);
        rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvCart.setAdapter(adapter);


        // setup giao diện khi ko có gì trong giỏ hàng
        if(adapter.getItemCount() == 0){
            layOutTrong.setVisibility(View.VISIBLE);
        } else {
            layOutTrong.setVisibility(View.GONE);
        }
        // chọn tất cả
        tvChonTB.setOnClickListener(v -> {
            Log.d(TAG, "Đã bấm nút Chọn tất cả");
            toggleSelectAll();
        });

        //Xử lý đặt hàng
        tvDatHang.setOnClickListener(v -> {
            List<CartItem> selectedItems = new ArrayList<>();
            int total = 0;
            for (CartItem item : cartList) {
                if (item.isSelected()) {
                    Log.d(TAG, "stock: " + item.getStock());
                    if(item.getQuantity() > item.getStock()){
                        Toast.makeText(getContext(), "Không đủ tồn kho sản phẩm "+ item.getName() , Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedItems.add(item);
                    total += item.getPrice() * item.getQuantity();
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(getContext(), "Chọn ít nhất 1 sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireContext(), DatHangActivity.class);
            intent.putParcelableArrayListExtra("selectedItems", new ArrayList<>(selectedItems));
            intent.putExtra("totalPrice", total);
            startActivity(intent);
        });
        layOutGY.setOnClickListener(v->{
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartList) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(getContext(), "Chọn ít nhất 1 sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireContext(), CartRecommendActivity.class);
            intent.putParcelableArrayListExtra("selectedItems", new ArrayList<>(selectedItems));
            startActivity(intent);
        });

        // Xóa sản phẩm được chọn
        tvXoa.setOnClickListener(v -> {
            List<String> selectedIds = new ArrayList<>();
            for (CartItem item : cartList) {
                if (item.isSelected()) {
                    Log.d(TAG, "Đã thêm sản phẩm chờ xóa" );
                    selectedIds.add(item.getProductId());
                }
            }


            if (selectedIds.isEmpty()) {
                Toast.makeText(getContext(), "Chọn ít nhất 1 sản phẩm để xoá", Toast.LENGTH_SHORT).show();
                return;
            }

            cartViewModel.removeSelectedItems(selectedIds);
        });

        updateTotal();
        return view;
    }

    private void observeCartData() {
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems == null) {
                // chưa load xong
                layOutTrong.setVisibility(View.GONE);
                return;
            }

            if (cartItems.isEmpty()) {
                // giỏ hàng trống
                layOutTrong.setVisibility(View.VISIBLE);
                layOutTT.setVisibility(View.GONE);
                layOut.setVisibility(View.GONE);
                layOutGY.setVisibility(View.GONE);
                cartList.clear();
                adapter.notifyDataSetChanged();
            } else {
                // có sản phẩm
                layOutTrong.setVisibility(View.GONE);
                layOutTT.setVisibility(View.VISIBLE);
                layOut.setVisibility(View.VISIBLE);
                cartList.clear();
                cartList.addAll(cartItems);
                //báo cho Adapter biết rằng dữ liệu đã thay đổi, để nó vẽ lại (refresh) toàn bộ danh sách hiển thị trên UI.
                adapter.notifyDataSetChanged();
                updateTotal();
            }
        });
    }

    private void toggleSelectAll() {
        boolean selectAll = false;

        for (CartItem item : cartList) {
            if (!item.isSelected()) {

                selectAll = true;
                break;
            }

        }

        adapter.selectAll(selectAll);
        adapter.notifyDataSetChanged(); // update RecyclerView
        tvChonTB.setText(selectAll ? "Bỏ chọn tất cả" : "Chọn toàn bộ giỏ hàng");
    }



    @Override
    public void onCartUpdated() {
        updateTotal();
    }

    private void updateTotal() {
        int total = 0;
        boolean hasSelectedItem = false;
        int isSelectedItem = 0;

        for (CartItem item : cartList) {
            if (item.isSelected()) {
                hasSelectedItem = true;
                total += item.getPrice() * item.getQuantity();
                isSelectedItem++;
            }
        }

        // Nếu có ít nhất 1 sản phẩm được chọn
        if (hasSelectedItem) {
            layOut.setVisibility(View.VISIBLE);
            tvXoa.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat("#,### đ");
            tvTongTien.setText(df.format(total));
            if(isSelectedItem <=5){
                layOutGY.setVisibility(View.VISIBLE);
            }
            else{
                layOutGY.setVisibility(View.GONE);
            }
        } else {
            // Ẩn đi khi không có sản phẩm nào được chọn
            layOut.setVisibility(View.GONE);
            tvXoa.setVisibility(View.GONE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        cartViewModel.refreshCart(); // mỗi lần quay lại giỏ hàng sẽ gọi API cập nhật lại
    }
    @Override
    public void onItemDeleted(String productId) {
        // Gọi ViewModel để xoá 1 sản phẩm
        cartViewModel.removeItem(productId);
        updateTotal();
        Toast.makeText(getContext(), "Đang xoá sản phẩm...", Toast.LENGTH_SHORT).show();
    }
}