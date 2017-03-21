package javasign.com.dompetsehat.presenter.login;

import android.content.Context;

import javax.inject.Inject;

import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ActivityContext;
import javasign.com.dompetsehat.ui.CommonInterface;
import javasign.com.dompetsehat.utils.LoadAndSaveImage;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by aves on 8/8/16.
 */

public class SignInViewPresenter extends BasePresenter<CommonInterface> {
    private Context context;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    MCryptNew mcrypt = new MCryptNew();
    private final DataManager dataManager;
    private final SessionManager session;
    private final LoadAndSaveImage saveImage;

    @Inject public SignInViewPresenter(DataManager dataManager, SessionManager session, LoadAndSaveImage saveImage, @ActivityContext Context context){
        this.context = context;
        this.dataManager = dataManager;
        this.session = session;
        this.saveImage = saveImage;
    }

    @Override public void attachView(CommonInterface mvpView) {
        super.attachView(mvpView);
    }

    @Override public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }


}