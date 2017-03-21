package javasign.com.dompetsehat.presenter.tag;

import android.content.Context;
import java.util.ArrayList;
import java.util.Arrays;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.presenter.transaction.TransactionsInterface;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 2/2/17.
 */

public class TagPresenter extends BasePresenter<TagInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final Context context;
  public final DbHelper db;
  public final SessionManager session;

  @Inject
  public TagPresenter(@ApplicationContext Context context, DataManager dataManager, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    //init();
  }

  @Override public void attachView(TagInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadTags() {
    ArrayList<String> tags = new ArrayList<>();
    compositeSubscription.add(Observable.from(db.getAllTag(session.getIdUser()))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(s -> s)
        .subscribe(tag -> {
          Timber.e("avesina " + tag);
          tags.add(tag);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().setTags(tags);
        }));
  }

  public void deleteTags(String tag) {
    ArrayList<Cash> cashes = db.getCashbyTag(session.getIdUser(), tag);
    Timber.e("ERROR "+cashes.size());
    compositeSubscription.add(Observable.from(cashes)
        .map(cash -> cash)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cash -> {
          String tags = cash.getCash_tag();
          String[] arr_tags = tags.toLowerCase().split(",");
          ArrayList<String> list_tags = new ArrayList<String>();
          list_tags.addAll(Arrays.asList(arr_tags));
          list_tags.remove(tag);
          Timber.e("ERROR "+list_tags.get(0));
          db.updateTag(cash.getId(), Helper.imploded(list_tags.toArray(new String[0]), ","));
        }, throwable -> {
          getMvpView().onError(throwable.getMessage());
          Timber.e("ERROR "+throwable);
        }, () -> {
          getMvpView().finishDelete();
        }));
  }

  public void updateTags(String before, String after) {
    ArrayList<Cash> cashes = db.getCashbyTag(session.getIdUser(), before);
    compositeSubscription.add(Observable.from(cashes)
        .map(cash -> cash)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cash -> {
          String tags = cash.getCash_tag();
          String[] arr_tags = tags.toLowerCase().split(",");
          ArrayList<String> list_tags = new ArrayList<String>();
          list_tags.addAll(Arrays.asList(arr_tags));
          int i = list_tags.indexOf(before);
          if(i>=0){
            list_tags.remove(i);
            list_tags.add(i,after);
            db.updateTag(cash.getId(), Helper.imploded(list_tags.toArray(new String[0]), ","));
          }
        }, throwable -> {
          getMvpView().onError(throwable.getMessage());
          Timber.e("ERROR "+throwable);
        }, () -> {
          getMvpView().finishUpdate();
        }));
  }
}
