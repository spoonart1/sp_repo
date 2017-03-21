package javasign.com.dompetsehat.presenter.mami;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialMultiAutoCompleteTextView;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.response.MamiDataResponse;
import javasign.com.dompetsehat.services.ErrorUtils;
import javasign.com.dompetsehat.ui.event.AddAccountEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 10/24/16.
 */

public class RegisterMamiPresenter extends BasePresenter<MamiInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final SessionManager session;
  private DbHelper db;
  private MCryptNew mCryptNew = new MCryptNew();
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private ProgressDialog dialog;

  @Inject public RegisterMamiPresenter(@ActivityContext Context context, DataManager dataManager,
      SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(MamiInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void validateData(String email, String ktp, String phone) {
    compositeSubscription.add(dataManager.validateForm(email, ktp, phone)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mamiDataResponse -> {
          if (mamiDataResponse.status.equalsIgnoreCase("fail")) {
            getMvpView().errorValidate();
            //Toast.makeText(context,"User sudah ada",Toast.LENGTH_SHORT).show();
            if (!TextUtils.isEmpty(email)) {
              getMvpView().errorTextEdit(context.getString(R.string.email_mami_if_error));
            } else if (!TextUtils.isEmpty(ktp)) {
              getMvpView().errorTextEdit(context.getString(R.string.id_card_number_mami_if_error));
            } else if (!TextUtils.isEmpty(phone)) {
              getMvpView().errorTextEdit(context.getString(R.string.phone_number_mami_if_error));
            }
          } else {
            if (mamiDataResponse.data.ClientStat > 0) {
              getMvpView().errorValidate();
              if (!TextUtils.isEmpty(email)) {
                getMvpView().errorTextEdit(context.getString(R.string.email_mami_if_error));
              } else if (!TextUtils.isEmpty(ktp)) {
                getMvpView().errorTextEdit(
                    context.getString(R.string.id_card_number_mami_if_error));
              } else if (!TextUtils.isEmpty(phone)) {
                getMvpView().errorTextEdit(context.getString(R.string.phone_number_mami_if_error));
              }
            } else {
              getMvpView().finishValidate();
            }
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          getMvpView().errorValidate();
        }, () -> {
        }));
  }

  public void validateData(String phone, String email) {
    //compositeSubscription.add(dataManager.validateForm(email, null, phone)
    //    .observeOn(Schedulers.io())
    //    .subscribeOn(Schedulers.io())
    //    .subscribe(
    //
    //    ));
    String[] input = { phone, email };
    ArrayList<Boolean> status = new ArrayList<Boolean>();
    Boolean[] set = {true};
    Boolean[] validOpenAccount = {false};
    compositeSubscription.add(Observable.from(input)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(si -> {
          Timber.e("avelina"+ si);
          if (input[0].equalsIgnoreCase(si)) {
            Timber.e("avelina satu "+ si);
            return dataManager.validateForm("", "", si)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
          } else {
            Timber.e("avelina dua "+ si);
            return dataManager.validateForm(si, "", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
          }
        })
        .subscribe(mamiDataResponse -> {
          if (mamiDataResponse.status.equalsIgnoreCase("fail")) {
            Toast.makeText(context,"Silahkan coba lagi",Toast.LENGTH_LONG).show();
            status.add(false);
            set[0] = set[0] && false;
          }else{
            Timber.e("avelina "+mamiDataResponse.data.Description+" "+mamiDataResponse.data.ClientStat);
            if(mamiDataResponse.data.ClientStat > 0){
              if(mamiDataResponse.data.ClientStat == 1){
                if(!validOpenAccount[0]){
                  validOpenAccount[0] = true;
                }
              }
              status.add(false);
              set[0] = set[0] && false;
            }else{
              status.add(true);
              set[0] = set[0] && true;
            }
            Timber.e("avelina "+set[0]);
          }
        }, throwable -> {
          getMvpView().errorValidate();
          String msg = Helper.combinePlural(ErrorUtils.getErrorUserMessage(throwable));
          Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
          Timber.e("ERROR " + throwable);
        }, () -> {
          if(!set[0]){
            getMvpView().showDialog(validOpenAccount[0]);
          }else{
            getMvpView().finishValidate();
          }
        }));
  }

  public void validateDataRefferal(String referral_code) {
    compositeSubscription.add(dataManager.validateReferral(referral_code)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mamiDataResponse -> {
          Timber.e("status " + mamiDataResponse.status);
          if (mamiDataResponse.status.equalsIgnoreCase("fail")) {
            getMvpView().errorValidate();
            for (MamiDataResponse.Data.Validation validation : mamiDataResponse.data.validation) {
              Toast.makeText(context, validation.message, Toast.LENGTH_LONG).show();
            }
          } else {
            session.setDraftReferral(referral_code);
            getMvpView().finishValidate();
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          //Helper.showError(context,ErrorUtils.getErrorUserMessage(throwable));
          String[] msgs = ErrorUtils.getErrorUserMessage(throwable);
          getMvpView().errorTextEdit(context.getString(R.string.referral_mami_if_error));
          getMvpView().errorValidate();
        }, () -> {
        }));
  }

  //http://111.221.107.97/mmincwsuatdev/NewProfile/UpdateProfile?RequestId={RequestId}
  public void registerMami(String id, String email, String country, String phone, String sandi,
      String referred_by, String referral_code) {
    User user = db.getUser(session.getIdUser(), DbHelper.TAB_USER);
    if (referral_code == null) {
      referral_code = user.getReferral_code();
    }
    Boolean[] isSuccess = { false };
    dialog = new ProgressDialog(context);
    dialog.setMessage("sedang proses daftar, mohon tunggu...");
    dialog.show();
    compositeSubscription.add(
        dataManager.registerMami(id, email, country, phone, sandi, referred_by, referral_code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(response -> {
              if (response.status.equals("success")) {
                String RequestID = response.data.mami_response.RequestId;
                dataManager.saveAccount(Integer.valueOf(session.getIdUser()),
                    response.data.account);
                getMvpView().successRegister(RequestID);
                //isSuccess[0] = true;
                //session.setHaveInstitutionAccount();
              } else {
                getMvpView().errorRegister(null);
              }
            }, throwable -> {
              Timber.e("ERROR " + throwable);
              getMvpView().errorRegister(ErrorUtils.getErrorUserMessage(throwable));
              //Helper.showError(context, ErrorUtils.getErrorUserMessage(throwable));
              dialog.dismiss();
            }, () -> {
              if (isSuccess[0]) {
                //compositeSubscription.add(dataManager.createAccount(10, email, sandi)
                //    .subscribeOn(Schedulers.io())
                //    .observeOn(AndroidSchedulers.mainThread())
                //    .subscribe(response -> {
                //      if (response.status.equalsIgnoreCase("OK")) {
                //        ArrayList<account> accounts = new ArrayList<account>();
                //        Timber.e("propertis " + response.response.account.properties);
                //        accounts.add(response.response.account);
                //        dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accounts);
                //        session.setHaveInstitutionAccount();
                //      } else {
                //        Timber.e("ERROR " + response.message);
                //      }
                //    }, throwable -> {
                //      Timber.e("ERROR login account after attach" + throwable);
                //    }, () -> {
                //    }));
              }
              dialog.dismiss();
            }));
  }

  public void loginMami(String email, String password) {
    User user = db.getUser(session.getIdUser(), DbHelper.TAB_USER);
    Timber.e("Referral Code " + user.getReferral_code());
    String referral_code = user.getReferral_code();
    dialog = new ProgressDialog(context);
    dialog.setMessage("Login...");
    dialog.show();
    compositeSubscription.add(dataManager.createAccount(10, email, password)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(response -> {
          if (response.status.equalsIgnoreCase("success")) {
            getMvpView().showMessage(1,response.message);
            ArrayList<account> accounts = new ArrayList<account>();
            accounts.add(response.response.account);
            dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accounts);
            session.setHaveInstitutionAccount();
            getMvpView().successLogin(referral_code, accounts.get(0).id);
          } else {
            if(response.response != null) {
              getMvpView().showMessage(response.response.ClientStat, response.message);
            }else{
              getMvpView().showMessage(0,response.message);
            }
          }
          dialog.dismiss();
        }, throwable -> {
          dialog.dismiss();
          Timber.e("ERROR " + throwable);
          Helper.showError(context, ErrorUtils.getErrorUserMessage(throwable));
        }, () -> {
          dialog.dismiss();
          rxBus.send(new AddAccountEvent("complete"));
        }));
    //compositeSubscription.add(dataManager.loginMami(email,password,referral_code)
    //    .subscribeOn(Schedulers.io())
    //    .observeOn(AndroidSchedulers.mainThread())
    //    .subscribe(
    //        response ->{
    //          Timber.e("response "+response);
    //          dialog.dismiss();
    //          if(response.status.equalsIgnoreCase("success")){
    //            session.setMamiToken(response.data.UserIdentityToken);
    //          }
    //          getMvpView().successLogin(referral_code);
    //        },throwable -> {
    //          Timber.e("ERROR "+throwable);
    //          Helper.showError(context,ErrorUtils.getErrorUserMessage(throwable));
    //          dialog.dismiss();
    //        },()->{}
    //    ));
  }

  public void passwordChecker(String s) {
    compositeSubscription.add(Observable.just(Helper.isLegalPassword(s))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isLegal -> {
          if (isLegal) {
            getMvpView().finishValidate();
          } else {
            getMvpView().errorValidate();
            getMvpView().errorTextEdit(
                context.getString(R.string.password_mami_if_error) + " (!#@$%^&*()<>?/,.-+_=~')");
          }
        }, throwable -> {
          getMvpView().errorValidate();
        }, () -> {
        }));
  }

  public void populateAutotext(MaterialMultiAutoCompleteTextView e) {
    ArrayList<String> map = session.getDraftReferral();
    if (map.size() > 0) {
      String[] datas = map.toArray(new String[map.size()]);
      Timber.e("avesina " + datas[datas.length - 1]);
      ArrayAdapter<String> adapter =
          new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, datas);
      e.setAdapter(adapter);
      e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
      e.setTokenizer(new MaterialMultiAutoCompleteTextView.Tokenizer() {
        @Override public int findTokenStart(CharSequence charSequence, int i) {
          return 0;
        }

        @Override public int findTokenEnd(CharSequence charSequence, int i) {
          return 0;
        }

        @Override public CharSequence terminateToken(CharSequence charSequence) {
          return charSequence;
        }
      });
    }
  }
}
