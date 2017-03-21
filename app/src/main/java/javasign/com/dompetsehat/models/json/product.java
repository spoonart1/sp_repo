package javasign.com.dompetsehat.models.json;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by aves on 8/10/16.
 */

public class product {
  public int id;
  public int account_id;
  public String number;
  public String type;
  public Float balance;
  public String nickname;
  public List<cashflow> cashflow;
  @SerializedName("properties") public String properties;
  public String created_at;
  public String updated_at;
  public String deleted_at;
}
