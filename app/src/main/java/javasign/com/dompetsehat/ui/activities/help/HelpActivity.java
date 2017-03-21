package javasign.com.dompetsehat.ui.activities.help;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.help.adapter.AdapterHelpFragmentPager;
import javasign.com.dompetsehat.ui.activities.help.listfragment.WebviewFragment;
import javasign.com.dompetsehat.ui.activities.help.pojo.FragmentHelpModel;
import javasign.com.dompetsehat.ui.fragments.feedback.FeedBackFragment;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.UnSwapContentViewPager;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class HelpActivity extends BaseActivity {

  @Bind(R.id.pager) UnSwapContentViewPager viewPager;
  @Bind(R.id.radio_group) RadioGroup radio_group;
  @Bind(R.id.btn_chat) View btn_chat;

  private AdapterHelpFragmentPager adapterHelpFragmentPager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    setTitle(getString(R.string.help));
    init();
  }

  private void navigateToSyarat(){
    radio_group.check(R.id.rb_link);
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.btn_chat) void chatNow() {
    //ConversationActivity.show(this);
    Helper.trackThis(this, "User menekan tombol (floating) live chat");
  }

  public void init() {
    btn_chat.setVisibility(View.GONE);
    List<FragmentHelpModel> models = new ArrayList<>();
    //String doc="<iframe src='http://docs.google.com/viewer?url="+full_link+"'width='100%' height='100%'style='border: none;'></iframe>";
    models.add(new FragmentHelpModel(getString(R.string.manual_book),
        WebviewFragment.newInstance("faq", Words.full_link_pdf)
        .showActionButton(true)
    ));

    models.add(new FragmentHelpModel(getString(R.string.link),
        WebviewFragment.newInstance("tautan",

            true,

            new String[]{
            getString(R.string.term_of_use),
            getString(R.string.privacy_policy),
            getString(R.string.security_practice)
            },

            "https://dompetsehat.com/terms",
            "https://dompetsehat.com/privacy",
            "https://dompetsehat.com/security"
        )
    ));

    models.add(new FragmentHelpModel(getString(R.string.feedback), new FeedBackFragment()));

    adapterHelpFragmentPager =
        new AdapterHelpFragmentPager(getSupportFragmentManager(), this, models);
    viewPager.setAdapter(adapterHelpFragmentPager);

    initButtonListener();

    if(getIntent().hasExtra("syarat")){
      navigateToSyarat();
    }
  }

  public void setViewPagerPosition(int i){
    viewPager.refreshDrawableState();
  }

  private void initButtonListener(){
    radio_group.setOnCheckedChangeListener((group, checkedId) -> {
      int radioButtonID = group.getCheckedRadioButtonId();
      RadioButton radioButton = ButterKnife.findById(group, radioButtonID);
      int idx = group.indexOfChild(radioButton) / 2; //soale ada divider e
      viewPager.setCurrentItem(idx);
      Helper.trackThis(HelpActivity.this, "user klik "+radioButton.getText());
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
