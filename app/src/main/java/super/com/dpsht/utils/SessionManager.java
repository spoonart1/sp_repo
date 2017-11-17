package javasign.com.dompetsehat.utils;

/**
 * Created by aulia on 29/06/2015.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.smooch.core.User;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.ui.activities.institusi.InstitusiActivity;
import javasign.com.dompetsehat.ui.activities.landing.LandingPages;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class SessionManager {
  private static int PRIVATE_MODE = 0;

  private String mUserId = null;

  private SharedPreferences pref;
  private SharedPreferences preff_tourguide;
  private SharedPreferences preff_passcode;
  private SharedPreferences preff_language;

  private Context _context;

  private Editor editor;
  private Editor editor_tourguide;
  private Editor editor_passcode;
  private Editor editor_language;

  public static final int USER_PICK_NONE = -1;
  public static final int USER_PICK_MY_FIN = 0;
  public static final int USER_PICK_MY_REFERRAL = 1;
  private static final String USER_KEY_MODE = "app_mode";
  private static final String USER_KEY_HAVE_INSTITUSI = "institusi";
  private static final int ALREADY_HAVE_INSTITUTION = 1;
  private static final String DS_LAUNCHED_KEY = "ds_launched_key";

  private int currentUserPickAppMode = USER_PICK_NONE;

  private static final String PREF_PASCODE = "DSPasscode";
  private static final String PREF_TOURGUIDE = "tourguide";
  private static final String KEYWORD_PASSCODE = "passcode";
  private static final String PREF_LANGUAGE = "DSBahasa";
  private static final String KEYWORD_LANGUAGE = "bahasa";

  private final String PREF_NAME = "DompetSehatPrefs";
  private final String TOKEN = "token";
  private final String ID_USER = "id_user";
  private final String USER_NAME = "name";
  private final String FB_ID = "fb_id";
  private final String VERIFIED_EMAIL = "verified_email";
  private final String enableActivitiesReminder = "enableActivitiesReminder";
  private final String hoursActivitiesReminder = "hoursActivitiesReminder";
  private final String minutesActivitiesReminder = "minutesActivitiesReminder";
  private final String hoursSync = "hoursSync";
  private final String minutesSync = "minutesSync";
  private final String mami_token = "mami_token";
  private final String comission_key = "comission_key";
  public static final String LAST_SYNC_PLAN = "last_sync_plan";
  public static final String LAST_SYNC_ACCOUNT = "last_sync_account";
  public static final String LAST_SYNC_BUDGET = "last_sync_budget";
  public static final String LAST_SYNC = "last_sync";
  public static final String FIREBASE_REGISTER_ID = "firebase";
  public static final String NETWORK_STATE = "network_state";
  public static final String INITIAL_DAY_OF_MONTH = "InitialDayOfMonth";

  private MCryptNew mCryptNew = new MCryptNew();

  public static final String SMOOCH_APP_TOKEN = "4bqlwdw177107df1ewv16ypqb";

  private static SessionManager sessionManager;

  // Constructor
  @Inject public SessionManager(@ApplicationContext Context context) {
    this._context = context;
    pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public static SessionManager getIt(Context context){
    if(sessionManager == null)
      sessionManager = new SessionManager(context);
    return sessionManager;
  }

  public SessionManager(Context context, boolean passcodeOnly) {
    //(ojo dibusak)
    this._context = context;
    passcodeOnly = false; // ra kanggo sakjane, ben iso nggawe 2 constructor wae :D
  }

  public void createLoginSession(String token, String id_user, String name, String fb_id,boolean verified_email) {
    editor.clear();
    editor.putString(TOKEN, token);
    editor.putString(ID_USER, id_user);
    editor.putString(USER_NAME, name);
    editor.putString(FB_ID, fb_id);
    editor.putBoolean(VERIFIED_EMAIL, verified_email);
    editor.commit();

    initSmooch();
  }

  public boolean isConnectedFB(){
    return !TextUtils.isEmpty(pref.getString(FB_ID,""));
  }

  public String getLastComission(){
    return pref.getString(comission_key,"");
  }

  public void saveLastCommision(String last_comisiion){
    editor.putString(comission_key,last_comisiion);
    editor.commit();
  }

  public String getFacebookID(){
    return pref.getString(FB_ID,"");
  }

  public boolean isVerifiedEmail(){
    return pref.getBoolean(VERIFIED_EMAIL,false);
  }

  public void verifiedEmail(){
    editor.putBoolean(VERIFIED_EMAIL,true);
    editor.commit();
  }

  public void setToken(String token) {
    editor.putString(TOKEN, token);
    editor.commit();
  }

  public String getAuthToken() {
    return pref.getString(TOKEN, null);
  }

  public String getIdUser() {
    if(mUserId == null) {
      mUserId = pref.getString(ID_USER, null);
    }
    return mUserId;
  }

  public String getUsername() {
    return pref.getString(USER_NAME, null);
  }

  public void setActivitiesReminder(boolean set){
    if(set) {
      setTimeActivitiesReminder(20, 00);
    }
    editor.putBoolean(enableActivitiesReminder,set);
    editor.commit();
  }

  public boolean getActivitiesReminder() {
    return pref.getBoolean(enableActivitiesReminder,true);
  }

  public void setTimeActivitiesReminder(Integer hours, Integer minutes) {
    editor.putInt(hoursActivitiesReminder, hours);
    editor.putInt(minutesActivitiesReminder, minutes);
    editor.commit();
  }

  public void setTimeSync(Integer hours, Integer minutes) {
    editor.putInt(hoursSync, hours);
    editor.putInt(minutesSync, minutes);
    editor.commit();
  }

  public Integer getHoursActivitiesReminder() {
    return pref.getInt(hoursActivitiesReminder, 20);
  }

  public Integer getMinutesActivitiesReminder() {
    return pref.getInt(minutesActivitiesReminder, 0);
  }

  public Integer getHoursSync() {
    return pref.getInt(hoursSync, 9);
  }

  public Integer getMinutesSync() {
    return pref.getInt(minutesSync, 0);
  }

  public void logoutUser() {
    editor.clear();
    editor.commit();
    //Smooch.logout();
    clearPassCodePreff();

    Intent i = new Intent(_context, LandingPages.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    if (_context instanceof Activity) {
      ((Activity) _context).finish();
    }
    _context.startActivity(i);
  }

  public boolean isLoggedIn() {
    String log = pref.getString(TOKEN, "");
    String id = pref.getString(ID_USER, "");

    Timber.e("ID USER" + id);

    if (log.equals("") || id.equals("")) {
      return false;
    } else {
      return true;
    }
  }

  public void initSmooch() {
    if (_context instanceof Application) {
      //Smooch.login(getIdUser(), null);
    }

    final Map<String, Object> customProperties = new HashMap<String, Object>();
    String lastname = mCryptNew.decrypt(this.getUsername());
    customProperties.put("nickname", lastname);

    User.getCurrentUser().setFirstName("DS - ");
    User.getCurrentUser().setLastName(lastname);
    User.getCurrentUser().addProperties(customProperties);

    editor.putBoolean(SMOOCH_APP_TOKEN, true);
    editor.commit();
  }

  public boolean isSmooch() {
    return pref.getBoolean(SMOOCH_APP_TOKEN, false);
  }

  //PASCODE SESSION
  public void savePassCodeToPreff(String passcode) {
    preff_passcode = _context.getSharedPreferences(PREF_PASCODE, PRIVATE_MODE);
    editor_passcode = preff_passcode.edit();
    passCodeSetup(passcode);
  }

  protected void passCodeSetup(String passcode) {
    editor_passcode.putString(KEYWORD_PASSCODE, passcode);
    editor_passcode.commit();
  }

  public String getPrefPascode() {
    preff_passcode = _context.getSharedPreferences(PREF_PASCODE, PRIVATE_MODE);
    String result = preff_passcode.getString(KEYWORD_PASSCODE, "");

    return result;
  }

  public void clearPassCodePreff() {
    preff_passcode = _context.getSharedPreferences(PREF_PASCODE, PRIVATE_MODE);
    editor_passcode = preff_passcode.edit();

    editor_passcode.clear();
    editor_passcode.commit();
  }

  //Settingan Bahasa
  public void saveLanguangePreff(String bahasa) {
    preff_language = _context.getSharedPreferences(PREF_LANGUAGE, PRIVATE_MODE);
    editor_language = preff_language.edit();

    String bhs = bahasa.substring(0, 2);
    languageSetup(bhs);
  }

  protected void languageSetup(String bahasa) {
    editor_language.putString(KEYWORD_LANGUAGE, bahasa);
    editor_language.commit();
  }

  public String getPrefLanguage() {
    preff_language = _context.getSharedPreferences(PREF_LANGUAGE, PRIVATE_MODE);
    String result = preff_language.getString(KEYWORD_LANGUAGE, "in");
    return result;
  }

  public void setToDefaultLanguage() {
    preff_language = _context.getSharedPreferences(PREF_LANGUAGE, PRIVATE_MODE);
    editor_language = preff_language.edit();

    editor_language.clear();
    editor_language.commit();
  }

  SharedPreferences dataPreff;
  Editor editorData;
  final String DATA_PREFF = "datapreff";
  final String DATA_KEY = "keyData";

  //test data
  public void bindData(String data) {
    dataPreff = _context.getSharedPreferences(DATA_PREFF, PRIVATE_MODE);
    editorData = dataPreff.edit();

    editorData.putString(DATA_KEY, data);
    editorData.commit();
  }

  public String getData() {
    dataPreff = _context.getSharedPreferences(DATA_PREFF, PRIVATE_MODE);
    String result = dataPreff.getString(DATA_KEY, "none");
    return result;
  }

  public void clearData() {
    dataPreff = _context.getSharedPreferences(DATA_PREFF, PRIVATE_MODE);
    editorData = dataPreff.edit();

    editorData.clear();
    editorData.commit();
  }

  public int getUserPickAppMode() {
    SharedPreferences other_preff = _context.getSharedPreferences("DS_Launched", PRIVATE_MODE);
    return other_preff.getInt(USER_KEY_MODE, USER_PICK_NONE);
  }

  public void setCurrentUserPickAppMode(int sessionMode) {
    SharedPreferences other_preff = _context.getSharedPreferences("DS_Launched", PRIVATE_MODE);
    Editor editor = other_preff.edit();
    editor.putInt(USER_KEY_MODE, sessionMode);
    editor.commit();
  }

  public void setHaveInstitutionAccount() {
    editor.putInt(USER_KEY_HAVE_INSTITUSI, ALREADY_HAVE_INSTITUTION);
    editor.commit();
  }

  public void removeHaveInstitutionAccount() {
    editor.putInt(USER_KEY_HAVE_INSTITUSI, USER_PICK_NONE);
    editor.commit();
  }

  public boolean ihaveAnInstutitionAccount() {
    int status = pref.getInt(USER_KEY_HAVE_INSTITUSI, USER_PICK_NONE);
    return status != USER_PICK_NONE;
  }

  public boolean isEverOpenDialogHomeByType(int dialogType) {
    SharedPreferences other_preff = _context.getSharedPreferences("DS_Launched", PRIVATE_MODE);
    int saveddType = other_preff.getInt("launched-dialog" + dialogType, -1);
    return saveddType == dialogType;
  }

  public void logoutReferral() {
    editor.clear();
    editor.commit();

    Intent i = new Intent(_context, InstitusiActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    if (_context instanceof Activity) {
      ((Activity) _context).finish();
    }
    _context.startActivity(i);
  }

  public void clearAllAppData() {
    editor.clear();
    editor.commit();

    SharedPreferences other_preff = _context.getSharedPreferences("DS_Launched", PRIVATE_MODE);
    Editor ed = other_preff.edit();
    ed.clear();
    ed.commit();
  }

  public void setUserHasLaunchedAppOnce() {
    SharedPreferences other_preff = _context.getSharedPreferences("DS_Launched", PRIVATE_MODE);
    Editor editor = other_preff.edit();
    editor.putInt(DS_LAUNCHED_KEY, 1);
    editor.commit();
  }

  public boolean isUserHasLaunchedAppOnce() {
    SharedPreferences other_preff = _context.getSharedPreferences("DS_Launched", PRIVATE_MODE);
    int status = other_preff.getInt(DS_LAUNCHED_KEY, USER_PICK_NONE);
    return status != USER_PICK_NONE;
  }

  public void deleteUserHasLaunchedAppData() {
    if (isUserHasLaunchedAppOnce()) {
      SharedPreferences other_preff = _context.getSharedPreferences("DS_Launched", PRIVATE_MODE);
      other_preff.edit().clear();
      other_preff.edit().commit();
    }
  }

  public static String UMUR = "u";
  public static String UMUR_PENSIUN = "up";
  public static String PENGHASILAN = "p";

  public static String PENGELURAN_PERBULAN = "pp";
  public static String BULAN_PERSIAPAN = "bp";
  public static String TAHUN = "t";

  public static String NAMA_ANAK = "na";
  public static String USIA = "usia";
  public static String SEMESTER = "s";
  public static String BIAYA_KULIAH = "bk";
  public static String UANG_SAKU = "us";

  public static String NAMA = "n";
  public static String JANGKA_WAKTU = "jw";
  public static String ANGGARAN = "a";

  public void saveDanaPensiun(int umur, float penghasilan, int umur_pensiun) {
    String penghasilanString = Helper.formatFloatToStringNonDecimal(penghasilan);
    editor.putString(UMUR_PENSIUN, String.valueOf(umur_pensiun));
    editor.putString(PENGHASILAN, penghasilanString);
    editor.putString(UMUR, String.valueOf(umur));
    editor.commit();
  }

  public HashMap<String, String> getDanaPensiun() {
    HashMap<String, String> map = new HashMap<>();
    map.put(UMUR_PENSIUN, pref.getString(UMUR_PENSIUN, "0"));
    map.put(UMUR, pref.getString(UMUR, "0"));
    map.put(PENGHASILAN, pref.getString(PENGHASILAN, "0"));
    return map;
  }

  public void clearDanaPensiun() {
    editor.remove(UMUR_PENSIUN);
    editor.remove(UMUR);
    editor.remove(PENGHASILAN);
    editor.commit();
  }

  public void saveDanaDarurat(float pendapatan, int bulan_persiapan, int tahun) {
    String pendapatanString = Helper.formatFloatToStringNonDecimal(pendapatan);
    editor.putString(PENGELURAN_PERBULAN, pendapatanString);
    editor.putString(BULAN_PERSIAPAN, String.valueOf(bulan_persiapan));
    editor.putString(TAHUN, String.valueOf(tahun));
    editor.commit();
  }

  public HashMap<String, String> getDanaDarurat() {
    HashMap<String, String> map = new HashMap<>();
    map.put(PENGELURAN_PERBULAN, pref.getString(PENGELURAN_PERBULAN, "0"));
    map.put(BULAN_PERSIAPAN, pref.getString(BULAN_PERSIAPAN, "0"));
    map.put(TAHUN, pref.getString(TAHUN, "0"));
    return map;
  }

  public void setMamiToken(String mamiToken) {
    editor.putString(mami_token, mamiToken);
    editor.commit();
  }

  public String getMamiToken() {
    if (pref.contains(mami_token)) {
      return pref.getString(mami_token, null);
    } else {
      return null;
    }
  }

  public void clearDanaDarurat() {
    editor.remove(PENGELURAN_PERBULAN);
    editor.remove(BULAN_PERSIAPAN);
    editor.remove(TAHUN);
    editor.commit();
  }

  public void saveDanaKuliah(String nama_anak, int usia, int semester, double biaya_kuliah,
      double uang_saku) {

    String biayaKuliahString = Helper.formatFloatToStringNonDecimal(biaya_kuliah);
    String uangSakuString = Helper.formatFloatToStringNonDecimal(uang_saku);

    editor.putString(NAMA_ANAK, nama_anak);
    editor.putString(USIA, String.valueOf(usia));
    editor.putString(SEMESTER, String.valueOf(semester));
    editor.putString(BIAYA_KULIAH, biayaKuliahString);
    editor.putString(UANG_SAKU, uangSakuString);
    editor.commit();
  }

  public HashMap<String, String> getDanaKuliah() {
    HashMap<String, String> map = new HashMap<>();
    map.put(NAMA_ANAK, (pref.getString(NAMA_ANAK, "").equalsIgnoreCase("0")? "":pref.getString(NAMA_ANAK,"")));
    map.put(USIA, (pref.getString(USIA, "").equalsIgnoreCase("0")? "":pref.getString(USIA,"")));
    map.put(SEMESTER, (pref.getString(SEMESTER, "").equalsIgnoreCase("0")? "":pref.getString(SEMESTER,"")));
    map.put(BIAYA_KULIAH, (pref.getString(BIAYA_KULIAH, "").equalsIgnoreCase("0")? "":pref.getString(BIAYA_KULIAH,"")));
    map.put(UANG_SAKU, (pref.getString(UANG_SAKU, "").equalsIgnoreCase("0")? "":pref.getString(UANG_SAKU,"")));
    return map;
  }

  public void clearDanaKuliah() {
    editor.remove(NAMA_ANAK);
    editor.remove(USIA);
    editor.remove(SEMESTER);
    editor.remove(BIAYA_KULIAH);
    editor.remove(UANG_SAKU);
    editor.commit();
  }

  public void saveDanaCustome(String nama, int jangka_waktu, float anggaran) {
    editor.putString(NAMA, nama);
    editor.putInt(JANGKA_WAKTU, jangka_waktu);
    editor.putFloat(ANGGARAN, anggaran);
    editor.commit();
  }

  public void setDraftReferral(String referral){
    final String key_referral = "referral";
    String key = key_referral+getIdUser();
    String json = pref.getString(key,null);
    Timber.e("avesina referral "+referral);
    if(json != null){
      try {
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> map = new Gson().fromJson(json,type);
        if(!map.contains(referral)){
          map.add(referral);
          editor.putString(key,new Gson().toJson(map));
          editor.commit();
        }
        return;
      }catch (Exception e){

      }
    }
    ArrayList<String> map = new ArrayList<>();
    map.add(referral);
    editor.putString(key,new Gson().toJson(map));
    editor.commit();
  }

  public ArrayList<String> getDraftReferral(){
    final String key_referral = "referral";
    String key = key_referral+getIdUser();
    String json  = pref.getString(key,null);
    ArrayList<String> map = new ArrayList<>();
    if(json != null){
     try {
       Type type = new TypeToken<ArrayList<String>>(){}.getType();
       map = new Gson().fromJson(json,type);
     }catch (Exception e){

     }
    }
    return map;
  }

  public HashMap<String, Object> getDanaCutome() {
    HashMap<String, Object> map = new HashMap<>();
    map.put(NAMA, pref.getString(NAMA, ""));
    map.put(JANGKA_WAKTU, pref.getInt(JANGKA_WAKTU, 0));
    map.put(ANGGARAN, pref.getFloat(ANGGARAN, 0));
    return map;
  }

  public void clearDanaCustome() {
    editor.remove(NAMA);
    editor.remove(JANGKA_WAKTU);
    editor.remove(ANGGARAN);
    editor.commit();
  }

  //public void saveLogin

  public Long getLastSync(){
    return Long.valueOf((int) Math.ceil(pref.getLong(LAST_SYNC, 0) / 1000));
  }

  public Long getLastSync(String key) {
    return Long.valueOf((int) Math.ceil(pref.getLong(key, 0) / 1000));
  }

  public void setLastSync(String key) {
    Calendar calendar = Calendar.getInstance();
    editor.putLong(key, calendar.getTimeInMillis());
    editor.commit();
  }

  public boolean canSyncBank() {
    Calendar calendar = Calendar.getInstance();
    if (pref.contains("canSyncBank")) {
      Long time = pref.getLong("canSyncBank", 0);
      Date d = new Date(time);
      int year = calendar.get(Calendar.YEAR);
      int date = calendar.get(Calendar.DATE);
      int month = calendar.get(Calendar.MONTH);
      calendar.setTime(d);
      int year1 = calendar.get(Calendar.YEAR);
      int date1 = calendar.get(Calendar.DATE);
      int month1 = calendar.get(Calendar.MONTH);
      if (year1 > year || month1 > month || (month1 == month && date1 > date)) {
        return true;
      } else {
        return false;
      }
    } else {
      calendar.add(Calendar.DATE, 1);
      editor.putLong("canSyncBank", calendar.getTimeInMillis());
      return true;
    }
  }

  public void saveRegisterId(String reg_id) {
    editor.putString(FIREBASE_REGISTER_ID, reg_id);
    editor.commit();
  }

  public String getRegisterId() {
    return pref.getString(FIREBASE_REGISTER_ID, null);
  }

  public void setInvalidAccessToken() {

    editor.putBoolean("invalid_token", true);
    editor.commit();
  }

  public boolean isInvalidAccessToken() {
    if (isLoggedIn()) {
      editor.putBoolean("invalid_token", false);
      editor.commit();
      return false;
    } else {
      return pref.getBoolean("invalid_token", false);
    }
  }

  public void savePosition(int pos) {
    editor.putInt("position", pos);
    editor.commit();
  }

  public int getPosition(){
    int pos = pref.getInt("position", -1);
    return pos;
  }

  public void clearPos(){
    editor.remove("position");
    editor.commit();
  }

  public void saveNetworkState(int state){
    editor.putInt(NETWORK_STATE,state);
    editor.commit();
  }

  public int networkState(){
    return pref.getInt(NETWORK_STATE,0);
  }

  public void setInitialDayOfMonth(int d){
    editor.putInt(INITIAL_DAY_OF_MONTH,d);
    editor.commit();
  }

  public void setTourGuideShow(String guidKey, boolean show){
    preff_tourguide = _context.getSharedPreferences(PREF_TOURGUIDE, PRIVATE_MODE);
    editor_tourguide = preff_tourguide.edit();
    int res = show ? 1 : -1;
    editor_tourguide.putInt(guidKey, res);
    editor_tourguide.commit();
  }

  public boolean isAllowedToShowGuide(String guideKey){
    preff_tourguide = _context.getSharedPreferences(PREF_TOURGUIDE, PRIVATE_MODE);
    //preff_tourguide.edit().clear().commit(); //comment this to work
    int result = preff_tourguide.getInt(guideKey, 1);
    return result == 1;
  }

  public static void clearTourPreff(Context context){
    SharedPreferences preff_tourguide = context.getSharedPreferences(PREF_TOURGUIDE, PRIVATE_MODE);
    preff_tourguide.edit().clear().commit();
  }

  public boolean isFirstTime(){
    return pref.getBoolean("firsttime",true);
  }

  public void setIsFirstTimeTrue(){
    editor.putBoolean("firsttime",false);
    editor.commit();
  }

  public void setPhoneMami(String phoneMami){
    editor.putString("mami_phone",phoneMami);
    editor.commit();
  }

  public void setEmailMami(String emailMami){
    editor.putString("mami_email",emailMami);
    editor.commit();
  }

  public String getPhoneMami(){
    return pref.getString("mami_phone",null);
  }

  public String getEmailMami(){
    return pref.getString("mami_email",null);
  }

  public int getInitialDayOfMonth(){
    return pref.getInt(INITIAL_DAY_OF_MONTH,1);
  }

  public void setLastConnectAccount(int account_id,long date){
    Calendar calendar = Calendar.getInstance();
    editor.putLong("account_id_"+account_id,date);
    editor.commit();
  }

  public long getLastConnectAccount(int account_id){
    return pref.getLong("account_id_"+account_id,0);
  }

  //public void setLastConnectCashflowAccount(int account_id,long date){
  //  Calendar calendar = Calendar.getInstance();
  //  editor.putLong("cashflow_account_id_"+account_id,date);
  //  editor.commit();
  //}
  //
  //public long getLastConnectCashflowAccount(int account_id){
  //  return pref.getLong("cashflow_account_id_"+account_id,0);
  //}
}
