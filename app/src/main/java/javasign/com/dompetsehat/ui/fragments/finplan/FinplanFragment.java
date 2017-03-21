package javasign.com.dompetsehat.ui.fragments.finplan;

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
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.main.FinplanPresenter;
import javasign.com.dompetsehat.ui.fragments.finplan.adapter.AdapterFinplanFragmentPager;
import javasign.com.dompetsehat.ui.fragments.finplan.listfragment.PlanFragment;
import javasign.com.dompetsehat.ui.fragments.finplan.pojo.FragmentFinplanModel;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/18/16.
 */
public class FinplanFragment extends Fragment {

  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.pager) ViewPager viewPager;

  private AdapterFinplanFragmentPager adapterFinplanFragmentPager;

  @Inject FinplanPresenter presenter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_finplan, null);
    ButterKnife.bind(this, view);

    init();

    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  private void init() {
    List<FragmentFinplanModel> models = new ArrayList<>();
    models.add(new FragmentFinplanModel("Rencana", new PlanFragment()));
    adapterFinplanFragmentPager = new AdapterFinplanFragmentPager(getChildFragmentManager(), getContext(), models);
    viewPager.setAdapter(adapterFinplanFragmentPager);
    presenter.loadData();
  }

}
