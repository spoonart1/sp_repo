package javasign.com.dompetsehat.ui.activities.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.fragments.feedback.FeedBackFragment;

/**
 * Created by bastianbentra on 8/23/16.
 */
public class FeedbackActivity extends BaseActivity {


  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    setTitle(R.string.feedback);
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init(){
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.fl_content, new FeedBackFragment()).commit();
  }
}
