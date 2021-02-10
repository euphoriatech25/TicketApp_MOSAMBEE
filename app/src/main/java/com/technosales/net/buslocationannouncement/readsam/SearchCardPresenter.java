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
 * 20190108  	         xieYb                   Modify
 * ===========================================================================================
 */

package com.technosales.net.buslocationannouncement.readsam;

//import com.pax.abl.core.AAction;
//import com.pax.abl.core.ActionResult;
//import com.pax.abl.utils.EncUtils;
//import com.pax.abl.utils.TrackUtils;
//import com.pax.commonlib.utils.LogUtils;
//import com.pax.dal.entity.EReaderType;
//import com.pax.dal.entity.PollingResult;
//import com.pax.data.entity.Issuer;
//import com.pax.device.Device;
//import com.pax.edc.R;
//import com.pax.eventbus.EmvCallbackEvent;
//import com.pax.pay.app.FinancialApplication;
//import com.pax.pay.trans.TransContext;
//import com.pax.pay.trans.TransResult;
//import com.pax.pay.trans.action.ActionInputPassword;
//import com.pax.pay.trans.action.ActionSearchCard;
//import com.pax.pay.utils.MyCardReaderHelper;
//import com.pax.pay.utils.ServiceReadType;
//import com.pax.settings.SysParam;


