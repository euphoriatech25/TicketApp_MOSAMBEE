/////*
//// * ===========================================================================================
//// * = COPYRIGHT
//// *          PAX Computer Technology(Shenzhen); CO., LTD PROPRIETARY INFORMATION
//// *   This software is supplied under the terms of a license agreement or nondisclosure
//// *   agreement with PAX Computer Technology(Shenzhen); CO., LTD and may not be copied or
//// *   disclosed except in accordance with the terms in that agreement.
//// *     Copyright (C); 2019-? PAX Computer Technology(Shenzhen); CO., LTD All rights reserved.
//// * Description: // Detail description about the voidction of this module,
//// *             // interfaces with the other modules, and dependencies.
//// * Revision History:
//// * Date                  Author	                 Action
//// * 20190108  	         xieYb                   Modify
//// * ===========================================================================================
//// */
//package com.technosales.net.buslocationannouncement.readsam;
//
//import android.content.Context;
//
//import com.technosales.net.buslocationannouncement.interfaces.BasePresenter;
//
//
//public interface SearchCardContract {
//    interface View extends IView {
//        //input cardNum
//        void onEditCardNoError();
//
//        ;
//
//        void onEditCardNo();
//
//        void showManualPwdErr();
//
//        String getCardNo();
//
//        String getExpDate();
//
//        void finish(ActionResult result);
//
//        //input expDate
//        void onEditDateError();
//
//        String trackData2();
//
//        //使用芯片卡刷卡读取成功
//        void iccCardMagReadOk(Byte mode);
//
//        //磁条卡读取成功，但expDate错误
//        void magCardMagExpDateErr(Byte mode);
//
//        //磁条卡读取成功
//        void magCardReadOk(String pan);
//
//        //插卡检测成功
//        void onIccDetectOk();
//
//        //拍卡检测成功
//        void onPiccDetectOk();
//
//        void onReadCardError();
//
//        void goToAdjustTip();
//
//        void resetEditText();
//
//        void onClickBack();
//
//
//    }
//
//    abstract class Presenter extends BasePresenter<View> {
//        public Presenter(Context context) {
//            super(context);
//        }
//
//        //input cardNum
//        public abstract void onTimerFinish();
//
//        public abstract void runInputMerchantPwdAction();
//
//        public abstract void onVerifyManualPan();
//
//        //input expDate
//        public abstract void processManualExpDate();
//
//        public abstract void initParam(Byte mode);
//
//        public abstract void runSearchCard();
//
//        public abstract void stopDetectCard();
//
//        public abstract void onOkClicked();
//
//        public abstract void onHeaderBackClicked();
//
//        public abstract void onKeyBackDown();
//
//    }
//}
