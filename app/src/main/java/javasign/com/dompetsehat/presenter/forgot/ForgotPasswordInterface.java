package javasign.com.dompetsehat.presenter.forgot;

import javasign.com.dompetsehat.base.MvpView;

/**
 * Created by avesina on 11/21/16.
 */

public interface ForgotPasswordInterface extends MvpView {
  void successForgot(String message);
  void alertMessage(String message);
}
