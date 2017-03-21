package javasign.com.dompetsehat.ui.fragments.finplan.listfragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.portofolio.PortofolioPresenter;
import javasign.com.dompetsehat.ui.activities.account.ManageEachAccountActivity;
import javasign.com.dompetsehat.ui.activities.institusi.InstitusiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.ListInstitusiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.VerificationDialog;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.LoginKlikMamiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.ManulifeHomePageActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyReferral;
import javasign.com.dompetsehat.ui.activities.portofolio.DetailPortofolioActivity;
import javasign.com.dompetsehat.ui.activities.portofolio.PortofolioActivity;
import javasign.com.dompetsehat.ui.activities.referral.ReferralLoaderActivity;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.ui.fragments.base.BaseFragment;
import javasign.com.dompetsehat.ui.fragments.finplan.adapter.AdapterPortofolio;
import javasign.com.dompetsehat.ui.fragments.finplan.pojo.Portofolio;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.NetworkUtil;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.TourHelper;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.AccountView;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;
import toan.android.floatingactionmenu.FloatingActionsMenu;

/**
 * Created by bastianbentra on 8/19/16.
 */
public class PortofolioFragment extends BaseFragment {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.tv_user_name) TextView tv_user_name;
  @Bind(R.id.tv_cif) TextView tv_cif;
  @Bind(R.id.ll_header) LinearLayout ll_header;
  @Bind(R.id.floating_action_menu) FloatingActionsMenu actionsMenu;
  @Bind(R.id.tv_subtitle) TextView tv_subtitle;

  View view;
  private BlankView bv;
  private boolean is_from_profile = false;
  private BaseActivity activity;
  private List<Portofolio> portofolioList = new ArrayList<>();
  @Inject PortofolioPresenter presenter;
  FloatingButtonHelper floatingButtonHelper;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    this.view = inflater.inflate(R.layout.fragment_portofolio, null);
    ButterKnife.bind(this, view);

    floatingButtonHelper = FloatingButtonHelper.init(getActivity(), view).leaveOnlyPlusButton(v -> {
      startActivity(new Intent(getActivity(), WebviewInstutisiActivity.class));
      Helper.trackThis(getActivity(), "user klik tombol + dihalaman portofolio");
    });

    if (getActivity() instanceof PortofolioActivity) {
      activity = (PortofolioActivity) getActivity();
    } else if (getActivity() instanceof NewMainActivityMyReferral) {
      activity = (NewMainActivityMyReferral) getActivity();
    }
    activity.getActivityComponent().inject(this);
    presenter.attachView(this);
    bv = new BlankView(view, DSFont.Icon.dsf_portofolio_filled.getFormattedName(),getString(R.string.referral_is_empty));
    bv.showActionButton(getString(R.string.connect_to_institution),
        v -> Helper.goTo(getActivity(), InstitusiActivity.class));
    bv.beginLoading(null);
    init();
    return view;
  }

  private void init() {
    if(getActivity().getIntent().getExtras()!= null){
      Intent i = getActivity().getIntent();
      if(i.hasExtra("from")) {
        if (i.getExtras().getString("from").equalsIgnoreCase("profile")) {
          is_from_profile = true;
        }
      }
    }
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    ll_header.setVisibility(View.GONE);
    actionsMenu.setVisible(false);
    tv_subtitle.setText("Per " + Helper.setSimpleDateFormat(Calendar.getInstance().getTime(),
        "dd MMM yyyy, HH:mm"));
    if (NetworkUtil.getConnectivityStatus(getActivity()) > 0) {
      presenter.setAdapterDirectly();
    } else {
      presenter.setAdapter();
    }
  }

  private Portofolio generatePortofolio(String title, double saldo, int jumlahUnit,
      double costPerUnit, String date) {
    Portofolio p = new Portofolio();
    p.title = title;
    p.totalSaldoAkhir = saldo;
    p.jumlahUnit = jumlahUnit;
    p.costPerUnit = costPerUnit;
    p.date = date;
    return p;
  }

  @Override public void setListPortofolio(ArrayList<Portofolio> portofolioList,boolean is_have) {
    activity.runOnUiThread(() -> {
      this.portofolioList = portofolioList;
      AdapterPortofolio adapter = new AdapterPortofolio(this.portofolioList);
      adapter.setOnPortofolioClick((v, portofolio, pos) -> startActivity(
          new Intent(getContext(), DetailPortofolioActivity.class).putExtra("cif", portofolio.cif)
              .putExtra("tanggal", tv_subtitle.getText())
              .putExtra("name", portofolio.name)
              .putExtra("title", portofolio.title)
              .putExtra("saldo", portofolio.totalSaldoAkhir)
              .putExtra("jumlah-unit", portofolio.jumlahUnit)
              .putExtra("cost-per-unit", portofolio.costPerUnit)
              .putExtra("category", portofolio.category)
              .putExtra("date", portofolio.date)));
      recyclerView.setAdapter(adapter);
      Helper.checkIfBlank(bv, this.portofolioList.isEmpty());
      actionsMenu.setVisible(is_have);
      if(is_have) {
        bv.setDesc(getString(R.string.portofolio_is_empty));
        bv.hideActionButton();
      }

      /*if (portofolioList.isEmpty()) {
        view.getViewTreeObserver()
            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override public void onGlobalLayout() {
                TourHelper.init(getActivity())
                    .withDefaultButtonEnable(true)
                    .setSessionKey(State.FLAG_FRAGMENT_PORTOFOLIO)
                    .setViewsToAttach(view, R.id.floating_action_menu)
                    .setTourTitles("Tambahkan Portofolio")
                    .setTourDescriptions("Menambahkan data dari institusi yang terhubung")
                    .setGravities(Gravity.TOP | Gravity.LEFT)
                    .create()
                    .show();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
              }
            });
      }*/
      actionsMenu.setVisible(this.portofolioList.isEmpty());
      if(is_from_profile) {
        if (is_have) {
            actionsMenu.setVisible(this.portofolioList.isEmpty());
        } else {
          if (portofolioList.isEmpty()) {
            bv.setDesc(
                "Kamu belum memiliki Portofolio, Untuk memulainya silahkan sambungkan Akun Reksana Dana Manulife");
            bv.getAditional_btn().setText("Sambungkan Sekarang");
            bv.getAditional_btn().setOnClickListener(view1 -> {
              AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle(
                  "Apakah Anda sudah memiliki akun klikMAMI?")
                  .setNeutralButton(getString(R.string.cancel), null)
                  .setNegativeButton("Belum", (dialog, which) -> {
                    VerificationDialog verificationDialog = VerificationDialog.newInstance((dialog2, phone, email) -> {
                      openVendorPage(10, false);
                      dialog2.dismissAllowingStateLoss();
                    });
                    verificationDialog.show(getActivity().getSupportFragmentManager(),
                        "dialog-verify");
                  })
                  .setPositiveButton("Sudah", (dialog, which) -> {
                    openVendorPage(10, true);
                  })
                  .create();
              alertDialog.show();
            });
          }
        }
      }
    });
  }

  private void openVendorPage(int idVendor, boolean isTryToLogin) {
    switch (idVendor) {
      case AccountView.MNL:
        openExplainerDialog(isTryToLogin);
        break;
    }
  }

  private void openExplainerDialog(boolean isTryToLogin) {
    int manulifeAccent = ContextCompat.getColor(getActivity(), R.color.green_manulife);
    AdvancedDialog.ABuilder builder =
        new AdvancedDialog.ABuilder(getActivity(), getString(R.string.manulife_comission_reksa_dana),
            ContextCompat.getColor(getActivity(), R.color.white)).withBackgroundColor(manulifeAccent)
            .withTitleBold(true)
            .withCustomButtonStyle(manulifeAccent,
                ContextCompat.getColor(getActivity(), R.color.yellow_manulife))
            .withRoundedCorner(false)
            .withCustomDescriptionTextColor(Color.WHITE)
            .addText(Gravity.LEFT, getString(R.string.manulife_comission_rules))
            .withSingleFooterButton(getString(R.string.selanjutnya));

    AdvancedDialog dialog = AdvancedDialog.newInstance(builder, null);
    dialog.setDelegate(identifier -> {
      nextAction(isTryToLogin);
    });
    dialog.show(getActivity().getSupportFragmentManager(), "explainer-dialog");
  }

  private void nextAction(boolean isTryToLogin) {
    if (false) {
      Helper.goTo(getActivity(), ListInstitusiActivity.class);
    } else {
      startActivity(
          new Intent(getActivity(), ManulifeHomePageActivity.class).putExtra("action", isTryToLogin));
    }
  }

  @Override public void setHeaderCif(String cif, String name) {
    super.setHeaderCif(cif, name);
    activity.runOnUiThread(() -> {
      tv_cif.setText("No CIF: " + cif);
      tv_user_name.setText(name);
      ll_header.setVisibility(View.VISIBLE);
    });
  }

  @Override public void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void showSnackbar(String message) {
    super.showSnackbar();
    Helper.showCustomSnackBar(this.view, LayoutInflater.from(getActivity()), message, true,
        ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), Gravity.BOTTOM);
  }

  @Override public void showDialog(Account account, String message) {
    super.showDialog(account, message);
    if (account != null) {
      new AlertDialog.Builder(getActivity()).setTitle(message)
          .setNegativeButton(getString(R.string.skip), (dialog, which) -> {
            dialog.dismiss();
          })
          .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            Intent i = new Intent(getActivity(), LoginKlikMamiActivity.class);
            i.putExtra(Words.ID_VENDOR, account.getIdvendor());
            i.putExtra(ManageEachAccountActivity.MODE_EDIT, true);
            i.putExtra(Words.ACCOUNT_ID, account.getIdaccount());
            startActivity(i);
          })
          .create()
          .show();
    }
  }

  @OnClick(R.id.btn_invest) void doInvest() {
    startActivity(new Intent(getActivity(), WebviewInstutisiActivity.class));
  }
}
