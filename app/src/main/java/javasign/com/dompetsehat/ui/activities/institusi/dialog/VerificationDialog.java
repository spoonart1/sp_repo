package javasign.com.dompetsehat.ui.activities.institusi.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.hbb20.CountryCodePicker;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.presenter.mami.MamiInterface;
import javasign.com.dompetsehat.presenter.mami.RegisterMamiPresenter;
import javasign.com.dompetsehat.presenter.verification.VerificationInterface;
import javasign.com.dompetsehat.presenter.verification.VerificationPresenter;
import javasign.com.dompetsehat.ui.activities.account.AddAccountActivity;
import javasign.com.dompetsehat.ui.activities.institusi.FormEditFieldActivity;
import javasign.com.dompetsehat.ui.activities.institusi.InstitusiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.LoginKlikMamiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.RegisterMamiActivity;
import javasign.com.dompetsehat.ui.activities.landing.LandingPages;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.plan.ProductListInstitusiActivity;
import javasign.com.dompetsehat.ui.activities.setting.SettingActivity;
import javasign.com.dompetsehat.ui.event.FormEditTextFieldEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.LocaleHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by lafran on 1/12/17.
 */

public class VerificationDialog extends DialogFragment implements VerificationInterface,MamiInterface {

  public interface OnVerificationListener {

    void onNextClick(VerificationDialog dialog, CharSequence phone, CharSequence email);
  }
  @Bind(R.id.tv_email) TextView tv_email;

  @Bind(R.id.tv_phone) TextView tv_phone;
  @Bind(R.id.tv_desc) TextView tv_desc;
  @Bind(R.id.ccp) CountryCodePicker ccp;
  @Inject VerificationPresenter presenter;
  @Inject RegisterMamiPresenter presenterMami;
  private OnVerificationListener listener;
  private ProgressDialog pDialog;

  public static VerificationDialog newInstance(OnVerificationListener onVerificationListener) {
    VerificationDialog dialog = new VerificationDialog();
    dialog.listener = onVerificationListener;
    return dialog;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_verification, null);
    ButterKnife.bind(this, view);
    tv_desc.setText(Html.fromHtml(getString(R.string.are_you_sure_want_to_register_mami)));
    pDialog = new ProgressDialog(getActivity());
    AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
    //MyCustomApplication.getRxBus().toObserverable()
    //    .ofType(FormEditTextFieldEvent.class)
    //    .subscribe(new SimpleObserver<FormEditTextFieldEvent>() {
    //      @Override public void onNext(FormEditTextFieldEvent event) {
    //        TextView textView = ButterKnife.findById(view,event.viewIdToBeFilled);
    //        textView.setText(event.text);
    //        switch (event.viewIdToBeFilled){
    //          case R.id.tv_phone:
    //            presenter.savePhoneEmail(event.text,null);
    //            break;
    //          case R.id.tv_email:
    //            presenter.savePhoneEmail(null,event.text);
    //            break;
    //        }
    //        ccp.setCountryForNameCode(event.countryCode);
    //      }
    //    });


    RelativeLayout relative_holder = (RelativeLayout) ccp.findViewById(com.hbb20.R.id.relative_countryCodeHolder);
    relative_holder.setOnClickListener(null);

    return dialog;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    AccountKit.initialize(getActivity().getApplicationContext());
    if(getActivity() instanceof InstitusiActivity) {
      ((InstitusiActivity) getActivity()).getActivityComponent().inject(this);
    }else if(getActivity() instanceof AddAccountActivity){
      ((AddAccountActivity) getActivity()).getActivityComponent().inject(this);
    }else if(getActivity() instanceof ProductListInstitusiActivity){
      ((ProductListInstitusiActivity)getActivity()).getActivityComponent().inject(this);
    }else{
      ((BaseActivity)getActivity()).getActivityComponent().inject(this);
    }
    presenter.attachView(this);
    presenterMami.attachView(this);
    presenter.init();
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void setUser(String phone,String email) {
    getActivity().runOnUiThread(() -> {
      tv_phone.setText(phone);
    });
  }

  @Override public void onError() {

  }

  @Override public void onNext() {

  }

