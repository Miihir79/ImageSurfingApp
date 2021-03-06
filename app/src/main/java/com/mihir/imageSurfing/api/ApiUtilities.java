package com.mihir.imageSurfing.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtilities {

    public static final String BASE_URL = "https://api.unsplash.com";
    public static final String BASE_URL_RANDOM = "https://picsum.photos/v2";
    public static final String API_KEY ="8ihl2NOeLMbDHVhSV3jwJb4_ZRPA6dd-HBhv6KbG4Pk";

    public static Retrofit retrofit = null;

    public static ApiInterface getApiInterface(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit.create(ApiInterface.class);
    }
    public static ApiInterface getApiInterface_random(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit.create(ApiInterface.class);
    }

}
