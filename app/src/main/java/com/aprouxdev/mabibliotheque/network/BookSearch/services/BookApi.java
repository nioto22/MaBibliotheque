package com.aprouxdev.mabibliotheque.network.BookSearch.services;


import com.aprouxdev.mabibliotheque.models.Book;
import com.aprouxdev.mabibliotheque.network.BookSearch.MetaData;
import com.aprouxdev.mabibliotheque.network.BookSearch.dto.BooksInfo;
import com.aprouxdev.mabibliotheque.network.BookSearch.dto.DetailedEBookInfo;
import com.aprouxdev.mabibliotheque.util.Constants;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookApi {
    @GET(MetaData.VolumesList.PATH)
    Single<BooksInfo> getBooks(@Query(MetaData.VolumesList.SEARCH_QUERY_PARAM) CharSequence bookName,
                               @Query("key") CharSequence api_key,
                               @Query("maxResults") CharSequence maxResults);

    @GET(MetaData.VolumeDetails.PATH)
    Single<DetailedEBookInfo> getBookDetails(@Path(MetaData.VolumeDetails.ID_PATH_PARAM) CharSequence bookName);
}
