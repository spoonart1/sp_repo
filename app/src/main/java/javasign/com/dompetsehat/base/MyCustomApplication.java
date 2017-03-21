package javasign.com.dompetsehat.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.WindowManager;
import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;
import io.fabric.sdk.android.Fabric;
import java.util.HashMap;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.injection.component.ApplicationComponent;
import javasign.com.dompetsehat.injection.component.DaggerApplicationComponent;
import javasign.com.dompetsehat.injection.module.ApplicationModule;
import javasign.com.dompetsehat.job.DompetSehatJobCreator;
import javasign.com.dompetsehat.job.SyncJob;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.GloblaLockScreen;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Spoonart on 9/25/2015.
 */
public class MyCustomApplication extends Application {
  private final static String DEFAULT_POSITION = "NewMainActivity";

  private static Context currentContext;
  private static String PreviousClass = DEFAULT_POSITION;
  private static String CurrentClass = "";
  private static int FROM_PAUSED = 1;
  private static int FROM_RESUMED = 2;
  private static int currentFlag = 0;

  // avesina
  private static RxBus rxBus;
  public static String class_name = null;
  private static CalligraphyConfig fontConfig;
  private ApplicationComponent applicationComponent;
  @Inject SessionManager session;

  private static final String PROPERTY_ID = "UA-64050383-3";
  HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

  public enum TrackerName {
    APP_TRACKER, // Tracker used only in this app.
    GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
  }

  public MyCustomApplication() {
    super();
  }

  public static RxBus getRxBus() {
    if (rxBus == null) {
      rxBus = new RxBus();
    }
    return rxBus;

  }

  public static void disposeRxBus() {
    if (rxBus != null) {
      rxBus = null;
    }
  }

  public void disposeApplicationComponent(){
    if (applicationComponent != null) {
      applicationComponent = null;
    }
  }

  public static MyCustomApplication get(Context context) {
    return (MyCustomApplication) context.getApplicationContext();
  }

