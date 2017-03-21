package javasign.com.dompetsehat.ui.activities.transaction.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;

/**
 * Created by bastianbentra on 8/10/16.
 */
public class AdapterTransactionFragmentPager<T extends BaseFragment> extends FragmentStatePagerAdapter {
  private Context context;
  private List<FragmentModel> listFragment;
  private ChangeViewPager changeViewPager;

  public AdapterTransactionFragmentPager(FragmentManager fm, Context context,
      List<FragmentModel> listFragment) {
    super(fm);
    this.context = context;
    this.listFragment = listFragment;
  }

  public void setListFragment(List<FragmentModel> listFragment) {
    this.listFragment = listFragment;
  }

  public void setChangeViewPager(ChangeViewPager changeViewPager) {
    this.changeViewPager = changeViewPager;
  }

  @Override public Fragment getItem(int position) {
    return listFragment.get(position).fragment;
  }

  public T getFragmentInPosition(int position){
    return (T) listFragment.get(position).fragment;
  }

  @Override public int getCount() {
    return listFragment.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return listFragment.get(position).title.toUpperCase();
  }

  public interface ChangeViewPager{
    void onChange(int position);
  }
}