public class SearchCardPresenter {
//    extends SearchCardContract.Presenter

//    private static final String TAG = "SearchCardPresenter";
//    private boolean isManualMode = false;
//    private ActionInputPassword inputPasswordAction = null;
//    private final AAction currentAction = TransContext.getInstance().getCurrentAction();
//    private int retryTime = 3;
//    private String cardNo = null; // 卡号
//    private Intent iDetectCard = null;
//    private EReaderType readerType = null; // 读卡类型
//    private SearchCardThread searchCardThread = null;
//    private PollingResult pollingResult = new PollingResult();
//    private byte mode = 0; // 寻卡模式
//    public float iccAdjustPercent = 0;
//    private boolean iccEnable = true;
//
//    public SearchCardPresenter(Context context) {
//        super(context);
//    }
//
//    @Override
//    public void initParam(Byte mode) {
//        this.mode = mode;
//        this.readerType = toReaderType(mode);
//        this.iccEnable = (mode & ActionSearchCard.SearchMode.INSERT) == ActionSearchCard.SearchMode.INSERT;
//        LogUtils.d(TAG, "mode:$mode");
//    }
//
//    /**
//     * 获取ReaderType
//     *
//     * @param mode [SearchMode]
//     * @return [EReaderType]
//     */
//    private EReaderType toReaderType(Byte mode) {
//        byte newMode = (byte) (mode & (~ActionSearchCard.SearchMode.KEYIN));
//        EReaderType[] types = EReaderType.values();
//        for (EReaderType type : types) {
//            if (type.getEReaderType() == newMode)
//                return type;
//        }
//        return null;
//    }
//
//    @Override
//    public void runSearchCard() {
//        startDetect();
//        runSearchCardThread();
//    }
//
//    @Override
//    public void stopDetectCard() {
//        MyCardReaderHelper.getInstance().stopPolling();
//        if (iDetectCard != null) {
//            getContext().stopService(iDetectCard);
//        }
//    }
//
//    private void startDetect() {
//        //start other detect service
//        ServiceReadType.getInstance().setReadType(EReaderType.DEFAULT.getEReaderType());
//        iDetectCard = new Intent(getContext(), OtherDetectCard.class);
//        iDetectCard.putExtra("readType", readerType.getEReaderType());
//        iDetectCard.putExtra("iccSlot", (byte) 0);
//        getContext().startService(iDetectCard);
//    }
//
//    private void runSearchCardThread() {
//        if (searchCardThread != null && searchCardThread.getState() == Thread.State.TERMINATED) {
//            MyCardReaderHelper.getInstance().stopPolling();
//            searchCardThread.interrupt();
//        }
//        isManualMode = false;
//        searchCardThread = new SearchCardThread();
//        searchCardThread.start();
//    }
//
//    private void onReadCardCancel() {
//        if (!isManualMode) { // AET-179
//            LogUtils.i(TAG, "SEARCH CARD CANCEL");
//            MyCardReaderHelper.getInstance().stopPolling();
//            proxyView.finish(new ActionResult(TransResult.ERR_USER_CANCEL, null));
//        }
//    }
//
//    @Override
//    public void runInputMerchantPwdAction() {
//        inputPasswordAction = new ActionInputPassword(new AAction.ActionStartListener() {
//
//            @Override
//            public void onStart(AAction action) {
//                ((ActionInputPassword) action).setParam(getContext(), 6,
//                        getContext().getString(R.string.prompt_merchant_pwd), null, false);
//                ((ActionInputPassword) action).setParam(TransResult.ERR_USER_CANCEL);
//            }
//        });
//
//        inputPasswordAction.setEndListener(new AAction.ActionEndListener() {
//
//            @Override
//            public void onEnd(AAction action, ActionResult result) {
//                TransContext.getInstance().setCurrentAction(currentAction);
//
//                if (result.getRet() != TransResult.SUCC) {
//                    //AET-156
//                    proxyView.finish(new ActionResult(result.getRet(), null));
//                    return;
//                }
//
//                String data = EncUtils.sha1((String) result.getData());
//                if (!data.equals(SysParam.getInstance().getString(R.string.SEC_MERCHANT_PWD))) {
//                    //retry three times
//                    retryTime--;
//                    if (retryTime > 0) {
//                        // AET-110, AET-157
//                        proxyView.showManualPwdErr();
//                    } else {
//                        proxyView.finish(new ActionResult(TransResult.ERR_PASSWORD, null));
//                    }
//                    return;
//                }
//
//                FinancialApplication.getApp().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        onVerifyManualPan();
//                    }
//                });
//            }
//        });
//
//        inputPasswordAction.execute();
//    }
//
//    //AET-158
//
//    public void processManualCardNo(String content) {
//        if (content.length() < 13) {
//            proxyView.onEditCardNoError();
//        } else {
//            isManualMode = true;
//            MyCardReaderHelper.getInstance().stopPolling();
//            proxyView.onEditCardNo();
//            cardNo = proxyView.getCardNo().replace(" ", "");
//            runInputMerchantPwdAction();
//        }
//    }
//
//    @Override
//    public void onTimerFinish() {
//        TransContext.getInstance().setCurrentAction(currentAction);
//        currentAction.setFinished(false); //AET-253
//        if (inputPasswordAction != null)
//            inputPasswordAction.setResult(new ActionResult(TransResult.ERR_TIMEOUT, null));
//        FinancialApplication.getApp().doEvent(new EmvCallbackEvent(EmvCallbackEvent.Status.TIMEOUT));
//    }
//
//    @Override
//    public void onVerifyManualPan() {
//        String date = proxyView.getExpDate().replace("/", "");
//        if (!date.isEmpty()) {
//            date = date.substring(2) + date.substring(0, 2);// 将MMyy转换成yyMM
//        }
//
//        Issuer matchedIssuer = FinancialApplication.getAcqManager().findIssuerByPan(cardNo);
//        if (matchedIssuer == null || !FinancialApplication.getAcqManager().isIssuerSupported(matchedIssuer)) {
//            proxyView.finish(new ActionResult(TransResult.ERR_CARD_UNSUPPORTED, null));
//            return;
//        }
//
//        if (!matchedIssuer.isAllowManualPan()) {
//            proxyView.finish(new ActionResult(TransResult.ERR_UNSUPPORTED_FUNC, null));
//            return;
//        }
//
//        if (!Issuer.validPan(matchedIssuer, cardNo)) {
//            proxyView.finish(new ActionResult(TransResult.ERR_CARD_INVALID, null));
//            return;
//        }
//
//        if (!Issuer.validCardExpiry(matchedIssuer, date)) {
//            proxyView.finish(new ActionResult(TransResult.ERR_CARD_EXPIRED, null));
//            return;
//        }
//
//        ActionSearchCard.CardInformation cardInfo = new ActionSearchCard.CardInformation(ActionSearchCard.SearchMode.KEYIN, cardNo, date, matchedIssuer);
//        proxyView.finish(new ActionResult(TransResult.SUCC, cardInfo));
//    }
//
//    @Override
//    public void processManualExpDate() {
//        final String content = proxyView.getExpDate().replace(" ", "");
//        if (content.isEmpty()) {
//            runInputMerchantPwdAction();
//        } else {
//            if (dateProcess(content)) {
//                runInputMerchantPwdAction();
//            } else {
//                proxyView.onEditDateError();
//            }
//        }
//    }
//
//    private boolean dateProcess(String content) {
//        final String mmYY = "MM/yy";
//        if (content.length() != mmYY.length()) {
//            return false;
//        }
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat(mmYY, Locale.US);
//        dateFormat.setLenient(false);
//        try {
//            dateFormat.parse(content);
//        } catch (ParseException e) {
//            LogUtils.w(TAG, "", e);
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public void onOkClicked() {
//        if (pollingResult.getReaderType() == EReaderType.ICC) {
//            boolean enableTip = SysParam.getInstance().getBoolean(R.string.EDC_SUPPORT_TIP);
//            if (enableTip && iccAdjustPercent > 0) {
//                proxyView.goToAdjustTip();
//            } else {
//                FinancialApplication.getApp().doEvent(new EmvCallbackEvent(EmvCallbackEvent.Status.CARD_NUM_CONFIRM_SUCCESS));
//            }
//        } else if (pollingResult.getReaderType() == EReaderType.MAG) {
//            processMag();
//        }
//    }
//
//    @Override
//    public void onHeaderBackClicked() {
//        if (pollingResult.getReaderType() == EReaderType.ICC) {
//            FinancialApplication.getApp().doEvent(new EmvCallbackEvent(EmvCallbackEvent.Status.CARD_NUM_CONFIRM_ERROR));
//        } else {
//            FinancialApplication.getApp().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    onReadCardCancel();
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onKeyBackDown() {
//        if (pollingResult.getReaderType() == EReaderType.ICC) {
//            FinancialApplication.getApp().doEvent(new EmvCallbackEvent(EmvCallbackEvent.Status.CARD_NUM_CONFIRM_ERROR));
//        } else {
//            if (isManualMode) {
//                proxyView.resetEditText();
//                if (cardNo != null) {
//                    cardNo = null;
//                    runSearchCardThread();
//                    proxyView.onClickBack();
//                }
//            } else {
//                FinancialApplication.getApp().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        onReadCardCancel();
//                    }
//                });
//            }
//        }
//    }
//
//    // 填写信息校验
//    private void processMag() {
//        String content = proxyView.getCardNo().replace(" ", "");
//        if (content.isEmpty()) {
//            FinancialApplication.getApp().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    proxyView.onReadCardError();
//                }
//            });
//            runSearchCardThread();
//            return;
//        }
//
//        String pan = TrackUtils.getPan(proxyView.trackData2());
//        Issuer matchedIssuer = FinancialApplication.getAcqManager().findIssuerByPan(pan);
//        if (pollingResult.getReaderType() == EReaderType.MAG) {
//            if (matchedIssuer == null || !FinancialApplication.getAcqManager().isIssuerSupported(matchedIssuer)) {
//                proxyView.finish(new ActionResult(TransResult.ERR_CARD_UNSUPPORTED, null));
//                return;
//            }
//
//            if (!Issuer.validPan(matchedIssuer, pan)) {
//                proxyView.finish(new ActionResult(TransResult.ERR_CARD_INVALID, null));
//                return;
//            }
//
//            if (!Issuer.validCardExpiry(matchedIssuer, TrackUtils.getExpDate(proxyView.trackData2()))) {
//                proxyView.finish(new ActionResult(TransResult.ERR_CARD_EXPIRED, null));
//                return;
//            }
//        }
//        ActionSearchCard.CardInformation cardInfo = new ActionSearchCard.CardInformation(ActionSearchCard.SearchMode.SWIPE, pollingResult.getTrack1(), pollingResult.getTrack2(),
//                pollingResult.getTrack3(), TrackUtils.getPan(proxyView.trackData2()), matchedIssuer);
//        proxyView.finish(new ActionResult(TransResult.SUCC, cardInfo));
//
//    }
//
//    // 寻卡线程
//    private class SearchCardThread extends Thread {
//
//
//        @Override
//        public void run() {
//            LogUtils.d(TAG, "SearchCardThread run");
//            MyCardReaderHelper cardReaderHelper = MyCardReaderHelper.getInstance();
//
//            if (readerType == null) {
//                return;
//            }
//
//            LogUtils.d(TAG, readerType.getEReaderType());
//            pollingResult = cardReaderHelper.polling(readerType, 60 * 1000);
//            cardReaderHelper.stopPolling();
//            ServiceReadType.getInstance().setReadType(EReaderType.DEFAULT.getEReaderType());
//            if (pollingResult.getOperationType() == PollingResult.EOperationType.CANCEL || pollingResult.getOperationType() == PollingResult.EOperationType.TIMEOUT) {
//                FinancialApplication.getApp().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        onReadCardCancel();
//                    }
//                });
//            } else if (pollingResult.getOperationType() == PollingResult.EOperationType.OK) {
//                FinancialApplication.getApp().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        onReadCardOk();
//                    }
//                });
//            }
//
//        }
//
//        private void onReadCardOk() {
//            //case of allowing Fallback
//            if (pollingResult.getReaderType() == EReaderType.MAG) {
//                if ((mode & ActionSearchCard.SearchMode.INSERT) == ActionSearchCard.SearchMode.INSERT && TrackUtils.isIcCard(proxyView.trackData2())) {
//                    Device.beepErr();
//                    LogUtils.d(TAG, "iccEnable:$iccEnable");
//                    if (iccEnable) {
//                        mode &= ~ActionSearchCard.SearchMode.SWIPE;
//                        readerType = toReaderType(mode);
//                        proxyView.iccCardMagReadOk(mode);
//                    } else {
//                        proxyView.onReadCardError();
//                    }
//                    runSearchCardThread();
//                    return;
//                }
//
//                Device.beepPrompt();
//                // 有时刷卡成功，单没有磁道II，做一下防护
//
//                String pan = TrackUtils.getPan(proxyView.trackData2());
//                String exp = TrackUtils.getExpDate(proxyView.trackData2());
//
//                //日期显示格式为（MMYY）
//                if (exp == null || exp.length() != 4) {
//                    Device.beepErr();
//                    proxyView.magCardMagExpDateErr(mode);
//                    runSearchCardThread();
//                    return;
//                }
//                if (TextUtils.isEmpty(pan)) {
//                    proxyView.finish(new ActionResult(TransResult.ERR_CARD_NO, null));
//                    return;
//                }
//                proxyView.magCardReadOk(pan);
//            } else if (pollingResult.getReaderType() == EReaderType.ICC) {
//                //需要通过EMV才能获取到卡号等信息,所以先在EMV里面获取到信息，再到case CARD_NUM_CONFIRM中显示
//                proxyView.onIccDetectOk();
//            } else if (pollingResult.getReaderType() == EReaderType.PICC) {
//                LogUtils.d("Joshua", "Contactless card detected.");
//                proxyView.onPiccDetectOk();
//            }
//        }
//
//    }

}
