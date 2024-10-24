package com.example.foodxplore;

import android.graphics.Bitmap;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurantList;
    private PlacesClient placesClient;

    public RestaurantAdapter(List<Restaurant> restaurantList, PlacesClient placesClient) {
        this.restaurantList = restaurantList;
        this.placesClient = placesClient;
        Log.d("RestaurantAdapter", "Restaurant list size: " + restaurantList.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.nameTextView.setText(restaurant.getName());
        holder.addressTextView.setText(restaurant.getAddress());

        if (restaurant.getRating() != 0.0) {
            holder.ratingTextView.setText(String.valueOf(restaurant.getRating()));
        } else {
            holder.ratingTextView.setText("No Rating");
        }

        List<String> types = restaurant.getTypes();
        if (types != null && !types.isEmpty()) {
            String typesText = TextUtils.join(", ", types);
            holder.typesTextView.setText(typesText);
        } else {
            holder.typesTextView.setText("No Type");
        }

        if (restaurant.getPhotoMetadatas() != null && !restaurant.getPhotoMetadatas().isEmpty()) {
            PhotoMetadata photoMetadata = restaurant.getPhotoMetadatas().get(0);
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500)
                    .setMaxHeight(300)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                holder.imageView.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                holder.imageView.setImageResource(R.drawable.default_image);
            });
        } else {
            holder.imageView.setImageResource(R.drawable.default_image);
        }
        /*
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ResDetailActivity.class);
            intent.putExtra("restaurant_name", restaurant.getName());
            intent.putExtra("restaurant_address", restaurant.getAddress());
            v.getContext().startActivity(intent);
        });
        */
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, ratingTextView,typesTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            typesTextView = itemView.findViewById(R.id.typesTextView);
            imageView = itemView.findViewById(R.id.restaurantImageView);
        }
    }
}
