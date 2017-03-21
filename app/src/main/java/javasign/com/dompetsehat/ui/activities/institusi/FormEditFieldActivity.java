package javasign.com.dompetsehat.ui.activities.institusi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.hbb20.CountryCodePicker;
import com.mikepenz.iconics.view.IconicsTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.mami.RegisterMamiPresenter;
import javasign.com.dompetsehat.presenter.profile.ProfilePresenter;
import javasign.com.dompetsehat.ui.activities.institusi.base.BaseInstitusi;
import javasign.com.dompetsehat.ui.activities.landing.LandingPages;
import javasign.com.dompetsehat.ui.event.FormEditTextFieldEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.Validate;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 9/1/16.
 */
public class FormEditFieldActivity extends BaseInstitusi {

  @Bind(R.id.et_general_field) MaterialMultiAutoCompleteTextView et_general_field;
  @Bind(R.id.et_password_new) MaterialEditText et_password_new;
  @Bind(R.id.et_password_old) MaterialEditText et_password_old;
  @Bind(R.id.fl_content_password) View fl_content_password;
  @Bind(R.id.tv_note) TextView tv_note;
  @Bind(R.id.btn_done) Button btn_done;
  @Bind(R.id.ccp) CountryCodePicker ccp;
  @Bind(R.id.show_pass_new) ImageView show_pass_new;
  @Bind(R.id.tv_brand) TextView tv_brand;
  @Bind(R.id.ic_back) IconicsTextView ic_back;
  @Bind(R.id.tv_title) TextView tv_title;

