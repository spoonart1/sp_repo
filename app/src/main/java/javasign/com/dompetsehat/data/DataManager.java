package javasign.com.dompetsehat.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.DanaDarurat;
import javasign.com.dompetsehat.models.DanaKuliah;
import javasign.com.dompetsehat.models.DanaPensiun;
import javasign.com.dompetsehat.models.ProductPlan;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.models.json.agent;
import javasign.com.dompetsehat.models.json.cashflow;
import javasign.com.dompetsehat.models.json.dana_darurat;
import javasign.com.dompetsehat.models.json.dana_kuliah;
import javasign.com.dompetsehat.models.json.dana_pensiun;
import javasign.com.dompetsehat.models.json.date;
import javasign.com.dompetsehat.models.json.permit;
import javasign.com.dompetsehat.models.json.plan;
import javasign.com.dompetsehat.models.json.plan_product;
import javasign.com.dompetsehat.models.json.user_defined;
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
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.Validate;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;
import javax.inject.Singleton;

import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.alarm;
import javasign.com.dompetsehat.models.json.budget;
import javasign.com.dompetsehat.models.json.category;
import javasign.com.dompetsehat.models.json.product;
import javasign.com.dompetsehat.models.json.user;
import javasign.com.dompetsehat.models.json.vendor;
import javasign.com.dompetsehat.models.response.CashFlowResponse;
import javasign.com.dompetsehat.models.response.CreateAccountResponse;
import javasign.com.dompetsehat.models.response.SignInResponse;
import javasign.com.dompetsehat.services.DompetSehatService;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by aves on 8/8/16.
 */

@Singleton public class DataManager {

  //    private final CacheProviders cacheProviders;
  private final DompetSehatService service;
  private final SessionManager sessionManager;
  private final DbHelper db;
  private MCryptNew mCryptNew = new MCryptNew();
  private String app_id = mCryptNew.encrypt("2");
  public static String _FACEBOOK = "facebook";
  public static String _ACCOUNT_KIT = "account_kit";
  public String AUTH_WITH_FACEBOOK = mCryptNew.encrypt(_FACEBOOK);
  public String AUTH_WITH_ACCOUNTKIT = mCryptNew.encrypt(_ACCOUNT_KIT);
  public String AUTH_WITH_APP = mCryptNew.encrypt("app");
  public String EMPTY_DATA = mCryptNew.encrypt("");
  public String AVATAR_DATA = mCryptNew.encrypt(
      "http://dompetsehat.com/blog/wp-content/uploads/2015/06/cropped-logo-blog.png");
  private Gson gson;
  public static String SYNC_ACCOUNT = "account";
  public static String SYNC_PLAN = "plan";
  public static String SYNC_BUDGET = "budget";
  public static String SYNC_ALARM = "alarm";

  @Inject public DataManager(@ApplicationContext Context context,DompetSehatService service, DbHelper db,
      Gson gson) {
    this.service = service;
    this.sessionManager = new SessionManager(context);
    this.db = db;
    this.gson = gson;
  }

  public void createSessionUser(String access_token, String user_id, String username,
      String fb_id,boolean isVerify) {
    Timber.d("accesstoken" + access_token);
    sessionManager.createLoginSession(access_token, user_id, username, fb_id,isVerify);
  }

  public void saveAccount(Integer user_id, List<account> accounts) {
    // menyimpan account
    Timber.d("menyimpan account ");
    for (final account account : accounts) {
      Boolean cek = db.updateAccount(user_id, account);
      if (!cek) continue;
      Timber.e("account vendor "+account.vendor.id);
      if (account.vendor.id == 10) {
        sessionManager.setHaveInstitutionAccount();
      }

      for (final product product : account.products) {
        Boolean cek_product = db.updateProduct(user_id, account, product);
        if(product.cashflow != null){
          saveCashflow(user_id,product.cashflow);
        }
        if (!cek_product) continue;
      }
    }
  }

  public void saveCashflow(Integer user_id, List<cashflow> cashes) {
    HashMap<Integer,Integer> map = new HashMap<>();
    for (cashflow cash : cashes) {
      //map.put(cash.product_id,cash.product_id);
      if(cash.deleted_at != null){
        if(cash.id_local > 0) {
          db.deleteCashByID(cash.id_local);
        }else{
          db.deleteCashByIDCashflow(cash.id);
        }
      }
      Cash cashflowObject = db.updateCashflow(cash, user_id);
      Timber.e("cashflow " + cashflowObject);
    }
    //for (Map.Entry<Integer,Integer> product_id:map.entrySet()){
    //  double total_saldo = db.getSaldoReal(product_id.getKey(),Integer.valueOf(sessionManager.getIdUser()));
    //  db.updateSaldo(product_id.getKey(),total_saldo);
    //}
  }

