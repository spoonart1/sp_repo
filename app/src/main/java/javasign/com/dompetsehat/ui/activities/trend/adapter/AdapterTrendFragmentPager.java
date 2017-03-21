package javasign.com.dompetsehat.ui.activities.trend.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;
import javasign.com.dompetsehat.ui.activities.trend.pojo.FragmentModel;

/**
 * Created by bastianbentra on 8/10/16.
 */
public class AdapterTrendFragmentPager extends FragmentStatePagerAdapter {
  private Context context;
  private List<FragmentModel> listFragment;

  public AdapterTrendFragmentPager(FragmentManager fm, Context context,
      List<FragmentModel> listFragment) {
    super(fm);
    this.context = context;
    this.listFragment = listFragment;
  }

  public void setListFragment(List<FragmentModel> listFragment) {
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
