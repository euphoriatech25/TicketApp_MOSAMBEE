package com.technosales.net.buslocationannouncement.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hornet.dateconverter.DateConverter;
import com.hornet.dateconverter.Model;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.mosambeesupport.Printer;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.additionalfeatures.QrCodeScanner;
import com.technosales.net.buslocationannouncement.utils.TextToVoice;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.List;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.NULL;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.STATUS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_PAYMENT;

public class PriceAdapterPrices extends RecyclerView.Adapter<PriceAdapterPrices.MyViewHolder> {
    SpannableStringBuilder builder = new SpannableStringBuilder();
    private List<RouteStationList> routeStationLists;
    private Context context;
    private SharedPreferences preferences, preferencesHelper;
    private boolean forward;
    private int orderPos;
    private double currentStationLat;
    private double currentStationLng;
    private float currentStationDistance;
    private int routeType;
    private String nearestName;
    private DatabaseHelper databaseHelper;
    private String helperId;
    private String busName;
    private int total_tickets;
    private int total_collections;
    private int total_collections_cash;
    private String deviceId;
    private String latitude;
    private String longitude;
    private String ticketType;
    private String discountType;
    private DateConverter dateConverter;
    private float totalDistance;
    private String currentStationId;
    private String price;
    private int routeStationListSize;
    private TextToVoice textToVoice;
    private Handler handler;
    public PriceAdapterPrices(List<RouteStationList> routeStationLists, Context context, DatabaseHelper databaseHelper) {
        this.routeStationLists = routeStationLists;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_station_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final RouteStationList routeStationModelList = routeStationLists.get(position);
        preferences = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        preferencesHelper = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
        routeType = preferences.getInt(UtilStrings.ROUTE_TYPE, UtilStrings.NON_RING_ROAD);
        routeStationListSize = preferences.getInt(UtilStrings.ROUTE_LIST_SIZE, 0);

        if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
            holder.routeStationItem.setTextColor(context.getResources().getColorStateList(R.color.discount_txt_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.routeStationItem.setBackground(ContextCompat.getDrawable(context, R.drawable.discount_price_bg));
            }
        } else {
            if (routeStationModelList.station_id.equals(preferences.getString(UtilStrings.CURRENT_ID, ""))) {
                holder.routeStationItem.setTextColor(context.getResources().getColorStateList(R.color.text_color));
            }
        }

        float distance, nearest = 0;
        totalDistance = 0;
        for (int i = 0; i < routeStationListSize; i++) {
            double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
            double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
            double endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
            double endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
            distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
            if (i == 0) {
                nearest = distance;
            } else {
                if (distance < nearest) {
                    nearest = distance;
                    orderPos = routeStationLists.get(i).station_order;
                    nearestName = routeStationLists.get(i).station_name;
                    currentStationLat = Double.parseDouble(routeStationLists.get(i).station_lat);
                    currentStationLng = Double.parseDouble(routeStationLists.get(i).station_lng);
                    currentStationDistance = routeStationLists.get(i).station_distance;
                    currentStationId = routeStationLists.get(i).station_id;
                }
            }
        }

        //TODO Checking distance and highlighting location
//        Log.i("TAG", "onBindViewHolder: " + orderPos);


        forward = preferences.getBoolean(UtilStrings.FORWARD, true);
        if (forward) {
//            Log.i("TAG", "onBindViewHolder: " + forward + " " + orderPos);

            if (routeStationModelList.station_order <= orderPos) {
                if (position == orderPos - 1) {
                    holder.routeStationItem.setTextColor(Color.parseColor("#c72893"));
                    holder.routeStationItem.setBackgroundColor(Color.parseColor("#D0E9FA"));
                holder.routeStationItem.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    holder.routeStationItem.setText(routeStationModelList.station_name);
                }else {
                     holder.routeStationItem.setTextColor(Color.parseColor("#ababab"));
                holder.routeStationItem.setText(routeStationModelList.station_name);
                holder.routeStationItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.routeStationItem.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.routeStationItem.setClickable(true);
                            }
                        }, 1000);
                        holder.routeStationItem.setClickable(false);


                        float distance, nearest = 0;
                        totalDistance = 0;

//                forward = false;
                        for (int i = 0; i < routeStationListSize; i++) {
                            double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
                            double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
                            double endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
                            double endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
                            distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
                            if (i == 0) {
                                nearest = distance;
                            } else {
                                if (distance < nearest) {
                                    nearest = distance;
                                }
                            }
                            Log.i("nearest", "asdasda" + startLat + "::" + startLng + "::" + endLat + "::" + endLng);
                        }


//                        orderPos = routeStationLists.get(positionNew).station_order;
//                        nearestName = routeStationLists.get(positionNew).station_name;
//                        currentStationLat = Double.parseDouble(routeStationLists.get(positionNew).station_lat);
//                        currentStationLng = Double.parseDouble(routeStationLists.get(positionNew).station_lng);
//                        currentStationDistance = routeStationLists.get(positionNew).station_distance;
//                        currentStationId = routeStationLists.get(positionNew).station_id;

                        if (routeType == UtilStrings.NON_RING_ROAD) {
                            price = databaseHelper.priceWrtDistance(Math.abs(currentStationDistance - routeStationModelList.station_distance), ((TicketAndTracking) context).normalDiscountToggle.isOn());
                            totalDistance = Math.abs(currentStationDistance - routeStationModelList.station_distance);
                            Log.i("priceWrt", price);

                        } else {
                            totalDistance = 0;
                            if (forward) {
                                if (orderPos <= routeStationModelList.station_order) {
                                    for (int k = 0; k < routeStationListSize - 1; k++) {
                                        if (routeStationLists.get(k).station_order >= orderPos && routeStationLists.get(k).station_order <= routeStationModelList.station_order) {
                                            if (routeStationLists.get(k).station_order == routeStationModelList.station_order) {
                                                break;
                                            } else {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));

                                            }
                                        }
                                    }

                                } else /*if (orderPos >= routeStationModelList.station_order)*/ {
                                    for (int i = 0; i < routeStationListSize; i++) {
                                        if (routeStationLists.get(i).station_order > orderPos) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i - 1).station_lat), Double.parseDouble(routeStationLists.get(i - 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                            if (i == routeStationListSize - 1) {
                                                for (int j = 1; j < routeStationModelList.station_order; j++) {
                                                    totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j - 1).station_lat), Double.parseDouble(routeStationLists.get(j - 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.i("totalDistance", orderPos + "::" + routeStationModelList.station_order);
                                if (orderPos >= routeStationModelList.station_order) {
                                    for (int k = orderPos - 2; k > -1; k--) {
                                        if (routeStationLists.get(k).station_order <= orderPos && routeStationLists.get(k).station_order >= routeStationModelList.station_order) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));
                                        }
                                    }

                                } else /*if (orderPos <= routeStationModelList.station_order)*/ {
                                    for (int i = orderPos - 2; i > -1; i--) {
                                        totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i + 1).station_lat), Double.parseDouble(routeStationLists.get(i + 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                        if (i == 0) {
                                            Log.i("totalDistance", "0");
                                            for (int j = routeStationListSize - 2; j > routeStationModelList.station_order - 2; j--) {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j + 1).station_lat), Double.parseDouble(routeStationLists.get(j + 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                            }
                                        }

                                    }
                                }
                            }
                            price = databaseHelper.priceWrtDistance(totalDistance, ((TicketAndTracking) context).normalDiscountToggle.isOn());
                            Log.i("totalDistance", "" + totalDistance / 1000);
                        }
                        if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                            ticketType = "discount";
                            discountType = "(छुट)";
                        } else {
                            ticketType = "full";
                            discountType = "(साधारण)";
                        }


                        String[] modes = {"Card", "Cash", "QR Code"};
                        // 0 card 1 cash 2 QR Code

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Select a payment mode:");
                        builder.setItems(modes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();

                                switch (item) {

                                    case 0: //card
                                        payByCard(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                    case 1: //cash
                                        payByCash(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                    case 2: //QR Code
                                        payByQR(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                }
                            }
                        }).show();
                    }
                });
                }
            } else {
                if ((position == orderPos)) {
                    holder.routeStationItem.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.routeStationItem.setBackgroundColor(Color.parseColor("#5393C0"));

                    Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.bus);
                    Bitmap smallIcon=GeneralUtils.getResizedBitmap(icon,60);
                    Drawable d = new BitmapDrawable(context.getResources(), smallIcon);

                    holder.routeStationItem.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
                    holder.routeStationItem.setGravity(Gravity.CENTER);
                    holder.routeStationItem.setText(routeStationModelList.station_name);

                    holder.routeStationItem.setText(routeStationModelList.station_name);
                    holder.routeStationItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.routeStationItem.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.routeStationItem.setClickable(true);
                                }
                            }, 1000);
                            holder.routeStationItem.setClickable(false);


                            float distance, nearest = 0;
                            totalDistance = 0;

