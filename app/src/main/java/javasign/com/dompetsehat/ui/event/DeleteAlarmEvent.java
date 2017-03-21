package javasign.com.dompetsehat.ui.event;

import android.content.Context;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by avesina on 12/21/16.
 */

public class DeleteAlarmEvent {
  @Inject SyncPresenter presenter;

  public DeleteAlarmEvent(Context context){
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    Helper.sync(context,presenter);
  }
}
