package com.technosales.net.buslocationannouncement.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.BuildConfig;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.mosambeesupport.M1CardHandlerMosambee;
import com.technosales.net.buslocationannouncement.network.PassengerCountUpdate;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.pojo.CallResponse;
import com.technosales.net.buslocationannouncement.pojo.PassengerCountList;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.serverconn.ServiceConfig;
import com.technosales.net.buslocationannouncement.userregistration.IssueCardActivity;
import com.technosales.net.buslocationannouncement.adapter.PriceAdapter;
import com.technosales.net.buslocationannouncement.adapter.PriceAdapterPlaces;
import com.technosales.net.buslocationannouncement.adapter.PriceAdapterPrices;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.network.BlockListCheck;
import com.technosales.net.buslocationannouncement.network.GetAdvertisements;
import com.technosales.net.buslocationannouncement.network.GetPricesFares;
import com.technosales.net.buslocationannouncement.network.TicketInfoDataPush;
import com.technosales.net.buslocationannouncement.pojo.PriceList;
import com.technosales.net.buslocationannouncement.pojo.RouteStationList;
import com.technosales.net.buslocationannouncement.trackcar.AutostartReceiver;
import com.technosales.net.buslocationannouncement.trackcar.TrackingController;
import com.technosales.net.buslocationannouncement.trackcar.TrackingService;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.technosales.net.buslocationannouncement.trackcar.MainFragment.KEY_ACCURACY;
import static com.technosales.net.buslocationannouncement.trackcar.MainFragment.KEY_DEVICE;
import static com.technosales.net.buslocationannouncement.trackcar.MainFragment.KEY_DISTANCE;
import static com.technosales.net.buslocationannouncement.trackcar.MainFragment.KEY_INTERVAL;
import static com.technosales.net.buslocationannouncement.trackcar.MainFragment.KEY_URL;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.USER_NUMBER;

public class TicketAndTracking extends AppCompatActivity implements GetPricesFares.OnPriceUpdate, TrackingController.TrackingListener {
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 1;
    private static final int PERMISSIONS_REQUEST_LOCATION = 2;
    private static final int ALARM_MANAGER_INTERVAL = 15000;
    private static final int STORAGE_PERMISSION_CODE = 111;
    private static final String TAG = "TicketAndTracking";
    private static final int REQUEST_PHONE_CALL = 1;
    private static final String apkPathLocal = "/Download/ticketapp.apk";
    public LabeledSwitch normalDiscountToggle;
    public TextView totalRemainingTickets;
    public TextView helperName;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int listVisiblePosition;
    Handler rHandler;
    Runnable rTicker;
    int i = 0;
    String deviceIDHelper, helperNameString, helperId = "";
    TokenManager tokenManager;
    String stationChange = "false";
    String link = "http://202.52.240.149:82/ticketapp.apk";
    int totalCount = 0;
    Handler handler = new Handler();
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private SharedPreferences trackCarPrefs;
    private List<PriceList> priceLists = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private RecyclerView priceListView;
    private TextView totalCollectionTickets;
    private TextView route_name;
    private TextView mode_selector;
    private ImageView settingMenu;
    private TextView totalPassenger;
    private Drawer mainDrawer;
    private Toolbar mainToolBar;
    private int totalTickets;
    private int totalCollections;
    private boolean reset = true;
    private int mode;
    private GridLayoutManager gridLayoutManager;
    private PriceAdapterPrices priceAdapterPrices;
    private List<RouteStationList> routeStationListsForInfinite;
    private SharedPreferences preferences, preferencesHelper;
    private List<PassengerCountList> passengerCountLists;

    //    Thread thread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            while (!Thread.interrupted())
//                try {
//                    Thread.sleep(10000);
//                    runOnUiThread(new Runnable() // start actions in UI thread
//                    {
//                        @Override
//                        public void run() {
//                            boolean changed;
//                            changed = preferences.getBoolean(UtilStrings.LOCATION_CHANGE, false);
//                            Log.i(TAG, "onCreate: " + changed);
//                            if (changed) {
//                                finish();
//                                overridePendingTransition(0, 0);
//                                startActivity(getIntent());
//                                overridePendingTransition(0, 0);
//                                Toast.makeText(TicketAndTracking.this, "changed", Toast.LENGTH_SHORT).show();
//                                getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0).edit().putBoolean(UtilStrings.LOCATION_CHANGE, false).apply();
//
//                            }
//                        }
//                    });
//                } catch (InterruptedException e) {
//                }
//
//        }
//    });
    private boolean isFirstRun, onLocationChanged;
    private String isOnlineCheck;
    private boolean isDownloadStarted = false;
    private ProgressBar progressBar;
    private DownloadManager manager;
    private long downloadId;
    private boolean finishDownload = false;
    private AlertDialog progressDownloadDialog;
    private TextView percentageProgress;
    private Handler printHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 505:
                    Toast.makeText(TicketAndTracking.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };
    private TrackingController trackingController;
    private int spanCount = 4;

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_and_tracking);
//        deviceIDHelper = getIntent().getStringExtra(UtilStrings.DEVICE_ID);
//        helperNameString = getIntent().getStringExtra(UtilStrings.NAME_HELPER);
//        setHelper(deviceIDHelper,helperNameString);

        /**/

        preferencesHelper = getSharedPreferences(UtilStrings.SHARED_PREFERENCES_HELPER, 0);
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, AutostartReceiver.class), 0);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        trackCarPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        trackCarPrefs.edit().putString(KEY_URL, getResources().getString(R.string.settings_url_default_value)).apply();
        trackCarPrefs.edit().putString(KEY_DEVICE, preferences.getString(UtilStrings.DEVICE_ID, "")).apply();
        trackCarPrefs.edit().putString(KEY_ACCURACY, "high").apply();
        trackCarPrefs.edit().putString(KEY_INTERVAL, "0").apply();
        trackCarPrefs.edit().putString(KEY_DISTANCE, "0").apply();
        /*trackCarPrefs.edit().putString(KEY_DEVICE, "12345678").apply();*/
        databaseHelper = new DatabaseHelper(this);
        trackingController = new TrackingController(this);
        trackingController.setListener(this);
