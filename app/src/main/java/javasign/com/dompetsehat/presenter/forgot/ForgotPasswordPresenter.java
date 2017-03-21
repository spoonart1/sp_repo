package javasign.com.dompetsehat.presenter.forgot;

import android.app.ProgressDialog;
import android.content.Context;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 11/21/16.
 */

public class ForgotPasswordPresenter extends BasePresenter<ForgotPasswordInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final Context context;
  private final DbHelper db;
  private final SessionManager session;
  ProgressDialog dialog;

  @Inject public ForgotPasswordPresenter(@ActivityContext Context context, DataManager datamanager,
      SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = datamanager;
    this.session = session;
    this.db = db;
    dialog = new ProgressDialog(context);
  }

  @Override public void attachView(ForgotPasswordInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void forgotPassword(String email, String password) {
    dialog.setMessage("Mengirim email");
    dialog.show();
    compositeSubscription.add(dataManager.forgot(email, password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(parentResponse -> {
              dialog.dismiss();
              if (parentResponse.status.equalsIgnoreCase("success")) {
                getMvpView().successForgot("Silahkan check email anda, untuk aktivasi password");
              } else {
                getMvpView().alertMessage(parentResponse.message);
              }
            }, throwable -> {
              Timber.e("ERROR " + throwable);
              dialog.dismiss();
            }, () -> {
              dialog.dismiss();
            }

        ));
  }
}
