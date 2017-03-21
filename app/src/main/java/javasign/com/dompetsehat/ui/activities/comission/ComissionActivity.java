package javasign.com.dompetsehat.ui.activities.comission;

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

/**
 * Created by lafran on 9/26/16.
 */

public class ComissionActivity extends BaseActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comission);

    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    setTitle(getString(R.string.list_comission));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    finish();
  }

  private void init() {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fl_content, new ComissionFragment()).commit();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
