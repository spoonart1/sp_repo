package javasign.com.dompetsehat.presenter.account;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.product;
import javasign.com.dompetsehat.services.AccountBankSyncService;
import javasign.com.dompetsehat.services.DompetSehatService;
import javasign.com.dompetsehat.services.ErrorUtils;
import javasign.com.dompetsehat.ui.event.AddAccountEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 10/19/16.
 */

public class AddAccountBankPresenter extends BasePresenter<AddAccountInterface> {
  private Context context;
  private DataManager dataManager;
  private SessionManager session;
  private DbHelper db;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  public RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
  private DompetSehatService service;
  private MCryptNew mCryptNew = new MCryptNew();
  private RxBus rxBus = MyCustomApplication.getRxBus();

  @Inject public AddAccountBankPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db, DompetSehatService service) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    this.service = service;
  }

  @Override public void attachView(AddAccountInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loginBank(int vendor_id, String username, String password, String name) {
    String m_username = mCryptNew.encrypt(username);
    String m_password = mCryptNew.encrypt(password);
    String m_name = mCryptNew.encrypt(name);
    getMvpView().startLoading();
    compositeSubscription.add(service.create_account(vendor_id, m_username, m_password, m_name)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(createAccountResponse -> {
          if (createAccountResponse.status.equalsIgnoreCase("fail")) {
            getMvpView().showMessage(createAccountResponse.message);
          } else {
            ArrayList<account> accounts = new ArrayList<>();
            accounts.add(createAccountResponse.response.account);
            dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accounts);
            double saldo = 0;
            for (product p:createAccountResponse.response.account.products){
              saldo += p.balance;
            }
            getMvpView().finishCreatedAccount(new Account(createAccountResponse.response.account),saldo,
                createAccountResponse.message);
            rxBus.send(new AddAccountEvent("complete"));
            Helper.trackThis(context, "user berhasil login manulife");
            context.startService(new Intent(context, AccountBankSyncService.class).putExtra(
                AccountBankSyncService.ACCOUNT_ID, createAccountResponse.response.account.id));
          }
        }, throwable -> {
          getMvpView().stopLoading();
          String[] error = ErrorUtils.getErrorUserMessage(throwable);
          getMvpView().showMessage(Helper.combinePlural(error));
        }, () -> {
          getMvpView().stopLoading();
          rxBus.send(new AddAccountEvent("complete"));
          Helper.trackThis(context, "User berhasil login bank");
        }));
  }

  public void loadAccount(int anInt) {
    compositeSubscription.add(getAccount(anInt).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(account -> {
          String username = mCryptNew.decrypt(account.getUsername());
          List<Product> products = db.getAllProductByAccount(anInt);
          double saldo = 0.0;
          for (Product p : products) {
            saldo += p.getBalance();
          }
          getMvpView().setDataAccount(username, saldo);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public Observable<Account> getAccount(int anInt) {
    return Observable.defer(() -> {
      Account account = db.getAccountById(anInt, 2, Integer.valueOf(session.getIdUser()));
      return Observable.just(account);
    });
  }

  public void ubahBank(int vendor, Integer id_account, String username, String password) {
    getMvpView().startLoading();
    compositeSubscription.add(dataManager.updateAccount(id_account, username, password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {
          if (response.status.equalsIgnoreCase("success")) {
            ArrayList<account> accounts = new ArrayList<account>();
            accounts.add(response.response.account);
            dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accounts);
            getMvpView().finishUpdateAccount(response.message);
          } else {
            getMvpView().stopLoading();
          }
        }, throwable -> {
          getMvpView().stopLoading();
          Timber.e("ERROR " + throwable);
        }, () -> {
          rxBus.send(new AddAccountEvent("complete"));
        }));
  }

  public void syncAccount(int id) {
    getMvpView().startLoading();
    compositeSubscription.add(dataManager.getCashFlowAccount(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          int total = 0;
          ArrayList<Integer> ids = new ArrayList<Integer>();
          for (account a:data.response.accounts){
            for(product p:a.products){
              ids.add(p.id);
              Timber.e("Heloo "+p.id);
              if(p.cashflow != null) {
                total += p.cashflow.size();
              }
            }
          }
          getMvpView().setCountTransaction((Integer[]) ids.toArray(new Integer[]{}),total);
          dataManager.saveAccount(Integer.valueOf(session.getIdUser()),data.response.accounts);
        }, throwable -> {
          getMvpView().stopLoading();
          Timber.e("ERROR "+throwable);
        }, () -> {
          getMvpView().stopLoading();
        }));
  }
}
