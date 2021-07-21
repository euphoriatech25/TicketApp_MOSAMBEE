package com.technosales.net.buslocationannouncement.network;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.RouteStationListModel.FareList;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.mosambeesupport.BeepLEDTest;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GetPricesFares {
    Context context;
    static   OnPriceUpdate onPriceUpdate;

    public GetPricesFares(Context context, OnPriceUpdate onPriceUpdate) {
        this.context = context;
        this.onPriceUpdate = onPriceUpdate;
    }


    public static void getFares(final String number, Context context, Dialog dialog, Boolean update) {
          dialog.show();
        RetrofitInterface post = ServerConfigNew.createService(RetrofitInterface.class);
        Call<FareList> call = post.getDeviceFareList(number);
        call.enqueue(new Callback<FareList>() {
            @Override
            public void onResponse(Call<FareList> call, Response<FareList> response) {
                if(response.isSuccessful()){
                    dialog.dismiss();
                    if (response != null) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        databaseHelper.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.PRICE_TABLE);
//                        JSONArray datArray = object.optJSONArray("data");
                        FareList fareList=response.body();
                        List<FareList.Datum> farelist=fareList.getData();
                       if(farelist.size()!=0){
                           for (int i = 0; i <farelist.size(); i++) {

//                            String normal_ticket_rate = jsonObject.optString("normal_ticket_rate");
//                            String discounted_ticket_rate = jsonObject.optString("discounted_ticket_rate");
//                            String min_distance = jsonObject.optString("min_distance");
//                            String distance_up_to = jsonObject.optString("distance_up_to");

                               String normal_ticket_rate=String.valueOf(farelist.get(i).getNormalTicketRate());
                               String discounted_ticket_rate=String.valueOf(farelist.get(i).getDiscountedTicketRate());
                               String min_distance = String.valueOf(farelist.get(i).getMinDistance());
                               String distance_up_to = String.valueOf(farelist.get(i).getDistanceUpTo());


                               ContentValues contentValues = new ContentValues();
                               contentValues.put(DatabaseHelper.PRICE_VALUE, GeneralUtils.getUnicodeNumber(normal_ticket_rate));
                               contentValues.put(DatabaseHelper.PRICE_DISCOUNT_VALUE, GeneralUtils.getUnicodeNumber(discounted_ticket_rate));
                               contentValues.put(DatabaseHelper.PRICE_MIN_DISTANCE, min_distance);
                               contentValues.put(DatabaseHelper.PRICE_DISTANCE, distance_up_to);
                               databaseHelper.insertPrice(contentValues);
                               if (!update) {

                               } else {
                                   onPriceUpdate.onPriceUpdate();
                               }
                           }    
                       }else {
                           Toast.makeText(context,
                                   context.getString(R.string.no_farelist), Toast.LENGTH_LONG).show();
                       }
                      

                    }else {
                        dialog.dismiss();
                        handleErrorOthers(response.errorBody(),context);
                    }
                }else if(response.code()==404){
                    handleErrorOthers(response.errorBody(),context);
                }else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FareList> call, Throwable t) {
//                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.i("TAG", "onFailure: "+t.getLocalizedMessage());
            }
        });

//        AQuery aQuery = new AQuery(context);
//        aQuery.ajax(UtilStrings.TICKET_PRICE_LIST + number, JSONObject.class, new AjaxCallback<JSONObject>() {
//            @Override
//            public void callback(String url, JSONObject object, AjaxStatus status) {
//                super.callback(url, object, status);
//        if (object != null) {
//            DatabaseHelper databaseHelper = new DatabaseHelper(context);
//            databaseHelper.getWritableDatabase().execSQL("DELETE FROM " + DatabaseHelper.PRICE_TABLE);
//            JSONArray datArray = object.optJSONArray("data");
//
//            for (int i = 0; i < datArray.length(); i++) {
//                JSONObject jsonObject = datArray.optJSONObject(i);
//                String normal_ticket_rate = jsonObject.optString("normal_ticket_rate");
//                String discounted_ticket_rate = jsonObject.optString("discounted_ticket_rate");
//                String min_distance = jsonObject.optString("min_distance");
//                String distance_up_to = jsonObject.optString("distance_up_to");
//
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(DatabaseHelper.PRICE_VALUE, GeneralUtils.getUnicodeNumber(normal_ticket_rate));
//                contentValues.put(DatabaseHelper.PRICE_DISCOUNT_VALUE, GeneralUtils.getUnicodeNumber(discounted_ticket_rate));
//                contentValues.put(DatabaseHelper.PRICE_MIN_DISTANCE, min_distance);
//                contentValues.put(DatabaseHelper.PRICE_DISTANCE, distance_up_to);
//                databaseHelper.insertPrice(contentValues);
//
//            }
//            if (!update) {
//                RegisterDevice.RegisterDevice(context, number,macAddress);
//            } else {
//                onPriceUpdate.onPriceUpdate();
//            }
//
//        }else {
//
//        }
//            }
//        });
    }

    private static void handleErrorOthers(ResponseBody errorBody, Context context) {
        ApiError apiErrors = GeneralUtils.convertErrors(errorBody);

        if (errorBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("message")) {
                    try {
                        BeepLEDTest.beepError();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setContentText(error.getValue().get(0))
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
        } else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
    public interface OnPriceUpdate {
        void onPriceUpdate();
    }
}