  public void saveCategory(List<category> categories) {
    // menyimpan category
    Timber.d("menyimpan category");
    for (int i = 0; i < categories.size(); i++) {
      Category category = new Category(categories.get(i));
      db.UpdateCategory(category);
    }
  }

  public void saveUserCategory(List<user_defined> user_defineds) {
    Timber.d("menyimpan user_category");
    for (int i = 0; i < user_defineds.size(); i++) {
      UserCategory userCategory = new UserCategory(user_defineds.get(i));
      db.UpdateUserCategory(userCategory);
    }
  }

  public void saveBudget(List<budget> budgets) {
    // menyimpan budget
    Timber.d("menyimpan budget");
    for (final budget budget : budgets) {
      if (budget.deleted_at != null) {
        if(budget.id_local != 0) {
          db.deleteBudgetByIDBudget(budget.id);
        }else{
          db.deleteBudgetByID(budget.id_local);
        }
        continue;
      }
      Budget _budget = new Budget(budget);
      db.UpdateBudget(_budget);
    }
  }

  public void saveAlarm(List<alarm> alarms) {
    // menyimpan alarm
    Timber.d("menyimpan alarm");
    for (final alarm alarm : alarms) {
      if (alarm.deleted_at != null) {
        db.deleteAlarmByIDAlarm(alarm.id);
        continue;
      }
      Alarm _alarm = new Alarm(alarm);
      db.updateAlarm(_alarm);
    }
  }

  public void saveUser(String access_token, user user) {
    // menyimpan user
    Timber.d("menyimpan user");
    try {
      Timber.e("last sync " + user.last_sync);
      User _user = new User(user);
      _user.setAccess_token(access_token);
      db.UpdateUser(_user);
    } catch (Exception e) {
      Timber.e("ERROR user " + e);
    }
  }

  public void savePlan(List<plan> plans) {
    // menyimpan plan
    Timber.e("menyimpan plan "+plans.size());
    try {
      for (final plan plan_ : plans) {
        Timber.e("avesina deleted "+plan_.deleted_at);
        if (plan_.deleted_at != null) {
          if(plan_.local_id == 0) {
            db.deletePlanByIDPlan(plan_.id, plan_.type);
          }else{
            db.deletePlanByIDLocal(plan_.local_id,plan_.type);
          }
          continue;
        }
        Plan _plan = new Plan(plan_);
        db.updatePlan(_plan);
        if (plan_.dana_pensiun != null) {
          saveDanaPensiun(plan_.dana_pensiun);
        } else if (plan_.dana_darurat != null) {
          saveDanaDarurat(plan_.dana_darurat);
        } else if (plan_.dana_kuliah != null) {
          saveDanaKuliah(plan_.dana_kuliah);
        }
      }
    } catch (Exception e) {
      Timber.e("ERROR savePlan " + e);
    }
  }

  public void saveDanaPensiun(dana_pensiun dana) {
    DanaPensiun danaPensiun = new DanaPensiun(dana);
    try {
      db.updateDanaPensiun(danaPensiun);
    } catch (Exception e) {
      Timber.e("ERROR saveDanaPensiun " + e);
    }
  }

  public void saveDanaKuliah(dana_kuliah dana) {
    DanaKuliah danaKuliah = new DanaKuliah(dana);
    try {
      db.updateDanaKuliah(danaKuliah);
    } catch (Exception e) {
      Timber.e("ERROR saveDanaPensiun " + e);
    }
  }

  public void saveDanaDarurat(dana_darurat dana) {
    DanaDarurat danaDarurat = new DanaDarurat(dana);
    try {
      db.updateDanaDarurat(danaDarurat);
    } catch (Exception e) {
      Timber.e("ERROR saveDanaPensiun " + e);
    }
  }

  public void saveVendor(List<vendor> vendors) {
    // menyimpan vendor
    Timber.d("menyimpan vendor");
    for (final vendor vendor : vendors) {
      Vendor _vendor = new Vendor(vendor);
      db.updateBank(_vendor);
    }
  }

  public Observable<SignInResponse> Login(String user, String password) {
    String username = null;
    String email = null;
    if (Validate.isValidEmail(user)) {
      email = mCryptNew.encrypt(user);
    } else {
      username = mCryptNew.encrypt(user);
    }
    password = mCryptNew.encrypt(password);
    return service.login(app_id, AUTH_WITH_APP, email, username, password);
  }

