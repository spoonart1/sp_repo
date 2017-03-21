package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.plan.DanaInterface;
import javasign.com.dompetsehat.presenter.plan.DanaPresenter;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/30/16.
 */
public class CustomPlanActivity extends BundlePlanActivity implements DanaInterface {

  @Bind(R.id.et_plan_name) MaterialEditText et_plan_name;
  @Bind(R.id.et_amount) MaterialEditText et_amount;
  @Bind(R.id.et_time) MaterialEditText et_time;
  @Bind(R.id.btn_next) Button btn_next;
  @Bind(R.id.sp_year_month) Spinner sp_year_month;
  String[] lebels;
  int selectedSpinner = 0;

  public static final String TITLE = "title";
  SessionManager sessionManager;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private boolean isTimeValid = false;
  @Inject DanaPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.custom_plan_activity);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);

    setTitle(getString(R.string.other_plan));
    sessionManager = new SessionManager(getActivityComponent().context());
    lebels = getResources().getStringArray(R.array.spinner_year_month);
    Helper.trackThis(this, "user klik rencana lainnya");
    init();
    initSession();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    finish();
  }

  @OnClick(R.id.btn_next) void onNext() {
    Intent intent = new Intent();
    intent.putExtra(FinishingPlanActivity.TYPE, FinishingPlanActivity.TYPE_DANA_CUSTOM);
    intent.putExtra(TITLE, et_plan_name.getText().toString());
    double danadisiapkan = presenter.hitungInflasi(Integer.valueOf(et_time.getText().toString()),
        Double.valueOf(RupiahCurrencyFormat.clearRp(et_amount.getText().toString())));
    intent.putExtra(SetupPlanActivity.DANA_DISIAPKAN, danadisiapkan);
    if (selectedSpinner == 0) {
      intent.putExtra(SetupPlanActivity.BULAN_PERSIAPAN,
          Integer.valueOf(et_time.getText().toString()) * 12);
    } else {
      intent.putExtra(SetupPlanActivity.BULAN_PERSIAPAN,
          Integer.valueOf(et_time.getText().toString()));
    }

    if(!isTimeValid)
      return;

    sessionManager.saveDanaCustome(et_plan_name.getText().toString(),
        Integer.valueOf(et_time.getText().toString()),
        Float.valueOf(RupiahCurrencyFormat.clearRp(et_amount.getText().toString())));

    Helper.goTo(this, SetupPlanActivity.class, intent);
  }

  @OnClick(R.id.tv_disclaimer) void seeDisclaimer() {
    Helper.showDisclaimerDialog(getSupportFragmentManager(),
        getString(R.string.ds_diclaimer_dialog_message), "(link: http://dompetsehat.com/terms)",
        "http://dompetsehat.com/terms");
  }

  private void init() {
    Words.setButtonToListen(btn_next, et_plan_name, et_amount, et_time);
    format.formatEditText(et_amount);
    et_time.addTextChangedListener(new Words.SimpleTextWatcer() {
      @Override public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);
        try {
          isTimeValid = true;
          int value = Integer.valueOf(editable.toString());
          if (sp_year_month.getSelectedItemPosition() == 0) {
            if (!checkJangkaWaktu(value, 25)) {
              et_time.setError("Maksimal adalah 25 tahun");
            }
          } else {
            if (!checkJangkaWaktu(value, 24)) {
              et_time.setError("Maksimal adalah 24 bulan");
            }
          }

          if (Integer.valueOf(editable.toString()) <= 0) {
            et_time.setText("");
          }
        } catch (Exception e) {
          Timber.e(e.getMessage());
        }
      }
    });

    sp_year_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedSpinner = i;
        et_time.setHint("0 " + lebels[i]);
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
        sp_year_month.setSelection(0);
      }
    });
  }

  private boolean checkJangkaWaktu(int waktu, int max) {
    boolean is_valid = true;
    if (waktu > max) {
      is_valid = false;
      isTimeValid = false;
    }
    return is_valid;
  }

  private void initSession() {
    HashMap<String, Object> s = sessionManager.getDanaCutome();
    if (!s.get(sessionManager.NAMA).toString().equalsIgnoreCase("")) {
      et_plan_name.setText(String.valueOf(s.get(sessionManager.NAMA)));
    }
    if ((float) s.get(sessionManager.ANGGARAN) > 0) {
      et_amount.setText(
          new RupiahCurrencyFormat().toRupiahFormatSimple((float) s.get(sessionManager.ANGGARAN)));
    }
    if ((int) s.get(sessionManager.JANGKA_WAKTU) > 0) {
      et_time.setText(String.valueOf(s.get(sessionManager.JANGKA_WAKTU)));
    }
  }

  @Override public void setUmur(int umur) {

  }

  @Override public void setDanaDisiapkan(double dana) {

  }

  @Override public void setProsentase(double prosentase) {

  }

  @Override public void setCicilanPerbulan(double cicilanPerbulan) {

  }

  @Override public void setPendapatanBulanan(double pendapatanBulanan) {

  }

  @Override public void setDibayarLunas(double dibayarLunas) {

  }

  @Override public void setCicilanPertahun(double cicilanPertahun) {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
