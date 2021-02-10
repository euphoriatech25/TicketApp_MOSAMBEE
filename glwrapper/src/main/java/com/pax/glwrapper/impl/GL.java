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

import com.pax.glwrapper.IGL;
import com.pax.glwrapper.imgprocessing.IImgProcessing;

public class GL implements IGL {

    private CommHelperImp comm;
    private PackerImp packer;
    private ImgProcessingImp imgProcessing;

    private static GL instance = null;

    private GL(Context context) {
        comm = new CommHelperImp(context);
        packer = new PackerImp(context);
        imgProcessing = new ImgProcessingImp(context);
    }

    public static void init(Context context){
        if (instance == null) {
            instance = new GL(context);
        }
    }

    public static GL getGL() {
        return instance;
    }



    @Override
    public IImgProcessing getImgProcessing() {
        return imgProcessing;
    }
}
