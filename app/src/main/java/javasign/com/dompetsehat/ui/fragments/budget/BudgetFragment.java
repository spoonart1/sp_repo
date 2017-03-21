package javasign.com.dompetsehat.ui.fragments.budget;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.presenter.budget.BudgetPresenter;
import javasign.com.dompetsehat.presenter.sync.SyncInterface;
import javasign.com.dompetsehat.ui.activities.budget.AddBudgetActivity;
import javasign.com.dompetsehat.ui.activities.budget.BudgetDetailActivity;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterBudget;
import javasign.com.dompetsehat.ui.activities.budget.pojo.HeaderBudget;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.ui.event.DeleteBudgetEvent;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.utils.BehaviorUtil;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
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
 * Created by lafran on 9/13/16.
 */

public class BudgetFragment extends BaseFragment implements SyncInterface {

  @Bind(R.id.refresh_layout) SwipeRefreshLayout refresh_layout;
  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Bind(R.id.tv_amount_left) TextView tv_amount_left;
  @Bind(R.id.circularSeekBar1) CircularSeekBar csb;
  @Bind(R.id.tv_progress) TextView tv_progress;
  @Bind(R.id.floating_action_menu) FloatingActionsMenu fab_action;

  @Inject BudgetPresenter presenter;

  private AdapterBudget adapterBudget;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private BlankView bv;
  private BaseActivity activity;
  private BehaviorUtil behaviorUtil;
  boolean onDelete = false;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_budget, null);
    ButterKnife.bind(this, view);
    FloatingButtonHelper.init(getActivity(), view)
        .attachToRecyclerview(recycleview)
        .leaveOnlyPlusButton(new FloatingButtonHelper.OnPlusButtonClick() {
          @Override public void onClick(View v) {
            Helper.trackThis(getActivity(), "user klik + anggaran");
            Helper.openDialogPreAddBudget(getActivity(), AddBudgetActivity.class);
          }
        });
    activity = (BaseActivity) getActivity();
    activity.getActivityComponent().inject(this);
    presenter.attachView(this);
    setHasMenuEditor("");
    bv = new BlankView(view, DSFont.Icon.dsf_budget_filled.getFormattedName(),
        getString(R.string.budget_is_empty));

    bv.showActionButton(getResources().getString(R.string.pelajari_lanjut),
        v -> showPracticeDialog());

    bv.beginLoading(null);
    behaviorUtil = BehaviorUtil.attach(view)
        .listenToTextView(tv_progress, tv_amount_left)
        .setTitles(getString(R.string.used).toUpperCase(), "Total".toUpperCase());

    TourHelper.init(getActivity())
        .withDefaultButtonEnable(true)
        .setSessionKey(State.FLAG_FRAGMENT_BUDGET)
        .setViewsToAttach(view, R.id.floating_action_menu)
        .setTourTitles(getString(R.string.add_budget))
        .setTourDescriptions("Persiapkan anggaran Anda.")
        .setGravities(Gravity.LEFT | Gravity.TOP)
        .create()
        .show();

    init();

    Helper.trackThis(getActivity(), "user membuka tampilan Anggaran");

    return view;
  }

  private void showPracticeDialog() {
    AdvancedDialog.ABuilder builder =
        new AdvancedDialog.ABuilder(getContext(), getString(R.string.budget_feature),
            Color.BLACK).withTitleBold(true)
            .withCloseButton(true)
            .withFooterButton(false)
            .addImage(R.drawable.ss_myfin_anggaran, null, ViewGroup.LayoutParams.MATCH_PARENT,
                ChartUtils.dp2px(getResources().getDisplayMetrics().density, 440))
            .addText(getString(R.string.budget_monthly_recommendation))
            .addSpace(getResources().getDimensionPixelSize(R.dimen.padding_size_high));

    Helper.showAdvancedDialog(getFragmentManager(), builder);
    Helper.trackThis(getActivity(), "User menekan 'Pelajari lebih lanjut di Anggaran'");
  }

  @Override public void onResume() {
    super.onResume();
    presenter.attachView(this);
    setData();
  }

  private void init() {
    presenter.init();
    listenRxBus();
    final LinearLayoutManager layoutManager =
        new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    recycleview.setLayoutManager(layoutManager);

    refresh_layout.setColorSchemeColors(getResources().getIntArray(R.array.color_scheme_loading));
    refresh_layout.setOnRefreshListener(this);

    csb.setIsTouchEnabled(false);
    adapterBudget = new AdapterBudget();

    setData();
    Helper.trackThis(getActivity(), "User di halaman anggaran");
  }

  private void setData() {
    if(!fab_action.isShown()){
      fab_action.setVisibleWithAnimation(true);
    }
    presenter.setData();
  }

  private void listenRxBus() {
    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(DeleteBudgetEvent.class)
        .subscribe(new SimpleObserver<DeleteBudgetEvent>() {
          @Override public void onNext(DeleteBudgetEvent deleteBudgetEvent) {
            setData();
          }
        });
  }

  @Override public void onPause() {
    Helper.disposeSwiper(refresh_layout);
    super.onPause();
  }

  @Override public void onDestroy() {
    Helper.disposeSwiper(refresh_layout);
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void onRefresh() {
    refresh_layout.setRefreshing(true);
    setData();
  }

  @Override public void setAdapterHeader(HeaderBudget headerBudget) {
    double sisa = Math.abs(headerBudget.to - headerBudget.from);
    Timber.e("sisa " + sisa);
    Timber.e("to " + headerBudget.to);
    Timber.e("from " + headerBudget.from);
    tv_amount_left.setText(format.toRupiahFormatSimple(sisa));
    int persen = (int) ((headerBudget.from / headerBudget.to) * 100);
    if(persen <= 0) persen = 0;
    else if(persen >= 100 ) persen = 100;
    tv_progress.setText(String.valueOf(persen));
    csb.setProgress(persen);
    csb.invalidate();
    behaviorUtil.register(-1);
  }

  @Override public void setAdapterBudget(List<Budget> budgets) {
    synchronized (this) {
      activity.runOnUiThread(() -> {
        adapterBudget.setRealData(budgets);
        adapterBudget.setItemEventHelper(itemHelper);
        itemHelper.setRefreshLayoutDisableIfExist(refresh_layout);
        adapterBudget.setOnClickItem(new ItemEventHelper.OnClickItem<Budget>() {
          @Override public void onClick(View v, Budget item,int section,int position) {
            seeDetail(item);
          }

          @Override public void onLongPressed(View v, final Budget item,int section,int position) {
            itemHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {
              @Override public void onDelete() {
                if(!onDelete) {
                  onDelete = true;
                  adapterBudget.removeItem(item);
                  fab_action.setVisibleWithAnimation(false);
                  final DeleteUtils deleteUtils = beginPendingRemoval(item);
                  Helper.deleteWithConfirmationMessage(v, getActivity(), getString(R.string.budget_has_been_deleted),
                      v1 -> deleteUtils.cancelRemoval(), new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override public void onDismissed(Snackbar transientBottomBar, int event) {
                          super.onDismissed(transientBottomBar, event);
                          onDelete = false;
                        }
                      });
                }
              }

              @Override public void onEdit() {
                seeDetail(item);
              }
            });
          }
        });
        recycleview.setAdapter(adapterBudget);
        adapterBudget.notifyDataSetChanged();
        Helper.checkIfBlank(bv, budgets.isEmpty());
        Helper.disposeSwiper(refresh_layout);
      });
    }
  }

  private DeleteUtils beginPendingRemoval(Budget item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, presenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        setData();
      }

      @Override public void onDoneRemoving() {
        MyCustomApplication.getRxBus().send(new DeleteBudgetEvent(getActivity().getApplicationContext()));
      }
    });
    return deleteUtils;
  }

  private void seeDetail(Budget budget) {
    Intent intent = new Intent(getActivity(), BudgetDetailActivity.class);
    intent.putExtra(Budget.MODE, Budget.MODE_EDIT);
    intent.putExtra(Budget.BUDGET_ID, budget.getId());
    startActivity(intent);
  }

  @Override public void onNext(int code, String message) {

  }
}
