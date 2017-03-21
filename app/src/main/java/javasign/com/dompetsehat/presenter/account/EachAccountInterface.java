package javasign.com.dompetsehat.presenter.account;

import java.util.ArrayList;
import java.util.Date;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;

/**
 * Created by avesina on 9/29/16.
 */

public interface EachAccountInterface extends MvpView {
  void setAccount(Account account,double saldo);
  void setListProduct(ArrayList<Product> products);
  void successGetData();
  void errorMessage(String message);
  void setLastSync(Date date);
}
