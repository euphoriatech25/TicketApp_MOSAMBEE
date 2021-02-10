//package com.technosales.net.buslocationannouncement.AddingPax.picc;
//
//import android.os.SystemClock;
//import android.util.Log;
//
//import com.pax.dal.IDAL;
//import com.pax.dal.IIcc;
//import com.pax.dal.IMag;
//import com.pax.dal.IPed;
//import com.pax.dal.IPicc;
//import com.pax.dal.entity.ApduRespInfo;
//import com.pax.dal.entity.ApduSendInfo;
//import com.pax.dal.entity.EBeepMode;
//import com.pax.dal.entity.EPedType;
//import com.pax.dal.entity.EPiccType;
//import com.pax.dal.entity.ETermInfoKey;
//import com.pax.dal.entity.RSAPinKey;
//import com.pax.dal.exceptions.EPedDevException;
//import com.pax.dal.exceptions.EPiccDevException;
//import com.pax.dal.exceptions.IccDevException;
//import com.pax.dal.exceptions.MagDevException;
//import com.pax.dal.exceptions.PedDevException;
//import com.pax.dal.exceptions.PiccDevException;
//import com.pax.jemv.device.IDevice;
//import com.pax.jemv.device.model.ApduRespL2;
//import com.pax.jemv.device.model.ApduSendL2;
//import com.pax.jemv.device.model.DeviceRetCode;
//import com.pax.jemv.device.model.RsaPinKeyL2;
//import com.pax.jemv.device.model.TransactionInterface;
//import com.technosales.net.buslocationannouncement.PrintLib.SysTester;
//import com.technosales.net.buslocationannouncement.R;
//import com.technosales.net.buslocationannouncement.ServerConn.RetrofitInterface;
//import com.technosales.net.buslocationannouncement.ServerConn.ServerConfigNew;
//import com.technosales.net.buslocationannouncement.TicketBusApp;
//import com.technosales.net.buslocationannouncement.activity.HelperLogin;
//import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;
//import com.technosales.net.buslocationannouncement.base.BaseActivity;
//import com.technosales.net.buslocationannouncement.pojo.HelperModel;
//import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
//import com.technosales.net.buslocationannouncement.utils.UtilStrings;
//
//import java.math.BigInteger;
//import java.security.KeyFactory;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.SecureRandom;
//import java.security.spec.RSAPrivateKeySpec;
//import java.security.spec.RSAPublicKeySpec;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.Map;
//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.DESKeySpec;
//import javax.crypto.spec.SecretKeySpec;
//
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static com.technosales.net.buslocationannouncement.utils.UtilStrings.SHARED_PREFERENCES;
//
//public class DEVICEINSTANCE implements IDevice {
//    private static final String TAG = "DEVICEINSTANCE";
//
//    private String expectPinLen = "0,4,5,6,7,8,9,10,11,12";
//    private int timeOut = 30000;
//    private byte iccSlot = 0;
//    private static final int RET_RF_ERR_USER_CANCEL = 0x27;
//    private boolean cancelKeyFlag = false;
//    private int transInterface = 0;
//
//    private IDAL dal;
//    private IPed ped;
//    private IIcc icc;
//    private IPicc picc;
//    private IMag mag;
//
//    private long leftTime = 0;
//
//    private static DEVICEINSTANCE instance = null;
//
//    private DEVICEINSTANCE() {
//        dal = TicketBusApp.getDal();
//        ped = dal.getPed(EPedType.INTERNAL);
//        icc = dal.getIcc();
//        picc = dal.getPicc(EPiccType.INTERNAL);
//        mag = dal.getMag();
//    }
//
//    public static DEVICEINSTANCE getInstance() {
//        if (instance == null) {
//            instance = new DEVICEINSTANCE();
//        }
//        return instance;
//    }
//
//
//    @Override
//    public void getTime(byte[] dateTime) {
//        String date = dal.getSys().getDate();
////        System.arraycopy(ConvertHelper.getConvert().strToBcdPaddingLeft(date), 0, dateTime, 0, 7);
//    }
//
//    @Override
//    public void readSN(byte[] serialNo) {
//        Map<ETermInfoKey, String> info = dal.getSys().getTermInfo();
//        String sn = info.get(ETermInfoKey.SN);
//        if (sn != null) {
//            System.arraycopy(sn.getBytes(), 0, serialNo, 0, sn.length());
//        }
//    }
//
//    @Override
//    public void getRand(byte[] buf, int len) {
//        byte[] random = dal.getSys().getRandom(len);
//        System.arraycopy(random, 0, buf, 0, len);
//    }
//
//    @Override
//    public void timerSet(byte[] timerNo, short timeMS) {
//        leftTime = timeMS;
////        tickTimer.start(timeMS / 10);
//    }
//
//    @Override
//    public short timerCheck(byte timerNo) {
//        return (short) (leftTime * 10);
//    }
//
//
//    @Override
//    public void delayMs(short timeMS) {
//        SystemClock.sleep(timeMS);
//    }
//
//    @Override
//    public int setPinInputParam(final byte[] expectPinLen, long timeoutMs) {
//        this.expectPinLen = new String(expectPinLen);
//        this.timeOut = (int) timeoutMs;
//        return DeviceRetCode.DEVICE_PED_OK;
//    }
//
//    @Override
//    public int pedVerifyPlainPin(byte[] bytes, byte b) {
//        return 0;
//    }
//
//    @Override
//    public int pedVerifyCipherPin(RsaPinKeyL2 rsaPinKeyL2, byte[] bytes, byte b) {
//        return 0;
//    }
//
//
////    @Override
////    public int pedVerifyPlainPin(byte[] iccRespOut, byte mode) {
////        try {
////            ped.setKeyboardLayoutLandscape(!ViewUtils.isScreenOrientationPortrait(ActivityStack.getInstance().top()));
////            byte[] result = ped.verifyPlainPin(iccSlot, expectPinLen, mode, timeOut);
////            System.arraycopy(result, 0, iccRespOut, 0, 2);
////            return DeviceRetCode.DEVICE_PROC_OK;
////        } catch (PedDevException e) {
////            Log.w(TAG, e);
////            int code = e.getErrCode();
////            if (code == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {
////                return DeviceRetCode.DEVICE_PEDERR_INPUT_CANCEL;
////            } else if (code == EPedDevException.PED_ERR_INPUT_TIMEOUT.getErrCodeFromBasement()) {
////                return DeviceRetCode.DEVICE_PEDERR_INPUT_TIMEOUT;
////            } else if (code == EPedDevException.PED_ERR_NO_PIN_INPUT.getErrCodeFromBasement()) {
////                return DeviceRetCode.DEVICE_PEDERR_NO_PIN_INPUT;
////            } else if (code == EPedDevException.PED_ERR_WAIT_INTERVAL.getErrCodeFromBasement()) {
////                return DeviceRetCode.DEVICE_PEDERR_WAIT_INTERVAL;
////            } else {
////                return DeviceRetCode.DEVICE_PEDERR_OTHER;
////            }
////        }
////    }
//
////    @Override
////    public int pedVerifyCipherPin(final RsaPinKeyL2 rsaPinKeyIn, byte[] iccRespOut, byte mode) {
////        RSAPinKey pinKey = new RSAPinKey();
////        System.arraycopy(rsaPinKeyIn.exp, 0, pinKey.getExponent(), 0, 4);
////        System.arraycopy(rsaPinKeyIn.iccrandom, 0, pinKey.getIccRandom(), 0, rsaPinKeyIn.iccrandomlen);
////        System.arraycopy(rsaPinKeyIn.mod, 0, pinKey.getModulus(), 0, pinKey.getModulus().length);
////        pinKey.setModulusLen(rsaPinKeyIn.modlen);
////
////        try {
////            ped.setKeyboardLayoutLandscape(!GeneralUtils.isScreenOrientationPortrait(ActivityStack.getInstance().top()));
////            byte[] result = ped.verifyCipherPin(iccSlot, expectPinLen, pinKey, mode, timeOut);
////            System.arraycopy(result, 0, iccRespOut, 0, 2);
////            return DeviceRetCode.DEVICE_PROC_OK;
////        } catch (PedDevException e) {
////            Log.w(TAG, e);
////            int code = e.getErrCode();
////            if (code == EPedDevException.PED_ERR_INPUT_CANCEL.getErrCodeFromBasement()) {
////                return DeviceRetCode.DEVICE_PEDERR_INPUT_CANCEL;
////            } else if (code == EPedDevException.PED_ERR_INPUT_TIMEOUT.getErrCodeFromBasement()) {
////                return DeviceRetCode.DEVICE_PEDERR_INPUT_TIMEOUT;
////            } else if (code == EPedDevException.PED_ERR_NO_PIN_INPUT.getErrCodeFromBasement()) {
////                return DeviceRetCode.DEVICE_PEDERR_NO_PIN_INPUT;
////            } else if (code == EPedDevException.PED_ERR_WAIT_INTERVAL.getErrCodeFromBasement()) {
////                return DeviceRetCode.DEVICE_PEDERR_WAIT_INTERVAL;
////            } else {
////                return DeviceRetCode.DEVICE_PEDERR_OTHER;
////            }
////        }
////    }
//
//    @Override
//    public void des(final byte[] input, byte[] output, final byte[] desKey, int mode) {
//        try {
//            byte[] in = Arrays.copyOfRange(input, 0, Math.min(input.length, 8));
//            byte[] out;
//            switch (mode) {
//                case 1:
//                    out = DES.encrypt(in, desKey);
//                    System.arraycopy(out, 0, output, 0, 8);
//                    break;
//                case 0:
//                    out = DES.decrypt(in, desKey);
//                    System.arraycopy(out, 0, output, 0, 8);
//                    break;
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            Log.w(TAG, e);
//        }
//
//    }
//
//    @Override
//    public int aes(final byte[] input, byte[] output, final byte[] aesKey, int keyLen, int mode) {
//        try {
//            byte[] in = Arrays.copyOfRange(input, 0, Math.min(input.length, 16));
//            byte[] out;
//            switch (mode) {
//                case 1:
//                    out = AES.encrypt(in, aesKey);
//                    System.arraycopy(out, 0, output, 0, 16);
//                    return DeviceRetCode.DEVICE_PROC_OK;
//                case 0:
//                    out = AES.decrypt(in, aesKey);
//                    System.arraycopy(out, 0, output, 0, 16);
//                    return DeviceRetCode.DEVICE_PROC_OK;
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            Log.w(TAG, e);
//        }
//
//        return DeviceRetCode.DEVICE_PROC_ERROR;
//    }
//
//    @Override
//    public void hash(final byte[] dataIn, int dataInLen, byte[] dataOut) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-1");
//            byte[] in = Arrays.copyOfRange(dataIn, 0, Math.min(dataIn.length, dataInLen));
//            byte[] cipher = digest.digest(in);
//            System.arraycopy(cipher, 0, dataOut, 0, 20);
//        } catch (NoSuchAlgorithmException e) {
//            Log.w(TAG, e);
//        }
//    }
//
//    @Override
//    public int rsaRecover(final byte[] module, int moduleLen, final byte[] exp, int expLen, final byte[] dataIn, byte[] dataOut) {
//        try {
//            byte[] out = RSA.recover(module, moduleLen, exp, expLen, dataIn);
//            if (out != null) {
//                System.arraycopy(out, 0, dataOut, 0, out.length);
//                return DeviceRetCode.DEVICE_PROC_OK;
//            }
//        } catch (Exception e) {
//            Log.w(TAG, e);
//        }
//        return DeviceRetCode.DEVICE_PROC_ERROR;
//    }
//
//    @Override
//    public int sm2Verify(byte pubKeyIn, final byte[] msgIn, int msgInLen, final byte[] signIn, int signInLen) {
////        byte[] uid = ConvertHelper.getConvert().strToBcdPaddingLeft("31323334353637383132333435363738");
////        byte[] byteMsgIn = new byte[msgInLen];
////        byte[] byteSignIn = new byte[signInLen];
////        System.arraycopy(msgIn, 0, byteMsgIn, 0, msgInLen);
////        System.arraycopy(signIn, 0, byteSignIn, 0, signInLen);
////
////        try {
////            ped.SM2Verify(pubKeyIn, uid, byteMsgIn, byteSignIn);
////            return DeviceRetCode.DEVICE_PROC_OK;
////        } catch (PedDevException e) {
////            Log.w(TAG, e);
////        }
//        return DeviceRetCode.DEVICE_PROC_ERROR;
//    }
//
//    @Override
//    public int sm3(final byte[] msgIn, int msgInLen, byte[] resultOut) {
//        try {
//            byte[] in = new byte[msgInLen];
//            System.arraycopy(msgIn, 0, in, 0, msgInLen);
//            System.arraycopy(ped.SM3(in, (byte) 0x00), 0, resultOut, 0, resultOut.length);
//            return DeviceRetCode.DEVICE_PROC_OK;
//        } catch (PedDevException e) {
//            Log.w(TAG, e);
//        }
//        return DeviceRetCode.DEVICE_PROC_ERROR;
//    }
//
//    @Override
//    public byte setControlParam(byte[] param) {
//        changeCancelKeyFlagDevice((param[0] & 1) == 1);
//        return DeviceRetCode.DEVICE_PROC_OK;
//    }
//
//    //FIXME not until updated Neptune Lite
//    private void changeCancelKeyFlagDevice(boolean control) {
//        if (control && !cancelKeyFlag) {
//            cancelKeyFlag = true;
//        } else if (!control && cancelKeyFlag) {
//            cancelKeyFlag = false;
//        }
//    }
//
//    @Override
//    public int setCancelKey(byte keyValue) {
//        cancelKeyFlag = false;
//        return DeviceRetCode.DEVICE_PED_OK;
//    }
//
//    @Override
//    public int iccSetTxnIF(int txnIF) {
//        if (txnIF != 0xFF && txnIF != 0) {
//            return DeviceRetCode.DEVICE_PARAM_ERROR;
//        }
//        transInterface = txnIF;
//        return DeviceRetCode.DEVICE_PED_OK;
//    }
//
//    @Override
//    public int iccGetTxnIF() {
//        return transInterface;
//    }
//
//    @Override
//    public void setIccSlot(byte slot) {
//        this.iccSlot = slot;
//    }
//
//    @Override
//    public int iccReset() {
//        try {
//            dal.getIcc().init(this.iccSlot); // ignore returned ATR
//            return DeviceRetCode.DEVICE_PICC_OK;
//        } catch (IccDevException e) {
//            Log.w(TAG, e);
//        }
//
//        return DeviceRetCode.DEVICE_PICC_OTHER_ERR;
//    }
//
//    @Override
//    public byte iccCommand(final ApduSendL2 apduSend, ApduRespL2 apduRecv) {
//        if (transInterface == TransactionInterface.DEVICE_CLSS_TXNIF) {
//            int ret = detectOtherCard();
//
//            return ret != 0 ? (byte) ret : (byte) piccIsoCommandDevice(apduSend, apduRecv);
//        } else {
//            return (byte) iccIsoCommandDevice(apduSend, apduRecv);
//        }
//    }
//
//    @Override
//    public int fInitiate() {
//        return 0;
//    }
//
//    @Override
//    public int fWriteData(int fileIndex, final byte[] dataIn, int dataInLen) {
//        return 0;
//    }
//
//    @Override
//    public int fReadData(int fileIndex, byte[] dataOut, int dataExceptLen) {
//        return 0;
//    }
//
//    @Override
//    public int fRemove(int fileIndex) {
//        return 0;
//    }
//
//    @Override
//    public void setDebug(byte debugFlag, byte portChannel) {
//        //do nothing
//    }
//
//    private int detectOtherCard() {
//        try {
//            if (icc.detect(this.iccSlot)) {
//                return DeviceRetCode.DEVICE_PICC_INSERTED_ICCARD;
//            }
//
//            if (mag.isSwiped()) {
//                return DeviceRetCode.DEVICE_PICC_SWIPED_MAGCARD;
//            }
//        } catch (MagDevException | IccDevException e) {
//            Log.w(TAG, e);
//        }
//
//        return DeviceRetCode.DEVICE_PICC_OK;
//    }
//
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
//
//    private int iccIsoCommandDevice(final ApduSendL2 apduSend, ApduRespL2 apduRecv) {
//        ApduSendInfo send = new ApduSendInfo();
//        send.setCommand(apduSend.command);
//        send.setDataIn(apduSend.dataIn);
//        send.setLc(apduSend.lc);
//        send.setLe(apduSend.le);
//        ApduRespInfo resp;
//        try {
//            resp = icc.isoCommandByApdu(this.iccSlot, send);
//        } catch (IccDevException e) {
//            Log.w(TAG, e);
//            return DeviceRetCode.DEVICE_PICC_OTHER_ERR;
//        }
//
//        System.arraycopy(resp.getDataOut(), 0, apduRecv.dataOut, 0, resp.getDataOut().length);
//        apduRecv.lenOut = (short) resp.getDataOut().length;
//        apduRecv.swa = resp.getSwA();
//        apduRecv.swb = resp.getSwB();
//        return DeviceRetCode.DEVICE_PICC_OK;
//    }
//
//    @Override
//    public long getTickCount() {
//        Date now = new Date();
//        return now.getTime();
//    }
//
//    private static class DES {
//        private static final String TRANSFORMATION = "DES/CBC/NoPadding";
//
//        private DES() {
//
//        }
//
//        private static SecretKey genKey(final byte[] password) throws AlgoException {
//            try {
//                DESKeySpec desKey = new DESKeySpec(password);
//                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//                return keyFactory.generateSecret(desKey);
//            } catch (Exception e) {
//                throw new AlgoException(e);
//            }
//        }
//
//        static byte[] encrypt(final byte[] input, final byte[] password) throws AlgoException {
//            try {
//                SecretKey secureKey = genKey(password);
//                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//                cipher.init(Cipher.ENCRYPT_MODE, secureKey);
//                return cipher.doFinal(input);
//            } catch (Exception e) {
//                throw new AlgoException(e);
//            }
//        }
//
//        static byte[] decrypt(final byte[] input, final byte[] password) throws AlgoException {
//            try {
//                SecretKey secureKey = genKey(password);
//                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//                cipher.init(Cipher.DECRYPT_MODE, secureKey);
//                return cipher.doFinal(input);
//            } catch (Exception e) {
//                throw new AlgoException(e);
//            }
//        }
//    }
//
//    private static class AES {
//        private static final String TRANSFORMATION = "AES/CBC/NoPadding";
//
//        private AES() {
//
//        }
//
//        private static SecretKeySpec genKey(final byte[] password) throws AlgoException {
//            try {
//                KeyGenerator kgen = KeyGenerator.getInstance("AES");
//                kgen.init(password.length * 8, new SecureRandom(password));
//                SecretKey secretKey = kgen.generateKey();
//                byte[] enCodeFormat = secretKey.getEncoded();
//                return new SecretKeySpec(enCodeFormat, "AES");
//            } catch (Exception e) {
//                throw new AlgoException(e);
//            }
//        }
//
//        static byte[] encrypt(final byte[] input, final byte[] password) throws AlgoException {
//            try {
//                SecretKeySpec key = genKey(password);
//                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//                cipher.init(Cipher.ENCRYPT_MODE, key);
//                return cipher.doFinal(input);
//            } catch (Exception e) {
//                throw new AlgoException(e);
//            }
//        }
//
//        static byte[] decrypt(final byte[] input, final byte[] password) throws AlgoException {
//            try {
//                SecretKeySpec key = genKey(password);
//                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//                cipher.init(Cipher.DECRYPT_MODE, key);
//                return cipher.doFinal(input);
//            } catch (Exception e) {
//                throw new AlgoException(e);
//            }
//        }
//    }
//
//    private static class RSA {
//        private static final String TRANSFORMATION = "RSA/ECB/NoPadding";
//
//        private RSA() {
//
//        }
//
//        static byte[] recover(final byte[] modulus, int moduleLen, final byte[] exp, int expLen, final byte[] dataIn) {
//            try {
//
//                byte[] temp;
//                if (moduleLen != expLen) {
//                    PublicKey publicKey = genPublicKey(modulus, exp);
//                    if (publicKey == null)
//                        throw new IllegalArgumentException();
//
//                    temp = encryptWithPublicKey(publicKey, dataIn);
//                } else {
//                    PrivateKey privateKey = genPrivateKey(modulus, exp);
//                    if (privateKey == null)
//                        throw new IllegalArgumentException();
//                    temp = decryptWithPrivateKey(privateKey, dataIn);
//                }
//                return temp;
//            } catch (Exception e) {
//                Log.w(TAG, e);
//                throw new IllegalArgumentException();
//            }
//        }
//
//        private static byte[] encryptWithPublicKey(PublicKey pubKey, byte[] input) throws AlgoException {
//            try {
//                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//                return cipher.doFinal(input);
//            } catch (Exception e) {
//                throw new AlgoException(e);
//            }
//        }
//
//        private static byte[] decryptWithPrivateKey(PrivateKey priKey, byte[] input) throws AlgoException {
//            try {
//                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//                cipher.init(Cipher.DECRYPT_MODE, priKey);
//                return cipher.doFinal(input);
//            } catch (Exception e) {
//                throw new AlgoException(e);
//            }
//        }
//
//        private static PublicKey genPublicKey(byte[] modulus, byte[] exp) {
//            try {
//                BigInteger modulusInt = new BigInteger(1, modulus);
//                BigInteger expInt = new BigInteger(exp);
//                KeyFactory kf = KeyFactory.getInstance("RSA");
//                RSAPublicKeySpec pks = new RSAPublicKeySpec(modulusInt, expInt);
//                return kf.generatePublic(pks);
//            } catch (Exception e) {
//                Log.w(TAG, e);
//                return null;
//            }
//        }
//
//        private static PrivateKey genPrivateKey(byte[] modulus, byte[] exp) {
//            try {
//                BigInteger modulusInt = new BigInteger(1, modulus);
//                BigInteger expInt = new BigInteger(exp);
//                KeyFactory kf = KeyFactory.getInstance("RSA");
//                RSAPrivateKeySpec pks = new RSAPrivateKeySpec(modulusInt, expInt);
//                return kf.generatePrivate(pks);
//            } catch (Exception e) {
//                Log.w(TAG, e);
//                return null;
//            }
//        }
//
//    }
//
//
//    private static class AlgoException extends Exception {
//        AlgoException(Throwable cause) {
//            super(cause);
//        }
//    }
//}