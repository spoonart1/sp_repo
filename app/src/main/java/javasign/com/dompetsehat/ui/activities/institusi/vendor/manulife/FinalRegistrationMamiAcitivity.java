package javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.widget.ViewFlipper;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.UnsupportedEncodingException;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/24/16.
 */
public class FinalRegistrationMamiAcitivity extends BaseActivity {

  final int FLIP_PAGE_0 = 0;
  final int FLIP_PAGE_1 = 1;
  public static String REQUEST_ID = "request_id";
  SessionManager session;
  boolean direct_to = false;

  @Bind(R.id.viewflipper) ViewFlipper viewflipper;
  @Bind(R.id.ic_back) View ic_back;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_final_registration_mami);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    session = new SessionManager(getActivityComponent().context());
    ic_back.setVisibility(View.INVISIBLE);
    if (getIntent().hasExtra("direct_to")) {
      Timber.e("iyaa disini");
      direct_to = getIntent().getExtras().getBoolean("direct_to", false);
      if (direct_to) {
        showProcedure();
      }
    }
  }

  @OnClick({ R.id.ic_back }) void closeProcedure() {
    closeScreen();
  }

  private void closeScreen() {
    if (direct_to) {
      closeDialog();
    } else {
      viewflipper.setDisplayedChild(FLIP_PAGE_0);
      ic_back.setVisibility(View.INVISIBLE);
      ic_back.setEnabled(false);
    }
  }

  @OnClick({ R.id.btn_close }) void closeDialog() {
    closeScreen();
  }

  @OnClick(R.id.btn_hint) void showProcedure() {
    viewflipper.setDisplayedChild(FLIP_PAGE_1);
    ic_back.setVisibility(View.VISIBLE);
    ic_back.setEnabled(true);
  }

  @OnClick(R.id.btn_next) void onNext() {
    Intent intent = getIntent();
    if (intent.hasExtra(REQUEST_ID)) {
      MCryptNew mCryptNew = new MCryptNew();
      String requestID = intent.getExtras().getString(REQUEST_ID);
      String APP_ID = mCryptNew.decrypt(BuildConfig.APP_ID_MAMI);
      String before = APP_ID + ";" + requestID;
      String ID = base64(before);
      String URL = mCryptNew.decrypt(State.URL_MAIN_MAMI)
          + State.URL_MAMI_REKSADANA
          + "/NewProfile/UpdateProfile?RequestId="
          + ID;
      //Timber.e("REQUEST ID "+ID.length());
      //Timber.e("URL "+URL);
      //Timber.e("REQUEST ID "+requestID);
      //Timber.e("APP_ID "+APP_ID);
      startActivity(new Intent(this, WebviewInstutisiActivity.class).putExtra("url",
          mCryptNew.decrypt(State.URL_MAIN_MAMI)).putExtra("from", "finalregsitration"));
    }
  }

  public String base64(String text) {
    byte[] data = new byte[0];
    try {
      data = text.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
    System.out.println("base64:" + base64);
    return base64;
  }

  @Override public void onBackPressed() {
    //super.onBackPressed();
    exitScreen();
  }

  private void exitScreen() {
    if (ic_back.getVisibility() == View.VISIBLE) {
      closeProcedure();
    } else {
      finish();
      if (session.getUserPickAppMode() == session.USER_PICK_MY_FIN) {
        //Helper.goTo(FinalRegistrationMamiAcitivity.this, ReferralLoaderActivity.class);
      } else {
        Helper.goTo(FinalRegistrationMamiAcitivity.this, NewMainActivity.class);
      }
    }
  }
}
