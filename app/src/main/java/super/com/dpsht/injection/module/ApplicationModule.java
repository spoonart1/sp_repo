package javasign.com.dompetsehat.injection.module;


import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.lang.reflect.Modifier;

import java.util.concurrent.TimeUnit;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.WatcherUtils;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.rx_cache.internal.RxCache;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.data.CacheProviders;
import javasign.com.dompetsehat.data.HeaderInterceptor;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.services.DompetSehatService;
import javasign.com.dompetsehat.utils.RxBus;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by pratama on 3/11/16.
 */
@Module
public class ApplicationModule {

  protected final Application application;

  public ApplicationModule(Application application) {
    this.application = application;
  }

  @Provides
  Application provideApplication() {
    return application;
  }

  @Provides @ApplicationContext
  Context provideContext() {
    return application;
  }

  @Provides @Singleton OkHttpClient provideHttpClient(@ApplicationContext Context context) {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
        .addInterceptor(new HeaderInterceptor(context))
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120,TimeUnit.SECONDS)
        .build();
    return httpClient;
  }

  @Provides @Singleton
  Gson provideGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
    return gsonBuilder.create();
  }

  @Provides @Singleton
  DompetSehatService provideDompetSehatService(OkHttpClient httpClient, Gson gson) {
    return new DompetSehatService.Creator().newDompetSehatService(httpClient,gson);
  }

  @Provides @Singleton static RxBus provideRxBus() {
    return new RxBus();
  }

  @Provides @Singleton static GeneralHelper provideHelper(){
    return new GeneralHelper();
  }

  @Provides
  CacheProviders provideCacheProvider(@ApplicationContext Context context) {
    File cacheDir = context.getFilesDir();
    return new RxCache.Builder().persistence(cacheDir).using(CacheProviders.class);
  }

  @Provides SyncPresenter provideSyncPresenter(@ApplicationContext Context context,DbHelper db,DataManager dataManager, Gson gson,FirebaseAuth auth){
    return new SyncPresenter(context,db,dataManager,gson,auth);
  }

  @Provides WatcherUtils provideWatcherUtils(@ApplicationContext Context context,DbHelper db,FirebaseDatabase database){
    return new WatcherUtils(context,db);
  }

  @Provides @Singleton static FirebaseDatabase provideFirebaseDatabase(){
    return FirebaseDatabase.getInstance();
  }

  @Provides @Singleton static FirebaseAuth provideFirebaseAuth(){
    return FirebaseAuth.getInstance();
  }



//  @Provides @Singleton
//  SessionManager provideSessionManager(@ApplicationContext Application application) {
//    return new SessionManager(application);
//  }

//  @Provides LoadAndSaveImage provideLoadAndSaveImage(@ApplicationContext Context context){
//    return new LoadAndSaveImage(context);
//  }

}
