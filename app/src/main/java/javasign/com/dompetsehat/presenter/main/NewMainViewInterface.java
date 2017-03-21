package javasign.com.dompetsehat.presenter.main;

import java.util.ArrayList;
import javasign.com.dompetsehat.ui.CommonInterface;

/**
 * Created by avesina on 1/19/17.
 */

public interface NewMainViewInterface extends CommonInterface {
  void changeNotif(String message,int count);
  void populateSpinner(ArrayList<?> datas,String[] labels);
  void showNotif(int count,int pos);
}
