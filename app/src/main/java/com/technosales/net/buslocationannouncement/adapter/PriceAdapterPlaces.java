package com.technosales.net.buslocationannouncement.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hornet.dateconverter.DateConverter;
import com.hornet.dateconverter.Model;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.additionalfeatures.QRScanner;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.mosambeesupport.BeepLEDTest;
import com.technosales.net.buslocationannouncement.mosambeesupport.Printer;
import com.technosales.net.buslocationannouncement.pojo.PassengerCountList;
import com.technosales.net.buslocationannouncement.pojo.PriceList;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.ArrayList;
import java.util.List;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.ID_HELPER;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.NULL;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.STATUS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_PAYMENT;

public class PriceAdapterPlaces extends RecyclerView.Adapter<PriceAdapterPlaces.MyViewHolder> {
    Handler handler;
    private List<PriceList> priceLists;
    private Context context;
    private SharedPreferences preferences, preferencesHelper;
    private int total_tickets;
    private int total_collections;
    private int total_collections_cash,total_passenger;
    private String deviceId;
    private String latitude;
    private String longitude;
    private String ticketType;
    private DatabaseHelper databaseHelper;
    private List<RouteStationList> routeStationLists = new ArrayList<>();
    private String nearest_name = "";
    private float nearestDistance;
    private DateConverter dateConverter;
    private String helperId;
    private String busName;
    private String discountType;
    private boolean forward;
    private int orderPos = 0;
    private String toGetOff = "";
    private int route_type,test;
    double endLat;
    double endLng;
    public PriceAdapterPlaces(List<PriceList> priceLists, Context context, Handler printHandler) {
        this.priceLists = priceLists;
        this.context = context;
        this.handler = printHandler;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_item_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        final PriceList priceList = priceLists.get(position);

        /*holder.price_value.setText(priceList.price_value);*/

        if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
            holder.price_value.setText(priceList.price_discount_value);
            priceList.price_value = priceList.price_discount_value;
            holder.price_value.setTextColor(context.getResources().getColorStateList(R.color.discount_txt_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.price_value.setBackground(ContextCompat.getDrawable(context, R.drawable.discount_price_bg));
            }
        } else {
            holder.price_value.setText(priceList.price_value);
        }

        holder.priceCard.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onClick(View v) {
///startProcess
                preferences = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
                preferencesHelper = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
                helperId = preferencesHelper.getString(ID_HELPER, "");
                databaseHelper = new DatabaseHelper(context);
                routeStationLists = databaseHelper.routeStationLists();
                route_type = preferences.getInt(UtilStrings.ROUTE_TYPE, UtilStrings.NON_RING_ROAD);
                total_passenger=  preferences.getInt(UtilStrings.TOTAL_PASSENGERS, 0);

                float distance = 0;
                float nearest = 0;
                int positionNew = 0;
                nearestDistance = 0;
                for (int i = 0; i < routeStationLists.size(); i++) {
                    double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
                    double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
                     endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
                     endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
                    distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
                    if (i == 0) {
                        nearest = distance;
                        positionNew = i;
                    } else {
                        if (distance < nearest) {
                            nearest = distance;
                            positionNew = i;
                            nearest_name = routeStationLists.get(i).station_name;
                            if (route_type == UtilStrings.NON_RING_ROAD) {
                                nearestDistance = routeStationLists.get(i).station_distance;
                            }
                            orderPos = routeStationLists.get(i).station_order;


                        }
                    }
                }

                nearest_name = routeStationLists.get(positionNew).station_name;
                if (route_type == UtilStrings.NON_RING_ROAD) {
                    nearestDistance = routeStationLists.get(positionNew).station_distance;
                }
                orderPos = routeStationLists.get(positionNew).station_order;

                forward = preferences.getBoolean(UtilStrings.FORWARD, true);
//                forward = true; //static
                final ArrayList<String> stationsGetoff = new ArrayList<>();
                final ArrayList<Integer> orderPosPassenger = new ArrayList<>();


                if (route_type == UtilStrings.RING_ROAD) {
                    for (int i = 0; i < routeStationLists.size(); i++) {
                        if (forward) {
                            if (i >= orderPos) {
                                if (nearestDistance < priceList.price_distance /*&& nearestDistance > priceList.price_min_distance*/) {
                                    nearestDistance = (nearestDistance + routeStationLists.get(i).station_distance);
                                    if (nearestDistance > priceList.price_min_distance && nearestDistance < priceList.price_distance) {

                                        stationsGetoff.add(routeStationLists.get(i).station_name);
                                        orderPosPassenger.add(routeStationLists.get(i).station_order);

                                    }
                                    if (i == routeStationLists.size() - 1) {
                                        for (int j = 1; j < routeStationLists.size(); j++) {
                                            nearestDistance = (nearestDistance + routeStationLists.get(j).station_distance);
                                            if (nearestDistance > priceList.price_min_distance && nearestDistance < priceList.price_distance)
                                                stationsGetoff.add(routeStationLists.get(j).station_name);
                                               orderPosPassenger.add(routeStationLists.get(j).station_order);

                                        }
                                    }
                                }

                            }
                        } else {

                            if (i <= orderPos) {
                                if (nearestDistance < priceList.price_distance /*&& nearestDistance > priceList.price_min_distance*/) {
                                    nearestDistance = (nearestDistance + routeStationLists.get(i).station_distance);
                                    if (nearestDistance > priceList.price_min_distance && nearestDistance < priceList.price_distance) {
                                        stationsGetoff.add(routeStationLists.get(i).station_name);
                                        orderPosPassenger.add(routeStationLists.get(i).station_order);

                                    }
                                    for (int j = routeStationLists.size() - 1; j > -1; j--) {
                                        if (j == routeStationLists.size() - 1) {
                                            nearestDistance = nearestDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(1).station_lat), Double.parseDouble(routeStationLists.get(1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                        } else {
                                            nearestDistance = nearestDistance + GeneralUtils.calculateDistance(Double.parseDouble(routeStationLists.get(j + 1).station_lat), Double.parseDouble(routeStationLists.get(j + 1).station_lng), Double.parseDouble(routeStationLists.get(j).station_lat), Double.parseDouble(routeStationLists.get(j).station_lng));
                                        }
                                        /*nearestDistance = (nearestDistance + routeStationLists.get(j).station_distance / 1000);*/
                                        if (nearestDistance > priceList.price_min_distance && nearestDistance < priceList.price_distance)
                                            stationsGetoff.add(routeStationLists.get(j).station_name);
                                        orderPosPassenger.add(routeStationLists.get(j).station_order);

                                    }

                                }

                            }
                        }

                    }
                } else {
                    float calcDistance = 0;
                    for (int i = 0; i < routeStationLists.size(); i++) {

                        if (forward) {

                            if (i >= orderPos) {
                                if (priceList.price_distance >= Math.abs(nearestDistance - routeStationLists.get(i).station_distance) && priceList.price_min_distance <= Math.abs(nearestDistance - routeStationLists.get(i).station_distance)) {
                                    stationsGetoff.add(routeStationLists.get(i).station_name);    orderPosPassenger.add(routeStationLists.get(i).station_order);

                                    /*calcDistance = calcDistance+  routeStationLists.get(i).station_distance;*/
                                }
                            }
                        } else {
                            if (i <= orderPos) {
                                if (priceList.price_distance >= Math.abs(nearestDistance - routeStationLists.get(i).station_distance) && priceList.price_min_distance <= Math.abs(nearestDistance - routeStationLists.get(i).station_distance)) {
                                    stationsGetoff.add(routeStationLists.get(i).station_name);    orderPosPassenger.add(routeStationLists.get(i).station_order);

                                    /*calcDistance = calcDistance+  routeStationLists.get(i).station_distance;*/
                                }
                            }
                        }
                    }
                    nearestDistance = Math.abs(nearestDistance - calcDistance);
                }


                Log.i("stationsGetoff", nearestDistance + "-" + route_type);


                final Dialog dialog = new Dialog(context);
                dialog.setTitle("रु. " + priceList.price_value + " " + nearest_name);
                dialog.setContentView(R.layout.dialog_layout);

                final ListView suggestionList = dialog.findViewById(R.id.suggestionList);
                for (int i = 0; i < orderPosPassenger.size(); i++) {
                    Log.i("TAG", "onClnnnnnnnk: "+orderPosPassenger.get(i));
                }

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, stationsGetoff);
                suggestionList.setAdapter(arrayAdapter);
                suggestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


//                        String[] modes = {"Card", "Cash", "QR Code"};
//                        // 0 card 1 cash 2 QR Code
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Select a payment mode:");
//                        builder.setItems(modes, new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog1, int item) {
//
//                                dialog1.dismiss();
//
//                                switch (item) {
//
//                                    case 0: //card
//                                        payByCard(priceList, toGetOff, nearest_name, position);
//                                        dialog1.dismiss();
//                                        dialog.dismiss();
//                                        orderPosPassenger.clear();
//
//                                        break;
//                                    case 1: //cash
//                                        payByCash(priceList, position);
//                                        dialog1.dismiss();
//                                        dialog.dismiss();
//                                        orderPosPassenger.clear();
//                                        break;
//                                    case 2: //QR Code
//                                        payByQR(priceList, toGetOff, nearest_name, position);
//                                        dialog1.dismiss();
//                                        dialog.dismiss();
//                                        orderPosPassenger.clear();
//                                        break;
//                                }
//                            }
//                        }).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                      builder.setTitle("Select a payment mode:");
                        CardView payment_by_card, payment_by_cash, payment_by_qr;
                        final View customLayout
                                = ((TicketAndTracking) context).getLayoutInflater()
                                .inflate(
                                        R.layout.payment_option,
                                        null);
                        builder.setView(customLayout);
                        AlertDialog dialogAlert
                                = builder.create();

                        payment_by_card = customLayout.findViewById(R.id.payment_by_card);
                        payment_by_cash = customLayout.findViewById(R.id.payment_by_cash);
                        payment_by_qr = customLayout.findViewById(R.id.payment_by_qr);

                        payment_by_card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                payByCard(priceList, toGetOff, nearest_name, position);
                                dialog.dismiss();
                                dialogAlert.dismiss();
                                orderPosPassenger.clear();

                            }
                        });
                        payment_by_cash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                payByCash(priceList, position);
                                dialog.dismiss();
                                dialogAlert.dismiss();
                                orderPosPassenger.clear();

                            }
                        });
                        payment_by_qr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                payByQR(priceList, toGetOff, nearest_name, position);
                                dialog.dismiss();
                                dialogAlert.dismiss();
                                orderPosPassenger.clear();
                            }
                        });
                        dialogAlert.show();

                        toGetOff = arrayAdapter.getItem(position);
                        if(test==0){
                            test=orderPosPassenger.get(position);
                            orderPosPassenger.clear();
                        }else {
                            test=0;
                        }



