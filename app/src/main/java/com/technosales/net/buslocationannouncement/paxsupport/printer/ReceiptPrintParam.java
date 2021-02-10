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

import android.graphics.Bitmap;

import java.util.List;

import static com.pax.dal.entity.EFontTypeAscii.FONT_16_32;
import static com.pax.dal.entity.EFontTypeExtCode.FONT_32_16;


/**
 * print receipt
 *
 * @author Steven.W
 */
public class ReceiptPrintParam {


    public void print(String print,PrintListener listener) {
//        this.listener = listener;
        if (listener != null)
            listener.onShowMessage(null, "Please Wait, Receipt Printing");

        ReceiptGeneratorParam receiptGeneratorParam =new ReceiptGeneratorTicketParam();

        List<Bitmap> bitmaps=receiptGeneratorParam.generateBitmaps(print);
        for(Bitmap i:bitmaps){
            printTransaction(i);
        }

        if (listener != null) {
            listener.onEnd();
        }
    }
    public void printTransaction(final Bitmap printData) {
        new Thread(new Runnable() {
            public void run() {
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().fontSet(FONT_16_32, FONT_32_16);
                PrinterTester.getInstance().printBitmap(printData);
                PrinterTester.getInstance().step(80);
                PrinterTester.getInstance().setGray(100);
                PrinterTester.getInstance().start();
            }
        }).start();
    }

}
