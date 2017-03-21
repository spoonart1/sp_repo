package javasign.com.dompetsehat.ui.activities.budget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.budget.base.BaseBudgetActivity;
import javasign.com.dompetsehat.ui.fragments.budget.BudgetFragment;

/**
 * Created by bastianbentra on 8/15/16.
 */
public class BudgetActivity extends BaseBudgetActivity{

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_budget);
    ButterKnife.bind(this);

    setTitle(getString(R.string.list_of_budget));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    ButterKnife.findById(this, R.id.ic_menu).setVisibility(View.GONE);
    ButterKnife.findById(this, R.id.ic_search).setVisibility(View.GONE);
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    finish();
  }

  private void init() {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fl_content, new BudgetFragment()).commit();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
