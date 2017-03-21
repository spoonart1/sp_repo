package javasign.com.dompetsehat.ui.event;

/**
 * Created by lafran on 9/13/16.
 */

public class FormEditTextFieldEvent {
  public String text;
  public String countryCode;
  public int viewIdToBeFilled;

  public FormEditTextFieldEvent(String text, int viewIdToBeFilled, String countryCode){
    this.text = text;
    this.viewIdToBeFilled = viewIdToBeFilled;
    this.countryCode = countryCode;
  }
}
