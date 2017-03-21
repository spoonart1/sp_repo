package javasign.com.dompetsehat.presenter.profile;

import android.content.Context;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.Badges;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.ui.event.AddAccountEvent;
import javasign.com.dompetsehat.ui.event.AddDompet;
import javasign.com.dompetsehat.ui.event.AddTransactionEvent;
import javasign.com.dompetsehat.ui.event.ChangeProfile;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javax.inject.Inject;

import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.ui.globaladapters.StatisticAdapter;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 8/24/16.
 */

public class ProfilePresenter extends BasePresenter<ProfileInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private final LoadAndSaveImage saveImage;
  MCryptNew mCryptNew = new MCryptNew();
  DbHelper db;
  LoadAndSaveImage img;
  @Inject RxBus rxBus = MyCustomApplication.getRxBus();

  @Inject public ProfilePresenter(DataManager dataManager, SessionManager session,
      LoadAndSaveImage saveImage, DbHelper dbHelper, @ActivityContext Context context) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.saveImage = saveImage;
    this.db = dbHelper;
    img = new LoadAndSaveImage(this.context);
  }

  @Override public void attachView(ProfileInterface mvpView) {
    super.attachView(mvpView);
    img = new LoadAndSaveImage(this.context);
    init();
  }

  public void saveAvatar(Uri resultUri) {
    compositeSubscription.add(ObservableSaveAvatar(resultUri).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(bitmap -> {
          if (bitmap != null) {
            rxBus.send(new ChangeProfile().setBitmap(bitmap));
            uploadAvatar(bitmap);
          }
        }));
  }

  public void uploadAvatar(Bitmap bitmap) {
    String img_name = mcrypt.decrypt(session.getUsername());
    compositeSubscription.add(dataManager.updateAvatar(img_name, bitmap)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(responseBody -> {
        }, throwable -> {
        }, () -> {

        }));
  }

  public Observable<Bitmap> ObservableSaveAvatar(Uri resultUri) {
    String path = "";
    if (resultUri != null) {
      if (resultUri.toString().contains("file://")) {
        path = resultUri.toString();
      } else {
        path = getRealPathFromUri(this.context, resultUri);
      }
      Bitmap bm = img.loadBitmap(path);
      String img_name = mcrypt.decrypt(session.getUsername());
      img.saveToInternalSorage(bm, img_name);
      boolean b = db.saveAvatar(session.getIdUser(), img_name);
      if (b) {
        return Observable.just(bm);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public void checkBadge() {
    compositeSubscription.add(hitDbCheckBadge(session.getIdUser()).observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .subscribe(label -> {
          if(!TextUtils.isEmpty(label)) {
            getMvpView().setLevel(label);
          }else{
            getMvpView().setLevel("Beginner");
            if(db.getBadgesCount() == 0){
              db.newBadges();
            }
          }
        }, throwable -> {
          Timber.e("ERROR checkBadge "+throwable);
        }, () -> {
        }));
  }

  private Observable<String> hitDbCheckBadge(String user_id) {
    return Observable.defer(() -> {
      db.badgesCheck(session.getIdUser());
      Integer id = db.getBadgesID(Integer.valueOf(user_id));
      Integer count = db.getBadgesCount();
      if (id < count) {
        id++;
      }
      String label = db.getBadges(Integer.valueOf(user_id));
      return Observable.just(label);
    });
  }

  public static String getRealPathFromUri(Context context, Uri contentUri) {
    Cursor cursor = null;
    try {
      String[] proj = { MediaStore.Images.Media.DATA };
      cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  public void init() {
    compositeSubscription.add(rxBus.toObserverable()
        .ofType(AddTransactionEvent.class)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(addTransaction -> {
          if (addTransaction.cash != null) {
            setAdapter(1);
          }
        }));
    compositeSubscription.add(rxBus.toObserverable()
        .ofType(AddDompet.class)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(addDompet -> {
          if (addDompet.getAccount() != null) {
            setAdapter(1);
          }
        }));
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void setProfile() {
    User user = db.getUser(session.getIdUser(), db.TAB_USER);
    Bitmap bitmap = null;
    if (user != null) {
      String path_avatar = user.getAvatar();
      if (path_avatar != null) {
        bitmap = img.loadImageFromStorage(img.dirApps, path_avatar);
      }
      boolean fb_connected = session.isConnectedFB();
      getMvpView().setProfile(mCryptNew.decrypt(user.getUsername()),
          mCryptNew.decrypt(user.getPhone()), mCryptNew.decrypt(user.getEmail()), "password",
          user.getBirthday(), user.getPenghasilan(), user.getAnak(), bitmap, fb_connected,
          session.getFacebookID());
    } else {
      getMvpView().setProfile("", "", "", "", "", null, null, bitmap, false, "");
    }
    getMvpView().showEmailVerificationButton(!session.isVerifiedEmail());
  }

  public void setAdapter(int reqid) {
    getMvpView().onLoad(reqid);
    compositeSubscription.add(getData().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data -> {
          getMvpView().putData(data);
          getMvpView().onNext(reqid);
        }, throwable -> {
          getMvpView().onError(reqid);
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().onComplete(reqid);
        }));
  }

  public Observable<HashMap<String, Object>> getData() {
    return Observable.defer(() -> {
      HashMap<String, Object> map = new HashMap();
      int size = 7;
      Integer[] counts = new Integer[size];
      Double[] values = new Double[size];
      // counts
      counts[StatisticAdapter.ACCOUNT_POS] = getCount(StatisticAdapter.ACCOUNT_POS);
      counts[StatisticAdapter.TRANS_POS] = getCount(StatisticAdapter.TRANS_POS);
      counts[StatisticAdapter.PORTO_POS] = getCount(StatisticAdapter.PORTO_POS);
      counts[StatisticAdapter.PLAN_POS] = getCount(StatisticAdapter.PLAN_POS);
      counts[StatisticAdapter.REMIND_POS] = getCount(StatisticAdapter.REMIND_POS);
      counts[StatisticAdapter.COMISSION_POS] = getCount(StatisticAdapter.COMISSION_POS);
      counts[StatisticAdapter.BUDGET_POS] = getCount(StatisticAdapter.BUDGET_POS);
      // values
      values[StatisticAdapter.ACCOUNT_POS] = getValues(StatisticAdapter.ACCOUNT_POS);
      values[StatisticAdapter.TRANS_POS] = getValues(StatisticAdapter.TRANS_POS);
      values[StatisticAdapter.PORTO_POS] = getValues(StatisticAdapter.PORTO_POS);
      values[StatisticAdapter.PLAN_POS] = getValues(StatisticAdapter.PLAN_POS);
      values[StatisticAdapter.REMIND_POS] = getValues(StatisticAdapter.REMIND_POS);
      values[StatisticAdapter.COMISSION_POS] = getValues(StatisticAdapter.COMISSION_POS);
      values[StatisticAdapter.BUDGET_POS] = getValues(StatisticAdapter.BUDGET_POS);

      map.put("counts", counts);
      map.put("values", values);
      Timber.e("transaksi "+counts[StatisticAdapter.TRANS_POS]);
      return Observable.just(map);
    });
  }

  public int getCount(int menu) {
    int count = 0;
    try {
      switch (menu) {
        case StatisticAdapter.ACCOUNT_POS:
          count = db.getAccountCount(Integer.valueOf(session.getIdUser()));
          break;
        case StatisticAdapter.TRANS_POS:
          count = db.getTransactionCount(Integer.valueOf(session.getIdUser()));
          break;
        case StatisticAdapter.PORTO_POS:
          Account account = db.getAccountById(10, 1, Integer.valueOf(session.getIdUser()));
          if (account != null) {
            count = db.getAllProductByAccount(account.getIdaccount()).size();
          }
          break;
        case StatisticAdapter.PLAN_POS:
          count = db.getAllPlan(session.getIdUser()).size();
          Timber.e("Plan " + count);
          break;
        case StatisticAdapter.COMISSION_POS:
          break;
        case StatisticAdapter.BUDGET_POS:
          count = db.getAllBudget(session.getIdUser()).size();
          Timber.e("budget " + count);
          break;
        case StatisticAdapter.REMIND_POS:
          count = db.getAllAlarm(Integer.valueOf(session.getIdUser())).size();
          break;
      }
    } catch (Exception e) {
      Timber.e("ERROR getValues " + e);
    }
    Timber.e("getCount " + count);
    return count;
  }

  public double getValues(int menu) {
    double values = 0.0;
    try {
      switch (menu) {
        case StatisticAdapter.ACCOUNT_POS:
          values = (double) db.getTotalSaldoAllAccount(Integer.valueOf(session.getIdUser()));
          break;
        case StatisticAdapter.TRANS_POS:
          values = (double) db.getTotalSaldoAllTransactions(Integer.valueOf(session.getIdUser()));
          break;
        case StatisticAdapter.PORTO_POS:
          Account account = db.getAccountById(10,1,Integer.valueOf(session.getIdUser()));
          if(account != null) {
            List<Product> products = db.getAllProductByAccount(account.getIdaccount());
            values = 0.0;
            for (Product p : products) {
              values += p.getBalance();
            }
          }
          break;
        case StatisticAdapter.PLAN_POS:
          List<Plan> planList = db.getAllPlan(session.getIdUser());
          values = 0.0;
          for (Plan plan : planList) {
            values += plan.getPlan_amount_cash();
          }
          break;
        case StatisticAdapter.COMISSION_POS:
          break;
        case StatisticAdapter.BUDGET_POS:
          List<Budget> budgets = db.getAllBudget(session.getIdUser());
          values = 0.0;
          for (Budget budget : budgets) {
            values += budget.getAmount_budget();
          }
          break;
        case StatisticAdapter.REMIND_POS:
          List<Alarm> alarms = db.getAllAlarm(Integer.valueOf(session.getIdUser()));
          values = 0.0;
          for (Alarm alarm : alarms) {
            values += alarm.getJumlah_alarm();
          }
          break;
      }
    } catch (Exception e) {
      Timber.e("ERROR getValues " + e);
    }
    Timber.e("getValues " + values);
    return values;
  }

  public void catchRefresh() {
    compositeSubscription.add(rxBus.toObserverable()
        .ofType(AddAccountEvent.class)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(addAccountEvent -> {
          setProfile();
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public void changePassword(String new_password, String old_password) {
    compositeSubscription.add(dataManager.changePassword(new_password, old_password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {
          if (response.equals("success")) {
            session.setToken(response.response);
            getMvpView().finishValidate();
          } else {
            Toast.makeText(context, Helper.combinePlural(response.message), Toast.LENGTH_LONG)
                .show();
          }
        }, throwable -> {
          getMvpView().finishValidate();
          Timber.e("ERROR " + throwable);
        }, () -> {
          getMvpView().finishValidate();
        }));
  }

  public void updateProfile(String phone, String email, Date date, Double income, Integer kids) {
    String[] tanggal = { "" };
    if (date != null) {
      tanggal[0] = Helper.setSimpleDateFormat(date, "yyyy-MM-dd");
    }
    compositeSubscription.add(dataManager.updateProfile(phone, email, tanggal[0], income, kids)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(signInResponse -> {
          Timber.e("RESPONSE " + signInResponse.message);
          User user = db.getUser(session.getIdUser(), DbHelper.TAB_USER);
          if (date != null) {
            user.setBirthday(tanggal[0]);
          }
          if (phone != null) {
            user.setPhone(phone);
          }
          if (email != null) {
            user.setEmail(mCryptNew.encrypt(email));
          }
          if (income != null) {
            user.setPenghasilan(income);
          }
          if (kids != null) {
            user.setAnak(kids);
          }
          db.updateUser(user, session.getIdUser());
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          getMvpView().onError(123);
        }, () -> {
          getMvpView().onComplete(123);
        }));
  }

  public void setRegister() {
    String email = session.getEmailMami();
    String phone = session.getPhoneMami();
    getMvpView().setPhoneEmail(phone, email);
  }
}
