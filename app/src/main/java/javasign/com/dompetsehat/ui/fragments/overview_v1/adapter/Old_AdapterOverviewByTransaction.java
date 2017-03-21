package javasign.com.dompetsehat.ui.fragments.overview_v1.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvCategoryModel;
import javasign.com.dompetsehat.utils.State;

/**
 * Created by lafran on 9/19/16.
 */
@Deprecated
public class Old_AdapterOverviewByTransaction
    extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

  private OvCategoryModel transactionModels;
  private HashMap<String, Double> dataChart;
  private Resources res;
  private int accentColor;
  private String[] labels = new String[] { "0a", "1a", "2a", "3a", "4a", "5a", "6a", "7a" };
  private float[] values = new float[] { 0, 11, 21, 13, 22, 44, 26, 8 };
  private HeadHolder h;
  private Context context;

  public Old_AdapterOverviewByTransaction(OvCategoryModel transactionModels,
      int accentColor) {
    this.transactionModels = transactionModels;
    this.accentColor = accentColor;
  }

  public void setList(OvCategoryModel transactionModels) {
    this.transactionModels = transactionModels;
  }

  public void setChart(HashMap<String, Double> dataChart) {
    this.dataChart = dataChart;
  }

  public void setLabelAndValues(String[] labels, float[] values) {
    this.labels = labels;
    this.values = values;
    if (h != null) {
      h.setChart();
    }
  }

  @Override public int getSectionCount() {
    return 1;
  }

  @Override public int getItemCount(int section) {
    if (transactionModels != null) {
      return 1; //transactionModels.size();
    } else {
      return 0;
    }
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
    this.h = (HeadHolder) holder;
    h.setChart();
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition,
      int absolutePosition) {
    //if(Helper.isEmptyObject(transactionModels) || transactionModels.isEmpty()) {
    //  System.out.println("Old_AdapterOverviewByTransaction.onBindViewHolder");
    //  return;
    //}
    ChildHolder ch = (ChildHolder) holder;
    ch.tv_value_transaction.setText(transactionModels.count_transaction + " "+context.getString(R.string.transactions));
    ch.btn_detail.setOnClickListener(v -> {
      int from = -1;
      if (transactionModels.type.equalsIgnoreCase(Cash.CREDIT)) {
        from = State.FROM_OVERVIEW_INCOME_TRANSACTION;
      } else {
        from = State.FROM_OVERVIEW_EXPENSE_TRANSACTION;
      }
      context.startActivity(
          new Intent(context, TransactionsActivity.class).putExtra(TransactionsActivity.FROM, from)
              .putExtra(State.IDS_TRANSACTION,
                  new ArrayList<>(Arrays.asList(transactionModels.ids_transaction))));
    });
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    res = parent.getContext().getResources();
    this.context = parent.getContext();
    View view = null;
    switch (viewType) {
      case VIEW_TYPE_HEADER:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_overview_transaction_header, parent, false);
        return new HeadHolder(view);

      case VIEW_TYPE_ITEM:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_overview_transaction_child, parent, false);
        return new ChildHolder(view);
    }
    return null;
  }

  class HeadHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.lineChart) LineChartView chart;

    public HeadHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    private void setChart() {

      chart.reset();

      Paint gridPaint = new Paint();
      gridPaint.setColor(res.getColor(R.color.grey_300));
      gridPaint.setStyle(Paint.Style.STROKE);
      gridPaint.setAntiAlias(true);
      gridPaint.setStrokeWidth(Tools.fromDpToPx(1.5f));
      chart.setGrid(ChartView.GridType.HORIZONTAL, gridPaint);
      chart.setAxisColor(res.getColor(R.color.grey_300));
      chart.setLabelsFormat(new DecimalFormat('#' + "rb"));
      chart.setAxisLabelsSpacing(Tools.fromDpToPx(2));

      LineSet dataset = new LineSet(labels, values);

      dataset.setThickness(Tools.fromDpToPx(3.0f));
      dataset.setDotsRadius(Tools.fromDpToPx(4.0f));
      dataset.setColor(accentColor);
      dataset.setDotsColor(accentColor);
      dataset.setGradientFill(new int[] { accentColor, Color.WHITE }, new float[] { 0, 2 });

      float[] temp_values = values.clone();
      Arrays.sort(temp_values);
      int step = (int) Math.ceil(temp_values[temp_values.length - 1] / 5);
      if (step <= 0) {
        chart.setStep(1);
      } else {
        chart.setStep(step);
      }
      chart.addData(dataset);
      chart.show();
    }
  }

  class ChildHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_value_transaction) TextView tv_value_transaction;
    @Bind(R.id.btn_detail) View btn_detail;

    public ChildHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
