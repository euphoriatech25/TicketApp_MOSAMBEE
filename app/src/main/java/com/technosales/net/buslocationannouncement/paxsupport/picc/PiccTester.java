package com.technosales.net.buslocationannouncement.paxsupport.picc;

import android.nfc.tech.MifareClassic;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.pax.dal.IPicc;
import com.pax.dal.entity.EBeepMode;
import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EM1KeyType;
import com.pax.dal.entity.EPiccRemoveMode;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.PiccCardInfo;
import com.pax.dal.entity.PiccPara;
import com.pax.dal.exceptions.EPedDevException;
import com.pax.dal.exceptions.EPiccDevException;
import com.pax.dal.exceptions.PiccDevException;
import com.pax.jemv.device.model.DeviceRetCode;
import com.technosales.net.buslocationannouncement.printlib.SysTester;
import com.technosales.net.buslocationannouncement.TicketBusApp;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

import java.io.UnsupportedEncodingException;

public class PiccTester {
    public static PiccTester piccTester;
    public IPicc picc;
    public static EPiccType piccType;

    byte[] Amount = null;
    byte[] TRANSACTIONHASH = null;
    byte[] TRANSACTIONNO = null;
    public String TAG = "PiccTester";

    private PiccTester(EPiccType type) {
        piccType = type;
        picc = TicketBusApp.getDal().getPicc(piccType);
    }

    public static PiccTester getInstance(EPiccType type) {
        if (piccTester == null || type != piccType) {
            piccTester = new PiccTester(type);
        }
        return piccTester;
    }

