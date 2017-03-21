package javasign.com.dompetsehat.presenter.overview;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvCategoryModel;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 9/22/16.
 */

public class OverviewCategoryPresenter extends BasePresenter<OverviewInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private String[] titles_tab;
  private DbHelper db;
  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private Helper helper = new Helper();

  @Inject
  public OverviewCategoryPresenter(@ActivityContext Context context, DataManager dataManager,
      SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = session;
    this.titles_tab = context.getResources().getStringArray(R.array.overview_transactin_tab_labels);
    this.db = db;
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

  public void populateOverviewCategory(String type_mutasi, String name) {
    Timber.e("avesina category "+name);
    switch (Arrays.asList(titles_tab).indexOf(name)) {
      case 0:
        getData(type_mutasi,seTahunLalu(type_mutasi));
      case 1:
        getData(type_mutasi,seTahun(type_mutasi));
        break;
      case 2:
        getData(type_mutasi,enamBulanLalu(type_mutasi));
        break;
      case 3:
        getData(type_mutasi,tigaBulanLalu(type_mutasi));
        break;
      case 4:
        getData(type_mutasi,bulanLalu(type_mutasi));
        break;
      case 5:
        getData(type_mutasi,bulanIni(type_mutasi));
        break;

    }
  }

  public void populateOverviewCategory(String type_mutasi, int year, int month) {
    HashMap<String, String> map = generateHashDate(year, month);
    getData(type_mutasi, map);
  }

  public void populateOverviewCategory(String type_mutasi, int year) {
    HashMap<String, String> map = generateHashDate(year);
    getData(type_mutasi, map);
  }

  private HashMap<String, String> generateHashDate(int year, int month) {
    HashMap<String, String> map = new HashMap<>();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    map.put(Helper.DATE_START, Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    map.put(Helper.DATE_END, Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
    return map;
  }

  private HashMap<String, String> generateHashDate(int year) {
    HashMap<String, String> map = new HashMap<>();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, 1);
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.DAY_OF_YEAR, 1);
    map.put(Helper.DATE_START, Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
    map.put(Helper.DATE_END, Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
    return map;
  }

  public void mingguIni(String type_mutasi) {
    HashMap<String, String> map = helper.populatePeriodicString(Helper.THIS_WEEK);
    Timber.e("Minggu ini " + map);
    getData(type_mutasi, map);
  }

  public HashMap<String, String> bulanIni(String type_mutasi) {
    HashMap<String, String> map = helper.populatePeriodicString(Helper.THIS_MONTH);
    return map;
  }

  public HashMap<String, String> bulanLalu(String type_mutasi) {
    HashMap<String, String> map = helper.populatePeriodicString(Helper.A_MONTH_AGO);
    return map;
  }

  public HashMap<String, String> tigaBulanLalu(String type_mutasi) {
    HashMap<String, String> map = helper.populatePeriodicString(Helper.THREE_MONTHS_LAST_AGO);
    return map;
  }

  public HashMap<String, String> enamBulanLalu(String type_mutasi) {
    HashMap<String, String> map = helper.populatePeriodicString(Helper.SIX_MONTHS_LAST_AGO);
    return map;
  }

  public HashMap<String,String> seTahun(String type_mutasi){
    HashMap<String, String> map = helper.populatePeriodicString(Helper.YEARLY);
    return map;
  }
  public HashMap<String,String> seTahunLalu(String type_mutasi){
    HashMap<String, String> map = helper.populatePeriodicString(Helper.A_YEAR_AGO);
    return map;
  }

  public void getData(String type_mutasi, HashMap<String, String> map) {
    ArrayList<Cash> cashes = new ArrayList<>();
    final double[] total = { 0 };
    compositeSubscription.add(
        Observable.from(getList(type_mutasi, map.get(Helper.DATE_START), map.get(Helper.DATE_END)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(cash -> {
              cashes.add(cash);
              total[0] += cash.getAmount();
            }, throwable -> Timber.e("ERROR " + throwable), () -> {
              setList(type_mutasi, total[0], cashes);
              getMvpView().setTotal(total[0]);
            }));
    compositeSubscription.add(
        getChartData(type_mutasi, map.get(Helper.DATE_START), map.get(Helper.DATE_END)).subscribeOn(
            Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(chart -> {
          getMvpView().setChartCategory(chart);
        }, throwable -> Timber.e("ERROR " + throwable), () -> {
        }));
  }

  public ArrayList<Cash> getList(String type_mutasi, String start_date, String end_date) {
    return db.getTransactionByDate(type_mutasi, start_date, end_date, session.getIdUser());
  }

  public void setList(String type_mutasi, double total, List<Cash> cashes) {
    Integer[] ids = new Integer[cashes.size()];
    int i = 0;
    for (Cash item : cashes) {
      ids[i] = item.getId();
      i++;
    }
    OvCategoryModel m = new OvCategoryModel();
    m.type = type_mutasi;
    m.ids_transaction = ids;
    m.count_transaction = cashes.size();
    m.total = total;
    getMvpView().setListTransaction(m);
  }

  public Observable<Map<Category, Double>> getChartData(String type_mutasi, String start_date,
      String end_date) {
    Map<Integer, Double> map =
        db.getTransactionByDateGroupByCategory(type_mutasi, start_date, end_date,
            session.getIdUser());
    HashMap<Category, Double> maps = new HashMap<>();
    for (Map.Entry<Integer, Double> data : map.entrySet()) {
      Category category = db.getCategoryByID(data.getKey());
      if (category.getIcon() != null) {
        maps.put(category, data.getValue());
      }
    }
    return Observable.just(maps);
  }

  public Integer[] getIdsByCategory(Category category, String type_mutasi, String tabName) {
    HashMap<String, String> date = new HashMap<>();
    switch (Arrays.asList(titles_tab).indexOf(tabName)) {
      case 0: // bulan ini
        date = bulanIni(type_mutasi);
        break;
      case 1: // bulan lalu
        date = bulanLalu(type_mutasi);
        break;
      case 2: // 3 bulan lalu
        date = tigaBulanLalu(type_mutasi);
        break;
      case 3: // 6 bulan lalau
        date = enamBulanLalu(type_mutasi);
        break;
      default:
        Timber.e(" ERROR " + tabName);
        break;
    }
    ArrayList<Cash> cashes =
        db.getCashbyCategory(String.valueOf(category.getId_category()), type_mutasi,
            session.getIdUser(), date.get(Helper.DATE_START), date.get(Helper.DATE_END));
    Integer[] integers = new Integer[cashes.size()];
    int i = 0;
    for (Cash cash : cashes) {
      integers[i] = cash.getId();
      i++;
    }
    return integers;
  }

  public Integer[] getIdsByCategory(Category category, String type_mutasi, int year, int month) {
    HashMap<String, String> date = generateHashDate(year, month);
    ArrayList<Cash> cashes = db.getCashbyCategory(String.valueOf(category.getId_category()),type_mutasi,session.getIdUser(), date.get(Helper.DATE_START), date.get(Helper.DATE_END));
    Integer[] integers = new Integer[cashes.size()];
    int i = 0;
    for (Cash cash : cashes) {
      integers[i] = cash.getId();
      i++;
    }
    return integers;
  }
}
