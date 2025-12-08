package com.example.sweetpawapp.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.product.Product;
import com.example.sweetpawapp.ui.activity.DetailProductActivity;
import com.example.sweetpawapp.ui.activity.TheoDoiDHActivity;
import com.example.sweetpawapp.ui.adapter.FeatureAdapter;
import com.example.sweetpawapp.ui.adapter.OrderAdapter;
import com.example.sweetpawapp.ui.viewmodel.OrderViewModel;
import com.example.sweetpawapp.ui.viewmodel.ProductViewModel;

import java.util.List;

public class LikedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ActivityResultLauncher<Intent> detailLauncher;
    private RecyclerView recyclerLiked;
    private ProductViewModel productViewModel;
    private FeatureAdapter adapter;


    public LikedFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static LikedFragment newInstance(String param1, String param2) {
        LikedFragment fragment = new LikedFragment();
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
        View view = inflater.inflate(R.layout.fragment_liked, container, false);
        recyclerLiked = view.findViewById(R.id.recyclerLiked);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        recyclerLiked.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        loadFavoriteProducts();
        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String productId = data.getStringExtra("productId");
                            boolean isFavorite = data.getBooleanExtra("isFavorite", false);

                            // Cập nhật lại danh sách trong adapter
                            updateFavoriteInAllAdapters(productId, isFavorite);
                        }
                    }
                }
        );

        return view;
    }
    private void loadFavoriteProducts(){
        productViewModel.getFavorite().observe(getViewLifecycleOwner(), response -> {
            if (response == null || response.getData() == null) {
                Toast.makeText(getContext(), "Không có sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Product> favoriteList = response.getData();
            markFavoriteProducts(favoriteList);

            adapter = new FeatureAdapter(favoriteList, getViewLifecycleOwner(), productViewModel);

            adapter.setOnItemClickListener(product -> {
                Intent intent = new Intent(getActivity(), DetailProductActivity.class);
                intent.putExtra("name", product.getName());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("description", product.getDescript());
                intent.putExtra("imageUrl", product.getImageUrl());
                intent.putExtra("id", product.getMongoId());
                intent.putExtra("quantity",product.getQuantity());
                Log.d("HomeFragment", "số lượng" + product.getQuantity() );
                intent.putExtra("isFavorite", product.isFavorite());
                detailLauncher.launch(intent);

                //startActivity(intent);
            });

            // Click vào nút Like
            adapter.setOnItemLikeClickListener((product, position) -> {
                productViewModel.toggleFavorite(product.getMongoId())
                        .observe(getViewLifecycleOwner(), responseFavorite -> {
                            if (responseFavorite != null) {
                                // Lấy message từ server
                                String message = responseFavorite.getMessage();
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                loadFavoriteProducts();

                                adapter.notifyItemChanged(position);
                            } else {
                                Toast.makeText(getContext(), "Không thể cập nhật sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            recyclerLiked.setAdapter(adapter);
        });
    }

    private void markFavoriteProducts(List<Product> favoriteList) {
        if (favoriteList == null) return;
        for (Product p : favoriteList) {
            p.setFavorite(true);
        }
    }
    private void updateFavoriteInAllAdapters(String productId, boolean isFavorite) {
        if (adapter == null) return;

        List<Product> list = adapter.getProductList();
        if (list == null) return;

        for (int i = 0; i < list.size(); i++) {
            Product p = list.get(i);

            if (p.getMongoId().equals(productId)) {

                if (!isFavorite) {
                    // Nếu bỏ thích → xóa khỏi danh sách yêu thích
                    list.remove(i);
                    adapter.notifyItemRemoved(i);
                } else {
                    // Nếu vẫn yêu thích → cập nhật icon
                    p.setFavorite(true);
                    adapter.notifyItemChanged(i);
                }
                break;
            }
        }

    }

}