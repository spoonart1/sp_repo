package javasign.com.dompetsehat.ui.fragments.finplan.listfragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.presenter.main.FinplanPresenter;
import javasign.com.dompetsehat.presenter.sync.SyncInterface;
import javasign.com.dompetsehat.ui.activities.plan.AddPlanActivity;
import javasign.com.dompetsehat.ui.activities.plan.DetailPlanActivity;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.ui.event.DeletePlanEvent;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.finplan.adapter.AdapterPlanSimple;
import javasign.com.dompetsehat.utils.BehaviorUtil;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.TourHelper;
import javasign.com.dompetsehat.view.BlankView;
import javasign.com.dompetsehat.view.CircularSeekBar;
import javax.inject.Inject;
import lecho.lib.hellocharts.util.ChartUtils;
import timber.log.Timber;
import toan.android.floatingactionmenu.FloatingActionsMenu;

/**
 * Created by bastianbentra on 8/25/16.
 */
public class PlanFragment extends BaseFragment implements SyncInterface {

  @Bind(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.circularSeekBar1) CircularSeekBar csb;
  @Bind(R.id.tv_progress) TextView tv_progress;
  @Bind(R.id.tv_amount_incomplete) TextView tv_amount_incomplete;
  @Bind(R.id.floating_action_menu) FloatingActionsMenu fab_action;
  @Bind(R.id.tv_persen) TextView tv_persen;

  private BlankView bv;
  private AdapterPlanSimple adapter;
  private BaseActivity activity;
  private BehaviorUtil behaviorUtil;

