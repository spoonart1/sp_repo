package javasign.com.dompetsehat.ui.activities.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.VeryFund.Http;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.json.facebook;
import javasign.com.dompetsehat.presenter.auth.AuthInterface;
import javasign.com.dompetsehat.presenter.auth.AuthPresenter;
import javasign.com.dompetsehat.ui.CommonInterface;
import javasign.com.dompetsehat.ui.activities.forgot.ForgotPasswordActivity;
import javasign.com.dompetsehat.ui.activities.home.HomeActivity;
import javasign.com.dompetsehat.ui.activities.landing.LandingPages;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyReferral;
import javasign.com.dompetsehat.ui.activities.onestep.OneStepCloserActivity;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Validate;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by Spoonart on 11/30/2015.
 */
public class SignInActivity extends BaseActivity implements CommonInterface, AuthInterface {

  @Bind(R.id.et_username) MaterialEditText et_username;
  @Bind(R.id.et_password) MaterialEditText et_password;
  @Bind(R.id.splash_view) View splash_view;
  @Bind(R.id.footer_menu) View footer_menu;
  @Bind(R.id.tv_forgot_password) TextView tv_forgot_password;
  @Bind(R.id.ll_signup) LinearLayout ll_signup;
  @Bind(R.id.ll_signup_button) LinearLayout ll_signup_button;
  @Bind(R.id.btn_signIn) Button btn_signIn;
  @Bind(R.id.login_button_fb) LoginButton login_button_fb;

  private Animation enterFromBottom;
  private Animation exitToBottom;
  private boolean menuIsOpen = false;
  private boolean isPasswordShown = false;

  public static final int APP_REQUEST_CODE_ACCOUNT_KIT_PHONE = 92;
  public static final int APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL = 93;

  public static final String MODE_SIGNUP = "signup";
  public static final String MODE_SIGNIN = "signin";
  private String mode = MODE_SIGNIN;

  DbHelper db;
  LoadAndSaveImage loadAndSaveImage;
  GeneralHelper helper = GeneralHelper.getInstance();
  private Activity context;
  CallbackManager callbackManager;
  Gson gson = new Gson();

  // avesina
  public boolean FLAG_STATUS = false;
  private RxBus rxBus;
  @Inject AuthPresenter presenter;
  @Inject SessionManager sessionManager;
  private ProgressDialog pDialog;

  final int REQUEST_LOGIN = 1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.context = this;
    setContentView(R.layout.activity_signin);
    ButterKnife.bind(this);

    setTitle(getString(R.string.greeting));

    db = DbHelper.getInstance(this);
    loadAndSaveImage = new LoadAndSaveImage(this);

    // avesina
    getActivityComponent().inject(this);
    presenter.attachView(this);
    rxBus = MyCustomApplication.getRxBus();
    pDialog = new ProgressDialog(context);
    pDialog.setMessage(getString(R.string.login_loading));
    pDialog.setCancelable(false);
    pDialog.setCanceledOnTouchOutside(false);

