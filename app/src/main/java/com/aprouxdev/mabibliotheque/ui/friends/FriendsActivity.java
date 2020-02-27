package com.aprouxdev.mabibliotheque.ui.friends;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.ui.friends.chat.ChatFragment;
import com.aprouxdev.mabibliotheque.ui.friends.friends.FriendsFragment;
import com.aprouxdev.mabibliotheque.ui.friends.home.FriendsHomeFragment;
import com.aprouxdev.mabibliotheque.ui.main.MainActivity;

import static com.aprouxdev.mabibliotheque.util.Constants.FRIENDS_CHAT_TAB_TAG;
import static com.aprouxdev.mabibliotheque.util.Constants.FRIENDS_FRIENDS_TAB_TAG;
import static com.aprouxdev.mabibliotheque.util.Constants.FRIENDS_HOME_TAB_TAG;

public class FriendsActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "FriendsActivity";

    // UI VARS
    // Top Container
    private TextView friendsProfileUserNameTV;
    private ImageView friendsProfileImageIV;
    private ImageButton friendsProfileImageChangeButton;
    private TextView friendsTopContainerFriendsNumber;
    private TextView friendsTopContainerMessagesNumber;
    // Tabs Container
    private RelativeLayout friendsTabHomeButtonRL;
    private ImageView friendsTabHomeIV;
    private View friendsTabStrokeView;
    private RelativeLayout friendsTabFriendsButtonRL;
    private ImageView friendsTabFriendsIV;
    private RelativeLayout friendsTabChatButtonRL;
    private ImageView friendsTabChatIV;

    // Data Vars
    private int currentTab = 0;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
        setupListeners();
        setupActionBar();
        setupFragment();
    }



    private void setupFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        FriendsHomeFragment homeFragment = new FriendsHomeFragment();
        fragmentTransaction.add(R.id.friendsFragment, homeFragment);
        fragmentTransaction.commit();
    }

    @Override
    public int getFragmentLayout() {
        return (R.layout.activity_friends);
    }



    // ------------------------
    //     ACTION  NAVIGATION
    // ------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            // back pressed save only new Book or any Change
            startActivity(new Intent(FriendsActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.friendsProfileImageChangeButton):{
                Toast.makeText(this, "Profile button clicked", Toast.LENGTH_SHORT).show();
                break;
            }
            case (R.id.friendsTabHomeButtonRL):{
                tabButtonClicked(FRIENDS_HOME_TAB_TAG);
                break;
            }
            case (R.id.friendsTabFriendsButtonRL):{
                tabButtonClicked(FRIENDS_FRIENDS_TAB_TAG);
                break;
            }
            case (R.id.friendsTabChatButtonRL):{
                tabButtonClicked(FRIENDS_CHAT_TAB_TAG);
                break;
            }
        }
    }

    /**
     * If it's a new tab clicked :
     * Change tab colors with animation
     * Change fragment
     * @param tag int Tag of the tab clicked
     */
    private void tabButtonClicked(int tag) {
        if (tag != currentTab){

            ImageView previousTabImage = getImageFromTag(currentTab);
            ImageView newTabImage = getImageFromTag(tag);

            tabDrawableTransition(previousTabImage);
            tabDrawableTransition(newTabImage);

            animateTabUnderline(tag);

            changeFragment(tag);

            currentTab = tag;
        }
    }

    private void changeFragment(int tag) {
        Fragment newFragment = tag == 0 ? new FriendsHomeFragment() : tag == 1 ? new FriendsFragment() : new ChatFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        // Set fragment direction way
        int enterTransition = (tag < currentTab) ? R.anim.enter_from_right : R.anim.enter_from_left;
        int exitTransition = (tag < currentTab) ? R.anim.exit_to_left : R.anim.exit_to_right;
        // Custom transition animations
        fragmentTransaction.setCustomAnimations(enterTransition,exitTransition);
        fragmentTransaction.replace(R.id.friendsFragment, newFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void animateTabUnderline(int tag) {
        int translationX = tag * friendsTabStrokeView.getWidth();
        friendsTabStrokeView.animate()
                .translationX(translationX)
                .setDuration(500);
    }

    private ImageView getImageFromTag(int tag){
        return (tag == 0) ? friendsTabHomeIV : tag == 1 ? friendsTabFriendsIV : friendsTabChatIV;
    }
    private void tabDrawableTransition(ImageView image){
        TransitionDrawable transitionTab = (TransitionDrawable) image.getBackground();
        transitionTab.reverseTransition(500);

    }

    // ------------------------
    //     SETUP VIEWS
    // ------------------------
    private void setupViews() {
        // TOP CONTAINER
            // PROFILE
        friendsProfileUserNameTV = findViewById(R.id.friendsProfileUserNameTV);
        friendsProfileImageIV = findViewById(R.id.friendsProfileImageIV);
        friendsProfileImageChangeButton = findViewById(R.id.friendsProfileImageChangeButton);
            // FRIENDS INFORMATION
        friendsTopContainerFriendsNumber = findViewById(R.id.friendsTopContainerFriendsNumber);
        friendsTopContainerMessagesNumber = findViewById(R.id.friendsTopContainerMessagesNumber);
        // TAB MENU
            // Home
        friendsTabHomeButtonRL = findViewById(R.id.friendsTabHomeButtonRL);
        friendsTabHomeIV = findViewById(R.id.friendsTabHomeIV);
        friendsTabStrokeView = findViewById(R.id.friendsTabHomeStrokeView);
        Display display = getWindowManager().getDefaultDisplay();
        friendsTabStrokeView.getLayoutParams().width = display.getWidth() / 3;
            // Friends
        friendsTabFriendsButtonRL = findViewById(R.id.friendsTabFriendsButtonRL);
        friendsTabFriendsIV = findViewById(R.id.friendsTabFriendsIV);
            // Chat
        friendsTabChatButtonRL = findViewById(R.id.friendsTabChatButtonRL);
        friendsTabChatIV = findViewById(R.id.friendsTabChatIV);
    }
    private void setupListeners(){
        friendsProfileImageChangeButton.setOnClickListener(this);
        friendsTabHomeButtonRL.setOnClickListener(this);
        friendsTabFriendsButtonRL.setOnClickListener(this);
        friendsTabChatButtonRL.setOnClickListener(this);
    }
}
