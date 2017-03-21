package javasign.com.dompetsehat.services;

import android.app.IntentService;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Calendar;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 10/20/16.
 */

public class SyncIntentService extends IntentService {
  public static final String DATA = "data_json";
  @Inject DataManager dataManager;
  SessionManager session;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  public SyncIntentService() {
    super("SyncIntentService");
  }

  @Override protected void onHandleIntent(Intent intent) {
    compositeSubscription.add(dataManager.syncAll(session.getLastSync())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(syncResponse -> {
          Timber.e("avesina sync " + syncResponse);
          if (syncResponse.status.equalsIgnoreCase("success")) {
            dataManager.saveSyncData(syncResponse);
          }
        }, throwable -> {
          Timber.e("avesina sync " + throwable);
        }, () -> {
          if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
          }
        }));
    long date = Calendar.getInstance().getTime().getTime();
    compositeSubscription.add(Observable.from(dataManager.getValidAccount())
        .flatMap(account -> {
          long last_sync_timestamp = session.getLastConnectAccount(account.getIdaccount());
          long hourly = 3600000;
          if ((date - last_sync_timestamp) >= hourly) {
            return dataManager.account_sync(account.getIdaccount())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext((e) -> Observable.empty());
          } else {
            return null;
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {
          if (response != null) {
            ArrayList<account> accountArrayList = new ArrayList<>();
            accountArrayList.add(response.response.accounts);
            session.setLastConnectAccount(response.response.accounts.id, date);
            dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accountArrayList);
          }
        }, throwable -> {
          Timber.e("ERROR observable " + throwable);
        }, () -> {
          Timber.e("avesina complete");
        }));
  }

  @Override public void onCreate() {
    super.onCreate();
    session = new SessionManager(getApplicationContext());
    ((MyCustomApplication) getApplication()).getApplicationComponent().inject(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }
}
