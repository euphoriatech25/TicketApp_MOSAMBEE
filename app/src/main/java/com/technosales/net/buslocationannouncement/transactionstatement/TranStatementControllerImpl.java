package com.technosales.net.buslocationannouncement.transactionstatement;

import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class TranStatementControllerImpl implements ITransactionStatement.Controller  {
    TokenManager tokenManager;
    @Override
    public void requestTransaction(String route, String mobileNo, String fromDate, String toDate, ITransactionStatement.OnFinishListener listener) {
//        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));

        RetrofitInterface post = ServerConfigNew.createService(RetrofitInterface.class);
        Call<TransactionStatementModel> call = post.getTransactionStatement(mobileNo,fromDate,toDate);
        call.enqueue(new Callback<TransactionStatementModel>() {
            @Override
            public void onResponse(Call<TransactionStatementModel> call, Response<TransactionStatementModel> response) {
                TransactionStatementModel transactionModel=response.body();
                if(response.code()==200){
                    listener.onSuccessSt(transactionModel);
                }else {
                    listener.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<TransactionStatementModel> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    listener.connectionTimeOut();
                } else {
                    listener.unKnownError();
                }
            }
        });


    }
}
