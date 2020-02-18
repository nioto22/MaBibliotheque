package com.aprouxdev.mabibliotheque.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.ui.addCapturedLibrary.AddLibraryActivity;
import com.aprouxdev.mabibliotheque.ui.authentication.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import static com.aprouxdev.mabibliotheque.util.Constants.ADD_LIBRARY_DRAWER_ITEM_INDEX;
import static com.aprouxdev.mabibliotheque.util.Constants.ADD_LIBRARY_INTENT_FOR_RESULT;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NO_LOGIN;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MainActivity";

    // UI Vars
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Firebase Vars
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Data
    public SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        isUserSigned();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        initNavigation();
        initPreferences();


    }

    /**
     * If user is not authentified and No_SIGN_PREF is not true
     *  Go to login Activity
     */
    private void isUserSigned() {
        currentUser = mAuth.getCurrentUser();
        boolean noSignInPref = preferences.getBoolean(SHARED_PREF_NO_LOGIN, false);
        if (currentUser == null && !noSignInPref){
            goToLoginActivity();
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_LIBRARY_INTENT_FOR_RESULT){
           navigationView.getMenu().getItem(ADD_LIBRARY_DRAWER_ITEM_INDEX).setChecked(false);
        }
    }

    private void initPreferences() {
        preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
    }

    private void initNavigation(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // ----------------------
    //      PROFILE METHODS
    // ----------------------

    /**
     * Logout : reset pref_no_login to false, go back to login Activity
     */
    private void logout() {
        preferences.edit().putBoolean(SHARED_PREF_NO_LOGIN, false).apply();
        goToLoginActivity();
    }

    private void deleteAccountAsked() {
        AlertDialog.Builder alertDeletAccount = new AlertDialog.Builder(this);
        alertDeletAccount.setTitle("Attention !!")
                .setMessage("Êtes-vous sûr de vouloir supprimer définitivement votre compte et perdre toute votre bibliothèque ?")
                .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFirebaseAccount();
                    }
                })
                .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void deleteFirebaseAccount() {
        Log.d(TAG, "deleteFirebaseAccount: " + currentUser.getEmail());
        currentUser.delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Compte supprimé", Toast.LENGTH_SHORT).show();
                        goToLoginActivity();
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.toast_failure_try_again), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "deleteFirebaseAccount: ", task.getException());
                    }
                });
    }


    // ------------------------
    //    CLICKS & NAVIGATION
    // ------------------------


    private void goToLoginActivity(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                else {
                    return false;
                }
            }
            // MAIN MENU
            case R.id.mainMenuProfile: {
                return true;
            }
            case R.id.mainMenuLogout: {
                logout();
                return true;
            }
            case R.id.mainMenuDeleteAccount: {
                deleteAccountAsked();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.homeScreen:{
                // BackStack issue :
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.main, true)
                        .build();

                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.homeScreen,
                                null,
                                navOptions
                        );
                break;
            }
            case R.id.nav_library:{
                if(isValidDestination(R.id.libraryScreen)){
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.libraryScreen);
                }
                break;
            }
            case R.id.nav_add_book:{
                if(isValidDestination(R.id.addBookScreen)){
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.addBookScreen);
                }
                break;
            }
            case R.id.nav_add_library:{
                startActivityForResult(new Intent(this, AddLibraryActivity.class), ADD_LIBRARY_INTENT_FOR_RESULT);

                break;
            }
        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void seeAllButtonClicked(View v){
        switch (v.getId()){
            case (R.id.topSeeAllButton):
            case (R.id.homeLastEntriesSeeAllButton):
                Log.d(TAG, "seeAllButtonClicked: ");
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.libraryScreen);
                break;
            case (R.id.homeToReadSeeAllButton):
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.libraryScreen);
                // TODO intent library with toRead temp filter
                break;
            case (R.id.homeFavoriteSeeAllButton):
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.libraryScreen);
                // TODO intent library with favorite temp filter
                break;
            case (R.id.homeNoBooksAddBookButton):
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.addBookScreen);
                break;
        }
    }

    private boolean isValidDestination(int destination){
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

}
