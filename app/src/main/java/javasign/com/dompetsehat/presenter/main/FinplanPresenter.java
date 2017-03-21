package javasign.com.dompetsehat.presenter.main;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.RxBus;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 9/14/16.
 */

public class FinplanPresenter extends BasePresenter<FinplanInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final SessionManager session;
  private DbHelper db;
  private MCryptNew mCryptNew = new MCryptNew();
  private RxBus rxBus = MyCustomApplication.getRxBus();

  @Inject public FinplanPresenter(@ActivityContext Context context, DataManager dataManager,DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(FinplanInterface mvpView) {
    super.attachView(mvpView);
    compositeSubscription.add(
        rxBus.toObserverable().ofType(AddPlanEvent.class).subscribe(addPlanActivity -> {
          loadData();
        }, throwable -> {
        }, () -> {
        }));
    //compositeSubscription.add(dataManager.sync_plan()
    //    .observeOn(AndroidSchedulers.mainThread())
    //    .subscribeOn(Schedulers.io())
    //    .subscribe(response -> {
    //      try {
    //        SyncPlanResponse syncPlanResponse =
    //            new Gson().fromJson(response.string(), SyncPlanResponse.class);
    //        if (syncPlanResponse.status.equals("OK")) {
    //          Timber.e(" response " + syncPlanResponse.response);
    //          for (javasign.com.dompetsehat.models.json.plan plan : syncPlanResponse.response.plan) {
    //
    //          }
    //        } else {
    //
    //        }
    //      } catch (Exception e) {
    //        Timber.e("SYNC salah");
    //      }
    //    }, throwable -> {
    //      Timber.e("SYNC salah");
    //    }, () -> {
    //    }));
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadData() {
    compositeSubscription.add(getList().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(plans -> {
          getMvpView().setAdapterPlan(plans);
          double total = 0.0;
          double saldo = 0.0;
          for (Plan plan : plans) {
            total += plan.getPlan_total();
            Timber.e("saldo "+plan.total_saldo+" versus "+plan.getPlan_total());
            if(plan.total_saldo <= plan.getPlan_total()) {
              saldo += plan.total_saldo;
            }else{
              saldo += plan.getPlan_total();
            }
            getMvpView().setCicularProgressBar(total, saldo);
          }
        }, throwable -> {
          Timber.e(" ERROR " + throwable);
        }, () -> {
        }));
  }

  private Observable<ArrayList<Plan>> getList() {
    ArrayList<Plan> plans = db.getAllPlan(session.getIdUser());
    int i = 0;
    for (Plan plan : plans) {
      if (plan.isConnected()) {
        Account account =
            db.getAccountById(plan.getAccount_id(), 2, Integer.valueOf(session.getIdUser()));
        Timber.e("plan product id " + plan.getProduct_id());
        if (plan.getProduct_id() > 0) {
          Product product = db.getProductBy(db.TAB_PRODUCT, plan.getProduct_id());
          if(product != null) {
            plan.product = product.setName(mCryptNew.decrypt(product.getName()));
            plan.total_saldo = db.getTotalSaldo(String.valueOf(product.getId_product()), 3);
          }
        } else {
          List<Product> products = db.getProductPlanByPlanLocalIDToProduct(plan.getId());
          plan.products = products;
          float total = 0;
          for (Product product : products) {
            total += product.getBalance();
          }
          plan.total_saldo = total;
        }
        if(account != null) {
          plan.account = account.setName(mCryptNew.decrypt(account.getName()));
        }
        Timber.e("total saldo " + plan.total_saldo);
        plans.set(i, plan);
      } else {
        plan.total_saldo = 0;
      }
      i += 1;
    }
    return Observable.just(plans);
  }

  public void deletePlan(int id, DeleteUtils.OnDeleteListener deleteListener) {
    compositeSubscription.add(Observable.just(db.softDeletePlanByID(id))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isDeleted -> {
          System.out.println("FinplanPresenter.deletePlan: sukses delete? "+isDeleted);
          if(deleteListener != null){
            deleteListener.onDoneRemoving();
          }
        }, throwable -> {
          Timber.e("ERROR Delete Plan", throwable.getLocalizedMessage());
        }, () -> {

        }));
  }
}
