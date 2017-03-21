package javasign.com.dompetsehat.ui.fragments.overview_v1.pojo;

import android.support.v4.app.Fragment;

/**
 * Created by lafran on 9/19/16.
 */
@Deprecated
public class Old_OverviewFragmentModel {
  public String title;
  public int type;
  public Fragment fragment;

  public Old_OverviewFragmentModel(String title, Fragment fragment){
    this.title = title;
    this.fragment = fragment;
  }
}
