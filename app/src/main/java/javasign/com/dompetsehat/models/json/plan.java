package javasign.com.dompetsehat.models.json;

import java.util.ArrayList;

/**
 * Created by avesina on 9/21/16.
 */

public class plan {
  public int id;
  public int local_id;
  public int user_id;
  public int type;
  public int account_id;
  public int product_id;
  public String title;
  public double total;
  public String date_start;
  public int lifetimes;
  public double amount_monthly;
  public double amount_yearly;
  public double amount_cash;
  public double risk;
  public ArrayList<plan_product> plan_products;
  public dana_pensiun dana_pensiun;
  public dana_darurat dana_darurat;
  public dana_kuliah dana_kuliah;
  public String created_at;
  public String updated_at;
  public String deleted_at;
}
