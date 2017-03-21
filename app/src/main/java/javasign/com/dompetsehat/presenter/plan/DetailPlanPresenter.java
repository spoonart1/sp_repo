package javasign.com.dompetsehat.presenter.plan;

import android.content.Context;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.DbHelper;
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
 * Created by avesina on 10/6/16.
 */

public class DetailPlanPresenter extends BasePresenter<DetailPlanInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private Context context;
  private DataManager dataManager;
  private SessionManager session;
  private DbHelper db;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  MCryptNew mCryptNew = new MCryptNew();

  @Inject public DetailPlanPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(DetailPlanInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadData(int id_plan_local) {
    compositeSubscription.add(getPlan(id_plan_local).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(plan -> {
          Object dana = null;
          switch (plan.getType()) {

          }
          getMvpView().setPlan(plan, dana);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));

    compositeSubscription.add(getTransactionByPlan(id_plan_local).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(ids -> {
          if(ids != null){
            getMvpView().setTransactions(ids);
          }
        }, throwable -> {
        }, () -> {
        }));
  }

  private Observable<ArrayList<Integer>> getTransactionByPlan(int plan_id) {
    Plan plan = db.getPlanBy(db.PLAN_ID_LOCAL, plan_id);
    ArrayList<Integer> integers = new ArrayList<>();
    if (plan.isConnected()) {
      List<Cash> cashes =
          db.getCashflowByProduct(Integer.valueOf(session.getIdUser()), plan.getProduct_id());
      int id = 0;
      for (Cash cash : cashes) {
        integers.add(cash.getId());
      }
    }
    return Observable.just(integers);
  }

  public void cacthChangePlan() {
    compositeSubscription.add(rxBus.toObserverable()
        .ofType(AddPlanEvent.class)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(addPlanEvent -> {
          getMvpView().init();
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public Observable<Plan> getPlan(int id_plan_local) {
    Plan plan = db.getPlanBy(db.PLAN_ID_LOCAL, id_plan_local);
    switch (plan.getType()) {
      case DbHelper.PLAN_TYPE_PENSIUN:
        plan.danaPensiun = db.getDanaPensiunByPlan(plan.getId());
        break;
      case DbHelper.PLAN_TYPE_DARURAT:
        plan.danaDarurat = db.getDanaDaruratByPlan(plan.getId());
        break;
      case DbHelper.PLAN_TYPE_KULIAH:
        plan.danaKuliah = db.getDanaKuliahByPlan(plan.getId());
        break;
    }
    if (plan.isConnected()) {
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
      Account account = db.getAccountById(plan.getAccount_id(), 2, Integer.valueOf(session.getIdUser()));
      plan.account = account;
    }
    Calendar calendar = Calendar.getInstance();
    try {
      int tahun_sekarang = calendar.get(Calendar.YEAR);
      Timber.e("created " + plan.getCreated_at());
      calendar.setTime(DbHelper.df.parse(plan.getCreated_at()));
      int tahun_plan = calendar.get(Calendar.YEAR);
      plan.time = (plan.getLifetime() - (tahun_sekarang - tahun_plan));
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    }
    return Observable.just(plan);
  }
}
