package com.technosales.net.buslocationannouncement;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.pax.dal.IDAL;
import com.pax.glwrapper.impl.GL;
import com.pax.neptunelite.api.NeptuneLiteUser;

public class TicketBusApp extends Application {
    private static IDAL dal;
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
        dal = getDal();
        handler = new Handler();
        GL.init(appContext);

    }

    // getter
    public static TicketBusApp getApp() {
        return appContext;
    }

    public static IDAL getDal() {
        if (dal == null) {
            try {
                long start = System.currentTimeMillis();
                dal = NeptuneLiteUser.getInstance().getDal(appContext);
                Log.i("Test", "get dal cost:" + (System.currentTimeMillis() - start) + " ms");
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(appContext, "error occurred,DAL is null.", Toast.LENGTH_LONG).show();
            }
        }
        return dal;
    }
    public void runOnUiThread(final Runnable runnable) {
        handler.post(runnable);
    }

}


