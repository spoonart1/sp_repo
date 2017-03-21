package javasign.com.dompetsehat.ui.activities.debts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.fragments.comission.ComissionFragment;
import javasign.com.dompetsehat.ui.fragments.debts.DebtFragment;

/**
 * Created by avesina on 2/17/17.
 */

public class DebtsActivity extends BaseActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comission);

    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    setTitle(getString(R.string.title_list_debt));
    init();
  }

  private void init() {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fl_content, new DebtFragment()).commit();
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    finish();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }
}
