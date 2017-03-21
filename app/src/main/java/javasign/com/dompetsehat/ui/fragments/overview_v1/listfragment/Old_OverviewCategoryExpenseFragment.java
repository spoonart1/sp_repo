package javasign.com.dompetsehat.ui.fragments.overview_v1.listfragment;

import android.graphics.Color;
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
import java.util.Map;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.presenter.overview.OverviewCategoryPresenter;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v1.adapter.Old_AdapterOverviewByCategory;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.SliceValue;
import timber.log.Timber;

/**
 * Created by lafran on 9/21/16.
 */
@Deprecated
public class Old_OverviewCategoryExpenseFragment extends BaseFragment{

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.tv_value) TextView tv_value;
  @Bind(R.id.tv_time) TextView tv_time;

  private Old_AdapterOverviewByCategory adapter;
  private int mAccentColor = Color.parseColor("#f44336");
  private String tab_name;
  Old_AdapterOverviewByCategory.model models;
  @Inject OverviewCategoryPresenter presenter;
  NewMainActivityMyFin activity;
  RupiahCurrencyFormat format = new RupiahCurrencyFormat();
  Map<Category, Double> map;

  public Old_OverviewCategoryExpenseFragment() {

  }

  public Old_OverviewCategoryExpenseFragment setTab_name(String tab_name) {
    this.tab_name = tab_name;
    return this;
  }

  public static Old_OverviewCategoryExpenseFragment newInstance() {
    Old_OverviewCategoryExpenseFragment o = new Old_OverviewCategoryExpenseFragment();
    o.init();
    return o;
  }

  private void init() {

  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.fragment_overview_category, null);
    ButterKnife.bind(this, view);
    this.activity = (NewMainActivityMyFin) getActivity();
    activity.getActivityComponent().inject(this);
    presenter.attachView(this);
    mAccentColor = Helper.getAccentColor(getActivity(), State.OVERVIEW_DETAIL_EXPENSE);
    initProperties();
    initData();
    tv_value.setTextColor(mAccentColor);

    return view;
  }

  private void initProperties() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
  }

  private void initData() {
    adapter = new Old_AdapterOverviewByCategory(models, mAccentColor);
    recyclerView.setAdapter(adapter);
    if(this.tab_name!=null) {
      tv_time.setText(getString(R.string.expense)+" "+this.tab_name);
      presenter.populateOverviewCategory("DB",tab_name);
    }
  }

  //@Override public void setListTransaction(Old_AdapterOverviewByCategory.model models) {
  //  activity.runOnUiThread(()->{
  //    this.models = models;
  //    adapter = new Old_AdapterOverviewByCategory(models, mAccentColor);
  //    adapter.setPieChartOnValueSelectListener(new PieChartOnValueSelectListener() {
  //      @Override public void onValueSelected(int arcIndex, SliceValue value) {
  //        if(adapter.getCategories() != null) {
  //          Integer[] ids = presenter.getIdsByCategory(adapter.getCategories().get(arcIndex),"DB");
  //          adapter.setIds(ids);
  //          adapter.notifyDataSetChanged();
  //        }
  //        setTotal(value.getValue());
  //      }
  //
  //      @Override public void onValueDeselected() {
  //        presenter.populateOverviewCategory("DB",tab_name);
  //      }
  //    });
  //    adapter.setDataSet(map);
  //    recyclerView.setAdapter(adapter);
  //    adapter.notifyDataSetChanged();
  //  });
  //}

  @Override public void setTotal(double total) {
    activity.runOnUiThread(()->{
      tv_value.setText(format.toRupiahFormatSimple(total));
      tv_value.setTextColor(mAccentColor);
    });
  }


  @Override public void setChartCategory(Map<Category, Double> map) {
    activity.runOnUiThread(()->{
      Timber.e("avesina data setChartCategory");
      this.map = map;
      adapter.setDataSet(map);
      recyclerView.setAdapter(adapter);
      adapter.notifyDataSetChanged();
    });
  }

  @Override public void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
