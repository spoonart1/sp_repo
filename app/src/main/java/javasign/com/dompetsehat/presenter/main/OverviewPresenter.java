package javasign.com.dompetsehat.presenter.main;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.presenter.overview.OverviewInterface;
import javasign.com.dompetsehat.ui.fragments.overview_v2.OverviewFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment.OvCategoryFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment.OvNettIncomeFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment.OvTransactionFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvFragmentModel;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
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
 * Created by aves on 8/29/16.
 */

public class OverviewPresenter extends BasePresenter<OverviewInterface> {

  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  private ArrayList<Double> income;
  private ArrayList<Double> expense;
  private DbHelper db;
  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
  RxBus rxBus = MyCustomApplication.getRxBus();

  private String TOTAL_FROM_KEY = "total_from";
  private String TOTAL_TO_KEY = "total_to";
  int range_year = 5; // tahun
  String[] titles_tab;

  @Inject public OverviewPresenter(@ActivityContext Context context, DataManager dataManager,
      SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = session;
    this.db = db;
    titles_tab = context.getResources().getStringArray(R.array.overview_transactin_tab_labels);
  }

  @Override public void attachView(OverviewInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void init() {

  }

  public void populateDataNet(int filter) {
    compositeSubscription.add(getListFragmentNett(filter).observeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(overviewFragmentModels -> {
          getMvpView().setListOverviewFragmentModel(overviewFragmentModels);
        }));
  }

  public void populateDataDetail(int filter) {
    compositeSubscription.add(getListFragmentDetail(filter).observeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(overviewFragmentModels -> {
          getMvpView().setListOverviewFragmentModel(overviewFragmentModels);
        }));
  }

  public void populateDataCategory(int filter) {
    compositeSubscription.add(getListFragmentCategory(filter).observeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(overviewFragmentModels -> {
          getMvpView().setListOverviewFragmentModel(overviewFragmentModels);
        }));
  }

  public Observable<List<OvFragmentModel>> getListFragmentNett(int filter) {
    return Observable.defer(() -> {
      List<OvFragmentModel> fragmentModels = new ArrayList<>();
      Calendar calendar = Calendar.getInstance();
      switch (filter) {
        case OverviewFragment.FILTER_SINGLE_MONTHLY:
          fragmentModels.addAll(grabDatabyMonthNet(filter));
          break;
        case OverviewFragment.FILTER_GROUPLY:
          for (int i = 0; i < titles_tab.length; i++) {
            fragmentModels.add(new OvFragmentModel(titles_tab[i],
                new OvNettIncomeFragment().setPosition(i)
                    .setTabType(filter)
                    .setTabName(titles_tab[i])
                    .setOnItemClik((x, y) -> getMvpView().scrollView(x, y))));
          }
          break;
        case OverviewFragment.FILTER_SINGLE_YEARLY:
          int year_now = calendar.get(Calendar.YEAR);
          List<Integer> tahuns = db.getYearAllTransaction(session.getIdUser());
          int i = 0;
          for (Integer tahun : tahuns) {
            fragmentModels.add(new OvFragmentModel(String.valueOf(tahun), new OvNettIncomeFragment()
                .setPosition(i)
                .setTabType(filter)
                .setTahun(tahun)
                .setOnItemClik((x, y) -> getMvpView().scrollView(x, y))));
            i++;
          }
          break;
      }
      return Observable.just(fragmentModels);
    });
  }

  public Observable<List<OvFragmentModel>> getListFragmentDetail(int filter) {
    List<OvFragmentModel> fragmentModels = new ArrayList<>();
    switch (filter) {
      case OverviewFragment.FILTER_SINGLE_MONTHLY:
        fragmentModels.addAll(grabDatabyMonthDetail(filter));
        break;
      case OverviewFragment.FILTER_GROUPLY:
        for (int i = 0; i < titles_tab.length; i++) {
          fragmentModels.add(new OvFragmentModel(titles_tab[i],
              OvTransactionFragment.newInstance(titles_tab[i], filter)));
        }
        break;
      case OverviewFragment.FILTER_SINGLE_YEARLY:
        fragmentModels.addAll(grabDatabyYearDetail(filter));
        break;
    }
    return Observable.just(fragmentModels);
  }

  public Observable<List<OvFragmentModel>> getListFragmentCategory(int filter) {
    List<OvFragmentModel> fragmentModels = new ArrayList<>();
    switch (filter) {
      case OverviewFragment.FILTER_SINGLE_MONTHLY:
        fragmentModels.addAll(grabDatabyMonthCategory(filter));
        break;
      case OverviewFragment.FILTER_GROUPLY:
        for (int i = 0; i < titles_tab.length; i++) {
          fragmentModels.add(new OvFragmentModel(titles_tab[i],
              OvCategoryFragment.newInstance(titles_tab[i], filter)));
        }
        break;
      case OverviewFragment.FILTER_SINGLE_YEARLY:
        fragmentModels.addAll(grabDatabyYearCategory(filter));
        break;
    }
    return Observable.just(fragmentModels);
  }

  public List<OvFragmentModel> grabDatabyMonthCategory(int filter) {
    Cash cash = db.getLastCashflow(session.getIdUser());
    Calendar calendarNow = Calendar.getInstance();
    Calendar lastCalendar = Calendar.getInstance();
    List<OvFragmentModel> fragmentModels = new ArrayList<>();
    if (cash != null) {
      lastCalendar.setTime(Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date()));
      calendarNow.set(Calendar.DAY_OF_MONTH, 1);
      lastCalendar.set(Calendar.DAY_OF_MONTH, 1);
      do {
        int month = calendarNow.get(Calendar.MONTH) + 1;
        int year = calendarNow.get(Calendar.YEAR);
        String title = Helper.setSimpleDateFormat(calendarNow.getTime(), "MMMM yyyy");
        fragmentModels.add(
            new OvFragmentModel(title, OvCategoryFragment.newInstance(title, filter, year, month)));
        if (month == 1) {
          calendarNow.set(Calendar.MONTH, 11);
          calendarNow.add(Calendar.YEAR, -1);
        } else {
          calendarNow.add(Calendar.MONTH, -1);
        }
      } while (calendarNow.getTime().getTime() >= lastCalendar.getTime().getTime());
      Collections.reverse(fragmentModels);
    }
    return fragmentModels;
  }

