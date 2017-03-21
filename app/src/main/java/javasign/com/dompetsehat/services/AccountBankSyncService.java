package javasign.com.dompetsehat.services;

import android.app.IntentService;
import android.content.Intent;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 1/11/17.
 */

public class AccountBankSyncService extends IntentService {
  @Inject DataManager dataManager;
  CompositeSubscription compositeSubscription = new CompositeSubscription();
  public static String ACCOUNT_ID = "account_id";

  public AccountBankSyncService() {
    super("AccountBankSyncService");
  }

  @Override public void onCreate() {
    super.onCreate();
    ((MyCustomApplication) getApplication()).getApplicationComponent().inject(this);
  }

  @Override protected void onHandleIntent(Intent intent) {
    if (intent.hasExtra(ACCOUNT_ID)) {
      int account_id = intent.getIntExtra(ACCOUNT_ID, 0);
      compositeSubscription.add(dataManager.getCashFlowAccount(account_id)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(response -> {
            if(response.status.equalsIgnoreCase("success")){
              dataManager.saveAccount(response.response.user_id,response.response.accounts);
            }
          }, throwable -> {
          }, () -> {
          }));
    }
  }
}
