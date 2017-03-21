package javasign.com.dompetsehat.presenter.account;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.response.CashflowAccountResponse;
import javasign.com.dompetsehat.models.response.SyncAccountResponse;
import javasign.com.dompetsehat.ui.event.AddAccountEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 9/29/16.
 */

public class EachAccountPresenter extends BasePresenter<EachAccountInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper;
  private DbHelper db;
  private MCryptNew mCryptNew = new MCryptNew();

  @Inject public EachAccountPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db, GeneralHelper helper) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    this.helper = helper;
  }

  @Override public void attachView(EachAccountInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadData(int account_id, boolean with_sync) {
    compositeSubscription.add(getAccount(account_id).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(account -> {
          double saldo = db.getTotalSaldo(String.valueOf(account.getIdaccount()), 2);
          getMvpView().setAccount(account, saldo);
          if (!with_sync && account.getIdvendor() == 6) {
            getSyncAccount(account_id);
          }
        }, throwable -> Timber.e("ERROR " + throwable), () -> {
          if (!with_sync) {
            subscriptionGetList(account_id);
          }
        }));
    if (with_sync) {
      getSyncAccount(account_id);
    } else {
      setLastConnect(account_id);
    }
  }

  private void setLastConnect(int account_id) {
    Long tms = session.getLastConnectAccount(account_id);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(tms);
    Date d = calendar.getTime();
    Timber.e("avesina last connect "+account_id+" "+d);
    getMvpView().setLastSync(d);
  }

  private void getSyncAccount(int account_id) {
    long date = Calendar.getInstance().getTime().getTime();
    Account accountku = db.getAccountById(account_id, 2, Integer.valueOf(session.getIdUser()));
    long last_sync = session.getLastConnectAccount(account_id);
    Timber.e("avesina last connect getSyncAccount "+last_sync+" - "+date);
    if (accountku.getIdvendor() == 6 || (date - last_sync >= Words.fifteenminute)) {
      compositeSubscription.add(dataManager.account_sync(account_id)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(syncAccountResponse -> {
            ArrayList<account> accounts = new ArrayList<account>();
            accounts.add(syncAccountResponse.response.accounts);
            dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accounts);
          }, throwable -> {
            subscriptionGetList(account_id);
            session.setLastConnectAccount(account_id, date);
            setLastConnect(account_id);
            Timber.e("ERROR avesina" + throwable);
          }, () -> {
            subscriptionGetList(account_id);
            Account account = db.getAccountById(account_id, 2, Integer.valueOf(session.getIdUser()));
            account.setName(mcrypt.decrypt(account.getName()));
            double saldo = db.getTotalSaldo(String.valueOf(account.getIdaccount()), 2);
            getMvpView().setAccount(account, saldo);
            syncAccount(account_id);
            session.setLastConnectAccount(account_id, date);
            setLastConnect(account_id);
          }));
    } else {
      subscriptionGetList(account_id);
      Account account = db.getAccountById(account_id, 2, Integer.valueOf(session.getIdUser()));
      account.setName(mcrypt.decrypt(account.getName()));
      double saldo = db.getTotalSaldo(String.valueOf(account.getIdaccount()), 2);
      getMvpView().setAccount(account, saldo);
      syncAccount(account_id);
    }
  }

  private void syncAccount(int account_id) {
    final CashflowAccountResponse[] response = { null };
    if (session.getLastSync() < 0) {
      compositeSubscription.add(dataManager.getCashFlowAccount(account_id)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(data -> {
            response[0] = data;
            if (data.status.equalsIgnoreCase("success")) {
              dataManager.saveAccount(Integer.valueOf(session.getIdUser()), data.response.accounts);
            }
          }, throwable -> {
            Timber.e("ERROR " + throwable);
          }, () -> {
          }));
    } else {
      compositeSubscription.add(dataManager.lastconnect_cashflow_account(account_id,
          session.getLastConnectAccount(account_id) / 1000)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(res -> {
            if (res.status.equalsIgnoreCase("success")) {
              dataManager.saveAccount(Integer.valueOf(session.getIdUser()), res.response.accounts);
            }
          }, throwable -> {
            Timber.e("ERROR " + throwable);
          }, () -> {
          }));
    }
  }

  private void subscriptionGetList(int account_id) {
    compositeSubscription.add(getList(account_id).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(products -> getMvpView().setListProduct(products),
            throwable -> Timber.e("ERROR " + throwable), () -> {
            }));
  }

  private Observable<Account> getAccount(int account_id) {
    Account account = db.getAccountById(account_id, 2, Integer.valueOf(session.getIdUser()));
    account.setName(mcrypt.decrypt(account.getName()));
    return Observable.just(account);
  }

  private Observable<ArrayList<Product>> getList(int account_id) {
    ArrayList<Product> products = db.getAllProductByAccount(account_id);
    for (Product product : products) {
      product.setName(mcrypt.decrypt(product.getName()));
    }
    return Observable.just(products);
  }

  public void deleteAccount(int id_account, DeleteUtils.OnDeleteListener deleteListener) {
    ProgressDialog dialog = new ProgressDialog(context);
    RxBus rxBus = MyCustomApplication.getRxBus();
    dialog.setMessage("Menghapus....");
    dialog.show();
    compositeSubscription.add(dataManager.deleteAccount(id_account)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          Timber.e("id account " + id_account);
          Account account = db.getAccountById(id_account, 2, Integer.valueOf(session.getIdUser()));
          if (data.status.equalsIgnoreCase("success")) {
            rxBus.send(new AddAccountEvent("complete"));
            db.deleteAccountByID(id_account);
            if (deleteListener != null) {
              deleteListener.onDoneRemoving();
            }
            if (account != null) {
              if (account.getIdvendor() == 10) {
                session.removeHaveInstitutionAccount();
              }
            }
          } else {
            if (data.message.equalsIgnoreCase("Account is not found")) {
              rxBus.send(new AddAccountEvent("complete"));
              db.deleteAccountByID(id_account);
              if (deleteListener != null) {
                deleteListener.onDoneRemoving();
              }
            }
          }
        }, throwable -> {
          dialog.dismiss();
        }, () -> {
          dialog.dismiss();
        }));
  }

  public void getDataBank(int account_id, int selectedMonth, int selectedYear) {
    compositeSubscription.add(
        dataManager.getAccountByMontYear(account_id, selectedMonth, selectedYear)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(data -> {
              if (data.status.equalsIgnoreCase("success")) {
                getMvpView().successGetData();
                ArrayList<account> accounts = new ArrayList<account>();
                accounts.add(data.response.accounts);
                dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accounts);
              } else {
                getMvpView().errorMessage(data.message);
              }
            }, throwable -> {
              Timber.e("ERROR " + throwable);
            }, () -> {
            }));
  }
}
