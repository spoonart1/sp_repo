package javasign.com.dompetsehat.presenter.category;

import android.content.Context;

import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;

import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.ui.event.ChangeCategoryEvent;
import javasign.com.dompetsehat.utils.RxBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by aves on 9/1/16.
 */

public class AddCategoryPresenter extends BasePresenter<AddCategoryInterface> {

  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final Context context;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private DbHelper db;
  boolean FLAG_SETCATEGORY = true;
  SessionManager session;

  @Inject public AddCategoryPresenter(@ActivityContext Context context, DbHelper db,
      SessionManager session) {
    this.context = context;
    this.db = db;
    this.session = session;
    FLAG_SETCATEGORY = true;
  }

  @Override public void attachView(AddCategoryInterface mvpView) {
    super.attachView(mvpView);
    init();
    if (FLAG_SETCATEGORY) {
      FLAG_SETCATEGORY = false;
      getMvpView().setCategory(db.getCategoryByID(4));
    }
  }

  public void init() {
    compositeSubscription.add(rxBus.toObserverable()
        .ofType(ChangeCategoryEvent.class)
        .subscribe(new SimpleObserver<ChangeCategoryEvent>() {
          @Override public void onNext(ChangeCategoryEvent changeCategoryEvent) {
            if (changeCategoryEvent.dontFinishActivity) return;
            if (changeCategoryEvent.category != null) {
              getMvpView().setCategory(changeCategoryEvent.category);
            } else if (changeCategoryEvent.userCategory != null) {
              getMvpView().setUserCategory(changeCategoryEvent.userCategory);
            }
          }
        }));
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadCashflow(int cashflow_id) {
    compositeSubscription.add(getCashById(cashflow_id).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cash -> {
          getMvpView().setCashflow(cash);
        }, throwable -> {

        }, () -> {

        }));
  }

  private Observable<Cash> getCashById(int cashflow_id) {
    Cash cashflow = db.getCashByID(cashflow_id);
    cashflow.setCategory(db.getCategoryByID(cashflow.getCategory_id()));
    Product product = db.getProductBy(db.TAB_PRODUCT, cashflow.getProduct_id());
    product.setAccount(db.getAccountById(product.getAccount_id(), 2, Integer.valueOf(session.getIdUser())));
    cashflow.setProduct(product);
    return Observable.just(cashflow);
  }
}
