package com.aprouxdev.mabibliotheque.network.BookSearch.services;

import com.aprouxdev.mabibliotheque.network.BookSearch.MetaData;
import com.aprouxdev.mabibliotheque.network.BookSearch.services.BookApi;
import com.aprouxdev.mabibliotheque.util.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static Retrofit getRetrofitInstance(){
        return new Retrofit.Builder()
                .baseUrl(MetaData.GOOGLE_BOOKS_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static BookApi createBookSearchService() {
        return getRetrofitInstance().create(BookApi.class);
    }
}
