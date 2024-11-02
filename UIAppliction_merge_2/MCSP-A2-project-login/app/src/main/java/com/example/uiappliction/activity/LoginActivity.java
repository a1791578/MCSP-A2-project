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

import com.example.uiappliction.Database.PersonDao;
import com.example.uiappliction.Database.PersonDatabase;
import com.example.uiappliction.Entity.Person;
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
        //去掉标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 设置语言为英语
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
        //跳转到Main时，清空Activity堆栈
        Intent navigateToHome = new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //判断是否有传入的Bundle数据
        if (initIntent.getExtras() != null) {
            //获取Bundle数据
            Bundle bundle = initIntent.getExtras();
            //获取Bundle中的数据
            Person user = (Person) bundle.getSerializable("user");
            //判断是否有传入的用户数据
            if (user != null) {
                //将用户数据显示在界面上
                mUsername.setText(user.username);
                mPassword.setText(user.password);
            }
        }

        //获取数据库
        PersonDatabase personDatabase = PersonDatabase.getDatabase(this);
        PersonDao personDao = personDatabase.getPersonDao();
        //登录按钮监听器
        mLoginButton.setOnClickListener(v -> {
            String username = this.mUsername.getText().toString();
            String phone = this.mPassword.getText().toString();
            String code = this.mCode.getText().toString();
//            Log.d("Login", "username: " + username + " phone: " + phone);
            mLoginButton.showProgress();
            //检测用户名密码是否为空
            if (checkEmpty(username, phone)) return;
            //检测用户名是否为纯数字
            boolean isNumber = isNumber(username);
            verifyCode(code);
            //查询数据库
//            if (checkDataBase(username, password, personDao)) {
//                mLoginButton.showSucceed();
////                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
//                //跳转到主界面
//                postDelayed(() -> {
//                    //查询该用户
//                    Person user = personDao.queryPerson(username);
//                    // 在用户登录成功后保存用户信息到 SharedPreferences
//                    SharedPreferences preferences = getSharedPreferences("UserLoginInfo", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString("username", username);  // 保存用户名
//                    editor.putString("gender", String.valueOf(user.gender));    // 假设 user.email 是用户的电子邮件
//                    // 可以根据需要保存其他用户信息
//                    editor.apply();
//                    //将用户数据传入Bundle
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("user", user);
//                    //将Bundle数据传入Intent
//                    navigateToHome.putExtras(bundle);
//                    startActivity(navigateToHome);
//                }, 1000);
//            } else {
//                mLoginButton.showError(3000);
//                new XToast<>(this)
//                        .setContentView(R.layout.window_hint)
//                        .setDuration(1000)
//                        .setImageDrawable(android.R.id.icon, R.drawable.icon_error)
//                        .setText(R.string.login_fail)
//                        //设置动画效果
//                        .setAnimStyle(R.style.IOSAnimStyle)
//                        // 设置外层是否能被触摸
//                        .setOutsideTouchable(false)
//                        // 设置窗口背景阴影强度
//                        .setBackgroundDimAmount(0.5f)
//                        .show();
//            }
        });

        mSendCode.setOnClickListener(v -> {
            String username = this.mUsername.getText().toString();
            String phone = this.mPassword.getText().toString();
            if (checkEmpty(username, phone)) return;
            checkCaptcha(phone);
        });

        //注册按钮监听器
        mSignUpButton.setOnClickListener(v -> {
            //跳转到注册界面
            startActivity(navigateToSignUp);
        });
        //点击到img则收起键盘
        findViewById(R.id.imageView_bg).setOnClickListener(v -> {
            //检测是否有焦点
            if (mUsername.isFocused() || mPassword.isFocused()) {
                //清除焦点
                mUsername.clearFocus();
                mPassword.clearFocus();
            }
            //收起键盘
            hideKeyboard(this);
        });
    }

    private boolean checkDataBase(String username, String password, PersonDao personDao) {
        if (personDao.checkLogin(username, password) != null
                || isNumber(username) && personDao.checkLoginByPhoneNumber(Long.parseLong(username), password) != null) {
            return true;
        }
        return false;
    }

    private boolean checkEmpty(String username, String password) {
        //判断是否为空
        if (username.isEmpty()) {
            new XToast<>(this)
                    .setContentView(R.layout.window_hint)
                    .setDuration(1000)
                    .setImageDrawable(android.R.id.icon, R.drawable.icon_error)
                    .setText(R.string.login_username_empty)
                    //设置动画效果
                    .setAnimStyle(R.style.IOSAnimStyle)
                    // 设置外层是否能被触摸
                    .setOutsideTouchable(false)
                    // 设置窗口背景阴影强度
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
                    //设置动画效果
                    .setAnimStyle(R.style.IOSAnimStyle)
                    // 设置外层是否能被触摸
                    .setOutsideTouchable(false)
                    // 设置窗口背景阴影强度
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
                                    showLoginFailedDialog("The SMS quota for the project has been exceeded");
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
                                        //设置动画效果
                                        .setAnimStyle(R.style.IOSAnimStyle)
                                        // 设置外层是否能被触摸
                                        .setOutsideTouchable(false)
                                        // 设置窗口背景阴影强度
                                        .setBackgroundDimAmount(0.5f)
                                        .show();
                            }

                            @Override
                            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                                // 保存验证码的 verificationId，用于后续验证用户输入的验证码
                                LoginActivity.this.verificationId = verificationId;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void showLoginFailedDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("login failed"); // 设置标题
        builder.setMessage(msg); // 设置内容

        // 设置"确定"按钮
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss(); // 关闭弹窗
        });

        // 创建并显示弹窗
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
        // 使用保存的 verificationId 和用户输入的验证码生成 PhoneAuthCredential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential); // 使用生成的凭证登录
    }

}