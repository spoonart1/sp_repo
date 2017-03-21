package javasign.com.dompetsehat.models;

import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.models.json.vendor;

/**
 * Created by aves on 4/7/15.
 */
public class Vendor {

  public static final String TYPE_BANK = "bank";
  public static final String TYPE_MARKETPLACE = "marketplace";
  public static final String TYPE_MANUAL = "manual";
  public static final String TYPE_INVESTMENT = "investment";

  private int id;
  private String vendor_name;
  private String vendor_type;
  private String vendor_image;

  public Vendor() {
    super();
  }

  public Vendor(vendor data) {
    MCryptNew mcrypt = new MCryptNew();
    setId(data.id);
    setVendor_image(mcrypt.decrypt(data.image));
    setVendor_name(mcrypt.decrypt(data.name));
    setVendor_type(data.type);
  }

  public int getId() {
    return id;
  }

  public Vendor setId(int id) {
    this.id = id;
    return this;
  }

  public String getVendor_image() {
    return vendor_image;
  }

  public Vendor setVendor_image(String vendor_image) {
    if (vendor_image == null) vendor_image = "";
    this.vendor_image = vendor_image;
    return this;
  }

  public String getVendor_name() {
    return vendor_name;
  }

  public Vendor setVendor_name(String vendor_name) {
    this.vendor_name = vendor_name;
    return this;
  }

  public String getVendor_type() {
    //1	bank
    //2	marketplace
    //3	manual
    //4	investment
    if(id == 1 || id == 2 || id == 3 || id == 4 || id == 7 || id == 8 || id == 11){
      return Vendor.TYPE_BANK;
    }else if(id == 5) {
      return Vendor.TYPE_MARKETPLACE;
    }else if(id == 6 || id == 9){
      return Vendor.TYPE_MANUAL;
    }else if(id == 10){
      return Vendor.TYPE_INVESTMENT;
    }
    return vendor_type;
  }

  public Vendor setVendor_type(String vendor_type) {
    this.vendor_type = vendor_type;
    return this;
  }
}
