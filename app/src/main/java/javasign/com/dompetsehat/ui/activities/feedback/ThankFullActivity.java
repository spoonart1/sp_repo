package javasign.com.dompetsehat.ui.activities.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;

/**
 * Created by bastianbentra on 8/23/16.
 */
public class ThankFullActivity extends BaseActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_thankfull);
    getActivityComponent().inject(this);
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    ButterKnife.bind(this);

  }

  @OnClick(R.id.btn_close) void onClose(){
    finish();
  }

  @Override protected void onPause() {
    super.onPause();
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