//                forward = false;
                            for (int i = 0; i < routeStationListSize; i++) {
                                double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
                                double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
                                double endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
                                double endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
                                distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
                                if (i == 0) {
                                    nearest = distance;
                                } else {
                                    if (distance < nearest) {
                                        nearest = distance;
                                    }
                                }
                                Log.i("nearest", "asdasda" + startLat + "::" + startLng + "::" + endLat + "::" + endLng);
                            }


//                        orderPos = routeStationLists.get(positionNew).station_order;
//                        nearestName = routeStationLists.get(positionNew).station_name;
//                        currentStationLat = Double.parseDouble(routeStationLists.get(positionNew).station_lat);
//                        currentStationLng = Double.parseDouble(routeStationLists.get(positionNew).station_lng);
//                        currentStationDistance = routeStationLists.get(positionNew).station_distance;
//                        currentStationId = routeStationLists.get(positionNew).station_id;

                            if (routeType == UtilStrings.NON_RING_ROAD) {
                                price = databaseHelper.priceWrtDistance(Math.abs(currentStationDistance - routeStationModelList.station_distance), ((TicketAndTracking) context).normalDiscountToggle.isOn());
                                totalDistance = Math.abs(currentStationDistance - routeStationModelList.station_distance);
                                Log.i("priceWrt", price);

                            } else {
                                totalDistance = 0;
                                if (forward) {
                                    if (orderPos <= routeStationModelList.station_order) {
                                        for (int k = 0; k < routeStationListSize - 1; k++) {
                                            if (routeStationLists.get(k).station_order >= orderPos && routeStationLists.get(k).station_order <= routeStationModelList.station_order) {
                                                if (routeStationLists.get(k).station_order == routeStationModelList.station_order) {
                                                    break;
                                                } else {
                                                    totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));

                                                }
                                            }
                                        }

                                    } else /*if (orderPos >= routeStationModelList.station_order)*/ {
                                        for (int i = 0; i < routeStationListSize; i++) {
                                            if (routeStationLists.get(i).station_order > orderPos) {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i - 1).station_lat), Double.parseDouble(routeStationLists.get(i - 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                                if (i == routeStationListSize - 1) {
                                                    for (int j = 1; j < routeStationModelList.station_order; j++) {
                                                        totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j - 1).station_lat), Double.parseDouble(routeStationLists.get(j - 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Log.i("totalDistance", orderPos + "::" + routeStationModelList.station_order);
                                    if (orderPos >= routeStationModelList.station_order) {
                                        for (int k = orderPos - 2; k > -1; k--) {
                                            if (routeStationLists.get(k).station_order <= orderPos && routeStationLists.get(k).station_order >= routeStationModelList.station_order) {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));
                                            }
                                        }

                                    } else /*if (orderPos <= routeStationModelList.station_order)*/ {
                                        for (int i = orderPos - 2; i > -1; i--) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i + 1).station_lat), Double.parseDouble(routeStationLists.get(i + 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                            if (i == 0) {
                                                Log.i("totalDistance", "0");
                                                for (int j = routeStationListSize - 2; j > routeStationModelList.station_order - 2; j--) {
                                                    totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j + 1).station_lat), Double.parseDouble(routeStationLists.get(j + 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                                }
                                            }

                                        }
                                    }
                                }
                                price = databaseHelper.priceWrtDistance(totalDistance, ((TicketAndTracking) context).normalDiscountToggle.isOn());
                                Log.i("totalDistance", "" + totalDistance / 1000);
                            }
                            if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                                ticketType = "discount";
                                discountType = "(छुट)";
                            } else {
                                ticketType = "full";
                                discountType = "(साधारण)";
                            }


                            String[] modes = {"Card", "Cash", "QR Code"};
                            // 0 card 1 cash 2 QR Code

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Select a payment mode:");
                            builder.setItems(modes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    dialog.dismiss();

                                    switch (item) {

                                        case 0: //card
                                            payByCard(routeStationModelList, price, position);
                                            dialog.dismiss();

                                            break;
                                        case 1: //cash
                                            payByCash(routeStationModelList, price, position);
                                            dialog.dismiss();

                                            break;
                                        case 2: //QR Code
                                            payByQR(routeStationModelList, price, position);
                                            dialog.dismiss();

                                            break;
                                    }
                                }
                            }).show();
                        }
                    });
                }else {

                holder.routeStationItem.setText(routeStationModelList.station_name);
                holder.routeStationItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.routeStationItem.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.routeStationItem.setClickable(true);
                            }
                        }, 1000);
                        holder.routeStationItem.setClickable(false);


                        float distance, nearest = 0;
                        totalDistance = 0;

