package com.aprouxdev.mabibliotheque.ui.friends;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.base.BaseActivity;
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

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
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

        // 1 get the previous tab
        ImageView previousTabImage = currentTab == 0 ? friendsTabHomeIV : currentTab == 1 ? friendsTabFriendsIV : friendsTabChatIV;
        View stroke = friendsTabStrokeView;
        // 2 get the clicked tab
        ImageView newTabImage = tag == 0 ? friendsTabHomeIV : tag == 1 ? friendsTabFriendsIV : friendsTabChatIV;

        // 3 is a new tab clicked
        if (tag != currentTab){
            // 4 ImageViews change drawable
            TransitionDrawable transitionPreviousTab = (TransitionDrawable) previousTabImage.getBackground();
            TransitionDrawable transitionNewTab = (TransitionDrawable) newTabImage.getBackground();
            transitionPreviousTab.reverseTransition(500);
            transitionNewTab.reverseTransition(500);

            // 5 Set stroke direction way
            int enterTransition = (tag < currentTab) ? R.anim.enter_from_right : R.anim.enter_from_left;
            int exitTransition = (tag < currentTab) ? R.anim.exit_to_left : R.anim.exit_to_right;
//                    FriendsHomeFragment homeFragment = new FriendsHomeFragment();
//            fragmentTransaction.add(R.id.friendsFragment, homeFragment);
//            fragmentTransaction.commit();

            // 6 Stroke animation
            int translationX = tag * stroke.getWidth();
            Log.d(TAG, "tabButtonClicked: translation = " + translationX);
            stroke.animate()
                    .translationX(translationX)
                    .setDuration(500);
            // 7 change fragment
            Fragment newFragment = tag == 0 ? new FriendsHomeFragment() : tag == 1 ? new FriendsFragment() : new ChatFragment();
            fragmentTransaction = fragmentManager.beginTransaction();
            // Custom transition animations
            fragmentTransaction.setCustomAnimations(enterTransition,exitTransition);   // R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right
            fragmentTransaction.replace(R.id.friendsFragment, newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            // 7 update current tab
            currentTab = tag;

        }




        // 4 change drawables and start animation

        // 5 change fragment


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
