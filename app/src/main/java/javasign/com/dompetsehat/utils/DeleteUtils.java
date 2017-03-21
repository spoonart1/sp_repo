package javasign.com.dompetsehat.utils;

import android.os.Handler;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.presenter.account.AccountPresenter;
import javasign.com.dompetsehat.presenter.account.EachAccountPresenter;
import javasign.com.dompetsehat.presenter.budget.BudgetPresenter;
import javasign.com.dompetsehat.presenter.main.FinplanPresenter;
import javasign.com.dompetsehat.presenter.reminder.ReminderPresenter;
import javasign.com.dompetsehat.presenter.transaction.TransactionsPresenter;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;

/**
 * Created by lafran on 12/16/16.
 */

public class DeleteUtils {
  public interface OnDeleteListener {
    void onCancelRemoving();
    void onDoneRemoving();
  }

  private final long timeout = 2500;
  private final Handler handler = new Handler();
  private Runnable pendingRemoval;
  private OnDeleteListener deleteListener;

  public DeleteUtils execute(Cash cash, TransactionsPresenter presenter) {
    pendingRemoval = () -> presenter.deleteTransaction(cash, deleteListener);
    handler.postDelayed(pendingRemoval, timeout);
    return this;
  }

  public DeleteUtils execute(Budget budget, BudgetPresenter presenter) {
    pendingRemoval = () -> presenter.deleteBudget(budget.getId(), deleteListener);
    handler.postDelayed(pendingRemoval, timeout);
    return this;
  }

  public DeleteUtils execute(Plan plan, FinplanPresenter presenter) {
    pendingRemoval = () -> presenter.deletePlan(plan.getId(), deleteListener);
    handler.postDelayed(pendingRemoval, timeout);
    return this;
  }

  public DeleteUtils execute(ReminderModel reminder, ReminderPresenter presenter) {
    pendingRemoval = () -> presenter.deleteReminder(reminder.identifier, deleteListener);
    handler.postDelayed(pendingRemoval, timeout);
    return this;
  }

  public DeleteUtils execute(Account account, AccountPresenter presenter) {
    pendingRemoval = () -> presenter.deleteAccount(account.getIdaccount(), deleteListener);
    handler.postDelayed(pendingRemoval, timeout);
    return this;
  }

  public DeleteUtils execute(Account account, EachAccountPresenter presenter) {
    pendingRemoval = () -> presenter.deleteAccount(account.getIdaccount(), deleteListener);
    handler.postDelayed(pendingRemoval, timeout);
    return this;
  }

  public DeleteUtils setOnDeleteListener(OnDeleteListener deleteListener) {
    this.deleteListener = deleteListener;
    return this;
  }

  public void cancelRemoval() {
    handler.removeCallbacks(pendingRemoval);

    if (deleteListener != null) deleteListener.onCancelRemoving();
  }
}
