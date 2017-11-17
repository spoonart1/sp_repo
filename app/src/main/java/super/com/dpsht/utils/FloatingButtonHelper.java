package javasign.com.dompetsehat.utils;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.account.AddAccountActivity;
import javasign.com.dompetsehat.ui.activities.budget.AddBudgetActivity;
import javasign.com.dompetsehat.ui.activities.plan.AddPlanActivity;
import javasign.com.dompetsehat.ui.activities.transaction.AddTransactionActivity;
import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

/**
 * Created by lafran on 12/2/16.
 */

public class FloatingButtonHelper {

  public interface OnPlusButtonClick {
    void onClick(View v);
  }

  @Bind(R.id.floating_action_menu) FloatingActionsMenu actionsMenu;
  @Bind(R.id.fab_add_transaction) FloatingActionButton fab_add_transaction;
  @Bind(R.id.fab_add_account) FloatingActionButton fab_add_account;
  @Bind(R.id.fab_add_plan) FloatingActionButton fab_add_plan;
  @Bind(R.id.fab_add_budget) FloatingActionButton fab_add_budget;
  @Bind(R.id.splash_view) View splash_view;

  public View rootView;
  private FragmentActivity mFragmentActivity;
  private FloatingActionsMenu.OnFloatingActionsMenuUpdateListener floatingActionsMenuUpdateListener;

  public FloatingButtonHelper(FragmentActivity activity, View rootView) {
    ButterKnife.bind(this, rootView);
    this.rootView = rootView;
    mFragmentActivity = activity;
    floatingActionsMenuUpdateListener =
        new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
          @Override public void onMenuExpanded() {
            Helper.animateFade(splash_view, true, activity);
          }

          @Override public void onMenuCollapsed() {
            Helper.animateFade(splash_view, false, activity);
          }
        };
    actionsMenu.setOnFloatingActionsMenuUpdateListener(floatingActionsMenuUpdateListener);
  }

  @OnClick(R.id.splash_view) void onScrimClick() {
    collapse();
  }

  public FloatingButtonHelper removeFabButton(int... fabIds) {
    for (int fabId : fabIds) {
      FloatingActionButton fab = ButterKnife.findById(actionsMenu, fabId);
      if(fab != null){
        actionsMenu.removeButton(fab);
      }
    }
    return this;
  }

  public FloatingButtonHelper attachToRecyclerview(RecyclerView recyclerView) {
    actionsMenu.attachToRecyclerView(recyclerView);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState != RecyclerView.SCROLL_STATE_IDLE) {
          actionsMenu.setVisibleWithAnimation(false);
        } else {
          actionsMenu.setVisibleWithAnimation(true);
        }
      }
    });
    return this;
  }

  public void collapse() {
    if (actionsMenu != null && actionsMenu.isExpanded()) {
      actionsMenu.collapse();
    }
  }

  public void expand() {
    if (actionsMenu != null && !actionsMenu.isExpanded()) actionsMenu.expand();
  }

  public FloatingButtonHelper leaveOnlyPlusButton(OnPlusButtonClick onPlusButtonClick) {
    removeFabButton(R.id.fab_add_transaction, R.id.fab_add_account, R.id.fab_add_plan,
        R.id.fab_add_budget);
    floatingActionsMenuUpdateListener = null;
    actionsMenu.setOnFloatingActionsMenuUpdateListener(
        new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
          @Override public void onMenuExpanded() {
            onPlusButtonClick.onClick(actionsMenu);
            actionsMenu.collapse();
            Helper.trackThis(mFragmentActivity, "user klik tombol +");
          }

          @Override public void onMenuCollapsed() {
            actionsMenu.collapse();
          }
        });
    return this;
  }

  public static FloatingButtonHelper init(FragmentActivity activity, View rootview) {
    FloatingButtonHelper floatingButtonHelper = new FloatingButtonHelper(activity, rootview);
    floatingButtonHelper.mFragmentActivity = activity;
    return floatingButtonHelper;
  }

}
