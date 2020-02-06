package com.aprouxdev.mabibliotheque.ui.main.addBook;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.models.Book;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.http.Path;
import retrofit2.http.Url;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books = new ArrayList<>();
    private OnItemClickListener mListener;

    //public Picasso picasso;

    public BookAdapter() { }
    public BookAdapter(List<Book> books) {
        this.books = books;
        //this.picasso = Picasso.get();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_book, parent, false);
        return new BookViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = books.get(position);
        if (currentBook.getThumbnailLink() != null) {
            Uri imageLink = Uri.parse(currentBook.getThumbnailLink());

            Picasso.get()
                    .load(imageLink)
                    .placeholder(R.drawable.book_placeholder)
                    .resize(128, 200)
                    .into(holder.bookImage);
            Picasso.get().setLoggingEnabled(true);
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mListener = onItemClickListener;
    }

    Book getBook(int id) {
        return books.get(id);
    }


   class BookViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookImage;

       public BookViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
           super(itemView);
           bookImage = itemView.findViewById(R.id.book_front_cover);

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
       }



   }
}
