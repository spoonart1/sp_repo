package javasign.com.dompetsehat.ui.fragments.overview_v1.listfragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
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
 * Created by lafran on 9/19/16.
 */
@Deprecated
public class Old_OverviewFragmentNettIncome extends BaseFragment {

  @Bind(R.id.columnChart) ColumnChartView columnChart;
  @Bind(R.id.tv_total_income) TextView tv_total_income;
  @Bind(R.id.tv_total_expense) TextView tv_total_expense;
  @Bind(R.id.tv_value_transaction) TextView tv_value_transaction;
  @Bind(R.id.tv_avg_expense) TextView tv_avg_expense;
  @Bind(R.id.simple_blank_view) View emptyView;
  @Bind(R.id.scroll) NestedScrollView scroll;

  private Integer tahun = 2016;
  private Integer month = 12;
  private int position;
  private Context context;

  @Inject OverviewNetIncomePresenter presenter;

  @Bind(R.id.tv_time) TextView tv_time;
  @Bind(R.id.tv_value) TextView tv_value;

  OnItemClik onItemClik;

  private ColumnChartData data;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private NewMainActivityMyFin activityMyFin;
  private ArrayList<Integer> ids;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.old_fragment_overview_nett_income, null);
    ButterKnife.bind(this, view);

    activityMyFin = (NewMainActivityMyFin) getActivity();
    activityMyFin.getActivityComponent().inject(this);
    presenter.attachView(this);
    if (this.tahun != null) {
      presenter.loadData(this.tahun);
      tv_time.setText(Words.toSentenceCase(getString(R.string.year))+" " + tahun);
    }
    Helper.checkIfZero(emptyView,true, v -> startActivity(
        new Intent(getActivity(), AddTransactionActivity.class).putExtra(
            AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_ADD)
            .putExtra(Words.MONTH, month)
            .putExtra(Words.YEAR, tahun)));
    return view;
  }

  public Old_OverviewFragmentNettIncome setTahun(int tahun) {
    this.tahun = tahun;
    return this;
  }

  public Old_OverviewFragmentNettIncome setPosition(int pos) {
    this.position = pos;
    return this;
  }

  private void setMonth(int month) {
    this.month = month;
    String nama_bulan = Helper.getMonthName(month, false);
    tv_time.setText("Bulan " + nama_bulan);
  }

  public Old_OverviewFragmentNettIncome setOnItemClik(OnItemClik onItemClik) {
    this.onItemClik = onItemClik;
    return this;
  }

  @Override public void setNetIncomeLabel(double total_pendapatan, double total_pengeluaran,
      int total_transaksi, ArrayList<Integer> ids) {
    try {
      activityMyFin.runOnUiThread(() -> {
        this.ids = ids;
        tv_total_income.setText(format.toRupiahFormatSimple(total_pendapatan));
        tv_total_expense.setText(format.toRupiahFormatSimple(total_pengeluaran));
        tv_value_transaction.setText(
            String.valueOf(total_transaksi) + " " + getString(R.string.transactions));
        double totalBersih = total_pendapatan - total_pengeluaran;
        tv_value.setText(format.toRupiahFormatSimple(totalBersih));
        Timber.e("total transaksi " + total_transaksi);
        Helper.checkIfZero(emptyView,total_transaksi == 0, v -> startActivity(
            new Intent(getActivity(), AddTransactionActivity.class).putExtra(
                AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_ADD)
                .putExtra(Words.MONTH, month)
                .putExtra(Words.YEAR, tahun)));
      });
    } catch (Exception e) {
      Timber.e("ERROR setNetIncomeLabel " + e);
    }
  }

  @Override public void setChartNetIncome(List<Column> columns, List<AxisValue> axisValues,String name) {
    activityMyFin.runOnUiThread(() -> {
      data = new ColumnChartData(columns);
      Axis axisX = new Axis();
      Axis axisY = axisX.setHasLines(true);
      axisY.setName(name);
      data.setAxisXBottom(null);
      data.setAxisYLeft(axisY);
      columnChart.setColumnChartData(data);
      columnChart.setScrollContainer(true);
      columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
      Viewport v = new Viewport(columnChart.getMaximumViewport());
      v.left = 4.5f;

      columnChart.setCurrentViewport(v);
      ValueTouchListener valueTouchListener = new ValueTouchListener();
      columnChart.setOnValueTouchListener(valueTouchListener);
      columnChart.scrollTo(0,0);
    });
  }

  public class ValueTouchListener implements ColumnChartOnValueSelectListener {

    @Override
    public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
      String[] time = getLabel(value.getLabelAsChars());
      int month = Integer.parseInt(time[1]);
      setMonth(month);
      double amount = value.getValue() * 1000;
      tv_value.setTextColor(presenter.accentColor(amount));
      presenter.loadDataMonth(month, tahun);
      scrollToBottom();
    }

    private void scrollToBottom(){
      scroll.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
      scroll.dispatchNestedPreScroll(0, 200, null, null);
      scroll.dispatchNestedScroll(0, 0, 0, 0, new int[]{0, -200});
      scroll.smoothScrollTo(0, scroll.getBottom());
    }

    @Override public void onValueDeselected() {
      // TODO Auto-generated method stub
    }

    private String[] getLabel(char[] chars) {
      String key = "";
      for (int i = 0; i < chars.length; i++) {
        key += chars[i];
      }
      return Words.FullMonthName.get(key);
    }
  }

  @OnClick(R.id.btn_detail) void seeDetail() {
    gotoTransactionActivityByThisTransactionData();
  }

  private void gotoTransactionActivityByThisTransactionData() {
    //TODO : data yg di tampilkan masih semua, belum sesuai data yg sesuai tanggal di fragment ini
    if (ids != null) {
      if (ids.size() != 0) {
        startActivity(new Intent(getActivity(), TransactionsActivity.class).putExtra(
            TransactionsActivity.FROM, State.FROM_OVERVIEW_NETT_INCOME)
            .putExtra(State.MONTH,month+1)
            .putExtra(State.YEAR,tahun)
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
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
