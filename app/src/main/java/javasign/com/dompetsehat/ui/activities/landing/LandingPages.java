package javasign.com.dompetsehat.ui.activities.landing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.json.facebook;
import javasign.com.dompetsehat.presenter.auth.AuthInterface;
import javasign.com.dompetsehat.presenter.auth.AuthPresenter;
import javasign.com.dompetsehat.ui.activities.ask.AskActivity;
import javasign.com.dompetsehat.ui.activities.institusi.FormEditFieldActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.RegisterMamiActivity;
import javasign.com.dompetsehat.ui.activities.login.SignInActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.onestep.OneStepCloserActivity;
import javasign.com.dompetsehat.ui.event.FormEditTextFieldEvent;
import javasign.com.dompetsehat.ui.fragments.ImageFragment;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.TourHelper;
import javasign.com.dompetsehat.view.CirclePageIndicator;
import javasign.com.dompetsehat.view.CustomViewPagerAdapter;
import javax.inject.Inject;
import timber.log.Timber;

public class LandingPages extends BaseActivity implements AuthInterface {

  @Bind(R.id.vp_content) ViewPager vp_content;
  @Bind(R.id.indicator) CirclePageIndicator indicator;
  @Bind(R.id.viewflipper) ViewFlipper viewFlipper;
  @Bind(R.id.login_button_fb) LoginButton loginButton;
  @Bind(R.id.splash_view) View splash_view;
  @Bind(R.id.footer_menu) View footer_menu;
  @Bind(R.id.rootview) View rootview;

  @Bind(R.id.label_bot) TextView label_bot;
  @Bind(R.id.ll_bot) View ll_bot;
  @Bind(R.id.ll_cen) View ll_cen;

  private String actionMenuOpen = "";
  private CustomViewPagerAdapter adapter;
  private String[] texts;

  private Animation enterFromBottom;
  private Animation exitToBottom;
  private boolean menuIsOpen = false;
  private int mCurrentPage = 0;

  public static final int APP_REQUEST_CODE_FB = 91;
  public static final int APP_REQUEST_CODE_ACCOUNT_KIT_PHONE = 92;
  public static final int APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL = 93;

  // avesina
  public boolean FLAG_STATUS = false;
  CallbackManager callbackManager;
  Gson gson = new Gson();
  @Inject AuthPresenter presenter;
  private RxBus rxBus;
  private ProgressDialog pDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    super.onCreate(savedInstanceState);
    FacebookSdk.sdkInitialize(getApplicationContext());
    setContentView(R.layout.activity_landing_pages);
    ButterKnife.bind(this);

    // avesina
    getActivityComponent().inject(this);
    presenter.attachView(this);
    rxBus = MyCustomApplication.getRxBus();
    pDialog = new ProgressDialog(getActivityComponent().context());
    pDialog.setMessage("Loading. . .");
    pDialog.setCancelable(false);
    pDialog.setCanceledOnTouchOutside(false);
    AccountKit.initialize(this);
    texts = new String[] {
        getString(R.string.manage_daily_financial_automatically),
        getString(R.string.plan_monitoring_reach_future), getString(R.string.persuade_your_friend)
    };

    int[] colors = new int[] {
        Color.parseColor("#9677d8"), Color.parseColor("#f08519"), Color.parseColor("#41a9cc")
    };
    TypedArray imgs = getResources().obtainTypedArray(R.array.landing_images);
    ImageFragment[] imageFragments = new ImageFragment[imgs.length()];
    for (int i = 0; i < imgs.length(); i++) {
      int resId = imgs.getResourceId(i, -1);
      imageFragments[i] = new ImageFragment().newInstance(i, resId, texts[i], colors[i]);
    }

    adapter = CustomViewPagerAdapter.newInstance(getSupportFragmentManager(), imageFragments);
    vp_content.setAdapter(adapter);
    indicator.setViewPager(vp_content);

    loginButton.setReadPermissions(
        Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
    callbackManager = CallbackManager.Factory.create();
    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        // App code
        GraphRequest request =
            GraphRequest.newMeRequest(loginResult.getAccessToken(), (object, response) -> {
              Timber.d("LoginActivity" + response.getJSONObject());
              facebook fb = gson.fromJson(response.getJSONObject().toString(), facebook.class);
              presenter.loginViaFacebook(APP_REQUEST_CODE_FB,
                  loginResult.getAccessToken().getToken(), fb);
            });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
      }

