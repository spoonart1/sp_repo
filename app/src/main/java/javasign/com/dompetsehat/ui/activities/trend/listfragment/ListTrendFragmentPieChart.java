package javasign.com.dompetsehat.ui.activities.trend.listfragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.trend.adapter.AdapterTrendsChartLabel;
import javasign.com.dompetsehat.ui.activities.trend.adapter.AdapterTrendListCategory;
import javasign.com.dompetsehat.ui.activities.trend.pojo.TrendModelCategory;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by bastianbentra on 8/16/16.
 */
public class ListTrendFragmentPieChart extends Fragment {

  private static final int MAX_SPAN_COUNT = 4;

  @Bind(R.id.chart) PieChartView pieChartView;
  @Bind(R.id.indicator_data) RecyclerView indicator;
  @Bind(R.id.recycleview) RecyclerView recycleview;

  private List<TrendModelCategory> trendModelCategories;

  public static ListTrendFragmentPieChart newInstance(
      List<TrendModelCategory> trendModelCategories) {
    ListTrendFragmentPieChart fragment = new ListTrendFragmentPieChart();
    fragment.init(trendModelCategories);
    return fragment;
  }

  private void init(List<TrendModelCategory> trendModelCategories) {
    this.trendModelCategories = trendModelCategories;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trend_list_category, null);
    ButterKnife.bind(this, view);

    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recycleview.setLayoutManager(layoutManager);

    initChart();
    drawIndicator();
    initData();
    return view;
  }

  private void initChart() {
    //properties
    pieChartView.setCircleFillRatio(1.0f);
    pieChartView.setValueSelectionEnabled(true);
    boolean hasLabels = false;
    boolean hasLabelsOutside = false;
    boolean hasCenterCircle = false;
    boolean hasCenterText1 = false;
    boolean hasCenterText2 = false;
    boolean isExploded = true;
    boolean hasLabelForSelected = false;

    List<SliceValue> values = new ArrayList<SliceValue>();
    for (int i = 0; i < trendModelCategories.size(); ++i) {
      SliceValue sliceValue = new SliceValue(trendModelCategories.get(i).amountTransaction,Color.parseColor(trendModelCategories.get(i).category.getBgColor()));
      values.add(sliceValue);
    }

    PieChartData data = new PieChartData(values);
    data.setHasLabels(hasLabels);
    data.setHasLabelsOnlyForSelected(hasLabelForSelected);
    data.setHasLabelsOutside(hasLabelsOutside);
    data.setHasCenterCircle(hasCenterCircle);

    if (isExploded) {
      data.setSlicesSpacing(4);
    }

    pieChartView.setPieChartData(data);
  }

  private void drawIndicator() {
    int spanCount = MAX_SPAN_COUNT;
    final StaggeredGridLayoutManager gridLayoutManager =
        new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
    indicator.setLayoutManager(gridLayoutManager);

    AdapterTrendsChartLabel adapterTrendsChartLabel = new AdapterTrendsChartLabel(trendModelCategories);
    indicator.setAdapter(adapterTrendsChartLabel);
  }

  private void initData() {
    AdapterTrendListCategory adapterTrendListCategory = new AdapterTrendListCategory(trendModelCategories);
    recycleview.setAdapter(adapterTrendListCategory);
  }
}
