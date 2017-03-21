package javasign.com.dompetsehat.injection.module;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import javasign.com.dompetsehat.injection.ActivityContext;

/**
 * Created by pratama on 3/11/16.
 */
@Module
public class ActivityModule {

  private Activity activity;

  public ActivityModule(Activity activity) {
    this.activity = activity;
  }

  @Provides Activity provideActivity() {
    return activity;
  }

  @Provides @ActivityContext
  Context provideContext() {
    return activity;
  }
}
