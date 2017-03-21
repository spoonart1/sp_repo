package javasign.com.dompetsehat.presenter.comission;

import java.util.ArrayList;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.ui.fragments.comission.pojo.Comission;

/**
 * Created by avesina on 10/25/16.
 */

public interface ComissionInterface extends MvpView {
  void setMoney(double total,double payed,double pending);
  void setListComission(ArrayList<Comission> comissions,boolean is_have_institusi);
  void showSnackbar();
  void setError();
}
