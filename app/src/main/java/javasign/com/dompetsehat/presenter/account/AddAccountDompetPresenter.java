package javasign.com.dompetsehat.presenter.account;

import android.app.ProgressDialog;
import android.content.Context;

import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.ui.event.AddAccountEvent;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javax.inject.Inject;

import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.models.json.product;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 9/9/16.
 */

public class AddAccountDompetPresenter extends BasePresenter<AddAccountInterface> {
  private Context context;
  private DataManager dataManager;
  private SessionManager session;
  private DbHelper db;
  private GeneralHelper helper;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  public RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
  RxBus rxBus = MyCustomApplication.getRxBus();
  private MCryptNew mCryptNew = new MCryptNew();
  ProgressDialog dialog;

  @Inject
  public AddAccountDompetPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db, GeneralHelper helper) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    this.helper = helper;
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

  public void init() {
    getMvpView().setBalance(db.getTotalSaldo(session.getIdUser(), 0));
  }

  public void saveDompet(int vendor_id, String name, int balance) {
    dialog = new ProgressDialog(context);
    dialog.setCancelable(false);
    dialog.setMessage("Sedang menambahkan dompet");
    dialog.show();
    compositeSubscription.add(dataManager.createDompet(vendor_id, name, balance)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(createAccountResponse -> {
          if (!createAccountResponse.status.equalsIgnoreCase("success")) {
            Toast.makeText(context, createAccountResponse.message, Toast.LENGTH_SHORT).show();
          } else {
            Account accountModel = new Account(createAccountResponse.response.account);
            db.newAccount(accountModel);
            double saldo = 0;
            for (product product : createAccountResponse.response.account.products) {
              saldo += product.balance;
              db.newProduct(new Product(product, createAccountResponse.response.account));
            }
            Vendor vendor = new Vendor(createAccountResponse.response.account.vendor);
            db.updateBank(vendor);
            syncAccount(accountModel.getIdaccount(),saldo,createAccountResponse.message);
          }
        }, throwable -> {
          dialog.dismiss();
          Timber.e(" ERROR " + throwable);
        }, () -> {
        }));
  }

  public void syncAccount(int id,double saldo,String message) {
    compositeSubscription.add(dataManager.getCashFlowAccount(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          dataManager.saveAccount(Integer.valueOf(session.getIdUser()),data.response.accounts);
          getMvpView().finishCreatedAccount(new Account(data.response.accounts.get(0)),
              saldo,message);
        }, throwable -> {
          Timber.e("ERROR "+throwable);
        }, () -> {
          dialog.dismiss();
          rxBus.send(new AddAccountEvent("complete"));
        }));
  }

  public void loadAccount(int anInt) {
    compositeSubscription.add(getAccount(anInt).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(account -> {
          List<Product> products = db.getAllProductByAccount(anInt);
          double saldo = 0.0;
          for (Product p : products) {
            saldo += p.getBalance();
          }
          getMvpView().setDataAccount(mCryptNew.decrypt(account.getName()), saldo);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public void ubahDompet(Integer id_account, String nickname, int balance) {
    getMvpView().startLoading();
    dialog = new ProgressDialog(context);
    dialog.setCancelable(false);
    dialog.setMessage("Sedang mengupdate dompet...");
    dialog.show();
    compositeSubscription.add(dataManager.updateDompet(id_account, nickname, balance)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {
          if (response.status.equalsIgnoreCase("success")) {
            ArrayList<account> accounts = new ArrayList<account>();
            accounts.add(response.response.account);
            dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accounts);
            getMvpView().finishUpdateAccount(response.message);
            dialog.dismiss();
          } else {
            dialog.dismiss();
            getMvpView().stopLoading();
          }
        }, throwable -> {
          dialog.dismiss();
          getMvpView().stopLoading();
          Timber.e("ERROR " + throwable);
        }, () -> {
          rxBus.send(new AddAccountEvent("complete"));
        }));
  }

  public Observable<Account> getAccount(int anInt) {
    return Observable.defer(() -> {
      Account account = db.getAccountById(anInt, 2, Integer.valueOf(session.getIdUser()));
      return Observable.just(account);
    });
  }
}
