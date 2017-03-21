package javasign.com.dompetsehat.presenter.overview;

import android.content.Context;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.ui.event.SetTotalEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.SubcolumnValue;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 9/22/16.
 */

public class OverviewNetIncomePresenter extends BasePresenter<OverviewInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  int jumlah_bulan = 12;
  String[] labels = new String[] {
      "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sept", "Okt", "Nov", "Des"
  };

  int tahun_sekarang;
  int bulan_sekarang;
  RxBus rxBus = MyCustomApplication.getRxBus();

  DbHelper db;

  @Inject
  public OverviewNetIncomePresenter(@ActivityContext Context context, DataManager dataManager,
      SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = session;
    this.db = db;
    tahun_sekarang = Calendar.getInstance().get(Calendar.YEAR);
    bulan_sekarang = Calendar.getInstance().get(Calendar.MONTH);
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

  int iterator = -1;

  public void loadData(int tahun) {
    iterator = -1;
    List<Column> columns = new ArrayList<Column>();
    List<AxisValue> axisValues = new ArrayList<>();
    final float[] pendapatan = { 0 };
    final float[] pengeluaran = { 0 };
    compositeSubscription.add(Observable.from(getListLabel(tahun))
        .map(label -> label)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(label -> {
          iterator++;
          List<SubcolumnValue> values = new ArrayList<>();
          AxisValue axisValue = new AxisValue(iterator).setLabel(label);
          int bulan = Arrays.asList(labels).indexOf(label);
          float total_cr = (float) db.getTotalCashByMonthByTypeMustasi(tahun, bulan + 1, "CR",
              session.getIdUser());
          float total_db = (float) db.getTotalCashByMonthByTypeMustasi(tahun, bulan + 1, "DB",
              session.getIdUser());
          pendapatan[0] = pendapatan[0] + total_cr;
          pengeluaran[0] += total_db;
          float total = total_cr - total_db;
          total = total / 1000;
          values.add(new SubcolumnValue(total, accentColor(total)).setLabel(label));
          Column column = new Column(values);
          column.setHasLabels(false);
          column.setHasLabelsOnlyForSelected(true);
          columns.add(column);
          axisValues.add(axisValue);
        }, throwable -> {
        }, () -> {
          getMvpView().setChartNetIncome(columns, axisValues, "K");
          ArrayList<Cash> cashes = db.getJumlahDataByYear(tahun, "All", session.getIdUser());
          final ArrayList<Integer> ids = new ArrayList<>();
          for(Cash c:cashes){
            ids.add(c.getId());
          }
          rxBus.send(new SetTotalEvent(pendapatan[0] - pengeluaran[0]));
          getMvpView().setNetIncomeLabel(pendapatan[0], pengeluaran[0], cashes.size(), ids);
        }));
  }

  public void loadData(int bulan, int tahun) {
    iterator = -1;
    List<Column> columns = new ArrayList<Column>();
    List<AxisValue> axisValues = new ArrayList<>();
    final float[] pendapatan = { 0 };
    final float[] pengeluaran = { 0 };

    compositeSubscription.add(Observable.from(getListLabel(bulan, tahun))
        .map(date -> date)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(date -> {
          iterator++;
          List<SubcolumnValue> values = new ArrayList<>();
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
          if (calendar.get(Calendar.MONTH) + 1 < bulan) {
            calendar.add(Calendar.MONTH,1);
            calendar.set(Calendar.DAY_OF_MONTH,1);
          }
          String first = Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd");
          calendar.add(Calendar.DAY_OF_YEAR,calendar.getFirstDayOfWeek()+7);
          if (calendar.get(Calendar.MONTH) + 1 > bulan) {
            calendar.add(Calendar.MONTH,-1);
            calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
          }
          String last = Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd");
          AxisValue axisValue = new AxisValue(iterator).setLabel("week " + (iterator + 1));
          float total_cr =
              (float) db.getTotalCashByDateByTypeMustasi(first, last, "CR", session.getIdUser());
          float total_db =
              (float) db.getTotalCashByDateByTypeMustasi(first, last, "DB", session.getIdUser());
          Timber.e("date first " + first + " end " + last + " " + total_cr + " " + total_db);
          pendapatan[0] = pendapatan[0] + total_cr;
          pengeluaran[0] += total_db;
          float total = total_cr - total_db;
          total = total / 1000;
          values.add(new SubcolumnValue(total, accentColor(total)).setLabel("week " + (iterator+1)));
          Column column = new Column(values);
          column.setHasLabels(false);
          column.setHasLabelsOnlyForSelected(true);
          columns.add(column);
          axisValues.add(axisValue);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().setChartNetIncome(columns, axisValues, "K");
          Calendar calendar = Calendar.getInstance();
          calendar.set(Calendar.MONTH, bulan - 1);
          calendar.set(Calendar.YEAR, tahun);
          calendar.set(Calendar.DAY_OF_MONTH, 1);
          String s = Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd");
          calendar.set(calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
          String e = Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd");
          ArrayList<Cash> cashes = db.getJumlahDataByDate(s, e, "All", session.getIdUser());
          ArrayList<Integer> ids = new ArrayList<Integer>();
          for (Cash c:cashes){
            ids.add(c.getId());
          }
          rxBus.send(new SetTotalEvent(pendapatan[0] - pengeluaran[0]));
          getMvpView().setNetIncomeLabel(pendapatan[0], pengeluaran[0], cashes.size(), ids);
        }));
  }

  public void loadData(String tabName) {
    iterator = -1;
    Calendar calendar = Calendar.getInstance();
    HashMap<String, String> map;
    Date s;
    Date e;
    switch (Arrays.asList(
        context.getResources().getStringArray(R.array.overview_transactin_tab_labels))
        .indexOf(tabName)) {
      case 0:
        map = Helper.populatePeriodicString(Helper.A_YEAR_AGO);
        s = Helper.setInputFormatter("yyyy-MM-dd", map.get(Helper.DATE_START));
        e = Helper.setInputFormatter("yyyy-MM-dd", map.get(Helper.DATE_END));
        setChart(map, getListLabel(s, e));
        break;
      case 1:
        map = Helper.populatePeriodicString(Helper.YEARLY);
        s = Helper.setInputFormatter("yyyy-MM-dd", map.get(Helper.DATE_START));
        e = Helper.setInputFormatter("yyyy-MM-dd", map.get(Helper.DATE_END));
        setChart(map, getListLabel(s, e));
        break;
      case 2:
        map = Helper.populatePeriodicString(Helper.SIX_MONTHS_LAST_AGO);
        s = Helper.setInputFormatter("yyyy-MM-dd", map.get(Helper.DATE_START));
        e = Helper.setInputFormatter("yyyy-MM-dd", map.get(Helper.DATE_END));
        setChart(map, getListLabel(s, e));
        break;
      case 3:
        map = Helper.populatePeriodicString(Helper.THREE_MONTHS_LAST_AGO);
        s = Helper.setInputFormatter("yyyy-MM-dd", map.get(Helper.DATE_START));
        e = Helper.setInputFormatter("yyyy-MM-dd", map.get(Helper.DATE_END));
        setChart(map, getListLabel(s, e));
        break;
      case 4:
        calendar.add(Calendar.MONTH, -1);
        loadData(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        break;
      case 5:
        loadData(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        break;
    }
  }

  public HashMap<String, String> getMapDate(String tabName) {
    HashMap<String, String> map = new HashMap<>();
    switch (Arrays.asList(
        context.getResources().getStringArray(R.array.overview_transactin_tab_labels))
        .indexOf(tabName)) {
      case 0:
        map = Helper.populatePeriodicString(Helper.A_YEAR_AGO);
        break;
      case 1:
        map = Helper.populatePeriodicString(Helper.YEARLY);
        break;
      case 2:
        map = Helper.populatePeriodicString(Helper.SIX_MONTHS_LAST_AGO);
        break;
      case 3:
        map = Helper.populatePeriodicString(Helper.THREE_MONTHS_LAST_AGO);
        break;
      case 4:
        map = Helper.populatePeriodicString(Helper.A_MONTH_AGO);
        break;
      case 5:
        map = Helper.populatePeriodicString(Helper.THIS_MONTH);
        break;
    }
    return map;
  }

  public void setChart(HashMap<String, String> map, Date[] dates) {
    List<Column> columns = new ArrayList<Column>();
    List<AxisValue> axisValues = new ArrayList<>();
    final float[] pendapatan = { 0 };
    final float[] pengeluaran = { 0 };
    compositeSubscription.add(Observable.from(dates)
        .map(date -> date)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(date -> {
          iterator++;
          List<SubcolumnValue> values = new ArrayList<>();
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          calendar.set(Calendar.DAY_OF_MONTH, 1);
          String first = Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd");
          calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
          String last = Helper.setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd");
          Timber.e("Carrisa first "+first+" "+last+" "+calendar.get(Calendar.MONTH));
          AxisValue axisValue = new AxisValue(iterator).setLabel(
              Helper.getMonthName(calendar.get(Calendar.MONTH), true));
          float total_cr =
              (float) db.getTotalCashByDateByTypeMustasi(first, last, "CR", session.getIdUser());
          float total_db =
              (float) db.getTotalCashByDateByTypeMustasi(first, last, "DB", session.getIdUser());
          pendapatan[0] = pendapatan[0] + total_cr;
          pengeluaran[0] += total_db;
          float total = total_cr - total_db;
          total = total / 1000;
          values.add(new SubcolumnValue(total, accentColor(total)).setLabel(
              Helper.getMonthName(calendar.get(Calendar.MONTH), true)));
          Column column = new Column(values);
          column.setHasLabels(false);
          column.setHasLabelsOnlyForSelected(true);
          columns.add(column);
          axisValues.add(axisValue);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().setChartNetIncome(columns, axisValues, "K");
          ArrayList<Cash> cashes = db.getJumlahDataByDate(map.get(Helper.DATE_START), map.get(Helper.DATE_END), "All", session.getIdUser());
          ArrayList<Integer> ids = new ArrayList<Integer>();
          for (Cash c:cashes){
            ids.add(c.getId());
          }
          rxBus.send(new SetTotalEvent(pendapatan[0] - pengeluaran[0]));
          getMvpView().setNetIncomeLabel(pendapatan[0], pengeluaran[0], cashes.size(), ids);
        }));
  }

  private Date[] getListLabel(int month, int year) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    int index = calendar.get(Calendar.WEEK_OF_MONTH);
    ArrayList<Date> dates = new ArrayList<>();
    do {
      Timber.e("avesina date week of " + (calendar.get(Calendar.MONTH) + 1));
      Timber.e(month
          + " avesina date week of "
          + calendar.get(Calendar.WEEK_OF_MONTH)
          + " "
          + calendar.getTime());
      dates.add(calendar.getTime());
      calendar.add(Calendar.WEEK_OF_MONTH, -1);
      index--;
    } while (0 < index);
    Collections.reverse(dates);
    return dates.toArray(new Date[0]);
  }

  private Date[] getListLabel(Date start, Date end) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(start);
    ArrayList<Date> dates = new ArrayList<>();
    do {
      dates.add(calendar.getTime());
      calendar.add(Calendar.MONTH, 1);
      start = calendar.getTime();
    } while (start.getTime() <= end.getTime());
    return dates.toArray(new Date[0]);
  }

  public int accentColor(double value) {
    return value >= 0 ? Color.parseColor("#4cda64") : Color.parseColor("#ff3b2f");
  }

  public List<String> getListLabel(int tahun) {
    ArrayList<String> l = new ArrayList<>(Arrays.asList(labels));
    return l;
  }

  public void loadDataMonth(int month, Integer tahun) {
    final float[] pendapatan = { 0 };
    final float[] pengeluaran = { 0 };
    final int[] jumlah = { 0 };
    final ArrayList<Integer> ids = new ArrayList<>();
    compositeSubscription.add(getListCashflow(month, tahun).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cash -> {
          if (cash.getType().equalsIgnoreCase("CR")) {
            pendapatan[0] += cash.getAmount();
          } else {
            pengeluaran[0] += cash.getAmount();
          }
          jumlah[0]++;
          ids.add(cash.getId());
          Timber.e("Heloo ini " + pendapatan[0]);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
          Timber.e("Heloo");
          getMvpView().setNetIncomeLabel(pendapatan[0], pengeluaran[0], jumlah[0], ids);
        }));
  }

  public void loadDataMonth(int month, Integer tahun, String tabName) {
    final float[] pendapatan = { 0 };
    final float[] pengeluaran = { 0 };
    final int[] jumlah = { 0 };
    final ArrayList<Integer> ids = new ArrayList<>();
    HashMap<String, String> map = getMapDate(tabName);
    Timber.e("Carrisa bulan "+month+" tabname "+tabName+" map "+map);
    compositeSubscription.add(
        getListCashflow(map.get(Helper.DATE_START), map.get(Helper.DATE_END)).subscribeOn(
            Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(cash -> {
          Date date = Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date());
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          Timber.e(" Carrisa "+calendar.getTime());
          if (calendar.get(Calendar.MONTH) == month) {
            if (cash.getType().equalsIgnoreCase("CR")) {
              pendapatan[0] += cash.getAmount();
            } else {
              pengeluaran[0] += cash.getAmount();
            }
            jumlah[0]++;
            ids.add(cash.getId());
            Timber.e("Heloo ini bulan "+month+" "+ pendapatan[0]+" "+ids.size());
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
          Timber.e("Heloo");
          getMvpView().setNetIncomeLabel(pendapatan[0], pengeluaran[0], jumlah[0], ids);
        }));
  }

  public void loadDataMonth(int week,Integer bulan,Integer tahun,String tabName) {
    final float[] pendapatan = { 0 };
    final float[] pengeluaran = { 0 };
    final int[] jumlah = { 0 };
    final ArrayList<Integer> ids = new ArrayList<>();
    HashMap<String, String> map = null;
    String s = null;
    String e = null;
    if(bulan != null && tahun != null){
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.MONTH,bulan-1);
      calendar.set(Calendar.YEAR,tahun);
      calendar.set(Calendar.DAY_OF_MONTH,1);
      s = Helper.setSimpleDateFormat(calendar.getTime(),"yyyy-MM-dd");
      calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      e = Helper.setSimpleDateFormat(calendar.getTime(),"yyyy-MM-dd");
    }else {
      map = getMapDate(tabName);
      s = map.get(Helper.DATE_START);
      e = map.get(Helper.DATE_END);
    }
    Timber.e("avena.id start "+s+" e "+e);
    compositeSubscription.add(getListCashflow(s,e).subscribeOn(
            Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(cash -> {
          Date date = Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date());
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(date);
          Timber.e(calendar.get(Calendar.WEEK_OF_MONTH) + " Carrisa week " + week);
          if (calendar.get(Calendar.WEEK_OF_MONTH) == week) {
            if (cash.getType().equalsIgnoreCase("CR")) {
              pendapatan[0] += cash.getAmount();
            } else {
              pengeluaran[0] += cash.getAmount();
            }
            jumlah[0]++;
            ids.add(cash.getId());
            Timber.e("Heloo ini " + cash.getId());
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
          Timber.e("Heloo");
          getMvpView().setNetIncomeLabel(pendapatan[0], pengeluaran[0], jumlah[0], ids);
        }));
  }

  private Observable<Cash> getListCashflow(int month, Integer tahun) {
    ArrayList<Cash> cashes = db.getCashByMonthByTypeMutasi(tahun, month + 1, "All",
        Integer.valueOf(session.getIdUser()));
    return Observable.from(cashes);
  }

  private Observable<Cash> getListCashflow(String s, String e) {
    ArrayList<Cash> cashes = db.getTransactionByDate("CR", s, e, session.getIdUser());
    ArrayList<Cash> cashes1 = db.getTransactionByDate("DB", s, e, session.getIdUser());
    cashes.addAll(cashes1);
    return Observable.from(cashes);
  }
}
