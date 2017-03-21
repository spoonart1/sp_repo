package javasign.com.dompetsehat.presenter.referral;

import javasign.com.dompetsehat.base.MvpView;

/**
 * Created by avesina on 10/24/16.
 */
public interface ReferralInterface extends MvpView{
   void setAdapter(boolean isConnectIstitusi,String referralCode,String status,String[] labels, String[] icons, int[] colors);
   void setAdapterReferral(String referralCode,String[] labels, String[] icons, int[] colors);
}
