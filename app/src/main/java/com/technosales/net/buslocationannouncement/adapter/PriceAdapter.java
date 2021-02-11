package com.technosales.net.buslocationannouncement.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hornet.dateconverter.DateConverter;
import com.hornet.dateconverter.Model;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.SDKManager;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;
import com.technosales.net.buslocationannouncement.additionalfeatures.QrCodeScanner;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.mosambeesupport.Printer;
import com.technosales.net.buslocationannouncement.pojo.PriceList;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.pojo.TicketInfoList;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.ArrayList;
import java.util.List;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.NULL;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.PAYMENT_CARD;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.STATUS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.TRANSACTION_TYPE_PAYMENT;


public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.MyViewHolder> {
    private List<PriceList> priceLists;
    private Context context;
    private SharedPreferences preferences,preferencesHelper;
    private int total_tickets;
    private int total_collections;
    private int total_collections_cash;
    private String deviceId;
    private String latitude;
    private String longitude;
    private String ticketType;
    private DatabaseHelper databaseHelper;
    private List<RouteStationList> routeStationLists = new ArrayList<>();
    private String nearest_name = "";
    private DateConverter dateConverter;
    private String helperId;
    private String busName;
    private String discountType;
    private int paymentType = 0;


    public PriceAdapter(List<PriceList> priceLists, Context context) {
        this.priceLists = priceLists;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        preferences = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        preferencesHelper = context.getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
        helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");


        final PriceList priceList = priceLists.get(position);
        if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
            holder.price_value.setTextColor(context.getResources().getColorStateList(R.color.discount_txt_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.price_value.setBackground(ContextCompat.getDrawable(context, R.drawable.discount_price_bg));
            }
            holder.price_value.setText(priceList.price_discount_value);
            priceList.price_value = priceList.price_discount_value;
        } else {
            holder.price_value.setText(priceList.price_value);

        }
        holder.priceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentSelection(context, priceList, position);
            }
        });
    }

    private void showPaymentSelection(final Context context, final PriceList priceList, int position) {

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
                        payByCard(priceList, position);
                        break;
                    case 1: //cash
                        payByCash(priceList, position);
                        break;
                    case 2: //QR Code
                        payByQR(priceList, position);
                        break;
                }

            }
        }).show();


    }

    public void payByCash(final PriceList priceList, int position) {
        ///startProcess

        databaseHelper = new DatabaseHelper(context);
        routeStationLists = databaseHelper.routeStationLists();


        float distance = 0;
        float nearest = 0;
        for (int i = 0; i < routeStationLists.size(); i++) {
            double startLat = Double.parseDouble(preferences.getString(UtilStrings.LATITUDE, "0.0"));
            double startLng = Double.parseDouble(preferences.getString(UtilStrings.LONGITUDE, "0.0"));
            double endLat = Double.parseDouble(routeStationLists.get(i).station_lat);
            double endLng = Double.parseDouble(routeStationLists.get(i).station_lng);
            distance = GeneralUtils.calculateDistance(startLat, startLng, endLat, endLng);
            Log.e("TAG", "payByCash: " + endLat + " " + endLng);
            if (i == 0) {
                nearest = distance;
            } else if (i > 0) {
                if (distance < nearest) {
                    nearest = distance;
                    nearest_name = routeStationLists.get(i).station_name;
                }
            }
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("रु. " + priceList.price_value /*+ " " + nearest_name*/);
//        alertDialog.setTitle("रु. " + priceList.price_value + " " + nearest_name);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");
                        busName = preferences.getString(UtilStrings.DEVICE_NAME, "");


                        Log.i("isdataSending", "" + preferences.getBoolean(UtilStrings.DATA_SENDING, false));


                        if (helperId.length() > 0) {

                            dialog.dismiss();

                            total_tickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
                            total_collections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
                            total_collections_cash = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
                            deviceId = preferences.getString(UtilStrings.DEVICE_ID, "");
                            latitude = preferences.getString(UtilStrings.LATITUDE, "0.0");
                            longitude = preferences.getString(UtilStrings.LONGITUDE, "0.0");

                            total_tickets = total_tickets + 1;
                            total_collections = total_collections + Integer.parseInt(priceList.price_value);
                            total_collections_cash=total_collections_cash+Integer.parseInt(priceList.price_value);

                            String helperAmt = preferencesHelper.getString(UtilStrings.AMOUNT_HELPER, "");
                            int newHelperAmt = Integer.valueOf(helperAmt) +  Integer.parseInt(priceList.price_value);
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

//                            if (total_tickets < 10) {
//                                valueOfTickets = "00" + String.valueOf(total_tickets);
//
//                            } else if (total_tickets > 9 && total_tickets < 100) {
//                                valueOfTickets = "0" + String.valueOf(total_tickets);
//                            } else {
//                                valueOfTickets = String.valueOf(total_tickets);
//                            }

                            valueOfTickets = String.format("%04d",total_tickets);
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
                            ticketInfoList.ticket_id = deviceId.substring(deviceId.length() - 2) +  GeneralUtils.getTicketDate() + GeneralUtils.getTicketTime() + "" + valueOfTickets;
                            ticketInfoList.transactionAmount = String.valueOf(Integer.parseInt(priceList.price_value));
                            ticketInfoList.helper_id = helperId;
                            ticketInfoList.device_id = deviceId;
                            ticketInfoList.device_time = GeneralUtils.getFullDate() + " " + GeneralUtils.getTime();
                            ticketInfoList.transactionMedium = UtilStrings.PAYMENT_CASH;
                            ticketInfoList.transactionType = TRANSACTION_TYPE_PAYMENT;
                            ticketInfoList.lat = routeStationLists.get(position).station_lat;
                            ticketInfoList.lng = routeStationLists.get(position).station_lng;
                            ticketInfoList.userType = ticketType;
                            ticketInfoList.transactionFee =NULL;
                            ticketInfoList.transactionCommission =NULL;
                            ticketInfoList.isOnline = isOnline;
                            ticketInfoList.offlineRefId =NULL;
                            ticketInfoList.status = STATUS;
                            ticketInfoList.passenger_id =NULL;
                            ticketInfoList.referenceHash =NULL;
                            ticketInfoList.referenceId =NULL;
                            databaseHelper.insertTicketInfo(ticketInfoList);
                            Log.i("TAG", "onClick: "+"helllooooo");

                            String printTransaction = busName + "\n" +
                                    GeneralUtils.getUnicodeNumber(ticketInfoList.ticket_id) +"(नगद)" +"\n" +
                                    "रु." + GeneralUtils.getUnicodeNumber(ticketInfoList.transactionAmount) + discountType + "\n" +
                                    GeneralUtils.getNepaliMonth(String.valueOf(month)) + " "
                                    + GeneralUtils.getUnicodeNumber(String.valueOf(day)) + " " +
                                    GeneralUtils.getUnicodeNumber(GeneralUtils.getTime());

//                            String status = PrinterTester.getInstance().getStatus();
//                            if(status.equalsIgnoreCase("Out of paper ")){
//                                Toast.makeText(context, "मुद्रण कागज समाप्त भयो।", Toast.LENGTH_SHORT).show();
//                            }else {
//                                ((TicketAndTracking) context).paraPrint(printTransaction);
//                            }
                            try {
                                Printer.Print(context, printTransaction);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(context, "सहायक छान्नुहोस् ।", Toast.LENGTH_SHORT).show();
                            ((TicketAndTracking) context).helperName.setText("सहायक छान्नुहोस् ।");
                        }

                        ///endProcess

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        alertDialog.show();
    }


    private void payByQR(PriceList priceList, int position) {

        if (helperId.length() > 0) {
            if (((TicketAndTracking) context).normalDiscountToggle.isOn()) {
                ticketType = "discount";
                discountType = "(छुट)";
            } else {
                ticketType = "full";
                discountType = "(साधारण)";
            }
            Intent intent = new Intent(context, QrCodeScanner.class);
            intent.putExtra(UtilStrings.PRICE_VALUE, priceList.price_value);
            if (ticketType != null && discountType != null) {
                intent.putExtra(UtilStrings.TICKET_TYPE, ticketType);
                intent.putExtra(UtilStrings.POSITION, position);
                intent.putExtra(UtilStrings.DISCOUNT_TYPE, discountType);
            }

            context.startActivity(intent);
        }else {
            Toast.makeText(context, "सहायक छान्नुहोस् ।", Toast.LENGTH_SHORT).show();
            ((TicketAndTracking) context).helperName.setText("सहायक छान्नुहोस् ।");
        }
    }

    private void payByCard(PriceList priceList, int position) {

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
            if (ticketType != null && discountType != null) {
                intent.putExtra(UtilStrings.SOURCE, "normal");
                intent.putExtra(UtilStrings.TICKET_TYPE, ticketType);
                intent.putExtra(UtilStrings.POSITION, position);
                intent.putExtra(UtilStrings.DISCOUNT_TYPE, discountType);
            }

            context.startActivity(intent);
        }else {
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
