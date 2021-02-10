package com.technosales.net.buslocationannouncement.network;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.technosales.net.buslocationannouncement.pojo.Passenger;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public  class IssueCard {

    public static void register(Context context, Passenger passenger, ProgressDialog p, Activity activity){

        final Map<String, Object> params = new HashMap<>();
        params.put("name", passenger.name);
        params.put("card_number", passenger.cardNumber);
        params.put("phone", passenger.phone);
        params.put("address", passenger.address);
        params.put("amount", passenger.amount);
        AQuery aQuery = new AQuery(context);
        if (GeneralUtils.isNetworkAvailable(context)) {
            aQuery.ajax(UtilStrings.PASSENGER_REGISTER, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    super.callback(url, object, status);

                    if(object!=null){
                        String error =object.optString("error");
                        String message =object.optString("message");
                        try{
                            if (error=="false"){
                                p.dismiss();
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                activity.finish();

                                p.setTitle("Success");

                            }else{
                                p.dismiss();
                                Log.e(message,status.toString());
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                            }

                        }catch(Exception e){
                            Toast.makeText(context, "Exception", Toast.LENGTH_SHORT).show();
                            p.dismiss();
                            Log.e("error",e.toString());

                        }
                    }
                    else{
                        p.dismiss();
                        Log.e("error",status.toString());
                        Toast.makeText(context, status.toString(), Toast.LENGTH_SHORT).show();

                    }
                }


            }.method(AQuery.METHOD_POST));

        }
        else{
            p.dismiss();
            Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();

        }

    }
}
