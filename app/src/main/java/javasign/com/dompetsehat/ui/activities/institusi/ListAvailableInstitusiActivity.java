package javasign.com.dompetsehat.ui.activities.institusi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.comission.ComissionActivity;
import javasign.com.dompetsehat.ui.activities.institusi.adapter.AdapterAvailableInstitusi;
import javasign.com.dompetsehat.ui.activities.portofolio.PortofolioActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.view.AccountView;

/**
 * Created by lafran on 1/18/17.
 */

public class ListAvailableInstitusiActivity extends BaseActivity {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  private AdapterAvailableInstitusi adapterAvailableInstitusi;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_available_institusi);
    getActivityComponent().inject(this);
    ButterKnife.bind(this);
    setTitle(getString(R.string.select_institusi));
    init();
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  private void init() {
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    ArrayList<AdapterAvailableInstitusi.Model> models = new ArrayList<>();
    models.add(new AdapterAvailableInstitusi.Model(getString(R.string.manulife_aset_manajemen),
        AccountView.MNL_COLOR, AccountView.iconVendor.get(AccountView.MNL)));

    adapterAvailableInstitusi = new AdapterAvailableInstitusi(models).setOnItemClick(view -> {
      goToNextActivity();
    });
    recyclerView.setAdapter(adapterAvailableInstitusi);
  }

  private void goToNextActivity() {
    String target = getIntent().getStringExtra("target");
    if (target.equalsIgnoreCase("komisi")) {
      Helper.goTo(this, ComissionActivity.class,getIntent());
    } else {
      Helper.goTo(this, PortofolioActivity.class,getIntent());
    }
  }
}
