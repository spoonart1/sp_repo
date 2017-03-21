package javasign.com.dompetsehat.ui.activities.institusi.base;

import android.graphics.Bitmap;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.account.AddAccountInterface;
import javasign.com.dompetsehat.presenter.mami.MamiInterface;
import javasign.com.dompetsehat.presenter.profile.ProfileInterface;

/**
 * Created by avesina on 10/24/16.
 */

public class BaseInstitusi extends BaseActivity implements MamiInterface,ProfileInterface,AddAccountInterface {
  @Override public void finishValidate() {

  }

  @Override public void setLevel(String level) {

  }

  @Override public void showEmailVerificationButton(boolean set) {

  }

  @Override public void errorValidate() {

  }

  @Override public void successRegister(String RequestId) {

  }

  @Override public void errorRegister(String[] messages) {

  }

  @Override public void successLogin(String reffCode,int account_id) {

  }

  @Override public void showMessage(int ClientStat, String message) {

  }

  @Override public void setBalance(double balance) {

  }

  @Override public void startLoading() {

  }

  @Override public void stopLoading() {

  }

  @Override public void finishUpdateAccount(String msg) {

  }

  @Override public void finishCreatedAccount(Account account, double Saldo, String mesage) {

  }

  @Override public void setDataAccount(String username, double saldo) {

  }

  @Override public void showMessage(String message) {

  }

  @Override public void setCountTransaction(Integer[] products_id, int counts) {

  }

  @Override public void errorTextEdit(String message) {

  }

  @Override public void showDialog(boolean validOpenAccount) {

  }

  @Override public void putData(Object data) {

  }

  @Override public void setProfile(String username, String phone, String email, String password,
      String tanggal_birthday, Double pendapatan, Integer anak, Bitmap bitmap,
      boolean fb_connected,String fb_id) {
    
  }

  @Override public void setPhoneEmail(String phone, String email) {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }
}
