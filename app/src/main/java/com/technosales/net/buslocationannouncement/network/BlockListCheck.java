package com.technosales.net.buslocationannouncement.network;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.technosales.net.buslocationannouncement.APIToken.TokenManager;
import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.HelperLogin;
import com.technosales.net.buslocationannouncement.pojo.ApiError;
import com.technosales.net.buslocationannouncement.serverconn.RetrofitInterface;
import com.technosales.net.buslocationannouncement.serverconn.ServerConfigNew;
import com.technosales.net.buslocationannouncement.helper.DatabaseHelper;
import com.technosales.net.buslocationannouncement.pojo.BlockListModel;
import com.technosales.net.buslocationannouncement.pojo.BlockList;
import com.technosales.net.buslocationannouncement.utils.GeneralUtils;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class BlockListCheck {

    public static void getBlockList(Context context) {
        TokenManager tokenManager;
        tokenManager = TokenManager.getInstance(context.getSharedPreferences("prefs", MODE_PRIVATE));
        RetrofitInterface post = ServerConfigNew.createServiceWithAuth(RetrofitInterface.class, tokenManager);
        Call<BlockList> call = post.getBlockList();
        call.enqueue(new Callback<BlockList>() {
            @Override
            public void onResponse(Call<BlockList> call, Response<BlockList> response) {
                if (response.isSuccessful()) {
                    BlockList repos = response.body();
                    final DatabaseHelper databaseHelper = new DatabaseHelper(context);
                    databaseHelper.clearBlockList();
                    List<BlockList.Datum> blocklistRes = repos.getData();
                    if (blocklistRes.size() != 0) {
                        for (int i = 0; i < blocklistRes.size(); i++) {
                            BlockListModel blockListModel = new BlockListModel();
                            blockListModel.identificationId = blocklistRes.get(i).getIdentificationId();
                            blockListModel.mobileNo = blocklistRes.get(i).getMobileNo();
                            databaseHelper.insertBlockList(blockListModel);
                        }
                    }else {
                        databaseHelper.clearBlockList();
                    }
                } else if(response.code()==401){
                    Toast.makeText(context, context.getString(R.string.token_expire), Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context,HelperLogin.class));
                }else if(response.code()==404){
                    handleErrors(response.errorBody(),context);
                }
            }

            @Override
            public void onFailure(Call<BlockList> call, Throwable t) {

            }
        });
    }

    private static void handleErrors(ResponseBody responseBody, Context context) {
        ApiError apiErrors = GeneralUtils.convertErrors(responseBody);
        if (responseBody != null) {
            for (Map.Entry<String, List<String>> error : apiErrors.getErrors().entrySet()) {
                Toast.makeText(context, error.getValue().get(0), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
