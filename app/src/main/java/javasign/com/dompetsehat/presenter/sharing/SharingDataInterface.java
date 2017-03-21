package javasign.com.dompetsehat.presenter.sharing;

import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.json.agent;
import javasign.com.dompetsehat.ui.activities.setting.sharing.adapters.AdapterMember;
import javasign.com.dompetsehat.ui.activities.setting.sharing.permission.model.PermissionModel;

/**
 * Created by avesina on 1/9/17.
 */

public interface SharingDataInterface extends MvpView {
  void setData(ArrayList<AdapterMember.Member> members);
  void setAdapterModels(ArrayList<PermissionModel> models);
  void onError();
  void onNext();
  void onComplete();
}
