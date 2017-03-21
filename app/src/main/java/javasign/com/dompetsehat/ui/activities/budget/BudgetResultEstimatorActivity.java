package javasign.com.dompetsehat.ui.activities.budget;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.presenter.budget.BudgetPresenter;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterBudgetResultEstimator;
import javasign.com.dompetsehat.ui.activities.budget.base.BaseBudgetActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/26/16.
 */
public class BudgetResultEstimatorActivity extends BaseBudgetActivity {

  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Bind(R.id.btn_done) Button btn_done;

  private SessionManager sessionManager;
  @Inject BudgetPresenter presenter;
  AdapterBudgetResultEstimator adapter;
  List<Budget> budgets;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_budget_result_estimator);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);

    Intent intent = getIntent();
    double monthly_income = intent.getExtras().getDouble(BudgetSetupActivity.MONTHLY_INCOME);
    double kredit = intent.getExtras().getDouble(BudgetSetupActivity.KREDIT);
    int child = intent.getExtras().getInt(BudgetSetupActivity.KIDS);

    presenter.hitungEstimasiBudget(monthly_income, kredit, child);
    btn_done.setEnabled(false);

    init();
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recycleview.setLayoutManager(layoutManager);
    sessionManager = new SessionManager(this);
    setData();
  }

  private void setData() {
    List<Budget> models = new ArrayList<>();
    adapter = new AdapterBudgetResultEstimator(models);
    adapter.setOnTouchItemView((pos, v, budget) -> {
      AlertDialog dialog = new AlertDialog.Builder(this).setNegativeButton(getString(R.string.delete),
          (dialogInterface, i) -> {

          }).create();
      dialog.show();
    });
    recycleview.setAdapter(adapter);
  }

  @OnClick(R.id.btn_done) void onDone() {
    if (this.budgets != null) {
      sessionManager.setUserHasLaunchedAppOnce();
      presenter.saveEstimations(this.budgets);
      Helper.trackThis(this, "User telah mengisi form preset budget/anggaran");
      Helper.finishAllPreviousActivityWithNextTarget(this, NewMainActivityMyFin.class,
          new Intent().putExtra("default", State.FLAG_FRAGMENT_BUDGET));
    }
  }

  @Override public void setAdapterBudget(List<Budget> budgets) {
    super.setAdapterBudget(budgets);
    runOnUiThread(() -> {
      btn_done.setEnabled(true);
      this.budgets = budgets;
      adapter.setModels(budgets);
      recycleview.setAdapter(adapter);
      adapter.notifyDataSetChanged();
    });
  }

  @OnClick(R.id.btn_edit) void onEdit() {
    finish();
  }

  @Override protected void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
