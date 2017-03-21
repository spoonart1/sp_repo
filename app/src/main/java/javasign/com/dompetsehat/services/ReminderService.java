package javasign.com.dompetsehat.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.cashflow;
import javasign.com.dompetsehat.models.json.product;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Xenix on 12/16/2015.
 */
public class ReminderService extends IntentService {

  private SessionManager session;
  private DbHelper db;
  private Calendar c = Calendar.getInstance();
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  private Double total = 0.0;
  private RupiahCurrencyFormat rp = new RupiahCurrencyFormat();
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private int SERVICEREMINDER = 0;
  @Inject DataManager dataManager;

  public ReminderService() {
    super("ReminderService");
  }

  @Override protected void onHandleIntent(Intent intent) {
    db = DbHelper.getInstance(getApplicationContext());
    session = new SessionManager(getApplicationContext());
    if (session.getIdUser() != null && !session.getIdUser().equals("") && !session.getIdUser()
        .equals("null") && session.getIdUser() != "null") {
      List<Account> accounts = db.getAllAccountByUserExceptDompet(session.getIdUser());
      compositeSubscription.add(Observable.from(accounts)
          .map(account -> account)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(account -> {
            getData(account.getIdaccount());
          }, throwable -> {
          }, () -> {
            if(total > 0) {
              //sendNotification();
            }
          }));
    }
  }

  private void getData(int account_id) {
    compositeSubscription.add(dataManager.getCashFlowAccount(account_id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          dataManager.saveAccount(data.response.user_id,data.response.accounts);
          for (account account : data.response.accounts) {
            for (product p: account.products){
              if(p.cashflow != null) {
                for (cashflow c : p.cashflow) {
                  if(c.type.equalsIgnoreCase("DB")){
                    total += c.amount;
                  }
                }
              }
            }
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  @Override public void onCreate() {
    super.onCreate();
    session = new SessionManager(getApplicationContext());
    ((MyCustomApplication) getApplication()).getApplicationComponent().inject(this);
  }

  private void sendNotification() {
    String msgText = "Hari ini anda melakukan penarikan  " + rp.toRupiahFormatSimple(total) + ", sudahkah Anda melakukan pencatatan? Catat pengeluaran manual anda disini";
    Intent notificationIntent = new Intent(this, NewMainActivity.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    Helper.sendNotification(SERVICEREMINDER, this, intent, "Notification", msgText);
  }
}
