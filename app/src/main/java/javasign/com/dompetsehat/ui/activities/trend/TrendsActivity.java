package javasign.com.dompetsehat.ui.activities.trend;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.trend.TrendInterface;
import javasign.com.dompetsehat.presenter.trend.TrendsPresenter;
import javasign.com.dompetsehat.ui.activities.search.SearchActivity;
import javasign.com.dompetsehat.ui.activities.trend.adapter.AdapterTrendFragmentPager;
import javasign.com.dompetsehat.ui.activities.trend.listfragment.ListTrendFragmentLineChart;
import javasign.com.dompetsehat.ui.activities.trend.listfragment.ListTrendFragmentPieChart;
import javasign.com.dompetsehat.ui.activities.trend.pojo.FragmentModel;
import javasign.com.dompetsehat.utils.Words;

/**
 * Created by bastianbentra on 8/10/16.
 */
public class TrendsActivity extends BaseActivity implements TrendInterface{

  @Bind(R.id.pager) ViewPager viewPager;
  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.radio_group) RadioGroup radioGroup;

  private AdapterTrendFragmentPager adapterTrendFragmentPager;

  @Inject
  TrendsPresenter presenter;
  int MENU_ID = R.id.byOverall;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_trends);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);

    setTitle(getString(R.string.all_trends));
    init();
    setDefaultCheck();
  }

  private void setDefaultCheck() {
    initFragmentBy(R.id.byOverall);
    radioGroup.check(R.id.byOverall);
  }

  private void init() {
    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        initFragmentBy(checkedId);
      }
    });
  }

  private void initFragmentBy(int checkedId) {
    List<FragmentModel> fragments = null;
    presenter.loadData(checkedId);
    MENU_ID = checkedId;
    switch (checkedId) {
      case R.id.byOverall:
//        fragments = generateFragmentModelsOverall(new String[] { "Minggu ini", "Bulan ini", "Bulan lalu", "3 Bulan lalu" });
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        break;
      case R.id.byCategory:
//        fragments = generateFragmentModelsCategory();
        tablayout.setTabMode(TabLayout.MODE_FIXED);
        break;
    }
//    setFragment(fragments);
  }

  private void setFragment(List<FragmentModel> fragments){
    if (fragments != null) {
      if (adapterTrendFragmentPager == null) {
        adapterTrendFragmentPager = new AdapterTrendFragmentPager(getSupportFragmentManager(), this, fragments);
      }else{
        adapterTrendFragmentPager.setListFragment(fragments);
      }
      viewPager.setAdapter(adapterTrendFragmentPager);
      tablayout.setupWithViewPager(viewPager);
    }
  }

  private List<FragmentModel> generateFragmentModelsOverall(String[] titles) {
    List<FragmentModel> fragments = new ArrayList<>();
    float[] amounFlowIncome = new float[] {
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue())
    };

    float[] amountFlowExpense = new float[] {
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue())
    };

    float[] amountFlowNetIncome = new float[] {
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue()),
        (Words.getRandomValue()), (Words.getRandomValue()), (Words.getRandomValue())
    };
    for (int i = 0; i < titles.length; i++) {
      FragmentModel fm = new FragmentModel(titles[i], ListTrendFragmentLineChart.newInstance(
          presenter.generateTrendModelOverall(amounFlowIncome, amountFlowExpense, amountFlowNetIncome)));

      fragments.add(fm);
    }
    return fragments;
  }

  private List<FragmentModel> generateFragmentModelsCategory() {
    List<FragmentModel> fragments = new ArrayList<>();

    fragments.add(new FragmentModel("Income",
        ListTrendFragmentPieChart.newInstance(presenter.generateTrendModelCategory(10))));

    fragments.add(new FragmentModel("Expense",
        ListTrendFragmentPieChart.newInstance(presenter.generateTrendModelCategory(15))));

    return fragments;
  }


  @Override public void setTitle(CharSequence title) {
    int bgColor = getResources().getColor(R.color.grey_200);
    int textColor = getResources().getColor(R.color.grey_600);
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);

    tv_title.setTextColor(textColor);

    View view = ButterKnife.findById(this, R.id.tab_container);
    view.setBackgroundColor(bgColor);

    IconicsTextView ic_back = ButterKnife.findById(this, R.id.ic_back);
    IconicsTextView ic_search = ButterKnife.findById(this, R.id.ic_search);


    ic_back.setTextColor(textColor);
    ic_search.setTextColor(textColor);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setStatusBarColor(bgColor);
    }

    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    finish();
  }

  @OnClick(R.id.ic_search) void doSearch(View v) {
    startActivity(new Intent(this, SearchActivity.class));
  }

  @Override
  public void onLoad(int RequestID) {

  }

  @Override
  public void onComplete(int RequestID) {

  }

  @Override
  public void onError(int RequestID) {

  }

  @Override
  public void onNext(int RequestID) {

  }

  @Override
  public void setData(int REQ_ID,List<FragmentModel> fragments) {
    if(MENU_ID == REQ_ID){
      setFragment(fragments);
    }
  }
}
