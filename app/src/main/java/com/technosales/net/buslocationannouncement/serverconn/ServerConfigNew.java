package com.technosales.net.buslocationannouncement.serverconn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.technosales.net.buslocationannouncement.APIToken.CustomAuthenticator;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TICKET_URL_NEW;

public class ServerConfigNew {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(UtilStrings.CONNECTION_TIME_OUT, TimeUnit.MINUTES)
            .readTimeout(UtilStrings.CONNECTION_TIME_OUT, TimeUnit.MINUTES)
            .writeTimeout(UtilStrings.CONNECTION_TIME_OUT, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true);

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(TICKET_URL_NEW)
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
    public static <T> T createServiceWithAuth(Class<T> service, final TokenManager tokenManager) {
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
                        if (tokenManager.getToken().getAccessToken() != null) {
                            builder.addHeader("Content-Type", "application/json")
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
                        }
                        request = builder.build();
                        return chain.proceed(request);
                    }
                }).authenticator(CustomAuthenticator.getInstance(tokenManager)).build();
                Retrofit.Builder builder = new Retrofit.Builder()
                .client(client)
                .baseUrl(TICKET_URL_NEW)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        return retrofit.create(service);

    }

    public static Retrofit retrofit() {
        return retrofit;
    }
}