      @Override public void onCancel() {
        // App code

        Toast.makeText(LandingPages.this, getString(R.string.cancel), Toast.LENGTH_LONG).show();
      }

      @Override public void onError(FacebookException exception) {
        // App code
        Toast.makeText(LandingPages.this, getString(R.string.error_source_unknown),
            Toast.LENGTH_LONG).show();
      }
    });

    Animation fadeIn = new AlphaAnimation(0, 1);
    fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
    fadeIn.setDuration(1000);

    Animation fadeOut = new AlphaAnimation(1, 0);
    fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
    fadeOut.setStartOffset(200);
    fadeOut.setDuration(1000);

    viewFlipper.setInAnimation(fadeIn);
    viewFlipper.setOutAnimation(fadeOut);
    //viewFlipper.startFlipping();

    enterFromBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);
    exitToBottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom);
    hideMenu(true);

    //beginAnimation();

    TourHelper.init(this)
        .withDefaultButtonEnable(true)
        .setTourTitles("Mengalami kendala?")
        .setTourDescriptions("Klik jika mengalami kesulitan login/daftar")
        .setGravities(Gravity.LEFT | Gravity.BOTTOM)
        .setViewsToAttach(rootview, R.id.btn_help)
        .setSessionKey(State.DEFAULT_VALUE_STR)
        .create()
        .show();

    Helper.trackThis(this, "Masuk ke Landing Page");
  }

  @OnClick(R.id.btn_help) void needHelp() {
    Helper.goTo(this, AskActivity.class);
    Helper.trackThis(this, "User klik tombol bantuan di Landing Page");
  }

  @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      SessionManager sessionManager = new SessionManager(getActivityComponent().context());
      sessionManager.clearAllAppData();
      Toast.makeText(this, getString(R.string.all_data_cleared), Toast.LENGTH_LONG).show();
      return BuildConfig.DEBUG || BuildConfig.DEBUG;
    }
    return super.onKeyLongPress(keyCode, event);
  }

  private void beginAnimation() {
    Handler handler = new Handler();

    Runnable update = new Runnable() {
      public void run() {

        if (mCurrentPage == adapter.getCount()) {
          mCurrentPage = 0;
        }
        int page = mCurrentPage++;
        vp_content.setCurrentItem(page, true);
        indicator.setCurrentItem(page);
      }
    };

    new Timer().schedule(new TimerTask() {

      @Override public void run() {
        handler.post(update);
      }
    }, 100, 3000);
  }

  private void hideMenu(boolean hide) {
    int visibility = hide ? View.GONE : View.VISIBLE;
    footer_menu.setVisibility(visibility);
  }

  @OnClick(R.id.splash_view) void disable() {
    closeFooterMenu();
  }

  private void openFooterMenu() {
    if (menuIsOpen) return;

    splash_view.setVisibility(View.VISIBLE);
    menuIsOpen = true;
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
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    footer_menu.startAnimation(enterFromBottom);
  }

  private void closeFooterMenu() {
    if (!menuIsOpen) return;

    menuIsOpen = false;
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
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    footer_menu.startAnimation(exitToBottom);
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.attachView(this);
    LoginManager.getInstance().logOut();
  }

  @OnClick(R.id.btn_login) void doLogin() {
    label_bot.setText(getString(R.string.email_or_username2));
    ll_cen.setVisibility(View.GONE);
    ll_bot.setOnClickListener(view -> {
      Helper.trackThis(LandingPages.this, "user membuka tampilan login via email/username");
      Helper.goTo(LandingPages.this, SignInActivity.class);
    });

    openFooterMenu();
    menuIsOpen = true;
    actionMenuOpen = "login";
  }

  @OnClick(R.id.ll_cen) void doLoginEmail() {
    doRegisterWithEmail();
  }

  @OnClick(R.id.btn_daftar) void daftar() {
    label_bot.setText("Email");
    ll_cen.setVisibility(View.GONE);
    ll_bot.setOnClickListener(view -> {
      //doRegisterWithEmail();
      Helper.goTo(this,SignInActivity.class,new Intent().putExtra("mode",SignInActivity.MODE_SIGNUP));
      Helper.trackThis(LandingPages.this, "user membuka tampilan sign up via email");
     //Intent i = new Intent(this,FormEditFieldActivity.class)
     //     .putExtra("field", view.getId())
     //     .putExtra("from", "profile")
     //     .putExtra("text", "")
     //     .putExtra("type", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
     //     .putExtra("note","Silahkan masukan email kamu, pastikan email tersebut valid, dan dapat kami verifikasikan")
     //     .putExtra("hint", "Input email kamu");
     // startActivity(i);
    });

    openFooterMenu();
    menuIsOpen = true;
    actionMenuOpen = "sign up";
  }

  private void doRegisterWithEmail() {
    Helper.trackThis(LandingPages.this, "user membuka tampilan sign up via email");
    final Intent intent = new Intent(this, AccountKitActivity.class);
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
        new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.EMAIL,
            AccountKitActivity.ResponseType.TOKEN);
    configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
    intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        configurationBuilder.build());
    startActivityForResult(intent, LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL);
  }

  @OnClick(R.id.ll_top) void accountKitRegisterWithPhone() {
    Helper.trackThis(LandingPages.this, "user membuka tampilan "+actionMenuOpen+" via no telepon");
    final Intent intent = new Intent(LandingPages.this, AccountKitActivity.class);
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
        new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
            AccountKitActivity.ResponseType.TOKEN);
    configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
    configurationBuilder.setDefaultCountryCode("ID");
    configurationBuilder.setReceiveSMS(true);
    intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        configurationBuilder.build());
    startActivityForResult(intent, APP_REQUEST_CODE_ACCOUNT_KIT_PHONE);
  }

  @OnClick(R.id.btn_facebook) void doFacebookLogin(View v) {
    loginButton.performClick();
    Helper.trackThis(LandingPages.this, "user klik tombol facebook untuk sign up");
  }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
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
            Helper.trackThis(LandingPages.this, "user berhasil "+actionMenuOpen+" no telepon");
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
      Toast.makeText(LandingPages.this, toastMessage, Toast.LENGTH_LONG).show();
      showErrorActivity(loginResult.getError());
    } else if (loginResult.wasCancelled()) {
      toastMessage = "Cancelled";
      Toast.makeText(LandingPages.this, toastMessage, Toast.LENGTH_LONG).show();
    } else {
      if (loginResult.getAccessToken() != null) {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
          @Override public void onSuccess(Account account) {
            Timber.e("login email");
            presenter.loginViaAccountKit(APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL,
                loginResult.getAccessToken().getToken(), null, account.getEmail());

            Helper.trackThis(LandingPages.this, "user berhasil sign up via email");
          }

          @Override public void onError(AccountKitError accountKitError) {
            Timber.d("accountKitError" + accountKitError);
          }
        });
      } else {

      }
    }
  }

  private void gotoOneStepActivity(String auth_with, String access_token, String auth_code,
      String email, String phone, String username, String birthday, String gender) {
    Intent i = new Intent(LandingPages.this, OneStepCloserActivity.class);
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

  private void showErrorActivity(AccountKitError error) {
    Toast.makeText(this, error.getUserFacingMessage(), Toast.LENGTH_LONG).show();
  }

  @Override public void onLoad(int requestid) {
    FLAG_STATUS = false;
    pDialog.show();
  }

  @Override public void onComplete(int requestid) {
    pDialog.dismiss();
    if (FLAG_STATUS) {
      Intent intent = new Intent(getActivityComponent().context(), NewMainActivity.class);
      startActivity(intent);
    }
  }

  @Override public void onError(int requestid) {
    FLAG_STATUS = false;
    pDialog.dismiss();
  }

  @Override public void onNext(int requestid) {
    FLAG_STATUS = true;
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
      Helper.trackThis(this, "user berhasil sign up/login dari tombol facebook");
    } else if (auth_with.equalsIgnoreCase("account_kit")) {
      Timber.e("access_token " + access_token);
      HashMap<String, String> map = (HashMap<String, String>) data;
      String email = (map.containsKey("email") ? map.get("email").toString() : "");
      String phone = (map.containsKey("phone") ? map.get("phone").toString() : "");
      gotoOneStepActivity(auth_with, access_token, "", email, phone, "", "", "");
    }
  }

  @Override public void showDialogConfirm(boolean is_valid,AuthPresenter.onClickDialog clickDialog) {

  }

  @Override protected void onPause() {
    closeFooterMenu();
    super.onPause();
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}