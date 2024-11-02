package com.example.uiappliction.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.example.uiappliction.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private Button camera_button;
    private Button storage_ImageP_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        camera_button=findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;

                    case R.id.nav_profile:
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        return true;

                    case R.id.nav_shake:
                        startActivity(new Intent(HomeActivity.this, ShakeActivity.class));
                        return true;

                    case R.id.nav_nearby:
                        startActivity(new Intent(HomeActivity.this, NearbyActivity.class));
                        return true;

                    default:
                        return false;
                }
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

}

