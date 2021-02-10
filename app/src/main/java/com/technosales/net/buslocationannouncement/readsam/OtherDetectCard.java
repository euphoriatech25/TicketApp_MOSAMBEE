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
 * 20190108  	         XuShuang                Create
 * ===========================================================================================
 */
package com.technosales.net.buslocationannouncement.readsam;

public class OtherDetectCard {
//        extends Service {
//    private static final String TAG = "OtherDetectCard";
//    private static final int ICC_REVERSE = 97;
//    private IDAL dal = TicketBusApp.getDal();
//    private IIcc icc = dal.getIcc();
//    private IMag mag = dal.getMag();
//    private boolean running = false;
//    private Byte readType = 0;
//    private Byte iccSlot = 0;
//    private boolean isDetcting = false;
//    private boolean isSwiped = false;
//    private boolean magDisabled = false;
//    private boolean iccDisabled = false;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        running = true;
//        readType = intent.getByteExtra("readType", (byte) 0);
//        iccSlot = intent.getByteExtra("iccSlot", (byte) 0);
//        startDetect();
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    private void startDetect() {
//        TicketBusApp.getApp().runInBackground(new Runnable() {
//            @Override
//            public void run() {
//                while (running && !iccDisabled) {
//                    detectIcCard(readType);
//                }
//            }
//        });
//
//        TicketBusApp.getApp().runInBackground(new Runnable() {
//            @Override
//            public void run() {
//                while (running && !magDisabled) {
//                    detectSwipeCard(readType);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        running = false;
//        Log.i(TAG, "stop service");
//    }
//
//
//    private void detectIcCard(Byte readerType) {
//        if ((readType & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
//            try {
//                icc.close((byte) 0);
//            } catch (IccDevException e) {
//                showDisableMsg(String.format("%s%s", e.getErrModule(), e.getErrMsg()));
//                e.printStackTrace();
//            }
//        }
//        while (running && !iccDisabled) {
//            try {
//                if ((readerType & EReaderType.ICC.getEReaderType()) == EReaderType.ICC.getEReaderType()) {
//                    if (icc.detect(iccSlot)) {
//                        isDetcting = true;
//                        byte[] res = icc.init(iccSlot);
//                        if (res != null) {
//                            TicketBusApp.getApp().doEvent(new OtherDetectEvent(EReaderType.ICC.getEReaderType()));
//                            Log.i(TAG, "icc.detect = $readerType");
//                            running = false;
//                            break;
//                        } else {
//                            showNotice(R.string.icc_error_swipe_card);
//                        }
//                    } else {
//                        if (isDetcting) {
//                            isDetcting = false;
//                            SystemClock.sleep(1000);
//                            if (!isSwiped) {
//                                showNotice(R.string.remove_read_card_error);
//                            } else {
//                                isSwiped = false;
//                            }
//                        }
//                    }
//                    SystemClock.sleep(5);
//                }
//            } catch (IccDevException e) {
//                showNotice(R.string.icc_error_swipe_card);
//                e.printStackTrace();
//                if (e.getErrCode() == ICC_REVERSE) {
//                    TicketBusApp.getApp().doEvent(new OtherDetectEvent(EReaderType.ICC.getEReaderType()));
//                    running = false;
//                    break;
//                }
//                //close icc function from pax store
//                if (e.getErrCode() == NoticeSwipe.FUNC_SEARCH_CLOSED) {
//                    iccDisabled = true;
//                    TicketBusApp.getApp().doEvent(new NoticeSwipe("ICC"));
//                    break;
//                }
//            }
//
//        }
//    }
//
//    private void showNotice(int resId) {
//        if ("A60".equals(Device.getDeviceModel())) {
//            TicketBusApp.getApp().doEvent(new NoticeSwipe(TicketBusApp.getApp().getResources().getString(resId)));
//        }
//    }
//
//    private void showDisableMsg(String errMsg) {
//        TicketBusApp.getApp().doEvent(new NoticeSwipe(errMsg));
//    }

}
