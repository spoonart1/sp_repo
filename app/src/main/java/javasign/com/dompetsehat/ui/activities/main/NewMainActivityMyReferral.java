package javasign.com.dompetsehat.ui.activities.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.accountkit.AccountKit;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.iconics.view.IconicsTextView;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.ui.activities.institusi.InstitusiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.RegisterMamiActivity;
import javasign.com.dompetsehat.ui.activities.profile.ProfileActivity;
import javasign.com.dompetsehat.ui.activities.search.SearchActivity;
import javasign.com.dompetsehat.ui.fragments.comission.ComissionFragment;
import javasign.com.dompetsehat.ui.fragments.finplan.listfragment.PortofolioFragment;
import javasign.com.dompetsehat.ui.fragments.more.MoreFragmentReferral;
import javasign.com.dompetsehat.ui.fragments.referral.ReferralFragment;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.State;

/**
 * Created by bastianbentra on 8/19/16.
 */
public class NewMainActivityMyReferral extends ParentMain implements TabHost.OnTabChangeListener {

  @Bind(android.R.id.tabhost) FragmentTabHost mTabHost;
  @Bind(R.id.tv_title) TextView actionBarTitle;
  @Bind(R.id.iv_user_pict) RoundedImageView iv_user_pict;
  //@Bind(R.id.iv_logo) ImageView iv_logo;
  @Bind(R.id.ic_search) IconicsTextView ic_search;
  @Bind(R.id.ic_menu) IconicsTextView ic_menu;
  @Bind(R.id.tab_container) RelativeLayout tab_container;

  private Class<?> classMenuTarget;
  private TextView currentTextChecked = null;