    public PiccPara setUp() {
        try {
            PiccPara readParam = picc.readParam();
            return readParam;
        } catch (PiccDevException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void open() {
        try {
            picc.open();
        } catch (PiccDevException e) {
            e.printStackTrace();
        }
    }



    public PiccCardInfo detect(EDetectMode mode) {
        try {
            PiccCardInfo cardInfo = picc.detect(mode);
            return cardInfo;
        } catch (PiccDevException e) {
            e.printStackTrace();
            Log.i("TAG", "detect: " + e.getLocalizedMessage());
            return null;
        }
    }

    public byte[] isoCommand(byte cid, byte[] send) {
        try {
            byte[] isoCommand = picc.isoCommand(cid, send);
            return isoCommand;
        } catch (PiccDevException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void remove(EPiccRemoveMode mode, byte cid) {
        try {
            picc.remove(mode, cid);
        } catch (PiccDevException e) {
            e.printStackTrace();
        }
    }

    public void setLed(byte led) {
        try {
            picc.setLed(led);
        } catch (PiccDevException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            picc.close();
        } catch (PiccDevException e) {
            e.printStackTrace();
        }
    }

    public void m1Auth(EM1KeyType type, byte blkNo, byte[] pwd, byte[] serialNo) {
        try {
            picc.m1Auth(type, blkNo, MifareClassic.KEY_DEFAULT, serialNo);
        } catch (PiccDevException e) {
            e.printStackTrace();
        }
    }

    public byte[] m1Read(byte blkNo) {
        try {
            byte[] result = picc.m1Read(blkNo);
            return result;
        } catch (PiccDevException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void registerCard(Handler handler, String reserveBlock, String customerId, String customerAmt, String customerHash) {
        PiccCardInfo cardInfo = null;
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {

                picc.m1Auth(EM1KeyType.TYPE_B, (byte) 4,MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());


                byte[] bWrite = new byte[32];
                byte[] userId = customerId.getBytes();
                System.arraycopy(userId, 0, bWrite, 0, userId.length);

                byte[] bWrite1 = new byte[32];
                byte[] userId1 = customerAmt.getBytes();
                System.arraycopy(userId1, 0, bWrite1, 0, userId1.length);

                byte[] bWrite2 = new byte[32];
                byte[] userId2 = customerHash.getBytes();
                System.arraycopy(userId2, 0, bWrite2, 0, userId2.length);

                byte[] bWrite3 = new byte[32];
                byte[] userId3 = customerHash.getBytes();
                System.arraycopy(userId3, 0, bWrite3, 0, userId3.length);

                picc.m1Write((byte) 4, bWrite);
                picc.m1Write((byte) 5, bWrite1);
                picc.m1Write((byte) 6, bWrite2);
                picc.m1Write((byte) 13, bWrite2);

                Message message = Message.obtain();
                message.what = 1;
                message.obj = "successful" ;
                handler.sendMessage(message);
            } catch (PiccDevException e) {
               Log.w(TAG, e);
               int ret1 = e.getErrCode();
               short ret2;
               if (ret1 == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {//test case 3B02-9001 for paypass 3.0.1 by zhoujie   // ?
                   ret2 = DeviceRetCode.DEVICE_PICC_USER_CANCEL;
               } else if (ret1 == EPiccDevException.PICC_ERR_PROTOCOL2.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_PROTOCOL_ERROR;
               } else if (ret1 == EPiccDevException.PICC_ERR_IO.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_TRANSMIT_ERROR;
               } else if (ret1 == EPiccDevException.PICC_ERR_TIMEOUT.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_TIME_OUT_ERROR;
               } else {
                   ret2 = DeviceRetCode.DEVICE_PICC_OTHER_ERR;
               }
               Log.i(TAG, "updateTransaction: "+ret2);
            }
        } else {
            Message.obtain(handler, 1, "failed").sendToTarget();
        }
    }
 public void rechargeCard(Handler handler,  String customerAmt) {
        PiccCardInfo cardInfo = null;
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte) 10,MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());
                byte[] bWrite = new byte[32];
                byte[] userId = "222".getBytes();
                System.arraycopy(userId, 0, bWrite, 0, userId.length);


                picc.m1Write((byte) 10, bWrite);

                Message message = Message.obtain();
                message.what = 2;
                message.obj = "successful" ;
                handler.sendMessage(message);

            } catch (PiccDevException e) {
               Log.w(TAG, e);
               int ret1 = e.getErrCode();
               short ret2;
               if (ret1 == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {//test case 3B02-9001 for paypass 3.0.1 by zhoujie   // ?
                   ret2 = DeviceRetCode.DEVICE_PICC_USER_CANCEL;
               } else if (ret1 == EPiccDevException.PICC_ERR_PROTOCOL2.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_PROTOCOL_ERROR;
               } else if (ret1 == EPiccDevException.PICC_ERR_IO.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_TRANSMIT_ERROR;
               } else if (ret1 == EPiccDevException.PICC_ERR_TIMEOUT.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_TIME_OUT_ERROR;
               } else {
                   ret2 = DeviceRetCode.DEVICE_PICC_OTHER_ERR;
               }
               Log.i(TAG, "updateTransaction: "+ret2);
            }
        } else {
            Message.obtain(handler, 1, "failed").sendToTarget();
        }
    }

    public void registerCard(Handler handler, String customerId) {
        PiccCardInfo cardInfo = null;
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte) 14, MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());


                byte[] bWrite = new byte[16];
                byte[] userId = customerId.getBytes();
                System.arraycopy(userId, 0, bWrite, 0, userId.length);
                picc.m1Write((byte) 14, bWrite);

                Message message = Message.obtain();
                message.what = 1;
                message.obj = "successful";
                handler.sendMessage(message);

            } catch (PiccDevException e) {
                Log.w(TAG, e);
                int ret1 = e.getErrCode();
                short ret2;
                if (ret1 == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {//test case 3B02-9001 for paypass 3.0.1 by zhoujie   // ?
                    ret2 = DeviceRetCode.DEVICE_PICC_USER_CANCEL;
                } else if (ret1 == EPiccDevException.PICC_ERR_PROTOCOL2.getErrCodeFromBasement()) {
                    ret2 = DeviceRetCode.DEVICE_PICC_PROTOCOL_ERROR;
                } else if (ret1 == EPiccDevException.PICC_ERR_IO.getErrCodeFromBasement()) {
                    ret2 = DeviceRetCode.DEVICE_PICC_TRANSMIT_ERROR;
                } else if (ret1 == EPiccDevException.PICC_ERR_TIMEOUT.getErrCodeFromBasement()) {
                    ret2 = DeviceRetCode.DEVICE_PICC_TIME_OUT_ERROR;
                } else {
                    ret2 = DeviceRetCode.DEVICE_PICC_OTHER_ERR;
                }
                Log.i(TAG, "updateTransaction: " + ret2);
            }
        } else {
            Message.obtain(handler, 1, "failed").sendToTarget();
        }
    }
    public void detectMWrite(Handler handler, String customerId,int blockNum) {
        PiccCardInfo cardInfo = null;
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte)blockNum, MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());
//                picc.m1Auth(EM1KeyType.TYPE_B, (byte) 5, MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());
//                picc.m1Auth(EM1KeyType.TYPE_B, (byte) 6, MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());


                byte[] bWrite = new byte[32];
                byte[] userId = customerId.getBytes();
                System.arraycopy(userId, 0, bWrite, 0, userId.length);

