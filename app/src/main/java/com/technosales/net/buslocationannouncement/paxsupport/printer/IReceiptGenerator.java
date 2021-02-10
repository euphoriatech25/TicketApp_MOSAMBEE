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

/**
 * receipt generator
 *
 * @author Steven.W
 */
public interface IReceiptGenerator {

    String TAG = "ReceiptGenerator";

    int FONT_BIG = 30;
    int FONT_NORMAL = 24;
    int FONT_SMALL = 19;

    /**
     * generate receipt
     *
     * @return
     */
    Bitmap generateBitmap();

    /**
     * generate simplified receipt string
     */
    String generateString();
}
