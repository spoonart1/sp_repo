package javasign.com.dompetsehat.models;

import java.util.Calendar;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.Helper;
import org.json.JSONObject;

import javasign.com.dompetsehat.models.json.budget;
import timber.log.Timber;

/**
 * Created by Fernando on 8/4/2015.
 */
public class Budget extends BaseModel {

  public static final String MODE = "mode";
  public static final String MODE_ADD = "add";
  public static final String MODE_EDIT = "edit";
  public static final String BUDGET_ID = "budget_id";

  public final static int BUDGET_REPEAT_TRUE = 1;
  public final static int BUDGET_REPEAT_FALSE = 0;
  public final static String THIS_WEEK = "Minggu ini";
  public final static String THIS_MONTH = "Bulan ini";
  public final static String QUARTER_MONTH = "Catur wulan";
  public final static String NEXT_MONTH = "Bulan depan";
  public final static String WEEKLY = "Mingguan";
  public final static String BIWEEKLY = "Dua mingguan";
  public final static String MONTHLY = "Bulanan";
  public final static String YEARLY = "Tahunan";
  public final static String CUSTOM = "Kustom";

  public final static String[] LIST_BUDGET_LOOP = {
      THIS_WEEK, THIS_MONTH, QUARTER_MONTH, NEXT_MONTH, WEEKLY, BIWEEKLY, MONTHLY, YEARLY, CUSTOM
  };

  private int id;
  private int id_budget;
  private double amount_budget;
  private int category_budget;
  private int user_category_budget;
  private String date_start;
  private String date_end;
  private int user_id;
  private int repeat;
  private String every;
  private String deleted_at;
  private String created_at;
  private String updated_at;
  private double category_cash_amount = 0;
  private Category category = null;
  private UserCategory userCategory = null;

  public Budget() {
    super();
  }

  public Budget(JSONObject inputcash) {
    try {
      setId_budget(inputcash.getInt("id"));
      setAmount_budget(inputcash.getDouble("debetAmount"));
      setCategory_budget(inputcash.getInt("category_id"));
      setUser_category_budget(inputcash.getInt("user_category_id"));
      setDate_start(inputcash.getString("date_start"));
      setDate_end(inputcash.getString("date_end"));
      setUser_id(inputcash.getInt("user_id"));
      setCreated_at(inputcash.getString("created_at"));
      setUpdated_at(inputcash.getString("updated_at"));
      setDeleted_at(inputcash.getString("deleted_at"));
    } catch (Exception e) {

    }
  }

  public Budget(budget budget) {
    try {
      setId(budget.id_local);
      setId_budget(budget.id);
      setAmount_budget(budget.amount);
      setCategory_budget(budget.category_id);
      setUser_category_budget(budget.user_category_id);
      setDate_start(budget.date_start);
      setDate_end(budget.date_end);
      setUser_id(budget.user_id);
      setEvery(budget.every);
      setRepeat(budget.is_repeat);
      setCreated_at(budget.created_at);
      setUpdated_at(updated_at);
      setDeleted_at(deleted_at);
    } catch (Exception e) {
      Timber.e("ERROR new Budget "+e);

    }
  }

  public int getId() {
    return id;
  }

  public Budget setId(int id) {
    this.id = id;
    return this;
  }

  public int getId_budget() {
    return id_budget;
  }

  public Budget setId_budget(int id_budget) {
    this.id_budget = id_budget;
    return this;
  }

  public double getAmount_budget() {
    return amount_budget;
  }

  public Budget setAmount_budget(double amount_budget) {
    this.amount_budget = amount_budget;
    return this;
  }

  public int getCategory_budget() {
    return category_budget;
  }

  public Budget setCategory_budget(int category_budget) {
    this.category_budget = category_budget;
    return this;
  }

  public int getUser_category_budget() {
    return user_category_budget;
  }

  public Budget setUser_category_budget(int user_category_budget) {
    this.user_category_budget = user_category_budget;
    return this;
  }

  public String getDate_start() {
    if (date_start != null) {
      return date_start;
    } else {
      return created_at;
    }
  }

  public Budget setDate_start(String date_start) {
    this.date_start = date_start;
    return this;
  }

  public String getDate_end() {
    return date_end;
  }

  public Budget setDate_end(String date_end) {
    this.date_end = date_end;
    return this;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public Budget setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public String getCreated_at() {
    if (created_at != null) {
      return created_at;
    } else {
      Calendar calendar = Calendar.getInstance();
      return Helper.setSimpleDateFormat(calendar.getTime(), GeneralHelper.FORMAT_LENGKAP);
    }
  }

  public Budget setCreated_at(String created_at) {
    this.created_at = created_at;
    if (date_start == null) {
      date_start = created_at;
    }
    return this;
  }

  public String getUpdated_at() {
    if (created_at != null) {
      return created_at;
    } else {
      Calendar calendar = Calendar.getInstance();
      return Helper.setSimpleDateFormat(calendar.getTime(), GeneralHelper.FORMAT_LENGKAP);
    }
  }

  public Budget setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public int getUser_id() {
    return user_id;
  }

  public Budget setUser_id(int user_id) {
    this.user_id = user_id;
    return this;
  }

  public Budget setCategory(Category category) {
    this.category = category;
    return this;
  }

  public Budget setUserCategory(UserCategory userCategory) {
    this.userCategory = userCategory;
    return this;
  }

  public void setCategory_cash_amount(double category_cash_amount) {
    this.category_cash_amount = category_cash_amount;
  }

  public Category getCategory() {
    return category;
  }

  public UserCategory getUserCategory() {
    return userCategory;
  }

  public double getCategory_cash_amount() {
    return category_cash_amount;
  }

  public Budget setEvery(String every) {
    this.every = every;
    return this;
  }

  public int getRepeatOrNo(String every) {
    this.every = every;
    if (every != null) {
      switch (every) {
        case Helper.THIS_WEEK:
          return BUDGET_REPEAT_FALSE;

        case Helper.THIS_MONTH:
          return BUDGET_REPEAT_FALSE;

        case Helper.QUATER_MONTH:
          return BUDGET_REPEAT_FALSE;

        case Helper.NEXT_MONTH:
          return BUDGET_REPEAT_FALSE;

        case Helper.WEEKLY:
          return BUDGET_REPEAT_TRUE;

        case Helper.BIWEEKLY:
          return BUDGET_REPEAT_TRUE;

        case Helper.MONTHLY:
          return BUDGET_REPEAT_TRUE;

        case Helper.YEARLY:
          return BUDGET_REPEAT_TRUE;
      }
    }
    return BUDGET_REPEAT_FALSE;
  }

  public Budget setRepeat(int repeat) {
    this.repeat = repeat;
    return this;
  }

  public int getRepeat() {
    return repeat;
  }

  public String getEvery() {
    return every;
  }
}
