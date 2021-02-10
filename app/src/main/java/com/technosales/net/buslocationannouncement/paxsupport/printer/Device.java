package com.technosales.net.buslocationannouncement.paxsupport.printer;

import android.os.Build;
import android.os.SystemClock;
import androidx.core.content.res.ResourcesCompat;

import com.pax.dal.ICardReaderHelper;
import com.pax.dal.entity.EBeepMode;
import com.pax.dal.entity.ENavigationKey;
import com.pax.dal.entity.EReaderType;
import com.pax.dal.entity.PollingResult;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PiccDevException;
import com.pax.glwrapper.impl.GL;
import com.pax.glwrapper.page.IPage;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.TicketBusApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Device {
    private static final String TAG = "Device";

    private Device() {
        //do nothing
    }

    /**
     * beep ok
     */
    public static void beepOk() {
        TicketBusApp.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_3, 100);
        TicketBusApp.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_4, 100);
        TicketBusApp.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_5, 100);
    }

    /**
     * beep error
     */
    public static void beepErr() {
        if (TicketBusApp.getDal() != null) {
            TicketBusApp.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_6, 200);
        }
    }

    /**
     * beep prompt
     */
    public static void beepPrompt() {
        TicketBusApp.getDal().getSys().beep(EBeepMode.FREQUENCE_LEVEL_6, 50);
    }


    public static String getTime(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);
        return dateFormat.format(new Date());
    }

    /**
     * enable/disable status bar
     *
     * @param enable true/false
     */
    public static void enableStatusBar(boolean enable) {
        TicketBusApp.getDal().getSys().enableStatusBar(enable);
    }

    /**
     * enable/disable home and recent key
     *
     * @param enable true/false
     */
    public static void enableHomeRecentKey(boolean enable) {
        TicketBusApp.getDal().getSys().enableNavigationKey(ENavigationKey.HOME, enable);
        TicketBusApp.getDal().getSys().enableNavigationKey(ENavigationKey.RECENT, enable);
    }

    public static IPage generatePage() {
        IPage page = GL.getGL().getImgProcessing().createPage();
        page.adjustLineSpace(-9);
        page.setTypeFace(ResourcesCompat.getFont(TicketBusApp.getApp(), R.font.verdana));
        return page;
    }

    public interface RemoveCardListener {
        void onShowMsg(PollingResult result);
    }

    public static void removeCard(RemoveCardListener listener) {
        boolean needShow = true;
        ICardReaderHelper helper = TicketBusApp.getDal().getCardReaderHelper();

        try {
            PollingResult result;
            while ((result = helper.polling(EReaderType.ICC_PICC, 100)).getReaderType() == EReaderType.ICC || result.getReaderType() == EReaderType.PICC) {
                // remove card prompt
                if (listener != null && needShow) {
                    needShow = false;
                    listener.onShowMsg(result);
                }
                SystemClock.sleep(500);
                Device.beepErr();
            }
        } catch (MagDevException | IccDevException | PiccDevException e) {
            //ignore the warning
        }
    }





    public static String getDeviceModel() {
        String termModel = Build.MODEL.toUpperCase(); //机器型号
        return termModel;
    }
}