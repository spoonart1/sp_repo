package javasign.com.dompetsehat.ui.activities.budget.base;

import android.support.annotation.Nullable;
import java.util.List;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.budget.BudgetInterface;
import javasign.com.dompetsehat.presenter.category.AddCategoryInterface;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;
import javasign.com.dompetsehat.ui.activities.budget.pojo.HeaderBudget;
import javasign.com.dompetsehat.ui.activities.budget.pojo.ScheduleFragmentModel;

/**
 * Created by lafran on 10/20/16.
 */

public class BaseBudgetActivity extends BaseActivity implements BudgetInterface,
    AddCategoryInterface{

  @Override public void setAdapterBudget(List<Budget> budgets) {

  }

  @Override public void setAdapterHeader(@Nullable HeaderBudget headerBudget) {

  }

  @Override public void setSHModel(AdapterScheduler.SHModel model) {

  }

  @Override public void setBudget(AdapterScheduler.SHModel model,Budget budget) {

  }

  @Override public void setScheduler(List<ScheduleFragmentModel> models) {

  }

  @Override public void setDetailCash(Integer[] ids) {

  }

  @Override public void setCategory(Category category) {

  }

  @Override public void setUserCategory(UserCategory userCategory) {

  }

  @Override public void setCashflow(Cash cashflow) {

  }

  @Override public void setIconCategory(String textLabel, Category category) {

  }
}
