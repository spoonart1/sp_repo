package javasign.com.dompetsehat.presenter.budget;

import android.support.annotation.Nullable;
import java.util.List;

import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;
import javasign.com.dompetsehat.ui.activities.budget.pojo.HeaderBudget;
import javasign.com.dompetsehat.ui.activities.budget.pojo.ScheduleFragmentModel;

/**
 * Created by aves on 9/1/16.
 */

public interface BudgetInterface extends MvpView {
    void setAdapterBudget(List<Budget> budgets);
    void setAdapterHeader(@Nullable HeaderBudget headerBudget);
    void setSHModel(AdapterScheduler.SHModel model);
    void setBudget(AdapterScheduler.SHModel model,Budget budget);
    void setScheduler(List<ScheduleFragmentModel> models);
    void setDetailCash(Integer[] ids);
}
