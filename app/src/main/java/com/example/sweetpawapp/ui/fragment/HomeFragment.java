package com.example.sweetpawapp.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.data.model.product.ProductResponse;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteRequest;
import com.example.sweetpawapp.data.model.recommend.RecommendHomeResponse;
import com.example.sweetpawapp.data.network.ApiService;
import com.example.sweetpawapp.data.network.RetrofitClient;
import com.example.sweetpawapp.ui.activity.DetailProductActivity;

import com.example.sweetpawapp.ui.activity.MessageActivity;

import com.example.sweetpawapp.ui.activity.NotificationActivity;
import com.example.sweetpawapp.ui.activity.PaymentActivity;

import com.example.sweetpawapp.ui.activity.SearchActivity;
import com.example.sweetpawapp.ui.adapter.BannerAdapter;
import com.example.sweetpawapp.ui.adapter.FeatureAdapter;
import com.example.sweetpawapp.data.model.product.Product;
import com.example.sweetpawapp.ui.viewmodel.NotificationViewModel;
import com.example.sweetpawapp.ui.viewmodel.ProductViewModel;
import com.example.sweetpawapp.ui.viewmodel.RecommendViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private RecommendViewModel recommendViewModel;
    private ActivityResultLauncher<Intent> detailLauncher;
    private View rootView;
    private ProductViewModel productViewModel;
    private Boolean hasFavoriteProducts = false;
    private List<Product> favoriteProducts = new ArrayList<>();
    private ViewPager2 bannerViewPager;
    private List<Integer> banners;
    private Handler handler = new Handler();
    private Runnable bannerRunnable;
    private LinearLayout danhmuc1, danhmuc2, danhmuc3, danhmuc4, danhmuc5, danhmuc6;
    private NestedScrollView nestedScrollView;
    private ImageView imgDanhmuc1, imgDanhmuc2, imgDanhmuc3, imgDanhmuc4, imgDanhmuc5, imgDanhmuc6, imgFilter, chatBtn, notificationBtn;
    private EditText edtSearch;
    private LinearLayout selectedCategory = null;
    private ImageView selectedImage = null;
    private ProgressBar progressBar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PreferencesManager pref;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //nhận dữ liệu được truyền vào Fragment thông qua Bundle
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        recommendViewModel = new ViewModelProvider(this).get(RecommendViewModel.class);
        rootView = view;

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

        progressBar = view.findViewById(R.id.progressBar);
        edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setFocusable(false);
        edtSearch.setClickable(true);
        edtSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        imgFilter = view.findViewById(R.id.imgFilter);
        imgFilter.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.putExtra("showFilter", true); // gửi cờ báo là bấm filter
            startActivity(intent);
        });

        //Chuyển sang nhắn tin
        chatBtn = view.findViewById(R.id.chat);
        chatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MessageActivity.class);
            startActivity(intent);
        });

        //Chuyển sang thông báo
        notificationBtn = view.findViewById(R.id.notification);
        notificationBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });


        bannerViewPager = view.findViewById(R.id.bannerViewPager);

        danhmuc1 = view.findViewById(R.id.danhmuc1);
        danhmuc2 = view.findViewById(R.id.danhmuc2);
        danhmuc3 = view.findViewById(R.id.danhmuc3);
        danhmuc4 = view.findViewById(R.id.danhmuc4);
        danhmuc5 = view.findViewById(R.id.danhmuc5);
        danhmuc6 = view.findViewById(R.id.danhmuc6);

        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        imgDanhmuc1 = view.findViewById(R.id.imgDanhmuc1);
        imgDanhmuc2 = view.findViewById(R.id.imgDanhmuc2);
        imgDanhmuc3 = view.findViewById(R.id.imgDanhmuc3);
        imgDanhmuc4 = view.findViewById(R.id.imgDanhmuc4);
        imgDanhmuc5 = view.findViewById(R.id.imgDanhmuc5);
        imgDanhmuc6 = view.findViewById(R.id.imgDanhmuc6);


        // Lấy reference các section tương ứng
        LinearLayout sectionBanhkem = (LinearLayout) view.findViewById(R.id.recycler_banhkem).getParent();
        LinearLayout sectionBanhmi = (LinearLayout) view.findViewById(R.id.recycler_banhmi).getParent();
        LinearLayout sectionBanhmini = (LinearLayout) view.findViewById(R.id.recycler_banhmini).getParent();
        LinearLayout sectionBanhngot = (LinearLayout) view.findViewById(R.id.recycler_banhngot).getParent();
        LinearLayout sectionDouong = (LinearLayout) view.findViewById(R.id.recycler_douong).getParent();
        LinearLayout sectionBanhquy = (LinearLayout) view.findViewById(R.id.recycler_banhquy).getParent();
        //  int offset = 16;

        // Tạo mảng chứa tất cả danh mục và imageView tương ứng
        LinearLayout[] categories = new LinearLayout[]{danhmuc1, danhmuc2, danhmuc3, danhmuc4, danhmuc5, danhmuc6};
        ImageView[] categoryImages = new ImageView[]{imgDanhmuc1, imgDanhmuc2, imgDanhmuc3, imgDanhmuc4, imgDanhmuc5, imgDanhmuc6};

        // Gán click listener bằng vòng lặp
        for (int i = 0; i < categories.length; i++) {
            final int index = i; // cần final để dùng trong lambda
            categories[i].setOnClickListener(v -> {
                // Scroll tới section tương ứng
                int offset = 16; //  cách đỉnh màn hình 16 pixel
                LinearLayout[] sections = new LinearLayout[]{sectionBanhkem, sectionBanhmi, sectionBanhmini, sectionBanhngot, sectionDouong, sectionBanhquy};
                nestedScrollView.post(() -> nestedScrollView.smoothScrollTo(0, sections[index].getTop() - offset));

                // Reset màu cũ
                if (selectedImage != null) {
                    selectedImage.setBackgroundResource(R.drawable.category_circle);
                }

                // Đổi màu ImageView mới
                categoryImages[index].setBackgroundResource(R.drawable.category_circle_selected);
                selectedImage = categoryImages[index];
            });
        }
        // Thêm danh sách ảnh
        banners = new ArrayList<>();
        banners.add(R.drawable.banner1);
        banners.add(R.drawable.banner2);
        banners.add(R.drawable.banner3);
        banners.add(R.drawable.banner4);

        // Gắn adapter
        BannerAdapter adapter = new BannerAdapter(banners);
        bannerViewPager.setAdapter(adapter);

        // Tự động chuyển ảnh
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % banners.size();
                bannerViewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000); // 3 giây đổi ảnh
            }
        };
        handler.postDelayed(bannerRunnable, 3000);

        // Hiển thị danh sách sản phẩm nổi bật
        RecyclerView recyclerFeatured = view.findViewById(R.id.recycler_featured);
        // Các RecyclerView danh mục
        RecyclerView recyclerBanhkem = view.findViewById(R.id.recycler_banhkem);
        RecyclerView recyclerBanhmi = view.findViewById(R.id.recycler_banhmi);
        RecyclerView recyclerBanhmini = view.findViewById(R.id.recycler_banhmini);
        RecyclerView recyclerBanhngot = view.findViewById(R.id.recycler_banhngot);
        RecyclerView recyclerDouong = view.findViewById(R.id.recycler_douong);
        RecyclerView recyclerBanhquy = view.findViewById(R.id.recycler_banhquy);

        // Set LayoutManager cho tất cả RecyclerView
        RecyclerView[] recyclerViews = {
                recyclerFeatured, recyclerBanhkem, recyclerBanhmi,
                recyclerBanhmini, recyclerBanhngot, recyclerDouong, recyclerBanhquy
        };
        for (RecyclerView rv : recyclerViews) {
            rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        // Gọi hàm load sản phẩm gợi ý
        loadRecommendHome(recyclerFeatured);
        // Gọi hàm load API theo từng danh mục
        loadProductsByCategory("Bánh kem", recyclerBanhkem);
        loadProductsByCategory("Bánh mì", recyclerBanhmi);
        loadProductsByCategory("Bánh mini", recyclerBanhmini);
        loadProductsByCategory("Bánh ngọt", recyclerBanhngot);
        loadProductsByCategory("Đồ uống", recyclerDouong);
        loadProductsByCategory("Bánh quy", recyclerBanhquy);


        // sản phẩm nổi bật riêng
        //loadProductsByCategory("Bánh kem", recyclerFeatured); // để tạm là bánh kem

    }
    private void loadProductsByCategory(String categoryName, RecyclerView recyclerView) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ProductResponse> call = apiService.getProductsByCategory(categoryName);
        // Khi bắt đầu gọi API
        progressBar.setVisibility(View.VISIBLE);
        // Ẩn sau 1 giây
        new android.os.Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
        }, 1000);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();

                    // Kiểm tra dữ liệu có hợp lệ không
                    if (productResponse.getData() != null && !productResponse.getData().isEmpty()) {
                        List<Product> productList = productResponse.getData();

                        getFavoriteProducts(favorites -> {
                            markFavoriteProducts(productList, favorites); //đánh dấu các sản phẩm là favorite

                            FeatureAdapter adapter = new FeatureAdapter(productList);
                            //Đặt sự kiện khi click vào sản phẩm
                            adapter.setOnItemClickListener(product -> {
                                Intent intent = new Intent(getActivity(), DetailProductActivity.class);
                                intent.putExtra("name", product.getName());
                                intent.putExtra("price", product.getPrice());
                                intent.putExtra("description", product.getDescript());
                                intent.putExtra("imageUrl", product.getImageUrl());
                                intent.putExtra("id", product.getMongoId());
                                intent.putExtra("quantity",product.getQuantity());
                                intent.putExtra("isFavorite", product.isFavorite());

                                Log.d("HomeFragment", "số lượng" + product.getQuantity() );
                              //  startActivity(intent);
                                detailLauncher.launch(intent);
                            });
                            // Click vào nút Like
                            adapter.setOnItemLikeClickListener((product, position) -> {
                                productViewModel.toggleFavorite(product.getMongoId())
                                    .observe(getViewLifecycleOwner(), responseFavorite -> {
                                        if (responseFavorite != null) {
                                            // Lấy message từ server
                                            String message = responseFavorite.getMessage();
                                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                                        //Optional: cập nhật UI nếu muốn đổi icon ngay lập tức
                                        //Nếu product đang trong danh sách products thì đánh dấu favorite, ngược lại không favorite
                                            if (responseFavorite.getData() != null && responseFavorite.getData().getProducts() != null) {
                                                boolean isFavorite = responseFavorite.getData().getProducts()
                                                        .contains(product.getMongoId());
                                                product.setFavorite(isFavorite);
                                            }

                                            adapter.notifyItemChanged(position);
                                        } else {
                                            showToast("Không thể cập nhật sản phẩm yêu thích");
                                        }
                                    });
                            });

                            recyclerView.setAdapter(adapter);

                        });
                    } else {
                        // Không có dữ liệu từ API
                        Log.e("API_EMPTY", "Danh mục \"" + categoryName + "\" không có sản phẩm nào.");
                        showToast("Không có sản phẩm trong danh mục: " + categoryName);
                    }
                } else {
                    // API trả lỗi HTTP hoặc body null
                    String errorMsg = "API trả lỗi " + response.code() + " cho danh mục: " + categoryName;
                    Log.e("API_ERROR", errorMsg);
                    showToast("Lỗi tải sản phẩm: " + categoryName);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                // Lỗi mạng, server, hoặc JSON sai định dạng
                Log.e("API_FAILURE", "Không thể gọi API cho danh mục " + categoryName + ": " + t.getMessage());
                showToast("Không thể kết nối máy chủ cho danh mục: " + categoryName);
            }
        });
    }
    private void loadRecommendHome(RecyclerView recyclerView){
        recommendViewModel.loadRecommend().observe(getViewLifecycleOwner(), response -> {
            progressBar.setVisibility(View.GONE);

            if (response == null || response.products == null) {
                Toast.makeText(getContext(), "Không tải được gợi ý sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            List<RecommendHomeResponse.ProductRecommend> list = response.products;

            // Convert sang List<Product> để dùng chung FeatureAdapter
            List<Product> productList = new ArrayList<>();

            for (RecommendHomeResponse.ProductRecommend p : list) {
                Product product = new Product();
                product.setMongoId(p._id);
                product.setName(p.name);
                product.setPrice(p.price);
                product.setDescript(p.des);
                product.setImageUrl(p.url);
                product.setQuantity(p.stock);
                product.setFavorite(false); // sẽ đánh dấu lại sau

                productList.add(product);
            }

            // Đánh dấu sản phẩm yêu thích
            getFavoriteProducts(favorites -> {
                markFavoriteProducts(productList, favorites);

                FeatureAdapter adapter = new FeatureAdapter(productList);

                // click vào item -> mở detail
                adapter.setOnItemClickListener(product -> {
                    Intent intent = new Intent(getActivity(), DetailProductActivity.class);
                    intent.putExtra("id", product.getMongoId());
                    intent.putExtra("name", product.getName());
                    intent.putExtra("price", product.getPrice());
                    intent.putExtra("description", product.getDescript());
                    intent.putExtra("imageUrl", product.getImageUrl());
                    intent.putExtra("quantity", product.getQuantity());
                    intent.putExtra("isFavorite", product.isFavorite());

                    detailLauncher.launch(intent);
                });

                // click like
                adapter.setOnItemLikeClickListener((product, position) -> {
                    productViewModel.toggleFavorite(product.getMongoId())
                            .observe(getViewLifecycleOwner(), favResponse -> {
                                if (favResponse != null && favResponse.getData() != null) {
                                    boolean isFavorite = favResponse.getData().getProducts()
                                            .contains(product.getMongoId());

                                    product.setFavorite(isFavorite);
                                    adapter.notifyItemChanged(position);
                                    // Lấy message từ server
                                    String message = favResponse.getMessage();
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                                }
                            });
                });

                recyclerView.setAdapter(adapter);
            });
        });

    }

    private void showToast(String message) {
        if (getActivity() != null) {
            requireActivity().runOnUiThread(() ->
                    android.widget.Toast.makeText(getActivity(), message, android.widget.Toast.LENGTH_SHORT).show()
            );
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && bannerRunnable != null) {
            handler.removeCallbacks(bannerRunnable);
        }
    }

    @Override
    public void onClick(View v) {

    }
    private void markFavoriteProducts(List<Product> productList, List<Product> favoriteList) {
        if (favoriteList == null || productList == null) return;
        for (Product p : productList) {
            for (Product fav : favoriteList) {
                if (p.getMongoId().equals(fav.getMongoId())) {
                    p.setFavorite(true);  //đánh dấu yêu thích
                    break;
                }
            }
        }
    }
    private void getFavoriteProducts(FavoriteCallback callback) {
        productViewModel.getFavorite().observe(getViewLifecycleOwner(), response -> {
            if (response == null || response.getData() == null) {
                callback.onLoaded(new ArrayList<>()); // trả list trống
                return;
            }

            callback.onLoaded(response.getData()); // trả list favorite
        });
    }
    public interface FavoriteCallback {
        void onLoaded(List<Product> favorites);
    }

    private void updateFavoriteInAllAdapters(String productId, boolean isFavorite) {

        if (rootView == null) return;

        List<RecyclerView> allRecyclers = List.of(
                rootView.findViewById(R.id.recycler_featured),
                rootView.findViewById(R.id.recycler_banhkem),
                rootView.findViewById(R.id.recycler_banhmi),
                rootView.findViewById(R.id.recycler_banhmini),
                rootView.findViewById(R.id.recycler_banhngot),
                rootView.findViewById(R.id.recycler_douong),
                rootView.findViewById(R.id.recycler_banhquy)
        );

        for (RecyclerView rv : allRecyclers) {

            FeatureAdapter adapter = (FeatureAdapter) rv.getAdapter();
            if (adapter == null) continue;

            List<Product> list = adapter.getProductList();
            if (list == null) continue;

            for (int i = 0; i < list.size(); i++) {
                Product p = list.get(i);

                if (p.getMongoId().equals(productId)) {
                    p.setFavorite(isFavorite);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }


}