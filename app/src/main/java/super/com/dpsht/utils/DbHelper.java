package javasign.com.dompetsehat.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.StringJoiner;
import java.util.TreeMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.DanaDarurat;
import javasign.com.dompetsehat.models.DanaKuliah;
import javasign.com.dompetsehat.models.DanaPensiun;
import javasign.com.dompetsehat.models.Invest;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.ProductPlan;
import javasign.com.dompetsehat.models.ReferralFee;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.cashflow;
import javasign.com.dompetsehat.models.json.product;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.json.JSONObject;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by Fernando on 7/27/2015.
 * Please notice that db version set in General Helper.
 */
@Singleton public class DbHelper extends SQLiteOpenHelper {

  private static DbHelper dbhelper;

  // 57
  // create table tag, add coloum cicilan, anak,
  // dan tanggungan di table user,
  // create table investasi, 59 insert data investas ,
  // create db plan
  public static final int SCHEMA_VERSION = 62;

  MCryptNew mcrypt = new MCryptNew();
  GeneralHelper helper = GeneralHelper.getInstance();

  public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public final static int PLAN_TYPE_PENSIUN = 1;
  public final static int PLAN_TYPE_DARURAT = 2;
  public final static int PLAN_TYPE_CUSTOME = 3;
  public final static int PLAN_TYPE_KULIAH = 4;

  // Database Name
  public static final String DATABASE_NAME = "mst_dompetsehat.db";
  public static final String ID = "id";

  // Table Names
  static public final String TAB_CATEGORY = "category";
  static public final String TAB_USER_CATEGORY = "user_category";
  static public final String TAB_BUDGET = "budget";
  static public final String TAB_USER = "user";
  static public final String TAB_ACCOUNT = "account";
  static public final String TAB_PRODUCT = "product";
  static public final String TAB_ALARM = "alarm";
  static public final String TAB_VENDORS = "vendors";
  static public final String TAB_CASH = "cashflows";
  static public final String TAB_BADGES = "badges";
  static public final String TAB_POINT = "point";
  static public final String TAB_HISTORY = "history";
  static public final String TAB_PLAN = "plan";
  static public final String TAB_TAG = "tag";
  static public final String TAB_INVEST = "investation";
  static public final String TAB_PLAN_PRODUCT = "product_plan";
  static public final String TAB_DANA_PENSIUN = "dana_pensiun";
  static public final String TAB_DANA_DARURAT = "dana_darurat";
  static public final String TAB_DANA_CUSTOME = "dana_custome";
  static public final String TAB_DANA_KULIAH = "dana_kuliah";
  static public final String TAB_HISTORY_INVESTASI = "history_investasi";
  static public final String TAB_REFERRAL_FEE = "referral_fee";
  static public final String TAB_DEBTS = "debts";

  //column names table TAB_CATEGORY
  private final String ID_CATEGORY = "id_category";
  private final String NAME_CATEGORY = "name";
  private final String TYPE_CATEGORY = "parent";
  private final String ICON_CATEGORY = "icon"; //&#xe800
  private final String COLOR_CATEGORY = "color"; //red
  private final String DESCRIPTION_CATEGORY = "description";
  private final String BELONG_TO_CATEGORY = "belong_to";
  //is income/expense -> (88 for income, 99 for expense)

  //columns names table USER_CATEGORY (parent is TABLE_CATEGORY)
  private final String COLOR_NAME = "color_name";
  private final String ID_USER_CATEGORY = "id_user_category";
  private final String USER_CATEGORY_ID = "user_category_id";

  //column names table USER
  private final String ID_USER = "id_user";
  private final String EMAIL = "email";
  private final String USERNAME = "username";
  private final String BIRTHDAY = "birthday";
  private final String OCCUPATION = "occupation";
  private final String AFFILIATION = "affiliation";
  private final String ADDRESS = "address";
  private final String POSTAL_CODE = "postal_code";
  private final String AVATAR = "avatar";
  private final String REFERRAL_CODE = "referral_code";
  private final String REFERRER = "referrer";
  public static final String CREATED_AT = "created_at";
  public static final String UPDATED_AT = "updated_at";
  public static final String DELETED_AT = "deleted_at";
  private final String GENDER = "gender";
  public static final String LAST_SYNC_CASH = "last_sync";
  public final String LAST_SYNC_BUDGET = "last_sync_budget";
  public final String LAST_SYNC_ALARM = "last_sync_alarm";
  public final String INCOME_EVERY_MONTH = "pemasukan";
  public final String INSTALMENT = "cicilan";
  public final String CHILDS = "anak";
  public final String PHONE = "phone";

  //column names table ALARM MANAGER
  public final String ALARM_ID = "id_alarm";
  public final String ALARM_ID_USER = "user_id";
  public final String ALARM_ID_DOMPET = "dompet_id";
  public final String ALARM_ID_CATEGORY = "category_id";
  public final String ALARM_DESKRIPSI = "title_alarm";
  public final String ALARM_DATE = "date_alarm";
  public final String ALARM_JUMLAH = "jumlah_alarm";
  public final String ALARM_ACTIVE = "is_active";
  public final String ALARM_MONTH = "month_alarm";
  public final String ALARM_TOGGLE = "alarm_toggle";

  //column names table VENDORS
  static public final String ID_VENDOR = "id_vendor";
  static public final String VENDOR_NAME = "vendor_name";
  static public final String VENDOR_TYPE = "vendor_type";
  static public final String VENDOR_IMAGE = "vendor_image";

  //column names table account
  private final String ID_ACCOUNT = "idaccount";
  public final String USER_ID = "user_id";
  private final String NUMBER = "number";
  private final String VENDOR_ID = "vendor_id";
  private final String COLOR = "color";
  private final String LAST_CONNECT = "last_connect";
  private final String LOGIN_STATUS = "login_status";
  private final String LOGIN_INFO = "login_info";
  private final String TANGGAL_LAMA = "1970-01-01 12:12:12";
  private final String EMAIL_ALERT = "email_alert";
  private final String USERNAME_ACCOUNT = "username_account";
  private final String NAME_ACCOUNT = "name_account";
  private final String TYPE_ACCOUNT = "type_account";
  private final String PROPERTIES = "properties";

  //column names table product
  private final String NAME_PRODUCT = "name_product";
  private final String ID_PRODUCT = "id_product";
  private final String BALANCE = "balance";
  private final String TYPE = "type";
  private final String ACCOUNT_ID = "account_id";

  //column names table cashflow
  public static final String CASHFLOW_ID = "cashflow_id";
  public static final String PRODUCT_ID = "product_id";
  public static final String CASHFLOW_NAME = "description";
  public static final String CASHFLOW_NOTE = "note";
  public static final String CASHFLOW_DATE = "cashflow_date";
  public static final String CASHFLOW_TAG = "cashflow_tag";
  public static final String AMOUNT = "debetAmount";
  public static final String CATEGORY_ID = "category_id";
  public static final String TANGGAL = "tanggal";
  public static final String HARI = "hari";
  public static final String BULAN = "bulan";
  public static final String TAHUN = "tahun";
  public static final String STATUS = "status";
  public static final String JENIS_MUTASI = "jenis_mutasi";
  public static final String CASHFLOW_RENAME = "cashflow_rename";

  //column names table budget
  public final String ID_BUDGET = "id_budget";
  public final String DATE_START_BUDGET = "date_start";
  public final String AMOUNT_BUDGET = "amount_budget";
  public final String DATE_END_BUDGET = "date_end";
  public final String CATEGORY_BUDGET = "category_id_budget";
  public final String REPEAT_BUDGET = "repeat_budget";
  public final String REPEAT_EVERY = "repeat_every";

  // coloumn referral fee
  public final String ID_REFERRAL_FEE = "id_referral";
  public final String FEE_DATE = "fee_date";
  public final String CURRENCY = "currency";

  //column names table badges
  public String BADGES_NAME = "badges_name";

  // column names table points
  public String BADGES_ID = "badges_id";
  public String POINT = "point";

  // column names table PLAN
  public String ID_PLAN = "id_plan";
  public static String PLAN_ID_LOCAL = "plan_id_local";
  public String PLAN_TYPE = "type";
  public String PLAN_TITLE = "title";
  public String PLAN_TOTAL = "total";
  public String LIFE_TIME = "lifetimes";
  public String PLAN_DATE_START = "date_start";
  public String PLAN_AMOUNT_MONTHLY = "amount_monthly";
  public String PLAN_AMOUNT_YEARLY = "amount_yearly";
  public String PLAN_AMOUNT_CASH = "amount_cash";
  public String PLAN_RISK = "goal_risk";

  // coloumn names table tag
  public String ID_TAG = "id_tag";
  public String TAG_NAME = "tag_name";

  // coloumn name table produk investasi
  public String ID_INVESTASI = "id_investasi";
  public String INVESTASI_NAME = "invest_name";
  public String INVESTASI_PROCENTASE = "invest_persentase";
  public String INVESTASI_RATE = "invest_rate";

  // coloumn name table produk plan
  public String ID_PLAN_PRODUCT = "id_product_plan";
  public String PLAN_ID = "plan_id";
  public String INVEST_ID = "investasi_id";

  // coloumn name dana pensiun
  public String ID_DANA_PENSIUN = "id_dana_pensiun";
  public String PENDAPATAN = "pendapatan";
  public String UMUR_PENSIUN = "umur_pensiun";
  public String UMUR = "umur";

  // coloumn name dana darurat
  public String ID_DANA_DARURAT = "id_dana_darurat";
  public String BULAN_PENGGUNAAN = "bulan_penggunaan";
  public String PENGELUARAN_BULANAN = "pengeluaran_bulanan";

  // coloumn name dana kuliah
  public String ID_DANA_KULIAH = "id_dana_kuliah";
  public String LAMA_KULIAH = "lama_kuliah";
  public String NAMA_ANAK = "nama_anak";
  public String USIA_ANAK = "usia_anak";
  public String BIAYA_KULIAH = "biaya_kuliah";
  public String BIAYA_UANG_SAKU = "uang_saku";

  // coloumn name history investasi
  public String ID_HISTORY = "id_history";
  public String HISTORY_NAME = "history_name";
  public String HISTORY_DATE = "history_date";
  public String HISTORY_SALDO = "history_saldo";
  public String HISTORY_DEBIT = "history_debit";
  public String HISTORY_CREDIT = "history_credit";

  public String PAYBACK = "payback";
  public String NAME_DEBTS = "name_debts";
  public String DATE = "date";
  public String IDENTITY_TOKEN = "identity_token";

  //Table User
  private final String SQL_TAB_USER = "CREATE TABLE IF NOT EXISTS " + TAB_USER + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_USER + " INTEGER " +
      "," + USERNAME + " TEXT " +
      "," + EMAIL + " TEXT " +
      "," + BIRTHDAY + " TEXT " +
      "," + OCCUPATION + " TEXT " +
      "," + AFFILIATION + " TEXT " +
      "," + ADDRESS + " TEXT " +
      "," + POSTAL_CODE + " INTEGER " +
      "," + AVATAR + " TEXT " +
      "," + PHONE + " TEXT " +
      "," + GENDER + " TEXT " +
      "," + REFERRAL_CODE + " TEXT " +
      "," + REFERRER + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + LAST_SYNC_CASH + " TEXT DEFAULT '" + TANGGAL_LAMA + "'" +
      "," + LAST_SYNC_BUDGET + " TEXT DEFAULT '" + TANGGAL_LAMA + "'" +
      "," + LAST_SYNC_ALARM + " TEXT DEFAULT '" + TANGGAL_LAMA + "'" +
      "," + INCOME_EVERY_MONTH + " REAL " +
      "," + INSTALMENT + " REAL " +
      "," + CHILDS + " INTEGER " +
      ")";

  //table alarm_manager
  private final String SQL_TAB_ALARM_MANAGER = "CREATE TABLE IF NOT EXISTS "
      + TAB_ALARM
      + "("
      + ID
      + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
      + ALARM_ID
      + " INTEGER , "
      + ALARM_ID_USER
      + " INTEGER , "
      + ALARM_ID_DOMPET
      + " INTEGER , "
      + ALARM_ID_CATEGORY
      + " INTEGER , "
      + USER_CATEGORY_ID
      + " INTEGER , "
      + ALARM_DESKRIPSI
      + " TEXT , "
      + ALARM_JUMLAH
      + " TEXT , "
      + ALARM_DATE
      + " TEXT , "
      + ALARM_MONTH
      + " INTEGER , "
      + ALARM_ACTIVE
      + " TEXT, "
      + ALARM_TOGGLE
      + " TEXT DEFAULT '1', "
      + TYPE +" TEXT, "
      + DELETED_AT
      + " TEXT, "
      + CREATED_AT
      + " TEXT, "
      + UPDATED_AT
      + " TEXT "
      + ")";

  private String SQL_TAB_VENDORS = "CREATE TABLE IF NOT EXISTS " + TAB_VENDORS + " (" +
      "" + ID_VENDOR + " INTEGER PRIMARY KEY NOT NULL " +
      "," + VENDOR_NAME + " TEXT " +
      "," + VENDOR_TYPE + " TEXT " +
      "," + VENDOR_IMAGE + " TEXT " +
      ")";

  private String SQL_TAB_ACCOUNT = "CREATE TABLE IF NOT EXISTS " + TAB_ACCOUNT + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_ACCOUNT + " INTEGER " +
      "," + USER_ID + " INTEGER " +
      "," + USERNAME_ACCOUNT + " TEXT " +
      "," + NAME_ACCOUNT + " TEXT " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      "," + LOGIN_STATUS + " TEXT " +
      "," + LOGIN_INFO + " TEXT " +
      "," + VENDOR_ID + " INTEGER " +
      "," + EMAIL_ALERT + " INTEGER" +
      "," + LAST_CONNECT + " TEXT " +
      "," + PROPERTIES + " TEXT " +
      ")";

  private String SQL_TAB_PRODUCT = "CREATE TABLE IF NOT EXISTS " + TAB_PRODUCT + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_PRODUCT + " INTEGER " +
      "," + ACCOUNT_ID + " INTEGER " +
      "," + NAME_PRODUCT + " TEXT " +
      "," + COLOR + " INTEGER" +
      "," + BALANCE + " TEXT " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      "," + NUMBER + " TEXT " +
      "," + PROPERTIES + " TEXT " +
      "," + TYPE + " TEXT " +
      ")";

  private String SQL_TAB_CASH = "CREATE TABLE IF NOT EXISTS " + TAB_CASH + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + CASHFLOW_ID + " INTEGER " +
      "," + PRODUCT_ID + " INTEGER " +
      "," + USER_CATEGORY_ID + " INTEGER " +
      "," + CASHFLOW_NAME + " TEXT " +
      "," + CASHFLOW_RENAME + " TEXT " +
      "," + CASHFLOW_TAG + " TEXT " +
      "," + CASHFLOW_NOTE + " TEXT " +
      "," + CASHFLOW_DATE + " TEXT " +
      "," + CATEGORY_ID + " TEXT " +
      "," + HARI + " TEXT " +
      "," + TANGGAL + " INTEGER " +
      "," + BULAN + " INTEGER " +
      "," + TAHUN + " INTEGER " +
      "," + AMOUNT + " REAL " +
      "," + JENIS_MUTASI + " TEXT " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      "," + STATUS + " TEXT " +
      ")";

  // Table CATEGORY
  private final String SQL_TAB_CATEGORY = "CREATE TABLE IF NOT EXISTS "
      + TAB_CATEGORY
      + "("
      + ID
      + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
      + ID_CATEGORY
      + " INTEGER, "
      + NAME_CATEGORY
      + " TEXT, "
      + TYPE_CATEGORY
      + " TEXT, "
      + ICON_CATEGORY
      + " TEXT, "
      + COLOR_CATEGORY
      + " TEXT, "
      + BELONG_TO_CATEGORY
      + " INTEGER, "
      + DESCRIPTION_CATEGORY
      + " TEXT, "
      + USER_ID
      + " INTEGER, "
      + DELETED_AT
      + " TEXT, "
      + CREATED_AT
      + " TEXT, "
      + UPDATED_AT
      + " TEXT "
      + ")";

  private final String SQL_TAB_USER_CATEGORY = "CREATE TABLE IF NOT EXISTS "
      + TAB_USER_CATEGORY
      + "("
      + ID
      + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
      + USER_ID
      + " INTEGER, "
      + ID_CATEGORY
      + " INTEGER, "
      + ID_USER_CATEGORY
      + " INTEGER, "
      + NAME_CATEGORY
      + " TEXT, "
      + COLOR_CATEGORY
      + " TEXT, "
      + COLOR_NAME
      + " TEXT, "
      + DELETED_AT
      + " TEXT, "
      + CREATED_AT
      + " TEXT, "
      + UPDATED_AT
      + " TEXT "
      + ")";

  //table inf_budget
  private final String SQL_TAB_BUDGET = "CREATE TABLE IF NOT EXISTS "
      + TAB_BUDGET
      + "("
      + ID
      + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
      + ID_BUDGET
      + " INTEGER, "
      + USER_ID
      + " INTEGER, "
      + AMOUNT_BUDGET
      + " DOUBLE,"
      + CATEGORY_BUDGET
      + " INTEGER,"
      + USER_CATEGORY_ID
      + " INTEGER,"
      + DATE_START_BUDGET
      + " DATE, "
      + DATE_END_BUDGET
      + " DATE, "
      + REPEAT_BUDGET
      + " INTEGER, "
      + REPEAT_EVERY
      + " TEXT, "
      + DELETED_AT
      + " TEXT, "
      + CREATED_AT
      + " TEXT, "
      + UPDATED_AT
      + " TEXT "
      + ")";

  //TABEL OVERVIEW
  private String SQL_TAB_BADGES = "CREATE TABLE IF NOT EXISTS " + TAB_BADGES + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + BADGES_NAME + " TEXT " +
      ")";

  //TABEL POINT
  private String SQL_TAB_POINT = "CREATE TABLE IF NOT EXISTS " + TAB_POINT + " (" +
      "" + ID + " INTEGER PRIMARY KEY " +
      "," + BADGES_ID + " TEXT " +
      "," + POINT + " INTEGER " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      ")";

  //HISTORY
  private String SQL_TAB_HISTORY = "CREATE TABLE IF NOT EXISTS " + TAB_HISTORY + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + TANGGAL + " INTEGER " +
      "," + BULAN + " INTEGER " +
      "," + TAHUN + " INTEGER " +
      "," + POINT + " INTEGER " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      ")";

  //MORE
  private String SQL_TAB_PLAN = "CREATE TABLE IF NOT EXISTS " + TAB_PLAN + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_PLAN + " INTEGER " +
      "," + USER_ID + " INTEGER " +
      "," + PLAN_TYPE + " INTEGER " +
      "," + ACCOUNT_ID + " INTEGER " +
      "," + PRODUCT_ID + " INTEGER " +
      "," + PLAN_TITLE + " TEXT " +
      "," + PLAN_TOTAL + " INTEGER " +
      "," + LIFE_TIME + " INTEGER " +
      "," + PLAN_AMOUNT_MONTHLY + " REAL " +
      "," + PLAN_AMOUNT_YEARLY + " REAL " +
      "," + PLAN_AMOUNT_CASH + " REAL " +
      "," + PLAN_RISK + " REAL " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      ")";

  private String SQL_TAB_DANA_PENSIUN = "CREATE TABLE IF NOT EXISTS " + TAB_DANA_PENSIUN + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_DANA_PENSIUN + " INTEGER " +
      "," + PLAN_ID_LOCAL + " INTEGER " +
      "," + PLAN_ID + " INTEGER " +
      "," + PENDAPATAN + " INTEGER " +
      "," + UMUR_PENSIUN + " INTEGER " +
      "," + UMUR + " INTEGER " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      ")";

  private String SQL_TAB_DANA_DARURAT = "CREATE TABLE IF NOT EXISTS " + TAB_DANA_DARURAT + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_DANA_DARURAT + " INTEGER " +
      "," + PLAN_ID_LOCAL + " INTEGER " +
      "," + PLAN_ID + " INTEGER " +
      "," + PENGELUARAN_BULANAN + " REAL " +
      "," + BULAN_PENGGUNAAN + " INTEGER " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      ")";

  private String SQL_TAB_DANA_KULIAH = "CREATE TABLE IF NOT EXISTS " + TAB_DANA_KULIAH + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_DANA_KULIAH + " INTEGER " +
      "," + PLAN_ID_LOCAL + " INTEGER " +
      "," + PLAN_ID + " INTEGER " +
      "," + LAMA_KULIAH + " INTEGER " +
      "," + NAMA_ANAK + " TEXT " +
      "," + USIA_ANAK + " INTEGER " +
      "," + BIAYA_KULIAH + " REAL " +
      "," + BIAYA_UANG_SAKU + " REAL " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      ")";

  private String SQL_TAB_PLAN_PRODUCT = "CREATE TABLE IF NOT EXISTS " + TAB_PLAN_PRODUCT + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_PLAN_PRODUCT + " INTEGER " +
      "," + PLAN_ID_LOCAL + " INTEGER " +
      "," + PLAN_ID + " INTEGER " +
      "," + PRODUCT_ID + " INTEGER " +
      "," + INVEST_ID + " INTEGER " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      ")";

  private String SQL_TAB_TAG = "CREATE TABLE IF NOT EXISTS " + TAB_TAG + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_TAG + " INTEGER " +
      "," + TAG_NAME + " TEXT " +
      ")";

  private String SQL_TAB_INVEST = "CREATE TABLE IF NOT EXISTS " + TAB_INVEST + " (" +
      "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
      "," + ID_INVESTASI + " INTEGER " +
      "," + VENDOR_ID + " INTEGER " +
      "," + INVESTASI_NAME + " TEXT " +
      "," + INVESTASI_PROCENTASE + " REAL " +
      "," + INVESTASI_RATE + " INTEGER " +
      "," + CREATED_AT + " TEXT " +
      "," + UPDATED_AT + " TEXT " +
      "," + DELETED_AT + " TEXT " +
      ")";

  private String SQL_TAB_HISTORY_INVESTASI =
      "CREATE TABLE IF NOT EXISTS " + TAB_HISTORY_INVESTASI + " (" +
          "" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " +
          "," + ID_HISTORY + " INTEGER " +
          "," + HISTORY_NAME + " TEXT " +
          "," + HISTORY_DATE + " TEXT " +
          "," + HISTORY_CREDIT + " REAL " +
          "," + HISTORY_DEBIT + " REAL " +
          "," + HISTORY_SALDO + " REAL " +
          "," + CREATED_AT + " TEXT " +
          "," + UPDATED_AT + " TEXT " +
          "," + DELETED_AT + " TEXT " +
          ")";


  private String SQL_TAB_REFERRAL_FEE = "CREATE TABLE IF NOT EXISTS "+TAB_REFERRAL_FEE+" ("
      + ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL "
      +","+ACCOUNT_ID+" INTEGER "
      +","+ID_REFERRAL_FEE+" INTEGER "
      +","+FEE_DATE+" TEXT "
      +","+CURRENCY+" TEXT "
      +","+TYPE+" TEXT "
      +","+AMOUNT+" REAL "
      +","+CREATED_AT+" TEXT "
      +","+UPDATED_AT+" TEXT "
      +","+DELETED_AT+" TEXT "
      + ")";

  private String SQL_TAB_DEBTS = "CREATE TABLE IF NOT EXISTS "+TAB_DEBTS+" ("
      + ""+ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL "
      +","+USER_ID+" INTEGER "
      +","+ACCOUNT_ID+" INTEGER "
      +","+PRODUCT_ID+" INTEGER "
      +","+CASHFLOW_ID+" INTEGER "
      +","+NAME_DEBTS+" TEXT "
      +","+TYPE+" TEXT "
      +","+AMOUNT+" REAL "
      +","+DATE+" TEXT "
      +","+PAYBACK+" TEXT "
      +","+EMAIL+" TEXT "
      +","+CREATED_AT+" TEXT "
      +","+UPDATED_AT+" TEXT "
      +","+DELETED_AT+" TEXT "
      + ")";

  Context context;
  SessionManager sessionManager;

  @Inject public DbHelper(@ApplicationContext Context context) {
    super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    this.context = context;
    this.sessionManager = new SessionManager(context);
  }

  public static synchronized DbHelper getInstance(Context context) {
    if (dbhelper == null) {
      dbhelper = new DbHelper(context);
    }
    return dbhelper;
  }

