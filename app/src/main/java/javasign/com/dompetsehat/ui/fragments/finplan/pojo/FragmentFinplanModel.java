package javasign.com.dompetsehat.ui.fragments.finplan.pojo;

import android.support.v4.app.Fragment;

/**
 * Created by bastianbentra on 8/25/16.
 */
public class FragmentFinplanModel {
  public String title;
  public int type;
  public Fragment fragment;

  public FragmentFinplanModel(String title, Fragment fragment){
    this.title = title;
    this.fragment = fragment;
  }
}
