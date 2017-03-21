package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.models.json.debt;

/**
 * Created by avesina on 2/21/17.
 */

public class Debt {
  private int id;
  private int id_local;
  private int user_id;//":4979,
  private int product_id;//":5829,
  private int cashflow_id;//":215829,
  private String name;//":"Pinjam ke Syarif",
  private String type;//":"LEND",
  private String date;//":"2017-01-25",
  private String payback;//":"2016-05-01",
  private String email;//":"njajalUtang@gmail.com",
  private String created_at;//":"2017-02-17 15:45:58",
  private String updated_at;//":"2017-02-17 15:45:58",
  private String deleted_at;//":null,

  private Cash cash;
  private Product product;

  public Debt(debt d) {
    setId(d.id);
    setId_local(d.id_local);
    setUser_id(d.user_id);
    setCashflow_id(d.cashflow_id);
    setName(d.name);
    setType(d.type);
    setDate(d.date);
    setPayback(d.payback);
    setCreated_at(d.created_at);
    setUpdated_at(d.updated_at);
    setDeleted_at(d.deleted_at);
  }

  public Debt setId(int id) {
    this.id = id;
    return this;
  }

  public int getId() {
    return id;
  }

  public Debt setId_local(int id_local) {
    this.id_local = id_local;
    return this;
  }

  public int getId_local() {
    return id_local;
  }

  public Debt setUser_id(int user_id) {
    this.user_id = user_id;
    return this;
  }

  public int getUser_id() {
    return user_id;
  }

  public Debt setProduct_id(int product_id) {
    this.product_id = product_id;
    return this;
  }

  public int getProduct_id() {
    return product_id;
  }

  public Debt setCashflow_id(int cashflow_id) {
    this.cashflow_id = cashflow_id;
    return this;
  }

  public int getCashflow_id() {
    return cashflow_id;
  }

  public Debt setName(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  public Debt setType(String type) {
    this.type = type;
    return this;
  }

  public String getType() {
    return type;
  }

  public Debt setDate(String date) {
    this.date = date;
    return this;
  }

  public String getDate() {
    return date;
  }

  public Debt setPayback(String payback) {
    this.payback = payback;
    return this;
  }

  public String getPayback() {
    return payback;
  }

  public Debt setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public String getCreated_at() {
    return created_at;
  }

  public Debt setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public Debt setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public Debt setProduct(Product product) {
    this.product = product;
    return this;
  }

  public Debt setCash(Cash cash) {
    this.cash = cash;
    return this;
  }

  public Cash getCash() {
    return cash;
  }

  public Product getProduct() {
    return product;
  }
}
