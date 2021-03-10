package com.technosales.net.buslocationannouncement.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.TicketBusApp;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.pojo.TransactionModel;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PassengerCountUpdate {
    public static void pushPassengerCount(final Context context, final String totalCount) {
        TokenManager tokenManager;
        tokenManager = TokenManager.getInstance(TicketBusApp.getContext().getSharedPreferences("prefs", MODE_PRIVATE));
        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putBoolean(UtilStrings.DATA_SENDING, true).apply();
        if (GeneralUtils.isNetworkAvailable(context)) {

            RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
            Call<ResponseBody> call = post.updatePassengerCount( context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getString(UtilStrings.DEVICE_ID, ""),totalCount);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                    } else if (response.code() == 404) {
                        handleError(response.errorBody(), context);
                    } else if (response.code() == 401) {

                    } else if (response.code() == 400) {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("TAG", "onFailure: " + t.getLocalizedMessage());
                }
            });

        }

    }

    private static void handleError(ResponseBody errorBody, Context context) {
        ApiError apiErrors = GeneralUtils.convertErrors(errorBody);

        if (errorBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("message")) {
                    Toast.makeText(context, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

}
