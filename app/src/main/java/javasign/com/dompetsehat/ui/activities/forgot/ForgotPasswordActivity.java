package javasign.com.dompetsehat.ui.activities.forgot;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.forgot.ForgotPasswordInterface;
import javasign.com.dompetsehat.presenter.forgot.ForgotPasswordPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/1/16.
 */
public class ForgotPasswordActivity extends BaseActivity implements ForgotPasswordInterface {

  private final int FIX_COUNTDOWN_TIMER = 16;

  @Bind(R.id.rootview) View rootview;
  @Bind(R.id.tv_send) TextView tv_send;
  @Bind(R.id.tv_counter) TextView tv_counter;
  @Bind(R.id.viewflipper) ViewFlipper viewFlipper;
  @Bind(R.id.et_dialog_fragment_email) MaterialEditText et_email;
  @Bind(R.id.et_dialog_fragment_newpassword) MaterialEditText et_password;
  @Bind(R.id.et_dialog_fragment_confirmpassword) MaterialEditText et_confirm;

  @Inject ForgotPasswordPresenter presenter;

  private boolean showPassword = false;
  private boolean isCountdown = false;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);

    setTitle(getString(R.string.greeting_forgot));
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.btn_reset) void resetPassword(View v) {
    boolean isValid = true;
    if (TextUtils.isEmpty(et_email.getText())) {
      et_email.setError(getString(R.string.error_email_if_blank));
      isValid = false;
    }
    if (TextUtils.isEmpty(et_password.getText())) {
      et_password.setError(getString(R.string.error_password_if_blank));
      isValid = false;
    }

    if (!TextUtils.equals(et_confirm.getText().toString().toLowerCase(),
        et_password.getText().toString().toLowerCase())) {
      et_confirm.setError(getString(R.string.error_confirm_password_if_invalid));
      isValid = false;
    }

    if (!isValid) return;

    presenter.forgotPassword(et_email.getText().toString(), et_password.getText().toString());
    //sendJSONRequestForgot();
  }

  @Override public void successForgot(String message) {
    //Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
    //finish();
    Helper.hideKeyboard(getCurrentFocus(), tv_counter);
    beginCountDown(FIX_COUNTDOWN_TIMER);
    viewFlipper.setDisplayedChild(1);
  }

  private void beginCountDown(int sec){
    if(isCountdown)
      return;

    isCountdown = true;
    tv_send.setEnabled(false);
    new CountDownTimer(sec * 1000, 1000) {
      @Override public void onTick(long millisUntilFinished) {
        long second = millisUntilFinished/1000;
        tv_counter.setText(String.valueOf(second));
      }

      @Override public void onFinish() {
        tv_send.setEnabled(true);
        tv_counter.setText("0");
        isCountdown = false;
      }
    }.start();
  }

  @OnClick(R.id.tv_send) void doResendEmail(){
    presenter.forgotPassword(et_email.getText().toString(), et_password.getText().toString());
    beginCountDown(FIX_COUNTDOWN_TIMER);
  }

  @Override public void alertMessage(String message) {
    et_email.setError(message);
  }

  @Override protected void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