  public Observable<SignInResponse> loginFacebook(String email, String access_token) {
    String _email = mCryptNew.encrypt(email);
    access_token = mCryptNew.encrypt(access_token);
    Timber.d("app_id:" + this.app_id);
    Timber.d("auth_with:" + this.AUTH_WITH_FACEBOOK);
    Timber.d("email:" + _email);
    Timber.d("access_token:" + access_token);
    return service.authFacebook(this.app_id, this.AUTH_WITH_FACEBOOK, _email, access_token, null,
        null, null, null);
  }

  public Observable<SignInResponse> updateAvatar(String avatar_name, Bitmap bitmap) {
    String avatar = Helper.BitmapToBase64String(bitmap);
    if (avatar != null) {
      User user = db.getUser(sessionManager.getIdUser(), db.TAB_USER);
      db.updateUser(user, sessionManager.getIdUser());
      return service.update_profile(avatar_name, avatar, null, null, null,null,null);
    } else {
      //MediaType type = MediaType.parse("text/plain");
      //ResponseBody body = ResponseBody.create(type, "{status ='ERROR'}");
      SignInResponse signInResponse = gson.fromJson("{status ='ERROR'}",SignInResponse.class);
      return Observable.just(signInResponse);
    }
  }

  public Observable<SignInResponse> updateProfile(String phone, String email, String date,Double income,Integer kids) {
    return service.update_profile(null, null, date, email, phone,income,kids);
  }

  public Observable<SignInResponse> registerFacebook(String email, String access_token,
      String birthday, String gender, String username, String password) {
    Timber.e("birth awal " + birthday);
    email = mCryptNew.encrypt(email);
    birthday = mCryptNew.encrypt(birthday);
    gender = mCryptNew.encrypt(gender);
    username = mCryptNew.encrypt(username);
    password = mCryptNew.encrypt(password);
    access_token = mCryptNew.encrypt(access_token);
    Timber.d("accesstoken:" + access_token);
    Timber.d("app_id:" + this.app_id);
    Timber.d("auth_with:" + this.AUTH_WITH_FACEBOOK);
    Timber.d("email:" + email);
    Timber.d("birhtday:" + birthday);
    Timber.d("gender:" + gender);
    Timber.d("password:" + password);
    Timber.d("username:" + username);
    return service.authFacebook(this.app_id, this.AUTH_WITH_FACEBOOK, email, access_token, birthday,
        gender, username, password);
  }

  public Observable<SignInResponse> loginAccountKit(String access_token, String phone,
      String email) {
    access_token = mCryptNew.encrypt(access_token);
    if (!TextUtils.isEmpty(phone)) {
      phone = mCryptNew.encrypt(phone);
    } else {
      phone = EMPTY_DATA;
    }

    if (!TextUtils.isEmpty(email)) {
      email = mCryptNew.encrypt(email);
    } else {
      email = EMPTY_DATA;
    }
    Timber.e("phone: " + phone);
    Timber.e("email: " + email);
    return service.authAccountKit(app_id, this.AUTH_WITH_ACCOUNTKIT, "", access_token, phone, email,
        null, null, null, null, null);
  }

  public Observable<SignInResponse> registerAccountKit(String access_token, String email,
      String phone, String username, String password, String birthday, String gender) {
    access_token = mCryptNew.encrypt(access_token);
    if (email != null) email = mCryptNew.encrypt(email);
    if (phone != null) phone = mCryptNew.encrypt(phone);
    username = mCryptNew.encrypt(username);
    password = mCryptNew.encrypt(password);
    birthday = mCryptNew.encrypt(birthday);
    gender = mCryptNew.encrypt(gender);
    Timber.d("Account Kit accesstoken: %s", access_token);
    Timber.d("Account Kit app_id: %s", this.app_id);
    Timber.d("Account Kit auth_with: %s", this.AUTH_WITH_ACCOUNTKIT);
    Timber.d("Account Kit email: %s", email);
    Timber.d("Account Kit birhtday: %s", birthday);
    Timber.d("Account Kit gender: %s", gender);
    Timber.d("Account Kit password: %s", password);
    Timber.d("Account Kit username: %s", username);
    Timber.d("Account Kit phone: %s", phone);
    Timber.d("Account Kit avatar: %s", AVATAR_DATA);
    return service.authAccountKit(app_id, this.AUTH_WITH_ACCOUNTKIT, EMPTY_DATA, access_token,
        phone, email, username, password, birthday, gender, null);
  }

