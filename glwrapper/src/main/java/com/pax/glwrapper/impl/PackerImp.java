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

import com.pax.gl.pack.IApdu;
import com.pax.gl.pack.IIso8583;
import com.pax.gl.pack.ITlv;
import com.pax.gl.pack.impl.PaxGLPacker;
import com.pax.glwrapper.packer.IPacker;

class PackerImp implements IPacker {

    private PaxGLPacker paxGLPacker;

    PackerImp(Context context) {
        paxGLPacker = PaxGLPacker.getInstance(context);
    }

    @Override
    public IApdu getApdu() {
        return paxGLPacker.getApdu();
    }

    @Override
    public IIso8583 getIso8583() {
        return paxGLPacker.getIso8583();
    }

    @Override
    public ITlv getTlv() {
        return paxGLPacker.getTlv();
    }
}
