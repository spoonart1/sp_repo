package javasign.com.dompetsehat.ui.fragments.overview_v1;

import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.main.OverviewPresenter;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.dialogs.MenuDialog;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v1.adapter.Old_AdapterOverviewFragmentPager;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvFragmentModel;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.TourHelper;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by spoonart on 2/1/16.
 */
@Deprecated
public class Old_OverViewFragment extends BaseFragment
    implements AdapterView.OnItemSelectedListener {

  private final int REQUEST_ID = 12;
  private BlankView bv;
  private Old_AdapterOverviewFragmentPager oldAdapterOverviewFragmentPager;
  private int mCurrentSelectedItemDialog = 0;
  private int mCurrentStateFilter = State.FILTER_BY_TRANSACTION;
  private List<OvFragmentModel> mFragmentModels;

  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.pager) ViewPager viewPager;
  @Bind(R.id.tv_filter) TextView tv_filter;
  @Bind(R.id.btn_filter) LinearLayout btn_filter;
  @Bind(R.id.indicator) ImageView arrow;
  @Bind(R.id.main_content) CoordinatorLayout main_content;

  @Inject OverviewPresenter presenter;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.old_fragment_overview, null);
    ButterKnife.bind(this, rootView);

    NewMainActivityMyFin activityMyFin = (NewMainActivityMyFin) getActivity();
    activityMyFin.getActivityComponent().inject(this);
    activityMyFin.setSimpleBarOnSelectedListener(this);
    presenter.attachView(this);

    bv = new BlankView(rootView, DSFont.Icon.dsf_overview_filled.getFormattedName(),
        getString(R.string.reportview_is_empty));

    /*bv.showActionButton(getResources().getString(R.string.pelajari_lanjut), new View.OnClickListener() {
      @Override public void onClick(View v) {
        Helper.showSimpleInformationDialog(getFragmentManager(), "Tahukah kamu?", "some texts will appear here");
      }
    });*/

    TourHelper.init(getActivity())
        .withDefaultButtonEnable(true)
        .setSessionKey(State.FLAG_FRAGMENT_OVERVIEW)
        .setViewsToAttach(rootView, R.id.spinner, R.id.ic_menu)
        .setTourTitles("Filter Data", "Simpan Data")
        .setTourDescriptions("Tampilkan data berdasarkan transaksi maupun kategori.", "Simpan data dalam format pdf/csv.")
        .setGravities(Gravity.CENTER | Gravity.BOTTOM, Gravity.LEFT | Gravity.BOTTOM)
        .create()
        .show();

    bv.beginLoading(null);


    return rootView;
  }

  private void loadDataTransaction() {
    mCurrentSelectedItemDialog = 0;
    switchFragment(getResources().getStringArray(R.array.overview_filter_transaction_labels)[0],State.OVERVIEW_NETT_INCOME); //default
    //switchFragment(getResources().getStringArray(R.array.overview_filter_transaction_labels)[0],State.OVERVIEW_DETAIL_EXPENSE); //default
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loadDataTransaction();
  }

  @OnClick(R.id.btn_filter) void filterBy(View v) {
    String[] menuLabels = null;
    if (mCurrentStateFilter == State.FILTER_BY_TRANSACTION) {
      menuLabels = getResources().getStringArray(R.array.overview_filter_transaction_labels);
    } else {
      menuLabels = getResources().getStringArray(R.array.overview_filter_category_labels);
    }

    MenuDialog dialog = MenuDialog.newInstance("Lihat berdasarkan", menuLabels);
    dialog.show(getFragmentManager(), "filter-dialog");
    dialog.hideOkButton(true);
    dialog.setDefaultCheckedItem(mCurrentSelectedItemDialog);
    dialog.setOnMenuDialogClick(new MenuDialog.OnMenuDialogClick() {
      @Override public void onOkClick(String item, int pos) {

      }

      @Override public void onMenuClick(String label, int pos, Dialog dialog) {
        switchFragment(label, pos);
        mCurrentSelectedItemDialog = pos;
        dialog.dismiss();
      }
    });
  }

  private void switchFragment(String item, int pos) {
    switch (mCurrentStateFilter) {
      case State.FILTER_BY_TRANSACTION:
        setFragmentTransaction(item, pos);
        break;
      case State.FILTER_BY_CATEGORY:
        setFragmentCategory(item, pos);
        break;
    }
  }

  private void setAdapter(List<OvFragmentModel> fragmentModels) {
    mFragmentModels = fragmentModels;
    oldAdapterOverviewFragmentPager =
        new Old_AdapterOverviewFragmentPager(getChildFragmentManager(), getContext(), fragmentModels);

    if (fragmentModels.size() == 1) {
      tablayout.setTabMode(TabLayout.MODE_FIXED);
    } else {
      tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    viewPager.setAdapter(oldAdapterOverviewFragmentPager);
    tablayout.setupWithViewPager(viewPager);
    Helper.checkIfBlank(bv, fragmentModels.isEmpty());
  }

  @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    mCurrentStateFilter = position;
    bv.beginLoading(null);
    switch (position) {
      case State.FILTER_BY_TRANSACTION:
        loadDataTransaction(); //reset to default
        break;
      case State.FILTER_BY_CATEGORY:
        loadDataByCategory();
        break;
    }
  }

  @Override public void onNothingSelected(AdapterView<?> parent) {

  }

  private void setFragmentTransaction(String item, int pos) {
    //if (TextUtils.isEmpty(item)) return;
    //
    //bv.beginLoading(null);
    //GradientDrawable drawable = (GradientDrawable) btn_filter.getBackground();
    //int color = Helper.getAccentColor(getActivity(),pos);
    //tv_filter.setText(item);
    //switch (pos) {
    //  case State.OVERVIEW_NETT_INCOME:
    //    presenter.populateDataNet();
    //    Helper.trackThis(getActivity(), "User memilih filter laporan pemasukan bersih");
    //    break;
    //  case State.OVERVIEW_DETAIL_INCOME:
    //    presenter.populateDataDetailIncome();
    //    Helper.trackThis(getActivity(), "User memilih filter laporan pemasukan saja");
    //    break;
    //  case State.OVERVIEW_DETAIL_EXPENSE:
    //    presenter.populateDataDetailExpense();
    //    Helper.trackThis(getActivity(), "User memilih filter laporan pengeluaran saja");
    //    break;
    //}
    //
    //arrow.setColorFilter(color);
    //drawable.setStroke(CircleShapeView.convertDpToPixel(1, getContext()), color);
    //tv_filter.setTextColor(color);
  }

  private void loadDataByCategory() {
    setFragmentCategory(getResources().getStringArray(R.array.overview_filter_category_labels)[1],
        State.OVERVIEW_DETAIL_EXPENSE - 1); //default position based on MenuDialog label position (0)
  }

  private void setFragmentCategory(String item, int pos) {
    if (TextUtils.isEmpty(item)) return;

    bv.beginLoading(null);
    int state = pos + 1;
    GradientDrawable drawable = (GradientDrawable) btn_filter.getBackground();
    int color = Helper.getAccentColor(getActivity(),state);
    tv_filter.setText(item);

    arrow.setColorFilter(color);
    drawable.setStroke(CircleShapeView.convertDpToPixel(1, getContext()), color);
    tv_filter.setTextColor(color);

    //switch (state) {
    //  case State.OVERVIEW_DETAIL_INCOME:
    //    presenter.populateDataCategoryIncome();
    //    Helper.trackThis(getActivity(),
    //        "User memilih filter laporan pemasukan berdasarkan kategori");
    //    break;
    //
    //  case State.OVERVIEW_DETAIL_EXPENSE:
    //    presenter.populateDataCategoryExpense();
    //    Helper.trackThis(getActivity(),
    //        "User memilih filter laporan pengeluaran berdasarkan kategori");
    //    break;
    //}
  }

  @Override public void setListOverviewFragmentModel(List<OvFragmentModel> listOverviewNet) {
    getActivity().runOnUiThread(() -> setAdapter(listOverviewNet));
  }

  @Override public void scrollView(int x, int y) {
    main_content.scrollTo(0,100);
  }

  @Override public void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
