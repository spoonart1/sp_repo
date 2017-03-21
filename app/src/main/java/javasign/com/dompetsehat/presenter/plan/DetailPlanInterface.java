package javasign.com.dompetsehat.presenter.plan;

import java.util.ArrayList;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Plan;

/**
 * Created by avesina on 10/6/16.
 */

public interface DetailPlanInterface extends MvpView {
  void init();
  void setPlan(Plan plan,Object dana);
  void setTransactions(ArrayList<Integer> ids);
}
