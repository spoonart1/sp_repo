package javasign.com.dompetsehat.ui.activities.setting.sharing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import javasign.com.dompetsehat.base.BaseActivity;

/**
 * Created by lafran on 1/4/17.
 */

public class SharingDataPermissionActivity extends BaseActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
  }
}
