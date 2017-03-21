package javasign.com.dompetsehat.models.response;

import java.util.ArrayList;
import javasign.com.dompetsehat.models.json.alarm;
import javasign.com.dompetsehat.models.json.debt;

/**
 * Created by avesina on 2/17/17.
 */

public class DebtsResponse extends ParentResponse {
  public Response response;

  public class Response{
    public ArrayList<debt> debts;
    public debt debt;
    public alarm alarm;
  }
}
