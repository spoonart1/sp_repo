package javasign.com.dompetsehat.presenter.transaction;

import android.content.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.transaction.listfragment.ListTransactionFragment;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.ui.event.AddTransactionEvent;
import javasign.com.dompetsehat.ui.fragments.timeline.pojo.TimelineView;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.WatcherUtils;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 8/16/16.
 */

public class TransactionsPresenter extends BasePresenter<TransactionsInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final Context context;
  public final DbHelper db;
  public final SessionManager session;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private int mRequestid, mId;
  private String mFilter;
  private String mTag;
  private final int MAXIMUM_LAST_TRANSACTION = 5;
  private Cash cashJustAdded = null;
  private boolean cardView = false;
  private WatcherUtils watcherUtils;
  private MCryptNew mCryptNew = new MCryptNew();

  @Inject public TransactionsPresenter(@ApplicationContext Context context, DataManager dataManager,
      DbHelper db, WatcherUtils watcherUtils) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    this.watcherUtils = watcherUtils.setSessionManager(session);
  }

  private void init() {
    rxBus.toObserverable()
        .ofType(AddTransactionEvent.class)
        .subscribe(new SimpleObserver<AddTransactionEvent>() {
          @Override public void onNext(AddTransactionEvent addTransactionEvent) {
            timelineLoadData(false);
            if (addTransactionEvent.cash != null) cashJustAdded = addTransactionEvent.cash;
            populateView(mRequestid, mId, mFilter, mTag, null, null, null);
          }
        });
  }

  @Override public void attachView(TransactionsInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void starSyncCashflow(SyncPresenter presenter) {

  }

  public void timelineLoadData(int account_id) {
    compositeSubscription.add(getTimeline(account_id).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(timelineView -> {
          getMvpView().setTimeline(timelineView, false);
        }, throwable -> {
          Timber.d(throwable, "Error loading data");
        }, () -> {

        }));
  }

  public void timelineLoadData(boolean isBroadcast) {
    compositeSubscription.add(getTimeline(null).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(timelineView -> {
          getMvpView().setTimeline(timelineView, isBroadcast);
        }, throwable -> {
          Timber.d(throwable, "Error loading data");
        }, () -> {
        }));
  }

  private Observable<TimelineView> getTimeline(Integer account_id) {
    return Observable.defer(() -> {
      Calendar calendar = Calendar.getInstance();
      int month = calendar.get(Calendar.MONTH) + 1;
      int year = calendar.get(Calendar.YEAR);
      ArrayList<Cash> cashs = db.getCashflowLastMonth(session.getIdUser(), year, month, account_id);
      if (cashs.size() <= 0) {
        List<Cash> last_cashes = db.get3CashflowLast(session.getIdUser(), account_id, 1);
        if (last_cashes.size() >= 1) {
          Cash cash = last_cashes.get(0);
          Date date = Helper.setInputFormatter("yyyy-MM-dd", cash.getCash_date());
          calendar.setTime(date);
          month = calendar.get(Calendar.MONTH) + 1;
          year = calendar.get(Calendar.YEAR);
          cashs = db.getCashflowLastMonth(session.getIdUser(), year, month, account_id);
        }
      }
      Double income =
          db.getTotalSaldoCashflowByJenisMutasi(session.getIdUser(), "CR", month, year, account_id);
      Double expense =
          db.getTotalSaldoCashflowByJenisMutasi(session.getIdUser(), "DB", month, year, account_id);
      String month_name = Helper.setSimpleDateFormat(calendar.getTime(), "MMMM");
      TimelineView timelineView = initLastTransaction(cashs, expense, income, month_name);
      return Observable.just(timelineView);
    });
  }

  public TimelineView initLastTransaction(ArrayList<Cash> cashs, Double totalExpense,
      Double totalIncome, String month_name) {
    TimelineView timelineView = new TimelineView();
    timelineView.cashs = cashs;
    TimelineView.Header header = new TimelineView.Header();
    header.title = "Arus transaksi";
    header.totalExpense = String.valueOf(totalExpense);
    header.totalIncome = String.valueOf(totalIncome);
    header.nettIncome = String.valueOf(totalIncome - totalExpense);
    header.month_name = month_name;
    timelineView.header = header;

    return timelineView;
  }

  public void performFilterBy(int requestid, int id, String filter, String tag, Integer bulan,
      Integer tahun, Integer[] ids, boolean cardView) {
    populateView(requestid, id, filter, tag, bulan, tahun, ids);
    this.mRequestid = requestid;
    this.mId = id;
    this.mFilter = filter;
    this.mTag = tag;
    this.cardView = cardView;
  }

  public void performFilterByProduct(int requestid, int id, String filter, String tag,
      Integer[] product_id) {
    populateViewProduct(requestid, id, filter, tag, product_id);
  }

  public void populateView(int requestid, int menu_id, String filter, String tag, Integer bulan,
      Integer tahun, Integer[] ids) {
    switch (menu_id) {
      case R.id.byDate:
        populateDate(requestid, filter, tag, bulan, tahun, ids);
        break;
      case R.id.byCategory:
        populateCategory(requestid, filter, tag, tahun, ids);
        break;
      case R.id.byAccount:
        populateAccount(requestid, filter, tag, tahun, ids);
        break;
      default:
        break;
    }
  }

  public void populateViewProduct(int requestid, int menu_id, String filter, String tag,
      Integer[] product_id) {
    switch (menu_id) {
      case R.id.byDate:
        populateDateByProduct(requestid, filter, tag, product_id);
        break;
      case R.id.byCategory:
        populateCategoryByProduct(requestid, filter, tag, product_id);
        break;
      default:
        populateDateByProduct(requestid, filter, tag, product_id);
        break;
    }
  }

  public void populateDate(int requestid, String filter, String tag, Integer bulan, Integer tahun,
      Integer[] ids) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        getTransactionDate(filter, tag, bulan, tahun, ids).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(fragmentModels -> {
              getMvpView().onNext(requestid);
              int pagerPosition = getPagerPositionByMonth(fragmentModels, bulan, tahun);
              getMvpView().setAdapterTransaction(fragmentModels, pagerPosition, cardView);
            }, throwable -> {
              Timber.e("ERROR populateDate " + throwable);
              getMvpView().onError(requestid);
            }, () -> {
              getMvpView().onComplete(requestid);
            }));
  }

  private int getPagerPositionByMonth(List<FragmentModel> fragmentModels) {
    return getPagerPositionByMonth(fragmentModels, null, null);
  }

  private int getPagerPositionByMonth(List<FragmentModel> fragmentModels, Integer bulan,
      Integer tahun) {
    Calendar calendar = Calendar.getInstance();
    int selectedTahun = calendar.get(Calendar.YEAR);
    int selectedBulan = calendar.get(Calendar.MONTH) + 1;
    if (bulan != null) {
      selectedBulan = bulan;
    }
    if (tahun != null) {
      selectedTahun = tahun;
    }
    int pagerPosition = 0;
    for (FragmentModel f : fragmentModels) {
      int month = Integer.valueOf(f.month);
      int year = Integer.valueOf(f.year);
      if (cashJustAdded != null) {
        if (cashJustAdded.getBulan() == month && cashJustAdded.getTahun() == year) {
          return pagerPosition;
        }
      } else {
        if (month == selectedBulan && year == selectedTahun) {
          return pagerPosition;
        }
      }
      pagerPosition++;
    }

    return pagerPosition;
  }

  public void populateDateByProduct(int requestid, String filter, String tag,
      Integer[] product_id) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        getTransactionDateByProduct(product_id, filter, tag).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(fragmentModels -> fragmentModels)
            .subscribe(fragmentModels -> {
              getMvpView().onNext(requestid);
              int i = getPagerPositionByMonth(fragmentModels);
              getMvpView().setAdapterTransaction(fragmentModels, i, cardView);
            }, throwable -> {
              Timber.e("ERROR populateDateByProduct 1 " + throwable);
              getMvpView().onError(requestid);
            }, () -> {
              getMvpView().onComplete(requestid);
            }));
  }

  public void populateCategoryByProduct(int requestid, String filter, String tag,
      Integer[] product_id) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        getTransactionCategoryByProduct(product_id, filter, tag).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(fragmentModels -> {
              getMvpView().onNext(requestid);
              getMvpView().setAdapterTransaction(fragmentModels, 0, cardView);
            }, throwable -> {
              Timber.e("ERROR populateCategoryByProduct " + throwable);
              getMvpView().onError(requestid);
            }, () -> {
              getMvpView().onComplete(requestid);
            }));
  }

  public void populateAccount(int requestid, String filter, String tag, Integer tahun,
      Integer[] ids) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        getTransactionAccount(filter, tag, tahun, ids).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(fragmentModels -> {
              getMvpView().onNext(requestid);
              getMvpView().setAdapterTransaction(fragmentModels, 0, cardView);
            }, throwable -> {
              Timber.e("ERROR populateAccount " + throwable);
              getMvpView().onError(requestid);
            }, () -> {
              getMvpView().onComplete(requestid);
            }));
  }

  public void populateCategory(int requestid, String filter, String tag, Integer tahun,
      Integer[] ids) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        getTransactionCategory(filter, tag, tahun, ids).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(fragmentModels -> {
              getMvpView().onNext(requestid);
              getMvpView().setAdapterTransaction(fragmentModels, 0, cardView);
            }, throwable -> {
              Timber.e("ERROR populateCategory " + throwable);
              getMvpView().onError(requestid);
            }, () -> {
              getMvpView().onComplete(requestid);
            }));
  }

  public Observable<List<FragmentModel>> getTransactionDate(String filter, String tag,
      Integer bulan, Integer tahun, Integer[] ids) {
    ArrayList<FragmentModel> fragmentModels =
        db.getAllTransactionsDate(filter, tag, null, tahun, ids, null, null);
    int i = 0;
    for (final FragmentModel fragmentModel : fragmentModels) {
      ListTransactionFragment.Filter f =
          new ListTransactionFragment.Filter(fragmentModel.month, fragmentModel.year, filter, tag,
              null, null, null, bulan, tahun, ids);
      f.filterBy = f.FILTER_BY_DATE;
      fragmentModel.setFragment(ListTransactionFragment.newInstance(i, fragmentModel.title, f));
      i++;
    }
    return Observable.just(fragmentModels);
  }

  public Observable<List<FragmentModel>> getTransactionAccount(String filter, String tag,
      Integer tahun, Integer[] ids) {
    ArrayList<FragmentModel> fragmentModels =
        db.getAllTransactionsAccount(Integer.valueOf(session.getIdUser()), filter, tag, tahun, ids);
    int i = 0;
    for (final FragmentModel fragmentModel : fragmentModels) {
      ListTransactionFragment.Filter f =
          new ListTransactionFragment.Filter(fragmentModel.month, fragmentModel.year, filter, tag,
              null, null, null, null, tahun, ids);
      f.filterBy = f.FILTER_BY_ACCOUNT;
      f.account_id = fragmentModel.acount_id;
      fragmentModel.setFragment(ListTransactionFragment.newInstance(i, fragmentModel.title, f));
      i++;
    }
    return Observable.just(fragmentModels);
  }

  public Observable<List<FragmentModel>> getTransactionCategory(String filter, String tag,
      Integer tahun, Integer[] ids) {
    ArrayList<FragmentModel> fragmentModels =
        db.getAllTransactionsCategory(filter, tag, null, tahun, ids, null, null);
    int i = 0;
    for (final FragmentModel fragmentModel : fragmentModels) {
      ListTransactionFragment.Filter f =
          new ListTransactionFragment.Filter(fragmentModel.month, fragmentModel.year, filter, tag,
              null, null, null, null, tahun, ids);
      f.filterBy = f.FILTER_BY_CATEGORY;
      f.category_id = fragmentModel.category_id;
      fragmentModel.setFragment(ListTransactionFragment.newInstance(i, fragmentModel.title, f));
      i++;
    }
    return Observable.just(fragmentModels);
  }

  public Observable<List<FragmentModel>> getTransactionDateByProduct(Integer[] product_id,
      String filter, String tag) {
    ArrayList<FragmentModel> fragmentModels =
        db.getAllTransactionsDate(filter, tag, product_id, null, null, null, null);
    int i = 0;
    for (final FragmentModel fragmentModel : fragmentModels) {
      ListTransactionFragment.Filter f =
          new ListTransactionFragment.Filter(fragmentModel.month, fragmentModel.year, filter, tag,
              product_id, null, null, null, null, null);
      f.filterBy = f.FILTER_BY_DATE;
      f.product_id = product_id;
      fragmentModel.setFragment(ListTransactionFragment.newInstance(i, fragmentModel.title, f));
      i++;
    }
    return Observable.just(fragmentModels);
  }

  public Observable<List<FragmentModel>> getTransactionCategoryByProduct(Integer[] product_id,
      String filter, String tag) {
    ArrayList<FragmentModel> fragmentModels =
        db.getAllTransactionsCategory(filter, tag, product_id, null, null, null, null);
    int i = 0;
    for (final FragmentModel fragmentModel : fragmentModels) {
      ListTransactionFragment.Filter f =
          new ListTransactionFragment.Filter(fragmentModel.month, fragmentModel.year, filter, tag,
              product_id, null, null, null, null, null);
      f.filterBy = f.FILTER_BY_CATEGORY;
      f.category_id = fragmentModel.category_id;
      fragmentModel.setFragment(ListTransactionFragment.newInstance(i, fragmentModel.title, f));
      i++;
    }
    return Observable.just(fragmentModels);
  }

  public void initTags() {
    compositeSubscription.add(getTags().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tags -> {
          getMvpView().initTags(tags);
        }, throwable -> {
          Timber.e("ERROR INIT TAGS: " + throwable);
        }, () -> {
        }));
  }

  public Observable<List<String>> getTags() {
    return Observable.defer(() -> {
      ArrayList<String> tags = new ArrayList<>();
      List<String> db_tags = Arrays.asList(db.getAllTag(session.getIdUser()));
      List<String> default_tags =
          Arrays.asList(context.getResources().getStringArray(R.array.default_tags));
      tags.addAll(default_tags);
      tags.addAll(db_tags);
      ArrayList<String> fix_tags = new ArrayList<>();
      for (String tag : tags) {
        String _tag = tag.toLowerCase();
        if (!fix_tags.contains(_tag)) {
          fix_tags.add(_tag);
        }
      }
      fix_tags.removeAll(Arrays.asList("", null));
      return Observable.just(fix_tags);
    });
  }

  public void loadDataTransaction(ListTransactionFragment.Filter filter) {
    Timber.e("loadDataTransaction avesina "+filter.status+" "+filter.account_id);
    compositeSubscription.add(getTransactionByFilter(filter).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(transactions -> {
          getMvpView().setTransaction(transactions);
        }, throwable -> {
          Timber.e("ERROR LOAD DATA : " + throwable);
        }, () -> {

        }));
  }

  public void deleteTransaction(Cash cashflow, DeleteUtils.OnDeleteListener deleteListener) {
    compositeSubscription.add(Observable.just(delete(cashflow))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Boolean>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onNext(Boolean isDelete) {
            if (isDelete && deleteListener != null) {
              if (deleteListener != null) deleteListener.onDoneRemoving();
            }
          }
        }));
  }

  private boolean delete(Cash cashflow) {
    boolean set;
    int product_id = cashflow.getProduct_id();
    float amount = cashflow.getAmount();
    String type_mutasi = cashflow.getType();
    Timber.e("avesina delete cash id_loca" + cashflow.getId());
    Timber.e("avesina delete cash cash_id" + cashflow.getCash_id());
    set = db.softDeleteCashflow(cashflow.getId());
    if (set) {
      db.updateSaldoAfterEditTransaction(product_id, amount, type_mutasi);
    }
    return set;
  }

  private Observable<ArrayList<Transaction>> getTransactionByFilter(
      ListTransactionFragment.Filter filter) {
    return Observable.defer(() -> {
      ArrayList<Transaction> transactions = new ArrayList<>();
      int bulan = 0;
      int tahun = 0;
      Timber.e("aves filter " + filter.filterBy);
      switch (filter.filterBy) {
        case ListTransactionFragment.Filter.FILTER_BY_DATE:
          try {
            bulan = Integer.valueOf(filter.month);
            tahun = Integer.valueOf(filter.year);
          } catch (Exception e) {
            Timber.e("getTransactionByFilter " + e);
          }
          Timber.i("bulan " + bulan + " tahun " + tahun);
          transactions =
              db.getTransactionDate(bulan, tahun, filter.filter, filter.tag, filter.product_id,
                  filter.filter_tahun, filter.ids,filter.account_id,filter.status);
          break;
        case ListTransactionFragment.Filter.FILTER_BY_CATEGORY:
          Timber.e("aves filter category " + filter.category_id);
          transactions = db.getTransactionCategory(filter.category_id, filter.filter, filter.tag,
              filter.product_id, filter.filter_tahun, filter.ids,filter.account_id,filter.status);

          break;
        case ListTransactionFragment.Filter.FILTER_BY_PRODUCT:
          try {
            bulan = Integer.valueOf(filter.month);
            tahun = Integer.valueOf(filter.year);
          } catch (Exception e) {
            Timber.e("getTransactionByFilter " + e);
          }
          Timber.i("bulan " + bulan + " tahun " + tahun);
          transactions =
              db.getTransactionDate(bulan, tahun, filter.filter, filter.tag, filter.product_id,
                  filter.filter_tahun, filter.ids,filter.account_id,filter.status);
          break;
        case ListTransactionFragment.Filter.FILTER_BY_ACCOUNT:
          transactions = db.getTransactionAccount(filter.account_id, filter.filter, filter.tag,
              filter.filter_tahun, filter.ids,filter.status);
          break;
      }
      return Observable.just(transactions);
    });
  }

  public void changeCategory(int cash_id, int category_id) {
    String type = "CR";
    Category category = db.getCategoryByID(category_id);
    Cash cash = db.getCashByID(cash_id);
    if (category.getBelong_to().equalsIgnoreCase("income")) {
      type = "CR";
    } else if (category.getBelong_to().equalsIgnoreCase("expense")) {
      type = "DB";
    } else if (category.getBelong_to().equalsIgnoreCase("transfer")) {
      type = cash.getType();
    }
    db.updateCashCategory(cash_id, category_id, type);
    MyCustomApplication.getRxBus().send(new AddTransactionEvent(context, cash, false, false));
  }

  public void changeUserCategory(int cash_id, int parent_id, int user_category_id) {
    String type = "CR";
    Category category = db.getCategoryByID(parent_id);
    Cash cash = db.getCashByID(cash_id);
    if (category.getBelong_to().equalsIgnoreCase("income")) {
      type = "CR";
    } else if (category.getBelong_to().equalsIgnoreCase("expense")) {
      type = "DB";
    } else if (category.getBelong_to().equalsIgnoreCase("transfer")) {
      type = cash.getType();
    }
    db.updateCashUserCategory(cash_id, parent_id, user_category_id, type);
    MyCustomApplication.getRxBus().send(new AddTransactionEvent(context, cash, false, false));
  }

  public void changeRenameCashflow(int id, String string) {
    db.updateRenameCash(id, string);
    Cash cash = db.getCashByID(id);
    MyCustomApplication.getRxBus().send(new AddTransactionEvent(context, cash, false, false));
  }

  public void checkNotif() {
    watcherUtils.triggerOverSpending();
  }

  public void performFilterBy(int requestid, int menu_id, String filter, String tag,
      Integer selectedAccount, String selectedStatus) {
    populateViewAccount(requestid, menu_id, filter, tag, selectedAccount, selectedStatus);
  }

  public void populateViewAccount(int requestid, int menu_id, String filter, String tag,
      Integer account_id, String status) {
    switch (menu_id) {
      case R.id.byDate:
        populateDateByAccount(requestid, filter, tag, account_id, status);
        break;
      case R.id.byCategory:
        populateCategoryByAccount(requestid, filter, tag, account_id, status);
        break;
      default:
        populateDateByAccount(requestid, filter, tag, account_id, status);
        break;
    }
  }

  public void populateDateByAccount(int requestid, String filter, String tag, Integer account,
      String status) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        getTransactionDateByAccount(account, status, filter, tag).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(fragmentModels -> fragmentModels)
            .subscribe(fragmentModels -> {
              getMvpView().onNext(requestid);
              Timber.e("avesina "+fragmentModels.size());
              int i = getPagerPositionByMonth(fragmentModels);
              getMvpView().setAdapterTransaction(fragmentModels, i, cardView);
            }, throwable -> {
              Timber.e("ERROR populateDateByProduct 2 " + throwable);
              getMvpView().onError(requestid);
            }, () -> {
              getMvpView().onComplete(requestid);
            }));
  }

  public void populateCategoryByAccount(int requestid, String filter, String tag, Integer account,
      String status) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        getTransactionCategoryByAccount(account, status, filter, tag).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(fragmentModels -> {
              getMvpView().onNext(requestid);
              getMvpView().setAdapterTransaction(fragmentModels, 0, cardView);
            }, throwable -> {
              Timber.e("ERROR populateCategoryByProduct " + throwable);
              getMvpView().onError(requestid);
            }, () -> {
              getMvpView().onComplete(requestid);
            }));
  }

  public Observable<List<FragmentModel>> getTransactionCategoryByAccount(Integer account_id,
      String status, String filter, String tag) {
    ArrayList<FragmentModel> fragmentModels =
        db.getAllTransactionsCategory(filter, tag, null, null, null, account_id, status);
    int i = 0;
    for (final FragmentModel fragmentModel : fragmentModels) {
      ListTransactionFragment.Filter f =
          new ListTransactionFragment.Filter(fragmentModel.month, fragmentModel.year, filter, tag,
              null, account_id, status, null, null, null);
      f.filterBy = f.FILTER_BY_CATEGORY;
      f.category_id = fragmentModel.category_id;
      fragmentModel.setFragment(ListTransactionFragment.newInstance(i, fragmentModel.title, f));
      i++;
    }
    return Observable.just(fragmentModels);
  }

  public Observable<List<FragmentModel>> getTransactionDateByAccount(Integer account_id,String status, String filter, String tag) {
    ArrayList<FragmentModel> fragmentModels = db.getAllTransactionsDate(filter, tag, null, null, null, account_id, status);
    int i = 0;
    for (final FragmentModel fragmentModel : fragmentModels) {
      ListTransactionFragment.Filter f = new ListTransactionFragment.Filter(fragmentModel.month, fragmentModel.year, filter, tag,null, account_id, status, null, null, null);
      f.filterBy = f.FILTER_BY_DATE;
      f.account_id = account_id;
      fragmentModel.setFragment(ListTransactionFragment.newInstance(i, fragmentModel.title, f));
      i++;
    }
    return Observable.just(fragmentModels);
  }
}
