package javasign.com.dompetsehat.ui.activities.budget.adapter;

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
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;

/**
 * Created by bastianbentra on 8/26/16.
 */
public class AdapterBudgetResultEstimator extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<Budget> models;
  RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
  private OnTouchItemView onTouchItemView;

  public AdapterBudgetResultEstimator(List<Budget> models){
    this.models = models;
  }

  public void setModels(List<Budget> models) {
    this.models = models;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_budget_result, parent, false);
    return new Holder(v);
  }

  public AdapterBudgetResultEstimator setOnTouchItemView(OnTouchItemView onTouchItemView) {
    this.onTouchItemView = onTouchItemView;
    return this;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Budget budget = models.get(position);
    Holder h = (Holder) holder;
    h.tv_label.setText(budget.getCategory().getName());
    h.tv_value.setText(rcf.toRupiahFormatSimple((int)budget.getAmount_budget()));
    h.icr_category.setIconTextColor(Color.WHITE);
    h.icr_category.setBackgroundColorIcon(Color.parseColor(budget.getCategory().getColor()));
    h.icr_category.setIconCode(budget.getCategory().getIcon());
    h.itemView.setOnLongClickListener(view -> {
      onTouchItemView.OnLongClickListener(position,view,budget);
      return false;
    });
  }

  public interface OnTouchItemView{
    void OnLongClickListener(int pos,View v,Budget budget);
  }

  @Override public int getItemCount() {
    return models.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.icr_category) IconCategoryRounded icr_category;
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_value) TextView tv_value;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
