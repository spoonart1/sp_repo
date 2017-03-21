package javasign.com.dompetsehat.presenter.mami;

import javasign.com.dompetsehat.base.MvpView;

/**
 * Created by avesina on 10/24/16.
 */

public interface MamiInterface extends MvpView {
  void finishValidate();
  void errorValidate();
  void successRegister(String RequestId);
  void errorRegister(String[] messages);
  void successLogin(String reffCode,int account_id);
  void showMessage(int ClientStat,String message);
  void errorTextEdit(String message);
  void showDialog(boolean validOpenAccount);
}
