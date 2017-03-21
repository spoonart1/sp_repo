package javasign.com.dompetsehat.presenter.profile;

import android.graphics.Bitmap;
import javasign.com.dompetsehat.ui.CommonInterface;

/**
 * Created by aves on 8/26/16.
 */

public interface ProfileInterface extends CommonInterface {
    void putData(Object data);
    void setProfile(String username,String phone,String email,String password,String tanggal_birthday,Double pendapatan,Integer anak,Bitmap bitmap, boolean fb_connected,String fb_id);
    void setPhoneEmail(String phone,String email);
    void finishValidate();
    void setLevel(String level);
    void showEmailVerificationButton(boolean set);
}
