package javasign.com.dompetsehat.ui.activities.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import butterknife.Bind;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.presenter.main.NewMainViewInterface;
import javasign.com.dompetsehat.presenter.main.NewMainViewPresenter;
import javasign.com.dompetsehat.services.InsertDataService;
import javasign.com.dompetsehat.ui.CommonInterface;
import javasign.com.dompetsehat.ui.event.ChangeProfile;
import javasign.com.dompetsehat.utils.CircleImageView;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import rx.Observer;

/**
 * Created by aves on 8/25/16.
 */

public class ParentMain extends BaseActivity implements NewMainViewInterface {

  // avesina
  @Inject DbHelper db;
  @Inject NewMainViewPresenter presenter;
  @Inject SessionManager sessionManager;
  @Inject LoadAndSaveImage loadAndSaveImage;

  @Bind(R.id.iv_user_pict) RoundedImageView iv_user_pict;
  private RxBus rxBus;

  final int REQUEST_CASHFLOW = 1;
  ProgressDialog pDialog;
  NotificationManager mNotificationManager;
  public int InsertDataService = 123;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getActivityComponent().inject(this);
    presenter.attachView(this);
    rxBus = MyCustomApplication.getRxBus();
    sessionManager = new SessionManager(this);
    db = DbHelper.getInstance(this);
    loadAndSaveImage = new LoadAndSaveImage(this);
    presenter.catchProfile(new Observer<ChangeProfile>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {

      }

      @Override public void onNext(ChangeProfile changeProfile) {
        iv_user_pict.setImageBitmap(getAvatar());
      }
    });
    pDialog = new ProgressDialog(this);
    pDialog.setButton(DialogInterface.BUTTON_POSITIVE, "minimize", (dialog, which) -> {
      dialog.cancel();
    });
    mNotificationManager =
        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    pDialog.setOnCancelListener(dialogInterface -> {
      Notification notification = buildNotif("DompetSehat", getString(R.string.dialog_loading));
      mNotificationManager.notify(InsertDataService, notification);
    });
  }

  private Notification buildNotif(String title, String message) {
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
    mBuilder.setContentTitle(title);
    mBuilder.setContentText(message);
    mBuilder.setSmallIcon(R.drawable.icon_dompet);
    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
    bigTextStyle.setBigContentTitle("Notification");
    bigTextStyle.bigText(message);
    mBuilder.setStyle(bigTextStyle);
    mBuilder.setVibrate(new long[] { -1 });
    mBuilder.setProgress(0, 0, true);
    //mBuilder.setContentIntent(intent);
    mBuilder.setAutoCancel(true);
    return mBuilder.build();
  }

  public Bitmap getAvatar() {
    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.user_empty);
    try {
      User user = db.getUser(sessionManager.getIdUser(), db.TAB_USER);
      if (user != null && user.getAvatar() != null && !user.getAvatar().equalsIgnoreCase("null")) {
        bm = loadAndSaveImage.loadImageFromStorage(loadAndSaveImage.dirApps, user.getAvatar());
        //iv_user_pict.setPadding(8,8,8,8);
        int paddingPixel = 5;
        float density = getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        iv_user_pict.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        //bm = Bitmap.createScaledBitmap(bm, bm.getHeight()-40, bm.getWidth()-40, false);
      }

      int size = getResources().getDimensionPixelSize(R.dimen.ds_action_bar_size);
      Bitmap ovalBm = CircleImageView.getRoundedCroppedBitmap(bm, size, size);

      return user != null ? ovalBm : bm;
    } catch (Exception e) {
      return bm;
    }
  }

  public void getCashFlow() {
    pDialog.setMessage(getString(R.string.dialog_ambil_data));
    pDialog.show();
    presenter.getCashflow(REQUEST_CASHFLOW);
  }

  public void LastConnectGetCashFlow() {
    pDialog.setMessage(getString(R.string.dialog_ambil_data));
    pDialog.show();
    presenter.getCashflow(REQUEST_CASHFLOW, sessionManager.getLastSync());
  }

  public void dismisDialog() {
    pDialog.dismiss();
    mNotificationManager.cancel(InsertDataService);
  }

  @Override public void changeNotif(String message, int count) {
    runOnUiThread(() -> {
      pDialog.setMessage(message);
    });
  }

  @Override public void populateSpinner(ArrayList<?> datas, String[] labels) {

  }

  @Override public void showNotif(int count, int pos) {

  }

  @Override public void onLoad(int requestid) {

  }

  @Override public void onComplete(int requestid) {

  }

  @Override public void onError(int requestid) {
    runOnUiThread(() -> pDialog.setOnDismissListener(dialog -> {
      if (requestid == State.ERROR_SERVER) {
        new AlertDialog.Builder(ParentMain.this).setTitle("Kesalahan")
            .setMessage(
                "Terjadi kesalahan ketika memuat data, swipe ke bawah untuk memuat ulang atau login ulang.")
            .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss())
            .create()
            .show();
      }
    }));
    pDialog.dismiss();
  }

  @Override public void onNext(int requestid) {

  }

  @Override protected void onDestroy() {
    dismisDialog();
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