//                        btn_ok.setEnabled(true);
//                        dialog.setTitle("रु. " + priceList.price_value + " " + nearest_name + " - " + toGetOff);
//
//                        helperId = preferences.getString(UtilStrings.ID_HELPER, "");
//                        busName = preferences.getString(UtilStrings.DEVICE_NAME, "");
//
//                        total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
//                        total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
//                        deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
//                        latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
//                        longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");
//                        if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
//                            ticketType = "discount";
//                            discountType = "(छुट)";
//                        } else {
//                            ticketType = "full";
//                            discountType = "(साधारण)";
//                        }
//                        ((TicketAndTracking) context).setTotal();
//                        String valueOfTickets = "";
//                        if (total_tickets < 10) {
//                            valueOfTickets = "00" + String.valueOf(total_tickets + 1);
//
//                        } else if (total_tickets < 100) {
//                            valueOfTickets = "0" + String.valueOf(total_tickets + 1);
//                        } else {
//                            valueOfTickets = String.valueOf(total_tickets);
//                        }
//
//                        dateConverter = new DateConverter();
//                        String dates[] = GeneralUtils.getFullDate().split("-");
//                        int dateYear = Integer.parseInt(dates[0]);
//                        int dateMonth = Integer.parseInt(dates[1]);
//                        int dateDay = Integer.parseInt(dates[2]);
//
//
//                        Model outputOfConversion = dateConverter.getNepaliDate(dateYear, dateMonth, dateDay);
//
//                        int year = outputOfConversion.getYear();
//                        int month = outputOfConversion.getMonth() + 1;
//                        int day = outputOfConversion.getDay();
//                        Log.i("getNepaliDate", "year=" + year + ",month:" + month + ",day:" + day);
//
//
//                        TicketInfoList ticketInfoList = new TicketInfoList();
//                        ticketInfoList.ticketNumber = deviceId.substring(deviceId.length() - 4) + GeneralUtils.getDate() + "" + valueOfTickets;
//                        ticketInfoList.ticketPrice = String.valueOf(Integer.parseInt(priceList.price_value));
//                        ticketInfoList.ticketType = ticketType;
//                        ticketInfoList.ticketDate = GeneralUtils.getFullDate();
//                        ticketInfoList.ticketTime = GeneralUtils.getTime();
//                        ticketInfoList.ticketLat = latitude;
//                        ticketInfoList.ticketLng = longitude;
//                        ticketInfoList.helper_id = helperId;
//
//                        String completeInfoStr = busName + "\n" +
//                                GeneralUtils.getUnicodeNumber(ticketInfoList.ticketNumber) + "\n" +
//                                "रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.ticketPrice) + discountType + "\n" +
//                                nearest_name + "-" + toGetOff + "\n" +
//                                GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
//                                + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
//                                GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());
//                        completeInfo.setText(completeInfoStr);
//                        completeInfo.setVisibility(View.VISIBLE);
//
//                        suggestionList.setVisibility(View.GONE);
//                    }
//                });
//                btn_ok.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//
//                        String[] modes = {"Card", "Cash", "QR Code"};
//                        // 0 card 1 cash 2 QR Code
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Select a payment mode:");
//                        builder.setItems(modes, new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int item) {
//
//                                dialog.dismiss();
//
//                                switch (item) {
//
//                                    case 0: //card
//                                        payByCard(priceList);
//                                        dialog.dismiss();
//
//                                        break;
//                                    case 1: //cash
//                                        payByCash(priceList);
//                                        dialog.dismiss();
//
//                                        break;
//                                    case 2: //QR Code
//                                        payByQR(priceList);
//                                        dialog.dismiss();
//
//                                        break;
//                                }
//                            }
//                        }).show();
//
//                        ///endProcess
//
//
//                    }
//                });
//                btn_cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (suggestionList.getVisibility() == View.VISIBLE) {
//                            dialog.dismiss();
//                        } else {
//                            suggestionList.setVisibility(View.VISIBLE);
//                            completeInfo.setVisibility(View.GONE);
//                        }
//
                    }
                });
                dialog.setCancelable(true);
                dialog.show();

            }
        });


    }

    private void payByCard(PriceList priceList, String toGetOff, String nearest_name, int position) {
        if (helperId.length() > 0) {
            if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                ticketType = "discount";
                discountType = "(छुट)";
            } else {
                ticketType = "full";
                discountType = "(साधारण)";
            }
            Intent intent = new Intent(context, PayByCardActivity.class);
            intent.putExtra(UtilStrings.PRICE_VALUE, priceList.price_value);
            intent.putExtra(UtilStrings.TOGETOFF, toGetOff);
            intent.putExtra(UtilStrings.NEAREST_PLACE, nearest_name);
            intent.putExtra(UtilStrings.POSITION, position);
            intent.putExtra(UtilStrings.SOURCE, UtilStrings.PLACE);
            if (ticketType != null && discountType != null) {
                intent.putExtra(UtilStrings.TICKET_TYPE, ticketType);
                intent.putExtra(UtilStrings.DISCOUNT_TYPE, discountType);
            }
            intent.putExtra(UtilStrings.STATION_POS_PASSENGERS, test);
            context.startActivity(intent);
            ((Activity)context).finish();
        } else {
            Toast.makeText(context, "सहायक छान्नुहोस् ।", Toast.LENGTH_SHORT).show();
            ((TicketAndTracking) context).helperName.setText("सहायक छान्नुहोस् ।");
        }
    }

    private void payByCash(PriceList priceList, int position) {
        processingPayment(priceList, position);
    }

    private void payByQR(PriceList priceList, String toGetOff, String nearest_name, int position) {
        if (helperId.length() > 0) {
            if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                ticketType = "discount";
                discountType = "(छुट)";
            } else {
                ticketType = "full";
                discountType = "(साधारण)";
            }
            Intent intent = new Intent(context, QRScanner.class);
            intent.putExtra(UtilStrings.PRICE_VALUE, priceList.price_value);
            intent.putExtra(UtilStrings.TOGETOFF, toGetOff);
            intent.putExtra(UtilStrings.NEAREST_PLACE, nearest_name);
            intent.putExtra(UtilStrings.SOURCE, UtilStrings.PLACE);
            intent.putExtra(UtilStrings.POSITION, position);
            if (ticketType != null && discountType != null) {
                intent.putExtra(UtilStrings.TICKET_TYPE, ticketType);
                intent.putExtra(UtilStrings.DISCOUNT_TYPE, discountType);
            }
            intent.putExtra(UtilStrings.STATION_POS_PASSENGERS,test);
            context.startActivity(intent);
            ((Activity)context).finish();
        } else {
            Toast.makeText(context, "सहायक छान्नुहोस् ।", Toast.LENGTH_SHORT).show();
            ((TicketAndTracking) context).helperName.setText("सहायक छान्नुहोस् ।");
        }
    }

    private void processingPayment(PriceList priceList, int position) {
        helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");
        busName = preferences.getString(UtilStrings.DEVICE_NAME, "");


        Log.i("isdataSending", "" + preferences.getBoolean(UtilStrings.DATA_SENDING, false));

        if (helperId.length() > 0) {
            total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
            total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
            total_collections_cash = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_CASH, 0);
            deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
            latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
            longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");

            total_tickets = total_tickets + 1;
            total_collections = total_collections + Integer.parseInt(priceList.price_value);
            total_collections_cash = total_collections_cash + Integer.parseInt(priceList.price_value);


            String helperAmt = preferencesHelper.getString(UtilStrings.AMOUNT_HELPER, "");
            int newHelperAmt = Integer.parseInt(helperAmt) + Integer.parseInt(priceList.price_value);
            preferencesHelper.edit().putString(UtilStrings.AMOUNT_HELPER, String.valueOf(newHelperAmt)).apply();


            preferences.edit().putInt(UtilStrings.TOTAL_TICKETS, total_tickets).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS, total_collections).apply();
            preferences.edit().putInt(UtilStrings.TOTAL_COLLECTIONS_BY_CASH, total_collections_cash).apply();
            Log.i("nearest_name", "" + nearest_name + ":" + total_tickets + "");

            if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                ticketType = "discount";
                discountType = "(छुट)";
            } else {
                ticketType = "full";
                discountType = "(साधारण)";
            }
            ((TicketAndTracking) context).setTotal();
            String valueOfTickets = "";
            valueOfTickets = String.format("%04d", total_tickets);
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
            ticketInfoList.transactionAmount = String.valueOf(Integer.parseInt(priceList.price_value));
            ticketInfoList.helper_id = helperId;
            ticketInfoList.device_id = deviceId;
            ticketInfoList.device_time = GeneralUtils.getFullDate() + " " + GeneralUtils.getTime();
            ticketInfoList.transactionMedium = UtilStrings.PAYMENT_CASH;
            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
            ticketInfoList.lat = latitude;
            ticketInfoList.lng = longitude;
            ticketInfoList.userType = ticketType;
            ticketInfoList.transactionFee = NULL;
            ticketInfoList.transactionCommission = NULL;
            ticketInfoList.isOnline = isOnline;
            ticketInfoList.offlineRefId = NULL;
            ticketInfoList.status = STATUS;
            ticketInfoList.passenger_id = NULL;
            ticketInfoList.referenceHash = NULL;
            ticketInfoList.referenceId = NULL;
            databaseHelper.insertTicketInfo(ticketInfoList);


            String printTransaction = "बस नम्बर :- " + busName + "(नगद)" + "\n" +
                    "टिकट नम्बर :-" + GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) + "\n" +
                    "रकम :- " + "रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                    "दूरी :-" + nearest_name + "-" + toGetOff + "\n" +
                    "जारी मिति :-" + GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());


//            ((TicketAndTracking) context).recreate();


            Log.i("TAG", "processingPayment: "+orderPos+"::::"+test);

            total_passenger++;
            preferences.edit().putInt(UtilStrings.TOTAL_PASSENGERS, total_passenger).apply();
            PassengerCountList passengerCountList = new PassengerCountList();
            passengerCountList.passenger_station_position=test;
            passengerCountList.passenger_direction=String.valueOf(forward);
            databaseHelper.insertPassengerCountList(passengerCountList);
            test=0;


            try {
                BeepLEDTest.beepSuccess();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

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

    @Override
    public int getItemCount() {
        return priceLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView price_value;
        CardView priceCard;

        public MyViewHolder(View itemView) {
            super(itemView);
            price_value = itemView.findViewById(R.id.price_value);
            priceCard = itemView.findViewById(R.id.priceCard);
        }
    }


}