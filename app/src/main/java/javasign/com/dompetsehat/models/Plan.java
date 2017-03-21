package javasign.com.dompetsehat.models;

import java.util.List;
import javasign.com.dompetsehat.models.json.plan;
import org.json.JSONObject;

import javasign.com.dompetsehat.models.json.goal;

/**
 * Created by Xenix on 3/11/2016.
 */
public class Plan extends BaseModel {
  private int id;
  private int id_plan;
  private int user_id;
  private int type;
  private Integer account_id;
  private Integer product_id;
  private int category_id;
  private String plan_title;
  private float plan_total;
  private int lifetime;
  private String plan_date_star;
  private float plan_amount_yearly;
  private float plan_amount_monthly;
  private float plan_amount_cash;
  private double plan_risk;
  private String created_at;
  private String updated_at;
  private String deleted_at;

  public Product product;
  public List<Product> products;
  public Account account;
  public Vendor vendor;

  public DanaDarurat danaDarurat;
  public DanaKuliah danaKuliah;
  public DanaPensiun danaPensiun;

  public int time;

  //property
  public double total_saldo;

  public Plan() {
    super();
  }

  public Plan(JSONObject data) {
    try {
      setId_plan(data.getInt("id"));
      setProduct_id(data.getInt("product_id"));
      setAccount_id(data.getInt("account_id"));
      setCategory_id(data.getInt("category_id"));
      setPlan_title(data.getString("title"));
      setLifetime(data.getInt("lifetime"));
      setPlan_total(data.getLong("total"));
      setPlan_amount_monthly(data.getLong("debetAmount"));
      setPlan_amount_yearly(data.getLong("debetAmount"));
      setPlan_amount_cash(data.getLong("debetAmount"));
      setPlan_risk(Double.valueOf(data.getString("risk")));
      setCreated_at(data.getString("created_at"));
      setUpdated_at(data.getString("updated_at"));
    } catch (Exception e) {

    }
  }

  public Plan(goal data) {
    try {
      setId_plan(data.id);
      setProduct_id(data.product_id);
      setCategory_id(data.category_id);
      setPlan_title(data.title);
      setPlan_total(data.total);
      setLifetime(data.lifetime);
      setPlan_amount_monthly(data.amount);
      setPlan_amount_yearly(data.amount);
      setPlan_amount_cash(data.amount);
      setCreated_at(data.created_at);
      setUpdated_at(data.updated_at);
    } catch (Exception e) {

    }
  }

  public Plan(plan data) {
    try {
      setId(data.local_id);
      setId_plan(data.id);
      setUser_id(data.user_id);
      setType(data.type);
      setAccount_id(data.account_id);
      setProduct_id(data.product_id);
      setPlan_title(data.title);
      setPlan_total((float) data.total);
      setLifetime(data.lifetimes);
      setPlan_date_star(data.date_start);
      setPlan_amount_cash((float) data.amount_cash);
      setPlan_amount_monthly((float)data.amount_monthly);
      setPlan_amount_yearly((float)data.amount_yearly);
      setPlan_risk(data.risk);
      setDeleted_at(data.deleted_at);
      setUpdated_at(data.updated_at);
      setCreated_at(data.created_at);
    } catch (Exception e) {

    }
  }

  public Plan setProduct_id(Integer product_id) {
    this.product_id = product_id;
    return this;
  }

  public Plan setUser_id(int user_id) {
    this.user_id = user_id;
    return this;
  }

  public Plan setCategory_id(int category_id) {
    this.category_id = category_id;
    return this;
  }

  public Plan setAccount(Account account) {
    this.account = account;
    return this;
  }

  public Plan setId(int id) {
    this.id = id;
    return this;
  }

  public Plan setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public Plan setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public Plan setId_plan(int id_plan) {
    this.id_plan = id_plan;
    return this;
  }

  public Plan setLifetime(int lifetime) {
    this.lifetime = lifetime;
    return this;
  }

  public Plan setPlan_amount_cash(float plan_amount_cash) {
    this.plan_amount_cash = plan_amount_cash;
    return this;
  }

  public Plan setPlan_amount_monthly(float plan_amount_monthly) {
    this.plan_amount_monthly = plan_amount_monthly;
    return this;
  }

  public Plan setPlan_amount_yearly(float plan_amount_yearly) {
    this.plan_amount_yearly = plan_amount_yearly;
    return this;
  }

  public Plan setPlan_date_star(String plan_date_star) {
    this.plan_date_star = plan_date_star;
    return this;
  }

  public Plan setPlan_risk(double plan_risk) {
    this.plan_risk = plan_risk;
    return this;
  }

  public Plan setPlan_title(String plan_title) {
    this.plan_title = plan_title;
    return this;
  }

  public Plan setPlan_total(float plan_total) {
    this.plan_total = plan_total;
    return this;
  }

  public Plan setProduct(Product product) {
    this.product = product;
    return this;
  }

  public Plan setAccount_id(Integer account_id) {
    this.account_id = account_id;
    return this;
  }

  public Plan setTotal_saldo(double total_saldo) {
    this.total_saldo = total_saldo;
    return this;
  }

  public Plan setType(int type) {
    this.type = type;
    return this;
  }

  public Plan setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public Plan setVendor(Vendor vendor) {
    this.vendor = vendor;
    return this;
  }

  public int getUser_id() {
    return user_id;
  }

  public Account getAccount() {
    return account;
  }

  public double getTotal_saldo() {
    return total_saldo;
  }

  public float getPlan_amount_cash() {
    return plan_amount_cash;
  }

  public float getPlan_amount_monthly() {
    return plan_amount_monthly;
  }

  public float getPlan_amount_yearly() {
    return plan_amount_yearly;
  }

  public float getPlan_total() {
    return plan_total;
  }

  public int getCategory_id() {
    return category_id;
  }

  public int getId() {
    return id;
  }

  public int getId_plan() {
    return id_plan;
  }

  public int getLifetime() {
    return lifetime;
  }

  public int getType() {
    return type;
  }

  public Integer getProduct_id() {
    return product_id;
  }

  public Product getProduct() {
    return product;
  }

  public String getCreated_at() {
    return created_at;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public String getPlan_date_star() {
    return plan_date_star;
  }

  public double getPlan_risk() {
    return plan_risk;
  }

  public String getPlan_title() {
    return plan_title;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public Vendor getVendor() {
    return vendor;
  }

  public Integer getAccount_id() {
    return account_id;
  }

  public boolean isConnected() {
    if (account_id == null) {
      return false;
    }
    try {
      if (account_id <= 0) {
        return false;
      }

      if (account_id > 0) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public plan to_plan() {
    plan plan = new plan();
    plan.id = getId();
    plan.local_id = getId_plan();
    plan.user_id = getUser_id();
    plan.type = getType();
    plan.account_id = getAccount_id();
    plan.product_id = getProduct_id();
    plan.title = getPlan_title();
    plan.total = getTotal_saldo();
    plan.date_start = getPlan_date_star();
    plan.lifetimes = getLifetime();
    plan.amount_monthly = getPlan_amount_monthly();
    plan.amount_yearly = getPlan_amount_yearly();
    plan.amount_cash = getPlan_amount_cash();
    plan.risk = getPlan_risk();
    plan.created_at = getCreated_at();
    plan.updated_at = getUpdated_at();
    plan.deleted_at = getDeleted_at();
    return plan;
  }
}
