package javasign.com.dompetsehat.ui.fragments.debts;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.List;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;

/**
 * Created by avesina on 2/17/17.
 */

public class AdaptetViewPagerDebts extends FragmentStatePagerAdapter {
  private Context context;
  private List<ListDebtFragment> listFragment;

  public AdaptetViewPagerDebts(FragmentManager fm, Context context,List<ListDebtFragment> listFragment) {
    super(fm);
    this.context = context;
    this.listFragment = listFragment;
  }

  @Override public Fragment getItem(int position) {
    return listFragment.get(position);
  }

  @Override public int getCount() {
    return listFragment.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return listFragment.get(position).title;
  }
}