  public List<OvFragmentModel> grabDatabyYearCategory(int filter) {
    Cash cash = db.getLastCashflow(session.getIdUser());
    Calendar calendarNow = Calendar.getInstance();
    Calendar lastCalendar = Calendar.getInstance();
    List<OvFragmentModel> fragmentModels = new ArrayList<>();
    if (cash != null) {
      lastCalendar.setTime(Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date()));
      calendarNow.set(Calendar.DAY_OF_MONTH, 1);
      lastCalendar.set(Calendar.DAY_OF_MONTH, 1);
      do {
        int now_year = calendarNow.get(Calendar.YEAR);
        fragmentModels.add(new OvFragmentModel("" + now_year,
            OvCategoryFragment.newInstance("" + now_year, filter, now_year, null)));
        calendarNow.add(Calendar.YEAR, -1);
      } while (calendarNow.get(Calendar.YEAR) >= lastCalendar.get(Calendar.YEAR));
      Collections.reverse(fragmentModels);
    }
    return fragmentModels;
  }

  public List<OvFragmentModel> grabDatabyMonthDetail(int filter) {
    Cash cash = db.getLastCashflow(session.getIdUser());
    Calendar calendarNow = Calendar.getInstance();
    Calendar lastCalendar = Calendar.getInstance();
    List<OvFragmentModel> fragmentModels = new ArrayList<>();
    if (cash != null) {
      lastCalendar.setTime(Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date()));
      calendarNow.set(Calendar.DAY_OF_MONTH, 1);
      lastCalendar.set(Calendar.DAY_OF_MONTH, 1);
      do {
        int now_month = calendarNow.get(Calendar.MONTH) + 1;
        int now_year = calendarNow.get(Calendar.YEAR);
        String title = Helper.setSimpleDateFormat(calendarNow.getTime(), "MMMM yyyy");
        fragmentModels.add(new OvFragmentModel(title,
            OvTransactionFragment.newInstance(title, filter, now_year, now_month)));
        if (now_month == 1) {
          calendarNow.set(Calendar.MONTH, 11);
          calendarNow.add(Calendar.YEAR, -1);
        } else {
          calendarNow.add(Calendar.MONTH, -1);
        }
      } while (calendarNow.getTime().getTime() >= lastCalendar.getTime().getTime());
      Collections.reverse(fragmentModels);
    }
    return fragmentModels;
  }

  public List<OvFragmentModel> grabDatabyMonthNet(int filter) {
    Cash cash = db.getLastCashflow(session.getIdUser());
    Calendar calendarNow = Calendar.getInstance();
    Calendar lastCalendar = Calendar.getInstance();
    List<OvFragmentModel> fragmentModels = new ArrayList<>();
    if (cash != null) {
      lastCalendar.setTime(Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date()));
      calendarNow.set(Calendar.DAY_OF_MONTH, 1);
      lastCalendar.set(Calendar.DAY_OF_MONTH, 1);
      int i = 0;
      do {
        int now_month = calendarNow.get(Calendar.MONTH) + 1;
        int now_year = calendarNow.get(Calendar.YEAR);
        String title = Helper.setSimpleDateFormat(calendarNow.getTime(), "MMMM yyyy");
        fragmentModels.add(new OvFragmentModel(title, new OvNettIncomeFragment().setPosition(i)
            .setTabType(filter)
            .setTabName(title)
            .setTahun(now_year)
            .setBulan(now_month)
            .setOnItemClik((x, y) -> getMvpView().scrollView(x, y))));
        if (now_month == 1) {
          calendarNow.set(Calendar.MONTH, 11);
          calendarNow.add(Calendar.YEAR, -1);
        } else {
          calendarNow.add(Calendar.MONTH, -1);
        }
        i++;
      } while (calendarNow.getTime().getTime() >= lastCalendar.getTime().getTime());
      Collections.reverse(fragmentModels);
    }
    return fragmentModels;
  }

  public List<OvFragmentModel> grabDatabyYearDetail(int filter) {
    Cash cash = db.getLastCashflow(session.getIdUser());
    Calendar calendarNow = Calendar.getInstance();
    Calendar lastCalendar = Calendar.getInstance();
    List<OvFragmentModel> fragmentModels = new ArrayList<>();
    if (cash != null) {
      lastCalendar.setTime(Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date()));
      calendarNow.set(Calendar.DAY_OF_MONTH, 1);
      lastCalendar.set(Calendar.DAY_OF_MONTH, 1);
      do {
        int now_year = calendarNow.get(Calendar.YEAR);
        fragmentModels.add(new OvFragmentModel("" + now_year,
            OvTransactionFragment.newInstance("" + now_year, filter, now_year, null)));
        calendarNow.add(Calendar.YEAR, -1);
      } while (calendarNow.get(Calendar.YEAR) >= lastCalendar.get(Calendar.YEAR));
      Collections.reverse(fragmentModels);
    }
    return fragmentModels;
  }
}
