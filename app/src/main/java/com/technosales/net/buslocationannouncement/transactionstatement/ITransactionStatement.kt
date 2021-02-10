package com.technosales.net.buslocationannouncement.transactionstatement

import com.technosales.net.buslocationannouncement.interfaces.BasePresenter
import com.technosales.net.buslocationannouncement.interfaces.BaseResponse
import com.technosales.net.buslocationannouncement.interfaces.BaseView


internal interface ITransactionStatement {
    interface View : BaseView {
        fun getMobileNo(): String
        fun getFromDate(): String
        fun getToDate(): String

        fun  setMobileError()
        fun setFromDateError()
        fun setToDateError()

        fun onSuccessSt(transactionModel: TransactionStatementModel)
        fun onFailure(message: String)
    }

    interface Presenter : BasePresenter {
        fun requestTransaction()
    }

    interface Controller {
        fun requestTransaction(route: String,
                               mobileNo:String,
                               fromDate: String,
                                toDate:String,listener: OnFinishListener)
    }
    interface OnFinishListener : BaseResponse {
        fun onSuccessSt(transactionModel: TransactionStatementModel)
        fun onFailure(message: String)
    }
}