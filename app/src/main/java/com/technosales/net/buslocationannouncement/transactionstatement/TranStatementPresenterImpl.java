package com.technosales.net.buslocationannouncement.transactionstatement;

import android.text.TextUtils;

public class TranStatementPresenterImpl implements ITransactionStatement.Presenter, ITransactionStatement.OnFinishListener {
    private ITransactionStatement.View view;
    private TranStatementControllerImpl controller;

    public TranStatementPresenterImpl(ITransactionStatement.View view, TranStatementControllerImpl controller) {
        this.view = view;
        this.controller = controller;
    }
    @Override
    public void requestTransaction() {
        if (view != null) {
            String mobileNo = view.getMobileNo();
            String fromDate = view.getFromDate();
           String toDate = view.getToDate();

            boolean hasError = false;
            if (TextUtils.isEmpty(mobileNo)) {
                hasError = true;
                view.setMobileError();
            }else if(TextUtils.isEmpty(fromDate)){
                hasError = true;
                view.setFromDateError();
            }else if(TextUtils.isEmpty(toDate)){
                hasError = true;
                view.setToDateError();
            }

            if (!hasError) {
                view.showProgressBar(true);
                controller.requestTransaction("route" , mobileNo,fromDate,toDate, this);
            }
        }

    }

    @Override
    public void onSuccessSt(TransactionStatementModel transactionModel) {
        if (view != null) {
            view.showProgressBar(false);
            view.onSuccessSt(transactionModel);
        }
    }


    @Override
    public void onFailure(String message) {
        if (view != null) {
            view.showProgressBar(false);
            view.onFailure(message);
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void noInternetConnection() {
        if (view != null) {
            view.showProgressBar(false);
            view.noInternetConnection();
        }
    }

    @Override
    public void connectionTimeOut() {
        if (view != null) {
            view.showProgressBar(false);
            view.connectionTimeOut();
        }
    }

    @Override
    public void unKnownError() {
        if (view != null) {
            view.showProgressBar(false);
            view.unKnownError();
        }
    }
}
