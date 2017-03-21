package javasign.com.dompetsehat.ui.fragments.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.presenter.account.AccountInterface;
import javasign.com.dompetsehat.presenter.budget.BudgetInterface;
import javasign.com.dompetsehat.presenter.comission.ComissionInterface;
import javasign.com.dompetsehat.presenter.main.FinplanInterface;
import javasign.com.dompetsehat.presenter.overview.OverviewInterface;
import javasign.com.dompetsehat.presenter.portofolio.PortofolioInterface;
import javasign.com.dompetsehat.presenter.referral.ReferralInterface;
import javasign.com.dompetsehat.presenter.transaction.TransactionsInterface;
import javasign.com.dompetsehat.ui.activities.account.pojo.CellAccount;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;
import javasign.com.dompetsehat.ui.activities.budget.pojo.HeaderBudget;
import javasign.com.dompetsehat.ui.activities.budget.pojo.ScheduleFragmentModel;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.FragmentModel;
import javasign.com.dompetsehat.ui.activities.transaction.pojo.Transaction;
import javasign.com.dompetsehat.ui.fragments.account.adapter.NewManageAccountAdapter;
import javasign.com.dompetsehat.ui.fragments.comission.pojo.Comission;
import javasign.com.dompetsehat.ui.fragments.finplan.pojo.Portofolio;
import javasign.com.dompetsehat.ui.fragments.overview_v1.adapter.Old_AdapterOverviewByCategory;
import javasign.com.dompetsehat.ui.fragments.overview_v1.pojo.Old_OverviewFragmentModel;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvCategoryModel;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvFragmentModel;
import javasign.com.dompetsehat.ui.fragments.timeline.pojo.TimelineView;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;

/**
 * Created by lafran on 10/27/16.
 */

public class BaseFragment extends Fragment
    implements SwipeRefreshLayout.OnRefreshListener, AccountInterface, BudgetInterface,
    ComissionInterface, OverviewInterface, ReferralInterface, TransactionsInterface,
    FinplanInterface, PortofolioInterface {


  public ItemEventHelper itemHelper;

  public void setHasMenuEditor(String title){
    if(itemHelper == null)
      itemHelper = ItemEventHelper.attachToActivity(getActivity(), title);
  }

  @Override public void onPause() {
    if(itemHelper != null){
      itemHelper.hideEditor();
    }
    super.onPause();
  }

  @Override public void onRefresh() {

  }

  @Override public void setTotalBalance(double totalBalance) {

  }

  @Override public void setAdapter(ArrayList<NewManageAccountAdapter.VendorParent> arrayList) {

  }

  @Override public void setSimpleAdapter(ArrayList<CellAccount> products) {

  }

  @Override public void setTotalAccount(int totalAccount) {

  }

  @Override public void deleteSuccess() {

  }

  @Override public void setAdapterBudget(List<Budget> budgets) {

  }

  @Override public void setAdapterHeader(@Nullable HeaderBudget headerBudget) {

  }

  @Override public void setSHModel(AdapterScheduler.SHModel model) {

  }

  @Override public void setBudget(AdapterScheduler.SHModel model, Budget budget) {

  }

  @Override public void setScheduler(List<ScheduleFragmentModel> models) {

  }

  @Override public void setDetailCash(Integer[] ids) {

  }

  @Override public void setMoney(double total, double payed, double pending) {

  }

  @Override public void setListComission(ArrayList<Comission> comissions, boolean is) {

  }

  @Override public void showSnackbar() {

  }

  @Override public void showSnackbar(String message) {

  }

  @Override public void showDialog(Account account,String message) {

  }

  @Override public void setError() {

  }

  @Override public void setListPortofolio(ArrayList<Portofolio> portofolioList,boolean is_have) {

  }

  @Override public void setHeaderCif(String cif, String name) {

  }

  @Override public void setListTransaction(OvCategoryModel models) {

  }

  @Override public void setTotal(double total) {

  }

  @Override public void setNetIncomeLabel(double total_pendapatan, double total_pengeluaran,
      int total_transaksi, ArrayList<Integer> ids) {

  }

  @Override public void setData(String label, ArrayList<Double> data) {

  }

  @Override public void setListOverviewFragmentModel(List<OvFragmentModel> listOverviewNet) {

  }

  @Override public void setChartNetIncome(List<Column> columns, List<AxisValue> axisValues,String name) {

  }

  @Override public void setChartCategory(Map<Category, Double> map) {

  }

  @Override public void setChartLabelValues(String[] labels,float[] values,int min, int max, String label) {

  }

  @Override public void scrollView(int x, int y) {

  }

  @Override public void setAdapter(boolean isConnectIstitusi, String referralCode, String status,
      String[] labels, String[] icons, int[] colors) {

  }

  @Override public void setAdapterReferral(String referralCode, String[] labels, String[] icons,
      int[] colors) {

  }

  @Override public void setAdapterTransaction(List<FragmentModel> fragmentModels, int position, boolean isCardView) {

  }

  @Override public void setTimeline(TimelineView timelineView, boolean isBroadcasted) {

  }

  @Override public void initTags(List<String> tags) {

  }

  @Override public void setTransaction(ArrayList<Transaction> transactions) {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }

  @Override public void setAdapterPlan(ArrayList<Plan> plans) {

  }

  @Override
  public void setCicularProgressBar(double total_budget, double total_budget_belum_tercapai) {

  }
}
