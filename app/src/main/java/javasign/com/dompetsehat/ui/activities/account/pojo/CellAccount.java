package javasign.com.dompetsehat.ui.activities.account.pojo;

import android.view.View;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.BaseModel;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.utils.DSFont;

/**
 * Created by avesina on 9/8/16.
 */

public class CellAccount extends BaseModel {
  int type;
  int color;
  int vendorId;
  String name;
  Product product;
  Account account;
  Vendor vendor;
  double balance;
  boolean is_synced = true;
  String icon = DSFont.Icon.dsf_bank.getFormattedName();

  public int childPos;
  public boolean isLastPos = false;


  public CellAccount setColor(int color) {
    this.color = color;
    return this;
  }

  public CellAccount setIs_synced(boolean is_synced) {
    this.is_synced = is_synced;
    return this;
  }

  public boolean is_synced() {
    return is_synced;
  }

  public CellAccount setVendorId(int vendorId){
    this.vendorId = vendorId;
    return this;
  }

  public CellAccount setName(String name) {
    this.name = name;
    return this;
  }

  public CellAccount setAccount(Account account) {
    this.account = account;
    return this;
  }

  public CellAccount setProduct(Product product) {
    this.product = product;
    return this;
  }

  public CellAccount setVendor(Vendor vendor) {
    this.vendor = vendor;
    return this;
  }

  public CellAccount setType(int type) {
    this.type = type;
    return this;
  }

  public CellAccount setBalance(double balance) {
    this.balance = balance;
    return this;
  }

  public CellAccount setIcon(String icon) {
    this.icon = icon;
    return this;
  }

  public int getColor() {
    return color;
  }

  public int getType() {
    return type;
  }

  public Account getAccount() {
    return account;
  }

  public Product getProduct() {
    return product;
  }

  public Vendor getVendor() {
    return vendor;
  }

  public String getName() {
    return name;
  }

  public double getBalance() {
    return balance;
  }

  public String getIcon() {
    return icon;
  }

  public int getVendorId(){
    return vendorId;
  }
}
