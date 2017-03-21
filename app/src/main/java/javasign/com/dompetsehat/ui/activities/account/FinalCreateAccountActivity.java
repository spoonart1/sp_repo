package javasign.com.dompetsehat.ui.activities.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.account.AccountPresenter;
import javasign.com.dompetsehat.presenter.account.AddAccountBankPresenter;
import javasign.com.dompetsehat.presenter.account.AddAccountInterface;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by avesina on 12/21/16.
 */

public class FinalCreateAccountActivity extends BaseActivity implements AddAccountInterface {
  @Bind(R.id.tv_username) TextView tv_username;
  @Bind(R.id.tv_desc) TextView tv_desc;

  public static String TAG_ID_ACCOUNT = "id_account";
  public static String TAG_SALDO = "saldo";
  public static String TAG_USERNAME = "username";
  public static String TAG_PASSWORD = "password";
  public static String TAG_MESSAGE = "message";
  @Bind(R.id.tv_value_transaction) TextView tv_value_transaction;
  @Inject AddAccountBankPresenter presenter;
  private ProgressDialog progressDialog;
  public static String PRODUCT_IDS = "product_ids";
  int account_id;

  private boolean isPasswordShown = false;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_final_add_account);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    ButterKnife.bind(this);
    init();
  }

  private void init() {
    Intent intent = getIntent();
    if(intent.hasExtra(Words.NAMA_VENDOR)){
      tv_desc.setText(tv_desc.getText()+" "+intent.getExtras().getString(Words.NAMA_VENDOR));
    }
    if (intent.hasExtra(TAG_ID_ACCOUNT) && intent.hasExtra(TAG_USERNAME) && intent.hasExtra(TAG_SALDO)) {
      account_id = intent.getExtras().getInt(TAG_ID_ACCOUNT);
      progressDialog = new ProgressDialog(this);
      tv_value_transaction.setText("Saldo " + RupiahCurrencyFormat.getInstance()
          .toRupiahFormatSimple(intent.getExtras().getDouble(TAG_SALDO, 0)));
      tv_username.setText(intent.getExtras().getString(TAG_USERNAME));
      presenter.syncAccount(account_id);
    } else {
      finish();
    }
  }

  @OnClick(R.id.btn_detail) void seeDetail(View v) {
    Intent intent = new Intent(this, TransactionsActivity.class);
    intent.putExtra(TransactionsActivity.FROM, State.FROM_DETAIL_ACCOUNT);
    if (tv_value_transaction.getTag() instanceof Integer[]) {
      Integer[] ids = (Integer[]) tv_value_transaction.getTag();
      ArrayList<Integer> icf = new ArrayList<>();
      icf.addAll(Arrays.asList(ids));
      intent.putIntegerArrayListExtra(PRODUCT_IDS,icf);
    }
    startActivity(intent);
  }

  @OnClick(R.id.btn_close) void onClose(View v) {
    finishActivity();
    setResult(State.FLAG_ACTIVITY_WILL_FINISH_AFTER);
  }

  private void finishActivity() {
    finish();
    Helper.finishAllPreviousActivityWithNextTarget(this, ManageEachAccountActivity.class,
        new Intent()
            .putExtra(ManageEachAccountActivity.KEY_ID_ACCOUNT, account_id)
            .putExtra("from", "login"));
  }

  @Override public void setBalance(double balance) {

  }

  @Override public void startLoading() {
    progressDialog.setMessage("Mengambil data bank");
    progressDialog.show();
  }

  @Override public void stopLoading() {
    progressDialog.dismiss();
  }

  @Override public void finishUpdateAccount(String msg) {

  }

  @Override public void finishCreatedAccount(Account account, double Saldo, String mesage) {

  }

  @Override public void setDataAccount(String username, double saldo) {

  }

  @Override public void showMessage(String message) {

  }

  @Override public void setCountTransaction(Integer[] products_id, int counts) {
    runOnUiThread(() -> {
      //tv_value_transaction.setText(counts + " Transaksi ");
      tv_value_transaction.setTag(products_id);
    });
  }
}
