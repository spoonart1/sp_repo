package javasign.com.dompetsehat.ui.activities.plan.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.ThirdParty;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.view.AccountView;

/**
 * Created by bastianbentra on 8/26/16.
 */
public class AdapterFinishingPlan extends
    ExpandableRecyclerAdapter<AdapterFinishingPlan.HeadHolder, AdapterFinishingPlan.ChildHolder> {

  private LayoutInflater inflater;
  private AppCompatActivity activity;
  private OnItemClicked onItemClicked;

  public AdapterFinishingPlan(AppCompatActivity activity,
      @NonNull List<? extends ParentListItem> parentItemList) {
    super(parentItemList);
    this.activity = activity;
    this.inflater = LayoutInflater.from(activity);
  }

  public void setOnItemClicked(OnItemClicked onItemClicked){
    this.onItemClicked = onItemClicked;
  }

  @Override public HeadHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
    View view = inflater.inflate(R.layout.adapter_finishing_plan_header, parentViewGroup, false);
    return new HeadHolder(view);
  }

  @Override public ChildHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
    View view = inflater.inflate(R.layout.adapter_finishing_plan_child, childViewGroup, false);
    return new ChildHolder(view);
  }

  @Override public void onBindParentViewHolder(HeadHolder parentViewHolder, int position,
      ParentListItem parentListItem) {
    HeadHolder hh = parentViewHolder;
    ParentModel model = (ParentModel) parentListItem;

    hh.tv_title.setText(model.title);
    hh.itemView.setTag(model);

  }

  @Override public void onBindChildViewHolder(ChildHolder childViewHolder, int position,
      Object childListItem) {
    ChildHolder ch = childViewHolder;

    if(childListItem instanceof Account) {
      Account a = (Account) childListItem;
      ch.itemView.setTag(a);

      String iconCode = AccountView.iconVendor.get(a.getIdvendor());
      int accentColor = AccountView.accountColor.get(a.getIdvendor());

      ch.icon.setBackgroundColorIcon(accentColor);
      ch.icon.setIconCode(iconCode);

      ch.tv_label.setText(a.getName());
      ch.tv_right.setText(RupiahCurrencyFormat.getInstance().toRupiahFormatSimple(a.saldo));

      ch.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (onItemClicked == null) return;
          Account ac = (Account) v.getTag();
          onItemClicked.itemClicked(v, ac);
        }
      });
    }else if(childListItem instanceof ThirdParty){
      ThirdParty v = (ThirdParty) childListItem;
      ch.itemView.setTag(v);

      ch.icon.setBackgroundColorIcon(v.color);
      ch.icon.setIconCode(v.icon);

      if(v.type == ThirdParty.TYPE_MANULIFE){
        ch.tv_label.setText(ch.itemView.getContext().getString(R.string.manulife_aset_manajemen));
      }else{
        ch.tv_label.setText(v.name);
      }

      ch.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (onItemClicked == null) return;

          ThirdParty ac = (ThirdParty) v.getTag();
          onItemClicked.itemClicked(v, ac);
        }
      });
    }
  }

  private int getListColor(int pos){
    return pos % 2 == 0 ? Color.WHITE : activity.getResources().getColor(R.color.grey_200);
  }

  class HeadHolder extends ParentViewHolder {

    @Bind(R.id.tv_title) TextView tv_title;

    public HeadHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Override public boolean shouldItemViewClickToggleExpansion() {
      return true;
    }
  }

  class ChildHolder extends ChildViewHolder{

    @Bind(R.id.icon) IconCategoryRounded icon;
    @Bind(R.id.tv_right) TextView tv_right;
    @Bind(R.id.tv_label) TextView tv_label;

    public ChildHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class ParentModel implements ParentListItem {

    private String title;
    private List<?> accounts;

    public ParentModel(String title, List<?> accounts) {
      this.title = title;
      this.accounts = accounts;
    }

    @Override public List<?> getChildItemList() {
      return accounts;
    }

    @Override public boolean isInitiallyExpanded() {
      return true;
    }
  }

  public interface OnItemClicked {
    void itemClicked(View v,Object ob);
  }
}
