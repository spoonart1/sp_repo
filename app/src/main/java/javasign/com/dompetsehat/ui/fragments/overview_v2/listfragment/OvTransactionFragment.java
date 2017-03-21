package javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.presenter.overview.OverviewTransactionPresenter;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.OverviewFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvCategoryModel;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvFragmentModel;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by lafran on 1/25/17.
 */

public class OvTransactionFragment extends BaseFragment {

  public final static int COLOR_EXPENSE = Color.parseColor("#f44336");
  public final static int COLOR_INCOME = Helper.GREEN_DOMPET_COLOR;

  @Bind(R.id.tv_value) TextView tv_value;
  @Bind(R.id.tv_time) TextView tv_time;
  @Bind(R.id.lineChart) LineChartView chart;
  @Bind(R.id.tv_value_transaction) TextView tv_value_transaction;
  @Bind(R.id.btn_detail) View btn_detail;
  @Bind(R.id.radio_group) RadioGroup radio_group;

  @Inject OverviewTransactionPresenter presenter;

  private String typeTransaction = null;
  private OvFragmentModel ovFragmentModel;
  private int accentColor;
  private String tabName = "";
  private int tabType = 0;
  private Integer tabYear;
  private Integer tabMonth;
  private NewMainActivityMyFin activity;
  private RupiahCurrencyFormat format = new RupiahCurrencyFormat();
  private OvCategoryModel model;
  private int originalState;

  public static OvTransactionFragment newInstance(String tabName, int tabType) {
    return newInstance(tabName, tabType, null, null);
  }

  public static OvTransactionFragment newInstance(String tabName, int tabType, Integer tabYear,
      Integer tabMonth) {
    OvTransactionFragment fragment = new OvTransactionFragment();
    fragment.tabName = tabName;
    fragment.tabType = tabType;
    fragment.tabYear = tabYear;
    fragment.tabMonth = tabMonth;
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_overview_transaction, null);
    ButterKnife.bind(this, view);

    this.activity = (NewMainActivityMyFin) getActivity();
    activity.getActivityComponent().inject(this);
    presenter.attachView(this);

    init();
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    radio_group.check(R.id.rb_expense);
  }

  private void init() {
    radio_group.setOnCheckedChangeListener((group, checkedId) -> {
      typeTransaction = checkedId == R.id.rb_income ? Cash.CREDIT : Cash.DEBIT;
      switchData(typeTransaction);
    });
  }

  private void switchData(String typeTransaction) {
    switch (typeTransaction) {
      case Cash.CREDIT:
        accentColor = COLOR_INCOME;
        originalState = State.FROM_OVERVIEW_INCOME_TRANSACTION;
        Helper.trackThis(getActivity(), "user klik transaksi berdasarkan pemasukan");
        new OvTrxIncome();
        break;
      case Cash.DEBIT:
        accentColor = COLOR_EXPENSE;
        originalState = State.FROM_OVERVIEW_EXPENSE_TRANSACTION;
        Helper.trackThis(getActivity(), "user klik transaksi berdasarkan pengeluaran");
        new OvTrxExpense();
        break;
    }
    tv_value.setTextColor(accentColor);
  }

  @Override public void setListTransaction(OvCategoryModel model) {
    this.model = model;
    tv_value_transaction.setText(model.count_transaction + " " + getString(R.string.transactions));
  }

  @OnClick(R.id.btn_detail) void seeDetail() {
    if (model == null) return;

    startActivity(
        new Intent(getActivity(), TransactionsActivity.class).putExtra(TransactionsActivity.FROM,
            originalState)
            .putExtra(State.IDS_TRANSACTION,
                new ArrayList<>(Arrays.asList(model.ids_transaction))));
  }

  @Override
  public void setChartLabelValues(String[] labels, float[] values, int min, int max, String label) {
    activity.runOnUiThread(() -> {
      setChart(labels, values, min, max, label);
    });
  }

  @Override public void setTotal(double total) {
    activity.runOnUiThread(() -> tv_value.setText(format.toRupiahFormatSimple(total)));
  }

  private void setChart(String[] labels, float[] values, int min, int max, String label) {
    activity.runOnUiThread(new Runnable() {
      @Override public void run() {
        chart.reset();
        Paint gridPaint = new Paint();
        gridPaint.setColor(ContextCompat.getColor(getContext(), R.color.grey_300));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1.5f));
        chart.setGrid(ChartView.GridType.HORIZONTAL, gridPaint);
        chart.setAxisColor(ContextCompat.getColor(getContext(), R.color.grey_300));
        chart.setLabelsFormat(new DecimalFormat('#' + label));
        chart.setAxisLabelsSpacing(Tools.fromDpToPx(2));
        LineSet dataset = new LineSet(labels, values);
        dataset.setThickness(Tools.fromDpToPx(3.0f));
        dataset.setDotsRadius(Tools.fromDpToPx(4.0f));
        dataset.setColor(accentColor);
        dataset.setDotsColor(accentColor);
        dataset.setGradientFill(new int[] { accentColor, Color.WHITE }, new float[] { 0, 2 });

        float[] temp_values = values.clone();
        Timber.e("avesina " + temp_values.length);
        if (temp_values.length > 0) {
          Arrays.sort(temp_values);
          int step = (int) Math.ceil((double) max / 5);
          if (step <= 0) {
            step = 1;
            chart.setStep(step);
          } else {
            chart.setStep(step);
            chart.setAxisBorderValues(min, max);
          }
        }

        chart.addData(dataset);
        chart.invalidate();
        chart.show();
        chart.setFontSize(20);
      }
    });
  }

  class OvTrxExpense {
    OvTrxExpense() {
      tv_time.setText(getString(R.string.expense) + " " + tabName.toLowerCase());
      if (tabType == OverviewFragment.FILTER_SINGLE_MONTHLY
          && tabMonth != null
          && tabYear != null) {
        presenter.populateOverviewTransactionByYearAndMonth(Cash.DEBIT, tabYear, tabMonth);
      } else if (tabType == OverviewFragment.FILTER_SINGLE_YEARLY && tabYear != null) {
        presenter.populateOverviewTransactionByYear(Cash.DEBIT, tabYear);
      } else {
        presenter.populateOverviewTransaction(Cash.DEBIT, tabName);
      }
    }
  }

  class OvTrxIncome {
    OvTrxIncome() {
      tv_time.setText(getString(R.string.income) + " " + tabName.toLowerCase());
      if (tabType == OverviewFragment.FILTER_SINGLE_MONTHLY
          && tabMonth != null
          && tabYear != null) {
        presenter.populateOverviewTransactionByYearAndMonth(Cash.CREDIT, tabYear, tabMonth);
      } else if (tabType == OverviewFragment.FILTER_SINGLE_YEARLY && tabYear != null) {
        presenter.populateOverviewTransactionByYear(Cash.CREDIT, tabYear);
      } else {
        presenter.populateOverviewTransaction(Cash.CREDIT, tabName);
      }
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }
}
