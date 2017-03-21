package javasign.com.dompetsehat.presenter.tag;

import java.util.ArrayList;
import javasign.com.dompetsehat.base.MvpView;

/**
 * Created by avesina on 2/2/17.
 */

public interface TagInterface extends MvpView {
  void setTags(ArrayList<String> tags);
  void finishDelete();
  void finishUpdate();
  void onError(String msg);
}
