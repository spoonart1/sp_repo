package javasign.com.dompetsehat.ui.fragments.debts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategoryFragmentPager;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.view.CustomViewPagerAdapter;

/**
 * Created by avesina on 2/17/17.
 */

public class DebtFragment extends Fragment {
  @Bind(R.id.pager) ViewPager pager;
  @Bind(R.id.tablayout) TabLayout tablayout;
  String[] labels = new String[] { "Hutang", "Piutang" };
  String[] types = new String[] { ListDebtFragment.TYPE_BORROW, ListDebtFragment.TYPE_LEND };
  AdaptetViewPagerDebts adapter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_debts, null);
    ButterKnife.bind(this, view);
    Helper.trackThis(getActivity(), "user membuka tampilan hutang piutang");
    init();
    return view;
  }

  private void init(){
    ArrayList<ListDebtFragment> fragments = new ArrayList<>();
    int i = 0;
    for (String label:labels){
      ListDebtFragment fragment = ListDebtFragment.newInstance(label.toUpperCase(),types[i]);
      fragments.add(fragment);
      i++;
    }
    adapter = new AdaptetViewPagerDebts(getActivity().getSupportFragmentManager(),getActivity(),fragments);
    pager.setAdapter(adapter);
    tablayout.setupWithViewPager(pager);
  }
}
