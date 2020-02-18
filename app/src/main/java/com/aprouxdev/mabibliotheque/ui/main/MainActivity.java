package com.aprouxdev.mabibliotheque.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.addCapturedLibrary.AddLibraryActivity;
import com.aprouxdev.mabibliotheque.ui.authentication.LoginActivity;
import com.aprouxdev.mabibliotheque.ui.main.home.HomeFragment;
import com.aprouxdev.mabibliotheque.viewmodels.LocalBookViewModel;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import static com.aprouxdev.mabibliotheque.util.Constants.ADD_LIBRARY_DRAWER_ITEM_INDEX;
import static com.aprouxdev.mabibliotheque.util.Constants.ADD_LIBRARY_INTENT_FOR_RESULT;
import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NO_LOGIN;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MainActivity";
    public static final String BUNDLE_IS_USER_PREF_NO_LOGIN = "BUNDLE_IS_USER_PREF_NO_LOGIN" ;
    public static final String BUNDLE_USER_UID = "BUNDLE_USER_UID" ;

    // UI Vars
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        initNavigation();


        
    }


    @Override
    public int getFragmentLayout() {
        return (R.layout.activity_main);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_LIBRARY_INTENT_FOR_RESULT){
            navigationView.getMenu().getItem(ADD_LIBRARY_DRAWER_ITEM_INDEX).setChecked(false);
        }
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
        bPreferences.edit().putBoolean(SHARED_PREF_NO_LOGIN, false).apply();
        goToLoginActivity();
    }

    private void deleteAccountAsked() {
        AlertDialog.Builder alertDeletAccount = new AlertDialog.Builder(this);
        alertDeletAccount.setTitle("Attention !!")
                .setMessage(getTheString(R.string.main_activity_delete_account_alert_title))
                .setPositiveButton(getTheString(R.string.yes_maj), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFirebaseAccount();
                    }
                })
                .setNegativeButton(getTheString(R.string.no_maj), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void deleteFirebaseAccount() {
        Log.d(TAG, "deleteFirebaseAccount: " + bUser.getEmail());
        bUser.delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, getTheString(R.string.main_activity_toast_account_deleted), Toast.LENGTH_SHORT).show();
                        goToLoginActivity();
                    } else {
                        Toast.makeText(this, getTheString(R.string.toast_failure_try_again), Toast.LENGTH_SHORT).show();
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
                if (!bIsUserPrefNoLogin) deleteAccountAsked();
                else Toast.makeText(this, getTheString(R.string.main_activity_toast_no_account), Toast.LENGTH_SHORT).show();
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
