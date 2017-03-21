package javasign.com.dompetsehat.models.response;

import javasign.com.dompetsehat.models.json.category;

/**
 * Created by avesina on 2/14/17.
 */

public class RenameCategoryResponse extends ParentResponse {
  public Data data;
  public class Data{
    public category category;
  }
}