//        trackingController.startHandler();
        startTrackingService(true, false);

        /**/
        priceListView = findViewById(R.id.priceListView);
        normalDiscountToggle = findViewById(R.id.normalDiscountToggle);
        totalCollectionTickets = findViewById(R.id.totalCollectionTickets);
        totalRemainingTickets = findViewById(R.id.remainingTickets);
        route_name = findViewById(R.id.route_name);
        helperName = findViewById(R.id.helperName);
        mainToolBar = findViewById(R.id.mainToolBar);
        mode_selector = findViewById(R.id.mode_selector);
        totalPassenger = findViewById(R.id.totalPassenger);
        settingMenu = findViewById(R.id.settingMenu);
        setSupportActionBar(mainToolBar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.helper_choose);
        mainToolBar.setOverflowIcon(drawable);

//        from here
        String helperNameString = preferencesHelper.getString(UtilStrings.NAME_HELPER, "");
        if (!helperNameString.equalsIgnoreCase("")) {
            helperName.setText(helperNameString);
            helperId = preferencesHelper.getString(UtilStrings.ID_HELPER, "");
        }
        mode = preferences.getInt(UtilStrings.MODE, UtilStrings.MODE_3);

        preferences.edit().putInt(UtilStrings.ROUTE_LIST_SIZE, databaseHelper.routeStationLists().size()).apply();
        passengerCountLists = databaseHelper.passengerCountLists();

        route_name.setSelected(true);
        route_name.setText(preferences.getString(UtilStrings.DEVICE_NAME, "") + "-" + preferences.getString(UtilStrings.ROUTE_NAME, ""));

        normalDiscountToggle.setLabelOn(getString(R.string.discount_rate));
        normalDiscountToggle.setLabelOff(getString(R.string.normal_rate));
        normalDiscountToggle.setOn(false);


        initializeDrawer();
        spanCount = 4;

        if (mode == UtilStrings.MODE_3) {
            spanCount = 1;
            new LinearSnapHelper().attachToRecyclerView(priceListView);

        }
        routeStationListsForInfinite = databaseHelper.routeStationLists();

        priceAdapterPrices = new PriceAdapterPrices(routeStationListsForInfinite, this, databaseHelper, printHandler);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        priceListView.setLayoutManager(gridLayoutManager);
        priceListView.setHasFixedSize(true);

        getNearestLocToTop();


        priceLists = databaseHelper.priceLists(normalDiscountToggle.isOn());

        if (mode == UtilStrings.MODE_1) {
            priceListView.setAdapter(new PriceAdapter(priceLists, this, printHandler));
            mode_selector.setText(getString(R.string.normal_mode));
        } else if (mode == UtilStrings.MODE_2) {
            priceListView.setAdapter(new PriceAdapterPlaces(priceLists, this, printHandler));
            mode_selector.setText(getString(R.string.price_mode));
        } else if (mode == UtilStrings.MODE_3) {
            //            priceListView.setAdapter(new PriceAdapterPrices(databaseHelper.routeStationLists(),TicketAndTracking.this));
            priceListView.setAdapter(priceAdapterPrices);
            mode_selector.setText(getString(R.string.places_mode));
        } else {
            Toast.makeText(this, "Default", Toast.LENGTH_SHORT).show();
        }


        mode_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(TicketAndTracking.this, mode_selector);
                //inflating menu from xml resource
                popup.inflate(R.menu.mode_select_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mode_1:
                                setMode(UtilStrings.MODE_1, 4, getString(R.string.normal_mode));
                                return true;
                            case R.id.mode_2:
                                setMode(UtilStrings.MODE_3, 1, getString(R.string.places_mode));
                                return true;
                            case R.id.mode_3:
                                setMode(UtilStrings.MODE_2, 4, getString(R.string.price_mode));
                                return true;
                        }
                        return true;

                    }
                });
                //displaying the popup
                //                popup.show();
            }
        });
        settingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawer.openDrawer();
                //old code start
                {
                    //
                    //
                    //                PopupMenu popup = new PopupMenu(TicketAndTracking.this, settingMenu);
                    //                //inflating menu from xml resource
                    //                popup.inflate(R.menu.pop_up_menu);
                    //                //adding click listener
                    //                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    //                    @Override
                    //                    public boolean onMenuItemClick(MenuItem item) {
                    //                        if (item.getItemId() == R.id.updateFare) {
                    //                            new GetPricesFares(TicketAndTracking.this, TicketAndTracking.this).getFares(preferences.getString(UtilStrings.DEVICE_ID, ""), true);
                    //                            return true;
                    //                        }
                    //                        return true;
                    //
                    //                    }
                    //                });
                    //                //displaying the popup
                    //                //popup.show();

                    //                old code end
                }
            }
        });


        normalDiscountToggle.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                mode = preferences.getInt(UtilStrings.MODE, UtilStrings.MODE_3);
    /*totalRemainingTickets.setText(GeneralUtils.getUnicodeNumber(String.valueOf(databaseHelper.listTickets().size())) + "\n" + GeneralUtils.getUnicodeNumber(String.valueOf(databaseHelper.remainingAmount())));
    if (databaseHelper.listTickets().size() > 0) {
        boolean datasending = preferences.getBoolean(UtilStrings.DATA_SENDING, false);
        if (!datasending) {
            databaseHelper.ticketInfoLists();
        }


    }*/

                if (isOn) {
                    if (mode != UtilStrings.MODE_3) {
                        setPriceLists(isOn);
                    } else {
                        //                        priceListView.setAdapter(new PriceAdapterPrices(databaseHelper.routeStationLists(),TicketAndTracking.this));
                        priceListView.setAdapter(priceAdapterPrices);
                        priceListView.getLayoutManager().scrollToPosition(listVisiblePosition);
                    }
                } else {
                    if (mode != UtilStrings.MODE_3) {
                        setPriceLists(isOn);
                    } else {
                        priceListView.setAdapter(priceAdapterPrices);
                        //                        priceListView.setAdapter(new PriceAdapterPrices(databaseHelper.routeStationLists(),TicketAndTracking.this));
                        priceListView.getLayoutManager().scrollToPosition(listVisiblePosition);
                    }
                }


            }
        });
        isToday();
        GeneralUtils.createTicketFolder();

        totalCollectionTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



         /* AlertDialog alertDialog = new AlertDialog.Builder(TicketAndTracking.this).create();
                alertDialog.setTitle("Clear Data");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                preferences.edit().remove(UtilStrings.TOTAL_TICKETS).apply();
                                preferences.edit().remove(UtilStrings.TOTAL_COLLECTIONS).apply();
                                setTotal();
                                databaseHelper.clearAllFromData();
                                databaseHelper.clearTxtTable();
                            }
                        });
                alertDialog.show(); */

            }
        });


        totalCollectionTickets.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog alertDialog = new AlertDialog.Builder(TicketAndTracking.this).create();
                alertDialog.setTitle("Write To Text");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                databaseHelper.clearAllFromPassengerTime();
