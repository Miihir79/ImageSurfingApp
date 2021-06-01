package com.mihir.imageSurfing.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtilities {

    public static final String BASE_URL = "https://api.unsplash.com";
    public static final String API_KEY ="P08Xh_YnHzBU1Uw4qlY8WVgeipyKx1eaqwy-j_PZ9ww";

    public static Retrofit retrofit = null;

    public static ApiInterface getApiInterface(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit.create(ApiInterface.class);
    }

}