  @Override public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_TAB_USER);
    sqLiteDatabase.execSQL(SQL_TAB_CATEGORY);
    sqLiteDatabase.execSQL(SQL_TAB_USER_CATEGORY);
    sqLiteDatabase.execSQL(SQL_TAB_VENDORS);
    sqLiteDatabase.execSQL(SQL_TAB_ALARM_MANAGER);
    sqLiteDatabase.execSQL(SQL_TAB_ACCOUNT);
    sqLiteDatabase.execSQL(SQL_TAB_PRODUCT);
    sqLiteDatabase.execSQL(SQL_TAB_BUDGET);
    sqLiteDatabase.execSQL(SQL_TAB_CASH);
    sqLiteDatabase.execSQL(SQL_TAB_BADGES);
    sqLiteDatabase.execSQL(SQL_TAB_POINT);
    sqLiteDatabase.execSQL(SQL_TAB_HISTORY);
    sqLiteDatabase.execSQL(SQL_TAB_PLAN);
    sqLiteDatabase.execSQL(SQL_TAB_TAG);
    sqLiteDatabase.execSQL(SQL_TAB_INVEST);
    sqLiteDatabase.execSQL(SQL_TAB_PLAN_PRODUCT);
    sqLiteDatabase.execSQL(SQL_TAB_DANA_PENSIUN);
    sqLiteDatabase.execSQL(SQL_TAB_DANA_DARURAT);
    sqLiteDatabase.execSQL(SQL_TAB_DANA_KULIAH);
    sqLiteDatabase.execSQL(SQL_TAB_HISTORY);
    sqLiteDatabase.execSQL(SQL_TAB_REFERRAL_FEE);
    sqLiteDatabase.execSQL(SQL_TAB_DEBTS);
    insertInvest(sqLiteDatabase);
    System.out.println("DbHelper.onCreate scheme : " + SCHEMA_VERSION);
  }

  @Override public void onOpen(SQLiteDatabase db) {
    super.onOpen(db);
    if (!db.isReadOnly()) {
      db.execSQL("PRAGMA automatic_index = off;");
    }
  }

  @Override public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    switch (oldVersion) {
      case 58:
        updateFromVersion58(sqLiteDatabase, oldVersion, newVersion);
        break;
      case 59:
        updateFromVersion59(sqLiteDatabase, oldVersion, newVersion);
        break;
      case 60:
        updateFromVersion60(sqLiteDatabase,oldVersion,newVersion);
      case 61:
        updateFromVersion61(sqLiteDatabase,oldVersion,newVersion);
      default:
        if (newVersion > oldVersion) {
          generalActionUpdateDB(sqLiteDatabase);
        }
        break;
    }
  }

  private void updateFromVersion58(SQLiteDatabase database, int oldVersion, int newVersion) {
    // heloo
    onUpgrade(database, oldVersion + 1, newVersion);
  }

  private void updateFromVersion59(SQLiteDatabase database, int oldVersion, int newVersion) {
    // heloo
    database.execSQL("ALTER TABLE " + TAB_CASH + " ADD COLUMN user_category_id INTEGER");
    database.execSQL("ALTER TABLE " + TAB_BUDGET + " ADD COLUMN user_category_id INTEGER");
    database.execSQL("ALTER TABLE " + TAB_ALARM + " ADD COLUMN user_category_id INTEGER");
    onUpgrade(database, oldVersion + 1, newVersion);
  }

  private void updateFromVersion60(SQLiteDatabase database, int oldVersion, int newVersion) {
    // heloo
    database.execSQL(SQL_TAB_REFERRAL_FEE);
    database.execSQL(SQL_TAB_DEBTS);
    onUpgrade(database, oldVersion + 1, newVersion);
  }

  private void updateFromVersion61(SQLiteDatabase database, int oldVersion, int newVersion){
    try {
      database.execSQL("ALTER TABLE "+TAB_ALARM+"  ADD "+TYPE+" text");
    }catch (Exception e){

    }
    onUpgrade(database,oldVersion +1,newVersion);
  }

  private void generalActionUpdateDB(SQLiteDatabase sqLiteDatabase) {
    try {
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_USER);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_CATEGORY);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_USER_CATEGORY);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_VENDORS);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_ACCOUNT);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_PRODUCT);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_BUDGET);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_CASH);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_BADGES);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_POINT);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_HISTORY);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_PLAN);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_INVEST);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_PLAN);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_TAG);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_PLAN_PRODUCT);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_DANA_PENSIUN);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_DANA_DARURAT);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_DANA_KULIAH);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TAB_DANA_CUSTOME);
      onCreate(sqLiteDatabase);
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    }
  }

  private void insertInvest(SQLiteDatabase db) {
    String[] labels = context.getResources().getStringArray(R.array.manulife_products);
    String[] persens = context.getResources().getStringArray(R.array.manulife_persentase);
    int id = 1;
    for (String label : labels) {
      ContentValues cv = new ContentValues();
      cv.put(ID_INVESTASI, id);
      cv.put(INVESTASI_NAME, label);
      cv.put(INVESTASI_PROCENTASE, persens[id - 1]);
      db.insert(TAB_INVEST, ID, cv);
      id += 1;
    }
  }

  /*BEGIN CONTENT VALUES*/
  private ContentValues getContentValuesUser(User user) {
    ContentValues cv = new ContentValues();
    cv.put(ID_USER, user.getUser_id());
    cv.put(USERNAME, user.getUsername());
    cv.put(EMAIL, user.getEmail());
    cv.put(BIRTHDAY, user.getBirthday());
    cv.put(PHONE, user.getPhone());
    cv.put(OCCUPATION, user.getOccupation());
    cv.put(AFFILIATION, user.getAffiliation());
    cv.put(ADDRESS, user.getAddress());
    cv.put(POSTAL_CODE, user.getPostal_code());
    cv.put(AVATAR, user.getAvatar());
    cv.put(GENDER, user.getGender());
    cv.put(REFERRAL_CODE, user.getReferral_code());
    cv.put(REFERRER, user.getReferrer());
    cv.put(CREATED_AT, user.getCreated_at());
    cv.put(UPDATED_AT, user.getUpdated_at());

    // table baru
    cv.put(INSTALMENT, user.getCicilan());
    cv.put(INCOME_EVERY_MONTH, user.getPenghasilan());
    cv.put(CHILDS, user.getAnak());

    //if (user.getLast_sync_cash() != null) {
    //  if (!user.getLast_sync_cash().isEmpty()
    //      && !user.getLast_sync_cash().equalsIgnoreCase("null")
    //      && !user.getLast_sync_cash().equalsIgnoreCase("")
    //      && user.getLast_sync_cash().length() != 0) {
    //    cv.put(LAST_SYNC_CASH,
    //        helper.converterEpochToDate(Long.valueOf(user.getLast_sync_cash()) - 1));
    //  }
    //}
    //
    //if (user.getLast_sync_budget() != null) {
    //  if (!user.getLast_sync_budget().isEmpty()
    //      && !user.getLast_sync_budget()
    //      .equalsIgnoreCase("null")
    //      && !user.getLast_sync_budget().equalsIgnoreCase("")
    //      && user.getLast_sync_budget().length() != 0) {
    //    cv.put(LAST_SYNC_BUDGET,
    //        helper.converterEpochToDate(Long.valueOf(user.getLast_sync_budget()) - 1));
    //  }
    //}
    //if (user.getLast_sync_alarm() != null) {
    //  if (!user.getLast_sync_alarm().isEmpty()
    //      && !user.getLast_sync_alarm()
    //      .equalsIgnoreCase("null")
    //      && !user.getLast_sync_alarm().equalsIgnoreCase("")
    //      && user.getLast_sync_alarm().length() != 0) {
    //    cv.put(LAST_SYNC_ALARM,
    //        helper.converterEpochToDate(Long.valueOf(user.getLast_sync_alarm()) - 1));
    //  }
    //}

    return cv;
  }

  private ContentValues getContentValuesAlarm(Alarm alarm) {
    ContentValues cv = new ContentValues();
    cv.put(ALARM_ID, alarm.getId_alarm());
    cv.put(ALARM_ID_USER, alarm.getId_user());
    cv.put(ALARM_ID_DOMPET, alarm.getId_dompet());
    cv.put(ALARM_ID_CATEGORY, alarm.getId_category());
    cv.put(USER_CATEGORY_ID, alarm.getUser_id_category());
    cv.put(ALARM_DESKRIPSI, alarm.getDeskripsi_alarm());
    cv.put(ALARM_JUMLAH, alarm.getJumlah_alarm());
    cv.put(ALARM_DATE, alarm.getDate_alarm());
    cv.put(ALARM_ACTIVE, alarm.getIs_active());
    cv.put(ALARM_MONTH, alarm.getMonth_alarm());
    cv.put(TYPE,alarm.getType());
    if (alarm.getToggle() != null && !alarm.getToggle().equalsIgnoreCase("")) {
      cv.put(ALARM_TOGGLE, alarm.getToggle());
    }
    cv.put(CREATED_AT, alarm.getCreated_at());
    cv.put(UPDATED_AT, alarm.getUpdated_at());
    cv.put(DELETED_AT, alarm.getDeleted_at());
    return cv;
  }

  private ContentValues getContentValuesPlan(Plan plan) {
    ContentValues cv = new ContentValues();
    cv.put(ID_PLAN, plan.getId_plan());
    cv.put(ACCOUNT_ID, plan.getAccount_id());
    cv.put(PRODUCT_ID, plan.getProduct_id());
    cv.put(USER_ID, plan.getUser_id());
    cv.put(PLAN_TYPE, plan.getType());
    cv.put(PLAN_TITLE, plan.getPlan_title());
    cv.put(PLAN_TOTAL, plan.getPlan_total());
    cv.put(LIFE_TIME, plan.getLifetime());
    cv.put(PLAN_AMOUNT_MONTHLY, plan.getPlan_amount_monthly());
    cv.put(PLAN_AMOUNT_YEARLY, plan.getPlan_amount_yearly());
    cv.put(PLAN_AMOUNT_CASH, plan.getPlan_amount_cash());
    cv.put(PLAN_RISK, plan.getPlan_risk());
    cv.put(CREATED_AT, plan.getCreated_at());
    cv.put(UPDATED_AT, plan.getUpdated_at());
    cv.put(DELETED_AT, plan.getDeleted_at());
    return cv;
  }

  private ContentValues getContentValuesDanaPensiun(DanaPensiun danaPensiun) {
    ContentValues cv = new ContentValues();
    cv.put(ID_DANA_PENSIUN, danaPensiun.getId_dana_pensiun());
    cv.put(PLAN_ID, danaPensiun.getId_plan());
    cv.put(PLAN_ID_LOCAL, danaPensiun.getId_plan_local());
    cv.put(PENDAPATAN, danaPensiun.getPendapatan());
    cv.put(UMUR_PENSIUN, danaPensiun.getUmur_pensiun());
    cv.put(UMUR, danaPensiun.getUmur());
    cv.put(CREATED_AT, danaPensiun.getCreated_at());
    cv.put(UPDATED_AT, danaPensiun.getUpdated_at());
    cv.put(DELETED_AT, danaPensiun.getDeleted_at());
    return cv;
  }

  private ContentValues getContentValuesDanaDarurat(DanaDarurat danaDarurat) {
    ContentValues cv = new ContentValues();
    cv.put(ID_DANA_DARURAT, danaDarurat.getId_dana_darurat());
    cv.put(PLAN_ID, danaDarurat.getId_plan());
    cv.put(PLAN_ID_LOCAL, danaDarurat.getId_plan_local());
    cv.put(PENGELUARAN_BULANAN, danaDarurat.getPengeluaran_bulanan());
    cv.put(BULAN_PENGGUNAAN, danaDarurat.getBulan_penggunaan());
    cv.put(CREATED_AT, danaDarurat.getCreated_at());
    cv.put(UPDATED_AT, danaDarurat.getUpdated_at());
    cv.put(DELETED_AT, danaDarurat.getDeleted_at());
    return cv;
  }

  private ContentValues getContentValuesDanaKuliah(DanaKuliah danaKuliah) {
    ContentValues cv = new ContentValues();
    cv.put(PLAN_ID, danaKuliah.getId_plan());
    cv.put(PLAN_ID_LOCAL, danaKuliah.getId_plan_local());
    cv.put(ID_DANA_KULIAH, danaKuliah.getId_dana_kuliah());
    cv.put(NAMA_ANAK, danaKuliah.getNama_anak());
    cv.put(USIA_ANAK, danaKuliah.getUsia_anak());
    cv.put(LAMA_KULIAH, danaKuliah.getLama_kuliah());
    cv.put(BIAYA_KULIAH, danaKuliah.getBiaya_kuliah());
    cv.put(BIAYA_UANG_SAKU, danaKuliah.getUang_saku());
    cv.put(CREATED_AT, danaKuliah.getCreated_at());
    cv.put(UPDATED_AT, danaKuliah.getUpdated_at());
    cv.put(DELETED_AT, danaKuliah.getDeleted_at());
    return cv;
  }

  private ContentValues getContentValuesProductPlan(ProductPlan productPlan) {
    ContentValues cv = new ContentValues();
    cv.put(ID_PLAN_PRODUCT,productPlan.getId_product_plan());
    cv.put(PLAN_ID, productPlan.getPlan_id());
    cv.put(PLAN_ID_LOCAL, productPlan.getPlan_id_local());
    cv.put(PRODUCT_ID, productPlan.getProduct_id());
    cv.put(INVEST_ID, productPlan.getInvest_id());
    cv.put(CREATED_AT, productPlan.getCreated_at());
    cv.put(UPDATED_AT, productPlan.getUpdated_at());
    cv.put(DELETED_AT, productPlan.getDeleted_at());
    return cv;
  }

  private ContentValues getContentValuesCategory(Category category) {
    ContentValues cv = new ContentValues();
    cv.put(ID_CATEGORY, category.getId_category());
    cv.put(NAME_CATEGORY, category.getName());
    cv.put(TYPE_CATEGORY, category.getParent());
    cv.put(ICON_CATEGORY, category.getIcon());
    cv.put(COLOR_CATEGORY, category.getColor());
    if(!TextUtils.isEmpty(category.getBelong_to())) {
      cv.put(BELONG_TO_CATEGORY, category.getBelong_to());
    }
    cv.put(DESCRIPTION_CATEGORY, category.getDescription());
    cv.put(UPDATED_AT, category.getUpdated_at());
    cv.put(CREATED_AT, category.getCreated_at());
    return cv;
  }

  private ContentValues getContentValuesUserCategory(UserCategory userCategory) {
    ContentValues cv = new ContentValues();
    cv.put(ID_USER_CATEGORY, userCategory.getId_user_category());
    cv.put(USER_ID, userCategory.getUser_id());
    cv.put(ID_CATEGORY, userCategory.getParentCategoryId());
    cv.put(NAME_CATEGORY, userCategory.getName());
    cv.put(COLOR_CATEGORY, userCategory.getColor());
    cv.put(COLOR_NAME, userCategory.getColorName());
    cv.put(UPDATED_AT, userCategory.getUpdated_at());
    cv.put(CREATED_AT, userCategory.getCreated_at());
    return cv;
  }

  private ContentValues getContentValuesAccount(Account account) {
    ContentValues cv = new ContentValues();
    cv.put(ID_ACCOUNT, account.getIdaccount());
    cv.put(USER_ID, account.getIduser());
    cv.put(NAME_ACCOUNT, account.getName());
    cv.put(USERNAME_ACCOUNT, account.getUsername());
    cv.put(LOGIN_STATUS, account.getLogin_status());
    cv.put(LOGIN_INFO, account.getLogin_info());
    cv.put(CREATED_AT, account.getCreated_at());
    cv.put(UPDATED_AT, account.getUpdated_at());
    cv.put(VENDOR_ID, account.getIdvendor());
    cv.put(LAST_CONNECT, account.getLast_connect());
    cv.put(PROPERTIES, account.getProperties());
    return cv;
  }

  private ContentValues getContentValuesCash(Cash cash) {
    ContentValues cv = new ContentValues();
    cv.put(CASHFLOW_ID, cash.getCash_id());
    cv.put(PRODUCT_ID, cash.getProduct_id());
    cv.put(CASHFLOW_NAME, cash.getDescription());
    cv.put(CASHFLOW_TAG, cash.getCash_tag().toLowerCase());
    cv.put(CASHFLOW_NOTE, cash.getNote());
    cv.put(CASHFLOW_DATE, cash.getCash_date());
    cv.put(CASHFLOW_RENAME, cash.getCashflow_rename());
    cv.put(CATEGORY_ID, cash.getCategory_id());
    cv.put(USER_CATEGORY_ID, cash.getUser_category_id());
    cv.put(HARI, cash.getHari());
    cv.put(TANGGAL, cash.getTanggal());
    cv.put(BULAN, cash.getBulan());
    cv.put(TAHUN, cash.getTahun());
    cv.put(AMOUNT, cash.getAmount());
    cv.put(JENIS_MUTASI, cash.getType());
    cv.put(CREATED_AT, cash.getCreated_at());
    cv.put(UPDATED_AT, cash.getUpdated_at());
    if (cash.getDeleted_at() != null) {
      cv.put(DELETED_AT, cash.getDeleted_at());
    }
    cv.put(STATUS, cash.getStatus());
    Timber.e("avesina mustari "+cv);
    return cv;
  }

  private ContentValues getContentValuesProduct(Product product) {
    ContentValues cv = new ContentValues();
    Random rand = new Random();
    cv.put(ID_PRODUCT, product.getId_product());
    cv.put(ACCOUNT_ID, product.getAccount_id());
    cv.put(BALANCE, product.getBalance());
    cv.put(CREATED_AT, product.getCreated_at());
    cv.put(UPDATED_AT, product.getUpdated_at());
    if (product.getProperties() != null) {
      cv.put(PROPERTIES, product.getProperties());
    }
    int color = rand.nextInt(14);
    cv.put(COLOR, color);
    cv.put(DELETED_AT, product.getDeleted_at());
    cv.put(NUMBER, product.getNumber());
    cv.put(TYPE, product.getType());
    cv.put(NAME_PRODUCT, product.getName());
    return cv;
  }

  private ContentValues getContentValuesBudget(Budget budget) {
    ContentValues cv = new ContentValues();
    cv.put(ID_BUDGET, budget.getId_budget());
    cv.put(USER_ID, budget.getUser_id());
    cv.put(AMOUNT_BUDGET, budget.getAmount_budget());
    cv.put(CATEGORY_BUDGET, budget.getCategory_budget());
    cv.put(USER_CATEGORY_ID, budget.getUser_category_budget());
    cv.put(DATE_START_BUDGET, budget.getDate_start());
    cv.put(DATE_END_BUDGET, budget.getDate_end());
    cv.put(REPEAT_BUDGET, budget.getRepeat());
    cv.put(REPEAT_EVERY, budget.getEvery());
    cv.put(DELETED_AT, budget.getDeleted_at());
    cv.put(CREATED_AT, budget.getCreated_at());
    cv.put(UPDATED_AT, budget.getUpdated_at());
    return cv;
  }



  private ContentValues getContentValuesReferralFee(ReferralFee fee) {
    ContentValues cv = new ContentValues();
    //cv.put(ID,fee.getId());
    cv.put(ACCOUNT_ID,fee.getAccount_id());
    cv.put(ID_REFERRAL_FEE,fee.getId_referral_fee());
    cv.put(FEE_DATE,fee.getFee_date());
    cv.put(CURRENCY,fee.getCurrency());
    cv.put(TYPE,fee.getType());
    cv.put(AMOUNT,fee.getAmount());
    cv.put(CREATED_AT,fee.getCreated_at());
    cv.put(UPDATED_AT,fee.getUpdated_at());
    cv.put(DELETED_AT,fee.getDeleted_at());
    return cv;
  }
    /*END CONTENT VALUES*/

  /*BEGIN CURSOR*/
  private User cursorToUser(Cursor cursor) {
    return new User().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setUser_id(cursor.getInt(cursor.getColumnIndex(ID_USER)))
        .setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)))
        .setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)))
        .setBirthday(cursor.getString(cursor.getColumnIndex(BIRTHDAY)))
        .setOccupation(cursor.getString(cursor.getColumnIndex(OCCUPATION)))
        .setAffiliation(cursor.getString(cursor.getColumnIndex(AFFILIATION)))
        .setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS)))
        .setPostal_code(cursor.getString(cursor.getColumnIndex(POSTAL_CODE)))
        .setAvatar(cursor.getString(cursor.getColumnIndex(AVATAR)))
        .setGender(cursor.getString(cursor.getColumnIndex(GENDER)))
        .setPhone(cursor.getString(cursor.getColumnIndex(PHONE)))
        .setReferral_code(cursor.getString(cursor.getColumnIndex(REFERRAL_CODE)))
        .setReferrer(cursor.getString(cursor.getColumnIndex(REFERRER)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)))
        .setLast_sync_cash(cursor.getString(cursor.getColumnIndex(LAST_SYNC_CASH)))
        .setLast_sync_budget(cursor.getString(cursor.getColumnIndex(LAST_SYNC_BUDGET)))
        .setLast_sync_alarm(cursor.getString(cursor.getColumnIndex(LAST_SYNC_ALARM)))
        .setPenghasilan(cursor.getDouble(cursor.getColumnIndex(INCOME_EVERY_MONTH)))
        .setCicilan(cursor.getDouble(cursor.getColumnIndex(INSTALMENT)))
        .setAnak(cursor.getInt(cursor.getColumnIndex(CHILDS)));
  }

  private Cash cursorToCash(Cursor cursor) {
    Cash cash = new Cash().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setCash_id(cursor.getInt(cursor.getColumnIndex(CASHFLOW_ID)))
        .setProduct_id(cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)))
        .setNote(cursor.getString(cursor.getColumnIndex(CASHFLOW_NOTE)))
        .setDescription(cursor.getString(cursor.getColumnIndex(CASHFLOW_NAME)))
        .setCashflow_rename(cursor.getString(cursor.getColumnIndex(CASHFLOW_RENAME)))
        .setCash_date(cursor.getString(cursor.getColumnIndex(CASHFLOW_DATE)))
        .setHari(cursor.getString(cursor.getColumnIndex(HARI)))
        .setTanggal(cursor.getInt(cursor.getColumnIndex(TANGGAL)))
        .setBulan(cursor.getInt(cursor.getColumnIndex(BULAN)))
        .setTahun(cursor.getInt(cursor.getColumnIndex(TAHUN)))
        .setAmount(cursor.getLong(cursor.getColumnIndex(AMOUNT)))
        .setType(cursor.getString(cursor.getColumnIndex(JENIS_MUTASI)))
        .setStatus(cursor.getString(cursor.getColumnIndex(STATUS)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)))
        .setCategory_id(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID)))
        .setUser_category_id(cursor.getInt(cursor.getColumnIndex(USER_CATEGORY_ID)))
        .setCash_tag(cursor.getString(cursor.getColumnIndex(CASHFLOW_TAG)));
    if (cursor.getColumnIndex(CATEGORY_ID) > -1) {
      Category category = getCategoryByID(cash.getCategory_id());
      cash = cash.setCategory(category);
      if (cursor.getColumnIndex(USER_CATEGORY_ID) > -1) {
        if (cursor.getInt(cursor.getColumnIndex(USER_CATEGORY_ID)) > 0) {
          UserCategory userCategory = getUserCategoryByID(cash.getUser_category_id());
          userCategory.setParentCategory(category);
          cash = cash.setUserCategory(userCategory);
        }
      }
    }
    return cash;
  }

  private Budget cursorToBudget(Cursor cursor) {
    return new Budget().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setId_budget(cursor.getInt(cursor.getColumnIndex(ID_BUDGET)))
        .setUser_id(cursor.getInt(cursor.getColumnIndex(USER_ID)))
        .setAmount_budget(cursor.getDouble(cursor.getColumnIndex(AMOUNT_BUDGET)))
        .setCategory_budget(cursor.getInt(cursor.getColumnIndex(CATEGORY_BUDGET)))
        .setUser_category_budget(cursor.getInt(cursor.getColumnIndex(USER_CATEGORY_ID)))
        .setDate_start(cursor.getString(cursor.getColumnIndex(DATE_START_BUDGET)))
        .setDate_end(cursor.getString(cursor.getColumnIndex(DATE_END_BUDGET)))
        .setRepeat(cursor.getInt(cursor.getColumnIndex(REPEAT_BUDGET)))
        .setEvery(cursor.getString(cursor.getColumnIndex(REPEAT_EVERY)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)));
  }

  private Alarm cursorToAlarm(Cursor cursor) {
    return new Alarm().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setId_alarm(cursor.getInt(cursor.getColumnIndex(ALARM_ID)))
        .setId_user(cursor.getInt(cursor.getColumnIndex(ALARM_ID_USER)))
        .setId_dompet(cursor.getInt(cursor.getColumnIndex(ALARM_ID_DOMPET)))
        .setId_category(cursor.getInt(cursor.getColumnIndex(ALARM_ID_CATEGORY)))
        .setUser_id_category(cursor.getInt(cursor.getColumnIndex(USER_CATEGORY_ID)))
        .setDeskripsi_alarm(cursor.getString(cursor.getColumnIndex(ALARM_DESKRIPSI)))
        .setJumlah_alarm(cursor.getFloat(cursor.getColumnIndex(ALARM_JUMLAH)))
        .setDate_alarm(cursor.getString(cursor.getColumnIndex(ALARM_DATE)))
        .setMonth_alarm(cursor.getInt(cursor.getColumnIndex(ALARM_MONTH)))
        .setIs_active(cursor.getString(cursor.getColumnIndex(ALARM_ACTIVE)))
        .setType(cursor.getString(cursor.getColumnIndex(TYPE)))
        .setToggle(cursor.getString(cursor.getColumnIndex(ALARM_TOGGLE)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)));
  }

  private Account cursorToAccount(Cursor cursor) {
    return new Account().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setIdaccount(cursor.getInt(cursor.getColumnIndex(ID_ACCOUNT)))
        .setIduser(cursor.getInt(cursor.getColumnIndex(USER_ID)))
        .setName(cursor.getString(cursor.getColumnIndex(NAME_ACCOUNT)))
        .setUsername(cursor.getString(cursor.getColumnIndex(USERNAME_ACCOUNT)))
        .setLogin_status(cursor.getString(cursor.getColumnIndex(LOGIN_STATUS)))
        .setLogin_info(cursor.getString(cursor.getColumnIndex(LOGIN_INFO)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setIdvendor(cursor.getInt(cursor.getColumnIndex(VENDOR_ID)))
        .setProperties(cursor.getString(cursor.getColumnIndex(PROPERTIES)))
        .setLast_connect(cursor.getString(cursor.getColumnIndex(LAST_CONNECT)));
  }

  private Product cursorToProduct(Cursor cursor) {
    return new Product().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setId_product(cursor.getInt(cursor.getColumnIndex(ID_PRODUCT)))
        .setAccount_id(cursor.getInt(cursor.getColumnIndex(ACCOUNT_ID)))
        .setBalance(cursor.getLong(cursor.getColumnIndex(BALANCE)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)))
        .setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)))
        .setColor(cursor.getInt(cursor.getColumnIndex(COLOR)))
        .setType(cursor.getString(cursor.getColumnIndex(TYPE)))
        .setProperties(cursor.getString(cursor.getColumnIndex(PROPERTIES)))
        .setName(cursor.getString(cursor.getColumnIndex(NAME_PRODUCT)));
  }

  private Plan cursorToPlan(Cursor cursor) {
    return new Plan().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setId_plan(cursor.getInt(cursor.getColumnIndex(ID_PLAN)))
        .setUser_id(cursor.getInt(cursor.getColumnIndex(USER_ID)))
        .setAccount_id(cursor.getInt(cursor.getColumnIndex(ACCOUNT_ID)))
        .setProduct_id(cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)))
        .setType(cursor.getInt(cursor.getColumnIndex(PLAN_TYPE)))
        .setLifetime(cursor.getInt(cursor.getColumnIndex(LIFE_TIME)))
        .setPlan_title(cursor.getString(cursor.getColumnIndex(PLAN_TITLE)))
        .setPlan_total(cursor.getLong(cursor.getColumnIndex(PLAN_TOTAL)))
        .setPlan_amount_monthly(cursor.getLong(cursor.getColumnIndex(PLAN_AMOUNT_MONTHLY)))
        .setPlan_amount_yearly(cursor.getLong(cursor.getColumnIndex(PLAN_AMOUNT_YEARLY)))
        .setPlan_amount_cash(cursor.getLong(cursor.getColumnIndex(PLAN_AMOUNT_CASH)))
        .setPlan_risk(cursor.getDouble(cursor.getColumnIndex(PLAN_RISK)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)));
  }

  private ProductPlan cursorToPlanProduct(Cursor cursor) {
    Timber.e("cursor " + cursor.getInt(cursor.getColumnIndex(ID)));
    return new ProductPlan().setId_product_plan(
        cursor.getInt(cursor.getColumnIndex(ID_PLAN_PRODUCT)))
        .setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setPlan_id(cursor.getInt(cursor.getColumnIndex(PLAN_ID)))
        .setPlan_id_local(cursor.getInt(cursor.getColumnIndex(PLAN_ID_LOCAL)))
        .setProduct_id(cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)))
        .setInvest_id(cursor.getInt(cursor.getColumnIndex(INVEST_ID)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)));
  }

  private DanaPensiun cursorToDanaPensiun(Cursor cursor) {
    return new DanaPensiun().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setId_dana_pensiun(cursor.getInt(cursor.getColumnIndex(ID_DANA_PENSIUN)))
        .setId_plan(cursor.getInt(cursor.getColumnIndex(PLAN_ID)))
        .setId_plan_local(cursor.getInt(cursor.getColumnIndex(PLAN_ID_LOCAL)))
        .setPendapatan(cursor.getDouble(cursor.getColumnIndex(PENDAPATAN)))
        .setUmur(cursor.getInt(cursor.getColumnIndex(UMUR)))
        .setUmur_pensiun(cursor.getInt(cursor.getColumnIndex(UMUR_PENSIUN)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)));
  }

  private DanaDarurat cursorToDanaDarurat(Cursor cursor) {
    return new DanaDarurat().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setId_dana_darurat(cursor.getInt(cursor.getColumnIndex(ID_DANA_DARURAT)))
        .setId_plan(cursor.getInt(cursor.getColumnIndex(PLAN_ID)))
        .setId_plan_local(cursor.getInt(cursor.getColumnIndex(PLAN_ID_LOCAL)))
        .setPengeluaran_bulanan(cursor.getDouble(cursor.getColumnIndex(PENGELUARAN_BULANAN)))
        .setBulan_penggunaan(cursor.getInt(cursor.getColumnIndex(BULAN_PENGGUNAAN)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)));
  }

  private UserCategory cursorToUserCategory(Cursor cursor) {
    return new UserCategory().setColor(cursor.getString(cursor.getColumnIndex(COLOR_CATEGORY)))
        .setName(cursor.getString(cursor.getColumnIndex(NAME_CATEGORY)))
        .setColorName(cursor.getString(cursor.getColumnIndex(COLOR_NAME)))
        .setId_user_category(cursor.getInt(cursor.getColumnIndex(ID_USER_CATEGORY)))
        .setParentCategoryId(cursor.getInt(cursor.getColumnIndex(ID_CATEGORY)))
        .setUser_id(cursor.getInt(cursor.getColumnIndex(USER_ID)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)));
  }

  private DanaKuliah cursorToDanaKuliah(Cursor cursor) {
    return new DanaKuliah().setId(cursor.getInt(cursor.getColumnIndex(ID)))
        .setId_dana_kuliah(cursor.getInt(cursor.getColumnIndex(ID_DANA_KULIAH)))
        .setId_plan(cursor.getInt(cursor.getColumnIndex(PLAN_ID)))
        .setId_plan_local(cursor.getInt(cursor.getColumnIndex(PLAN_ID_LOCAL)))
        .setLama_kuliah(cursor.getInt(cursor.getColumnIndex(LAMA_KULIAH)))
        .setNama_anak(cursor.getString(cursor.getColumnIndex(NAMA_ANAK)))
        .setBiaya_kuliah(cursor.getDouble(cursor.getColumnIndex(BIAYA_KULIAH)))
        .setUang_saku(cursor.getDouble(cursor.getColumnIndex(BIAYA_UANG_SAKU)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)));
  }

  private Category cursorToCategory(Cursor cursor) {
    return new Category().setId_category(cursor.getInt(cursor.getColumnIndex(ID_CATEGORY)))
        .setName(cursor.getString(cursor.getColumnIndex(NAME_CATEGORY)))
        .setIcon(cursor.getString(cursor.getColumnIndex(ICON_CATEGORY)))
        .setColor(cursor.getString(cursor.getColumnIndex(COLOR_CATEGORY)))
        .setParent(cursor.getString(cursor.getColumnIndex(TYPE_CATEGORY)))
        .setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION_CATEGORY)))
        .setBelong_to(cursor.getString(cursor.getColumnIndex(BELONG_TO_CATEGORY)))
        .setDeleted_at(cursor.getString(cursor.getColumnIndex(DELETED_AT)))
        .setUpdated_at(cursor.getString(cursor.getColumnIndex(UPDATED_AT)))
        .setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)));
  }

  private Vendor cursorToVendor(Cursor cursor) {
    return new Vendor().setId(cursor.getInt(cursor.getColumnIndex(ID_VENDOR)))
        .setVendor_name(cursor.getString(cursor.getColumnIndex(VENDOR_NAME)))
        .setVendor_image(cursor.getString(cursor.getColumnIndex(VENDOR_IMAGE)))
        .setVendor_type(cursor.getString(cursor.getColumnIndex(VENDOR_TYPE)));
  }

  private Invest cursorToInvest(Cursor cursor) {
    return new Invest().setId_ivest(cursor.getInt(cursor.getColumnIndex(ID_INVESTASI)))
        .setInvest_name(cursor.getString(cursor.getColumnIndex(INVESTASI_NAME)))
        .setInvest_procentase(cursor.getDouble(cursor.getColumnIndex(INVESTASI_PROCENTASE)))
        .setVendor_id(cursor.getInt(cursor.getColumnIndex(VENDOR_ID)));
  }
    /*END CURSOR*/

  /*BEGIN USER*/
  public void newUser(User user) {
    ContentValues cv = getContentValuesUser(user);
    getWritableDatabase().insert(TAB_USER, ID_USER, cv);
  }

  public void updateUser(User user, String id_user) {
    ContentValues cv = getContentValuesUser(user);
    getWritableDatabase().update(TAB_USER, cv, ID_USER + "=" + id_user, null);
  }

  public User getUser(String id, String by) {
    Cursor cursor = null;
    if (by.equalsIgnoreCase(TAB_USER)) {
      cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TAB_USER + " WHERE " + ID_USER + " = " + id, null);
    } else if (by.equalsIgnoreCase(TAB_CASH)) {
      cursor = getReadableDatabase().rawQuery("SELECT "
          + TAB_USER
          + ".* FROM "
          + TAB_USER
          + " JOIN "
          + TAB_ACCOUNT
          + " ON "
          + TAB_USER
          + "."
          + ID_USER
          + " = "
          + TAB_ACCOUNT
          + "."
          + USER_ID
          + " JOIN "
          + TAB_PRODUCT
          + " ON "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " = "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " JOIN "
          + TAB_CASH
          + " ON "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " = "
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + " WHERE "
          + TAB_CASH
          + "."
          + ID
          + " = "
          + id, null);
    }

    try {
      if (cursor.moveToFirst()) {
        return cursorToUser(cursor);
      }
    } finally {
      cursor.close();
    }
    return null;
  }

  public boolean isUserEmpty() {
    boolean cek = true;
    Cursor cursor = getReadableDatabase().rawQuery("select * from " + TAB_USER, null);
    try {
      if (cursor.getCount() > 0) {
        cek = false;
      }
    } finally {
      cursor.close();
    }
    return cek;
  }

  public int UpdateUser(User user) {
    ContentValues cv = getContentValuesUser(user);

    int i = getWritableDatabase().update(TAB_USER, //table
        cv, // column/value
        ID_USER + " = ?", // selections
        new String[] { String.valueOf(user.getUser_id()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_USER, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }
    /*END USER*/

  public int UpdateCategory(Category category) {
    ContentValues cv = getContentValuesCategory(category);

    int i = getWritableDatabase().update(TAB_CATEGORY, //table
        cv, // column/value
        ID_CATEGORY + " = ?", // selections
        new String[] { String.valueOf(category.getId_category()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_CATEGORY, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }

  public int UpdateUserCategory(UserCategory userCategory) {
    ContentValues cv = getContentValuesUserCategory(userCategory);

    int i = getWritableDatabase().update(TAB_USER_CATEGORY, //table
        cv, // column/value
        ID_USER_CATEGORY + " = ?", // selections
        new String[] { String.valueOf(userCategory.getId_user_category()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_USER_CATEGORY, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }

  /*BEGIN ALARM*/
  public void newAlarm(Alarm alarm) {
    ContentValues cv = getContentValuesAlarm(alarm);
    getWritableDatabase().insert(TAB_ALARM, ID, cv);
  }

  public void updateAlarm(Alarm alarm) {
    ContentValues cv = getContentValuesAlarm(alarm);
    if (alarm.getId() > 0) {
      getWritableDatabase().update(TAB_ALARM, cv, ID + " = ?",
          new String[] { String.valueOf(alarm.getId()) });
    } else if (alarm.getId_alarm() != -1) {
      getWritableDatabase().update(TAB_ALARM, cv, ALARM_ID + " = ?",
          new String[] { String.valueOf(alarm.getId_alarm()) });
    }

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));
        if (rows == 0) {
          getWritableDatabase().insert(TAB_ALARM, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
  }

  public void updateAlarmByID(Alarm alarm) {
    ContentValues cv = new ContentValues();
    cv.put(ID, alarm.getId());
    cv.put(ALARM_ID, alarm.getId_alarm());
    cv.put(ALARM_ID_USER, alarm.getId_user());
    cv.put(ALARM_ID_DOMPET, alarm.getId_dompet());
    cv.put(ALARM_ID_CATEGORY, alarm.getId_category());
    cv.put(ALARM_DESKRIPSI, alarm.getDeskripsi_alarm());
    cv.put(ALARM_JUMLAH, alarm.getJumlah_alarm());
    cv.put(ALARM_DATE, alarm.getDate_alarm());
    cv.put(ALARM_ACTIVE, alarm.getIs_active());
    cv.put(ALARM_MONTH, alarm.getMonth_alarm());
    if (alarm.getToggle() != null && !alarm.getToggle().equalsIgnoreCase("")) {
      cv.put(ALARM_TOGGLE, alarm.getToggle());
    }
    cv.put(CREATED_AT, alarm.getCreated_at());
    cv.put(UPDATED_AT, alarm.getUpdated_at());
    cv.put(DELETED_AT, alarm.getDeleted_at());

    getWritableDatabase().update(TAB_ALARM, cv, ID + " = ?",
        new String[] { String.valueOf(alarm.getId()) });

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_ALARM, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
  }

  public ArrayList<Alarm> getAlarmTomorrow(int id_user, String msg) {
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf;
    sdf = new SimpleDateFormat("dd");

    String sql = null;
    sql = "SELECT * FROM "
        + TAB_ALARM
        + " WHERE "
        + ALARM_ID_USER
        + " = "
        + id_user
        + " AND "
        + ALARM_ACTIVE
        + " = '0'"
        + " AND "
        + ALARM_TOGGLE
        + " = '1'";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Alarm> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Alarm mModel = cursorToAlarm(cursor);
        if (Integer.valueOf(mModel.getDate_alarm()) == 1) {
          int maxmonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
          int current = Integer.valueOf(sdf.format(c.getTime()));
          if (maxmonth == current) mModels.add(mModel);
        } else {
          int date_alarm = Integer.valueOf(mModel.getDate_alarm()) - 1;
          int current = Integer.valueOf(sdf.format(c.getTime()));
          if (date_alarm == current) mModels.add(mModel);
        }
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Alarm> getAlarmToday(int id_user, String msg) {
    String sql = null;
    sql = "SELECT * FROM "
        + TAB_ALARM
        + " WHERE "
        + ALARM_ID_USER
        + " = "
        + id_user
        + " AND "
        + ALARM_DATE
        + " = '"
        + helper.getCurrentTime(msg)
        + "'"
        + " AND "
        + ALARM_ACTIVE
        + " = '0'"
        + " AND "
        + ALARM_TOGGLE
        + " = '1'";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Alarm> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Alarm mModel = cursorToAlarm(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Alarm> getAlarmYesterday(int id_user, String msg) {
    String sql = null;
    sql = "SELECT * FROM "
        + TAB_ALARM
        + " WHERE "
        + ALARM_ID_USER
        + " = "
        + id_user
        + " AND "
        + ALARM_DATE
        + " = '"
        + helper.getYesterdayTime(msg)
        + "'"
        + " AND "
        + ALARM_ACTIVE
        + " = '0'"
        + " AND "
        + ALARM_TOGGLE
        + " = '1'";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Alarm> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Alarm mModel = cursorToAlarm(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Alarm> getAllAlarm(int id_user) {
    String sql;
    sql = "SELECT * FROM " + TAB_ALARM + " WHERE " + ALARM_ID_USER + " = " + id_user + withoutTrash(
        TAB_ALARM) + " ORDER BY " + ALARM_ACTIVE + " ASC";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Alarm> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Alarm mModel = cursorToAlarm(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public void UpdateActiveAlarm(int id) {
    ContentValues cv = new ContentValues();
    cv.put(ALARM_ACTIVE, "1");
    getWritableDatabase().update(TAB_ALARM, cv, ID + " = ?", new String[] { String.valueOf(id) });
  }

  public void checkAllAlarm(int id_user) {
    SQLiteDatabase db = this.getReadableDatabase();
    int month;
    int id_alarm;

    String selectQuery = "SELECT * FROM " + TAB_ALARM + " WHERE " + ALARM_ID_USER + " = " + id_user;

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          month = cursor.getInt(cursor.getColumnIndex(ALARM_MONTH));
          id_alarm = cursor.getInt(cursor.getColumnIndex(ID));
          if (month != helper.getCurrentMonth()) {
            ContentValues cv = new ContentValues();
            cv.put(ALARM_ACTIVE, "0");
            cv.put(ALARM_MONTH, helper.getCurrentMonth());

            getWritableDatabase().update(TAB_ALARM, //table
                cv, // column/value
                ID + " = ?", // selections
                new String[] { String.valueOf(id_alarm) }); //selection args
          }
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
  }
    /*END ALARM*/

  /*BEGIN VENDOR*/
  public boolean isBank() {
    boolean check = false;
    Cursor cursor = getReadableDatabase().rawQuery("select * from " + TAB_VENDORS + "", null);
    try {
      if (cursor.getCount() > 0) {
        check = true;
      }
    } finally {
      cursor.close();
    }
    return check;
  }

  public void updateBank(Vendor vendors) {
    ContentValues cv = new ContentValues();
    cv.put(ID_VENDOR, vendors.getId());
    cv.put(VENDOR_NAME, vendors.getVendor_name());
    cv.put(VENDOR_IMAGE, vendors.getVendor_image());
    cv.put(VENDOR_TYPE, vendors.getVendor_type());

    getWritableDatabase().update(TAB_VENDORS, cv, ID_VENDOR + " = ?",
        new String[] { String.valueOf(vendors.getId()) });

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_VENDORS, ID_VENDOR, cv);
        }
      }
    } finally {
      cursor.close();
    }
  }

  public String getVendorName(int id, int by) {
    //BY : 0 = ID_USER
    //   : 1 = ID_VENDOR
    //   : 2 = ID_ACCOUNT
    //   : 3 = ID_PRODUCT
    //   : 4 = ID_CASHFLOW
    String vendor_name = "Mandiri";
    Cursor cursor = null;

    if (by == 1) {
      cursor = getReadableDatabase().rawQuery(
          "SELECT * FROM " + TAB_VENDORS + " WHERE " + TAB_VENDORS + "." + ID_VENDOR + " = " + id,
          null);
    } else if (by == 2) {
      cursor = getReadableDatabase().rawQuery("SELECT * FROM "
          + TAB_ACCOUNT
          + " JOIN "
          + TAB_VENDORS
          + " ON "
          + TAB_ACCOUNT
          + "."
          + VENDOR_ID
          + " = "
          + TAB_VENDORS
          + "."
          + ID_VENDOR
          +
          " WHERE "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " = "
          + id, null);
    } else if (by == 3) {
      cursor = getReadableDatabase().rawQuery("SELECT * FROM "
          + TAB_PRODUCT
          + " JOIN "
          + TAB_ACCOUNT
          + " ON "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " = "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " JOIN "
          + TAB_VENDORS
          + " ON "
          + TAB_ACCOUNT
          + "."
          + VENDOR_ID
          + " = "
          + TAB_VENDORS
          + "."
          + ID_VENDOR
          +
          " WHERE "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " = "
          + id, null);
    } else if (by == 4) {
      cursor = getReadableDatabase().rawQuery("SELECT "
          + TAB_VENDORS
          + "."
          + VENDOR_NAME
          + " FROM "
          + TAB_CASH
          + " JOIN "
          + TAB_PRODUCT
          + " ON "
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + " = "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " JOIN "
          + TAB_ACCOUNT
          + " ON "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " = "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " JOIN "
          + TAB_VENDORS
          + " ON "
          + TAB_ACCOUNT
          + "."
          + VENDOR_ID
          + " = "
          + TAB_VENDORS
          + "."
          + ID_VENDOR
          +
          " WHERE "
          + TAB_CASH
          + "."
          + ID
          + " = "
          + id, null);
    }

    try {
      if (cursor.moveToFirst()) {
        vendor_name = cursor.getString(cursor.getColumnIndex(VENDOR_NAME));
        Timber.e("kamu " + vendor_name);
      }
    } finally {
      cursor.close();
    }
    return vendor_name;
  }
    /*END VENDOR*/

  public ArrayList<Cash> getAllCashOffline(String id_user) {
    String sql = null;
    sql = "select "
        + TAB_CASH
        + ".* from "
        + TAB_CASH
        + " join "
        + TAB_PRODUCT
        + " on "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " join "
        + TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " join "
        + TAB_USER
        + " on "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + TAB_USER
        + "."
        + ID_USER
        + " where "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + id_user
        + " AND "
        + TAB_CASH
        + "."
        + UPDATED_AT
        + ">"
        + TAB_USER
        + "."
        + LAST_SYNC_CASH;
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Cash mModel = cursorToCash(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Cash> getAllCashOfflineByAccount(String id_user, int account_id,
      String last_sync) {
    String sql = null;
    sql = "select "
        + TAB_CASH
        + ".* from "
        + TAB_CASH
        + " join "
        + TAB_PRODUCT
        + " on "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " join "
        + TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " join "
        + TAB_USER
        + " on "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + TAB_USER
        + "."
        + ID_USER
        + " where "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + id_user
        + " AND "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + "="
        + account_id
        + " AND DATE("
        + TAB_CASH
        + "."
        + UPDATED_AT
        + ") >= DATE('"
        + last_sync
        + "')";
    Timber.e("QUERY getAllCashOfflineByAccount " + sql);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Cash mModel = cursorToCash(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Budget> getAllBudgetOffline(String id_user, String lastsync) {
    String sql = null;
    sql = "SELECT "
        + TAB_BUDGET
        + ".* FROM "
        + TAB_BUDGET
        + " JOIN "
        + TAB_USER
        + " ON "
        + TAB_BUDGET
        + "."
        + USER_ID
        + " = "
        + TAB_USER
        + "."
        + ID_USER
        + " WHERE "
        + TAB_BUDGET
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND DATE("
        + TAB_BUDGET
        + "."
        + UPDATED_AT
        + ") >= "
        + "DATE('"
        + lastsync
        + "')";
    Timber.e("QUERY getAllBudgetOffline " + sql);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Budget> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Budget mModel = cursorToBudget(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Alarm> getAllAlarmOffline(int user_id, String s_date) {
    String sql = null;
    sql = "SELECT "
        + TAB_ALARM
        + ".* FROM "
        + TAB_ALARM
        + " JOIN "
        + TAB_USER
        + " ON "
        + TAB_ALARM
        + "."
        + USER_ID
        + " = "
        + TAB_USER
        + "."
        + ID_USER
        + " WHERE "
        + TAB_ALARM
        + "."
        + USER_ID
        + " = "
        + user_id
        + " AND DATE("
        + TAB_ALARM
        + "."
        + UPDATED_AT
        + ") >= DATE('"
        + s_date
        + "')";
    Timber.e("sql alarm " + sql);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Alarm> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Alarm mModel = cursorToAlarm(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Plan> getAllPlanOffline(String id_user) {
    String sql = null;
    sql = "SELECT " + TAB_PLAN + ".* FROM " + TAB_PLAN + " WHERE " + USER_ID + "=" + id_user + " ";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Plan> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Plan mModel = cursorToPlan(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Product> getAllDompet(String id_user) {
    String sql = null;
    sql = "SELECT * FROM "
        + TAB_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        +
        " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + " = 6";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Product> products = new ArrayList<Product>();
    Product product;
    try {
      if (cursor.moveToFirst()) {
        do {
          product = cursorToProduct(cursor);
          products.add(product);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return products;
  }

  public ArrayList<Account> getAllDompetAccount(String id_user) {
    String sql = null;
    sql = "SELECT * FROM "
        + TAB_ACCOUNT
        +
        " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + " = 6";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Account> accounts = new ArrayList<>();
    Account account;
    try {
      if (cursor.moveToFirst()) {
        do {
          account = cursorToAccount(cursor);
          accounts.add(account);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return accounts;
  }

  public ArrayList<Product> getAllDompetAndPlan(String id_user) {
    String sql = null;
    sql = "SELECT * FROM "
        + TAB_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        +
        " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + " = 6 OR "
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + " = 9";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Product> products = new ArrayList<Product>();
    Product product;
    try {
      if (cursor.moveToFirst()) {
        do {
          product = cursorToProduct(cursor);
          products.add(product);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return products;
  }

  public ArrayList<Cash> getCashbyDate(String cash_date, String type_mutasi, String id_user) {
    String sql = null;

    if (type_mutasi.equalsIgnoreCase("ALL")) {
      sql = "select c.* from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + "=" + id_user + " AND " +
          "c." + CASHFLOW_DATE + "= '" + cash_date +
          "' order by c." + UPDATED_AT + " desc";
    } else if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql = "select c.* from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + " = " + id_user + " AND " +
          "c." + JENIS_MUTASI + " = '" + type_mutasi + "' AND " +
          "c." + CASHFLOW_DATE + " = '" + cash_date +
          "' order by c." + UPDATED_AT + " desc";
    } else { // TYPE MUTASI HANYA PENAMPUNG NILAI UNTUK CATEGORY ID SAAT TERDAPAT OVERVIEW DI DETAIL ACCOUNTS :)
      sql = "select c.* from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + " = " + id_user + " AND " +
          "c." + CATEGORY_ID + " = " + type_mutasi + " AND " +
          "c." + JENIS_MUTASI + " = 'DB' AND " +
          "c." + CASHFLOW_DATE + " = '" + cash_date +
          "' order by c." + UPDATED_AT + " desc";
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    //System.out.println("DbHelper.getCashbyDate cursor : " + DatabaseUtils.dumpCursorToString(cursor));
    ArrayList<Cash> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Cash mModel = cursorToCash(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public ArrayList<Cash> getCashbyCategory(String category_id, String type_mutasi, String id_user,
      String date_start, String date_end) {
    String sql = null;

    String sql_date = "";
    if (date_start != null && date_end != null) {
      sql_date =
          " AND c." + CASHFLOW_DATE + " BETWEEN '" + date_start + "' AND '" + date_end + "' ";
    }

    String sql_add = withoutTrash(TAB_CASH) + withoutTransfer();
    sql_add = sql_add.replaceAll(TAB_CASH,"c");

    Timber.e("carrisa ghasini "+sql_add);

    if (type_mutasi.equalsIgnoreCase("ALL")) {
      sql = "select c.* from " + TAB_CASH +
          "  join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + "=" + id_user + " AND " +
          "c." + CATEGORY_ID + "= " + category_id +
          sql_date + sql_add +
          " order by c." + UPDATED_AT + " desc";
    } else if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql = "select c.* from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + " = " + id_user + " AND " +
          "c." + JENIS_MUTASI + " = '" + type_mutasi + "' AND " +
          "c." + CATEGORY_ID + " = " + category_id +
          sql_date + sql_add +
          " order by c." + UPDATED_AT + " desc";
    } else { // TYPE MUTASI HANYA PENAMPUNG NILAI UNTUK CATEGORY ID SAAT TERDAPAT OVERVIEW DI DETAIL ACCOUNTS :)
      sql = "select c.* from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + " = " + id_user + " AND " +
          "c." + CATEGORY_ID + " = " + type_mutasi + " AND " +
          "c." + JENIS_MUTASI + " = 'DB' AND " +
          "c." + CATEGORY_ID + " = " + category_id +
          sql_date + sql_add +
          " order by c." + UPDATED_AT + " desc";
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Cash mModel = cursorToCash(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public float getTotalAmountCashByCategory(String category_id, String type_mutasi,
      String id_user) {
    String sql = null;

    if (type_mutasi.equalsIgnoreCase("ALL")) {
      sql = "select IFNULL(SUM(" + AMOUNT + "),0) as total from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + "=" + id_user + " AND " +
          "c." + CATEGORY_ID + "= " + category_id + " order by c." + UPDATED_AT + " desc";
    } else if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql = "select IFNULL(SUM(" + AMOUNT + "),0) as total from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + " = " + id_user + " AND " +
          "c." + JENIS_MUTASI + " = '" + type_mutasi + "' AND " +
          "c." + CATEGORY_ID + " = " + category_id + " order by c." + UPDATED_AT + " desc";
    } else { // TYPE MUTASI HANYA PENAMPUNG NILAI UNTUK CATEGORY ID SAAT TERDAPAT OVERVIEW DI DETAIL ACCOUNTS :)
      sql = "select IFNULL(SUM(" + AMOUNT + "),0) as total from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "a." + USER_ID + " = " + id_user + " AND " +
          "c." + CATEGORY_ID + " = " + type_mutasi + " AND " +
          "c." + JENIS_MUTASI + " = 'DB' AND " +
          "c." + CATEGORY_ID + " = " + category_id + " order by c." + UPDATED_AT + " desc";
    }
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.getCount() > 0) {
        if (cursor.moveToFirst()) {
          float total = cursor.getFloat(cursor.getColumnIndex("total"));
          return total;
        }
      }
    } finally {
      cursor.close();
    }
    return 0;
  }

  public float getTotalAmountCashByCategoryAndDate(String category_id, String type_mutasi,
      String id_user, String start_date, String end_date) {
    String sql = null;
    if (type_mutasi.equalsIgnoreCase("ALL")) {
      sql = "select IFNULL(SUM(" + AMOUNT + "),0) as total from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "c." + CASHFLOW_DATE + " BETWEEN '" + start_date + "' AND '" + end_date + "' " +
          "AND a." + USER_ID + "=" + id_user + " AND " +
          "c." + CATEGORY_ID + "= " + category_id + " order by c." + UPDATED_AT + " desc";
    } else if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql = "select IFNULL(SUM(" + AMOUNT + "),0) as total from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "c." + CASHFLOW_DATE + " BETWEEN '" + start_date + "' AND '" + end_date + "' " +
          "AND a." + USER_ID + " = " + id_user + " AND " +
          "c." + JENIS_MUTASI + " = '" + type_mutasi + "' AND " +
          "c." + CATEGORY_ID + " = " + category_id + " order by c." + UPDATED_AT + " desc";
    } else { // TYPE MUTASI HANYA PENAMPUNG NILAI UNTUK CATEGORY ID SAAT TERDAPAT OVERVIEW DI DETAIL ACCOUNTS :)
      sql = "select IFNULL(SUM(" + AMOUNT + "),0) as total from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "c." + CASHFLOW_DATE + " BETWEEN '" + start_date + "' AND '" + end_date + "' " +
          "AND a." + USER_ID + " = " + id_user + " AND " +
          "c." + CATEGORY_ID + " = " + type_mutasi + " AND " +
          "c." + JENIS_MUTASI + " = 'DB' AND " +
          "c." + CATEGORY_ID + " = " + category_id + " order by c." + UPDATED_AT + " desc";
    }
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.getCount() > 0) {
        if (cursor.moveToFirst()) {
          float total = cursor.getFloat(cursor.getColumnIndex("total"));
          return total;
        }
      }
    } finally {
      cursor.close();
    }
    return 0;
  }

  public ArrayList<Cash> getCashByCategoryAndDate(String category_id, String type_mutasi,
      String id_user, String start_date, String end_date) {
    String sql = null;
    if (type_mutasi.equalsIgnoreCase("ALL")) {
      sql = "select c.* from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "c." + CASHFLOW_DATE + " BETWEEN '" + start_date + "' AND '" + end_date + "' " +
          "AND a." + USER_ID + "=" + id_user + " AND " +
          "c." + CATEGORY_ID + "= " + category_id + " order by c." + UPDATED_AT + " desc";
    } else if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql = "select  c.* from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "c." + CASHFLOW_DATE + " BETWEEN '" + start_date + "' AND '" + end_date + "' " +
          "AND a." + USER_ID + " = " + id_user + " AND " +
          "c." + JENIS_MUTASI + " = '" + type_mutasi + "' AND " +
          "c." + CATEGORY_ID + " = " + category_id + " order by c." + UPDATED_AT + " desc";
    } else { // TYPE MUTASI HANYA PENAMPUNG NILAI UNTUK CATEGORY ID SAAT TERDAPAT OVERVIEW DI DETAIL ACCOUNTS :)
      sql = "select  c.* as total from " + TAB_CASH +
          " c join " + TAB_PRODUCT + " p on c." + PRODUCT_ID + " = p." + ID_PRODUCT +
          " join " + TAB_ACCOUNT + " a on p." + ACCOUNT_ID + " = a." + ID_ACCOUNT + " where " +
          "c." + CASHFLOW_DATE + " BETWEEN '" + start_date + "' AND '" + end_date + "' " +
          "AND a." + USER_ID + " = " + id_user + " AND " +
          "c." + CATEGORY_ID + " = " + type_mutasi + " AND " +
          "c." + JENIS_MUTASI + " = 'DB' AND " +
          "c." + CATEGORY_ID + " = " + category_id + " order by c." + UPDATED_AT + " desc";
    }
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> cashes = new ArrayList<>();
    try {
      if (cursor.getCount() > 0) {
        if (cursor.moveToFirst()) {
          do {
            Cash cash = cursorToCash(cursor);
            cashes.add(cash);
          } while (cursor.moveToNext());
        }
      }
    } finally {
      cursor.close();
    }
    return cashes;
  }

  public boolean checkAccountDompet(String id_user) {
    boolean check = false;
    Cursor cursor = getReadableDatabase().rawQuery(
        "SELECT * FROM " + TAB_PRODUCT + " JOIN " + TAB_ACCOUNT + " ON " +
            TAB_PRODUCT + "." + ACCOUNT_ID + " = " + TAB_ACCOUNT + "." + ID_ACCOUNT + " WHERE " +
            TAB_ACCOUNT + "." + USER_ID + " = " + id_user, null);
    try {
      while (cursor.moveToNext()) {
        if (cursor.getInt(cursor.getColumnIndex(VENDOR_ID)) == 6 && mcrypt.decrypt(
            cursor.getString(cursor.getColumnIndex(NAME_ACCOUNT))).equalsIgnoreCase("Dompet")) {
          check = true;
        }
      }
    } finally {
      cursor.close();
    }
    return check;
  }

  public int getIdDompetByName(String nama_dompet, String id_user) {
    int id_dompet = 0;
    Cursor cursor = getReadableDatabase().rawQuery(
        "SELECT * FROM " + TAB_PRODUCT + " JOIN " + TAB_ACCOUNT + " ON " +
            TAB_PRODUCT + "." + ACCOUNT_ID + " = " + TAB_ACCOUNT + "." + ID_ACCOUNT + " WHERE " +
            TAB_ACCOUNT + "." + USER_ID + " = " + id_user, null);
    try {
      while (cursor.moveToNext()) {
        if (mcrypt.decrypt(cursor.getString(cursor.getColumnIndex(NAME_PRODUCT)))
            .equalsIgnoreCase(nama_dompet)) {
          id_dompet = cursor.getInt(cursor.getColumnIndex(ID_PRODUCT));
        }
      }
    } finally {
      cursor.close();
    }
    return id_dompet;
  }

  public void newAccount(Account account) {
    ContentValues cv = getContentValuesAccount(account);
    getWritableDatabase().insert(TAB_ACCOUNT, ID, cv);
  }

  public void newProduct(Product product) {
    ContentValues cv = getContentValuesProduct(product);
    getWritableDatabase().insert(TAB_PRODUCT, ID, cv);
  }

  public Account getAccountById(int id, int by, int user_id) {
    //BY : 0 = ID_USER
    //   : 1 = ID_VENDOR
    //   : 2 = ID_ACCOUNT
    //   : 3 = ID_PRODUCT
    //   : 4 = ID_CASHFLOW
    //   : 6 = ID_CASHFLOW : ID Primary-key nya
    Account account = null;
    Cursor cursor = null;
    if (by == 1) {
      cursor = getReadableDatabase().rawQuery("select * from "
          + TAB_ACCOUNT
          + " where "
          + VENDOR_ID
          + "="
          + id
          + " AND "
          + USER_ID
          + "="
          + user_id, null);
    } else if (by == 2) {
      cursor = getReadableDatabase().rawQuery(
          "select * from " + TAB_ACCOUNT + " where " + ID_ACCOUNT + "=" + id + " ", null);
    } else if (by == 3) {
      cursor = getReadableDatabase().rawQuery("select * from "
          + TAB_ACCOUNT
          + " JOIN "
          + TAB_PRODUCT
          + " ON "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " = "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " where "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + "="
          + id, null);
    } else if (by == 4) {
      cursor = getReadableDatabase().rawQuery("select * from "
          + TAB_ACCOUNT
          + " JOIN "
          + TAB_PRODUCT
          + " ON "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " = "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " JOIN "
          + TAB_CASH
          + " ON "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " = "
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + " where "
          + TAB_CASH
          + "."
          + CASHFLOW_ID
          + " = "
          + id, null);
    } else if (by == 6) {
      cursor = getReadableDatabase().rawQuery("select * from "
          + TAB_ACCOUNT
          + " JOIN "
          + TAB_PRODUCT
          + " ON "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " = "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " JOIN "
          + TAB_CASH
          + " ON "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " = "
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + " where "
          + TAB_CASH
          + "."
          + ID
          + " = "
          + id, null);
    }
    try {
      if (cursor.moveToFirst()) {
        account = cursorToAccount(cursor);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return account;
  }

  public int UpdateProduct(Product product) {
    Random rand = new Random();
    ContentValues cv = new ContentValues();
    cv.put(ID_PRODUCT, product.getId_product());
    cv.put(ACCOUNT_ID, product.getAccount_id());
    cv.put(BALANCE, product.getBalance());
    cv.put(CREATED_AT, product.getCreated_at());
    cv.put(UPDATED_AT, product.getUpdated_at());
    cv.put(DELETED_AT, product.getDeleted_at());
    cv.put(NUMBER, product.getNumber());
    cv.put(NAME_PRODUCT, product.getName());
    Timber.e("UpdateProduct getId_product " + product.getId_product());
    if (product.getProperties() != null) {
      cv.put(PROPERTIES, product.getProperties());
    }
    cv.put(TYPE, product.getType());

    Cursor cursor_before = getReadableDatabase().rawQuery("SELECT "
        + TAB_PRODUCT
        + "."
        + UPDATED_AT
        + " FROM "
        + TAB_PRODUCT
        + " WHERE "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + "="
        + product.getId_product(), null);

    if (cursor_before.moveToFirst()) {
      try {
        Timber.e("column pertama " + cursor_before.getString(0));
        Date date_before =
            df.parse(cursor_before.getString(cursor_before.getColumnIndex(UPDATED_AT)));
        Date date_after = df.parse(product.getUpdated_at());
        Timber.e("date before " + date_before.getTime());
        Timber.e("date after " + date_after.getTime());
        if (date_before.getTime() <= date_after.getTime()) {
          Timber.e("UpdateProduct olele " + cv);
          int i = getWritableDatabase().update(TAB_PRODUCT, //table
              cv, // column/value
              ID_PRODUCT + " = ?", // selections
              new String[] { String.valueOf(product.getId_product()) }); //selection args

          Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

          try {
            if (cursor.moveToFirst()) {
              int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));
              if (rows == 0) {
                int color = rand.nextInt(14);
                cv.put(COLOR, color);
                getWritableDatabase().insert(TAB_PRODUCT, ID, cv);
              }
            }
          } finally {
            cursor.close();
          }
          return i;
        }
      } catch (Exception e) {
        Timber.e("ERROR UpdateProduct " + e);
      }
    } else {
      Timber.e("else tidak");
    }
    return -1;
  }

  public int UpdateAccount(Account account) {
    ContentValues cv = new ContentValues();
    cv.put(ID_ACCOUNT, account.getIdaccount());
    cv.put(USER_ID, account.getIduser());
    cv.put(USERNAME_ACCOUNT, account.getUsername());
    cv.put(NAME_ACCOUNT,account.getName());
    cv.put(CREATED_AT, account.getCreated_at());
    cv.put(UPDATED_AT, account.getUpdated_at());
    cv.put(VENDOR_ID, account.getIdvendor());
    cv.put(LOGIN_STATUS, account.getLogin_status());
    cv.put(LOGIN_INFO, account.getLogin_info());
    if (account.getProperties() != null) {
      cv.put(PROPERTIES, account.getProperties());
    }

    int i = getWritableDatabase().update(TAB_ACCOUNT, //table
        cv, // column/value
        ID_ACCOUNT + " = ?", // selections
        new String[] { String.valueOf(account.getIdaccount()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          cv.put(NAME_ACCOUNT, account.getName());
          getWritableDatabase().insert(TAB_ACCOUNT, ID, cv);
        }
      }
    } catch (Exception e) {
      Timber.e("ERROR UpdateAccount" + e);
    } finally {
      cursor.close();
    }
    return i;
  }

  public boolean CheckProduct(int product_id) {

    boolean check = false;
    Cursor cursor = getReadableDatabase().rawQuery(
        "select * from " + TAB_PRODUCT + " WHERE " + ID_PRODUCT + "=" + product_id, null);
    try {
      if (cursor.getCount() > 0) {
        check = true;
      }
    } catch (Exception e) {
      Timber.e("ERROR CheckProduct " + e);
    } finally {
      cursor.close();
    }
    return check;
  }

  public boolean CheckAccount(int id_account) {

    boolean check = false;
    String query = "select * from " + TAB_ACCOUNT + " WHERE " + ID_ACCOUNT + " = " + id_account;
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    try {
      if (cursor.getCount() > 0) {
        check = true;
      }
    } finally {
      cursor.close();
    }
    return check;
  }

  public ArrayList<Category> getAllCategory() {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Category> listModel = new ArrayList<Category>();
    String selectQuery = null;

    selectQuery = "SELECT * FROM " + TAB_CATEGORY + " WHERE " + ID_CATEGORY + " != 0";

    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          Category category = cursorToCategory(cursor);
          listModel.add(category);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }

    return listModel;
  }

  public ArrayList<Category> getAllCategoryByType(String id_user, String type) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Category> listModel = new ArrayList<Category>();
    String selectQuery = null;
    String typeToString;

    if (type.equalsIgnoreCase("0") || type.equalsIgnoreCase("DB")) {
      typeToString = "expense";
    } else if (type.equalsIgnoreCase("1") || type.equalsIgnoreCase("CR")) {
      typeToString = "income";
    } else {
      typeToString = "transfer";
    }

    selectQuery = "SELECT * FROM "
        + TAB_CATEGORY
        + " WHERE "
        + ID_CATEGORY
        + "!= 0 AND "
        + BELONG_TO_CATEGORY
        + " = '"
        + typeToString
        + "'";

    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          Category category = cursorToCategory(cursor);
          listModel.add(category);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }

    return listModel;
  }

  public ArrayList<Category> getMostCategory(String id_user, String type) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Category> listModel = new ArrayList<Category>();
    String selectQuery = null;
    String cashQuery = null;
    String typeToString;

    if (type.equalsIgnoreCase("0") || type.equalsIgnoreCase("DB")) {
      type = "DB";
      typeToString = "expense";
    } else {
      type = "CR";
      typeToString = "income";
    }
    cashQuery =
        "SELECT "
            + CATEGORY_ID
            + ",COUNT("
            + CATEGORY_ID
            + ") as jumlah FROM "
            + TAB_CASH
            + " "
            +
            "JOIN "
            + TAB_PRODUCT
            + " ON ("
            + TAB_CASH
            + "."
            + PRODUCT_ID
            + "="
            + TAB_PRODUCT
            + "."
            + ID_PRODUCT
            + ") "
            +
            "JOIN "
            + TAB_ACCOUNT
            + " ON ("
            + TAB_PRODUCT
            + "."
            + ACCOUNT_ID
            + "="
            + TAB_ACCOUNT
            + "."
            + ID_ACCOUNT
            + ") "
            +
            "WHERE "
            + CATEGORY_ID
            + "!= 0 AND "
            + JENIS_MUTASI
            + "='"
            + type
            + "' AND "
            + TAB_ACCOUNT
            + "."
            + USER_ID
            + "="
            + id_user
            + " "
            +
            "ORDER BY jumlah DESC "
            +
            "LIMIT 5";

    selectQuery = "SELECT "
        + TAB_CATEGORY
        + ".* FROM "
        + TAB_CATEGORY
        + " JOIN ("
        + cashQuery
        + ") most ON (most."
        + CATEGORY_ID
        + "="
        + TAB_CATEGORY
        + "."
        + ID_CATEGORY
        + ")"
        + " WHERE "
        + USER_ID
        + " = "
        + id_user
        + " OR "
        + USER_ID
        + " = 0"
        + " AND "
        + ID_CATEGORY
        + " != 0"
        + " AND "
        + BELONG_TO_CATEGORY
        + " = '"
        + typeToString
        + "'";

    Cursor cursorCat = db.rawQuery(selectQuery, null);
    try {
      if (cursorCat.moveToFirst()) {
        do {
          Category category = cursorToCategory(cursorCat);
          listModel.add(category);
        } while (cursorCat.moveToNext());
      }
    } finally {
      cursorCat.close();
    }

    return listModel;
  }

  public int updateCashByID(Cash cash) {
    ContentValues cv = new ContentValues();
    cv.put(ID, cash.getId());
    cv.put(CASHFLOW_ID, cash.getCash_id());
    cv.put(PRODUCT_ID, cash.getProduct_id());
    cv.put(CASHFLOW_NAME, cash.getDescription());
    cv.put(CASHFLOW_TAG, cash.getCash_tag().toLowerCase());
    cv.put(CASHFLOW_NOTE, cash.getNote());
    cv.put(CASHFLOW_DATE, cash.getCash_date());
    cv.put(CASHFLOW_RENAME, cash.getCashflow_rename());
    cv.put(CATEGORY_ID, cash.getCategory_id());
    cv.put(HARI, cash.getHari());
    cv.put(TANGGAL, cash.getTanggal());
    cv.put(BULAN, cash.getBulan());
    cv.put(TAHUN, cash.getTahun());
    cv.put(AMOUNT, cash.getAmount());
    cv.put(JENIS_MUTASI, cash.getType());
    cv.put(CREATED_AT, cash.getCreated_at());
    cv.put(UPDATED_AT, cash.getUpdated_at());
    cv.put(DELETED_AT, cash.getDeleted_at());
    cv.put(STATUS, cash.getStatus());

    int i = getWritableDatabase().update(TAB_CASH, //table
        cv, // column/value
        ID + " = ?", // selections
        new String[] { String.valueOf(cash.getId()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_CASH, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }

  public synchronized void markAllCashToReadStatus() {
    new AsyncTask<String, String, String>() {
      @Override protected String doInBackground(String... strings) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "read");
        int i = getWritableDatabase().update(TAB_CASH, //table
            cv, // column/value
            STATUS + " = 'unread'", // selections
            null); //selection args
        return null;
      }
    }.execute();
  }

  public void newTransaction(Cash cash) {
    ContentValues values = getContentValuesCash(cash);
    Timber.e("newTransaction " + values);
    getWritableDatabase().insert(TAB_CASH, ID, values);
  }

  public Double getTotalAmountCurrentMonthByTypeMutation(String id, String type) {
    //BY : 0 = ID_USER
    //   : 1 = ID_VENDOR
    //   : 2 = ID_ACCOUNT
    //   : 3 = ID_PRODUCT
    //   : 4 = ID_CASHFLOW
    SQLiteDatabase db = this.getReadableDatabase();
    float saldo = 0;
    double total = 0.0;
    String selectQuery = null;
    selectQuery = "SELECT * FROM "
        + TAB_CASH
        + " JOIN "
        + TAB_PRODUCT
        + " ON "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id
        + " AND "
        + TAB_CASH
        + "."
        + BULAN
        + " = "
        + helper.getCurrentMonth()
        + " AND "
        + TAB_CASH
        + "."
        + JENIS_MUTASI
        + " = '"
        + type
        + "'";

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          saldo = cursor.getFloat(cursor.getColumnIndex(AMOUNT));
          total += saldo;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return total;
  }

  public Double getTotalAmountAlarm(String id) {
    SQLiteDatabase db = this.getReadableDatabase();
    float saldo = 0;
    double total = 0.0;
    String selectQuery = null;
    selectQuery =
        "SELECT * FROM " + TAB_ALARM + " WHERE " + ALARM_ID_USER + " = " + id + withoutTrash(
            TAB_ALARM);

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          saldo = cursor.getFloat(cursor.getColumnIndex(ALARM_JUMLAH));
          total += saldo;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return total;
  }

  public Double getTotalSaldo(String id, int by) {
    //BY : 0 = ID_USER
    //   : 1 = ID_VENDOR
    //   : 2 = ID_ACCOUNT
    //   : 3 = ID_PRODUCT
    //   : 4 = ID_CASHFLOW
    SQLiteDatabase db = this.getReadableDatabase();
    float saldo = 0;
    double total = 0.0;
    String selectQuery = null;

    if (by == 0) {
      selectQuery = "SELECT * FROM "
          + TAB_PRODUCT
          + " JOIN "
          + TAB_ACCOUNT
          + " ON "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " = "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " WHERE "
          + TAB_ACCOUNT
          + "."
          + USER_ID
          + " = "
          + id
          + " AND "
          + TAB_ACCOUNT
          + "."
          + VENDOR_ID
          + " != 9";
    } else if (by == 2) {
      selectQuery = "SELECT * FROM "
          + TAB_PRODUCT
          + " JOIN "
          + TAB_ACCOUNT
          + " ON "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " = "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " WHERE "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " = "
          + id;
    } else if (by == 3) {
      selectQuery =
          "SELECT * FROM " + TAB_PRODUCT + " WHERE " + TAB_PRODUCT + "." + ID_PRODUCT + " = " + id;
    }

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          saldo = cursor.getFloat(cursor.getColumnIndex(BALANCE));
          total += saldo;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return total;
  }

  public Double getTotalSaldoCashflowByIDCategoryCurrentMonth(int id, String type_mutasi) {
    SQLiteDatabase db = this.getReadableDatabase();
    float saldo = 0;
    double total = 0.0;
    String selectQuery = null;

    selectQuery = "SELECT * FROM "
        + TAB_CASH
        + " JOIN "
        + TAB_CATEGORY
        + " ON "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + " = "
        + TAB_CATEGORY
        + "."
        + ID_CATEGORY
        + " WHERE "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + " = "
        + id
        + " AND "
        + TAB_CASH
        + "."
        + BULAN
        + " = "
        + helper.getCurrentMonth()
        + " AND "
        + TAB_CASH
        + "."
        + JENIS_MUTASI
        + " = '"
        + type_mutasi
        + "'";

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          saldo = cursor.getFloat(cursor.getColumnIndex(AMOUNT));
          total += saldo;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return total;
  }

  public Double getTotalSaldoCashflowByJenisMutasi(String user_id, String type_mutasi,
      Integer month, Integer year,Integer account_id) {
    SQLiteDatabase db = this.getReadableDatabase();
    float saldo = 0;
    double total = 0.0;
    String selectQuery = null;

    String sql_filter_bulan_tahun = "";
    if (month != null && year != null) {
      sql_filter_bulan_tahun = " AND strftime('%Y',"
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + ") = '"
          + year
          + "' AND CAST(strftime('%m',"
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + ") as decimal)="
          + month
          + withoutTransfer()
          + "";
    }


    String sql_account = "";
    if(account_id != null){
      sql_account = " AND "+TAB_ACCOUNT+"."+ID_ACCOUNT+"="+account_id;
    }

    selectQuery = "SELECT * FROM "
        + TAB_CASH
        + " JOIN "
        + TAB_CATEGORY
        + " ON "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + " = "
        + TAB_CATEGORY
        + "."
        + ID_CATEGORY
        + " "
        + generatequerybyuserid(user_id)
        + " AND "
        + TAB_CASH
        + "."
        + JENIS_MUTASI
        + " = '"
        + type_mutasi
        + "'"
        + sql_account
        + sql_filter_bulan_tahun;


    Cursor cursor = db.rawQuery(selectQuery, null);

    Timber.e("sql " + selectQuery);

    try {
      if (cursor.moveToFirst()) {
        do {
          saldo = cursor.getFloat(cursor.getColumnIndex(AMOUNT));
          total += saldo;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return total;
  }

  public Double getTotalSaldoDompet(String id, String date) {
    SQLiteDatabase db = this.getReadableDatabase();
    float saldo = 0;
    double total = 0.0;
    String selectQuery = null;

    selectQuery = "SELECT "
        + TAB_CASH
        + "."
        + AMOUNT
        + ", "
        + TAB_ACCOUNT
        + "."
        + NAME_ACCOUNT
        + " FROM "
        + TAB_CASH
        + " JOIN "
        + TAB_PRODUCT
        + " ON "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id
        + " AND "
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + " = '"
        + date
        + "'";

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          if (mcrypt.decrypt(cursor.getString(cursor.getColumnIndex(NAME_ACCOUNT)))
              .equalsIgnoreCase("Dompet")) {
            saldo = cursor.getFloat(cursor.getColumnIndex(AMOUNT));
            total += saldo;
          }
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return total;
  }

  public void updateSaldo(int id_produk, float amount, String type_mutasi) {
    double total = 0.0;
    Cursor cursor = getReadableDatabase().rawQuery(
        "select * from " + TAB_PRODUCT + " where " + ID_PRODUCT + "=" + id_produk, null);
    try {
      if (cursor.moveToFirst()) {
        if (type_mutasi.equalsIgnoreCase("CR")) {
          total = cursor.getDouble(cursor.getColumnIndex(BALANCE)) + amount;
        } else {
          total = cursor.getDouble(cursor.getColumnIndex(BALANCE)) - amount;
        }
      }
    } finally {
      cursor.close();
    }

    ContentValues cv = new ContentValues();
    cv.put(BALANCE, total);

    getWritableDatabase().update(TAB_PRODUCT, //table
        cv, // column/value
        ID_PRODUCT + " = ?", // selections
        new String[] { String.valueOf(id_produk) }); //selection args
    Timber.e("update " + id_produk + " saldo " + amount + " type mutasi " + type_mutasi);
  }

  public void updateSaldo(int id_produk, double amount) {
    ContentValues cv = new ContentValues();
    cv.put(BALANCE, amount);

    getWritableDatabase().update(TAB_PRODUCT, //table
        cv, // column/value
        ID_PRODUCT + " = ?", // selections
        new String[] { String.valueOf(id_produk) }); //selection args
  }

  public double getSaldoReal(int id_product, int user_id) {
    String sql_cr = "select SUM("
        + AMOUNT
        + ") as total from "
        + TAB_CASH
        + " WHERE "
        + JENIS_MUTASI
        + "='CR' AND "
        + PRODUCT_ID
        + "="
        + id_product
        + " "
        + withoutTrash(TAB_CASH);
    String sql_db = "select SUM("
        + AMOUNT
        + ") as total from "
        + TAB_CASH
        + " WHERE "
        + JENIS_MUTASI
        + "='DB' AND "
        + PRODUCT_ID
        + "="
        + id_product
        + " "
        + withoutTrash(TAB_CASH);
    Cursor c_cr = getReadableDatabase().rawQuery(sql_cr, null);
    Cursor c_db = getReadableDatabase().rawQuery(sql_db, null);
    double total = 0;
    try {
      if (c_cr.moveToFirst()) {
        total += c_cr.getDouble(c_cr.getColumnIndex("total"));
      }
      if (c_db.moveToFirst()) {
        total -= c_db.getDouble(c_db.getColumnIndex("total"));
      }
    } finally {
      c_cr.close();
      c_db.close();
    }
    return total;
  }

  public void updateSaldoAfterEditTransaction(int id_produk, float amount, String type_mutasi) {
    double total = 0.0;
    Cursor cursor = getReadableDatabase().rawQuery(
        "select * from " + TAB_PRODUCT + " where " + ID_PRODUCT + "=" + id_produk, null);
    try {
      if (cursor.moveToFirst()) {
        if (type_mutasi.equalsIgnoreCase("CR")) {
          total = cursor.getDouble(cursor.getColumnIndex(BALANCE)) - amount;
        } else {
          total = cursor.getDouble(cursor.getColumnIndex(BALANCE)) + amount;
        }
      }
    } finally {
      cursor.close();
    }

    ContentValues cv = new ContentValues();
    cv.put(BALANCE, total);

    getWritableDatabase().update(TAB_PRODUCT, //table
        cv, // column/value
        ID_PRODUCT + " = ?", // selections
        new String[] { String.valueOf(id_produk) }); //selection args
  }

  public ArrayList<Cash> getAllCashDistincDate(String id_user, String type_mutasi, int limit,
      int countlist) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Cash> listModel = new ArrayList<Cash>();
    String selectQuery = null;

    if (type_mutasi.equalsIgnoreCase("ALL")) {
      selectQuery = "SELECT "
          + TAB_CASH
          + "."
          + ID
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_ID
          + ","
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_NAME
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_TAG
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_RENAME
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_NOTE
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + ","
          + TAB_CASH
          + "."
          + CATEGORY_ID
          + ","
          + TAB_CASH
          + "."
          + HARI
          + ","
          + TAB_CASH
          + "."
          + TANGGAL
          + ","
          + TAB_CASH
          + "."
          + BULAN
          + ","
          + TAB_CASH
          + "."
          + TAHUN
          + ","
          + TAB_CASH
          + "."
          + AMOUNT
          + ","
          + TAB_CASH
          + "."
          + JENIS_MUTASI
          + ","
          + TAB_CASH
          + "."
          + CREATED_AT
          + ","
          + TAB_CASH
          + "."
          + UPDATED_AT
          + ","
          + TAB_CASH
          + "."
          + DELETED_AT
          + ","
          + TAB_CASH
          + "."
          + STATUS
          + " FROM "
          + TAB_CASH
          + " JOIN "
          + TAB_PRODUCT
          + " ON "
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + " = "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " JOIN "
          + TAB_ACCOUNT
          + " ON "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " = "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " WHERE "
          + TAB_ACCOUNT
          + "."
          + USER_ID
          + " = "
          + id_user
          + " GROUP BY "
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + " ORDER BY "
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + " DESC limit "
          + limit
          + " offset "
          + countlist;
    } else {
      selectQuery = "SELECT "
          + TAB_CASH
          + "."
          + ID
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_ID
          + ","
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_NAME
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_TAG
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_RENAME
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_NOTE
          + ","
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + ","
          + TAB_CASH
          + "."
          + CATEGORY_ID
          + ","
          + TAB_CASH
          + "."
          + HARI
          + ","
          + TAB_CASH
          + "."
          + TANGGAL
          + ","
          + TAB_CASH
          + "."
          + BULAN
          + ","
          + TAB_CASH
          + "."
          + TAHUN
          + ","
          + TAB_CASH
          + "."
          + AMOUNT
          + ","
          + TAB_CASH
          + "."
          + JENIS_MUTASI
          + ","
          + TAB_CASH
          + "."
          + CREATED_AT
          + ","
          + TAB_CASH
          + "."
          + UPDATED_AT
          + ","
          + TAB_CASH
          + "."
          + DELETED_AT
          + ","
          + TAB_CASH
          + "."
          + STATUS
          + " FROM "
          + TAB_CASH
          + " JOIN "
          + TAB_PRODUCT
          + " ON "
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + " = "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " JOIN "
          + TAB_ACCOUNT
          + " ON "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " = "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " WHERE "
          + TAB_ACCOUNT
          + "."
          + USER_ID
          + " = "
          + id_user
          + " AND "
          + TAB_CASH
          + "."
          + JENIS_MUTASI
          + " = '"
          + type_mutasi
          + "' GROUP BY "
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + " ORDER BY "
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + " DESC limit "
          + limit
          + " offset "
          + countlist;
    }

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          listModel.add(cash);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }

    return listModel;
  }

  public ArrayList<Cash> getAllCashDistincDateForBudget(String id_user, String type_mutasi,
      int id_category, String date_start, String date_end, int limit, int countlist) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Cash> listModel = new ArrayList<Cash>();
    String selectQuery = null;

    selectQuery = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " JOIN "
        + TAB_PRODUCT
        + " ON "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + " = "
        + id_category
        + " AND "
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + " >= '"
        + date_start
        + "'"
        + " AND "
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + " <= '"
        + date_end
        + "'"
        + " AND "
        + TAB_CASH
        + "."
        + JENIS_MUTASI
        + " = '"
        + type_mutasi
        + "' GROUP BY "
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + " ORDER BY "
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + " DESC limit "
        + limit
        + " offset "
        + countlist;

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          listModel.add(cash);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }

    return listModel;
  }

  public ArrayList<Cash> getCashflowLastMonth(String id_user, Integer year, Integer month,Integer account_id) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Cash> listModel = new ArrayList<Cash>();
    String selectQuery = null;
    String sql_filter_bulan_tahun = "";

    if (month != null && year != null) {
      sql_filter_bulan_tahun = " AND strftime('%Y',"
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + ") = '"
          + year
          + "' AND CAST(strftime('%m',"
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + ") as decimal)="
          + month
          + withoutTransfer()
          + "";
    }

    String sql_account = "";
    if(account_id != null){
      sql_account = " AND "+TAB_ACCOUNT+"."+ID_ACCOUNT+"="+account_id;
    }

    selectQuery = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        +
        "JOIN "
        + TAB_ACCOUNT
        + " ON ("
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + ") "
        +
        "JOIN "
        + TAB_PRODUCT
        + " ON ("
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " = "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + ") "
        +
        "WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + id_user
        + " "
        +sql_account
        + withoutTrash(TAB_CASH)
        + sql_filter_bulan_tahun
        +
        " ORDER BY "
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + " DESC, "
        + TAB_CASH
        + "."
        + ID
        + " DESC";
    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          Account account = getAccountById(cash.getProduct_id(), 3, Integer.valueOf(id_user));
          account.setName(mcrypt.decrypt(account.getName()));
          cash.setAccount(account);
          listModel.add(cash);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return listModel;
  }

  public ArrayList<Cash> get3CashflowLast(String id_user,Integer account_id,int limit) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<Cash> listModel = new ArrayList<Cash>();
    String selectQuery = null;

    String sql_account = "";
    if(account_id != null){
      sql_account = " AND "+TAB_ACCOUNT+"."+ID_ACCOUNT+"="+account_id;
    }

    selectQuery = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        +
        "JOIN "
        + TAB_ACCOUNT
        + " ON ("
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + ") "
        +
        "JOIN "
        + TAB_PRODUCT
        + " ON ("
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " = "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + ") "
        +
        "WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + id_user
        + " "
        + sql_account
        + withoutTrash(TAB_CASH)
        + withoutTransfer()
        +
        "ORDER BY "
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + " DESC, "
        + TAB_CASH
        + "."
        + ID
        + " DESC LIMIT "
        + limit;
    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          Account account = getAccountById(cash.getProduct_id(), 3, Integer.valueOf(id_user));
          account.setName(mcrypt.decrypt(account.getName()));
          cash.setAccount(account);
          listModel.add(cash);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return listModel;
  }

  public float getCashTotalbyDate(ArrayList<Cash> data) {
    float total = 0;
    for (int i = 0; i < data.size(); i++) {
      if (data.get(i).getType().equalsIgnoreCase("CR")) {
        total += data.get(i).getAmount();
      } else {
        total -= data.get(i).getAmount();
      }
    }
    return total;
  }

  public Category getCategoryByID(int id_category) {
    Category category = new Category();
    String query = "SELECT * FROM " + TAB_CATEGORY + " WHERE " + ID_CATEGORY + " = " + id_category;
    Cursor cursor = getReadableDatabase().rawQuery(query, null);

    try {
      if (cursor.moveToFirst()) {
        category = cursorToCategory(cursor);
      }
    } finally {
      cursor.close();
    }

    return category;
  }

  public UserCategory getUserCategoryByID(int id_user_category) {
    UserCategory userCategory = new UserCategory();
    String query = "SELECT * FROM "
        + TAB_USER_CATEGORY
        + " WHERE "
        + ID_USER_CATEGORY
        + " = "
        + id_user_category;
    Cursor cursor = getReadableDatabase().rawQuery(query, null);

    try {
      if (cursor.moveToFirst()) {
        userCategory = cursorToUserCategory(cursor);
      }
    } finally {
      cursor.close();
    }

    return userCategory;
  }

  public Category getCategoryByIcon(String icon) {
    Category category = new Category();
    String query =
        "SELECT * FROM " + TAB_CATEGORY + " WHERE " + ICON_CATEGORY + " = '" + icon + "'";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);

    try {
      if (cursor.moveToFirst()) {
        category = cursorToCategory(cursor);
      }
    } finally {
      cursor.close();
    }

    return category;
  }

  public ArrayList<Account> getAllAccountByUser(String id_user) {
    ArrayList<Account> accounts = new ArrayList<Account>();
    Account account = null;
    String not_in_vendor = "";
    if (!MyCustomApplication.showAllVendor()) {
      not_in_vendor =
          " AND " + TAB_VENDORS + "." + ID_VENDOR + " not in (" + Helper.generateCommaString(
              MyCustomApplication.invalidVendor()) + ")";
    }
    String sql = "select "
        + TAB_ACCOUNT
        + ".* from "
        + TAB_ACCOUNT
        + " join "
        + TAB_VENDORS
        + " on ("
        + TAB_VENDORS
        + "."
        + ID_VENDOR
        + "="
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + ") where "
        + USER_ID
        + "="
        + id_user
        + " "
        + not_in_vendor;
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    Timber.e("Heloo " + sql);
    try {
      if (cursor.moveToFirst()) {
        do {
          account = cursorToAccount(cursor);
          account.products = getAllProductByAccount(account.getIdaccount());
          accounts.add(account);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return accounts;
  }

  public ArrayList<Account> getAllAccountByUserExceptDompet(String id_user) {
    ArrayList<Account> accounts = new ArrayList<Account>();
    Account account;
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_ACCOUNT
        + " where "
        + USER_ID
        + "="
        + id_user
        + " AND "
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + " != 6", null);
    try {
      if (cursor.moveToFirst()) {
        do {
          account = cursorToAccount(cursor);
          accounts.add(account);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return accounts;
  }

  public ArrayList<Product> getAllProductByUser(String id_user) {
    ArrayList<Product> products = new ArrayList<Product>();
    Product product;
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          product = cursorToProduct(cursor);
          products.add(product);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return products;
  }

  public ArrayList<Product> getAllProductByUserExceptPlan(String id_user) {
    ArrayList<Product> products = new ArrayList<Product>();
    Product product;
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + " != 9", null);
    try {
      if (cursor.moveToFirst()) {
        do {
          product = cursorToProduct(cursor);
          products.add(product);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return products;
  }

  public ArrayList<Product> getAllProductByAccount(int id_account) {
    ArrayList<Product> products = new ArrayList<Product>();
    Product product;
    Cursor cursor = getReadableDatabase().rawQuery("select "
        + TAB_PRODUCT
        + ".* from "
        + TAB_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + id_account
        + " ORDER BY "
        + VENDOR_ID
        + " DESC", null);
    try {
      if (cursor.moveToFirst()) {
        do {
          product = cursorToProduct(cursor);
          products.add(product);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return products;
  }

  public int getIDCOLOR(int id, int by) {
    //BY : 0 = ID_USER
    //   : 1 = ID_VENDOR
    //   : 2 = ID_ACCOUNT
    //   : 3 = ID_PRODUCT
    //   : 4 = ID_CASHFLOW
    Cursor cursor = null;
    int color = 0;

    if (by == 2) {
      cursor = getReadableDatabase().rawQuery(
          "select * from " + TAB_PRODUCT + " WHERE " + TAB_PRODUCT + "." + ACCOUNT_ID + " = " + id,
          null);
    } else if (by == 3) {
      cursor = getReadableDatabase().rawQuery(
          "select * from " + TAB_PRODUCT + " where " + ID_PRODUCT + "=" + id, null);
    } else if (by == 4) {
      cursor = getReadableDatabase().rawQuery("select * from "
          + TAB_PRODUCT
          + " JOIN "
          + TAB_CASH
          + " ON "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " = "
          + TAB_CASH
          + "."
          + PRODUCT_ID
          + " WHERE "
          + TAB_CASH
          + "."
          + ID
          + "="
          + id, null);
    }

    try {
      if (cursor.moveToFirst()) {
        color = cursor.getInt(cursor.getColumnIndex(COLOR));
        return color;
      }
    } finally {
      cursor.close();
    }
    return color;
  }

  public String withoutTrash(String table_name) {
    return " AND ("
        + table_name
        + "."
        + DELETED_AT
        + " IS NULL OR "
        + table_name
        + "."
        + DELETED_AT
        + " = 'null')";
  }

  public String withoutTransfer() {
    String sql_tf = "select * from "+TAB_CATEGORY+" where "+TAB_CATEGORY+"."+BELONG_TO_CATEGORY+" = 'transfer'";
    Cursor cursor = getReadableDatabase().rawQuery(sql_tf,null);
    ArrayList<Integer> ids = new ArrayList<>();
    try {
      if(cursor.moveToFirst()){
        do {
          Category category = cursorToCategory(cursor);
          ids.add(category.getId_category());
        }while (cursor.moveToNext());
      }
    }finally {
      cursor.close();
    }
    if(ids.size()>0) {
      Integer[] array_ids = ids.toArray(new Integer[0]);
      String s_ids = Helper.imploded(array_ids,",");
      return " AND " + TAB_CASH + "." + CATEGORY_ID + "  NOT IN  ("+s_ids+")  ";
    }else{
      return "";
    }
  }

  //get budget of user
  public ArrayList<Budget> getAllBudget(String user_id) {
    ArrayList<Budget> mListModel = new ArrayList<Budget>();
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT * FROM "
        + TAB_BUDGET
        + " WHERE "
        + TAB_BUDGET
        + "."
        + USER_ID
        + " = "
        + user_id
        + withoutTrash(TAB_BUDGET)
        + " ORDER BY "
        + CREATED_AT
        + " DESC";

    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          Budget model = cursorToBudget(cursor);
          mListModel.add(model);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return mListModel;
  }

  public ArrayList<Budget> getAllBudgetGroupByDate(String id) {
    ArrayList<Budget> mListModel = new ArrayList<Budget>();
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT * FROM "
        + TAB_BUDGET
        + " WHERE "
        + TAB_BUDGET
        + "."
        + USER_ID
        + " = "
        + id
        + " GROUP BY "
        + TAB_BUDGET
        + "."
        + DATE_START_BUDGET
        + ", "
        + TAB_BUDGET
        + "."
        + DATE_END_BUDGET
        + " ORDER BY "
        + TAB_BUDGET
        + "."
        + DATE_START_BUDGET;

    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          Budget model = cursorToBudget(cursor);
          mListModel.add(model);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return mListModel;
  }

  public ArrayList<Budget> getBudgetActive(String id) {
    ArrayList<Budget> mListModel = new ArrayList<Budget>();
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT * FROM "
        + TAB_BUDGET
        + " WHERE "
        + TAB_BUDGET
        + "."
        + USER_ID
        + " = "
        + id
        + " AND '"
        + helper.getCurrentTime(helper.FORMAT_MONTH_MM)
        + "' >= "
        + TAB_BUDGET
        + "."
        + DATE_START_BUDGET
        + " AND '"
        + helper.getCurrentTime(helper.FORMAT_MONTH_MM)
        + "' <="
        + TAB_BUDGET
        + "."
        + DATE_END_BUDGET;

    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          Budget model = cursorToBudget(cursor);
          mListModel.add(model);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return mListModel;
  }

  public ArrayList<Budget> getBudgetbyDate(String id, String date_start, String date_end) {
    ArrayList<Budget> mListModel = new ArrayList<Budget>();
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT "
        + TAB_BUDGET
        + "."
        + ID
        +
        ", "
        + TAB_BUDGET
        + "."
        + ID_BUDGET
        +
        ", "
        + TAB_BUDGET
        + "."
        + USER_ID
        +
        ", "
        + TAB_BUDGET
        + "."
        + AMOUNT_BUDGET
        +
        ", "
        + TAB_BUDGET
        + "."
        + CATEGORY_BUDGET
        +
        ", "
        + TAB_BUDGET
        + "."
        + DATE_START_BUDGET
        +
        ", "
        + TAB_BUDGET
        + "."
        + DATE_END_BUDGET
        +
        ", "
        + TAB_BUDGET
        + "."
        + DELETED_AT
        +
        ", "
        + TAB_BUDGET
        + "."
        + UPDATED_AT
        +
        ", "
        + TAB_BUDGET
        + "."
        + CREATED_AT
        + " FROM "
        + TAB_BUDGET
        + " WHERE "
        + TAB_BUDGET
        + "."
        + USER_ID
        + " = "
        + id
        + " AND "
        + TAB_BUDGET
        + "."
        + DATE_START_BUDGET
        + " = '"
        + date_start
        + "' AND "
        + TAB_BUDGET
        + "."
        + DATE_END_BUDGET
        + " = '"
        + date_end
        + "' ORDER BY "
        + TAB_BUDGET
        + "."
        + DATE_START_BUDGET
        + " ASC";

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          Budget model = cursorToBudget(cursor);
          mListModel.add(model);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return mListModel;
  }

  public void newBudget(Budget budget) {
    ContentValues cv = getContentValuesBudget(budget);
    getWritableDatabase().insert(TAB_BUDGET, ID, cv);
  }

  public void updateBudget(Budget budget) {
    ContentValues cv = getContentValuesBudget(budget);
    getWritableDatabase().update(TAB_BUDGET, cv, ID + "=" + budget.getId(), null);
  }

  public void UpdateBudget(Budget budget) {
    ContentValues cv = getContentValuesBudget(budget);
    String sql_before = "select "
        + UPDATED_AT
        + " from "
        + TAB_BUDGET
        + " where "
        + CATEGORY_BUDGET
        + "="
        + budget.getCategory_budget();
    Cursor cursorBefore = getReadableDatabase().rawQuery(sql_before, null);
    if (cursorBefore.moveToFirst() && cursorBefore.getCount() > 0) {
      Date before_date = Helper.setInputFormatter("yyyy-MM-dd",
          cursorBefore.getString(cursorBefore.getColumnIndex(UPDATED_AT)));
      Date after_date = Helper.setInputFormatter("yyyy-MM-dd", budget.getUpdated_at());
      if (before_date.getTime() <= after_date.getTime()) {
        int i = getWritableDatabase().update(TAB_BUDGET, //table
            cv, // column/value
            CATEGORY_BUDGET + " = ?", // selections
            new String[] { String.valueOf(budget.getCategory_budget()) });
      }
    } else {
      int i = getWritableDatabase().update(TAB_BUDGET, //table
          cv, // column/value
          CATEGORY_BUDGET + " = ?", // selections
          new String[] { String.valueOf(budget.getCategory_budget()) });

      Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

      try {
        if (cursor.moveToFirst()) {
          int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

          if (rows == 0) {
            getWritableDatabase().insert(TAB_BUDGET, ID, cv);
          }
        }
      } finally {
        cursor.close();
      }
    }
  }

  public double getTotalPerCategoryAndRangeTime(String user_id, int category_id, String start_date,
      String end_date) {
    Double result = 0.0;
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT * FROM "
        + TAB_CASH
        + " join "
        +
        TAB_PRODUCT
        + " on "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " join "
        +
        TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE ("
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + " BETWEEN "
        + "'"
        + start_date
        + "' AND '"
        + end_date
        + "')"
        + " AND "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + " = "
        + category_id
        + " AND "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = '"
        + user_id
        + "'"
        + " AND "
        + TAB_CASH
        + "."
        + JENIS_MUTASI
        + " = 'DB'";

    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          result += cursor.getDouble(cursor.getColumnIndex(AMOUNT));
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return result;
  }

  public ArrayList<Cash> getCash(String id_user) {
    Cursor cursor = getWritableDatabase().rawQuery("select * from "
        + TAB_CASH
        + " join "
        +
        TAB_PRODUCT
        + " on "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " join "
        +
        TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        +
        " where "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + id_user, null);
    ArrayList<Cash> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        Cash mModel = cursorToCash(cursor);
        mModels.add(mModel);
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public Product getProductBy(String depend, int id) {
    Product product = null;
    Cursor cursor = null;
    if (depend.equalsIgnoreCase(TAB_PRODUCT)) {
      cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TAB_PRODUCT + " WHERE " +
          ID_PRODUCT + "=" + id, null);
    } else if (depend.equalsIgnoreCase(TAB_ACCOUNT)) {
      cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TAB_PRODUCT + " WHERE " +
          ACCOUNT_ID + "=" + id, null);
    }
    try {
      if (cursor.moveToFirst()) {
        product = cursorToProduct(cursor);
      }
    } finally {
      cursor.close();
    }
    return product;
  }

  public ArrayList<Category> getCashGroupByCategory(String id_user, String type_mutasi) {
    ArrayList<Category> mListModel = new ArrayList<Category>();
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT "
        + TAB_CATEGORY
        + "."
        + ID
        +
        ", "
        + TAB_CATEGORY
        + "."
        + ID_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + NAME_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + TYPE_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + ICON_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + COLOR_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + BELONG_TO_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + DESCRIPTION_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + USER_ID
        +
        ", "
        + TAB_CATEGORY
        + "."
        + DELETED_AT
        +
        ", "
        + TAB_CATEGORY
        + "."
        + UPDATED_AT
        +
        ", "
        + TAB_CATEGORY
        + "."
        + CREATED_AT
        + " FROM "
        + TAB_CATEGORY
        + " JOIN "
        + TAB_CASH
        + " ON "
        + TAB_CATEGORY
        + "."
        + ID_CATEGORY
        + " = "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + " JOIN "
        + TAB_PRODUCT
        + " ON "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_CASH
        + "."
        + JENIS_MUTASI
        + " = '"
        + type_mutasi
        + "'"
        + " GROUP BY "
        + TAB_CASH
        + "."
        + CATEGORY_ID;

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          Category category = cursorToCategory(cursor);
          mListModel.add(category);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return mListModel;
  }

  public ArrayList<Category> getCashGroupByCategoryCurrentMonth(String id_user,
      String type_mutasi) {
    ArrayList<Category> mListModel = new ArrayList<Category>();
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT "
        + TAB_CATEGORY
        + "."
        + ID
        +
        ", "
        + TAB_CATEGORY
        + "."
        + ID_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + NAME_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + TYPE_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + ICON_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + COLOR_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + BELONG_TO_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + DESCRIPTION_CATEGORY
        +
        ", "
        + TAB_CATEGORY
        + "."
        + USER_ID
        +
        ", "
        + TAB_CATEGORY
        + "."
        + DELETED_AT
        +
        ", "
        + TAB_CATEGORY
        + "."
        + UPDATED_AT
        +
        ", "
        + TAB_CATEGORY
        + "."
        + CREATED_AT
        + " FROM "
        + TAB_CATEGORY
        + " JOIN "
        + TAB_CASH
        + " ON "
        + TAB_CATEGORY
        + "."
        + ID_CATEGORY
        + " = "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + " JOIN "
        + TAB_PRODUCT
        + " ON "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_CASH
        + "."
        + JENIS_MUTASI
        + " = '"
        + type_mutasi
        + "'"
        + " AND "
        + TAB_CASH
        + "."
        + BULAN
        + " = "
        + helper.getCurrentMonth()
        + " GROUP BY "
        + TAB_CASH
        + "."
        + CATEGORY_ID;

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        do {
          Category category = cursorToCategory(cursor);
          mListModel.add(category);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return mListModel;
  }

  public void deleteAllData() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TAB_CASH, null, null);
    db.delete(TAB_ALARM, null, null);
    db.delete(TAB_BUDGET, null, null);
    db.delete(TAB_PRODUCT, null, null);
    db.delete(TAB_ACCOUNT, null, null);
  }

  public void deleteDataTable(String table) {
    SQLiteDatabase db = this.getWritableDatabase();
    try {
      db.delete(table, null, null);
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    }
  }

  public boolean deleteAccountByID(int id) {
    List<Plan> planArrayList = getAllPlanByAccount(id);
    for (Plan p : planArrayList) {
      disconnectPlan(p.getId());
    }
    deleteCashByAccount(id);
    return getWritableDatabase().delete(TAB_ACCOUNT, ID_ACCOUNT + "=" + id, null) > 0;
  }

  public int disconnectPlan(int id_plan) {
    ContentValues cv = new ContentValues();
    cv.put(ACCOUNT_ID, 0);
    return getWritableDatabase().update(TAB_PLAN, cv, ID + "=" + id_plan, null);
  }

  public boolean deleteAccountByUser(int id) {
    return getWritableDatabase().delete(TAB_ACCOUNT, USER_ID + "=" + id, null) > 0;
  }

  public boolean deleteProductByIDProduct(int id) {
    return getWritableDatabase().delete(TAB_PRODUCT, ID_PRODUCT + "=" + id, null) > 0;
  }

  public boolean deleteProductByIDAccount(int id) {
    return getWritableDatabase().delete(TAB_PRODUCT, ACCOUNT_ID + "=" + id, null) > 0;
  }

  public boolean deleteCashByProduct(int id_product) {
    return getWritableDatabase().delete(TAB_CASH, PRODUCT_ID + "=" + id_product, null) > 0;
  }

  public boolean deleteCashByIDCashflow(int id_cashflow) {
    return getWritableDatabase().delete(TAB_CASH, CASHFLOW_ID + "=" + id_cashflow, null) > 0;
  }

  public boolean deleteCashByIDCashflowLocal(int id_local) {
    return getWritableDatabase().delete(TAB_CASH, ID + "=" + id_local, null) > 0;
  }

  public boolean softDeleteCashflow(int id) {
    String sql_find = "select * from " + TAB_CASH + " where " + ID + "=" + id;
    Cursor cursor = getReadableDatabase().rawQuery(sql_find, null);
    if (cursor.moveToFirst()) {
      Cash cash = cursorToCash(cursor);
      if (cash.getCash_id() > 0) {
        ContentValues cv = new ContentValues();
        cv.put(DELETED_AT, df.format(Calendar.getInstance().getTime()));
        cv.put(UPDATED_AT, df.format(Calendar.getInstance().getTime()));
        getWritableDatabase().update(TAB_CASH, cv, ID + "=" + id, null);
        return true;
      } else {
        return getWritableDatabase().delete(TAB_CASH, ID + "=" + id, null) > 0;
      }
    } else {
      return false;
    }
  }

  public boolean deleteBudgetByIDBudget(int id_budget) {
    return getWritableDatabase().delete(TAB_BUDGET, ID_BUDGET + "=" + id_budget, null) > 0;
  }

  public boolean deleteAlarmByIDAlarm(int id_alarm) {
    return getWritableDatabase().delete(TAB_ALARM, ALARM_ID + "=" + id_alarm, null) > 0;
  }

  public boolean deletePlanByIDPlan(int id, int type) {
    getWritableDatabase().delete(TAB_PLAN_PRODUCT, PLAN_ID + "=" + id, null);
    switch (type) {
      case PLAN_TYPE_PENSIUN:
        getWritableDatabase().delete(TAB_DANA_PENSIUN, PLAN_ID + "=" + id, null);
        break;
      case PLAN_TYPE_CUSTOME:
        //getWritableDatabase().delete(TAB_DANA_CUSTOME, ID + "=" + id, null);
        break;
      case PLAN_TYPE_DARURAT:
        getWritableDatabase().delete(TAB_DANA_DARURAT, PLAN_ID + "=" + id, null);
        break;
      case PLAN_TYPE_KULIAH:
        getWritableDatabase().delete(TAB_DANA_KULIAH, PLAN_ID + "=" + id, null);
        break;
    }
    return getWritableDatabase().delete(TAB_PLAN, ID + "=" + id, null) > 0;
  }

  public boolean deletePlanByIDLocal(int id, int type) {
    getWritableDatabase().delete(TAB_PLAN_PRODUCT, PLAN_ID_LOCAL + "=" + id, null);
    switch (type) {
      case PLAN_TYPE_PENSIUN:
        getWritableDatabase().delete(TAB_DANA_PENSIUN, PLAN_ID_LOCAL + "=" + id, null);
        break;
      case PLAN_TYPE_CUSTOME:
        //getWritableDatabase().delete(TAB_DANA_CUSTOME, ID + "=" + id, null);
        break;
      case PLAN_TYPE_DARURAT:
        getWritableDatabase().delete(TAB_DANA_DARURAT, PLAN_ID_LOCAL + "=" + id, null);
        break;
      case PLAN_TYPE_KULIAH:
        getWritableDatabase().delete(TAB_DANA_KULIAH, PLAN_ID_LOCAL + "=" + id, null);
        break;
    }
    return getWritableDatabase().delete(TAB_PLAN, ID + "=" + id, null) > 0;
  }

  public boolean deleteProductPlanByIdPlanLocal(int id_local) {
    return getWritableDatabase().delete(TAB_PLAN_PRODUCT, PLAN_ID_LOCAL + "=" + id_local, null) > 0;
  }

  public boolean deletePlanByIDProduct(int id_product) {
    return getWritableDatabase().delete(TAB_PLAN, PRODUCT_ID + "=" + id_product, null) > 0;
  }

  public boolean deletePlanByIDAccount(int id_account) {
    Boolean cek = false;
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_PRODUCT
        + " join "
        + TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " where "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + "="
        + id_account, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          cek = getWritableDatabase().delete(TAB_PLAN,
              PRODUCT_ID + "=" + cursor.getInt(cursor.getColumnIndex(ID_PRODUCT)), null) > 0;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return cek;
  }

  public boolean deleteCashByID(int id) {
    return getWritableDatabase().delete(TAB_CASH, ID + "=" + id, null) > 0;
  }

  public boolean deleteBudgetByID(int id) {
    return getWritableDatabase().delete(TAB_BUDGET, ID + "=" + id, null) > 0;
  }

  public boolean softDeleteBudgetByID(int id) {
    String sql_find = "select * from " + TAB_BUDGET + " where " + ID + "=" + id;
    Cursor cursor = getReadableDatabase().rawQuery(sql_find, null);
    if (cursor.moveToFirst()) {
      Budget budget = cursorToBudget(cursor);
      if (budget.getId_budget() > 0) {
        ContentValues cv = new ContentValues();
        cv.put(DELETED_AT, df.format(Calendar.getInstance().getTime()));
        cv.put(UPDATED_AT, df.format(Calendar.getInstance().getTime()));
        getWritableDatabase().update(TAB_BUDGET, cv, ID + "=" + id, null);
        return true;
      } else {
        return getWritableDatabase().delete(TAB_BUDGET, ID + "=" + id, null) > 0;
      }
    } else {
      return false;
    }
  }

  public boolean softDeletePlanByID(int id) {
    String sql_find = "select * from " + TAB_PLAN + " where " + ID + "=" + id;
    Cursor cursor = getReadableDatabase().rawQuery(sql_find, null);
    if (cursor.moveToFirst()) {
      Plan plan = cursorToPlan(cursor);
      if (plan.getId_plan() > 0) {
        ContentValues cv = new ContentValues();
        cv.put(DELETED_AT, df.format(Calendar.getInstance().getTime()));
        cv.put(UPDATED_AT, df.format(Calendar.getInstance().getTime()));
        getWritableDatabase().update(TAB_PLAN, cv, ID + "=" + id, null);
        return true;
      } else {
        return deletePlanByIDPlan(plan.getId(), plan.getType());
      }
    } else {
      return false;
    }
  }

  public boolean deleteAlarmByID(int id) {
    return getWritableDatabase().delete(TAB_ALARM, ID + "=" + id, null) > 0;
  }

  public boolean softDeleteAlarmByID(int id) {
    String sql_find = "select * from " + TAB_ALARM + " where " + ID + "=" + id;
    Cursor cursor = getReadableDatabase().rawQuery(sql_find, null);
    if (cursor.moveToFirst()) {
      Alarm alarm = cursorToAlarm(cursor);
      if (alarm.getId_alarm() > 0) {
        ContentValues cv = new ContentValues();
        cv.put(DELETED_AT, df.format(Calendar.getInstance().getTime()));
        cv.put(UPDATED_AT, df.format(Calendar.getInstance().getTime()));
        getWritableDatabase().update(TAB_ALARM, cv, ID + "=" + alarm.getId(), null);
        return true;
      } else {
        return deleteAlarmByID(alarm.getId());
      }
    } else {
      return false;
    }
  }

  public boolean deleteCashByAccount(int idaccount) {
    Cursor cursor = getReadableDatabase().rawQuery(
        "select * from " + TAB_PRODUCT + " where " + ACCOUNT_ID + "=" + idaccount, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          return getWritableDatabase().delete(TAB_CASH,
              PRODUCT_ID + "=" + cursor.getInt(cursor.getColumnIndex(ID_PRODUCT)), null) > 0;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return false;
  }

  public boolean deleteAccountbyIDProduct(int idproduct) {
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " where "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + "="
        + idproduct, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          return getWritableDatabase().delete(TAB_ACCOUNT,
              ID_ACCOUNT + "=" + cursor.getInt(cursor.getColumnIndex(ID_ACCOUNT)), null) > 0;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return false;
  }

  public int getId_category_by_icon(String icon) {
    int id = 3;
    if (icon.equalsIgnoreCase("{gmd-card-giftcard}")) {
      id = 1;
    } else if (icon.equalsIgnoreCase("{gmd-car}")) {
      id = 2;
    } else if (icon.equalsIgnoreCase("{gmd-toys}")) {
      id = 3;
    } else if (icon.equalsIgnoreCase("{gmd-cutlery}")) {
      id = 4;
    } else if (icon.equalsIgnoreCase("{gmd-hospital}")) {
      id = 5;
    } else if (icon.equalsIgnoreCase("{gmd-local-mall}")) {
      id = 6;
    } else if (icon.equalsIgnoreCase("{gmd-gas-station}")) {
      id = 7;
    } else if (icon.equalsIgnoreCase("{gmd-airplane}")) {
      id = 8;
    } else if (icon.equalsIgnoreCase("{gmd-bike}")) {
      id = 9;
    } else if (icon.equalsIgnoreCase("{gmd-smartphone-android}")) {
      id = 10;
    } else if (icon.equalsIgnoreCase("{gmd-tv}")) {
      id = 11;
    } else if (icon.equalsIgnoreCase("{gmd-cocktail}")) {
      id = 12;
    } else if (icon.equalsIgnoreCase("{gmd-cake}")) {
      id = 13;
    } else if (icon.equalsIgnoreCase("{gmd-graduation-cap}")) {
      id = 14;
    } else if (icon.equalsIgnoreCase("{gmd-blur}")) {
      id = 15;
    } else if (icon.equalsIgnoreCase("{gmd-airline-seat-flat}")) {
      id = 16;
    } else if (icon.equalsIgnoreCase("{gmd-flash}")) {
      id = 17;
    } else if (icon.equalsIgnoreCase("{gmd-local-shipping}")) {
      id = 18;
    } else if (icon.equalsIgnoreCase("{gmd-roller}")) {
      id = 19;
    } else if (icon.equalsIgnoreCase("{gmd-account}")) {
      id = 20;
    } else if (icon.equalsIgnoreCase("{gmd-local-atm}")) id = 21;
    return id;
  }

  public Boolean InitRefresh(int iduser) {
    ArrayList<Product> products = new ArrayList<Product>();
    products = getAllProductByUserWhereStatusLogin(iduser);

    if (products.size() > 0) {
      for (int x = 0; x < products.size(); x++) {
        if (CheckCashFlow(String.valueOf(iduser), products.get(x).getId_product())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean CheckCashFlowLocal(int id_local) {

    boolean check = false;
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_CASH
        + " join "
        + TAB_PRODUCT
        + " on "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " join "
        + TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_CASH
        + "."
        + ID
        + " = "
        + id_local, null);
    try {
      if (cursor.getCount() > 0) {
        check = true;
      }
    } finally {
      cursor.close();
    }
    return check;
  }

  public boolean CheckCashFlow(int cash_id) {

    boolean check = false;
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_CASH
        + " join "
        + TAB_PRODUCT
        + " on "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " join "
        + TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_CASH
        + "."
        + CASHFLOW_ID
        + " = "
        + cash_id, null);
    try {
      if (cursor.getCount() > 0) {
        check = true;
      }
    } finally {
      cursor.close();
    }
    return check;
  }

  public int UpdateCash(Cash cash) {
    ContentValues cv = getContentValuesCash(cash);
    int i = 0;
    if (cash.getCash_id() == -1) {
      Timber.e("aves_in seharus nya tidak di sini");
      i = getWritableDatabase().update(TAB_CASH, //table
          cv, // column/value
          ID + " = ?", // selections
          new String[] { String.valueOf(cash.getId()) }); //selection args
    } else {
      Timber.e("aves_in " + cash.getId() + " " + cash.getCash_id());
      Cash before = null;
      if (cash.getId() != 0) {
        before = getCashByID(cash.getId());
      } else {
        before = getCashByCashflowId(cash.getCash_id());
      }
      Timber.e("aves_in before " + before);
      if (before != null) {
        try {
          Date date_before = df.parse(before.getUpdated_at());
          Date date_after = df.parse(cash.getUpdated_at());
          Timber.e("aves_in before " + date_before + " " + date_after);
          if (date_before.getTime() <= date_after.getTime()) {
            Timber.e(cash.getId()+"aves_in keren banget " + cv);
            i = getWritableDatabase().update(TAB_CASH, //table
                cv, // column/value
                ID + " = ?", // selections
                new String[] { String.valueOf(before.getId()) }); //selection args
          }
        } catch (Exception e) {
          Timber.e("Heloo ERROR " + e);
        }
      } else {
        getWritableDatabase().insert(TAB_CASH, ID, cv);
      }
    }
    //Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);
    //
    //try {
    //  if (cursor.moveToFirst()) {
    //    int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));
    //    Timber.e("row " + rows);
    //    if (rows == 0) {
    //      getWritableDatabase().insert(TAB_CASH, ID, cv);
    //    }
    //  }
    //} finally {
    //  cursor.close();
    //}
    return i;
  }

  public ArrayList<Account> getAllAccountByUserWhereStatusLogin(int id_user) {
    ArrayList<Account> accounts = new ArrayList<Account>();
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_ACCOUNT
        + " where "
        + USER_ID
        + "="
        + id_user
        + " AND "
        + LOGIN_STATUS
        + " = 1", null);
    try {
      if (cursor.moveToFirst()) {
        do {
          accounts.add(cursorToAccount(cursor));
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return accounts;
  }

  public List<Cash> getCashflowByProduct(int id_user, int id_produk) {
    String q = "SELECT "
        + TAB_CASH
        + ".* "
        + " FROM "
        + TAB_ACCOUNT
        + " JOIN "
        + TAB_PRODUCT
        + " ON "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " = "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " JOIN "
        + TAB_CASH
        + " ON "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " = "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " = "
        + id_produk;
    Timber.e("QUERY " + q);
    Cursor cursor = getReadableDatabase().rawQuery(q, null);

    ArrayList<Cash> cashes = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          cashes.add(cash);
        } while (cursor.moveToNext());
      }
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    } finally {
      cursor.close();
    }
    return cashes;
  }

  //Todo : perlu diperbaiki fungsi ini, terlalu rumit (CheckCashFlow)
  public boolean CheckCashFlow(String id_user, int id_product) {
    Cursor cursor;

    cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_ACCOUNT
        + " a join "
        + TAB_PRODUCT
        + " p on a."
        + ID_ACCOUNT
        + " = p."
        + ACCOUNT_ID
        + " where p."
        + ID_PRODUCT
        + " = "
        + id_product, null);

    String id_account = null;
    int id_vendor = 0;
    try {
      if (cursor.moveToFirst()) {
        id_account = cursor.getString(cursor.getColumnIndex(ID_ACCOUNT));
        id_vendor = cursor.getInt(cursor.getColumnIndex(VENDOR_ID));
      }
    } finally {
      cursor.close();
    }

    cursor = getReadableDatabase().rawQuery(
        "select * from " + TAB_ACCOUNT + " where " + ID_ACCOUNT + " = " + id_account, null);

    String last_connect = null;
    try {
      if (cursor.moveToFirst()) {
        last_connect = cursor.getString(cursor.getColumnIndex(LAST_CONNECT));
      }
    } finally {
      cursor.close();
    }
    boolean check = false;
    if (id_vendor != 9) {
      cursor = getReadableDatabase().rawQuery("select * from " +
          TAB_CASH + " WHERE " +
          PRODUCT_ID + " = " + id_product, null);

      try {
        if (cursor.getCount() == 0 && last_connect == null) {
          check = true;
        }
      } finally {
        cursor.close();
      }
    }
    return check;
  }

  public ArrayList<Product> getAllProductByUserWhereStatusLogin(int id_user) {
    ArrayList<Product> products = new ArrayList<Product>();
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_PRODUCT
        + " p join "
        + TAB_ACCOUNT
        + " a on p."
        + ACCOUNT_ID
        + " = a."
        + ID_ACCOUNT
        + " where a."
        + USER_ID
        + "="
        + id_user
        + " AND a."
        + LOGIN_STATUS
        + " = 1", null);
    try {
      if (cursor.moveToFirst()) {
        do {
          products.add(cursorToProduct(cursor));
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return products;
  }

  public void getLastUpdatedCash(int idaccount) {
    Cursor cursor = getWritableDatabase().rawQuery("select max(datetime(c."
        + UPDATED_AT
        + ")) as newlastconnect from "
        + TAB_CASH
        + " c join "
        + TAB_PRODUCT
        + " p on c."
        + PRODUCT_ID
        + " = p."
        + ID_PRODUCT
        + " join "
        + TAB_ACCOUNT
        + " a on p."
        + ACCOUNT_ID
        + " = a."
        + ID_ACCOUNT
        + " where a."
        + ID_ACCOUNT
        + "="
        + idaccount, null);
    try {
      if (cursor.moveToFirst()) {
        String last_connect = cursor.getString(cursor.getColumnIndex("newlastconnect"));
        ContentValues cv = new ContentValues();
        cv.put(LAST_CONNECT, last_connect);
        getWritableDatabase().update(TAB_ACCOUNT, cv, ID_ACCOUNT + "=" + idaccount, null);
      }
    } finally {
      cursor.close();
    }
  }

  public Long getLastConnectByAccount(int idaccount) {
    String lc = TANGGAL_LAMA;
    Cursor cursor = getReadableDatabase().rawQuery(
        "select * from " + TAB_ACCOUNT + " where " + ID_ACCOUNT + "=" + idaccount, null);
    try {
      if (cursor.moveToFirst()) {
        if (cursor.getString(cursor.getColumnIndex(LAST_CONNECT)) != null) {
          lc = cursor.getString(cursor.getColumnIndex(LAST_CONNECT));
        } else {
          lc = TANGGAL_LAMA;
        }
      }
    } finally {
      cursor.close();
    }

    return helper.converterEpoc(lc);
  }

  public void getLastSyncCash(int id_user) {
    Cursor cursor = getWritableDatabase().rawQuery("select a."
        + USER_ID
        + " as id_user, c."
        + CASHFLOW_ID
        + ", max(datetime(c."
        + UPDATED_AT
        + ")) as newlastconnect from "
        + TAB_CASH
        + " c join "
        + TAB_PRODUCT
        + " p on c."
        + PRODUCT_ID
        + " = p."
        + ID_PRODUCT
        + " join "
        + TAB_ACCOUNT
        + " a on p."
        + ACCOUNT_ID
        + " = a."
        + ID_ACCOUNT
        + " where a."
        + USER_ID
        + "="
        + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        String last_sync = cursor.getString(cursor.getColumnIndex("newlastconnect"));
        ContentValues cv = new ContentValues();
        cv.put(LAST_SYNC_CASH, last_sync);
        getWritableDatabase().update(TAB_USER, cv, ID_USER + "=" + id_user, null);
      }
    } finally {
      cursor.close();
    }
  }

  public void getLastSyncBudget(int id_user) {
    Cursor cursor = getWritableDatabase().rawQuery("SELECT MAX(DATETIME("
        + UPDATED_AT
        + ")) as newlastconnect from "
        + TAB_BUDGET
        + " WHERE "
        + USER_ID
        + " = "
        + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        String last_sync = cursor.getString(cursor.getColumnIndex("newlastconnect"));
        ContentValues cv = new ContentValues();
        cv.put(LAST_SYNC_BUDGET, last_sync);
        getWritableDatabase().update(TAB_USER, cv, ID_USER + "=" + id_user, null);
      }
    } finally {
      cursor.close();
    }
  }

  public void getLastSyncAlarm(int id_user) {
    Cursor cursor = getWritableDatabase().rawQuery("SELECT MAX(DATETIME("
        + UPDATED_AT
        + ")) as newlastconnect from "
        + TAB_ALARM
        + " WHERE "
        + USER_ID
        + " = "
        + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        String last_sync = cursor.getString(cursor.getColumnIndex("newlastconnect"));
        ContentValues cv = new ContentValues();
        cv.put(LAST_SYNC_ALARM, last_sync);
        getWritableDatabase().update(TAB_USER, cv, ID_USER + "=" + id_user, null);
      }
    } finally {
      cursor.close();
    }
  }

  public Cash getCashByID(int id) {
    Cash cash = null;
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT * FROM " + TAB_CASH + " WHERE " + ID + " = " + id;

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        cash = cursorToCash(cursor);
      }
    } finally {
      cursor.close();
    }
    return cash;
  }

  public Cash getCashByCashflowId(int id) {
    Cash cash = null;
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT * FROM " + TAB_CASH + " WHERE " + CASHFLOW_ID + " = " + id;

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        cash = cursorToCash(cursor);
      }
    } finally {
      cursor.close();
    }
    return cash;
  }

  public Budget getBudgetByID(int id) {
    Budget budget = null;
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT * FROM " + TAB_BUDGET + " WHERE " + ID + " = " + id;

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        budget = cursorToBudget(cursor);
      }
    } finally {
      cursor.close();
    }
    return budget;
  }

  public Alarm getAlarmByID(int id) {
    Alarm alarm = null;
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery = "SELECT * FROM " + TAB_ALARM + " WHERE " + ID + " = " + id;

    Cursor cursor = db.rawQuery(selectQuery, null);

    try {
      if (cursor.moveToFirst()) {
        alarm = cursorToAlarm(cursor);
      }
    } finally {
      cursor.close();
    }
    return alarm;
  }

  public Integer getProductCount(String depend, int id) {
    Integer productcount = 0;
    Cursor cursor = null;

    if (depend.equalsIgnoreCase(TAB_PRODUCT)) {
      cursor = getReadableDatabase().rawQuery(
          "SELECT count(*) AS productcount FROM "
              + TAB_PRODUCT
              + " JOIN "
              +
              TAB_ACCOUNT
              + " on "
              + TAB_PRODUCT
              + "."
              + ACCOUNT_ID
              + " = "
              + TAB_ACCOUNT
              + "."
              + ID_ACCOUNT
              +
              " where "
              + TAB_PRODUCT
              + "."
              + ID_PRODUCT
              + "="
              + id, null);
    } else if (depend.equalsIgnoreCase(TAB_ACCOUNT)) {
      cursor =
          getReadableDatabase().rawQuery("SELECT count(*) AS productcount FROM " + TAB_PRODUCT +
              " WHERE " + TAB_PRODUCT + "." + ACCOUNT_ID + "=" + id, null);
    } else if (depend.equalsIgnoreCase(TAB_USER)) {
      cursor = getReadableDatabase().rawQuery(
          "SELECT count(*) AS productcount FROM "
              + TAB_PRODUCT
              + " JOIN "
              +
              TAB_ACCOUNT
              + " on "
              + TAB_PRODUCT
              + "."
              + ACCOUNT_ID
              + " = "
              + TAB_ACCOUNT
              + "."
              + ID_ACCOUNT
              +
              " where "
              + TAB_ACCOUNT
              + "."
              + USER_ID
              + "="
              + id, null);
    }

    try {
      if (cursor.moveToFirst()) {
        productcount = cursor.getInt(cursor.getColumnIndex("productcount"));
      }
    } finally {
      cursor.close();
    }
    return productcount;
  }

  public Integer getCountCashflowByDate(int id, String date) {
    Integer cashflow_count = 0;
    Cursor cursor = null;

    cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) AS cashflow_count from "
        + TAB_CASH
        + " JOIN "
        + TAB_PRODUCT
        + " ON "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " WHERE "
        + TAB_CASH
        + "."
        + UPDATED_AT
        + " LIKE '"
        + date
        + "%'"
        + " AND "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id, null);

    try {
      if (cursor.moveToFirst()) {
        cashflow_count = cursor.getInt(cursor.getColumnIndex("cashflow_count"));
      }
    } finally {
      cursor.close();
    }
    return cashflow_count;
  }

  public boolean DeleteCashByIdAccount(int id_account) {
    Boolean cek = false;
    Cursor cursor = getReadableDatabase().rawQuery("select * from "
        + TAB_PRODUCT
        + " join "
        + TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " where "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + "="
        + id_account, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          cek = getWritableDatabase().delete(TAB_CASH,
              PRODUCT_ID + "=" + cursor.getInt(cursor.getColumnIndex(ID_PRODUCT)), null) > 0;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return cek;
  }

  public JSONObject updateAccount(JSONObject account, JSONObject response, String id_user) {
    try {
      int id_account = account.getInt("id");
      String deleted_at = "null";
      if (account.has("deleted_at")) {
        deleted_at = account.getString("deleted_at");
      }

      if (CheckAccount(id_account)) {
        if (!deleted_at.equalsIgnoreCase("null")) {
          deleteAccountByID(id_account);
          deleteProductByIDAccount(id_account);
          deleteCashByAccount(id_account);
          return null;
        }

        Account accountModel = new Account(account);
        if (response.has("user_id")) {
          accountModel.setIduser(response.getInt("user_id"));
        } else {
          accountModel.setIduser(Integer.valueOf(id_user));
        }
        UpdateAccount(accountModel);
        return account;
      } else if (deleted_at.equalsIgnoreCase("null")) {
        Account accountModel = new Account(account);
        if (response.has("user_id")) accountModel.setIduser(Integer.valueOf(id_user));
        newAccount(accountModel);
        return account;
      }
    } catch (Exception e_account) {
      Timber.e("ERROR updateAccount " + e_account.toString());
    }
    return null;
  }

  public Boolean updateAccount(Integer id_user, account account) {
    try {
      Timber.d("save account id user" + id_user);
      int id_account = account.id;
      String deleted_at = account.deleted_at;
      if (CheckAccount(id_account)) {
        if (deleted_at != null) {
          deleteAccountByID(id_account);
          deleteProductByIDAccount(id_account);
          deleteCashByAccount(id_account);
          return false;
        }

        Account accountModel = new Account(account);
        if (id_user != null) {
          accountModel.setIduser(id_user);
        }
        Timber.e("update Account " + id_account);
        UpdateAccount(accountModel);
        return true;
      } else if (deleted_at == null) {
        Timber.e("new Account ");
        Account accountModel = new Account(account);
        if (id_user != null) accountModel.setIduser(id_user);
        newAccount(accountModel);
        return true;
      }
    } catch (Exception e_account) {
      Timber.e("save account" + e_account.toString());
    }
    return false;
  }

  public JSONObject updateProduct(JSONObject product, JSONObject account, String id_user) {
    try {
      Timber.e("update product ");
      int id_product = product.getInt("id");
      String deleted_at = "null";
      if (product.has("deleted_at")) {
        deleted_at = product.getString("deleted_at");
      }
      if (CheckProduct(id_product)) {
        if (!deleted_at.equalsIgnoreCase("null")) {
          deleteProductByIDProduct(id_product);
          deleteCashByProduct(id_product);
          return null;
        }

        Product productModel = new Product(product, account);
        if (account.has("id")) productModel.setAccount_id(account.getInt("id"));
        UpdateProduct(productModel);
        return product;
      } else if (deleted_at.equalsIgnoreCase("null")) {
        Product productModel = new Product(product, account);
        if (account.has("id")) productModel.setAccount_id(account.getInt("id"));
        newProduct(productModel);
        return product;
      }
    } catch (Exception e_product) {
      Timber.e("updateProduct error " + e_product.toString());
    }
    return null;
  }

  public Boolean updateProduct(Integer id_user, account account, product product) {
    try {
      int id_product = product.id;
      if (CheckProduct(id_product)) {
        if (product.deleted_at != null) {
          deleteProductByIDProduct(id_product);
          deleteCashByProduct(id_product);
          return false;
        }
        Product productModel = new Product(product, account);
        Timber.e("product getProperties model1 " + productModel.getProperties());
        if (account.id != 0 || Integer.valueOf(account.id) != null) {
          productModel.setAccount_id(account.id);
        }
        UpdateProduct(productModel);
        return true;
      } else if (product.deleted_at == null) {
        Product productModel = new Product(product, account);
        Timber.e("product getProperties model2 " + productModel.getProperties());
        if (account.id != 0 || Integer.valueOf(account.id) != null) {
          productModel.setAccount_id(account.id);
        }

        newProduct(productModel);
        return true;
      }
    } catch (Exception e_product) {
      Timber.e("ERROR updateProduct " + e_product);
    }
    return false;
  }

  public JSONObject updateCashflow(JSONObject cashflow, JSONObject product, String id_user) {
    try {
      Timber.e("update cash");
      int id_cashflow = cashflow.getInt("id");
      int id_local = 0;
      if (cashflow.has("id_local")) {
        id_local = cashflow.getInt("id_local");
      }
      String deleted_at = "null";
      if (cashflow.has("deleted_at")) {
        deleted_at = cashflow.getString("deleted_at");
      }
      if (CheckCashFlowLocal(id_local)) {
        if (!deleted_at.equalsIgnoreCase("null")) {
          deleteCashByIDCashflowLocal(id_local);
          return null;
        }
        Cash cashModel = new Cash(cashflow);
        if (product.has("id")) cashModel.setProduct_id(product.getInt("id"));
        UpdateCash(cashModel);
        return cashflow;
      } else if (CheckCashFlow(id_cashflow)) {
        if (!deleted_at.equalsIgnoreCase("null")) {
          deleteCashByIDCashflow(id_cashflow);
          return null;
        }
        Cash cashModel = new Cash(cashflow);
        if (product.has("id")) cashModel.setProduct_id(product.getInt("id"));
        UpdateCash(cashModel);
        return cashflow;
      } else if (deleted_at.equalsIgnoreCase("null")) {
        Cash cashModel = new Cash(cashflow);
        if (product.has("id")) cashModel.setProduct_id(product.getInt("id"));
        newTransaction(cashModel);
        return cashflow;
      }
    } catch (Exception e2) {
      Timber.e("ERROR cashflow " + e2);
    }
    return null;
  }

  public JSONObject updateCashflow(JSONObject cashflow, String id_user) {
    try {
      int id_cashflow = cashflow.getInt("id");
      int id_local = 0;
      if (cashflow.has("id_local")) {
        id_local = cashflow.getInt("id_local");
      }
      String deleted_at = cashflow.getString("deleted_at");
      if (CheckCashFlowLocal(id_local)) {
        if (!deleted_at.equalsIgnoreCase("null")) {
          deleteCashByIDCashflowLocal(id_local);
          return null;
        }

        Cash cashModel = new Cash(cashflow);
        UpdateCash(cashModel);
        return cashflow;
      } else if (CheckCashFlow(id_cashflow)) {
        if (!deleted_at.equalsIgnoreCase("null")) {
          deleteCashByIDCashflow(id_cashflow);
          return null;
        }
        Cash cashModel = new Cash(cashflow);
        UpdateCash(cashModel);
        return cashflow;
      } else if (deleted_at.equalsIgnoreCase("null")) {
        Cash cashModel = new Cash(cashflow);
        newTransaction(cashModel);
        return cashflow;
      }
    } catch (Exception e) {

    }
    return null;
  }

  public Cash updateCashflow(cashflow cashflow, int id_user) {
    try {
      int id_cashflow = cashflow.id;
      int id_local = 0;
      Timber.e("aves_ina " + id_cashflow + " " + id_local);
      if (cashflow.id_local > 0) {
        id_local = cashflow.id_local;
      }
      String deleted_at = cashflow.deleted_at;
      if (CheckCashFlowLocal(id_local)) {
        if (deleted_at != null) {
          deleteCashByIDCashflowLocal(id_local);
          return null;
        }

        Cash cashModel = new Cash(cashflow);
        UpdateCash(cashModel);
        return cashModel;
      } else if (CheckCashFlow(id_cashflow)) {
        if (deleted_at != null) {
          deleteCashByIDCashflow(id_cashflow);
          return null;
        }
        Cash cashModel = new Cash(cashflow);
        UpdateCash(cashModel);
        return cashModel;
      } else if (deleted_at == null) {
        Cash cashModel = new Cash(cashflow);

        newTransaction(cashModel);
        return cashModel;
      }
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    }
    return null;
  }

  public Integer getTransactionCount(int iduser) {
    Integer transaction = null;
    String sql = "select count(*) as transaksi from "
        + TAB_CASH
        + " join "
        +
        TAB_PRODUCT
        + " on "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " = "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " join "
        +
        TAB_ACCOUNT
        + " on "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        +
        " where "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + iduser
        + excludeTransferDompet()
        + withoutTrash(TAB_CASH);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.moveToFirst()) {
        transaction = cursor.getInt(cursor.getColumnIndex("transaksi"));
        Timber.e("transaksi " + transaction);
      }
    } finally {
      cursor.close();
    }
    return transaction;
  }

  public Integer getAccountCount(int iduser) {
    Integer transaction = null;
    Cursor cursor = getReadableDatabase().rawQuery("select count(*) as jumlah from "
        + TAB_ACCOUNT
        + " where "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + iduser, null);
    try {
      if (cursor.moveToFirst()) {
        transaction = cursor.getInt(cursor.getColumnIndex("jumlah"));
      }
    } finally {
      cursor.close();
    }
    return transaction;
  }

  public float getTotalSaldoAllAccount(int id_user) {
    float totalsaldo = 0f;
    Cursor cursor = getReadableDatabase().rawQuery(
        "SELECT SUM("
            + BALANCE
            + ") as total from "
            + TAB_PRODUCT
            + " join "
            +
            TAB_ACCOUNT
            + " on "
            + TAB_PRODUCT
            + "."
            + ACCOUNT_ID
            + " = "
            + TAB_ACCOUNT
            + "."
            + ID_ACCOUNT
            + " where "
            + TAB_ACCOUNT
            + "."
            + USER_ID
            + " = "
            + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        totalsaldo = cursor.getFloat(cursor.getColumnIndex("total"));
      }
    } finally {
      cursor.close();
    }
    return totalsaldo;
  }

  public float getTotalSaldoAllTransactions(int id_user) {
    float totalsaldoDB = 0f;
    float totalsaldoCR = 0f;
    Cursor cursorDB =
        getReadableDatabase().rawQuery("SELECT SUM("
            + AMOUNT
            + ") as total from "
            + TAB_CASH
            +
            " join "
            + TAB_PRODUCT
            + " on ("
            + TAB_CASH
            + "."
            + PRODUCT_ID
            + "="
            + TAB_PRODUCT
            + "."
            + ID_PRODUCT
            + ") join "
            +
            TAB_ACCOUNT
            + " on "
            + TAB_PRODUCT
            + "."
            + ACCOUNT_ID
            + " = "
            + TAB_ACCOUNT
            + "."
            + ID_ACCOUNT
            + " where "
            + TAB_CASH
            + "."
            + JENIS_MUTASI
            + "='DB' AND "
            + TAB_ACCOUNT
            + "."
            + USER_ID
            + " = "
            + id_user, null);
    Cursor cursorCR =
        getReadableDatabase().rawQuery("SELECT SUM("
            + AMOUNT
            + ") as total from "
            + TAB_CASH
            +
            " join "
            + TAB_PRODUCT
            + " on ("
            + TAB_CASH
            + "."
            + PRODUCT_ID
            + "="
            + TAB_PRODUCT
            + "."
            + ID_PRODUCT
            + ") join "
            +
            TAB_ACCOUNT
            + " on "
            + TAB_PRODUCT
            + "."
            + ACCOUNT_ID
            + " = "
            + TAB_ACCOUNT
            + "."
            + ID_ACCOUNT
            + " where "
            + TAB_CASH
            + "."
            + JENIS_MUTASI
            + "='CR' AND "
            + TAB_ACCOUNT
            + "."
            + USER_ID
            + " = "
            + id_user, null);

    try {
      if (cursorDB.moveToFirst()) {
        totalsaldoDB = cursorDB.getFloat(cursorDB.getColumnIndex("total"));
      }
      if (cursorCR.moveToFirst()) {
        totalsaldoCR = cursorCR.getFloat(cursorCR.getColumnIndex("total"));
      }
    } finally {
      cursorDB.close();
      cursorCR.close();
    }
    return (totalsaldoCR - totalsaldoDB);
  }

  public Integer getBadgesID(int id_user) {
    int id_badges = 0;
    Cursor cursor = getReadableDatabase().rawQuery("select " + BADGES_ID + " from " + TAB_POINT +
        " where " + ID + "=" + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        id_badges = cursor.getInt(cursor.getColumnIndex(BADGES_ID));
      }
    } finally {
      cursor.close();
    }
    return id_badges;
  }

  public Integer getBadgesCount() {
    Integer badges_count = 0;
    Cursor cursor =
        getReadableDatabase().rawQuery("select count(*) as badges_count from " + TAB_BADGES, null);
    try {
      if (cursor.moveToFirst()) {
        badges_count = cursor.getInt(cursor.getColumnIndex("badges_count"));
      }
    } finally {
      cursor.close();
    }
    return badges_count;
  }

  public void newBadges() {
    String[] badges = new String[] {
        "Beginner's luck", "First Runner", "Baby Dragon", "Cash Ninja", "King Pin", "Gold Digger",
        "Master of Coins", "Iron Bank of Bravoos", "Colombian Cartel", "The Godfather"
    };
    ContentValues cv = new ContentValues();
    for (int i = 0; i < badges.length; i++) {
      cv.put(BADGES_NAME, badges[i].toString());
      Timber.e("avesina newBadges "+cv);
      getWritableDatabase().insert(TAB_BADGES, ID, cv);
    }
  }

  public void insertBadges(String id_user, int id_badges, int point) {
    Timber.e(" avesina badgesCheck "+id_user);
    String id = null;
    int id_badg = 0;
    int poin = 0;
    Date convertedDate = new Date();

    Cursor cursor = getReadableDatabase().rawQuery("select * from " + TAB_POINT + " where " + ID + "=" + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        id = cursor.getString(cursor.getColumnIndex(ID));
        id_badg = cursor.getInt(cursor.getColumnIndex(BADGES_ID));
        poin = cursor.getInt(cursor.getColumnIndex(POINT));
      }
    } finally {
      cursor.close();
    }

    if (id_badges > id_badg) {
      //Insert new Point & History
      if (id == null) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id_user);
        cv.put(BADGES_ID, id_badges);
        cv.put(POINT, point);
        getWritableDatabase().insert(TAB_POINT, ID, cv);
      }
      //Update Point & Insert History
      else {
        point = poin + point;

        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(BADGES_ID, id_badges);
        cv.put(POINT, point);
        getWritableDatabase().update(TAB_POINT, //table
            cv, // column/value
            ID + " = ?", // selections
            new String[] { String.valueOf(id) });
      }

      Calendar c = Calendar.getInstance();
      SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
      try {
        convertedDate = dateFormat2.parse(dateFormat2.format(c.getTime()));
      } catch (ParseException e) {
        e.printStackTrace();
      }

      String m = (String) android.text.format.DateFormat.format("MM", convertedDate); //06
      String y = (String) android.text.format.DateFormat.format("yyyy", convertedDate); //2013
      String d = (String) android.text.format.DateFormat.format("dd", convertedDate); //20

      ContentValues cv = new ContentValues();
      cv.put(TANGGAL, d);
      cv.put(BULAN, m);
      cv.put(TAHUN, y);
      cv.put(POINT, point);
      getWritableDatabase().insert(TAB_HISTORY, ID, cv);
    }
  }

  public Integer getPoint(int id_user) {
    int point = 0;
    Cursor cursor = getReadableDatabase().rawQuery("select " + POINT + " from " + TAB_POINT +
        " where " + ID + "=" + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        point = cursor.getInt(cursor.getColumnIndex(POINT));
      }
    } finally {
      cursor.close();
    }
    return point;
  }

  public String getBadges(int id_user) {
    String badges_name = "";
    Cursor cursor = getReadableDatabase().rawQuery("select b."
        + BADGES_NAME
        + " from "
        + TAB_BADGES
        + " b JOIN "
        + TAB_POINT
        + " p ON b."
        + ID
        + " = "
        + " p."
        + BADGES_ID
        + " WHERE p."
        + ID
        + " = "
        + id_user, null);
    try {
      if (cursor.moveToFirst()) {
        badges_name = cursor.getString(cursor.getColumnIndex(BADGES_NAME)) + "\n";
      }
    } finally {
      cursor.close();
    }
    Timber.e("avesina badgesCheck "+badges_name);
    return badges_name;
  }

  public void badgesCheck(String id_user) {
    int transaction_count = 0;
    float total = 0;
    ArrayList<Product> products;
    products = getAllProductByUser(id_user);
    double saldo = 0f;
    for (int ii = 0; ii < products.size(); ii++) {
      saldo = getTotalSaldo(String.valueOf(products.get(ii).getId_product()), 3);
      total += saldo;
    }
    transaction_count = getTransactionCount(Integer.parseInt(id_user));

    Timber.e("avesina badgesCheck "+transaction_count);
    Timber.e("avesina badgesCheck total"+total);

    if (total >= 100000.0 && transaction_count >= 25) insertBadges(id_user, 1, 1000);
    if (total >= 250000.0 && transaction_count >= 50) insertBadges(id_user, 2, 100);
    if (total >= 500000.0 && transaction_count >= 100) insertBadges(id_user, 3, 250);
    if (total >= 750000.0 && transaction_count >= 200) insertBadges(id_user, 4, 500);
    if (total >= 1000000.0 && transaction_count >= 400) insertBadges(id_user, 5, 750);
    if (total >= 2500000.0 && transaction_count >= 800) insertBadges(id_user, 6, 1000);
    if (total >= 5000000.0 && transaction_count >= 1500) insertBadges(id_user, 7, 1500);
    if (total >= 10000000.0 && transaction_count >= 3000) insertBadges(id_user, 8, 2000);
    if (total >= 50000000.0 && transaction_count >= 6000) insertBadges(id_user, 9, 0);
    if (total >= 100000000.0 && transaction_count >= 10000) insertBadges(id_user, 10, 0);
  }

  public void scraping_atm(JSONObject cashflow, String id_user) {
    try {
      if (cashflow.getString("description").contains("Tarikan ATM") ||
          cashflow.getString("description").contains("SA ATM Withdrawal") ||
          cashflow.getString("description").contains("PENARIKAN DARI ATM") ||
          cashflow.getString("description").contains("PENARIKAN TUNAI")) {
        Cash cash_new = new Cash();
        cash_new.setAmount(cashflow.getLong("debetAmount"));
        cash_new.setType("CR");
        cash_new.setProduct_id(getIdDompetByName("Dompet", id_user));
        updateSaldo(cash_new.getProduct_id(), cash_new.getAmount(), cash_new.getType());
      }
    } catch (Exception e) {
      Timber.e("ERROR scraping_atm" + e.toString());
    }
  }

  /*BEGIN FINPLAN*/
  public long newPlan(Plan plan) {
    Calendar calendar = Calendar.getInstance();
    String dateNow = df.format(calendar.getTime());
    plan.setCreated_at(dateNow);
    plan.setUpdated_at(dateNow);
    plan.setPlan_date_star(dateNow);
    ContentValues values = getContentValuesPlan(plan);
    return getWritableDatabase().insert(TAB_PLAN, ID, values);
  }

  public void newDanaPensiun(DanaPensiun danaPensiun) {
    Calendar calendar = Calendar.getInstance();
    String dateNow = df.format(calendar.getTime());
    danaPensiun.setCreated_at(dateNow);
    ContentValues values = getContentValuesDanaPensiun(danaPensiun);
    getWritableDatabase().insert(TAB_DANA_PENSIUN, ID, values);
  }

  public void newDanaDarurat(DanaDarurat danaDarurat) {
    Calendar calendar = Calendar.getInstance();
    String dateNow = df.format(calendar.getTime());
    danaDarurat.setCreated_at(dateNow);
    ContentValues values = getContentValuesDanaDarurat(danaDarurat);
    getWritableDatabase().insert(TAB_DANA_DARURAT, ID, values);
  }

  public void newDanaKuliah(DanaKuliah danaKuliah) {
    ContentValues values = getContentValuesDanaKuliah(danaKuliah);
    getWritableDatabase().insert(TAB_DANA_KULIAH, ID, values);
  }

  public void newPlanProduct(ProductPlan productPlan) {
    Calendar calendar = Calendar.getInstance();
    String dateNow = df.format(calendar.getTime());
    productPlan.setCreated_at(dateNow);
    ContentValues values = getContentValuesProductPlan(productPlan);
    getWritableDatabase().insert(TAB_PLAN_PRODUCT, ID, values);
  }

  public int updatePlan(Plan plan) {
    ContentValues cv = getContentValuesPlan(plan);

    int i = 0;
    if (plan.getId() == 0) {
      i = getWritableDatabase().update(TAB_PLAN, cv, ID_PLAN + " = ?",
          new String[] { String.valueOf(plan.getId_plan()) }); //selection args
    } else {
      i = getWritableDatabase().update(TAB_PLAN, cv, ID + " = ?",
          new String[] { String.valueOf(plan.getId()) }); //selection args
    }

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_PLAN, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }

  public int updateDanaPensiun(DanaPensiun danaPensiun) {
    ContentValues cv = getContentValuesDanaPensiun(danaPensiun);

    int i = getWritableDatabase().update(TAB_DANA_PENSIUN, cv, ID + " = ?",
        new String[] { String.valueOf(danaPensiun.getId()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_DANA_PENSIUN, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }

  public int updateDanaDarurat(DanaDarurat danaDarurat) {
    ContentValues cv = getContentValuesDanaDarurat(danaDarurat);

    int i = getWritableDatabase().update(TAB_DANA_DARURAT, cv, ID + " = ?",
        new String[] { String.valueOf(danaDarurat.getId()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_DANA_DARURAT, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }

  public int updateDanaKuliah(DanaKuliah danaKuliah) {
    ContentValues cv = getContentValuesDanaKuliah(danaKuliah);

    int i = getWritableDatabase().update(TAB_DANA_KULIAH, cv, ID + " = ?",
        new String[] { String.valueOf(danaKuliah.getId()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_DANA_KULIAH, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }

  public int updatePlanByID(Plan plan) {
    ContentValues cv = new ContentValues();
    cv.put(ID, plan.getId());
    cv.put(ID_PLAN, plan.getId_plan());
    cv.put(USER_ID, plan.getUser_id());
    cv.put(PRODUCT_ID, plan.getProduct_id());
    cv.put(CATEGORY_ID, plan.getCategory_id());
    cv.put(PLAN_TITLE, plan.getPlan_title());
    cv.put(PLAN_TOTAL, plan.getPlan_total());
    cv.put(LIFE_TIME, plan.getLifetime());
    cv.put(PLAN_AMOUNT_MONTHLY, plan.getPlan_amount_monthly());
    cv.put(PLAN_AMOUNT_YEARLY, plan.getPlan_amount_yearly());
    cv.put(PLAN_AMOUNT_CASH, plan.getPlan_amount_cash());
    cv.put(PLAN_RISK, plan.getPlan_risk());
    cv.put(CREATED_AT, plan.getCreated_at());
    cv.put(UPDATED_AT, plan.getUpdated_at());
    cv.put(DELETED_AT, plan.getDeleted_at());

    int i = getWritableDatabase().update(TAB_PLAN, //table
        cv, // column/value
        ID + " = ?", // selections
        new String[] { String.valueOf(plan.getId()) }); //selection args

    Cursor cursor = getReadableDatabase().rawQuery("select changes() as affected_row", null);

    try {
      if (cursor.moveToFirst()) {
        int rows = cursor.getInt(cursor.getColumnIndex("affected_row"));

        if (rows == 0) {
          getWritableDatabase().insert(TAB_PLAN, ID, cv);
        }
      }
    } finally {
      cursor.close();
    }
    return i;
  }

  public ArrayList<Plan> getAllPlan(String id) {
    ArrayList<Plan> mListModel = new ArrayList<Plan>();
    SQLiteDatabase db = this.getReadableDatabase();

    String selectQuery =
        "SELECT * FROM " + TAB_PLAN + " WHERE " + USER_ID + "=" + id + " " + withoutTrash(TAB_PLAN);

    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          Plan model = cursorToPlan(cursor);
          mListModel.add(model);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return mListModel;
  }

  public Double getTotalSaldoPlan(String depend, int value) {
    SQLiteDatabase db = this.getReadableDatabase();
    float saldo = 0;
    double total = 0.0;
    String selectQuery = null;

    if (depend.equalsIgnoreCase(TAB_USER)) {
      selectQuery = "SELECT "
          + TAB_PLAN
          + ".* FROM "
          + TAB_PLAN
          + " JOIN "
          + TAB_PRODUCT
          + " ON "
          + TAB_PLAN
          + "."
          + PRODUCT_ID
          + " = "
          + TAB_PRODUCT
          + "."
          + ID_PRODUCT
          + " JOIN "
          + TAB_ACCOUNT
          + " ON "
          + TAB_PRODUCT
          + "."
          + ACCOUNT_ID
          + " = "
          + TAB_ACCOUNT
          + "."
          + ID_ACCOUNT
          + " WHERE "
          + TAB_ACCOUNT
          + "."
          + USER_ID
          + " = "
          + value;
    }

    Cursor cursor = db.rawQuery(selectQuery, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          saldo = cursor.getFloat(cursor.getColumnIndex(PLAN_TOTAL));
          total += saldo;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return total;
  }

  public ArrayList<String> getAllPlanAccount(String id_user) {
    String sql = null;
    sql = "SELECT * FROM "
        + TAB_PRODUCT
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " = "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        +
        " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + " = "
        + id_user
        + " AND "
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + " = 9";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<String> mModels = new ArrayList<>();
    try {
      while (cursor.moveToNext()) {
        mModels.add(cursor.getString(cursor.getColumnIndex(NAME_PRODUCT)));
      }
    } finally {
      cursor.close();
    }
    return mModels;
  }

  public Plan getPlanBy(String depend, int id) {
    Plan plan = null;
    String sql = null;
    if (depend.equalsIgnoreCase(TAB_PLAN)) {
      sql = "SELECT * FROM " + TAB_PLAN + " WHERE " + ID_PLAN + " = " + id;
    }
    if (depend.equalsIgnoreCase(PLAN_ID_LOCAL)) {
      sql = "SELECT * FROM " + TAB_PLAN + " WHERE " + ID + "=" + id;
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.moveToFirst()) {
        plan = cursorToPlan(cursor);
      }
    } finally {
      cursor.close();
    }
    return plan;
  }
    /*END FINPLAN*/

  /* NEW DESIGN ALL TRANSACTIONS */
  public ArrayList<FragmentModel> getAllTransactionsDate(String filter, String tag,Integer[] product_id, Integer filter_tahun, Integer[] ids,Integer account,String status) {
    int user_id = Integer.parseInt(sessionManager.getIdUser());
    String sql_where = "";
    if (filter.equalsIgnoreCase("CR") || filter.equalsIgnoreCase("DB")) {
      sql_where = " AND " + TAB_CASH + "." + JENIS_MUTASI + "='" + filter + "'";
    }

    String sql_where_product_id = "";
    if (product_id != null) {
      if (product_id.length > 0) {
        sql_where_product_id = " AND "
            + TAB_PRODUCT
            + "."
            + ID_PRODUCT
            + " in ("
            + Helper.imploded(product_id, ",")
            + ") ";
      }
    }

    String sql_account = "";
    if(account != null){
      sql_account = " AND "+TAB_ACCOUNT+"."+ID_ACCOUNT+"="+account+" ";
    }

    String sql_status ="";
    if(status != null){
      sql_status = " AND "+TAB_CASH+"."+STATUS+"='"+status+"' ";
    }

    String sql_where_tag = "";
    if (!tag.equalsIgnoreCase("")) {
      tag = "%" + tag + "%";
      sql_where_tag = " AND " + TAB_CASH + "." + CASHFLOW_TAG + " like '" + tag + "' ";
    }

    String sql_tahun = "";
    if (filter_tahun != null) {
      sql_tahun =
          " AND strftime('%Y'," + TAB_CASH + "." + CASHFLOW_DATE + ")='" + filter_tahun + "' ";
    }

    String sql_ids = "";
    if (ids != null) {
      sql_ids = " AND " + TAB_CASH + "." + ID + " in (" + Helper.imploded(ids, ",") + ") ";
    }

    String sql = "SELECT CAST(strftime('%m', "+ CASHFLOW_DATE+ ") as decimal) as "+ BULAN + ", CAST(strftime('%Y',"+ CASHFLOW_DATE + ")as decimal ) as "
        + TAHUN
        + " FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(String.valueOf(user_id))
        + " "
        + sql_where
        + " "
        + sql_where_product_id
        + sql_where_tag
        + sql_tahun
        + sql_ids
        + sql_account
        + sql_status
        + withoutTrash(TAB_CASH)
        + " GROUP BY strftime('%Y-%m', "
        + CASHFLOW_DATE
        + ") ORDER BY "
        + CASHFLOW_DATE
        + " ASC";
    Timber.e("SEQUEL " + sql);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<FragmentModel> fragmentModelList = new ArrayList<>();
    final SimpleDateFormat dateFormat =
        new SimpleDateFormat(GeneralHelper.MMM_YYYY, State.getLocale());
    try {
      if (cursor.getCount() > 0) {
        if (cursor.moveToFirst()) {
          Calendar calendar = Calendar.getInstance();
          do {
            FragmentModel fragmentModel = new FragmentModel();
            int tahun = cursor.getInt(cursor.getColumnIndex(TAHUN));
            int bulan = cursor.getInt(cursor.getColumnIndex(BULAN));
            calendar.set(Calendar.YEAR, Integer.valueOf(tahun));
            calendar.set(Calendar.MONTH, Integer.valueOf(bulan) - 1);
            String title = dateFormat.format(calendar.getTime());
            fragmentModel.setTitle(title);
            fragmentModelList.add(fragmentModel.setMonth(String.valueOf(bulan)).setYear(String.valueOf(tahun)));
          } while (cursor.moveToNext());
          //calendar.add(Calendar.MONTH, 1);
          //String title = dateFormat.format(calendar.getTime());
          //FragmentModel fragmentModel = new FragmentModel();
          //fragmentModel.setTitle(title);
          //fragmentModelList.add(
          //    fragmentModel.setMonth(String.valueOf(calendar.get(Calendar.MONTH) + 1))
          //        .setYear(String.valueOf(calendar.get(Calendar.YEAR))));
        }
      }
    } finally {
      cursor.close();
    }
    return fragmentModelList;
  }

  public ArrayList<FragmentModel> getAllTransactionsCategory(String filter, String tag,
      Integer[] product_id, Integer tahun, Integer[] ids,Integer account,String status) {
    String sql_where = "";
    if (filter.equalsIgnoreCase("CR") || filter.equalsIgnoreCase("DB")) {
      sql_where = " AND " + TAB_CASH + "." + JENIS_MUTASI + "='" + filter + "'";
    }
    String sql_where_product_id = "";
    if (product_id != null) {
      if (product_id.length > 0) {
        sql_where_product_id = " AND "
            + TAB_PRODUCT
            + "."
            + ID_PRODUCT
            + " in ("
            + Helper.imploded(product_id, ",")
            + ") ";
      }
    }


    String sql_account = "";
    if(account != null){
      sql_account = " AND "+TAB_ACCOUNT+"."+ID_ACCOUNT+"="+account+" ";
    }

    String sql_status ="";
    if(status != null){
      sql_status = " AND "+TAB_CASH+"."+STATUS+"='"+status+"' ";
    }

    String sql_where_tag = "";
    if (!tag.equalsIgnoreCase("")) {
      tag = "%" + tag + "%";
      sql_where_tag = " AND " + TAB_CASH + "." + CASHFLOW_TAG + " like '" + tag + "' ";
    }

    String sql_tahun = "";
    if (tahun != null) {
      sql_tahun = " AND strftime('%Y'," + TAB_CASH + "." + CASHFLOW_DATE + ")='" + tahun + "' ";
    }

    String sql_ids = "";
    if (ids != null) {
      sql_ids = " AND " + TAB_CASH + "." + ID + " in (" + Helper.imploded(ids, ",") + ") ";
    }

    String sql = "SELECT "
        + CATEGORY_ID
        + ","
        + NAME_CATEGORY
        + " FROM "
        + TAB_CASH
        + " JOIN "
        + TAB_CATEGORY
        + " ON ("
        + TAB_CATEGORY
        + "."
        + ID_CATEGORY
        + " = "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + ") "
        + generatequerybyuserid(String.valueOf(sessionManager.getIdUser()))
        + " "
        + sql_where
        + " "
        + sql_where_product_id
        + sql_where_tag
        + sql_tahun
        + sql_ids
        + sql_account
        + sql_status
        + withoutTrash(TAB_CASH)
        + " GROUP BY "
        + CATEGORY_ID
        + " ORDER BY "
        + CASHFLOW_DATE
        + " DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<FragmentModel> fragmentModelList = new ArrayList<>();
    try {
      if (cursor.getCount() > 0) {
        int i = 0;
        if (cursor.moveToFirst()) {
          do {
            FragmentModel fragmentModel = new FragmentModel();
            Integer category_id = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
            String name_category = cursor.getString(cursor.getColumnIndex(NAME_CATEGORY));
            fragmentModel.setTitle(name_category);
            fragmentModel.category_id = category_id;
            fragmentModelList.add(fragmentModel.setCategory_id(category_id));
          } while (cursor.moveToNext());
        }
      }
    } finally {
      cursor.close();
    }
    return fragmentModelList;
  }

  public ArrayList<FragmentModel> getAllTransactionsAccount(int user_id, String filter, String tag,
      Integer tahun, Integer[] ids) {
    String sql_where = "";
    if (filter.equalsIgnoreCase("CR") || filter.equalsIgnoreCase("DB")) {
      sql_where = " AND " + TAB_CASH + "." + JENIS_MUTASI + "='" + filter + "'";
    }

    String sql_where_tag = "";
    if (!tag.equalsIgnoreCase("")) {
      tag = "%" + tag + "%";
      sql_where_tag = " AND " + TAB_CASH + "." + CASHFLOW_TAG + " like '" + tag + "' ";
    }

    String sql_tahun = "";
    if (tahun != null) {
      sql_tahun = " AND strftime('%Y'," + TAB_CASH + "." + CASHFLOW_DATE + ")='" + tahun + "' ";
    }

    String sql_ids = "";
    if (ids != null) {
      sql_ids = " AND " + TAB_CASH + "." + ID + " in (" + Helper.imploded(ids, ",") + ") ";
    }

    String sql = "SELECT "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + ", "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + ","
        + TAB_VENDORS
        + "."
        + VENDOR_NAME
        + ","
        + TAB_ACCOUNT
        + "."
        + NAME_ACCOUNT
        + " FROM "
        + TAB_CASH
        + " "
        +
        "JOIN "
        + TAB_PRODUCT
        + " ON ("
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " = "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + ") "
        +
        "JOIN "
        + TAB_ACCOUNT
        + " ON ("
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " = "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + ") "
        +
        "JOIN "
        + TAB_VENDORS
        + " ON ("
        + TAB_VENDORS
        + "."
        + ID_VENDOR
        + " = "
        + TAB_ACCOUNT
        + "."
        + VENDOR_ID
        + ") "
        +
        " WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + user_id
        + " "
        +
        " "
        + sql_where
        + sql_where_tag
        + sql_tahun
        + sql_ids
        + withoutTrash(TAB_CASH)
        + " "
        +
        "GROUP BY "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " ORDER BY "
        + CASHFLOW_DATE
        + " DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<FragmentModel> fragmentModelList = new ArrayList<>();
    try {
      if (cursor.getCount() > 0) {
        int i = 0;

        if (cursor.moveToFirst()) {
          do {
            FragmentModel fragmentModel = new FragmentModel();
            Integer account_id = cursor.getInt(cursor.getColumnIndex(ID_ACCOUNT));
            String vendor_name = mcrypt.decrypt(cursor.getString(cursor.getColumnIndex(NAME_ACCOUNT)));
            fragmentModel.setTitle(vendor_name);
            fragmentModelList.add(fragmentModel.setAcount_id(account_id));
          } while (cursor.moveToNext());
        }
      }
    } finally {
      cursor.close();
    }
    return fragmentModelList;
  }

  public ArrayList<Transaction> getAllTransaction(String id_user, Integer selectedAccount,
      String selectedType, Integer selectedCategory, String selectedTag, String selectedDateStart,
      String selectedDateEnd) {
    String sql_account = "";
    if (selectedAccount != null) {
      sql_account = " AND " + TAB_ACCOUNT + "." + ID_ACCOUNT + "=" + selectedAccount + " ";
    }
    String sql_type = "";
    if (selectedType != null) {
      sql_type = "AND " + TAB_CASH + "." + JENIS_MUTASI + "='" + selectedType + "' ";
    }
    String sql_date = "";
    if (selectedDateStart != null && selectedDateStart != null) {
      sql_date = " AND DATE("
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + ") BETWEEN DATE('"
          + selectedDateStart
          + "') AND DATE('"
          + selectedDateEnd
          + "') ";
    }
    String sql_category = "";
    if (selectedCategory != null) {
      sql_category = " AND " + TAB_CASH + "." + CATEGORY_ID + "=" + selectedCategory + " ";
    }
    String sql_tag = "";
    if (!TextUtils.isEmpty(selectedTag)) {
      sql_tag = " AND " + TAB_CASH + "." + CASHFLOW_TAG + " like '%" + selectedTag + "%' ";
    }
    String sql = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(id_user)
        + withoutTrash(TAB_CASH)
        + sql_account
        + sql_type
        + sql_date
        + sql_category
        + " ORDER BY "
        + CASHFLOW_DATE
        + " DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    return populateHeaderCash(cursor);
  }

  public ArrayList<Cash> getAllCash(String id_user, Integer selectedAccount, String selectedType,
      Integer selectedCategory, String selectedTag, String selectedDateStart,
      String selectedDateEnd) {
    String sql_account = "";
    if (selectedAccount != null) {
      sql_account = " AND " + TAB_ACCOUNT + "." + ID_ACCOUNT + "=" + selectedAccount + " ";
    }
    String sql_type = "";
    if (selectedType != null) {
      sql_type = "AND " + TAB_CASH + "." + JENIS_MUTASI + "='" + selectedType + "' ";
    }
    String sql_date = "";
    if (selectedDateStart != null && selectedDateStart != null) {
      sql_date = " AND DATE("
          + TAB_CASH
          + "."
          + CASHFLOW_DATE
          + ") BETWEEN DATE('"
          + selectedDateStart
          + "') AND DATE('"
          + selectedDateEnd
          + "') ";
    }
    String sql_category = "";
    if (selectedCategory != null) {
      sql_category = " AND " + TAB_CASH + "." + CATEGORY_ID + "=" + selectedCategory + " ";
    }
    String sql_tag = "";
    if (!TextUtils.isEmpty(selectedTag)) {
      sql_tag = " AND " + TAB_CASH + "." + CASHFLOW_TAG + " like '%" + selectedTag + "%' ";
    }
    String sql = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(id_user)
        + withoutTrash(TAB_CASH)
        + sql_account
        + sql_type
        + sql_date
        + sql_category
        + " ORDER BY "
        + CASHFLOW_DATE
        + " DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> cashes = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          cashes.add(cash);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return cashes;
  }

  public ArrayList<Transaction> getTransactionDate(int month, int year, String filter, String tag,
      Integer[] product_id, Integer tahun, Integer[] ids,Integer account_id,String status) {
    int user_id = Integer.parseInt(sessionManager.getIdUser());
    String sql_where = "";
    if (filter.equalsIgnoreCase("CR") || filter.equalsIgnoreCase("DB")) {
      sql_where = " AND " + TAB_CASH + "." + JENIS_MUTASI + "='" + filter + "'";
    }

    String sql_where_tag = "";
    if (!tag.equalsIgnoreCase("")) {
      tag = "%" + tag + "%";
      sql_where_tag = " AND " + TAB_CASH + "." + CASHFLOW_TAG + " like '" + tag + "' ";
    }

    String sql_status = "";
    if(status!=null){
      sql_status = " AND "+TAB_CASH+"."+STATUS+"='"+status+"' ";
    }

    String sql_account_id = "";
    if(account_id!=null){
      sql_account_id = " AND "+TAB_ACCOUNT+"."+ID_ACCOUNT+"="+account_id+" ";
    }

    String sql_where_product_id = "";
    if (product_id != null) {
      if (product_id.length > 0) {
        sql_where_product_id = " AND "
            + TAB_PRODUCT
            + "."
            + ID_PRODUCT
            + " in ("
            + Helper.imploded(product_id, ",")
            + ")";
      }
    }

    String sql_tahun = "";
    if (tahun != null) {
      sql_tahun = " AND strftime('%Y'," + TAB_CASH + "." + CASHFLOW_DATE + ")='" + tahun + "' ";
    }

    String sql_ids = "";
    if (ids != null) {
      sql_ids = " AND " + TAB_CASH + "." + ID + " in (" + Helper.imploded(ids, ",") + ") ";
    }

    String sql = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(String.valueOf(user_id))
        + " AND "
        + BULAN
        + "="
        + month
        + " AND "
        + TAHUN
        + "="
        + year
        + ""
        + sql_where
        + ""
        + sql_where_product_id
        + sql_where_tag
        + sql_tahun
        + sql_ids
        + sql_account_id
        + sql_status
        + withoutTrash(TAB_CASH)
        + " ORDER BY "
        + CASHFLOW_DATE

        + " DESC, "
        + ID
        + " DESC ";
    Timber.e("avesina sql heloo" + sql);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    return populateHeaderCash(cursor);
  }

  public ArrayList<Transaction> getTransactionCategory(int category_id, String filter, String tag,
      Integer[] product_id, Integer tahun, Integer[] ids,Integer account_id,String status) {
    String sql_where = "";
    int user_id = Integer.parseInt(sessionManager.getIdUser());
    if (filter.equalsIgnoreCase("CR") || filter.equalsIgnoreCase("DB")) {
      sql_where = " AND " + TAB_CASH + "." + JENIS_MUTASI + "='" + filter + "'";
    }

    String sql_where_product_id = "";
    if (product_id != null) {
      if (product_id.length > 0) {
        sql_where_product_id = " AND "
            + TAB_PRODUCT
            + "."
            + ID_PRODUCT
            + " in ("
            + Helper.imploded(product_id, ",")
            + ") ";
      }
    }

    String sql_where_tag = "";
    if (!tag.equalsIgnoreCase("")) {
      tag = "%" + tag + "%";
      sql_where_tag = " AND " + TAB_CASH + "." + CASHFLOW_TAG + " like '" + tag + "' ";
    }

    String sql_tahun = "";
    if (tahun != null) {
      sql_tahun = " AND strftime('%Y'," + TAB_CASH + "." + CASHFLOW_DATE + ")='" + tahun + "' ";
    }

    String sql_ids = "";
    if (ids != null) {
      sql_ids = " AND " + TAB_CASH + "." + ID + " in (" + Helper.imploded(ids, ",") + ") ";
    }

    String sql_status = "";
    if(status!=null){
      sql_status = " AND "+TAB_CASH+"."+STATUS+"='"+status+"' ";
    }

    String sql_account_id = "";
    if(account_id!=null){
      sql_account_id = " AND "+TAB_ACCOUNT+"."+ID_ACCOUNT+"="+account_id+" ";
    }

    String sql = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(String.valueOf(user_id))
        + " AND "
        + CATEGORY_ID
        + "="
        + category_id
        + ""
        + sql_where
        + ""
        + sql_where_product_id
        + sql_where_tag
        + sql_tahun
        + sql_ids
        + sql_account_id
        + sql_status
        + withoutTrash(TAB_CASH)
        + "  ORDER BY "
        + CASHFLOW_DATE
        + " DESC , "
        + ID
        + " DESC";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    return populateHeaderCash(cursor);
  }

  public ArrayList<Transaction> getTransactionAccount(int account_id, String filter, String tag,
      Integer tahun, Integer[] ids,String status) {
    String sql_where = "";
    int user_id = Integer.parseInt(sessionManager.getIdUser());
    if (filter.equalsIgnoreCase("CR") || filter.equalsIgnoreCase("DB")) {
      sql_where = " AND " + TAB_CASH + "." + JENIS_MUTASI + "='" + filter + "'";
    }

    String sql_where_tag = "";
    if (!tag.equalsIgnoreCase("")) {
      tag = "%" + tag + "%";
      sql_where_tag = " AND " + TAB_CASH + "." + CASHFLOW_TAG + " like '" + tag + "' ";
    }

    String sql_status = "";
    if(status != null){
      sql_status = " AND "+TAB_CASH+"."+STATUS+"='"+status+"' ";
    }

    String sql_tahun = "";
    if (tahun != null) {
      sql_tahun = " AND strftime('%Y'," + TAB_CASH + "." + CASHFLOW_DATE + ")='" + tahun + "' ";
    }

    String sql_ids = "";
    if (ids != null) {
      sql_ids = " AND " + TAB_CASH + "." + ID + " in (" + Helper.imploded(ids, ",") + ") ";
    }

    String sql = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + "JOIN "
        + TAB_PRODUCT
        + " ON ("
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " = "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + ") "
        + "JOIN "
        + TAB_ACCOUNT
        + " ON ("
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " = "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + ") "
        + "WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + user_id
        + " AND "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + "="
        + account_id
        + " "
        + ""
        + sql_where
        + sql_where_tag
        + sql_tahun
        + sql_ids
        + sql_status
        + withoutTrash(TAB_CASH)
        + "ORDER BY "
        + CASHFLOW_DATE
        + " DESC , "
        + ID
        + " DESC";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    return populateHeaderCash(cursor);
  }

  public ArrayList<Transaction> populateHeaderCash(Cursor cursor) {
    Map<String, ArrayList<Cash>> map = new HashMap<>();
    try {
      int index = 0;
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          // triky
          Account account = getAccountById(cash.getProduct_id(), 3, 0);
          account.setName(mcrypt.decrypt(account.getName()));
          cash.setAccount(account);
          if (index == 0 || !map.containsKey(cash.getCash_date())) {
            ArrayList<Cash> cashs = new ArrayList<>();
            cashs.add(cash);
            map.put(cash.getCash_date(), cashs);
          } else {
            if (map.containsKey(cash.getCash_date())) {
              ArrayList<Cash> cashs = map.get(cash.getCash_date());
              cashs.add(cash);
              map.put(cash.getCash_date(), cashs);
            }
          }
          index += 1;
        } while (cursor.moveToNext());
      }
      TreeMap<String, ArrayList<Cash>> treeMap = new TreeMap<String, ArrayList<Cash>>(map);
      NavigableMap<String, ArrayList<Cash>> descMap = treeMap.descendingMap();
      ArrayList<Transaction> transactions = new ArrayList<>();
      for (Map.Entry<String, ArrayList<Cash>> entry : descMap.entrySet()) {
        Transaction transaction = new Transaction();
        transaction.date = entry.getKey();
        transaction.cashs = entry.getValue();
        transaction.amount = getAmount(entry.getValue());
        transactions.add(transaction);
      }
      return transactions;
    } finally {
      cursor.close();
    }
    //        return;
  }

  public Double getAmount(List<Cash> cashList) {
    if (cashList.size() > 0) {
      Double temp = 0.0;
      for (final Cash cash : cashList) {
        if (cash.getType().equalsIgnoreCase("CR")) {
          temp += cash.getAmount();
        } else if (cash.getType().equalsIgnoreCase("DB")) {
          temp -= cash.getAmount();
        }
      }
      return temp;
    }
    return 0.0;
  }
    /* ____________________________*/

  public HashMap<String, Double> getGroupAndSumCashflowBy(String type, int daysago) {
    HashMap<String, Double> values = new HashMap<>();
    String sql = "SELECT "
        + CASHFLOW_DATE
        + ",SUM("
        + AMOUNT
        + ") as jumlah FROM "
        + TAB_CASH
        + " WHERE "
        + JENIS_MUTASI
        + "='"
        + type
        + "' AND "
        + CASHFLOW_DATE
        + " > (SELECT DATETIME('now', '-"
        + daysago
        + " day')) GROUP BY "
        + CASHFLOW_DATE
        + "";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.getCount() > 0 && cursor.moveToFirst()) {
        do {
          String date = cursor.getString(cursor.getColumnIndex(CASHFLOW_DATE));
          Double jumlah = cursor.getDouble(cursor.getColumnIndex("jumlah"));
          values.put(date, jumlah);
        } while (cursor.moveToNext());
      } else {
        Calendar c = Calendar.getInstance();
        values.put(df.format(c.getTime()), 0.0);
      }
    } finally {
      cursor.close();
    }
    return values;
  }

  // tag
  public void addTag(String name) {

  }

  public String[] getList() {
    return new String[1];
  }

  // detail trends
  public float getTotalCashByDaysAgo(String type_mutasi, int days, String id_user) {
    String sql = "";
    if (type_mutasi.equalsIgnoreCase("All")) {
      sql = "SELECT SUM(" + AMOUNT + ") as total FROM " + TAB_CASH + " " + generatequerybyuserid(
          id_user) + " " +
          "AND " + CASHFLOW_DATE + " > (SELECT DATETIME('now', '-" + days + " day'))";
    } else {
      sql = "SELECT SUM(" + AMOUNT + ") as total FROM " + TAB_CASH + " " + generatequerybyuserid(
          id_user) + " AND " + JENIS_MUTASI + "='" + type_mutasi + "' " +
          "AND " + CASHFLOW_DATE + " > (SELECT DATETIME('now', '-" + days + " day'))";
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.moveToFirst()) {
        return cursor.getFloat(cursor.getColumnIndex("total"));
      }
    } finally {
      cursor.close();
    }
    return 0;
  }

  public int getCountCashByDaysAgo(String type_mutasi, int days, String id_user) {
    String sql = "";
    if (type_mutasi.equalsIgnoreCase("All")) {
      sql = "SELECT count("
          + CASHFLOW_ID
          + ") as count FROM "
          + TAB_CASH
          + " "
          + generatequerybyuserid(id_user)
          + " "
          +
          "AND "
          + CASHFLOW_DATE
          + " > (SELECT DATETIME('now', '-"
          + days
          + " day'))";
    } else {
      sql = "SELECT count("
          + CASHFLOW_ID
          + ") as count FROM "
          + TAB_CASH
          + " "
          + generatequerybyuserid(id_user)
          + " AND "
          + JENIS_MUTASI
          + "='"
          + type_mutasi
          + "' "
          +
          "AND "
          + CASHFLOW_DATE
          + " > (SELECT DATETIME('now', '-"
          + days
          + " day'))";
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.moveToFirst()) {
        return cursor.getInt(cursor.getColumnIndex("count"));
      }
    } finally {
      cursor.close();
    }
    return 0;
  }

  public String generatequerybyuserid(String id_user) {
    String sql = "JOIN "
        + TAB_PRODUCT
        + " ON ("
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + " = "
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + ") "
        +
        "JOIN "
        + TAB_ACCOUNT
        + " ON ("
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + " = "
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + ") "
        +
        "WHERE "
        + TAB_ACCOUNT
        + "."
        + USER_ID
        + "="
        + id_user;

    return sql;
  }

  public double getTotalCashByMonthByTypeMustasi(int month, String type_mutasi, String id_user) {
    String sql =
        "SELECT SUM(" + AMOUNT + ") as total FROM " + TAB_CASH + " " + generatequerybyuserid(
            id_user) + " AND " + BULAN + "=" + month + "";

    if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql += " AND " + JENIS_MUTASI + " = '" + type_mutasi + "'";
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.moveToFirst()) {
        if (cursor.getCount() > 0) {
          return cursor.getDouble(cursor.getColumnIndex("total"));
        }
      }
    } finally {
      cursor.close();
    }
    return 0;
  }

  public ArrayList<Cash> getJumlahDataByYear(int tahun, String type_mutasi, String id_user) {
    String sql = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(id_user)
        +withoutTrash(TAB_CASH)
        + withoutTransfer()
        + " AND CAST(strftime('%Y', "
        + CASHFLOW_DATE
        + ") as INTEGER) = "
        + tahun
        + " ";

    // SELECT SUM(debet) as total, CAST(strftime('%m', cashflow_date)as INTEGER) as bulan FROM cashflows WHERE CAST(strftime('%Y', cashflow_date)as INTEGER) = 2015 AND CAST(strftime('%m', cashflow_date)as INTEGER)= 2

    if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql += " AND " + JENIS_MUTASI + " = '" + type_mutasi + "'";
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> cashes = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do{
          Cash cash = cursorToCash(cursor);
          cashes.add(cash);
        }while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return cashes;
  }

  public ArrayList<Cash> getJumlahDataByDate(String date_s, String date_e, String type_mutasi, String id_user) {
    String sql = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(id_user)
        + withoutTrash(TAB_CASH)
        + withoutTransfer()
        + " AND DATE("
        + CASHFLOW_DATE
        + ") BETWEEN DATE('"
        + date_s
        + "') AND DATE('"
        + date_e
        + "') ";

    // SELECT SUM(debet) as total, CAST(strftime('%m', cashflow_date)as INTEGER) as bulan FROM cashflows WHERE CAST(strftime('%Y', cashflow_date)as INTEGER) = 2015 AND CAST(strftime('%m', cashflow_date)as INTEGER)= 2

    if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql += " AND " + JENIS_MUTASI + " = '" + type_mutasi + "'";
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> cashes = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          Cash c  = cursorToCash(cursor);
          cashes.add(c);
        }while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return cashes;
  }

  public double getTotalCashByMonthByTypeMustasi(int tahun, int month, String type_mutasi,
      String id_user) {
    String sql =
        "SELECT SUM("
            + AMOUNT
            + ") as total FROM "
            + TAB_CASH
            + " "
            + generatequerybyuserid(id_user)
            + withoutTrash(TAB_CASH)
            + withoutTransfer()
            + " AND CAST(strftime('%Y', "
            + CASHFLOW_DATE
            + ") as INTEGER) = "
            + tahun
            + " AND CAST(strftime('%m', "
            + CASHFLOW_DATE
            + ") as INTEGER)="
            + month
            + "";

    // SELECT SUM(debet) as total, CAST(strftime('%m', cashflow_date)as INTEGER) as bulan FROM cashflows WHERE CAST(strftime('%Y', cashflow_date)as INTEGER) = 2015 AND CAST(strftime('%m', cashflow_date)as INTEGER)= 2

    if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql += " AND " + JENIS_MUTASI + " = '" + type_mutasi + "'" + excludeTransferDompet();
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.moveToFirst()) {
        if (cursor.getCount() > 0) {
          return cursor.getDouble(cursor.getColumnIndex("total"));
        }
      }
    } finally {
      cursor.close();
    }
    return 0;
  }

  public double getTotalCashByDateByTypeMustasi(String date_start, String date_end,
      String type_mutasi, String id_user) {
    String sql =
        "SELECT SUM("
            + AMOUNT
            + ") as total FROM "
            + TAB_CASH
            + " "
            + generatequerybyuserid(id_user)
            + withoutTrash(TAB_CASH)
            + withoutTransfer()
            + " AND DATE("
            + CASHFLOW_DATE
            + ") BETWEEN DATE('"
            + date_start
            + "') AND DATE('"
            + date_end
            + "') ";

    if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql += " AND " + JENIS_MUTASI + " = '" + type_mutasi + "'" + excludeTransferDompet();
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      if (cursor.moveToFirst()) {
        if (cursor.getCount() > 0) {
          return cursor.getDouble(cursor.getColumnIndex("total"));
        }
      }
    } finally {
      cursor.close();
    }
    return 0;
  }

  public String excludeTransferDompet() {
    String exclude = "";
    String ex = "%" + "Transfer saldo ke Dompet" + "%";
    exclude = " AND (("
        + TAB_CASH
        + "."
        + CASHFLOW_NAME
        + " NOT LIKE '"
        + ex
        + "' AND "
        + JENIS_MUTASI
        + "='DB') OR "
        + JENIS_MUTASI
        + "='CR') ";
    return exclude;
  }

  public ArrayList<Cash> getCashByMonthByTypeMutasi(int tahun, int month, String type_mutasi,
      int user_id) {
    String sql = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(String.valueOf(user_id))
        + " AND strftime('%Y', "
        + CASHFLOW_DATE
        + ")='"
        + tahun
        + "' "
        + " AND CAST(strftime('%m',"
        + CASHFLOW_DATE
        + ") as decimal)="
        + month
        + "";

    if (type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")) {
      sql += " AND " + JENIS_MUTASI + " = '" + type_mutasi + "'";
    }

    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> cashes = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          cashes.add(cash);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return cashes;
  }

  public HashMap<String, Double> getUserBudget(String id_user) {
    String query = "SELECT * FROM " + TAB_USER + " WHERE " + USER_ID + "=" + id_user + "";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    HashMap<String, Double> map = new HashMap<>();
    try {
      if (cursor.moveToFirst()) {
        if (cursor.getCount() > 0) {
          map.put(INSTALMENT, cursor.getDouble(cursor.getColumnIndex(INSTALMENT)));
          map.put(INCOME_EVERY_MONTH, cursor.getDouble(cursor.getColumnIndex(INCOME_EVERY_MONTH)));
          return map;
        }
      }
    } finally {
      cursor.close();
    }
    return null;
  }

  public ArrayList<Vendor> getAllVendors() {
    ArrayList<Vendor> vendors = new ArrayList<>();
    String not_in_vendor = "";
    if (!MyCustomApplication.showAllVendor()) {
      not_in_vendor =
          " WHERE " + TAB_VENDORS + "." + ID_VENDOR + " not in (" + Helper.generateCommaString(
              MyCustomApplication.invalidVendor()) + ")";
    }
    String query = "SELECT * FROM " + TAB_VENDORS + " " + not_in_vendor;
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          vendors.add(cursorToVendor(cursor));
        } while (cursor.moveToNext());
        return vendors;
      }
    } finally {
      cursor.close();
    }
    return vendors;
  }

  public ArrayList<Invest> getListInvest() {
    String query = "SELECT * FROM " + TAB_INVEST + "";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    ArrayList<Invest> invests = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          Invest invest = cursorToInvest(cursor);
          invests.add(invest);
        } while (cursor.moveToNext());
        return invests;
      }
    } finally {
      cursor.close();
    }

    return invests;
  }

  public ArrayList<Cash> getTransactionByDate(String type_mutasi, String start_date,
      String end_date, String idUser) {
    String query = "SELECT "
        + TAB_CASH
        + ".* FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(idUser)
        + withoutTrash(TAB_CASH)
        + withoutTransfer()
        + " AND "
        + JENIS_MUTASI
        + "='"
        + type_mutasi
        + "' AND "
        + CASHFLOW_DATE
        + " BETWEEN '"
        + start_date
        + "' AND '"
        + end_date
        + "' ORDER BY "
        + CASHFLOW_DATE
        + " DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    ArrayList<Cash> cashs = new ArrayList<>();

    Timber.e("query " + query);
    try {
      Timber.e("sebelum if");
      if (cursor.moveToFirst()) {
        do {
          Cash cash = cursorToCash(cursor);
          cashs.add(cash);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return cashs;
  }

  public HashMap<Integer, Double> getTransactionByDateGroupByCategory(String type_mutasi,
      String start_date, String end_date, String idUser) {
    String query = "SELECT "
        + TAB_CASH
        + "."
        + CATEGORY_ID
        + ",SUM("
        + TAB_CASH
        + "."
        + AMOUNT
        + ") as "
        + AMOUNT
        + " FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(idUser)
        + withoutTrash(TAB_CASH)
        + withoutTransfer()
        + " AND "
        + JENIS_MUTASI
        + "='"
        + type_mutasi
        + "' AND "
        + CASHFLOW_DATE
        + " BETWEEN '"
        + start_date
        + "' AND '"
        + end_date
        + "' GROUP BY "
        + CATEGORY_ID
        + " ORDER BY "
        + CASHFLOW_DATE
        + " DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    ArrayList<Cash> cashs = new ArrayList<>();

    Timber.e("query " + query);
    HashMap<Integer, Double> map = new HashMap<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          Integer category_id = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
          Double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
          map.put(category_id, amount);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return map;
  }

  public HashMap<String, Double> getTransactionByDateGroupByDate(String type_mutasi,
      String start_date, String end_date, String idUser) {
    String query = "SELECT "
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + ",SUM("
        + TAB_CASH
        + "."
        + AMOUNT
        + ") as "
        + AMOUNT
        + " FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(idUser)
        + withoutTrash(TAB_CASH)
        + withoutTransfer()
        + " AND "
        + JENIS_MUTASI
        + "='"
        + type_mutasi
        + "' "
        + excludeTransferDompet()
        + " AND date("
        + CASHFLOW_DATE
        + ") BETWEEN date('"
        + start_date
        + "') AND date('"
        + end_date
        + "') GROUP BY "
        + CASHFLOW_DATE
        + " ORDER BY  "
        + CASHFLOW_DATE
        + " DESC ";
    //String query = "SELECT strftime('%W',"+TAB_CASH+"."+CASHFLOW_DATE+") as week,SUM("+TAB_CASH+"."+AMOUNT+") as "+AMOUNT+" FROM "+TAB_CASH+" "+generatequerybyuserid(idUser)+" AND "+JENIS_MUTASI+"='"+type_mutasi+"' AND "+CASHFLOW_DATE+" BETWEEN '"+start_date+"' AND '"+end_date+"' GROUP BY "+CASHFLOW_DATE+" ORDER BY  strftime('%W','"+CASHFLOW_DATE+"') DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    ArrayList<Cash> cashs = new ArrayList<>();

    Timber.e("query " + query);
    HashMap<String, Double> map = new HashMap<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          String date = cursor.getString(cursor.getColumnIndex(CASHFLOW_DATE));
          Double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
          map.put(date, amount);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return map;
  }

  public HashMap<String, Double> getTransactionByDateGroupByWeek(String type_mutasi,
      String start_date, String end_date, String idUser) {
    //String query = "SELECT "+TAB_CASH+"."+CASHFLOW_DATE+",SUM("+TAB_CASH+"."+AMOUNT+") as "+AMOUNT+" FROM "+TAB_CASH+" "+generatequerybyuserid(idUser)+" AND "+JENIS_MUTASI+"='"+type_mutasi+"' AND "+CASHFLOW_DATE+" BETWEEN '"+start_date+"' AND '"+end_date+"' GROUP BY "+CASHFLOW_DATE+" ORDER BY  "+CASHFLOW_DATE+" DESC ";
    String query = "SELECT strftime('%W',"
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + ") as week,SUM("
        + TAB_CASH
        + "."
        + AMOUNT
        + ") as "
        + AMOUNT
        + " FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(idUser)
        + excludeTransferDompet()
        + withoutTrash(TAB_CASH)
        + withoutTransfer()
        + " AND "
        + JENIS_MUTASI
        + "='"
        + type_mutasi
        + "' AND DATE("
        + CASHFLOW_DATE
        + ") BETWEEN DATE('"
        + start_date
        + "') AND DATE('"
        + end_date
        + "') GROUP BY "
        + "strftime('%W',"
        + CASHFLOW_DATE
        + ")"
        + " ORDER BY  strftime('%W','"
        + CASHFLOW_DATE
        + "') DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    ArrayList<Cash> cashs = new ArrayList<>();

    Timber.e("query " + query);
    HashMap<String, Double> map = new HashMap<>();
    try {
      if (cursor.moveToFirst()) {
        int i = 0;
        do {
          String date = cursor.getString(cursor.getColumnIndex("week"));
          Double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
          map.put(date, amount);
          i++;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return map;
  }

  public HashMap<String, Double> getTransactionByDateGroupByMonth(String type_mutasi,
      String start_date, String end_date, String idUser) {
    //String query = "SELECT "+TAB_CASH+"."+CASHFLOW_DATE+",SUM("+TAB_CASH+"."+AMOUNT+") as "+AMOUNT+" FROM "+TAB_CASH+" "+generatequerybyuserid(idUser)+" AND "+JENIS_MUTASI+"='"+type_mutasi+"' AND "+CASHFLOW_DATE+" BETWEEN '"+start_date+"' AND '"+end_date+"' GROUP BY "+CASHFLOW_DATE+" ORDER BY  "+CASHFLOW_DATE+" DESC ";
    String query = "SELECT strftime('%Y-%m',"
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + ") as month,SUM("
        + TAB_CASH
        + "."
        + AMOUNT
        + ") as "
        + AMOUNT
        + " FROM "
        + TAB_CASH
        + " "
        + generatequerybyuserid(idUser)
        + excludeTransferDompet()
        + withoutTrash(TAB_CASH)
        + withoutTransfer()
        + " AND "
        + JENIS_MUTASI
        + "='"
        + type_mutasi
        + "' AND DATE("
        + CASHFLOW_DATE
        + ") BETWEEN DATE('"
        + start_date
        + "') AND DATE('"
        + end_date
        + "') GROUP BY "
        + "strftime('%Y-%m',"
        + CASHFLOW_DATE
        + ") "
        + " ORDER BY  strftime('%Y-%m','"
        + CASHFLOW_DATE
        + "') DESC ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    Timber.e("query " + query);
    HashMap<String, Double> map = new HashMap<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          String date = cursor.getString(cursor.getColumnIndex("month"));
          Double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));
          map.put(date, amount);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return map;
  }

  public Vendor getVendorsByID(int vendors_id) {
    String query = "SELECT "
        + TAB_VENDORS
        + ".* FROM "
        + TAB_VENDORS
        + " WHERE "
        + ID_VENDOR
        + "="
        + vendors_id
        + "";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    Vendor vendors = new Vendor();
    try {
      if (cursor.moveToFirst()) {
        vendors = cursorToVendor(cursor);
      }
    } finally {
      cursor.close();
    }
    return vendors;
  }

  public ArrayList<ProductPlan> getProductPlanByPlanLocalID(int plan_id) {
    String query = "SELECT "
        + TAB_PLAN_PRODUCT
        + ".* FROM "
        + TAB_PLAN_PRODUCT
        + " WHERE "
        + PLAN_ID_LOCAL
        + "="
        + plan_id
        + " ";
    Timber.e("query " + query);
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    ArrayList<ProductPlan> plans = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          ProductPlan productPlan = cursorToPlanProduct(cursor);
          plans.add(productPlan);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return plans;
  }

  public ArrayList<ProductPlan> getProductPlanByPlan(int plan_id) {
    String query = "SELECT "
        + TAB_PLAN_PRODUCT
        + ".* FROM "
        + TAB_PLAN_PRODUCT
        + " WHERE "
        + PLAN_ID
        + "="
        + plan_id
        + " ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    ArrayList<ProductPlan> plans = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          ProductPlan productPlan = cursorToPlanProduct(cursor);
          plans.add(productPlan);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return plans;
  }

  public DanaPensiun getDanaPensiunByPlanIDLocal(int plan_id) {
    String query = "SELECT "
        + TAB_DANA_PENSIUN
        + ".* FROM "
        + TAB_DANA_PENSIUN
        + " WHERE "
        + PLAN_ID_LOCAL
        + "="
        + plan_id
        + " ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    DanaPensiun danaPensiun = new DanaPensiun();
    try {
      if (cursor.moveToFirst()) {
        danaPensiun = cursorToDanaPensiun(cursor);
      }
    } finally {
      cursor.close();
    }
    return danaPensiun;
  }

  public DanaPensiun getDanaPensiunByPlan(int plan_id) {
    String query = "SELECT "
        + TAB_DANA_PENSIUN
        + ".* FROM "
        + TAB_DANA_PENSIUN
        + " WHERE "
        + PLAN_ID_LOCAL
        + "="
        + plan_id
        + " ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    DanaPensiun danaPensiun = new DanaPensiun();
    try {
      if (cursor.moveToFirst()) {
        danaPensiun = cursorToDanaPensiun(cursor);
      }
    } finally {
      cursor.close();
    }
    return danaPensiun;
  }

  public DanaDarurat getDanaDaruratByPlanIDLocal(int plan_id) {
    String query = "SELECT "
        + TAB_DANA_DARURAT
        + ".* FROM "
        + TAB_DANA_DARURAT
        + " WHERE "
        + PLAN_ID_LOCAL
        + "="
        + plan_id
        + " ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    DanaDarurat danaDarurat = new DanaDarurat();
    try {
      if (cursor.moveToFirst()) {
        danaDarurat = cursorToDanaDarurat(cursor);
      }
    } finally {
      cursor.close();
    }
    return danaDarurat;
  }

  public DanaDarurat getDanaDaruratByPlan(int plan_id) {
    String query = "SELECT "
        + TAB_DANA_DARURAT
        + ".* FROM "
        + TAB_DANA_DARURAT
        + " WHERE "
        + PLAN_ID_LOCAL
        + "="
        + plan_id
        + " ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    DanaDarurat danaDarurat = new DanaDarurat();
    try {
      if (cursor.moveToFirst()) {
        danaDarurat = cursorToDanaDarurat(cursor);
      }
    } finally {
      cursor.close();
    }
    return danaDarurat;
  }

  public DanaKuliah getDanaKuliahByPlanIDLocal(int plan_id) {
    String query = "SELECT "
        + TAB_DANA_KULIAH
        + ".* FROM "
        + TAB_DANA_KULIAH
        + " WHERE "
        + PLAN_ID_LOCAL
        + "="
        + plan_id
        + " ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    DanaKuliah danaKuliah = new DanaKuliah();
    try {
      if (cursor.moveToFirst()) {
        danaKuliah = cursorToDanaKuliah(cursor);
      }
    } finally {
      cursor.close();
    }
    return danaKuliah;
  }

  public DanaKuliah getDanaKuliahByPlan(int plan_id) {
    String query = "SELECT "
        + TAB_DANA_PENSIUN
        + ".* FROM "
        + TAB_DANA_PENSIUN
        + " WHERE "
        + PLAN_ID_LOCAL
        + "="
        + plan_id
        + " ";
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    DanaKuliah danaKuliah = new DanaKuliah();
    try {
      if (cursor.moveToFirst()) {
        danaKuliah = cursorToDanaKuliah(cursor);
      }
    } finally {
      cursor.close();
    }
    return danaKuliah;
  }

  public boolean saveAvatar(String userid, String avatar) {
    String query = "SELECT * FROM " + TAB_USER + " WHERE " + ID_USER + "=" + userid;
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    try {
      if (cursor.getColumnCount() > 0) {
        ContentValues cv = new ContentValues();
        cv.put(AVATAR, avatar);
        getWritableDatabase().update(TAB_USER, cv, ID_USER + "=" + userid, null);
        Cursor cursor2 = getReadableDatabase().rawQuery("select changes() as affected_row", null);
        try {
          if (cursor2.moveToFirst()) {
            int rows = cursor2.getInt(cursor2.getColumnIndex("affected_row"));
            return true;
          }
        } finally {
          cursor2.close();
        }
      }
    } finally {
      cursor.close();
    }
    return false;
  }

  public List<UserCategory> getUserCategoryByCategory(String idUser, int id_category) {
    ArrayList<UserCategory> user_categories = new ArrayList<>();
    String query = "SELECT * FROM "
        + TAB_USER_CATEGORY
        + " WHERE "
        + ID_CATEGORY
        + "="
        + id_category
        + " AND "
        + USER_ID
        + "="
        + idUser;
    Cursor cursor = getReadableDatabase().rawQuery(query, null);
    try {
      if (cursor.moveToFirst()) {
        do {
          UserCategory userCategory = cursorToUserCategory(cursor);
          user_categories.add(userCategory);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return user_categories;
  }

  public Cursor selectTable(String table_name) {
    try {
      Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + table_name, null);
      return cursor;
    } catch (Exception e) {
    }
    return null;
  }

  public List<Product> getProductPlanByPlanLocalIDToProduct(int id) {
    List<ProductPlan> pps = getProductPlanByPlanLocalID(id);
    ArrayList<Product> products = new ArrayList<>();
    for (ProductPlan pl : pps) {
      Product product = getProductBy(TAB_PRODUCT, pl.getProduct_id());
      products.add(product);
    }
    return products;
  }

  public String[] getAllTag(String idUser) {
    String sql = "select "
        + TAB_CASH
        + "."
        + CASHFLOW_TAG
        + " from "
        + TAB_CASH
        + " "
        + generatequerybyuserid(idUser)
        + " "
        + withoutTrash(TAB_CASH);
    Timber.e("sql " + sql);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    try {
      String tags = "";
      if (cursor.moveToFirst()) {
        boolean first = true;
        do {
          if (first) {
            tags += cursor.getString(cursor.getColumnIndex(CASHFLOW_TAG));
          } else {
            String tg = cursor.getString(cursor.getColumnIndex(CASHFLOW_TAG));
            if (!TextUtils.isEmpty(tg)) {
              tags += "," + tg;
            }
          }
          first = false;
        } while (cursor.moveToNext());
        String[] arry = tags.split(",");
        List<String> uniqueList = new ArrayList<>(new LinkedHashSet<String>(Arrays.asList(arry)));
        return uniqueList.toArray(new String[0]);
      }
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    } finally {
      cursor.close();
    }
    return null;
  }

  public ArrayList<Integer> getYearAllTransaction(String idUser) {
    String SQL_TAHUN = "select strftime('%Y',"
        + TAB_CASH
        + "."
        + CASHFLOW_DATE
        + ") as year from "
        + TAB_CASH
        + "  "
        + generatequerybyuserid(idUser)
        + " "
        + withoutTrash(TAB_CASH)
        + withoutTransfer()
        + " GROUP BY strftime('%Y',"
        + CASHFLOW_DATE
        + ") ORDER BY "
        + CASHFLOW_DATE
        + " ASC";
    Cursor cursor = getReadableDatabase().rawQuery(SQL_TAHUN, null);
    ArrayList<Integer> tahuns = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {

        do {
          Integer tahun = cursor.getInt(cursor.getColumnIndex("year"));
          tahuns.add(tahun);
        } while (cursor.moveToNext());
        return tahuns;
      }
    } finally {
      cursor.close();
    }
    return tahuns;
  }

  public Cursor ExportCashflow(int idaccount, String type, String from, String to) {
    String sql_date = "";
    if (from != null && to != null) {
      sql_date = " AND " + CASHFLOW_DATE + " BETWEEN '" + from + "' AND '" + to + "' ";
    } else if (from != null) {
      sql_date = " AND " + CASHFLOW_DATE + " >= '" + from;
    } else if (to != null) {
      sql_date = " AND " + CASHFLOW_DATE + " <= '" + to;
    }

    String sql_type = "";
    if (type.equalsIgnoreCase("CR") || type.equalsIgnoreCase("DB")) {
      sql_type = " AND " + TAB_CASH + "." + CASHFLOW_DATE + "='" + type + "'";
    }

    String sql = "select "
        + TAB_CASH
        + "."
        + CASHFLOW_ID
        + ","
        + PRODUCT_ID
        + ","
        + CASHFLOW_NAME
        + ","
        + CASHFLOW_RENAME
        + ","
        + CASHFLOW_NOTE
        + ","
        + CASHFLOW_TAG
        + ","
        + CATEGORY_ID
        + ","
        + AMOUNT
        + ","
        + TAB_CASH
        + "."
        + CREATED_AT
        + ","
        + TAB_CASH
        + "."
        + UPDATED_AT
        + " "
        + " from "
        + TAB_CASH
        + " JOIN "
        + TAB_PRODUCT
        + " ON "
        + TAB_PRODUCT
        + "."
        + ID_PRODUCT
        + "="
        + TAB_CASH
        + "."
        + PRODUCT_ID
        + " JOIN "
        + TAB_ACCOUNT
        + " ON "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + "="
        + TAB_PRODUCT
        + "."
        + ACCOUNT_ID
        + " WHERE "
        + TAB_ACCOUNT
        + "."
        + ID_ACCOUNT
        + "="
        + idaccount
        + sql_type
        + sql_date;

    Timber.e("export " + sql);

    return getReadableDatabase().rawQuery(sql, null);
  }

  public List<Plan> getAllPlanByAccount(int id_account) {
    String sql = "SELECT * from "
        + TAB_PLAN
        + " where "
        + TAB_PLAN
        + "."
        + ACCOUNT_ID
        + "="
        + id_account
        + withoutTrash(TAB_PLAN);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Plan> plans = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          Plan plan = cursorToPlan(cursor);
          plans.add(plan);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return plans;
  }

  public ArrayList<ProductPlan> getPlanProduct(int plan_id) {
    String sql = "SELECT * FROM "
        + TAB_PLAN_PRODUCT
        + " WHERE "
        + PLAN_ID_LOCAL
        + " = "
        + plan_id
        + " "
        + withoutTrash(TAB_PLAN_PRODUCT);
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<ProductPlan> planArrayList = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          ProductPlan productPlan = cursorToPlanProduct(cursor);
          planArrayList.add(productPlan);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return planArrayList;
  }

  public void updateCashCategory(int cash_id, int category_id, String type) {
    ContentValues cv = new ContentValues();
    cv.put(CATEGORY_ID, category_id);
    cv.put(UPDATED_AT, Helper.getDateNow());
    cv.put(JENIS_MUTASI, type);
    getWritableDatabase().update(TAB_CASH, cv, ID + "=" + cash_id, null);
  }

  public void updateCashUserCategory(int cash_id, int parent_id, int user_category_id,
      String type) {
    ContentValues cv = new ContentValues();
    cv.put(CATEGORY_ID, parent_id);
    cv.put(USER_CATEGORY_ID, user_category_id);
    cv.put(UPDATED_AT, Helper.getDateNow());
    cv.put(JENIS_MUTASI, type);
    getWritableDatabase().update(TAB_CASH, cv, ID + "=" + cash_id, null);
  }

  public void updateRenameCash(int cash_id,String rename) {
    ContentValues cv = new ContentValues();
    cv.put(CASHFLOW_RENAME, rename);
    cv.put(UPDATED_AT, Helper.getDateNow());
    getWritableDatabase().update(TAB_CASH, cv, ID + "=" + cash_id, null);
  }

  public ArrayList<Cash> getCashbyTag(String idUser, String tag) {
    String sql = "select "
        + TAB_CASH
        + ".* from "
        + TAB_CASH
        + " "
        + generatequerybyuserid(idUser)
        + " AND "
        + CASHFLOW_TAG
        + " like '%"
        + tag
        + "%' ";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    ArrayList<Cash> cashes = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          Cash c = cursorToCash(cursor);
          cashes.add(c);
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
    return cashes;
  }

  public void updateTag(int id, String tags) {
    ContentValues cv = new ContentValues();
    cv.put(CASHFLOW_TAG, tags);
    cv.put(UPDATED_AT, Helper.getDateNow());
    getWritableDatabase().update(TAB_CASH, cv, ID + "=" + id, null);
  }

  public Cash getLastCashflow(String idUser) {
    String sql = "select "
        + TAB_CASH
        + ".* from "
        + TAB_CASH
        + " "
        + generatequerybyuserid(idUser)
        + " order by "
        + CASHFLOW_DATE
        + " ASC limit 1";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    Cash cash = null;
    try {
      if (cursor.moveToFirst()) {
        cash = cursorToCash(cursor);
      }
    } finally {
      cursor.close();
    }
    return cash;
  }

  public void renameAccount(int id, String text) {
    ContentValues cv = new ContentValues();
    cv.put(NAME_ACCOUNT, text);
    cv.put(UPDATED_AT, Helper.getDateNow());
    getWritableDatabase().update(TAB_ACCOUNT, cv, ID + "=" + id, null);
  }

  public void renameCategory(int id_category, String name_category,String desc_category) {
    ContentValues cv = new ContentValues();
    cv.put(NAME_CATEGORY,name_category);
    cv.put(DESCRIPTION_CATEGORY,desc_category);
    cv.put(UPDATED_AT,Helper.getDateNow());
    getWritableDatabase().update(TAB_CATEGORY,cv,ID_CATEGORY +"="+id_category,null);
  }

  public ArrayList<Cash> getCashbyProduct(int id_product,String type_mutasi) {
    String sql_mutasi = "";
    if(type_mutasi.equalsIgnoreCase("CR") || type_mutasi.equalsIgnoreCase("DB")){
      sql_mutasi = "and "+JENIS_MUTASI+"='"+type_mutasi+"'";
    }
    String sql = "select * from "+TAB_CASH+" where "+PRODUCT_ID+"="+id_product+" "+sql_mutasi;
    Cursor cursor = getReadableDatabase().rawQuery(sql,null);
    ArrayList<Cash> cashs = new ArrayList<>();
    try {
      if(cursor.moveToFirst()){
       do {
         cashs.add(cursorToCash(cursor));
       }while (cursor.moveToNext());
      }
    }finally {
      cursor.close();
    }
    return cashs;
  }


  public void updateRefferalFee(ReferralFee referralFee) {
    ContentValues cv = getContentValuesReferralFee(referralFee);
    String sql = "select * from "
        + TAB_REFERRAL_FEE
        + " where "
        + ID_REFERRAL_FEE
        + "="
        + referralFee.getId_referral_fee()
        + " ";
    Cursor cursor = getReadableDatabase().rawQuery(sql, null);
    if (cursor.getCount() > 0) {
      getWritableDatabase().update(TAB_REFERRAL_FEE, cv, ID_REFERRAL_FEE + "=" + referralFee.getId_referral_fee(),
          null);
    } else {
      getWritableDatabase().insert(TAB_REFERRAL_FEE, ID, cv);
    }
  }

  public ArrayList<Product> getAllProductBySaldo(String idUser,String delimiter,float saldo) {
    String sql_db = "select "+TAB_PRODUCT+".* from "+TAB_PRODUCT+" join "+TAB_ACCOUNT+" "
        + "on ("+TAB_ACCOUNT+"."+ID_ACCOUNT+"="+TAB_PRODUCT+"."+ACCOUNT_ID+") "
        + "where "+TAB_ACCOUNT+"."+USER_ID+"="+idUser+" and "+BALANCE+""+delimiter+""+saldo;
    Cursor cursor = getReadableDatabase().rawQuery(sql_db,null);
    ArrayList<Product> products = new ArrayList<>();
    if(cursor.moveToFirst()){
      do{
        Product product = cursorToProduct(cursor);
        products.add(product);
      }while (cursor.moveToNext());
    }
    return products;
  }
}