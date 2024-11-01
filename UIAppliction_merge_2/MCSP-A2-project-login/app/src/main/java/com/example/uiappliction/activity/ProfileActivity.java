package com.example.uiappliction.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappliction.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private List<String> favoriteRestaurants;
    private ArrayAdapter<String> favoritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoriteRestaurants = getFavoriteRestaurants();

        favoritesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoriteRestaurants);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(new FavoritesAdapter(favoriteRestaurants));

        TextView tvUsername = findViewById(R.id.tv_username);

        SharedPreferences preferences = getSharedPreferences("UserLoginInfo", MODE_PRIVATE);
        String username = preferences.getString("username", "Guest");

        tvUsername.setText("Username: " + username);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                        return true;

                    case R.id.nav_profile:
                        return true;

                    case R.id.nav_shake:
                        startActivity(new Intent(ProfileActivity.this, ShakeActivity.class));
                        return true;

                    case R.id.nav_nearby:
                        startActivity(new Intent(ProfileActivity.this, NearbyActivity.class));
                        return true;

                    default:
                        return false;
                }
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
    private List<String> getFavoriteRestaurants() {
        SharedPreferences preferences = getSharedPreferences("Favorites", MODE_PRIVATE);
        Set<String> favoritesSet = preferences.getStringSet("favorite_restaurants", new HashSet<>());
        Log.d("ProfileActivity", "Favorite restaurants: " + favoritesSet);
        return new ArrayList<>(favoritesSet);
    }

    private static class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
        private List<String> favoriteRestaurants;

        public FavoritesAdapter(List<String> favoriteRestaurants) {
            this.favoriteRestaurants = favoriteRestaurants;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_restaurant, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.restaurantNameTextView.setText(favoriteRestaurants.get(position));
        }

        @Override
        public int getItemCount() {
            return favoriteRestaurants.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView restaurantNameTextView;
            ImageView restaurantImageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                restaurantNameTextView = itemView.findViewById(R.id.restaurantNameTextView);
                restaurantImageView = itemView.findViewById(R.id.restaurantImageView);
            }
        }
    }
}
