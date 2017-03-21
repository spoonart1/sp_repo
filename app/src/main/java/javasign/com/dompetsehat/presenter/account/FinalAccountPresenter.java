package javasign.com.dompetsehat.presenter.account;

import android.content.Context;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by avesina on 12/22/16.
 */

public class FinalAccountPresenter extends BasePresenter<FinalAccountInterface> {
  CompositeSubscription compositeSubscription = new CompositeSubscription();
  private Context context;
  private DataManager dataManager;
  private SessionManager session;
  private DbHelper db;

  @Inject public FinalAccountPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(FinalAccountInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void populateAccount(int id_account) {
    //compositeSubscription.add(
    //    Observable.just(db.getAccountById(id_account, 2, Integer.valueOf(session.getIdUser())))
    //        .observeOn(AndroidSchedulers.mainThread())
    //        .subscribeOn(Schedulers.io())
    //        .subscribe(account -> {
    //          getMvpView().setData(String username,String password);
    //        }, throwable -> {
    //        }, () -> {
    //        }));
  }
}
