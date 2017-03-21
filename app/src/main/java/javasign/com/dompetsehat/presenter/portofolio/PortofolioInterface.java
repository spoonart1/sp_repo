package javasign.com.dompetsehat.presenter.portofolio;

import java.util.ArrayList;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.ui.fragments.finplan.pojo.Portofolio;

/**
 * Created by avesina on 10/25/16.
 */

public interface PortofolioInterface extends MvpView {
  void setListPortofolio(ArrayList<Portofolio> portofolioList,boolean is_have);
  void setHeaderCif(String cif,String name);
  void showSnackbar(String message);
  void showDialog(Account account,String message);
}
