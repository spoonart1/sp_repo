package javasign.com.dompetsehat.presenter.debts;

import java.util.ArrayList;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Debt;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.json.debt;

/**
 * Created by avesina on 2/17/17.
 */

public interface DebtsInterface extends MvpView {

  void setAdapter(ArrayList<Debt> debts);
  void setTotal(double total);
  void finish();
  void snackBar(String message);
  void onRefresh();
  void setSpinnerProduct(ArrayList<Product> products,String[] labels);
  void setSpinnerCashflow(ArrayList<Cash> cashes,String[] labels);
}
