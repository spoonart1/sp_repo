package javasign.com.dompetsehat.injection.component;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import javasign.com.dompetsehat.base.MyFirebaseInstanceIDService;
import javasign.com.dompetsehat.job.SyncJob;
import javasign.com.dompetsehat.presenter.interceptor.InterceptorPresenter;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.receivers.NetworkChangeReceiver;
import javasign.com.dompetsehat.services.AccountBankSyncService;
import javasign.com.dompetsehat.services.InsertDataService;
import javasign.com.dompetsehat.services.InsertLoginDataService;
import javasign.com.dompetsehat.services.ReminderService;
import javasign.com.dompetsehat.services.SyncIntentService;
import javasign.com.dompetsehat.ui.event.AddBudgetEvent;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.ui.event.AddReminderEvent;
import javasign.com.dompetsehat.ui.event.AddTransactionEvent;
import javasign.com.dompetsehat.ui.event.DeleteAlarmEvent;
import javasign.com.dompetsehat.ui.event.DeleteBudgetEvent;
import javasign.com.dompetsehat.ui.event.DeletePlanEvent;
import javasign.com.dompetsehat.ui.event.DeleteTransactionEvent;
import javasign.com.dompetsehat.utils.WatcherUtils;
import javax.inject.Singleton;

import dagger.Component;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.data.CacheProviders;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.data.HeaderInterceptor;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.injection.module.ApplicationModule;
import javasign.com.dompetsehat.services.DompetSehatService;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import javasign.com.dompetsehat.utils.RxBus;

/**
 * Created by pratama on 3/11/16.
 */
@Singleton @Component(modules = ApplicationModule.class) public interface ApplicationComponent {

  @ApplicationContext Context context();

  void inject(HeaderInterceptor interceptor);
  void inject(InterceptorPresenter presenter);
  void inject(SyncIntentService service);
  void inject(ReminderService service);
  void inject(AccountBankSyncService service);
  void inject(NetworkChangeReceiver networkChangeReceiver);
  void inject(MyFirebaseInstanceIDService service);
  void inject(AddTransactionEvent event);
  void inject(AddBudgetEvent event);
  void inject(AddPlanEvent event);
  void inject(AddReminderEvent event);
  void inject(DeleteTransactionEvent event);
  void inject(DeletePlanEvent event);
  void inject(DeleteBudgetEvent event);
  void inject(DeleteAlarmEvent event);
  void inject(SyncJob job);
  void inject(InsertLoginDataService service);

  Application application();

  DompetSehatService dompetSehatService();

  DataManager dataManager();

  RxBus rxBus();

  SessionManager sessionManager();

  CacheProviders cacheProvider();

  LoadAndSaveImage saveImage();

  DbHelper dbHelper();

  GeneralHelper helper();

  Gson gson();

  SyncPresenter syncPresenter();

  WatcherUtils watcherUtils();

  FirebaseDatabase firebaseDatabase();

  FirebaseAuth authFirebaseAuth();

}
