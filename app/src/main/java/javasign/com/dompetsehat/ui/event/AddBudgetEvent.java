package javasign.com.dompetsehat.ui.event;

import android.content.Context;
import java.util.List;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by lafran on 9/13/16.
 */

public class AddBudgetEvent {

  //TODO : create custom constructor here, or some variables to be sent
  public Budget budget;
  public List<Budget> budgets;
  @Inject SyncPresenter presenter;
  Context context;

  public AddBudgetEvent(Context context, Budget budget) {
    this.context = context;
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    this.budget = budget;
    Helper.sync(context,presenter);
  }

  public AddBudgetEvent(Context context, List<Budget> budgets) {
    this.context = context;
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    this.budgets = budgets;
    Helper.sync(context,presenter);
  }
}
