package com.technosales.net.buslocationannouncement.mosambeesupport;

import android.os.RemoteException;
import android.util.Log;

import com.morefun.yapi.device.beeper.BeepModeConstrants;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.SDKManager;

public class BeepLEDTest {
    public static void beepSuccess() throws RemoteException {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        mSDKManager.getBeeper().beep(BeepModeConstrants.SUCCESS);
    }

    public static void beepFailed() throws RemoteException {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        mSDKManager.getBeeper().beep(BeepModeConstrants.FAIL);
    }

    public static void beepError() throws RemoteException {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        mSDKManager.getBeeper().beep(BeepModeConstrants.ERROR);
    }
}
