package javasign.com.dompetsehat.ui.activities.profile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.ProfileTracker;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.profile.ProfileInterface;
import javasign.com.dompetsehat.presenter.profile.ProfilePresenter;
import javasign.com.dompetsehat.presenter.setting.SettingInterface;
import javasign.com.dompetsehat.presenter.setting.SettingPresenter;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.institusi.FormEditFieldActivity;
import javasign.com.dompetsehat.ui.activities.setting.adapter.AdapterSetting;
import javasign.com.dompetsehat.ui.event.FormEditTextFieldEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.NetworkUtil;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import rx.Observer;
import timber.log.Timber;

/**
 * Created by bastianbentra on 9/2/16.
 */
public class EditProfileActivity extends BaseActivity
    implements ProfileInterface, SettingInterface {

  @Bind(R.id.ccp) CountryCodePicker ccp;
  @Bind(R.id.tv_date) TextView tv_date;
  @Bind(R.id.tv_username) TextView tv_username;
  @Bind(R.id.tv_phone) TextView tv_phone;
  @Bind(R.id.tv_email) TextView tv_email;
  @Bind(R.id.tv_password) TextView tv_password;
  @Bind(R.id.tv_anak) TextView tv_anak;
  @Bind(R.id.tv_pendapatan) TextView tv_pendapatan;
  @Bind(R.id.tv_user_id) TextView tv_user_id;
  @Bind(R.id.ic_back) IconicsTextView ic_back;
  @Bind(R.id.btn_facebook) LinearLayout btn_facebook;
  @Bind(R.id.tv_btn_facebook) TextView tv_btn_facebook;
  @Bind(R.id.btn_logout) Button btn_logout;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private GeneralHelper helper = GeneralHelper.getInstance();
  private Date mCurrentDateProfile;
  private ProfileTracker profileTracker;

  private Date original_date = null;
  private String original_phone = "";
  private String original_email = "";
  private Double original_income = null;
  private Integer original_kids = null;
  @Inject ProfilePresenter presenter;
  @Inject SettingPresenter settingPresenter;
  @Inject SyncPresenter syncPresenter;
  ProgressDialog dialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    settingPresenter.attachView(this);
    setTitle("Edit Profile");
    init();
    presenter.setProfile();
    dialog = new ProgressDialog(this);

    Helper.trackThis(this, "user membuka tampilan ubah profile");
  }

  @Override public void setTitle(CharSequence title) {
    ((TextView) ButterKnife.findById(this, R.id.tv_title)).setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    String edit_phone = null;
    String edit_email = null;
    Double edit_income = null;
    Integer edit_kids = null;
    Date edit_date = null;
    if (original_email == null) {
      edit_email = tv_email.getText().toString();
    } else if (!original_email.equalsIgnoreCase(tv_email.getText().toString())) {
      edit_email = tv_email.getText().toString();
    }
    if (original_phone == null) {
      edit_phone = tv_phone.getText().toString();
    } else if (!original_phone.equalsIgnoreCase(tv_phone.getText().toString())) {
      edit_phone = tv_phone.getText().toString();
    }
    Timber.e("phone " + edit_phone);
    if (original_date == null) {
      edit_date = mCurrentDateProfile;
    } else if (original_date.getTime() != mCurrentDateProfile.getTime()) {
      edit_date = mCurrentDateProfile;
    }
    Timber.e("original_pendapatan " + original_income + " pendapatan " + Double.valueOf(
        RupiahCurrencyFormat.clearRp(tv_pendapatan.getText().toString().equals("") ? "0.0"
            : tv_pendapatan.getText().toString())));
    if (original_income == null) {
      edit_income = Double.valueOf(RupiahCurrencyFormat.clearRp(
          tv_pendapatan.getText().toString().equals("") ? "0"
              : tv_pendapatan.getText().toString()));
      Timber.e("sini 1");
    } else if (!original_income.equals(Double.valueOf(RupiahCurrencyFormat.clearRp(
        tv_pendapatan.getText().toString().equals("") ? "0.0"
            : tv_pendapatan.getText().toString())))) {
      Timber.e("sini 2");
      edit_income = Double.valueOf(RupiahCurrencyFormat.clearRp(
          tv_pendapatan.getText().toString().equals("") ? "0"
              : tv_pendapatan.getText().toString()));
    }
    if (original_kids == null) {
      edit_kids = Integer.valueOf(RupiahCurrencyFormat.clearRp(
          tv_anak.getText().toString().equals("") ? "0" : tv_anak.getText().toString()));
    } else if (original_kids != Integer.valueOf(
        RupiahCurrencyFormat.clearRp(tv_anak.getText().toString()).equals("") ? "0"
            : tv_anak.getText().toString())) {
      edit_kids = Integer.valueOf(RupiahCurrencyFormat.clearRp(
          tv_anak.getText().toString().equals("") ? "0" : tv_anak.getText().toString()));
    }
    Timber.e("edit_income " + edit_income);
    Timber.e("edit_kids " + edit_kids);
    if (edit_date != null
        || edit_email != null
        || edit_phone != null
        || edit_income != null
        || edit_kids != null) {
      dialog.setMessage("Menyimpan perubahan");
      dialog.show();
      if(NetworkUtil.getConnectivityStatus(this) > 0) {
        presenter.updateProfile(edit_phone, edit_email, edit_date, edit_income, edit_kids);
      }else{
        finish();
      }
    } else {
      finish();
    }
  }

  @Override public void onBackPressed() {
    onBack();
  }

  @OnClick({ R.id.tv_phone, R.id.tv_password, R.id.tv_pendapatan, R.id.tv_anak })
  void editField(View view) {
    String labelHint = getLabelHint(view.getId());
    String note = getNote(view.getId());
    TextView tv = (TextView) view;
    Intent i = new Intent(this, FormEditFieldActivity.class);
    i.putExtra("from", "profile").putExtra("field", view.getId());
    i.putExtra("button-label", getString(R.string.save));
    if (view.getId() != R.id.tv_password) {
      i.putExtra("text", tv.getText().toString());
      if (view.getId() == R.id.tv_pendapatan || view.getId() == R.id.tv_anak) {
        i.putExtra("type", InputType.TYPE_CLASS_NUMBER);
      } else {
        i.putExtra("type", InputType.TYPE_CLASS_TEXT);
      }
    }
    i.putExtra("hint", labelHint)
        .putExtra("note", note)
        .putExtra("code", ccp.getSelectedCountryNameCode());
    startActivity(i);
  }

  //@OnClick(R.id.tv_phone) void validatePhone(View v) {
  //  Helper.showCustomSnackBar(v, getLayoutInflater(), "Verify with account kit (hapus ini nanti)");
  //}

  private void init() {
    tv_user_id.setText("DS"+new SessionManager(this).getIdUser());
    rxBus.toObserverable()
        .ofType(FormEditTextFieldEvent.class)
        .subscribe(new Observer<FormEditTextFieldEvent>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onNext(FormEditTextFieldEvent event) {
            TextView textView =
                ButterKnife.findById(EditProfileActivity.this, event.viewIdToBeFilled);
            if (event.viewIdToBeFilled == tv_phone.getId()) {
              PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
              try {
                Timber.e("phone " + event.text);
                Phonenumber.PhoneNumber phoneNumber =
                    phoneUtil.parse(event.text, State.getLocale().getCountry());
                String phone =
                    phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                Timber.e("before " + phone);
                event.text = String.valueOf(Helper.cleanPhoneNumber(phone));
                Timber.e("after " + event.text);
              } catch (NumberParseException e) {
                Timber.e("ERROR NumberParseException " + e);
                event.text = "";
              }
            }
            textView.setText(event.text);
            ccp.setCountryForNameCode(event.countryCode);
          }
        });
  }

  @OnClick(R.id.btn_facebook) void connectFb() {

  }

  @OnClick(R.id.btn_logout) void logout(){
    Helper.trackThis(this, "User logout dari aplikasi DompetSehat");
    ProgressDialog dialog = new ProgressDialog(this);
    dialog.setMessage("Logout......");
    dialog.show();
    syncPresenter.onLogout(() -> {
      new SessionManager(EditProfileActivity.this).logoutUser();
      MyCustomApplication.disposeRxBus();
      ((MyCustomApplication) getApplication()).disposeApplicationComponent();
      btn_logout.setClickable(false);
      dialog.dismiss();
    });

    Helper.trackThis(this, "user telah klik logout");
  }

  @OnClick(R.id.btn_delete) void deleteAllData() {
    createDialog(getString(R.string.delete_confirmation_dialog_title),
        getString(R.string.delete_confirmation_dialog_message),
        getString(R.string.delete),
        getString(R.string.cancel), (dialogInterface, i) -> {
          settingPresenter.deleteAllData();
        }, (dialogInterface, i) -> {
          dialogInterface.dismiss();
        });
  }

  private void createDialog(String title, String message, String positiveLabel, String negativeLabel,
      DialogInterface.OnClickListener dialogInterfacePositif,
      DialogInterface.OnClickListener dialogInterfaceNegatif) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(positiveLabel, dialogInterfacePositif);
    builder.setNegativeButton(negativeLabel, dialogInterfaceNegatif);
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  private SlideDateTimeListener listener = new SlideDateTimeListener() {
    @Override public void onDateTimeSet(Date date) {
      tv_date.setText(Helper.setSimpleDateFormat(date, GeneralHelper.FORMAT_BIRTHDAY));
      setCurrentDateProfile(date);
    }
  };

  @OnClick(R.id.tv_date) void setDate(TextView t) {
    Date date = Helper.isEmptyObject(mCurrentDateProfile) ? new Date() : mCurrentDateProfile;
    new SlideDateTimePicker.Builder(getSupportFragmentManager()).setInitialDate(date)
        .setListener(listener)
        .setIsDateOnly(true)
        .setIs24HourTime(true)
        .setIndicatorColor(Color.WHITE)
        .build()
        .show();
  }

  private String getLabelHint(int idView) {
    switch (idView) {
      case R.id.tv_username:
        return "USERNAME ANDA";
      case R.id.et_email:
        return "EMAIL VALID ANDA";
      case R.id.tv_phone:
        return "NOMOR TELEPON";
      case R.id.tv_date:
        return "TANGGAL LAHIR ANDA";
      case R.id.tv_pendapatan:
        return "PENDAPATAN BULANAN";
      case R.id.tv_anak:
        return "JUMLAH ANAK";
    }

    return "";
  }

  private String getNote(int idView) {
    switch (idView) {
      case R.id.tv_username:
        return "Username yang digunakan untuk masuk ke aplikasi";
      case R.id.tv_email:
        return "Email yang didaftarkan harus valid";
      case R.id.tv_phone:
        return "Nomor handphone yang didaftarkan harus aktif dan valid";
      case R.id.tv_date:
        return "Tanggal lahir yang sesuai dengan kartu identitas Anda";
      case R.id.tv_password:
        return "Kata sandi akan meningkatkan keamanan pada akun anda yang digunakan untuk masuk ke aplikasi";
      case R.id.tv_pendapatan:
        return "Pendapatan akan memudahkan mengestimasi anggaran anda";
      case R.id.tv_anak:
        return "Jumlah anak menentukan estimasi anggaran untuk anak Anda";
    }

    return "";
  }

  @Override public void putData(Object data) {

  }

  @Override public void setProfile(String username, String phone, String email, String password,
      String tanggal_birthday, Double pendapatan, Integer anak, Bitmap bitmap,
      boolean fb_connected,String fb_id) {
    runOnUiThread(() -> {
      original_email = email;
      original_phone = phone;
      original_income = pendapatan;
      original_kids = anak;
      tv_username.setText(username);
      tv_phone.setText(phone);
      tv_email.setText(email);
      tv_password.setText(password);
      if (pendapatan != null) {
        if (pendapatan > 0) {
          tv_pendapatan.setText(new RupiahCurrencyFormat().toRupiahFormatSimple(pendapatan));
        }
      }
      if (anak != null) {
        if (anak > 0) {
          tv_anak.setText("" + anak);
        }
      }
      try {
        Date date = Helper.setInputFormatter(GeneralHelper.FORMAT_YYYY_MM_DD, tanggal_birthday);
        original_date = date;
        if(date != null) {
          setCurrentDateProfile(date);
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          String tgl = calendar.get(Calendar.DATE) + " " + Helper.getMonthName(calendar.get(Calendar.MONTH),
              false) + " " + calendar.get(Calendar.YEAR);
          tv_date.setText(tgl);
        }
      } catch (Exception e) {
        Timber.e("ERROR " + e);
      }
    });
    if (fb_connected) {
      btn_facebook.setVisibility(View.VISIBLE);
      tv_btn_facebook.setText(getString(R.string.terkoneksi_facebook)+" "+fb_id);
    } else {
      btn_facebook.setVisibility(View.GONE);
    }
  }

  @Override public void setPhoneEmail(String phone, String email) {

  }

  @Override public void finishValidate() {

  }

  @Override public void setLevel(String level) {

  }

  @Override public void showEmailVerificationButton(boolean set) {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {
    if (RequestID == 123) {
      dialog.dismiss();
      finish();
    }
  }

  @Override public void onError(int RequestID) {
    if(RequestID == 123) {
      finish();
    }
  }

  @Override public void onNext(int RequestID) {

  }

  private void setCurrentDateProfile(Date date) {
    this.mCurrentDateProfile = date;
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
      settingPresenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void startLoading(String message) {
    dialog.setMessage(message);
    dialog.show();
  }

  @Override public void error() {
    dialog.dismiss();
  }

  @Override public void complete(String message) {
    dialog.dismiss();
    Helper.showCustomSnackBar(ButterKnife.findById(this, R.id.btn_delete), getLayoutInflater(),
        message);
  }

  @Override public void onnext(String message) {
    runOnUiThread(() -> {
      dialog.setMessage(message);
    });
  }

  @Override public void setAdapter(List<AdapterSetting.SettingModel> settingModels) {

  }

  @Override public void setSpinner(ArrayList<Account> arrayLists, String[] labels) {

  }

  @Override public void setPath(String path) {

  }
}