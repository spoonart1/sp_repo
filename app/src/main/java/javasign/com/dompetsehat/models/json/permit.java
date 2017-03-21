package javasign.com.dompetsehat.models.json;

/**
 * Created by avesina on 1/9/17.
 */

public class permit {
  boolean plan;
  boolean budget;
  boolean alarm;
  boolean transaction;

  public permit setAlarm(boolean alarm) {
    this.alarm = alarm;
    return this;
  }

  public permit setBudget(boolean budget) {
    this.budget = budget;
    return this;
  }

  public permit setPlan(boolean plan) {
    this.plan = plan;
    return this;
  }

  public permit setTransaction(boolean transaction) {
    this.transaction = transaction;
    return this;
  }
}
