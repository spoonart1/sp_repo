package javasign.com.dompetsehat.presenter.comission;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.ReferralFee;
import javasign.com.dompetsehat.models.json.referral_fee;
import javasign.com.dompetsehat.models.response.ReferralFeeHistoryDetail;
import javasign.com.dompetsehat.models.response.ReferralMamiFeeResponse;
import javasign.com.dompetsehat.ui.fragments.comission.pojo.Comission;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.NetworkUtil;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 10/25/16.
 */

public class ComissionPresenter extends BasePresenter<ComissionInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  public final SessionManager session;
  private DbHelper db;
  private MCryptNew mCryptNew = new MCryptNew();
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private ProgressDialog dialog;
  private int range_bulan = -5;

  @Inject public ComissionPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(ComissionInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void grabData() {
    if (session.ihaveAnInstutitionAccount()) {
      Calendar calendar = Calendar.getInstance();
      Date dateEnd = calendar.getTime();
      calendar.setTimeInMillis(0L);
      Date dateStart = calendar.getTime();
      if (NetworkUtil.getConnectivityStatus(context) > 0) {
        compositeSubscription.add(dataManager.getComission(dateStart, dateEnd)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(responseBody -> {
              try {
                String json = responseBody.string();
                setData(json);
              } catch (Exception e) {
                Timber.e("ERROR " + e);
                getMvpView().setError();
              }
            }, throwable -> {
              Timber.e("ERROR " + throwable);
              getMvpView().setError();
            }, () -> {
              saveReferralFee();
            }));
      } else {
        String json = session.getLastComission();
        if (!TextUtils.isEmpty(json)) {
          setData(json);
        }
        getMvpView().showSnackbar();
      }
    } else {
      getMvpView().setMoney(0, 0, 0);
      getMvpView().setListComission(new ArrayList<>(), false);
    }
  }

  private void saveReferralFee() {
    Account account = db.getAccountById(10, 1, Integer.valueOf(session.getIdUser()));
    compositeSubscription.add(dataManager.saveReferralFee(account.getIdaccount())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          for (referral_fee f : data.response)
            db.updateRefferalFee(new ReferralFee(f));
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  private void setData(String json) {
    try {
      session.saveLastCommision(json);
      ReferralMamiFeeResponse responseArray =
          (ReferralMamiFeeResponse) gsonUtil(json, ReferralMamiFeeResponse.class);
      if (responseArray != null) {
        populateData(responseArray);
      } else {
        getMvpView().setError();
      }
    } catch (Exception e) {
      Timber.e("ERROR " + e);
      getMvpView().setError();
    }
  }

  public Object gsonUtil(String json, Class classOfT) {
    try {
      return new Gson().fromJson(json, classOfT);
    } catch (Exception e) {
      return null;
    }
  }

  public void populateData(ReferralMamiFeeResponse response) {
    double total = 0;
    double pending = 0;
    double payed = 0;
    ArrayList<ReferralFeeHistoryDetail> referralFeeDetails = new ArrayList<>();
    for (ReferralMamiFeeResponse.Data.ReferralFeeDetails.ReferralFeeDetail referralFeeDetail : response.data.ReferralFeeDetails.ReferralFeeDetail) {
      total += referralFeeDetail.TotalFee;
      pending += referralFeeDetail.Pending;
      payed += referralFeeDetail.TotalSubcription;
      int index = 0;
      Collections.reverse(referralFeeDetail.ReferralFeeHistoryDetails.ReferralFeeHistoryDetail);
      for (ReferralFeeHistoryDetail rf : referralFeeDetail.ReferralFeeHistoryDetails.ReferralFeeHistoryDetail) {
        Timber.e("avesina " + index);
        Timber.e("avesina count " + referralFeeDetails.size());
        if (index != 0 && compareReferralFee(referralFeeDetails.get(index - 1), rf)) {
          referralFeeDetails.remove(index - 1);
          index--;
        } else {
          referralFeeDetails.add(rf);
          index++;
        }
      }
    }
    Collections.reverse(referralFeeDetails);
    int max = referralFeeDetails.size();
    getMvpView().setMoney(total, pending, payed);
    if (max > 10) {
      max = 10;
    }
    listData(referralFeeDetails.subList(0, max));
  }

  private boolean compareReferralFee(ReferralFeeHistoryDetail refBefore,
      ReferralFeeHistoryDetail refAfter) {
    boolean cek1 = refBefore.FeeDate.equalsIgnoreCase(refAfter.FeeDate);
    boolean cek2 = Math.abs(refAfter.Amount) == Math.abs(refBefore.Amount);
    boolean cek3 = refBefore.Amount > refAfter.Amount || refBefore.Amount < refAfter.Amount;
    return cek1 && cek2 && cek3;
  }

  //public void populateData(ReferralMamiFeeResponseArray response) {
  //  double total = response.data.ReferralFeeDetails.ReferralFeeDetail.TotalFee;
  //  double pending = response.data.ReferralFeeDetails.ReferralFeeDetail.Pending;
  //  double payed = response.data.ReferralFeeDetails.ReferralFeeDetail.TotalSubcription;
  //  getMvpView().setMoney(total, pending, payed);
  //  List<ReferralFeeHistoryDetail> referralFeeDetails  = response.data.ReferralFeeDetails.ReferralFeeDetail.ReferralFeeHistoryDetails.ReferralFeeHistoryDetailArray;
  //  int max = referralFeeDetails.size();
  //  Timber.e("max data "+max);
  //  if(max > 10){
  //    max = 10;
  //  }
  //  listData(referralFeeDetails.subList(0,max));
  //}

  private void listData(List<ReferralFeeHistoryDetail> list) {
    ArrayList<Comission> comissions = new ArrayList<>();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", State.getLocale());
    compositeSubscription.add(Observable.from(list)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(referralFeeHistoryDetail -> {
          double Debet = 0;
          double AmountC = 0;
          Comission.Status status = Comission.Status.subscribed;
          if (referralFeeHistoryDetail.Status.equalsIgnoreCase("D")) {
            Debet = referralFeeHistoryDetail.Amount;
          } else {
            status = Comission.Status.komisi;
            AmountC = referralFeeHistoryDetail.Amount;
          }
          String dt = "";
          try {
            Date date = df.parse(referralFeeHistoryDetail.FeeDate);
            dt = format.format(date);
          } catch (Exception e) {

          }
          comissions.add(generateComission(Debet, AmountC, status, dt));
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          getMvpView().setError();
        }, () -> {
          getMvpView().setListComission(comissions, true);
        }));
  }

  private Comission generateComission(double amountDebet, double amountComission,
      Comission.Status from, String date) {
    Comission comission = new Comission();
    comission.debetAmount = amountDebet;
    comission.comissionAmount = amountComission;
    comission.from = from;
    comission.date = date;

    return comission;
  }
}
