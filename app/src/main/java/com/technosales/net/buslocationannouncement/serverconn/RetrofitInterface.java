package com.technosales.net.buslocationannouncement.serverconn;

import com.technosales.net.buslocationannouncement.RouteStationListModel.DeviceLoginModel;
import com.technosales.net.buslocationannouncement.RouteStationListModel.FareList;
import com.technosales.net.buslocationannouncement.RouteStationListModel.RouteModel;
import com.technosales.net.buslocationannouncement.RouteStationListModel.StationModel;
import com.technosales.net.buslocationannouncement.pojo.BlockList;
import com.technosales.net.buslocationannouncement.pojo.CallResponse;
import com.technosales.net.buslocationannouncement.pojo.CheckBalanceModel;
import com.technosales.net.buslocationannouncement.pojo.HelperModel;
import com.technosales.net.buslocationannouncement.pojo.ReIssueCardResponse;
import com.technosales.net.buslocationannouncement.pojo.Recharge;
import com.technosales.net.buslocationannouncement.pojo.TraModel;
import com.technosales.net.buslocationannouncement.pojo.TransactionModel;
import com.technosales.net.buslocationannouncement.userregistration.CreateAccountModel;
import com.technosales.net.buslocationannouncement.utils.UtilStrings;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {


    @FormUrlEncoded
    @POST("helper_login")
    Call<HelperModel> helperLogin(@Field("identificationId") String card_number,
                                  @Field("unique_id") String device_id);

    @FormUrlEncoded
    @POST(UtilStrings.LOGIN_DEVICE)
    Call<DeviceLoginModel> deviceLogin(@FieldMap Map<String, Object> params);


    @GET(UtilStrings.GET_ROUTE_LIST)
    Call<RouteModel> getRouteList(@Path("id") String id);

    @GET(UtilStrings.GET_STATION_LIST)
    Call<StationModel> getStationList(@Path("id") String id);

    @GET(UtilStrings.GET_DEVICE_FARE_LIST)
    Call<FareList> getDeviceFareList(@Path("id") String id);


    @POST(UtilStrings.NEW_PASSENGER_REGISTER)
    Call<CreateAccountModel.CreateAccountResponse> issueCard(@Body CreateAccountModel createAccountModel);

    @FormUrlEncoded
    @POST(UtilStrings.NEW_TRANSACTION)
    Call<TransactionModel.TransactionResponse> transaction(@FieldMap Map<String, Object> params);


     @FormUrlEncoded
    @POST(UtilStrings.NEW_TRANSACTION)
    Call<TraModel> transactionHistory(@FieldMap Map<String, Object> params);


    @FormUrlEncoded
    @POST(UtilStrings.NEW_TRANSACTION)
    Call<Recharge> recharge(@FieldMap Map<String, Object> params);

//
//    @FormUrlEncoded
//    @POST(UtilStrings.UPDATE_DEVICE_INFO)
//    Call<ResponseBody> updateDeviceInfo(@Field("device_id") String deviceId,
//                                        @Field("mobileNo") String currentHelper);


    @FormUrlEncoded
    @POST(UtilStrings.CARD_BLOCK)
    Call<ResponseBody> block_card(@Field("mobileNo") String mobileNo);

    @FormUrlEncoded
    @POST(UtilStrings.CARD_REISSUE)
    Call<ReIssueCardResponse> reissue_card(@Field("identificationId") String identificationId,
                                           @Field("mobileNo") String mobileNo);

    @FormUrlEncoded
    @POST(UtilStrings.PASSENGER_CHECK_BALANCE)
    Call<CheckBalanceModel> checkBalance(@Field("identificationId") String card_number);


    @POST(UtilStrings.REFRESH_TOKEN)
    @FormUrlEncoded
    Call< HelperModel.Token> refresh(@Field("refresh_token")String refreshToken);



    @GET(UtilStrings.GET_CARD_BLOCK)
    Call<BlockList> getBlockList();


    @GET("call_record_verification")
    Call<CallResponse> getNumber(@Query("mobile_number") String mobile_number,@Query("server_number") String server_number);

    @FormUrlEncoded
    @POST(UtilStrings.UPDATE_PASSENGER_COUNT)
    Call<ResponseBody> updatePassengerCount(@Field("unique_id") String device_id, @Field("current_passenger_number") String passengerCount);


    @GET("get_server_number")
    Call<CallResponse.GetServerNumber> getServerNumber();


}
