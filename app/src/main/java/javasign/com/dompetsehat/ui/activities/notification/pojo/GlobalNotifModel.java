package javasign.com.dompetsehat.ui.activities.notification.pojo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.firebase.database.DataSnapshot;
import java.util.HashMap;
import javasign.com.dompetsehat.models.BaseModel;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.portofolio.PortofolioActivity;
import javasign.com.dompetsehat.ui.activities.referral.ReferralLoaderActivity;
import javasign.com.dompetsehat.ui.activities.setting.sharing.permission.PermissionSettingActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.utils.State;
import timber.log.Timber;

/**
 * Created by lafran on 3/9/17.
 */

public class GlobalNotifModel extends BaseModel {
  public String key;
  public String title;
  public String message;
  public String date;
  public String status;
  public String code;
  public TypeNotif typeNotif;
  public boolean visibility;
  public Long created_at;
  public HashMap<String, Object> properties;

  public void starActivity(Activity activity, SyncPresenter presenter) {
    switch (code) {
      case TypeNotif.N001:
        activity.startActivity(new Intent(activity, PermissionSettingActivity.class));
        break;
      case TypeNotif.N002:
        Timber.e("starActivity avesina " + this.properties + " " + this.properties.get("account"));
        if (this.properties.containsKey("account") && presenter != null) {
          try {
            Integer i = Integer.parseInt((String) this.properties.get("account"));
            activity.startActivity(new Intent(activity, TransactionsActivity.class).putExtra(TransactionsActivity.FROM,State.FROM_NOTIFICATION).putExtra(State.STATUS,"read").putExtra(State.ID_ACCOUNT,i));
            Timber.e("starActivity avesina " + i);
            if (i > 0) {
              presenter.getCashflowByAccount(i);
            }
          } catch (Exception e) {

          }
        }
        break;
      case TypeNotif.N003:
        activity.startActivity(new Intent(activity, PortofolioActivity.class));
        break;
      case TypeNotif.N004:
        activity.startActivity(new Intent(activity, ReferralLoaderActivity.class));
        break;
      default:
        activity.finish();
    }
  }

  public void starActivity(Activity activity) {
    starActivity(activity, null);
  }
}
