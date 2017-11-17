package javasign.com.dompetsehat.callback;

import rx.Observer;
import timber.log.Timber;

/**
 * Created by lafran on 12/16/16.
 */

public class SimpleObserver<T> implements Observer<T> {
  @Override public void onCompleted() {

  }

  @Override public void onError(Throwable e) {
    Timber.e("Error Simple Observer: " + e.getLocalizedMessage());
  }

  @Override public void onNext(T t) {

  }
}