  @OnClick({ R.id.tv_phone, R.id.tv_email }) void editField(View view) {
    final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
    if(view.getId() == R.id.tv_phone){
      AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
          new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
              AccountKitActivity.ResponseType.TOKEN);
      configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
      intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
          configurationBuilder.build());
      startActivityForResult(intent, LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_PHONE);
    }else if(view.getId() == R.id.tv_email){
      AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
          new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.EMAIL,
              AccountKitActivity.ResponseType.TOKEN);
      configurationBuilder.setTitleType(AccountKitActivity.TitleType.APP_NAME);
      intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
          configurationBuilder.build());
      startActivityForResult(intent, LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL);
    }
  }

  //@OnClick({ R.id.tv_phone, R.id.tv_email }) void editField(View view) {
  //  String labelHint = getLabelHint(view.getId());
  //  String note = getNote(view.getId());
  //  TextView tv = (TextView) view;
  //
  //
  //  Timber.e("avesina keren editField "+view.getId());
  //  startActivity(new Intent(getActivity(), FormEditFieldActivity.class)
  //      .putExtra("form-mami", true)
  //      .putExtra("field", view.getId())
  //      .putExtra("from","register")
  //      .putExtra("text", tv.getText().toString())
  //      .putExtra("hint", labelHint)
  //      .putExtra("note", note)
  //      .putExtra("code", ccp.getSelectedCountryNameCode()));
  //}

  @OnLongClick({ R.id.tv_phone, R.id.tv_email }) boolean longeditField(View view) {
    if(BuildConfig.DEBUG_MODE) {
      if (view.getId() == R.id.tv_phone) {
        //tv_phone.setText("85729" + String.valueOf(Helper.randomInt(6)));
        //tv_phone.setText("85803018436");
        tv_phone.setText("811930434");
        return true;
      } else if (view.getId() == R.id.tv_email) {
        //tv_email.setText(Helper.randomString(5) + "@dompetsehat.com");
        tv_email.setText("joesimatupang1@gmail.com");
        return true;
      }
    }
    return false;
  }


  @Override public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_PHONE) {
      checkIfPhoneKit(data);
    }
    else if(requestCode == LandingPages.APP_REQUEST_CODE_ACCOUNT_KIT_EMAIL){
      checkIfEmailKit(data);
    }
  }

  private void checkIfPhoneKit(Intent data){
    AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
    String toastMessage = "error";
    if (loginResult.getError() != null) {
      toastMessage = loginResult.getError().getErrorType().getMessage();
    } else if (loginResult.wasCancelled()) {
      toastMessage = "Cancelled";
    } else {
      if (loginResult.getAccessToken() != null) {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
          @Override public void onSuccess(Account account) {
            //presenterMami.validateData(null,null,account.getPhoneNumber().getPhoneNumber());
            tv_phone.setText(account.getPhoneNumber().getPhoneNumber());
          }
          @Override public void onError(AccountKitError accountKitError) {
            Timber.d("accountKitError" + accountKitError);
          }
        });
      } else {

      }
    }
  }



  private void checkIfEmailKit(Intent data){
    AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
    Timber.e("Here email");
    String toastMessage = "error";
    if (loginResult.getError() != null) {
      toastMessage = loginResult.getError().getErrorType().getMessage();
    } else if (loginResult.wasCancelled()) {
      toastMessage = "Cancelled";
    } else {
      if (loginResult.getAccessToken() != null) {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
          @Override public void onSuccess(Account account) {
            tv_email.setText(account.getEmail());
          }
          @Override public void onError(AccountKitError accountKitError) {
            Timber.d("accountKitError" + accountKitError);
          }
        });
      } else {

      }
    }
  }

  @OnClick(R.id.btn_cancel) void onCancel() {
    dismissAllowingStateLoss();
  }

  @OnClick(R.id.btn_next) void onNextClick() {
    if (listener == null) return;
    if(TextUtils.isEmpty(tv_phone.getText())){
      tv_phone.setError("Tidak boleh kosong");
      return;
    }
    if(TextUtils.isEmpty(tv_email.getText())){
      tv_email.setError("Tidak bolek kosong");
      return;
    }
    pDialog.setMessage("Validasi data...");
    pDialog.show();
    presenterMami.validateData(tv_phone.getText().toString(),tv_email.getText().toString());
    //presenter.savePhoneEmail(tv_phone.getText().toString(),tv_email.getText().toString());
    //listener.onNextClick(this, tv_phone.getText(), tv_email.getText());
  }

  private String getNote(int idView) {
    switch (idView) {
      case R.id.tv_email:
        return getString(R.string.warning_email_must_valid);
      case R.id.tv_phone:
        return getString(R.string.warning_phone_must_valid);
    }
    return "";
  }

  private String getLabelHint(int idView) {
    switch (idView) {
      case R.id.tv_email:
        return getString(R.string.email_manulife_hint);
      case R.id.tv_phone:
        return getString(R.string.phone_manulife_hint);
    }
    return "";
  }

  @Override public void finishValidate() {
    getActivity().runOnUiThread(()->{
      pDialog.dismiss();
      presenter.savePhoneEmail(tv_phone.getText().toString(),tv_email.getText().toString());
      listener.onNextClick(this, tv_phone.getText(), tv_email.getText());
    });
  }

  @Override public void showDialog(boolean validOpenAccount) {
    getActivity().runOnUiThread(()->{
      pDialog.dismiss();
      final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setMessage("Email atau no ponsel kamu sudah terdaftar di sistem klikMAMI, silahkan login untuk melanjutkan, jika butuh bantuan silahkan hubungi CS Reksa Dana Manulife: (021) 2555 2255");
      builder.setPositiveButton("Login", (dialog12, id) -> {
        if(validOpenAccount){
          startActivity(new Intent(getActivity(), LoginKlikMamiActivity.class));
        }else{
          startActivity(new Intent(getActivity(), WebviewInstutisiActivity.class)
              .putExtra("url",(new  MCryptNew()).decrypt(State.URL_MAIN_MAMI))
              .putExtra("from", "finalregsitration"));
        }
      }).setNegativeButton("Skip", (dialog13, id) -> {
        dialog13.dismiss();
      });
      builder.create();
      builder.show();
    });
  }

  @Override public void errorValidate() {
    pDialog.dismiss();
  }

  @Override public void successRegister(String RequestId) {

  }

  @Override public void errorRegister(String[] messages) {

  }

  @Override public void successLogin(String reffCode, int account_id) {

  }

  @Override public void showMessage(int ClientStat, String message) {

  }

  @Override public void errorTextEdit(String message) {

  }
}
