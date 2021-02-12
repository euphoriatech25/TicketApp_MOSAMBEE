package com.technosales.net.buslocationannouncement.mosambeesupport;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.morefun.yapi.ServiceResult;
import com.morefun.yapi.device.reader.icc.ICCSearchResult;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccCardType;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.device.reader.icc.OnSearchIccCardListener;
import com.morefun.yapi.device.reader.mag.MagCardInfoEntity;
import com.morefun.yapi.device.reader.mag.OnSearchMagCardListener;
import com.morefun.yapi.engine.DeviceServiceEngine;
import com.technosales.net.buslocationannouncement.additionalfeatures.PayByCardActivity;


public class SearchCardOrCardReaderTest {
    private static final String TAG = "SearchCardTest";
    private DeviceServiceEngine mEngine;

    public SearchCardOrCardReaderTest(DeviceServiceEngine mEngine) {
        this.mEngine = mEngine;

    }

    public void searchRFCard(final String[] cardtype, final PayByCardActivity.OnSearchListener onSearchListener) {
        try {
            final IccCardReader rfReader = mEngine.getIccCardReader(IccReaderSlot.RFSlOT);

            OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
                @Override
                public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {
//                    ICCSearchResult.CARDTYPE
                    if (retCode == 0) {
                        Log.d(TAG, "retCode= " + retCode);
                        Log.d(TAG, "cardType:" + bundle.getString(ICCSearchResult.CARDTYPE));
                        rfReader.stopSearch();
                        onSearchListener.onSearchResult(retCode, bundle);
                    } else {

                    }
                }
            };
            rfReader.searchCard(listener, 30, cardtype);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
