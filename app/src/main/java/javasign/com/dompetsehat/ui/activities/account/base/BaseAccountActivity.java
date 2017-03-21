package javasign.com.dompetsehat.ui.activities.account.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import java.util.ArrayList;
import java.util.Date;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.presenter.account.AccountInterface;
import javasign.com.dompetsehat.presenter.account.AddAccountInterface;
import javasign.com.dompetsehat.presenter.account.EachAccountInterface;
import javasign.com.dompetsehat.ui.CommonInterface;
import javasign.com.dompetsehat.ui.activities.account.pojo.CellAccount;
import javasign.com.dompetsehat.ui.fragments.account.adapter.NewManageAccountAdapter;

/**
 * Created by lafran on 10/20/16.
 */

public class BaseAccountActivity extends BaseActivity implements AccountInterface, SwipeRefreshLayout.OnRefreshListener,
    CommonInterface, EachAccountInterface, AddAccountInterface {
  @Override public void onRefresh() {

  }

  @Override public void setTotalBalance(double totalBalance) {

  }

  @Override public void setAdapter(ArrayList<NewManageAccountAdapter.VendorParent> arrayList) {

  }

  @Override public void setSimpleAdapter(ArrayList<CellAccount> products) {

  }

  @Override public void setTotalAccount(int totalAccount) {

  }

  @Override public void deleteSuccess() {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }

  @Override public void setAccount(Account account, double saldo) {

  }

  @Override public void setListProduct(ArrayList<Product> products) {

  }

  @Override public void successGetData() {

  }

  @Override public void errorMessage(String message) {

  }

  @Override public void setLastSync(Date date) {

  }

  @Override public void startLoading() {

  }

  @Override public void stopLoading() {

  }

  @Override public void finishUpdateAccount(String msg) {

  }

  @Override public void finishCreatedAccount(Account account,double Saldo,String message) {

  }

  @Override public void setDataAccount(String username, double saldo) {

  }

  @Override public void showMessage(String message) {

  }

  @Override public void setCountTransaction(Integer[] products_id, int counts) {

  }

  @Override public void setBalance(double balance) {

  }
}
