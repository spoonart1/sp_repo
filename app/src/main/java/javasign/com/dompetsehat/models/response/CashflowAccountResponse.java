package javasign.com.dompetsehat.models.response;

import java.util.List;
import javasign.com.dompetsehat.models.json.account;

/**
 * Created by avesina on 1/11/17.
 */

public class CashflowAccountResponse extends ParentResponse {
  public Response response;
  public class Response{
    public int user_id;
    public List<account> accounts;
  }
}
