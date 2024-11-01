package com.example.uiappliction.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.uiappliction.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Button take_picture_button;
    private Button upload_button;
    private TextView textview;
    private ImageView backButton;
    private LinearLayout matchedItemsContainer;

    private Bitmap bitmap = null;
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private static final int SELECT_PICTURE_REQUEST_CODE = 200;

    private final List<String> menuItems = Arrays.asList("Coffee", "Pizza", "Chicken Thigh", "Chicken Burger", "Beef Burger", "Cola");

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // initialize
        previewView = findViewById(R.id.preview_image);
        take_picture_button = findViewById(R.id.scan_button);
        upload_button = findViewById(R.id.upload_button);
//        textview = findViewById(R.id.textview);
        backButton = findViewById(R.id.back_button);
        matchedItemsContainer = findViewById(R.id.matchedItemsContainer);

//        textview.setVisibility(View.GONE);
        matchedItemsContainer.setVisibility(View.GONE);

        // camera permission
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // take_picture_button touch event
        take_picture_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    takePhoto();
                    return true;
                }
                return false;
            }
        });

// upload_button touch event
        upload_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    openGallery();
                    return true;
                }
                return false;
            }
        });

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back to MainActivity
                Intent intent = new Intent(CameraActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // start CameraX
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                bitmap = imageProxyToBitmap(image);
                image.close();
                Log.d(TAG, "Photo captured successfully");
                Toast.makeText(CameraActivity.this, "Photo taken", Toast.LENGTH_SHORT).show();

                recognizeTextFromImage(bitmap);  // use MLKit for OCR
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
    }


    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void recognizeTextFromImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String recognizedText = visionText.getText();
                    Log.d(TAG, "Recognized text: " + recognizedText);

                    // Compare recognized text with menu items
                    List<String> matchedItems = compareWithMenu(recognizedText);

                    // Hide preview and show matched items container
                    previewView.setVisibility(View.GONE);
                    matchedItemsContainer.setVisibility(View.VISIBLE);
                    matchedItemsContainer.removeAllViews(); // Clear previous items

                    if (!matchedItems.isEmpty()) {
                        for (String item : matchedItems) {
                            addMenuItemToLayout(item, "AUD$10"); // Add each matched item to the layout with price
                        }
                    } else {
                        Toast.makeText(this, "No matching dishes found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Text recognition failed", e);
                    Toast.makeText(CameraActivity.this, "Text recognition failed", Toast.LENGTH_SHORT).show();
                });
    }


    // Add a menu item to the matched items container
    private void addMenuItemToLayout(String itemName, String price) {
        View menuItemView = getLayoutInflater().inflate(R.layout.menu_item_layout, matchedItemsContainer, false);

        // Set item name, price, and button (currently from local)
        TextView itemNameTextView = menuItemView.findViewById(R.id.menu_item_name);
        TextView priceTextView = menuItemView.findViewById(R.id.menu_item_price);
        Button viewArButton = menuItemView.findViewById(R.id.menu_item_view_ar_button);

        itemNameTextView.setText(itemName);
        priceTextView.setText(price);

        // Handle View AR button click to launch ModelSelectionActivity
        viewArButton.setOnClickListener(v -> {
            Toast.makeText(this, "Launching AR for " + itemName, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ModelSelectionActivity.class);
            intent.putExtra("ITEM_NAME", itemName); // Pass item name to ModelSelectionActivity
            startActivity(intent);
        });

        /*
        // Handle View AR button click if necessary
        viewArButton.setOnClickListener(v -> {
            Toast.makeText(this, "Launching AR for " + itemName, Toast.LENGTH_SHORT).show();
            // Implement AR functionality here
        });
         */

        // Add the menu item view to the container
        matchedItemsContainer.addView(menuItemView);
    }

    private List<String> compareWithMenu(String recognizedText) {
        List<String> matchedItems = new ArrayList<>();

        for (String menuItem : menuItems) {
            if (recognizedText.contains(menuItem)) {
                matchedItems.add(menuItem);
            }
        }
        return matchedItems;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE_REQUEST_CODE) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    recognizeTextFromImage(bitmap);  // use selected picture to recognize text
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
