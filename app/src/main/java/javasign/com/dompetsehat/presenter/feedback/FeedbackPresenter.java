package javasign.com.dompetsehat.presenter.feedback;

import android.content.Context;
import android.widget.Toast;
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
 * Created by avesina on 9/28/16.
 */

public class FeedbackPresenter extends BasePresenter<FeedbackInterface> {

  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final Context context;
  private final DbHelper db;
  private final SessionManager session;

  @Inject
  public FeedbackPresenter(@ActivityContext Context context, DataManager dataManager, SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = session;
    this.db = db;
  }

  @Override public void attachView(FeedbackInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void sendFeedBack(String subjek, String msg) {
    compositeSubscription.add(dataManager.sendFeedBack(subjek,msg)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            response->{
              Timber.e("Response "+response);
              if(!response.status.equalsIgnoreCase("success")){
                getMvpView().onError(1);
                Toast.makeText(context,""+response.message+" "+response.msg,Toast.LENGTH_LONG).show();
              }else {
                getMvpView().SuccessFeedBack();
              }
            },
            throwable -> {
              getMvpView().onError(1);
              Toast.makeText(context,""+throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            },
            () ->{
              getMvpView().onComplete(1);
            }
        ));
  }
}