//                                databaseHelper.writeToFile();

                            }
                        });
                alertDialog.show();
                return false;
            }
        });

        setTotal();
        try {
            rHandler.removeCallbacks(rTicker);
        } catch (Exception ex) {

        }


        interValDataPush();

//TODO
//       priceListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                listVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
//                if (dy > 0) //check for scroll down
//                {
//                    visibleItemCount = gridLayoutManager.getChildCount();
//                    totalItemCount = gridLayoutManager.getItemCount();
//                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
//                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 9) {
//                        mode = preferences.getInt(UtilStrings.MODE, UtilStrings.MODE_3);
//                        if (mode == UtilStrings.MODE_3 && preferences.getInt(UtilStrings.ROUTE_TYPE, UtilStrings.NON_RING_ROAD) == UtilStrings.RING_ROAD) {
//                            routeStationListsForInfinite.addAll(databaseHelper.routeStationLists());
//                            priceAdapterPrices.notifyDataChange(routeStationListsForInfinite);
//                        }
//                    }
//                }
//            }
//        });


        //        getBlockListHere
        BlockListCheck.getBlockList(this);


//        thread.start();

    }

    private void getNearestLocToTop() {
        float distance, nearest = 0;
        int orderPos = 0;
        List<RouteStationList> routeStationLists = new ArrayList<>();
        routeStationLists = databaseHelper.routeStationLists();
        int routeStationListSize = preferences.getInt(UtilStrings.ROUTE_LIST_SIZE, 0);


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
                    gridLayoutManager.scrollToPositionWithOffset(orderPos - 1, 10);
                }
            }

        }
        Log.i(TAG, "getNearestLocToTop: aamamsmsmss"+orderPos);
        updateTotalPassengerCount(orderPos);
    }

    public String getNetworkInfo() {
        if (GeneralUtils.isNetworkAvailable(this)) {
            isOnlineCheck = "true";
        } else {
            isOnlineCheck = "false";
        }
        return isOnlineCheck;
    }

    private void initializeDrawer() {
        final PrimaryDrawerItem FareItem = new PrimaryDrawerItem().withIdentifier(1).withName("Fare Type").withTag("FareItem").withBadge("▼");

        final SecondaryDrawerItem Normal = new SecondaryDrawerItem().withIdentifier(2).withName("Normal");
        final SecondaryDrawerItem Places = new SecondaryDrawerItem().withIdentifier(3).withName("Places");
        final SecondaryDrawerItem Price = new SecondaryDrawerItem().withIdentifier(4).withName("Price");

        final PrimaryDrawerItem UpdatePrice = new PrimaryDrawerItem().withName("Update Price").withBadge("$");


        final PrimaryDrawerItem CardOption = new PrimaryDrawerItem().withIdentifier(5).withName("Card Option").withTag("CardOption").withBadge("▼");
        final SecondaryDrawerItem IssueCard = new SecondaryDrawerItem().withIdentifier(6).withName("Issue Card");
        final SecondaryDrawerItem CardBlock = new SecondaryDrawerItem().withIdentifier(7).withName("Card Block");
        final SecondaryDrawerItem CardReissue = new SecondaryDrawerItem().withIdentifier(8).withName("Card Reissue");

        final PrimaryDrawerItem checkBalance = new PrimaryDrawerItem().withName("Check Balance");

        final PrimaryDrawerItem updateApp = new PrimaryDrawerItem().withName("Update App");

        Toolbar toolbar = new Toolbar(this);
        mainDrawer = new DrawerBuilder()
                .withTranslucentNavigationBar(true)
                .withCloseOnClick(true)
                .withActivity(this)
                .withStickyHeader(R.layout.activity_helper_header)
                .withToolbar(toolbar)
                .addDrawerItems(
                        FareItem.withSubItems(Normal, Places, Price),
                        new DividerDrawerItem(),
                        UpdatePrice,
                        new DividerDrawerItem(),
                        CardOption.withSubItems(IssueCard, CardBlock, CardReissue),
                        new DividerDrawerItem(),
                        checkBalance,
                        new DividerDrawerItem(),
                        updateApp
//                        new DividerDrawerItem(),
//                        Transaction ,
//                        new DividerDrawerItem(),
//                        Encrypt
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                        mainDrawer.closeDrawer();
                        if (drawerItem.equals(Normal)) {
                            priceListView.setAdapter(new PriceAdapter(priceLists, view.getContext(), printHandler));
                            setMode(UtilStrings.MODE_1, 4, getString(R.string.normal_mode));
                            priceListView.setAdapter(new PriceAdapter(priceLists, TicketAndTracking.this, printHandler));
                            recreate();
                            mainDrawer.closeDrawer();


                        } else if (drawerItem.equals(Places)) {
                            priceListView.setAdapter(priceAdapterPrices);
                            setMode(UtilStrings.MODE_3, 1, getString(R.string.places_mode));


                            startActivity(getIntent());
                            finish();
                            overridePendingTransition(0, 0);

                            mainDrawer.closeDrawer();

                        } else if (drawerItem.equals(Price)) {
                            priceListView.setAdapter(new PriceAdapterPlaces(priceLists, TicketAndTracking.this, printHandler));
                            setMode(UtilStrings.MODE_2, 4, getString(R.string.price_mode));
                            recreate();
                            mainDrawer.closeDrawer();

                        } else if (drawerItem.equals(UpdatePrice)) {
                            new GetPricesFares(TicketAndTracking.this, TicketAndTracking.this)
                                    .getFares(preferences.getString(UtilStrings.DEVICE_ID, ""), getMacAddr(),
                                            true);

                            mainDrawer.closeDrawer();

                        } else if (drawerItem.equals(IssueCard)) {
//                            if(acceptCallPermission()) {
                            boolean result = IssueProcess();
                            if (result) {
                            } else {
                            }
                            mainDrawer.closeDrawer();

                        } else if (drawerItem.equals(checkBalance)) {
                            Intent intent = new Intent(TicketAndTracking.this, CheckBalanceActivity.class);
                            startActivity(intent);
                            mainDrawer.closeDrawer();

                        } else if (drawerItem.equals(CardBlock)) {
                            boolean result = CardBlockProcess();
                            mainDrawer.closeDrawer();
                            if (result) {
                            } else {
                            }
                        } else if (drawerItem.equals(CardReissue)) {
                            Intent intent = new Intent(TicketAndTracking.this, ReIssueCard.class);
                            startActivity(intent);
                            mainDrawer.closeDrawer();
                        } else if (drawerItem.equals(updateApp)) {
                            UpdateLatestApp(link);

                            mainDrawer.closeDrawer();

                        }
                        return true;
                    }
                }).build();
        setHelper();

    }

    void setHelper() {

        View view = mainDrawer.getStickyHeader();
        LinearLayout helperLayout = view.findViewById(R.id.helperLayout);
        ImageButton helper_login = view.findViewById(R.id.helper_login);
        ImageButton image = view.findViewById(R.id.image);
        TextView helperNameDrawer = view.findViewById(R.id.helperName);
        TextView helperContactDrawer = view.findViewById(R.id.helperContact);
        TextView helperEmailDrawer = view.findViewById(R.id.helperEmail);
        Button incomeDetails = view.findViewById(R.id.todaysincome);

        String helperAmt = preferencesHelper.getString(UtilStrings.AMOUNT_HELPER, "");
        String contactHelper = preferencesHelper.getString(UtilStrings.CONTACT_HELPER, "");
        String emailHelper = preferencesHelper.getString(UtilStrings.EMAIL_HELPER, "");
        String helperName = preferencesHelper.getString(UtilStrings.NAME_HELPER, "");
        int incomeIssue = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_CARD, 0);
        int incomeTicket = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);

        int incomeByCard = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_CARD, 0);
        int incomeByCash = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_CASH, 0);
        int incomeByQR = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_QR, 0);

        if (helperAmt.equalsIgnoreCase("") && contactHelper.equalsIgnoreCase("")) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TicketAndTracking.this, HelperLogin.class);
                    startActivity(intent);
                    finish();
                }
            });

            helperLayout.setVisibility(View.GONE);
            helper_login.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
        } else {
            helper_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TicketAndTracking.this, HelperLogin.class);
                    startActivity(intent);
                }
            });
            helper_login.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            helperLayout.setVisibility(View.VISIBLE);
        }

        if (!contactHelper.equalsIgnoreCase("")) {
            helperContactDrawer.setText("  " + contactHelper);
        } else {
            helperContactDrawer.setVisibility(View.GONE);
        }
        if (!emailHelper.equalsIgnoreCase("")) {
            helperEmailDrawer.setText("  " + emailHelper);
        } else {
            helperEmailDrawer.setVisibility(View.GONE);
        }
        if (!helperName.equalsIgnoreCase("")) {
            helperNameDrawer.setText("  " + helperName);
        } else {
            helperNameDrawer.setVisibility(View.GONE);
        }


        incomeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TicketAndTracking.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.income_layout, viewGroup, false);
                TextView helperBalanceDrawer = dialogView.findViewById(R.id.helper_balance);
                TextView income_issue = dialogView.findViewById(R.id.income_issue);
                TextView income_ticket = dialogView.findViewById(R.id.income_ticket);

                if (!preferencesHelper.getString(UtilStrings.AMOUNT_HELPER, "").equalsIgnoreCase("")) {
                    helperBalanceDrawer.setText("वर्तमान ब्यालेन्स :- " + "रू " + GeneralUtils.getUnicodeNumber(preferencesHelper.getString(UtilStrings.AMOUNT_HELPER, "")));
                } else {
                    helperBalanceDrawer.setVisibility(View.GONE);
                }

                if (preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_CARD, 0) > 0) {
                    income_issue.setText("कार्ड जारी/रिचार्ज मार्फत :- " + "रू " + GeneralUtils.getUnicodeNumber(String.valueOf(preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_CARD, 0))));
                } else {
                    income_issue.setVisibility(View.GONE);
                }


                if ( preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0) > 0) {
                    income_ticket.setText("टिकटको माध्यमबाट :- " + "रू " + GeneralUtils.getUnicodeNumber(String.valueOf( preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0)))
                            + "\n" + "कार्ड मार्फत :-" + "रू " + GeneralUtils.getUnicodeNumber(String.valueOf(preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_CARD, 0)))
                            + "\n" + "नगद मार्फत :-" + "रू " + GeneralUtils.getUnicodeNumber(String.valueOf(preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_CASH, 0)))
                            + "\n" + "QR मार्फत  :-" + "रू " + GeneralUtils.getUnicodeNumber(String.valueOf(preferences.getInt(UtilStrings.TOTAL_COLLECTIONS_BY_QR, 0))));

                } else {
                    income_ticket.setVisibility(View.GONE);
                }

                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        helper_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TicketAndTracking.this, HelperLogin.class);
                startActivity(intent);
            }
        });
    }

    private void UpdateLatestApp(String link) {

        if (isDownloadStarted) return;
//        stopIntervalListener();
        isDownloadStarted = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
            } else {
            }
        }

        //Storage Permission

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        String destination = Environment.getExternalStorageDirectory() + apkPathLocal;
        final Uri uri = Uri.parse("file://" + destination);
        Log.i("TAG", "UpdateLatestApp: " + destination);
        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            file.delete();

        //get url of app on server
        String url = link;
