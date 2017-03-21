package javasign.com.dompetsehat.ui.activities.plan;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.plan.adapter.AdapterAddPlan;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by bastianbentra on 8/18/16.
 */
public class AddPlanActivity extends BundlePlanActivity
    implements AdapterAddPlan.OnNextClickListener {

  @Bind(R.id.recycleview) RecyclerView recyclerView;

  private Class<?>[] classes = new Class<?>[] {
      DanaPensiunActivity.class, DanaDaruratActivity.class, DanaKuliahActivity.class,
      CustomPlanActivity.class
  };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_plan);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    setTitle(getString(R.string.choose_your_plan));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
    tv_title.setTypeface(null, Typeface.BOLD);
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    finish();
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    AdapterAddPlan adapterAddPlan = new AdapterAddPlan(this);
    adapterAddPlan.setOnNextClickListener(this);
    recyclerView.setAdapter(adapterAddPlan);
  }

  @Override public void onClick(View v, int pos) {
    Helper.goTo(this, classes[pos]);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
