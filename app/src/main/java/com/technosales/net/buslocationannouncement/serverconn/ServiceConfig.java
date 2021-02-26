package com.technosales.net.buslocationannouncement.serverconn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.CALL_REGISTER_CHECK;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TICKET_URL;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TICKET_URL_NEW;

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
    public static <T> T createServiceWithAuth(Class<T> service) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder();

                        builder.addHeader("Content-Type", "application/json")
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization", "Bearer " +"abcdefghij");

                        request = builder.build();
                        return chain.proceed(request);
                    }
                }).build();
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://202.52.240.148:8092/callserver/public/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        return retrofit.create(service);

    }
    public static Retrofit retrofit() {
        return retrofit;
    }
}