//        Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", url);
        //set download manager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setDestinationUri(uri)
                .setTitle(getString(R.string.app_name))
                .setDescription("Downloading")// Description of the Download Notification
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);

        // get download service and enqueue file
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                isDownloadStarted = false;

                String PATH = Environment.getExternalStorageDirectory() + apkPathLocal;
                File file = new File(PATH);
                if (file.exists()) {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setDataAndType(uriFromFile(getApplicationContext(), new File(PATH)), "application/vnd.android.package-archive");
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        getApplicationContext().startActivity(intent1);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Log.e("TAG", "Error in opening the file!");
                    }
                } else {
//                    Toast.makeText(getApplicationContext(),"installing",Toast.LENGTH_LONG).show();
                }
            }

        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mainDrawer.closeDrawer();
        showDownloadDialog();

        new Thread(() -> {
            int progress;
            while (!finishDownload) {
                Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(downloadId));
                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    switch (status) {
                        case DownloadManager.STATUS_FAILED: {
                            finishDownload = true;
                            break;
                        }
                        case DownloadManager.STATUS_PAUSED:
                            break;
                        case DownloadManager.STATUS_PENDING:
                            break;
                        case DownloadManager.STATUS_RUNNING: {
                            final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            if (total >= 0) {
                                final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                progress = (int) ((downloaded * 100L) / total);

                                publishProgress(progress);
                                // if you use downloadmanger in async task, here you can use like this to display progress.
                                // Don't forget to do the division in long to get more digits rather than double.
                                //  publishProgress((int) ((downloaded * 100L) / total));
                            }
                            break;
                        }
                        case DownloadManager.STATUS_SUCCESSFUL: {
                            progress = 100;
                            progressBar.setProgress(progress);
                            // if you use aysnc task
                            publishProgress(100);
                            finishDownload = true;
                            break;
                        }
                    }
                }
                cursor.close();
            }

        }).start();
    }

    public Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
