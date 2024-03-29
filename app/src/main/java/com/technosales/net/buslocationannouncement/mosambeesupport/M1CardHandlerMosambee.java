package com.technosales.net.buslocationannouncement.mosambeesupport;

import android.content.pm.ServiceInfo;
import android.nfc.tech.MifareClassic;
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
import com.technosales.net.buslocationannouncement.serverconn.ServiceConfig;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.io.UnsupportedEncodingException;

import okio.ByteString;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_A;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_B;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_DEFAULT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_CUSTOMER;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_FIRST_TRANSATION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_SECOND_TRANSATION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_DETAILS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_FIRST_TRANSACTION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_SECOND_TRANSACTION;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_TRANSACTION_NO;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRANSATION;


public class M1CardHandlerMosambee {

    private static final String TAG = "M1CardHandlerMosambee";

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
                Log.i(TAG, "onSearchResult: " + retCode);
                if (ServiceResult.Success == retCode) {
                    String cardType = bundle.getString(ICCSearchResult.CARDTYPE);
                    if (IccCardType.M1CARD.equals(cardType)) {
                        try {
                            M1CardHandler m1CardHandler = mSDKManager.getM1CardHandler(mSDKManager.getIccCardReader(IccReaderSlot.RFSlOT));
                            if (m1CardHandler == null) {
                                return;
                            }
                            byte[] uid = new byte[4];
                            if (fromWhichActivity.equalsIgnoreCase("HelperLogin")) {
                                m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_CUSTOMER, MifareClassic.KEY_DEFAULT, uid);

                                Message messageCardId = new Message();
                                messageCardId.what = 100;
                                messageCardId.obj = GeneralUtils.ByteArrayToHexString(uid);
                                handler.sendMessage(messageCardId);


                            } else if (fromWhichActivity.equalsIgnoreCase("ReIssueCard") || fromWhichActivity.equalsIgnoreCase("IssueCardActivity")) {
                                int value = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_CUSTOMER, KEY_A, uid);

                                if (value == ServiceResult.Success) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 100;
                                    messageCardId.obj = GeneralUtils.ByteArrayToHexString(uid);
                                    handler.sendMessage(messageCardId);

                                } else if (value == ServiceResult.M1Card_Verify_Err) {
                                    Message messageCardId1 = new Message();
                                    messageCardId1.what = 505;
                                    messageCardId1.obj = "यो एक अधिकृत कार्ड हैन।";
                                    handler.sendMessage(messageCardId1);
                                }

                            } else if (fromWhichActivity.equalsIgnoreCase("PayByCardActivity")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_CUSTOMER, KEY_A, uid);
                                Log.i(TAG, "onSearchResult: 111" + ret);
                                if (ret == ServiceResult.Success) {
                                    readCustomerDetails(handler, m1CardHandler, customerDetailsBlock, uid);
                                } else if (ret == -10301) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 404;
                                    messageCardId.obj = "Error";
                                    handler.sendMessage(messageCardId);
                                } else if (ret == -10304) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 405;
                                    messageCardId.obj = "Please Card Show Again Properly";
                                    handler.sendMessage(messageCardId);
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("CheckBalanceActivity")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_CUSTOMER, KEY_A, uid);

                                Log.i(TAG, "onSearchResult CheckBalanceActivity: " + ret);
                                if (ret == ServiceResult.Success) {
                                    readCustomerRechargeDetails(handler, m1CardHandler, customerDetailsBlock, uid);
                                } else if (ret == -10301) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 404;
                                    messageCardId.obj = "Error";
                                    handler.sendMessage(messageCardId);
                                } else if (ret == -10304) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 405;
                                    messageCardId.obj = "Please Card Show Again Properly";
                                    handler.sendMessage(messageCardId);
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("GetFirstOfflineTransaction")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_FIRST_TRANSATION, KEY_A, uid);
                                Log.i(TAG, "onSearchResult111111: " + ret);
                                if (ret == ServiceResult.Success) {
                                    readCustomerFirstOfflineTranDetails(handler, m1CardHandler, customerDetailsBlock);
                                } else if (ret == -10301) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 404;
                                    messageCardId.obj = "Error";
                                    handler.sendMessage(messageCardId);
                                } else if (ret == ServiceResult.M1Card_Data_Block_Err) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 405;
                                    messageCardId.obj = "Please Card Show Again Properly";
                                    handler.sendMessage(messageCardId);
                                }

                            } else if (fromWhichActivity.equalsIgnoreCase("GetSecondOfflineTransaction")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_SECOND_TRANSATION, KEY_A, uid);
                                Log.i(TAG, "onSearchResult: " + ret);
                                if (ret == ServiceResult.Success) {
                                    readCustomerSecondOfflineTranDetails(handler, m1CardHandler, customerDetailsBlock);
                                } else if (ret == -10301) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 404;
                                    messageCardId.obj = "Error";
                                    handler.sendMessage(messageCardId);
                                } else if (ret == -10304) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 405;
                                    messageCardId.obj = "Please Card Show Again Properly";
                                    handler.sendMessage(messageCardId);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                }
            }
        });
    }


    public static void write_miCard() {
        final DeviceServiceEngine mSDKManager;
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
                                return;
                            }
                            byte[] uid = new byte[4];
                            int value = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_CUSTOMER, MifareClassic.KEY_DEFAULT, uid);
                            int valueWrite1 = m1CardHandler.writeBlock(SECTOR_TRAILER_CUSTOMER_DETAILS, KEY_DEFAULT);

                            int value1 = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_TRANSATION, MifareClassic.KEY_DEFAULT, uid);
                            int valueWrite2 = m1CardHandler.writeBlock(SECTOR_TRAILER_TRANSACTION_NO, KEY_DEFAULT);

                            int value2 = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_FIRST_TRANSATION, MifareClassic.KEY_DEFAULT, uid);
                            int valueWrite3 = m1CardHandler.writeBlock(SECTOR_TRAILER_CUSTOMER_FIRST_TRANSACTION, KEY_DEFAULT);

                            int value3 = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_SECOND_TRANSATION, MifareClassic.KEY_DEFAULT, uid);
                            int valueWrite4 = m1CardHandler.writeBlock(SECTOR_TRAILER_CUSTOMER_SECOND_TRANSACTION, KEY_DEFAULT);

                            Log.i(TAG, "onSearchResult: " + value + value1 + value2 + value3 + "    " + "444");
                            Log.i(TAG, "onSearchResult: " + valueWrite1 + valueWrite4 + valueWrite2 + valueWrite3);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                }
            }
        });
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
                                return;
                            }
                            byte[] uid = new byte[4];

                            if (fromWhichActivity.equalsIgnoreCase("PayByCardActivity-UpdateCard")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_CUSTOMER, KEY_B, uid);
                                Log.i(TAG, "onSearchResultWrite: " + ret);
                                if (ret == ServiceResult.Success) {
                                    writeCustomerDetails(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock);
                                    // return;
                                } else if (ret == -10301) {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 405;
                                    messageCardId.obj = "Verification Error ";
                                    handler.sendMessage(messageCardId);
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("CheckBalanceActivity-UpdateCard")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_CUSTOMER, KEY_B, uid);
                                Log.i(TAG, "onSearchResultWrite: " + ret);
                                if (ret == ServiceResult.Success) {
                                    writeCustomerDetails(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock);
                                    // return;
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("ReIssueCard-UpdateCard") || fromWhichActivity.equalsIgnoreCase("IssueCardActivity-CreateCard")) {

                                int value = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_CUSTOMER, KEY_B, uid);

                                Log.i(TAG, "onSearchResult: " + value);
                                if (value == ServiceResult.Success) {
                                    writeCustomerReIssue(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock, uid);
                                } else if (value == ServiceResult.M1Card_Verify_Err) {
                                    Message messageCardId1 = new Message();
                                    messageCardId1.what = 505;
                                    messageCardId1.obj = "यो कार्ड पहिले नै प्रयोग गरीएको छ। कृपया नयाँ कार्ड प्रयोग गर्नुहोस्।";
                                    handler.sendMessage(messageCardId1);
                                } else {
                                    Message messageCardId = new Message();
                                    messageCardId.what = 500;
                                    messageCardId.obj = "Card invalid ...Please contract Admin";
                                    handler.sendMessage(messageCardId);
                                }

                            } else if (fromWhichActivity.equalsIgnoreCase("FirstOfflineTransaction-Write")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_FIRST_TRANSATION, KEY_B, uid);
                                Log.i(TAG, "onSearchResultWrite111: " + ret);
                                if (ret == ServiceResult.Success) {
                                    writeOfflineTransaction(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock, uid);
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("SecondOfflineTransaction-Write")) {
                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_SECOND_TRANSATION, KEY_B, uid);
                                Log.i(TAG, "onSearchResultWrite111: " + ret);
                                if (ret == ServiceResult.Success) {
                                    writeOfflineTransaction(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock, uid);
                                }
                            } else if (fromWhichActivity.equalsIgnoreCase("OfflineTransactionNoUpdate")) {

                                int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_TRANSATION, KEY_B, uid);
                                Log.i(TAG, "onSearchResult: from sadi" + ret);
                                if (ret == ServiceResult.Success) {
                                    writeTransationNo(handler, m1CardHandler, customerUpdatedValue, customerDetailsBlock);
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

    private static void writeCustomerDetailsWrite(Handler handler, M1CardHandler m1CardHandler, String[] customerUpdatedValue, int[] customerDetailsBlock, byte[] uid) {
        try {
            int ret = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_CUSTOMER, KEY_B, uid);
            Log.i(TAG, "writeCustomerDetailsWrite: " + ret);
            if (ret == ServiceResult.Success) {
                int ret1 = m1CardHandler.writeBlock((byte) customerDetailsBlock[0], customerUpdatedValue[0].getBytes());
                int ret2 = m1CardHandler.writeBlock((byte) customerDetailsBlock[1], customerUpdatedValue[1].getBytes());
                int ret3 = m1CardHandler.writeBlock((byte) customerDetailsBlock[2], customerUpdatedValue[2].getBytes());


                int ret4 = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_TRANSATION, KEY_B, uid);

                Log.i(TAG, "writeCustomerReIssue: " + ret1 + ret2 + ret3 + ret4);
                if (ret4 == ServiceResult.Success) {
                    int ret5 = m1CardHandler.writeBlock((byte) customerDetailsBlock[3], customerUpdatedValue[3].getBytes());
                    Log.i(TAG, "writeCustomerReIssue: " + ret1 + ret2 + ret3 + ret4 + ret5);
                }
                if (ret1 == ServiceResult.Success && ret2 == ServiceResult.Success && ret3 == ServiceResult.Success && ret4 == ServiceResult.Success) {
                    Message messageSuccess = new Message();
                    messageSuccess.what = 200;
                    messageSuccess.obj = "Success";
                    handler.sendMessage(messageSuccess);
                } else if (ret1 == -10303 || ret2 == -10303 || ret3 == -10303 || ret4 == -10303) {
                    Message messageSuccess = new Message();
                    messageSuccess.what = 500;
                    messageSuccess.obj = "Something went wrong..Please Wait....";
                    handler.sendMessage(messageSuccess);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void readCustomerFirstOfflineTranDetails(Handler handler, M1CardHandler m1CardHandler, int[] customerDetailsBlock) {
        byte[] firstOffTranTicketId = new byte[16];
        byte[] firstOffTranTicketAmt = new byte[16];
        byte[] firstOffTranTicketHash = new byte[16];
        int ret, ret1, ret2;

        try {
            ret = m1CardHandler.readBlock((byte) customerDetailsBlock[0], firstOffTranTicketId);
            String str1 = null;
            try {
                str1 = new String(firstOffTranTicketId, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "read: " + ret + str1);
            Message messageCustomerId = new Message();
            messageCustomerId.what = 105;
            messageCustomerId.obj = str1;
            handler.sendMessage(messageCustomerId);


            ret1 = m1CardHandler.readBlock((byte) customerDetailsBlock[1], firstOffTranTicketAmt);
            byte[] dataAmt = Base64.decode(firstOffTranTicketAmt, Base64.DEFAULT);
            String str2 = null;
            try {
                str2 = new String(dataAmt, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "read: " + ret1 + str2);

            Message messageCustomerAmt = new Message();
            messageCustomerAmt.what = 106;
            messageCustomerAmt.obj = str2;
            handler.sendMessage(messageCustomerAmt);


            ret2 = m1CardHandler.readBlock((byte) customerDetailsBlock[2], firstOffTranTicketHash);
            byte[] data = Base64.decode(firstOffTranTicketHash, Base64.DEFAULT);
            String str3 = null;
            try {
                str3 = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "read: " + ret2 + str3);

            Message messageCustomerHash = new Message();
            messageCustomerHash.what = 107;
            messageCustomerHash.obj = str3;
            handler.sendMessage(messageCustomerHash);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void readCustomerSecondOfflineTranDetails(Handler handler, M1CardHandler m1CardHandler, int[] customerDetailsBlock) {
        byte[] secondOffTranTicketId = new byte[16];
        byte[] secondOffTranTicketAmt = new byte[16];
        byte[] secondOffTranTicketHash = new byte[16];
        int ret, ret1, ret2;

        try {
            ret = m1CardHandler.readBlock((byte) customerDetailsBlock[0], secondOffTranTicketId);
            String str1 = null;
            try {
                str1 = new String(secondOffTranTicketId, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "read: " + ret + str1);
            Message messageCustomerId = new Message();
            messageCustomerId.what = 108;
            messageCustomerId.obj = str1;
            handler.sendMessage(messageCustomerId);


            ret1 = m1CardHandler.readBlock((byte) customerDetailsBlock[1], secondOffTranTicketAmt);
            byte[] dataAmt = Base64.decode(secondOffTranTicketAmt, Base64.DEFAULT);
            String str2 = null;
            try {
                str2 = new String(dataAmt, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "read: " + ret1 + str2);

            Message messageCustomerAmt = new Message();
            messageCustomerAmt.what = 109;
            messageCustomerAmt.obj = str2;
            handler.sendMessage(messageCustomerAmt);


            ret2 = m1CardHandler.readBlock((byte) customerDetailsBlock[2], secondOffTranTicketHash);
            byte[] data = Base64.decode(secondOffTranTicketHash, Base64.DEFAULT);
            String str3 = null;
            try {
                str3 = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "read: " + ret2 + str3);

            Message messageCustomerHash = new Message();
            messageCustomerHash.what = 110;
            messageCustomerHash.obj = str3;
            handler.sendMessage(messageCustomerHash);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void readCustomerRechargeDetails(Handler handler, M1CardHandler m1CardHandler, int[] customerDetailsBlock, byte[] uid) {
        byte[] customerId = new byte[16];
        byte[] customerHash = new byte[16];
        byte[] customerTranNo = new byte[16];
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
            m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_TRANSATION, KEY_A, uid);
            ret1 = m1CardHandler.readBlock((byte) customerDetailsBlock[2], customerTranNo);
            if (customerTranNo != null) {
                byte[] dataTranNo = Base64.decode(customerTranNo, Base64.DEFAULT);
                String str2 = null;
                try {
                    str2 = new String(dataTranNo, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "read: " + ret1 + str2);

                Message messageCustomerAmt = new Message();
                messageCustomerAmt.what = 104;
                messageCustomerAmt.obj = str2;
                handler.sendMessage(messageCustomerAmt);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private static void writeTransationNo(Handler handler, M1CardHandler m1CardHandler, String[] customerUpdatedValue, int[] customerDetailsBlock) {
        try {
            int ret = m1CardHandler.writeBlock((byte) customerDetailsBlock[0], customerUpdatedValue[0].getBytes());
            Log.i(TAG, "writeTransationNo: " + ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static void writeOfflineTransaction(Handler handler, M1CardHandler m1CardHandler, String[] customerUpdatedValue, int[] customerDetailsBlock, byte[] uid) {
        Log.i(TAG, "writeOfflineTransaction: i am here");
        int ret4 = 0;
        try {
            int ret1 = m1CardHandler.writeBlock((byte) customerDetailsBlock[0], customerUpdatedValue[0].getBytes());
            int ret2 = m1CardHandler.writeBlock((byte) customerDetailsBlock[1], customerUpdatedValue[1].getBytes());
            Log.i(TAG, "writeOfflineTransaction111111111111111111: " + customerDetailsBlock[2]);
            int ret3 = m1CardHandler.writeBlock((byte) customerDetailsBlock[2], customerUpdatedValue[2].getBytes());

            int ret111 = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_TRANSATION, KEY_B, uid);
            if (ret111 == ServiceResult.Success) {
                ret4 = m1CardHandler.writeBlock((byte) customerDetailsBlock[3], customerUpdatedValue[3].getBytes());
            }
            Log.i(TAG, "writeCustomerDetails: " + ret1 + ret2 + ret3 + ret111 + ret4);
            if (ret1 == ServiceResult.Success && ret2 == ServiceResult.Success && ret3 == ServiceResult.Success) {
                Message messageSuccess = new Message();
                messageSuccess.what = 202;
                messageSuccess.obj = "Success";
                handler.sendMessage(messageSuccess);
            } else if (ret1 == -10303 || ret2 == -10303 || ret3 == -10303) {
                Message messageSuccess = new Message();
                messageSuccess.what = 500;
                messageSuccess.obj = "Something went wrong";
                handler.sendMessage(messageSuccess);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static void writeCustomerReIssue(Handler handler, M1CardHandler m1CardHandler, String[] customerUpdatedValue, int[] customerDetailsBlock, byte[] uid) {

        try {
            Log.i(TAG, "writeCustomerReIssue: " + customerDetailsBlock[0] + customerUpdatedValue[0]);
            int ret1 = m1CardHandler.writeBlock((byte) customerDetailsBlock[0], customerUpdatedValue[0].getBytes());
            int ret2 = m1CardHandler.writeBlock((byte) customerDetailsBlock[1], customerUpdatedValue[1].getBytes());
            int ret3 = m1CardHandler.writeBlock((byte) customerDetailsBlock[2], customerUpdatedValue[2].getBytes());


            int ret4 = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_B, SECTOR_TRANSATION, KEY_B, uid);

            Log.i(TAG, "writeCustomerReIssue: " + ret1 + ret2 + ret3 + ret4);
            if (ret4 == ServiceResult.Success) {
                int ret5 = m1CardHandler.writeBlock((byte) customerDetailsBlock[3], customerUpdatedValue[3].getBytes());
                Log.i(TAG, "writeCustomerReIssue: " + ret1 + ret2 + ret3 + ret4 + ret5);
            }
            if (ret1 == ServiceResult.Success && ret2 == ServiceResult.Success && ret3 == ServiceResult.Success && ret4 == ServiceResult.Success) {
                Message messageSuccess = new Message();
                messageSuccess.what = 200;
                messageSuccess.obj = "Success";
                handler.sendMessage(messageSuccess);
            } else if (ret1 == -10303 || ret2 == -10303 || ret3 == -10303 || ret4 == -10303) {
                Message messageSuccess = new Message();
                messageSuccess.what = 500;
                messageSuccess.obj = "Something went wrong..Please Wait....";
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
            Log.i(TAG, "writeCustomerDetails: " + ret1 + ret2);
            if (ret1 == ServiceResult.Success && ret2 == ServiceResult.Success) {
                Message messageSuccess = new Message();
                messageSuccess.what = 200;
                messageSuccess.obj = "Success";
                handler.sendMessage(messageSuccess);
            } else if (ret1 == -10303 || ret2 == -10303) {
                Message messageSuccess = new Message();
                messageSuccess.what = 500;
                messageSuccess.obj = "Hash Written Missed..Please Wait....";
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

//      to get Passenger Id

        try {
            ret = m1CardHandler.readBlock((byte) blockList[0], customerId);
            byte[] data1 = Base64.decode(customerId, Base64.DEFAULT);
            String str1 = null;
            try {
                str1 = new String(data1, "UTF-8");
                if (str1.length() > 0) {
                    Message messageCustomerId = new Message();
                    messageCustomerId.what = 101;
                    messageCustomerId.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerId);
                } else {
                    Message messageCustomerId = new Message();
                    messageCustomerId.what = 101;
                    messageCustomerId.obj = "null";
                    handlerTransaction.sendMessage(messageCustomerId);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


//      to get Passenger Amount
            m1CardHandler.readBlock((byte) blockList[1], customerAmt);

                byte[] dataAmt = Base64.decode(customerAmt, Base64.DEFAULT);
                String str2 = null;
                try {
                    str2 = new String(dataAmt, "UTF-8");
                    if (str2.length() > 0) {
                        Message messageCustomerAmt = new Message();
                        messageCustomerAmt.what = 102;
                        messageCustomerAmt.obj = str2;
                        handlerTransaction.sendMessage(messageCustomerAmt);
                    } else {
                        Message messageCustomerAmt = new Message();
                        messageCustomerAmt.what = 102;
                        messageCustomerAmt.obj = "null";
                        handlerTransaction.sendMessage(messageCustomerAmt);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


//   to get Customer Hash
            ret2 = m1CardHandler.readBlock((byte) blockList[2], customerHash);
            if (customerHash != null) {
                byte[] data = Base64.decode(customerHash, Base64.DEFAULT);
                String str3 = null;
                try {
                    str3 = new String(data, "UTF-8");
                    if (str3.length() > 0) {
                        Message messageCustomerHash = new Message();
                        messageCustomerHash.what = 103;
                        messageCustomerHash.obj = str3;
                        handlerTransaction.sendMessage(messageCustomerHash);
                    } else {
                        Message messageCustomerHash = new Message();
                        messageCustomerHash.what = 103;
                        messageCustomerHash.obj = "null";
                        handlerTransaction.sendMessage(messageCustomerHash);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "read: " + ret2 + str3);


            }


// to get Transaction No
            int retTransNo = m1CardHandler.authority(M1KeyTypeConstrants.KEYTYPE_A, SECTOR_TRANSATION, KEY_A, uid);
            if (retTransNo == ServiceResult.Success) {
                ret3 = m1CardHandler.readBlock((byte) blockList[3], customerTranNo);
                if (customerTranNo != null) {
                    byte[] data = Base64.decode(customerTranNo, Base64.DEFAULT);
                    String str4 = null;
                    try {
                        str4 = new String(data, "UTF-8");
                        if (str4.length() > 0) {
                            Message messageCustomerTranNo = new Message();
                            messageCustomerTranNo.what = 104;
                            messageCustomerTranNo.obj = str4;
                            handlerTransaction.sendMessage(messageCustomerTranNo);
                        } else {
                            Message messageCustomerTranNo = new Message();
                            messageCustomerTranNo.what = 104;
                            messageCustomerTranNo.obj = "null";
                            handlerTransaction.sendMessage(messageCustomerTranNo);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.i(TAG, "readCustomerDetails: " + e.getLocalizedMessage());
        }
    }
}
