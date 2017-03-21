package javasign.com.dompetsehat.presenter.setting;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.PassCodeActivity;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.services.SyncIntentService;
import javasign.com.dompetsehat.ui.activities.setting.Setting;
import javasign.com.dompetsehat.ui.activities.setting.SettingActivity;
import javasign.com.dompetsehat.ui.activities.setting.adapter.AdapterSetting;
import javasign.com.dompetsehat.ui.event.SettingCheckBoxEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by avesina on 11/1/16.
 */

public class SettingPresenter extends BasePresenter<SettingInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private GeneralHelper helper;
  private final Context context;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private DbHelper db;
  private SessionManager session;
  private MCryptNew mc = new MCryptNew();
  private DataManager dataManager;
  String[] name_tables;

  @Inject
  public SettingPresenter(@ActivityContext Context context, DbHelper db, GeneralHelper helper,
      DataManager dataManager) {
    this.context = context;
    this.db = db;
    this.session = new SessionManager(context);
    this.helper = helper;
    this.dataManager = dataManager;
    name_tables = new String[] {
        DbHelper.TAB_ACCOUNT, DbHelper.TAB_PRODUCT, DbHelper.TAB_PLAN, DbHelper.TAB_PLAN_PRODUCT,
        DbHelper.TAB_ALARM, DbHelper.TAB_BUDGET, DbHelper.TAB_DANA_KULIAH,
        DbHelper.TAB_DANA_DARURAT, DbHelper.TAB_DANA_PENSIUN, DbHelper.TAB_DANA_PENSIUN,
        DbHelper.TAB_CASH, DbHelper.TAB_USER_CATEGORY
    };
  }

  @Override public void attachView(SettingInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void deleteAllData() {
    getMvpView().startLoading(context.getString(R.string.deleting));
    compositeSubscription.add(dataManager.resetDataUser()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {
          if (response.status.equalsIgnoreCase("success")) {
            deleteOnDB();
            session.removeHaveInstitutionAccount();
          }
        }, throwable -> {
          Timber.e("ERROR "+throwable);
        }, () -> {
        }));
  }

  private void deleteOnDB() {
    compositeSubscription.add(Observable.from(name_tables)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(table -> table)
        .subscribe(table -> {
          Timber.e("table " + table);
          getMvpView().onnext(context.getString(R.string.deleting));
          db.deleteDataTable(table);
        }, throwable -> {
          getMvpView().error();
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().complete(context.getString(R.string.data_has_been_deleted));
        }));
  }

  public void syncData() {
    ArrayList<Cash> cashList = db.getAllCashOffline(session.getIdUser());
    User user = db.getUser(session.getIdUser(), DbHelper.TAB_USER);
    String json = Helper.ListCashFlowToString(cashList, user.getLast_sync_cash());
    Intent intent = new Intent(context, SyncIntentService.class);
    intent.putExtra(SyncIntentService.DATA, json);
    context.startService(intent);
  }

  public void exportData(String method, Account account, String type, String from, String to,
      boolean isShare) {
    getMvpView().startLoading(context.getString(R.string.exporting));
    compositeSubscription.add(
        Observable.just(db.ExportCashflow(account.getIdaccount(), type, from, to))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(cursor -> {
              try {
                String path = null;
                switch (method) {
                  case "PDF":
                    path = Helper.exportPDF(cursor, "Transaction_"
                        + mc.decrypt(account.getName())
                        + "_"
                        + type
                        + "_"
                        + from
                        + "_"
                        + to);
                    break;
                  case "XLS":
                    path = Helper.exportToExcel(cursor, "Transaction_"
                        + mc.decrypt(account.getName())
                        + "_"
                        + type
                        + "_"
                        + from
                        + "_"
                        + to);
                    break;
                  case "CSV":
                    path = Helper.exportCSV(cursor, "Transaction_"
                        + mc.decrypt(account.getName())
                        + "_"
                        + type
                        + "_"
                        + from
                        + "_"
                        + to);
                    break;
                }
                if (isShare) {
                  getMvpView().setPath(path);
                }
              } finally {
                cursor.close();
              }
            }, throwable -> {
            }, () -> {
              getMvpView().complete("");
            }));
    //compositeSubscription.add(Observable.from(name_tables)
    //    .subscribeOn(Schedulers.io())
    //    .observeOn(AndroidSchedulers.mainThread())
    //    .map(table -> table)
    //    .subscribe(table -> {
    //      getMvpView().onnext(context.getString(R.string.exporting));
    //      Cursor cursor = db.selectTable(table);
    //      try {
    //        if (cursor != null) {
    //          Helper.exportCSV(cursor, table + "CSV");
    //        }
    //      } finally {
    //        cursor.close();
    //      }
    //    }, throwable -> {
    //      getMvpView().error();
    //      Timber.e("ERROR " + throwable);
    //    }, () -> {
    //      getMvpView().complete(context.getString(R.string.data_has_been_exported));
    //    }));
  }

  public void populateData() {
    compositeSubscription.add(getData().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          getMvpView().setAdapter(data);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public Observable<List<AdapterSetting.SettingModel>> getData() {
    return Observable.defer(() -> {
      String[] generalDescriptions = new String[] {
          context.getString(R.string.user_profile_wording),
          context.getString(R.string.accounts_wording),
          context.getString(R.string.category_wording),
          context.getString(R.string.tap_to_manage_tag),
          context.getString(R.string.sync_wording),
          context.getString(R.string.group_sharing_wording),
      };
      String initial_date = ": " + session.getInitialDayOfMonth();

      String time_smart_reminder = ": 20.00";
      String[] otherDescriptions = new String[] {
          context.getString(R.string.languange_wording),
          context.getString(R.string.more_switcher_mode),
          //context.getString(R.string.initial_day_of_the_month_wording) + initial_date,
          context.getString(R.string.smart_reminder_wording) + time_smart_reminder,
          context.getString(R.string.passcode_wording),
          context.getString(R.string.export_data_wording),
          context.getString(R.string.change_log_wording),
          context.getString(R.string.about_wording)
      };

      Setting[] generalEnums = new Setting[] {
          Setting.UserProfile,
          Setting.Accounts,
          Setting.Categories,
          Setting.Tags,
          Setting.Synchronisation,
          Setting.GroupSharing,
      };

      Setting[] otherEnums = new Setting[] {
          Setting.Languange,
          Setting.SwitchMode,
          //Setting.InitialDayOfMonth,
          Setting.SmartReminder,
          Setting.PassCode,
          Setting.ExportData,
          Setting.ChangeLog,
          Setting.About,
      };

      AdapterSetting.SettingModel.CheckboxStyle[] checkboxStyles =
          new AdapterSetting.SettingModel.CheckboxStyle[] {
              null,
              null,
              new AdapterSetting.SettingModel.CheckboxStyle().isCheckboxActive(session.getActivitiesReminder()),
              new AdapterSetting.SettingModel.CheckboxStyle().isCheckboxActive(session.getPrefPascode().toString().length() > 0),
              null,
              null,
              null
          };

      List<AdapterSetting.SettingModel> settingModels = new ArrayList<>();

      settingModels.add(generateModel(context.getString(R.string.general_setting_title),R.array.setting_child_item_general_labels, R.array.setting_child_item_general_icons, generalDescriptions, generalEnums, null));
      settingModels.add(generateModel(context.getString(R.string.other_setting_title),R.array.setting_child_item_other_labels, R.array.setting_child_item_other_icons, otherDescriptions, otherEnums, checkboxStyles));

      return Observable.just(settingModels);
    });
  }

  private AdapterSetting.SettingModel generateModel(String title, @ArrayRes int labelArrayResId,
      @ArrayRes int iconArrayResId, String[] notes, Setting[] types,
      @Nullable AdapterSetting.SettingModel.CheckboxStyle[] checkboxStyles) {
    AdapterSetting.SettingModel model = new AdapterSetting.SettingModel();

    model.title = title;
    model.items = new ArrayList<>();

    String[] labels = context.getResources().getStringArray(labelArrayResId);
    String[] icons = context.getResources().getStringArray(iconArrayResId);

    for (int i = 0; i < labels.length; i++) {
      AdapterSetting.SettingModel.Item item = new AdapterSetting.SettingModel.Item();
      item.type = types[i];
      item.label = labels[i];
      item.icon = icons[i];

      if (checkboxStyles != null) item.checkboxStyle = checkboxStyles[i];
      if (notes != null) item.note = notes[i];

      model.items.add(item);
    }

    return model;
  }

  public void clearPasscode() {
    session.clearPassCodePreff();
  }

  public void listenPasscode(SettingActivity activity) {
    compositeSubscription.add(MyCustomApplication.getRxBus()
        .toObserverable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .ofType(SettingCheckBoxEvent.class)
        .subscribe(event -> {
          switch (event.type) {
            case SmartReminder:
              session.setActivitiesReminder(event.checked);
              populateData();
              break;
            case PassCode:
              if (event.checked) {
                Intent i = new Intent(activity, PassCodeActivity.class);
                activity.startActivity(i);
              } else {
                Intent i = new Intent(activity, PassCodeActivity.class);
                i.putExtra(PassCodeActivity.KEY_TAG_LOCKMODE, PassCodeActivity.ENABLE_LOCK_MODE);
                i.putExtra(PassCodeActivity.KEY_TAG_CLEARMODE, PassCodeActivity.ENABLE_LOCK_MODE);
                activity.startActivityForResult(i, activity.REQ_PASS);
              }
              break;
            default:
              break;
          }
        }, throwable -> {
        }, () -> {
        }));
  }

  public void saveInitialDateofMonth(int number) {
    session.setInitialDayOfMonth(number);
    populateData();
  }

  public int getInitialDateofMonth() {
    return session.getInitialDayOfMonth();
  }

  public void setSpinnerAccount() {
    String[] accounts = new String[] { "Dompet", "Mandiri", "BCA" };
    compositeSubscription.add(Observable.just(db.getAllAccountByUser(session.getIdUser()))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(listAccounts -> {
          String[] labels = new String[listAccounts.size()];
          int i = 0;
          for (Account account : listAccounts) {
            labels[i] = mc.decrypt(account.getName());
            i++;
          }
          getMvpView().setSpinner(listAccounts, labels);
        }, throwable -> {
        }, () -> {
        }));
  }
}
