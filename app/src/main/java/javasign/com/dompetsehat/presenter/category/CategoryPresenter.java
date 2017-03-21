package javasign.com.dompetsehat.presenter.category;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategory;
import javasign.com.dompetsehat.ui.event.AddCategoryEvent;
import javax.inject.Inject;

import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategoryFragmentPager;
import javasign.com.dompetsehat.ui.activities.category.listfragment.ListCategoryFragment;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;
import javasign.com.dompetsehat.utils.RxBus;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 8/18/16.
 */

public class CategoryPresenter extends BasePresenter<CategoryInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final Context context;
  private final DbHelper db;
  private final SessionManager session;
  private AdapterCategoryFragmentPager adapterCategoryFragmentPager;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private Integer[] excluded = new Integer[] {};
  private String LIST_PARENT = "list_parent";
  private String LIST_CHILD = "list_child";

  @Inject public CategoryPresenter(@ActivityContext Context context, DataManager dataManager,
      SessionManager session, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = session;
    this.db = db;
  }

  @Override public void attachView(CategoryInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public ArrayList<FragmentCategoryModel> getCategory(String active, String type, boolean is_finish,
      boolean add_button_showed, AdapterCategory.OnCategoryClick onCategoryClick) {
    ArrayList<FragmentCategoryModel> fragments = new ArrayList<>();
    Timber.e("avesina ganteng " + active + " heloo " + type);
    if (type.equalsIgnoreCase("All")) {
      if (active.equalsIgnoreCase("DB")) {
        fragments.add(getCategoryExpense(is_finish, add_button_showed, onCategoryClick));
        fragments.add(getCategoryIncome(is_finish, add_button_showed, onCategoryClick));
        fragments.add(getCategoryTransfer(is_finish, add_button_showed, onCategoryClick));
      } else if (active.equalsIgnoreCase("CR")) {
        fragments.add(getCategoryIncome(is_finish, add_button_showed, onCategoryClick));
        fragments.add(getCategoryExpense(is_finish, add_button_showed, onCategoryClick));
        fragments.add(getCategoryTransfer(is_finish, add_button_showed, onCategoryClick));
      } else if (active.equalsIgnoreCase("TF")) {
        fragments.add(getCategoryTransfer(is_finish, add_button_showed, onCategoryClick));
        fragments.add(getCategoryIncome(is_finish, add_button_showed, onCategoryClick));
        fragments.add(getCategoryExpense(is_finish, add_button_showed, onCategoryClick));
      }
    } else if (type.equalsIgnoreCase("DB")) {
      fragments.add(getCategoryExpense(is_finish, add_button_showed, onCategoryClick));
      fragments.add(getCategoryTransfer(is_finish, add_button_showed, onCategoryClick));
    } else if (type.equalsIgnoreCase("CR")) {
      fragments.add(getCategoryIncome(is_finish, add_button_showed, onCategoryClick));
      fragments.add(getCategoryTransfer(is_finish, add_button_showed, onCategoryClick));
    }

    Timber.e("avesina ganteng " + fragments.size());

    return fragments;
  }

  public FragmentCategoryModel getCategoryIncome(boolean is_finish, boolean add_button_showed,
      AdapterCategory.OnCategoryClick onCategoryClick) {
    List<Category> mostUsedCategoryIncome =
        removeExcluded(db.getMostCategory(session.getIdUser(), "CR"));
    HashMap<String, Object> data =
        RemoveAndGetChild(db.getAllCategoryByType(session.getIdUser(), "CR"));
    List<Category> categoriesIncome = (List<Category>) data.get(LIST_PARENT);
    HashMap<Integer, List<UserCategory>> userCategories =
        (HashMap<Integer, List<UserCategory>>) data.get(LIST_CHILD);
    ListCategoryFragment f =
        ListCategoryFragment.newInstance(mostUsedCategoryIncome, categoriesIncome, userCategories,
            ListCategoryFragment.INCOME_CATEGORY);
    if (!is_finish) {
      f.setDontFinishActivity();
    }
    if (!add_button_showed) {
      f.hideFloatingButton();
    }
    if (onCategoryClick != null) {
      f.setOnCategoryClick(onCategoryClick);
    }
    return new FragmentCategoryModel(context.getString(R.string.income), f);
  }

  public FragmentCategoryModel getCategoryTransfer(boolean is_finish, boolean add_button_showed,
      AdapterCategory.OnCategoryClick onCategoryClick) {
    List<Category> mostUsedCategoryIncome =
        removeExcluded(db.getMostCategory(session.getIdUser(), "TF"));
    HashMap<String, Object> data =
        RemoveAndGetChild(db.getAllCategoryByType(session.getIdUser(), "TF"));
    List<Category> categoriesIncome = (List<Category>) data.get(LIST_PARENT);
    HashMap<Integer, List<UserCategory>> userCategories =
        (HashMap<Integer, List<UserCategory>>) data.get(LIST_CHILD);
    ListCategoryFragment f =
        ListCategoryFragment.newInstance(mostUsedCategoryIncome, categoriesIncome, userCategories,
            ListCategoryFragment.TRANSFER_CATEGORY);
    if (!is_finish) {
      f.setDontFinishActivity();
    }
    if (onCategoryClick != null) {
      f.setOnCategoryClick(onCategoryClick);
    }
    if (!add_button_showed) {
      f.hideFloatingButton();
    }
    return new FragmentCategoryModel(context.getString(R.string.transfer), f);
  }

  public FragmentCategoryModel getCategoryExpense(boolean is_finish, boolean add_button_showed,
      AdapterCategory.OnCategoryClick onCategoryClick) {
    List<Category> mostUsedCategoryExpense =
        removeExcluded(db.getMostCategory(session.getIdUser(), "DB"));
    HashMap<String, Object> data =
        RemoveAndGetChild(db.getAllCategoryByType(session.getIdUser(), "DB"));
    List<Category> categoriesExpense = (List<Category>) data.get(LIST_PARENT);
    HashMap<Integer, List<UserCategory>> userCategories =
        (HashMap<Integer, List<UserCategory>>) data.get(LIST_CHILD);

    ListCategoryFragment f =
        ListCategoryFragment.newInstance(mostUsedCategoryExpense, categoriesExpense, userCategories,
            ListCategoryFragment.EXPENSE_CATEGORY);
    if (!is_finish) {
      f.setDontFinishActivity();
    }
    if (!add_button_showed) {
      f.hideFloatingButton();
    }
    if (onCategoryClick != null) {
      f.setOnCategoryClick(onCategoryClick);
    }
    return new FragmentCategoryModel(context.getString(R.string.expense), f);
  }

  public Observable<ArrayList<FragmentCategoryModel>> ObservableGetCategory(String active_type,
      String type, boolean is_finish, boolean add_button_showed,
      AdapterCategory.OnCategoryClick onCategoryClick) {
    return Observable.just(
        getCategory(active_type, type, is_finish, add_button_showed, onCategoryClick));
  }

  public List<Category> removeExcluded(ArrayList<Category> categories) {
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    if (excluded.length > 0) {
      for (Category category : categories) {
        if (!Arrays.asList(excluded).contains(category.getId_category())) {
          categoryArrayList.add(category);
        }
      }
      return categoryArrayList;
    } else {
      return categories;
    }
  }

  public HashMap<String, Object> RemoveAndGetChild(ArrayList<Category> categories) {
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    HashMap<Integer, List<UserCategory>> map = new HashMap<>();
    for (Category category : categories) {
      if (!Arrays.asList(excluded).contains(category.getId_category())) {
        categoryArrayList.add(category);
        map.put(category.getId_category(),
            db.getUserCategoryByCategory(session.getIdUser(), category.getId_category()));
      }
    }
    HashMap<String, Object> maps = new HashMap<>();
    maps.put(LIST_PARENT, categoryArrayList);
    maps.put(LIST_CHILD, map);
    return maps;
  }

  public void init(int requestid, String active, String type, boolean with_exc) {
    getMvpView().onLoad(requestid);
    if (with_exc) {
      List<Budget> budgets = db.getAllBudget(session.getIdUser());
      Integer[] integers = new Integer[budgets.size()];
      int[] i = new int[] { 0 };
      compositeSubscription.add(Observable.from(budgets)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .map(budget -> budget)
          .subscribe(budget -> {
            integers[i[0]] = budget.getCategory_budget();
            i[0]++;
          }, throwable -> {
            Timber.e("ERROR " + throwable);
          }, () -> {
            excluded = integers;
            getCategegoryList(requestid, active, type);
          }));
    } else {
      getCategegoryList(requestid, active, type);
    }
  }

  public void getEventAddCategory() {
    compositeSubscription.add(rxBus.toObserverable()
        .ofType(AddCategoryEvent.class)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new SimpleObserver<AddCategoryEvent>() {
          @Override public void onNext(AddCategoryEvent addCategoryEvent) {
            Timber.e("iki nang kene");
            if (addCategoryEvent.userCategory != null) {
              getMvpView().onAddCategory();
            }
          }
        }));
  }

  public void getCategegoryList(int requestid, String active, String type) {
    compositeSubscription.add(
        ObservableGetCategory(active, type, true, true, null).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(fragmentCategoryModels -> {
              getMvpView().setAdapter(fragmentCategoryModels);
              getMvpView().onNext(requestid);
            }, throwable -> {
              getMvpView().onError(requestid);
            }, () -> {
              getMvpView().onComplete(requestid);
            }));
  }

  public void getCategegoryList(int requestid, String active, String type, boolean is_finish,
      boolean add_button_showed, AdapterCategory.OnCategoryClick onCategoryClick) {
    compositeSubscription.add(ObservableGetCategory(active, type, is_finish, add_button_showed,
        onCategoryClick).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(fragmentCategoryModels -> {
          getMvpView().setAdapter(fragmentCategoryModels);
          getMvpView().onNext(requestid);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          getMvpView().onError(requestid);
        }, () -> {
          getMvpView().onComplete(requestid);
        }));
  }

  public void saveSubCategory(int id_category_parent, String color, String color_name,
      String name) {
    getMvpView().onLoad(1);
    compositeSubscription.add(
        dataManager.createCategory(id_category_parent, color, color_name, name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(data -> {
              if (data.status.equalsIgnoreCase("success")) {
                UserCategory userCategory = new UserCategory(data.data.user_category);
                if (db.UpdateUserCategory(userCategory) > 0) {
                  getMvpView().onNext(1);
                }
                rxBus.send(new AddCategoryEvent(userCategory));
              } else {
                getMvpView().onError(1);
              }
            }, throwable -> {
              Timber.e("ERROR " + throwable);
              getMvpView().onError(1);
            }, () -> {
              getMvpView().onComplete(1);
            }));
  }

  public void changeCategory(int id_category, String name,String desc) {
    ProgressDialog dialog = new ProgressDialog(context);
    dialog.setMessage("Mengubah category...");
    dialog.show();
    compositeSubscription.add(dataManager.renameCategory(id_category, name,desc)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(renameCategoryResponse -> {
          if(renameCategoryResponse.status.equalsIgnoreCase("success")){
            db.renameCategory(id_category,name,desc);
            getMvpView().onNext(1);
          }else{
            getMvpView().onError(1);
            dialog.dismiss();
          }
        }, throwable -> {
          getMvpView().onError(1);
          getMvpView().errorRenameCategory();
          dialog.dismiss();
        }, () -> {
          getMvpView().onComplete(1);
          getMvpView().finishRenameCategory();
          dialog.dismiss();
        }));
  }
}
