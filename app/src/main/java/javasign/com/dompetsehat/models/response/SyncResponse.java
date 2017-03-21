package javasign.com.dompetsehat.models.response;

import java.util.List;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.alarm;
import javasign.com.dompetsehat.models.json.budget;
import javasign.com.dompetsehat.models.json.cashflow;
import javasign.com.dompetsehat.models.json.plan;

/**
 * Created by avesina on 10/10/16.
 */

public class SyncResponse extends ParentResponse {
  public Response response;
  public class Response{
    public List<plan>plan;
    public List<account> accounts;
    public List<budget> budget;
    public List<cashflow> cashflows;
    public List<alarm> alarm;
  }
}
