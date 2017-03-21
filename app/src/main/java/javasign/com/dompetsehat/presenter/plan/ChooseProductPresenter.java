package javasign.com.dompetsehat.presenter.plan;

import android.content.Context;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.ProductPlan;
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
 * Created by avesina on 10/27/16.
 */

public class ChooseProductPresenter extends BasePresenter<ChooseProductInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  private DbHelper db;

  @Inject public ChooseProductPresenter(@ActivityContext Context context, DataManager dataManager, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(ChooseProductInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void initNonAdapter(int id_account) {
    compositeSubscription.add(ObservableGetNoNList(id_account).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(list -> {
          Account account = db.getAccountById(id_account, 2,Integer.valueOf(session.getIdUser()));
          List<Plan> ex_plans = db.getAllPlanByAccount(id_account);
          ArrayList<Integer> ex_plans_id = new ArrayList<Integer>();
          for(Plan plan:ex_plans){
            Timber.e("avesina mustari "+plan.getProduct_id());
            ex_plans_id.add(plan.getProduct_id());
          }
          getMvpView().getHeaderTextById(account.getIdvendor());
          getMvpView().setListNonIntitusi(account,list,ex_plans_id);
        }, throwable -> {
          Timber.e("ERROR "+throwable);
        }, () -> {
        }));
  }

  public void initAdapter(int id_account) {
    compositeSubscription.add(ObservableGetNoNList(id_account).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(list -> {
          Account account = db.getAccountById(id_account, 2,Integer.valueOf(session.getIdUser()));
          List<Plan> ex_plans = db.getAllPlanByAccount(id_account);
          ArrayList<Integer> ex_plans_id = new ArrayList<Integer>();
          boolean is_show = false;
          for(Plan plan:ex_plans){
            List<ProductPlan> planList = db.getPlanProduct(plan.getId());
            Timber.e("avesina mustari kui"+planList.size());
            for (ProductPlan p:planList){
              Timber.e("avesina mustari kui"+p.getProduct_id());
              ex_plans_id.add(p.getProduct_id());
            }
          }
          ArrayList<Product> products = db.getAllProductByAccount(account.getIdaccount());
          is_show = true;
          for(Product p:products){
            if(!ex_plans_id.contains(p.getId_product())){
              is_show = false;
              break;
            }
          }
          getMvpView().getHeaderTextById(account.getIdvendor());
          getMvpView().setListIntitusi(account,list,ex_plans_id,is_show);
        }, throwable -> {
          Timber.e("ERROR "+throwable);
        }, () -> {
        }));
  }

  //public Observable<List<AdapterInstitusi.Institusi>> ObservableGetList(int id_account) {
  //  List<Product> products = db.getAllProductByAccount(id_account);
  //  ArrayList<AdapterInstitusi.Institusi> institusis = new ArrayList<>();
  //  for(Product product:products){
  //    AdapterInstitusi.Institusi institusi = new AdapterInstitusi.Institusi();
  //    institusi.color = product.getColor();
  //    institusi.icon = product.icon;
  //    institusi.label = mcrypt.decrypt(product.getName());
  //    institusis.add(institusi);
  //  }
  //  return Observable.just(institusis);
  //}

  public Observable<List<Product>> ObservableGetNoNList(int id_account) {
    List<Product> products = db.getAllProductByAccount(id_account);
    Timber.e("avelina "+products.size());
    return Observable.just(products);
  }
}
