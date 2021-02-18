package com.technosales.net.buslocationannouncement.serverconn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CALL_REGISTER_CHECK;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TICKET_URL;

public class ServiceConfig {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
//            .connectTimeout(Constants.CONNECTION_TIME_OUT, TimeUnit.MINUTES)
//            .readTimeout(Constants.CONNECTION_TIME_OUT, TimeUnit.MINUTES)
//            .writeTimeout(Constants.CONNECTION_TIME_OUT, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true);

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(CALL_REGISTER_CHECK)
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.client(httpClient.build()).build();

    public static <S> S createService(Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            builder.addConverterFactory(GsonConverterFactory.create(gson));
            builder.client(httpClient.build());
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }

    public static Retrofit retrofit() {
        return retrofit;
    }
}