  public Observable<SignInResponse> registerApp(String email,String username, String password, String birthday, String gender){
    if (email != null) email = mCryptNew.encrypt(email);
    username = mCryptNew.encrypt(username);
    password = mCryptNew.encrypt(password);
    birthday = mCryptNew.encrypt(birthday);
    gender = mCryptNew.encrypt(gender);
    return service.register(app_id,this.AUTH_WITH_APP,email,username,password,birthday,gender);
  }

  public Observable<CashFlowResponse> getCashFlow() {

    return service.cashflow();
  }

  public Observable<CashFlowResponse> getCashFlow(long last) {
    return service.cashflow(last);
  }

  public Observable<CreateAccountResponse> createDompet(int vendor_id, String nickname,
      int balance) {
    nickname = mCryptNew.encrypt(nickname);
    return service.account_create(vendor_id, nickname, balance, "", "");
  }

  public Observable<CreateAccountResponse> createAccount(int vendor_id, String username,
      String password) {
    username = mCryptNew.encrypt(username);
    password = mCryptNew.encrypt(password);
    Timber.e("username "+username);
    return service.account_create(vendor_id, "", 0, username, password);
  }

  public Observable<ParentResponse> sendFeedBack(String subject, String message) {
    return service.send_feedback(2, subject, "medium", message);
  }

  public ArrayList<plan> getOfflinePlan() {
    ArrayList<plan> planArrayList = new ArrayList<>();
    ArrayList<Plan> plans = db.getAllPlanOffline(sessionManager.getIdUser());
    for (Plan item : plans) {
      plan planModel = new plan();
      planModel.id = item.getId_plan();
      planModel.local_id = item.getId();
      planModel.amount_cash = item.getPlan_amount_cash();
      planModel.amount_monthly = item.getPlan_amount_monthly();
      planModel.amount_yearly = item.getPlan_amount_yearly();
      planModel.date_start = item.getPlan_date_star();
      planModel.lifetimes = item.getLifetime();
      planModel.account_id = item.getAccount_id();
      planModel.product_id = item.getProduct_id();
      planModel.risk = item.getPlan_risk();
      planModel.title = item.getPlan_title();
      planModel.type = item.getType();
      planModel.total = item.getPlan_total();
      planModel.user_id = item.getUser_id();
      planModel.created_at = item.getCreated_at();
      planModel.updated_at = item.getUpdated_at();
      planModel.deleted_at = item.getDeleted_at();
      planModel.plan_products = getPlanProduct(item.getId(), true);
      if (item.getType() == db.PLAN_TYPE_PENSIUN) {
        planModel.dana_pensiun = getDanaPensiunBy(item.getId(), true);
      } else if (item.getType() == db.PLAN_TYPE_DARURAT) {
        planModel.dana_darurat = getDanaDaruratBy(item.getId(), true);
      } else if (item.getType() == db.PLAN_TYPE_KULIAH) {
        planModel.dana_kuliah = getDanaKuliahBy(item.getId(), true);
      }
      planArrayList.add(planModel);
    }
    return planArrayList;
  }

  private ArrayList<budget> getOfflineBudget(String s_date) {
    ArrayList<Budget> budgets = db.getAllBudgetOffline(sessionManager.getIdUser(), s_date);
    ArrayList<budget> budgetArrayList = new ArrayList<>();
    for (Budget budget : budgets) {
      budget b = new budget();
      b.id = budget.getId_budget();
      b.id_local = budget.getId();
      b.user_id = budget.getUser_id();
      b.amount = budget.getAmount_budget();
      b.category_id = budget.getCategory_budget();
      b.user_category_id = budget.getUser_category_budget();
      b.date_start = budget.getDate_start();
      b.date_end = budget.getDate_end();
      b.is_repeat = budget.getRepeat();
      b.every = budget.getEvery();
      b.deleted_at = budget.getDeleted_at();
      b.updated_at = budget.getUpdated_at();
      b.created_at = budget.getCreated_at();
      budgetArrayList.add(b);
    }
    return budgetArrayList;
  }

