package com.technosales.net.buslocationannouncement.userregistration;

import android.content.Context;
import android.text.TextUtils;

import com.technosales.net.buslocationannouncement.utils.GeneralUtils;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import okhttp3.ResponseBody;

public class RegisterImplPresenter implements ICreateAccount.Presenter, ICreateAccount.OnFinishListener {

    private static final String TAG = "CREATEACCOUNTACITIVITY";
    private ICreateAccount.View view;
    private RegisterControllerImpl controller;

    public RegisterImplPresenter(ICreateAccount.View view, RegisterControllerImpl controller) {
        this.view = view;
        this.controller = controller;
    }

    @Override
    public void createAccount(Context context) {
        if (view != null) {
            String identificationId = view.getIdentificationId();
            String mobileNo = view.getMobileNo();
            String firstName = view.getFirstName();
            String middleName = view.getMiddleName();
            String lastName = view.getLastName();
            String contactNo = view.getContactNo();
            String email = view.getEmailAddress();
            String address = view.getAddress();
            String userType = view.getUserType();
            String deviceId = view.getDeviceId();
            String deviceUserId = view.getDeviceUserId();

            String lat=view.getLat();
            String lng=view.getLng();
            String deviceTime=view.getDeviceTime();
            String ticketId=view.getTicketId();

            boolean hasError = false;

            if (!TextUtils.isEmpty(email) && !GeneralUtils.isValidEmailId(email)) {
                hasError = true;

            }


            if (contactNo.length()!=10) {
                hasError = true;
            }


            if (!hasError) {
                view.showProgressBar(true);
                CreateAccountModel model = new CreateAccountModel(identificationId,mobileNo,firstName,middleName,lastName,contactNo,email,address,userType,deviceId,deviceUserId,lat,lng,deviceTime,ticketId);
                controller.createAccount(UtilStrings.NEW_PASSENGER_REGISTER, model,context, this);
            }
        }
    }

    @Override
    public void onSuccess(CreateAccountModel.CreateAccountResponse createAccountResponse) {
        if (view != null) {
            view.showProgressBar(false);
            view.onSuccess(createAccountResponse);
        }
    }


    @Override
    public void onFailure(ResponseBody responseBody) {
        if (view != null) {
            view.showProgressBar(false);
            view.onFailure(responseBody);
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
