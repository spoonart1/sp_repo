package javasign.com.dompetsehat.ui.activities.institusi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.institusi.adapter.AdapterInstitusi;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.InstitusiConditionDialog;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.VerificationDialog;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.LoginKlikMamiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.ManulifeHomePageActivity;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.view.AccountView;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/22/16.
 */
//DS1AVESYX
//DS1EYJPMX
public class InstitusiActivity extends BaseActivity {

  @Bind(R.id.recycleview) RecyclerView recyclerView;

  private boolean isUserHasAccount = false;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_institusi);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    init();
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    if (new SessionManager(this).ihaveAnInstutitionAccount()) {
      onSkip();
    }
    setData();
  }

  @OnClick(R.id.btn_skip) void onSkip() {
    finish();
  }

  private void setData() {
    List<AdapterInstitusi.Institusi> institusiList = new ArrayList<>();

    institusiList.add(generateItem(AccountView.MNL, DSFont.Icon.dsf_manulife.getFormattedName(),
        getString(R.string.manulife_aset_manajemen),
        ContextCompat.getColor(this, R.color.green_manulife),
        getString(R.string.share_your_referral_code_and_get_profit)));

    AdapterInstitusi adapter = new AdapterInstitusi(institusiList);

    adapter.setOnInstitusiClick((v, institusi) -> {
      promptToVerify(institusi.idVendor);
    });

    recyclerView.setAdapter(adapter);
  }

  private void promptToVerify(int idVendor) {
    if (isUserHasAccount) return;

    AlertDialog alertDialog =
        new AlertDialog.Builder(this).setTitle("Apakah Anda sudah memiliki akun klikMAMI?")
            .setNeutralButton(getString(R.string.cancel), null)
            .setNegativeButton("Belum", (dialog, which) -> {
              VerificationDialog verificationDialog =
                  VerificationDialog.newInstance((dialog2, phone, email) -> {
                    openVendorPage(idVendor, false);
                    dialog2.dismissAllowingStateLoss();
                  });
              verificationDialog.show(getSupportFragmentManager(), "dialog-verify");
            })
            .setPositiveButton("Sudah", (dialog, which) -> {
              openVendorPage(idVendor, true);
            })
            .create();
    alertDialog.show();
  }



  @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    return super.onKeyLongPress(keyCode, event);

  }

  private void openVendorPage(int idVendor, boolean isTryToLogin) {
    switch (idVendor) {
      case AccountView.MNL:
        openExplainerDialog(isTryToLogin);
        break;
    }
  }

  private void openExplainerDialog(boolean isTryToLogin) {
    int manulifeAccent = ContextCompat.getColor(this, R.color.green_manulife);
    AdvancedDialog.ABuilder builder =
        new AdvancedDialog.ABuilder(this, getString(R.string.manulife_comission_reksa_dana),
            ContextCompat.getColor(this, R.color.white)).withBackgroundColor(manulifeAccent)
            .withTitleBold(true)
            .withCustomButtonStyle(manulifeAccent,
                ContextCompat.getColor(this, R.color.yellow_manulife))
            .withRoundedCorner(false)
            .withCustomDescriptionTextColor(Color.WHITE)
            .addText(Gravity.LEFT, getString(R.string.manulife_comission_rules))
            .withSingleFooterButton(getString(R.string.selanjutnya));

    AdvancedDialog dialog = AdvancedDialog.newInstance(builder, null);
    dialog.setDelegate(identifier -> {
      nextAction(isTryToLogin);
    });
    dialog.show(getSupportFragmentManager(), "explainer-dialog");
  }

  private void nextAction(boolean isTryToLogin) {
    if (isUserHasAccount) {
      Helper.goTo(InstitusiActivity.this, ListInstitusiActivity.class);
    } else {
      startActivity(
          new Intent(this, ManulifeHomePageActivity.class).putExtra("action", isTryToLogin));
    }
  }

  private AdapterInstitusi.Institusi generateItem(int idVendor, String icon, String label,
      int color, String longDesc) {
    AdapterInstitusi.Institusi ins = new AdapterInstitusi.Institusi();
    ins.idVendor = idVendor;
    ins.icon = icon;
    ins.label = label;
    ins.color = color;
    ins.longDesc = longDesc;
    return ins;
  }

  @Override public void onBackPressed() {
    //super.onBackPressed();

  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
