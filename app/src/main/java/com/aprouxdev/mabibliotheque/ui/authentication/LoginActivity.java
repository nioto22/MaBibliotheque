package com.aprouxdev.mabibliotheque.ui.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.ui.main.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NO_LOGIN;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "LoginActivity";

    // UI Vars
    private Button loginButton;
    private Button cancelButton;

    // Data
    private SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        setupViews();
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
        preferences.edit().putBoolean(SHARED_PREF_NO_LOGIN, true).apply();
        Intent intentToMainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intentToMainActivity);
    }


}
