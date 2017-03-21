package javasign.com.dompetsehat.ui.fragments.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.account.AccountPresenter;
import javasign.com.dompetsehat.ui.fragments.account.adapter.NewManageAccountAdapter;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;

/**
 * Created by bastianbentra on 8/9/16.
 */
public class NewManageAccountFragment extends BaseFragment{

  @Bind(R.id.tv_total) TextView tv_total;
  @Bind(R.id.refresh_layout) SwipeRefreshLayout refresh_layout;
  @Bind(R.id.recycleview) RecyclerView recycleview;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private NewManageAccountAdapter adapter;

  @Inject AccountPresenter presenter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_new_manage_account, null);
    ButterKnife.bind(this, view);

    ((BaseActivity)getActivity()).getActivityComponent().inject(this);
    presenter.attachView(this);

    init();

    return view;
  }

  private void init() {
    final LinearLayoutManager layoutManager =
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    recycleview.setLayoutManager(layoutManager);
    refresh_layout.setOnRefreshListener(this);

    setData();
  }

  private void setData() {
    presenter.loadData();
  }

  @Override
  public void setTotalBalance(double total) {
    getActivity().runOnUiThread(() -> {
    String totalBalances = format.toRupiahFormatSimple(total);
    tv_total.setText(totalBalances);
    });
  }

  @Override
  public void setAdapter(ArrayList<NewManageAccountAdapter.VendorParent> arrayList) {
    getActivity().runOnUiThread(() -> {
      adapter = new NewManageAccountAdapter(getActivity(), getContext(), arrayList);
      recycleview.setAdapter(adapter);
      adapter.notifyDataSetChanged();
      Helper.disposeSwiper(refresh_layout);
    });
  }

  @Override public void onRefresh() {
    presenter.loadData();
  }

  @Override public void onDestroy() {
    Helper.disposeSwiper(refresh_layout);
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void onPause() {
    Helper.disposeSwiper(refresh_layout);
    super.onPause();
  }
}
