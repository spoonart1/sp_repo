package javasign.com.dompetsehat.ui.fragments.timeline;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.presenter.transaction.TransactionsPresenter;
import javasign.com.dompetsehat.services.InsertDataService;
import javasign.com.dompetsehat.ui.activities.account.AddAccountActivity;
import javasign.com.dompetsehat.ui.activities.budget.AddBudgetActivity;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategory;
import javasign.com.dompetsehat.ui.activities.category.listfragment.ListCategoryFragment;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.activities.plan.AddPlanActivity;
import javasign.com.dompetsehat.ui.activities.transaction.AddTransactionActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.ui.dialogs.DialogPickCategory;
import javasign.com.dompetsehat.ui.event.AddTransactionEvent;
import javasign.com.dompetsehat.ui.event.DeleteTransactionEvent;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.timeline.adapter.NewTimelineAdapter;
import javasign.com.dompetsehat.ui.fragments.timeline.pojo.TimelineView;
import javasign.com.dompetsehat.utils.BehaviorUtil;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.TourHelper;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.BlankView;
import javasign.com.dompetsehat.view.CustomProgress;
import javax.inject.Inject;
import timber.log.Timber;
import toan.android.floatingactionmenu.FloatingActionButton;

public class NewTimelineFragment extends BaseFragment
    implements AdapterView.OnItemSelectedListener {

  private NewTimelineAdapter adapter;
  String[] color_array;
  private boolean isReady = true;
  private BlankView bv;
  private int currentPosition = 0;

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;

  @Bind(R.id.tv_title) TextView tv_title;
  @Bind(R.id.value_nett) TextView value_nett;
  @Bind(R.id.label_top) TextView label_top;
  @Bind(R.id.label_bot) TextView label_bot;
  @Bind(R.id.value_right_top) TextView value_right_top;
  @Bind(R.id.value_right_bot) TextView value_right_bot;
  @Bind(R.id.tv_bulan) TextView tv_bulan;
  @Bind(R.id.tv_total_balance) TextView tv_total_balance;
  @Bind(R.id.line_progress_top) CustomProgress line_progress_top;
  @Bind(R.id.line_progress_bot) CustomProgress line_progress_bot;

  @Inject TransactionsPresenter presenter;
  @Inject SyncPresenter presenterSync;
  private NewMainActivityMyFin activity;
  private FloatingButtonHelper buttonHelper;
  private BehaviorUtil behaviorUtil;
  private TourHelper tourHelper;
  private LinearLayoutManager layoutManager;
  private View rootView;
  private TimelineView mCurrentTimelineview;
  private Account account = null;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
    ButterKnife.bind(this, rootView);
    buttonHelper =
        FloatingButtonHelper.init(getActivity(), rootView).attachToRecyclerview(recyclerView);
    this.activity = ((NewMainActivityMyFin) getActivity());
    this.activity.setSimpleBarOnSelectedListener(this);
    this.activity.getActivityComponent().inject(this);
    presenter.attachView(this);

    color_array = getResources().getStringArray(R.array.color_array);
    currentPosition = 0;

    refreshLayout.setOnRefreshListener(this);
    refreshLayout.setColorSchemeColors(Helper.GREEN_DOMPET_COLOR);

    layoutManager = new LinearLayoutManager(getActivity());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(layoutManager);

    State.registerBroadCast(getActivity(), onDataUpdated);

    bv = new BlankView(rootView, DSFont.Icon.dsf_transactions.getFormattedName(),
        getString(R.string.transaction_is_empty));

    bv.beginLoading(null);
    /*bv.showActionButton(getResources().getString(R.string.pelajari_lanjut), new View.OnClickListener() {
      @Override public void onClick(View v) {
        Helper.showSimpleInformationDialog(getFragmentManager(), "Tahukah kamu?", "some texts will appear here");
      }
    });*/

    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(DeleteTransactionEvent.class)
        .subscribe(new SimpleObserver<DeleteTransactionEvent>() {
          @Override public void onNext(DeleteTransactionEvent deleteTransactionEvent) {
            if (account != null) {
              presenter.timelineLoadData(account.getIdaccount());
            } else {
              presenter.timelineLoadData(false);
            }
          }
        });

    //presenter.timelineLoadData(false);

    line_progress_top.setProgressBackgroundColor(
        ContextCompat.getColor(getContext(), R.color.grey_300));
    line_progress_bot.setProgressBackgroundColor(
        ContextCompat.getColor(getContext(), R.color.grey_300));

    behaviorUtil = BehaviorUtil.attach(rootView)
        .listenToTextView(value_right_top, value_right_bot)
        .setTitles(label_top.getText().toString().toUpperCase(),
            label_bot.getText().toString().toUpperCase())
        .setCustomSymbol("");

    listenRxBus();
    return rootView;
  }

  private void listenRxBus() {
    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(AddTransactionEvent.class)
        .subscribe(new SimpleObserver<AddTransactionEvent>() {
          @Override public void onNext(AddTransactionEvent addTransactionEvent) {
            if (account != null) {
              presenter.timelineLoadData(account.getIdaccount());
            } else {
              presenter.timelineLoadData(false);
            }
          }
        });

    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(DeleteTransactionEvent.class)
        .subscribe(new SimpleObserver<DeleteTransactionEvent>() {
          @Override public void onNext(DeleteTransactionEvent deleteTransactionEvent) {
            if (account != null) {
              presenter.timelineLoadData(account.getIdaccount());
            } else {
              presenter.timelineLoadData(false);
            }
          }
        });
  }

  @OnClick({
      R.id.fab_add_transaction, R.id.fab_add_account, R.id.fab_add_plan, R.id.fab_add_budget
  }) void onFabClick(FloatingActionButton button) {
    Intent intent = null;
    switch (button.getId()) {
      case R.id.fab_add_transaction:
        intent = new Intent(getActivity(), AddTransactionActivity.class);
        intent.putExtra(AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_ADD);
        Helper.trackThis(getActivity(), "user klik tombol tambah transaksi (dari home)");
        break;
      case R.id.fab_add_account:
        intent = new Intent(getActivity(), AddAccountActivity.class);
        Helper.trackThis(getActivity(), "user klik tombol tambah rekening (dari home)");
        break;
      case R.id.fab_add_plan:
        intent = new Intent(getActivity(), AddPlanActivity.class);
        Helper.trackThis(getActivity(), "user klik tombol tambah rencana (dari home)");
        break;
      case R.id.fab_add_budget:
        Helper.openDialogPreAddBudget(getActivity(), AddBudgetActivity.class);
        Helper.trackThis(getActivity(), "user klik tombol tambah anggaran (dari home)");
        break;
    }
    if (intent != null) startActivity(intent);
  }

  @OnClick(R.id.btn_see_all) void seeAllTransactions() {
    startActivity(
        new Intent(getContext(), TransactionsActivity.class).putExtra(TransactionsActivity.FROM,
            State.FROM_TIMELINE));
  }

  @Override public void onRefresh() {
    if (refreshLayout != null && isReady) {
      if (!isMyServiceRunning(InsertDataService.class)) {
        activity.LastConnectGetCashFlow();
      }
      if (account != null) {
        presenter.timelineLoadData(account.getIdaccount());
      } else {
        presenter.timelineLoadData(false);
      }
    }
  }

  private boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
        Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  @Override public void onPause() {
    Helper.disposeSwiper(refreshLayout);
    buttonHelper.collapse();
    super.onPause();
  }

  @Override public void onDestroy() {
    State.unRegisterBroadCast(getActivity(), onDataUpdated);
    Helper.disposeSwiper(refreshLayout);
    try {
      presenter.detachView();
      presenterSync.detachView();
    } catch (Exception e) {

    }
    super.onDestroy();
  }

  private BroadcastReceiver onDataUpdated = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      String eventName = intent.getStringExtra(State.DEFAULT_KEY_STR);
      if (eventName.equalsIgnoreCase(State.EVENT_CONTENT_NEED_UPDATE)) {
        presenter.timelineLoadData(true);
        try {
          Helper.showCustomSnackBar(rootView, LayoutInflater.from(getActivity()),
              getString(R.string.data_updated), false,
              ContextCompat.getColor(context, R.color.colorPrimaryDark));
        } catch (Exception e) {

        }
      }
    }
  };

  @Override public void setTimeline(TimelineView timelineView, boolean isBroadcasted) {
    this.activity.runOnUiThread(() -> {
      System.out.println("NewTimelineFragment.setTimeline: asdasd");
      this.mCurrentTimelineview = timelineView;
      setHeaderData(timelineView.header);
      adapter = new NewTimelineAdapter(timelineView).setHideCategoryLabel(true);
      adapter.setOnClickItem(new NewTimelineAdapter.OnClickItem() {
        @Override public void onClick(View v, Cash cash, int section, int position) {
          Intent i = new Intent(getActivity(), AddTransactionActivity.class);
          i.putExtra(AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_EDIT);
          i.putExtra(AddTransactionActivity.KEY_CASHFLOW_ID, cash.getId());
          getActivity().startActivityForResult(i, 1);
        }

        @Override public void onLongClick(View v, Cash cash, int section, int position) {
          final EditText input = new EditText(getActivity());
          String desc = cash.getCashflow_rename();
          if (TextUtils.isEmpty(desc)) {
            desc = cash.getDescription();
          }
          input.setText(desc);
          LinearLayout.LayoutParams lp =
              new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                  LinearLayout.LayoutParams.MATCH_PARENT);
          lp.setMargins(30, 30, 30, 30);
          input.setLayoutParams(lp);
          AlertDialog alertDialog =
              new AlertDialog.Builder(getActivity()).setTitle("Edit description")
                  .setView(input)
                  .setPositiveButton(getContext().getString(R.string.save),
                      new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                          presenter.changeRenameCashflow(cash.getId(), input.getText().toString());
                          currentPosition = position;
                          cash.setCashflow_rename(input.getText().toString());
                          adapter.changeItem(position, cash);
                        }
                      })
                  .setNegativeButton(getContext().getString(R.string.cancel),
                      new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                          dialogInterface.dismiss();
                        }
                      })
                  .create();
          alertDialog.show();
        }

        @Override public void onCategoryClick(View v, Cash item, int position) {
          int typetransaction =
              item.getType().equalsIgnoreCase(Cash.CREDIT) ? ListCategoryFragment.INCOME_CATEGORY
                  : ListCategoryFragment.EXPENSE_CATEGORY;
          final DialogPickCategory dialogPickCategory =
              DialogPickCategory.newInstance(item.getType(), item.getType());
          dialogPickCategory.setOnCategoryClick(new AdapterCategory.OnCategoryClick() {
            @Override public void onClickParent(Category category, int section) {
              Timber.e("avesin onClickParent " + category.getName());
              currentPosition = position;
              presenter.changeCategory(item.getId(), category.getId_category());
              adapter.changeItem(item, category);
              dialogPickCategory.dismissAllowingStateLoss();
            }

            @Override public void onClickChild(UserCategory userCategory, int position) {
              Timber.e("avesin onClickChild " + userCategory.getName());
              if (userCategory != null) {
                currentPosition = position;
                Timber.e("avesin onClickChild heloo " + userCategory.getName());
                presenter.changeUserCategory(item.getId(), userCategory.getParentCategoryId(),
                    userCategory.getId_user_category());
                adapter.changeChidlItem(item, userCategory);
                dialogPickCategory.dismissAllowingStateLoss();
              }
            }

            @Override public void onLongClickParent(Category category, int section) {

            }
          });
          dialogPickCategory.show(getActivity().getSupportFragmentManager(), "category");
        }
      });
      recyclerView.setAdapter(adapter);
      recyclerView.scrollToPosition(currentPosition);
      refreshLayout.setRefreshing(false);
      isReady = true;
      tourHelper = null;

      Helper.checkIfBlank(bv, timelineView.cashs.isEmpty());
      Helper.disposeSwiper(refreshLayout);
      boolean isFirstTimeLaunched = presenter.session.isFirstTime();
      boolean allowedToShowTourguide = presenter.session.isAllowedToShowGuide(State.FLAG_FRAGMENT_TIMELINE);

      if (isBroadcasted) {
        showTour(timelineView);
        presenter.session.setIsFirstTimeTrue();
        System.out.println("NewTimelineFragment.setTimeline: show first time");
      } else if (!isFirstTimeLaunched && allowedToShowTourguide) {
        System.out.println("NewTimelineFragment.setTimeline: show now when skipped");
        showTour(timelineView);
      }
    });
  }

  private void showTour(TimelineView timelineView) {
    if (tourHelper == null) {
      tourHelper = TourHelper.init(getActivity())
          .setSessionKey(State.FLAG_FRAGMENT_TIMELINE)
          .setSupportFloatingButtonHelper(buttonHelper, 0)
          .withDefaultButtonEnable(true)
          .setViewsToAttach(rootView, R.id.fab_add_transaction, R.id.btn_see_all, R.id.ic_search, R.id.iv_user_pict)
          .setTourTitles(getResources().getStringArray(R.array.tour_guides_titles_step_timeline))
          .setTourDescriptions(getResources().getStringArray(R.array.tour_guides_desc_timeline))
          .setGravities(Gravity.LEFT | Gravity.TOP, Gravity.TOP | Gravity.CENTER,
              Gravity.BOTTOM | Gravity.LEFT, Gravity.BOTTOM | Gravity.RIGHT)
          .create();

      if (timelineView.cashs.isEmpty()) {
        tourHelper.removeGuideAtPosition(1);
      }
      tourHelper.show();
      tourHelper.setTourListener(() -> {
        if (timelineView.cashs.size() > 0) {
          View circleItemInRecyclerview =
              layoutManager.findViewByPosition(0).findViewById(R.id.cv_background);
          tourHelper.showSingleTourGuide(circleItemInRecyclerview, "Ubah Kategori Per Transaksi",
              "Klik untuk mengubah kategori lebih mudah dan cepat", Gravity.BOTTOM | Gravity.RIGHT);
        }
      });
    }
  }

  private void setHeaderData(TimelineView.Header headerData) {
    RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
    int expenseColor = ContextCompat.getColor(getContext(), R.color.red_700);
    int incomeColor = ContextCompat.getColor(getContext(), R.color.green_600);

    label_top.setText(getContext().getString(R.string.income).toUpperCase());
    label_bot.setText(getContext().getString(R.string.expense).toUpperCase());

    Double maxValue = Double.valueOf(headerData.totalExpense);
    if (Double.valueOf(headerData.totalIncome) > Double.valueOf(headerData.totalExpense)) {
      maxValue = Double.valueOf(headerData.totalIncome);
    }

    double maxExpense = maxValue;
    double currentExpense = Double.valueOf(headerData.totalExpense);
    line_progress_bot.setColors(expenseColor, expenseColor, expenseColor);
    line_progress_bot.setMaxValue(maxExpense);
    line_progress_bot.setProgressValue(currentExpense);
    value_right_bot.setText(format.toRupiahFormatSimple(currentExpense));

    double maxIncome = maxValue;
    double currentIncome = Double.valueOf(headerData.totalIncome);
    line_progress_top.setColors(incomeColor, incomeColor, incomeColor);
    line_progress_top.setMaxValue(maxIncome);
    line_progress_top.setProgressValue(currentIncome);
    value_right_top.setText(format.toRupiahFormatSimple(currentIncome));

    value_nett.setText(format.toRupiahFormatSimple(Double.valueOf(headerData.nettIncome)));
    value_nett.setTextColor(
        Helper.accentColor(getContext(), Double.valueOf(headerData.nettIncome)));
    tv_bulan.setText(
        Words.toSentenceCase(getContext().getString(R.string.month)) + " " + headerData.month_name);
    double total_net =
        Double.valueOf(headerData.totalIncome) - Double.valueOf(headerData.totalExpense);
    String total_net_string = total_net < 0 ? "-" + format.toRupiahFormatSimple(Math.abs(total_net))
        : format.toRupiahFormatSimple(Math.abs(total_net));
    int total_nett_color = total_net < 0 ? expenseColor : incomeColor;
    tv_total_balance.setText(total_net_string);
    tv_total_balance.setTextColor(total_nett_color);
    line_progress_top.invalidateDraw();
    line_progress_bot.invalidateDraw();
    behaviorUtil.register(-1);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      if (account != null) {
        presenter.timelineLoadData(account.getIdaccount());
      } else {
        presenter.timelineLoadData(false);
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    if (i > 0) {
      this.account = (Account) this.activity.getSimpleBarData().get(i - 1);
      presenter.timelineLoadData(account.getIdaccount());
    } else {
      account = null;
      presenter.timelineLoadData(false);
    }
  }

  @Override public void onNothingSelected(AdapterView<?> adapterView) {

  }
}
