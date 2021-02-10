package com.technosales.net.buslocationannouncement.picc;

import android.nfc.tech.MifareClassic;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.pax.dal.IPicc;
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
import com.technosales.net.buslocationannouncement.TicketBusApp;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_A;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_B;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.KEY_DEFAULT;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRAILER_CUSTOMER_DETAILS;
import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SECTOR_TRANSACTION_NO;

public class PiccTransaction {
    public static PiccTransaction piccTransaction;
    public static EPiccType piccType;
    public IPicc picc;
    public String TAG = "PiccTransaction";
    byte[] Amount = null;
    byte[] TRANSACTIONHASH = null;

    private Message messageCustomerId = new Message();
    private Message messageCustomerAmt = new Message();

    private PiccTransaction(EPiccType type) {
        piccType = type;
        picc = TicketBusApp.getDal().getPicc(piccType);
    }

    public static PiccTransaction getInstance(EPiccType type) {
        if (piccTransaction == null || type != piccType) {
            piccTransaction = new PiccTransaction(type);
        }
        return piccTransaction;
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
    public boolean detectCard(EDetectMode mode) {
        try {
            PiccTransaction.getInstance(piccType).open();
            PiccCardInfo cardInfo = picc.detect(mode);

           return true;
        } catch (PiccDevException e) {
            e.printStackTrace();
            Log.i("TAG", "c: " + e.getLocalizedMessage());
            return false;
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

    public boolean m1Auth(int blocknum) {
        try {
            PiccCardInfo cardInfo = null;
            cardInfo = detect(EDetectMode.ONLY_M);
            PiccTransaction.getInstance(piccType).open();
            picc.m1Auth(EM1KeyType.TYPE_B, (byte) blocknum, MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());
            return true;
        } catch (PiccDevException e) {
            Log.i(TAG, "m1Auth: " + e.getLocalizedMessage());
            e.printStackTrace();
            return false;
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

    public void read(Handler handler, int[] blockList) {
        PiccCardInfo cardInfo = null;
        byte[] customerId = null;
        byte[] customerHash = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            Message message = Message.obtain();
            message.what = 100;
            message.obj = GeneralUtils.ByteArrayToHexString(
                    (cardInfo.getSerialInfo() == null) ? "".getBytes() : cardInfo.getSerialInfo());
            handler.sendMessage(message);

            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[0], KEY_A, cardInfo.getSerialInfo());
                customerId = picc.m1Read((byte) blockList[0]);
            if (customerId != null) {
                byte[] data = Base64.decode(customerId, Base64.DEFAULT);
                String str1 = null;
                try {
                    str1 = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "read: " + str1);

                Message messageCustomerHash = new Message();
                messageCustomerHash.what = 101;
                messageCustomerHash.obj = str1;
                handler.sendMessage(messageCustomerHash);
            }
            } catch (PiccDevException e) {
                    e.printStackTrace();
            }

            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[1], KEY_A, cardInfo.getSerialInfo());
               customerHash = picc.m1Read((byte) blockList[1]);
             if (customerHash != null) {
                byte[] data = Base64.decode(customerHash, Base64.DEFAULT);
                String str1 = null;
                try {
                    str1 = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "read: " + str1);

                Message messageCustomerHash = new Message();
                messageCustomerHash.what = 102;
                messageCustomerHash.obj = str1;
                handler.sendMessage(messageCustomerHash);
            }


            } catch (PiccDevException e) {
                    e.printStackTrace();
            }
            PiccTransaction.getInstance(piccType).close();
        }
    }