//                unregisterReceiver(this);
//                finish();
    }

    public void publishProgress(int progress) {
        handler.post(() -> {
            progressBar.setProgress(progress);
            percentageProgress.setText(String.valueOf(progress));
            if (progress == 100) {
                progressDownloadDialog.dismiss();
            }
        });
    }

    public void showDownloadDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle(getString(R.string.downloading));
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress_bar, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
            manager.remove(downloadId);
            dialogInterface.dismiss();
        });

        progressBar = dialogView.findViewById(R.id.progress_horizontal);
        percentageProgress = dialogView.findViewById(R.id.percentage_progress);
        progressDownloadDialog = dialogBuilder.create();
        progressDownloadDialog.show();
    }

    private boolean CardBlockProcess() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("कार्ड ब्लक");
        builder.setIcon(R.drawable.ic_baseline_card_membership_24);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText numberInput = new EditText(this);
        numberInput.setHint("ग्राहक मोबाइल नम्बर");
        numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 0, 0);
        numberInput.setLayoutParams(params);
        numberInput.setTextSize(35);
        numberInput.setHeight(150);
        layout.addView(numberInput);
        builder.setView(layout);
        builder.setPositiveButton("ठिक छ", null);
        builder.setNegativeButton("रद्द गर्नुहोस्", null);

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button okay = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String num = numberInput.getText().toString();
                        if (num.length() == 10) {
                            ProgressDialog progressDialog = new ProgressDialog(TicketAndTracking.this);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setTitle("कृपया पर्खनुहोस्....");
                            progressDialog.show();
                            blockCard(num, progressDialog);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(TicketAndTracking.this, "फोन नम्बर मान्य छैन।", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mAlertDialog.show();
        return true;
    }

    private void blockCard(String toString, ProgressDialog progressDialog) {
        RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
        Call<ResponseBody> call = post.block_card(toString);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    Toast.makeText(TicketAndTracking.this, "Successfully blocked", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (response.code() == 404) {
                    Toast.makeText(TicketAndTracking.this, "Card Not Found", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (response.code() == 401) {
                    startActivity(new Intent(TicketAndTracking.this, HelperLogin.class));
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private boolean IssueProcess() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Issue Card");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText numberInput = new EditText(this);

        numberInput.setHint(R.string.last_three);
        numberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        numberInput.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(10)
        });
//        numberInput.setTextSize(40);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(15,10,0,0);
//        numberInput.setLayoutParams(params);
//        numberInput.setHeight(150);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 0, 0);
        numberInput.setLayoutParams(params);
        numberInput.setTextSize(40);
        numberInput.setHeight(150);
        layout.addView(numberInput);
        builder.setView(layout);
        builder.setPositiveButton("ok", null);
        builder.setNegativeButton("cancel", null);

        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button okay = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (GeneralUtils.isNetworkAvailable(TicketAndTracking.this)) {
                            RetrofitInterface post = ServiceConfig.createServiceWithAuth(RetrofitInterface.class);
                            Call<CallResponse.GetServerNumber> call = post.getServerNumber();
                            call.enqueue(new Callback<CallResponse.GetServerNumber>() {
                                @Override
                                public void onResponse(Call<CallResponse.GetServerNumber> call, Response<CallResponse.GetServerNumber> response) {
                                    CallResponse.GetServerNumber callResponse = response.body();
                                    if (callResponse != null) {
                                        SharedPreferences sharedPreferences = getSharedPreferences("User_NUM", MODE_PRIVATE);
                                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                        myEdit.putString("userNum", numberInput.getText().toString());
                                        myEdit.apply();
                                        getCallDetails(numberInput.getText().toString(), callResponse.getData().getServerNumber(), mAlertDialog);
                                    }
                                }

                                @Override
                                public void onFailure(Call<CallResponse.GetServerNumber> call, Throwable t) {
                                    Log.i("TAG", "onResponse: Failed" + t.getLocalizedMessage());
                                }
                            });
                        } else {
                            Toast.makeText(TicketAndTracking.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        mAlertDialog.show();

        return true;
    }

    private void getCallDetails(String userNum, final String serverNum, AlertDialog mAlertDialog1) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle("Number Received").setMessage("कृपया " + serverNum + " मा कल गर्नुहोस यसमा कुनै शुल्क लाग्दैन " + userNum);

        builder.setPositiveButton("चेक गर्नुहोस", null);
        builder.setNegativeButton("रद्द गर्नुहोस", null);


        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(TicketAndTracking.this);
        tv.setPadding(5, 5, 5, 5);
        tv.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        layout.addView(tv);
        builder.setView(layout);

        AlertDialog mAlertDialog;
        mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
        mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pClick = new ProgressDialog(TicketAndTracking.this); //Your Activity.this
                pClick.setMessage("कृपया पर्खनुहोस्...");
                pClick.setCancelable(true);
                pClick.show();
                getNumberFromServer(userNum, serverNum, pClick, mAlertDialog1, mAlertDialog);
            }
        });

        mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "रद्द गर्नुहोस",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                });
    }

    private void getNumberFromServer(String num, String userNum, ProgressDialog pClick, AlertDialog alertDialog1, AlertDialog mAlertDialog1) {

//        TODO
        RetrofitInterface post = ServiceConfig.createServiceWithAuth(RetrofitInterface.class);
        Call<CallResponse> call = post.getNumber(num, userNum);
        call.enqueue(new Callback<CallResponse>() {
            @Override
            public void onResponse(Call<CallResponse> call, Response<CallResponse> response) {
                if (response.isSuccessful()) {
                    pClick.dismiss();
                    if (userNum != null && num != null) {
                        Intent intent = new Intent(TicketAndTracking.this, IssueCardActivity.class);
                        intent.putExtra(USER_NUMBER, num);
                        startActivity(intent);
                        finish();
                        alertDialog1.dismiss();
                        mAlertDialog1.dismiss();
                    }
                } else if (response.code() == 404) {
                    pClick.dismiss();
                    alertDialog1.dismiss();
                    mAlertDialog1.dismiss();
                    handleErrors(response.errorBody());
                } else if (response.code() == 403) {
                    pClick.dismiss();
                    alertDialog1.dismiss();
                    mAlertDialog1.dismiss();
                    handleErrors(response.errorBody());
                } else {
                    alertDialog1.dismiss();
                    mAlertDialog1.dismiss();
                    pClick.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CallResponse> call, Throwable t) {
                pClick.dismiss();
                Log.i("TAG", "onResponse: Failed" + t.getLocalizedMessage());
            }
        });
    }

    private void handleErrors(ResponseBody responseBody) {
        ApiError apiErrors = GeneralUtils.convertErrors(responseBody);

        if (responseBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                if (error.getKey().equals("message")) {
                    Toast.makeText(this, error.getValue().get(0), Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        } else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void setMode(int modeType, int spanCount, String modeStr) {
        preferences.edit().putInt(UtilStrings.MODE, modeType).apply();
        gridLayoutManager = new GridLayoutManager(TicketAndTracking.this, spanCount);
        priceListView.setLayoutManager(gridLayoutManager);

//
        mode_selector.setText(modeStr);
        switch (modeType) {
            case UtilStrings.MODE_1:
                priceListView.setAdapter(new PriceAdapter(priceLists, this, printHandler));
                break;
            case UtilStrings.MODE_2:
//                price bata dekhaune yo chai
                priceListView.setAdapter(new PriceAdapterPlaces(priceLists, this, printHandler));
                break;
            case UtilStrings.MODE_3:
//                 location bata dekhaune
                priceListView.setAdapter(priceAdapterPrices);
                break;
        }
    }

    private void isToday() {
        String isToday = preferences.getString(UtilStrings.DATE_TIME, "");
        if (!isToday.equals(GeneralUtils.getDate())) {
            preferences.edit().putString(UtilStrings.DATE_TIME, GeneralUtils.getDate()).apply();
            preferences.edit().remove(UtilStrings.TOTAL_TICKETS).apply();
            preferences.edit().remove(UtilStrings.TOTAL_COLLECTIONS).apply();
            preferences.edit().remove(UtilStrings.TOTAL_COLLECTIONS_CARD).apply();
            preferences.edit().remove(UtilStrings.TOTAL_COLLECTIONS_BY_CARD).apply();
            preferences.edit().remove(UtilStrings.TOTAL_COLLECTIONS_BY_CASH).apply();
            preferences.edit().remove(UtilStrings.TOTAL_COLLECTIONS_BY_QR).apply();
            databaseHelper.clearAllFromPassengerTime();
            new DatabaseHelper(this).clearTxtTable();
            setTotal();
            isFirstRun = preferences.getBoolean(UtilStrings.FIRST_RUN, true);


            /*TicketInfoDataPush.resetData(TicketAndTracking.this);*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadStorageAllowed()) {
                    new GetAdvertisements(this).getAdv();
                }
            } else {
                new GetAdvertisements(this).getAdv();
            }

        } else {
            if (databaseHelper.noticeAdSize() == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isReadStorageAllowed()) {
                        new GetAdvertisements(this).getAdv();
                    }
                } else {
                    new GetAdvertisements(this).getAdv();
                }
            }
        }
    }

    public void setPriceLists(boolean discountToogle) {
        priceLists = databaseHelper.priceLists(discountToogle);
        if (mode == UtilStrings.MODE_1) {
            priceListView.setAdapter(new PriceAdapter(priceLists, TicketAndTracking.this, printHandler));
        } else {
            priceListView.setAdapter(new PriceAdapterPlaces(priceLists, TicketAndTracking.this, printHandler));
        }
        /*saveBitmap(getBitmapFromView(priceListView));*/
        setTotal();

    }

    private void saveBitmap(Bitmap bitmap) {
        File deviceScreenShotPath = new File("/storage/sdcard0/DCIM/Camera/file.jpg");


        FileOutputStream fos;
        try {
            fos = new FileOutputStream(deviceScreenShotPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);

        }

    }

    public void setTotal() {
        totalTickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
        totalCollections = preferences.getInt(UtilStrings.TOTAL_COLLECTIONS, 0);
        //        totalCollectionTickets.setText("Total Tickets :" + String.valueOf(totalTickets) + "\n Total Colletions :" + String.valueOf(totalCollections));
        totalCollectionTickets.setText(getString(R.string.total_tickets) + GeneralUtils.getUnicodeNumber(String.valueOf(totalTickets)) + "\n" + getString(R.string.total_collections) + GeneralUtils.getUnicodeNumber(String.valueOf(totalCollections)));


        if (passengerCountLists != null) {
            totalPassenger.setText(String.valueOf(passengerCountLists.size()));
        }

    }

    private void startTrackingService(boolean checkPermission, boolean permission) {
        if (checkPermission) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permission = true;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, PERMISSIONS_REQUEST_LOCATION);
                }
                return;
            }

        }


        if (permission) {
            ContextCompat.startForegroundService(this, new Intent(this, TrackingService.class));
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    ALARM_MANAGER_INTERVAL, ALARM_MANAGER_INTERVAL, alarmIntent);

        } else {

        }
    }

    private void stopTrackingService() {
        alarmManager.cancel(alarmIntent);
        this.stopService(new Intent(this, TrackingService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                } else {

                    if (!isReadStorageAllowed()) {
                        requestStoragePermission();
                    }
                }
            }

            startTrackingService(false, granted);
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new GetAdvertisements(this).getAdv();
            } else {
                requestStoragePermission();

            }
        } else if (requestCode == PERMISSION_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
//                acceptCallPermission();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //        List<HelperList> helperLists = databaseHelper.helperLists();
        //        for (int i = 0; i < helperLists.size(); i++) {
        //            HelperList helperList = databaseHelper.helperLists().get(i);
        //
        //            menu.add(0, i, 0, helperList.helper_id + "-" + helperList.helper_name);
        //        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //        int id = item.getItemId(); //to get the selected menu id
        //        String name = (String) item.getTitle(); //to get the selected menu name
        //        Log.i("menuItem", name + "");
        //        String helperNameId[] = name.split("-");
        //
        //        /*helperName = helperNameId[1];*/
        //        preferences.edit().putString(UtilStrings.ID_HELPER, helperNameId[0]).apply();
        //        preferences.edit().putString(UtilStrings.NAME_HELPER, helperNameId[1]).apply();
        //
        //        helperName.setText(preferences.getString(UtilStrings.NAME_HELPER, ""));
        //        return super.onOptionsItemSelected(item);
        return true;
    }
