package javasign.com.dompetsehat.presenter.main;

import java.util.ArrayList;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Plan;

/**
 * Created by avesina on 9/14/16.
 */

public interface FinplanInterface extends MvpView {
  void setAdapterPlan(ArrayList<Plan> plans);
  void setCicularProgressBar(double total_budget,double total_budget_belum_tercapai);
}
