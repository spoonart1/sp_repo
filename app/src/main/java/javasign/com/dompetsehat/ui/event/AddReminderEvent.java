package javasign.com.dompetsehat.ui.event;

import android.content.Context;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by lafran on 10/28/16.
 */

public class AddReminderEvent {
  @Inject SyncPresenter presenter;

  public AddReminderEvent(Context context){
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    Helper.sync(context,presenter);
  }
}
