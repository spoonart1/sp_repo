package javasign.com.dompetsehat.presenter.debts;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import java.util.ArrayList;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Debt;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.json.debt;
import javasign.com.dompetsehat.presenter.search.SearchInterface;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 2/17/17.
 */

public class DebtsPresenter extends BasePresenter<DebtsInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final Context context;
  public final DbHelper db;
  public final SessionManager session;
  private MCryptNew mCryptNew = new MCryptNew();

  @Inject
  public DebtsPresenter(@ActivityContext Context context, DataManager dataManager, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(DebtsInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadData(String type) {
    final double[] total = { 0.0 };
    ArrayList<Debt> debts = new ArrayList<>();
    compositeSubscription.add(dataManager.getDebts(type)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          for (debt d : data.response.debts) {
            total[0] += d.amount;
            Debt _d = new Debt(d);
            Cash c = db.getCashByCashflowId(d.cashflow_id);
            Product p = db.getProductBy(DbHelper.TAB_PRODUCT, d.product_id);
            _d.setCash(c);
            if(p!= null) {
              p.setName(mCryptNew.decrypt(p.getName()));
            }
            _d.setProduct(p);
            debts.add(_d);
          }
          getMvpView().setAdapter(debts);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().setTotal(total[0]);
        }));
  }

  public void initSpinner() {
    ArrayList<Product> products = db.getAllProductByUser(session.getIdUser());
    ArrayList<String> labels = new ArrayList<>();
    compositeSubscription.add(Observable.from(products)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          String name = mCryptNew.decrypt(data.getName());
          if (!TextUtils.isEmpty(data.getNumber())) {
            name = mCryptNew.decrypt(data.getNumber());
            name = name.substring(name.length() - 5, name.length());
          }
          Account account = db.getAccountById(data.getAccount_id(), 2, Integer.valueOf(session.getIdUser()));
          String account_name = mCryptNew.decrypt(account.getName());
          labels.add(account_name + " - " + name);
        }, throwable -> {
          Timber.e("ERRROR "+throwable);
        }, () -> {
          getMvpView().setSpinnerProduct(products, labels.toArray(new String[0]));
        }));
  }

  //AccountManager am = AccountManager.get(context);
  //if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS)
  //    != PackageManager.PERMISSION_GRANTED) {
  //  // TODO: Consider calling
  //  //    ActivityCompat#requestPermissions
  //  // here to request the missing permissions, and then overriding
  //  //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
  //  //                                          int[] grantResults)
  //  // to handle the case where the user grants the permission. See the documentation
  //  // for ActivityCompat#requestPermissions for more details.
  //  return;
  //}
  //android.accounts.Account[] acc = am.getAccounts();

  public void initSpinner(Product product, String type) {
    if (product != null) {
      ArrayList<Cash> cashes = db.getCashbyProduct(product.getId_product(), type);
      ArrayList<String> labels = new ArrayList<>();
      compositeSubscription.add(Observable.from(cashes)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(data -> {
            if (!TextUtils.isEmpty(data.getDescription())) {
              labels.add(data.getDescription());
            } else {
              labels.add(data.getCategory().toString());
            }
          }, throwable -> {
          }, () -> {
            getMvpView().setSpinnerCashflow(cashes, labels.toArray(new String[0]));
          }));
    }
  }

  public void addDebt(int selectedMode, String selectedType, Product selectedProduct,
      Cash selectedCash, String name, int amount, String date, String payback, String email) {
    Integer cash_id = null;
    if (selectedCash != null) {
      cash_id = selectedCash.getCash_id();
    }
    ProgressDialog dialog = new ProgressDialog(context);
    dialog.setMessage("Menambahkan hutang...");
    dialog.show();
    compositeSubscription.add(
        dataManager.createDebt(selectedType, selectedMode, name, amount, cash_id,
            selectedProduct.getId_product(), date, payback, email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(debtsResponse -> {
              getMvpView().snackBar(debtsResponse.message);
              if(debtsResponse.status.equalsIgnoreCase("success")) {
                if(debtsResponse.response.alarm != null) {
                  Alarm a = new Alarm(debtsResponse.response.alarm);
                  db.updateAlarm(a);
                }
              }
            }, throwable -> {
              dialog.dismiss();
            }, () -> {
              dialog.dismiss();
              getMvpView().finish();
            }));
  }

  public void deleteDebt(int id) {
    ProgressDialog dialog = new ProgressDialog(context);
    dialog.setMessage("Mendelete hutang...");
    dialog.show();
    compositeSubscription.add(dataManager.deleteDebt(id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(parentResponse -> {
          getMvpView().snackBar(parentResponse.message);
        }, throwable -> {
          dialog.dismiss();
        }, () -> {
          dialog.dismiss();
          getMvpView().onRefresh();
        }));
  }
}
