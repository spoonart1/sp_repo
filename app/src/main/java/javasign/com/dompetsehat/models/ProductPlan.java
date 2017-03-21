package javasign.com.dompetsehat.models;

/**
 * Created by avesina on 9/14/16.
 */

public class ProductPlan {
  public int id;
  public int id_product_plan;
  public int plan_id;
  public int plan_id_local;
  public int product_id;
  public int invest_id;
  private String created_at;
  private String updated_at;
  private String deleted_at;

  public ProductPlan setId(int id) {
    this.id = id;
    return this;
  }

  public ProductPlan setPlan_id(int plan_id) {
    this.plan_id = plan_id;
    return this;
  }

  public ProductPlan setId_product_plan(int id_product_plan) {
    this.id_product_plan = id_product_plan;
    return this;
  }

  public ProductPlan setProduct_id(int product_id) {
    this.product_id = product_id;
    return this;
  }

  public ProductPlan setInvest_id(int invest_id) {
    this.invest_id = invest_id;
    return this;
  }

  public int getId() {
    return id;
  }

  public int getPlan_id() {
    return plan_id;
  }

  public int getId_product_plan() {
    return id_product_plan;
  }

  public int getInvest_id() {
    return invest_id;
  }

  public int getProduct_id() {
    return product_id;
  }

  public ProductPlan setPlan_id_local(int plan_id_local) {
    this.plan_id_local = plan_id_local;
    return this;
  }

  public int getPlan_id_local() {
    return plan_id_local;
  }

  public ProductPlan setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public ProductPlan setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public ProductPlan setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public String getCreated_at() {
    return created_at;
  }

  public String getDeleted_at() {
    return deleted_at;
  }
}
