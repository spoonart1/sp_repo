package javasign.com.dompetsehat.ui.fragments.debts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Debt;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.json.debt;
import javasign.com.dompetsehat.presenter.debts.DebtsInterface;
import javasign.com.dompetsehat.presenter.debts.DebtsPresenter;
import javasign.com.dompetsehat.services.InsertDataService;
import javasign.com.dompetsehat.ui.activities.debts.AddDebtActivity;
import javasign.com.dompetsehat.ui.activities.debts.DebtsActivity;
import javasign.com.dompetsehat.ui.activities.reminder.AddReminderActivity;
import javasign.com.dompetsehat.ui.activities.reminder.ReminderActivity;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by avesina on 2/17/17.
 */

public class ListDebtFragment extends Fragment
    implements DebtsInterface, SwipeRefreshLayout.OnRefreshListener {
  public String title;
  public DebtsActivity activity;
  private BlankView bv;
  @Inject DebtsPresenter presenter;
  @Bind(R.id.tv_total_balance) TextView tv_total_balance;
  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Bind(R.id.tv_subtitle) TextView tv_subtitle;
  public String type = "all";
  public static final String TYPE_LEND = "lend";
  public static final String TYPE_BORROW = "borrow";
  @Bind(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
  public int REQUEST_ADD = 123;
  private ItemEventHelper<Debt> itemEventHelper;

  public static ListDebtFragment newInstance(String title, String type) {
    Bundle args = new Bundle();
    ListDebtFragment fragment = new ListDebtFragment();
    fragment.setArguments(args);
    fragment.title = title;
    fragment.type = type;
    return fragment;
  }

  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list_debts, null);
    ButterKnife.bind(this, view);
    this.activity = (DebtsActivity) getActivity();
    this.activity.getActivityComponent().inject(this);
    presenter.attachView(this);
    itemEventHelper = ItemEventHelper.attachToActivity(getActivity(), "");
    init(view);
    return view;
  }

  private void init(View view) {
    FloatingButtonHelper.init(getActivity(), view)
        .leaveOnlyPlusButton(v -> startActivityForResult(
            new Intent(getActivity(), AddDebtActivity.class).putExtra(AddDebtActivity.TAG_MODE,
                AddDebtActivity.TAG_MODE_ADD).putExtra(AddDebtActivity.TAG_TYPE, type),
            REQUEST_ADD))
        .attachToRecyclerview(recycleview);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recycleview.setLayoutManager(layoutManager);
    refreshLayout.setOnRefreshListener(this);
    refreshLayout.setColorSchemeColors(Helper.GREEN_DOMPET_COLOR);
    String name = getString(R.string.borrow);
    String desc = getString(R.string.no_debt);
    if(type.equalsIgnoreCase(ListDebtFragment.TYPE_LEND)){
      name = getString(R.string.lend);
      desc = desc.replace("hutang","piutang");
    }
    bv = new BlankView(view, DSFont.Icon.dsf_pot_money.getFormattedName(),desc);
    bv.beginLoading(null);
    tv_subtitle.setText("Total "+name.substring(0, 1).toUpperCase() + name.substring(1));
    presenter.loadData(type);
  }

  @Override public void setAdapter(ArrayList<Debt> debts) {
    activity.runOnUiThread(() -> {
      Helper.disposeSwiper(refreshLayout);
      itemEventHelper.hideMenu(R.id.btn_edit);
      itemEventHelper.setAdapterOnClickItem(new ItemEventHelper.OnClickItem<Debt>() {
        @Override public void onClick(View v, Debt item, int section, int postion) {

        }

        @Override public void onLongPressed(View v, Debt item, int section, int postion) {
          itemEventHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {
            @Override public void onDelete() {
              super.onDelete();
              presenter.deleteDebt(item.getId());
            }
          });
        }
      });
      AdapterDebts adapter = new AdapterDebts(debts).setItemEventHelper(itemEventHelper);
      recycleview.setAdapter(adapter);
      Helper.checkIfBlank(bv, debts.isEmpty());
    });
  }

  @Override public void setTotal(double total) {
    activity.runOnUiThread(() -> {
      tv_total_balance.setText(new RupiahCurrencyFormat().toRupiahFormatSimple(total));
    });
  }

  @Override public void finish() {

  }

  @Override public void snackBar(String message) {

  }

  @Override public void onRefresh() {
    if (refreshLayout != null) {
      presenter.loadData(type);
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (REQUEST_ADD == requestCode) {
      refreshLayout.setRefreshing(true);
      presenter.loadData(type);
    }
  }

  @Override public void setSpinnerProduct(ArrayList<Product> products, String[] labels) {

  }

  @Override public void setSpinnerCashflow(ArrayList<Cash> cashes, String[] labels) {

  }
}
