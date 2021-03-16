package com.technosales.net.buslocationannouncement.APIToken;


import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.technosales.net.buslocationannouncement.TicketBusApp;
import com.technosales.net.buslocationannouncement.activity.HelperLogin;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;
import com.technosales.net.buslocationannouncement.pojo.HelperModel;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SHARED_PREFERENCES_HELPER;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TICKET_URL_NEW;


public class CustomAuthenticator implements Authenticator {
    int a;
      private TokenManager tokenManager;
        private static CustomAuthenticator INSTANCE;
        private CustomAuthenticator(TokenManager tokenManager){
            this.tokenManager = tokenManager;
        }
        public static synchronized CustomAuthenticator getInstance(TokenManager tokenManager){
            if(INSTANCE == null){
                INSTANCE = new CustomAuthenticator(tokenManager);
            }

            return INSTANCE;
        }

        @Nullable
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
           HelperModel.Token token = tokenManager.getToken();
           a++;
            Log.i("TAG", "authenticate: "+route.toString());
            if (response.code() == 401) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100, TimeUnit.SECONDS)
                        .addInterceptor(interceptor)
                        .build();
                Retrofit.Builder builder = new Retrofit.Builder()
                        .client(client)
                        .baseUrl(TICKET_URL_NEW)
                        .addConverterFactory(GsonConverterFactory.create());

                Log.i("TAG", "authenticate: "+ token.getRefreshToken());
                Retrofit retrofit = builder.build();
               RetrofitInterface apiInterface = retrofit.create(RetrofitInterface.class);
                Call<HelperModel.Token> call = apiInterface.refresh(token.getRefreshToken());
                retrofit2.Response< HelperModel.Token> res = call.execute();
                if (res != null && res.code() == 200) {
                    if (res.isSuccessful()) {
                        HelperModel.Token newToken = res.body();
                        tokenManager.saveToken(newToken);
                        Log.i("TAG", "authenticate: " + res.body().getRefreshToken());
                        return response.request().newBuilder().header("Authorization", "Bearer " + res.body().getAccessToken()).build();
                    }
                } else if(res.code() == 401){
                    SharedPreferences preferences = TicketBusApp.getContext().getSharedPreferences(SHARED_PREFERENCES_HELPER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    TicketBusApp.getContext().startActivity(new Intent(TicketBusApp.getContext(),HelperLogin.class));
                }
            }
            return  null;
        }

    }