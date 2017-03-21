package javasign.com.dompetsehat.ui.activities.help.pojo;

import android.support.v4.app.Fragment;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class FragmentHelpModel {
  public String title;
  public Fragment fragment;

  public FragmentHelpModel(String title, Fragment fragment) {
    this.title = title;
    this.fragment = fragment;
  }
}
