package com.example.uiappliction.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.uiappliction.R;

import java.util.ArrayList;
import java.util.List;

public class PageJumpActivity extends AppCompatActivity {

    RvAdapter rvAdapter;
    List<Boolean> flag;
    int lastPosition;
    BannerAdapter adapter;
    ViewPager viewPager;
    RecyclerView rv;
    Button jumpButton;
    boolean isLastPage = false;
    int[] images = {R.mipmap.llustration, R.mipmap.llustrationsc, R.mipmap.llustrationsa, R.mipmap.llustrationb};
    String []titles = {"AR Your Food", "Measure what matters", "Shake to Decide", "Never Be Disappointed Again"};
    String []contents = {
            "Welcome\nBefore you order\nSee the Interactive Size Comparison",
            "Futuristic and tech-focused",
            "Find Your Meal",
            "Save Yourself from Small Portions"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean("isFirstLaunch", true);
        if (!isFirstLaunch) {
            setContentView(R.layout.activity_page_jump);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            jumpButton = findViewById(R.id.iv);
            jumpButton.setEnabled(false);
            initPage();
            initRV();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }


    private void initRV() {
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(this,4));
        flag = new ArrayList<>();
        flag.add(true);
        flag.add(false);
        flag.add(false);
        flag.add(false);
        lastPosition = 0;
        rvAdapter = new RvAdapter(R.layout.dot,flag);
        rv.setAdapter(rvAdapter);
    }

    class RvAdapter extends BaseQuickAdapter<Boolean, BaseViewHolder> {

        public RvAdapter(int layoutResId, List<Boolean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, Boolean s) {
            View viewById = holder.getView(R.id.dot);
            if(s)
                viewById.setBackground(getResources().getDrawable(R.drawable.tabindicator));
            else
                viewById.setBackground(getResources().getDrawable(R.drawable.tabindicator1));
        }
    }

    private void initPage() {
        viewPager = findViewById(R.id.viewPager);
        adapter = new BannerAdapter();
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                flag.set(lastPosition,false);
                flag.set(position,true);
                lastPosition = position;
                rvAdapter.setNewInstance(flag);
                rvAdapter.notifyDataSetChanged();

                isLastPage = (position == images.length - 1);
                jumpButton.setEnabled(isLastPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void jump(View view) {
        if (isLastPage) {
            startActivity(new Intent(PageJumpActivity.this, LoginActivity.class));
        }
    }

    public class BannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(PageJumpActivity.this).inflate(R.layout.banner_item,null,false);
            ImageView imageView = view.findViewById(R.id.iv);
            TextView tv = view.findViewById(R.id.tv_a);
            TextView tv1 = view.findViewById(R.id.tv_d);
            imageView.setImageResource(images[position]);
            tv.setText(titles[position]);
            tv1.setText(contents[position]);
            container.addView(view);
            return view;
                }
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
