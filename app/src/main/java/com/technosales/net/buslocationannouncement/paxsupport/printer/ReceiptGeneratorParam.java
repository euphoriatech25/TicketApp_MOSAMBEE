/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2019-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                  Author	                 Action
 * 20190108  	         Steven.W                Create
 * ===========================================================================================
 */
package com.technosales.net.buslocationannouncement.paxsupport.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.pax.glwrapper.imgprocessing.IImgProcessing;
import com.pax.glwrapper.impl.GL;
import com.pax.glwrapper.page.IPage;
import com.technosales.net.buslocationannouncement.TicketBusApp;

import java.util.ArrayList;
import java.util.List;

abstract class ReceiptGeneratorParam implements IReceiptGenerator {

    public ReceiptGeneratorParam() {
        //do nothing
    }

    @Override
    public Bitmap generateBitmap() {
        return null;
    }

    public List<Bitmap> generateBitmaps(String print) {
        Context context = TicketBusApp.getApp();
        List<IPage> pages = generatePages(print, context);
        IImgProcessing imgProcessing = GL.getGL().getImgProcessing();
        List<Bitmap> bitmaps = new ArrayList<>();


        for (IPage i : pages) {
            Bitmap bitmap = imgProcessing.pageToBitmap(i, 384);
            if (bitmap != null) {
                bitmaps.add(bitmap);
            }
        }
        return bitmaps;
    }


    public static Bitmap drawTextBitmap(String text) {

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
//        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(35);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint, 380, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

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

    protected abstract List<IPage> generatePages(String print, Context context);

    @Override
    public String generateString() {
        return "";
    }
}