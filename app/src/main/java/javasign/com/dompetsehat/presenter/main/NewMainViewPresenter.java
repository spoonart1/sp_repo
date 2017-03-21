package javasign.com.dompetsehat.presenter.main;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.product;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.event.ChangeProfile;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;

import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.services.InsertDataService;
import javasign.com.dompetsehat.ui.CommonInterface;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 8/12/16.
 */

public class NewMainViewPresenter extends BasePresenter<NewMainViewInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  DbHelper db;
  @Inject RxBus rxBus = MyCustomApplication.getRxBus();

  int H = 0;
  int W = 0;

  DatabaseReference mRef;

  @Inject public NewMainViewPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db, FirebaseDatabase mDatabase) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    mRef = mDatabase.getReference("notification");
  }

  @Override public void attachView(NewMainViewInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void catchProfile(Observer<ChangeProfile> onChange) {
    compositeSubscription.add(
        rxBus.toObserverable().ofType(ChangeProfile.class).subscribe(onChange));
  }

  public void getCashflow(int requestid) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(dataManager.getCashFlow()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cashFlowResponse -> {
          int total = 0;
          if (cashFlowResponse.response != null) {
            for (account a : cashFlowResponse.response.accounts) {
              for (product p : a.products) {
                total += p.cashflow.size();
              }
            }
            getMvpView().changeNotif(context.getString(R.string.dialog_loading), total);
            Intent i = new Intent(this.context, InsertDataService.class);
            Gson gson = new Gson();
            session.bindData(gson.toJson(cashFlowResponse));
            i.putExtra(helper.FLAG, helper.SENDJSONREQUESTREFRESH3);
            this.context.startService(i);
          }else {
            getMvpView().onError(State.UNDEFINED);
          }
        }, throwable -> {
          Timber.d(throwable, "Error loading data, server error");
          if(throwable instanceof HttpException) {
            getMvpView().onError(State.ERROR_SERVER);
          }else{
            getMvpView().onError(State.ERROR_SCRIPT);
          }
        }, () -> {
          getMvpView().onComplete(requestid);
        }));
  }

  public void getCashflow(int requestid, long last_sync) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(dataManager.getCashFlow(last_sync)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cashFlowResponse -> {
          int total = 0;
          if (cashFlowResponse.response != null) {
            for (account a : cashFlowResponse.response.accounts) {
              for (product p : a.products) {
                total += p.cashflow.size();
              }
            }
            getMvpView().changeNotif(context.getString(R.string.dialog_loading), total);
            Intent i = new Intent(this.context, InsertDataService.class);
            Gson gson = new Gson();
            session.bindData(gson.toJson(cashFlowResponse));
            i.putExtra(helper.FLAG, helper.SENDJSONREQUESTREFRESH3);
            this.context.startService(i);
          }
          else {
            getMvpView().onError(State.UNDEFINED);
          }
        }, throwable -> {
          Timber.d(throwable, "Error loading data, server error");
          if(throwable instanceof HttpException) {
            getMvpView().onError(State.ERROR_SERVER);
          }else{
            getMvpView().onError(State.ERROR_SCRIPT);
          }
        }, () -> {
          getMvpView().onComplete(requestid);
        }));
  }

  public void exportToPDF(NewMainActivityMyFin activity) {
    compositeSubscription.add(createBitmap(activity).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(bitmap -> {
          if (bitmap != null) {
            Helper.exportPDF(bitmap, H, W);
          } else {
            Toast.makeText(this.context, context.getString(R.string.export_failed),
                Toast.LENGTH_LONG).show();
          }
        }, throwable -> {
          getMvpView().onError(1);
        }, () -> {
          getMvpView().onComplete(1);
          Helper.openPDFFiles(this.context);
        }));
  }

  public void exportToCSV() {
    compositeSubscription.add(getListCash().observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(cashes -> {
          String data = Helper.ArrayToCSV(cashes);
          Helper.writeToFile(data);
        }, throwable -> getMvpView().onError(2), () -> {
          getMvpView().onComplete(2);
          Timber.e("open file");
          Helper.openFileCSV(this.context);
        }));
  }

  private Observable<ArrayList<Cash>> getListCash() {
    return Observable.just(db.getCash(session.getIdUser()));
  }

  private Observable<Bitmap> createBitmap(NewMainActivityMyFin activity) {
    NestedScrollView z = (NestedScrollView) (activity.findViewById(R.id.scroll));
    RecyclerView r = (RecyclerView) activity.findViewById(R.id.recycleview);
    Bitmap bitmap = null;
    if (z != null) {
      H = z.getChildAt(0).getHeight();
      W = z.getChildAt(0).getWidth();
      bitmap = Helper.getBitmapFromView(z, H, W);
    } else if (r != null) {
      H = r.getRootView().getHeight();
      W = r.getRootView().getWidth();
      bitmap = Helper.getBitmapFromView(r.getRootView(), H, W);
    }
    return Observable.just(bitmap);
  }

  public void initSpinner() {
    ArrayList<String> labels = new ArrayList<>();
    labels.add(context.getString(R.string.all_account));
    ArrayList<Account> accounts = db.getAllAccountByUser(session.getIdUser());
    ArrayList<Account> newAccounts = new ArrayList<>();
    compositeSubscription.add(Observable.from(accounts)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          if (data.getIdvendor() != 10) {
            newAccounts.add(data);
            labels.add(mcrypt.decrypt(data.getName()));
          }
        }, throwable -> {
        }, () -> {
          getMvpView().populateSpinner(newAccounts, labels.toArray(new String[0]));
        }));
  }

  public void initNotif(int tab) {
    compositeSubscription.add(RxFirebaseDatabase.observeValueEvent(
        mRef.child(session.getIdUser()).orderByChild("status").startAt("unread"))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(dataSnapshot -> {
          int count = (int)dataSnapshot.getChildrenCount();
          getMvpView().showNotif(count,tab);
        }, throwable -> {
          Timber.e("ERROR initNotif " + throwable);
        }, () -> {
        }));
  }
}
