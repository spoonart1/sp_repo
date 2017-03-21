package javasign.com.dompetsehat.ui.event;

import javasign.com.dompetsehat.models.Account;

/**
 * Created by avesina on 10/14/16.
 */

public class AddDompet {
  Account account;

  public void setAccount(Account account) {
    this.account = account;
  }

  public Account getAccount() {
    return account;
  }
}
