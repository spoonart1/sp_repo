package javasign.com.dompetsehat.models;

/**
 * Created by lafran on 9/15/16.
 */

public class Invest {
  public int id_ivest;
  public int vendor_id;
  public String invest_name;
  public double invest_procentase;
  public int invest_rate = 0;
  public String created_at;
  public String deleted_at;
  public String updated_at;

  public boolean hideScoreRate = false;

  public Invest setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public Invest setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public Invest setHideScoreRate(boolean hideScoreRate){
    this.hideScoreRate = hideScoreRate;
    return this;
  }

  public Invest setId_ivest(int id_ivest) {
    this.id_ivest = id_ivest;
    return this;
  }

  public Invest setInvest_name(String invest_name) {
    this.invest_name = invest_name;
    return this;
  }

  public Invest setInvest_procentase(double invest_procentase) {
    this.invest_procentase = invest_procentase;
    return this;
  }

  public Invest setInvest_rate(int invest_rate) {
    this.invest_rate = invest_rate;
    return this;
  }

  public Invest setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public Invest setVendor_id(int vendor_id) {
    this.vendor_id = vendor_id;
    return this;
  }

  public double getInvest_procentase() {
    return invest_procentase;
  }

  public int getId_ivest() {
    return id_ivest;
  }

  public int getInvest_rate() {
    return invest_rate;
  }

  public int getVendor_id() {
    return vendor_id;
  }

  public String getCreated_at() {
    return created_at;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public String getInvest_name() {
    return invest_name;
  }

  public String getUpdated_at() {
    return updated_at;
  }
}
