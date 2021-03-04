package com.technosales.net.buslocationannouncement.mosambeesupport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.printer.MultipleAppPrinter;
import com.morefun.yapi.device.printer.OnPrintListener;
import com.morefun.yapi.device.printer.PrinterConfig;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.SDKManager;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

public class Printer {
    private static final String TAG = "PrinterTest";

    public static void Print(Context context, String path, Handler handler) throws RemoteException {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        String heading = context.getString(R.string.heading);
        Bitmap bmpHeader = drawTextBitmap(heading, 20, false,Layout.Alignment.ALIGN_CENTER);

        String newString =path + "\n\n\n";
        Bitmap bmp = drawTextBitmap(newString, 23, false, Layout.Alignment.ALIGN_NORMAL);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.person);
        Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.download);
        Bitmap smallIcon = GeneralUtils.getResizedBitmap(icon, 40);
        Bitmap img=GeneralUtils.mergeToPin(icon1,smallIcon);
        PrintImage(mSDKManager, img,bmpHeader,bmp,handler);
    }


    public static Bitmap drawTextBitmap(String text, int textSize, boolean isBold, Layout.Alignment align) {

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
//        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(textSize);

        if (isBold)
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));


        StaticLayout mTextLayout = new StaticLayout(text, textPaint, 380, align, 1.0f, 0.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(380, mTextLayout.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.parseColor("#000000"));
        paint.setColor(Color.parseColor("#ffffff"));
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();
        return b;
    }


    public static void PrintImage(DeviceServiceEngine engine, Bitmap icon, Bitmap header, Bitmap value, Handler handler) throws RemoteException {
        MultipleAppPrinter printer = engine.getMultipleAppPrinter();
        Bundle config = new Bundle();
        config.putInt(PrinterConfig.COMMON_GRAYLEVEL, 30);
        printer.printImage(icon, new OnPrintListener.Stub() {
            @Override
            public void onPrintResult(int result) throws RemoteException {
                printer.printImage(header, new OnPrintListener.Stub() {
                    @Override
                    public void onPrintResult(int result) throws RemoteException {
                        Log.d(TAG, "onPrintResult = " + result);
                        printer.printImage(value, new OnPrintListener.Stub() {
                            @Override
                            public void onPrintResult(int result) throws RemoteException {
                                if(result== -1005){
                                    Message messageCardId = new Message();
                                    messageCardId.what = 505;
                                    messageCardId.obj = "मुद्रण कागज समाप्त भयो। कृपया जाँच गर्नुहोस्।";
                                    handler.sendMessage(messageCardId);
                                }
                            }
                        }, config);
                    }
                }, config);
//                listener.showMessage(result == ServiceResult.Success ? context.getString(R.string.msg_succ) : context.getString(R.string.msg_fail));
            }
        }, config);

    }
}
