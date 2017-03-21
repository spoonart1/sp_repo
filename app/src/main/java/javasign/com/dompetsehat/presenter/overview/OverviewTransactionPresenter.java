package javasign.com.dompetsehat.presenter.overview;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.ui.fragments.overview_v1.adapter.Old_AdapterOverviewByCategory;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvCategoryModel;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvTransactionModel;
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

public class OverviewTransactionPresenter extends BasePresenter<OverviewInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private String[] titles_tab;
  private DbHelper db;
  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

  @Inject
  public OverviewTransactionPresenter(@ActivityContext Context context, DataManager dataManager,
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

  public void populateOverviewTransaction(String type_mutasi, String name) {
    Timber.e("Menu " + name);
    switch (Arrays.asList(titles_tab).indexOf(name)) {
      case 0:
        satuTahunLalu(type_mutasi, name);
        break;
      case 1:
        satuTahun(type_mutasi, name);
        break;
      case 2:
        enamBulanLalu(type_mutasi, name);
        break;
      case 3:
        tigaBulanLalu(type_mutasi, name);
        break;
      case 4:
        bulanLalu(type_mutasi, name);
        break;
      case 5:
        bulanIni(type_mutasi, name);
        break;
    }
  }

  public void populateOverviewTransactionByYearAndMonth(String type_mutasi, int year, int month) {
    HashMap<String, String> map = new HashMap<>();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    map.put(Helper.DATE_START, Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    map.put(Helper.DATE_END, Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
    Timber.e("avesina map " + map);
    getDataByYearMonth(type_mutasi, map);
  }

  public void populateOverviewTransactionByYear(String type_mutasi, int year) {
    HashMap<String, String> map = new HashMap<>();
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_YEAR,1);
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    map.put(Helper.DATE_START, Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
    map.put(Helper.DATE_END, Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
    Timber.e("avesina map " + map);
    getDataByYear(type_mutasi, map);
  }

  public void getDataByYearMonth(String type_mutasi, HashMap<String, String> map) {
    ArrayList<Cash> cashes = new ArrayList<>();
    final double[] total = { 0 };
    compositeSubscription.add(
        Observable.from(getList(type_mutasi, map.get(Helper.DATE_START), map.get(Helper.DATE_END)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(cash -> {
              cashes.add(cash);
              total[0] += cash.getAmount();
            }, throwable -> Timber.e("ERROR getData getList " + throwable), () -> {
              setList(type_mutasi, total[0], cashes);
              getMvpView().setTotal(total[0]);
            }));
    compositeSubscription.add(
        getChartData(type_mutasi, map.get(Helper.DATE_START), map.get(Helper.DATE_END)).subscribeOn(
            Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(entry -> {
          if (entry.size() > 0) {
            float[] values = new float[entry.size()];
            String[] labels = new String[entry.size()];
            int i = 0;
            float min = Float.POSITIVE_INFINITY;
            float max = 0;
            for (Map.Entry<String, Double> item : entry.entrySet()) {
              labels[i] = dateFormat(item.getKey());
              values[i] = item.getValue().floatValue();
              if (Float.compare(values[i], min) < 0) {
                min = values[i];
              }
              if (Float.compare(max, values[i]) < 0) {
                max = values[i];
              }
              if (values[i] != 0) {
                values[i] = values[i];
              }
              Timber.e("Heloo " + item.getKey() + " value " + item.getValue());
              i += 1;
            }
            values = normalisasiValues((int) max, values);
            int pemabagi = getPembagi((int) max);
            getMvpView().setChartLabelValues(labels, values, (int) min / pemabagi,
                (int) max / pemabagi, generateLabel((int) max));
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public void getDataByYear(String type_mutasi, HashMap<String, String> map) {
    ArrayList<Cash> cashes = new ArrayList<>();
    final double[] total = { 0 };
    compositeSubscription.add(
        Observable.from(getList(type_mutasi, map.get(Helper.DATE_START), map.get(Helper.DATE_END)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(cash -> {
              cashes.add(cash);
              total[0] += cash.getAmount();
            }, throwable -> Timber.e("ERROR getData getList " + throwable), () -> {
              setList(type_mutasi, total[0], cashes);
              getMvpView().setTotal(total[0]);
            }));
    compositeSubscription.add(getChartDataMonthly(type_mutasi, map.get(Helper.DATE_START),
        map.get(Helper.DATE_END)).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(entry -> {
          if (entry.size() > 0) {
            float[] values = new float[entry.size()];
            String[] labels = new String[entry.size()];
            int i = 0;
            float min = Float.POSITIVE_INFINITY;
            float max = 0;
            for (Map.Entry<String, Double> item : entry.entrySet()) {
              String[] key = item.getKey().split("-");
              Calendar calendar = Calendar.getInstance();
              calendar.set(Calendar.MONTH, Integer.valueOf(key[1]) - 1);
              calendar.set(Calendar.YEAR, Integer.valueOf(key[0]));
              labels[i] = Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM");
              values[i] = item.getValue().floatValue();
              if (Float.compare(values[i], min) < 0) {
                min = values[i];
              }
              if (Float.compare(max, values[i]) < 0) {
                max = values[i];
              }
              if (values[i] != 0) {
                values[i] = values[i];
              }
              i += 1;
            }
            values = normalisasiValues((int) max, values);
            int pemabagi = getPembagi((int) max);
            getMvpView().setChartLabelValues(labels, values, (int) min / pemabagi,
                (int) max / pemabagi, generateLabel((int) max));
          }
        }, throwable -> {

        }, () -> {

        }));
  }

  private float[] normalisasiValues(int max, float[] values) {
    if (max < 100) {
      return values;
    }
    int pembagi = getPembagi(max);
    for (int i = 0; i < values.length; i++) {
      if (Float.compare(values[i], 0) > 0) {
        values[i] = values[i] / pembagi;
      }
    }
    return values;
  }

  private int getPembagi(int max) {
    int pembagi = 1;
    if (max >= 100 && max < 1000) {
      pembagi = 100;
    } else if (max >= 1000 && max < 1000000) {
      pembagi = 1000;
    } else if (max >= 1000000 && max < 1000000000) {
      pembagi = 1000000;
    } else if (max >= 1000000000 && max < Integer.MAX_VALUE) {
      pembagi = 1000000000;
    }
    return pembagi;
  }

  private String generateLabel(int max) {
    if (max >= 100 && max < 1000) {
      return "rts";
    } else if (max >= 1000 && max < 1000000) {
      return "rb";
    } else if (max >= 1000000 && max < 1000000000) {
      return "jt";
    } else if (max >= 1000000000 && max < Integer.MAX_VALUE) {
      return "m";
    }
    return "";
  }

  public void mingguIni(String type_mutasi, String name) {
    HashMap<String, String> map = Helper.populatePeriodicString(Helper.THIS_WEEK);
    getData(type_mutasi, map, name);
  }

  public void bulanIni(String type_mutasi, String name) {
    HashMap<String, String> map = Helper.populatePeriodicString(Helper.THIS_MONTH);
    Timber.e("bulan ini " + map);
    getData(type_mutasi, map, name);
  }

  public void bulanLalu(String type_mutasi, String name) {
    HashMap<String, String> map = Helper.populatePeriodicString(Helper.A_MONTH_AGO);
    getData(type_mutasi, map, name);
  }

  public void tigaBulanLalu(String type_mutasi, String name) {
    HashMap<String, String> map = Helper.populatePeriodicString(Helper.THREE_MONTHS_LAST_AGO);
    getData(type_mutasi, map, name);
  }

  public void enamBulanLalu(String type_mutasi, String name) {
    HashMap<String, String> map = Helper.populatePeriodicString(Helper.SIX_MONTHS_LAST_AGO);
    getData(type_mutasi, map, name);
  }

  public void satuTahun(String type_mutasi, String name) {
    HashMap<String, String> map = Helper.populatePeriodicString(Helper.YEARLY);
    getData(type_mutasi, map, name);
  }

  public void satuTahunLalu(String type_mutasi, String name) {
    HashMap<String, String> map = Helper.populatePeriodicString(Helper.A_YEAR_AGO);
    getData(type_mutasi, map, name);
  }

  public void getData(String type_mutasi, HashMap<String, String> map, String name) {
    ArrayList<Cash> cashes = new ArrayList<>();
    final double[] total = { 0 };
    int idx = Arrays.asList(titles_tab).indexOf(name);
    compositeSubscription.add(
        Observable.from(getList(type_mutasi, map.get(Helper.DATE_START), map.get(Helper.DATE_END)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(cash -> {
              cashes.add(cash);
              total[0] += cash.getAmount();
            }, throwable -> Timber.e("ERROR getData getList " + throwable), () -> {
              setList(type_mutasi, total[0], cashes);
              getMvpView().setTotal(total[0]);
            }));
    if (idx == 4 || idx == 5) {
      compositeSubscription.add(getChartData(type_mutasi, map.get(Helper.DATE_START),
          map.get(Helper.DATE_END)).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(entry -> {
            if (entry.size() > 0) {
              float[] values = new float[entry.size()];
              String[] labels = new String[entry.size()];
              int i = 0;
              float min = Float.POSITIVE_INFINITY;
              float max = 0;
              for (Map.Entry<String, Double> item : entry.entrySet()) {
                labels[i] = dateFormat(item.getKey());
                values[i] = item.getValue().floatValue();
                if (Float.compare(values[i], min) < 0) {
                  min = values[i];
                }
                if (Float.compare(max, values[i]) < 0) {
                  max = values[i];
                }
                if (values[i] != 0) {
                  values[i] = values[i];
                }
                Timber.e("Heloo "
                    + item.getKey()
                    + " value "
                    + item.getValue()
                    + " values "
                    + values[i]
                    + " max "
                    + max
                    + " min "
                    + min);
                i += 1;
              }
              values = normalisasiValues((int) max, values);
              int pemabagi = getPembagi((int) max);
              getMvpView().setChartLabelValues(labels, values, (int) min / pemabagi,
                  (int) max / pemabagi, generateLabel((int) max));
            }
          }, throwable -> {
          }, () -> {
          }));
    } else if (false) {
      compositeSubscription.add(getChartDataWeekly(type_mutasi, map.get(Helper.DATE_START),
          map.get(Helper.DATE_END)).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(entry -> {
            if (entry.size() > 0) {
              float[] values = new float[entry.size()];
              String[] labels = new String[entry.size()];
              int i = 0;
              float min = Float.POSITIVE_INFINITY;
              float max = 0;
              Calendar calendar2 = Calendar.getInstance();
              for (Map.Entry<String, Double> item : entry.entrySet()) {
                calendar2.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(item.getKey()));
                int d2 = calendar2.get(Calendar.DAY_OF_MONTH);
                int m2 = calendar2.get(Calendar.MONTH) + 1;
                calendar2.add(Calendar.DAY_OF_WEEK, -7);
                int d1 = calendar2.get(Calendar.DAY_OF_MONTH);
                int m1 = calendar2.get(Calendar.MONTH) + 1;
                labels[i] = d1 + "/" + m1 + " - " + d2 + "/" + m2;
                values[i] = item.getValue().floatValue();
                if (Float.compare(values[i], min) < 0) {
                  min = values[i];
                }
                if (Float.compare(max, values[i]) < 0) {
                  max = values[i];
                }
                if (values[i] != 0) {
                  values[i] = values[i];
                }
                i += 1;
              }
              values = normalisasiValues((int) max, values);
              int pemabagi = getPembagi((int) max);
              getMvpView().setChartLabelValues(labels, values, (int) min / pemabagi,
                  (int) max / pemabagi, generateLabel((int) max));
            }
          }, throwable -> Timber.e("ERROR getData getChartDataWeekly " + throwable), () -> {
          }));
    } else if (idx == 3 || idx == 2 || idx == 1 || idx == 0) {
      compositeSubscription.add(getChartDataMonthly(type_mutasi, map.get(Helper.DATE_START),
          map.get(Helper.DATE_END)).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(entry -> {
            if (entry.size() > 0) {
              float[] values = new float[entry.size()];
              String[] labels = new String[entry.size()];
              int i = 0;
              float min = Float.POSITIVE_INFINITY;
              float max = 0;
              for (Map.Entry<String, Double> item : entry.entrySet()) {
                String[] key = item.getKey().split("-");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, Integer.valueOf(key[1]) - 1);
                calendar.set(Calendar.YEAR, Integer.valueOf(key[0]));
                labels[i] = Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM");
                values[i] = item.getValue().floatValue();
                if (Float.compare(values[i], min) < 0) {
                  min = values[i];
                }
                if (Float.compare(max, values[i]) < 0) {
                  max = values[i];
                }
                if (values[i] != 0) {
                  values[i] = values[i];
                }
                i += 1;
              }
              values = normalisasiValues((int) max, values);
              int pemabagi = getPembagi((int) max);
              getMvpView().setChartLabelValues(labels, values, (int) min / pemabagi,
                  (int) max / pemabagi, generateLabel((int) max));
            }
          }, throwable -> {

          }, () -> {

          }));
    }
  }

  private String dateFormat(String date) {
    String[] strings = date.split("-");
    Timber.e("date string " + strings);
    if (strings.length == 3) {
      return "" + Integer.valueOf(strings[2]);
    } else {
      return date;
    }
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

  public ArrayList<Cash> getList(String type_mutasi, String start_date, String end_date) {
    return db.getTransactionByDate(type_mutasi, start_date, end_date, session.getIdUser());
  }

  public Observable<Map<String, Double>> getChartData(String type_mutasi, String start_date,
      String end_date) {
    Map<String, Double> map =
        db.getTransactionByDateGroupByDate(type_mutasi, start_date, end_date, session.getIdUser());
    Calendar calendar = Calendar.getInstance();
    try {
      calendar.setTime(simpleDateFormat.parse(start_date));
      Date start = calendar.getTime();
      calendar.setTime(simpleDateFormat.parse(end_date));
      Date end = calendar.getTime();
      Date temp = start;
      while (temp.getTime() <= end.getTime()) {
        calendar.setTime(temp);
        if (!map.containsKey(simpleDateFormat.format(calendar.getTime()))) {
          map.put(simpleDateFormat.format(calendar.getTime()), 0.0);
        }
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        temp = calendar.getTime();
      }
    } catch (Exception e) {
      Timber.e("ERROR getChartData " + e);
    }
    Map<String, Double> mapSorted = new TreeMap<>(map);
    return Observable.just(mapSorted);
  }

  public Observable<Map<String, Double>> getChartDataWeekly(String type_mutasi, String start_date,
      String end_date) {
    Map<String, Double> map =
        db.getTransactionByDateGroupByWeek(type_mutasi, start_date, end_date, session.getIdUser());
    Calendar calendar = Calendar.getInstance();
    try {
      calendar.setTime(simpleDateFormat.parse(start_date));
      Date start = calendar.getTime();
      calendar.setTime(simpleDateFormat.parse(end_date));
      Date end = calendar.getTime();
      Date temp = start;
      while (temp.getTime() <= end.getTime()) {
        calendar.setTime(temp);
        if (!map.containsKey(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)))) {
          map.put(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)), 0.0);
        }
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        temp = calendar.getTime();
      }
    } catch (Exception e) {
      Timber.e("ERROR getChartDataWeekly " + e);
    }
    Map<String, Double> mapSorted = new TreeMap<>(map);
    return Observable.just(mapSorted);
  }

  public Observable<Map<String, Double>> getChartDataMonthly(String type_mutasi, String start_date,
      String end_date) {
    Map<String, Double> map =
        db.getTransactionByDateGroupByMonth(type_mutasi, start_date, end_date, session.getIdUser());
    Calendar calendar = Calendar.getInstance();
    try {
      calendar.setTime(simpleDateFormat.parse(start_date));
      Date start = calendar.getTime();
      calendar.setTime(simpleDateFormat.parse(end_date));
      Date end = calendar.getTime();
      Date temp = start;
      while (temp.getTime() <= end.getTime()) {
        calendar.setTime(temp);
        if (!map.containsKey(Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM"))) {
          map.put(Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM"), 0.0);
        }
        calendar.add(Calendar.MONTH, 1);
        temp = calendar.getTime();
      }
    } catch (Exception e) {
      Timber.e("ERROR getChartDataWeekly " + e);
    }
    Timber.e("avesina asyk " + map);
    Map<String, Double> mapSorted = new TreeMap<>(map);
    return Observable.just(mapSorted);
  }

  public int accentColor(double value) {
    return value >= 0 ? context.getResources().getColor(R.color.green_dompet_ori)
        : context.getResources().getColor(R.color.red_500);
  }
}
