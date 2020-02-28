package com.aprouxdev.mabibliotheque.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.BookViewHolder> {
    private static final String TAG = "LibraryAdapter";

    private List<Book> books = new ArrayList<>();
    private OnItemClickListener mListener;

    public static final String SHARE_BUTTON_CLICKED = "SHARE_BUTTON_CLICKED";
    public static final String DELETE_BUTTON_CLICKED = "DELETE_BUTTON_CLICKED";

    private OnItemLongClickListener longClickListener;
    private int height;
    private int width;
    private List<Integer> cell_size;
    private boolean isListViewAdaptor;
    private Context context;

    public LibraryAdapter() { }

    public LibraryAdapter(Context context, List<Book> books, List<Integer> cell_size) {
        this.books = books;
        this.cell_size = cell_size;
        this.isListViewAdaptor = (cell_size == null);
        this.context = context;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (isListViewAdaptor){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recyclerview_list_book, parent, false);
            width = 128;
            height = 200;
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recyclerview_book, parent, false);
            width = parent.getMeasuredWidth() / cell_size.get(0);
            height = parent.getMeasuredHeight() / cell_size.get(1);
        }
        return new BookViewHolder(itemView, mListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = books.get(position);

        if (isListViewAdaptor){
            setupBookInformation(holder, currentBook);
        } else {
            holder.itemView.getLayoutParams().width = width;
            holder.itemView.getLayoutParams().height = height;
        }

        if (currentBook.getThumbnailLink() != null) {
            Uri imageLink = Uri.parse(currentBook.getThumbnailLink());

            Picasso.get()
                    .load(imageLink)
                    .placeholder(R.drawable.book_placeholder)
                    .resize(width, height)
                    .into(holder.bookImage);
            Picasso.get().setLoggingEnabled(true);
        }
    }

    private void setupBookInformation(BookViewHolder holder, Book book) {

        holder.listItemBookTitle.setText(book.getTitle());
        String author = (book.getAuthor() != null) ? book.getAuthor(): "Auteur non renseigné";
        holder.listItemBookAuthor.setText(author);

        String readDate;
        if (book.getHasBeenRead() != null && book.getHasBeenRead()){
            readDate = (book.getReadTimestamp() != null) ? book.getReadTimestamp() : "Lu";
        } else {
            readDate = "Pas encore lu";
        }
        holder.listItemBookRead.setText(readDate);

        int numberOfStars = book.getMark();
        List<ImageView> markStars = Arrays.asList(holder.listItemStar1,
                holder.listItemStar2, holder.listItemStar3, holder.listItemStar4, holder.listItemStar5);
        for (ImageView star : markStars) {
            star.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_empty_star));
        }
        for (int i = 0; i < numberOfStars ; i++) {
            markStars.get(i).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_full_star));
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    /**
     * Displays books for new "search input" from scratch.
     *
     * @param books
     */
    public void displayNewBooks(List<Book> books) {
        this.books.clear();
        this.books.addAll(books);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener { void onItemClick(int position);  }
    public interface OnItemLongClickListener { void onItemLongClick(String click, int position);}
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.longClickListener = onItemLongClickListener;
    }

    public Book getBook(int id) {
        return books.get(id);
    }


    class BookViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookImage;
        private TextView listItemBookTitle;
        private TextView listItemBookAuthor;
        private TextView listItemBookRead;
        private ImageView listItemStar1, listItemStar2, listItemStar3, listItemStar4, listItemStar5;


        private LinearLayout itemLongClickRootLayout;
        private ImageButton shareButton;
        private ImageButton deleteButton;
        private View cancelButton;

        public BookViewHolder(@NonNull View itemView,
                              final OnItemClickListener listener,
                              final OnItemLongClickListener longClickListener) {
            super(itemView);
            int bookImageResource = isListViewAdaptor ? R.id.listItemBookFrontCover : R.id.itemBookFrontCover;
            bookImage = itemView.findViewById(bookImageResource);

            if (isListViewAdaptor){
                listItemBookTitle = itemView.findViewById(R.id.listItemBookTitle);
                listItemBookAuthor = itemView.findViewById(R.id.listItemBookAuthor);
                listItemBookRead = itemView.findViewById(R.id.listItemBookRead);
                listItemStar1 = itemView.findViewById(R.id.listItemStar1);
                listItemStar2 = itemView.findViewById(R.id.listItemStar2);
                listItemStar3 = itemView.findViewById(R.id.listItemStar3);
                listItemStar4 = itemView.findViewById(R.id.listItemStar4);
                listItemStar5 = itemView.findViewById(R.id.listItemStar5);
                // TODO implement long click in list item xml
            } else {
                itemLongClickRootLayout = itemView.findViewById(R.id.itemLongClickRootLayout);
                shareButton = itemView.findViewById(R.id.itemLongClickImageButtonShare);
                deleteButton = itemView.findViewById(R.id.itemLongClickImageButtonDelete);
                cancelButton = itemView.findViewById(R.id.itemLongClickTopView);
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null){
                        itemLongClickRootLayout.setVisibility(View.VISIBLE);
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            shareButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    longClickListener.onItemLongClick(SHARE_BUTTON_CLICKED, position);
                                    itemLongClickRootLayout.setVisibility(View.GONE);
                                }
                            });
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    longClickListener.onItemLongClick(DELETE_BUTTON_CLICKED, position);
                                    itemLongClickRootLayout.setVisibility(View.GONE);
                                }
                            });
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    itemLongClickRootLayout.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                    return true;
                }
            });
        }
    }
}
