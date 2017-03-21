package javasign.com.dompetsehat.ui.activities.transaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.presenter.transaction.TransactionsInterface;
import javasign.com.dompetsehat.presenter.transaction.TransactionsPresenter;
import javasign.com.dompetsehat.ui.activities.account.FinalCreateAccountActivity;
import javasign.com.dompetsehat.ui.activities.account.ManageEachAccountActivity;
import javasign.com.dompetsehat.ui.activities.search.SearchActivity;
import javasign.com.dompetsehat.ui.activities.transaction.adapter.AdapterTransactionFragmentPager;
import javasign.com.dompetsehat.ui.activities.transaction.listfragment.ListTransactionFragment;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.ui.dialogs.MenuDialog;
import javasign.com.dompetsehat.ui.event.DeleteTransactionEvent;
import javasign.com.dompetsehat.ui.fragments.timeline.pojo.TimelineView;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.TourHelper;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;
import timber.log.Timber;
import tourguide.tourguide.TourGuide;

/**
 * Created by bastianbentra on 8/10/16.
 */
public class TransactionsActivity extends BaseActivity implements TransactionsInterface {

  public static String FROM = "from";

  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.pager) ViewPager viewPager;
  @Bind(R.id.tv_title) TextView tv_title;
  @Bind(R.id.radio_group) RadioGroup radioGroup;
  @Bind(R.id.container) View container;

  // avesina
  @Inject TransactionsPresenter presenter;
  @Inject RxBus rxBus;
  @Inject DbHelper db;

  private final int REQUEST_FILTER = 1;

  private String selectedFilter = "All";
  private String selectedTag = "";
  private Integer selectedMonth = null;
  private Integer selectedYear = null;
  private Integer[] selectedIDS = null;
  private Integer selectedAccount = null;
  private String selectedStatus = null;
  private int selectedMenu = R.id.byDate;
  private int currentFilterPos = 0;
  private AdapterTransactionFragmentPager<ListTransactionFragment> tabsPagerAdapter;

  private String[] labelMenuTags;
  Integer[] product_id = null;
  private BlankView bv;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_transactions);

    getActivityComponent().inject(this);
    presenter.attachView(this);

    ButterKnife.bind(this);

    inflateMenu();
    init();
  }

  @Override public void initTags(List<String> tags) {
    runOnUiThread(() -> {
      String[] additionalFilter = getResources().getStringArray(R.array.menu_dialog_labels);
      tags.add(0, getString(R.string.all_transactions));
      tags.add(1, additionalFilter[1]);
      tags.add(2, additionalFilter[2]);
      labelMenuTags = new String[tags.size()];
      labelMenuTags = tags.toArray(labelMenuTags);
      setTitle(labelMenuTags[0]);
    });
  }

  @Override public void setTransaction(ArrayList<Transaction> transactions) {

  }

  public void init() {
    bv = new BlankView(container, DSFont.Icon.dsf_transactions_filled.getFormattedName(),
        getString(R.string.no_transaction));
    bv.beginLoading(null);

    presenter.initTags();
    performFilterByStateFrom();
    initRadioGroupListener();

    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(DeleteTransactionEvent.class)
        .subscribe(new SimpleObserver<DeleteTransactionEvent>() {
          @Override public void onNext(DeleteTransactionEvent deleteTransactionEvent) {
            runOnUiThread(() -> init());
          }
        });
  }

  private void performFilterByStateFrom() {
    Intent intent = getIntent();
    if (!intent.hasExtra(FROM)) {
      finish();
      return;
    }
    if (intent.hasExtra(State.YEAR)) {
      selectedYear = intent.getExtras().getInt(State.YEAR);
    }
    if (intent.hasExtra(State.MONTH)) {
      selectedMonth = intent.getExtras().getInt(State.MONTH);
    }
    if(intent.hasExtra(State.ID_ACCOUNT)){
      selectedAccount = intent.getExtras().getInt(State.ID_ACCOUNT);
    }
    if(intent.hasExtra(State.STATUS)){
      selectedStatus = intent.getExtras().getString(State.STATUS);
    }
    try {
      if (intent.hasExtra(State.IDS_TRANSACTION)) {
        ArrayList<Integer> comps = intent.getExtras().getIntegerArrayList(State.IDS_TRANSACTION);
        selectedIDS = (Integer[]) comps.toArray(new Integer[comps.size()]);
      }
    } catch (Exception e) {
      Timber.e("ERROR " + e);
      selectedIDS = null;
    }
    switch (intent.getExtras().getInt(FROM)) {
      case State.FROM_DETAIL_ACCOUNT:
        ButterKnife.findById(this, R.id.byAccount).setVisibility(View.GONE);
        if (intent.hasExtra(ManageEachAccountActivity.PRODUCT_ID)) {
          product_id =
              new Integer[] { intent.getExtras().getInt(ManageEachAccountActivity.PRODUCT_ID) };
        }
        if (intent.hasExtra(FinalCreateAccountActivity.PRODUCT_IDS)) {
          ArrayList<Integer> mProduct_id =
              intent.getExtras().getIntegerArrayList(FinalCreateAccountActivity.PRODUCT_IDS);
          product_id = new Integer[mProduct_id.size()];
          int index = 0;
          for (int i : mProduct_id) {
            product_id[index] = i;
          }
        }
        Timber.e("avesina product " + product_id);
        if (product_id != null) {
          presenter.performFilterByProduct(REQUEST_FILTER, selectedMenu, selectedFilter,
              selectedTag, product_id);
          //Integer[] finalProduct_id = product_id;
          //radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
          //  selectedMenu = checkedId;
          //  presenter.performFilterByProduct(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
          //      finalProduct_id);
          //  RadioButton btn = ButterKnife.findById(group, checkedId);
          //  Helper.trackThis(this, "user membuka transaksi dan klik filter " + btn.getText());
          //});
        } else {
          finish();
        }
        break;

      case State.FROM_TIMELINE:
        //setTitle(getResources().getString(R.string.transactions));
        presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
            selectedMonth, selectedYear, selectedIDS, false);
        /*radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
          selectedMenu = checkedId;
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS);
          RadioButton btn = ButterKnife.findById(group, checkedId);
          Helper.trackThis(this, "user membuka transaksi dan klik filter "+btn.getText());
        });*/
        break;

      case State.FROM_OVERVIEW_NETT_INCOME:
        presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
            selectedMonth, selectedYear, selectedIDS, false);
        /*radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
          selectedMenu = checkedId;
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS);
          RadioButton btn = ButterKnife.findById(group, checkedId);
          Helper.trackThis(this, "user membuka transaksi dan klik filter "+btn.getText());
        });*/
        break;

      case State.FROM_OVERVIEW_INCOME_TRANSACTION:
        presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
            selectedMonth, selectedYear, selectedIDS, false);
        /*radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
          selectedMenu = checkedId;
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS);
          RadioButton btn = ButterKnife.findById(group, checkedId);
          Helper.trackThis(this, "user membuka transaksi dan klik filter "+btn.getText());
        });*/
        break;

      case State.FROM_OVERVIEW_EXPENSE_TRANSACTION:
        presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
            selectedMonth, selectedYear, selectedIDS, false);
        /*radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
          selectedMenu = checkedId;
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS);
          RadioButton btn = ButterKnife.findById(group, checkedId);
          Helper.trackThis(this, "user membuka transaksi dan klik filter "+btn.getText());
        });*/
        break;

      case State.FROM_OVERVIEW_INCOME_CATEGORY:
        presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
            selectedMonth, selectedYear, selectedIDS, false);
        /*radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
          selectedMenu = checkedId;
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS);
          RadioButton btn = ButterKnife.findById(group, checkedId);
          Helper.trackThis(this, "user membuka transaksi dan klik filter "+btn.getText());
        });*/
        break;

      case State.FROM_OVERVIEW_EXPENSE_CATEGORY:
        presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
            selectedMonth, selectedYear, selectedIDS, false);
        /*radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
          selectedMenu = checkedId;
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS);
          RadioButton btn = ButterKnife.findById(group, checkedId);
          Helper.trackThis(this, "user membuka transaksi dan klik filter "+btn.getText());
        });*/
        break;
      case State.FROM_NOTIFICATION:
        ButterKnife.findById(this, R.id.byAccount).setVisibility(View.GONE);
        presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,selectedAccount,selectedStatus);
        break;
      default:
        finish();
    }
  }

  private void initRadioGroupListener() {
    radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
      selectedMenu = checkedId;
      RadioButton btn = ButterKnife.findById(group, checkedId);
      boolean isFilterByCategory = checkedId == R.id.byCategory;
      Integer[] finalProduct_id = product_id;
      switch (getIntent().getExtras().getInt(FROM)) {
        case State.FROM_DETAIL_ACCOUNT:
          presenter.performFilterByProduct(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              finalProduct_id);
          break;

        case State.FROM_TIMELINE:
          presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS, isFilterByCategory);
          break;

        case State.FROM_OVERVIEW_NETT_INCOME:
          presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS, isFilterByCategory);
          break;

        case State.FROM_OVERVIEW_INCOME_TRANSACTION:
          presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS, isFilterByCategory);
          break;

        case State.FROM_OVERVIEW_EXPENSE_TRANSACTION:
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS, isFilterByCategory);
          break;

        case State.FROM_OVERVIEW_INCOME_CATEGORY:
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS, isFilterByCategory);
          break;

        case State.FROM_OVERVIEW_EXPENSE_CATEGORY:
          presenter.performFilterBy(REQUEST_FILTER, checkedId, selectedFilter, selectedTag,
              selectedMonth, selectedYear, selectedIDS, isFilterByCategory);
          break;
        case State.FROM_NOTIFICATION:
          presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,selectedAccount,selectedStatus);
          break;
      }
      Helper.trackThis(this, "user membuka transaksi dan klik filter " + btn.getText());
    });
  }

  @Override public void setTitle(CharSequence title) {
    tv_title.setText(title);
    ButterKnife.findById(this, R.id.ic_menu).setVisibility(View.GONE);
  }

  @OnClick(R.id.btn_filter) void filterByTag(View view) {
    if (labelMenuTags != null) {
      if (view.getTag() == null) view.setTag(0);
      int currentTagFilter = (int) view.getTag();
      final MenuDialog dialog = MenuDialog.newInstance(getString(R.string.show_by), labelMenuTags);
      dialog.show(getSupportFragmentManager(), "menu-filter-tag");
      dialog.hideOkButton(true);
      dialog.setDefaultCheckedItem(currentTagFilter);
      dialog.setOnMenuDialogClick(new MenuDialog.OnMenuDialogClick() {

        @Override public void onOkClick(String item, int pos) {

        }

        @Override public void onMenuClick(String label, int pos, Dialog dialog1) {
          setTitle(label);

          selectedTag = "";
          if (pos == 0) {
            selectedFilter = "All";
          } else if (pos == 1) {
            selectedFilter = "CR";
          } else if (pos == 2) {
            selectedFilter = "DB";
          } else {
            selectedTag = label;
          }
          if (product_id != null) {
            presenter.performFilterByProduct(REQUEST_FILTER, selectedMenu, selectedFilter,selectedTag, product_id);
          } else {
            presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
                selectedMonth, selectedYear, selectedIDS, false);
          }

          view.setTag(pos);
          dialog.dismissAllowingStateLoss();
        }
      });
    }
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    finish();
  }

  @OnClick(R.id.ic_search) void doSearch(View v) {
    db.markAllCashToReadStatus();
    startActivity(new Intent(this, SearchActivity.class).putExtra(SearchActivity.KEY_FLAG_FROM,
        SearchActivity.FLAG_SET_SEARCH_FOR_TRANSACTION));
  }

  @OnClick(R.id.ic_menu) void openMenu(View v) {
    final MenuDialog dialog = MenuDialog.newInstance(getString(R.string.show_by),
        getResources().getStringArray(R.array.menu_dialog_labels));
    dialog.show(getSupportFragmentManager(), "menu");
    dialog.hideOkButton(true);
    dialog.setDefaultCheckedItem(currentFilterPos);
    dialog.setOnMenuDialogClick(new MenuDialog.OnMenuDialogClick() {

      @Override public void onOkClick(String item, int pos) {
        Timber.e("Heloo");
      }

      @Override public void onMenuClick(String label, int pos, Dialog dialog1) {
        Timber.d("Choosen : %s ", label);
        if (pos == 0) {
          selectedFilter = "All";
        } else if (pos == 1) {
          selectedFilter = Cash.CREDIT;
        } else if (pos == 2) {
          selectedFilter = Cash.DEBIT;
        }

        currentFilterPos = pos;
        Timber.e("Selected " + selectedFilter);
        presenter.performFilterBy(REQUEST_FILTER, selectedMenu, selectedFilter, selectedTag,
            selectedMonth, selectedYear, selectedIDS, false);
        dialog.dismissAllowingStateLoss();
      }
    });
  }

  private void inflateMenu() {
    IconicsTextView ic_menu = ButterKnife.findById(this, R.id.ic_menu);
    ic_menu.setText(DSFont.Icon.dsf_filter.getFormattedName());
  }

  @Override public void onLoad(int requestid) {

  }

  @Override public void onComplete(int requestid) {

  }

  @Override public void onError(int requestid) {

  }

  @Override public void onNext(int requestid) {

  }

  @Override public void setAdapterTransaction(List<FragmentModel> fragmentModels, int position,
      boolean isCardView) {
    runOnUiThread(() -> {
      if (Helper.isEmptyObject(this.tabsPagerAdapter)) {
        this.tabsPagerAdapter = new AdapterTransactionFragmentPager<ListTransactionFragment>(
            getSupportFragmentManager(), this, fragmentModels);
      } else {
        this.tabsPagerAdapter.setListFragment(fragmentModels);
      }
      viewPager.setAdapter(tabsPagerAdapter);
      viewPager.setCurrentItem(position, true);
      tablayout.setupWithViewPager(viewPager);
      viewPager.refreshDrawableState();

      Helper.checkIfBlank(bv, fragmentModels.isEmpty(), v -> startActivity(
          new Intent(TransactionsActivity.this, AddTransactionActivity.class).putExtra(
              AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_ADD)
              .putExtra(AddTransactionActivity.KEY_TRANSACTION_TYPE, selectedFilter)));

      final TourHelper tourHelper = TourHelper.init(this);
      ListTransactionFragment fragment =
          tabsPagerAdapter.getFragmentInPosition(viewPager.getCurrentItem());
      fragment.setOnFragmentAttached(buttonHelper -> {
        if (!tourHelper.isCreated()) {
          buttonHelper.rootView.getViewTreeObserver()
              .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override public void onGlobalLayout() {
                  tourHelper.withDefaultButtonEnable(true)
                      .setSessionKey(State.FLAG_TRANSACTION_ACTIVITY)
                      .setViewsToAttach(buttonHelper.rootView, R.id.btn_filter, R.id.ic_search)
                      .setTourTitles("Urutkan Transaksi", "Cari Transaksi")
                      .setTourDescriptions("Mengurutkan transaksi berdasarkan pilihan",
                          "Cari transaksi yang diinginkan")
                      .setGravities(Gravity.CENTER | Gravity.BOTTOM, Gravity.LEFT | Gravity.BOTTOM)
                      .create()
                      .show();
                  buttonHelper.rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
              });
        }
      });
    });
  }

  @Override public void setTimeline(TimelineView timelineView, boolean isBroadcasted) {

  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void finish() {
    db.markAllCashToReadStatus();
    super.finish();
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
