package javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.accountkit.AccountKit;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.presenter.mami.RegisterMamiPresenter;
import javasign.com.dompetsehat.presenter.profile.ProfilePresenter;
import javasign.com.dompetsehat.ui.activities.institusi.FormEditFieldActivity;
import javasign.com.dompetsehat.ui.activities.institusi.base.BaseInstitusi;
import javasign.com.dompetsehat.ui.event.FormEditTextFieldEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import rx.Observer;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/24/16.
 */
public class RegisterMamiActivity extends BaseInstitusi {

  public static String accessFrom = "";

  @Bind(R.id.tv_ktp) TextView tv_ktp;
  @Bind(R.id.tv_email) TextView tv_email;
  @Bind(R.id.tv_phone) TextView tv_phone;
  @Bind(R.id.tv_password) TextView tv_password;
  @Bind(R.id.tv_referal) TextView tv_referal;
  @Bind(R.id.ccp) CountryCodePicker ccp;
  @Bind(R.id.btn_send) Button btn_send;
  @Bind(R.id.tv_info_button) TextView tv_info_button;
  @Bind(R.id.label_ktp) TextView label_ktp;
  @Bind(R.id.label_email) TextView label_email;
  @Bind(R.id.label_password) TextView label_password;
  @Bind(R.id.label_phone) TextView label_phone;
  @Bind(R.id.ll_main) LinearLayout ll_main;

  private RxBus rxBus = MyCustomApplication.getRxBus();
  @Inject RegisterMamiPresenter presenter;
  @Inject ProfilePresenter presenterProfile;

  public String referral_code = null;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register_mami);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    AccountKit.initialize(this);
    presenter.attachView(this);
    presenterProfile.attachView(this);
    init();
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {
    Words.setButtonToListen(btn_send, tv_ktp, tv_email, tv_phone, tv_password);
    Words.setViewToListenVisibility(tv_info_button, tv_ktp, tv_email, tv_phone, tv_password);
    giveAsterix(label_ktp, label_email, label_phone, label_password);
    presenterProfile.setRegister();
    Helper.setDefaultFontPasswordHint(new PasswordTransformationMethod(), tv_password);
    rxBus.toObserverable()
        .ofType(FormEditTextFieldEvent.class)
        .subscribe(new SimpleObserver<FormEditTextFieldEvent>() {
          @Override public void onNext(FormEditTextFieldEvent event) {
            TextView textView =
                ButterKnife.findById(RegisterMamiActivity.this, event.viewIdToBeFilled);
            textView.setText(event.text);
            ccp.setCountryForNameCode(event.countryCode);
          }
        });

    Helper.trackThis(this, "user berhasil ke tampilan sign up manulife");
  }

  public void giveAsterix(final TextView... texts) {
    String bintang = "<font color=\"red\">*</font>";
    for (TextView t : texts) {
      String text = t.getText().toString() + " " + bintang;
      if (Build.VERSION.SDK_INT >= 24) {
        t.setText(Html.fromHtml(text, 0)); // for 24 api and more
      } else {
        t.setText(Html.fromHtml(text));// or for older api
      }
    }
  }

  @OnClick({ R.id.tv_ktp, R.id.tv_password, R.id.tv_referal }) void editField(View view) {
    String labelHint = getLabelHint(view.getId());
    String note = getNote(view.getId());
    TextView tv = (TextView) view;

    startActivity(new Intent(this, FormEditFieldActivity.class).putExtra("form-mami", true)
        .putExtra("field", view.getId())
        .putExtra("from", "register")
        .putExtra("text", tv.getText().toString())
        .putExtra("hint", labelHint)
        .putExtra("note", note)
        .putExtra("code", ccp.getSelectedCountryNameCode()));
  }

  @OnClick(R.id.btn_send) void send() {
    String id = tv_ktp.getText().toString();
    String email = tv_email.getText().toString();
    String phone = tv_phone.getText().toString();
    String sandi = tv_password.getText().toString();
    String country = ccp.getSelectedCountryCode();
    String referred_by = tv_referal.getText().toString();
    if (TextUtils.isEmpty(referred_by)) {
      Helper.createDialog(this, "Konfirmasi",
          "Referral kode teman anda belum terisi, \nApakah anda akan melanjutkan pendaftaran tanpa referral kode ?",
          "Ya", "Tidak", (dialogInterface, i) -> {
            presenter.registerMami(id, email, country, phone, sandi, referred_by, referral_code);
          }, (dialogInterface, i) -> {
            dialogInterface.dismiss();
          }).show();
      return;
    }
    presenter.registerMami(id, email, country, phone, sandi, referred_by, referral_code);
  }

  private String getLabelHint(int idView) {
    switch (idView) {
      case R.id.tv_ktp:
        return getString(R.string.register_mami_no_ktp);
      case R.id.tv_email:
        return getString(R.string.email_manulife_hint);
      case R.id.tv_phone:
        return getString(R.string.phone_manulife_hint);
      case R.id.tv_referal:
        return getString(R.string.register_mami_referral_used);
    }

    return "";
  }

  @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      tv_ktp.setText(String.valueOf(Helper.randomInt(9)));
      tv_phone.setText("85729" + String.valueOf(Helper.randomInt(6)));
      tv_email.setText(Helper.randomString(5) + "@dompetsehat.com");
      tv_password.setText("Qwaszx1234");
      //tv_referal.setText("ABCD1234");
      referral_code = Helper.randomString(5).toUpperCase();
      Toast.makeText(this, "Dummy referral code " + referral_code, Toast.LENGTH_LONG).show();
      Timber.e("Heloo " + BuildConfig.DEBUG);
      return BuildConfig.DEBUG || BuildConfig.DEBUG;
    }
    return super.onKeyLongPress(keyCode, event);
  }

  private String getNote(int idView) {
    switch (idView) {
      case R.id.tv_ktp:
        return getString(R.string.warning_id_card_usage);
      case R.id.tv_email:
        return getString(R.string.warning_email_must_valid);
      case R.id.tv_phone:
        return getString(R.string.warning_phone_must_valid);
      case R.id.tv_referal:
        return getString(R.string.warning_registering_friends_code);
      case R.id.tv_password:
        return getString(R.string.warning_password_usage) + " (!#@$%^&*()<>?/,.-+_=~')";
    }

    return "";
  }

  @Override public void successRegister(String RequestId) {
    super.successRegister(RequestId);
    Intent intent = new Intent();
    intent.putExtra(FinalRegistrationMamiAcitivity.REQUEST_ID, RequestId);
    Helper.goTo(this, FinalRegistrationMamiAcitivity.class, intent);

    String from = "";
    if(!TextUtils.isEmpty(accessFrom)){
      from = "dari "+accessFrom;
    }
    Helper.trackThis(this, "User telah berhasil sign Manulife "+from);
    accessFrom = "";
    finish();
  }

  @Override public void setPhoneEmail(String phone, String email) {
    super.setPhoneEmail(phone, email);
    tv_email.setText(email);
    tv_phone.setText(phone);
  }

  @Override public void errorRegister(String[] messages) {
    super.errorRegister(messages);
    runOnUiThread(() -> {
      if (messages != null) {
        String msg = Helper.combinePlural(messages);
        Helper.showCustomSnackBar(ll_main, getLayoutInflater(), msg, true,
            ContextCompat.getColor(this, R.color.red_400), Gravity.BOTTOM);
      }
    });
  }
}
