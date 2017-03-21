package javasign.com.dompetsehat.presenter.portofolio;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.response.PortofolioResponse;
import javasign.com.dompetsehat.services.ErrorUtils;
import javasign.com.dompetsehat.ui.fragments.finplan.pojo.Portofolio;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.HEAD;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import javasign.com.dompetsehat.models.response.PortofolioResponse.Data.portfolio.investmentAccounts.*;

/**
 * Created by avesina on 10/25/16.
 */

public class PortofolioPresenter extends BasePresenter<PortofolioInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  public final SessionManager session;
  private DbHelper db;
  private MCryptNew mCryptNew = new MCryptNew();
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private ProgressDialog dialog;

  @Inject public PortofolioPresenter(@ActivityContext Context context, DataManager dataManager,
      SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(PortofolioInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void setAdapterDirectly() {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", State.getLocale());
    if (session.ihaveAnInstutitionAccount()) {
      syncAccount();
      compositeSubscription.add(dataManager.getPortofolio()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(response -> {
            ArrayList<Portofolio> portofolioList = new ArrayList<>();
            if (response.status.equalsIgnoreCase("success")) {
              String name =
                  (response.data.CliFirstNm == null ? "" : response.data.CliFirstNm) + " " + (
                      response.data.CliLastNm == null ? "" : response.data.CliLastNm);
              String cif = response.data.CifNum;
              getMvpView().setHeaderCif(cif, name);
              double total = 0;
              for (investmentAccount invest : response.data.Portfolio.InvestmentAccounts.InvestmentAccount) {
                total += invest.AccountBalance;
                String date = "";
                try {
                  Date dt = df.parse(invest.FundLastValuationDate);
                  date = format.format(dt);
                } catch (Exception e) {

                }
                portofolioList.add(
                    generatePortofolio(invest.FundName, invest.AccountBalance, invest.UnitOnHand,
                        invest.NavPerUnit, date, invest.FundCategory));
              }
            } else {
              getMvpView().showDialog(
                  db.getAccountById(AccountView.MNL, 1, Integer.valueOf(session.getIdUser())),
                  response.message);
            }
            getMvpView().setListPortofolio(portofolioList,session.ihaveAnInstutitionAccount());
          }, throwable -> {
            if (!throwable.toString().contains("BEGIN_OBJECT but was BEGIN_ARRAY")) {
              if (throwable instanceof HttpException) {
                //String m = Helper.combinePlural(ErrorUtils.getErrorUserMessage(throwable));
                getMvpView().showDialog(db.getAccountById(AccountView.MNL, 1, Integer.valueOf(session.getIdUser())),
                    "Akses di tolak masa berlaku login anda habis, kemungkinan kamu mengganti password klikMAMI, silahkan login ulang kembali ");
              } else {
                getMvpView().showDialog(
                    db.getAccountById(AccountView.MNL, 1, Integer.valueOf(session.getIdUser())),
                    throwable.getMessage());
                getMvpView().showSnackbar(throwable.toString());
              }
            }
            getMvpView().setListPortofolio(new ArrayList<>(),session.ihaveAnInstutitionAccount());
          }, () -> {
          }));
    } else {
      Timber.e("avesina keren");
      getMvpView().setListPortofolio(new ArrayList<>(),session.ihaveAnInstutitionAccount());
    }
  }

  public void syncAccount() {
    Account account = db.getAccountById(10,1,Integer.valueOf(session.getIdUser()));
    if(account != null) {
      compositeSubscription.add(dataManager.account_sync(account.getIdaccount())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(data -> {
            ArrayList<account> accounts = new ArrayList<account>();
            accounts.add(data.response.accounts);
            dataManager.saveAccount(Integer.valueOf(session.getIdUser()), accounts);
          }, throwable -> {
            Timber.e("ERROR " + throwable);
          }, () -> {
          }));
    }
  }

  public void setAdapter() {
    String token = session.getMamiToken();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", State.getLocale());
    if (session.ihaveAnInstutitionAccount()) {
      compositeSubscription.add(getPortofolio().subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(response -> {
            ArrayList<Portofolio> portofolioList = new ArrayList<>();
            if (response != null) {
              double total = 0;
              Timber.e("avesina " + response.CifNum);
              String name = (response.CliFirstNm == null ? "" : response.CliFirstNm) + " " + (
                  response.CliLastNm == null ? "" : response.CliLastNm);
              String cif = response.CifNum;
              Timber.e("name " + name);
              Timber.e("cif " + cif);
              getMvpView().setHeaderCif(cif, name);
              Account account = db.getAccountById(10, 1, Integer.valueOf(session.getIdUser()));
              if (account != null) {
                ArrayList<Product> products = db.getAllProductByAccount(account.getIdaccount());
                try {
                  for (Product product : products) {
                    if (!TextUtils.isEmpty(product.getProperties())) {
                      Timber.e("product " + product.getProperties());
                      PortofolioResponse.Data.portfolio.investmentAccounts.investmentAccount ins =
                          new Gson().fromJson(product.getProperties(),
                              PortofolioResponse.Data.portfolio.investmentAccounts.investmentAccount.class);
                      total += product.getBalance();
                      String date = "";
                      try {
                        Date dt = df.parse(ins.FundLastValuationDate);
                        date = format.format(dt);
                      } catch (Exception e) {

                      }
                      portofolioList.add(generatePortofolio(product.getName(),
                          Double.valueOf(product.getBalance()), ins.UnitOnHand, ins.NavPerUnit,
                          date, ins.FundCategory));
                    }
                  }
                } catch (Exception e) {
                  Timber.e("ERROR " + e);
                }
              }
            }
            getMvpView().setListPortofolio(portofolioList,session.ihaveAnInstutitionAccount());
          }, throwable -> {
            Timber.e("ERROR " + throwable);
            getMvpView().setListPortofolio(new ArrayList<>(),session.ihaveAnInstutitionAccount());
          }, () -> {
          }));
      getMvpView().showSnackbar(context.getString(R.string.app_need_connection));
    } else {
      getMvpView().setListPortofolio(new ArrayList<>(),session.ihaveAnInstutitionAccount());
    }
  }

  public Observable<PortofolioResponse.Data> getPortofolio() {
    Gson gson = new Gson();
    Account account = db.getAccountById(10, 1, Integer.valueOf(session.getIdUser()));
    try {
      if (account != null) {
        PortofolioResponse.Data data =
            gson.fromJson(account.getProperties(), PortofolioResponse.Data.class);
        return Observable.just(data);
      } else {
        return Observable.just(null);
      }
    } catch (Exception e) {
      Timber.e("ERROR " + e);
      return Observable.just(null);
    }
  }

  private Portofolio generatePortofolio(String title, double saldo, double jumlahUnit,
      double costPerUnit, String date, String category) {
    Portofolio p = new Portofolio();
    p.title = title;
    p.totalSaldoAkhir = saldo;
    p.jumlahUnit = jumlahUnit;
    p.costPerUnit = costPerUnit;
    p.date = date;
    p.category = category;
    return p;
  }
}
