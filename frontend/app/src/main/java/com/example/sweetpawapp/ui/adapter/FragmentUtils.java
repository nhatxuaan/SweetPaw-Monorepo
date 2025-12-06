package com.example.sweetpawapp.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.sweetpawapp.R;

public class FragmentUtils {
    public static void setupSmartBackButton(Fragment fragment, View rootView, int backButtonId, Integer viewPagerPageIndex) {
        View backBtn = rootView.findViewById(backButtonId);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                if (viewPagerPageIndex != null) {
                    ViewPager viewPager = fragment.requireActivity().findViewById(R.id.view_pager);
                    if (viewPager != null) {
                        viewPager.setCurrentItem(viewPagerPageIndex, true);
                        return;
                    }
                }
                FragmentManager fm = fragment.requireActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    fragment.requireActivity().onBackPressed();
                }
            });
        }
    }


    // Hàm setup nút Like có thể tái sử dụng
    public static void setupToggleLikeButton(View rootView, int likeButtonId) {
        ImageView likeBtn = rootView.findViewById(likeButtonId);
        if (likeBtn == null) return;

        final boolean[] isLiked = {false};

        likeBtn.setOnClickListener(v -> {
            isLiked[0] = !isLiked[0];
            likeBtn.setImageResource(isLiked[0]
                    ? R.drawable.liked_heart   // icon khi đã thích
                    : R.drawable.heart_white  // icon khi bỏ thích
            );
        });


    }
    public interface OnLikeClickListener {
        void onLikeClicked(boolean newState);
    }
}
