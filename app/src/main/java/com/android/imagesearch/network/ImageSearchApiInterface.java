package com.android.imagesearch.network;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ImageSearchApiInterface {

    String PATH_VAR_SEARCH = "gpssearch";
    String PATH_VAR_THUMB_SIZE = "pithumbsize";

    @GET("/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pilimit=50&generator=prefixsearch")
    public void getImageList(
            @Query(PATH_VAR_SEARCH)
            String searchText,
            @Query(PATH_VAR_THUMB_SIZE)
            String imageSize, Callback<? extends Response> resCallback);
}
