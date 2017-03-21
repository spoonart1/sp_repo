package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
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
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/30/16.
 */
public class DanaPensiunActivity extends BundlePlanActivity implements DanaInterface {

  private final int MAX_RETIRED_AGE = 50;

  @Bind(R.id.btn_next) View btn_next;
  @Bind(R.id.et_age) MaterialEditText et_age;
  @Bind(R.id.et_monthly_income) MaterialEditText et_monthly_income;
  @Bind(R.id.et_age_now) MaterialEditText et_age_now;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();

  public static final String UMUR_PENSIUN = "umur_pensiun";
  public static final String UMUR = "umur";
  public static final String PENDAPATAN_PERBULAN = "perndapatan_perbulan";

  private String errorAgeNow = null;
  private String errorAgeRetired = null;

  SessionManager sessionManager;

  @Inject DanaPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dana_pensiun);
    ButterKnife.bind(this);
    setTitle(getString(R.string.plan_retirement_title));
    getActivityComponent().inject(this);
    presenter.attachView(this);
    presenter.init();
    sessionManager = new SessionManager(getActivityComponent().context());
    init();
    initSession();
    Helper.trackThis(this, "user klik dana pensiun");
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    saveToDraft();
    finish();
  }



  @OnClick(R.id.btn_next) void onNext() {
    Intent intent = new Intent();
    intent.putExtra(FinishingPlanActivity.TYPE, FinishingPlanActivity.TYPE_DANA_PENSIUN);
    double monthly_income =
        Double.valueOf(RupiahCurrencyFormat.clearRp(et_monthly_income.getText().toString()));

    int retire_age = Integer.valueOf(et_age.getText().toString());
    int ageNow = Integer.valueOf(et_age_now.getText().toString());

    if (!checkUmur(retire_age, ageNow))
      return;

    double dana_disiapkan = presenter.hitungDanaPensiun(retire_age, ageNow, monthly_income);
    Timber.e(" dana di siapkan " + dana_disiapkan);
    intent.putExtra(PENDAPATAN_PERBULAN, monthly_income);
    intent.putExtra(UMUR, ageNow);
    intent.putExtra(UMUR_PENSIUN, retire_age);
    intent.putExtra(SetupPlanActivity.DANA_DISIAPKAN, dana_disiapkan);
    intent.putExtra(SetupPlanActivity.BULAN_PERSIAPAN, (retire_age - ageNow) * 12);

    saveToDraft();

    Helper.goTo(this, SetupPlanActivity.class, intent);
  }

  private boolean checkUmur(int retire_age, int ageNow){
    boolean nextable = true;
    if (ageNow > MAX_RETIRED_AGE) {
      et_age_now.setError(errorAgeNow);
      nextable = false;
    }
    if (retire_age < ageNow) {
      et_age.setError(errorAgeRetired);
      nextable = false;
    }

    return nextable;
  }

  private void saveToDraft() {
    int umurSekarang = 0;
    int umurPensiun = 60;
    float monthlySalary = 0;

    if (!TextUtils.isEmpty(et_monthly_income.getText())) {
      monthlySalary =
          Float.valueOf(RupiahCurrencyFormat.clearRp(et_monthly_income.getText().toString()));
    }

    if (!TextUtils.isEmpty(et_age_now.getText())) {
      umurSekarang = Integer.valueOf(et_age_now.getText().toString());
    }

    if (!TextUtils.isEmpty(et_age.getText())) {
      umurPensiun = Integer.valueOf(et_age.getText().toString());
    }

    sessionManager.saveDanaPensiun(umurSekarang, monthlySalary, umurPensiun);
  }

  @OnClick(R.id.tv_disclaimer) void openDisclaimer() {
    Helper.showDisclaimerDialog(getSupportFragmentManager(),
        getString(R.string.ds_diclaimer_dialog_message),
        "(http://dompetsehat.com/terms)", "http://dompetsehat.com/terms");
  }

  private void init() {
    errorAgeNow = getString(R.string.error_field_age_now);
    errorAgeRetired = getString(R.string.error_field_age_retired);

    Words.setButtonToListen(btn_next, et_age, et_monthly_income, et_age_now);
    format.formatEditText(et_monthly_income);
    et_monthly_income.setFloatingLabelAnimating(true);
    et_age_now.addTextChangedListener(new Words.SimpleTextWatcer() {
      @Override public void afterTextChanged(Editable editable) {
        if (!TextUtils.isEmpty(editable)) {
          String num = editable.toString();
          if (Integer.valueOf(num) > MAX_RETIRED_AGE) {
            et_age_now.setError(errorAgeNow);
          }
          et_age_now.setSelection(editable.length());
        }
      }
    });

    et_age.addTextChangedListener(new Words.SimpleTextWatcer() {
      @Override public void afterTextChanged(Editable editable) {
        if (!TextUtils.isEmpty(editable) && !TextUtils.isEmpty(et_age_now.getText())) {
          int retired_age = Integer.valueOf(editable.toString());
          int age_now = Integer.valueOf(et_age_now.getText().toString());
          et_age.setError(retired_age < age_now ? errorAgeRetired : null);
        }
      }
    });
  }

  private void initSession() {
    et_age.setText("60");
    HashMap<String, String> s = sessionManager.getDanaPensiun();
    Timber.e(" "+s);
    if (!s.get(sessionManager.UMUR_PENSIUN).equalsIgnoreCase("0")) {
      et_age.setText(s.get(sessionManager.UMUR_PENSIUN));
    }
    if (!s.get(sessionManager.UMUR).equalsIgnoreCase("0")) {
      et_age_now.setText(s.get(sessionManager.UMUR));
    }
    try {
      if (Double.valueOf(s.get(sessionManager.PENGHASILAN)) > 0) {
        et_monthly_income.setText(s.get(sessionManager.PENGHASILAN));
      }
    }catch (Exception e){

    }
  }

  //  private void showDateDialog(final TextView target) {
  //    DatePickerDialog datePickerDialog = new DatePickerDialog();
  //    datePickerDialog.show(getSupportFragmentManager(), "datepicker");
  //    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
  //      @Override public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
  //        String monthName = DateUtils.getMonthString(month, DateUtils.LENGTH_LONG);
  //        target.setText(day + " " + monthName + " " + year);
  //        datePickerDialog.dismissAllowingStateLoss();
  //      }
  //    });
  //  }

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

  @Override public void setUmur(int umur) {
    if(umur <= 0)
      return;
    et_age_now.setText(String.valueOf(umur));
  }

  @Override public void setDanaDisiapkan(double danaPensiun) {

  }

  @Override public void setProsentase(double prosentase) {

  }

  @Override public void setCicilanPerbulan(double cicilanPerbulan) {

  }

  @Override public void setPendapatanBulanan(double pendapatanBulanan) {
    if(pendapatanBulanan > 0) {
      et_monthly_income.setText(new RupiahCurrencyFormat().toRupiahFormatSimple(pendapatanBulanan));
    }
  }

  @Override public void setDibayarLunas(double dibayarLunas) {

  }

  @Override public void setCicilanPertahun(double cicilanPertahun) {

  }

  @Override protected void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
