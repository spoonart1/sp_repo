package javasign.com.dompetsehat.presenter.notification;

import android.content.Context;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.presenter.plan.ChooseProductInterface;
import javasign.com.dompetsehat.ui.activities.notification.pojo.GlobalNotifModel;
import javasign.com.dompetsehat.ui.activities.notification.pojo.TypeNotif;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 3/13/17.
 */

public class NotificationPresenter extends BasePresenter<NotificationInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  private DbHelper db;
  private DatabaseReference mRef;

  @Inject public NotificationPresenter(@ActivityContext Context context, DataManager dataManager,
      DbHelper db, FirebaseDatabase mDatabase) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
    this.mRef = mDatabase.getReference().child("notification");
  }

  @Override public void attachView(NotificationInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadData() {
    RxFirebaseDatabase.observeValueEvent(mRef.child(session.getIdUser()),DataSnapshotMapper.mapOf(GlobalNotifModel.class))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          ArrayList<GlobalNotifModel> notifs = new ArrayList<GlobalNotifModel>();
          for (Map.Entry<String,GlobalNotifModel> map:data.entrySet()){
            GlobalNotifModel notif = map.getValue();
            notif.key = map.getKey();
            notif.typeNotif = TypeNotif.codeType(notif.code);
            notifs.add(notif);
          }
          Collections.reverse(notifs);
          getMvpView().setAdapter(notifs);
        }, throwable -> {
          Timber.e("ERROR "+throwable);
        }, () -> {
        });
  }

  public void readNotif(String key) {
   FirebaseDatabase database =  mRef.child(session.getIdUser()).child(key).getDatabase();
    Timber.e("avesina readNotif "+database);
        //.child("status").setValue("read");
  }
}