  @Inject FinplanPresenter presenter;
  int healthyColor;
  int warningColor;
  int redAlerColor;
  private boolean onDelete = false;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_plan, null);
    ButterKnife.bind(this, view);
    FloatingButtonHelper.init(getActivity(), view)
        .leaveOnlyPlusButton(v -> {
          startActivity(new Intent(getActivity(), AddPlanActivity.class));
          Helper.trackThis(getActivity(), "user tambah rencana dari menu rencana");
        });
    bv = new BlankView(view, DSFont.Icon.dsf_finplan_filled.getFormattedName(),
        getString(R.string.plan_is_empty));

    bv.showActionButton(getResources().getString(R.string.pelajari_lanjut), v -> showPracticeDialog());

    bv.beginLoading(null);
    setHasMenuEditor("");
    itemHelper.deleteMenuOnly();

    activity = ((BaseActivity) getActivity());
    activity.getActivityComponent().inject(this);
    presenter.attachView(this);
    behaviorUtil = BehaviorUtil.attach(view)
        .listenToTextView(tv_progress, tv_amount_incomplete)
        .setTitles(getString(R.string.progress).toUpperCase(), "Total".toUpperCase());

    TourHelper.init(getActivity())
        .withDefaultButtonEnable(true)
        .setSessionKey(State.FLAG_FRAGMENT_FINPLAN)
        .setViewsToAttach(view, R.id.floating_action_menu)
        .setTourTitles(getString(R.string.add_plan))
        .setTourDescriptions("Rencanakan masa depan Anda.")
        .setGravities(Gravity.LEFT | Gravity.TOP)
        .create().show();

    init();

    Helper.trackThis(getActivity(), "user klik menu rencana");

    return view;
  }

  private void init() {
    healthyColor = ContextCompat.getColor(getContext(), R.color.green_health);
    warningColor = ContextCompat.getColor(getContext(), R.color.yellow_warning);
    redAlerColor = ContextCompat.getColor(getContext(), R.color.red_alert);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    refreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.color_scheme_loading));
    refreshLayout.setOnRefreshListener(this);
    setData();
    csb.setIsTouchEnabled(false);


    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(DeletePlanEvent.class)
        .subscribe(new SimpleObserver<DeletePlanEvent>() {
          @Override public void onNext(DeletePlanEvent deletePlanEvent) {
            setData();
          }
        });
  }

  @Override public void onResume() {
    super.onResume();
    presenter.attachView(this);
  }

  private void setData() {
    if (!fab_action.isShown()) {
      fab_action.setVisibleWithAnimation(true);
    }
    presenter.loadData();
  }

  private void showPracticeDialog() {
    AdvancedDialog.ABuilder builder =
        new AdvancedDialog.ABuilder(getContext(), getString(R.string.plan_feature),
            Color.BLACK).withTitleBold(true)
            .withCloseButton(true)
            .withFooterButton(false)
            .addImage(R.drawable.ss_myfin_rencana, null, ViewGroup.LayoutParams.MATCH_PARENT,
                ChartUtils.dp2px(getResources().getDisplayMetrics().density, 440))
            .addText(getString(R.string.you_can_create_future_plan))
            .addSpace(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.line_divider_size))
            .addImage(R.drawable.ss_rencana_item, null, ViewGroup.LayoutParams.MATCH_PARENT,
                ChartUtils.dp2px(getResources().getDisplayMetrics().density, 105))
            .addText(getString(R.string.manage_monitor_your_plan))
            .addSpace(getResources().getDimensionPixelSize(R.dimen.padding_size_high));

    Helper.showAdvancedDialog(getFragmentManager(), builder);
    Helper.trackThis(getActivity(), "User menekan 'Pelajari lebih lanjut di Rencana'");
  }

  @Override public void onRefresh() {
    setData();
  }

  @Override public void onPause() {
    Helper.disposeSwiper(refreshLayout);
    super.onPause();
  }

  @Override public void onDestroy() {
    Helper.disposeSwiper(refreshLayout);
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void setAdapterPlan(ArrayList<Plan> plans) {
    activity.runOnUiThread(() -> {
      adapter = new AdapterPlanSimple(plans);
      adapter.setItemEventHelper(itemHelper);
      itemHelper.setRefreshLayoutDisableIfExist(refreshLayout);
      adapter.setOnClickItem(new ItemEventHelper.OnClickItem<Plan>() {
        @Override public void onClick(View v, Plan item,int section,int position) {
          seeDetail(item);
        }

        @Override public void onLongPressed(View v, final Plan item,int section,int position) {
          itemHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {
            @Override public void onEdit() {

              seeDetail(item);
            }

            @Override public void onDelete() {
              if (!onDelete) {
                onDelete = true;
                adapter.removeItem(item);
                fab_action.setVisibleWithAnimation(false);
                final DeleteUtils deleteUtils = beginPendingRemoval(item);
                Helper.deleteWithConfirmationMessage(v, getActivity(),
                    getString(R.string.plan_has_been_deleted), v1 -> {
                      deleteUtils.cancelRemoval();
                    }, new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                      @Override public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        onDelete = false;
                      }
                    });
              }
            }
          });
        }
      });
      recyclerView.setAdapter(adapter);
      adapter.notifyDataSetChanged();
      Helper.checkIfBlank(bv, plans.isEmpty());
      Helper.disposeSwiper(refreshLayout);
    });
  }

  private DeleteUtils beginPendingRemoval(Plan item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, presenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        setData();
        fab_action.setVisibleWithAnimation(true);
      }

      @Override public void onDoneRemoving() {
        MyCustomApplication.getRxBus()
            .send(new DeletePlanEvent(getActivity().getApplicationContext()));
      }
    });
    return deleteUtils;
  }

  private void seeDetail(Plan plan) {
    Intent i = new Intent(getActivity(), DetailPlanActivity.class);
    i.putExtra(DetailPlanActivity.PLAN_ID_LOCAL, plan.getId());
    startActivity(i);
  }

  @Override public void setCicularProgressBar(double total_budget, double total_budget_tercapai) {
    getActivity().runOnUiThread(() -> {
      Timber.e(
          "total_budget " + total_budget + " total_budget_belum_tercapai " + total_budget_tercapai);
      int prosentase = (int) Math.ceil((total_budget_tercapai / total_budget) * 100);

      if(prosentase <= 0) prosentase = 0;
      else if(prosentase >= 100 ) prosentase = 100;

      double total_budget_belum_tercapai = 0;
      if (total_budget_tercapai < total_budget) {
        total_budget_belum_tercapai = total_budget - total_budget_tercapai;
      }
      tv_amount_incomplete.setText(
          new RupiahCurrencyFormat().toRupiahFormatSimple(total_budget_belum_tercapai));
      csb.setProgress(prosentase);
      tv_progress.setText(String.valueOf(prosentase));

      int accentColor = generateColor(prosentase);
      csb.setCircleProgressColor(accentColor);
      tv_progress.setTextColor(accentColor);
      tv_persen.setTextColor(accentColor);
      behaviorUtil.register(accentColor);
    });

    Helper.disposeSwiper(refreshLayout);
  }

  private int generateColor(int prosentase) {
    if (prosentase <= 30) {
      return redAlerColor;
    } else if (prosentase > 30 && prosentase <= 60) {
      return warningColor;
    }

    return healthyColor;
  }

  @Override public void onNext(int code, String message) {

  }
}
