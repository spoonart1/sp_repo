package javasign.com.dompetsehat.ui.activities.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.account.AccountPresenter;
import javasign.com.dompetsehat.ui.activities.account.base.BaseAccountActivity;
import javasign.com.dompetsehat.ui.activities.account.pojo.CellAccount;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.VerificationDialog;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.LoginKlikMamiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.ManulifeHomePageActivity;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.ui.fragments.account.adapter.NewManageAccountAdapter;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/18/16.
 */
public class AddAccountActivity extends BaseAccountActivity {

  @Bind(R.id.recycleview) RecyclerView recyclerView;

  private NewManageAccountAdapter adapter;

  @Inject AccountPresenter presenter;
  @Inject SessionManager session;
  public final static String IS_REGISTER_HIDDEN = "is_register_hidden";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_accounts_list);
    ButterKnife.bind(this);
    setTitle(getString(R.string.add_account));
    getActivityComponent().inject(this);
    presenter.attachView(this);

    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    presenter.loadAddData();
  }

  @Override public void setAdapter(ArrayList<NewManageAccountAdapter.VendorParent> arrayList) {
    adapter = new NewManageAccountAdapter(this, AddAccountActivity.this, arrayList);
    adapter.forceRightIconWithChevronArrow(true, true, true);
    adapter.setAddAccountClick((view, childListItem) -> {
      CellAccount cell = (CellAccount) childListItem;
      Timber.e("avesina keren tiada tara"+cell.getType());
      Bundle b = new Bundle();
      switch (cell.getType()) {
        case AccountPresenter.SECTION_BANK:
          b.putInt(Words.ID_VENDOR, cell.getVendor().getId());
          b.putString(Words.NAMA_VENDOR, cell.getVendor().getVendor_name());
          //if(cell.getVendor().getId() == 2){
          //  showSnackbar("BCA");
          //  return;
          //}else if(cell.getVendor().getId() == 3){
          //  showSnackbar("BNI");
          //  return;
          //}
          startActivityForResult(
              new Intent(AddAccountActivity.this, AddAccountBankActivity.class).putExtras(b),
              State.FLAG_ACTIVITY_WILL_FINISH_LATER);
          break;
        case AccountPresenter.SECTION_DOMPET:
          b.putInt(Words.ID_VENDOR, cell.getVendor().getId());
          b.putString(Words.NAMA_VENDOR, cell.getVendor().getVendor_name());
          b.putBoolean("add-mode", true);
          startActivityForResult(
              new Intent(AddAccountActivity.this, AddAccountDompetActivity.class).putExtras(b),
              State.FLAG_ACTIVITY_WILL_FINISH_AFTER);

          break;
        case AccountPresenter.SECTION_INVESTMENT:
          if (cell.getName().toLowerCase().contains("manulife")) {
            if (session.ihaveAnInstutitionAccount()) {
              Helper.showCustomSnackBar(recyclerView, getLayoutInflater(),
                  getString(R.string.manulife_account_connected));
            } else {
              //promptToVerify(cell.getVendorId());
              startActivityForResult(
                  new Intent(AddAccountActivity.this, LoginKlikMamiActivity.class).putExtra(
                      IS_REGISTER_HIDDEN, true), State.FLAG_ACTIVITY_WILL_FINISH_AFTER);
              Helper.trackThis(this, "user klik manulife di menu tambah rekening "
                  + NewManageAccountActivity.accessFrom);
              NewManageAccountActivity.accessFrom = "";
            }
          }
          break;
        case AccountPresenter.SECTION_MERCHANT:
          b.putInt(Words.ID_VENDOR, cell.getVendor().getId());
          b.putString(Words.NAMA_VENDOR, cell.getVendor().getVendor_name());
          //if(cell.getVendor().getId() == 2){
          //  showSnackbar("BCA");
          //  return;
          //}else if(cell.getVendor().getId() == 3){
          //  showSnackbar("BNI");
          //  return;
          //}
          startActivityForResult(
              new Intent(AddAccountActivity.this, AddAccountBankActivity.class).putExtras(b),
              State.FLAG_ACTIVITY_WILL_FINISH_LATER);
          break;
      }
    });
    recyclerView.setAdapter(adapter);
  }

  private void promptToVerify(int idVendor) {
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
        .create()
        .show();
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
      startActivity(
          new Intent(this, ManulifeHomePageActivity.class).putExtra("action", isTryToLogin));
    });
    dialog.show(getSupportFragmentManager(), "explainer-dialog");
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == State.FLAG_ACTIVITY_WILL_FINISH_AFTER && resultCode == RESULT_OK) {
      finish();
    }
  }

  private void showSnackbar(String bank_name) {
    Helper.showCustomSnackBar(recyclerView, getLayoutInflater(),
        getString(R.string.login_bank) + " " + bank_name + " " + getString(
            R.string.under_construction), R.color.orange_500, Gravity.TOP);
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
