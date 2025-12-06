package com.example.sweetpawapp.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.model.review.AddReviewRequest;
import com.example.sweetpawapp.data.model.review.DeleteRatingRequest;
import com.example.sweetpawapp.data.model.review.ReviewOrder;
import com.example.sweetpawapp.data.model.review.UpdateReviewRequest;
import com.example.sweetpawapp.ui.viewmodel.ReviewViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DanhGiaAdapter extends RecyclerView.Adapter<DanhGiaAdapter.DanhGiaViewHolder>{
    private Context context;
    private List<ReviewOrder> reviewList;
    private ReviewViewModel viewModel;

    public DanhGiaAdapter(Context context, List<ReviewOrder> reviewList, ReviewViewModel viewModel) {
        this.context = context;
        this.reviewList = reviewList;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public DanhGiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_danhgia, parent, false);
        return new DanhGiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhGiaViewHolder holder, int position) {
        ReviewOrder review = reviewList.get(position);

        holder.tvTensanpham.setText(review.getProductName());
        holder.edtND.setText(review.getComment());
      //  holder.tvTime.setText(review.getTime());
        holder.id_rating.setRating(review.getStars());

        boolean daDanhGia = review.getStars() > 0 || (review.getComment() != null && !review.getComment().isEmpty());

        if (daDanhGia) {
            // ĐÃ ĐÁNH GIÁ
            holder.btnGui.setVisibility(View.GONE);
            holder.loXoaCN.setVisibility(View.VISIBLE);   // Hiện Xóa - Cập nhật
            holder.edtND.setEnabled(false);               // Không cho sửa
            holder.id_rating.setIsIndicator(true);        // RatingBar không cho chỉnh
        } else {
            // CHƯA ĐÁNH GIÁ
            holder.btnGui.setVisibility(View.VISIBLE);
            holder.loXoaCN.setVisibility(View.GONE);
            holder.edtND.setEnabled(true);
            holder.id_rating.setIsIndicator(false);

        }


        //Sự kiện thêm đánh giá
        holder.btnGui.setOnClickListener(v -> {
            String comment = holder.edtND.getText().toString().trim();
            int stars = (int) holder.id_rating.getRating();

            if(comment.isEmpty() || stars <=0 ){
                Toast.makeText(context, "Vui lòng nhập đầy đủ nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            // Lấy productId trực tiếp từ review hiện tại
            String currentProductId = review.getProductId();
            String orderID = review.getOrderId();
            Log.d("ThemDanhGia", "request: " + currentProductId +" " + orderID +" " + stars +" "+ comment);
            AddReviewRequest req = new AddReviewRequest(currentProductId, orderID, stars, comment);
            viewModel.createReview(req);

            holder.btnGui.setVisibility(View.GONE);
            holder.loXoaCN.setVisibility(View.VISIBLE);
            holder.edtND.setEnabled(false);               // Không cho sửa
            holder.id_rating.setIsIndicator(true);
        });
        holder.btnCapNhat.setOnClickListener(v->{
            holder.loXoaCN.setVisibility(View.GONE);
            holder.loHuyLuu.setVisibility(View.VISIBLE);
            holder.edtND.setEnabled(true);
            holder.id_rating.setIsIndicator(false);

        });
        holder.btnHuy.setOnClickListener(v->{
            holder.loHuyLuu.setVisibility(View.GONE);
            holder.loXoaCN.setVisibility(View.VISIBLE);
            holder.edtND.setEnabled(false);               // Không cho sửa
            holder.id_rating.setIsIndicator(true);
        });

        holder.btnLuu.setOnClickListener(v->{
            //Xử lý cập nhật
            String comment = holder.edtND.getText().toString().trim();
            int stars = (int) holder.id_rating.getRating();
            String ratingId = review.getRatingId();
            Log.d("CapNhatDanhGia", "request: " + ratingId +" " + comment +" " + stars);
            if(comment.isEmpty() || stars <=0 ){
                Toast.makeText(context, "Vui lòng nhập đầy đủ nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            UpdateReviewRequest req = new UpdateReviewRequest(ratingId, stars, comment);
            viewModel.updateReview(req);

            holder.loHuyLuu.setVisibility(View.GONE);
            holder.loXoaCN.setVisibility(View.VISIBLE);
            holder.edtND.setEnabled(false);               // Không cho sửa
            holder.id_rating.setIsIndicator(true);

        });

        holder.btnXoa.setOnClickListener(v->{
            String ratingId = review.getRatingId();
            DeleteRatingRequest req = new DeleteRatingRequest(ratingId);
            viewModel.deleteReview(req);

            // Reset lại model để không gây lỗi sau này
            review.setRatingId(null);
            review.setComment("");
            review.setStars(0);

            holder.loXoaCN.setVisibility(View.GONE);
            holder.btnGui.setVisibility(View.VISIBLE);
            holder.edtND.setEnabled(true);
            holder.id_rating.setIsIndicator(false);

            //Xóa nội dung (Load lai)
            holder.edtND.setText("");
            holder.id_rating.setRating(0f);
        });


    }

    @Override
    public int getItemCount() {
        return reviewList != null ? reviewList.size() : 0;
    }
    public void setReviewList(List<ReviewOrder> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }

    public static class DanhGiaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTensanpham;
        EditText edtND;
        RatingBar id_rating;
        Button btnGui;
        LinearLayout loXoaCN, loHuyLuu;
        MaterialButton btnXoa, btnCapNhat, btnHuy, btnLuu;

        public DanhGiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTensanpham = itemView.findViewById(R.id.tvTensanpham);
            edtND = itemView.findViewById(R.id.edtND);
            id_rating = itemView.findViewById(R.id.id_rating);
            btnGui = itemView.findViewById(R.id.btnGui);
            loXoaCN = itemView.findViewById(R.id.loXoaCN);
            loHuyLuu = itemView.findViewById(R.id.loHuyLuu);
            btnXoa = itemView.findViewById(R.id.btnXoa);
            btnCapNhat = itemView.findViewById(R.id.btnCapNhat);
            btnHuy = itemView.findViewById(R.id.btnHuy);
            btnLuu = itemView.findViewById(R.id.btnLuu);
        }
    }


    public void updateRatingIdForCreatedItem(ReviewOrder serverReview) {
        if (serverReview == null) return;
        for (int i = 0; i < reviewList.size(); i++) {
            ReviewOrder local = reviewList.get(i);

            // tìm đúng item đang chờ update ratingId
            if (local.getOrderId().equals(serverReview.getOrderId()) &&
                    local.getProductId().equals(serverReview.getProductId())) {

                local.setRatingId(serverReview.getId_created());
                local.setComment(serverReview.getComment());
                local.setStars(serverReview.getStars());
                Log.d("CapNhatDanhGia", "response: " + serverReview.getId_created() +" " + serverReview.getComment() +" " + serverReview.getStars());
                notifyItemChanged(i);
                break;
            }
        }
    }



}
