package com.example.shack;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.kongzue.stacklabelview.StackLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;
    private TextView randomNumberText;

    private static final float SHAKE_THRESHOLD = 500.0f; // 调整为合适的摇动灵敏度
    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;
    private List<String> menuList; // 菜单列表
    private long lastShakeTime = 0; // 上一次摇动触发时间
    private static final int SHAKE_COOLDOWN = 2000; // 2秒冷却时间

    private StackLabel stackLabel;
    List<String> str = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stackLabel = findViewById(R.id.stackLabelView);
        // 初始化菜单列表
        menuList = new ArrayList<>();
        menuList.add("Pizza");
        menuList.add("Burger");
        menuList.add("Sushi");
        menuList.add("Pasta");
        menuList.add("Pizza");
        menuList.add("Burger");
        menuList.add("Sushi");
        menuList.add("Pasta");
        randomNumberText = findViewById(R.id.randomNumberText);

        stackLabel.setLabels(menuList);

        // 初始化传感器管理器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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

            // 每100毫秒处理一次传感器数据
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                // 如果速度超过摇动阈值，就认为是一次摇动
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

    // 当检测到摇动时调用
    private void onShake() {
        long currentTime = System.currentTimeMillis();
        // 如果距离上一次摇动的时间少于2秒，不进行处理
        if ((currentTime - lastShakeTime) < SHAKE_COOLDOWN) {
            return;
        }

        // 更新上一次摇动触发时间
        lastShakeTime = currentTime;

        if (vibrator != null) {
            vibrator.vibrate(500); // 震动500毫秒
        }

        // 生成随机索引
        Random random = new Random();

        int randomIndex = random.nextInt(menuList.size()); // 从菜单列表中随机选择一个

        str.clear();
        str.add(menuList.get(randomIndex));
        // 获取随机菜单项
        String randomMenuItem = menuList.get(randomIndex);
        stackLabel.setSelectMode(true,str);


        // 在界面上显示随机菜单项
        randomNumberText.setText(randomMenuItem);
    }
}
