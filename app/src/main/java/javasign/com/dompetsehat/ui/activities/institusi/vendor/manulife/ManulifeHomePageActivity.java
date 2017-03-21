package javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.InstitusiConditionDialog;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by lafran on 1/11/17.
 */

public class ManulifeHomePageActivity extends BaseActivity {

  private boolean isTryToLogin = false;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_homepage_manulife);
    GeneralHelper.statusBarColor(getWindow(), ContextCompat.getColor(this, R.color.green_manulife));
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    setTitle("");
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setTextColor(Color.WHITE);
    tv_title.setText(title);

    IconicsTextView ic_back = ButterKnife.findById(this, R.id.ic_back);
    ic_back.setTextColor(Color.WHITE);
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  private void init() {
    if(getIntent().hasExtra("action")){
      isTryToLogin = getIntent().getBooleanExtra("action", false);
    }

  }

  @OnClick(R.id.btn_next) void onNext(){
    openConditionDialog();
  }

  private void openConditionDialog(){
    InstitusiConditionDialog dialog = InstitusiConditionDialog.newInstance(
        isAgree -> {
          if(isAgree){
            if(isTryToLogin) {
              LoginKlikMamiActivity.accessFrom = "My Referral";
              Helper.goTo(this, LoginKlikMamiActivity.class);
            }
            else {
              Helper.goTo(this, TermsAndConditionManulife.class);
            }
            finish();
          }
        });
    dialog.showNow(getSupportFragmentManager());
  }
}
