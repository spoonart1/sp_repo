package javasign.com.dompetsehat.models.response;

import java.util.List;

/**
 * Created by aves on 8/10/16.
 */

public class ErrorModel {
  public String status;
  public String msg;
  public String message;
  public MamiDataResponse.Data data;

  public class Data {
    public List<Validation> validation;

    public class Validation {
      public String code;
      public String message;
    }
  }
}
