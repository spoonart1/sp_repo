package javasign.com.dompetsehat.ui.activities.trend.listfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javasign.com.dompetsehat.ui.activities.trend.adapter.AdapterTrendsChartLabel;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.trend.TrendsPresenter;
import javasign.com.dompetsehat.ui.activities.trend.pojo.TrendModelOverall;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by bastianbentra on 8/16/16.
 */
public class ListTrendFragmentLineChart extends Fragment{

  private static final int MAX_LINE_DATA = 3;

  private static final int REQUEST_ID = 21;

  @Bind(R.id.chart) LineChartView lineChartView;
  @Bind(R.id.indicator_data) RecyclerView indicator;
  @Bind(R.id.tv_trend_avg_transaction) TextView tv_trend_avg_transaction;
  @Bind(R.id.tv_trend_balance) TextView tv_trend_balance;
  @Bind(R.id.tv_trend_category) TextView tv_trend_category;
  @Bind(R.id.tv_trend_expense) TextView tv_trend_expense;
  @Bind(R.id.tv_trend_income) TextView tv_trend_income;
  @Bind(R.id.tv_trend_transaction) TextView tv_trend_transaction;
  private List<TrendModelOverall> trendModelOveralls;
  private DetailOverAll detailOverAll;
  RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();

  @Inject
  TrendsPresenter presenter;

  public static ListTrendFragmentLineChart newInstance(List<TrendModelOverall> trendModelOveralls){
    ListTrendFragmentLineChart fragment = new ListTrendFragmentLineChart();
    fragment.init(trendModelOveralls);

    return fragment;
  }

  public Fragment setDetailOverAll(DetailOverAll detailOverAll) {
    this.detailOverAll = detailOverAll;
    return this;
  }

  private void init(List<TrendModelOverall> trendModelOveralls){
    this.trendModelOveralls = trendModelOveralls;
    System.out.println("ListTrendFragmentLineChart.init size  " +trendModelOveralls.size());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_trend_list_overall, null);
    ButterKnife.bind(this, view);

    initChart();
    drawIndicator();

    return view;
  }

  private void initChart(){
    lineChartView.setInteractive(true);
    lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

    List<Line> lines = new ArrayList<Line>();
    for (int i = 0; i < trendModelOveralls.size(); ++i) {
      List<PointValue> values = new ArrayList<PointValue>();
      TrendModelOverall trendModelOverall = trendModelOveralls.get(i);
      for (int j = 0; j < trendModelOverall.amountFlows.length; ++j) {
        float amount = trendModelOverall.amountFlows[j];
        values.add(new PointValue(j, amount));
      }

      Line line = new Line(values);
      line.setColor(trendModelOverall.lineColor);
      line.setShape(ValueShape.CIRCLE);
      line.setCubic(false);
      line.setFilled(false);
      line.setHasLabels(false);
      line.setHasLabelsOnlyForSelected(true);
      line.setHasLines(true);
      line.setHasPoints(true);
      lines.add(line);
    }

    lineChartView.setValueSelectionEnabled(true);
    LineChartData data = new LineChartData(lines);
    data.setBaseValue(Float.NEGATIVE_INFINITY);
    lineChartView.setLineChartData(data);

    if(detailOverAll != null){
      tv_trend_balance.setText(rcf.toRupiahFormatSimple(detailOverAll.balance));
      tv_trend_avg_transaction.setText(detailOverAll.avg_transaction+" transaksi");
      tv_trend_expense.setText(rcf.toRupiahFormatSimple(detailOverAll.total_expense));
      tv_trend_transaction.setText(detailOverAll.transaction+" transaksi");
      tv_trend_income.setText(rcf.toRupiahFormatSimple(detailOverAll.total_income));
      tv_trend_category.setText("1 category");
    }
  }

  private void drawIndicator() {
    int spanCount = MAX_LINE_DATA;
    final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
    indicator.setLayoutManager(gridLayoutManager);

    AdapterTrendsChartLabel adapterTrendsChartLabel = new AdapterTrendsChartLabel(trendModelOveralls);
    indicator.setAdapter(adapterTrendsChartLabel);
  }

  public static class DetailOverAll{
    public float balance;
    public float total_income;
    public float total_expense;
    public int transaction;
    public int total_category;
    public float avg_transaction;

    public DetailOverAll(){

    }

  }

}
