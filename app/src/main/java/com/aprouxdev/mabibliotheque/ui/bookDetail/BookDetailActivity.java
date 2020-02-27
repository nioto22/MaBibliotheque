package com.aprouxdev.mabibliotheque.ui.bookDetail;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.ui.base.BaseActivity;
import com.aprouxdev.mabibliotheque.database.firestoreDatabase.LibraryHelper;
import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.ui.main.MainActivity;
import com.aprouxdev.mabibliotheque.viewmodels.LocalBookViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_BOOK;
import static com.aprouxdev.mabibliotheque.util.Constants.BUNDLE_EXTRA_IS_NEW_BOOK;
import static com.aprouxdev.mabibliotheque.util.Constants.months;

public class BookDetailActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "BookDetailActivity";

    // UI VARS
    // Top container
    ImageView bookDetailImage;
    TextView bookDetailTitle;
    TextView bookDetailAuthor;
    TextView bookDetailReadDate;
    ImageView star1, star2, star3, star4, star5;
    ImageButton editPrimaryInfoButton;
    // Edit Top container
    LinearLayout editTopContainerLayout;
    EditText bookDetailTitleEditText;
    EditText bookDetailAuthorEditText;
    TextView editReadStateTextView;
    Switch editReadStateSwitch;
    ImageButton star1Button, star2Button, star3Button, star4Button, star5Button;
    // Info container
    Button informationButton;
    ImageView informationArrow;
    ImageButton editInformationContent;
    RelativeLayout informationContentLayout;
    TextView categoryTextView;
    EditText categoryEditText;
    TextView pagesTextView;
    EditText pagesEditText;
    TextView loanTextView;
    EditText loanEditText;
    // Description container
    Button descriptionButton;
    ImageView descriptionArrow;
    ImageButton editDescriptionContent;
    RelativeLayout descriptionContentLayout;
    TextView descriptionTextView;
    EditText descriptionEditText;
    // Comment container
    Button commentButton;
    ImageView commentArrow;
    ImageButton editCommentContent;
    RelativeLayout commentContentLayout;
    TextView commentTextView;
    EditText commentEditText;

    // Data Vars
    private int numberOfEditableOpen;
    private Book oldBook;
    private Book book;
    private LocalBookViewModel viewModel;
    private int numberOfStars;
    private boolean isANewBook;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oldBook = getCurrentBook();
        try {
            book = (Book) oldBook.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        viewModel = ViewModelProviders.of(this).get(LocalBookViewModel.class);

        setupViews();
        setupActionBar();
        isItANewBook();

        setupData();
    }

    @Override
    public int getFragmentLayout() {
        return (R.layout.activity_book_detail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_detail_menu, menu);
        return true;
    }

    // --------------------
    //        DATA
    // --------------------

    private void saveCurrentBook() {
        if (bIsUserPrefNoLogin) {
            viewModel.insert(book);
            Toast.makeText(this, "Livre sauvegardé", Toast.LENGTH_SHORT).show();
        }
        else LibraryHelper.addBook(bUserUid, book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "Task successful: Book added");
                    Toast.makeText(getApplicationContext(), "Livre sauvegardé", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "Task not successful : ", task.getException());
                }

            }
        });


    }

    private boolean bookHasCHanged(){
           return !book.getTitle().equals(oldBook.getTitle())
                   || !Objects.equals(book.getAuthor(), oldBook.getAuthor())
                   || !Objects.equals(book.getDescription(), oldBook.getDescription())
                   || !Objects.equals(book.getCategory(), oldBook.getCategory())
                   || !Objects.equals(book.getReadTimestamp(), oldBook.getReadTimestamp())
                   || !Objects.equals(book.getComment(), oldBook.getComment())
                   || !Objects.equals(book.getLoan(), oldBook.getLoan())
                   || book.getPageCount() != oldBook.getPageCount()
                   || book.getMark() != oldBook.getMark();
    }

    private void isItANewBook() {
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(BUNDLE_EXTRA_IS_NEW_BOOK)) isANewBook = intent.getBooleanExtra(BUNDLE_EXTRA_IS_NEW_BOOK, false);
        }
    }

    // --------------------
    //       SETUP DATA
    // --------------------


    private void setupData() {
        numberOfEditableOpen = 0;
        setupBookPrimaryInformation();
        setupBookSecondaryInformation();
        setupBookDescription();
        setupBookComments();
        book.getId();
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
        String author = (book.getAuthor() != null) ? book.getAuthor(): "Auteur non renseigné";
        bookDetailAuthor.setText(author);

        String readDate;
        if (book.getHasBeenRead() != null && book.getHasBeenRead()){
            readDate = (book.getReadTimestamp() != null) ? book.getReadTimestamp() : "Lu";
        } else {
            readDate = getResources().getString(R.string.not_read_state);
        }
        bookDetailReadDate.setText(readDate);

        numberOfStars = book.getMark();
        setupMarkStarsDrawable(numberOfStars);
    }

    private void setupBookSecondaryInformation() {
        informationButton.setOnClickListener(this);
        String categoryString = (book.getCategory() != null) ? book.getCategory() : getResources().getString(R.string.not_specified);
        String pageCountString = (book.getPageCount() != 0) ? Integer.toString(book.getPageCount()) : getResources().getString(R.string.not_specified);
        String loanString = (book.getLoan() != null) ? book.getLoan() : getResources().getString(R.string.not_specified);
        categoryTextView.setText(categoryString);
        pagesTextView.setText(pageCountString);
        loanTextView.setText(loanString);
    }

    private void setupBookDescription() {
        descriptionButton.setOnClickListener(this);
        String descriptionString = (book.getDescription() != null) ? book.getDescription() : getResources().getString(R.string.not_specified);
        descriptionTextView.setText(descriptionString);
    }

    private void setupBookComments() {
        commentButton.setOnClickListener(this);
        String commentString = (book.getComment() != null) ? book.getComment() : getResources().getString(R.string.not_specified);
        commentTextView.setText(commentString);
    }


    private Book getCurrentBook() {
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(BUNDLE_EXTRA_BOOK))
                book = (Book) intent.getSerializableExtra(BUNDLE_EXTRA_BOOK);
        }
        return book;
    }

    // --------------------
    //       SETUP VIEWS
    // --------------------


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
        // Edit Top Container
        editTopContainerLayout = findViewById(R.id.editTopContainerLayout);
        bookDetailTitleEditText = findViewById(R.id.bookDetailTitleEditText);
        bookDetailAuthorEditText = findViewById(R.id.bookDetailAuthorEditText);
        editReadStateTextView = findViewById(R.id.editReadStateTextView);
        editReadStateSwitch = findViewById(R.id.editReadStateSwitch);
        setupSwitchListener();
        star1Button = findViewById(R.id.star1Button);
        star2Button = findViewById(R.id.star2Button);
        star3Button = findViewById(R.id.star3Button);
        star4Button = findViewById(R.id.star4Button);
        star5Button = findViewById(R.id.star5Button);
        // Info container
        informationButton = findViewById(R.id.informationButton);
        informationArrow = findViewById(R.id.informationArrow);
        editInformationContent = findViewById(R.id.editInformationContent);
        informationContentLayout = findViewById(R.id.informationContentLayout);
        categoryTextView = findViewById(R.id.categoryTextView);
        categoryEditText = findViewById(R.id.categoryEditText);
        pagesTextView = findViewById(R.id.pagesTextView);
        pagesEditText = findViewById(R.id.pagesEditText);
        loanTextView = findViewById(R.id.loanTextView);
        loanEditText = findViewById(R.id.loanEditText);

        // Description container
        descriptionButton = findViewById(R.id.descriptionButton);
        descriptionArrow = findViewById(R.id.descriptionArrow);
        editDescriptionContent = findViewById(R.id.editDescriptionContent);
        descriptionContentLayout = findViewById(R.id.descriptionContentLayout);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        // Comment container
        commentButton = findViewById(R.id.commentButton);
        commentArrow = findViewById(R.id.commentArrow);
        editCommentContent = findViewById(R.id.editCommentContent);
        commentContentLayout = findViewById(R.id.commentContentLayout);
        commentTextView = findViewById(R.id.commentTextView);
        commentEditText = findViewById(R.id.commentEditText);
    }





    private void setupMarkStarsDrawable(int numberOfStars) {
        List<ImageView> markStars = Arrays.asList(star1, star2, star3, star4, star5);
        List<ImageButton> markStarsButton = Arrays.asList(star1Button, star2Button, star3Button, star4Button, star5Button);
        for (ImageView star : markStars) {
            star.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_empty_star));
        }
        for (ImageButton starButton : markStarsButton) {
            starButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_empty_edit_star));
        }
        for (int i = 0; i < numberOfStars ; i++) {
            markStars.get(i).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_full_star));
            markStarsButton.get(i).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_full_edit_star));
        }
    }

    // --------------
    //     ACTION
    // --------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            // back pressed save only new Book or any Change
            if (isANewBook || bookHasCHanged()) showAlertDialogBackPressed();
            else onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_save){
            if (numberOfEditableOpen > 0 ) {
                updateAllTextViews();
            }
            saveCurrentBook();
            startActivity(new Intent(BookDetailActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAllTextViews() {
        updateTopContainerTextViewsText();
        updateInformationTextViewsText();
        updateDescriptionTextViewsText();
        updateCommentTextViewsText();
    }

    private void showAlertDialogBackPressed() {
        String alertTitle = (isANewBook) ? "Sauvegarder le livre ?" : "Sauver les modifications ?";
        String alertMessage = (isANewBook) ?
                String.format("Voulez-vous enregistrer %s dans votre bibliothèque", book.getTitle())
                : String.format("Voulez-vous enregistrer les modifications apportées à %s", book.getTitle());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(alertTitle)
                .setMessage(alertMessage)
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        saveCurrentBook();
                        onBackPressed();
                    }
                })
                .setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create()
                .show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.editPrimaryInfoButton):
                onClickTopContainerEditButton();
                break;
            case (R.id.informationButton):
                arrowRotation(informationArrow);
                int informationVisibility = informationContentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                informationContentLayout.setVisibility(informationVisibility);
                break;
            case (R.id.editInformationContent):
                onClickEditInformationButton();
                break;
            case (R.id.descriptionButton):
                arrowRotation(descriptionArrow);
                int descriptionVisibility = descriptionContentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                descriptionContentLayout.setVisibility(descriptionVisibility);
                break;
            case (R.id.editDescriptionContent):
                onClickEditDescriptionButton();
                break;
            case (R.id.commentButton):
                arrowRotation(commentArrow);
                int commentVisibility = commentContentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                commentContentLayout.setVisibility(commentVisibility);
                break;
            case (R.id.editCommentContent):
                onClickEditCommentButton();
                break;
        }
    }

    public void onClickStarButton(View view) {
        numberOfStars = Integer.parseInt(view.getTag().toString());
        book.setMark(numberOfStars);
        setupMarkStarsDrawable(numberOfStars);
    }

    private void setupSwitchListener() {
        if (book.getHasBeenRead() != null) editReadStateSwitch.setChecked(book.getHasBeenRead());
        else  editReadStateSwitch.setChecked(false);
        editReadStateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String readText = isChecked ? getResources().getString(R.string.read_state) : getResources().getString(R.string.not_read_state);
                int readColor = isChecked ? getResources().getColor(R.color.background_primary_blue) : getResources().getColor(R.color.text_secondary_white);
                editReadStateTextView.setText(readText);
                editReadStateTextView.setTextColor(readColor);
                if (isChecked) showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        int mYear, mMonth, mDay;
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String month = getResources().getString(months.get(monthOfYear));
                    String dateText = "Lu le " + dayOfMonth + " " + month + " " + year;
                    editReadStateTextView.setText(dateText);

                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    // ---------------------
    // Action Edit Button
    // ---------------------

    /**
     * OnClick Top container EditButton :
     *  Function of Edit state :
     *      Open or close EditLayout
     *      Update EditText or TextViews
     *      Update EditButton
     */
    private void onClickTopContainerEditButton() {
        // Check Edit state
        boolean editStateIsOpen = editTopContainerLayout.getVisibility() == View.VISIBLE;
        numberOfEditableOpen = editStateIsOpen ? numberOfEditableOpen - 1 : numberOfEditableOpen + 1;
        // Animation floats
        float fromX = editStateIsOpen ? editTopContainerLayout.getScaleX() : 0f ;
        float fromY = editStateIsOpen ? editTopContainerLayout.getScaleY() : 0f ;
        float toX = editStateIsOpen ? 0f : editTopContainerLayout.getScaleX() ;
        float toY = editStateIsOpen ? 0f : editTopContainerLayout.getScaleY();

        if (editStateIsOpen) updateTopContainerTextViewsText();
        else updateTopContainerEditTextText();

        animateEditLayoutScale(editStateIsOpen, fromX, toX, fromY, toY);
    }

    private void onClickEditInformationButton() {
        // Edit category, pages and loan contents
        boolean editStateIsEnable = categoryEditText.getVisibility() == View.VISIBLE;
        numberOfEditableOpen = editStateIsEnable ? numberOfEditableOpen - 1 : numberOfEditableOpen + 1;
        if (editStateIsEnable) updateInformationTextViewsText();
        else updateInformationEditTextText();

        updateInformationEditLayoutView(editStateIsEnable);
    }
    private void onClickEditDescriptionButton() {
        // Edit category, pages and loan contents
        boolean editStateIsEnable = descriptionEditText.getVisibility() == View.VISIBLE;
        numberOfEditableOpen = editStateIsEnable ? numberOfEditableOpen - 1 : numberOfEditableOpen + 1;
        if (editStateIsEnable) updateDescriptionTextViewsText();
        else updateDescriptionEditTextText();

        updateDescriptionEditLayoutView(editStateIsEnable);
    }
    private void onClickEditCommentButton() {
        // Edit category, pages and loan contents
        boolean editStateIsEnable = commentEditText.getVisibility() == View.VISIBLE;
        numberOfEditableOpen = editStateIsEnable ? numberOfEditableOpen - 1 : numberOfEditableOpen + 1;
        if (editStateIsEnable) updateCommentTextViewsText();
        else updateCommentEditTextText();

        updateCommentEditLayoutView(editStateIsEnable);
    }


    // Top container Edit methods
    private void updateTopContainerEditTextText() {
        if (bookDetailTitle.getText() != null){
            bookDetailTitleEditText.setText(bookDetailTitle.getText());
        }
        if (bookDetailAuthor.getText() != null){
            bookDetailAuthorEditText.setText(bookDetailAuthor.getText());
        }
        if (bookDetailReadDate.getText() != getResources().getString(R.string.not_read_state)) {
            editReadStateTextView.setText(bookDetailReadDate.getText().toString());
        }
    }

    private void updateTopContainerTextViewsText() {
        if (bookDetailTitleEditText.getText() != null && bookDetailTitleEditText.getText().length() != 0){
            bookDetailTitle.setText(bookDetailTitleEditText.getText());
            book.setTitle(bookDetailTitleEditText.getText().toString());
        }
        if (bookDetailAuthorEditText.getText() != null && bookDetailAuthorEditText.getText().length() != 0){
            bookDetailAuthor.setText(bookDetailAuthorEditText.getText());
            book.setAuthor(bookDetailAuthorEditText.getText().toString());
        }
        bookDetailReadDate.setText(editReadStateTextView.getText());
        if (editReadStateTextView.getText() != getResources().getString(R.string.not_read_state)) book.setHasBeenRead(true);
        else book.setHasBeenRead(false);
        book.setReadTimestamp(editReadStateTextView.getText().toString());
    }
    private void animateEditLayoutScale(boolean editStateIsOpen, float fromX, float toX, float fromY, float toY) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                fromX, toX, fromY, toY,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF , 1f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (editStateIsOpen) updateEditButtonView();
                else editTopContainerLayout.setVisibility(View.VISIBLE);

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                updateEditLayoutView();
                if(!editStateIsOpen) updateEditButtonView();
            }
            private void updateEditLayoutView() {
                int editLayoutVisibility = editStateIsOpen ? View.INVISIBLE : View.VISIBLE;
                editTopContainerLayout.setVisibility(editLayoutVisibility);
            }
            private void updateEditButtonView() {
                int editButtonDrawable = editStateIsOpen ? R.drawable.ic_edit : R.drawable.ic_check;
                int editButtonBackground = editStateIsOpen ? R.color.background_turquoise : R.color.background_primary_blue;
                editPrimaryInfoButton.setImageDrawable(ContextCompat.getDrawable(BookDetailActivity.this, editButtonDrawable));
                editPrimaryInfoButton.setBackgroundColor(getResources().getColor(editButtonBackground));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        editTopContainerLayout.startAnimation(scaleAnimation);
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

    // Information Edit methods
    private void updateInformationTextViewsText() {
        if (categoryEditText.getText() != null && categoryEditText.getText().length() != 0) {
            categoryTextView.setText(categoryEditText.getText());
            book.setCategory(categoryEditText.getText().toString());
        }
        if (pagesEditText.getText() != null && pagesEditText.getText().length() != 0) {
            pagesTextView.setText(pagesEditText.getText());
            book.setPageCount(Integer.parseInt(pagesEditText.getText().toString()));
        }
        if (loanEditText.getText() != null && loanEditText.getText().length() != 0) {
            loanTextView.setText(loanEditText.getText());
            book.setLoan(loanEditText.getText().toString());
        }
    }
    private void updateInformationEditTextText() {
        if(categoryTextView.getText() != getResources().getString(R.string.not_specified)) categoryEditText.setText(categoryTextView.getText());
        else categoryEditText.setHint(getResources().getString(R.string.not_specified));
        if(pagesTextView.getText() != getResources().getString(R.string.not_specified)) pagesEditText.setText(pagesTextView.getText());
        else pagesEditText.setHint(getResources().getString(R.string.not_specified));
        if(loanTextView.getText() != getResources().getString(R.string.not_specified)) loanEditText.setText(loanTextView.getText());
        else loanEditText.setHint(getResources().getString(R.string.not_specified));
    }
    private void updateInformationEditLayoutView(boolean editStateIsEnable) {
        int editLayoutVisibility = editStateIsEnable ? View.INVISIBLE : View.VISIBLE;
        int textLayoutVisibility = !editStateIsEnable ? View.INVISIBLE : View.VISIBLE;
        categoryTextView.setVisibility(textLayoutVisibility);
        categoryEditText.setVisibility(editLayoutVisibility);
        pagesTextView.setVisibility(textLayoutVisibility);
        pagesEditText.setVisibility(editLayoutVisibility);
        loanTextView.setVisibility(textLayoutVisibility);
        loanEditText.setVisibility(editLayoutVisibility);

        int editButtonDrawable = editStateIsEnable ? R.drawable.ic_edit_gray : R.drawable.ic_check;
        editInformationContent.setImageDrawable(ContextCompat.getDrawable(BookDetailActivity.this, editButtonDrawable));
    }
    // Description Edit methods
    private void updateDescriptionTextViewsText() {
        if (descriptionEditText.getText() != null && descriptionEditText.getText().length() != 0) {
            descriptionTextView.setText(descriptionEditText.getText());
            book.setDescription(descriptionEditText.getText().toString());
        }

    }
    private void updateDescriptionEditTextText() {
        if(descriptionTextView.getText() != getResources().getString(R.string.not_specified)) descriptionEditText.setText(descriptionTextView.getText());
        else descriptionEditText.setHint(getResources().getString(R.string.not_specified));
    }
    private void updateDescriptionEditLayoutView(boolean editStateIsEnable) {
        int editLayoutVisibility = editStateIsEnable ? View.INVISIBLE : View.VISIBLE;
        int textLayoutVisibility = !editStateIsEnable ? View.INVISIBLE : View.VISIBLE;
        descriptionTextView.setVisibility(textLayoutVisibility);
        descriptionEditText.setVisibility(editLayoutVisibility);

        int editButtonDrawable = editStateIsEnable ? R.drawable.ic_edit_gray : R.drawable.ic_check;
        editDescriptionContent.setImageDrawable(ContextCompat.getDrawable(BookDetailActivity.this, editButtonDrawable));
    }
    // Comment Edit methods
    private void updateCommentTextViewsText() {
        if (commentEditText.getText() != null && commentEditText.getText().length() != 0){
            String newComment = commentEditText.getText().toString();
            commentTextView.setText(commentEditText.getText());
            book.setComment(newComment);
        }

    }
    private void updateCommentEditTextText() {
        if(commentTextView.getText() != getResources().getString(R.string.not_specified)) commentEditText.setText(commentTextView.getText());
        else commentEditText.setHint(commentTextView.getText());
    }
    private void updateCommentEditLayoutView(boolean editStateIsEnable) {
        int editLayoutVisibility = editStateIsEnable ? View.INVISIBLE : View.VISIBLE;
        int textLayoutVisibility = !editStateIsEnable ? View.INVISIBLE : View.VISIBLE;
        commentEditText.setVisibility(editLayoutVisibility);
        commentTextView.setVisibility(textLayoutVisibility);

        int editButtonDrawable = editStateIsEnable ? R.drawable.ic_edit_gray : R.drawable.ic_check;
        editCommentContent.setImageDrawable(ContextCompat.getDrawable(BookDetailActivity.this, editButtonDrawable));
    }
}
