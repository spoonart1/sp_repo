package javasign.com.dompetsehat.presenter.plan;

import java.util.List;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;

/**
 * Created by avesina on 10/27/16.
 */

public interface ChooseProductInterface extends MvpView {
  void setListNonIntitusi(Account account,List<Product> list,List<Integer> excluded);
  void setListIntitusi(Account account,List<Product> list,List<Integer> excluded,boolean is_show);
  void getHeaderTextById(int vendor_id);
}
