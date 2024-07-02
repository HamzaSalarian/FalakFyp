package com.example.sellerapp1;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(loggingInterceptor);
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                SessionManager sessionManager = SessionManager.getInstance();

                if(sessionManager.getConsumerKey() == null || sessionManager.getConsumerSecret() == null){
                    Request request = original.newBuilder()
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);

                }
                Request request = original.newBuilder()
                        .addHeader("Authorization", Credentials.basic(sessionManager.getConsumerKey(), sessionManager.getConsumerSecret()))
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);

            });

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://falakshop.online/wp-json/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
