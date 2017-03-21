package javasign.com.dompetsehat.ui;

import javasign.com.dompetsehat.base.MvpView;

/**
 * Created by aves on 8/8/16.
 */

public interface CommonInterface extends MvpView {

    void onLoad(int RequestID);

    void onComplete(int RequestID);

    void onError(int RequestID);

    void onNext(int RequestID);
}
