package javasign.com.dompetsehat.ui.activities.search;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.Date;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.presenter.search.SearchInterface;
import javasign.com.dompetsehat.presenter.search.SearchPresenter;
import javasign.com.dompetsehat.presenter.transaction.TransactionsPresenter;
import javasign.com.dompetsehat.ui.activities.transaction.AddTransactionActivity;
import javasign.com.dompetsehat.ui.activities.transaction.adapter.StickyTransactionAdapter;
import javasign.com.dompetsehat.ui.dialogs.MenuDialog;
import javasign.com.dompetsehat.ui.event.DeleteTransactionEvent;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.CustomSpinnerAdapter;
import javax.inject.Inject;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/11/16.
 */
public class SearchActivity extends BaseActivity implements SearchInterface {

  public static final String KEY_FLAG_FROM = "from";

  public static final int FLAG_SET_SEARCH_FOR_TRANSACTION = 0;
  public static final int FLAG_SET_SEARCH_FOR_BUDGET = 1;
  public static final int FLAG_SET_SEARCH_FOR_PLAN = 2;
  public static final int FLAG_SET_SEARCH_FOR_REMINDER = 3;

  public static final int FLAG_SET_SEARCH_FOR_REFERRAL_COMISSION = 11;

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.et_search) EditText et_search;
  @Bind(R.id.btn_close) IconicsTextView btn_close;
  @Bind(R.id.ll_search_more) View ll_search_more;
  @Bind(R.id.sp_account) Spinner sp_account;
  @Bind(R.id.sp_type) Spinner sp_type;
  @Bind(R.id.sp_tag) Spinner sp_tags;
  @Bind(R.id.sp_category) Spinner sp_category;
  @Bind(R.id.tv_start) TextView tv_start;
  @Bind(R.id.tv_end) TextView tv_end;
  @Bind(R.id.ic_back) IconicsTextView ic_back;
  @Bind(R.id.ic_check) IconicsTextView ic_check;

  @Inject SearchPresenter presenter;
  @Inject TransactionsPresenter transactionsPresenter;

  private String chevronUp = DSFont.Icon.dsf_up_chevron_thin.getFormattedName();
  private String chevronDown = DSFont.Icon.dsf_down_chevron_thin.getFormattedName();

  public Integer selectedAccount;
  public String selectedType;
  public String selectedTag;
  public String selectedDateStart;
  public String selectedDateEnd;
  public Integer selectedCategory;
  public String selectedKeyword = "";
  public Date dateStart;
  public Date dateEnd;

  private int searchFlagIntent = -1;
  private boolean onDelete = false;
  private boolean isAdvancedSearchOpened = false;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);

    if (getIntent() != null) {
      searchFlagIntent = getIntent().getIntExtra(KEY_FLAG_FROM, -1);
    }

    init();
    recyclerView.setVisibility(View.GONE);

    if (searchFlagIntent == -1) {
      showSearchOption();
      return;
    } else {
      searchItemBy(searchFlagIntent);
    }
  }

  private void init() {
    Words.setButtonToListen(ButterKnife.findById(this, R.id.ic_search), et_search);
    final StickyHeaderLayoutManager layoutManager = new StickyHeaderLayoutManager();
    recyclerView.setLayoutManager(layoutManager);
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    onBackPressed();
  }

  @OnClick(R.id.ic_search) void doSearch(View v) {
    loadFilter();
    ic_check.setVisibility(View.VISIBLE);
  }

  private void showSearchOption() {
    final MenuDialog menuDialog =
        MenuDialog.newInstance(getString(R.string.search_by), new String[] {
            getString(R.string.transactions), getString(R.string.budget),
            getString(R.string.tab_title_report), getString(R.string.tab_title_plan),
            getString(R.string.reminder_title)
        });
    menuDialog.simpleModeOn();
    menuDialog.setOnMenuDialogClick(new MenuDialog.OnMenuDialogClick() {
      @Override public void onOkClick(String item, int pos) {
        /* leave onOkClick blank if simpleModeOn() */
      }

      @Override public void onMenuClick(String label, int pos, Dialog dialog) {
        searchItemBy(pos);
        menuDialog.dismissAllowingStateLoss();
      }
    });
    menuDialog.show(getSupportFragmentManager(), "menu_dialog");
  }

  @OnClick({ R.id.tv_end, R.id.tv_start }) void openDate(TextView t) {
    t.setError(null);
    if (t.getId() == R.id.tv_end && dateStart == null) {
      tv_start.setError("Pilih Awal tanggal terlebih dahulu");
      return;
    }
    SlideDateTimePicker.Builder builder =
        new SlideDateTimePicker.Builder(getSupportFragmentManager()).setInitialDate(null)
            .setListener(new SlideDateTimeListener() {
              @Override public void onDateTimeSet(Date date) {
                switch (t.getId()) {
                  case R.id.tv_end:
                    dateEnd = date;
                    selectedDateEnd = Helper.setSimpleDateFormat(date, "yyyy-MM-dd");
                    break;
                  case R.id.tv_start:
                    dateStart = date;
                    selectedDateStart = Helper.setSimpleDateFormat(date, "yyyy-MM-dd");
                    break;
                }
                t.setText(Helper.setSimpleDateFormat(date, "yyyy-MM-dd"));
                if (dateStart != null && dateEnd != null) {
                  loadFilter();
                }
              }
            })
            .setIsDateOnly(true);
    if (dateStart != null && t.getId() == R.id.tv_end) {
      builder.setMinDate(dateStart);
    }
    builder.setIs24HourTime(true).setIndicatorColor(Helper.GREEN_DOMPET_COLOR).build().show();
  }

  private void searchItemBy(int flagFrom) {
    switch (flagFrom) {
      case FLAG_SET_SEARCH_FOR_TRANSACTION:
        presenter.loadAdapterTransaction(flagFrom, selectedAccount, selectedType, selectedCategory,
            selectedTag, selectedDateStart, selectedDateEnd);
        break;
      case FLAG_SET_SEARCH_FOR_BUDGET:
        break;
      case FLAG_SET_SEARCH_FOR_PLAN:
        break;
    }
  }

  @Override public void setAdapter(int flagFrom, RecyclerView.Adapter adapter) {
    runOnUiThread(() -> {
      switch (flagFrom) {
        case FLAG_SET_SEARCH_FOR_TRANSACTION:
          new SearchTransaction((AdapterTableResult) adapter);
          break;
        case FLAG_SET_SEARCH_FOR_BUDGET:
          break;
        case FLAG_SET_SEARCH_FOR_PLAN:
          break;
      }
    });
  }

  @Override
  public void setSeachMore(ArrayList<Account> accounts, String[] labelAccounts, String[] labelTypes,
      String[] tags) {
    runOnUiThread(() -> {
      ArrayAdapter accountAdapter =
          new CustomSpinnerAdapter(this, 0, labelAccounts).showIndicator(false);
      ArrayAdapter typeAdapter = new CustomSpinnerAdapter(this, 0, labelTypes).showIndicator(false);
      ArrayAdapter tagsAdapter = new CustomSpinnerAdapter(this, 0, tags).showIndicator(false);
      accountAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
      typeAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
      tagsAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
      sp_account.setAdapter(accountAdapter);
      sp_account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          if (i > 0) {
            selectedAccount = accounts.get(i - 1).getIdaccount();
          } else {
            selectedAccount = null;
          }
          loadFilter();
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView) {
          selectedAccount = null;
        }
      });
      sp_account.setSelection(0);
      sp_type.setAdapter(typeAdapter);
      sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          switch (i) {
            case 1:
              selectedType = Cash.CREDIT;
              presenter.loadSeachCategory(selectedType);
              break;
            case 2:
              selectedType = Cash.DEBIT;
              presenter.loadSeachCategory(selectedType);
              break;
            case 3:
              selectedType = Cash.TRANSFER;
              presenter.loadSeachCategory(selectedType);
              break;
            default:
              selectedType = null;
              presenter.loadSeachCategory(Cash.ALL);
              break;
          }
          loadFilter();
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView) {
          selectedType = null;
        }
      });
      sp_type.setSelection(0);
      sp_tags.setAdapter(tagsAdapter);
      sp_tags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          if (i > 0) {
            selectedTag = tags[i].toLowerCase();
          } else {
            selectedTag = null;
          }
          loadFilter();
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView) {
          selectedTag = null;
        }
      });
    });
  }

  @Override public void setSeachCategory(ArrayList<Category> categories, String[] labelCategories) {
    runOnUiThread(() -> {
      ArrayAdapter categoryAdapter =
          new CustomSpinnerAdapter(this, 0, labelCategories).showIndicator(false);
      sp_category.setAdapter(categoryAdapter);
      sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          if (i > 0) {
            selectedCategory = categories.get(i - 1).getId_category();
            Timber.e("avesina tags " + selectedCategory);
          } else {
            selectedCategory = null;
          }
          loadFilter();
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView) {
          selectedCategory = null;
        }
      });
    });
  }

  @Override public void setSeachTags(String[] labelTags) {

  }

  private void loadFilter() {
    if (ll_search_more.getVisibility() == View.VISIBLE) {
      Timber.e("avesina heloo account "
          + selectedAccount
          + " type "
          + selectedType
          + " datestart "
          + selectedDateStart
          + " end "
          + selectedDateEnd);
      recyclerView.setVisibility(View.VISIBLE);
      presenter.loadAdapterTransaction(FLAG_SET_SEARCH_FOR_TRANSACTION, selectedAccount,
          selectedType, selectedCategory, selectedTag, selectedDateStart, selectedDateEnd);
    }
  }

  @OnClick(R.id.btn_search_more) void doSearchMore() {
    if (ll_search_more.getVisibility() == View.GONE) {
      doOpenSearch();
    } else {
      doCloseSearch();
    }
  }

  private void doOpenSearch() {
    ic_back.setText(GoogleMaterial.Icon.gmd_close.getFormattedName());
    Helper.animateFade(ll_search_more, true, this);
    btn_close.setText(chevronUp);

    presenter.loadSeachMore();
    isAdvancedSearchOpened = true;
  }

  private void doCloseSearch() {
    ic_back.setText(DSFont.Icon.dsf_arrow_back.getFormattedName());
    Helper.animateFade(ll_search_more, false, this);
    btn_close.setText(chevronDown);
    tv_start.setText("---");
    tv_end.setText("---");
    isAdvancedSearchOpened = false;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    reload();
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  private void reload() {
    searchItemBy(FLAG_SET_SEARCH_FOR_TRANSACTION);
  }

  @OnClick(R.id.ll_start) void onClikStart() {
    tv_start.performClick();
  }

  @OnClick(R.id.ll_end) void onClikEnd() {
    tv_end.performClick();
  }

  @Override public void onBackPressed() {
    if (isAdvancedSearchOpened) {
      doCloseSearch();
      return;
    }
    super.onBackPressed();
  }

  protected class SearchTransaction {
    //StickyTransactionAdapter transactionAdapter;
    //ItemEventHelper itemEventHelper = ItemEventHelper.attachToActivity(SearchActivity.this, "");

    AdapterTableResult transactionAdapter;

    SearchTransaction(AdapterTableResult adapter) {
      transactionAdapter = adapter;
      transactionAdapter.setOnClickItem((cash, position) -> seeDetail(cash));
      recyclerView.setAdapter(transactionAdapter);
      et_search.addTextChangedListener(new Words.SimpleTextWatcer() {
        @Override public void afterTextChanged(Editable s) {
          String keyword = "";
          if (!TextUtils.isEmpty(s)) {
            keyword = s.toString();
            recyclerView.setVisibility(View.VISIBLE);
          } else {
            if (ll_search_more.getVisibility() != View.VISIBLE) {
              recyclerView.setVisibility(View.GONE);
            }
          }
          selectedKeyword = keyword;
          transactionAdapter.getFilter().filter(selectedKeyword);
        }
      });
      if (!TextUtils.isEmpty(et_search.getText())) {
        transactionAdapter.getFilter().filter(selectedKeyword);
      }
    }

    private void seeDetail(Cash item) {
      Intent i = new Intent(SearchActivity.this, AddTransactionActivity.class);
      i.putExtra(AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_EDIT);
      i.putExtra(AddTransactionActivity.KEY_CASHFLOW_ID, item.getId());
      startActivityForResult(i, 1);
    }

    private DeleteUtils beginPendingRemoval(Cash item) {
      DeleteUtils deleteUtils = new DeleteUtils().execute(item, transactionsPresenter);
      deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
        @Override public void onCancelRemoving() {
          searchItemBy(FLAG_SET_SEARCH_FOR_TRANSACTION);
        }

        @Override public void onDoneRemoving() {
          reload();
          MyCustomApplication.getRxBus().send(new DeleteTransactionEvent(getApplicationContext()));
        }
      });
      return deleteUtils;
    }

    private void seeTransactionDetail(Cash item) {
      Intent i = new Intent(SearchActivity.this, AddTransactionActivity.class);
      i.putExtra(AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_EDIT);
      i.putExtra(AddTransactionActivity.KEY_CASHFLOW_ID, item.getId());
      startActivityForResult(i, 1);
    }
  }
}