//                forward = false;
                        for (int i = 0; i < routeStationListSize; i++) {
                            double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
                            double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
                            double endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
                            double endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
                            distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
                            if (i == 0) {
                                nearest = distance;
                            } else {
                                if (distance < nearest) {
                                    nearest = distance;
                                }
                            }
                            Log.i("nearest", "asdasda" + startLat + "::" + startLng + "::" + endLat + "::" + endLng);
                        }


//                        orderPos = routeStationLists.get(positionNew).station_order;
//                        nearestName = routeStationLists.get(positionNew).station_name;
//                        currentStationLat = Double.parseDouble(routeStationLists.get(positionNew).station_lat);
//                        currentStationLng = Double.parseDouble(routeStationLists.get(positionNew).station_lng);
//                        currentStationDistance = routeStationLists.get(positionNew).station_distance;
//                        currentStationId = routeStationLists.get(positionNew).station_id;

                        if (routeType == UtilStrings.NON_RING_ROAD) {
                            price = databaseHelper.priceWrtDistance(Math.abs(currentStationDistance - routeStationModelList.station_distance), ((TicketAndTracking) context).normalDiscountToggle.isOn());
                            totalDistance = Math.abs(currentStationDistance - routeStationModelList.station_distance);
                            Log.i("priceWrt", price);

                        } else {
                            totalDistance = 0;
                            if (forward) {
                                if (orderPos <= routeStationModelList.station_order) {
                                    for (int k = 0; k < routeStationListSize - 1; k++) {
                                        if (routeStationLists.get(k).station_order >= orderPos && routeStationLists.get(k).station_order <= routeStationModelList.station_order) {
                                            if (routeStationLists.get(k).station_order == routeStationModelList.station_order) {
                                                break;
                                            } else {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));

                                            }
                                        }
                                    }

                                } else /*if (orderPos >= routeStationModelList.station_order)*/ {
                                    for (int i = 0; i < routeStationListSize; i++) {
                                        if (routeStationLists.get(i).station_order > orderPos) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i - 1).station_lat), Double.parseDouble(routeStationLists.get(i - 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                            if (i == routeStationListSize - 1) {
                                                for (int j = 1; j < routeStationModelList.station_order; j++) {
                                                    totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j - 1).station_lat), Double.parseDouble(routeStationLists.get(j - 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.i("totalDistance", orderPos + "::" + routeStationModelList.station_order);
                                if (orderPos >= routeStationModelList.station_order) {
                                    for (int k = orderPos - 2; k > -1; k--) {
                                        if (routeStationLists.get(k).station_order <= orderPos && routeStationLists.get(k).station_order >= routeStationModelList.station_order) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));
                                        }
                                    }

                                } else /*if (orderPos <= routeStationModelList.station_order)*/ {
                                    for (int i = orderPos - 2; i > -1; i--) {
                                        totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i + 1).station_lat), Double.parseDouble(routeStationLists.get(i + 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                        if (i == 0) {
                                            Log.i("totalDistance", "0");
                                            for (int j = routeStationListSize - 2; j > routeStationModelList.station_order - 2; j--) {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j + 1).station_lat), Double.parseDouble(routeStationLists.get(j + 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                            }
                                        }

                                    }
                                }
                            }
                            price = databaseHelper.priceWrtDistance(totalDistance, ((TicketAndTracking) context).normalDiscountToggle.isOn());
                            Log.i("totalDistance", "" + totalDistance / 1000);
                        }
                        if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                            ticketType = "discount";
                            discountType = "(छुट)";
                        } else {
                            ticketType = "full";
                            discountType = "(साधारण)";
                        }


                        String[] modes = {"Card", "Cash", "QR Code"};
                        // 0 card 1 cash 2 QR Code

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Select a payment mode:");
                        builder.setItems(modes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();

                                switch (item) {

                                    case 0: //card
                                        payByCard(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                    case 1: //cash
                                        payByCash(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                    case 2: //QR Code
                                        payByQR(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                }
                            }
                        }).show();
                    }
                });
            }
        }
        } else {
            if (routeStationModelList.station_order >= orderPos) {
                holder.routeStationItem.setTextColor(Color.parseColor("#ababab"));

                holder.routeStationItem.setText(routeStationModelList.station_name);
                holder.routeStationItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.routeStationItem.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.routeStationItem.setClickable(true);
                            }
                        }, 1000);
                        holder.routeStationItem.setClickable(false);


                        float distance, nearest = 0;
                        totalDistance = 0;

