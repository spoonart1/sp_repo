package javasign.com.dompetsehat.ui.fragments.debts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Debt;
import javasign.com.dompetsehat.models.json.debt;
import javasign.com.dompetsehat.ui.activities.reminder.adapter.AdapterReminder;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;

/**
 * Created by avesina on 2/17/17.
 */

public class AdapterDebts extends  RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemEventHelper.ItemEventInterface {
  List<Debt> models;
  List<Debt> backups;
  Context context;
  private ItemEventHelper<Debt> itemEventHelper;

  public AdapterDebts(List<Debt> models) {
    setModels(models);
  }

  public void setModels(List<Debt> models) {
    this.models = models;
    this.backups = new ArrayList<>(this.models);
  }
  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_debts, parent, false);
    return new AdapterDebts.Holder(view);
  }

  public AdapterDebts setItemEventHelper(ItemEventHelper itemEventHelper) {
    this.itemEventHelper = itemEventHelper;
    return this;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    Debt d = models.get(position);
    h.itemView.setTag(d);
    h.tv_name.setText(d.getName());
    h.tv_time.setText(d.getPayback());
    h.tv_amount.setText("-");
    if(d.getCash() != null){
      h.tv_amount.setText(new RupiahCurrencyFormat().toRupiahFormatSimple(d.getCash().getAmount()));
      if(d.getCash().getCategory() != null){
        h.cv_background.setCircleColor(Color.parseColor(d.getCash().getCategory().getColor()));
        h.tv_category_icon.setTextColor(Color.WHITE);
        if(d.getCash().getCategory().getIcon() != null) {
          h.tv_category_icon.setText(d.getCash().getCategory().getIcon());
        }
      }
    }

    if (d.getType().equalsIgnoreCase("BORROW")) {
      h.tv_amount.setTextColor(ContextCompat.getColor(this.context, R.color.red_700));
    } else {
      h.tv_amount.setTextColor(ContextCompat.getColor(this.context, R.color.green_600));
    }

    if(d.getProduct() != null) {
      h.tv_rekening.setVisibility(View.VISIBLE);
      h.tv_rekening.setText(d.getProduct().getName());
    }else{
      h.tv_rekening.setText(context.getString(R.string.account_not_found));
    }

    h.itemView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override public boolean onLongClick(View view) {
        return false;
      }
    });
    itemEventHelper.attach(h.itemView, h.cv_background,0,position);
  }

  @Override public int getItemCount() {
    return models.size();
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return null;
  }

  @Override public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {

  }

  public static class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.cv_background) CircleShapeView cv_background;
    @Bind(R.id.tv_category_icon) IconicsTextView tv_category_icon;
    @Bind(R.id.tv_name) TextView tv_name;
    @Bind(R.id.tv_amount) TextView tv_amount;
    @Bind(R.id.tv_time) TextView tv_time;
    @Bind(R.id.tv_rekening) TextView tv_rekening;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
