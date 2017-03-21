package javasign.com.dompetsehat.base;

/**
 * Created by aves on 8/8/16.
 */

public interface Presenter<V extends MvpView> {
    void attachView(V mvpView);
    void detachView();
}
