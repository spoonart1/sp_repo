package javasign.com.dompetsehat.presenter.plan;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javax.inject.Inject;

import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.FinanceLib;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.utils.RxBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 9/6/16.
 */

public class DanaPresenter extends BasePresenter<DanaInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  private RupiahCurrencyFormat format = new RupiahCurrencyFormat();
  private DbHelper db;
  private RxBus rxBus = MyCustomApplication.getRxBus();

  private final double presentase_kenaikan_pendapatan = 0.10;
  private final double presentasi_inflasi = 0.03;
  private final int umur_harapan_hidup_male = 80;
  private final int umur_harapan_hidup_female = 90;
  private final double presentase_tingkat_pengembalian = 0.12;
  public static String KEY_DANA_DISIAPKAN = "dana";
  public static String KEY_PROSENTASE_PENGEMBALIAN = "prosentase_pengembalian";
  public static String KEY_CICILAN_PERBULAN = "cicilan_perbulan";

  // Dana Darurat : Dana yang harus disiapkan saat anda memakai dana darurat selama 2 bulan, adalah sebesar
  // Dana Kuliah  : Dana yang harus disiapkan saat anak anda mulai kuliah selama 3 semester, adalah sebesar
  // Dana Kustome : Dana yang harus disiapkan saat memulai rencana '____' selama __ tahun, adalah sebesar

  @Inject
  public DanaPresenter(@ActivityContext Context context, DataManager dataManager, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(DanaInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public String getNoteDanaPensiun(int usia) {
    String note =
        "Dana yang harus anda siapkan saat pensiun di usia " + usia + " tahun adalah sebesar: ";
    return note;
  }

  public String getNoteDanaDarurat(int bulan) {
    String note = "Dana darurat yang di persiapkan selama " + bulan + " bulan adalah sebesar: ";
    return note;
  }

  public String getNoteDanaKuliah(int semester) {
    String note = "Dana yang harus disiapkan saat anak anda mulai kuliah selama "
        + semester
        + " semester, adalah sebesar: ";
    return note;
  }

  public String getNoteDanaCustome(String title, int tahun) {
    String note = "Dana yang harus disiapkan saat memulai rencana "
        + title
        + " selama "
        + tahun
        + " bulan, adalah sebesar: ";
    return note;
  }

  public void init() {
    getMvpView().setUmur(getUmur());
    getMvpView().setPendapatanBulanan(getPendapatan());
  }

  public int getUmur() {
    User user = db.getUser(session.getIdUser(), db.TAB_USER);
    getMvpView().setPendapatanBulanan(user.getPenghasilan());
    SimpleDateFormat dateFormat = new SimpleDateFormat(helper.FORMAT_YYYY_MM_DD);
    Calendar calendar = Calendar.getInstance();
    try {
      Date birthday = dateFormat.parse(user.getBirthday());
      calendar.setTime(birthday);
      return helper.getAge(calendar);
    } catch (Exception e) {
    }
    return 0;
  }

  public double getPendapatan() {
    User user = db.getUser(session.getIdUser(), db.TAB_USER);
    if (user.getPenghasilan() != 0) {
      return user.getPenghasilan();
    } else {
      return db.getTotalSaldoCashflowByIDCategoryCurrentMonth(Integer.valueOf(session.getIdUser()),
          "CR");
    }
  }

  public void hitungCicilanBulanan(double dana_disiapkan, double persentase_asumsi,
      int bulan_cicilan) {
    getMvpView().setDanaDisiapkan(dana_disiapkan);
    getMvpView().setProsentase(persentase_asumsi);
    compositeSubscription.add(
        Observable.just(CicilanBulanan(dana_disiapkan, persentase_asumsi, bulan_cicilan))
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(uang -> {
              Timber.e("cicilan bulanan " + uang);
              getMvpView().setCicilanPerbulan(uang);
            }, throwable -> {
            }, () -> {
            }));
  }

  public void hitungCicilanTahunan(double dana_disiapkan, double persentase_asumsi,
      int bulan_cicilan) {
    getMvpView().setDanaDisiapkan(dana_disiapkan);
    getMvpView().setProsentase(persentase_asumsi);
    compositeSubscription.add(
        Observable.just(CicilanTahunan(dana_disiapkan, persentase_asumsi, bulan_cicilan))
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(uang -> {
              Timber.e("cicilan tahunan " + uang);
              getMvpView().setCicilanPertahun(uang);
            }, throwable -> {
            }, () -> {
            }));
  }

  public void hitungDibayarLunas(double dana_disiapkan, double persentase_asumsi,
      int bulan_cicilan) {
    getMvpView().setDanaDisiapkan(dana_disiapkan);
    getMvpView().setProsentase(persentase_asumsi);
    compositeSubscription.add(
        Observable.just(DibayarLunas(dana_disiapkan, persentase_asumsi, bulan_cicilan))
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(uang -> {
              Timber.e("lunas " + uang);
              getMvpView().setDibayarLunas(uang);
            }, throwable -> {
            }, () -> {
            }));
  }

  public double hitungInflasi(int tahun, double value) {
    return FinanceLib.fv(presentasi_inflasi, tahun, 0, -(value), true);
  }

  public double hitungDanaPensiun(int age_retire, int age, double penghasilan) {
    User user = db.getUser(session.getIdUser(), db.TAB_USER);
    int umur = age;
    int tahun_persiapan = age_retire - umur;
    int umur_harapan_hidup = 0;
    if (user.getGender() != null) {
      if (user.getGender().equalsIgnoreCase("male")) {
        umur_harapan_hidup = umur_harapan_hidup_male;
      } else {
        umur_harapan_hidup = umur_harapan_hidup_female;
      }
    } else {
      umur_harapan_hidup = umur_harapan_hidup_male;
    }

    Timber.e("umur harapan hidup " + umur_harapan_hidup);

    double penghasilan_pertahun = 12 * penghasilan;
    Timber.e(" penghasilan pertahun " + penghasilan);
    double total_kekurangan_penghasilan =
        FinanceLib.fv(presentase_kenaikan_pendapatan, tahun_persiapan, 0, -(penghasilan_pertahun),
            true);
    Timber.e(" FV => " + total_kekurangan_penghasilan);

    double prosentase_tingkat_bersih_pengembalian =
        presentase_tingkat_pengembalian - presentasi_inflasi;

    Timber.e("Tingkat Bersih Pengembalian " + Math.abs(prosentase_tingkat_bersih_pengembalian));

    int lama_pensiun = umur_harapan_hidup - age_retire;
    double tingkat_penarikan_tahun_pertama =
        FinanceLib.pmt(prosentase_tingkat_bersih_pengembalian, lama_pensiun, -1, 0, true);
    Timber.e(" PMT " + tingkat_penarikan_tahun_pertama);

    double dana_yang_disiapkan = total_kekurangan_penghasilan / tingkat_penarikan_tahun_pertama;
    Timber.e("dana yang di siapkan " + dana_yang_disiapkan);

    return dana_yang_disiapkan;
  }

  private double CicilanBulanan(double dana_yang_disiapkan, double persentase_asumsi,
      int bulan_cicilan) {
    double cicilan_perbulan =
        FinanceLib.pmt(persentase_asumsi / 12, bulan_cicilan, 0, -(dana_yang_disiapkan), true);
    return cicilan_perbulan;
  }

  private double CicilanTahunan(double dana_yang_disiapkan, double persentase_asumsi,
      int bulan_cicilan) {
    if (bulan_cicilan >= 12) {
      double cicilan_tahunan =
          FinanceLib.pmt(persentase_asumsi, bulan_cicilan / 12, 0, -(dana_yang_disiapkan), true);
      return cicilan_tahunan;
    } else {
      return 0.0;
    }
  }

  private double DibayarLunas(double dana_yang_disiapkan, double persentase_asumsi,
      int bulan_cicilan) {
    double dibayar_lunas =
        FinanceLib.pv(persentase_asumsi, bulan_cicilan / 12, 0, -(dana_yang_disiapkan), true);
    return dibayar_lunas;
  }
}
