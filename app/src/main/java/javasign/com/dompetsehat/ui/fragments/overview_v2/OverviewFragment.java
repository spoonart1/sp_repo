package javasign.com.dompetsehat.ui.fragments.overview_v2;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.main.OverviewPresenter;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyFin;
import javasign.com.dompetsehat.ui.dialogs.MenuDialog;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.adapter.AdapterOverviewFragmentPager;
import javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment.OvCategoryFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment.OvNettIncomeFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.listfragment.OvTransactionFragment;
import javasign.com.dompetsehat.ui.fragments.overview_v2.pojo.OvFragmentModel;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.TourHelper;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by lafran on 1/25/17.
 */

public class OverviewFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

  public static final int FILTER_SINGLE_MONTHLY = 1;
  public static final int FILTER_SINGLE_YEARLY = 2;
  public static final int FILTER_GROUPLY = 0;

  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.pager) ViewPager viewPager;
  private int selectedFilter = FILTER_GROUPLY;
  private int selectedPosition = State.FILTER_BY_NETT;

  @Inject OverviewPresenter presenter;

  private BlankView bv;
  NewMainActivityMyFin activityMyFin;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_overview, null);
    ButterKnife.bind(this, rootView);
    activityMyFin = (NewMainActivityMyFin) getActivity();
    activityMyFin.getActivityComponent().inject(this);
    activityMyFin.setSimpleBarOnSelectedListener(this);
    activityMyFin.setOnMenuFilterDialogListener(new MenuDialog.OnMenuDialogClick() {
      @Override public void onOkClick(String item, int pos) {

      }

      @Override public void onMenuClick(String label, int pos, Dialog dialog) {
        doFilter(label, pos);
        dialog.dismiss();
      }
    });
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
        .setTourDescriptions("Tampilkan data berdasarkan transaksi maupun kategori.",
            "Simpan data dalam format pdf/csv.")
        .setGravities(Gravity.CENTER | Gravity.BOTTOM, Gravity.LEFT | Gravity.BOTTOM)
        .create()
        .show();

    bv.beginLoading(null);

    Helper.trackThis(getActivity(), "user klik menu laporan");

    return rootView;
  }

  private void doFilter(String label, int position) {
    switch (position) {
      case FILTER_GROUPLY:
        selectedFilter = FILTER_GROUPLY;
        loadDataTransaction(selectedPosition);
        break;
      case FILTER_SINGLE_MONTHLY:
        selectedFilter = FILTER_SINGLE_MONTHLY;
        loadDataTransaction(selectedPosition);
        break;
      case FILTER_SINGLE_YEARLY:
        selectedFilter = FILTER_SINGLE_YEARLY;
        loadDataTransaction(selectedPosition);
        break;
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loadDataTransaction(State.FILTER_BY_NETT); //default
  }

  private void loadDataTransaction(int position) {
    switch (position) {
      case State.FILTER_BY_NETT:
        presenter.populateDataNet(selectedFilter);
        Helper.trackThis(getActivity(), "User memilih filter laporan pemasukan bersih");
        activityMyFin.setVisibilityMenu(View.VISIBLE);
        //setListOverviewFragmentModel(getFragmentModelBy_NETT_INCOME());
        break;
      case State.FILTER_BY_TRANSACTION:
        presenter.populateDataDetail(selectedFilter);
        Helper.trackThis(getActivity(), "User memilih filter laporan pemasukan saja");
        activityMyFin.setVisibilityMenu(View.VISIBLE);
        //setListOverviewFragmentModel(getFragmentModelsBy_TRANSACTION());
        break;
      case State.FILTER_BY_CATEGORY:
        presenter.populateDataCategory(selectedFilter);
        Helper.trackThis(getActivity(), "User memilih filter laporan pemasukan berdasarkan kategori");
        activityMyFin.setVisibilityMenu(View.VISIBLE);
        //setListOverviewFragmentModel(getFragmentModelsBy_CATEGORY());
        break;
    }
  }

  @Override public void setListOverviewFragmentModel(List<OvFragmentModel> models) {
    getActivity().runOnUiThread(() -> {

      if (models == null) {
        System.out.println("OverviewFragment.run: model is null");
        return;
      }

      AdapterOverviewFragmentPager adapterOverviewFragmentPager =
          new AdapterOverviewFragmentPager(getChildFragmentManager(), getContext(), models);

      if (models.size() == 1) {
        tablayout.setTabMode(TabLayout.MODE_FIXED);
      } else {
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
      }

      viewPager.setAdapter(adapterOverviewFragmentPager);
      viewPager.setCurrentItem(models.size() - 1, false);
      tablayout.setupWithViewPager(viewPager);
      Helper.checkIfBlank(bv, models.isEmpty());
    });
  }

  @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    selectedPosition = position;
    if (view instanceof TextView) {
      System.out.println("OverviewFragment.onItemSelected textview");
    } else {
      System.out.println("OverviewFragment.onItemSelected bukan textview");
    }
    loadDataTransaction(position);
  }

  @Override public void onNothingSelected(AdapterView<?> parent) {
    return;
  }

  private void setSelectedTextViewColor(boolean selected) {

  }

  @Override public void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
