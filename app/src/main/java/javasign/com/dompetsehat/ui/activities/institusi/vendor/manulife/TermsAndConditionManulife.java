package javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by lafran on 1/16/17.
 */

public class TermsAndConditionManulife extends BaseActivity {

  @Bind(R.id.webview) WebView webview;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_terms_and_condition_manulife);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    init();
  }

  private void init() {
    String path = "file:///android_asset/supported_files/klikmami_tnc.html";
    webview.loadUrl(path);
  }

  @OnClick({R.id.btn_disagree, R.id.btn_agree}) void onResult(View v){
    switch (v.getId()){
      case R.id.btn_disagree:
        Helper.trackThis(this, "User tidak menyetujui TNC klikMAMI");
        break;
      case R.id.btn_agree:
        RegisterMamiActivity.accessFrom = "My Referral";
        Helper.goTo(this, RegisterMamiActivity.class);
        break;
    }

    finish();
  }
}
