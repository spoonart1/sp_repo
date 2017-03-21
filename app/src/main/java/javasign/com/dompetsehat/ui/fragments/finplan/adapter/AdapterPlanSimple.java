package javasign.com.dompetsehat.ui.fragments.finplan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.view.AccountView;
import javasign.com.dompetsehat.view.CustomProgress;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/25/16.
 */
public class AdapterPlanSimple extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemEventHelper.ItemEventInterface {

  private List<Plan> planList;
  private Context context;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private int[] colorAccents;
  private ItemEventHelper itemEventHelper;

  public AdapterPlanSimple(List<Plan> planList) {
    this.planList = planList;
  }

  public void removeItem(Plan plan) {
    int position = planList.indexOf(plan);
    if (position >= 0) {
      planList.remove(position);
      notifyItemRemoved(position);
    }
  }

  public AdapterPlanSimple setItemEventHelper(ItemEventHelper itemEventHelper) {
    this.itemEventHelper = itemEventHelper;
    return this;
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return itemEventHelper;
  }

  @Override public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {
    getItemEventHelper().setAdapterOnClickItem(onClickItem);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    View view = LayoutInflater.from(context).inflate(R.layout.adapter_plan_simple, parent, false);
    this.colorAccents = new int[] {
        ContextCompat.getColor(context, R.color.dana_pensiun_color),
        ContextCompat.getColor(context, R.color.dana_darurat_color),
        ContextCompat.getColor(context, R.color.dana_kuliah_color),
        ContextCompat.getColor(context, R.color.dana_kustom_color)
    };
    return new ChildHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ChildHolder h = (ChildHolder) holder;
    Plan plan = planList.get(position);

    h.itemView.setTag(plan);
    h.v_divider.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);

    h.tv_title.setText(plan.getPlan_title());

    double from = plan.total_saldo;
    if (from < 0) {
      from = 0;
    }
    double total = plan.getPlan_total();
    double persen = 100;
    if (total > 0 && total >= from) {
      persen = 100 - ((total - from) / total * 100);
    }
    double sisa = 0;
    if (total >= from) {
      sisa = total - from;
    }

    h.tv_amount.setText(format.toRupiahFormatSimple(sisa));
    h.tv_persen.setText(String.format("%.0f%%", persen));

    h.cp_bar.setMaxValue(total);
    h.cp_bar.setProgressValue(from);
    int healthyColor = ContextCompat.getColor(context, R.color.green_health);
    int warningColor = ContextCompat.getColor(context, R.color.yellow_warning);
    int redAlerColor = ContextCompat.getColor(context, R.color.red_alert);
    h.cp_bar.setColors(redAlerColor, warningColor, healthyColor);
    Timber.e("Plan type " + plan.getType());
    switch (plan.getType()) {
      case DbHelper.PLAN_TYPE_PENSIUN:
        h.tv_title.setTextColor(colorAccents[0]);
        h.icr_category.setBackgroundColorIcon(colorAccents[0]);
        h.icr_category.setIconCode(DSFont.Icon.dsf_danapensiun.getFormattedName());
        break;
      case DbHelper.PLAN_TYPE_CUSTOME:
        h.tv_title.setTextColor(colorAccents[3]);
        h.icr_category.setBackgroundColorIcon(colorAccents[3]);
        h.icr_category.setIconCode(DSFont.Icon.dsf_danacustome.getFormattedName());
        break;
      case DbHelper.PLAN_TYPE_DARURAT:
        h.tv_title.setTextColor(colorAccents[1]);
        h.icr_category.setBackgroundColorIcon(colorAccents[1]);
        h.icr_category.setIconCode(DSFont.Icon.dsf_danadarurat.getFormattedName());
        break;
      case DbHelper.PLAN_TYPE_KULIAH:
        h.tv_title.setTextColor(colorAccents[2]);
        h.icr_category.setBackgroundColorIcon(colorAccents[2]);
        h.icr_category.setIconCode(DSFont.Icon.dsf_education.getFormattedName());
    }

    if (plan.account != null) {
      if (plan.isConnected()) {
        h.tv_status.setText(plan.account.getName());
        @ColorInt int statusColor = ContextCompat.getColor(context, R.color.grey_200);
        Integer logoVendorRes = -1;
        try {
          statusColor = AccountView.accountColor.get(plan.account.getIdvendor());
          logoVendorRes = AccountView.logoVendorRes.get(plan.account.getIdvendor());
        } catch (Exception e) {

        }
        h.frame_status.setBackgroundColor(statusColor);
        h.icon.setText(AccountView.iconVendor.get(plan.account.getIdvendor()));
        h.icon.setVisibility(View.VISIBLE);
        h.tv_status.setVisibility(View.VISIBLE);
        h.iv_logo.setVisibility(View.GONE);

        if (logoVendorRes != -1) {
          h.iv_logo.setVisibility(View.VISIBLE);
          h.iv_logo.setImageResource(logoVendorRes);
          h.frame_status.setBackgroundColor(Color.TRANSPARENT);
          h.icon.setVisibility(View.GONE);
          h.tv_status.setVisibility(View.GONE);
        }
      } else {
        h.tv_status.setText(context.getString(R.string.not_connected));
        h.icon.setVisibility(View.GONE);
        h.tv_status.setVisibility(View.VISIBLE);
      }
    } else {
      h.icon.setVisibility(View.GONE);
    }

    getItemEventHelper().attach(h.itemView, h.icr_category,0,position);
  }

  private String getPercentageString(double percent) {
    String percentage = "";
    if (percent >= 100) {
      percentage = context.getString(R.string.completed);
    } else {
      percentage = context.getString(R.string.progress);
    }

    return percentage;
  }

  @Override public int getItemCount() {
    return planList.size();
  }

  class ChildHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_title) TextView tv_title;
    @Bind(R.id.tv_amount) TextView tv_amount;
    @Bind(R.id.tv_status) TextView tv_status;
    @Bind(R.id.frame_status) View frame_status;
    @Bind(R.id.cp_bar) CustomProgress cp_bar;
    @Bind(R.id.icon) IconicsTextView icon;
    @Bind(R.id.iv_logo) ImageView iv_logo;
    @Bind(R.id.v_divider) View v_divider;
    @Bind(R.id.icr_category) IconCategoryRounded icr_category;
    @Bind(R.id.tv_persen) TextView tv_persen;

    public ChildHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}