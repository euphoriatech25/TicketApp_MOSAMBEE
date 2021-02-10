package com.technosales.net.buslocationannouncement.mosambeesupport;


import android.os.RemoteException;
import com.morefun.yapi.device.reader.icc.IccCardReader;
import com.morefun.yapi.device.reader.icc.IccReaderSlot;
import com.morefun.yapi.engine.DeviceServiceEngine;


public class SearchCardOrCardReader {
    private static final String TAG = "SearchCardTest";
    private DeviceServiceEngine mEngine;

    public SearchCardOrCardReader(DeviceServiceEngine mEngine) {
        this.mEngine = mEngine;

    }

    public void searchRFCard(final String[] cardtype) {
        try {
            final IccCardReader rfReader = mEngine.getIccCardReader(IccReaderSlot.RFSlOT);
//            mAlertDialogOnShowListener.showProgress(getString(R.string.msg_rfcard), new ActionItems.OnCancelCall() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    try {
//                        Log.v(TAG, "rfReader.stopSearch();");
//                        rfReader.stopSearch();
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            OnSearchIccCardListener.Stub listener = new OnSearchIccCardListener.Stub() {
//                @Override
//                public void onSearchResult(int retCode, Bundle bundle) throws RemoteException {
////                    ICCSearchResult.CARDTYPE
//                    if (retCode == 0) {
//                        Log.d(TAG, "retCode= " + retCode);
//                        Log.d(TAG, "cardType:" + bundle.getString(ICCSearchResult.CARDTYPE));
//                        rfReader.stopSearch();
//                        beeper(retCode);
//                        onSearchListener.onSearchResult(retCode, bundle);
//                    } else {
//                        mAlertDialogOnShowListener.dismissProgress();
//                    }
//                }
//            };
//            rfReader.searchCard(listener, 30, cardtype);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    private void beeper(int retCode) throws RemoteException {
//        mEngine.getBeeper().beep(retCode == ServiceResult.Success ? BeepModeConstrants.SUCCESS : BeepModeConstrants.FAIL);
    }





}
