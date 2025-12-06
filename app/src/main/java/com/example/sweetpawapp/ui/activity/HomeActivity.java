package com.example.sweetpawapp.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.sweetpawapp.R;
import com.example.sweetpawapp.data.local.PreferencesManager;
import com.example.sweetpawapp.ui.adapter.CustomViewPager;
import com.example.sweetpawapp.ui.adapter.ViewPagerAdapter;
import com.example.sweetpawapp.ui.fragment.HomeFragment;
import com.example.sweetpawapp.ui.fragment.OrdersFragment;
import com.example.sweetpawapp.ui.fragment.ProfileFragment;
import com.example.sweetpawapp.ui.viewmodel.OrderViewModel;
import com.example.sweetpawapp.ui.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
// Sau khi dang nhap xong, chuyen toi man hinh nay
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "User";
    private CustomViewPager mViewPager;
    //    ViewPager giup khoi tao san cac fragment ke tiep va xoa bo cac fragment cu
    private BottomNavigationView mbottomNavigationView;
    private UserViewModel userViewModel;
    private boolean isUserLoaded = false;
    ImageView imgNotice, imgChat, imgSetting;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mViewPager = findViewById(R.id.view_pager);
        mbottomNavigationView = findViewById(R.id.bottom_navigation);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);  // mac dinh la 1
        //       Xu kien chuyen page
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(int position){
                switch (position){
                    case 0:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                        break;
                    case 1:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_cart).setChecked(true);
                        break;
                    case 2:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_order).setChecked(true);
                        break;
                    case 3:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);
                        break;
                    case 4:
                        mbottomNavigationView.getMenu().findItem(R.id.menu_heart).setChecked(true);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mbottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_home) {
                    mViewPager.setCurrentItem(0);
                    HomeFragment home = (HomeFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 0);
                } else if (id == R.id.menu_cart) {
                    mViewPager.setCurrentItem(1);
                } else if (id == R.id.menu_order) {
                    mViewPager.setCurrentItem(2);
                    // Lấy OrderViewModel scoped cho Activity
                    OrderViewModel orderViewModel = new ViewModelProvider(HomeActivity.this).get(OrderViewModel.class);

                    // Chỉ gọi fetchUserOrders một lần, dữ liệu sẽ được LiveData quản lý
                    orderViewModel.fetchUserOrders();
                } else if (id == R.id.menu_profile) {
                    mViewPager.setCurrentItem(3);
                    if (!isUserLoaded) {
                        loadUserProfile();
                        isUserLoaded = true;
                    }
                } else if (id == R.id.menu_heart) {
                    mViewPager.setCurrentItem(4);
                }
                return true;
            }
        });
    }

    // Hàm gọi API lấy thông tin user
    private void loadUserProfile() {
        PreferencesManager prefs = PreferencesManager.getInstance(getApplicationContext());
        String token = prefs.getToken();

        if (token == null || token.isEmpty()) {
            Log.w(TAG, "Token rỗng, không thể lấy thông tin user.");
            return;
        }

        // Gọi API lấy thông tin user
        userViewModel.fetchUserProfile(token);

        // Nghe dữ liệu user trả về 1 lần, nếu muốn log kiểm tra
        userViewModel.getUserProfileLiveData().observe(this, user -> {
            if (user != null) {
                Log.d(TAG, "Đã load user: " + user.getHoTen());
            } else {
                Log.w(TAG, "Không lấy được thông tin user");
            }
        });
    }

}


