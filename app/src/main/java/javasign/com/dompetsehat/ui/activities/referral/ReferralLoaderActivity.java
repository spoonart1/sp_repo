package javasign.com.dompetsehat.ui.activities.referral;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.referral.adapter.AdapterReferralLoader;
import javasign.com.dompetsehat.ui.activities.referral.pojo.FragmentReferralLoaderModel;
import javasign.com.dompetsehat.ui.fragments.comission.ComissionFragment;
import javasign.com.dompetsehat.ui.fragments.referral.ReferralFragment;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class ReferralLoaderActivity extends BaseActivity {

  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.pager) ViewPager viewPager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_referral_loader);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    init();
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  private void init() {
    List<FragmentReferralLoaderModel> models = new ArrayList<>();
    models.add(new FragmentReferralLoaderModel(getString(R.string.referral), new ReferralFragment()));
    models.add(new FragmentReferralLoaderModel(getString(R.string.comission), new ComissionFragment()));

    AdapterReferralLoader adapterPager = new AdapterReferralLoader(getSupportFragmentManager(), this, models);
    viewPager.setAdapter(adapterPager);

    tablayout.setupWithViewPager(viewPager);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
