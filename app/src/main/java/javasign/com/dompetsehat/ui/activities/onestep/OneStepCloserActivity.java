package javasign.com.dompetsehat.ui.activities.onestep;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.CallbackManager;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.gson.Gson;
import com.itextpdf.tool.xml.svg.graphic.Text;
import com.mikepenz.iconics.view.IconicsTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.auth.AuthInterface;
import javasign.com.dompetsehat.presenter.auth.AuthPresenter;
import javasign.com.dompetsehat.ui.activities.home.HomeActivity;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.ui.activities.landing.LandingPages;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Validate;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/4/16.
 */
public class OneStepCloserActivity extends BaseActivity implements AuthInterface {

  @Bind(R.id.et_username) MaterialEditText et_username;
  @Bind(R.id.et_password) MaterialEditText et_password;
  @Bind(R.id.btn_submit) Button btn_selesai;
  @Bind(R.id.fl_content_password1) View content_password;
  @Bind(R.id.et_email) MaterialEditText et_email;
  @Bind(R.id.sp_gender) Spinner sp_gender;
  @Bind(R.id.floating_date) TextView floating_date;
  @Bind(R.id.tv_date) TextView tv_date;
  @Bind(R.id.tv_date_alert) TextView tv_date_alert;
  @Bind(R.id.tv_word) TextView tv_word;
  @Bind(R.id.tv_disclaimer) TextView tv_disclaimer;
  @Bind(R.id.checkbox) AppCompatCheckBox checkbox;
  @Bind(R.id.show_pass) ImageView show_pass;

  private SimpleDateFormat formatStandart = new SimpleDateFormat("yyyy-MM-dd");
  private SimpleDateFormat formatBirthday = new SimpleDateFormat("dd MMMM yyyy");

  public boolean FLAG_STATUS = false;

  private boolean showPassword = false;
  private boolean mExtensionMode = false;
  private Date initialDate = null;
  private GeneralHelper helper = GeneralHelper.getInstance();
  private SessionManager sessionManager;

  // avesina
  CallbackManager callbackManager;
  Gson gson = new Gson();
  @Inject AuthPresenter presenter;
  private RxBus rxBus;
  String auth_with;
  String access_token;
  String auth_code;
  String username;
  String email;
  String phone;
  String birthday;
  String gender;
  String password;

  final int REQUEST_REG_FB = 1;
  final int REQUEST_REG_APP = 3;
  final int REQUEST_REQ_ACCOUNT_KIT = 2;

  String[] genders = { "male", "female" };

