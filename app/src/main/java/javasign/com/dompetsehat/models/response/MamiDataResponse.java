package javasign.com.dompetsehat.models.response;

import java.util.List;

/**
 * Created by avesina on 10/24/16.
 */

public class MamiDataResponse {
  public String status;
  public Data data;
  public class Data{
    public int ClientStat;
    public int CliRegStat;
    public String UserIdentityToken;
    public String UserId;
    public String ReferralCode;
    public String RequestId;
    public List<Validation> validation;
    public String Description;
    public class Validation{
      public String code;
      public String message;
    }
  }
}
