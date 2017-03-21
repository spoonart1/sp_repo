package javasign.com.dompetsehat.ui.event;

import android.content.Context;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by avesina on 9/20/16.
 */

public class AddPlanEvent {
  public final static int ADD_PLANT_EVENT_SUCCESS = 10;
  public int eventCode = -1;
  @Inject SyncPresenter presenter;
  public AddPlanEvent(Context context,int eventCode){
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    this.eventCode = eventCode;
    Helper.sync(context,presenter);
  }

}
