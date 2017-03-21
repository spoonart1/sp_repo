package javasign.com.dompetsehat.models.response;

import javasign.com.dompetsehat.models.json.account;

/**
 * Created by avesina on 12/23/16.
 */

public class SyncAccountResponse extends ParentResponse {
  public Response response;
  public class Response{
    public account accounts;
  }
}
