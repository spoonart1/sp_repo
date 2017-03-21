package javasign.com.dompetsehat.ui.fragments.overview_v2.pojo;

import android.support.v4.app.Fragment;

/**
 * Created by lafran on 1/25/17.
 */

public class OvFragmentModel {

  public String title;
  public Integer type;
  public Integer month;
  public Integer year;
  public Fragment fragment;

  public OvFragmentModel(String title,Fragment fragment) {
    this.title = title;
    this.fragment = fragment;
  }
}