//                byte[] bWrite1 = new byte[32];
//                byte[] userId1 = customerAmt.getBytes();
//                System.arraycopy(userId1, 0, bWrite1, 0, userId1.length);
//
//                byte[] bWrite2 = new byte[32];
//                byte[] userId2 = customerHash.getBytes();
//                System.arraycopy(userId2, 0, bWrite2, 0, userId2.length);

                picc.m1Write((byte)blockNum, bWrite);

//                picc.m1Write((byte) 2, bWrite1);
//                picc.m1Write((byte) 3, bWrite2);

                Message message = Message.obtain();
                message.what = 1;
                message.obj = "successful" ;
                handler.sendMessage(message);

            } catch (PiccDevException e) {
               Log.w(TAG, e);
               int ret1 = e.getErrCode();
               short ret2;
               if (ret1 == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {//test case 3B02-9001 for paypass 3.0.1 by zhoujie   // ?
                   ret2 = DeviceRetCode.DEVICE_PICC_USER_CANCEL;
               } else if (ret1 == EPiccDevException.PICC_ERR_PROTOCOL2.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_PROTOCOL_ERROR;
               } else if (ret1 == EPiccDevException.PICC_ERR_IO.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_TRANSMIT_ERROR;
               } else if (ret1 == EPiccDevException.PICC_ERR_TIMEOUT.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_TIME_OUT_ERROR;
               } else {
                   ret2 = DeviceRetCode.DEVICE_PICC_OTHER_ERR;
               }
               Log.i(TAG, "updateTransaction: "+ret2);

                Log.i(TAG, "detectMWrite: " + e.getLocalizedMessage());
            }
        } else {
            Message.obtain(handler, 1, "failed").sendToTarget();
        }
    }

    public void getId(Handler handler) {
        PiccCardInfo cardInfo = null;
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            Message message = Message.obtain();
            message.what = 0;
            message.obj = GeneralUtils.ByteArrayToHexString(
                    (cardInfo.getSerialInfo() == null) ? "".getBytes() : cardInfo.getSerialInfo());
            handler.sendMessage(message);
            SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);

        }
    }


 public void getRechargeInfo(Handler handler) {
     PiccCardInfo cardInfo = null;
     if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
         Message message = Message.obtain();
         message.what = 0;
         message.obj = GeneralUtils.ByteArrayToHexString(
                 (cardInfo.getSerialInfo() == null) ? "".getBytes() : cardInfo.getSerialInfo());
         handler.sendMessage(message);

         byte[] customerId = null;
         try {
             picc.m1Auth(EM1KeyType.TYPE_B, (byte) 12, MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());
             customerId = picc.m1Read((byte) 12);
             try {
                 if (customerId != null) {
                     byte[] data = Base64.decode(customerId, Base64.DEFAULT);
                     String str1 = new String(data, "UTF-8");
                     Message message1 = Message.obtain();
                     Log.i(TAG, "getCardTagID: " + str1);
                     message1.what = 1;
                     message1.obj = str1;
                     handler.sendMessage(message1);
                     SysTester.getInstance().beep(EBeepMode.FREQUENCE_LEVEL_6, 100);
                 }
             } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
             }

         } catch (PiccDevException e) {
             e.printStackTrace();
         }
     }
 }





   public void updateTransaction(Handler handler,  String customerAmt, String customerHash,String transaction,String transactionno) {
        PiccCardInfo cardInfo = null;
       if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
           try {
               picc.m1Auth(EM1KeyType.TYPE_B, (byte) 5, MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());


               byte[] bWrite = new byte[32];
               byte[] userId = customerAmt.getBytes();
               System.arraycopy(userId, 0, bWrite, 0, userId.length);

               byte[] bWrite1 = new byte[32];
               byte[] userId1 = customerHash.getBytes();
               System.arraycopy(userId1, 0, bWrite1, 0, userId1.length);

               byte[] bWrite2 = new byte[32];
               byte[] userId2 = transaction.getBytes();
               System.arraycopy(userId2, 0, bWrite2, 0, userId2.length);

               byte[] bWrite3 = new byte[32];
               byte[] userId3 = transaction.getBytes();
               System.arraycopy(userId3, 0, bWrite3, 0, userId3.length);

               picc.m1Write((byte) 5, bWrite);
               picc.m1Write((byte) 6, bWrite1);
               picc.m1Write((byte) 7, bWrite2);
               picc.m1Write((byte) 13, bWrite3);

               Message message = Message.obtain();
               message.what = 5;
               message.obj = "successful" ;
               handler.sendMessage(message);
           } catch (PiccDevException e) {
               Log.w(TAG, e);
               int ret1 = e.getErrCode();
               short ret2;
               if (ret1 == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {//test case 3B02-9001 for paypass 3.0.1 by zhoujie   // ?
                   ret2 = DeviceRetCode.DEVICE_PICC_USER_CANCEL;
               } else if (ret1 == EPiccDevException.PICC_ERR_PROTOCOL2.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_PROTOCOL_ERROR;
               } else if (ret1 == EPiccDevException.PICC_ERR_IO.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_TRANSMIT_ERROR;
               } else if (ret1 == EPiccDevException.PICC_ERR_TIMEOUT.getErrCodeFromBasement()) {
                   ret2 = DeviceRetCode.DEVICE_PICC_TIME_OUT_ERROR;
               } else {
                   ret2 = DeviceRetCode.DEVICE_PICC_OTHER_ERR;
               }

               Log.i(TAG, "updateTransaction: "+ret2);
           }
       } else {
           Message.obtain(handler, 1, "failed").sendToTarget();
       }
    }

