package javasign.com.dompetsehat.presenter.setting;

import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.ui.activities.setting.adapter.AdapterSetting;

/**
 * Created by avesina on 11/1/16.
 */

public interface SettingInterface extends MvpView {
  void startLoading(String message);
  void error();
  void complete(String message);
  void onnext(String message);
  void setAdapter(List<AdapterSetting.SettingModel> settingModels);
  void setSpinner(ArrayList<Account> arrayLists,String[] labels);
  void setPath(String path);
}