//                forward = false;
                        for (int i = 0; i < routeStationListSize; i++) {
                            double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
                            double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
                            double endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
                            double endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
                            distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
                            if (i == 0) {
                                nearest = distance;
                            } else {
                                if (distance < nearest) {
                                    nearest = distance;
                                }
                            }
                            Log.i("nearest", "asdasda" + startLat + "::" + startLng + "::" + endLat + "::" + endLng);
                        }


//                        orderPos = routeStationLists.get(positionNew).station_order;
//                        nearestName = routeStationLists.get(positionNew).station_name;
//                        currentStationLat = Double.parseDouble(routeStationLists.get(positionNew).station_lat);
//                        currentStationLng = Double.parseDouble(routeStationLists.get(positionNew).station_lng);
//                        currentStationDistance = routeStationLists.get(positionNew).station_distance;
//                        currentStationId = routeStationLists.get(positionNew).station_id;

                        if (routeType == UtilStrings.NON_RING_ROAD) {
                            price = databaseHelper.priceWrtDistance(Math.abs(currentStationDistance - routeStationModelList.station_distance), ((TicketAndTracking) context).normalDiscountToggle.isOn());
                            totalDistance = Math.abs(currentStationDistance - routeStationModelList.station_distance);
                            Log.i("priceWrt", price);

                        } else {
                            totalDistance = 0;
                            if (forward) {
                                if (orderPos <= routeStationModelList.station_order) {
                                    for (int k = 0; k < routeStationListSize - 1; k++) {
                                        if (routeStationLists.get(k).station_order >= orderPos && routeStationLists.get(k).station_order <= routeStationModelList.station_order) {
                                            if (routeStationLists.get(k).station_order == routeStationModelList.station_order) {
                                                break;
                                            } else {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));

                                            }
                                        }
                                    }

                                } else /*if (orderPos >= routeStationModelList.station_order)*/ {
                                    for (int i = 0; i < routeStationListSize; i++) {
                                        if (routeStationLists.get(i).station_order > orderPos) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i - 1).station_lat), Double.parseDouble(routeStationLists.get(i - 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                            if (i == routeStationListSize - 1) {
                                                for (int j = 1; j < routeStationModelList.station_order; j++) {
                                                    totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j - 1).station_lat), Double.parseDouble(routeStationLists.get(j - 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.i("totalDistance", orderPos + "::" + routeStationModelList.station_order);
                                if (orderPos >= routeStationModelList.station_order) {
                                    for (int k = orderPos - 2; k > -1; k--) {
                                        if (routeStationLists.get(k).station_order <= orderPos && routeStationLists.get(k).station_order >= routeStationModelList.station_order) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));
                                        }
                                    }

                                } else /*if (orderPos <= routeStationModelList.station_order)*/ {
                                    for (int i = orderPos - 2; i > -1; i--) {
                                        totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i + 1).station_lat), Double.parseDouble(routeStationLists.get(i + 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                        if (i == 0) {
                                            Log.i("totalDistance", "0");
                                            for (int j = routeStationListSize - 2; j > routeStationModelList.station_order - 2; j--) {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j + 1).station_lat), Double.parseDouble(routeStationLists.get(j + 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                            }
                                        }

                                    }
                                }
                            }
                            price = databaseHelper.priceWrtDistance(totalDistance, ((TicketAndTracking) context).normalDiscountToggle.isOn());
                            Log.i("totalDistance", "" + totalDistance / 1000);
                        }
                        if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                            ticketType = "discount";
                            discountType = "(छुट)";
                        } else {
                            ticketType = "full";
                            discountType = "(साधारण)";
                        }


                        String[] modes = {"Card", "Cash", "QR Code"};
                        // 0 card 1 cash 2 QR Code

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Select a payment mode:");
                        builder.setItems(modes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();

                                switch (item) {

                                    case 0: //card
                                        payByCard(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                    case 1: //cash
                                        payByCash(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                    case 2: //QR Code
                                        payByQR(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                }
                            }
                        }).show();
                    }
                });
            } else {
                holder.routeStationItem.setText(routeStationModelList.station_name);

                Log.i("TAG", "onBindViewHolder: " + routeStationModelList.station_name);
                holder.routeStationItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.routeStationItem.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.routeStationItem.setClickable(true);
                            }
                        }, 1000);
                        holder.routeStationItem.setClickable(false);


                        float distance, nearest = 0;
                        totalDistance = 0;
                        int positionNew = 0;