//    commented for new code 7/28/2020

    @Override
    protected void onStart() {
        super.onStart();
        isToday();
    }

    //    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        isToday();
//    }
    //    commented for new code 7/28/2020
    public void interValDataPush() {
        rHandler = new Handler();
        rTicker = new Runnable() {
            public void run() {
                long now = SystemClock.uptimeMillis();
                long next = now + 20000;
                i++;
                if (i >= 10) {
                    i = 0;
                }
                isToday();

                totalTickets = preferences.getInt(UtilStrings.TOTAL_TICKETS, 0);
                if (totalTickets != preferences.getInt(UtilStrings.SENT_TICKET, 0))
                    TicketInfoDataPush.pushBusData(TicketAndTracking.this, totalTickets, totalCollections);

                if (databaseHelper.listTickets().size() > 0) {
                    databaseHelper.ticketInfoLists();
                }
                Log.i(TAG, "run: " + totalCount);

                rHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (databaseHelper.passengerCountLists().size() > 0) {
                            PassengerCountUpdate.pushPassengerCount(TicketAndTracking.this, String.valueOf(totalCount));
                        }
                    }
                }, 15000);


                totalRemainingTickets.setText(GeneralUtils.getUnicodeNumber(String.valueOf(databaseHelper.listTickets().size())) + "\n" + GeneralUtils.getUnicodeNumber(String.valueOf(databaseHelper.remainingAmount())));
                rHandler.postAtTime(rTicker, next);
            }
        };
        rTicker.run();

    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, STORAGE_PERMISSION_CODE);

    }
