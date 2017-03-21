package javasign.com.dompetsehat.presenter.sharing;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.models.json.agent;
import javasign.com.dompetsehat.models.json.permit;
import javasign.com.dompetsehat.presenter.auth.AuthInterface;
import javasign.com.dompetsehat.ui.activities.setting.sharing.adapters.AdapterMember;
import javasign.com.dompetsehat.ui.activities.setting.sharing.permission.model.PermissionModel;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 1/9/17.
 */

public class SharingDataPresenter extends BasePresenter<SharingDataInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private final DataManager dataManager;
  private final SessionManager session;
  private DbHelper db;

  @Inject public SharingDataPresenter(DataManager dataManager, DbHelper db,
      @ApplicationContext Context context) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(SharingDataInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void getData() {
    ArrayList<AdapterMember.Member> members = new ArrayList<>();
    compositeSubscription.add(dataManager.getRequestList(null)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {
          if (response.status.equalsIgnoreCase("success")) {
            for (agent a : response.data) {
              a.populatePermission();
              members.add(new AdapterMember.Member(a.agent_id, a.agent_name,new HashMap<String, Boolean>(a.h_permission), a.status));
            }
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          getMvpView().setData(members);
        }, () -> {
          getMvpView().setData(members);
        }));
  }

  public void addAgent(String name) {
    compositeSubscription.add(dataManager.add_agent(name,
        new permit().setAlarm(true).setBudget(true).setPlan(true).setTransaction(true))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {

        }, throwable -> {
          Timber.e("ERROR addAgent " + throwable);
        }, () -> {
        }));
  }

  public void saveData(int id_agent, HashMap<String, Boolean> permission) {
    compositeSubscription.add(dataManager.setPermissions(id_agent, permission)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          if(data.status.equalsIgnoreCase("success")){
            getMvpView().onNext();
          }else{
            getMvpView().onError();
          }
        }, throwable -> {
          getMvpView().onError();
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().onComplete();
        }));
  }

  public void setListPermission(Map<String,Boolean> permission) {
    compositeSubscription.add(getModels(permission).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          getMvpView().setAdapterModels(data);
        }, throwable -> {
          Timber.e("ERROR "+throwable);
        }, () -> {
        }));
  }

  private Observable<ArrayList<PermissionModel>> getModels(Map<String,Boolean> permission) {
    ArrayList<PermissionModel> models = new ArrayList<>();
    String[] permissionsLabels = context.getResources().getStringArray(R.array.permission_labels);
    String[] permissionKeys = context.getResources().getStringArray(R.array.permission_key);
    if (permissionsLabels.length != permissionKeys.length) {
      throw new Error("Permission lenght nggak sama");
    }

    for (int i = 0; i < permissionsLabels.length; i++) {
      boolean is_visible = false;
      if(permission.containsKey(permissionKeys[i])){
        is_visible = permission.get(permissionKeys[i]);
      }
      PermissionModel p = new PermissionModel(permissionsLabels[i], permissionKeys[i]).setDataIsVisible(is_visible);
      models.add(p);
    }
    return Observable.just(models);
  }
}
