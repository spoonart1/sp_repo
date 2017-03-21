package javasign.com.dompetsehat.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.NetworkUtil;
import javax.inject.Inject;

import javasign.com.dompetsehat.utils.SessionManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Xenix on 12/28/2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
  @Inject DataManager dataManager;
  SessionManager session;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  public static int TAG_ID = 5;
  private NotificationManager mNotificationManager;

  @Override public void onReceive(Context context, Intent intent) {
    String status = NetworkUtil.getConnectivityStatusString(context);
    //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    try {
      ((MyCustomApplication) context.getApplicationContext()).getApplicationComponent()
          .inject(this);
      boolean isConnected = NetworkUtil.getConnectivityStatus(context) > 0;
      Timber.e("avesina NetworkChangeReceiver " + isConnected);
      session = new SessionManager(context.getApplicationContext());
      if (isConnected && session.isLoggedIn()) {
        mNotificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        compositeSubscription.add(dataManager.syncAll(session.getLastSync())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(syncResponse -> {
              Timber.e("avesina NetworkChangeReceiver sync " + syncResponse);
              if (syncResponse.status.equalsIgnoreCase("success")) {
                SyncPresenter.sendNotification(TAG_ID, mNotificationManager, context, null,
                    "Sync Data", "Sedang Menyamakan data", SyncPresenter.countData(syncResponse));
                dataManager.saveSyncData(syncResponse);
              }
            }, throwable -> {
              Timber.e("avesina NetworkChangeReceiver sync " + throwable);
            }, () -> {
              if (mNotificationManager != null) {
                mNotificationManager.cancel(TAG_ID);
              }
              if (compositeSubscription != null) {
                compositeSubscription.unsubscribe();
              }
            }));
      }
    } catch (Exception e) {
      Timber.e(" NetworkChangeReceiver onReceive " + e);
    }
  }
}