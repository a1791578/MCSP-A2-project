package com.example.foodxplore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResDetailActivity extends AppCompatActivity {

    private MenuAdapter menuAdapter;
    private List<String> menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.res_detail_activity);

        TextView nameTextView, addressTextView;
        RecyclerView menuRecyclerView;

        //nameTextView = findViewById(R.id.nameTextView);
        //addressTextView = findViewById(R.id.addressTextView);
        //menuRecyclerView = findViewById(R.id.menuRecyclerView);

        Intent intent = getIntent();
        String restaurantName = intent.getStringExtra("restaurant_name");
        String restaurantAddress = intent.getStringExtra("restaurant_address");

        //nameTextView.setText(restaurantName);
        //addressTextView.setText(restaurantAddress);

        menuItems = getMenuItemsForRestaurant(restaurantName);

        //menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new MenuAdapter(menuItems);
        //menuRecyclerView.setAdapter(menuAdapter);
    }

    private List<String> getMenuItemsForRestaurant(String restaurantName) {
        List<String> menu = new ArrayList<>();

        if (restaurantName.equals("Thai Town Melbourne")) {
            menu.add("Pad Thai");
            menu.add("Green Curry");
            menu.add("Tom Yum Soup");
        } else {
            menu.add("Dish 1");
            menu.add("Dish 2");
            menu.add("Dish 3");
        }
        return menu;
    }
}

