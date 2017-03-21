package javasign.com.dompetsehat.ui.activities.budget.listfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;

/**
 * Created by lafran on 10/3/16.
 */

public class SchedulerFragment extends Fragment {

  private List<AdapterScheduler.SHModel> models;
  private AdapterScheduler adapter;
  private LinearLayoutManager layoutManager;
  @Bind(R.id.recycleview) RecyclerView recycleview;

  public SchedulerFragment(){

  }

  public static SchedulerFragment newInstance(List<AdapterScheduler.SHModel> models){
    SchedulerFragment fragment = new SchedulerFragment();
    fragment.init(models);
    return fragment;
  }

  public void init(List<AdapterScheduler.SHModel> models){
    this.models = models;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.fragment_scheduler, null);
    ButterKnife.bind(this, view);

    layoutManager = new LinearLayoutManager(getContext());

    if(adapter == null && models != null) {
      adapter = new AdapterScheduler(getActivity(), getFragmentManager(), models);
      recycleview.setLayoutManager(layoutManager);
      recycleview.setAdapter(adapter);
    }

    return view;
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }
}