    public void write(Handler handler, String[] blockContent, int[] writeBlock) {
        PiccCardInfo cardInfo = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte) writeBlock[0], KEY_B, cardInfo.getSerialInfo());
                byte[] bWrite1 = new byte[32];
                byte[] userHash = blockContent[0].getBytes();
                System.arraycopy(userHash, 0, bWrite1, 0, userHash.length);
                picc.m1Write((byte) writeBlock[0], bWrite1);
                Log.i(TAG, "write: 11111111111111111111111111111");
                try {
                    Message messageSuccess = new Message();
                    messageSuccess.what = 200;
                    messageSuccess.obj = "1";
                    handler.sendMessage(messageSuccess);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                PiccTransaction.getInstance(piccType).close();
            } catch (PiccDevException e) {
                Log.w(TAG, e);
                int ret1 = e.getErrCode();
                short ret2;
                if (ret1 == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {
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

            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte) writeBlock[1], KEY_B, cardInfo.getSerialInfo());
                byte[] bWrite1 = new byte[32];
                byte[] userHash = blockContent[1].getBytes();
                System.arraycopy(userHash, 0, bWrite1, 0, userHash.length);
                picc.m1Write((byte) writeBlock[1], bWrite1);
                Log.i(TAG, "write: 11111111111111111111111111111");
                try {
                    Message messageSuccess = new Message();
                    messageSuccess.what = 200;
                    messageSuccess.obj = "1";
                    handler.sendMessage(messageSuccess);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                PiccTransaction.getInstance(piccType).close();
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
            Message.obtain(handler, 404, "failed").sendToTarget();
        }
    }

    public void authSector(Handler handler,int writeBlock) {
        PiccCardInfo cardInfo = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte) writeBlock, MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());
                picc.m1Write((byte) writeBlock, KEY_DEFAULT);

                Message messageSuccess = new Message();
                messageSuccess.what = 201;
                messageSuccess.obj = "1";
                handler.sendMessage(messageSuccess);
            } catch (PiccDevException e) {
                Log.i(TAG, "authSector: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "authSector: failed");
        }
    }


    //    update the card details
    public void writeData(Handler handler, String blockContent, int writeBlock) {
        PiccCardInfo cardInfo = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte) writeBlock, KEY_B, cardInfo.getSerialInfo());

                byte[] bWrite1 = new byte[32];
                byte[] userHash = blockContent.getBytes();
                System.arraycopy(userHash, 0, bWrite1, 0, userHash.length);
                picc.m1Write((byte) writeBlock, bWrite1);
//                picc.m1Write((byte) SECTOR_TRANSACTION_NO, KEY_DEFAULT);
                Log.i(TAG, "writeData: " + "sssssssssss");
                try {
                    Message messageSuccess = new Message();
                    messageSuccess.what = 200;
                    messageSuccess.obj = "1";
                    handler.sendMessage(messageSuccess);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                PiccTransaction.getInstance(piccType).close();

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
            Message.obtain(handler, 404, "failed").sendToTarget();
        }
    }


    public void registerCustomerCard(Handler handler,String[] customerDetails, int[] customerDetailsBlock) {
        PiccCardInfo cardInfo = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte) customerDetailsBlock[0],MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());
                byte[] bWrite1 = new byte[32];
                byte[] userHash1 = customerDetails[0].getBytes();
                System.arraycopy(userHash1, 0, bWrite1, 0, userHash1.length);


                byte[] bWrite2 = new byte[32];
                byte[] userHash2 = customerDetails[1].getBytes();
                System.arraycopy(userHash2, 0, bWrite2, 0, userHash2.length);

                 byte[] bWrite3 = new byte[32];
                byte[] userHash3 = customerDetails[2].getBytes();
                System.arraycopy(userHash3, 0, bWrite3, 0, userHash3.length);


                byte[] bWrite4 = new byte[32];
                byte[] userHash4 = customerDetails[3].getBytes();
                System.arraycopy(userHash4, 0, bWrite4, 0, userHash4.length);

                picc.m1Write((byte) customerDetailsBlock[0], bWrite1);
                picc.m1Write((byte) customerDetailsBlock[1], bWrite2);
                picc.m1Write((byte) customerDetailsBlock[2], bWrite3);
                picc.m1Write((byte) SECTOR_TRAILER_CUSTOMER_DETAILS,KEY_DEFAULT);


                picc.m1Auth(EM1KeyType.TYPE_B, (byte) customerDetailsBlock[3],MifareClassic.KEY_DEFAULT, cardInfo.getSerialInfo());
                picc.m1Write((byte) customerDetailsBlock[3], bWrite4);
                picc.m1Write((byte) SECTOR_TRANSACTION_NO,KEY_DEFAULT);

                PiccTransaction.getInstance(piccType).close();

