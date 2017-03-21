package javasign.com.dompetsehat.job;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.receivers.NetworkChangeReceiver;
import javasign.com.dompetsehat.utils.NetworkUtil;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 12/29/16.
 */

public class SyncJob extends Job {
  public static final String TAG = "sync_job_tag";
  public static Integer JobId;
  @Inject DataManager dataManager;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  SessionManager sessionManager;
  NotificationManager mNotificationManager;

  @NonNull @Override protected Result onRunJob(Params params) {
    Timber.e("avesina SyncJob");
    Handler handler = new Handler(getContext().getMainLooper());
    handler.post(() -> {
      int status_id = NetworkUtil.getConnectivityStatus(getContext());
      sessionManager = new SessionManager(getContext());
      String status = NetworkUtil.getConnectivityStatusString(getContext());
      if (status_id == 2
          && status_id != sessionManager.networkState()
          && sessionManager.isLoggedIn()) {
        Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
        MyCustomApplication.get(getContext()).getApplicationComponent().inject(this);
        mNotificationManager =
            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        compositeSubscription.add(dataManager.syncAll(sessionManager.getLastSync())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(syncResponse -> {
              Timber.e("avesina NetworkChangeReceiver sync " + syncResponse);
              if (syncResponse.status.equalsIgnoreCase("success")) {
                SyncPresenter.sendNotification(NetworkChangeReceiver.TAG_ID, mNotificationManager,
                    getContext(), null, "Sync Data", "Sedang Menyamakan data",
                    SyncPresenter.countData(syncResponse));
                dataManager.saveSyncData(syncResponse);
              }
            }, throwable -> {
              Timber.e("avesina NetworkChangeReceiver sync " + throwable);
            }, () -> {
              if (mNotificationManager != null) {
                mNotificationManager.cancel(NetworkChangeReceiver.TAG_ID);
              }
              if (compositeSubscription != null) {
                compositeSubscription.unsubscribe();
              }
            }));
      }
      sessionManager.saveNetworkState(status_id);
    });
    return Result.RESCHEDULE;
  }

  @Override protected void onReschedule(int newJobId) {
    Timber.e("SyncJob RESCHEDULE " + newJobId);
    super.onReschedule(newJobId);
  }

  public static void scheduleJob() {
    new JobRequest.Builder(SyncJob.TAG).setExecutionWindow(30_000L, 40_000L)
        .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
        .setRequiresCharging(true)
        .setRequiresDeviceIdle(false)
        .setRequiredNetworkType(JobRequest.NetworkType.UNMETERED)
        .setRequirementsEnforced(true)
        .setPersisted(true)
        .setUpdateCurrent(true)
        .build()
        .schedule();
  }

  public static void cancelJob() {
    if (JobId != null) {
      JobManager.instance().cancel(JobId);
    }
  }
}
