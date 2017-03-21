package javasign.com.dompetsehat.presenter.trend;

import java.util.List;

import javasign.com.dompetsehat.ui.CommonInterface;
import javasign.com.dompetsehat.ui.activities.trend.pojo.FragmentModel;

/**
 * Created by aves on 8/29/16.
 */

public interface TrendInterface extends CommonInterface {
    void setData(int MENU_ID,List<FragmentModel> fragments);
}
