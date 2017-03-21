package javasign.com.dompetsehat.presenter.interceptor;

import android.content.Context;
import com.google.gson.Gson;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.response.ParentResponse;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 11/15/16.
 */

public class InterceptorPresenter extends BasePresenter<InterceptorInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private SessionManager session;
  private Gson gson;

  @Inject
  public InterceptorPresenter(@ActivityContext Context context, SessionManager session, Gson gson) {
    this.context = context;
    this.session = session;
    this.gson = gson;
  }

  @Override public void attachView(InterceptorInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void validateToken(String body) {
    compositeSubscription.add(ObserveValidateToken(body).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {
          if (!response) {
            session.logoutUser();
            session.setInvalidAccessToken();
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {

        }));
  }

  private Observable<Boolean> ObserveValidateToken(String body) {
    Boolean response = true;
    try {
      ParentResponse parent = gson.fromJson(body, ParentResponse.class);
      if (parent.message != null) {
        if (parent.message.equalsIgnoreCase("Invalid access token.")) {
          response = false;
        }
      } else if (parent.msg != null) {
        if (parent.msg.equalsIgnoreCase("Invalid access token.")) {
          response = false;
        }
      }
    } catch (Exception e) {
      Timber.e("Error " + e);
    }
    return Observable.just(response);
  }
}
