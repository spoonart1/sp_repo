package javasign.com.dompetsehat.services.mandrill;

/**
 * Created by avesina on 2/17/17.
 */

public class Recipient {

  private String email;
  private String name;
  private String type="to";

  public String getEmail() {
    return email;
  }

  public void setName(String name ){
    this.name =  name ;
  }


  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

}
