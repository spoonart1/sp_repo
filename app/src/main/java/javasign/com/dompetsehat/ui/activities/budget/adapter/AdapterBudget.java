package javasign.com.dompetsehat.ui.activities.budget.adapter;

import android.content.Context;
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
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.view.CustomProgress;

/**
 * Created by bastianbentra on 8/15/16.
 */
public class AdapterBudget extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemEventHelper.ItemEventInterface{

  private List<Budget> budgets;
  private Context context;
  private ItemEventHelper itemEventHelper;

  private RupiahCurrencyFormat format = new RupiahCurrencyFormat();

  public AdapterBudget() {

  }

  public AdapterBudget setItemEventHelper(ItemEventHelper itemEventHelper){
    this.itemEventHelper = itemEventHelper;
    return this;
  }

  public void removeItem(Budget budget){
    int position = budgets.indexOf(budget);
    if(position >=0) {
      budgets.remove(position);
      notifyItemRemoved(position);
    }
  }

  public void setRealData(List<Budget> budgets) {
    this.budgets = budgets;
  }


  public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {
    this.getItemEventHelper().setAdapterOnClickItem(onClickItem);
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return itemEventHelper;
  }


  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_budget, parent, false);
    return new BH(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    BH bh = (BH) holder;
    Budget budget = this.budgets.get(position);
    bh.itemView.setTag(budget);
    bh.icr_category.setIconCode(budget.getCategory().getIcon());
    bh.icr_category.setIconTextColor(Color.WHITE);
    bh.icr_category.setBackgroundColorIcon(Color.parseColor(budget.getCategory().getColor()));
    bh.tv_title.setText(budget.getCategory().getName().toString());
    if (budget.getCategory_cash_amount() <= 0) {
      bh.tv_note.setText(context.getString(R.string.amount_target));
      bh.tv_amount.setText(format.toRupiahFormatSimple(budget.getAmount_budget()));
    } else {
      bh.tv_note.setText(context.getString(R.string.amount_left));
      bh.tv_amount.setText(format.toRupiahFormatSimple(
          budget.getAmount_budget() - budget.getCategory_cash_amount()));
    }
    bh.cp_bar.setMaxValue(budget.getAmount_budget());
    bh.cp_bar.setProgressValue(budget.getCategory_cash_amount());
    bh.cp_bar.invalidateDraw();
    getItemEventHelper().attach(bh.itemView, bh.icr_category,0,position);
  }

  @Override public int getItemCount() {
    return budgets != null ? budgets.size() : 0;
  }

  public class BH extends RecyclerView.ViewHolder {

    @Bind(R.id.icr_category) IconCategoryRounded icr_category;
    @Bind(R.id.tv_title) TextView tv_title;
    @Bind(R.id.tv_note) TextView tv_note;
    @Bind(R.id.tv_amount) TextView tv_amount;
    @Bind(R.id.cp_bar) CustomProgress cp_bar;
    public View itemView;

    public BH(View itemView) {
      super(itemView);
      this.itemView = itemView;
      ButterKnife.bind(this, itemView);
    }
  }
}
