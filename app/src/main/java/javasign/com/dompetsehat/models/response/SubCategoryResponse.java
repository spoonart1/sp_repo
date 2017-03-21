package javasign.com.dompetsehat.models.response;

import javasign.com.dompetsehat.models.json.user_defined;

/**
 * Created by avesina on 10/31/16.
 */

public class SubCategoryResponse extends ParentResponse{
  public Data data;
  public class Data{
    public user_defined user_category;
  }
}
