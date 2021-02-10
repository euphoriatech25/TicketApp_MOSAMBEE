package com.technosales.net.buslocationannouncement.paxsupport.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;

import com.pax.glwrapper.page.IPage;
import com.technosales.net.buslocationannouncement.R;

import java.util.ArrayList;
import java.util.List;

public class ReceiptGeneratorTicketParam extends ReceiptGeneratorParam implements IReceiptGenerator {
    public ReceiptGeneratorTicketParam() {
    }

    @Override
    protected List<IPage> generatePages(String print, Context context) {
        List<IPage> pages = new ArrayList<>();
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.person);
        Bitmap smallIcon=getResizedBitmap(icon,50);
        IPage page = Device.generatePage();
        page.addLine().addUnit(page.createUnit().setBitmap(smallIcon).setGravity(Gravity.CENTER));
        page.addLine().addUnit(page.createUnit().setText("360 Transport Solutions").setFontSize(25).setWeight(10.0F).setGravity(Gravity.CENTER));
        page.addLine().addUnit(page.createUnit().setText(context.getString(R.string.company_location)).setFontSize(18).setGravity(Gravity.CENTER));
         page.addLine().addUnit(page.createUnit().setText("------------------------").setGravity(Gravity.CENTER));
        page.addLine()
                .addUnit(page.createUnit()
                        .setText("\n"+print)
                        .setWeight(10.0f)
                        .setFontSize(30)
                        .setGravity(Gravity.LEFT));
        pages.add(page);
        page = Device.generatePage();
        pages.add(page);
        return pages;
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
