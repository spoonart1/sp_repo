package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.product;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.MCryptNew;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Created by Xenix on 10/19/2015.
 */
public class Product{
  private int id;
  private int id_product;
  private int account_id;
  private float balance;
  private String created_at;
  private String updated_at;
  private String deleted_at;
  private String number;
  private String type;
  private String name;
  private int color;
  private String properties;
  private Invest invest;

  private Account account;

  MCryptNew mcrypt = new MCryptNew();
  String numberCek;
  public String icon = DSFont.Icon.dsf_bank.getFormattedName();

  public Product() {
    super();
  }

  public Product setInvest(Invest invest){
    this.invest = invest;
    return this;
  }

  public Product(JSONObject data, JSONObject account) {
    try {
      setId_product(data.getInt("id"));

      if (data.has("number")) setNumber(data.getString("number"));

      if (data.has("type")) setType(data.getString("type"));

      if (data.has("created_at")) setCreated_at(data.getString("created_at"));

      if (data.has("updated_at")) setUpdated_at(data.getString("updated_at"));

      if (data.has("deleted_at")) setDeleted_at(data.getString("deleted_at"));

      //if balances null ga bisa, request 0!
      if (data.has("balance")) setBalance(data.getLong("balance"));

      if (data.has("account_id")) setAccount_id(data.getInt("account_id"));

      if(data.has("properties")) {
        Timber.e("Product properties "+data.getString("properties"));
        setProperties(data.getString("properties"));
      }

      if (!data.has("nickname")
          || data.getString("nickname").equalsIgnoreCase("null")
          || data.getString("nickname").length() == 0) {
        numberCek = mcrypt.decrypt(data.getString("number"));
        if (numberCek.equalsIgnoreCase("XXbsp") || numberCek.equalsIgnoreCase("XXXXXXned")) {
          if (account.has("vendor")) {
            setName(account.getJSONObject("vendor").getString("name"));
          }
        } else {
          setName(data.getString("number"));
        }
      } else {
        setName(data.getString("nickname"));
      }
    } catch (Exception e) {
      Timber.d("new product" + e);
    }
  }

  public Product(product data, account account) {
    try {
      setId_product(data.id);
      setNumber(data.number);
      setType(data.type);
      setCreated_at(data.created_at);
      setUpdated_at(data.updated_at);
      setDeleted_at(data.deleted_at);
      setBalance(data.balance);
      Timber.e("propertis product "+data.properties);
      setProperties(data.properties);
      if (Integer.valueOf(data.account_id) != null) setAccount_id(data.account_id);

      if(data.nickname != null && data.nickname.length() > 0){
        setName(data.nickname);
      }else{
        numberCek = mcrypt.decrypt(data.number);
        if (numberCek.equalsIgnoreCase("XXbsp") || numberCek.equalsIgnoreCase("XXXXXXned")) {
          if (account.vendor != null) {
            setName(account.vendor.name);
          }
        } else {
          setName(data.number);
        }
      }
    } catch (Exception e) {
      Timber.e("ERROR "+e);
    }
  }


  public int getId() {
    return id;
  }

  public Product setId(int id) {
    this.id = id;
    return this;
  }

  public Product setId_product(int id_product) {
    this.id_product = id_product;
    return this;
  }

  public int getAccount_id() {
    return account_id;
  }

  public Product setAccount_id(int account_id) {
    this.account_id = account_id;
    return this;
  }

  public Product setBalance(float balance) {
    this.balance = balance;
    return this;
  }

  public Product setCreated_at(String created_at) {
    this.created_at = created_at;
    return this;
  }

  public Product setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
    return this;
  }

  public Product setType(String type) {
    this.type = type;
    return this;
  }

  public Product setDeleted_at(String deleted_at) {
    this.deleted_at = deleted_at;
    return this;
  }

  public Product setNumber(String number) {
    this.number = number;
    return this;
  }

  public Product setName(String name) {
    this.name = name;
    return this;
  }

  public Product setColor(int color) {
    this.color = color;
    return this;
  }

  public Product setProperties(String properties) {
    this.properties = properties;
    return this;
  }

  public String getProperties() {
    return properties;
  }

  public int getId_product() {
    return id_product;
  }

  public float getBalance() {
    return balance;
  }

  public String getCreated_at() {
    return created_at;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public String getType() {
    return type;
  }

  public String getDeleted_at() {
    return deleted_at;
  }

  public String getNumber() {
    return number;
  }

  public String getName() {
    return name;
  }

  public int getColor() {
    return color;
  }

  public Invest getInvest(){
    return this.invest;
  }

  @Override public String toString() {
    return mcrypt.decrypt(this.name);            // What to display in the Spinner list.
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Account getAccount() {
    return account;
  }
}
