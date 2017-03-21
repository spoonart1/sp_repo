package javasign.com.dompetsehat.presenter.overview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.ui.fragments.overview_v1.adapter.Old_AdapterOverviewByCategory;
import javasign.com.dompetsehat.ui.fragments.overview_v1.pojo.Old_OverviewFragmentModel;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvCategoryModel;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvFragmentModel;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;

/**
 * Created by avesina on 9/22/16.
 */

public interface OverviewInterface extends MvpView {
  void setListTransaction(OvCategoryModel models);
  void setTotal(double total);
  void setNetIncomeLabel(double total_pendapatan,double total_pengeluaran,int total_transaksi,ArrayList<Integer> ids);
  void setData(String label, ArrayList<Double> data);
  void setListOverviewFragmentModel(List<OvFragmentModel> listOverviewNet);
  void setChartNetIncome(List<Column> columns, List<AxisValue> axisValues, String name);
  void setChartCategory(Map<Category,Double> map);
  void setChartLabelValues(String[] labels,float[] values,int min, int max, String label);
  void scrollView(int x,int y);
}