  private ArrayList<alarm> getOfflineAlarm(String s_date){
    ArrayList<Alarm> alarms = db.getAllAlarmOffline(Integer.valueOf(sessionManager.getIdUser()),s_date);
    ArrayList<alarm> alarmArrayList = new ArrayList<>();
    for(Alarm alarm:alarms){
      alarm a = new alarm();
      a.id = alarm.getId_alarm();
      a.id_local = alarm.getId();
      a.description = alarm.getDeskripsi_alarm();
      a.amount = alarm.getJumlah_alarm();
      a.date = alarm.getDate_alarm();
      a.user_id = alarm.getId_user();
      a.category_id = alarm.getId_category();
      a.user_category_id = alarm.getUser_id_category();
      a.is_active = alarm.getIs_active();
      a.created_at = alarm.getCreated_at();
      a.updated_at = alarm.getUpdated_at();
      a.deleted_at = alarm.getDeleted_at();
      alarmArrayList.add(a);
    }
    return alarmArrayList;
  }

  private ArrayList<plan_product> getPlanProduct(int plan, boolean isLocal_ID) {
    Timber.e("plan local " + plan);
    ArrayList<plan_product> plan_products = new ArrayList<>();
    ArrayList<ProductPlan> list = new ArrayList<>();
    if (isLocal_ID) {
      Timber.e("here product plan");
      list = db.getProductPlanByPlanLocalID(plan);
    } else {
      list = db.getProductPlanByPlan(plan);
    }
    Timber.e("jumlah list " + list.size());
    for (ProductPlan item : list) {
      Timber.e(" id " + item.getId() + "");
      plan_product pp = new plan_product();
      pp.id = item.getId_product_plan();
      pp.local_id = item.getId();
      pp.plan_id_local = item.getPlan_id_local();
      pp.plan_id = item.getPlan_id();
      pp.invest_id = item.getInvest_id();
      pp.product_id = item.getProduct_id();
      pp.created_at = item.getCreated_at();
      pp.updated_at = item.getUpdated_at();
      pp.deleted_at = item.getDeleted_at();
      plan_products.add(pp);
    }
    return plan_products;
  }

  public dana_pensiun getDanaPensiunBy(int plan, boolean isLocal_ID) {
    DanaPensiun danaPensiun;
    if (isLocal_ID) {
      danaPensiun = db.getDanaPensiunByPlanIDLocal(plan);
    } else {
      danaPensiun = db.getDanaPensiunByPlan(plan);
    }
    dana_pensiun dp = new dana_pensiun();

    dp.id = danaPensiun.getId_plan();
    dp.local_id = danaPensiun.getId();
    dp.plan_id_local = danaPensiun.getId_plan_local();
    dp.pendapatan = danaPensiun.getPendapatan();
    dp.umur_pensiun = danaPensiun.getUmur_pensiun();
    dp.umur = danaPensiun.getUmur();
    dp.created_at = danaPensiun.getCreated_at();
    dp.updated_at = danaPensiun.getUpdated_at();
    dp.deleted_at = danaPensiun.getDeleted_at();
    return dp;
  }

  public dana_darurat getDanaDaruratBy(int plan, boolean isLocal_ID) {
    DanaDarurat danaDarurat;
    if (isLocal_ID) {
      danaDarurat = db.getDanaDaruratByPlanIDLocal(plan);
    } else {
      danaDarurat = db.getDanaDaruratByPlan(plan);
    }
    dana_darurat dana = new dana_darurat();
    dana.id = danaDarurat.getId_dana_darurat();
    dana.local_id = danaDarurat.getId();
    dana.plan_id_local = danaDarurat.getId_plan_local();
    dana.plan_id = danaDarurat.getId_plan();
    dana.pengeluaran_bulanan = danaDarurat.getPengeluaran_bulanan();
    dana.bulan_penggunaan = danaDarurat.getBulan_penggunaan();
    dana.created_at = danaDarurat.getCreated_at();
    dana.updated_at = danaDarurat.getUpdated_at();
    dana.deleted_at = danaDarurat.getDeleted_at();

    return dana;
  }

  public dana_kuliah getDanaKuliahBy(int plan, boolean isLocal_ID) {
    DanaKuliah DANA;
    if (isLocal_ID) {
      DANA = db.getDanaKuliahByPlanIDLocal(plan);
    } else {
      DANA = db.getDanaKuliahByPlan(plan);
    }
    dana_kuliah dana = new dana_kuliah();

    dana.id = DANA.getId_dana_kuliah();
    dana.local_id = DANA.getId();
    dana.plan_id_local = DANA.getId_plan_local();
    dana.plan_id = DANA.getId_plan();
    dana.lama_kuliah = DANA.getLama_kuliah();
    dana.nama_anak = DANA.getNama_anak();
    dana.usia_anak = DANA.getUsia_anak();
    dana.biaya_kuliah = DANA.getBiaya_kuliah();
    dana.uang_saku = DANA.getUang_saku();
    return dana;
  }

