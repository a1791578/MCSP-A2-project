package com.example.foodxplore;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final String[] titles = {"AR Your Food", "Measure what matters", "Shake to Decide", "Never Be Disappointed Again"}; //页面标题
    private final String[] descriptions = {
            "Welcome\nBefore you order\nSee the Interactive Size Comparison",
            "Futuristic and tech-focused",
            "Find Your Meal",
            "Save Yourself from Small Portions"
    };
    private final int[] images = {R.drawable.onboarding_1, R.drawable.onboarding_2, R.drawable.onboarding_3, R.drawable.onboarding_4};

    public static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView imageView;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.onboardingTitle);
            descriptionTextView = itemView.findViewById(R.id.onboardingDescription);
            imageView = itemView.findViewById(R.id.onboardingImage);
        }
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_page, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.titleTextView.setText(titles[position]);
        holder.descriptionTextView.setText(descriptions[position]);
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}

