package javasign.com.dompetsehat.models;

/**
 * Created by lafran on 10/21/16.
 */

public class BaseModel<T> {
  private Object object;
  public void setTag(Object object){
    this.object = object;
  }

  public Object getTag(){
    return object;
  }
}
