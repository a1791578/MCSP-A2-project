package com.example.imagepro;

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

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageView take_picture_button;
    private ImageView show_image_button;
    private ImageView current_image;
    private TextView textview;

    private Bitmap bitmap = null;
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        take_picture_button = findViewById(R.id.take_picture_button);
        show_image_button = findViewById(R.id.show_image_button);
        current_image = findViewById(R.id.current_image);
        textview = findViewById(R.id.textview);

        // initialize
        textview.setVisibility(View.GONE);
        current_image.setVisibility(View.GONE);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // click photo button
        take_picture_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    takePhoto(); // 调用拍照
                    return true;
                }
                return false;
            }
        });

        // click image button
        show_image_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (bitmap != null) {
                        current_image.setVisibility(View.VISIBLE);
                        current_image.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(CameraActivity.this, "Please take a picture first", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    // check camera permission
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


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
                bitmap = imageProxyToBitmap(image);  // convert it into bitmap
                image.close();
                Log.d(TAG, "Photo captured successfully");
                Toast.makeText(CameraActivity.this, "Photo taken", Toast.LENGTH_SHORT).show();

                recognizeTextFromImage(bitmap);  // use ML Kit to recognize text
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

                    // show the recognized text
                    textview.setVisibility(View.VISIBLE);
                    textview.setText(recognizedText);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Text recognition failed", e);
                    Toast.makeText(CameraActivity.this, "Text recognition failed", Toast.LENGTH_SHORT).show();
                });
    }


}
