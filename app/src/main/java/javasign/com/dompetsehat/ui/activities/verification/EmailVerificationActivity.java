package javasign.com.dompetsehat.ui.activities.verification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.verification.VerificationInterface;
import javasign.com.dompetsehat.presenter.verification.VerificationPresenter;
import javax.inject.Inject;

/**
 * Created by lafran on 3/10/17.
 */

public class EmailVerificationActivity extends BaseActivity implements VerificationInterface {

  @Bind(R.id.et_email) EditText et_email;
  @Bind(R.id.viewflipper) ViewFlipper viewflipper;
  @Bind(R.id.label2) TextView label2;
  @Inject VerificationPresenter presenter;
  ProgressDialog dialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_email_verification);
    getActivityComponent().inject(this);
    ButterKnife.bind(this);
    presenter.attachView(this);
    //label2.setText("Pastikan email kamu masukan benar");
    presenter.init();
    dialog = new ProgressDialog(this);
  }

  @OnClick(R.id.btn_verify) void doVerification() {
    dialog.setMessage(getString(R.string.loading_verification));
    dialog.show();
    presenter.setEmail(et_email.getText().toString());
  }

  @OnClick(R.id.btn_action) void doResendEmail() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
    startActivity(intent);
  }

  @OnClick(R.id.btn_resend) void resend(){
    viewflipper.showPrevious();
  }

  @Override public void setUser(String phone, String email) {
    runOnUiThread(() -> {
      et_email.setText(email);
      //et_email.setEnabled(true);
    });
  }

  @Override public void onError() {
    dialog.dismiss();
  }

  @Override public void onNext() {
    dialog.dismiss();
    viewflipper.showNext();
  }
}
