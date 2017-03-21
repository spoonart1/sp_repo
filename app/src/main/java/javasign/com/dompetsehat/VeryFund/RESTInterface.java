package javasign.com.dompetsehat.VeryFund;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit2.http.GET;


/**
 * Created by Xenix on 11/30/2015.
 */
public interface RESTInterface {

    @Multipart
    @Headers("APIKEY: kVBkcfp3ivy3GqzSn62Dn2sW27X408VN")
    @POST("/register")
    void register(@Part("app_id") Integer app_id, @Part("username")String username, @Part("email")String email,
                  @Part("password")String password, @Part("gender")String gender, @Part("birthday")String birthday, Callback<Response> callback);

    @Multipart
    @Headers("APIKEY: kVBkcfp3ivy3GqzSn62Dn2sW27X408VN")
    @POST("/login")
    void login(@Part("app_id")Integer app_id, @Part("username")String username, @Part("password")String password, Callback<Response> callback);

    @Headers("APIKEY: kVBkcfp3ivy3GqzSn62Dn2sW27X408VN")
    @GET("/version/2")
    void check_version(Callback<Response> callback);

    @Multipart
    @Headers("APIKEY: kVBkcfp3ivy3GqzSn62Dn2sW27X408VN")
    @POST("/forgot")
    void forgot(@Part("email")String username, @Part("new")String password, @Part("confirm")String confirm,
                @Part("app_id")int app_id, Callback<Response> callback);

    @Multipart
    @POST("/syncoffline")
    void syncOfflineCashflow(@Header("ACCESSTOKEN") String access_token, @Part("cashflow")String cashflow, Callback<Response> callback);

    @Multipart
    @POST("/syncofflinebudget")
    void syncOfflineBudget(@Header("ACCESSTOKEN") String access_token, @Part("budget")String budget, Callback<Response> callback);

    @Multipart
    @POST("/syncofflinealarm")
    void syncOfflineAlarm(@Header("ACCESSTOKEN") String access_token, @Part("alarm")String alarm, Callback<Response> callback);

    @Multipart
    @POST("/account/create")
    void account_create(@Header("ACCESSTOKEN") String access_token, @Part("vendor_id") Integer vendor_id, @Part("username") String username,
                   @Part("password") String password, @Part("balance") String balance, @Part("nickname") String nickname, Callback<Response> callback);
    @FormUrlEncoded
    @POST("/userinfo/updatePhoto")
    void userinfo_updatePhoto(@Header("ACCESSTOKEN")String access_token, @Field("avatar")String avatar, @Field("avatar_name")String avatar_name, Callback<Response> callback);

    @FormUrlEncoded
    @POST("/userinfo/updateProfile")
    void userinfo_updateProfile(@Header("ACCESSTOKEN")String access_token, @Field("birthday")String birthday, @Field("occupation")String occupation, @Field("address")String address, @Field("postal")String postal,
                                @Field("gender") String gender, Callback<Response> callback);

    @DELETE("/account/delete/{id_account}")
    void delete_account(@Header("ACCESSTOKEN")String access_token, @Path("id_account") int id_account, Callback<Response> callback);

    @DELETE("/account/delete/product/{id_product}")
    void delete_product(@Header("ACCESSTOKEN")String access_token, @Path("id_product") int id_product, Callback<Response> callback);

    @GET("/cashflow/all")
    void cashflow_all(@Header("ACCESSTOKEN")String access_token, Callback<Response> callback);

    @GET("/account/sync/{id_account}")
    void sync_peraccount(@Header("ACCESSTOKEN")String access_token, @Path("id_account") int id_account, Callback<Response> callback);

    @GET("/cashflow/lastconnect/account/{id_account}")
    void account_lastconnect(@Header("ACCESSTOKEN")String access_token, @Path("id_account") int id_account, @Query("date") Long date, Callback<Response> callback);

    @GET("/user/reset")
    void delete_all(@Header("ACCESSTOKEN")String access_token, Callback<Response> callback);

    @DELETE("/budget/delete/{id_budget}")
    void delete_budget(@Header("ACCESSTOKEN")String access_token, @Path("id_budget")int id_budget, Callback<Response> callback);

    @DELETE("/alarm/delete/{id_alarm}")
    void delete_alarm(@Header("ACCESSTOKEN")String access_token, @Path("id_alarm")int id_alarm, Callback<Response> callback);

    @DELETE("/cashflow/delete/{id_cashflow}")
    void delete_cashflow(@Header("ACCESSTOKEN")String access_token, @Path("id_cashflow")int id_cashflow, Callback<Response> callback);

    @FormUrlEncoded
    @POST("/feedback/create")
    void feed_back(@Header("ACCESSTOKEN")String access_token, @Field("app_id")int app_id, @Field("title")String title,
                   @Field("subject")String subject, @Field("message")String message, Callback<Response> callback);

    @FormUrlEncoded
    @POST("/plan/create")
    void goal_create(@Header("ACCESSTOKEN")String access_token, @Field("title")String title, @Field("total")String total,
                     @Field("date_start")String date_start, @Field("date_end")String date_end, @Field("debetAmount")String amount,
                     @Field("spare_each")String spare_each, @Field("category_id")int category_id,
                     Callback<Response> callback);

    @FormUrlEncoded
    @POST("/userinfo/updateReferrer")
    void add_referral(@Header("ACCESSTOKEN")String access_token, @Field("referrer")String referrer, Callback<Response> callback);

    @Multipart
    @PUT("/account/update/product/{product_id}")
    void update_product(@Header("ACCESSTOKEN")String access_token, @Path("product_id")int
            product_id, @Part("nickname")String nickname, @Part("balance")String balance,
                      Callback<retrofit.client.Response> callback);

    @GET("/userinfo/referrals")
    void getAll_downline(@Header("ACCESSTOKEN")String access_token, Callback<Response> callback);

}