package com.aprouxdev.mabibliotheque.ui.main;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.viewmodels.BookViewModel;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import static com.aprouxdev.mabibliotheque.util.Constants.SHARED_PREF_NAME;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    public SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        initNavigation();
        initPreferences();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout: {
                return true;
            }
            case android.R.id.home:{
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                else {
                    return false;
                }
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
                if(isValidDestination(R.id.addLibraryScreen)){
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.addLibraryScreen);
                }
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
            case (R.id.homeNoBooksAddLibraryButton):
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.addLibraryScreen);
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
