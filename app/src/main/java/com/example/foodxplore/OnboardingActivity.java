package com.example.foodxplore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    private Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean onboardingComplete = prefs.getBoolean("onboarding_complete", false);

        if (onboardingComplete) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_onboarding);
        }

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        OnboardingAdapter onboardingAdapter = new OnboardingAdapter();
        viewPager.setAdapter(onboardingAdapter);

        TabLayout tabLayout = findViewById(R.id.indicator);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
        }).attach();

        getStartedButton = findViewById(R.id.getStartedButton);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == onboardingAdapter.getItemCount() - 1) {
                    getStartedButton.setVisibility(View.VISIBLE);
                } else {
                    getStartedButton.setVisibility(View.GONE);
                }
            }
        });

        getStartedButton.setOnClickListener(v -> {
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("onboarding_complete", true)
                    .apply();

            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });



    }
}