  public String sync_plan() {
    ArrayList<plan> planArrayList = getOfflinePlan();
    Gson g = new Gson();
    String json = g.toJson(planArrayList);
    Timber.e("JSON PLAN " + json);
    return json;
  }

  public Observable<SyncResponse> do_sync_account(Integer account_id){
    String json = sync_account(account_id);
    return service.sync(json,null,null,null,0,"true");
  }

  public Observable<SyncAccountResponse> account_sync(Integer id_account){
    return service.account_sync(id_account);
  }

  public Observable<CashflowAccountResponse> lastconnect_cashflow_account(Integer id_account,long date){
    return service.lastconnect_cashflow_account(id_account,date);
  }

  public String sync_account(Integer account_id) {
    String key = "account_id";
    Long long_last_sync = sessionManager.getLastSync();
    String s_date = Helper.converterEpochToDate(long_last_sync);
    ArrayList<HashMap<String, Object>> json_array = new ArrayList<>();
    ArrayList<Account> accounts = new ArrayList<>();
    if(account_id == null) {
      accounts = db.getAllAccountByUser(sessionManager.getIdUser());
      //accounts = db.getAllDompetAccount(sessionManager.getIdUser());
    }else {
      Account account = db.getAccountById(account_id,2,Integer.valueOf(sessionManager.getIdUser()));
      if(account != null){
        accounts.add(account);
      }
    }
    for (Account account : accounts) {
      if (account.getIdvendor() != AccountView.MNL && account.getIdvendor() != AccountView.DP) {
        //if (sessionManager.canSyncBank() || account_id != null) {
          HashMap<String, Object> json_object = new HashMap<>();
          json_object.put(key, account.getIdaccount());
          List<Cash> cashes = db.getAllCashOfflineByAccount(sessionManager.getIdUser(), account.getIdaccount(), s_date);
          List<cashflow> cs = getCashflow(cashes);
          json_object.put("cashflows", cs);
          json_array.add(json_object);
        //}
      } else if (account.getIdvendor() == AccountView.DP) {
        HashMap<String, Object> json_object = new HashMap<>();
        json_object.put(key, account.getIdaccount());
        List<Cash> cashes = db.getAllCashOfflineByAccount(sessionManager.getIdUser(), account.getIdaccount(), s_date);
        List<cashflow> cs = getCashflow(cashes);
        json_object.put("cashflows", cs);
        json_array.add(json_object);
      } else if(account.getIdvendor() == AccountView.MNL){
        if(MyCustomApplication.showInvestasi()){
          HashMap<String, Object> json_object = new HashMap<>();
          json_object.put(key, account.getIdaccount());
          json_array.add(json_object);
        }
      }else {
        HashMap<String, Object> json_object = new HashMap<>();
        json_object.put(key, account.getIdaccount());
        json_array.add(json_object);
      }
    }
    Timber.e("JSON ACCOUNT "+json_array);
    String json_string = new Gson().toJson(json_array);
    Timber.e("JSON ACCOUNT "+json_string);
    return json_string;
  }

  private ArrayList<cashflow> getCashflow(List<Cash> Cashs){
    ArrayList<cashflow> cashflows = new ArrayList<>();
    for (Cash cash:Cashs){
      cashflow c = new cashflow();
      c.id_local = cash.getId();
      c.id = cash.getCash_id();
      c.product_id = cash.getProduct_id();
      c.description = cash.getDescription();
      c.category_id = cash.getCategory_id();
      c.user_category_id = cash.getUser_category_id();
      c.amount = cash.getAmount();
      c.note = cash.getNote();
      c.user_tag = cash.getCash_tag();
      date d = new date();
      d.original = cash.getCash_date();
      Calendar calendar = Calendar.getInstance();
      Date dt = Helper.setInputFormatter("yyyy-MM-dd",cash.getCash_date());
      calendar.setTime(dt);
      d.date = calendar.get(Calendar.DATE);
      d.month = calendar.get(Calendar.MONTH)+1;
      d.year = calendar.get(Calendar.YEAR);
      d.day = Helper.setSimpleDateFormat(dt,"EEEE");
      c.date = d;
      c.user_description = cash.getCashflow_rename();
      c.type = cash.getType();
      c.status = cash.getStatus();
      c.created_at = cash.getCreated_at();
      c.updated_at = cash.getUpdated_at();
      c.deleted_at = cash.getDeleted_at();
      cashflows.add(c);
    }
    return cashflows;
  }