  private ProgressDialog pDialog;
  private Calendar calendar = Calendar.getInstance();
  private Date maxDate;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_onestepcloser);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);
    rxBus = MyCustomApplication.getRxBus();

    sessionManager = new SessionManager(getActivityComponent().context());

    setTitle(getString(R.string.greeting_one_step));

    Bundle b = getIntent().getExtras();

    if (b != null) {
      this.auth_with = b.getString("auth_with", "");
      this.access_token = b.getString("access_token", "");
      this.auth_code = b.getString("auth_code", "");
      this.username = b.getString("username", "");
      this.email = b.getString("email", "");
      this.phone = b.getString("phone", "");
      this.birthday = b.getString("birthday");
      this.password = b.getString("password", "");
      this.gender = b.getString("gender", "");
      et_username.setText(this.username);
      if (!TextUtils.isEmpty(this.birthday)) {
        try {
          setInitialDate(formatStandart.parse(this.birthday));
          tv_date.setText(formatBirthday.format(formatStandart.parse(this.birthday).getTime()));
        } catch (Exception e) {

        }
      } else {
        calendar.set(1980, 1, 1);
        setInitialDate(calendar.getTime());
      }

      setMaxDate(2000, 12, 31);

      et_email.setText(this.email);
      if (!TextUtils.isEmpty(this.email)){
        setTitle(this.email);
        et_email.setVisibility(View.GONE);
        tv_word.setText(R.string.please_fill_username_and_password);
      }

      if(!TextUtils.isEmpty(this.phone)){
        setTitle("0"+this.phone);
      }

      et_password.setText(this.password);
      if (!TextUtils.isEmpty(this.password)) {
        et_password.setVisibility(View.GONE);
        show_pass.setVisibility(View.GONE);
      }

      sp_gender.setSelection(Arrays.asList(genders).indexOf(gender));
      pDialog = new ProgressDialog(getActivityComponent().context());
      pDialog.setMessage(getString(R.string.memuat_data));
      pDialog.setCancelable(false);
      pDialog.setCanceledOnTouchOutside(false);
    }

    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    IconicsTextView ic_menu = ButterKnife.findById(this, R.id.ic_menu);
    ic_menu.setText(DSFont.Icon.dsf_information_outline.getFormattedName());
    ic_menu.setTextColor(Color.BLACK);
    ic_menu.setEnabled(true);
  }

  public void setSpan() {
    SpannableStringBuilder spanTxt = new SpannableStringBuilder(getString(R.string.dengan_ini));
    spanTxt.append(" ");
    spanTxt.append(getString(R.string.terms));
    spanTxt.append(" ");
    spanTxt.setSpan(new ClickableSpan() {
      @Override public void onClick(View widget) {
        openRulesDompetSehat();
      }
    }, spanTxt.length() - getString(R.string.terms).length() - 1, spanTxt.length() - 1, 0);
    spanTxt.append(" ");
    spanTxt.append(getString(R.string.and));
    spanTxt.append(" ");
    spanTxt.append(getString(R.string.privacy));
    spanTxt.append(" ");
    spanTxt.setSpan(new ClickableSpan() {
      @Override public void onClick(View widget) {
        openPrivacyDompetSehat();
      }
    }, spanTxt.length() - getString(R.string.privacy).length() - 1, spanTxt.length() - 1, 0);
    tv_disclaimer.setMovementMethod(LinkMovementMethod.getInstance());
    tv_disclaimer.setText(spanTxt, TextView.BufferType.SPANNABLE);
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    finish();
  }

  private void openRulesDompetSehat() {
    startActivity(
        new Intent(this, WebviewInstutisiActivity.class).putExtra(WebviewInstutisiActivity.URL_KEY,
            "http://dompetsehat.com/terms")
            .putExtra("from", WebviewInstutisiActivity.FLAG_TO_HIDE)
            .putExtra("accent-color", Helper.GREEN_DOMPET_COLOR));

    Helper.trackThis(this, "User melihat syarat dan ketentuan dompetsehat");
  }

  private void openPrivacyDompetSehat() {
    startActivity(
        new Intent(this, WebviewInstutisiActivity.class).putExtra(WebviewInstutisiActivity.URL_KEY,
            "http://dompetsehat.com/privacy")
            .putExtra("from", WebviewInstutisiActivity.FLAG_TO_HIDE)
            .putExtra("accent-color", Helper.GREEN_DOMPET_COLOR));

    Helper.trackThis(this, "User melihat syarat dan ketentuan dompetsehat");
  }

  @OnClick(R.id.ic_menu) void showInfo(View v) {
    AdvancedDialog.ABuilder builder =
        new AdvancedDialog.ABuilder(this, getString(R.string.explanation),
            Color.BLACK).withCloseButton(true).withFooterButton(false);

    int gravity = Gravity.LEFT;
    builder.addText(gravity, getString(R.string.explanation_username));
    builder.addSpace(ViewGroup.LayoutParams.MATCH_PARENT,
        getResources().getDimensionPixelSize(R.dimen.line_divider_size));
    builder.addText(gravity, getString(R.string.explanation_email));
    builder.addSpace(ViewGroup.LayoutParams.MATCH_PARENT,
        getResources().getDimensionPixelSize(R.dimen.line_divider_size));
    builder.addText(gravity, getString(R.string.explanation_password));
    builder.addSpace(ViewGroup.LayoutParams.MATCH_PARENT,
        getResources().getDimensionPixelSize(R.dimen.line_divider_size));
    //builder.addText(gravity, "<b>Tanggal lahir</b> <br>"+getString(R.string.lorem_ipsum));
    //builder.addSpace(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.line_divider_size));
    //builder.addText(gravity, "<b>Gender</b> <br>"+getString(R.string.lorem_ipsum));

    Helper.showAdvancedDialog(getSupportFragmentManager(), builder);
  }

  @OnClick(R.id.btn_submit) void doSubmit(View v) {
    submit(v);
  }

  private void setMaxDate(int year, int month, int day) {
    calendar.set(year, month, day);
    this.maxDate = calendar.getTime();
  }

  private void setInitialDate(Date date) {
    this.initialDate = date;
  }

  private SlideDateTimeListener listener = new SlideDateTimeListener() {
    @Override public void onDateTimeSet(Date date) {
      tv_date.setText(Helper.setSimpleDateFormat(date, helper.FORMAT_BIRTHDAY));
      setInitialDate(date);
    }
  };

  @OnClick(R.id.tv_date) void setDate(TextView t) {
    new SlideDateTimePicker.Builder(getSupportFragmentManager()).setInitialDate(initialDate)
        .setMaxDate(maxDate)
        .setListener(listener)
        .setIsDateOnly(true)
        .setIs24HourTime(true)
        .setIndicatorColor(Color.WHITE)
        .build()
        .show();
  }

  private void submit(View v) {
    boolean isValid = true;
    if (TextUtils.isEmpty(et_username.getText())) {
      et_username.setError(getString(R.string.error_username_if_blank));
      isValid = false;
    }
    if (!et_username.isCharactersCountValid()) {
      et_username.setError(getString(R.string.error_username_if_less_six));
      isValid = false;
    }
    if (TextUtils.isEmpty(et_password.getText())) {
      et_password.setError(getString(R.string.error_password_if_blank));
      isValid = false;
    }

    if (TextUtils.isEmpty(et_email.getText())) {
      et_email.setError(getString(R.string.error_email_if_blank));
      isValid = false;
    }

    if (!Validate.isValidEmail(et_email.getText().toString())) {
      et_email.setError(getString(R.string.error_email_invalid));
      isValid = false;
    }

    if (TextUtils.isEmpty(tv_date.getText())) {
      tv_date_alert.setVisibility(View.VISIBLE);
      tv_date.addTextChangedListener(new Words.SimpleTextWatcer() {
        @Override public void afterTextChanged(Editable s) {
          if (!TextUtils.isEmpty(s)) {
            tv_date_alert.setVisibility(View.GONE);
            tv_date.removeTextChangedListener(this);
          }
        }
      });

      isValid = false;
    }

    if (!isValid) return;

    if (!checkbox.isChecked()) {
      Helper.showCustomSnackBar(v, getLayoutInflater(), getString(R.string.error_ds_agreement));
      return;
    }

    this.gender = genders[sp_gender.getSelectedItemPosition()];
    this.username = et_username.getText().toString();
    this.birthday = formatStandart.format(initialDate.getTime());
    this.password = et_password.getText().toString();
    this.email = et_email.getText().toString();
    switch (auth_with) {
      case "account_kit":
        Timber.e("via account kit");
        presenter.registerViaAccountKit(REQUEST_REQ_ACCOUNT_KIT, this.access_token, this.phone,
            this.email, this.username, this.password, this.birthday, this.gender);
        break;
      case "facebook":
        Timber.e("via facebook");
        presenter.registerViaFacebook(REQUEST_REG_FB, this.access_token, this.email, this.username,
            this.password, this.birthday, this.gender);
        break;
      case "app":
        Timber.e("via app");
        presenter.registerViaApp(REQUEST_REG_APP, this.email, this.username, this.password,
            this.birthday, this.gender);
        break;
    }
  }

  private void init() {
    setSpan();
    int type = getIntent().getIntExtra(Words.TYPE, LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL);
    mExtensionMode = type == LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_PHONE
        || type == LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL;
    if (type == LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_PHONE) {
      tv_word.setText(getString(R.string.command_one_step_by_phone));
      content_password.setVisibility(View.GONE);
    }

    Words.setButtonToListen(floating_date, tv_date);
  }

  @OnClick(R.id.show_pass) void hawkEye(View v) {
    ImageView eye = (ImageView) v;
    if (showPassword) {
      et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      eye.setColorFilter(getResources().getColor(R.color.green_dompet_ori));
      showPassword = false;
    } else {
      et_password.setInputType(129);
      showPassword = true;
      eye.setColorFilter(getResources().getColor(R.color.grey_600));
    }

    int pos = et_password.getText().length();
    et_password.setSelection(pos);
  }

  @Override public void onLoad(int requestid) {
    FLAG_STATUS = false;
    pDialog.show();
  }

  @Override public void onComplete(int requestid) {
    pDialog.dismiss();
    if (FLAG_STATUS) {
      Helper.trackThis(this, "User telah berhasil melakukan registrasi dan login");
      sessionManager.deleteUserHasLaunchedAppData();
      if (MyCustomApplication.showInvestasi()) {
        Helper.finishAllActivityWithDelay(this, HomeActivity.class);
      } else {
        Helper.finishAllActivityWithDelay(this, NewMainActivityMyFin.class);
      }
    }
  }

  @Override public void onError(int requestid) {
    FLAG_STATUS = false;
    pDialog.dismiss();

    switch (requestid) {
      case 10://username exist
        et_username.setError(getString(R.string.error_username_exist));
        break;
      case 11: //email exist
        et_email.setError(getString(R.string.error_email_exist));
        break;
      case 12: //username && email exist
        et_username.setError(getString(R.string.error_username_exist));
        et_email.setError(getString(R.string.error_email_exist));
        break;
    }
  }

  @Override public void onNext(int requestid) {
    FLAG_STATUS = true;
  }

  @Override public void gotoNextActivity(String auth_with, String access_token, Object data) {

  }

  @Override
  public void showDialogConfirm(boolean is_valid, AuthPresenter.onClickDialog clickDialog) {

  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
