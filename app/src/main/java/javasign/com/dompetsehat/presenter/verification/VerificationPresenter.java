package javasign.com.dompetsehat.presenter.verification;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.presenter.account.AccountInterface;
import javasign.com.dompetsehat.ui.event.AddAccountEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 1/13/17.
 */

public class VerificationPresenter extends BasePresenter<VerificationInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper;
  private DbHelper db;

  @Inject public VerificationPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db, GeneralHelper helper) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    this.helper = helper;
  }

  @Override public void attachView(VerificationInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void init() {
    compositeSubscription.add(getUser().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(user -> {
          MCryptNew mc = new MCryptNew();
          String phone = user.getPhone();
          String email = mc.decrypt(user.getEmail());
          if (!TextUtils.isEmpty(phone)) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
              Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, null);
              phone = "" + phoneNumber.getNationalNumber();
            } catch (NumberParseException e) {
              e.printStackTrace();
            }
          }
          if (!TextUtils.isEmpty(session.getEmailMami())) {
            email = session.getEmailMami();
          }
          if (!TextUtils.isEmpty(session.getPhoneMami())) {
            phone = session.getPhoneMami();
          }
          getMvpView().setUser(phone, email);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public Observable<User> getUser() {
    return Observable.just(db.getUser(session.getIdUser(), DbHelper.TAB_USER));
  }

  public void savePhoneEmail(String phone, String email) {
    if (email != null) {
      session.setEmailMami(email);
    }
    if (phone != null) {
      session.setPhoneMami(phone);
    }
  }

  public void setEmail(String email) {
    compositeSubscription.add(dataManager.resentEmail(email)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          Timber.e("Data "+data);
          getMvpView().onNext();
        }, throwable -> {
          Timber.e("ERROR "+throwable);
          getMvpView().onError();
        }, () -> {
        }));
  }
}