//    private int piccIsoCommandDevice(ApduSendL2 apduSend, ApduRespL2 apduRecv) {
//        ApduSendInfo send = new ApduSendInfo();
//        send.setCommand(apduSend.command);
//        send.setDataIn(apduSend.dataIn);
//        send.setLc(apduSend.lc);
//        send.setLe(apduSend.le);
//        Log.i(TAG, "apduSend = " + GeneralUtils.bcdToStr(apduSend.dataIn));
//
//        try {
//            ApduRespInfo resp = picc.isoCommandByApdu(iccSlot, send);
//            Log.i(TAG, "apduRecv = " + GeneralUtils.bcdToStr(resp.getDataOut()));
//            System.arraycopy(resp.getDataOut(), 0, apduRecv.dataOut, 0, resp.getDataOut().length);
//            apduRecv.lenOut = (short) resp.getDataOut().length;
//            apduRecv.swa = resp.getSwA();
//            apduRecv.swb = resp.getSwB();
//            Log.i(TAG, "swa = " + GeneralUtils.bcdToStr(new byte[]{apduRecv.swa}));
//            Log.i(TAG, "swb = " + GeneralUtils.bcdToStr(new byte[]{apduRecv.swb}));
//            return DeviceRetCode.DEVICE_PICC_OK;
//        } catch (PiccDevException e) {
//            Log.w(TAG, e);
//            int ret1 = e.getErrCode();
//            short ret2;
//            if (ret1 == RET_RF_ERR_USER_CANCEL) {//test case 3B02-9001 for paypass 3.0.1 by zhoujie   // ?
//                ret2 = DeviceRetCode.DEVICE_PICC_USER_CANCEL;
//            } else if (ret1 == EPiccDevException.PICC_ERR_PROTOCOL2.getErrCodeFromBasement()) {
//                ret2 = DeviceRetCode.DEVICE_PICC_PROTOCOL_ERROR;
//            } else if (ret1 == EPiccDevException.PICC_ERR_IO.getErrCodeFromBasement()) {
//                ret2 = DeviceRetCode.DEVICE_PICC_TRANSMIT_ERROR;
//            } else if (ret1 == EPiccDevException.PICC_ERR_TIMEOUT.getErrCodeFromBasement()) {
//                ret2 = DeviceRetCode.DEVICE_PICC_TIME_OUT_ERROR;
//            } else {
//                ret2 = DeviceRetCode.DEVICE_PICC_OTHER_ERR;
//            }
//
//            return ret2;
//        }
//    }

}
