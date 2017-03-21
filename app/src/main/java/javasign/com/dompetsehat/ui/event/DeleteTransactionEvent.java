package javasign.com.dompetsehat.ui.event;

import android.content.Context;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by lafran on 12/15/16.
 */

public class DeleteTransactionEvent {
  boolean isEmpty;
  @Inject SyncPresenter presenter;

  public DeleteTransactionEvent(Context context){
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    Helper.sync(context,presenter);
  }
  public DeleteTransactionEvent(Context context,boolean isEmpty){
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    this.isEmpty = isEmpty;
    Helper.sync(context,presenter);
  }
}
