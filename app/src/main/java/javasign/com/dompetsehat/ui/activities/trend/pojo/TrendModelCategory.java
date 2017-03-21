package javasign.com.dompetsehat.ui.activities.trend.pojo;

import javasign.com.dompetsehat.models.Category;

/**
 * Created by bastianbentra on 8/16/16.
 */
public class TrendModelCategory {
  public float amountTransaction;
  public Category category;

  public TrendModelCategory(float amountTransaction, Category category){
    this.amountTransaction = amountTransaction;
    this.category = category;
  }
}
