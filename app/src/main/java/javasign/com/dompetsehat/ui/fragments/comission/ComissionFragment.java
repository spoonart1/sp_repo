package javasign.com.dompetsehat.ui.fragments.comission;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.comission.ComissionInterface;
import javasign.com.dompetsehat.presenter.comission.ComissionPresenter;
import javasign.com.dompetsehat.ui.activities.comission.ComissionActivity;
import javasign.com.dompetsehat.ui.activities.institusi.InstitusiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.ListInstitusiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.VerificationDialog;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.ManulifeHomePageActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivityMyReferral;
import javasign.com.dompetsehat.ui.activities.referral.ReferralLoaderActivity;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.ui.fragments.comission.adapter.AdapterComission;
import javasign.com.dompetsehat.ui.fragments.comission.pojo.Comission;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.view.AccountView;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/19/16.
 */
public class ComissionFragment extends Fragment implements ComissionInterface {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.tv_value) TextView tv_value;
  @Bind(R.id.tv_subscribe) TextView tv_subscribe;
  @Bind(R.id.tv_total_pending) TextView tv_total_pending;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private BlankView bv;
  private BaseActivity activity;
  private boolean is_from_profile = false;
  @Inject ComissionPresenter presenter;
  ArrayList<Comission> comissions = new ArrayList<>();
  View view;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_comission, null);
    ButterKnife.bind(this, view);
    if (getActivity() instanceof NewMainActivityMyReferral) {
      activity = (NewMainActivityMyReferral) getActivity();
    } else if (getActivity() instanceof ReferralLoaderActivity) {
      activity = (ReferralLoaderActivity) getActivity();
    } else if (getActivity() instanceof ComissionActivity) {
      activity = (ComissionActivity) getActivity();
    } else {
      getActivity().finish();
    }
    activity.getActivityComponent().inject(this);
    presenter.attachView(this);

    bv = new BlankView(view, DSFont.Icon.dsf_comission_filled.getFormattedName(),
        getString(R.string.comission_is_empty));

    //bv.showActionButton(getResources().getString(R.string.pelajari_lanjut),
    //    new View.OnClickListener() {
    //      @Override public void onClick(View v) {
    //        Helper.showSimpleInformationDialog(getFragmentManager(), "Tahukah kamu?",
    //            "some texts will appear here");
    //      }
    //    });

    bv.beginLoading(null);

    init();
    return view;
  }

  @OnClick(R.id.tv_disclaimer) void openDisclaimer() {
    Helper.showDisclaimerDialogWithTitle(getFragmentManager(), "Disclaimer",
        getString(R.string.comission_disclaimer_msg_dialog));
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layoutManager);
    if(getActivity().getIntent().getExtras() != null){
      if(getActivity().getIntent().getExtras().getString("from").equalsIgnoreCase("profile")){
        is_from_profile = true;
      }
    }
    presenter.grabData();
  }

  private void setData() {
    setHeader(100000, 8000, 2000);

    List<Comission> comissions = new ArrayList<>();
    //comissions.add(generateComission(50000, 0, Comission.Status.subscribed, "2 Agt 2016"));
    //comissions.add(generateComission(50000, 0, Comission.Status.subscribed, "4 Agt 2016"));
    //comissions.add(generateComission(50000, 0, Comission.Status.subscribed, "6 Agt 2016"));
    //comissions.add(generateComission(0, 150000, Comission.Status.komisi, "8 Agt 2016"));
    //comissions.add(generateComission(3000, 0, Comission.Status.subscribed, "10 Agt 2016"));
    //comissions.add(generateComission(16000, 0, Comission.Status.subscribed, "12 Agt 2016"));
    //comissions.add(generateComission(17000, 0, Comission.Status.subscribed, "14 Agt 2016"));
    //comissions.add(generateComission(0, 36000, Comission.Status.komisi, "16 Sept 2016"));
    //comissions.add(generateComission(2000, 0, Comission.Status.subscribed, "18 Sept 2016"));
    //comissions.add(generateComission(4000, 0, Comission.Status.subscribed, "20 Agt 2016"));

    AdapterComission adapter = new AdapterComission(comissions);
    recyclerView.setAdapter(adapter);

    Helper.checkIfBlank(bv, comissions.isEmpty());
    Helper.trackThis(getActivity(), "User di halaman Komisi");
  }

  @OnClick({ R.id.infoSubscribe, R.id.infoPending }) void openToolTip(View view) {
    switch (view.getId()) {
      case R.id.infoSubscribe:
        Helper.showSimpleInformationDialog(getFragmentManager(), "Apa itu Komisi dibayarkan?",
            "Total komisi yang telah dipindahkan dari dompet kedalam reksa dana. Dana tersebut sudah dapat ditarik sewaktu - waktu sesuai dengan mekanisme penarikan reksa dana pada umumnya.");
        break;

      case R.id.infoPending:
        Helper.showSimpleInformationDialog(getFragmentManager(), "Apa itu Komisi tertahan?",
            "Akumulasi dana yang masih tertahan. Bila nilainya sudah lebih dari Rp 150.000, maka dana tersebut akan otomatis berpindah ke rekening reksa dana Anda. Pemindahan setiap akhir bulan berjalan.");
        break;
    }
  }

  private void setHeader(double totalKomisi, double totalSubscribed, double totalPending) {
    tv_value.setText(format.toRupiahFormatSimple(totalKomisi, true));
    tv_subscribe.setText(format.toRupiahFormatSimple(totalSubscribed, true));
    tv_total_pending.setText(format.toRupiahFormatSimple(totalPending, true));
  }

  private Comission generateComission(double amountDebet, double amountComission,
      Comission.Status from, String date) {
    Comission comission = new Comission();
    comission.debetAmount = amountDebet;
    comission.comissionAmount = amountComission;
    comission.from = from;
    comission.date = date;

    return comission;
  }

  @Override public void setMoney(double total, double payed, double pending) {
    activity.runOnUiThread(() -> {
      setHeader(total, payed, pending);
    });
  }

  @Override public void setListComission(ArrayList<Comission> comissions, boolean is_have) {
    activity.runOnUiThread(() -> {
      this.comissions = comissions;
      AdapterComission adapter = new AdapterComission(comissions);
      recyclerView.setAdapter(adapter);
      Helper.checkIfBlank(bv, comissions.isEmpty());
      if (comissions.isEmpty()) {
        setData();
      }
      if(is_from_profile) {
        if (is_have) {
          if (comissions.isEmpty()) {
            bv.setDesc(
                "Kamu belum memiliki Referral Fee, Untuk memulainya, bagikan Kode Referral agar mendapatkan Referral Fee yang lebih banyak");
            bv.getAditional_btn().setText("Lihat Kode Referrall");
            bv.getAditional_btn().setOnClickListener(view1 -> {
              Helper.goTo(getActivity(), ReferralLoaderActivity.class);
            });
          }
        } else {
          if (comissions.isEmpty()) {
            bv.setDesc(
                "Kamu belum memiliki Referral Fee, Untuk memulainya silahkan sambungkan Akun Reksana Dana Manulife");
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

  @Override public void showSnackbar() {
    Helper.showCustomSnackBar(this.view, LayoutInflater.from(getActivity()),
        getString(R.string.app_need_connection), true,
        ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), Gravity.BOTTOM);
  }

  @Override public void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void setError() {
    Helper.checkIfBlank(bv, this.comissions.isEmpty());
  }
}