  private String[] unchecked_icons = new String[] {
      DSFont.Icon.dsf_referral_ref.getFormattedName(),
      //DSFont.Icon.dsf_comission.getFormattedName(),
      DSFont.Icon.dsf_portofolio.getFormattedName(),
      DSFont.Icon.dsf_dots_horizontal.getFormattedName()
  };
  private String[] checked_icons = new String[] {
      DSFont.Icon.dsf_referral_filled.getFormattedName(),
      //DSFont.Icon.dsf_comission_filled.getFormattedName(),
      DSFont.Icon.dsf_portofolio_filled.getFormattedName(),
      DSFont.Icon.dsf_dots_horizontal_filled.getFormattedName()
  };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_main_referral);
    ButterKnife.bind(this);

    AccountKit.initialize(this);

    init();
    initFragmentTabView();
  }

  @OnClick(R.id.iv_user_pict) void userPict() {
    startActivity(new Intent(this, ProfileActivity.class));
  }

  @OnClick(R.id.ic_search) void doSearch() {
    startActivity(new Intent(this, SearchActivity.class).putExtra(SearchActivity.KEY_FLAG_FROM,
        SearchActivity.FLAG_SET_SEARCH_FOR_REFERRAL_COMISSION));
  }

  @OnClick(R.id.ic_menu) void addPortofolio() {
    boolean isIconInfo = (boolean) ic_menu.getTag(R.id.btn_info);
    if (isIconInfo) {
      Helper.openInfoDialog(getSupportFragmentManager());
      return;
    }
    startActivity(new Intent(this, getClassMenuTarget()));
  }

  private void init() {
    iv_user_pict.setImageBitmap(getAvatar());
    saveDefaultBarStyle();
    promptToOpenListIntitusi();
  }

  private void promptToOpenListIntitusi() {
    if (!sessionManager.ihaveAnInstutitionAccount()) Helper.goTo(this, InstitusiActivity.class);
  }

  private void initFragmentTabView() {
    mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
    mTabHost.getTabWidget().setDividerDrawable(new ColorDrawable(Color.WHITE));

    setNewTab(ReferralFragment.class, getString(R.string.tab_title_referral_code), unchecked_icons[0]);
    //setNewTab(ComissionFragment.class, getString(R.string.tab_title_comission), unchecked_icons[1]);
    setNewTab(PortofolioFragment.class, getString(R.string.tab_title_portofplio), unchecked_icons[1]);
    setNewTab(MoreFragmentReferral.class, getString(R.string.tab_title_more), unchecked_icons[2]);

    setTab(getIntent().getStringExtra("dummy"));

    setBarStyle(State.FLAG_FRAGMENT_REFERRAL);
  }

  private void setNewTab(Class<?> clss, String label, String iconFont) {
    Bundle b = new Bundle();
    b.putString("key", label);
    mTabHost.addTab(
        mTabHost.newTabSpec(label.toLowerCase()).setIndicator(createTabButton(iconFont, label)),
        clss, b);
    mTabHost.setOnTabChangedListener(this);
  }

  private View createTabButton(String icon, String label) {
    View view = getLayoutInflater().inflate(R.layout.tab_button, null);

    IconicsTextView tvIcon = (IconicsTextView) view.findViewById(R.id.tab_icon);
    TextView tvTabText = (TextView) view.findViewById(R.id.tab_text);

    tvIcon.setText(icon);
    tvTabText.setText(label);

    return view;
  }

  private void setTab(String tagFragment) {
    if (tagFragment == null) return;

    mTabHost.setCurrentTabByTag(tagFragment.toLowerCase());
    onTabChanged(tagFragment.toLowerCase());
  }

  private void setBarStyle(String tagFragment) {
    if (tagFragment == null) return;

    resetStyleToDefault();
    setClassMenuTarget(null);

    if (tagFragment.toLowerCase().equalsIgnoreCase(State.FLAG_FRAGMENT_REFERRAL)) {
      hideViews(R.id.ic_search, R.id.ic_menu);
    } else if (tagFragment.toLowerCase().equalsIgnoreCase(State.FLAG_FRAGMENT_COMISSION)) {
      hideViews(R.id.ic_search);
      ic_menu.setTag(R.id.btn_info, true);
      ic_menu.setText(DSFont.Icon.dsf_information_outline.getFormattedName());
      setClassMenuTarget(WebviewInstutisiActivity.class);
      //changeColor();
    } else if (tagFragment.toLowerCase().equalsIgnoreCase(State.FLAG_FRAGMENT_PORTOFOLIO)) {
      hideViews(R.id.ic_search, R.id.ic_menu);
      setClassMenuTarget(RegisterMamiActivity.class);
    } else if (tagFragment.toLowerCase().equalsIgnoreCase(State.FLAG_FRAGMENT_MORE)) {
      hideViews(R.id.ic_search, R.id.ic_menu);
    }
  }

  private void saveDefaultBarStyle() {
    //iv_logo.setTag(iv_logo.getColorFilter());
    ic_menu.setTag(ic_menu.getCurrentTextColor());
    ic_menu.setTag(R.id.ic_menu, DSFont.Icon.dsf_plus.getFormattedName());
    ic_search.setTag(ic_search.getCurrentTextColor());
    tab_container.setTag(Color.WHITE);
  }

  private void resetStyleToDefault() {
    //iv_logo.setColorFilter((ColorFilter) iv_logo.getTag());

    ic_menu.setTextColor((int) ic_menu.getTag());
    ic_menu.setText((String) ic_menu.getTag(R.id.ic_menu));
    ic_menu.setTag(R.id.btn_info, false);

    ic_search.setTextColor((int) ic_search.getTag());

    tab_container.setBackgroundColor((int) tab_container.getTag());
    GeneralHelper.statusBarColor(getWindow(), getResources().getColor(R.color.grey_800));

    hideViews(R.id.ic_search, R.id.ic_menu);
  }

  private void hideViews(int... resIds) {
    for (int resId : resIds) {
      ButterKnife.findById(this, resId).setVisibility(View.GONE);
    }
  }

  public void setClassMenuTarget(Class<?> classMenuTarget) {
    this.classMenuTarget = classMenuTarget;
  }

  public Class<?> getClassMenuTarget() {
    return classMenuTarget;
  }

  @Override public void onTabChanged(String tabId) {
    animateFragment(getSupportFragmentManager());
    setBarStyle(tabId);
    int idx = mTabHost.getCurrentTab();
    IconicsTextView icon =
        (IconicsTextView) mTabHost.getTabWidget().getChildAt(idx).findViewById(R.id.tab_icon);
    TextView label = (TextView) mTabHost.getTabWidget().getChildAt(idx).findViewById(R.id.tab_text);
    changeTabIcon(icon, label, idx);
    Helper.trackThis(this, "user membuka "+tabId+" di My Referral");
  }

  private void animateFragment(FragmentManager manager) {
    FragmentTransaction transaction = manager.beginTransaction();
    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
  }

  private void changeTabIcon(IconicsTextView target, TextView label, int pos) {
    target.setText(checked_icons[pos]);

    if (currentTextChecked != null) {
      deselectOtherButton(currentTextChecked, pos);
      currentTextChecked = null;
    }

    Helper.animateGrowing(label);
    currentTextChecked = label;
  }

  private void deselectOtherButton(TextView text, int currentPos) {
    for (int i = 0; i < unchecked_icons.length; i++) {
      if (i != currentPos) {
        IconicsTextView icon =
            (IconicsTextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_icon);
        icon.setText(unchecked_icons[i]);
      }
    }

    Helper.animateShrinking(text);
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  private static final int TIME_INTERVAL = 2000;
  private long mBackPressed;

  @Override public void onBackPressed() {
    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
      super.onBackPressed();
      return;
    } else {
      Toast.makeText(getBaseContext(), getString(R.string.press_once_again_to_exit),
          Toast.LENGTH_SHORT).show();
    }

    mBackPressed = System.currentTimeMillis();
  }
}
