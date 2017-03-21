package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.plan.DanaInterface;
import javasign.com.dompetsehat.presenter.plan.DanaPresenter;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/30/16.
 */
public class DanaDaruratActivity extends BundlePlanActivity implements DanaInterface {

  @Bind(R.id.et_amount) MaterialEditText et_amount;
  @Bind(R.id.seekbar) SeekBar seekbar;
  @Bind(R.id.tv_month) TextView tv_month;
  @Bind(R.id.floating_periode) TextView floating_periode;
  @Bind(R.id.ll_label) LinearLayout ll_label;
  @Bind(R.id.tv_desc) TextView tv_desc;
  @Bind(R.id.tv_year) MaterialEditText tv_year;

  RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();

  public static final String TAHUN_PERSIAPAN = "tahun_persiapan";
  public static final String BULAN_PENGGUNAAN = "bulan_penggunaan";
  public static final String PENGELUARAN_BULANAN = "pengeluaran_bulanan";
  public static final String TAHUN = "tahun";

  private int maksimal_tahun = 20;

  int bulan = 0;
  Double amount = 0.0;

  @Inject DanaPresenter presenter;
  SessionManager sessionManager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dana_darurat);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);
    tv_year.setText("1");

    setTitle(getString(R.string.plan_emergency_title));
    sessionManager = new SessionManager(getActivityComponent().context());

    Helper.trackThis(this, "user klik dana darurat");
    init();
    initSession();
  }

  @OnClick(R.id.ic_back) void onBack() {
    saveToDraft();
    finish();
  }

  @OnClick(R.id.btn_next) void onNext() {
    Intent intent = new Intent();

    try {
      if (Integer.valueOf(tv_year.getText().toString()) > maksimal_tahun) {
        return;
      }
    } catch (Exception e) {

    }

    intent.putExtra(FinishingPlanActivity.TYPE, FinishingPlanActivity.TYPE_DANA_DARURAT);
    intent.putExtra(TAHUN_PERSIAPAN, Integer.valueOf(tv_year.getText().toString()));
    intent.putExtra(BULAN_PENGGUNAAN, bulan);
    intent.putExtra(PENGELUARAN_BULANAN, Double.valueOf(amount));
    intent.putExtra(SetupPlanActivity.DANA_DISIAPKAN,
        presenter.hitungInflasi(Integer.valueOf(tv_year.getText().toString()), bulan * amount));
    intent.putExtra(SetupPlanActivity.BULAN_PERSIAPAN,
        Integer.valueOf(tv_year.getText().toString()) * 12);

    saveToDraft();

    Helper.goTo(this, SetupPlanActivity.class, intent);
  }

  private void saveToDraft() {
    float pendapatan = 0;
    int seekbarProgress = seekbar.getProgress();
    tv_month.setText(seekbarProgress + " " + getString(R.string.month));
    if (!TextUtils.isEmpty(et_amount.getText())) {
      pendapatan = Float.valueOf(RupiahCurrencyFormat.clearRp(et_amount.getText().toString()));
    }

    int year = 1;
    if (!TextUtils.isEmpty(tv_year.getText())) {
      year = Integer.valueOf(tv_year.getText().toString());
    }

    sessionManager.saveDanaDarurat(pendapatan, seekbarProgress, year);
  }

  @OnClick(R.id.tv_disclaimer) void seeDisclaimer() {
    Helper.showDisclaimerDialog(getSupportFragmentManager(),
        getString(R.string.ds_diclaimer_dialog_message), "(link: http://dompetsehat.com/terms)",
        "http://dompetsehat.com/terms");
  }

  private void init() {
    Words.setButtonToListen(ButterKnife.findById(this, R.id.btn_next), et_amount, tv_year);
    Words.setButtonToListen(floating_periode, tv_year);
    rcf.formatEditText(et_amount);
    updateLabel();
    et_amount.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!RupiahCurrencyFormat.clearRp(s.toString()).equalsIgnoreCase("")) {
          amount = Double.valueOf(RupiahCurrencyFormat.clearRp(s.toString()));
        } else {
          amount = 0.0;
        }
        updateLabel();
      }

      @Override public void afterTextChanged(Editable s) {

      }
    });
    tv_year.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateLabel();
      }

      @Override public void afterTextChanged(Editable s) {
        try {
          if (Integer.valueOf(s.toString()) > maksimal_tahun) {
            tv_year.setError(getString(R.string.maximum) + " " + maksimal_tahun + " " + getString(
                R.string.year));
          }
        } catch (Exception e) {

        }
      }
    });
  }

  private void initSession() {
    HashMap<String, String> s = sessionManager.getDanaDarurat();
    if (!s.get(sessionManager.PENGELURAN_PERBULAN).equalsIgnoreCase("0")) {
      et_amount.setText(new RupiahCurrencyFormat().toRupiahFormatSimple(
          Float.parseFloat(s.get(sessionManager.PENGELURAN_PERBULAN))));
    }
    if (!s.get(sessionManager.BULAN_PERSIAPAN).equalsIgnoreCase("0")) {
      bulan = Integer.parseInt(s.get(sessionManager.BULAN_PERSIAPAN));
      seekbar.setProgress(bulan);
      tv_month.setText(bulan + " " + getString(R.string.month));
    }
    if (!s.get(sessionManager.TAHUN).equalsIgnoreCase("0")) {
      tv_year.setText(s.get(sessionManager.TAHUN));
    }
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  public void updateLabel() {
    int tahun =
        !TextUtils.isEmpty(tv_year.getText()) ? Integer.valueOf(tv_year.getText().toString()) : 1;
    double hasil_inflasi = presenter.hitungInflasi(tahun, bulan * amount);
    String desc = getString(R.string.based_on_monthly_spending) + " " + rcf.toRupiahFormatSimple(
        hasil_inflasi) + " " + getString(R.string.if_emergency_need);
    tv_desc.setText(desc);
  }

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    seekbar.setMax(12);
    seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        bulan = progress;
        tv_month.setText(progress + " " + getString(R.string.month));
        updateLabel();
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
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
    if (AddPlanEvent.ADD_PLANT_EVENT_SUCCESS == RequestID) {
      finish();
    }
  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }

  @Override protected void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
