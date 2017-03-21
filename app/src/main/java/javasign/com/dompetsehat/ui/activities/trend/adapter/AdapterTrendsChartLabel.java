package javasign.com.dompetsehat.ui.activities.trend.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.trend.pojo.TrendModelCategory;
import javasign.com.dompetsehat.ui.activities.trend.pojo.TrendModelOverall;
import javasign.com.dompetsehat.utils.CircleShapeView;

/**
 * Created by bastianbentra on 8/16/16.
 */
public class AdapterTrendsChartLabel extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<?> trendObjects;

  public AdapterTrendsChartLabel(List<?> categories){
    this.trendObjects = trendObjects;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chart_label ,parent , false);
    return new Holder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    if(trendObjects.get(position) instanceof TrendModelOverall) {
      TrendModelOverall t = (TrendModelOverall) trendObjects.get(position);
      h.tv_label.setText(t.title);
      h.circle.setCircleColor(t.lineColor);
    }
    else {
      TrendModelCategory t = (TrendModelCategory) trendObjects.get(position);
      h.tv_label.setText(t.category.getName());
      h.circle.setCircleColor(Color.parseColor(t.category.getBgColor()));
    }
  }

  @Override public int getItemCount() {
    return trendObjects.size();
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
