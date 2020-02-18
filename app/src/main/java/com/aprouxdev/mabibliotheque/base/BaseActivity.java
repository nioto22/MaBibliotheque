package com.aprouxdev.mabibliotheque.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.aprouxdev.mabibliotheque.ui.authentication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NO_LOGIN;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";


    protected SharedPreferences bPreferences;
    protected FirebaseAuth bAuth;
    protected FirebaseUser bUser;
    protected String bUserUid;

    public boolean bIsUserPrefNoLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(this.getFragmentLayout());

        setupBaseData();
    }

    // Abstract Methods
    public abstract int getFragmentLayout();

    private void setupBaseData(){
        bPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        bAuth = FirebaseAuth.getInstance();
        isUserSignIn();
        // TODO Put base vars on SharedPref
    }

    /**
     * If user is not authentified and No_SIGN_PREF is not true
     *  Go to login Activity
     */
    private void isUserSignIn() {
        bUser = bAuth.getCurrentUser();
        bIsUserPrefNoLogin = bPreferences.getBoolean(SHARED_PREF_NO_LOGIN, false);
        if (bUser == null && !bIsUserPrefNoLogin){
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (bUser != null){
            bUserUid = bUser.getUid();
        }
    }



    protected String getTheString(int resource){
        return getResources().getString(resource);
    }

}
