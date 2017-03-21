package javasign.com.dompetsehat.ui.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.facebook.accountkit.AccountKit;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.VeryFund.ServiceHandler;
import javasign.com.dompetsehat.VeryFund.URLAdapter;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.sync.SyncInterface;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.home.HomeActivity;
import javasign.com.dompetsehat.ui.activities.landing.LandingPages;
import javasign.com.dompetsehat.ui.activities.onestep.OneStepCloserActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/4/16.
 */
public class NewMainActivity extends BaseActivity implements SyncInterface {

  public static boolean FLAG_RETROFIT_REFRESH3 = false;
  //Digunakan untuk pengecekan retrofit sendJSONRequest3 masih jalan atau tidak.
  public final static String FLAG = "flag";
  public final static String MAIN_ACTIVITY = "main_activity";
  public final static String SETTING_ACTIVITY = "setting_activity";
  public final String clickedProduct = "clickedProduct";
  public final String clickedAccount = "clickedAccount";
  private final String YES = "yes";
  private final String MESSAGE = "message";
  public static int SERVICEREMINDER = 0, SERVICEALARM = 1, SERVICEACTVITIESREMINDER = 2,
      SERVICESYNC = 3;

  final URLAdapter url = new URLAdapter();
  final ServiceHandler jsonParser = new ServiceHandler();

  private Intent i = null;

  // avesina
  @Inject SessionManager sessionManager;
  @Inject SyncPresenter presenter;

  @Bind(R.id.tv_version) TextView tv_version;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_main);

    GeneralHelper.statusBarColor(getWindow(), Helper.GREEN_DOMPET_COLOR);

    ButterKnife.bind(this);
    AccountKit.initialize(this);
    //Smooch.init(getApplication(), SessionManager.SMOOCH_APP_TOKEN);

    getActivityComponent().inject(this);

    sessionManager = new SessionManager(getActivityComponent().context());
    tv_version.setText(getString(R.string.version) + " " + BuildConfig.VERSION_NAME);

    if (!sessionManager.isLoggedIn()) {
      i = new Intent(this, LandingPages.class);
      beginload(i);
      if (sessionManager.isInvalidAccessToken()) {
        Toast.makeText(NewMainActivity.this, getString(R.string.error_login_expired),
            Toast.LENGTH_LONG).show();
      }
      return;
    }

    //TODO : session is logged in but need to check which mode that user picked?

    if (!MyCustomApplication.showInvestasi()) {
      sessionManager.setCurrentUserPickAppMode(SessionManager.USER_PICK_MY_FIN);
    }
    if (sessionManager.getUserPickAppMode() == SessionManager.USER_PICK_MY_FIN) {
      i = new Intent(this, NewMainActivityMyFin.class);
    } else if (sessionManager.getUserPickAppMode() == SessionManager.USER_PICK_MY_REFERRAL) {
      i = new Intent(this, NewMainActivityMyReferral.class);
    } else {
      i = new Intent(this, HomeActivity.class);

    }

    if (sessionManager.isLoggedIn()) {
      presenter.registerFCM();
      if (!sessionManager.isSmooch()) {
        sessionManager.initSmooch();
      }
    }

    if (getIntent().hasExtra(State.GO_ACTIVITY)) {
      String className = getIntent().getExtras().getString(State.GO_ACTIVITY);
      Timber.e("avesina package "+getPackageName());
      if(className.contains(getPackageName())) {
        Bundle bundle = this.getIntent().getExtras();
        i.putExtras(bundle);
      }
    }
    beginload(i);
  }

  private void beginload(final Intent intent) {
    final Handler handler = new Handler();
    final boolean OVERRIDE_MODE = false;

    handler.postDelayed(() -> {
      if (OVERRIDE_MODE) {
        i = new Intent(NewMainActivity.this, OneStepCloserActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        return;
      }
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }, 2500);
  }

  @Override protected void onPause() {
    super.onPause();
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override protected void onResume() {
    this.getKeyHash();
    super.onResume();
  }

  @Override public void onComplete(int code) {

  }

  @Override public void onError(int code) {

  }

  @Override public void onNext(int code, String message) {

  }
}