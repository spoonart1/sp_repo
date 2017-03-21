package javasign.com.dompetsehat.ui.activities.transaction.pojo;

import android.support.v4.app.Fragment;

/**
 * Created by bastianbentra on 8/10/16.
 */
public class FragmentModel {
  public String title;
  public Fragment fragment;
  public String month;
  public String year;
  public Integer category_id;
  public Integer acount_id;

  public FragmentModel(){
  }

  public FragmentModel(String title, Fragment fragment) {
    this.title = title;
    this.fragment = fragment;
  }

  public FragmentModel setTitle(String title){
    this.title = title;
    return this;
  }

  public FragmentModel setAcount_id(Integer acount_id) {
    this.acount_id = acount_id;
    return this;
  }

  public FragmentModel setCategory_id(Integer category_id) {
    this.category_id = category_id;
    return this;
  }

  public FragmentModel setFragment(Fragment fragment) {
    this.fragment = fragment;
    return this;
  }

  public FragmentModel setMonth(String month) {
    this.month = month;
    return this;
  }

  public FragmentModel setYear(String year) {
    this.year = year;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Fragment getFragment() {
    return fragment;
  }
}
