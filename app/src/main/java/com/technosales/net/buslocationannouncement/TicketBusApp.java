package com.technosales.net.buslocationannouncement;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


public class TicketBusApp extends Application {

    private static TicketBusApp appContext;
    private static Handler handler;

    private static Context appCon;
    private static TicketBusApp instance;


    public static TicketBusApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return appContext;
    }

    public static Context getAppCon() {
        return appCon;
    }

    public void setAppContext(Context appCon) {
        this.appCon = appCon;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        handler = new Handler();
        SDKManager.getInstance().bindService(getApplicationContext());


    }

    // getter
    public static TicketBusApp getApp() {
        return appContext;
    }


    public void runOnUiThread(final Runnable runnable) {
        handler.post(runnable);
    }

}


