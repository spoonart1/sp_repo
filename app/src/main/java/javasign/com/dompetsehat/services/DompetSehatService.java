package javasign.com.dompetsehat.services;

import com.google.gson.Gson;

import javasign.com.dompetsehat.models.response.AgentListResponse;
import javasign.com.dompetsehat.models.response.CashflowAccountResponse;
import javasign.com.dompetsehat.models.response.DebtsResponse;
import javasign.com.dompetsehat.models.response.MamiDataResponse;
import javasign.com.dompetsehat.models.response.OtherResponse;
import javasign.com.dompetsehat.models.response.ParentResponse;
import javasign.com.dompetsehat.models.response.PortofolioResponse;
import javasign.com.dompetsehat.models.response.ReferralFeeResponse;
import javasign.com.dompetsehat.models.response.RegisterMamiResponse;
import javasign.com.dompetsehat.models.response.RenameCategoryResponse;
import javasign.com.dompetsehat.models.response.SubCategoryResponse;
import javasign.com.dompetsehat.models.response.SyncAccountResponse;
import javasign.com.dompetsehat.models.response.SyncResponse;
import javax.inject.Inject;

import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.models.response.CashFlowResponse;
import javasign.com.dompetsehat.models.response.CreateAccountResponse;
import javasign.com.dompetsehat.models.response.SignInResponse;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by aves on 8/8/16.
 */

public interface DompetSehatService {
  static MCryptNew mcrypt = new MCryptNew();
  static String URL = "https://" + mcrypt.decrypt(BuildConfig.SERVER_URL_RELEASE) + "/";

  @FormUrlEncoded @POST("auth") Observable<SignInResponse> login(@Field("app_id") String app_id,
      @Field("auth_with") String auth_with, @Field("email") String email,
      @Field("username") String username, @Field("password") String password);

  @FormUrlEncoded @POST("auth") Observable<SignInResponse> authFacebook(
      @Field("app_id") String app_id, @Field("auth_with") String auth_with,
      @Field("email") String email, @Field("access_token") String access_token,
      @Field("birthday") String birthday, @Field("gender") String gender,
      @Field("username") String username, @Field("password") String password);

  @FormUrlEncoded @POST("auth") Observable<SignInResponse> authAccountKit(
      @Field("app_id") String app_id, @Field("auth_with") String auth_with,
      @Field("auth_code") String auth_code, @Field("access_token") String access_token,
      @Field("phone") String phone, @Field("email") String email,
      @Field("username") String username, @Field("password") String password,
      @Field("birthday") String birthday, @Field("gender") String gender,
      @Field("avatar") String avatar);

  @FormUrlEncoded @POST("auth") Observable<SignInResponse> register(@Field("app_id") String app_id,
      @Field("auth_with") String auth_with, @Field("email") String email,
      @Field("username") String username, @Field("password") String password,
      @Field("birthday") String birthday, @Field("gender") String gender);

  @FormUrlEncoded @POST("account/create") Observable<CreateAccountResponse> account_create(
      @Field("vendor_id") int vendor_id, @Field("nickname") String nickname,
      @Field("balance") int balance, @Field("username") String username,
      @Field("password") String password);

  @FormUrlEncoded @POST("sync/all") Observable<ResponseBody> sync_all(@Field("type") String type,
      @Field("data") String data);

  @FormUrlEncoded @POST("syncoffline") Observable<CashFlowResponse> sync_cashflow(
      @Field("cashflow") String cashflow);

  @FormUrlEncoded @POST("feedback/create") Observable<ParentResponse> send_feedback(
      @Field("app_id") int app_id, @Field("subject") String subject, @Field("prioritas") String prioritas,
      @Field("message") String message);

  @FormUrlEncoded @POST("account/create ") Observable<CreateAccountResponse> create_account(
      @Field("vendor_id") int vendor_id, @Field("username") String username,
      @Field("password") String password, @Field("name") String name);

  @FormUrlEncoded @POST("syn-all") Observable<SyncResponse> sync(
      @Field("account") String account,
      @Field("plan") String plan,
      @Field("budget") String budget,
      @Field("alarm") String alarm,
      @Field("lastsync") long lastsync,
      @Field("notification") String notif
  );

  @GET("cashflow/all") Observable<CashFlowResponse> cashflow();

  @GET("cashflow/lastconnect") Observable<CashFlowResponse> cashflow(@Query("date") long date);

  @FormUrlEncoded @POST("userinfo/password") Observable<OtherResponse> change_password(
      @Field("old") String old_password,
      @Field("new") String new_password);


  // Account
  @DELETE("account/delete/{account_id}")Observable<ParentResponse> delete_account(
      @Path("account_id") int account_id
  );

  // Manulife
  @FormUrlEncoded @POST("manulife/validate-customer")
  Observable<MamiDataResponse> validate_customer(@Field("email") String email,
      @Field("identity_number") String identity_number, @Field("phone") String phone);

  @FormUrlEncoded @POST("manulife/validate-referral-code")
  Observable<MamiDataResponse> validate_referral_code(@Field("referral_code") String referral_code);

  @FormUrlEncoded @POST("manulife/register") Observable<RegisterMamiResponse> register_manulife(
      @Field("identity_number") String identity_number, @Field("email") String email,
      @Field("country") String country, @Field("phone") String phone,
      @Field("password") String password, @Field("confirm_password") String confirm_password,
      @Field("referred_by") String referred_by, @Field("referral_code") String referral_code);

