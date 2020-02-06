package com.aprouxdev.mabibliotheque.database;

import android.content.Context;

import com.aprouxdev.mabibliotheque.models.Book;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Book.class}, version = 1)
public abstract class BookDatabase extends RoomDatabase {

    public abstract BookDao bookDao();
    private static BookDatabase instance;

    public static synchronized BookDatabase getInstance(Context context) {
        if (instance == null) {
            // Creation of a Room database
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    BookDatabase.class, MetaData.bookDatabase)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)  // = for first instance
                    .build();
        }
        return instance;
    }

    // For first instance
    private static Callback roomCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

}
