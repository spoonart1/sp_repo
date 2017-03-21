package javasign.com.dompetsehat.ui.activities.account;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.ui.activities.account.base.BaseAccountActivity;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.presenter.account.AddAccountDompetPresenter;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.Words;

/**
 * Created by bastianbentra on 8/18/16.
 */
public class AddAccountDompetActivity extends BaseAccountActivity{

  @Bind(R.id.et_name) MaterialEditText et_name;
  @Bind(R.id.et_saldo) MaterialEditText et_saldo;
  @Bind(R.id.btn_save) Button btn_save;
  @Bind(R.id.tv_value) TextView tv_value;
  @Bind(R.id.tv_greeting) TextView tv_greeting;

  int vendor_id;
  boolean isEditMode = false;
  RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
  private static boolean MODE_EDIT = false;
  private Integer ID_ACCOUNT;

  @Inject AddAccountDompetPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_account_dompet);
    ButterKnife.bind(this);

    setTitle(Words.NONE);

    getActivityComponent().inject(this);
    presenter.attachView(this);
    presenter.init();

    View rootview = ButterKnife.findById(this, R.id.rootview);
    View content = ButterKnife.findById(this, R.id.ll_content);
    ScrollView scrollView = ButterKnife.findById(this, R.id.scrollView);

    Helper.moveViewUpIfKeyboardAppear(rootview, scrollView, content);

    preLoad();
    init();
  }

  private void preLoad() {
    if (getIntent().hasExtra(Words.ID_VENDOR)) {
      vendor_id = getIntent().getExtras().getInt(Words.ID_VENDOR);
    } else {
      Toast.makeText(this, getString(R.string.error_source_unknown), Toast.LENGTH_LONG).show();
      finish();
    }

    isEditMode = !getIntent().hasExtra("add-mode");
    if (isEditMode) {
      tv_greeting.setText(getString(R.string.edit_account)+" " + getIntent().getStringExtra(Words.NAMA_VENDOR));
    }
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    IconicsTextView ic_back = ButterKnife.findById(this, R.id.ic_back);
    ic_back.setTextColor(Color.WHITE);

    GeneralHelper.greenStatusBar(getWindow(), getResources());
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {
    Words.setButtonToListen(btn_save, et_name, et_saldo);
    rcf.formatEditText(et_saldo);
    et_saldo.addTextChangedListener(new Words.SimpleTextWatcer() {
      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        tv_value.setText(s);
      }
    });
    if (getIntent().hasExtra(ManageEachAccountActivity.MODE_EDIT) && getIntent().hasExtra(Words.ACCOUNT_ID)) {
      MODE_EDIT = getIntent().getExtras().getBoolean(ManageEachAccountActivity.MODE_EDIT);
      if (MODE_EDIT) {
        tv_greeting.setText(Words.toTitleCase(getString(R.string.edit_account))+" Manual");
        if (getIntent().hasExtra(Words.ACCOUNT_ID)) {
          ID_ACCOUNT = getIntent().getExtras().getInt(Words.ACCOUNT_ID);
          presenter.loadAccount(ID_ACCOUNT);
        } else {
          Toast.makeText(this, getString(R.string.error_source_unknown), Toast.LENGTH_LONG).show();
          finish();
        }
      }
    }
  }

  @OnClick(R.id.btn_save) void doSave() {
    if(!isEditMode) {
      presenter.saveDompet(vendor_id, et_name.getText().toString(),Integer.valueOf(RupiahCurrencyFormat.clearRp(et_saldo.getText().toString())));
    }else{
      presenter.ubahDompet(ID_ACCOUNT,et_name.getText().toString(),Integer.valueOf(RupiahCurrencyFormat.clearRp(et_saldo.getText().toString())));
    }
  }

  @Override public void finishCreatedAccount(Account account,double saldo,String message) {
    Helper.trackThis(this, "User telah menambahkan Rekening Dompet baru");
    //startActivity(new Intent(this,NewManageAccountActivity.class));
    setResult(RESULT_OK);
    finish();
  }

  @Override public void setBalance(double balance) {
    tv_value.setText(presenter.rcf.toRupiahFormatSimple(0));
  }

  @Override protected void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void setDataAccount(String username, double saldo) {
    super.setDataAccount(username, saldo);
    runOnUiThread(()->{
      et_name.setText(username);
      et_saldo.setText(rcf.toRupiahFormatSimple(saldo));
    });
  }

  @Override public void finishUpdateAccount(String msg) {
    super.finishUpdateAccount(msg);
    finish();
  }
}
