package javasign.com.dompetsehat.presenter.auth;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import br.com.goncalves.pugnotification.notification.PugNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.kelvinapps.rxfirebase.RxFirebaseAuth;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.models.json.facebook;
import javasign.com.dompetsehat.models.response.SignInResponse;
import javasign.com.dompetsehat.services.InsertDataService;
import javasign.com.dompetsehat.services.InsertLoginDataService;
import javasign.com.dompetsehat.ui.activities.login.SignInActivity;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by aves on 8/22/16.
 */

public class AuthPresenter extends BasePresenter<AuthInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private final LoadAndSaveImage saveImage;
  private DbHelper db;
  FirebaseAuth auth;

  @Inject public AuthPresenter(DataManager dataManager, LoadAndSaveImage saveImage, DbHelper db,
      @ActivityContext Context context, FirebaseAuth auth) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.saveImage = saveImage;
    this.db = db;
    this.auth = auth;
  }

  @Override public void attachView(AuthInterface mvpView) {
    super.attachView(mvpView);
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public interface onClickDialog {
    void onClick(int button);
  }

  public void loginViaApp(int requestid, String user, String password) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(dataManager.Login(user, password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(signInResponse -> {
          if (signInResponse != null) {
            if (signInResponse.status.toString().equalsIgnoreCase("success")) {
              getMvpView().showDialogConfirm(true, button -> {
                if (button == 1) {
                  saveData(signInResponse);
                }
              });
              getMvpView().onNext(requestid);
            } else {
              if (signInResponse.message.toLowerCase().contains("username or email")) {
                getMvpView().onError(11);
              } else if (signInResponse.message.toLowerCase().contains("password")) {
                getMvpView().showDialogConfirm(false, button -> {
                  if (button == 1) {
                    //saveData(signInResponse);
                  }
                });
                getMvpView().onError(12);
              } else {
                Toast.makeText(context, signInResponse.message, Toast.LENGTH_LONG).show();
                getMvpView().onError(12);
              }
            }
          }
        }, throwable -> {
          getMvpView().onError(requestid);
          checkError(throwable);
          Timber.e("ERROR Logging in: %d with error message %s ", requestid, throwable);
          Toast.makeText(context, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }, () -> {
          getMvpView().onComplete(requestid);
        }));
  }

  public void loginViaFacebook(int requestid, String access_token, facebook fb) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(dataManager.loginFacebook(fb.email, access_token)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(signInResponse -> {
          if (signInResponse.status.equalsIgnoreCase("success")) {
            saveData(signInResponse);
            getMvpView().onNext(requestid);
          } else {
            Timber.d("Error registrasi" + signInResponse.status);
            if (signInResponse.message.equalsIgnoreCase("Please provide data for new user.")) {
              getMvpView().gotoNextActivity("facebook", access_token, fb);
            }
          }
        }, throwable -> {
          Timber.e("ERORR" + throwable);
          checkError(throwable);
          getMvpView().onError(requestid);
          Toast.makeText(context, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }, () -> {
          getMvpView().onComplete(requestid);
        }));
  }

  private void checkError(Throwable throwable) {
    if (throwable instanceof HttpException) {
      if (((HttpException) throwable).code() == 500) {
        showNotif();
      }
    } else if (throwable instanceof SocketTimeoutException) {
      showNotif();
    }
  }

  private void showNotif() {
    PugNotification.with(context)
        .load()
        .identifier(123)
        .title("DompetSehat")
        .message("Dompetsehat saat ini sedang melakukan maintenance")
        .bigTextStyle(
            "[INFO] ntuk meningkatkan pelayanan bagi para pengguna, Aplikasi Dompetsehat saat ini sedang melakukan maintenance. Kami akan infokan jika sudah bisa di akses kembali")
        .smallIcon(R.drawable.ic_launcher)
        .largeIcon(R.drawable.ic_launcher)
        .flags(Notification.DEFAULT_ALL)
        .simple()
        .build();
  }

  public void loginViaAccountKit(int requestid, String access_token, String phone, String email) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(dataManager.loginAccountKit(access_token, phone, email)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(signInResponse -> {
          if (signInResponse.status.equalsIgnoreCase("success")) {
            saveData(signInResponse);
            getMvpView().onNext(requestid);
          } else {
            if (signInResponse.message.equalsIgnoreCase("Please provide data for new user.")) {
              HashMap<String, String> map = new HashMap<>();
              if (email != null) map.put("email", email.toString());
              if (phone != null) map.put("phone", phone.toString());
              getMvpView().gotoNextActivity("account_kit", access_token, map);
            }
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          checkError(throwable);
          getMvpView().onError(requestid);
          Toast.makeText(context, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }, () -> {
          getMvpView().onComplete(requestid);
        }));
  }

  public void registerViaApp(int requestid, String email, String username, String password,
      String birthday, String gender) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(dataManager.registerApp(email, username, password, birthday, gender)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(signInResponse -> {
          if (signInResponse.status.equalsIgnoreCase("success")) {
            saveData(signInResponse);
            getMvpView().onNext(requestid);
            //Smooch.track("Login/Signup");
          } else {
            Toast.makeText(this.context, signInResponse.message, Toast.LENGTH_LONG).show();
            getMvpView().onError(requestid);
          }
        }, throwable -> {
          Timber.e("ERROR " + throwable);
          getMvpView().onError(requestid);
          Toast.makeText(context, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }, () -> {
          getMvpView().onComplete(requestid);
          getMvpView().gotoNextActivity(null, null, null);
        }));
  }

  public void registerViaFacebook(int requestid, String access_token, String email, String username,
      String password, String birthday, String gender) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        dataManager.registerFacebook(email, access_token, birthday, gender, username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(signInResponse -> {
              if (signInResponse.status.equalsIgnoreCase("success")) {
                saveData(signInResponse);
                getMvpView().onNext(requestid);
              } else {
                Toast.makeText(this.context, signInResponse.message, Toast.LENGTH_LONG).show();
                getMvpView().onError(requestid);
              }
            }, throwable -> {
              Timber.e("ERROR " + throwable);
              getMvpView().onError(requestid);
              Toast.makeText(context, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }, () -> {
              getMvpView().onComplete(requestid);
              getMvpView().gotoNextActivity(null, null, null);
            }));
  }

  public void registerViaAccountKit(int requestid, String access_token, String phone, String email,
      String username, String password, String birthday, String gender) {
    getMvpView().onLoad(requestid);
    compositeSubscription.add(
        dataManager.registerAccountKit(access_token, email, phone, username, password, birthday,
            gender)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(signInResponse -> {
              if (signInResponse.status.equalsIgnoreCase("success")) {
                saveData(signInResponse);
                getMvpView().onNext(requestid);
              } else {

                if (signInResponse.message.contains("username") && signInResponse.message.contains(
                    "email")) {
                  getMvpView().onError(12);
                  return;
                } else if (!signInResponse.message.contains("username")
                    && signInResponse.message.contains("email")) {
                  getMvpView().onError(11);
                  return;
                } else if (signInResponse.message.contains("username")
                    && !signInResponse.message.contains("email")) {
                  getMvpView().onError(10);
                  return;
                }

                Toast.makeText(context, signInResponse.message, Toast.LENGTH_LONG).show();
              }
            }, throwable -> {
              Timber.e("Error " + throwable.getLocalizedMessage());
              getMvpView().onError(requestid);
              Toast.makeText(context, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }, () -> {
              getMvpView().onComplete(requestid);
              getMvpView().gotoNextActivity(null, null, null);
            }));
  }

  public void saveData(SignInResponse signInResponse, int requestid) {
    compositeSubscription.add(saveDataOb(signInResponse).observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(aBoolean -> {
          if (aBoolean) {

          }
        }, throwable -> {
          Timber.e("ERROR saveData " + throwable);
        }, () -> {
          getMvpView().onComplete(requestid);
        }));
  }

  public void saveData(SignInResponse signInResponse) {
    if (db.getBadgesCount() == 0) {
      db.newBadges();
    }
    boolean isV = false;
    if(signInResponse.response.user.verified_email == 1){
      isV = true;
    }
    dataManager.createSessionUser(signInResponse.response.access_token, String.valueOf(signInResponse.response.user.id), signInResponse.response.user.username, signInResponse.response.user.facebook_id,isV);
    Helper.signInFirebase(session, auth,mcrypt.decrypt(signInResponse.response.user.email));
    dataManager.saveUser(signInResponse.response.access_token, signInResponse.response.user);
    dataManager.saveAccount(signInResponse.response.user.id, signInResponse.response.accounts);
    dataManager.saveCategory(signInResponse.response.categories);
    dataManager.saveBudget(signInResponse.response.budgets);
    dataManager.saveAlarm(signInResponse.response.alarms);
    dataManager.savePlan(signInResponse.response.plans);
    dataManager.saveVendor(signInResponse.response.vendors);
    dataManager.saveUserCategory(signInResponse.response.user_categories);

    try {
      Thread thread = new Thread(() -> {
        try {
          //Your code goes here
          if (signInResponse.response.user.avatar != null) {
            if (!signInResponse.response.user.avatar.equalsIgnoreCase("null")) {
              //saveImage.saveToInternalSorage(
              //    new GeneralHelper().decodeBase64(signInResponse.response.user.avatar_base64),
              //    signInResponse.response.user.avatar);
              Timber.e("avatar " + signInResponse.response.user.avatar);
              Bitmap bitmap = Helper.getBitmapFromURL(signInResponse.response.user.avatar);
              String name = mcrypt.decrypt(signInResponse.response.user.username)
                  + "_"
                  + Calendar.getInstance().getTime().getTime();
              if (bitmap != null) {
                saveImage.saveToInternalSorage(bitmap, name);
                db.saveAvatar(session.getIdUser(), name);
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      thread.start();
    } catch (NullPointerException e) {
      Timber.e(" ERROR NullPointerException " + e);
    } catch (Exception e) {
      Timber.e(" ERROR  Exception " + e);
    }
  }

  public Observable<Boolean> saveDataOb(SignInResponse signInResponse) {
    return Observable.defer(() -> {
      saveData(signInResponse);
      return Observable.just(true);
    });
  }
}
