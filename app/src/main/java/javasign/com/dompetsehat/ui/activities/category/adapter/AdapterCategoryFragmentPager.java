package javasign.com.dompetsehat.ui.activities.category.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;

/**
 * Created by bastianbentra on 8/10/16.
 */
public class AdapterCategoryFragmentPager extends FragmentStatePagerAdapter {
  private Context context;
  private List<FragmentCategoryModel> listFragment;

  public AdapterCategoryFragmentPager(FragmentManager fm, Context context,
      List<FragmentCategoryModel> listFragment) {
    super(fm);
    this.context = context;
    this.listFragment = listFragment;
  }

  @Override public Fragment getItem(int position) {
    return listFragment.get(position).fragment;
  }

  @Override public int getCount() {
    return listFragment.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return listFragment.get(position).title;
  }
}