//    private boolean acceptCallPermission(){
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
//                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
//                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
//           return true;
//            }
//        }
//        return false;
//    }

    private boolean isReadStorageAllowed() {

        //Getting the permission status
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    @Override
    public void onPriceUpdate() {
        startActivity(new Intent(this, TicketAndTracking.class));
        finish();
    }

    @Override
    public void onLocationUpdated() {
        priceAdapterPrices = new PriceAdapterPrices(routeStationListsForInfinite, this, databaseHelper, printHandler);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        priceListView.setLayoutManager(gridLayoutManager);
        getNearestLocToTop();

    }

    private void updateTotalPassengerCount(int orderPos) {
        passengerCountLists=databaseHelper.passengerCountLists();
        if(databaseHelper.passengerCountLists().size()>0){
//            totalPassenger.setText(String.valueOf(passengerCountLists.size()));
            for (int i1 = 0; i1 < databaseHelper.passengerCountLists().size(); i1++) {
                Log.i(TAG, "getNearestLocToTop: "+passengerCountLists.get(i1).toString());
                Log.i(TAG, "getNearestLocToTop: "+i1+"  "+databaseHelper.passengerCountLists().get(i1).passenger_station_position+"  "+databaseHelper.passengerCountLists().get(i1).passenger_direction);
                if(databaseHelper.passengerCountLists().get(i1).passenger_station_position<=orderPos){
                    databaseHelper.updatePassengerCountForward(passengerCountLists.get(i1).id);
                    Log.i(TAG, "getNearestLocToTop: "+passengerCountLists.size());

                }
//                else if(databaseHelper.passengerCountLists().get(i1).passenger_station_position>=orderPos){
//                    databaseHelper.updatePassengerCountBackward(orderPos);
//                }
            }
        }
        if (passengerCountLists != null) {
            totalPassenger.setText(String.valueOf(passengerCountLists.size()));
        }

    }
}