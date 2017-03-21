package javasign.com.dompetsehat.ui.activities.setting.about;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BaseActivity;
import jp.wasabeef.blurry.Blurry;

/**
 * Created by lafran on 1/3/17.
 */

public class AboutActivity extends BaseActivity {

  @Bind(R.id.tv_version) TextView tv_version;
  @Bind(R.id.background) ImageView background;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    getActivityComponent().inject(this);
    ButterKnife.bind(this);
    GeneralHelper.greenStatusBar(getWindow(), getResources());
    init();
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  @OnClick({R.id.facebook, R.id.twitter, R.id.g_plus, R.id.contact_us}) void doClicks(View view){
    switch (view.getId()){
      case R.id.facebook:
        startActivity(getOpenFacebookIntent());
        break;

      case R.id.twitter:
        startActivity(getOpenTwitterIntent());
        break;

      case R.id.g_plus:
        startActivity(getOpenGPlus());
        break;

      case R.id.contact_us:
        startActivity(getOpenEmail());
        break;
    }
  }

  public Intent getOpenFacebookIntent() {
    String uri = "fb://page/205823366191979";
    try {
      getPackageManager().getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES);
      return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    } catch (Exception e) {
      return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/205823366191979"));
    }
  }

  public Intent getOpenTwitterIntent() {
    String uri = "twitter://user?user_id=460827946";
    try {
      getPackageManager().getPackageInfo("com.twitter.android", PackageManager.GET_ACTIVITIES);
      return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    } catch (Exception e) {
      return new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/dompetsehat"));
    }
  }

  public Intent getOpenGPlus(){
    return new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/101839105638971401281/posts"));
  }

  public Intent getOpenEmail(){
    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
    sendIntent.setType("plain/text");
    sendIntent.setData(Uri.parse("test@gmail.com"));
    sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "info@dompetsehat.com" });
    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "");
    sendIntent.putExtra(Intent.EXTRA_TEXT, "");
    return sendIntent;
  }

  private void init() {
    String version = getString(R.string.version) + " " + BuildConfig.VERSION_NAME;
    tv_version.setText(version);

    Bitmap blurry = BitmapFactory.decodeResource(getResources(), R.drawable.bg_about);
    Blurry.with(this).from(blurry).into(background);
  }
}
