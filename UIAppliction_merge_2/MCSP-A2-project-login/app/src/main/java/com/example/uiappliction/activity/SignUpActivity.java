package com.example.uiappliction.activity;

import static com.example.uiappliction.Utils.KeyboardUtils.hideKeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappliction.R;
import com.example.uiappliction.Utils.BaseDialog;
import com.example.uiappliction.Utils.SubmitButton;
import com.example.uiappliction.Utils.SwitchButton;
import com.example.uiappliction.action.HandlerAction;
import com.hjq.xtoast.XToast;


public class SignUpActivity extends AppCompatActivity
        implements HandlerAction {
    EditText mUsername, mPhoneNumber;
    SubmitButton mSignUpButton;
    SwitchButton mGender;
    private String TAG = "SignUpActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_signup);

        mUsername = findViewById(R.id.textView_username);
//        mPassword = findViewById(R.id.textView_password);
        mPhoneNumber = findViewById(R.id.textView_phoneNumber);
        mGender = findViewById(R.id.SwitchButton_gender);
        mSignUpButton = findViewById(R.id.btn_signup);

        Intent navigateToLogin = new Intent(this, LoginActivity.class);

        mPhoneNumber.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        final int[] isFemale = {0};


        mGender.setOnClickListener(v -> {
            if (mGender.isChecked())
                isFemale[0] = 1;
            else
                isFemale[0] = 0;
        });

        findViewById(R.id.btn_signup).setOnClickListener(v -> {
            mSignUpButton.showProgress();
            String username = this.mUsername.getText().toString();
//            String password = this.mPassword.getText().toString();
            String phoneNumber = this.mPhoneNumber.getText().toString();

            if (checkEmpty(username, phoneNumber)) {

                mSignUpButton.showSucceed();
//                                    Toast.makeText(SignUpActivity.this, getRString(R.string.register_success), Toast.LENGTH_SHORT).show();
                postDelayed(() -> {

                    Bundle bundle = new Bundle();
                    navigateToLogin.putExtras(bundle);
                    startActivity(navigateToLogin);
                    finish();
                }, 2000);

            }
        });

        findViewById(R.id.imageView_bg).setOnClickListener(v -> {
            if (mUsername.isFocused()) {
                mUsername.clearFocus();
            }
            hideKeyboard(this);
        });
    }


    private boolean checkEmpty(String username, String phoneNumber) {
        if (username.isEmpty()) {
            newErrorXToast(R.string.register_username_empty);
            mSignUpButton.showError(2000);
        } else if (phoneNumber.isEmpty()) {
            newErrorXToast(R.string.register_phone_empty);
            mSignUpButton.showError(2000);
        } else {
            return true;
        }
        return false;
    }

    private String getRString(@StringRes int id) {
        return getResources().getString(id);
    }

    private void newErrorXToast(@StringRes int id) {
        new XToast<>(this)
                .setContentView(R.layout.window_hint)
                .setDuration(1000)
                .setImageDrawable(android.R.id.icon, R.drawable.icon_error)
                .setText(getRString(id))
                .setAnimStyle(R.style.IOSAnimStyle)
                .setOutsideTouchable(false)
                .setBackgroundDimAmount(0.5f)
                .show();
    }

}
