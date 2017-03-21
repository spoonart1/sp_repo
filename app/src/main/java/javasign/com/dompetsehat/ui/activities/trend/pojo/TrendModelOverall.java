package javasign.com.dompetsehat.ui.activities.trend.pojo;

/**
 * Created by bastianbentra on 8/16/16.
 */
public class TrendModelOverall {
  public String title;
  public int lineColor;
  public float[] amountFlows;

  public TrendModelOverall(String title, int lineColor, float[] amountFlows){
    this.title = title;
    this.lineColor = lineColor;
    this.amountFlows = amountFlows;
  }
}
