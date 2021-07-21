package com.technosales.net.buslocationannouncement.network;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.RouteStationListModel.DeviceLoginModel;
import com.technosales.net.buslocationannouncement.RouteStationListModel.RouteModel;
import com.technosales.net.buslocationannouncement.mosambeesupport.BeepLEDTest;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
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


public class RegisterDevice {
   static Dialog dialog;
    public static void RegisterDevice(final Context context, final String device_no, final String mac_address) {
        if (GeneralUtils.isNetworkAvailable(context)) {

            ImageButton cancel_dialog;
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.customer_dialog);
            cancel_dialog = dialog.findViewById(R.id.cancel_dialog);
            cancel_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            RetrofitInterface post = ServerConfigNew.createService(RetrofitInterface.class);
            final Map<String, Object> params = new HashMap<>();
            params.put("unique_id", device_no);
            params.put("mac_address", mac_address);
            Log.i("TAG", "RegisterDevice: " + mac_address);
            Call<DeviceLoginModel> call = post.deviceLogin(params);
            call.enqueue(new Callback<DeviceLoginModel>() {
                @Override
                public void onResponse(Call<DeviceLoginModel> call, Response<DeviceLoginModel> response) {
                    if (response.isSuccessful()) {
                        Log.i("TAG", "onResponse: " + response.body());
                        DeviceLoginModel deviceLoginModel = response.body();
                        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.DEVICE_NAME, deviceLoginModel.getData().getVehicleName()).apply();
                        getRoute(context, String.valueOf(deviceLoginModel.getData().getId()), dialog);
                    }else if(response.code()==500){
                        dialog.dismiss();
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }else if(response.code()==404){
                        dialog.dismiss();
                        handleError(response.errorBody(),context, device_no,  mac_address);
                    }else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DeviceLoginModel> call, Throwable t) {
                    showNoInternet(context, t.getLocalizedMessage());
                    dialog.dismiss();
                }
            });
        }else {
            dialog.dismiss();
            showNoInternet(context,"No internet");
        }
    }
    private static void handleError(ResponseBody errorBody, Context context, String device_no, String mac_address) {
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
                                    RegisterDevice(context,device_no,mac_address);
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
    private static void getRoute(final Context context, String device_no, Dialog dialog) {
        if (GeneralUtils.isNetworkAvailable(context)) {
            RetrofitInterface post = ServerConfigNew.createService(RetrofitInterface.class);
            Call<RouteModel> call = post.getRouteList(device_no);
            call.enqueue(new Callback<RouteModel>() {
                @Override
                public void onResponse(Call<RouteModel> call, Response<RouteModel> response) {

                    if(response.isSuccessful()){
                        dialog.dismiss();
                        RouteModel route=response.body();
                        List<RouteModel.Datum> routeList=route.getData();
                        if(routeList.size()!=0) {
                            if (routeList.size() > 1) {
                                displayRouteList(context, response.body(),dialog);

                            } else {

                                RouteStation.getRouteStation(context, String.valueOf(routeList.get(0).getId()), (ProgressDialog) dialog);
                                context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.ROUTE_NAME, routeList.get(0).getRouteNepali()).apply();
                                GetPricesFares.getFares(String.valueOf(routeList.get(0).getId()),context, dialog,false);
                                context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.ROUTE_ID, String.valueOf(routeList.get(0).getId())).apply();
                            }
                        }else {
                            Toast.makeText(context, "Route list is empty", Toast.LENGTH_SHORT).show();
                        }

                    }else if(response.code()==404){
                        dialog.dismiss();
                        handleErrorOthers(response.errorBody(),context);
                    }else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RouteModel> call, Throwable t) {
                    showNoInternet(context, t.getLocalizedMessage());
                    dialog.dismiss();
                }
            });

        }else {
            dialog.dismiss();
            showNoInternet(context,"No internet");
        }

    }

    static void showNoInternet(Context context, String localizedMessage) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error!")
                .setContentText(localizedMessage)
                .setConfirmText("close")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }


    private static void displayRouteList(Context context, RouteModel routeList, Dialog dialogMain) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_baseline_close_24);
        builderSingle.setTitle("Select One Name:-");

        List<RouteModel.Datum> routeListName=routeList.getData();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
        for (int i = 0; i < routeList.getData().size(); i++) {
            arrayAdapter.add(routeListName.get(i).getRouteNepali());
        }
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                int station_id=routeListName.get(which).getId();
                String station_name=routeListName.get(which).getRouteNepali();
                AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialogMain.show();
                        RouteStation.getRouteStation(context,String.valueOf(station_id), dialogMain);
                        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.ROUTE_NAME, station_name).apply();
