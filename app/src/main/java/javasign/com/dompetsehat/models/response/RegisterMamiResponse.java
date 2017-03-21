package javasign.com.dompetsehat.models.response;

import java.util.List;
import javasign.com.dompetsehat.models.json.account;

/**
 * Created by avesina on 11/7/16.
 */

public class RegisterMamiResponse extends ParentResponse {
  public Data data;
  public class Data{
    public MamiResponse mami_response;
    public class MamiResponse{
      public int ClientStat;
      public String UserIdentityToken;
      public String RequestId;
      public List<Validation> validation;
      public List<Notification> notification;
      public List<Error> error;
      public class Validation{}
      public class Notification{}
      public class Error{}
    }
    public List<account> account;
  }

}
