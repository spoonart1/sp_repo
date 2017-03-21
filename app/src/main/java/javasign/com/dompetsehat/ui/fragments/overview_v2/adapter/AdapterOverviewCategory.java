package javasign.com.dompetsehat.ui.fragments.overview_v2.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;

/**
 * Created by lafran on 1/26/17.
 */

public class AdapterOverviewCategory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private ArrayList<Category> categories;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private OnClikItem onClikItemListener;

  public AdapterOverviewCategory(ArrayList<Category> categories) {
    this.categories = categories;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_chart_label, parent, false);
    return new Holder(view);
  }

  public AdapterOverviewCategory setOnClikItem(OnClikItem onClikItemListener) {
    this.onClikItemListener = onClikItemListener;
    return this;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    Category category = categories.get(position);
    if(category.getIcon() != null) {
      Transaction transaction = category.getTransaction();
      h.tv_label.setText(category.getName());
      if (transaction != null) {
        h.tv_value.setText(format.toRupiahFormatSimple(transaction.amount));
      }
      h.icr_category.setIconCode(category.getIcon());
      h.icr_category.setBackgroundColorIcon(Color.parseColor(category.getColor()));
      h.itemView.setOnClickListener(view -> {
        if (onClikItemListener != null) {
          onClikItemListener.OnClik(view, position, category, null);
        }
      });
    }
  }

  @Override public int getItemCount() {
    return categories.size();
  }

  public interface OnClikItem{
    void OnClik(View v,int position,Category category,Integer[] ids);
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_value) TextView tv_value;
    @Bind(R.id.icr_category) IconCategoryRounded icr_category;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
