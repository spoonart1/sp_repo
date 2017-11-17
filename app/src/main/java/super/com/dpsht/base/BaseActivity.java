package javasign.com.dompetsehat.base;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.injection.component.ActivityComponent;
import javasign.com.dompetsehat.injection.component.DaggerActivityComponent;
import javasign.com.dompetsehat.injection.module.ActivityModule;
import javasign.com.dompetsehat.utils.LocaleHelper;
import timber.log.Timber;

/**
 * Created by aves on 8/8/16.
 */

public class BaseActivity extends AppCompatActivity {
  private ActivityComponent activityComponent;

  public ActivityComponent getActivityComponent() {
    if (activityComponent == null) {
      activityComponent = DaggerActivityComponent.builder()
          .activityModule(new ActivityModule(this))
          .applicationComponent(MyCustomApplication.get(this).getApplicationComponent())
          .build();
    }
    return activityComponent;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    LocaleHelper.onCreate(this, "in");
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

  @Override protected void onResume() {
    super.onResume();
    MyCustomApplication.class_name = activityComponent.context().getClass().getSimpleName();
    Timber.e("passcode onresume "+activityComponent.context().getClass().getSimpleName());
    MyCustomApplication.activityResumed(this,MyCustomApplication.class_name);
  }

  @Override protected void onPause() {
    super.onPause();
    MyCustomApplication.class_name = activityComponent.context().getClass().getSimpleName();
    MyCustomApplication.activityPaused(MyCustomApplication.class_name);
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
  }

  public void getKeyHash() {
    try {
      PackageInfo info = getPackageManager().getPackageInfo("javasign.com.dompetsehat",
          PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Timber.d("KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
      }
    } catch (PackageManager.NameNotFoundException e) {
      Timber.e("ERROR " + e);
    } catch (NoSuchAlgorithmException e) {
      Timber.e("ERROR " + e);
    }
  }

}
