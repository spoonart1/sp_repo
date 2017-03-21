package javasign.com.dompetsehat.presenter.budget;

import android.content.Context;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;
import javasign.com.dompetsehat.ui.activities.budget.listfragment.SchedulerFragment;
import javasign.com.dompetsehat.ui.activities.budget.pojo.HeaderBudget;
import javasign.com.dompetsehat.ui.activities.budget.pojo.ScheduleFragmentModel;
import javasign.com.dompetsehat.ui.event.AddBudgetEvent;
import javasign.com.dompetsehat.ui.event.BudgetSchedulerEvent;
import javasign.com.dompetsehat.ui.event.DeleteBudgetEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 9/1/16.
 */

public class BudgetPresenter extends BasePresenter<BudgetInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  private Helper hp = new Helper();
  private ArrayList<Double> income;
  private ArrayList<Double> expense;
  private DbHelper db;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private double header_from = 0;
  private double header_to = 0;
  Integer[] category_ids = {
      4, 24, 2, 20, 6, 7, 11, 23, 21, 5, 32
  };
  Integer[] priorities = {
      300, // food 4
      150, // bill 24
      100, // transport 2
      90, // keluarga dan personal 20
      90, // shoping 6
      75, // pajak 7
      90, // entertaiment 11
      50, // investment 23
      50, // insurent 21
      75, // health 5
      15, // kids 32
  };

  HashMap<String, String[]> berulang;
  HashMap<String, String> sekali;
  private List<ScheduleFragmentModel> models;
  SimpleDateFormat format_ddM = new SimpleDateFormat("dd MMMM");
  SimpleDateFormat format_ddMyy = new SimpleDateFormat("dd MMMM yyyy");
  SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
  String[] index_berulang = { Helper.WEEKLY, Helper.BIWEEKLY, Helper.MONTHLY, Helper.YEARLY };
  String[] index_sekali = { Helper.THIS_WEEK, Helper.THIS_MONTH, Helper.QUATER_MONTH };

  @Inject public BudgetPresenter(@ApplicationContext Context context, DataManager dataManager,
      DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    berulang = new HashMap<>();

    berulang.put(Helper.WEEKLY, new String[] { Budget.WEEKLY, "Ulangi setiap minggu" });
    berulang.put(Helper.BIWEEKLY, new String[] { Budget.BIWEEKLY, "Ulangi setiap dua minggu" });
    berulang.put(Helper.MONTHLY, new String[] { Budget.MONTHLY, "Ulangi setiap bulan" });
    berulang.put(Helper.YEARLY, new String[] { Budget.YEARLY, "Ulangi setiap tahun" });
    sekali = new HashMap<>();
    sekali.put(Helper.THIS_WEEK, Budget.WEEKLY);
    sekali.put(Helper.THIS_MONTH, Budget.THIS_MONTH);
    sekali.put(Helper.QUATER_MONTH, Budget.QUARTER_MONTH);
  }

  @Override public void attachView(BudgetInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void init() {
    //TODO : get from rxBus
    rxBus.toObserverable().ofType(AddBudgetEvent.class).subscribe(new Observer<AddBudgetEvent>() {
      @Override public void onCompleted() {
        Timber.d("RxBus AddBudgetEvent Completed");
      }

      @Override public void onError(Throwable e) {
        Timber.d("RxBus AddBudgetEvent Error: %s", e.getLocalizedMessage());
      }

      @Override public void onNext(AddBudgetEvent addBudgetEvent) {
        setData();
      }
    });
  }

  public void catchAddPeriodic() {
    rxBus.toObserverable()
        .ofType(BudgetSchedulerEvent.class)
        .subscribe(new SimpleObserver<BudgetSchedulerEvent>() {
          @Override public void onNext(BudgetSchedulerEvent budgetSchedulerEvent) {
            getMvpView().setSHModel(budgetSchedulerEvent.shModel);
          }
        });

    rxBus.toObserverable()
        .ofType(DeleteBudgetEvent.class)
        .subscribe(new SimpleObserver<DeleteBudgetEvent>() {
          @Override public void onNext(DeleteBudgetEvent deleteBudgetEvent) {
            setData();
          }
        });
  }

  public void saveBudgetUser(double penghasilan, double cicilan, int anak) {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(helper.FORMAT_LENGKAP);
    compositeSubscription.add(dataManager.updateProfile(null, null, null, penghasilan, anak)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(signInResponse -> {
          if (signInResponse.status.equalsIgnoreCase("success")) {
            User user = db.getUser(session.getIdUser(), db.TAB_USER);
            user.setAnak(anak);
            user.setPenghasilan(penghasilan);
            user.setCicilan(cicilan);
            user.setUpdated_at(sdf.format(calendar.getTime()));
            user.setLast_sync_cash(null);
            user.setLast_sync_budget(null);
            user.setLast_sync_alarm(null);
            db.UpdateUser(user);
          }
        }, throwable -> {
          Timber.e("ERRO " + throwable);
        }, () -> {
        }));
  }

  public void setData() {
    compositeSubscription.add(getObservableList().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(budgets -> {
          getMvpView().setAdapterBudget(budgets);
        }, throwable -> {
        }, () -> {
          getMvpView().setAdapterHeader(getHeader());
        }));
  }

  public HeaderBudget getHeader() {
    HeaderBudget headerBudget = new HeaderBudget();
    headerBudget.headTitle = "Total Budget";
    headerBudget.from = this.header_from;
    headerBudget.to = this.header_to;
    return headerBudget;
  }

  private Observable<List<Budget>> getObservableList() {
    return Observable.just(getList());
  }

  private List<Budget> getList() {
    ArrayList<Budget> budgets = db.getAllBudget(session.getIdUser());
    this.header_to = 0;
    this.header_from = 0;
    for (Budget budget : budgets) {
      budget.setCategory(db.getCategoryByID(budget.getCategory_budget()));
      budget.setUserCategory(db.getUserCategoryByID(budget.getUser_category_budget()));
      Timber.e("ERVERY " + budget.getEvery() + " " + budget.getRepeat());
      if (budget.getRepeat() == Budget.BUDGET_REPEAT_TRUE) {
        HashMap<String, String> map = new HashMap<>();
        if (budget.getEvery().equalsIgnoreCase(Budget.CUSTOM.toLowerCase())) {
          map = Helper.populatePeriodicCustome(
              Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_start()),
              Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_end()), true);
        } else {
          map = Helper.populatePeriodicString(budget.getEvery().toLowerCase());
        }
        budget.setDate_start(map.get(hp.DATE_START));
        budget.setDate_end(map.get(hp.DATE_END));
      }
      if (budget.getDate_start() != null && budget.getDate_end() != null) {
        budget.setCategory_cash_amount(
            db.getTotalAmountCashByCategoryAndDate(String.valueOf(budget.getCategory_budget()),
                "All", session.getIdUser(), budget.getDate_start(), budget.getDate_end()));
      }
      this.header_to += budget.getAmount_budget();
      this.header_from += budget.getCategory_cash_amount();
    }
    Timber.e("header_to" + header_to);
    Timber.e("header_from" + header_from);
    return budgets;
  }

  public void saveBudget(Budget budget) {
    budget.setId_budget(-1);
    budget.setUser_id(Integer.valueOf(session.getIdUser()));
    db.newBudget(budget);
    Helper.trackThis(context, "user berhasil membuat anggaran per kategori ");
    rxBus.send(new AddBudgetEvent(context, budget));
  }

  public void updateBudget(Budget budget) {
    db.updateBudget(budget);
    rxBus.send(new AddBudgetEvent(context, budget));
  }

  public void hitungEstimasiBudget(double monthly_income, double kredit, int child) {
    compositeSubscription.add(
        ObservableEstimasiBudget(monthly_income, kredit, child).subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(budgets -> {
              getMvpView().setAdapterBudget(budgets);
            }, throwable -> {
              Timber.e("ERROR " + throwable);
            }, () -> {
            }));
  }

  public Observable<ArrayList<Budget>> ObservableEstimasiBudget(double monthly_income,
      double kredit, int child) {
    ArrayList<Budget> budgets = new ArrayList<>();
    int i = 0;
    ArrayList<Category> avalaible_categories = new ArrayList<>();
    ArrayList<Integer> avalaible_priority = new ArrayList<>();
    int total_priority = 0;
    for (Integer category_id : category_ids) {
      Category category = db.getCategoryByID(category_id);
      if (category.getIcon() != null) {
        if (category_id == 32) {
          total_priority += (priorities[i] * child);
        } else {
          total_priority += priorities[i];
        }
        avalaible_priority.add(priorities[i]);
        avalaible_categories.add(category);
      }
      i += 1;
    }
    i = 0;
    Timber.e(" total_priority " + total_priority);
    for (Category category : avalaible_categories) {
      double amount_budget = 0.0;
      if (category.getId_category() == 32) {
        Timber.e("prioritas " + avalaible_priority.get(i) + " total_prioryty " + total_priority);
        amount_budget = monthly_income * (avalaible_priority.get(i) * child) / total_priority;
      } else {
        amount_budget = monthly_income * avalaible_priority.get(i) / total_priority;
      }
      double int_amount_budget = Math.floor(amount_budget / 100);
      HashMap<String, String> map = Helper.populatePeriodicString(Budget.MONTHLY.toLowerCase());
      budgets.add(new Budget().setAmount_budget(int_amount_budget * 100)
          .setCategory_budget(category.getId_category())
          .setCategory(category)
          .setEvery(Budget.MONTHLY.toLowerCase())
          .setDate_start(map.get(Helper.DATE_START))
          .setDate_end(map.get(Helper.DATE_END))
          .setRepeat(Budget.BUDGET_REPEAT_TRUE)
          .setUser_id(Integer.valueOf(session.getIdUser())));
      i += 1;
    }
    return Observable.just(budgets);
  }

  public void saveEstimations(List<Budget> budgets) {
    compositeSubscription.add(Observable.from(budgets).map(budget -> {
      budget.setId_budget(-1);
      return budget;
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(budget -> {
      db.UpdateBudget(budget);
    }, throwable -> {
      Timber.e("ERROR " + throwable);
    }, () -> {
      rxBus.send(new AddBudgetEvent(context, budgets));
      Helper.trackThis(context, "user berhasil membuat anggaran sekaligus");
    }));
  }

  public void deleteBudget(int id, DeleteUtils.OnDeleteListener deleteListener) {
    compositeSubscription.add(Observable.just(db.softDeleteBudgetByID(id))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isDeleted -> {
          if (isDeleted) {
            if (deleteListener != null) deleteListener.onDoneRemoving();
          }
        }, throwable -> {
        }, () -> {

        }));
  }

  public void setBudget(int budget_id) {
    compositeSubscription.add(getBudget(budget_id).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(budget -> {
          AdapterScheduler.SHModel shModel = new AdapterScheduler.SHModel(budget.getEvery());
          shModel.identify = budget.getEvery().toLowerCase();
          if (budget.getRepeat() == 1) {
            try {
              HashMap<String, String> periode = new HashMap<String, String>();
              if (budget.getEvery().equalsIgnoreCase(Budget.CUSTOM.toLowerCase())) {
                Timber.e("avesina repeat " + budget.getDate_start() + " " + budget.getDate_end());
                periode = Helper.populatePeriodicCustome(
                    Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_start()),
                    Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_end()), true);
                Timber.e("avesina repeat " + periode);
              } else {
                periode = Helper.populatePeriodicString(budget.getEvery().toLowerCase());
              }
              Date start = format.parse(periode.get(Helper.DATE_START));
              Date end = format.parse(periode.get(Helper.DATE_END));
              shModel.setChecked(true);
              shModel.setPeriode(Helper.setSimpleDateFormat(start, GeneralHelper.FORMAT_YYYY_MM_DD),
                  Helper.setSimpleDateFormat(end, GeneralHelper.FORMAT_YYYY_MM_DD));
              shModel.setLabelDatePeriode(format_ddM.format(start), format_ddM.format(end));
              shModel.leftSwitchLabel = berulang.get(budget.getEvery().toLowerCase())[1];
              shModel.setRepeatAble(true);
            } catch (Exception e) {
              Timber.e("ERROR " + e);
            }
          } else {
            shModel.setChecked(false);
            shModel.setPeriode(budget.getDate_start(), budget.getDate_end());
            shModel.leftSwitchLabel = sekali.get(budget.getEvery().toLowerCase());
            shModel.setRepeatAble(false);
          }
          setDetailListCash(budget.getCategory_budget(), "All", budget.getDate_start(),
              budget.getDate_end());
          getMvpView().setBudget(shModel, budget);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public void setDetailListCash(int category_id, String type_mutasi, String start_date,
      String end_date) {
    ArrayList<Cash> cashes =
        db.getCashByCategoryAndDate(String.valueOf(category_id), type_mutasi, session.getIdUser(),
            start_date, end_date);
    ArrayList<Integer> ids = new ArrayList<>();
    compositeSubscription.add(Observable.from(cashes)
        .map(cash -> cash)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cash -> {
          ids.add(cash.getId());
        }, throwable -> {
        }, () -> {
          getMvpView().setDetailCash(ids.toArray(new Integer[0]));
        }));
  }

  public Observable<Budget> getBudget(int budget_id) {
    Budget budget = db.getBudgetByID(budget_id);
    budget.setCategory(db.getCategoryByID(budget.getCategory_budget()));
    if (budget.getUser_category_budget() > 0) {
      budget.setUserCategory(db.getUserCategoryByID(budget.getUser_category_budget()));
    }
    if (budget.getRepeat() == Budget.BUDGET_REPEAT_TRUE) {
      HashMap<String, String> map = new HashMap<>();
      if (budget.getEvery().equalsIgnoreCase(Budget.CUSTOM)) {
        Timber.e("avesina repeat " + budget.getDate_start() + " " + budget.getDate_end());
        map = Helper.populatePeriodicCustome(
            Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_start()),
            Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_end()), true);
      } else {
        map = hp.populatePeriodicString(budget.getEvery().toLowerCase());
      }
      budget.setDate_start(map.get(hp.DATE_START));
      budget.setDate_end(map.get(hp.DATE_END));
    }
    if (budget.getDate_start() != null && budget.getDate_end() != null) {
      budget.setCategory_cash_amount(
          db.getTotalAmountCashByCategoryAndDate(String.valueOf(budget.getCategory_budget()), "All",
              session.getIdUser(), budget.getDate_start(), budget.getDate_end()));
    }
    return Observable.just(budget);
  }

  public Budget getBudgetById(int budget_id) {
    Budget budget = db.getBudgetByID(budget_id);
    budget.setCategory(db.getCategoryByID(budget.getCategory_budget()));
    if (budget.getUser_category_budget() > 0) {
      budget.setUserCategory(db.getUserCategoryByID(budget.getUser_category_budget()));
    }
    return budget;
  }

  public void loadScheduler() {
    models = new ArrayList<>();
    models.add(new ScheduleFragmentModel("Berulang",
        SchedulerFragment.newInstance(getRepeatablePeriode())));
    models.add(new ScheduleFragmentModel("Sekali",
        SchedulerFragment.newInstance(getNonRepeatablePeriode())));
    getMvpView().setScheduler(models);
  }

  private List<AdapterScheduler.SHModel> getNonRepeatablePeriode() {
    List<AdapterScheduler.SHModel> fragmentModels = new ArrayList<>();
    for (String key : index_sekali) {
      String value = sekali.get(key);
      HashMap<String, String> map_date = Helper.populatePeriodicString(key);
      Timber.e("map " + map_date);
      try {
        Date start = format.parse(map_date.get(Helper.DATE_START));
        Date end = format.parse(map_date.get(Helper.DATE_END));
        AdapterScheduler.SHModel model =
            new AdapterScheduler.SHModel(value).setIdentify(key).setChecked(false);
        model.setPeriode(map_date.get(Helper.DATE_START), map_date.get(Helper.DATE_END));
        if (!key.equalsIgnoreCase(Helper.QUATER_MONTH)) {
          fragmentModels.add(
              model.setLabelDatePeriode(format_ddM.format(start), format_ddM.format(end)));
        } else {
          fragmentModels.add(
              model.setLabelDatePeriode(format_ddMyy.format(start), format_ddMyy.format(end)));
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    AdapterScheduler.SHModel model =
        new AdapterScheduler.SHModel(Budget.CUSTOM).setIdentify(Budget.CUSTOM.toLowerCase())
            .setLabelDatePeriode("---", "---")
            .setTypeViewCustom()
            .setChecked(false);
    fragmentModels.add(model);

    return fragmentModels;
  }

  private List<AdapterScheduler.SHModel> getRepeatablePeriode() {
    List<AdapterScheduler.SHModel> fragmentModels = new ArrayList<>();
    for (String key : index_berulang) {
      String[] value = berulang.get(key);
      HashMap<String, String> map_date = Helper.populatePeriodicString(key);
      try {
        Date start = format.parse(map_date.get(Helper.DATE_START));
        Date end = format.parse(map_date.get(Helper.DATE_END));
        AdapterScheduler.SHModel model = new AdapterScheduler.SHModel(value[0]).setIdentify(key)
            .setChecked(false)
            .setRepeatAble(true)
            .setLeftSwitchLabel(value[1]);
        model.setPeriode(map_date.get(Helper.DATE_START), map_date.get(Helper.DATE_END));
        if (!key.equalsIgnoreCase(Helper.YEARLY)) {
          fragmentModels.add(
              model.setLabelDatePeriode(format_ddM.format(start), format_ddM.format(end)));
        } else {
          fragmentModels.add(
              model.setLabelDatePeriode(format_ddMyy.format(start), format_ddMyy.format(end)));
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    AdapterScheduler.SHModel model =
        new AdapterScheduler.SHModel(Budget.CUSTOM).setIdentify(Budget.CUSTOM.toLowerCase())
            .setLabelDatePeriode("---", "---")
            .setTypeViewCustom()
            .setChecked(false)
            .setRepeatAble(true)
            .setLeftSwitchLabel("Ulangi jika telah selesai diselanjutnya");
    fragmentModels.add(model);

    return fragmentModels;
  }
}
