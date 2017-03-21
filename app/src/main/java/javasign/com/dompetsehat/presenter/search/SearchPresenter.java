package javasign.com.dompetsehat.presenter.search;

import android.content.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.ui.activities.search.AdapterTableResult;
import javasign.com.dompetsehat.ui.activities.search.SearchActivity;
import javasign.com.dompetsehat.ui.activities.transaction.adapter.StickyTransactionAdapter;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 9/27/16.
 */

public class SearchPresenter extends BasePresenter<SearchInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final Context context;
  public final DbHelper db;
  public final SessionManager session;
  //private StickyTransactionAdapter transactionAdapter;

  @Inject public SearchPresenter(@ActivityContext Context context, DataManager dataManager,
      SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(SearchInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadAdapterTransaction(int flagFrom, Integer selectedAccount, String selectedType,
      Integer selectedCategory, String selectedTag, String selectedDateStart,
      String selectedDateEnd) {
    switch (flagFrom) {
      case SearchActivity.FLAG_SET_SEARCH_FOR_TRANSACTION:
        //transactionAdapter = new StickyTransactionAdapter(new ArrayList<Transaction>());
        //compositeSubscription.add(
        //    getTransactionDate(selectedAccount, selectedType, selectedCategory, selectedTag,
        //        selectedDateStart, selectedDateEnd).subscribeOn(Schedulers.io())
        //        .observeOn(AndroidSchedulers.mainThread())
        //        .subscribe(transactions -> {
        //          getMvpView().setAdapter(flagFrom,
        //              transactionAdapter.setTransactions(transactions));
        //          transactionAdapter.notifyAllSectionsDataSetChanged();
        //        }, throwable -> {
        //          Timber.e("ERROR " + throwable);
        //        }, () -> {
        //        }));

        AdapterTableResult adapterTableResult = new AdapterTableResult(new ArrayList<Cash>());
        compositeSubscription.add(
            getCashflow(selectedAccount, selectedType, selectedCategory, selectedTag,
                selectedDateStart, selectedDateEnd).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cashes -> {
                  adapterTableResult.setTransactions(cashes);
                  getMvpView().setAdapter(flagFrom, adapterTableResult);
                }, throwable -> {
                  Timber.e("ERROR "+throwable);
                }, () -> {
                }));
        break;
      default:
    }
  }

  public Observable<ArrayList<Transaction>> getTransactionDate(Integer selectedAccount,
      String selectedType, Integer selectedCategory, String selectedTag, String selectedDateStart,
      String selectedDateEnd) {
    return Observable.just(
        db.getAllTransaction(session.getIdUser(), selectedAccount, selectedType, selectedCategory,
            selectedTag, selectedDateStart, selectedDateEnd));
  }

  public Observable<ArrayList<Cash>> getCashflow(Integer selectedAccount, String selectedType,
      Integer selectedCategory, String selectedTag, String selectedDateStart,
      String selectedDateEnd) {
    return Observable.just(
        db.getAllCash(session.getIdUser(), selectedAccount, selectedType, selectedCategory,
            selectedTag, selectedDateStart, selectedDateEnd));
  }

  public void loadSeachMore() {
    ArrayList<Account> accounts = db.getAllAccountByUser(session.getIdUser());
    String[] db_tags = db.getAllTag(session.getIdUser());
    if(db_tags != null){
    List<String> tags = Arrays.asList(db_tags);
    ArrayList<String> fix_tags = new ArrayList<>();
    fix_tags.add(context.getString(R.string.all).toLowerCase());
    fix_tags.addAll(tags);
    fix_tags.removeAll(Arrays.asList("", null));
    ArrayList<String> labels = new ArrayList<>();
    labels.add(context.getString(R.string.all));
    compositeSubscription.add(Observable.from(accounts)
        .map(account -> account)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(account -> {
          labels.add(new MCryptNew().decrypt(account.getName()));
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().setSeachMore(accounts, (String[]) labels.toArray(new String[0]),
              new String[] { context.getString(R.string.all),context.getString(R.string.income),context.getString(R.string.expense),context.getString(R.string.transfer)},
              fix_tags.toArray(new String[0]));
        }));
    }
  }

  public void loadSeachCategory(String type) {
    ArrayList<Category> categories = db.getAllCategoryByType(session.getIdUser(), type);
    if (type.equalsIgnoreCase(Cash.ALL)) {
      categories = db.getAllCategory();
    }
    ArrayList<String> labels = new ArrayList<>();
    labels.add(context.getString(R.string.all));
    ArrayList<Category> finalCategories = categories;
    compositeSubscription.add(Observable.from(categories)
        .map(category -> category)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(category -> {
              labels.add(category.getName());
            }, throwable -> {
              Timber.e("ERROR "+throwable);
            }, () -> {
              getMvpView().setSeachCategory(finalCategories, labels.toArray(new String[0]));
            }

        ));
  }
}
