package javasign.com.dompetsehat.ui.activities.trend.pojo;

import android.support.v4.app.Fragment;

/**
 * Created by bastianbentra on 8/10/16.
 */
public class FragmentModel {
  public String title;
  public Fragment fragment;

  public FragmentModel(String title, Fragment fragment) {
    this.title = title;
    this.fragment = fragment;
  }

  public String getTitle() {
    return title;
  }

  public Fragment getFragment() {
    return fragment;
  }
}