  private boolean isPasswordNewShown = false;
  private boolean isPasswordOldShown = false;
  private boolean showPasswordField = false;
  private int viewId;
  @Inject RegisterMamiPresenter presenter;
  @Inject ProfilePresenter presenterProfile;
  ProgressDialog dialog;
  private String from;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_form_edit_field);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    presenterProfile.attachView(this);
    AccountKit.initialize(this);
    setTitle("");
    init();
    dialog = new ProgressDialog(this);
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {

    Bundle b = getIntent().getExtras();
    from = b.getString("from", "profile");
    String title = b.getString("title", "");
    setTitle(title);

    viewId = b.getInt("field", -1);
    et_general_field.setEnabled(true);
    if (viewId == R.id.tv_phone) {
      goestoPhoneValidate();
      et_general_field.setEnabled(false);
    } else if (viewId == R.id.tv_email) {
      goestoEmailValidate();
      et_general_field.setEnabled(false);
    }

    if (viewId == R.id.tv_referal) {
      presenter.populateAutotext(et_general_field);
    }

    if (getIntent().hasExtra("type")) {
      //if(getIntent().getExtras().getInt("type") == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS){
      //  et_general_field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
      //}else {
        et_general_field.setInputType(getIntent().getExtras().getInt("type"));
      //}
    }

    if (!getIntent().hasExtra("form-mami")) {
      tv_brand.setVisibility(View.GONE);
      tv_title.setVisibility(View.VISIBLE);
      tv_brand.setTextColor(Color.BLACK);
      ic_back.setTextColor(Color.BLACK);
      tv_title.setTextColor(Color.BLACK);

      et_general_field.setBaseColor(Helper.GREEN_DOMPET_COLOR);
      et_general_field.setPrimaryColor(Helper.GREEN_DOMPET_COLOR);
      et_general_field.setTextColor(Color.BLACK);

      et_password_new.setBaseColor(Helper.GREEN_DOMPET_COLOR);
      et_password_new.setPrimaryColor(Helper.GREEN_DOMPET_COLOR);
      et_password_new.setTextColor(Color.BLACK);

      et_password_old.setBaseColor(Helper.GREEN_DOMPET_COLOR);
      et_password_old.setPrimaryColor(Helper.GREEN_DOMPET_COLOR);
      et_password_old.setTextColor(Color.BLACK);

      if(getIntent().hasExtra("button-label")){
        btn_done.setText(getIntent().getStringExtra("button-label"));
      }

      Drawable drawable =
          ContextCompat.getDrawable(this, R.drawable.button_form_edit);
      ColorStateList colorStateList = null;
      Resources resources = getResources();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        colorStateList =
            resources.getColorStateList(R.color.text_state_color_white, this.getTheme());
      } else {
        colorStateList = resources.getColorStateList(R.color.text_state_color_white);
      }
      btn_done.setBackground(drawable);
      btn_done.setTextColor(colorStateList);
    }

    if (viewId == R.id.tv_pendapatan) {
      new RupiahCurrencyFormat().formatEditText(et_general_field);
    } else if (viewId == R.id.tv_anak) {
      et_general_field.setMaxCharacters(2);
    }

    String countryCode = b.getString("code", "ID");
    ccp.setCountryForNameCode(countryCode);
    showPasswordField = viewId == R.id.tv_password;
    if (showPasswordField) {
      showOnlyPasswordField(b);
    } else {
      hidePasswordField(b);
    }
  }

  public void goestoEmailValidate() {
    final Intent intent = new Intent(this, AccountKitActivity.class);
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
        new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.EMAIL,
            AccountKitActivity.ResponseType.TOKEN);
    configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
    intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        configurationBuilder.build());
    startActivityForResult(intent, LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL);
  }

  public void goestoPhoneValidate() {
    final Intent intent = new Intent(this, AccountKitActivity.class);
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
        new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
            AccountKitActivity.ResponseType.TOKEN);
    configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
    intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        configurationBuilder.build());
    startActivityForResult(intent, LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_PHONE);
  }

  @OnClick(R.id.show_pass_new) void showNew(ImageView v) {
    ImageView eye = (ImageView) v;

    if (!isPasswordNewShown) {
      et_password_new.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      eye.setColorFilter(getResources().getColor(R.color.green_dompet_ori));
      isPasswordNewShown = true;
    } else {
      et_password_new.setInputType(129);
      isPasswordNewShown = false;
      eye.setColorFilter(getResources().getColor(R.color.grey_600));
    }

    int pos = et_password_new.getText().length();
    et_password_new.setSelection(pos);
  }

  @OnClick(R.id.show_pass_old) void showOld(ImageView v) {
    ImageView eye = (ImageView) v;

    if (!isPasswordOldShown) {
      et_password_old.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      eye.setColorFilter(getResources().getColor(R.color.green_dompet_ori));
      isPasswordOldShown = true;
    } else {
      et_password_old.setInputType(129);
      isPasswordOldShown = false;
      eye.setColorFilter(getResources().getColor(R.color.grey_600));
    }

    int pos = et_password_old.getText().length();
    et_password_old.setSelection(pos);
  }

  private void hidePasswordField(Bundle b) {
    fl_content_password.setVisibility(View.GONE);

    Words.setButtonToListen(btn_done, et_general_field);

    String hint = b.getString("hint", "");
    String text = b.getString("text", "");
    String note = b.getString("note", "");
    if (viewId == R.id.tv_ktp) {
      et_general_field.setInputType(InputType.TYPE_CLASS_NUMBER);
    } else if (viewId == R.id.tv_phone) {
      ccp.setVisibility(View.VISIBLE);
      et_general_field.setInputType(InputType.TYPE_CLASS_PHONE);
    }

    et_general_field.setFloatingLabelText(hint);
    et_general_field.setText(text);
    tv_note.setText(note);

    int pos = et_general_field.getText().length();
    et_general_field.setSelection(pos);
  }

  private void showOnlyPasswordField(Bundle b) {

    Words.setButtonToListen(btn_done, et_password_new);

    String text = b.getString("text", "");
    String note = b.getString("note", "");

    et_general_field.setVisibility(View.GONE);
    fl_content_password.setVisibility(View.VISIBLE);
    et_password_new.setText(text);
    tv_note.setText(note);

    if (b.getString("from", "profile").equals("register")) {
      et_password_old.setFloatingLabelText("Password");
      et_password_new.setVisibility(View.GONE);
      show_pass_new.setVisibility(View.GONE);
      Words.setButtonToListen(btn_done, et_password_old);
    }

    int pos = et_password_new.getText().length();
    et_password_new.setSelection(pos);
  }

  @OnClick(R.id.btn_done) void onDone() {
    System.out.println("FormEditFieldActivity.onDone ccp " + ccp.getSelectedCountryNameCode());
    String email = "";
    String ktp = "";
    String phone = "";
    String username = "";
    dialog.setMessage(getString(R.string.on_checking));
    dialog.show();
    if (from.equalsIgnoreCase("register")) {
      switch (viewId) {
        case R.id.tv_ktp:
          ktp = et_general_field.getText().toString();
          presenter.validateData(email, ktp, phone);
          break;
        case R.id.tv_email:
          email = et_general_field.getText().toString();
          presenter.validateData(email, ktp, phone);
          break;
        case R.id.tv_phone:
          phone = et_general_field.getText().toString();
          presenter.validateData(email, ktp, phone);
          break;
        case R.id.tv_password:
          presenter.passwordChecker(et_password_old.getText().toString());
          break;
        case R.id.tv_referal:
          String refferal_code = et_general_field.getText().toString();
          presenter.validateDataRefferal(refferal_code);
          break;
      }
    } else if (from.equalsIgnoreCase("profile")) {
      if (viewId == R.id.tv_password) {
        presenterProfile.changePassword(et_password_new.getText().toString(),
            et_password_old.getText().toString());
      }else {
        if(et_general_field.getInputType()==InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS){
          if(!Validate.isValidEmail(et_general_field.getText().toString())){
            dialog.dismiss();
            et_general_field.setError("Email tidak valid");
            return;
          }
        }
        finishValidate();
      }
    }
  }

  @Override public void finishValidate() {
    dialog.dismiss();
    if (!showPasswordField) {
      String text = et_general_field.getText().toString();
      MyCustomApplication.getRxBus()
          .send(new FormEditTextFieldEvent(text, viewId, ccp.getSelectedCountryNameCode()));
    } else {
      if (getIntent().getExtras().getString("from").equals("register")) {
        MyCustomApplication.getRxBus()
            .send(new FormEditTextFieldEvent(et_password_old.getText().toString(), viewId,
                ccp.getSelectedCountryNameCode()));
      } else {
        MyCustomApplication.getRxBus()
            .send(new FormEditTextFieldEvent(et_password_new.getText().toString(), viewId,
                ccp.getSelectedCountryNameCode()));
      }
    }
    finish();
  }

  @Override public void errorValidate() {
    dialog.dismiss();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL) {
        checkIfEmailKit(data);
      } else if (requestCode == LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_PHONE) {
        checkIfPhoneKit(data);
      }
    } else {

    }
  }

  private void checkIfPhoneKit(Intent data) {
    AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
    String toastMessage = "error";
    if (loginResult.getError() != null) {
      toastMessage = loginResult.getError().getErrorType().getMessage();
      showErrorActivity(loginResult.getError());
    } else if (loginResult.wasCancelled()) {
      toastMessage = "Cancelled";
    } else {
      if (loginResult.getAccessToken() != null) {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
          @Override public void onSuccess(Account account) {
            et_general_field.setText(account.getPhoneNumber().getPhoneNumber());
            //presenter.validateData("","",account.getPhoneNumber().getPhoneNumber());
          }

          @Override public void onError(AccountKitError accountKitError) {
            Timber.d("accountKitError" + accountKitError);
          }
        });
      } else {

      }
    }
  }

  private void checkIfEmailKit(Intent data) {
    AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
    Timber.e("Here email");
    String toastMessage = "error";
    if (loginResult.getError() != null) {
      toastMessage = loginResult.getError().getErrorType().getMessage();
      showErrorActivity(loginResult.getError());
    } else if (loginResult.wasCancelled()) {
      toastMessage = getString(R.string.cancel);
      Toast.makeText(FormEditFieldActivity.this, toastMessage, Toast.LENGTH_LONG).show();
    } else {
      if (loginResult.getAccessToken() != null) {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
          @Override public void onSuccess(Account account) {
            String email = account.getEmail();
            et_general_field.setText(email);
            //presenter.validateData(email,"","");
          }

          @Override public void onError(AccountKitError accountKitError) {
            Timber.d("accountKitError" + accountKitError);
          }
        });
      } else {

      }
    }
  }

  @Override public void errorTextEdit(String message) {
    super.errorTextEdit(message);
    runOnUiThread(() -> {
      if (et_general_field.getVisibility() == View.VISIBLE) {
        et_general_field.setError(message);
      } else if (et_password_old.getVisibility() == View.VISIBLE) {
        et_password_old.setError(message);
      } else if (et_password_new.getVisibility() == View.VISIBLE) {
        et_password_new.setError(message);
      }
    });
  }

  private void showErrorActivity(AccountKitError error) {
    Toast.makeText(this, error.getUserFacingMessage(), Toast.LENGTH_LONG).show();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
    presenterProfile.detachView();
  }
}
