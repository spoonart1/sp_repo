package javasign.com.dompetsehat.ui.activities.referral.pojo;

import android.support.v4.app.Fragment;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class FragmentReferralLoaderModel {
  public String title;
  public Fragment fragment;

  public FragmentReferralLoaderModel(String title, Fragment fragment){
    this.title = title;
    this.fragment = fragment;
  }
}
