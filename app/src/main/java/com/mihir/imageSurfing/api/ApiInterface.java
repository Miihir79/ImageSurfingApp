package com.mihir.imageSurfing.api;

import com.mihir.imageSurfing.model.ImageModel;
import com.mihir.imageSurfing.model.SearchModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static com.mihir.imageSurfing.api.ApiUtilities.API_KEY;

public interface ApiInterface {

    @Headers("Authorization: Client-ID "+ API_KEY)
    @GET("/photos")
    Call<List<ImageModel>> getImages(
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @Headers("Authorization: Client-ID "+ API_KEY)
    @GET("/search/photos")
    Call<SearchModel> searchImage(
            @Query("query") String query,
            @Query("per_page") int perPageSearch,
            @Query("page") int pageSearch
    );

    @Headers("Authorization: Client-ID "+ API_KEY)
    @GET("/photos/random")
    Call<SearchModel> randomImage(

    );
}
