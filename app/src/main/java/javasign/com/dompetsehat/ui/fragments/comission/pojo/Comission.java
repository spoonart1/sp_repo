package javasign.com.dompetsehat.ui.fragments.comission.pojo;

import android.graphics.Color;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by bastianbentra on 8/22/16.
 */
public class Comission {

  public enum Status{
    komisi(0), subscribed(1);

    public String from;
    public int colorIndicator;
    private Status(int type){
      if(type == 0){
        from = "komisi";
        colorIndicator = Helper.GREEN_DOMPET_COLOR;
      }
      else if(type == 1){
        from = "subscribed";
        colorIndicator = Color.parseColor("#F39D41");
      }
    }
  }

  public String date;
  public Status from;
  public double debetAmount;
  public double comissionAmount;
}
