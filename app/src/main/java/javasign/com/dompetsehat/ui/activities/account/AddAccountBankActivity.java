package javasign.com.dompetsehat.ui.activities.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.account.AddAccountBankPresenter;
import javasign.com.dompetsehat.ui.activities.account.base.BaseAccountActivity;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/18/16.
 */
public class AddAccountBankActivity extends BaseAccountActivity {

  @Bind(R.id.et_username) MaterialEditText et_username;
  @Bind(R.id.et_password) MaterialEditText et_password;
  @Bind(R.id.et_confirm_password) MaterialEditText et_confirm_password;
  @Bind(R.id.tv_vendor_name) TextView tv_vendor_name;
  @Bind(R.id.tv_note) TextView tv_note;
  @Bind(R.id.tv_disclaimer) TextView tv_disclaimer;
  @Bind(R.id.tv_greeting) TextView tv_greeting;
  @Bind(R.id.ic_menu) View btn_login;
  @Bind(R.id.ll_parent) LinearLayout ll_parent;

  private boolean isPasswordShown = false;
  @Inject AddAccountBankPresenter presenter;
  ProgressDialog dialog;
  int vendor;
  String name_vendor;
  private static boolean MODE_EDIT = false;
  private Integer ID_ACCOUNT;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_account_bank);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    dialog = new ProgressDialog(this);

    setTitle(Words.NONE);
    if (getIntent().hasExtra(Words.ID_VENDOR) && getIntent().hasExtra(Words.NAMA_VENDOR)) {
      vendor = getIntent().getExtras().getInt(Words.ID_VENDOR);
      name_vendor = getIntent().getExtras().getString(Words.NAMA_VENDOR);
      Timber.e("avesina keren tiada tara "+vendor);
      if(vendor == 12){
        String name = tv_greeting.getText().toString();
        name = name.replace("Internet Banking",getString(R.string.account));
        tv_greeting.setText(name);
      }
    } else {
      Toast.makeText(this, getString(R.string.error_source_unknown), Toast.LENGTH_LONG).show();
      finish();
    }
    if (getIntent().hasExtra(ManageEachAccountActivity.MODE_EDIT) && getIntent().hasExtra(
        Words.ACCOUNT_ID)) {
      MODE_EDIT = getIntent().getExtras().getBoolean(ManageEachAccountActivity.MODE_EDIT);
      if (MODE_EDIT) {
        tv_greeting.setText(getString(R.string.edit_internet_banking));
        if (getIntent().hasExtra(Words.ACCOUNT_ID)) {
          ID_ACCOUNT = getIntent().getExtras().getInt(Words.ACCOUNT_ID);
          presenter.loadAccount(ID_ACCOUNT);
        } else {
          Toast.makeText(this, getString(R.string.error_source_unknown), Toast.LENGTH_LONG).show();
          finish();
        }
      }
    }
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    IconicsTextView ic_back = ButterKnife.findById(this, R.id.ic_back);
    IconicsTextView ic_menu = ButterKnife.findById(this, R.id.ic_menu);
    ic_back.setTextColor(Color.WHITE);
    ic_menu.setBackground(ContextCompat.getDrawable(this, R.drawable.button_white_oval_2));

    int id = getIntent().getIntExtra(Words.ID_VENDOR, 0);
    int color = AccountView.accountColor.get(id);
    String url = AccountView.urls.get(id);
    tv_note.setText(url);
    String name = AccountView.vendors.get(id);
    tv_vendor_name.setText(name);

    View v = ButterKnife.findById(this, R.id.rootview);
    v.setBackgroundColor(color);
    GeneralHelper.statusBarColor(getWindow(), color);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    onBack();
  }

  public void setSpan() {
    SpannableStringBuilder spanTxt = new SpannableStringBuilder(getString(R.string.dengan_ini));
    spanTxt.append(" ");
    spanTxt.append(Words.toTitleCase(getString(R.string.terms)));
    spanTxt.append(" ");
    spanTxt.setSpan(new ClickableSpan() {
      @Override public void onClick(View widget) {
        startActivity(
            new Intent(AddAccountBankActivity.this, WebviewInstutisiActivity.class).putExtra(
                WebviewInstutisiActivity.URL_KEY, "http://dompetsehat.com/terms")
                .putExtra("from", WebviewInstutisiActivity.FLAG_TO_HIDE)
                .putExtra("accent-color", Helper.GREEN_DOMPET_COLOR));
      }
    }, spanTxt.length() - getString(R.string.terms).length() - 1, spanTxt.length() - 1, 0);
    spanTxt.append(" ");
    spanTxt.append(getString(R.string.and));
    spanTxt.append(" ");
    spanTxt.append(Words.toTitleCase(getString(R.string.privacy)));
    spanTxt.append(" ");
    spanTxt.setSpan(new ClickableSpan() {
      @Override public void onClick(View widget) {
        startActivity(
            new Intent(AddAccountBankActivity.this, WebviewInstutisiActivity.class).putExtra(
                WebviewInstutisiActivity.URL_KEY, "http://dompetsehat.com/privacy")
                .putExtra("from", WebviewInstutisiActivity.FLAG_TO_HIDE)
                .putExtra("accent-color", Helper.GREEN_DOMPET_COLOR));
      }
    }, spanTxt.length() - getString(R.string.privacy).length() - 1, spanTxt.length() - 1, 0);
    tv_disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
    tv_disclaimer.setText(spanTxt, TextView.BufferType.SPANNABLE);
  }

  @OnClick(R.id.show_pass) void hawkEye(View v) {
    ImageView eye = (ImageView) v;
    if (!isPasswordShown) {
      et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      et_confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      eye.setColorFilter(ContextCompat.getColor(this, R.color.green_dompet_ori));
      isPasswordShown = true;
    } else {
      et_password.setInputType(129);
      et_confirm_password.setInputType(129);
      isPasswordShown = false;
      eye.setColorFilter(getResources().getColor(R.color.grey_600));
    }

    int pos = et_password.getText().length();
    et_password.setSelection(pos);
  }

  @OnClick(R.id.ic_menu) void loginBank() {
    if (TextUtils.isEmpty(et_username.getText().toString())) {

    } else if (TextUtils.isEmpty(et_password.getText().toString())) {

    } else if (TextUtils.isEmpty(et_confirm_password.getText().toString())) {

    } else if (et_username.getText().toString().length() < 5) {
      et_username.setError("Username tidak valid");
    } else {
      Helper.hideKeyboard(this);
      if (MODE_EDIT) {
        presenter.ubahBank(vendor, ID_ACCOUNT, et_username.getText().toString(),
            et_password.getText().toString());
      } else {
        presenter.loginBank(vendor, et_username.getText().toString(),
            et_password.getText().toString(), name_vendor);
      }
    }
  }

  private void init() {
    Words.setButtonToListen(btn_login, et_password, et_confirm_password, "",
        getString(R.string.error_password_doesnt_match), et_username);
    setSpan();
    if(vendor == 8){
      et_password.setHint(getString(R.string.password_only));
      et_password.setFloatingLabelText(getString(R.string.password_only));
      et_confirm_password.setHint(getString(R.string.confirm_password_only));
      et_confirm_password.setFloatingLabelText(getString(R.string.confirm_password_only));
    }
  }

  @Override public void startLoading() {
    dialog.setCancelable(false);
    dialog.setMessage(getString(R.string.connecting) + "...");
    dialog.show();
  }

  @Override public void stopLoading() {
    dialog.dismiss();
  }

  @Override public void finishUpdateAccount(String msg) {
    dialog.dismiss();
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    Helper.finishAllPreviousActivityWithNextTarget(this, NewManageAccountActivity.class);
  }

  @Override public void finishCreatedAccount(Account account, double Saldo, String message) {
    super.finishCreatedAccount(account, Saldo, message);
    startActivityForResult(new Intent(this, FinalCreateAccountActivity.class).putExtra(
        FinalCreateAccountActivity.TAG_ID_ACCOUNT, account.getIdaccount())
            .putExtra(Words.NAMA_VENDOR, name_vendor)
            .putExtra(FinalCreateAccountActivity.TAG_SALDO, Saldo)
            .putExtra(FinalCreateAccountActivity.TAG_USERNAME, et_username.getText().toString())
            .putExtra(FinalCreateAccountActivity.TAG_MESSAGE, message),
        State.FLAG_ACTIVITY_WILL_FINISH_AFTER);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == State.FLAG_ACTIVITY_WILL_FINISH_AFTER) {
      finish();
    }
  }

  @Override public void setDataAccount(String username, double saldo) {
    super.setDataAccount(username, saldo);
    et_username.setText(username);
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void showMessage(String message) {
    Helper.showCustomSnackBar(ll_parent, getLayoutInflater(), message, true,
        ContextCompat.getColor(this, R.color.red_400), Gravity.BOTTOM);
    super.showMessage(message);
  }
}
