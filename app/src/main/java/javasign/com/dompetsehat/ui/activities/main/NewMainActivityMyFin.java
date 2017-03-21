package javasign.com.dompetsehat.ui.activities.main;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.accountkit.AccountKit;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.services.ActivitiesReminderService;
import javasign.com.dompetsehat.services.AlarmService;
import javasign.com.dompetsehat.services.ReminderService;
import javasign.com.dompetsehat.services.SyncIntentService;
import javasign.com.dompetsehat.ui.activities.budget.BudgetSetupActivity;
import javasign.com.dompetsehat.ui.activities.notification.GlobalNotificationActivity;
import javasign.com.dompetsehat.ui.activities.profile.ProfileActivity;
import javasign.com.dompetsehat.ui.activities.search.SearchActivity;
import javasign.com.dompetsehat.ui.activities.setting.export.ExportSettingActivity;
import javasign.com.dompetsehat.ui.dialogs.DSAdvancedDialog;
import javasign.com.dompetsehat.ui.dialogs.MenuDialog;
import javasign.com.dompetsehat.ui.fragments.budget.BudgetFragment;
import javasign.com.dompetsehat.ui.fragments.finplan.listfragment.PlanFragment;
import javasign.com.dompetsehat.ui.fragments.more.MoreFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.OverviewFragment;
import javasign.com.dompetsehat.ui.fragments.timeline.NewTimelineFragment;
import javasign.com.dompetsehat.utils.ConnectionDetector;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.view.CustomSpinner;
import javasign.com.dompetsehat.view.CustomSpinnerAdapter;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/4/16.
 */
public class NewMainActivityMyFin extends ParentMain implements TabHost.OnTabChangeListener {

  @Bind(android.R.id.tabhost) FragmentTabHost mTabHost;
  @Bind(R.id.tv_title) TextView actionBarTitle;
  @Bind(R.id.iv_user_pict) RoundedImageView iv_user_pict;
  @Bind(R.id.ic_search) IconicsTextView ic_search;
  @Bind(R.id.ic_notif) View ic_notif;
  @Bind(R.id.ic_menu) IconicsTextView ic_menu;
  @Bind(R.id.iv_logo) ImageView iv_logo;
  @Bind(R.id.spinner) CustomSpinner spinner;
  @Bind(R.id.tab_container) RelativeLayout tab_container;

  private ArrayAdapter spinnerAdapter;

  private String[] unchecked_icons = new String[] {
      DSFont.Icon.dsf_transactions.getFormattedName(),
      DSFont.Icon.dsf_overview_2.getFormattedName(), DSFont.Icon.dsf_finplan.getFormattedName(),
      DSFont.Icon.dsf_budget.getFormattedName(), DSFont.Icon.dsf_dots_horizontal.getFormattedName()
  };
  private String[] checked_icons = new String[] {
      DSFont.Icon.dsf_transactions_filled.getFormattedName(),
      DSFont.Icon.dsf_overview_filled.getFormattedName(),
      DSFont.Icon.dsf_finplan_filled.getFormattedName(),
      DSFont.Icon.dsf_budget_filled.getFormattedName(),
      DSFont.Icon.dsf_dots_horizontal_filled.getFormattedName()
  };

  private TextView currentTextChecked = null;
  private DbHelper db;
  private SessionManager session;
  private LoadAndSaveImage loadAndSaveImage;
  private AlarmManager alarmManager;
  private PendingIntent reminderIntent, alarmIntent, reminderActivitiesIntent, syncActivitiesIntent;
  private Handler pendingHandler;
  private GregorianCalendar calendar;
  private MenuDialog filterMenuDialog;
  private int notifCount = 0;

