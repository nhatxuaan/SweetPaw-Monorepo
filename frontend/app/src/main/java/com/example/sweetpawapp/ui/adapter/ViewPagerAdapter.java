package com.example.sweetpawapp.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.sweetpawapp.ui.fragment.CartFragment;
import com.example.sweetpawapp.ui.fragment.HomeFragment;
import com.example.sweetpawapp.ui.fragment.LikedFragment;
import com.example.sweetpawapp.ui.fragment.OrdersFragment;
import com.example.sweetpawapp.ui.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private String name, description;
    private double price;
    private String imageUrl;
    private String id;
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior){
        super(fm, behavior);
    }
    @NonNull
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new CartFragment();
            case 2:
                return new OrdersFragment();
            case 3:
                return new ProfileFragment();
            case 4:
                return new LikedFragment();
            default:
                return new HomeFragment();
        }
    }
    public int getCount(){
        return 5;
    }
}
