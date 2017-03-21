package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.models.json.referral_fee;

/**
 * Created by avesina on 2/24/17.
 */

public class ReferralFee {
  private int id;//":1,
  private int id_referral_fee;
  private int account_id;//":8322,
  private String fee_date;//":"21-12-16 00:00:00",
  private String currency;//":"IDR",
  private String type;//":"CR",
  private double amount;//":595000,
  private String created_at;//":"2017-02-16 16:04:27",
  private String updated_at;//":"2017-02-16 16:04:27",
  private String deleted_at;//":null,

  public ReferralFee(referral_fee data) {
    setId_referral_fee(data.id).setAccount_id(data.account_id)
        .setFee_date(data.fee_date)
        .setCurrency(data.currency)
        .setType(data.type)
        .setAmount(data.amount)
        .setCreated_at(data.created_at)
        .setUpdated_at(data.updated_at)
        .setDeleted_at(data.deleted_at);
  }

  public ReferralFee setId_referral_fee(int id_referral_fee) {
    this.id_referral_fee = id_referral_fee;
    return this;
  }

  public int getId_referral_fee() {
    return id_referral_fee;
  }

  public ReferralFee setId(int id) {
    this.id = id;
    return this;
  }

  public int getId() {
    return id;
  }

  public ReferralFee setAccount_id(int account_id) {
    this.account_id = account_id;
    return this;
  }

  public int getAccount_id() {
    return account_id;
  }

  public ReferralFee setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public ReferralFee setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public ReferralFee setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public ReferralFee setAmount(double amount) {
    this.amount = amount;
    return this;
  }

  public ReferralFee setCurrency(String currency) {
    this.currency = currency;
    return this;
  }

  public ReferralFee setFee_date(String fee_date) {
    this.fee_date = fee_date;
    return this;
  }

  public ReferralFee setType(String type) {
    this.type = type;
    return this;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public double getAmount() {
    return amount;
  }

  public String getCreated_at() {
    return created_at;
  }

  public String getCurrency() {
    return currency;
  }

  public String getFee_date() {
    return fee_date;
  }

  public String getType() {
    return type;
  }
}
