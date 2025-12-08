package com.example.sweetpawapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.cart.CartItem;
import com.example.sweetpawapp.data.model.order.Orders;
import com.example.sweetpawapp.ui.activity.TheoDoiDHActivity;
import com.example.sweetpawapp.ui.adapter.OrderAdapter;
import com.example.sweetpawapp.ui.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView rvDonHang;
    private OrderAdapter adapter;
    private List<Orders> orderList = new ArrayList<>();



    public OrdersFragment() {
        // Required empty public constructor
    }

    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
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
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        rvDonHang = view.findViewById(R.id.rvDonHang);
        rvDonHang.setLayoutManager(new LinearLayoutManager(getContext()));
        OrderViewModel orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        adapter = new OrderAdapter(requireContext(), orderList, orderId -> {

            orderViewModel.fetchOrderDetail(orderId);

            Intent intent = new Intent(getContext(), TheoDoiDHActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });

        rvDonHang.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OrderViewModel orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        orderViewModel.getUserOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && !orders.isEmpty()) {
                adapter.updateData(orders);
                Log.d("Order", "Nhận dữ liệu Orders, size=" + orders.size());
            } else {
                adapter.updateData(new ArrayList<>());
                Log.d("Order", "Danh sách Orders rỗng");
            }
        });
        orderViewModel.fetchUserOrders();
    }


}