  @FormUrlEncoded @POST("manulife/login") Observable<MamiDataResponse> login_mami(
      @Field("username") String username,
      @Field("password") String password,
      @Field("referral_code") String referral_code
  );

  @FormUrlEncoded @POST("userinfo/updateProfile")Observable<SignInResponse>update_profile(
      @Field("avatar_name") String avatar_name,
      @Field("avatar") String avatar,
      @Field("birthday") String bithday,
      @Field("email") String email,
      @Field("phone") String phone,
      @Field("income") Double income,
      @Field("kids") Integer kids
  );

  @FormUrlEncoded @POST("request-list") Observable<AgentListResponse> get_list_request(
      @Field("status") String status
  );

  @FormUrlEncoded @POST("add-agent") Observable<ParentResponse> add_agent(
      @Field("register") String register,
      @Field("username") String username,
      @Field("status") String approved,
      @Field("data") String data
  );

  @GET("manulife/referral-fee/{dateStart}/{dateEnd}") Observable<ResponseBody> referral_fee(
      @Path(value = "dateStart", encoded = true) String dateStart,
      @Path(value = "dateEnd", encoded = true) String dateEnd
  );

  @GET("manulife/customers/portofolio") Observable<PortofolioResponse> portofolio();

  @GET("account/sync/{id_account}") Observable<SyncAccountResponse> account_sync(
      @Path("id_account") int id_account
  );

  @GET("cashflow/lastconnect/account/{id_account}") Observable<CashflowAccountResponse> lastconnect_cashflow_account(
      @Path("id_account") int id_account,
      @Query("date") long lastconnect
  );

  @FormUrlEncoded @POST("category/user-category") Observable<SubCategoryResponse> create_category(
      @Field("category_id") int id_category_parent,
      @Field("color") String color,
      @Field("color_name") String color_name,
      @Field("name") String name
  );

  @FormUrlEncoded @POST("forgot") Observable<ParentResponse> forgot_password(
      @Field("email") String email,
      @Field("new") String new_password,
      @Field("confirm")String confirm_password
  );

  @FormUrlEncoded @POST("category/rename") Observable<RenameCategoryResponse> rename_category(
      @Field("category_id") int category_id,
      @Field("name") String name,
      @Field("description") String description
  );

  @FormUrlEncoded @PUT("account/update/{account_id}") Observable<CreateAccountResponse> update_account(
      @Path("account_id") int account_id,
      @Field("username") String username,
      @Field("password") String password
  );

  @FormUrlEncoded @PUT("account/update/{account_id}") Observable<CreateAccountResponse> update_dompet(
      @Path("account_id") int account_id,
      @Field("nickname") String nickname,
      @Field("balance") int balance
  );

  @FormUrlEncoded @PUT("account/update/{account_id}") Observable<CreateAccountResponse> rename_account(
      @Path("account_id") int account_id,
      @Field("nickname") String nickname
  );

  @FormUrlEncoded @PUT("account/update/product/{product_id}") Observable<CreateAccountResponse> update_product(
      @Path("product_id") int product_id,
      @Field("nickname") String nickname
  );

  @GET("cashflow/account/{account_id}") Observable<CashflowAccountResponse> cashflow_account(
      @Path("account_id")int account_id
  );

  @GET("cashflow/lastconnect/account/{account_id}") Observable<CashflowAccountResponse> cashflow_account(
      @Path("account_id")int account_id,
      @Query("date") long date);


  @GET("debt/get") Observable<DebtsResponse> get_debts();
  @GET("debt/getByType/BORROW") Observable<DebtsResponse> get_debts_borrow();
  @GET("debt/getByType/LEND") Observable<DebtsResponse> get_debts_lend();
  @FormUrlEncoded @POST("debt/create") Observable<DebtsResponse> create_debt(
      @Field("type") String type,
      @Field("switch") int _switch,
      @Field("name") String name,
      @Field("amount") Integer amount,
      @Field("cashflow_id") Integer cashflow_id,
      @Field("product_id") Integer product_id,
      @Field("date") String date,
      @Field("payback") String payback,
      @Field("email") String email,
      @Field("alarm_switch") int alarm_switch
  );

  @DELETE("debt/delete/{id}")Observable<ParentResponse> delete_debt(@Path("id") int id);


  @GET("userinfo/reset")Observable<ParentResponse> reset_data();

  @FormUrlEncoded @POST("fcm-id") Observable<ResponseBody> fcm(@Field("fcm")String fcm);

  @GET("account/getReferralFee/{account_id}") Observable<ReferralFeeResponse> get_referral_fee(@Path("account_id") int account_id);

  @FormUrlEncoded @POST("account/sync/{account_id}") Observable<SyncAccountResponse> get_manual_bank(
      @Path("account_id")int account_id,
      @Field("start_date")String start_date,
      @Field("end_date")String end_date
  );

  @FormUrlEncoded @POST("set-permissions") Observable<ParentResponse> set_permisions(
      @Field("id") int id,
      @Field("status")String status,
      @Field("data")String permission
  );

  @FormUrlEncoded @POST("resend-email") Observable<ParentResponse> resend_email(@Field("email")String email);

  class Creator {
    @Inject public DompetSehatService newDompetSehatService(OkHttpClient httpClient, Gson gson) {
      Retrofit retrofit = new Retrofit.Builder().baseUrl(URL)
          .addConverterFactory(GsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .client(httpClient)
          .build();
      return retrofit.create(DompetSehatService.class);
    }
  }
}