  public String sync_budget() {
    Long long_last_sync = sessionManager.getLastSync();
    String s_date = Helper.converterEpochToDate(long_last_sync);
    List<budget> budgets = getOfflineBudget(s_date);
    String json = gson.toJson(budgets);
    Timber.e("JSON BUDGET " + json);
    Timber.i("SYNC TYPE " + SYNC_BUDGET);
    Timber.i("last " + SYNC_BUDGET);
    return json;
  }

  public String sync_alarm(){
    Long long_last_sync = sessionManager.getLastSync();
    String s_date = Helper.converterEpochToDate(long_last_sync);
    Timber.e("s_date "+s_date);
    List<alarm> alarms = getOfflineAlarm(s_date);
    String json = gson.toJson(alarms);
    Timber.e("JSON ALARM " + json);
    Timber.i("SYNC TYPE " + SYNC_ALARM);
    Timber.i("last " + s_date);
    return json;
  }

  public Observable<SyncResponse> syncAll(long last_sync){
    String json_account = sync_account(null);
    String json_plan = sync_plan();
    String json_budget = sync_budget();
    String json_alarm = sync_alarm();
    return service.sync(json_account,json_plan,json_budget,json_alarm,sessionManager.getLastSync(),"false");
  }

  public Observable<MamiDataResponse> validateForm(String email, String identity_number,
      String phone) {
    if (!email.equals("")) {
      email = mCryptNew.encrypt(email);
    } else if (!identity_number.equals("")) {
      identity_number = mCryptNew.encrypt(identity_number);
    } else if (!phone.equals("")) {
      phone = mCryptNew.encrypt(phone);
    }
    Timber.e("phone " + phone + " email " + email + " identity " + identity_number);
    return service.validate_customer(email, identity_number, phone);
  }

  public Observable<MamiDataResponse> validateReferral(String referral) {
    referral = mCryptNew.encrypt(referral);
    return service.validate_referral_code(referral);
  }

  public Observable<RegisterMamiResponse> registerMami(String identity_number, String email,
      String country, String phone, String password, String referred_by, String referral_code) {
    identity_number = mCryptNew.encrypt(identity_number);
    if (!TextUtils.isEmpty(email)) email = mCryptNew.encrypt(email);
    if (!TextUtils.isEmpty(phone)) phone = mCryptNew.encrypt(phone);
    if (!TextUtils.isEmpty(country)) country = mCryptNew.encrypt(country);
    if (!TextUtils.isEmpty(password)) password = mCryptNew.encrypt(password);
    String confirm_password = password;
    if (!TextUtils.isEmpty(referred_by)) {
      referred_by = mCryptNew.encrypt(referred_by);
    }else{
      referred_by = mCryptNew.encrypt("");
    }
    referral_code = mCryptNew.encrypt(referral_code);
    return service.register_manulife(identity_number, email, country, phone, password,
        confirm_password, referred_by, referral_code);
  }

  public Observable<MamiDataResponse> loginMami(String email, String password,
      String referral_code) {
    if (!TextUtils.isEmpty(email)) email = mCryptNew.encrypt(email);
    if (!TextUtils.isEmpty(password)) password = mCryptNew.encrypt(password);
    if (!TextUtils.isEmpty(referral_code)) referral_code = mCryptNew.encrypt(referral_code);
    return service.login_mami(email, password, referral_code);
  }

  public Observable<ResponseBody> getComission(Date dateStart, Date dateEnd) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String start = df.format(dateStart);
    String end = df.format(dateEnd);

