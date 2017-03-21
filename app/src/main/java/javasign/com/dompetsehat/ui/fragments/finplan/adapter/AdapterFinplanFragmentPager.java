package javasign.com.dompetsehat.ui.fragments.finplan.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;
import javasign.com.dompetsehat.ui.fragments.finplan.pojo.FragmentFinplanModel;

/**
 * Created by bastianbentra on 8/25/16.
 */
public class AdapterFinplanFragmentPager extends FragmentStatePagerAdapter {

  private Context context;
  private List<FragmentFinplanModel> listFragment;

  public AdapterFinplanFragmentPager(FragmentManager fm, Context context,
      List<FragmentFinplanModel> listFragment) {
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
