package javasign.com.dompetsehat.models;

import android.content.Context;
import java.util.List;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Created by aves on 4/7/15.
 */
public class Account {
  private int id;
  private int idaccount;
  private int iduser;
  private int idvendor;
  private String username;
  private String name;
  private String created_at;
  private String updated_at;
  private String last_connect;
  private String login_status;
  private String login_info;
  private String email_alert;
  private String properties;

  public double saldo;
  public List<Product> products;

  private Vendor vendor;
  MCryptNew mcrypt = new MCryptNew();

  public Account() {
    super();
  }

  public Account(JSONObject data) {
    try {
      setIdaccount(data.getInt("id"));
      if(data.has("username")) {
        setUsername(data.getString("username"));
      }

      if (!data.has("nickname")
          || data.getString("nickname").equalsIgnoreCase("null")
          || data.getString("nickname").length() == 0) {
        setName(mcrypt.encrypt(mcrypt.decrypt(data.getJSONObject("vendor").getString("name"))));
      } else {
        setName(data.getString("nickname"));
      }

      if (data.has("login_status")) {
        setLogin_status(data.getString("login_status"));
      }

      if (data.has("login_info")) {
        setLogin_info(data.getString("login_info"));
      }

      if (data.has("email_alert")) {
        setEmail_alert(data.getString("email_alert"));
      }

      if (data.has("created_at")) {
        setCreated_at(data.getString("created_at"));
      }

      if (data.has("updated_at")) {
        setUpdated_at(data.getString("updated_at"));
      }

      if (data.has("vendor")) {
        Timber.d("id_vendor" + data.getJSONObject("vendor").getInt("id"));
        setIdvendor(data.getJSONObject("vendor").getInt("id"));
      }

      if(data.has("properties")){
        setProperties(data.getString("properties"));
      }

      if (data.has("user_id")) {
        setIduser(data.getInt("user_id"));
      }
    } catch (Exception e) {
      Timber.e("ERROR Model Account " + e);
    }
  }

  public Account(account data) {
    try {
      setIdaccount(data.id);
      setUsername(data.username);

      if (data.nickname == null) {
        setName(mcrypt.encrypt(mcrypt.decrypt(data.vendor.name)));
      } else {
        setName(data.nickname);
      }

      setLogin_status(data.login_status);
      setLogin_info(data.login_info);
      setEmail_alert(data.email_alert);
      setCreated_at(data.created_at);
      setUpdated_at(data.updated_at);
      setIdvendor(data.vendor.id);
      if(data.properties != null) {
        setProperties(data.properties.toString());
      }
      if (data.user_id != 0 || Integer.valueOf(data.user_id) != null) setIduser(data.user_id);
    } catch (Exception e) {
      Timber.e("ERROR "+e);
    }
  }

  public int getId() {
    return id;
  }

  public Account setId(int id) {
    this.id = id;
    return this;
  }

  public int getIdaccount() {
    return idaccount;
  }

  public Account setIdaccount(int idaccount) {
    this.idaccount = idaccount;
    return this;
  }

  public int getIduser() {
    return iduser;
  }

  public Account setIduser(int iduser) {
    this.iduser = iduser;
    return this;
  }

  public String getCreated_at() {
    return created_at;
  }

  public Account setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public Account setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public String getLast_connect() {
    return last_connect;
  }

  public Account setLast_connect(String last_connect) {
    this.last_connect = last_connect;
    return this;
  }

  public String getEmail_alert() {
    return email_alert;
  }

  public Account setEmail_alert(String email_alert) {
    this.email_alert = email_alert;
    return this;
  }

  public String getLogin_status() {
    return login_status;
  }

  public Account setLogin_status(String login_status) {
    this.login_status = login_status;
    return this;
  }

  public String getLogin_info() {
    return login_info;
  }

  public Account setLogin_info(String login_info) {
    this.login_info = login_info;
    return this;
  }

  public int getIdvendor() {
    return idvendor;
  }

  public Account setIdvendor(int idvendor) {
    this.idvendor = idvendor;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public Account setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getName() {
    return name;
  }

  public Account setName(String name) {
    this.name = name;
    return this;
  }

  public Vendor getVendor(Context context){
    if(vendor == null){
      vendor = DbHelper.getInstance(context).getVendorsByID(getIdvendor());
      return vendor;
    }
    return vendor;
  }

  public Account setProperties(String properties) {
    this.properties = properties;
    return this;
  }

  public String getProperties() {
    return properties;
  }
}
