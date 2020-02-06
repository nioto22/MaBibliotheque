package com.aprouxdev.mabibliotheque.ui.bookDetail;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.models.DetailedBook;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static com.aprouxdev.mabibliotheque.ui.main.addBook.AddBookFragment.BUNDLE_EXTRA_BOOK;

public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "BookDetailActivity";

    // UI VARS
        // Top container
        ImageView bookDetailImage;
        TextView bookDetailTitle;
        TextView bookDetailAuthor;
        TextView bookDetailReadDate;
        ImageView star1, star2, star3, star4, star5;
        ImageButton editPrimaryInfoButton;
        // Info container
        Button informationButton;
        ImageView informationArrow;
        // Description container
        Button descriptionButton;
        ImageView descriptionArrow;
        RelativeLayout descriptionContentLayout;
        TextView descriptionTextView;
        // Comment container
        Button commentButton;
        ImageView commentArrow;

    // Data Vars
    Book book;
    List<ImageView> markStars;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        setupViews();
        setupActionBar();
        book = getCurrentBook();
        //TODO CHeck if book not null
        setupData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_detail_menu, menu);
        return true;
    }

    private void setupData() {
        setupBookPrimaryInformation();
        setupBookSecondaryInformation();
        setupBookDescription();
        setupBookComments();


    }

    private  void setupBookPrimaryInformation() {
        editPrimaryInfoButton.setOnClickListener(this);
        bookDetailTitle.setText(book.getTitle());
        if(book.getThumbnailLink() != null){
            Picasso picasso = Picasso.get();
            picasso.load(book.getThumbnailLink())
                    .placeholder(R.drawable.book_placeholder)
                    .into(bookDetailImage);
        }
        String author = (book.getAuthor() != null) ? "De " + book.getAuthor(): "";
        bookDetailAuthor.setText(author);

        String readDate;
        if (book.getHasBeenRead() != null && book.getHasBeenRead()){
            readDate = (book.getReadTimestamp() != null) ? "Lu" + book.getReadTimestamp() : "Lu";
        } else {
            readDate = "Pas encore lu";
        }
        bookDetailReadDate.setText(readDate);

        int numberOfStars = book.getMark();
        markStars = Arrays.asList(star1, star2, star3, star4, star5);
        for (int i = 0; i < numberOfStars ; i++) {
            markStars.get(i).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_full_star));
        }
    }

    private void setupBookSecondaryInformation() {
        informationButton.setOnClickListener(this);
    }

    private void setupBookDescription() {
        descriptionButton.setOnClickListener(this);
        String descriptionString = (book.getDescription() != null) ? book.getDescription() : "Aucune description";
        descriptionTextView.setText(descriptionString);
    }

    private void setupBookComments() {
        commentButton.setOnClickListener(this);
    }


    private Book getCurrentBook() {
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(BUNDLE_EXTRA_BOOK))
                book = (Book) intent.getSerializableExtra(BUNDLE_EXTRA_BOOK);
        }
        return book;
    }

    private void setupViews() {
        // Top container
        editPrimaryInfoButton = findViewById(R.id.editPrimaryInfoButton);
        bookDetailImage = findViewById(R.id.bookDetailImage);
        bookDetailTitle = findViewById(R.id.bookDetailTitle);
        bookDetailAuthor = findViewById(R.id.bookDetailAuthor);
        bookDetailReadDate = findViewById(R.id.bookDetailReadDate);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
        // Info container
        informationButton = findViewById(R.id.informationButton);
        informationArrow = findViewById(R.id.informationArrow);
        // Description container
        descriptionButton = findViewById(R.id.descriptionButton);
        descriptionArrow = findViewById(R.id.descriptionArrow);
        descriptionContentLayout = findViewById(R.id.descriptionContentLayout);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        // Comment container
        commentButton = findViewById(R.id.commentButton);
        commentArrow = findViewById(R.id.commentArrow);
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.editPrimaryInfoButton):
                editPrimaryInfo();
                break;
            case (R.id.informationButton):

                break;
            case (R.id.descriptionButton):
                arrowRotation(descriptionArrow);
                int visibility = descriptionContentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                descriptionContentLayout.setVisibility(visibility);
                break;
            case (R.id.commentButton):
                break;
        }
    }

    private void editPrimaryInfo() {
    }

    private void arrowRotation(ImageView arrow){
        Animation animation = AnimationUtils.loadAnimation(arrow.getContext(), R.anim.rotate_around_center_point);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Integer integer = (Integer) arrow.getTag();
                integer = integer == null ? R.drawable.ic_arrow_down : integer;
                int newArrow = (integer == R.drawable.ic_arrow_up) ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_up;
                arrow.setImageDrawable(ContextCompat.getDrawable(BookDetailActivity.this, newArrow));
                arrow.setTag(newArrow);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        arrow.startAnimation(animation);
    }
}
