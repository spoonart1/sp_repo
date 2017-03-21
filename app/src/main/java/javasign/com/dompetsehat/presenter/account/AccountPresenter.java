package javasign.com.dompetsehat.presenter.account;

import android.app.ProgressDialog;
import android.content.Context;

import android.text.Editable;
import android.widget.Toast;
import java.util.ArrayList;

import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.ui.event.AddAccountEvent;
import javasign.com.dompetsehat.ui.event.SelectProductEvent;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;

import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.ui.activities.account.pojo.CellAccount;
import javasign.com.dompetsehat.ui.fragments.account.adapter.NewManageAccountAdapter;
import javasign.com.dompetsehat.utils.RxBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 9/7/16.
 */

public class AccountPresenter extends BasePresenter<AccountInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper;
  private DbHelper db;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  String[] dompet = null;
  String[] banks = new String[] { "Mandiri", "BCA", "Permata", "Mandiri Syariah" };
  String[] merchant = new String[] { "Tokopedia" };
  String[] invests = new String[] { "Manulife", "Prudential" };

  public static final int SECTION_DOMPET = 0;
  public static final int SECTION_BANK = 2;
  public static final int SECTION_INVESTMENT = 1;
  public static final int SECTION_MERCHANT = 3;
  String[] labels = new String[] { "Manual", "Investasi", "Bank", "Lainnya" };
  String[] color_array;
  ProgressDialog dialog;

  @Inject
  public AccountPresenter(@ActivityContext Context context, DataManager dataManager, DbHelper db,
      GeneralHelper helper) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    this.helper = helper;
    color_array = context.getResources().getStringArray(R.array.color_array);
    dialog = new ProgressDialog(context);

    init();
  }

  private void init() {
    rxBus.toObserverable()
        .ofType(AddAccountEvent.class)
        .subscribe(new SimpleObserver<AddAccountEvent>() {
          @Override public void onNext(AddAccountEvent addAccountEvent) {
            loadData();
          }
        });
  }

  @Override public void attachView(AccountInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadAddData() {
    compositeSubscription.add(ObservableGetListVendor().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(vendorParents -> {
          getMvpView().setAdapter(vendorParents);
        }, throwable -> {
          Timber.e("ERRPOR " + throwable);
        }, () -> {
        }));
  }

  public void loadSimpleData() {
    compositeSubscription.add(ObservableGetSimpleList().subscribeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(cellAccounts -> {
          getMvpView().setSimpleAdapter(cellAccounts);
        }));
  }

  public void loadData() {
    compositeSubscription.add(ObservableGetTotalSado().subscribeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(totalSaldo -> {
          getMvpView().setTotalBalance(totalSaldo);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
    compositeSubscription.add(ObservableAccountCount().subscribeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(total -> {
          getMvpView().setTotalAccount(total);
        }, throwable -> {
        }, () -> {
        }));
    compositeSubscription.add(ObservableGetListAccountOnly().subscribeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(vendorParents -> {
          Timber.e("count " + vendorParents.size());
          getMvpView().setAdapter(vendorParents);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  private Observable<Double> ObservableGetTotalSado() {
    return Observable.just(db.getTotalSaldo(String.valueOf(session.getIdUser()), 0));
  }

  private Observable<Integer> ObservableAccountCount() {
    return Observable.just(db.getAccountCount(Integer.valueOf(session.getIdUser())));
  }

  private Observable<ArrayList<NewManageAccountAdapter.VendorParent>> ObservableGetListVendor() {
    ArrayList<CellAccount> productsBank = new ArrayList<>();
    ArrayList<CellAccount> productsDompet = new ArrayList<>();
    ArrayList<CellAccount> productsMerchant = new ArrayList<>();
    ArrayList<CellAccount> productsInvest = new ArrayList<>();
    ArrayList<Vendor> vendorArrayList = db.getAllVendors();
    for (Vendor vendor : vendorArrayList) {
      switch (AccountView.getSection(vendor.getId())) {
        case SECTION_BANK:
          productsBank.add(new CellAccount().setName(vendor.getVendor_name())
              .setColor(vendor.getId())
              .setType(SECTION_BANK)
              .setVendor(vendor)
              .setVendorId(vendor.getId()));
          break;
        case SECTION_DOMPET:
          String name = vendor.getVendor_name();
          if(vendor.getId() == 6){
            name = context.getString(R.string.cash);
          }
          productsDompet.add(new CellAccount().setName(vendor.getVendor_name())
              .setColor(vendor.getId())
              .setType(SECTION_DOMPET)
              .setVendor(vendor)
              .setVendorId(vendor.getId()));
          break;
        case SECTION_INVESTMENT:
          productsInvest.add(new CellAccount().setName(vendor.getVendor_name())
              .setColor(vendor.getId())
              .setType(SECTION_INVESTMENT)
              .setIcon(AccountView.iconVendor.get(vendor.getId()))
              .setVendor(vendor)
              .setVendorId(vendor.getId()));
          break;
        case SECTION_MERCHANT:
          productsMerchant.add(new CellAccount().setName(vendor.getVendor_name())
              .setColor(vendor.getId())
              .setType(SECTION_MERCHANT)
              .setVendor(vendor)
              .setVendorId(vendor.getId()));
          break;
      }
    }

    ArrayList<NewManageAccountAdapter.VendorParent> vendorParents = new ArrayList<>();
    for (int i = 0; i < labels.length; i++) {
      switch (i) {
        case SECTION_DOMPET:
          if (productsDompet.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent0 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_DOMPET], productsDompet);
          vendorParent0.setSection(i);
          vendorParents.add(vendorParent0);
          break;
        case SECTION_BANK:
          if (productsBank.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent1 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_BANK], productsBank);
          vendorParent1.setSection(i);
          vendorParents.add(vendorParent1);
          break;
        case SECTION_INVESTMENT:
          if (productsInvest.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent2 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_INVESTMENT], productsInvest);
          vendorParent2.setSection(i);
          vendorParents.add(vendorParent2);
          break;
        case SECTION_MERCHANT:
          if (productsMerchant.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent3 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_MERCHANT], productsMerchant);
          vendorParent3.setSection(i);
          vendorParents.add(vendorParent3);
          break;
      }
    }

    return Observable.just(vendorParents);
  }

  private Observable<ArrayList<CellAccount>> ObservableGetSimpleList() {
    ArrayList<CellAccount> cellAccountArrayList = new ArrayList<>();
    ArrayList<Product> products = db.getAllProductByUser(session.getIdUser());
    double total_bank = 0.0;
    for (Product product : products) {
      total_bank += product.getBalance();
      Account account =
          db.getAccountById(product.getAccount_id(), 2, Integer.valueOf(session.getIdUser()));
      Vendor v = db.getVendorsByID(account.getIdvendor());
      cellAccountArrayList.add(new CellAccount().setProduct(product)
          .setAccount(account)
          .setVendor(v)
          .setName(mcrypt.decrypt(product.getName()))
          .setColor(product.getColor())
          .setBalance(product.getBalance())
          .setIcon(AccountView.iconVendor.get(account.getIdvendor()))
          .setVendorId(account.getIdvendor()));
    }
    return Observable.just(cellAccountArrayList);
  }

  private Observable<ArrayList<NewManageAccountAdapter.VendorParent>> ObservableGetList() {
    // 1,2,3,4,8 Bank
    // 5 Merchant
    // 6 Dompet
    double total_bank = 0;
    double total_dompet = 0;
    double total_merchant = 0;
    double total_invest = 0;
    ArrayList<CellAccount> productsBank = new ArrayList<>();
    ArrayList<CellAccount> productsDompet = new ArrayList<>();
    ArrayList<CellAccount> productsMerchant = new ArrayList<>();
    ArrayList<CellAccount> productsInvest = new ArrayList<>();
    ArrayList<Product> products = new ArrayList<>();
    ArrayList<Account> accounts = db.getAllAccountByUser(session.getIdUser());
    for (Account account : accounts) {
      Vendor v = db.getVendorsByID(account.getIdvendor());
      switch (AccountView.getSection(account.getIdvendor())) {
        case SECTION_BANK:
          products = db.getAllProductByAccount(account.getIdaccount());
          for (Product product : products) {
            total_bank += product.getBalance();
            productsBank.add(new CellAccount().setProduct(product)
                .setAccount(account)
                .setName(mcrypt.decrypt(product.getName()))
                .setColor(product.getColor())
                .setBalance(product.getBalance())
                .setIcon(product.icon)
                .setType(SECTION_BANK)
                .setVendor(v)
                .setVendorId(account.getIdvendor()));
          }
          break;
        case SECTION_DOMPET:
          products = db.getAllProductByAccount(account.getIdaccount());
          for (Product product : products) {
            total_dompet += product.getBalance();
            Timber.e("akuuuu " + product.getId_product());
            productsDompet.add(new CellAccount().setProduct(product)
                .setAccount(account)
                .setName(mcrypt.decrypt(product.getName()))
                .setColor(product.getColor())
                .setBalance(product.getBalance())
                .setIcon(product.icon)
                .setType(SECTION_DOMPET)
                .setVendor(v)
                .setVendorId(account.getIdvendor()));
          }
          break;
        case SECTION_INVESTMENT:
          products = db.getAllProductByAccount(account.getIdaccount());
          for (Product product : products) {
            total_invest += product.getBalance();
            productsMerchant.add(new CellAccount().setProduct(product)
                .setAccount(account)
                .setName(mcrypt.decrypt(product.getName()))
                .setColor(product.getColor())
                .setBalance(product.getBalance())
                .setIcon(AccountView.iconVendor.get(account.getIdvendor()))
                .setType(SECTION_INVESTMENT)
                .setVendor(v)
                .setVendorId(account.getIdvendor()));
          }
          break;
        case SECTION_MERCHANT:
          products = db.getAllProductByAccount(account.getIdaccount());
          for (Product product : products) {
            total_merchant += product.getBalance();
            productsInvest.add(new CellAccount().setProduct(product)
                .setAccount(account)
                .setName(mcrypt.decrypt(product.getName()))
                .setColor(product.getColor())
                .setBalance(product.getBalance())
                .setIcon(product.icon)
                .setType(SECTION_MERCHANT)
                .setVendor(v)
                .setVendorId(account.getIdvendor()));
          }
          break;
      }
    }

    ArrayList<NewManageAccountAdapter.VendorParent> vendorParents = new ArrayList<>();
    for (int i = 0; i < labels.length; i++) {
      switch (i) {
        case SECTION_DOMPET:
          if (productsDompet.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent0 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_DOMPET], productsDompet);
          vendorParent0.setSaldo(total_dompet);
          vendorParent0.setSection(i);
          vendorParents.add(vendorParent0);
          break;
        case SECTION_BANK:
          if (productsBank.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent1 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_BANK], productsBank);
          vendorParent1.setSaldo(total_bank);
          vendorParent1.setSection(i);
          vendorParents.add(vendorParent1);
          break;
        case SECTION_INVESTMENT:
          if (productsInvest.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent2 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_INVESTMENT], productsInvest);
          vendorParent2.setSaldo(total_invest);
          vendorParent2.setSection(i);
          vendorParents.add(vendorParent2);
          break;
        case SECTION_MERCHANT:
          if (productsMerchant.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent3 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_MERCHANT], productsMerchant);
          vendorParent3.setSaldo(total_merchant);
          vendorParent3.setSection(i);
          vendorParents.add(vendorParent3);
          break;
      }
    }
    return Observable.just(vendorParents);
  }

  private Observable<ArrayList<NewManageAccountAdapter.VendorParent>> ObservableGetListAccountOnly() {
    // 1,2,3,4,8 Bank
    // 5 Merchant
    // 6 Dompet
    double total_bank = 0;
    double total_dompet = 0;
    double total_merchant = 0;
    double total_invest = 0;
    double total_balance = 0;
    ArrayList<CellAccount> productsBank = new ArrayList<>();
    ArrayList<CellAccount> productsDompet = new ArrayList<>();
    ArrayList<CellAccount> productsMerchant = new ArrayList<>();
    ArrayList<CellAccount> productsInvest = new ArrayList<>();
    ArrayList<Product> products = new ArrayList<>();
    ArrayList<Account> accounts = db.getAllAccountByUser(session.getIdUser());
    Timber.e("Here Count " + accounts.size());
    boolean sync = true;
    for (Account account : accounts) {
      Vendor v = db.getVendorsByID(account.getIdvendor());
      switch (AccountView.getSection(account.getIdvendor())) {
        case SECTION_BANK:
          total_balance = 0;
          products = db.getAllProductByAccount(account.getIdaccount());
          if (products.isEmpty()) {
            sync = false;
          }
          for (Product product : products) {
            total_balance += product.getBalance();
          }
          productsBank.add(new CellAccount().setAccount(account)
              .setIs_synced(sync)
              .setName(mcrypt.decrypt(account.getName()))
              .setColor(AccountView.accountColor.get(account.getIdvendor()))
              .setBalance(total_balance)
              .setIcon(AccountView.iconVendor.get(account.getIdvendor()))
              .setType(SECTION_BANK)
              .setVendor(v)
              .setVendorId(account.getIdvendor()));
          break;
        case SECTION_DOMPET:
          total_balance = 0;
          products = db.getAllProductByAccount(account.getIdaccount());
          if (products.isEmpty()) {
            sync = false;
          }
          for (Product product : products) {
            //db.updateSaldo(product.getId_product(), db.getSaldoReal(product.getId_product(), Integer.valueOf(session.getIdUser())));
            //total_balance += db.getSaldoReal(product.getId_product(), Integer.valueOf(session.getIdUser()));
            total_balance += product.getBalance();
          }
          productsDompet.add(new CellAccount().setAccount(account)
              .setIs_synced(sync)
              .setName(mcrypt.decrypt(account.getName()))
              .setColor(AccountView.accountColor.get(account.getIdvendor()))
              .setBalance(total_balance)
              .setIcon(AccountView.iconVendor.get(account.getIdvendor()))
              .setType(SECTION_DOMPET)
              .setVendor(v)
              .setVendorId(account.getIdvendor()));
          break;
        case SECTION_INVESTMENT:
          total_balance = 0;
          products = db.getAllProductByAccount(account.getIdaccount());
          if (products.isEmpty()) {
            sync = false;
          }
          for (Product product : products) {
            total_balance += product.getBalance();
          }
          productsInvest.add(new CellAccount().setAccount(account)
              .setIs_synced(sync)
              .setName(mcrypt.decrypt(account.getName()))
              .setColor(account != null ? (account.getIdvendor() > 0 ? AccountView.accountColor.get(
                  account.getIdvendor()) : 0) : 0)
              .setBalance(total_balance)
              .setIcon(AccountView.iconVendor.get(account.getIdvendor()))
              .setType(SECTION_INVESTMENT)
              .setVendor(v)
              .setVendorId(account.getIdvendor()));
          Timber.e("aku di sini ");
          break;
        case SECTION_MERCHANT:
          total_balance = 0;
          products = db.getAllProductByAccount(account.getIdaccount());
          if (products.isEmpty()) {
            sync = false;
          }
          for (Product product : products) {
            total_balance += product.getBalance();
          }
          productsMerchant.add(new CellAccount().setAccount(account)
              .setIs_synced(sync)
              .setName(mcrypt.decrypt(account.getName()))
              .setColor(AccountView.accountColor.get(account.getIdvendor()))
              .setBalance(total_balance)
              .setIcon(AccountView.iconVendor.get(account.getIdvendor()))
              .setType(SECTION_MERCHANT)
              .setVendor(v)
              .setVendorId(account.getIdvendor()));
          break;
      }
    }

    ArrayList<NewManageAccountAdapter.VendorParent> vendorParents = new ArrayList<>();
    for (int i = 0; i < labels.length; i++) {
      switch (i) {
        case SECTION_DOMPET:
          if (productsDompet.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent0 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_DOMPET], productsDompet);
          vendorParent0.setSaldo(total_dompet);
          vendorParent0.setSection(i);
          vendorParents.add(vendorParent0);
          break;
        case SECTION_BANK:
          if (productsBank.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent1 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_BANK], productsBank);
          vendorParent1.setSaldo(total_bank);
          vendorParent1.setSection(i);
          vendorParents.add(vendorParent1);
          break;
        case SECTION_INVESTMENT:
          if (productsInvest.isEmpty()) break;
          Timber.e("productsInvest" + productsInvest.size());
          NewManageAccountAdapter.VendorParent vendorParent2 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_INVESTMENT], productsInvest);
          vendorParent2.setSaldo(total_invest);
          vendorParent2.setSection(i);
          vendorParents.add(vendorParent2);
          break;
        case SECTION_MERCHANT:
          if (productsMerchant.isEmpty()) break;
          NewManageAccountAdapter.VendorParent vendorParent3 =
              new NewManageAccountAdapter.VendorParent(labels[SECTION_MERCHANT], productsMerchant);
          vendorParent3.setSaldo(total_merchant);
          vendorParent3.setSection(i);
          vendorParents.add(vendorParent3);
          break;
      }
    }
    return Observable.just(vendorParents);
  }



  public void deleteAccount(int id_account, DeleteUtils.OnDeleteListener deleteListener) {
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

  public void selectProduct(Product product) {
    rxBus.send(new SelectProductEvent(product));
  }

  public void renameAccount(Account account, String text) {
    dialog.setMessage("Mengupdate....");
    dialog.show();
    compositeSubscription.add(dataManager.renameAccount(account.getIdaccount(), text)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          if(data.status.equalsIgnoreCase("success")){
            ArrayList<javasign.com.dompetsehat.models.json.account> accounts = new ArrayList<>();
            accounts.add(data.response.account);
            dataManager.saveAccount(Integer.valueOf(session.getIdUser()),accounts);
            loadData();
          }else{
            dialog.dismiss();
          }
        }, throwable -> {
          Timber.e("ERROR "+throwable);
          dialog.dismiss();
        }, () -> {
          dialog.dismiss();
        }));
  }
}
