package com.example.sweetpawapp.ui.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.product.Product;

import java.text.DecimalFormat;
import java.util.List;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteRequest;
import com.example.sweetpawapp.data.model.product.ToggleFavoriteResponse;
import com.example.sweetpawapp.data.model.review.ReviewOrder;
import com.example.sweetpawapp.ui.viewmodel.ProductViewModel;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {
    private ProductViewModel viewModel;
    private LifecycleOwner lifecycleOwner;
    private static final String TAG = "FeatureAdapter";
    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemLikeClickListener {
        void onItemLikeClicked(Product product, int position);
    }

    private OnItemLikeClickListener likeClickListener;

    public void setOnItemLikeClickListener(OnItemLikeClickListener listener) {
        this.likeClickListener = listener;
    }

    public FeatureAdapter(List<Product> productList) {
        this.productList = productList;
    }
    public FeatureAdapter(List<Product> list, LifecycleOwner owner, ProductViewModel viewModel) {
        this.productList = list;
        this.lifecycleOwner = owner;
        this.viewModel = viewModel;
    }

    public void setData(List<Product> newList) {
        if (newList == null || newList.isEmpty()) {
            Log.e(TAG, "Dữ liệu sản phẩm bị rỗng hoặc null!");
        }
        this.productList = newList;
        notifyDataSetChanged();
    }
    public List<Product> getProductList() {
        return productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) {
            Log.e(TAG, "Lỗi: Product tại vị trí " + position + " là null!");
            return;
        }
        try {
            holder.bind(product);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi bind dữ liệu sản phẩm tại vị trí " + position + ": " + e.getMessage(), e);
        }

        holder.bind(product);
       // FragmentUtils.setupToggleLikeButton(holder.itemView, R.id.imgLike);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });

        holder.imgLike.setOnClickListener(v -> {
            if (likeClickListener != null) {
                likeClickListener.onItemLikeClicked(productList.get(position), position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgLike;
        TextView tvName, tvPrice, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.ProductName);
            tvPrice = itemView.findViewById(R.id.ProductPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
            imgLike = itemView.findViewById(R.id.imgLike);

        }
        public void bind(Product product) {
            tvName.setText(product.getName());
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            tvPrice.setText(decimalFormat.format(product.getPrice()) + "đ");
            tvRating.setText(product.getRatingAvg() +"");
           // Log.e("Rating", "Rating: "+ product.getRatingAvg() +"");

            // ✅ Load ảnh từ link bằng Glide
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.banh)
                        .error(R.drawable.banh_tho)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Không load được ảnh cho sản phẩm: "
                                        + product.getName() + " | URL: " + product.getImageUrl(), e);
                                return false; // vẫn hiển thị ảnh lỗi mặc định
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(imgProduct);

                if (product.isFavorite()) {
                    imgLike.setImageResource(R.drawable.liked_heart); // icon đã thích
                } else {
                    imgLike.setImageResource(R.drawable.heart_white); // icon chưa thích
                }

            } else {
                Log.w(TAG, "Sản phẩm \"" + product.getName() + "\" không có URL ảnh!");
                imgProduct.setImageResource(R.drawable.banh);
            }


        }
    }
}