  private ProgressDialog progressDialog;
  @Inject RxBus rxBus = MyCustomApplication.getRxBus();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_main_my_fin);
    ButterKnife.bind(this);
    AccountKit.initialize(this);
    init();
    State.registerBroadCast(this, onDataUpdated);
    progressDialog = new ProgressDialog(this);
    Handler handler = new Handler();
    handler.postDelayed(() -> {
      if (getIntent().hasExtra(State.GO_ACTIVITY)) {
        try {
          String className = getIntent().getExtras().getString(State.GO_ACTIVITY);
          Timber.e("avesina className " + className);
          Intent i = getIntent().setClass(this, Class.forName(className));
          startActivity(i);
        } catch (ClassNotFoundException e) {
          Timber.e("ClassNotFoundException " + e);
        }
      }
    }, 1000);

    //startService(new Intent(this, AccountBankSyncService.class).putExtra(AccountBankSyncService.ACCOUNT_ID,7926));
  }

  @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      //startActivity(new Intent(this, WebviewInstutisiActivity.class)
      //    .putExtra("url",new MCryptNew().decrypt(State.URL_MAIN_MAMI))
      //    .putExtra("from", "finalregsitration"));
      Toast.makeText(this, "ID " + session.getIdUser(), Toast.LENGTH_LONG).show();
      Helper.exportDB(this);
      return BuildConfig.DEBUG || BuildConfig.DEBUG;
    }
    return super.onKeyLongPress(keyCode, event);
  }

  @OnClick(R.id.iv_user_pict) void userPict() {
    startActivity(new Intent(this, ProfileActivity.class));
  }

  @OnClick(R.id.ic_search) void doSearch(View v) {
    getAction(mTabHost.getCurrentTabTag());
  }

  @OnClick(R.id.ic_menu) void onRightIconClick(View v) {
    directTo(mTabHost.getCurrentTabTag());
  }

  private void init() {
    db = DbHelper.getInstance(this);
    session = new SessionManager(this);
    initializeService();
    saveDefaultBarStyle();
    initFragmentTabView();
    loadAndSaveImage = new LoadAndSaveImage(this);
    loadAndSaveImage.executePingFile();

    if ((!session.isLoggedIn() && db.isUserEmpty())
        || (db.isUserEmpty() && session.isLoggedIn())
        || !session.isLoggedIn()) {

      session.logoutUser();
      clearAlarmIfExist();
      return;
    } else {
      service();
      ConnectionDetector internet = new ConnectionDetector(this);
      if (internet.isConnectingToInternet()) {
        if (new SessionManager(this).isFirstTime()) {
          getCashFlow();
        }
      }
    }

    prepareToBudgetSetup();
    iv_user_pict.setImageBitmap(getAvatar());
    String[] filter_menu = new String[] { "Rangkap", "Perbulan", "Pertahun" };
    filterMenuDialog = MenuDialog.newInstance("Sorting Berdasarkan", filter_menu);
  }

  public void populateSpinner(ArrayList<?> datas, String[] labels) {
    spinnerAdapter = new CustomSpinnerAdapter(this, 0, labels);
    if (datas != null) {
      spinnerAdapter = new CustomSpinnerAdapter(this, 0, labels, datas);
    }
    spinner.setAdapter(spinnerAdapter);
    spinner.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
      @Override public void onSpinnerOpened() {
        spinner.getRealAdapter().setInitialPositionWhenOpenned(spinner.getSelectedItemPosition());
      }

      @Override public void onSpinnerClosed() {

      }
    });
    spinner.setDropDownVerticalOffset(
        getResources().getDimensionPixelSize(R.dimen.ds_action_bar_size));
  }

  public void setFilterMenuDialogMenu(String[] filter_menu) {
    filterMenuDialog.setLabels(filter_menu);
  }

  public void setFilterMenuDialogTitle(String title) {
    filterMenuDialog.setTitle(title);
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (spinner.hasBeenOpened() && hasFocus) {
      spinner.performClosedEvent();
    }
  }

  @Override public void dismisDialog() {
    super.dismisDialog();
  }

  private void prepareToBudgetSetup() {
    if (session.isUserHasLaunchedAppOnce()) {
      return;
    }
    User user = db.getUser(session.getIdUser(), DbHelper.TAB_USER);
    //Toast.makeText(this," "+user.getPenghasilan(),Toast.LENGTH_LONG).show();
    if (user.getPenghasilan() <= 0) {
      Helper.goTo(this, BudgetSetupActivity.class);
    }
    Helper.trackThis(this, "User telah berhasil masuk ke halaman utama MyFin (transaksi)");
  }

  private void initFragmentTabView() {
    mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
    mTabHost.getTabWidget().setDividerDrawable(new ColorDrawable(Color.WHITE));

    setNewTab(NewTimelineFragment.class, getString(R.string.tab_title_transactions),
        unchecked_icons[0]);
    setNewTab(OverviewFragment.class, getString(R.string.tab_title_report), unchecked_icons[1]);
    setNewTab(PlanFragment.class, getString(R.string.tab_title_plan), unchecked_icons[2]);
    setNewTab(BudgetFragment.class, getString(R.string.tab_title_budget), unchecked_icons[3]);
    setNewTab(MoreFragment.class, getString(R.string.tab_title_more), unchecked_icons[4]);

    Bundle b = getIntent().getExtras();
    if (b != null) {
      setTab(b.getString("default", State.FLAG_FRAGMENT_TIMELINE));
    } else {
      setTab(State.FLAG_FRAGMENT_TIMELINE);
    }
  }

  public void initializeService() {
    Timber.d("initializeService");
    Intent myIntent = new Intent(this, ReminderService.class);
    reminderIntent = PendingIntent.getService(this, NewMainActivity.SERVICEREMINDER, myIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    Intent myIntent2 = new Intent(this, AlarmService.class);
    alarmIntent = PendingIntent.getService(this, NewMainActivity.SERVICEALARM, myIntent2,
        PendingIntent.FLAG_UPDATE_CURRENT);

    Intent myIntent3 = new Intent(this, ActivitiesReminderService.class);
    reminderActivitiesIntent =
        PendingIntent.getService(this, NewMainActivity.SERVICEACTVITIESREMINDER, myIntent3,
            PendingIntent.FLAG_UPDATE_CURRENT);

    Intent myIntent4 = new Intent(this, SyncIntentService.class);
    syncActivitiesIntent = PendingIntent.getService(this, NewMainActivity.SERVICESYNC, myIntent4,
        PendingIntent.FLAG_UPDATE_CURRENT);
    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
  }

  private void setNewTab(Class<?> clss, String label, String iconFont) {
    Bundle b = new Bundle();
    b.putString("key", label);
    mTabHost.addTab(
        mTabHost.newTabSpec(label.toLowerCase()).setIndicator(createTabButton(iconFont, label)),
        clss, b);
    mTabHost.setOnTabChangedListener(this);
  }

  private View createTabButton(String icon, String label) {
    View view = getLayoutInflater().inflate(R.layout.tab_button, null);

    IconicsTextView tvIcon = ButterKnife.findById(view, R.id.tab_icon);
    TextView tvTabText = ButterKnife.findById(view, R.id.tab_text);

    tvIcon.setText(icon);
    tvTabText.setText(label);

    return view;
  }

  private void setTab(String tagFragment) {
    if (tagFragment == null) return;

    mTabHost.setCurrentTabByTag(tagFragment.toLowerCase());
    onTabChanged(tagFragment.toLowerCase());
    setBarStyle(tagFragment.toLowerCase());
  }

  private void setBarStyle(String tagFragment) {
    if (tagFragment == null) return;

    resetStyleToDefault();
    if (!tagFragment.toLowerCase().equalsIgnoreCase(State.FLAG_FRAGMENT_TIMELINE)) {
      hideViews(R.id.ic_search);
    }
    if (tagFragment.toLowerCase().equalsIgnoreCase(State.FLAG_FRAGMENT_OVERVIEW)) {
      ic_search.setText(DSFont.Icon.dsf_filter.getFormattedName());
      ic_search.setVisibility(View.VISIBLE);
    }
    changeBarStyle(tagFragment);
  }

  private void hideViews(int... resIds) {
    for (int resId : resIds) {
      ButterKnife.findById(this, resId).setVisibility(View.GONE);
    }
  }

  private void resetStyleToDefault() {
    iv_logo.setColorFilter(Color.BLACK);
    spinner.setVisibility(View.GONE);
    iv_logo.setColorFilter((ColorFilter) iv_logo.getTag());
    iv_logo.setVisibility(View.VISIBLE);

    ic_menu.setTextColor((int) ic_menu.getTag());
    ic_menu.setText((String) ic_menu.getTag(R.id.ic_menu));
    ic_menu.setTag(R.id.btn_info, false);

    ic_search.setTextColor((int) ic_search.getTag());
    ic_search.setText(DSFont.Icon.dsf_search.getFormattedName());

    tab_container.setBackgroundColor((int) tab_container.getTag());
    GeneralHelper.statusBarColor(getWindow(), ContextCompat.getColor(this, R.color.grey_800));
    hideViews(R.id.ic_search);
  }

  private void saveDefaultBarStyle() {
    ic_menu.setTag(ic_menu.getCurrentTextColor());
    ic_menu.setTag(R.id.ic_menu, DSFont.Icon.dsf_notification_2.getFormattedName());
    ic_search.setTag(ic_search.getCurrentTextColor());
    tab_container.setTag(Color.WHITE);
  }

  private void directTo(String fragmentTag) {
    if (fragmentTag.equalsIgnoreCase(State.FLAG_FRAGMENT_OVERVIEW)) {
      Helper.goTo(this, ExportSettingActivity.class);
      //openExportDialogOverview();
      Helper.trackThis(this, "user klik export data di laporan");
    } else {
      Helper.goTo(this, GlobalNotificationActivity.class);
    }
  }

  private void getAction(String fragmentTag) {
    if (fragmentTag.equalsIgnoreCase(State.FLAG_FRAGMENT_OVERVIEW)) {
      openDialogFilterOverview();
      return;
    }
    startActivity(new Intent(this, SearchActivity.class).putExtra(SearchActivity.KEY_FLAG_FROM,
        SearchActivity.FLAG_SET_SEARCH_FOR_TRANSACTION));
    Helper.trackThis(this, "user klik search (dari home)");
  }

  private void openDialogFilterOverview() {
    int checkedPos = filterMenuDialog.getLastSelectedPosition();
    filterMenuDialog.setDefaultCheckedItem(checkedPos);
    filterMenuDialog.hideOkButton(true);
    progressDialog.setCancelable(false);
    filterMenuDialog.show(getSupportFragmentManager(), "filter");
  }

  private void openExportDialogOverview() {
    String[] export_menu = new String[] { "PDF", "CSV" };
    MenuDialog menuDialog = MenuDialog.newInstance(getString(R.string.save_as), export_menu);
    progressDialog.setCancelable(false);
    menuDialog.show(getSupportFragmentManager(), "ekspor");
    menuDialog.simpleModeOn();
    menuDialog.setOnMenuDialogClick(new MenuDialog.OnMenuDialogClick() {
      @Override public void onOkClick(String item, int pos) {

      }

      @Override public void onMenuClick(String label, int pos, Dialog dialog) {
        switch (export_menu[pos]) {
          case "PDF":
            progressDialog.setMessage(getString(R.string.exporting));
            progressDialog.show();
            presenter.exportToPDF(NewMainActivityMyFin.this);
            break;
          case "CSV":
            progressDialog.setMessage(getString(R.string.exporting));
            progressDialog.show();
            presenter.exportToCSV();
            break;
        }
        menuDialog.dismissAllowingStateLoss();
      }
    });
  }

  public void clearAlarmIfExist() {
    alarmManager.cancel(reminderIntent);
    alarmManager.cancel(alarmIntent);
    alarmManager.cancel(reminderActivitiesIntent);
    alarmManager.cancel(syncActivitiesIntent);
  }

  private void service() {
    startAlarmService(false);
    if (session.getActivitiesReminder()) {
      startReminderActiviesService(false);
    } else {
      alarmManager.cancel(reminderActivitiesIntent);
    }
    startReminderService(false);
    startSyncService(false);
  }

  private void startAlarmService(Boolean debug) {
    if (debug == null) {
      debug = false;
    }
    calendar = (GregorianCalendar) Calendar.getInstance();

    /* debug setup */
    calendar.add(Calendar.SECOND, 30);
    Long INTERVAL = 5000L;
    /**/

    if (!debug) {
      INTERVAL = AlarmManager.INTERVAL_DAY;
      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, 7);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DATE, 1);
      }
    }
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL,
        alarmIntent);
  }

  private void startReminderService(Boolean debug) {
    if (debug == null) {
      debug = false;
    }
    calendar = (GregorianCalendar) Calendar.getInstance();

    /* debug setup */
    calendar.add(Calendar.SECOND, 30);
    Long INTERVAL = 5000L;
    /**/

    if (!debug) {
      INTERVAL = AlarmManager.INTERVAL_DAY;
      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, 21);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DATE, 1);
      }
    }

    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL,
        reminderIntent);
  }

  private void startReminderActiviesService(Boolean debug) {
    if (debug == null) {
      debug = false;
    }
    calendar = (GregorianCalendar) Calendar.getInstance();

    /* debug setup */
    calendar.add(Calendar.SECOND, 30);
    Long INTERVAL = 5000L;
    /**/

    if (!debug) {
      INTERVAL = AlarmManager.INTERVAL_DAY;
      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, session.getHoursActivitiesReminder());
      calendar.set(Calendar.MINUTE, session.getMinutesActivitiesReminder());
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DATE, 1);
      }
    }

    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL,
        reminderActivitiesIntent);
  }

  private void startSyncService(Boolean debug) {
    if (debug == null) {
      debug = false;
    }
    calendar = (GregorianCalendar) Calendar.getInstance();

    /* debug setup */
    calendar.add(Calendar.SECOND, 30);
    Long INTERVAL = 60000L;
    /**/

    if (!debug) {
      INTERVAL = AlarmManager.INTERVAL_DAY;
      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, session.getHoursSync());
      calendar.set(Calendar.MINUTE, session.getMinutesSync());
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DATE, 1);
      }
    }

    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL,
        syncActivitiesIntent);
  }

  @Override public void onTabChanged(String tabId) {
    setBarStyle(tabId);
    int idx = mTabHost.getCurrentTab();
    IconicsTextView icon = (IconicsTextView) mTabHost.getTabWidget().getChildAt(idx).findViewById(R.id.tab_icon);
    TextView label = (TextView) mTabHost.getTabWidget().getChildAt(idx).findViewById(R.id.tab_text);
    changeTabIcon(icon, label, idx);
    presenter.initNotif(0);
  }

  private void changeBarStyle(String fragmentTag) {
    if (fragmentTag.equalsIgnoreCase(State.FLAG_FRAGMENT_OVERVIEW)) {
      spinner.setSelection(0); //default
      spinner.setVisibility(View.VISIBLE);
      ic_menu.setVisibility(View.VISIBLE);
      ic_menu.setText(DSFont.Icon.dsf_download.getFormattedName());
      hideViews(R.id.ic_notif, R.id.iv_logo);
      populateSpinner(null, getResources().getStringArray(R.array.spinner_bar_labels));
    } else if (fragmentTag.equalsIgnoreCase(State.FLAG_FRAGMENT_TIMELINE)) {
      spinner.setVisibility(View.VISIBLE);
      ic_search.setVisibility(View.VISIBLE);
      ic_menu.setVisibility(View.VISIBLE);
      ic_menu.setText(DSFont.Icon.dsf_notification_2.getFormattedName());
      hideViews(R.id.iv_logo);
      populateSpinner(null, new String[] { getString(R.string.all_account) });
      presenter.initSpinner();
    }
  }

  private void changeTabIcon(IconicsTextView target, TextView label, int pos) {
    target.setText(checked_icons[pos]);

    if (currentTextChecked != null) {
      deselectOtherButton(currentTextChecked, pos);
      currentTextChecked = null;
    }

    Helper.animateGrowing(label);
    currentTextChecked = label;
  }

  private void deselectOtherButton(TextView text, int currentPos) {
    for (int i = 0; i < unchecked_icons.length; i++) {
      if (i != currentPos) {
        IconicsTextView icon =
            (IconicsTextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_icon);
        icon.setText(unchecked_icons[i]);
      }
    }

    Helper.animateShrinking(text);
  }

  public void setSimpleBarOnSelectedListener(AdapterView.OnItemSelectedListener onCheckedListener) {
    spinner.setOnItemSelectedListener(onCheckedListener);
  }

  public ArrayList<?> getSimpleBarData() {
    return spinner.getRealAdapter().getDatas();
  }

  public void setOnMenuFilterDialogListener(MenuDialog.OnMenuDialogClick onMenuDialogClick) {
    filterMenuDialog.setOnMenuDialogClick(onMenuDialogClick);
  }

  //TODO: Showing optional dialog from agent
  private void showNotificationDialogFromAgent() {
    new DSGoNotifHelper(this).withSimpleButtonListener(
        new DSAdvancedDialog.SimpleOnButtonsClicked() {
          @Override //call this to return checked item
          public void positive(Dialog alertDialog, LinkedHashMap<Integer, Boolean> checkedMap) {
            super.positive(alertDialog, checkedMap);
          }
        }).show(getFragmentManager());
  }

  @Override public void onLoad(int requestid) {
    super.onLoad(requestid);
  }

  @Override public void onError(int requestid) {
    super.onError(requestid);
    progressDialog.dismiss();
  }

  @Override public void onComplete(int requestid) {
    super.onComplete(requestid);
    progressDialog.dismiss();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
    fragment.onActivityResult(requestCode, resultCode, data);
    if (resultCode == State.BUDGET) {
      pendingHandler = new Handler();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (!Helper.isEmptyObject(pendingHandler)) {
      pendingHandler.postDelayed(() -> setTab(State.FLAG_FRAGMENT_BUDGET), 300);
      pendingHandler = null;
    }
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    State.unRegisterBroadCast(this, onDataUpdated);
    super.onDestroy();
  }

  private BroadcastReceiver onDataUpdated = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      dismisDialog();
    }
  };

  private static final int TIME_INTERVAL = 2000;
  private long mBackPressed;

  @Override public void onBackPressed() {
    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
      super.onBackPressed();
      return;
    } else {
      Toast.makeText(getBaseContext(), getString(R.string.press_once_again_to_exit),
          Toast.LENGTH_SHORT).show();
    }

    mBackPressed = System.currentTimeMillis();
  }

  public void showNotif(int count, int tabIndex) {
    View view = mTabHost.getTabWidget().getChildTabViewAt(tabIndex);
    TextView tab_notif = ButterKnife.findById(view, R.id.tab_notif);
    TextView ic_notif = ButterKnife.findById(this, R.id.ic_notif);
    int visibility = count == 0 ? View.GONE : View.VISIBLE;
    tab_notif.setVisibility(visibility);
    ic_notif.setVisibility(visibility);
    String label = count > 99 ? 99 + "+" : String.valueOf(count);

    if (mTabHost == null) return;

    if (mTabHost.getCurrentTab() == 0) {
      tab_notif.setVisibility(View.GONE);
    }

    if (mTabHost.getCurrentTab() == 1) {
      ic_notif.setVisibility(View.GONE);
    }

    Timber.e("avesina showNotif "+mTabHost.getCurrentTab());

    tab_notif.setText(label);
    ic_notif.setText(label);
  }

  public void setVisibilityMenu(int Visibility) {
    ic_search.setVisibility(Visibility);
  }

  private int getNotifCount() {
    return notifCount;
  }
}
