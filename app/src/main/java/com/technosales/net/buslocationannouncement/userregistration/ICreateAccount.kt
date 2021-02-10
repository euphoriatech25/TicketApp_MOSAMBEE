package com.technosales.net.buslocationannouncement.userregistration;
import android.content.Context
import com.technosales.net.buslocationannouncement.interfaces.BasePresenter
import com.technosales.net.buslocationannouncement.interfaces.BaseResponse
import com.technosales.net.buslocationannouncement.interfaces.BaseView
import okhttp3.ResponseBody


internal interface ICreateAccount {
    interface View : BaseView {
        fun getIdentificationId(): String
        fun getMobileNo(): String
        fun getFirstName(): String
        fun getMiddleName(): String
        fun getLastName(): String
        fun getContactNo(): String
        fun getEmailAddress(): String
        fun getAddress(): String
        fun getUserType(): String
        fun getDeviceId(): String
        fun getDeviceUserId(): String

        fun getLat():String
        fun getLng():String
        fun getTicketId():String
        fun getDeviceTime():String

        fun onSuccess(createAccountResponse: CreateAccountModel.CreateAccountResponse)
            fun onFailure(responseBody: ResponseBody)
    }

    interface Presenter : BasePresenter {
        fun createAccount(context:Context)

    }

    interface Controller {
        fun createAccount(route: String, model: CreateAccountModel,context:Context, listener: OnFinishListener)
       }
    interface OnFinishListener : BaseResponse {
        fun onSuccess(createAccountResponse: CreateAccountModel.CreateAccountResponse)
        fun onFailure(responseBody: ResponseBody)
    }
}