package com.technosales.net.buslocationannouncement.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.TicketBusApp;
import com.technosales.net.buslocationannouncement.activity.CheckBalanceActivity;
import com.technosales.net.buslocationannouncement.activity.HelperLogin;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.pojo.TransactionModel;
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

public class TicketInfoDataPush {

    public static void pushTicketData(final Context context, final List<TicketInfoList> ticketInfoLists) {
     TokenManager tokenManager;
        tokenManager = TokenManager.getInstance(TicketBusApp.getContext().getSharedPreferences("prefs", MODE_PRIVATE));
        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putBoolean(UtilStrings.DATA_SENDING, true).apply();
        if (GeneralUtils.isNetworkAvailable(context)) {
            for (int i = 0; i < ticketInfoLists.size(); i++) {
                final TicketInfoList ticketInfoList = ticketInfoLists.get(i);
                Map<String, Object> params = new HashMap<>();
                params.put("helper_id", ticketInfoList.helper_id);
                params.put("ticket_id", ticketInfoList.ticket_id);
                params.put("transactionType", ticketInfoList.transactionType);
                params.put("device_time", ticketInfoList.device_time);
                params.put("transactionAmount", ticketInfoList.transactionAmount);
                params.put("transactionMedium", ticketInfoList.transactionMedium);
                params.put("lat", ticketInfoList.lat);
                params.put("lng", ticketInfoList.lng);
                params.put("userType", ticketInfoList.userType);
                params.put("transactionFee", ticketInfoList.transactionFee);
                params.put("transactionCommission", ticketInfoList.transactionCommission);
                params.put("isOnline", ticketInfoList.isOnline);
                params.put("offlineRefId", ticketInfoList.offlineRefId);
                params.put("status", ticketInfoList.status);
                params.put("referenceId", ticketInfoList.referenceId);
                params.put("referenceHash", ticketInfoList.referenceHash);
                params.put("passenger_id", ticketInfoList.passenger_id);
                params.put("device_id", context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getString(UtilStrings.DEVICE_ID, ""));


                Log.i("getParams", "" + params);

                RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class,tokenManager);
                Call<TransactionModel.TransactionResponse> call = post.transaction(params);
                call.enqueue(new Callback<TransactionModel.TransactionResponse>() {
                    @Override
                    public void onResponse(Call<TransactionModel.TransactionResponse> call, Response<TransactionModel.TransactionResponse> response) {
                        if(response.isSuccessful()){
                            new DatabaseHelper(context).deleteFromLocalId(ticketInfoList.ticket_id);
                        }else if(response.code()==404) {
                            handleError(response.errorBody(),context,ticketInfoList.ticket_id);
                        }else if(response.code()==401){
                            Toast.makeText(context, context.getString(R.string.token_expire), Toast.LENGTH_SHORT).show();
                        }else if(response.code()==400){
                            new DatabaseHelper(context).deleteFromLocalId(ticketInfoList.ticket_id);
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionModel.TransactionResponse> call, Throwable t) {
//                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("TAG", "onFailure: "+t.getLocalizedMessage());
                    }
                });

            }
            context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putBoolean(UtilStrings.DATA_SENDING, false).apply();
        } else {
            context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putBoolean(UtilStrings.DATA_SENDING, false).apply();

        }

    }

    private static void handleError(ResponseBody errorBody, Context context, String ticket_id) {
        ApiError apiErrors = GeneralUtils.convertErrors(errorBody);

        if (errorBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("message")) {
                    new DatabaseHelper(context).deleteFromLocalId(ticket_id);
                      Toast.makeText(context, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    public static void pushBusData(final Context context, final int totalTicks, final int totalCollection) {
        if (GeneralUtils.isNetworkAvailable(context)) {
            final Map<String, Object> params = new HashMap<>();
            params.put("current_helper", context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0).getString(UtilStrings.ID_HELPER, ""));
            params.put("ticket", totalTicks);
            params.put("income", totalCollection);
            params.put("device_id", context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getString(UtilStrings.DEVICE_ID, ""));
            AQuery aQuery = new AQuery(context);
            aQuery.ajax(UtilStrings.UPDATE_TICKET, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    super.callback(url, object, status);
                    Log.i("getParams", object + ":" + params + ":" + url);
                    if (object != null) {
                        if (object.optString("error").equals("false")) {
                            /*new DatabaseHelper(context).deleteFromLocalId(ticketInfoList.ticketNumber);*/
                            context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putInt(UtilStrings.SENT_TICKET, totalTicks).apply();
                        }
                    } else {
                        /*pushBusData(context, totalTicks, totalCollection);*/
                    }
                }
            });
        } else {

        }

    }
}
