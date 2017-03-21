package javasign.com.dompetsehat.ui.activities.trend.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.trend.pojo.TrendModelCategory;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;

/**
 * Created by bastianbentra on 8/16/16.
 */
public class AdapterTrendListCategory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<TrendModelCategory> trendModelCategories;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();

  public AdapterTrendListCategory(List<TrendModelCategory> trendModelCategories){
    this.trendModelCategories = trendModelCategories;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_trend_list_category, parent, false);
    return new HolderListCategory(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    HolderListCategory h = (HolderListCategory) holder;
    TrendModelCategory tmc = trendModelCategories.get(position);

    h.icr_category.setIconCode(tmc.category.getIcon());
    h.icr_category.setBackgroundColorIcon(Color.parseColor(tmc.category.getBgColor()));
    h.tv_label.setText(tmc.category.getName());
    h.tv_value.setText(format.toRupiahFormatSimple(tmc.amountTransaction));
  }

  @Override public int getItemCount() {
    return trendModelCategories.size();
  }

  class HolderListCategory extends RecyclerView.ViewHolder {

    @Bind(R.id.icr_category) IconCategoryRounded icr_category;
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_value) TextView tv_value;

    public HolderListCategory(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