    return service.referral_fee(start, end);
  }

  public Observable<PortofolioResponse> getPortofolio() {
    return service.portofolio();
  }

  public Observable<OtherResponse> changePassword(String newPassword, String oldPassword) {
    oldPassword = mCryptNew.encrypt(oldPassword);
    newPassword = mCryptNew.encrypt(newPassword);
    Timber.e("old:" + oldPassword);
    Timber.e("new:" + newPassword);
    return service.change_password(oldPassword, newPassword);
  }

  public Observable<ParentResponse> deleteAccount(int id_account) {
    return service.delete_account(id_account);
  }

  public Observable<SubCategoryResponse> createCategory(int id_category_parent, String color,
      String color_name, String name) {
    return service.create_category(id_category_parent, color, color_name, name);
  }

  public Observable<ResponseBody> register_fcm(String token) {
    return service.fcm(token);
  }

  public Observable<ParentResponse> forgot(String email, String password) {
    email = mCryptNew.encrypt(email);
    password = mCryptNew.encrypt(password);
    return service.forgot_password(email, password, password);
  }

  public Observable<CreateAccountResponse> updateAccount(int account_id, String username,
      String password) {
    username = mCryptNew.encrypt(username);
    password = mCryptNew.encrypt(password);
    return service.update_account(account_id, username, password);
  }

  public Observable<CreateAccountResponse> updateDompet(int account_id, String nickname,
      int balance) {
    nickname = mCryptNew.encrypt(nickname);
    return service.update_dompet(account_id, nickname, balance);
  }

  public void saveSyncData(SyncResponse syncResponse) {
    if (Helper.checkList(syncResponse.response.accounts)) {
      saveAccount(Integer.valueOf(sessionManager.getIdUser()),
          syncResponse.response.accounts);
    }
    if (Helper.checkList(syncResponse.response.cashflows)) {
      saveCashflow(Integer.valueOf(sessionManager.getIdUser()),
          syncResponse.response.cashflows);
      sessionManager.setLastSync(sessionManager.LAST_SYNC_ACCOUNT);
    }
    if (Helper.checkList(syncResponse.response.alarm)) {
      saveAlarm(syncResponse.response.alarm);
      sessionManager.setLastSync(sessionManager.LAST_SYNC_BUDGET);
    }
    if (Helper.checkList(syncResponse.response.budget)) {
      saveBudget(syncResponse.response.budget);
      sessionManager.setLastSync(sessionManager.LAST_SYNC_BUDGET);
    }
    if (Helper.checkList(syncResponse.response.plan)) {
      savePlan(syncResponse.response.plan);
      sessionManager.setLastSync(sessionManager.LAST_SYNC_PLAN);
    }
    sessionManager.setLastSync(sessionManager.LAST_SYNC);
  }

  public Observable<AgentListResponse> getRequestList(String status){
    return service.get_list_request(status);
  }

  public Observable<ParentResponse> add_agent(String username,permit permission){
    String json = gson.toJson(permission);
    Timber.e("json permission "+json);
    return service.add_agent("DSGO",username,"pending",json);
  }

  public Observable<CashflowAccountResponse> getCashFlowAccount(int account_id) {
    return service.cashflow_account(account_id);
  }

  public Observable<CashflowAccountResponse> getCashFlowAccount(int account_id,long date) {
    return service.cashflow_account(account_id,date);
  }

  public Observable<RenameCategoryResponse> renameCategory(int category_id,String name,String desc){
    return service.rename_category(category_id,name,desc);
  }

  public Observable<DebtsResponse> getDebts(String type){
    switch (type){
      case "lend":
        return service.get_debts_lend();
      case "borrow":
        return service.get_debts_borrow();
      default:
        return service.get_debts();
    }
  }

  public Observable<DebtsResponse> createDebt(String type,int _switch,String name,int amount,Integer cashflow_id,Integer product_id,String date,String payback,String email) {
    return service.create_debt(type.toUpperCase(),_switch,name,amount,cashflow_id,product_id,date,payback,email,1);
  }

  public Observable<ParentResponse> deleteDebt(int id){
    return service.delete_debt(id);
  }

  public Observable<ParentResponse> resetDataUser() {
    return service.reset_data();
  }

  public Observable<ReferralFeeResponse> saveReferralFee(int idaccount) {
    return service.get_referral_fee(idaccount);
  }

  public Observable<CreateAccountResponse> renameAccount(int account_id,String nickname) {
    nickname = mCryptNew.encrypt(nickname);
    return service.rename_account(account_id,nickname);
  }

  public List<Account> getValidAccount() {
    ArrayList<Account> accounts = db.getAllAccountByUser(sessionManager.getIdUser());
    return accounts;
  }

  public Observable<SyncAccountResponse> getAccountByMontYear(int account_id, int selectedMonth, int selectedYear) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH,selectedMonth);
    cal.set(Calendar.YEAR,selectedYear);
    cal.set(Calendar.DAY_OF_MONTH,1);
    String start_date = Helper.setSimpleDateFormat(cal.getTime(),"yyyy-MM-dd");
    cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    String end_date = Helper.setSimpleDateFormat(cal.getTime(),"yyyy-MM-dd");
    return service.get_manual_bank(account_id,start_date,end_date);
  }

  public Observable<ParentResponse> setPermissions(int id_agent,HashMap<String,Boolean>permissions){
    String data = new Gson().toJson(permissions);
    Timber.e("avesina "+data);
    return service.set_permisions(id_agent,"approved",data);
  }

  public Observable<ParentResponse> resentEmail(String email) {
    return service.resend_email(email);
  }
}
