package com.example.uiappliction.activity;

import static com.example.uiappliction.Utils.KeyboardUtils.hideKeyboard;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.uiappliction.MainActivity;
import com.example.uiappliction.R;
import com.example.uiappliction.Utils.SubmitButton;
import com.example.uiappliction.action.HandlerAction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hjq.xtoast.XToast;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements HandlerAction {
    EditText mUsername, mPassword, mCode;
    SubmitButton mLoginButton;
    AppCompatTextView mSignUpButton;

    Button mSendCode;

    private FirebaseAuth mAuth;

    private String verificationId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());



        setContentView(R.layout.activity_login);
        mUsername = findViewById(R.id.textView_username);
        mPassword = findViewById(R.id.textView_password);
        mCode = findViewById(R.id.editText_verification_code);
        mLoginButton = findViewById(R.id.btn_login);
        mSendCode = findViewById(R.id.button_send_verification_code);
        mSignUpButton = findViewById(R.id.btn_signup);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");

        Intent initIntent = getIntent();
        Intent navigateToSignUp = new Intent(this, SignUpActivity.class);
        Intent navigateToHome = new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        mLoginButton.setOnClickListener(v -> {
            String username = this.mUsername.getText().toString();
            String phone = this.mPassword.getText().toString();
            String code = this.mCode.getText().toString();
//            Log.d("Login", "username: " + username + " phone: " + phone);
            mLoginButton.showProgress();

            if (checkEmpty(username, phone)) return;

            boolean isNumber = isNumber(username);
            verifyCode(code);
        });

        mSendCode.setOnClickListener(v -> {
            String username = this.mUsername.getText().toString();
            String phone = this.mPassword.getText().toString();
            if (checkEmpty(username, phone)) return;
            checkCaptcha(phone);
        });


        mSignUpButton.setOnClickListener(v -> {
            startActivity(navigateToSignUp);
        });
        findViewById(R.id.imageView_bg).setOnClickListener(v -> {
            if (mUsername.isFocused() || mPassword.isFocused()) {
                mUsername.clearFocus();
                mPassword.clearFocus();
            }
            hideKeyboard(this);
        });
    }

    private boolean checkEmpty(String username, String password) {
        if (username.isEmpty()) {
            new XToast<>(this)
                    .setContentView(R.layout.window_hint)
                    .setDuration(1000)
                    .setImageDrawable(android.R.id.icon, R.drawable.icon_error)
                    .setText(R.string.login_username_empty)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setOutsideTouchable(false)
                    .setBackgroundDimAmount(0.5f)
                    .show();
            mLoginButton.showError(3000);
            return true;
        } else if (password.isEmpty()) {
            new XToast<>(this)
                    .setContentView(R.layout.window_hint)
                    .setDuration(1000)
                    .setImageDrawable(android.R.id.icon, R.drawable.icon_error)
                    .setText(R.string.login_password_empty)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setOutsideTouchable(false)
                    .setBackgroundDimAmount(0.5f)
                    .show();
            mLoginButton.showError(3000);
            return true;
        }
        return false;
    }

    private boolean isNumber(String username) {
        boolean isNumber = true;
        for (int i = 0; i < username.length(); i++) {
            if (!Character.isDigit(username.charAt(i))) {
                isNumber = false;
                break;
            }
        }
        return isNumber;
    }

    private void checkCaptcha(String phoneNumber){

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid request
                                    showLoginFailedDialog("Invalid request");
                                } else if (e instanceof FirebaseTooManyRequestsException) {

                                    // The SMS quota for the project has been exceeded
                                    showLoginFailedDialog(e.getMessage());
                                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                                    // reCAPTCHA verification attempted with null Activity
                                    showLoginFailedDialog("reCAPTCHA verification attempted with null Activity");
                                }
                                mLoginButton.showError(3000);
                                new XToast<>(LoginActivity.this)
                                        .setContentView(R.layout.window_hint)
                                        .setDuration(1000)
                                        .setImageDrawable(android.R.id.icon, R.drawable.icon_error)
                                        .setText(R.string.login_fail)
                                        .setAnimStyle(R.style.IOSAnimStyle)
                                        .setOutsideTouchable(false)
                                        .setBackgroundDimAmount(0.5f)
                                        .show();
                            }

                            @Override
                            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                                LoginActivity.this.verificationId = verificationId;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void showLoginFailedDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("login failed");
        builder.setMessage(msg);


        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private static JSONObject sendRequest(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String data = response.body().string();
            return new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Intent navigateToHome = new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Bundle bundle = new Bundle();
                            bundle.putString("user", user.getDisplayName());
                            navigateToHome.putExtras(bundle);
                            mLoginButton.showSucceed();
                            postDelayed(() -> {
                                startActivity(navigateToHome);
                            }, 1000);
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                showLoginFailedDialog("The verification code entered was invalid");
                            }
                        }
                    }
                });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

}