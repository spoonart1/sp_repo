package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.models.json.cashflow;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by aves on 4/7/15.
 */
public class Cash extends BaseModel {
  public static final String DEBIT = "DB"; // uang keluar
  public static final String CREDIT = "CR"; // uang masuk
  public static final String TRANSFER = "TF"; // uang masuk
  public static final String ALL = "ALL";

  private int id;
  private int cash_id;
  private int product_id;
  private String description;
  private String cashflow_rename;
  private String note;
  private String cash_date;
  private String cash_tag;
  private String hari;
  private int tanggal;
  private int bulan;
  private int tahun;
  private float amount;
  private String type;
  private String created_at;
  private int category_id;
  private int user_category_id;
  private String updated_at;
  private String deleted_at;
  private String status;
  private int maxCharAllowed;

  // category property
  private Category category;
  private UserCategory userCategory;
  private Product product;
  private Account account;

  public boolean after_update = false;

  public int getMaxCharAllowed() {
    return maxCharAllowed;
  }

  public void setMaxCharAllowed(int maxCharAllowed) {
    this.maxCharAllowed = maxCharAllowed;
  }

  public Cash() {
    super();
  }

  public Cash(JSONObject inputcash) {
    try {
      setCash_id(inputcash.getInt("id"));
      if (inputcash.has("description")) setDescription(inputcash.getString("description"));

      if (inputcash.has("user_tag")) setCash_tag(inputcash.getString("user_tag"));

      if (inputcash.has("amount")) setAmount(inputcash.getLong("amount"));

      if (inputcash.has("type")) setType(inputcash.getString("type"));

      if (inputcash.has("created_at")) setCreated_at(inputcash.getString("created_at"));

      if (inputcash.has("updated_at")) setUpdated_at(inputcash.getString("updated_at"));

      if (inputcash.has("deleted_at")) setDeleted_at(inputcash.getString("deleted_at"));

      if (inputcash.has("status")) setStatus(inputcash.getString("status"));

      if (inputcash.has("category_id")) setCategory_id(inputcash.getInt("category_id"));

      if (inputcash.has("user_category_id")) setUser_category_id(inputcash.getInt("user_category_id"));

      if (inputcash.has("date")) {
        setCash_date(inputcash.getJSONObject("date").getString("original"));
        setHari(inputcash.getJSONObject("date").getString("day"));
        setTanggal(inputcash.getJSONObject("date").getInt("date"));
        setBulan(inputcash.getJSONObject("date").getInt("month"));
        setTahun(inputcash.getJSONObject("date").getInt("year"));
      }

      if (inputcash.has("product_id")) setProduct_id(inputcash.getInt("product_id"));
      if (inputcash.has("user_description")) {
        setCashflow_rename(inputcash.getString("user_description"));
      }
      if (inputcash.has("note")) setNote(inputcash.getString("note"));
    } catch (Exception e) {
      Timber.e("ERROR CASH " + e);
    }
  }

  public Cash(cashflow inputcash) {
    try {
      setCash_id(inputcash.id);
      setId(inputcash.id_local);
      if (inputcash.description != null) setDescription(inputcash.description);

      if (inputcash.user_tag != null) setCash_tag(inputcash.user_tag);

      Timber.e("avesina mustari amount "+inputcash.amount);
      setAmount(inputcash.amount);

      if (inputcash.type!= null) setType(inputcash.type);

      if (inputcash.created_at != null ) setCreated_at(inputcash.created_at);

      if (inputcash.updated_at != null) setUpdated_at(inputcash.updated_at);

      if (inputcash.deleted_at != null) setDeleted_at(inputcash.deleted_at);

      if (inputcash.status != null) setStatus(inputcash.status);

      if (inputcash.category_id > 0) setCategory_id(inputcash.category_id);

      if (inputcash.user_category_id > 0) setUser_category_id(inputcash.user_category_id);

      if (inputcash.date != null) {
        setCash_date(inputcash.date.original);
        setHari(inputcash.date.day);
        setTanggal(inputcash.date.date);
        setBulan(inputcash.date.month);
        setTahun(inputcash.date.year);
      }

      if (inputcash.product_id > 0) setProduct_id(inputcash.product_id);
      if (inputcash.user_description != null) {
        setCashflow_rename(inputcash.user_description);
      }
      if (inputcash.note != null) setNote(inputcash.note);
    } catch (Exception e) {
      Timber.e("ERROR CASH " + e);
    }
  }

  public int getId() {
    return id;
  }

  public Cash setId(int id) {
    this.id = id;
    return this;
  }

  public int getCash_id() {
    return cash_id;
  }

  public Cash setCash_id(int cash_id) {
    this.cash_id = cash_id;
    return this;
  }

  public String getCash_date() {
    return cash_date;
  }

  public Cash setCash_date(String cash_date) {
    this.cash_date = cash_date;
    return this;
  }

  public float getAmount() {
    return amount;
  }

  public Cash setAmount(float amount) {
    this.amount = amount;
    return this;
  }

  public String getHari() {
    return hari;
  }

  public Cash setHari(String hari) {
    this.hari = hari;
    return this;
  }

  public int getTanggal() {
    return tanggal;
  }

  public Cash setTanggal(int tanggal) {
    this.tanggal = tanggal;
    return this;
  }

  public int getBulan() {
    return bulan;
  }

  public Cash setBulan(int bulan) {
    this.bulan = bulan;
    return this;
  }

  public int getTahun() {
    return tahun;
  }

  public Cash setTahun(int tahun) {
    this.tahun = tahun;
    return this;
  }

  public String getCreated_at() {
    return created_at;
  }

  public Cash setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public Cash setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public Cash setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public String getStatus() {
    return status;
  }

  public Cash setStatus(String status) {
    this.status = status;
    return this;
  }

  public String getCash_tag() {
    return cash_tag;
  }

  public Cash setCash_tag(String cash_tag) {
    this.cash_tag = cash_tag;
    return this;
  }

  public int getProduct_id() {
    return product_id;
  }

  public Cash setProduct_id(int product_id) {
    this.product_id = product_id;
    return this;
  }

  public String getType() {
    return type;
  }

  public Cash setType(String type) {
    this.type = type;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Cash setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getNote() {
    return note;
  }

  public Cash setNote(String note) {
    this.note = note;
    return this;
  }

  public int getCategory_id() {
    return category_id;
  }

  public Cash setCategory_id(int category_id) {
    this.category_id = category_id;
    return this;
  }

  public int getUser_category_id() {
    return user_category_id;
  }

  public Cash setUser_category_id(int user_category_id) {
    this.user_category_id = user_category_id;
    return this;
  }

  public String getCashflow_rename() {
    return cashflow_rename;
  }

  public Cash setCashflow_rename(String cashflow_rename) {
    this.cashflow_rename = cashflow_rename;
    return this;
  }

  public Cash setCategory(Category category) {
    this.category = category;
    return this;
  }

  public Category getCategory() {
    return category;
  }

  public Cash setUserCategory(UserCategory userCategory) {
    this.userCategory = userCategory;
    return this;
  }

  public UserCategory getUserCategory() {
    return userCategory;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Account getAccount() {
    return account;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Product getProduct() {
    return product;
  }
}