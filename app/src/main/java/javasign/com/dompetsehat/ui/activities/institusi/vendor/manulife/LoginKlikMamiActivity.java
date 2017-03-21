package javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.account.AddAccountBankPresenter;
import javasign.com.dompetsehat.presenter.mami.RegisterMamiPresenter;
import javasign.com.dompetsehat.ui.activities.account.AddAccountActivity;
import javasign.com.dompetsehat.ui.activities.account.NewManageAccountActivity;
import javasign.com.dompetsehat.ui.activities.closing.ClosingActivity;
import javasign.com.dompetsehat.ui.activities.institusi.base.BaseInstitusi;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.VerificationDialog;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Validate;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/22/16.
 */
public class LoginKlikMamiActivity extends BaseInstitusi {

  public static String accessFrom = "";

  @Bind(R.id.et_email) MaterialEditText et_email;
  @Bind(R.id.et_password) MaterialEditText et_password;
  @Bind(R.id.tv_forgot_password) TextView tv_forgot_password;
  @Bind(R.id.relative_layout) View relative_layout;
  @Bind(R.id.ll_register_content) View ll_register_content;
  @Bind(R.id.ll_keterangan) LinearLayout ll_keterangan;
  @Bind(R.id.btn_signUp) Button btn_signUp;

  private boolean isPasswordShown = false;
  private int mVendor;
  private boolean isEditMode = false;
  private int mAccountId;
  private ProgressDialog dialog;
  @Inject RegisterMamiPresenter presenter;
  @Inject AddAccountBankPresenter presenterBank;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_institusi);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    presenterBank.attachView(this);
    checkBundling();
    init();
    //et_email.setText("testskywalker900@gmail.com");
    //et_password.setText("Abcd1234");
  }

  private void checkBundling() {
    Bundle b = getIntent().getExtras();
    if (b != null) {
      mVendor = b.getInt(Words.ID_VENDOR, -1);
      mAccountId = b.getInt(Words.ACCOUNT_ID, -1);
      isEditMode = b.getBoolean("mode_edit", false);
    }
  }

  @OnClick(R.id.show_pass) void hawkEye(View v) {
    ImageView eye = (ImageView) v;
    if (!isPasswordShown) {
      et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      eye.setColorFilter(ContextCompat.getColor(this, R.color.white));
      isPasswordShown = true;
    } else {
      et_password.setInputType(129);
      isPasswordShown = false;
      eye.setColorFilter(getResources().getColor(R.color.grey_600));
    }

    int pos = et_password.getText().length();
    et_password.setSelection(pos);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.tv_forgot_password) void doForgotPassword() {
    Intent intent =
        new Intent(this, WebviewInstutisiActivity.class).putExtra(WebviewInstutisiActivity.URL_KEY,
            WebviewInstutisiActivity.URL_MAMI_FORGOT_PASS);
    startActivity(intent);
  }

  @OnClick(R.id.btn_signIn) void loginVendor(View v) {
    Helper.hideKeyboard(this.getCurrentFocus(), v);
    if (!Validate.isValidEmail(et_email.getText().toString())) {
      et_email.setError(getString(R.string.if_format_email_wrong));
      return;
    }
    if (!isEditMode) {
      presenter.loginMami(et_email.getText().toString(), et_password.getText().toString());
    } else {
      presenterBank.ubahBank(mVendor, mAccountId, et_email.getText().toString(),
          et_password.getText().toString());
    }
  }

  @Override public void successLogin(String reffCode, int account_id) {
    String sentence = getString(R.string.use_the_refferal_code_to_be_shared);

    startActivity(new Intent(this, ClosingActivity.class).putExtra(ClosingActivity.TITLE_KEY,
        getString(R.string.congrats))
        .putExtra("id_account", account_id)
        .putExtra("reffcode", reffCode)
        .putExtra(ClosingActivity.TEXT_2_KEY,
            getString(R.string.your_account_has_been_integrated_mami))
        .putExtra(ClosingActivity.TEXT_3_KEY, sentence));
    SessionManager sessionManager = new SessionManager(this);
    sessionManager.setHaveInstitutionAccount();
    String from = "";
    if(!TextUtils.isEmpty(LoginKlikMamiActivity.accessFrom)){
      from = "dari "+LoginKlikMamiActivity.accessFrom;
    }
    Helper.trackThis(this, "User telah berhasil login Manulife "+from);
    LoginKlikMamiActivity.accessFrom = "";
    setResult(RESULT_OK);
    finish();
  }

  private void init() {
    ll_register_content.setVisibility(isEditMode ? View.GONE : View.VISIBLE);
    if (getIntent().hasExtra(AddAccountActivity.IS_REGISTER_HIDDEN)) {
      ll_register_content.setVisibility(
          getIntent().getBooleanExtra(AddAccountActivity.IS_REGISTER_HIDDEN, false) ? View.GONE
              : View.VISIBLE);
    }
    tv_forgot_password.setPaintFlags(
        tv_forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    Words.setButtonToListen(ButterKnife.findById(this, R.id.btn_signIn), et_email, et_password);
    SessionManager sessionManager = new SessionManager(this);
    if (TextUtils.isEmpty(sessionManager.getPhoneMami()) || TextUtils.isEmpty(
        sessionManager.getEmailMami())) {
      ll_keterangan.setVisibility(View.GONE);
      btn_signUp.setVisibility(View.GONE);
    } else {
      ll_keterangan.setVisibility(View.VISIBLE);
      btn_signUp.setVisibility(View.VISIBLE);
    }
  }

  @OnClick(R.id.btn_signUp) void registerVendor() {
    //startActivity(
    //    new Intent(this, WebviewInstutisiActivity.class).putExtra(WebviewInstutisiActivity.URL_KEY,
    //        WebviewInstutisiActivity.URL_MAMI_DISCLAIMER));
    Helper.goTo(this, TermsAndConditionManulife.class);
  }

  @Override public void showMessage(int ClientStat, String message) {
    if (ClientStat == 0) {
      Helper.showCustomSnackBar(relative_layout, LayoutInflater.from(this), message, true,
          ContextCompat.getColor(this, R.color.red_400), Gravity.TOP);
    } else if (ClientStat != 1) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(message);
      if (ClientStat == 3 || ClientStat == 4) {
        builder.setNeutralButton("Disini", (dialogInterface, i) -> {
          startActivity(new Intent(this, WebviewInstutisiActivity.class).putExtra("url",
              new MCryptNew().decrypt(State.URL_MAIN_MAMI)).putExtra("from", "finalregsitration"));
        }).setNegativeButton("Di komputer", (dialogInterface, i) -> {
          //dialogInterface.dismiss();
          Helper.goTo(this, FinalRegistrationMamiAcitivity.class,
              new Intent().putExtra("direct_to", true));
        });
      } else if (ClientStat == 2) {
        builder.setPositiveButton("Daftar KlikMAMI", (dialogInterface, i) -> {
          VerificationDialog verificationDialog =
              VerificationDialog.newInstance((dialog2, phone, email) -> {
                dialog2.dismissAllowingStateLoss();
                Helper.goTo(this, TermsAndConditionManulife.class);
              });
          verificationDialog.show(getSupportFragmentManager(), "dialog-verify");
        });
      } else if (ClientStat == 5 || ClientStat == 6) {
        builder.setPositiveButton("OK", null);
      }
      AlertDialog alertDialog = builder.create();
      alertDialog.show();
    }
  }

  @Override public void startLoading() {
    super.startLoading();
    dialog = new ProgressDialog(this);
    dialog.setMessage("Sedang login kembali....");
    dialog.show();
  }

  @Override public void stopLoading() {
    super.stopLoading();
    if (dialog != null) {
      dialog.dismiss();
    }
  }

  @Override public void finishUpdateAccount(String mesage) {
    dialog.dismiss();
    Toast.makeText(this, mesage, Toast.LENGTH_LONG).show();
    Helper.finishAllPreviousActivityWithNextTarget(this, NewManageAccountActivity.class);
  }
}
