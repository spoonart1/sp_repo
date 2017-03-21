package javasign.com.dompetsehat.ui.event;

import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;

/**
 * Created by lafran on 10/4/16.
 */

public class BudgetSchedulerEvent {

  public AdapterScheduler.SHModel shModel;
  public BudgetSchedulerEvent(AdapterScheduler.SHModel model){
    this.shModel = model;
  }
}
