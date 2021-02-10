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
 * 20190108  	         Kim.L                   Create
 * ===========================================================================================
 */
package com.pax.glwrapper.impl;

import android.content.Context;
import android.graphics.Bitmap;

import com.pax.gl.impl.ImgProcessing;
import com.pax.glwrapper.imgprocessing.IImgProcessing;
import com.pax.glwrapper.page.IPage;

class ImgProcessingImp implements IImgProcessing {

    private ImgProcessing imgProcessing;
    private PaxGLPage paxGLPage;

    ImgProcessingImp(Context context) {
        imgProcessing = ImgProcessing.getInstance(context);
        paxGLPage = new PaxGLPage(context);
    }

    @Override
    public IPage createPage() {
        return paxGLPage.createPage();
    }

    @Override
    public Bitmap pageToBitmap(IPage page, int pageWidth) {
        return paxGLPage.pageToBitmap(page, pageWidth);
    }
}
