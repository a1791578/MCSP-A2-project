package com.example.uiappliction.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappliction.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kongzue.stacklabelview.StackLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;
    private TextView randomNumberText;
    private Button startShakeButton;
    private Button viewArButton;
    private TextView shakeItemName;
    private ImageView menuItemImage;

    private static final float SHAKE_THRESHOLD = 500.0f; // Adjust for appropriate shaking sensitivity
    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;
    private List<String> menuList; // menu list
    private long lastShakeTime = 0; // Last shake trigger time
    private static final int SHAKE_COOLDOWN = 2000; // 2 seconds cooldown




    //private StackLabel stackLabel;
    List<String> str = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        menuItemImage = findViewById(R.id.menu_item_image);
        shakeItemName = findViewById(R.id.shake_item_name);
        viewArButton = findViewById(R.id.shake_item_view_ar_button);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_shake);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(ShakeActivity.this, HomeActivity.class));
                        return true;

                    case R.id.nav_profile:
                        startActivity(new Intent(ShakeActivity.this, ProfileActivity.class));
                        return true;

                    case R.id.nav_shake:
                        return true;

                    case R.id.nav_nearby:
                        startActivity(new Intent(ShakeActivity.this, NearbyActivity.class));
                        return true;

                    default:
                        return false;
                }
            }
        });

        //stackLabel = findViewById(R.id.stackLabelView);
        menuList = new ArrayList<>();
        menuList.add("Coffee");
        menuList.add("Pizza");
        menuList.add("Chicken Thigh");
        menuList.add("Chicken Burger");
        menuList.add("Beef Burger");
        menuList.add("Cola");
        randomNumberText = findViewById(R.id.randomNumberText);

        //stackLabel.setLabels(menuList);

        // 初始化传感器管理器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        startShakeButton = findViewById(R.id.startShakeButton);
        startShakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(ShakeActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            // Sensor data is processed every 100 milliseconds
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                // If the speed exceeds the shaking threshold, it is considered a shaking
                if (speed > SHAKE_THRESHOLD) {
                    onShake();
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // Called when shaking is detected
    private void onShake() {
        long currentTime = System.currentTimeMillis();
        // If the time since the last shaking is less than 2 seconds, do not process
        if ((currentTime - lastShakeTime) < SHAKE_COOLDOWN) {
            return;
        }

        // Updated the last shake trigger time
        lastShakeTime = currentTime;

        if (vibrator != null) {
            vibrator.vibrate(500); // Vibration 500 ms
        }

        // Generate random index
        Random random = new Random();

        int randomIndex = random.nextInt(menuList.size()); // Select one at random from the menu list

        str.clear();
        str.add(menuList.get(randomIndex));
        // Gets random menu items
        String randomMenuItem = menuList.get(randomIndex);
        //stackLabel.setSelectMode(true,str);
        // Displays random menu items on the interface
        //randomNumberText.setText(randomMenuItem);

        shakeItemName.setText(randomMenuItem);
        menuItemImage.setVisibility(View.VISIBLE);
        shakeItemName.setVisibility(View.VISIBLE);
        viewArButton.setVisibility(View.VISIBLE);

        viewArButton.setOnClickListener(v -> {
            Toast.makeText(this, "Launching AR for " + randomMenuItem, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ModelSelectionActivity.class);
            intent.putExtra("ITEM_NAME", randomMenuItem);
            intent.putExtra("FROM_PAGE", "Shake");
            startActivity(intent);
        });
    }
}

