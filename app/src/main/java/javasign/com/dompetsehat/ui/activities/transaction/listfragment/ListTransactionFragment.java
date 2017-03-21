package javasign.com.dompetsehat.ui.activities.transaction.listfragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.BaseModel;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.transaction.TransactionsPresenter;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategory;
import javasign.com.dompetsehat.ui.activities.category.listfragment.ListCategoryFragment;
import javasign.com.dompetsehat.ui.activities.transaction.AddTransactionActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.ui.activities.transaction.adapter.StickyTransactionAdapter;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.ui.dialogs.DialogPickCategory;
import javasign.com.dompetsehat.ui.event.AddTransactionEvent;
import javasign.com.dompetsehat.ui.event.ChangeCategoryEvent;
import javasign.com.dompetsehat.ui.event.DeleteTransactionEvent;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/10/16.
 */
public class ListTransactionFragment extends BaseFragment
    implements StickyHeaderLayoutManager.HeaderPositionChangedCallback {

  public interface OnFragmentAttached {
    void onAttached(FloatingButtonHelper buttonHelper);
  }

  protected int color;
  @Bind(R.id.recycleview) RecyclerView recyclerView;

  @Inject TransactionsPresenter presenter;
  @Inject DbHelper db;

  public String fragmentTitle;
  public FloatingButtonHelper buttonHelper;
  public int position;
  public int currentPosition = 0;

  private OnFragmentAttached onFragmentAttached;
  private int justInsertAtPosition = -1;
  private StickyTransactionAdapter adapter;
  private StickyHeaderLayoutManager layoutManager;
  private ArrayList<Transaction> data;
  private BlankView bv;
  private Filter filter;
  private SessionManager session;
  private TransactionsActivity activity;
  private boolean onDelete = false;
  private boolean firsttime = true;

  public static ListTransactionFragment newInstance(int position, String fragmentTitle,
      Filter filter) {
    ListTransactionFragment fragment = new ListTransactionFragment();
    fragment.init(position, fragmentTitle, filter);
    return fragment;
  }

  private void init(int position, String fragmentTitle, Filter filter) {
    this.position = position;
    this.fragmentTitle = fragmentTitle;
    this.filter = filter;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_transaction_list, null);
    ButterKnife.bind(this, view);
    buttonHelper = FloatingButtonHelper.init(getActivity(), view).leaveOnlyPlusButton(v -> {
      if (getActivity() instanceof TransactionsActivity) {
        db.markAllCashToReadStatus();
      }
      int month = TextUtils.isEmpty(filter.month) ? Calendar.getInstance().get(Calendar.MONTH) + 1
          : Integer.valueOf(filter.month);

      int year = TextUtils.isEmpty(filter.year) ? Calendar.getInstance().get(Calendar.YEAR)
          : Integer.valueOf(filter.year);
      startActivityForResult(new Intent(getActivity(), AddTransactionActivity.class).putExtra(
          AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_ADD)
          .putExtra(AddTransactionActivity.KEY_TRANSACTION_TYPE, filter.filter)
          .putExtra(Words.MONTH, month)
          .putExtra(Words.ID, position)
          .putExtra(Words.YEAR, year), 1);
    }).attachToRecyclerview(recyclerView);

    this.activity = (TransactionsActivity) getActivity();
    this.activity.getActivityComponent().inject(this);
    presenter.attachView(this);
    setHasMenuEditor("");
    layoutManager = new StickyHeaderLayoutManager();
    recyclerView.setLayoutManager(layoutManager);
    session = new SessionManager(getContext());
    bv = new BlankView(view, DSFont.Icon.dsf_transactions_filled.getFormattedName(),
        getString(R.string.no_transaction));
    bv.beginLoading(null);
    initData();
    listenRxBus();
    currentPosition = 0;
    return view;
  }

  public void setOnFragmentAttached(OnFragmentAttached onFragmentAttached) {
    this.onFragmentAttached = onFragmentAttached;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (onFragmentAttached != null) {
      onFragmentAttached.onAttached(buttonHelper);
    }
  }

  private void initData() {
    presenter.loadDataTransaction(filter);
  }

  private void shouldScrollTo() {
    final int pos = session.getPosition();
    if (pos != -1) {
      recyclerView.getViewTreeObserver()
          .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
              if (pos > adapter.getItemCount()) return;
              recyclerView.scrollToPosition(pos);
              session.clearPos();
              recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
          });
    }
  }

  @Override public void setAdapterTransaction(List<FragmentModel> fragmentModels, int position,
      boolean isCardView) {

  }

  @Override public void setTransaction(ArrayList<Transaction> transactions) {
    Helper.checkIfBlank(bv, transactions.isEmpty());
    Thread thread = new Thread(() -> {
      ListTransactionFragment.this.data = transactions;
      adapter = new StickyTransactionAdapter(data);
      adapter.setItemEventHelper(itemHelper);
      itemHelper.setOnSecondaryItemClick(new ItemEventHelper.OnSecondaryItemClick<Cash>() {
        @Override
        public void onSecondaryItemCLick(View parentView, View itemView, Cash item, int s, int p) {
          int typetransaction =
              item.getType().equalsIgnoreCase(Cash.CREDIT) ? ListCategoryFragment.INCOME_CATEGORY
                  : ListCategoryFragment.EXPENSE_CATEGORY;
          final DialogPickCategory dialogPickCategory =
              DialogPickCategory.newInstance(item.getType(), item.getType());
          dialogPickCategory.setOnCategoryClick(new AdapterCategory.OnCategoryClick() {
            @Override public void onClickParent(Category category, int section) {
              ListTransactionFragment.this.currentPosition =
                  adapter.getAdapterPositionForSectionHeader(s) + (p + 1);
              presenter.changeCategory(item.getId(), category.getId_category());
              adapter.changeItem(item, category, s, p);
              dialogPickCategory.dismissAllowingStateLoss();
            }

            @Override public void onClickChild(UserCategory userCategory, int position) {
              if (userCategory != null) {
                ListTransactionFragment.this.currentPosition =
                    adapter.getAdapterPositionForSectionHeader(s) + (p + 1);
                presenter.changeUserCategory(item.getId(), userCategory.getParentCategoryId(),
                    userCategory.getId_user_category());
                adapter.changeChidlItem(item, userCategory, s, p);
                dialogPickCategory.dismissAllowingStateLoss();
              }
            }

            @Override public void onLongClickParent(Category category, int section) {

            }
          });
          dialogPickCategory.show(getActivity().getSupportFragmentManager(), "category");
        }
      });
      adapter.setOnClickItem(new ItemEventHelper.OnClickItem<Cash>() {
        @Override public void onClick(View v, Cash item, int section, int position) {
          seeDetail(item);
        }

        @Override public void onLongPressed(View v, final Cash item, int section, int position) {
          itemHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {
            @Override public void onEdit() {
              seeDetail(item);
            }

            @Override public void onDelete() {
              if (!onDelete) {
                onDelete = true;
                adapter.remove(item);
                final DeleteUtils deleteUtils = beginPendingRemoval(item);
                Helper.deleteWithConfirmation(v, getActivity(), v1 -> deleteUtils.cancelRemoval(),
                    new BaseTransientBottomBar.BaseCallback<Snackbar>() {
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
      recyclerView.scrollToPosition(currentPosition);
      layoutManager.setHeaderPositionChangedCallback(this);
      Helper.checkIfBlank(bv, data.isEmpty());
      if (!data.isEmpty()) {
        shouldScrollTo();
      }
    });
    thread.run();
  }

  private DeleteUtils beginPendingRemoval(Cash item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, presenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        initData();
      }

      @Override public void onDoneRemoving() {
        MyCustomApplication.getRxBus()
            .send(
                new DeleteTransactionEvent(getActivity().getApplicationContext(), data.isEmpty()));
      }
    });
    return deleteUtils;
  }

  private void seeDetail(Cash item) {
    Intent i = new Intent(getActivity(), AddTransactionActivity.class);
    i.putExtra(AddTransactionActivity.KEY_TYPE, AddTransactionActivity.TYPE_EDIT);
    i.putExtra(AddTransactionActivity.KEY_CASHFLOW_ID, item.getId());
    if (filter != null) {
      if (filter.month != null) {
        i.putExtra(Words.MONTH, Integer.valueOf(filter.month));
      }
      if (filter.year != null) {
        i.putExtra(Words.YEAR, Integer.valueOf(filter.year));
      }
    }
    startActivityForResult(i, 1);
  }

  private void listenRxBus() {
    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(AddTransactionEvent.class)
        .subscribe(new SimpleObserver<AddTransactionEvent>() {
          @Override public void onNext(AddTransactionEvent addTransactionEvent) {
            if (addTransactionEvent.justAddNew) {
              find("rxbus", addTransactionEvent);
              initData();
            } else {
              initData();
            }
          }
        });

    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(DeleteTransactionEvent.class)
        .subscribe(new SimpleObserver<DeleteTransactionEvent>() {
          @Override public void onNext(DeleteTransactionEvent deleteTransactionEvent) {
            initData();
          }
        });
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == State.FLAG_DATA_NEED_UPDATE) {
      initData();
      adapter.notifyDataSetChanged();
    }
  }

  int o = 0;

  private void find(String from, AddTransactionEvent event) {
    o++;
    System.out.println("ListTransactionFragment.find eventPos: "
        + event.identifier
        + " | fragment pos: "
        + position
        + " from: "
        + from);
    if (event.identifier != position) return;

    Cash cashJustNow = event.cash;
    int section = adapter.getSectionByCashDate(cashJustNow.getCash_date());
    if (section != -1) {
      justInsertAtPosition = adapter.getAdapterPositionForSectionHeader(section);
      System.out.println("ListTransactionFragment.find just insert at pos: -> "
          + justInsertAtPosition
          + " times : "
          + o);
      session.savePosition(justInsertAtPosition);
    }
  }

  @Override public void onHeaderPositionChanged(int sectionIndex, View header,
      StickyHeaderLayoutManager.HeaderPosition oldPosition,
      StickyHeaderLayoutManager.HeaderPosition newPosition) {
    if (sectionIndex == 0 || newPosition != StickyHeaderLayoutManager.HeaderPosition.NATURAL) {
      header.setBackgroundColor(ContextCompat.getColor(activity, R.color.grey_200));
    } else {
      header.setBackgroundColor(Color.WHITE);
    }
  }

  public static class Filter {

    public static final int FILTER_BY_DATE = 0;
    public static final int FILTER_BY_CATEGORY = 1;
    public static final int FILTER_BY_ACCOUNT = 2;
    public static final int FILTER_BY_PRODUCT = 3;

    public String month;
    public String year;

    public String filter, tag;
    public Integer[] product_id;
    public Integer filter_tahun;
    public Integer filter_bulan;
    public Integer[] ids;
    public Integer category_id;
    public Integer account_id;
    public String status;
    public int filterBy = FILTER_BY_DATE;

    public Filter(String month, String year, String filter, String tag, Integer[] product_id,Integer account_id,String status,
        Integer filter_bulan, Integer filter_tahun, Integer[] ids) {
      this.month = month;
      this.year = year;
      this.filter = filter;
      this.tag = tag;
      this.product_id = product_id;
      this.filter_tahun = filter_tahun;
      this.filter_bulan = filter_bulan;
      this.account_id = account_id;
      this.status = status;
      this.ids = ids;
    }
  }

  @Override public void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
