package com.technosales.net.buslocationannouncement.network;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.TicketBusApp;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.mosambeesupport.BeepLEDTest;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.pojo.TransactionModel;
import com.technosales.net.buslocationannouncement.serverconn.Encrypt;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECRET_KEY;

public class TicketInfoDataPush {

    public static void pushTicketData(final Context context, final List<TicketInfoList> ticketInfoLists) {
     TokenManager tokenManager;
        tokenManager = TokenManager.getInstance(TicketBusApp.getContext().getSharedPreferences("prefs", MODE_PRIVATE));
        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putBoolean(UtilStrings.DATA_SENDING, true).apply();
       if (GeneralUtils.isNetworkAvailable(context)) {
            for (int i = 0; i < ticketInfoLists.size(); i++) {
                final TicketInfoList ticketInfoList = ticketInfoLists.get(i);
                byte[] value1 = GeneralUtils.decoderfun(SECRET_KEY);
                String amt = null;
                try {
                    amt= Encrypt.encrypt(value1, ticketInfoList.transactionAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Map<String, Object> params = new HashMap<>();
                params.put("helper_id", ticketInfoList.helper_id);
                params.put("ticket_id", ticketInfoList.ticket_id);
                params.put("transactionType", ticketInfoList.transactionType);
                params.put("device_time", ticketInfoList.device_time);
                params.put("transactionAmount",amt);
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
                    if(error.getValue().get(0).contains("Something error plz consult me")){
                        Log.i("TAG", "handleError: "+error.getValue().get(0));
                    }else {
//                        Toast.makeText(context, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                        hashMishmatch(error.getValue().get(0),context);
                    }

                }
            }
        } else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    static void hashMishmatch(String msg, Context context) {

        try {
            BeepLEDTest.beepError();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error!")
                .setContentText(msg)
                .setConfirmText("close")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}
