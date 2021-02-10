package com.technosales.net.buslocationannouncement.paxsupport.scanner;

import android.os.Handler;
import android.os.Message;

import com.pax.dal.IScanner;
import com.pax.dal.IScanner.IScanListener;
import com.pax.dal.entity.EScannerType;
import com.technosales.net.buslocationannouncement.TicketBusApp;

public class ScannerPax {
    private static ScannerPax cameraTester;

    private static EScannerType scannerType;

    private IScanner scanner;

    private ScannerPax(EScannerType type) {
        ScannerPax.scannerType = type;
        scanner = TicketBusApp.getDal().getScanner(scannerType);
    }

    public static ScannerPax getInstance(EScannerType type) {
        if (cameraTester == null || type != scannerType) {
            cameraTester = new ScannerPax(type);
        }
        return cameraTester;
    }

    public void scan(final Handler handler,int timeout) {
        scanner.open();
        setTimeOut(timeout);
        scanner.setContinuousTimes(1);
        scanner.setContinuousInterval(1000);
        scanner.start(new IScanListener() {
            @Override
            public void onRead(String arg0) {
                Message message = Message.obtain();
                message.what = 0;
                message.obj = arg0;
                handler.sendMessage(message);
            }

            @Override
            public void onFinish() {
                close();
            }

            @Override
            public void onCancel() {
                Message message = Message.obtain();
                message.what = 0;
                message.obj = "Empty";
                handler.sendMessage(message);
                close();
            }
        });

    }

    public void close() {
        scanner.close();
    }
    
    public void setTimeOut(int timeout){
        scanner.setTimeOut(timeout);

    }

}
