package javasign.com.dompetsehat.models;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import javasign.com.dompetsehat.models.json.category;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;

/**
 * Created by Xenix on 12/14/2015.
 */
public class Category {
  private int id;
  private int id_category;
  private String name;
  private String description;
  private String belong_to;
  private String icon;
  private String color = "#FFFFFF";
  private String parent;
  private String deleted_at;
  private String updated_at;
  private String created_at;

  private String BgColor = "#4CAF50";
  public int visibility = View.VISIBLE;
  public boolean isAddBtn = false;
  public Transaction transaction;

  public Category() {
    super();
  }

  public Category(JSONObject jsonObject) {
    try {
      setId_category(jsonObject.getInt("id"));
      setParent(jsonObject.getString("parent"));
      setName(jsonObject.getString("name"));
      setBelong_to(jsonObject.getString("type"));
      setDescription(jsonObject.getString("description"));
      setIcon(jsonObject.getString("icon"));
      setColor(jsonObject.getString("color"));
      setCreated_at(jsonObject.getString("created_at"));
      setUpdated_at(jsonObject.getString("updated_at"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public Category(category data) {
    try {
      setId_category(data.id);
      setParent(data.parent);
      setName(data.name);
      setBelong_to(data.type);
      setDescription(data.description);
      setIcon(data.icon);
      setColor(data.color);
      setCreated_at(data.created_at);
      setUpdated_at(data.updated_at);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Category setId(int id) {
    this.id = id;
    return this;
  }

  public int getId() {
    return this.id;
  }

  public Category setName(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public Category setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getIcon() {
    return icon;
  }

  public Category setIcon(String icon) {
    this.icon = icon;
    return this;
  }

  public String getColor() {
    return color;
  }

  public Category setColor(String color) {
    this.color = color;
    return this;
  }

  public String getParent() {
    return parent;
  }

  public Category setParent(String parent) {
    this.parent = parent;
    return this;
  }

  public int getId_category() {
    return id_category;
  }

  public Category setId_category(int id_category) {
    this.id_category = id_category;
    return this;
  }

  public String getBelong_to() {
    return belong_to;
  }

  public Category setBelong_to(String belong_to) {
    this.belong_to = belong_to;
    return this;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public Category setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public Category setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public String getCreated_at() {
    return created_at;
  }

  public Category setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public String getBgColor() {
    return BgColor;
  }

  public Category setBgColor(String bgColor) {
    BgColor = bgColor;
    return this;
  }

  public void setTransaction(Transaction transaction) {
    this.transaction = transaction;
  }

  public Transaction getTransaction() {
    return transaction;
  }
}