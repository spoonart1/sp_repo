package javasign.com.dompetsehat.presenter.reminder;

import android.content.Context;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.ui.event.AddReminderEvent;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javax.inject.Inject;

import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.FinanceLib;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;
import javasign.com.dompetsehat.utils.RxBus;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 9/5/16.
 */

public class ReminderPresenter extends BasePresenter<ReminderInterface> {
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private GeneralHelper helper;
  private final Context context;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private DbHelper db;
  boolean FLAG_SETCATEGORY = true;
  SessionManager session;
  private String KEY_BALANCE = "balance";
  private String KEY_BILL = "bill";
  private String KEY_COUNT = "count";

  @Inject
  public ReminderPresenter(@ApplicationContext Context context, DbHelper db, SessionManager session,
      GeneralHelper helper) {
    this.context = context;
    this.db = db;
    this.session = session;
    this.helper = helper;
    this.init();
  }

  @Override public void attachView(ReminderInterface mvpView) {
    super.attachView(mvpView);
    Timber.e("  PMT " + FinanceLib.pmt(0.09, 40, -1, 0, true));
  }

  private void init() {
    rxBus.toObserverable()
        .ofType(AddReminderEvent.class)
        .subscribe(new Observer<AddReminderEvent>() {
          @Override public void onCompleted() {

          }

          @Override public void onError(Throwable e) {

          }

          @Override public void onNext(AddReminderEvent addEventReminder) {
            loadData();
          }
        });
  }

  public void saveReminder(Alarm alarm) {
    SimpleDateFormat df = new SimpleDateFormat(helper.FORMAT_LENGKAP);
    Calendar calendar = Calendar.getInstance();
    alarm.setIs_active("0");
    alarm.setToggle("1");
    alarm.setId_user(Integer.valueOf(session.getIdUser()));
    alarm.setDeleted_at("null");
    alarm.setId(-1);
    alarm.setCreated_at(df.format(calendar.getTime()));
    alarm.setUpdated_at(df.format(calendar.getTime()));
    db.newAlarm(alarm);

    rxBus.send(new AddReminderEvent(context));
  }

  public void updateReminder(Alarm alarm) {
    SimpleDateFormat df = new SimpleDateFormat(helper.FORMAT_LENGKAP);
    Calendar calendar = Calendar.getInstance();
    alarm.setUpdated_at(df.format(calendar.getTime()));
    db.updateAlarm(alarm);
    rxBus.send(new AddReminderEvent(context));
  }

  public void loadData() {
    System.out.println("ReminderPresenter.loadData");
    compositeSubscription.add(getHeader().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(stringDoubleHashMap -> {
          System.out.println(
              "ReminderPresenter.loadData"
                  + " | keycount: "
                  + stringDoubleHashMap.get(KEY_COUNT)
                  .intValue()
                  + " | keyBill: "
                  + stringDoubleHashMap.get(KEY_BILL)
                  + " | keyBalance: "
                  + stringDoubleHashMap.get(KEY_BALANCE));

          getMvpView().setHeaderData(stringDoubleHashMap.get(KEY_COUNT).intValue(),
              stringDoubleHashMap.get(KEY_BILL), stringDoubleHashMap.get(KEY_BALANCE));
        }, throwable -> {
          System.out.println(
              "ReminderPresenter.loadData error: " + throwable.getLocalizedMessage());
          Timber.e("Error setHeader %s", throwable.getMessage());
        }, () -> {
        }));

    compositeSubscription.add(getReminder().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(reminderModels -> {
          getMvpView().setAdapter(reminderModels);
        }, throwable -> {
          Timber.e("Error setAdapter %s", throwable.getMessage());
        }, () -> {
        }));
  }

  public boolean setActiveReminder(ReminderModel model) {
    Timber.e("Heloo" + model.identifier);
    if (model.identifier != null) {
      Alarm alarm = db.getAlarmByID(model.identifier);
      if (alarm != null) {
        if (model.isActive) {
          alarm.setToggle("0");
        } else {
          alarm.setToggle("1");
        }
        db.updateAlarmByID(alarm);
        //Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        return true;
      }
    }
    return false;
  }

  public Observable<HashMap<String, Double>> getHeader() {
    HashMap<String, Double> map = new HashMap<>();
    Double bill = db.getTotalAmountAlarm(session.getIdUser());
    Double balance = db.getTotalSaldo(session.getIdUser(), 0);
    Double count = Double.valueOf(db.getAllAlarm(Integer.valueOf(session.getIdUser())).size());
    map.put(KEY_BILL, bill);
    map.put(KEY_BALANCE, balance);
    map.put(KEY_COUNT, count);
    return rx.Observable.just(map);
  }

  public Observable<ArrayList<ReminderModel>> getReminder() {
    ArrayList<ReminderModel> models = new ArrayList<>();
    ArrayList<Alarm> alarms = db.getAllAlarm(Integer.valueOf(session.getIdUser()));
    for (Alarm alarm : alarms) {
      Category category = db.getCategoryByID(alarm.getId_category());
      boolean isActive = alarm.getToggle().equalsIgnoreCase("1");
      ReminderModel model =
          new ReminderModel(category.getName(), alarm.getDate_alarm(), alarm.getJumlah_alarm(),
              Color.parseColor(category.getColor()));
      model.withIcon(category.getIcon());
      model.setIsActive(isActive);
      model.setIdentifier(alarm.getId());
      model.setAlarm(alarm);
      Timber.e("Alarm id " + alarm.getId());
      models.add(model);
    }
    return Observable.just(models);
  }

  public void deleteReminder(Integer identifier, DeleteUtils.OnDeleteListener listener) {
    if (identifier != null) {
      compositeSubscription.add(Observable.just(db.softDeleteAlarmByID(identifier))
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(isDeleted -> {
            System.out.println("ReminderPresenter.deleteReminder isDelete" + isDeleted);
            if (listener != null) {
              listener.onDoneRemoving();
            }
          }, throwable -> {
            Timber.e("Error delete reminder", throwable.getMessage());
          }, () -> {

          }));
    }
  }

  public void setAlarm(int anInt) {
    compositeSubscription.add(Observable.just(db.getAlarmByID(anInt))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(data -> {
          Category category = db.getCategoryByID(data.getId_category());
          UserCategory userCategory = null;
          if (data.getUser_id_category() > 0) {
            userCategory = db.getUserCategoryByID(data.getUser_id_category());
            userCategory.setParentCategory(category);
          }
          getMvpView().setAlarm(data, category, userCategory);
        }, throwable -> {
        }, () -> {
        }));
  }
}
