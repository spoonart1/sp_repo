package javasign.com.dompetsehat.presenter.verification;

import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.User;

/**
 * Created by avesina on 1/13/17.
 */

public interface VerificationInterface extends MvpView {
  void setUser(String phone,String email);
  void onError();
  void onNext();
}
