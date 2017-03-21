package javasign.com.dompetsehat.ui.activities.budget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.budget.BudgetPresenter;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterSchedulerFragmentPager;
import javasign.com.dompetsehat.ui.activities.budget.base.BaseBudgetActivity;
import javasign.com.dompetsehat.ui.activities.budget.pojo.ScheduleFragmentModel;
import javax.inject.Inject;

/**
 * Created by lafran on 10/4/16.
 */

public class SchedulerMainActivity extends BaseBudgetActivity {

  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.pager) ViewPager pager;
  @Inject BudgetPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scheduler_main);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    setTitle(getString(R.string.select_periode_budget));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    finish();
  }

  private void init() {
    presenter.loadScheduler();
  }

  @Override public void setScheduler(List<ScheduleFragmentModel> models) {
    super.setScheduler(models);
    runOnUiThread(() -> {
      AdapterSchedulerFragmentPager adapterPager =
          new AdapterSchedulerFragmentPager(getSupportFragmentManager(), this, models);
      pager.setAdapter(adapterPager);
      tablayout.setupWithViewPager(pager);
    });
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
