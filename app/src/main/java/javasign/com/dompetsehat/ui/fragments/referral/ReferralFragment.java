package javasign.com.dompetsehat.ui.fragments.referral;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.referral.ReferralPresenter;
import javasign.com.dompetsehat.ui.activities.institusi.InstitusiActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyReferral;
import javasign.com.dompetsehat.ui.activities.referral.ReferralLoaderActivity;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.referral.adapter.AdapterReferral;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.TourHelper;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/19/16.
 */
public class ReferralFragment extends BaseFragment {

  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Bind(R.id.tv_referral_code) TextView tv_referral_code;
  @Bind(R.id.tv_referral_status) TextView tv_referral_status;

  private BlankView bv;
  private BaseActivity activity;
  private AdapterReferral adapter;
  private View view;
  @Inject ReferralPresenter presenter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_referral, null);
    ButterKnife.bind(this, view);
    if (getActivity() instanceof NewMainActivityMyReferral) {
      activity = (NewMainActivityMyReferral) getActivity();
    } else {
      activity = (ReferralLoaderActivity) getActivity();
    }
    activity.getActivityComponent().inject(this);
    presenter.attachView(this);

    bv = new BlankView(view, DSFont.Icon.dsf_referral_filled.getFormattedName(),
        getString(R.string.referral_is_empty));

    bv.showActionButton(getString(R.string.connect_to_institution),
        v -> Helper.goTo(getActivity(), InstitusiActivity.class));
    
    bv.beginLoading(null);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recycleview.setLayoutManager(layoutManager);

    presenter.init();
    Helper.trackThis(getActivity(), "user membuka halaman Kode Referral");
    return view;
  }

  @Override public void setAdapter(boolean isConnectIstitusi, String referralCode, String status,
      String[] labels, String[] icons, int[] colors) {
    activity.runOnUiThread(() -> {
      tv_referral_code.setText(referralCode);
      Helper.checkIfBlank(bv, !isConnectIstitusi);
      adapter = new AdapterReferral(labels, icons, colors);
      adapter.setKode_referral(referralCode);
      recycleview.setAdapter(adapter);
      if (status != null) {
        tv_referral_status.setVisibility(View.VISIBLE);
        tv_referral_status.setText(status);
      } else {
        tv_referral_status.setVisibility(View.GONE);
      }
      if(!isConnectIstitusi){
        TourHelper.init(getActivity())
            .withDefaultButtonEnable(true)
            .setSessionKey(State.FLAG_FRAGMENT_REFERRAL)
            .setViewsToAttach(view, R.id.action_btn)
            .setTourTitles("Sambungkan Institusi")
            .setTourDescriptions("Sambungkan dengan institusi untuk mendapatkan Kode Referral.")
            .setGravities(Gravity.CENTER | Gravity.TOP)
            .create()
            .show();
      }

    });
  }

  @Override public void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
