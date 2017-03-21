package javasign.com.dompetsehat.ui.fragments.timeline.pojo;

import java.util.ArrayList;
import javasign.com.dompetsehat.models.Cash;

/**
 * Created by bastianbentra on 8/8/16.
 */
public class TimelineView {

  public Header header;
  public ArrayList<Cash> cashs;

  public static class Header{
    public String title;
    public String totalExpense;
    public String totalIncome;
    public String nettIncome;
    public String month_name;
  }
}
