package javasign.com.dompetsehat.models.orm;

/**
 * Created by avesina on 9/22/16.
 */

public class DBSTRING {
  public final static String ID = "id";
  public final static String USER_ID = "user_id";
  public final static String PLAN_ID = "plan_id";
  public final static String INVEST_ID =  "investasi_id";
  public final static String TYPE = "type";
  public final static String EMAIL = "email";
  public final static String CREATED_AT = "created_at";
  public final static String UPDATED_AT = "updated_at";
  public final static String DELETED_AT = "deleted_at";

  //column names table CATEGORY
  //is income/expense -> (88 for income, 99 for expense)

  //column names table USER
  public final static String ID_USER = "id_user";
  public final static String USERNAME = "username";
  public final static String BIRTHDAY = "birthday";
  public final static String OCCUPATION = "occupation";
  public final static String AFFILIATION = "affiliation";
  public final static String ADDRESS = "address";
  public final static String POSTAL_CODE = "postal_code";
  public final static String AVATAR = "avatar";
  public final static String REFERRAL_CODE = "referral_code";
  public final static String REFERRER = "referrer";
  public final static String GENDER = "gender";
  public final static String LAST_SYNC_CASH = "last_sync";
  public final static String LAST_SYNC_BUDGET = "last_sync_budget";
  public final static String LAST_SYNC_ALARM = "last_sync_alarm";
  public final static String INCOME_EVERY_MONTH = "pemasukan";
  public final static String INSTALMENT = "cicilan";
  public final static String CHILDS = "anak";


  //column names table ALARM MANAGER

  //column names table VENDORS
  public final static String ID_VENDOR = "id_vendor";
  public final static String VENDOR_NAME = "vendor_name";
  public final static String VENDOR_TYPE = "vendor_type";
  public final static String VENDOR_IMAGE = "vendor_image";

  //column names table account

  //column names table product
  public final static String NAME_PRODUCT = "name_product";
  public final static String ID_PRODUCT = "id_product";
  public final static String BALANCE = "balance";
  public final static String ACCOUNT_ID = "account_id";

  //column names table cashflow


  //column names table budget


  // PLAN
  public final static String ID_PLAN = "id_plan";
  public final static String PLAN_ID_LOCAL = "plan_id_local";
  public final static String PLAN_TITLE = "title";
  public final static String PLAN_TOTAL = "total";
  public final static String LIFE_TIME = "lifetimes";
  public final static String PLAN_DATE_START = "date_start";
  public final static String PLAN_AMOUNT_MONTHLY = "amount_monthly";
  public final static String PLAN_AMOUNT_YEARLY = "amount_yearly";
  public final static String PLAN_AMOUNT_CASH = "amount_cash";
  public final static String PLAN_RISK = "goal_risk";
}
