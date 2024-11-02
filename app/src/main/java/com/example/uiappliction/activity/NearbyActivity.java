package com.example.uiappliction.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappliction.Adapters.RestaurantAdapter;
import com.example.uiappliction.MainActivity;
import com.example.uiappliction.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NearbyActivity extends AppCompatActivity implements RestaurantAdapter.OnFavoriteClickListener {

    private PlacesClient placesClient;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_activity);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_nearby);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(NearbyActivity.this, HomeActivity.class));
                        return true;

                    case R.id.nav_profile:
                        startActivity(new Intent(NearbyActivity.this, ProfileActivity.class));
                        return true;

                    case R.id.nav_shake:
                        startActivity(new Intent(NearbyActivity.this, ShakeActivity.class));
                        return true;

                    case R.id.nav_nearby:
                        return true;

                    default:
                        return false;
                }
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Places.initialize(getApplicationContext(), "AIzaSyCnQSdQHLJobqMpaqE_t2LbK1Pfd-A4Lno");
        placesClient = Places.createClient(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Location selectedLocation = new Location("");
                selectedLocation.setLatitude(place.getLatLng().latitude);
                selectedLocation.setLongitude(place.getLatLng().longitude);
                fetchNearbyRestaurants(selectedLocation);
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(NearbyActivity.this, "An error occurred: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ImageView currentLocationIcon = findViewById(R.id.currentLocationIcon);
        currentLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autocompleteFragment != null) {
                    autocompleteFragment.setText("");
                }
                getLocation();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    fetchNearbyRestaurants(location);
                } else {
                    Toast.makeText(NearbyActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchNearbyRestaurants(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d("Selected Location", "Latitude: " + latitude + ", Longitude: " + longitude);

            String apiKey = "AIzaSyCnQSdQHLJobqMpaqE_t2LbK1Pfd-A4Lno";
            String nearbySearchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + latitude + "," + longitude + "&radius=1500&type=restaurant&key=" + apiKey;

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(nearbySearchUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(NearbyActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            JSONArray results = jsonObject.getJSONArray("results");
                            List<Restaurant> restaurants = new ArrayList<>();

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject restaurantJson = results.getJSONObject(i);
                                String name = restaurantJson.getString("name");
                                String address = restaurantJson.getString("vicinity");
                                double rating = restaurantJson.has("rating") ? restaurantJson.getDouble("rating") : 0.0;

                                JSONArray typesArray = restaurantJson.getJSONArray("types");
                                List<String> types = new ArrayList<>();
                                for (int j = 0; j < typesArray.length(); j++) {
                                    String type = typesArray.getString(j);
                                    if (type.equals("cafe") || type.equals("bar") || type.equals("restaurant") || type.equals("store") || type.equals("meal_takeaway") || type.equals("meal_delivery")) {
                                        types.add(type);
                                    }
                                }

                                List<PhotoMetadata> photoMetadatas = fetchPhotoMetadata(restaurantJson);

                                Restaurant restaurant = new Restaurant(name, address, rating, photoMetadatas, types);
                                restaurants.add(restaurant);
                            }
                            Set<String> favoriteRestaurants = loadFavorites();
                            runOnUiThread(() -> {
                                adapter = new RestaurantAdapter(restaurants, placesClient, NearbyActivity.this, favoriteRestaurants);
                                recyclerView.setLayoutManager(new LinearLayoutManager(NearbyActivity.this));
                                recyclerView.setAdapter(adapter);
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private Set<String> loadFavorites() {
        SharedPreferences preferences = getSharedPreferences("Favorites", MODE_PRIVATE);
        return new HashSet<>(preferences.getStringSet("favorite_restaurants", new HashSet<>()));
    }

    private List<PhotoMetadata> fetchPhotoMetadata(JSONObject restaurantJson) {
        return new ArrayList<>();
    }

    @Override
    public void onFavoriteClick(Restaurant restaurant, ImageView favoriteButton) {
        SharedPreferences preferences = getSharedPreferences("Favorites", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> favorites = new HashSet<>(preferences.getStringSet("favorite_restaurants", new HashSet<>()));

        String restaurantKey = restaurant.getName() + " - " + restaurant.getAddress();

        if (favorites.contains(restaurantKey)) {
            // Remove from favorites
            favorites.remove(restaurantKey);
            editor.putStringSet("favorite_restaurants", favorites);
            editor.apply();

            // Change to unfavorited image
            favoriteButton.setImageResource(R.drawable.heart);  // Unselected heart icon
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            favorites.add(restaurantKey);
            editor.putStringSet("favorite_restaurants", favorites);
            editor.apply();

            favoriteButton.setImageResource(R.drawable.favorite); // Selected heart icon
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }

        Log.d("Favorites", "Current favorites: " + favorites.toString());
    }
}


