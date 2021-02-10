package com.technosales.net.buslocationannouncement.userregistration;

import android.content.Context;
import android.util.Log;

import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class RegisterControllerImpl implements ICreateAccount.Controller {
TokenManager tokenManager;
    @Override
    public void createAccount(String route, CreateAccountModel model, Context context, ICreateAccount.OnFinishListener listener) {

        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class,tokenManager);
        Call<CreateAccountModel.CreateAccountResponse> call = post.issueCard(model);
        call.enqueue(new Callback<CreateAccountModel.CreateAccountResponse>() {
            @Override
            public void onResponse(Call<CreateAccountModel.CreateAccountResponse> call, Response<CreateAccountModel.CreateAccountResponse> response) {
                CreateAccountModel.CreateAccountResponse createAccountModel=response.body();
                if(response.code()==200) {
                    listener.onSuccess(createAccountModel);
                }else if(response.code()==400){
                    listener.onFailure(response.errorBody());

                }
            }

            @Override
            public void onFailure(Call<CreateAccountModel.CreateAccountResponse> call, Throwable t) {
                Log.i("TAG", "onFailure: "+t.getLocalizedMessage());
                if (t instanceof SocketTimeoutException) {
                    listener.connectionTimeOut();
                } else {
                    listener.unKnownError();
                }
            }
        });
    }

}
