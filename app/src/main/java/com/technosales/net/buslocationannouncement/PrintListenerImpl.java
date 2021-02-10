package com.technosales.net.buslocationannouncement;

import android.content.Context;
import android.os.ConditionVariable;

import com.technosales.net.buslocationannouncement.paxsupport.printer.PrintListener;

public class PrintListenerImpl implements PrintListener {

    private Context context;
    private ConditionVariable cv;
    private Status result = Status.OK;

    public PrintListenerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onShowMessage(final String title, final String message) {
        TicketBusApp.getApp().runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                if (showMsgDialog == null) {
//                    showMsgDialog = new CustomAlertDialog(context, CustomAlertDialog.PROGRESS_TYPE);
//                    showMsgDialog.show();
//                    showMsgDialog.setCancelable(false);
//                    showMsgDialog.setTitleText(title);
//                    showMsgDialog.setContentText(message);
//
//                } else {
//                    if (!showMsgDialog.isShowing()) {
//                        showMsgDialog.show();
//                    }
//                    showMsgDialog.setTitleText(title);
//                    showMsgDialog.setContentText(message);
//                }
            }
        });
    }

    @Override
    public Status onConfirm(final String title, final String message) {
        cv = new ConditionVariable();
        result = Status.OK;
        TicketBusApp.getApp().runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                if (confirmDialog != null) {
//                    confirmDialog.dismiss();
//                }
//                confirmDialog = new CustomAlertDialog(context, CustomAlertDialog.ERROR_TYPE);
//                confirmDialog.show();
//                confirmDialog.setTimeout(30);
//                confirmDialog.setTitleText(title);
//                confirmDialog.setContentText(message);
//                confirmDialog.setCancelable(false);
//                confirmDialog.setCanceledOnTouchOutside(false);
//                confirmDialog.showCancelButton(true);
//                confirmDialog.setCancelClickListener(new OnCustomClickListener() {
//
//                    @Override
//                    public void onClick(CustomAlertDialog alertDialog) {
//                        result = Status.CANCEL;
//                        alertDialog.dismiss();
//                    }
//                });
//                confirmDialog.showConfirmButton(true);
//                confirmDialog.setConfirmClickListener(new OnCustomClickListener() {
//
//                    @Override
//                    public void onClick(CustomAlertDialog alertDialog) {
//                        result = Status.CONTINUE;
//                        alertDialog.dismiss();
//                    }
//                });
//                confirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        if (result == Status.OK) {
//                            result = Status.CANCEL;
//                        }
//                        if (cv != null) {
//                            cv.open();
//                        }
//                    }
//                });
//                confirmDialog.show();

            }
        });
        cv.block();
        return result;
    }

    @Override
    public void onEnd() {
//        if (showMsgDialog != null) {
//            showMsgDialog.dismiss();
//        }
//        if (confirmDialog != null) {
//            confirmDialog.dismiss();
//        }
    }

}