//                forward = false;
                        for (int i = 0; i < routeStationListSize; i++) {
                            double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
                            double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
                            double endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
                            double endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
                            distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
                            if (i == 0) {
                                nearest = distance;
                                positionNew = i;
                            } else {
                                if (distance < nearest) {
                                    nearest = distance;
                                    positionNew = i;
                                }
                            }
                            Log.i("nearest", "asdasda" + startLat + "::" + startLng + "::" + endLat + "::" + endLng);
                        }


                        orderPos = routeStationLists.get(positionNew).station_order;

                        if (routeType == UtilStrings.NON_RING_ROAD) {
                            nearestName = routeStationLists.get(positionNew).station_name;
                        }

                        nearestName = routeStationLists.get(positionNew).station_name;

                        currentStationLat = Double.parseDouble(routeStationLists.get(positionNew).station_lat);
                        currentStationLng = Double.parseDouble(routeStationLists.get(positionNew).station_lng);
                        currentStationDistance = routeStationLists.get(positionNew).station_distance;
                        currentStationId = routeStationLists.get(positionNew).station_id;

                        if (routeType == UtilStrings.NON_RING_ROAD) {
                            price = databaseHelper.priceWrtDistance(Math.abs(currentStationDistance - routeStationModelList.station_distance), ((TicketAndTracking) context).normalDiscountToggle.isOn());
                            totalDistance = Math.abs(currentStationDistance - routeStationModelList.station_distance);
                            Log.i("priceWrt", price);

                        } else {
                            totalDistance = 0;

                            if (forward) {
                                if (orderPos <= routeStationModelList.station_order) {
                                    for (int k = 0; k < routeStationListSize - 1; k++) {
                                        if (routeStationLists.get(k).station_order >= orderPos && routeStationLists.get(k).station_order <= routeStationModelList.station_order) {
                                            if (routeStationLists.get(k).station_order == routeStationModelList.station_order) {
                                                break;
                                            } else {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));

                                            }
                                        }
                                    }

                                } else /*if (orderPos >= routeStationModelList.station_order)*/ {
                                    for (int i = 0; i < routeStationListSize; i++) {
                                        if (routeStationLists.get(i).station_order > orderPos) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i - 1).station_lat), Double.parseDouble(routeStationLists.get(i - 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                            if (i == routeStationListSize - 1) {
                                                for (int j = 1; j < routeStationModelList.station_order; j++) {
                                                    totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j - 1).station_lat), Double.parseDouble(routeStationLists.get(j - 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.i("totalDistance", orderPos + "::" + routeStationModelList.station_order);
                                if (orderPos >= routeStationModelList.station_order) {
                                    for (int k = orderPos - 2; k > -1; k--) {
                                        if (routeStationLists.get(k).station_order <= orderPos && routeStationLists.get(k).station_order >= routeStationModelList.station_order) {
                                            totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(k + 1).station_lat), Double.parseDouble(routeStationLists.get(k + 1).station_lng), Double.parseDouble(routeStationLists.get(k).station_lat), Double.parseDouble(routeStationLists.get(k).station_lng));
                                        }
                                    }

                                } else /*if (orderPos <= routeStationModelList.station_order)*/ {
                                    for (int i = orderPos - 2; i > -1; i--) {
                                        totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(i + 1).station_lat), Double.parseDouble(routeStationLists.get(i + 1).station_lng), Double.parseDouble(routeStationLists.get(i).station_lat), Double.parseDouble(routeStationLists.get(i).station_lng));
                                        if (i == 0) {
                                            Log.i("totalDistance", "0");
                                            for (int j = routeStationListSize - 2; j > routeStationModelList.station_order - 2; j--) {
                                                totalDistance = totalDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j + 1).station_lat), Double.parseDouble(routeStationLists.get(j + 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                            }
                                        }

                                    }
                                }
                            }
                            price = databaseHelper.priceWrtDistance(totalDistance, ((TicketAndTracking) context).normalDiscountToggle.isOn());
                            Log.i("totalDistance", "" + totalDistance / 1000);
                        }
                        if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                            ticketType = "discount";
                            discountType = "(छुट)";
                        } else {
                            ticketType = "full";
                            discountType = "(साधारण)";
                        }


                        String[] modes = {"Card", "Cash", "QR Code"};
                        // 0 card 1 cash 2 QR Code

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Select a payment mode:");
                        builder.setItems(modes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();

                                switch (item) {

                                    case 0: //card
                                        payByCard(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                    case 1: //cash
                                        payByCash(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                    case 2: //QR Code
                                        payByQR(routeStationModelList, price, position);
                                        dialog.dismiss();

                                        break;
                                }
                            }
                        }).show();
                    }
                });
            }
        }
        holder.setIsRecyclable(false);
    }

    private void payByQR(RouteStationList routeStationModelList, String price, int position) {

        helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");
        if (helperId.length() > 0) {
            Intent intent = new Intent(context, QrCodeScanner.class);
            if (price != null) {
                intent.putExtra(UtilStrings.PRICE_VALUE, price);
            }
            if (nearestName != null && !nearestName.equalsIgnoreCase("")) {
                intent.putExtra(UtilStrings.NEAREST_PLACE, nearestName);
            }

            if (routeStationModelList.station_name != null && !routeStationModelList.station_name.equalsIgnoreCase("")) {
                intent.putExtra(UtilStrings.STATION_NAME, routeStationModelList.station_name);
            }
            intent.putExtra(UtilStrings.TOTAL_DISTANCE, totalDistance);

            intent.putExtra(UtilStrings.SOURCE, UtilStrings.PRICE);

            intent.putExtra(UtilStrings.POSITION, position);

            if (ticketType != null && discountType != null) {
                intent.putExtra(UtilStrings.TICKET_TYPE, ticketType);
                intent.putExtra(UtilStrings.DISCOUNT_TYPE, discountType);
            }

            ((TicketAndTracking) context).finish();
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "सहायक छान्नुहोस् ।", Toast.LENGTH_SHORT).show();
            ((TicketAndTracking) context).helperName.setText("सहायक छान्नुहोस् ।");

        }

    }

    private void payByCard(RouteStationList routeStationModelList, String price, int position) {
        helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");
        if (helperId.length() > 0) {
            Intent intent = new Intent(context, PayByCardActivity.class);
            if (price != null) {
                intent.putExtra(UtilStrings.PRICE_VALUE, price);
            }

            if (nearestName != null && !nearestName.equalsIgnoreCase("")) {
                intent.putExtra(UtilStrings.NEAREST_PLACE, nearestName);
            }

            if (routeStationModelList.station_name != null && !routeStationModelList.station_name.equalsIgnoreCase("")) {
                intent.putExtra(UtilStrings.STATION_NAME, routeStationModelList.station_name);
            }

            intent.putExtra(UtilStrings.POSITION, position);
            intent.putExtra(UtilStrings.TOTAL_DISTANCE, totalDistance);
            intent.putExtra(UtilStrings.SOURCE, UtilStrings.PRICE);

            if (ticketType != null && discountType != null) {
                intent.putExtra(UtilStrings.TICKET_TYPE, ticketType);
                intent.putExtra(UtilStrings.DISCOUNT_TYPE, discountType);
            }
            ((TicketAndTracking) context).finish();
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "सहायक छान्नुहोस् ।", Toast.LENGTH_SHORT).show();
            ((TicketAndTracking) context).helperName.setText("सहायक छान्नुहोस् ।");
        }
    }

    private void payByCash(RouteStationList routeStationModelList, String price, int position) {
        processingPayment(routeStationModelList, price, position);
    }


    private void processingPayment(RouteStationList routeStationModelList, String price, int position) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("रु." + price + " " + nearestName + " - " + routeStationModelList.station_name);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");
                busName = preferences.getString(UtilStrings.DEVICE_NAME, "");


                Log.i("isdataSending", "" + preferences.getBoolean(UtilStrings.DATA_SENDING, false));

                if (helperId.length() > 0) {

                    dialog.dismiss();
                    total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
                    total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
                    total_collections_cash = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_CASH, 0);
                    deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
                    latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
                    longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");
                    total_tickets = total_tickets + 1;
                    total_collections = total_collections + Integer.parseInt(price);
                    total_collections_cash = total_collections_cash + Integer.parseInt(price);


                    String helperAmt = preferencesHelper.getString(UtilStrings.AMOUNT_HELPER, "");
                    int newHelperAmt = Integer.parseInt(helperAmt) + Integer.parseInt(price);
                    preferencesHelper.edit().putString(UtilStrings.AMOUNT_HELPER, String.valueOf(newHelperAmt)).apply();


                    preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
                    preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS, total_collections).apply();
                    preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_BY_CASH, total_collections_cash).apply();


                    if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                        ticketType = "discount";
                        discountType = "(छुट)";
                    } else {
                        ticketType = "full";
                        discountType = "(साधारण)";
                    }
                    ((TicketAndTracking) context).setTotal();
                    String valueOfTickets = "";
                    if (total_tickets < 10) {
                        valueOfTickets = "000" + String.valueOf(total_tickets);

                    } else if (total_tickets < 100) {
                        valueOfTickets = "00" + String.valueOf(total_tickets);
                    } else if (total_tickets < 1000) {
                        valueOfTickets = "0" + String.valueOf(total_tickets);
                    } else {
                        valueOfTickets = String.valueOf(total_tickets);
                    }

