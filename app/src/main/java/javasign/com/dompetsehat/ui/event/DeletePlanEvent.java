package javasign.com.dompetsehat.ui.event;

import android.content.Context;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by lafran on 12/19/16.
 */

public class DeletePlanEvent {
  @Inject SyncPresenter presenter;

  public DeletePlanEvent(Context context) {
    ((MyCustomApplication) context).getApplicationComponent().inject(this);
    Helper.sync(context, presenter);
  }
}
