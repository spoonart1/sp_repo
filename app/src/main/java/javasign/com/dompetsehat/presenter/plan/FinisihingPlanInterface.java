package javasign.com.dompetsehat.presenter.plan;

import java.util.List;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Invest;
import javasign.com.dompetsehat.models.ThirdParty;
import javasign.com.dompetsehat.ui.CommonInterface;

/**
 * Created by avesina on 9/9/16.
 */

public interface FinisihingPlanInterface extends CommonInterface {
  void setAccounts(List<ThirdParty> registerableIntitusi, List<Account> registeredAccount);
  void setInvests(List<Invest> invests);
}
