package javasign.com.dompetsehat.ui.fragments.account.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.ui.activities.account.ManageEachAccountActivity;
import javasign.com.dompetsehat.ui.activities.account.NewManageAccountActivity;
import javasign.com.dompetsehat.ui.activities.account.pojo.CellAccount;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.view.AccountView;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/9/16.
 */
public class NewManageAccountAdapter extends
    ExpandableRecyclerAdapter<NewManageAccountAdapter.HeadHolder, NewManageAccountAdapter.ChildHolder>
    implements ItemEventHelper.ItemEventInterface {

  public void changeItem(int section, int position, CellAccount item) {

  }

  public interface AddAccountClick {
    void addClick(View view, Object childListItem);
  }

  public interface DeleteAccountClick {
    void onDelete(View view, int position, Account account);
  }

  private Context context;
  private LayoutInflater inflater;
  private RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
  private MCryptNew mCryptNew = new MCryptNew();
  private boolean mForceChevronRight = false;
  private boolean mHideValue = false;

  private boolean mHideChildRightIcon = false;
  private AddAccountClick addAccountClick;
  private DeleteAccountClick deleteAccountClick;
  private Activity activity;
  private ItemEventHelper itemEventHelper;
  private ArrayList<Integer> divider;
  String[] color_array;

  public NewManageAccountAdapter(Activity activity, Context context,
      @NonNull List<? extends ParentListItem> parentItemList) {
    super(parentItemList);
    this.activity = activity;
    this.context = context;
    this.inflater = LayoutInflater.from(context);

    //check if child in last pos
    for (ParentListItem p : parentItemList) {
      VendorParent vp = (VendorParent) p;
      int section = vp.section;
      for (int i = 0; i < vp.data.size(); i++) {
        int childPos = i;
        vp.data.get(i).childPos = childPos;
        vp.data.get(i).isLastPos = childPos == vp.data.size() - 1;
      }
    }

    color_array = context.getResources().getStringArray(R.array.color_array);
  }

  public NewManageAccountAdapter setItemEventHelper(ItemEventHelper itemEventHelper) {
    this.itemEventHelper = itemEventHelper;
    return this;
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return itemEventHelper;
  }

  @Override public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {
    this.itemEventHelper.setAdapterOnClickItem(onClickItem);
  }

  public void forceRightIconWithChevronArrow(boolean force, boolean hideValue,
      boolean hideChildRightIcon) {
    mForceChevronRight = force;
    mHideValue = hideValue;
    mHideChildRightIcon = hideChildRightIcon;
  }

  public void setDeleteAccountClick(DeleteAccountClick deleteAccountClick) {
    this.deleteAccountClick = deleteAccountClick;
  }

  public void setAddAccountClick(AddAccountClick addAccountClick) {
    this.addAccountClick = addAccountClick;
  }

  @Override public HeadHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
    View view = inflater.inflate(R.layout.adapter_header_manage_account, parentViewGroup, false);
    HeadHolder hh = new HeadHolder(view);
    return hh;
  }

  @Override public ChildHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
    View view = inflater.inflate(R.layout.adapter_child_manage_account, childViewGroup, false);
    return new ChildHolder(view);
  }

  @Override public void onBindParentViewHolder(final HeadHolder parentViewHolder, int position,
      ParentListItem parentListItem) {
    VendorParent parent = (VendorParent) parentListItem;
    parentViewHolder.tv_title.setText(parent.getLabel().toUpperCase());
    /*if (parent.getSaldo() != null) {
      if(parent.getSaldo() > 0 || parent.getSaldo() < 0) {
        parentViewHolder.tv_total.setText(rcf.toRupiahFormatSimple(parent.getSaldo()));
      }else{
        parentViewHolder.tv_total.setText("");
      }
    }*/

    //parentViewHolder.tv_count.setVisibility(parent.data.isEmpty() ? View.GONE : View.VISIBLE);
    parentViewHolder.tv_count.setVisibility(View.GONE);
    parentViewHolder.tv_count.setText(parent.data.size() + "");

    /*if (!mForceChevronRight) {
      parentViewHolder.indicator.setText(DSFont.Icon.dsf_right_chevron_thin.getFormattedName());
      parentViewHolder.addListenerForView(parentViewHolder.itemView, false);
    } else {
      parentViewHolder.indicator.setText(DSFont.Icon.dsf_right_chevron_thin.getFormattedName());
      int visibility = mHideValue ? View.GONE : View.VISIBLE;
      parentViewHolder.tv_total.setVisibility(visibility);
      parentViewHolder.addListenerForView(parentViewHolder.itemView, false);
    }*/
  }

  @Override public void onBindChildViewHolder(ChildHolder childViewHolder, int position,
      final Object childListItem) {
    CellAccount cell = (CellAccount) childListItem;
    childViewHolder.itemView.setTag(cell);
    childViewHolder.v_divider.setVisibility(cell.isLastPos ? View.GONE : View.VISIBLE);
    if (cell.getBalance() > 0 || cell.getBalance() < 0) {
      childViewHolder.tv_values.setText(rcf.toRupiahFormatSimple(cell.getBalance()));
    } else {
      if (!cell.is_synced()) {
        //Helper.setTextSizeToFit(true,childViewHolder.tv_values);
        childViewHolder.tv_values.setText(cell.getAccount().getLogin_info());
      } else {
        childViewHolder.tv_values.setText("-");
      }
    }
    try {
      childViewHolder.cv_background.setCircleColor(
          cell.getVendorId() > 0 ? AccountView.accountColor.get(cell.getVendorId()) : 0);
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    }
    Timber.e("carrisa "+cell.getIcon());
    if (cell.getIcon() != null) {
      childViewHolder.icon.setText(cell.getIcon());
    } else {
      childViewHolder.icon.setText("");
    }
    if (cell.getName() != null) {
      childViewHolder.tv_account_name.setText(cell.getName().toUpperCase());
    } else {
      childViewHolder.tv_account_name.setText("");
    }

    //childViewHolder.indicator.setOnClickListener(
    //    (View v) -> childViewHolder.showPopup(v, position, cell));
    if (getItemEventHelper() != null) {
      getItemEventHelper().attach(childViewHolder.itemView, childViewHolder.cv_background,0,position);
    } else {
      childViewHolder.itemView.setOnClickListener(v -> {
        if (addAccountClick != null) {
          addAccountClick.addClick(v, childListItem);
          return;
        }
      });
    }
  }

  class HeadHolder extends ParentViewHolder {

    @Bind(R.id.tv_title) TextView tv_title;
    //@Bind(R.id.tv_total) TextView tv_total;
    @Bind(R.id.tv_count) TextView tv_count;
    //@Bind(R.id.indicator) IconicsTextView indicator;
    //@Bind(R.id.v_divider) View v_divider;

    public HeadHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void addListenerForView(View v, boolean expand) {

      /*if (expand) {
        v_divider.setVisibility(View.GONE);
      }

      indicator.setClickable(false);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        indicator.setBackground(null);
      } else {
        indicator.setBackgroundDrawable(null);
      }

      v.setOnClickListener(v1 -> {
        if (isExpanded()) {
          collapseView();
          v_divider.setVisibility(View.VISIBLE);
          indicator.setText(DSFont.Icon.dsf_right_chevron_thin.getFormattedName());
        } else {
          expandView();
          v_divider.setVisibility(View.GONE);
          indicator.setText(DSFont.Icon.dsf_up_chevron_thin.getFormattedName());
        }
      });*/
    }

    @Override public boolean shouldItemViewClickToggleExpansion() {
      return false;
    }
  }

  class ChildHolder extends ChildViewHolder {

    @Bind(R.id.cv_background) CircleShapeView cv_background;
    @Bind(R.id.icon) IconicsTextView icon;
    //@Bind(R.id.indicator) IconicsTextView indicator;
    @Bind(R.id.tv_account_name) TextView tv_account_name;
    @Bind(R.id.tv_total) TextView tv_values;
    @Bind(R.id.v_divider) View v_divider;

    public ChildHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      int visibility = mHideChildRightIcon ? View.GONE : View.VISIBLE;
      //indicator.setVisibility(visibility);
      tv_values.setVisibility(visibility);
    }

    //public void showPopup(View v, int position, CellAccount cellAccount) {
    //  PopupMenu popup = new PopupMenu(context, v);
    //  popup.getMenuInflater().inflate(R.menu.account_action_menu, popup.getMenu());
    //  popup.setOnMenuItemClickListener(item -> {
    //    if (item.getItemId() == R.id.mn_edit) {
    //      editItem(cellAccount, cv_background);
    //    } else if (item.getItemId() == R.id.mn_delete) {
    //      deleteAccountClick.onDelete(v, position, cellAccount.getAccount());
    //    }
    //    return true;
    //  });
    //
    //  popup.show();
    //}
  }

  public void editItem(Object childListItem, CircleShapeView cv_background) {
    Bundle b = new Bundle();
    CellAccount cellAccount = (CellAccount) childListItem;
    b.putInt(ManageEachAccountActivity.KEY_ID_ACCOUNT, cellAccount.getAccount().getIdaccount());
    b.putInt(ManageEachAccountActivity.KEY_ID_ACCOUNT, cellAccount.getAccount().getIdaccount());
    int color = AccountView.accountColor.get(cellAccount.getVendorId());
    b.putInt("color", color);

    Intent intent = new Intent(context, ManageEachAccountActivity.class);
    intent.putExtras(b);
    View sharedView = cv_background;
    String transitionName = context.getString(R.string.category);

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      ActivityOptions transitionActivityOptions =
          ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, transitionName);
      context.startActivity(intent, transitionActivityOptions.toBundle());
    } else {
      context.startActivity(intent);
    }

    if(cellAccount.getAccount().getIdaccount() == AccountView.MNL) {
      Helper.trackThis(context, "user klik manulife "+ NewManageAccountActivity.accessFrom);
    }
  }

  public static class VendorParent implements ParentListItem {

    private String label;
    private Double saldo;
    private int section;
    private List<CellAccount> data;

    public void setSection(int section) {
      this.section = section;
    }

    public int getSection() {
      return section;
    }

    public VendorParent(String label, List<CellAccount> data) {
      this.label = label;
      this.data = data;
    }

    public VendorParent setLabel(String label) {
      this.label = label;
      return this;
    }

    public VendorParent setSaldo(Double saldo) {
      this.saldo = saldo;
      return this;
    }

    @Override public List<?> getChildItemList() {
      return data;
    }

    @Override public boolean isInitiallyExpanded() {
      return true;
    }

    public String getLabel() {
      return label;
    }

    public Double getSaldo() {
      return saldo;
    }
  }
}
