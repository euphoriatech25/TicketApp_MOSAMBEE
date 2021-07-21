package com.technosales.net.buslocationannouncement.network;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.RouteStationListModel.StationModel;
import com.technosales.net.buslocationannouncement.activity.RegisterActivity;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteStation {
    public static void getRouteStation(final Context context, final String routeId, final Dialog progressDialog) {
        progressDialog.show();
//        AQuery aQuery = new AQuery(context);
        final DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (GeneralUtils.isNetworkAvailable(context)) {
            RetrofitInterface post = ServerConfigNew.createService(RetrofitInterface.class);
            Call<StationModel> call = post.getStationList(routeId);
            call.enqueue(new Callback<StationModel>() {
                @Override
                public void onResponse(Call<StationModel> call, Response<StationModel> response) {
                    if(response.isSuccessful()) {
                        progressDialog.dismiss();
                        if (response != null) {
//                            String error = object.optString("error");
//                            JSONArray data = object.optJSONArray("data");
                            StationModel stationModel=response.body();
                            List<StationModel.Datum> stationModelList=stationModel.getData();
                            if(stationModelList.size()!=0){
                                int order = 0;
                                databaseHelper.clearStations();
                                if (stationModelList.get(0).getId().equals(stationModelList.get(stationModelList.size()-1).getId())) {
                                    context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putInt(UtilStrings.ROUTE_TYPE, UtilStrings.RING_ROAD).apply();
                                } else {
                                    context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putInt(UtilStrings.ROUTE_TYPE, UtilStrings.NON_RING_ROAD).apply();
                                }

                                for (int i = 0; i < stationModelList.size(); i++) {
                                    String sts =String.valueOf(stationModelList.get(i).getStatus());
                                    int routeType = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getInt(UtilStrings.ROUTE_TYPE, UtilStrings.NON_RING_ROAD);
                                    if (sts.equals("0")) {
                                        order++;
                                        RouteStationList routeStationList = new RouteStationList();
                                        routeStationList.station_id = String.valueOf(stationModelList.get(i).getId());
                                        routeStationList.station_order = order;
                                        routeStationList.station_name =stationModelList.get(i).getNameNepali();
                                        routeStationList.station_name_eng =stationModelList.get(i).getName();
                                        routeStationList.station_lat = stationModelList.get(i).getLatitude();
                                        routeStationList.station_lng = stationModelList.get(i).getLongitude();

                                        if (i == 0) {
                                            routeStationList.station_distance = 0;
                                        } else {
                                            if (routeType == UtilStrings.RING_ROAD) {
                                                routeStationList.station_distance =/*databaseHelper.distancesFromStart()+ */GeneralUtils.calculateDistance(databaseHelper.recentStationLat(order - 1), databaseHelper.recentStationLng(order - 1), Double.parseDouble(routeStationList.station_lat), Double.parseDouble(routeStationList.station_lng));
                                            } else {
                                                routeStationList.station_distance = databaseHelper.distancesFromStart() + GeneralUtils.calculateDistance(databaseHelper.recentStationLat(order - 1), databaseHelper.recentStationLng(order - 1), Double.parseDouble(routeStationList.station_lat), Double.parseDouble(routeStationList.station_lng));
                                            }
                                        }
                                        databaseHelper.insertStations(routeStationList);
                                    }
                                }
//                            progressDialog.dismiss();
                                context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.ROUTE_ID, routeId).apply();
                                context.startActivity(new Intent(context, TicketAndTracking.class));
                                try {
                                    ((RegisterActivity) context).finish();
                                } catch (Exception ex) {
                                }
                            } else {
                                getRouteStation(context, routeId, progressDialog);
                            }
                            }else {
                            Toast.makeText(context, context.getString(R.string.no_routes), Toast.LENGTH_SHORT).show();
                        }
                          
                    }else if(response.code()==404){
                        progressDialog.dismiss();
                        GeneralUtils.handleErrors(response.errorBody(),context);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<StationModel> call, Throwable t) {
//                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("TAG", "onFailure: "+t.getLocalizedMessage());
                }
            });



















//            aQuery.ajax(UtilStrings.GET_STATION_LIST, params, JSONObject.class, new AjaxCallback<JSONObject>() {
//                @Override
//                public void callback(String url, JSONObject object, AjaxStatus status) {
//                    super.callback(url, object, status);
//                    Log.i("getObject11", "" + object);
//                    if (object != null) {
//                        String error = object.optString("error");
//                        JSONArray data = object.optJSONArray("data");
//                        int order = 0;
//                        databaseHelper.clearStations();
//                        if (data.optJSONObject(0).optString("station_id").equals(data.optJSONObject(data.length() - 1).optString("station_id"))) {
//                            context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putInt(UtilStrings.ROUTE_TYPE, UtilStrings.RING_ROAD).apply();
//                        } else {
//                            context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putInt(UtilStrings.ROUTE_TYPE, UtilStrings.NON_RING_ROAD).apply();
//                        }
//                        for (int i = 0; i < data.length(); i++) {
//                            JSONObject dataobj = data.optJSONObject(i);
//                            String sts = dataobj.optString("status");
//                            int routeType = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).getInt(UtilStrings.ROUTE_TYPE, UtilStrings.NON_RING_ROAD);
//                            if (sts.equals("0")) {
//                                order++;
//                                RouteStationList routeStationList = new RouteStationList();
//                                routeStationList.station_id = dataobj.optString("station_id");
//                                routeStationList.station_order = order;
//                                routeStationList.station_name = dataobj.optString("name_nepali");
//                                routeStationList.station_name_eng = dataobj.optString("name");
//                                routeStationList.station_lat = dataobj.optString("latitude");
//                                routeStationList.station_lng = dataobj.optString("longitude");
//                                if (i == 0) {
//                                    routeStationList.station_distance = 0;
//                                } else {
//                                    if (routeType == UtilStrings.RING_ROAD) {
//                                        routeStationList.station_distance =/*databaseHelper.distancesFromStart()+ */GeneralUtils.calculateDistance(databaseHelper.recentStationLat(order - 1), databaseHelper.recentStationLng(order - 1), Double.parseDouble(routeStationList.station_lat), Double.parseDouble(routeStationList.station_lng));
//                                    } else {
//                                        routeStationList.station_distance = databaseHelper.distancesFromStart() + GeneralUtils.calculateDistance(databaseHelper.recentStationLat(order - 1), databaseHelper.recentStationLng(order - 1), Double.parseDouble(routeStationList.station_lat), Double.parseDouble(routeStationList.station_lng));
//                                    }
//                                }
//                                databaseHelper.insertStations(routeStationList);
//                            }
//                        }
//                        progressDialog.dismiss();
//                        context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putString(UtilStrings.ROUTE_ID, routeId).apply();
//                        context.startActivity(new Intent(context, TicketAndTracking.class));
//                        try {
//                            ((RegisterActivity) context).finish();
//                        } catch (Exception ex) {
//                        }
//                    } else {
//                        getRouteStation(context, routeId, progressDialog);
//                    }
//                }
//            });
        }
    }
}
