package javasign.com.dompetsehat.presenter.sync;

import javasign.com.dompetsehat.base.MvpView;

/**
 * Created by avesina on 11/9/16.
 */

public interface SyncInterface extends MvpView {
  void onComplete(int code);
  void onError(int code);
  void onNext(int code,String message);
}
