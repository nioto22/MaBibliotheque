package com.aprouxdev.mabibliotheque.ui.authentication;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.CommonUserHelper;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.UserHelper;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NO_LOGIN;

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPasswordActivity";

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView statusTextView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private SharedPreferences preferences;

    private String username;
    private String userMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);
        setupData();
        setupViews();
        setupButtons();
    }

    private void setupData() {
        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
    }


    // ----------------------
    //         SETUP
    // ----------------------

    private void setupViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        statusTextView = findViewById(R.id.emailLoginStatusTextView);
        progressBar = findViewById(R.id.emailLoginProgressBar);
    }

    private void setupButtons() {
        findViewById(R.id.emailLoginSignIn).setOnClickListener(this);
        findViewById(R.id.emailLoginCreateAccount).setOnClickListener(this);
        findViewById(R.id.loginPasswordLostButton).setOnClickListener(this);
    }

    // ----------------------
    //         LOGIC
    // ----------------------

    private void createAccount(String email, String password) {

        resetStatusTextView();
        if (invalidForm()) return;
        showProgressBar();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(EmailPasswordActivity.this, getTheString(R.string.email_login_activity_toast_login_successful), Toast.LENGTH_SHORT).show();
                            userMail = email;
                            addUserToFirestore();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            // Catch Firebase exception
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthUserCollisionException e){
                                Toast.makeText(EmailPasswordActivity.this, getTheString(R.string.email_login_activity_toast_create_account_failure), Toast.LENGTH_SHORT).show();
                                statusTextView.setText(getTheString(R.string.email_login_activity_create_user_fail_user_collision_alert));
                            } catch (Exception e){
                                statusTextView.setText(getTheString(R.string.email_login_activity_create_user_fail_alert));
                                Toast.makeText(EmailPasswordActivity.this, getTheString(R.string.email_login_activity_toast_create_account_failure), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                });
    }

    private void askForUsername(String email, String password) {
        EditText inputEditTextField = new EditText(this);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Nouveau compte")
                .setMessage("Comment voulez-vous qu-on vous appelle ?")
                .setView(inputEditTextField, 50, 8, 50 ,8)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String editTextInput = inputEditTextField.getText().toString();
                        if (!editTextInput.isEmpty()){
                          username = editTextInput;
                          createAccount(email, password);
                        }else {
                            Toast.makeText(EmailPasswordActivity.this, "Veuillez entrer un nom !", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        alertDialog.show();


    }


    private void signIn(String email, String password) {
        resetStatusTextView();
        if (invalidForm()) return;
        showProgressBar();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(EmailPasswordActivity.this, getTheString(R.string.email_login_activity_toast_login_successful), Toast.LENGTH_SHORT).show();
                            userMail = email;
                            addUserToFirestore();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, getTheString(R.string.email_login_activity_toast_sign_in_failure),
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful()) {
                            statusTextView.setText(getTheString(R.string.email_login_activity_status_text_view_connexion_failure));
                        }
                    }
                });
    }


    private void sendPasswordResetEmail() {
        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getTheString(R.string.email_login_activity_required_error));
        } else {
            emailEditText.setError(null);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(EmailPasswordActivity.this, getTheString(R.string.email_login_activity_email_send), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EmailPasswordActivity.this, getTheString(R.string.toast_failure_try_again), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * Add user into firebase with UserHelper createUser method
     * On complete go back to MainActivity
     */
    private void addUserToFirestore() {
        String mUserUid = mAuth.getUid();
        CommonUserHelper.createCommonUser(mUserUid, true, userMail, username, null).commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    hideProgressBar();
                    navigateToMainActivity();
                } else {
                    Log.w(TAG, "Create Common User task not successful: ", task.getException());
                }
            }
        })
        .addOnFailureListener(this.onFailureListener("Create common User"));
    }



    // ----------------------
    //         UI
    // ----------------------

    private void resetStatusTextView() {
        String status = statusTextView.getText().toString();
        if (!TextUtils.isEmpty(status)){
            statusTextView.setText("");
        }
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    // ----------------------
    //         TOOLS
    // ----------------------

    private boolean invalidForm() {
        boolean valid = false;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getTheString(R.string.email_login_activity_required_error));
            valid = true;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getTheString(R.string.email_login_activity_required_error));
            valid = true;
        } else {
            passwordEditText.setError(null);
        }
        return valid;
    }

    private String getTheString(int resource){
        return getResources().getString(resource);
    }


    // --------------------------
    //    CLICKS AND NAVIGATION
    // --------------------------

    private void navigateToMainActivity() {
        startActivity(new Intent(EmailPasswordActivity.this, MainActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.emailLoginSignIn):
                signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
                break;
            case (R.id.emailLoginCreateAccount):
                askForUsername(emailEditText.getText().toString(), passwordEditText.getText().toString());
                break;
            case (R.id.loginPasswordLostButton):
                sendPasswordResetEmail();
                break;

        }
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(String task){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_unknown_error), Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: Fail on " + task, e);
            }
        };
    }

}
