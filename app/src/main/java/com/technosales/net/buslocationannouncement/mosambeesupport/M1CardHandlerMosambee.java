package com.technosales.net.buslocationannouncement.mosambeesupport;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.card.mifare.M1CardHandler;
import com.morefun.yapi.card.mifare.M1KeyTypeConstrants;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.SDKManager;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

import java.io.UnsupportedEncodingException;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_A;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_B;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_CUSTOMER;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRANSATION;


public class M1CardHandlerMosambee {

    private static final String TAG = "M1CardHandlerTest";

    public static void read_miCard(Handler handler, int[] customerDetailsBlock, String fromWhichActivity) {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        new SearchCardOrCardReaderTest(mSDKManager).searchRFCard(new String[]{IccCardType.M1CARD}, new PayByCardActivity.OnSearchListener() {
            @Override
            public void onSearchResult(int retCode, Bundle bundle) {
                if (ServiceResult.Success == retCode) {
                    String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                    if (IccCardType.M1CARD.equals(cardType)) {
                        try {
                            M1CardHandler m1CardHandler = mSDKManager.getM1CardHandler(mSDKManager.getIccCardReader(IccReaderSlot.RFSlOT));
                            if (m1CardHandler == null) {
// alertDialogOnShowListener.showMessage(getString(R.string.msg_readfail_retry));
                                return;
                            }
                            byte[] uid = new byte[4];

                            if (fromWhichActivity.equalsIgnoreCase("HelperLogin") || fromWhichActivity.equalsIgnoreCase("ReIssueCard")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_CUSTOMER, KEY_A, uid);
                                Message messageCardId = new Message();
                                messageCardId.what = 100;
                                messageCardId.obj = GeneralUtils.ByteArrayToHexString(
                                        (uid == null) ? "".getBytes() : uid);
                                handler.sendMessage(messageCardId);
                            } else if (fromWhichActivity.equalsIgnoreCase("PayByCardActivity")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_CUSTOMER, KEY_A, uid);
                                Log.i(TAG, "onSearchResult111111: " + ret);
                                if (ret == ServiceResult.Success) {
                                    readCustomerDetails(handler, m1CardHandler, customerDetailsBlock, uid);
// return;
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("CheckBalanceActivity")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_CUSTOMER, KEY_A, uid);
                                Log.i(TAG, "onSearchResult111111: " + ret);
                                if (ret == ServiceResult.Success) {
                                    readCustomerRechargeDetails(handler, m1CardHandler, customerDetailsBlock, uid);
// return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
// alertDialogOnShowListener.showMessage(getString(R.string.msg_icorrfid));
                }
            }
        });
    }

    private static void readCustomerRechargeDetails(Handler handler, M1CardHandler m1CardHandler, int[] customerDetailsBlock, byte[] uid) {
        byte[] customerId = new byte[16];
        byte[] customerHash = new byte[16];
        int ret, ret1;

        Message messageCardId = new Message();
        messageCardId.what = 100;
        messageCardId.obj = GeneralUtils.ByteArrayToHexString(
                (uid == null) ? "".getBytes() : uid);
        handler.sendMessage(messageCardId);

        try {
            ret = m1CardHandler.readBlock((byte) customerDetailsBlock[0], customerId);
            byte[] data1 = Base64.decode(customerId, Base64.DEFAULT);
            String str1 = null;
            try {
                str1 = new String(data1, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "read: " + ret + str1);
            Message messageCustomerId = new Message();
            messageCustomerId.what = 101;
            messageCustomerId.obj = str1;
            handler.sendMessage(messageCustomerId);


            ret1 = m1CardHandler.readBlock((byte) customerDetailsBlock[1], customerHash);
            if (customerHash != null) {
                byte[] dataAmt = Base64.decode(customerHash, Base64.DEFAULT);
                String str2 = null;
                try {
                    str2 = new String(dataAmt, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "read: " + ret1 + str2);

                Message messageCustomerAmt = new Message();
                messageCustomerAmt.what = 102;
                messageCustomerAmt.obj = str2;
                handler.sendMessage(messageCustomerAmt);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public static void write_miCard(Handler handler, String[] customerUpdatedValue, int[] customerDetailsBlock, String fromWhichActivity) {
        DeviceServiceEngine mSDKManager;
        mSDKManager = SDKManager.getInstance().getDeviceServiceEngine();
        if (mSDKManager == null) {
            Log.e("TAG", "ServiceEngine is Null");
            return;
        }
        new SearchCardOrCardReaderTest(mSDKManager).searchRFCard(new String[]{IccCardType.M1CARD}, new PayByCardActivity.OnSearchListener() {
            @Override
            public void onSearchResult(int retCode, Bundle bundle) {
                if (ServiceResult.Success == retCode) {
                    String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                    if (IccCardType.M1CARD.equals(cardType)) {
                        try {
                            M1CardHandler m1CardHandler = mSDKManager.getM1CardHandler(mSDKManager.getIccCardReader(IccReaderSlot.RFSlOT));
                            if (m1CardHandler == null) {
                                // alertDialogOnShowListener.showMessage(getString(R.string.msg_readfail_retry));
                                return;
                            }
                            byte[] uid = new byte[4];

                            if (fromWhichActivity.equalsIgnoreCase("PayByCardActivity-UpdateCard")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_CUSTOMER, KEY_B, uid);
                                Log.i(TAG, "onSearchResultWrite: " + ret);
                                if (ret == ServiceResult.Success) {
                                    writeCustomerDetails(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock);
                                    // return;
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("CheckBalanceActivity-UpdateCard")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_CUSTOMER, KEY_B, uid);
                                Log.i(TAG, "onSearchResultWrite: " + ret);
                                if (ret == ServiceResult.Success) {
                                    writeCustomerDetails(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock);
                                    // return;
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("ReIssueCard-UpdateCard")||fromWhichActivity.equalsIgnoreCase("IssueCardActivity-UpdateCard")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_CUSTOMER, KEY_B, uid);
                                Log.i(TAG, "onSearchResultWrite: " + ret);
                                if (ret == ServiceResult.Success) {
                                    writeCustomerReIssue(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock, uid);
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // alertDialogOnShowListener.showMessage(getString(R.string.msg_icorrfid));
                }
            }
        });
    }

    private static void writeCustomerReIssue(Handler handler, M1CardHandler m1CardHandler, String[] customerUpdatedValue, int[] customerDetailsBlock, byte[] uid) {

        try {
            int ret1 = m1CardHandler.writeBlock((byte) customerDetailsBlock[0], customerUpdatedValue[0].getBytes());
            int ret2 = m1CardHandler.writeBlock((byte) customerDetailsBlock[1], customerUpdatedValue[1].getBytes());

            int ret3 = m1CardHandler.writeBlock((byte) customerDetailsBlock[2], customerUpdatedValue[2].getBytes());

            int ret5=0;
            int ret4 = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_TRANSATION, KEY_B, uid);
            if (ret4 == ServiceResult.Success) {
               ret5 = m1CardHandler.writeBlock((byte) customerDetailsBlock[3], customerUpdatedValue[3].getBytes());
            }
            Log.i(TAG, "writeCustomerReIssue: "+ret1+ret2+ret3+ret4+ret5);
            if(ret1==ServiceResult.Success&&ret2==ServiceResult.Success&&ret3==ServiceResult.Success&&ret4==ServiceResult.Success){
                Message messageSuccess = new Message();
                messageSuccess.what = 200;
                messageSuccess.obj = "Success";
                handler.sendMessage(messageSuccess);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

}

    private static void writeCustomerDetails(Handler handler, M1CardHandler m1CardHandler, String[] customerUpdatedValue, int[] customerDetailsBlock) {
        try {
            int ret1 = m1CardHandler.writeBlock((byte) customerDetailsBlock[0], customerUpdatedValue[0].getBytes());
            int ret2 = m1CardHandler.writeBlock((byte) customerDetailsBlock[1], customerUpdatedValue[1].getBytes());
            Log.i(TAG, "writeCustomerDetails: "+ret1+ret2);
            if(ret1==ServiceResult.Success&&ret2==ServiceResult.Success){
                Message messageSuccess = new Message();
                messageSuccess.what = 200;
                messageSuccess.obj = "Success";
                handler.sendMessage(messageSuccess);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void readCustomerDetails(Handler handlerTransaction, M1CardHandler m1CardHandler, int[] blockList, byte[] uid) {

        byte[] customerId = new byte[16];
        byte[] customerAmt = new byte[16];
        byte[] customerHash = new byte[16];
        byte[] customerTranNo = new byte[16];
        int ret, ret1, ret2, ret3;

        Message messageCardId = new Message();
        messageCardId.what = 100;
        messageCardId.obj = GeneralUtils.ByteArrayToHexString(
                (uid == null) ? "".getBytes() : uid);
        handlerTransaction.sendMessage(messageCardId);

        try {
            ret = m1CardHandler.readBlock((byte) blockList[0], customerId);
            byte[] data1 = Base64.decode(customerId, Base64.DEFAULT);
            String str1 = null;
            try {
                str1 = new String(data1, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "read: " + ret + str1);
            Message messageCustomerId = new Message();
            messageCustomerId.what = 101;
            messageCustomerId.obj = str1;
            handlerTransaction.sendMessage(messageCustomerId);


            ret1 = m1CardHandler.readBlock((byte) blockList[1], customerAmt);
            if (customerAmt != null) {
                byte[] dataAmt = Base64.decode(customerAmt, Base64.DEFAULT);
                String str2 = null;
                try {
                    str2 = new String(dataAmt, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "read: " + ret1 + str2);

                Message messageCustomerAmt = new Message();
                messageCustomerAmt.what = 102;
                messageCustomerAmt.obj = str2;
                handlerTransaction.sendMessage(messageCustomerAmt);
            }


            ret2 = m1CardHandler.readBlock((byte) blockList[2], customerHash);
            if (customerHash != null) {
                byte[] data = Base64.decode(customerHash, Base64.DEFAULT);
                String str3 = null;
                try {
                    str3 = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "read: " + ret2 + str3);

                Message messageCustomerHash = new Message();
                messageCustomerHash.what = 103;
                messageCustomerHash.obj = str3;
                handlerTransaction.sendMessage(messageCustomerHash);
            }

            int ret111 = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, 7, KEY_A, uid);
            if (ret111 == ServiceResult.Success) {
                ret3 = m1CardHandler.readBlock((byte) blockList[3], customerTranNo);
                if (customerTranNo != null) {
                    byte[] data = Base64.decode(customerTranNo, Base64.DEFAULT);
                    String str4 = null;
                    try {
                        str4 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + ret3 + str4);

                    Message messageCustomerTranNo = new Message();
                    messageCustomerTranNo.what = 104;
                    messageCustomerTranNo.obj = str4;
                    handlerTransaction.sendMessage(messageCustomerTranNo);
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
