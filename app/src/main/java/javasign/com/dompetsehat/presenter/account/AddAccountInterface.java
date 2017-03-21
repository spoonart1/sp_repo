package javasign.com.dompetsehat.presenter.account;

import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Account;

/**
 * Created by avesina on 9/9/16.
 */

public interface AddAccountInterface extends MvpView {
  void setBalance(double balance);

  void startLoading();

  void stopLoading();

  void finishUpdateAccount(String msg);

  void finishCreatedAccount(Account account,double Saldo,String mesage);

  void setDataAccount(String username, double saldo);

  void showMessage(String message);

  void setCountTransaction(Integer[] products_id,int counts);
}