//                        new GetPricesFares(context, null).getFares(String.valueOf(station_id));


                        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.ROUTE_ID, String.valueOf(station_id)).apply();
                        GetPricesFares.getFares(String.valueOf(station_id),context, dialogMain, false);
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
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

//        final Map<String, Object> params = new HashMap<>();
//        params.put("unique_id", device_no);
//        params.put("mac_address",macAddress);
//        AQuery aQuery = new AQuery(context);
//        if (GeneralUtils.isNetworkAvailable(context)) {
//            aQuery.ajax(UtilStrings.REGISTER_URL, params, JSONObject.class, new AjaxCallback<JSONObject>() {
//                @Override
//                public void callback(String url, JSONObject object, AjaxStatus status) {
//                    super.callback(url, object, status);
//                    Log.i("response", "register :" + object+": "+params+UtilStrings.REGISTER_URL);
//                    if (object != null) {
//
//                        String error = object.optString("error");
//                        if (error.equalsIgnoreCase("false")) {
//                            JSONArray data = object.optJSONArray("data");
//                            JSONObject metaData = object.optJSONObject("metaData");
//                            JSONArray helpersArray = metaData.optJSONArray("helpers");
//                            JSONObject device = metaData.optJSONObject("device");
//                            String deviceName = device.optString("name");
//
//                            context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.DEVICE_NAME, deviceName).apply();
//                            context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0).edit().remove(UtilStrings.NAME_HELPER).apply();
//                            context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0).edit().remove(UtilStrings.ID_HELPER).apply();
//
//
//
//                            ArrayList<String> routeList = new ArrayList<>();
//
//                            if (data.length() > 1) {
//
//                                for (int i = 0; i < data.length(); i++) {
//                                    try {
//                                        JSONObject route = data.getJSONObject(i);
//                                        routeList.add(route.getString("route_id") + "(" + route.optString("route_nepali") + ")");
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                final Dialog dialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
//                                View view = LayoutInflater.from(context).inflate(R.layout.choose_route, null);
//
//                                ListView listView = (ListView) view.findViewById(R.id.routeList);
//                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
//                                        R.layout.route_list_item, routeList);
//
//                                listView.setAdapter(adapter);
//
//                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                                        StringTokenizer stringTokenizer = new StringTokenizer(adapter.getItem(i), "(");
//
//                                        dialog.dismiss();
//                                        progressDialog = new ProgressDialog(context);
//                                        progressDialog.setMessage("Please Wait");
//                                        progressDialog.setCancelable(false);
//                                        progressDialog.show();
//                                        Log.i("TAG", "onItemClick: "+ stringTokenizer.nextToken());
//                                        RouteStation.getRouteStation(context, stringTokenizer.nextToken(), progressDialog);
//                                        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.ROUTE_NAME, stringTokenizer.nextToken().replace(")", "")).apply();
//                                    }
//                                });
//
//
//                                dialog.setContentView(view);
//                                dialog.setCancelable(true);
//                                dialog.show();
//                            } else {
//                                progressDialog = new ProgressDialog(context);
//                                progressDialog.setMessage("Please Wait");
//                                progressDialog.setCancelable(false);
//                                progressDialog.show();
//                                RouteStation.getRouteStation(context, data.optJSONObject(0).optString("route_id"), progressDialog);
//                                context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.ROUTE_NAME, data.optJSONObject(0).optString("route_nepali")).apply();
//
//                            }
//                        }
//                    }
//                }
//            });
//        }



}
