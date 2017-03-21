package javasign.com.dompetsehat.ui.fragments.overview_v1.listfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.overview.OverviewTransactionPresenter;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v1.adapter.Old_AdapterOverviewByTransaction;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvCategoryModel;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javax.inject.Inject;

/**
 * Created by lafran on 9/20/16.
 */
@Deprecated
public class Old_OverviewTransactionIncomeFragment extends BaseFragment{
  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.tv_value) TextView tv_value;
  @Bind(R.id.tv_time) TextView tv_time;

  private Old_AdapterOverviewByTransaction adapter;
  private int mAccentColor = Helper.GREEN_DOMPET_COLOR;
  String tab_name;
  NewMainActivityMyFin activity;
  @Inject OverviewTransactionPresenter presenter;
  RupiahCurrencyFormat format = new RupiahCurrencyFormat();


  public Old_OverviewTransactionIncomeFragment(){
    //keep empty
  }

  public static Old_OverviewTransactionIncomeFragment newInstance(){
    Old_OverviewTransactionIncomeFragment fragment = new Old_OverviewTransactionIncomeFragment();
    return fragment;
  }

  public Old_OverviewTransactionIncomeFragment setTab_name(String tab_name) {
    this.tab_name = tab_name;
    return this;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.fragment_overview_inout, null);
    ButterKnife.bind(this, view);

    this.activity = (NewMainActivityMyFin) getActivity();
    activity.getActivityComponent().inject(this);
    presenter.attachView(this);
    initProperties();

    tv_value.setTextColor(mAccentColor);

    return view;
  }

  private void initProperties() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    setData();
  }

  private void setData(){
    adapter = new Old_AdapterOverviewByTransaction(null, mAccentColor);
    recyclerView.setAdapter(adapter);
    if(this.tab_name != null){
      tv_time.setText(getString(R.string.income)+" "+this.tab_name.toLowerCase());
      presenter.populateOverviewTransaction("CR",this.tab_name);
    }
  }

  @Override public void setListTransaction(OvCategoryModel models) {
    activity.runOnUiThread(()->{
      adapter.setList(models);
      recyclerView.setAdapter(adapter);
      adapter.notifyDataSetChanged();
    });
  }

  @Override public void setTotal(double total) {
    getActivity().runOnUiThread(()->{
      tv_value.setText(format.toRupiahFormatSimple(total));
    });
  }

  @Override public void setChartLabelValues(String[] labels,float[] values,int min, int max, String label) {
    activity.runOnUiThread(()->{
      adapter.setLabelAndValues(labels,values);
      adapter.notifyDataSetChanged();
    });
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }
}
