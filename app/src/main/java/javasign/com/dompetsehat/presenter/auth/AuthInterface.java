package javasign.com.dompetsehat.presenter.auth;

import javasign.com.dompetsehat.ui.CommonInterface;

/**
 * Created by aves on 8/23/16.
 */

public interface AuthInterface extends CommonInterface {
    void gotoNextActivity(String auth_with,String access_token,Object data);
    void showDialogConfirm(boolean is_password_valid,AuthPresenter.onClickDialog clickDialog);
}