    //onBoardingMessage();
    enterFromBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);
    exitToBottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom);
    hideMenu(true);
    Helper.trackThis(this, "User di halaman login");
    Intent intent = getIntent();
    showView();
    if (intent.getExtras() != null) {
      if (intent.hasExtra("mode")) {
        Timber.e("avesina " + intent.getExtras().getString("mode"));
        if (intent.getExtras().getString("mode").equalsIgnoreCase(MODE_SIGNUP)) {
          mode = MODE_SIGNUP;
          setTitle(getString(R.string.gretting_signup));
        }
      }
    }
    init();
  }

  private void init() {
    switch (mode) {
      case MODE_SIGNIN:
        et_username.setFloatingLabelText(getResources().getString(R.string.email_or_username));
        btn_signIn.setText(getResources().getString(R.string.login));
        ll_signup.setVisibility(View.VISIBLE);
        ll_signup_button.setVisibility(View.GONE);
        tv_forgot_password.setVisibility(View.VISIBLE);
        break;
      case MODE_SIGNUP:
        et_username.setFloatingLabelText(getResources().getString(R.string.email_only));
        btn_signIn.setText(getResources().getString(R.string.sign_up));
        ll_signup.setVisibility(View.GONE);
        tv_forgot_password.setVisibility(View.GONE);
        ll_signup_button.setVisibility(View.VISIBLE);
        login_button_fb.setReadPermissions(
            Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
        callbackManager = CallbackManager.Factory.create();
        login_button_fb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override public void onSuccess(LoginResult loginResult) {
            // App code
            GraphRequest request =
                GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
                  Timber.d("LoginActivity" + response.getJSONObject());
                  facebook fb = gson.fromJson(response.getJSONObject().toString(), facebook.class);
                  presenter.loginViaFacebook(LandingPages.APP_REQUEST_CODE_FB,
                      loginResult.getAccessToken().getToken(), fb);
                });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
          }

          @Override public void onCancel() {
            // App code

            Toast.makeText(SignInActivity.this, getString(R.string.cancel), Toast.LENGTH_LONG)
                .show();
          }

          @Override public void onError(FacebookException exception) {
            // App code
            Toast.makeText(SignInActivity.this, getString(R.string.error_source_unknown),
                Toast.LENGTH_LONG).show();
          }
        });
        break;
    }
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    finish();
  }

  //@Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
  //  if (keyCode == KeyEvent.KEYCODE_BACK) {
  //    SessionManager.clearTourPreff(this);
  //    Toast.makeText(this, "cleared", Toast.LENGTH_SHORT).show();
  //    return BuildConfig.DEBUG || BuildConfig.DEBUG;
  //  }
  //  return super.onKeyLongPress(keyCode, event);
  //}

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    View tab_container = ButterKnife.findById(this, R.id.tab_container);
    tv_title.setText(title);
    tab_container.setBackgroundColor(Color.WHITE);
  }

  private void hideMenu(boolean hide) {
    int visibility = hide ? View.GONE : View.VISIBLE;
    footer_menu.setVisibility(visibility);
  }

  @OnClick(R.id.show_pass) void hawkEye(View v) {
    ImageView eye = (ImageView) v;
    if (!isPasswordShown) {
      et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      eye.setColorFilter(getResources().getColor(R.color.green_dompet_ori));
      isPasswordShown = true;
    } else {
      et_password.setInputType(129);
      isPasswordShown = false;
      eye.setColorFilter(getResources().getColor(R.color.grey_600));
    }

    int pos = et_password.getText().length();
    et_password.setSelection(pos);
  }

  @OnClick(R.id.splash_view) void disable() {
    closeFooterMenu();
    return;
  }

  @OnClick(R.id.btn_facebook) void signupFacebook() {
    login_button_fb.performClick();
  }

  @OnClick(R.id.btn_email) void signupAccountEmail() {
    doRegisterWithEmail();
  }

  @OnClick(R.id.btn_hp) void signupAccountPhone() {
    doLoginAccountKit();
  }

  @OnClick(R.id.btn_daftar) void openMenu() {
    openFooterMenu();
    menuIsOpen = true;
  }

  private void openFooterMenu() {
    if (menuIsOpen) return;

    splash_view.setVisibility(View.VISIBLE);
    ViewCompat.animate(splash_view).alpha(0.5f).withEndAction(new Runnable() {
      @Override public void run() {
        splash_view.setAlpha(0.5f);
      }
    });

    enterFromBottom.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        hideMenu(false);
      }

      @Override public void onAnimationEnd(Animation animation) {
        hideMenu(false);
        menuIsOpen = true;
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    footer_menu.startAnimation(enterFromBottom);
  }

  private void closeFooterMenu() {
    if (!menuIsOpen) return;

    ViewCompat.animate(splash_view).alpha(0.0f).withEndAction(new Runnable() {
      @Override public void run() {
        splash_view.setAlpha(0.0f);
        splash_view.setVisibility(View.GONE);
      }
    });

    exitToBottom.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        hideMenu(false);
      }

      @Override public void onAnimationEnd(Animation animation) {
        hideMenu(true);
        menuIsOpen = false;
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    footer_menu.startAnimation(exitToBottom);
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.attachView(this);
  }

  @OnClick(R.id.btn_signIn) void doLogin(View v) {
    //if(BuildConfig.DEBUG){
    //  sessionManager.createLoginSession("asnamsdhuwqdhaiouyqwyesjd", "22", "fake");
    //  Helper.goTo(this, HomeActivity.class);
    //  return;
    //}

    boolean isValid = true;
    if (TextUtils.isEmpty(et_username.getText())) {
      et_username.setError(getString(R.string.error_username_if_blank));
      isValid = false;
    } else {
      if (mode.equalsIgnoreCase(MODE_SIGNUP)) {
        if (!et_username.getText().toString().contains("@")) {
          et_username.setError("Email tidak valid");
          return;
        }
      }
      if (et_username.getText().toString().contains("@")) {
        if (!Validate.isValidEmail(et_username.getText().toString())) {
          et_username.setError("Email tidak valid");
          isValid = false;
        }
      }
    }

    if (TextUtils.isEmpty(et_password.getText())) {
      et_password.setError(getString(R.string.error_password_if_blank));
      isValid = false;
    }

    if (!isValid) return;

    presenter.loginViaApp(REQUEST_LOGIN, et_username.getText().toString(),
        et_password.getText().toString());

    Helper.trackThis(this, "user berhasil login via email/username");
  }

  @OnClick(R.id.tv_forgot_password) void actionForgotPassword(View v) {
    startActivity(new Intent(this, ForgotPasswordActivity.class));
  }

  @OnClick(R.id.ll_top) void doLoginAccountKit() {
    Helper.hideKeyboard(this);
    final Intent intent = new Intent(this, AccountKitActivity.class);
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
        new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
            AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
    // ... perform additional configuration ...
    configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
    configurationBuilder.setDefaultCountryCode("ID");
    configurationBuilder.setReceiveSMS(true);
    intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        configurationBuilder.build());
    startActivityForResult(intent, APP_REQUEST_CODE_ACCOUNT_KIT_PHONE);
  }

  private void showErrorActivity(AccountKitError error) {
    Toast.makeText(this, error.getUserFacingMessage(), Toast.LENGTH_LONG).show();
  }

  @OnClick(R.id.ll_bot) void doRegisterWithEmail() {
    Helper.hideKeyboard(this);
    Helper.trackThis(SignInActivity.this, "user daftar via account kit email");
    final Intent intent = new Intent(this, AccountKitActivity.class);
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
        new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.EMAIL,
            AccountKitActivity.ResponseType.TOKEN);
    configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
    intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        configurationBuilder.build());
    startActivityForResult(intent, LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL);
  }

  @Override protected void onStop() {
    super.onStop();
  }

  @Override protected void onDestroy() {
    helper.dismissProgressDialog(pDialog);
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void onLoad(int requestid) {
    pDialog.show();
  }

  @Override public void onComplete(int requestid) {
    if (FLAG_STATUS) {

      if (sessionManager.getUserPickAppMode() == SessionManager.USER_PICK_MY_FIN) {
        Helper.finishAllPreviousActivityWithNextTarget(this, NewMainActivityMyFin.class);
      } else if (sessionManager.getUserPickAppMode() == SessionManager.USER_PICK_MY_REFERRAL) {
        Helper.finishAllPreviousActivityWithNextTarget(this, NewMainActivityMyReferral.class);
      } else {
        Helper.finishAllPreviousActivityWithNextTarget(this, HomeActivity.class);
      }
    }
  }

  @Override public void onError(int requestid) {
    FLAG_STATUS = false;
    pDialog.dismiss();
    switch (requestid) {
      case 11:
        if (mode.equalsIgnoreCase(MODE_SIGNUP)) {
          startActivity(new Intent(this, OneStepCloserActivity.class).putExtra("auth_with", "app")
              .putExtra("email", et_username.getText().toString())
              .putExtra("password", et_password.getText().toString()));
          finish();
          return;
        }
        et_username.setError(getString(R.string.error_username_email_not_found));
        break;
      case 12:
        et_password.setError(getString(R.string.error_password_invalid));
        break;
    }
  }

  @Override public void onNext(int requestid) {
    if (mode.equalsIgnoreCase(MODE_SIGNIN)
        || requestid == LandingPages.APP_REQUEST_CODE_FB
        || requestid == APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL
        || requestid == APP_REQUEST_CODE_ACCOUNT_KIT_PHONE) {
      FLAG_STATUS = true;
    }
    pDialog.dismiss();
  }

  @OnClick(R.id.btn_login) void doLogin() {
    AlertDialog alertDialog = new AlertDialog.Builder(this).setView(R.layout.crop_image_view)
        .setView(R.layout.dialog_information_account_kit)
        .setPositiveButton("Saya Mengerti, lanjutkan", (dialogInterface, i) -> {
          doRegisterWithEmail();
        })
        .create();
    alertDialog.show();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == APP_REQUEST_CODE_ACCOUNT_KIT_PHONE) {
      checkIfPhoneKit(data);
    } else if (requestCode == APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL) {
      checkIfEmailKit(data);
    } else {
      callbackManager.onActivityResult(requestCode, resultCode, data);
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
            presenter.loginViaAccountKit(APP_REQUEST_CODE_ACCOUNT_KIT_PHONE,
                loginResult.getAccessToken().getToken(), account.getPhoneNumber().getPhoneNumber(),
                account.getEmail());
            Helper.trackThis(SignInActivity.this, "user berhasil sign up no telepon");
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
      Toast.makeText(SignInActivity.this, toastMessage, Toast.LENGTH_LONG).show();
      showErrorActivity(loginResult.getError());
    } else if (loginResult.wasCancelled()) {
      toastMessage = "Cancelled";
      Toast.makeText(SignInActivity.this, toastMessage, Toast.LENGTH_LONG).show();
    } else {
      if (loginResult.getAccessToken() != null) {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
          @Override public void onSuccess(Account account) {
            Timber.e("login email");
            presenter.loginViaAccountKit(APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL,
                loginResult.getAccessToken().getToken(), null, account.getEmail());
            Helper.trackThis(SignInActivity.this, "user berhasil sign up via email");
          }

          @Override public void onError(AccountKitError accountKitError) {
            Timber.d("accountKitError" + accountKitError);
          }
        });
      } else {

      }
    }
  }

  @Override public void gotoNextActivity(String auth_with, String access_token, Object data) {
    if (auth_with.equalsIgnoreCase("facebook")) {
      facebook fb = (facebook) data;
      String birthday = "";
      try {
        birthday = (new SimpleDateFormat("yyyy-M-dd")).format(
            (new SimpleDateFormat("M/dd/yyyy")).parse(fb.birthday).getTime());
      } catch (Exception e) {

      }
      gotoOneStepActivity(auth_with, access_token, fb.id, fb.email, null,
          fb.name.toLowerCase().replace(" ", ""), birthday, fb.gender);
    } else if (auth_with.equalsIgnoreCase("account_kit")) {
      Timber.e("access_token " + access_token);
      HashMap<String, String> map = (HashMap<String, String>) data;
      String email = (map.containsKey("email") ? map.get("email").toString() : "");
      String phone = (map.containsKey("phone") ? map.get("phone").toString() : "");
      gotoOneStepActivity(auth_with, access_token, "", email, phone, "", "", "");
    }
  }

  private void gotoOneStepActivity(String auth_with, String access_token, String auth_code,
      String email, String phone, String username, String birthday, String gender) {
    Intent i = new Intent(SignInActivity.this, OneStepCloserActivity.class);
    i.putExtra("auth_with", auth_with);
    i.putExtra("access_token", access_token);
    i.putExtra("auth_code", auth_code);
    i.putExtra("email", email);
    i.putExtra("phone", phone);
    i.putExtra("username", username);
    i.putExtra("birthday", birthday);
    i.putExtra("gender", gender);
    startActivity(i);
  }

  @Override public void showDialogConfirm(boolean is_password_valid,
      AuthPresenter.onClickDialog clickDialog) {
    runOnUiThread(() -> {
      if (mode.equalsIgnoreCase(MODE_SIGNUP)) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(
            "email anda sudah terdaftar, silahkan pilih opsi dibawah ini :");
        builder.setNeutralButton("Ganti Email", (dialogInterface, i) -> {
          dialogInterface.dismiss();
        });
        if (is_password_valid) {
          builder.setPositiveButton("Login", (dialogInterface, i) -> {
            clickDialog.onClick(1);
            FLAG_STATUS = true;
            onComplete(1);
          });
        }
        if (!is_password_valid) {
          builder.setNegativeButton("Forgot Password", (dialogInterface, i) -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
          });
        }
        builder.create().show();
      } else {
        clickDialog.onClick(1);
      }
    });
  }

  private void hiddenView() {
    tv_forgot_password.setVisibility(View.GONE);
    ll_signup.setVisibility(View.GONE);
  }

  private void showView() {
    tv_forgot_password.setVisibility(View.VISIBLE);
    ll_signup.setVisibility(View.VISIBLE);
  }
}