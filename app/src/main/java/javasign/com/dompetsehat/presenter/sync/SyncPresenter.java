package javasign.com.dompetsehat.presenter.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.kelvinapps.rxfirebase.RxFirebaseAuth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.product;
import javasign.com.dompetsehat.models.response.SyncResponse;
import javasign.com.dompetsehat.services.InsertDataService;
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
 * Created by avesina on 11/9/16.
 */

public class SyncPresenter extends BasePresenter<SyncInterface> {
  String lastSync;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private GeneralHelper helper;
  private final Context context;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private DbHelper db;
  public SessionManager session;
  private DataManager dataManager;
  private Gson gson;
  private MCryptNew mCryptNew = new MCryptNew();

  public static int TAG_SYNC_ALL = 0;
  public static int TAG_SYNC_ACCOUNT = 1;
  private FirebaseAuth mAuth;

  @Inject
  public SyncPresenter(@ActivityContext Context context, DbHelper db, DataManager dataManager,
      Gson gson, FirebaseAuth auth) {
    this.context = context;
    this.db = db;
    this.session = new SessionManager(context);
    this.dataManager = dataManager;
    this.gson = gson;
    this.mAuth = auth;
  }

  private void init() {
    Timber.i(
        "SYNC LAST ACCOUNT " + new Date(session.getLastSync(session.LAST_SYNC_ACCOUNT)).toString());
    Timber.i(
        "SYNC LAST BUDGET " + new Date(session.getLastSync(session.LAST_SYNC_BUDGET)).toString());
    Timber.i("SYNC LAST PLAN " + new Date(session.getLastSync(session.LAST_SYNC_PLAN)).toString());
  }

  @Override public void attachView(SyncInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void syncAll() {
    compositeSubscription.add(grabData().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(syncResponse -> {
          if (syncResponse.status.equalsIgnoreCase("success")) {
            dataManager.saveSyncData(syncResponse);
            session.setLastSync(SessionManager.LAST_SYNC);
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          if (getMvpView() != null) {
            getMvpView().onError(TAG_SYNC_ALL);
          }
        }, () -> {
          if (getMvpView() != null) {
            getMvpView().onComplete(TAG_SYNC_ALL);
          }
        }));
  }

  public void onLogout(AfterLogout logout) {
    mAuth.signOut();
    compositeSubscription.add(grabData().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(syncResponse -> {
          if (syncResponse.status.equalsIgnoreCase("success")) {
            dataManager.saveSyncData(syncResponse);
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          logout.onlogout();
          if (getMvpView() != null) {
            getMvpView().onError(TAG_SYNC_ALL);
          }
        }, () -> {
          logout.onlogout();
          if (getMvpView() != null) {
            getMvpView().onComplete(TAG_SYNC_ALL);
          }
        }));
  }

  public interface AfterLogout {
    void onlogout();
  }

  public void SyncAllAccount(Context context) {
    ProgressDialog dialog = new ProgressDialog(context);
    dialog.setMessage("Loading ");
    dialog.show();
    long date = Calendar.getInstance().getTime().getTime();
    List<Account> accounts = db.getAllAccountByUserExceptDompet(session.getIdUser());
    compositeSubscription.add(Observable.from(accounts)
        .flatMap(account -> {
          Timber.e("avesina " + account.getIdaccount());
          long last_sync_timestamp = session.getLastConnectAccount(account.getIdaccount());
          long hourly = 3600000;
          Timber.e("avesina date " + (date - last_sync_timestamp));
          if ((date - last_sync_timestamp) >= hourly) {
            dialog.setMessage(
                "Sedang Sinkronisasi Akun Bank " + mCryptNew.decrypt(account.getName()) + " ....");
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
          dialog.dismiss();
        }, () -> {
          Timber.e("avesina complete");
          dialog.dismiss();
        }));
  }

  public Observable<SyncResponse> grabData() {
    return Observable.defer(() -> dataManager.syncAll(session.getLastSync()));
  }

  public void syncAccount(Integer account_id) {
    compositeSubscription.add(dataManager.do_sync_account(account_id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(syncResponse -> {
          Timber.e("RESPONSE " + syncResponse);
          try {
            if (syncResponse.status.equalsIgnoreCase("success")) {
              dataManager.saveAccount(Integer.valueOf(session.getIdUser()),
                  syncResponse.response.accounts);
              dataManager.saveCashflow(Integer.valueOf(session.getIdUser()),
                  syncResponse.response.cashflows);
              session.setLastSync(session.LAST_SYNC_ACCOUNT);
            }
          } catch (Exception e) {
            Timber.e("ERROR " + e);
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public void getCashflowByAccount(int account_id) {
    compositeSubscription.add(dataManager.getCashFlowAccount(account_id,session.getLastConnectAccount(account_id))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cashFlowResponse -> {
          Intent i = new Intent(this.context, InsertDataService.class);
          Gson gson = new Gson();
          session.bindData(gson.toJson(cashFlowResponse));
          i.putExtra(helper.FLAG, helper.SENDJSONREQUESTREFRESH3);
          this.context.startService(i);
        }, throwable -> {
        }, () -> {
        }));
  }

  public void syncAccountBank() {
    List<Account> accounts = db.getAllAccountByUserExceptDompet(session.getIdUser());
    Observable.from(accounts).map(account -> account).subscribe(account -> {
      syncAccount(account.getIdaccount());
    }, throwable -> {
      Timber.e("ERROR " + throwable);
      //getMvpView().onError(TAG_SYNC_ACCOUNT);
    }, () -> {
      getMvpView().onComplete(TAG_SYNC_ACCOUNT);
    });
  }

  public void registerFCM() {
    if (session.getRegisterId() != null) {
      compositeSubscription.add(dataManager.register_fcm(session.getRegisterId())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(response -> {
          }, throwable -> {
          }, () -> {
          }));
    } else {
      String token = FirebaseInstanceId.getInstance().getToken();
      session.saveRegisterId(token);
    }
  }

  public static int countData(SyncResponse sync) {
    int count = 0;
    if (Helper.checkList(sync.response.accounts)) {
      count += sync.response.accounts.size();
    }
    if (Helper.checkList(sync.response.plan)) {
      count += sync.response.plan.size();
    }
    if (Helper.checkList(sync.response.budget)) {
      count += sync.response.budget.size();
    }
    if (Helper.checkList(sync.response.cashflows)) {
      count += sync.response.cashflows.size();
    }
    if (Helper.checkList(sync.response.alarm)) {
      count += sync.response.alarm.size();
    }
    return count;
  }

  public static void sendNotification(int id, NotificationManager mNotificationManager,
      Context context, PendingIntent intent, String title, String message, int max) {
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
    mBuilder.setContentTitle(title);
    mBuilder.setContentText(message);

    mBuilder.setSmallIcon(R.drawable.icon_dompet);
    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
    bigTextStyle.setBigContentTitle("Notification");
    bigTextStyle.bigText(message);

    mBuilder.setStyle(bigTextStyle);
    mBuilder.setProgress(max, 0, false);
    mBuilder.setContentIntent(intent);
    mBuilder.setAutoCancel(true);
    mBuilder.setVibrate(new long[] { -1 });
    mNotificationManager.notify(id, mBuilder.build());
  }
}
