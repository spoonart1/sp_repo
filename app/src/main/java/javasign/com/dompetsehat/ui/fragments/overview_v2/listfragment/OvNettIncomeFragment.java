package javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.overview.OverviewNetIncomePresenter;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.activities.transaction.AddTransactionActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.OverviewFragment;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import timber.log.Timber;

/**
 * Created by lafran on 1/25/17.
 */

public class OvNettIncomeFragment extends BaseFragment {

  @Bind(R.id.columnChart) ColumnChartView columnChart;
  @Bind(R.id.tv_total_income) TextView tv_total_income;
  @Bind(R.id.tv_total_expense) TextView tv_total_expense;
  @Bind(R.id.tv_value_transaction) TextView tv_value_transaction;
  @Bind(R.id.tv_avg_expense) TextView tv_avg_expense;
  @Bind(R.id.simple_blank_view) View emptyView;
  @Bind(R.id.scroll) NestedScrollView scroll;

  private int tabType;
  private Integer tahun;
  private Integer bulan;
  private Integer month;
  private String tabName;
  private int position;
  private Context context;

  @Inject OverviewNetIncomePresenter presenter;

  @Bind(R.id.tv_time) TextView tv_time;
  @Bind(R.id.tv_value) TextView tv_value;

  private ColumnChartData data;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private NewMainActivityMyFin activityMyFin;
  private ArrayList<Integer> ids;
  private OnItemClik onItemClik;
  ;
  private boolean isChartEmpty = true;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.fragment_overview_nett_income, null);
    ButterKnife.bind(this, view);

    activityMyFin = (NewMainActivityMyFin) getActivity();
    activityMyFin.getActivityComponent().inject(this);
    presenter.attachView(this);
    if (tabType == OverviewFragment.FILTER_SINGLE_YEARLY && this.tahun != null) {
      presenter.loadData(this.tahun);
      tv_time.setText(Words.toSentenceCase(getString(R.string.year)) + " " + tahun);
    }else if(tabType == OverviewFragment.FILTER_SINGLE_MONTHLY && this.tahun != null && this.bulan != null){
      presenter.loadData(this.bulan,this.tahun);
      tv_time.setText(Words.toSentenceCase(tabName));
    }else if(tabType == OverviewFragment.FILTER_GROUPLY && this.tabName != null){
      presenter.loadData(tabName);
      tv_time.setText(Words.toSentenceCase(tabName));
    }
    Helper.checkIfZero(emptyView, true, v -> startActivity(
        new Intent(getActivity(), AddTransactionActivity.class).putExtra(
            AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_ADD)
            .putExtra(Words.MONTH, month)
            .putExtra(Words.YEAR, tahun)));
    return view;
  }

  public OvNettIncomeFragment setTahun(int tahun) {
    this.tahun = tahun;
    return this;
  }

  public OvNettIncomeFragment setBulan(int bulan){
    this.bulan = bulan;
    return this;
  }

  public OvNettIncomeFragment setPosition(int pos) {
    this.position = pos;
    return this;
  }

  public OvNettIncomeFragment setTabType(int tabType) {
    this.tabType = tabType;
    return this;
  }

  public OvNettIncomeFragment setTabName(String tabName) {
    this.tabName = tabName;
    return this;
  }

  private OvNettIncomeFragment setMonth(int month) {
    this.month = month;
    String nama_bulan = Helper.getMonthName(month, false);
    tv_time.setText("Bulan " + nama_bulan);
    return this;
  }

  public OvNettIncomeFragment setOnItemClik(OnItemClik onItemClik) {
    this.onItemClik = onItemClik;
    return this;
  }

  @Override public void setNetIncomeLabel(double total_pendapatan, double total_pengeluaran,
      int total_transaksi, ArrayList<Integer> ids) {
    try {
      activityMyFin.runOnUiThread(() -> {
        this.ids = ids;
        Timber.e("avesina product "+ids);
        tv_total_income.setText(format.toRupiahFormatSimple(total_pendapatan));
        tv_total_expense.setText(format.toRupiahFormatSimple(total_pengeluaran));
        tv_value_transaction.setText(
            String.valueOf(total_transaksi) + " " + getString(R.string.transactions));
        double totalBersih = total_pendapatan - total_pengeluaran;
        tv_value.setText(format.toRupiahFormatSimple(totalBersih));
        Timber.e("total transaksi " + total_transaksi);
        if (total_transaksi > 0) {
          isChartEmpty = false;
        }
      });
    } catch (Exception e) {
      Timber.e("ERROR setNetIncomeLabel " + e);
    }

    Helper.checkIfZero(emptyView, isChartEmpty, v -> startActivity(
        new Intent(getActivity(), AddTransactionActivity.class).putExtra(
            AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_ADD)
            .putExtra(Words.MONTH, month)
            .putExtra(Words.YEAR, tahun)));
  }

  @Override
  public void setChartNetIncome(List<Column> columns, List<AxisValue> axisValues, String name) {
    activityMyFin.runOnUiThread(() -> {
      data = new ColumnChartData(columns);
      Axis axisBottom = new Axis();
      Axis axisLeft = new Axis().setHasLines(true);

      axisBottom.setValues(axisValues);
      axisBottom.setHasSeparationLine(true);
      axisLeft.setFormatter(getValueFormatter(name));
      axisLeft.setMaxLabelChars(4);

      data.setAxisXBottom(axisBottom);
      data.setAxisYLeft(axisLeft);

      columnChart.setColumnChartData(data);
      columnChart.setScrollContainer(true);
      columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
      //Viewport v = new Viewport(columnChart.getMaximumViewport());
      //v.left = 4.5f;
      //columnChart.setCurrentViewport(v);
      ValueTouchListener valueTouchListener = new ValueTouchListener();
      columnChart.setOnValueTouchListener(valueTouchListener);
      //columnChart.scrollTo(0, 0);
    });
  }

  private SimpleAxisValueFormatter getValueFormatter(String prefix) {
    SimpleAxisValueFormatter valueFormatter = new SimpleAxisValueFormatter();
    valueFormatter.setAppendedText(prefix.toCharArray());

    return valueFormatter;
  }

  public class ValueTouchListener implements ColumnChartOnValueSelectListener {

    @Override
    public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
      String key =  getLabel(value.getLabelAsChars());
      if(key.contains("week")){
        presenter.loadDataMonth(Integer.valueOf(key.replace("week ","")),bulan,tahun,tabName);
      }else {
        String[] time = Words.FullMonthName.get(key);
        int month = Integer.parseInt(time[1]);
        setMonth(month);
        double amount = value.getValue() * 1000;
        tv_value.setTextColor(presenter.accentColor(amount));
        if(tahun != null) {
          presenter.loadDataMonth(month, tahun);
        }else if(tabName != null){
          presenter.loadDataMonth(month,tahun,tabName);
        }
        scrollToBottom();
      }
    }

    private void scrollToBottom() {
      scroll.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
      scroll.dispatchNestedPreScroll(0, 200, null, null);
      scroll.dispatchNestedScroll(0, 0, 0, 0, new int[] { 0, -200 });
      scroll.smoothScrollTo(0, scroll.getBottom());
    }

    @Override public void onValueDeselected() {
      // TODO Auto-generated method stub
    }

    private String getLabel(char[] chars) {
      String key = "";
      for (int i = 0; i < chars.length; i++) {
        key += chars[i];
      }
      return key;
    }
  }

  @OnClick(R.id.btn_detail) void seeDetail() {
    gotoTransactionActivityByThisTransactionData();
  }

  private void gotoTransactionActivityByThisTransactionData() {
    if (ids != null) {
      if (ids.size() != 0) {
        startActivity(new Intent(getActivity(), TransactionsActivity.class).putExtra(
            TransactionsActivity.FROM, State.FROM_OVERVIEW_NETT_INCOME)
            .putExtra(State.IDS_TRANSACTION, ids));
        return;
      }
    }
    startActivity(
        new Intent(getActivity(), TransactionsActivity.class).putExtra(TransactionsActivity.FROM,
            State.FROM_OVERVIEW_NETT_INCOME).putExtra(State.YEAR, tahun));
  }

  public interface OnItemClik {
    void OnClick(int x, int y);
  }

  @Override public void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
