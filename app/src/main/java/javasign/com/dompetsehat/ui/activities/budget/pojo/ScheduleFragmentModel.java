package javasign.com.dompetsehat.ui.activities.budget.pojo;

import android.support.v4.app.Fragment;
import java.util.List;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;

/**
 * Created by lafran on 10/3/16.
 */

public class ScheduleFragmentModel {

  public static final int REPEAT_OFF = 0;
  public static final int REPEAT_ON = 1;
  public String title;
  public int type;
  public Fragment fragment;
  public List<AdapterScheduler.SHModel> shModels;

  public ScheduleFragmentModel(String title, Fragment fragment){
    this.title = title;
    this.fragment = fragment;
  }

}
