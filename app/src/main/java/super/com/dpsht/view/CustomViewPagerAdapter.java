package javasign.com.dompetsehat.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import java.util.List;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;
import javasign.com.dompetsehat.ui.fragments.ImageFragment;
import javasign.com.dompetsehat.ui.fragments.debts.ListDebtFragment;

/**
 * Created by Spoonart on 12/31/2015.
 */
public class CustomViewPagerAdapter extends FragmentStatePagerAdapter {

  ImageFragment[] imageFragments;

  public CustomViewPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  public static CustomViewPagerAdapter newInstance(FragmentManager fm,
      ImageFragment[] imageFragments) {
    CustomViewPagerAdapter customViewPagerAdapter = new CustomViewPagerAdapter(fm);
    customViewPagerAdapter.init(imageFragments);
    return customViewPagerAdapter;
  }

  private void init(ImageFragment[] imageFragments) {
    this.imageFragments = imageFragments;
  }

  @Override public int getCount() {
    return imageFragments.length;
  }

  @Override public Fragment getItem(int position) {
    return imageFragments[position];
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    if(object instanceof ImageFragment){
      view.setTag(((ImageFragment) object).getIdentifier());
    }
    return super.isViewFromObject(view, object);
  }
}
