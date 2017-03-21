package javasign.com.dompetsehat.ui.fragments.overview_v1.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import java.util.List;
import javasign.com.dompetsehat.ui.fragments.overview_v1.pojo.Old_OverviewFragmentModel;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvFragmentModel;

/**
 * Created by bastianbentra on 8/25/16.
 */
@Deprecated
public class Old_AdapterOverviewFragmentPager extends FragmentStatePagerAdapter {

  private Context context;
  private List<OvFragmentModel> listFragment;

  public Old_AdapterOverviewFragmentPager(FragmentManager fm, Context context,
      List<OvFragmentModel> listFragment) {
    super(fm);
    this.context = context;
    this.listFragment = listFragment;
  }

  public void setListFragment(List<OvFragmentModel> listFragment){
    this.listFragment = listFragment;
  }

  @Override
  public Parcelable saveState() {
    return null;
  }

  public List<OvFragmentModel> getListFragments(){
    return this.listFragment;
  }

  @Override public Fragment getItem(int position) {
    return listFragment.get(position).fragment;
  }

  @Override public int getCount() {
    return listFragment.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return listFragment.get(position).title.toUpperCase();
  }

  @Override
  public int getItemPosition(Object object){
    return PagerAdapter.POSITION_NONE;
  }
}
