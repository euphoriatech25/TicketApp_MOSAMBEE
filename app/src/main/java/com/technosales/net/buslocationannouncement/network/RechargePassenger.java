package com.technosales.net.buslocationannouncement.network;

import android.app.ProgressDialog;
import android.content.Context;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RechargePassenger {

    public static void recharge(Context context,String deviceId, int helperID, String cardNumber, int amount, ProgressDialog p){
        final Map<String, Object> params = new HashMap<>();
        params.put("helper_id", helperID);
        params.put("card_number", cardNumber);
        params.put("device_id", deviceId);
        params.put("amount", amount);


        AQuery aQuery = new AQuery(context);
        if (GeneralUtils.isNetworkAvailable(context)) {
            aQuery.ajax(UtilStrings.RECHARGE, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    super.callback(url, object, status);
                    if (object != null) {
                        String error =object.optString("error");
                        String message =object.optString("message");
                        if (error.equalsIgnoreCase("false")){

                        }

                        } else {

                    }
                }
            });
        }

    }
}