//                    valueOfTickets = String.format("%04d", total_tickets);

                    dateConverter = new DateConverter();
                    String dates[] = GeneralUtils.getFullDate().split("-");
                    int dateYear = Integer.parseInt(dates[0]);
                    int dateMonth = Integer.parseInt(dates[1]);
                    int dateDay = Integer.parseInt(dates[2]);


                    Model outputOfConversion = dateConverter.getNepaliDate(dateYear, dateMonth, dateDay);

                    int year = outputOfConversion.getYear();
                    int month = outputOfConversion.getMonth() + 1;
                    int day = outputOfConversion.getDay();
                    Log.i("getNepaliDate", "year=" + year + ",month:" + month + ",day:" + day);

                    String isOnline = ((TicketAndTracking) context).getNetworkInfo();


                    TicketInfoList ticketInfoList = new TicketInfoList();
                    ticketInfoList.ticket_id = deviceId.substring(deviceId.length() - 2) + GeneralUtils.getTicketDate() + GeneralUtils.getTicketTime() + "" + valueOfTickets;
                    ticketInfoList.transactionAmount = String.valueOf(Integer.parseInt(price));
                    ticketInfoList.helper_id = helperId;
                    ticketInfoList.device_id = deviceId;
                    ticketInfoList.device_time = GeneralUtils.getFullDate() + " " + GeneralUtils.getTime();
                    ticketInfoList.transactionMedium = UtilStrings.PAYMENT_CASH;
                    ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
                    ticketInfoList.lat = routeStationLists.get(position).station_lat;
                    ticketInfoList.lng = routeStationLists.get(position).station_lng;
                    ticketInfoList.userType = ticketType;
                    ticketInfoList.status = STATUS;
                    ticketInfoList.transactionFee = NULL;
                    ticketInfoList.transactionCommission =NULL;
                    ticketInfoList.isOnline = isOnline;
                    ticketInfoList.offlineRefId =NULL;

                    ticketInfoList.passenger_id = NULL;
                    ticketInfoList.referenceHash = NULL;
                    ticketInfoList.referenceId = NULL;
                    databaseHelper.insertTicketInfo(ticketInfoList);

                    float distanceInKm = (totalDistance / 1000);
                    String strTotal = distanceInKm + "";
                    if (strTotal.length() > 4) {
                        strTotal = strTotal.substring(0, 4);
                    }

                    String printTransaction ="बस नम्बर :- "+busName + " (कार्ड)" + "\n" +
                            "टिकट नम्बर :-"+ GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) + "\n" +
                            GeneralUtils.getUnicodeNumber(strTotal) + "कि.मी \n रकम :- रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                            "दूरी :-"+nearestName + "-" + routeStationModelList.station_name + "\n" +
                            "जारी मिति :-"+ GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                            + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                            GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());
                    ((TicketAndTracking)context).recreate();
                    Toast.makeText(context, "टिकट सफलतापूर्वक काटियो।", Toast.LENGTH_SHORT).show();

                    try {
                        Printer.Print(context, printTransaction, handler);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "सहायक छान्नुहोस् ।", Toast.LENGTH_SHORT).show();
                    ((TicketAndTracking) context).helperName.setText("सहायक छान्नुहोस् ।");
                }

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return routeStationLists.size();
    }

    public void notifyDataChange(List<RouteStationList> routeStationLists) {
        this.routeStationLists = routeStationLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView routeStationItem;
        RelativeLayout rl_route_station;


        public MyViewHolder(View itemView) {
            super(itemView);

            routeStationItem = itemView.findViewById(R.id.routeStationItem);
            rl_route_station = itemView.findViewById(R.id.rl_route_station);


        }
    }

}