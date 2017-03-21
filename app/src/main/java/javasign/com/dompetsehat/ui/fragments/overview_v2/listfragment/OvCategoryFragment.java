package javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment;

import android.content.Intent;
import android.graphics.Color;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.presenter.overview.OverviewCategoryPresenter;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.OverviewFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.adapter.AdapterOverviewCategory;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvCategoryModel;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import timber.log.Timber;

/**
 * Created by lafran on 1/26/17.
 */

public class OvCategoryFragment extends BaseFragment {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.tv_value) TextView tv_value;
  @Bind(R.id.tv_value_transaction) TextView tv_value_transaction;
  @Bind(R.id.tv_title) TextView tv_title;
  @Bind(R.id.btn_detail) View btn_detail;
  @Bind(R.id.radio_group) RadioGroup radio_group;
  @Bind(R.id.pie_chart) PieChartView pieChartView;

  @Inject OverviewCategoryPresenter presenter;

  private String typeTransaction = null;
  private String tabName;
  private int tabType = 0;
  private Integer tabYear;
  private Integer tabMonth;
  private String savedCenterTitle = "";
  private double savedTotalAmount = 0.0;
  private OvCategoryModel model;
  private NewMainActivityMyFin activity;
  private int accentColor;
  private int originalState;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();

  public static OvCategoryFragment newInstance(String tabName, int tabType) {
    return newInstance(tabName, tabType, null, null);
  }

  public static OvCategoryFragment newInstance(String tabName, int tabType, Integer tabYear,
      Integer tabMonth) {
    OvCategoryFragment fragment = new OvCategoryFragment();
    fragment.tabName = tabName;
    fragment.tabType = tabType;
    fragment.tabYear = tabYear;
    fragment.tabMonth = tabMonth;
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_overview_category, null);
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

  private void switchData(String typeTransaction) {
    switch (typeTransaction) {
      case Cash.CREDIT:
        accentColor = OvTransactionFragment.COLOR_INCOME;
        originalState = State.FROM_OVERVIEW_INCOME_TRANSACTION;
        Helper.trackThis(getActivity(), "user klik kategori berdasarkan pemasukan");
        new OvCatIncome();
        break;
      case Cash.DEBIT:
        accentColor = OvTransactionFragment.COLOR_EXPENSE;
        originalState = State.FROM_OVERVIEW_EXPENSE_TRANSACTION;
        Helper.trackThis(getActivity(), "user klik kategori berdasarkan pengeluaran");
        new OvCatExpense();
        break;
    }
    tv_value.setTextColor(accentColor);
  }

  @OnClick(R.id.btn_detail) void seeDetail() {
    Integer[] ids = (Integer[]) tv_value_transaction.getTag();
    startActivity(
        new Intent(getActivity(), TransactionsActivity.class).putExtra(TransactionsActivity.FROM,
            originalState).putExtra(State.IDS_TRANSACTION, new ArrayList<>(Arrays.asList(ids))));
  }

  private void init() {
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    radio_group.setOnCheckedChangeListener((group, checkedId) -> {
      typeTransaction = checkedId == R.id.rb_income ? Cash.CREDIT : Cash.DEBIT;
      switchData(typeTransaction);
      resetView();
    });
  }

  private void setButtonDetailVisible(boolean visible) {
    btn_detail.setVisibility(visible ? View.VISIBLE : View.GONE);
    recyclerView.setVisibility(visible ? View.GONE : View.VISIBLE);
  }

  private void drawPie(Map<Category, Double> dataSet) {

    pieChartView.setCircleFillRatio(1.0f);
    pieChartView.setValueSelectionEnabled(true);

    boolean hasLabels = false;
    boolean hasLabelsOutside = false;
    boolean hasCenterCircle = true;
    boolean hasCenterText1 = false;
    boolean hasCenterText2 = false;
    boolean isExploded = false;
    boolean hasLabelForSelected = false;

    ArrayList<Category> categories = new ArrayList<Category>();
    List<SliceValue> values = new ArrayList<SliceValue>();
    if (dataSet != null) {
      categories.clear();
      for (Map.Entry<Category, Double> map : dataSet.entrySet()) {
        Timber.e("value " + map.getValue());
        if (map.getKey().getIcon() != null) {
          SliceValue sliceValue = new SliceValue(map.getValue().floatValue(),
              Color.parseColor(map.getKey().getColor()));
          values.add(sliceValue);
          Transaction t = new Transaction();
          t.amount = map.getValue();
          map.getKey().setTransaction(t);
          categories.add(map.getKey());
        } else {
          SliceValue sliceValue = new SliceValue(map.getValue().floatValue(),
              ContextCompat.getColor(getContext(), R.color.grey_400));
          values.add(sliceValue);
        }
      }
      drawList(categories, values);
    }

    PieChartData data = new PieChartData(values);
    data.setHasLabels(hasLabels);
    data.setHasLabelsOnlyForSelected(hasLabelForSelected);
    data.setHasLabelsOutside(hasLabelsOutside);
    data.setHasCenterCircle(hasCenterCircle);
    data.setCenterCircleScale(0.7f);
    data.setSlicesSpacing(0);

    if (isExploded) {
      data.setSlicesSpacing(4);
    }

    pieChartView.setPieChartData(data);

    pieChartView.setOnValueTouchListener(new PieChartOnValueSelectListener() {
      @Override public void onValueSelected(int arcIndex, SliceValue value) {
        if (categories.size() - 1 >= arcIndex) {
          Category category = categories.get(arcIndex);
          setButtonDetailVisible(true);
          Integer[] ids = presenter.getIdsByCategory(category, typeTransaction, tabName);
          int textColor = Color.parseColor(category.getColor());
          setCenterText(category.getName(), value.getValue(), textColor, textColor, ids.length,
              ids);
        }
      }

      @Override public void onValueDeselected() {
        resetView();
      }
    });
  }

  @Override public void setTotal(double total) {
    if (savedTotalAmount <= 0.0) {
      savedTotalAmount = total;
    }
    activity.runOnUiThread(() -> tv_value.setText(format.toRupiahFormatSimple(total)));
  }

  private void resetView() {
    setButtonDetailVisible(false);
    setCenterText(savedCenterTitle, savedTotalAmount,
        ContextCompat.getColor(getActivity(), R.color.grey_700), accentColor, 0, null);
  }

  private void setCenterText(String title, double value, int titleTextColor, int valueTextColor,
      int countTransaction, Integer[] ids) {
    setTotal(value);
    activity.runOnUiThread(() -> {
      tv_title.setText(title);
      tv_title.setTextColor(titleTextColor);
      tv_value.setTextColor(valueTextColor);
      tv_value_transaction.setTag(ids);
      tv_value_transaction.setText(
          String.valueOf(countTransaction) + " " + getString(R.string.transactions));
    });
  }

  private void drawList(ArrayList<Category> categories, List<SliceValue> values) {
    AdapterOverviewCategory adapter = new AdapterOverviewCategory(categories);
    recyclerView.setAdapter(adapter.setOnClikItem((v, position, category, ids) -> {
      Integer[] ids_cash = null;
      if (tabType == OverviewFragment.FILTER_SINGLE_MONTHLY
          && tabMonth != null
          && tabYear != null) {
        ids_cash = presenter.getIdsByCategory(category, typeTransaction, tabYear, tabMonth);
      } else {
        ids_cash = presenter.getIdsByCategory(category, typeTransaction, tabName);
      }
      if (ids_cash != null) {
        startActivity(new Intent(getActivity(), TransactionsActivity.class).putExtra(
            TransactionsActivity.FROM, originalState)
            .putExtra(State.IDS_TRANSACTION, new ArrayList<>(Arrays.asList(ids_cash))));
      }
      //if (pieChartView.getOnValueTouchListener() != null) {
      //  SelectedValue selectedValue = pieChartView.getChartRenderer().getSelectedValue();
      //  selectedValue.set(position, position, SelectedValue.SelectedValueType.NONE);
      //  pieChartView.selectValue(selectedValue);
      //}
    }));
  }

  @Override public void setChartCategory(Map<Category, Double> map) {
    activity.runOnUiThread(() -> {
      if (map.size() == 0) {
        map.put(new Category(), 9999.0);
        drawPie(map);
      } else {
        drawPie(map);
      }
    });
  }

  class OvCatExpense {
    OvCatExpense() {
      if (tabName != null) {
        tv_title.setText(tabName);
        savedCenterTitle = tv_title.getText().toString();
        if (tabType == OverviewFragment.FILTER_SINGLE_MONTHLY
            && tabMonth != null
            && tabYear != null) {
          presenter.populateOverviewCategory(Cash.DEBIT, tabYear, tabMonth);
        } else if (tabType == OverviewFragment.FILTER_SINGLE_YEARLY && tabYear != null) {
          presenter.populateOverviewCategory(Cash.DEBIT, tabYear);
        } else {
          presenter.populateOverviewCategory(Cash.DEBIT, tabName);
        }
      }
    }
  }

  class OvCatIncome {
    OvCatIncome() {
      if (tabName != null) {
        tv_title.setText(tabName);
        savedCenterTitle = tv_title.getText().toString();
        if (tabType == OverviewFragment.FILTER_SINGLE_MONTHLY
            && tabMonth != null
            && tabYear != null) {
          presenter.populateOverviewCategory(Cash.CREDIT, tabYear, tabMonth);
        } else if (tabType == OverviewFragment.FILTER_SINGLE_YEARLY && tabYear != null) {
          presenter.populateOverviewCategory(Cash.CREDIT, tabYear);
        } else {
          presenter.populateOverviewCategory(Cash.CREDIT, tabName);
        }
      }
    }
  }

  @Override public void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
