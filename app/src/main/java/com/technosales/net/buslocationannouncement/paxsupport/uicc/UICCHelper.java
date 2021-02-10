package com.technosales.net.buslocationannouncement.paxsupport.uicc;

import com.pax.dal.IIcc;
import com.pax.dal.entity.ApduRespInfo;
import com.pax.dal.entity.ApduSendInfo;
import com.pax.dal.memorycard.ICardAT24Cxx;
import com.pax.dal.memorycard.ICardAT88SC102;
import com.pax.dal.memorycard.ICardAT88SC153;
import com.pax.dal.memorycard.ICardAT88SC1608;
import com.pax.dal.memorycard.ICardSle4428;
import com.pax.dal.memorycard.ICardSle4442;

public class UICCHelper implements IIcc {

    UICCHelper() {
        //do nothing
    }

    @Override
    public byte[] init(byte var1) {
        return new byte[]{0x00};
    }

    @Override
    public void close(byte var1) {
        //do nothing
    }

    @Override
    public void autoResp(byte var1, boolean var2) {
        //do nothing
    }

    @Override
    public byte[] isoCommand(byte var1, byte[] var2) {
        return new byte[]{0x00};
    }

    @Override
    public boolean detect(byte var1) {
        return true;
    }

    @Override
    public void light(boolean var1) {
        //do nothing
    }

    @Override
    public ApduRespInfo isoCommandByApdu(byte var1, ApduSendInfo var2) {
        return null;
    }

    @Override
    public ICardAT24Cxx getCardAT24Cxx() {
        return null;
    }

    @Override
    public ICardAT88SC102 getCardAT88SC102() {
        return null;
    }

    @Override
    public ICardAT88SC153 getCardAT88SC153() {
        return null;
    }

    @Override
    public ICardSle4428 getCardSle4428() {
        return null;
    }

    @Override
    public ICardAT88SC1608 getCardAT88SC1608() {
        return null;
    }

    @Override
    public ICardSle4442 getCardSle4442() {
        return null;
    }
}