  public ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(new ApplicationModule(this))
          .build();
    }
    return applicationComponent;
  }

  synchronized public Tracker getTracker(TrackerName trackerId) {
    if (!mTrackers.containsKey(trackerId)) {
      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
      Tracker t = null;
      if (trackerId == TrackerName.APP_TRACKER) {
        t = analytics.newTracker(PROPERTY_ID);
      }
      mTrackers.put(trackerId, t);
    }
    return mTrackers.get(trackerId);
  }

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @Override public void onCreate() {
    super.onCreate();
    MyCustomApplication.currentContext = getApplicationContext();
    initialization();
  }

  protected void fabricChecker() {
    boolean initNow = false;
    try {
      for (String deviceId : Helper.DEVELOPER_DEVICE_ID.split("#")) {
        if (!Helper.getDeviceId(this).equalsIgnoreCase(deviceId)) {
          initNow = true;
        } else {
          initNow = false;
          break;
        }
      }
    } catch (Exception e) {
      Timber.e("ERROR getDeviceId " + e);
      initNow = false;
    }

    if (initNow) {
      if (!Fabric.isInitialized()) {
        Fabric.with(this, new Crashlytics());
      } else {
        Fabric.with(this, Crashlytics.getInstance());
      }
      System.out.println("MyCustomApplication: You are not developer : INIT FABRIC.IO SUCCEEDED");
    } else {
      System.out.println("MyCustomApplication : You are developer : INIT FABRIC.IO SKIPPED");
    }
  }

  private static CalligraphyConfig getFontConfig() {
    if (fontConfig == null) {
      return new CalligraphyConfig.Builder().setDefaultFontPath("fonts/default_app_font.ttf")
          .setFontAttrId(R.attr.fontPath)
          .build();
    }
    return fontConfig;
  }

  private void initialization() {
    if (this.session == null) {
      this.session = new SessionManager(getApplicationContext());
    }

    setupFBSDK();
    //CalligraphyConfig.initDefault(getFontConfig());
    fabricChecker();
    timberChecker();
    registerFonts();
    registerJob();
    FirebaseDatabase.getInstance().setPersistenceEnabled(false);

    if (!BuildConfig.DEBUG_MODE || !BuildConfig.DEBUG) {
      if (session.isLoggedIn()) Crashlytics.setUserIdentifier(session.getIdUser());
      getTracker(TrackerName.APP_TRACKER).set("&uid", session.getIdUser());
    }

    if(session.isLoggedIn()){
      FirebaseAuth auth = FirebaseAuth.getInstance();
      if(auth.getCurrentUser() == null){
        session.logoutUser();
      }
    }

    System.out.println("MyCustomApplication.initialization density" + checkScreenResolution());
  }

  private static void checker() {
    Timber.e("passcode checker "+currentContext+" currentFlag "+currentFlag+"="+FROM_RESUMED+" CurrentClass "+CurrentClass+"="+PreviousClass);
    if(currentContext != null) {
      if(currentFlag == FROM_RESUMED && CurrentClass.equalsIgnoreCase(PreviousClass)) {
        Timber.e("passcode "+PreviousClass+" heloo "+!PreviousClass.equalsIgnoreCase("PassCodeActivity"));
        if(!PreviousClass.equalsIgnoreCase("PassCodeActivity") && !PreviousClass.equalsIgnoreCase("SettingActivity")) {
          GloblaLockScreen.performPasscodeDialog(currentContext);
        }
      }
      else if(currentFlag == FROM_RESUMED && !CurrentClass.equalsIgnoreCase(PreviousClass)) {
        PreviousClass = CurrentClass;
      }
    }
  }

  private void registerJob(){
    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    if (currentapiVersion >= Build.VERSION_CODES.N){
      JobManager.create(getApplicationContext()).addJobCreator(new DompetSehatJobCreator());
      SyncJob.scheduleJob();
    }
  }

  private void registerFonts() {
    DSFont dsFont = DSFont.getInstance();
    GoogleMaterial googleMaterialFont = new GoogleMaterial();
    CommunityMaterial communityMaterial = new CommunityMaterial();
    Iconics.registerFont(dsFont);
    Iconics.registerFont(googleMaterialFont);
    Iconics.registerFont(communityMaterial);
  }

  public void timberChecker() {
    if (BuildConfig.DEBUG || BuildConfig.DEBUG_MODE) {
      Timber.plant(new Timber.DebugTree());
    }
  }

  public String checkScreenResolution() {
    String device = "";
    float density = getResources().getDisplayMetrics().density;
    if (density == 0.75f) {
      device = "LDPI";
    } else if (density == 1.0f) {
      device = "MDPI";
    } else if (density == 1.5f) {
      device = "HDPI";
    } else if (density == 2.0f) {
      device = "XHDPI";
    } else if (density == 3.0f) {
      device = "XXHDPI";
    } else if (density == 4.0f) device = "XXXHDPI";

    return device;
  }

  private void setupFBSDK() {
    FacebookSdk.sdkInitialize(getApplicationContext());
  }

  public static void initTracker(Context activity, String screenName) {
    Tracker tracker = ((MyCustomApplication) activity.getApplicationContext()).getTracker(
        TrackerName.APP_TRACKER);
    tracker.setScreenName(screenName);
  }

  public static void sendEvent(Context activity, String category, String action) {
    Tracker tracker = ((MyCustomApplication) activity.getApplicationContext()).getTracker(
        TrackerName.APP_TRACKER);

    tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).build());
  }

  public static void activityResumed(Context context, String className) {
    MyCustomApplication.currentContext = context;
    CurrentClass = className;
    currentFlag = FROM_RESUMED;
    checker();
  }

  /** A tree which logs important information for crash reporting. */
  private class CrashReportingTree extends Timber.DebugTree {
    private final String CRASHLYTICS_KEY_PRIORITY = "priority";
    private final String CRASHLYTICS_KEY_TAG = "tag";
    private final String CRASHLYTICS_KEY_MESSAGE = "message";

    @Override protected void log(int priority, @Nullable String tag, @Nullable String message,
        @Nullable Throwable t) {
      if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
        return;
      }

      Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority);
      Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag);
      Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message);

      if (t == null) {
        Crashlytics.logException(new Exception(message));
      } else {
        Crashlytics.logException(t);
      }
    }
  }

  public static boolean showInvestasi() {
    return true;
  }

  public static boolean showAllVendor() { return false; }

  public static Integer[] invalidVendor() {
    //MD = 1, BC = 2, BN = 3, BR = 4, TP = 5, DP = 6, PM = 7, BSM = 8, MNL = 10;
    return new Integer[]{5};
  }

  public static boolean directToWeb(){
    return true;
  }

  public void showMessage(Context context,String message) {
    Timber.e("Connected " + message);
    final AlertDialog alertDialog = new AlertDialog.Builder(context)
        .setTitle("Connections")
        .setMessage(message)
        .create();
    alertDialog.show();
  }

  public static boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) get(context).getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  @Override public void onTerminate() {
    disposeRxBus();
    super.onTerminate();
  }

  public static void activityPaused(String className) {
    PreviousClass = className;
    currentFlag = FROM_PAUSED;
  }
}