//                picc.m1Write((byte) SECTOR_TRAILER_CUSTOMER_DETAILS, KEY_DEFAULT);

                try {
                    Message messageSuccess = new Message();
                    messageSuccess.what = 200;
                    messageSuccess.obj = "Successful";
                    handler.sendMessage(messageSuccess);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
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
            Message.obtain(handler, 404, "failed").sendToTarget();
        }
    }

    public void readId(Handler handler) {
        PiccCardInfo cardInfo = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            Message message = Message.obtain();
            message.what = 100;
            message.obj = GeneralUtils.ByteArrayToHexString(
                    (cardInfo.getSerialInfo() == null) ? "".getBytes() : cardInfo.getSerialInfo());
            handler.sendMessage(message);
        }
    }

    public void registerTranBlock(Handler handler, String transactionNo, int customerTransactionNo) {
        PiccCardInfo cardInfo = null;

        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_B, (byte) customerTransactionNo, KEY_B, cardInfo.getSerialInfo());

                byte[] bWrite1 = new byte[32];
                byte[] userHash = transactionNo.getBytes();
                System.arraycopy(userHash, 0, bWrite1, 0, userHash.length);
                picc.m1Write((byte) customerTransactionNo, bWrite1);
//                picc.m1Write((byte) SECTOR_TRANSACTION_NO, KEY_DEFAULT);
                Log.i(TAG, "writeData: " + "sssssssssss");
                try {

                    Message messageSuccess = new Message();
                    messageSuccess.what = 200;
                    messageSuccess.obj = "1";
                    handler.sendMessage(messageSuccess);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                PiccTransaction.getInstance(piccType).close();
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
            Message.obtain(handler, 404, "failed").sendToTarget();
        }
    }

    public void readCustomerDetails(Handler handlerTransaction, int[] blockList) {
        PiccCardInfo cardInfo = null;
        byte[] customerId = null;
        byte[] customerAmt = null;
        byte[] customerHash = null;
        byte[] customerTranNo = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            Message message = Message.obtain();
            message.what = 100;
            message.obj = GeneralUtils.ByteArrayToHexString(
                    (cardInfo.getSerialInfo() == null) ? "".getBytes() : cardInfo.getSerialInfo());
            handlerTransaction.sendMessage(message);

            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[0], KEY_A, cardInfo.getSerialInfo());
                customerId = picc.m1Read((byte) blockList[0]);
                if (customerId != null) {
                    byte[] data = Base64.decode(customerId, Base64.DEFAULT);
                    String str1 = null;
                    try {
                        str1 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerId = new Message();
                    messageCustomerId.what = 101;
                    messageCustomerId.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerId);
                }
            } catch (PiccDevException e) {
                e.printStackTrace();
            }

            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[1], KEY_A, cardInfo.getSerialInfo());
                customerAmt = picc.m1Read((byte) blockList[1]);
                if (customerAmt != null) {
                    byte[] data = Base64.decode(customerAmt, Base64.DEFAULT);
                    String str1 = null;
                    try {
                        str1 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerAmt= new Message();
                    messageCustomerAmt.what = 102;
                    messageCustomerAmt.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerAmt);
                }


            } catch (PiccDevException e) {
                e.printStackTrace();
            }
            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[2], KEY_A, cardInfo.getSerialInfo());
                customerHash = picc.m1Read((byte) blockList[2]);
                if (customerHash != null) {
                    byte[] data = Base64.decode(customerHash, Base64.DEFAULT);
                    String str1 = null;
                    try {
                        str1 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerHash = new Message();
                    messageCustomerHash.what = 103;
                    messageCustomerHash.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerHash);
                }


            } catch (PiccDevException e) {
                e.printStackTrace();
            }


            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[3], KEY_A, cardInfo.getSerialInfo());
                customerTranNo = picc.m1Read((byte) blockList[3]);
                if (customerTranNo != null) {
                    byte[] data = Base64.decode(customerTranNo, Base64.DEFAULT);
                    String str1 = null;
                    try {
                        str1 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerTranNo = new Message();
                    messageCustomerTranNo.what = 104;
                    messageCustomerTranNo.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerTranNo);
                }


            } catch (PiccDevException e) {
                e.printStackTrace();
            }
            PiccTransaction.getInstance(piccType).close();
        }
    }


    public void readCustomerFirstTrans(Handler handlerTransaction, int[] blockList) {
        PiccCardInfo cardInfo = null;
        byte[] firstOffLineTicketId = null;
        byte[] firstOffLineAmt = null;
        byte[] firstOffLineHash = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[0], KEY_A, cardInfo.getSerialInfo());
                firstOffLineTicketId = picc.m1Read((byte) blockList[0]);
                if (firstOffLineTicketId != null) {
                    String str1 = null;
                    try {
                        str1 = new String(firstOffLineTicketId, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerId = new Message();
                    messageCustomerId.what = 105;
                    messageCustomerId.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerId);
                }
            } catch (PiccDevException e) {
                e.printStackTrace();
            }

            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[1], KEY_A, cardInfo.getSerialInfo());
                firstOffLineAmt = picc.m1Read((byte) blockList[1]);
                if (firstOffLineAmt != null) {
                    byte[] data = Base64.decode(firstOffLineAmt, Base64.DEFAULT);
                    String str1 = null;
                    try {
                        str1 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerAmt= new Message();
                    messageCustomerAmt.what = 106;
                    messageCustomerAmt.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerAmt);
                }


            } catch (PiccDevException e) {
                e.printStackTrace();
            }

            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[2], KEY_A, cardInfo.getSerialInfo());
                firstOffLineHash = picc.m1Read((byte) blockList[2]);
                if (firstOffLineHash != null) {
                    byte[] data = Base64.decode(firstOffLineHash, Base64.DEFAULT);
                    String str1 = null;
                    try {
                        str1 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerHash = new Message();
                    messageCustomerHash.what = 107;
                    messageCustomerHash.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerHash);
                }
            } catch (PiccDevException e) {
                e.printStackTrace();
            }
            PiccTransaction.getInstance(piccType).close();
        }
    }

    public void readCustomerSecondTrans(Handler handlerTransaction, int[] blockList) {
        PiccCardInfo cardInfo = null;
        byte[] secondOffTicketId = null;
        byte[] secondOffAmt = null;
        byte[] secondOffHash = null;
        PiccTransaction.getInstance(piccType).open();
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {
            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[0], KEY_A, cardInfo.getSerialInfo());
                secondOffTicketId = picc.m1Read((byte) blockList[0]);
                if (secondOffTicketId != null) {
                     String str1 = null;
                    try {
                        str1 = new String(secondOffTicketId, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerId = new Message();
                    messageCustomerId.what = 108;
                    messageCustomerId.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerId);
                }
            } catch (PiccDevException e) {
                e.printStackTrace();
            }

            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[1], KEY_A, cardInfo.getSerialInfo());
                secondOffAmt = picc.m1Read((byte) blockList[1]);
                if (secondOffAmt != null) {
                    byte[] data = Base64.decode(secondOffAmt, Base64.DEFAULT);
                    String str1 = null;
                    try {
                        str1 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerAmt= new Message();
                    messageCustomerAmt.what = 109;
                    messageCustomerAmt.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerAmt);
                }
            } catch (PiccDevException e) {
                e.printStackTrace();
            }


            try {
                picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockList[2], KEY_A, cardInfo.getSerialInfo());
                secondOffHash = picc.m1Read((byte) blockList[2]);
                if (secondOffHash != null) {
                    byte[] data = Base64.decode(secondOffHash, Base64.DEFAULT);
                    String str1 = null;
                    try {
                        str1 = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "read: " + str1);

                    Message messageCustomerHash = new Message();
                    messageCustomerHash.what = 110;
                    messageCustomerHash.obj = str1;
                    handlerTransaction.sendMessage(messageCustomerHash);
                }
            } catch (PiccDevException e) {
                e.printStackTrace();
            }
            PiccTransaction.getInstance(piccType).close();
        }
    }


}
