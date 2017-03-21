package javasign.com.dompetsehat.presenter.notification;

import java.util.ArrayList;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.ui.activities.notification.pojo.GlobalNotifModel;

/**
 * Created by avesina on 3/13/17.
 */

public interface NotificationInterface extends MvpView {
  void setAdapter(ArrayList<GlobalNotifModel> models);
}
