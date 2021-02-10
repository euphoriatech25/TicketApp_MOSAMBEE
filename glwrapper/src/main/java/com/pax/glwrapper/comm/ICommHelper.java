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
package com.pax.glwrapper.comm;

import com.pax.gl.commhelper.IBtLeScanner;
import com.pax.gl.commhelper.IBtScanner;
import com.pax.gl.commhelper.IBtServer;
import com.pax.gl.commhelper.ICommBt;
import com.pax.gl.commhelper.ICommSslClient;
import com.pax.gl.commhelper.ICommTcpClient;
import com.pax.gl.commhelper.IHttpClient;
import com.pax.gl.commhelper.IHttpsClient;
import com.pax.gl.commhelper.ISslKeyStore;
import com.pax.gl.commhelper.ITcpServer;

public interface ICommHelper {
    IBtScanner getBtScanner();

    IBtLeScanner getBtLeScanner();

    ICommBt createBt(String identifier);

    ICommBt createBt(String identifier, boolean useBle);

    ISslKeyStore createSslKeyStore();

    ICommSslClient createSslClient(String host, int port, ISslKeyStore keystore);

    ICommTcpClient createTcpClient(String host, int port);

    IHttpClient createHttpClient();

    IHttpsClient createHttpsClient(ISslKeyStore keyStore);

    ITcpServer createTcpServer(int port, int maxTaskNum, ITcpServer.IListener listener);

    IBtServer createBtServer(int maxTaskNum, IBtServer.IListener listener);
}

/* Location:           D:\Android逆向助手_v2.2\PaxGL_V1.00.04_20170303.jar
 * Qualified Name:     com.pax.gl.comm.ICommHelper
 * JD-Core Version:    0.6.0
 */