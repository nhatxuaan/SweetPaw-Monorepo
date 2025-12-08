package com.example.sweetpawapp.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.review.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvUserName.setText(review.getUserName());
        holder.tvReview.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());

        //holder.tvTime.setText(review.getTime());
        //Chuyển đổi thời gian
        try {
            String utcTime = review.getTime();

            // Định dạng của chuỗi từ server (ISO-8601)
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Giờ gốc là UTC

            java.util.Date date = sdf.parse(utcTime);

            // Đổi sang giờ Việt Nam (UTC+7)
            java.text.SimpleDateFormat vnFormat = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            vnFormat.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

            String formattedDate = vnFormat.format(date);

            holder.tvTime.setText(formattedDate);
            Log.d("TgDat", "Giờ VN: " + formattedDate);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TgDat", "Parse lỗi: " + e.getMessage());
            holder.tvTime.setText(review.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvReview, tvTime;
        RatingBar ratingBar;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvReview = itemView.findViewById(R.id.tvReview);
            tvTime = itemView.findViewById(R.id.tvTime);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}

