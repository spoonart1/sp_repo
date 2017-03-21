package javasign.com.dompetsehat.ui.event;

import android.content.Context;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by lafran on 10/14/16.
 */

public class AddTransactionEvent {
  public Cash cash;
  public boolean justAddNew = false;
  public boolean is_refresh = true;
  public int identifier;
  @Inject SyncPresenter presenter;

  public AddTransactionEvent(Context context,Cash cash, boolean justAddNew){
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    this.cash = cash;
    this.justAddNew = justAddNew;
    Helper.sync(context,presenter);
  }

  public AddTransactionEvent(Context context,Cash cash, boolean is_refresh, boolean is_sync){
    ((MyCustomApplication)context).getApplicationComponent().inject(this);
    this.cash = cash;
    this.is_refresh = is_refresh;
    if(is_sync) {
      Helper.sync(context, presenter);
    }
  }

  public AddTransactionEvent setIdentifier(int identifier){
    this.identifier = identifier;
    return this;
  }
}
