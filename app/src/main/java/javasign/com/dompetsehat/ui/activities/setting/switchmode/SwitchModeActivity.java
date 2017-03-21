package javasign.com.dompetsehat.ui.activities.setting.switchmode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.ui.dialogs.HintDialog;

/**
 * Created by bastianbentra on 8/23/16.
 */
public class SwitchModeActivity extends BaseActivity implements
    CompoundButton.OnCheckedChangeListener {

  @Bind(R.id.ll_referral) LinearLayout contentReferral;
  @Bind(R.id.ll_both) LinearLayout contentBoth;
  @Bind(R.id.v_divider) View v_divider;
  @Bind(R.id.sw_both) SwitchCompat sw_both;
  @Bind(R.id.sw_referral) SwitchCompat sw_referral;

  private SessionManager sessionManager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_switch_mode);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    setTitle(getString(R.string.switch_mode));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {
    sessionManager = new SessionManager(this);
    int pickMode = sessionManager.getUserPickAppMode();

    switch (pickMode){
      case SessionManager.USER_PICK_MY_FIN:
        sessionManager.setCurrentUserPickAppMode(SessionManager.USER_PICK_MY_FIN);
        hideMenuModeMyFinAndReferral();
        break;

      case  SessionManager.USER_PICK_MY_REFERRAL:
        sessionManager.setCurrentUserPickAppMode(SessionManager.USER_PICK_MY_REFERRAL);
        hideMenuModeReferral();
        break;
    }


    sw_both.setOnCheckedChangeListener(this);
    sw_referral.setOnCheckedChangeListener(this);
  }

  protected void hideMenuModeReferral(){
    contentReferral.setVisibility(View.GONE);
    v_divider.setVisibility(View.GONE);
  }

  protected void hideMenuModeMyFinAndReferral(){
    contentBoth.setVisibility(View.GONE);
    v_divider.setVisibility(View.GONE);
  }

  @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if(!isChecked) return;

    HintDialog dialog = new HintDialog();
    dialog.setSwitchCompat((SwitchCompat) buttonView);
    if(buttonView.getId() == R.id.sw_referral)
      dialog.setPickMode(sessionManager, SessionManager.USER_PICK_MY_REFERRAL);
    else
      dialog.setPickMode(sessionManager, SessionManager.USER_PICK_MY_FIN);

    dialog.show(getSupportFragmentManager(), "hint");
  }
}
