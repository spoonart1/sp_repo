package javasign.com.dompetsehat.ui.fragments.overview_v1.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import timber.log.Timber;

/**
 * Created by lafran on 9/22/16.
 */
@Deprecated
public class Old_AdapterOverviewByCategory
    extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

  protected final int DEFAULT_DUMMY_DATA = 4;

  private model Model;
  private int mAccentColor;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private HeadHolder h;
  private Map<Category, Double> dataSet;
  private Context context;
  private PieChartOnValueSelectListener pieChartOnValueSelectListener;
  ArrayList<Category> categories = new ArrayList<>();

  public Old_AdapterOverviewByCategory(model Model, int accentColor) {
    this.Model = Model;
    this.mAccentColor = accentColor;
  }

  public void setDataSet(Map<Category, Double> dataSet) {
    this.dataSet = dataSet;
  }

  public void setPieChartOnValueSelectListener(
      PieChartOnValueSelectListener pieChartOnValueSelectListener) {
    this.pieChartOnValueSelectListener = pieChartOnValueSelectListener;
  }

  public ArrayList<Category> getCategories() {
    return categories;
  }

  public HeadHolder getHolder() {
    return h;
  }

  @Override public int getSectionCount() {
    return 1;
  }

  @Override public int getItemCount(int section) {
    return 1;
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {

  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition,
      int absolutePosition) {
    ChildHolder ch = (ChildHolder) holder;
    if (Model != null) {
      ch.tv_value_transaction.setText(Model.count_transaction+" "+context.getString(R.string.transactions));
      ch.btn_detail.setOnClickListener(v -> {
        int from = -1;
        if (Model.type.equalsIgnoreCase(Cash.CREDIT)) {
          from = State.FROM_OVERVIEW_INCOME_CATEGORY;
        } else {
          from = State.FROM_OVERVIEW_EXPENSE_CATEGORY;
        }

        context.startActivity(
            new Intent(context, TransactionsActivity.class).putExtra(TransactionsActivity.FROM,
                from)
                .putExtra(State.IDS_TRANSACTION,
                    new ArrayList<Integer>(Arrays.asList(Model.ids_transaction))));
      });
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    this.context = parent.getContext();
    View view = null;
    switch (viewType) {
      case VIEW_TYPE_HEADER:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_overview_category_header, parent, false);
        this.h = new HeadHolder(view);
        this.h.drawPie();
        return this.h;
      case VIEW_TYPE_ITEM:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_overview_category_child, parent, false);
        return new ChildHolder(view);
    }
    return null;
  }

  class HeadHolder extends RecyclerView.ViewHolder {

    final int MAX_SPAN_COUNT = 4;

    @Bind(R.id.pie_chart) PieChartView pieChartView;
    @Bind(R.id.indicator) RecyclerView indicator;

    public HeadHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      drawPie();
      int spanCount = MAX_SPAN_COUNT;
      final StaggeredGridLayoutManager gridLayoutManager =
          new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
      indicator.setLayoutManager(gridLayoutManager);
    }

    public void drawPie() {
      Timber.e("avesina data "+dataSet);
      pieChartView.setCircleFillRatio(1.0f);
      pieChartView.setValueSelectionEnabled(true);
      pieChartView.setOnValueTouchListener(pieChartOnValueSelectListener);
      boolean hasLabels = false;
      boolean hasLabelsOutside = false;
      boolean hasCenterCircle = true;
      boolean hasCenterText1 = false;
      boolean hasCenterText2 = false;
      boolean isExploded = false;
      boolean hasLabelForSelected = false;

      List<SliceValue> values = new ArrayList<SliceValue>();
      if (dataSet != null) {
        categories.clear();
        for (Map.Entry<Category, Double> map : dataSet.entrySet()) {
          Timber.e("value " + map.getValue());
          SliceValue sliceValue = new SliceValue(map.getValue().floatValue(),
              Color.parseColor(map.getKey().getColor()));
          values.add(sliceValue);
          categories.add(map.getKey());
        }
        drawIndicator(categories);
      }

      PieChartData data = new PieChartData(values);
      data.setHasLabels(hasLabels);
      data.setHasLabelsOnlyForSelected(hasLabelForSelected);
      data.setHasLabelsOutside(hasLabelsOutside);
      data.setHasCenterCircle(hasCenterCircle);
      data.setCenterCircleScale(0.4f);
      data.setSlicesSpacing(0);

      if (isExploded) {
        data.setSlicesSpacing(4);
      }

      pieChartView.setPieChartData(data);
    }

    private void drawIndicator(ArrayList<Category> categories) {
      AdapterChartLabel adapterChartLabel = new AdapterChartLabel(categories);
      indicator.setAdapter(adapterChartLabel);
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

  public void setIds(Integer[] ids_transaction){
    if(Model != null){
      Model.ids_transaction = ids_transaction;
      Model.count_transaction = ids_transaction.length;
    }
  }

  public static class model {

    public static model newInstance() {
      return new model();
    }

    public String type;
    public int count_transaction = 0;
    public Integer[] ids_transaction = null;
    public double total = 0;
  }

  protected class AdapterChartLabel extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Category> categories;

    public AdapterChartLabel(ArrayList<Category> categories) {
      this.categories = categories;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.adapter_chart_label, parent, false);
      return new Holder(view);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      Holder h = (Holder) holder;
      Category category = categories.get(position);
      h.tv_label.setText(category.getName());
      h.circle.setCircleColor(Color.parseColor(category.getColor()));
    }

    @Override public int getItemCount() {
      return dataSet.size();
    }

    public Object getElementByIndex(Map<Category, Double> map, int index) {
      return map.get(map.keySet().toArray()[index]);
    }

    class Holder extends RecyclerView.ViewHolder {

      @Bind(R.id.tv_label) TextView tv_label;
      @Bind(R.id.indicator) CircleShapeView circle;

      public Holder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }
    }
  }
}
