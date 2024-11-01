package com.example.uiappliction.Adapters;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappliction.R;
import com.example.uiappliction.activity.Restaurant;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;
import java.util.Set;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurantList;
    private PlacesClient placesClient;
    private OnFavoriteClickListener favoriteClickListener;
    private Set<String> favoriteRestaurants;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Restaurant restaurant, ImageView favoriteButton);
    }

    public RestaurantAdapter(List<Restaurant> restaurantList, PlacesClient placesClient, OnFavoriteClickListener listener, Set<String> favoriteRestaurants) {
        this.restaurantList = restaurantList;
        this.placesClient = placesClient;
        this.favoriteClickListener = listener;
        this.favoriteRestaurants = favoriteRestaurants;
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

        String restaurantKey = restaurant.getName() + " - " + restaurant.getAddress();
        if (favoriteRestaurants.contains(restaurantKey)) {
            holder.favoriteButton.setImageResource(R.drawable.favorite);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.heart);
        }
        holder.favoriteButton.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(restaurant, holder.favoriteButton);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, ratingTextView,typesTextView;
        ImageView imageView, favoriteButton;;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            typesTextView = itemView.findViewById(R.id.typesTextView);
            imageView = itemView.findViewById(R.id.restaurantImageView);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}
