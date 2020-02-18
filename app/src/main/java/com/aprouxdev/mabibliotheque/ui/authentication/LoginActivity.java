package com.aprouxdev.mabibliotheque.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.main.MainActivity;

import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NO_LOGIN;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "LoginActivity";

    // UI Vars
    private Button loginButton;
    private Button cancelButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
    }

    @Override
    public int getFragmentLayout() {
        return (R.layout.activity_login);
    }


    private void setupViews() {
        loginButton = findViewById(R.id.loginLoginButton);
        loginButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.loginCancelButton);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.loginLoginButton):
                startSignInActivity();
                break;
            case (R.id.loginCancelButton):
                skipSignInActivity();
                break;
        }

    }

    private void startSignInActivity() {
        Intent intentToEmailLogin = new Intent(LoginActivity.this, EmailPasswordActivity.class);
        startActivity(intentToEmailLogin);
    }

    private void skipSignInActivity() {
        bPreferences.edit().putBoolean(SHARED_PREF_NO_LOGIN, true).apply();
        Intent intentToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intentToMainActivity);
    }


}
