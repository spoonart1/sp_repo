package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsButton;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.plan.DanaInterface;
import javasign.com.dompetsehat.presenter.plan.DanaPresenter;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/25/16.
 */
public class SetupPlanActivity extends BundlePlanActivity implements DanaInterface {

  @Bind(R.id.radio_group) RadioGroup radio_group;
  @Bind(R.id.tv_total) TextView tv_total;
  @Bind(R.id.tv_persen) TextView tv_persen;
  @Bind(R.id.tv_amount) TextView tv_amount;
  @Bind(R.id.tv_amount_yearly) TextView tv_amount_yearly;
  @Bind(R.id.tv_amount_lunas) TextView tv_amount_lunas;
  @Bind(R.id.tv_summary) TextView tv_summary;
  @Bind(R.id.ll_container) LinearLayout ll_container;
  @Bind({ R.id.div_1, R.id.ll_tab_yearly, R.id.div_2, R.id.ll_tab_lunas }) View[] views;
  RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
  boolean is_yearly_still_hidden = false;

  public final static String CICILAN_BULANAN = "cicilan_bulanan";
  public final static String CICILAN_TAHUNAN = "cicilan_tahunan";
  public final static String DIBAYAR_LUNAS = "dibayar_lunas";
  public final static String PERSENTASE_ASUMSI = "procentase_trust";

  public static String DANA_DISIAPKAN = "dana_disiapkan";
  public static String BULAN_PERSIAPAN = "bulan_periapan";

  public static final String TYPE_ASUMSI = "type_asumsi";

  public static final int TYPE_CONSERVATIVE = 0;
  public static final int TYPE_MODERATE = 1;
  //public static final int TYPE_HIGH = 2;
  public static final int TYPE_AGGRESIVE = 2;

  int selected_type_asumsi = TYPE_CONSERVATIVE;

  public double procentase = 0.0;

  private boolean isExpanded = false;

  public double[] rates = { 0.07, 0.10, 0.15, 0.18 };

  @Inject DanaPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setup_plan);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);
    setTitle("");
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.attachView(this);
    init();
  }

  public void init() {
    if (getIntent().hasExtra(DANA_DISIAPKAN) && getIntent().hasExtra(BULAN_PERSIAPAN) && getIntent()
        .hasExtra(FinishingPlanActivity.TYPE)) {
      Intent intent = getIntent();
      String type = intent.getExtras().getString(FinishingPlanActivity.TYPE);
      switch (type) {
        case FinishingPlanActivity.TYPE_DANA_PENSIUN:
          int usia = intent.getExtras().getInt(DanaPensiunActivity.UMUR_PENSIUN);
          tv_summary.setText(presenter.getNoteDanaPensiun(usia));
          break;
        case FinishingPlanActivity.TYPE_DANA_DARURAT:
          int bulan = intent.getExtras().getInt(DanaDaruratActivity.BULAN_PENGGUNAAN);
          tv_summary.setText(presenter.getNoteDanaDarurat(bulan));
          break;
        case FinishingPlanActivity.TYPE_DANA_KULIAH:
          int semester = intent.getExtras().getInt(DanaKuliahActivity.LAMA_KULIAH);
          tv_summary.setText(presenter.getNoteDanaKuliah(semester));
          break;
        case FinishingPlanActivity.TYPE_DANA_CUSTOM:
          String title = intent.getExtras().getString(CustomPlanActivity.TITLE);
          int bulan_pesiapan = intent.getExtras().getInt(BULAN_PERSIAPAN);
          tv_summary.setText(presenter.getNoteDanaCustome(title, bulan_pesiapan));
          break;
      }
      double dana_disiapkan = getIntent().getExtras().getDouble(DANA_DISIAPKAN);
      Timber.d("dana disiapkan " + dana_disiapkan);
      int bulan_persiapan = getIntent().getExtras().getInt(BULAN_PERSIAPAN);
      presenter.hitungCicilanBulanan(dana_disiapkan, rates[0], bulan_persiapan);
      presenter.hitungCicilanTahunan(dana_disiapkan, rates[0], bulan_persiapan);
      presenter.hitungDibayarLunas(dana_disiapkan, rates[0], bulan_persiapan);
      radio_group.setOnCheckedChangeListener((group, checkedId) -> {
        switch (checkedId) {
          case R.id.opsi_1:
            selected_type_asumsi = TYPE_CONSERVATIVE;
            presenter.hitungCicilanBulanan(dana_disiapkan, rates[0], bulan_persiapan);
            presenter.hitungCicilanTahunan(dana_disiapkan, rates[0], bulan_persiapan);
            presenter.hitungDibayarLunas(dana_disiapkan, rates[0], bulan_persiapan);
            Helper.trackThis(this, "user memilih konservatif di " + type);
            break;
          case R.id.opsi_2:
            selected_type_asumsi = TYPE_MODERATE;
            presenter.hitungCicilanBulanan(dana_disiapkan, rates[1], bulan_persiapan);
            presenter.hitungCicilanTahunan(dana_disiapkan, rates[1], bulan_persiapan);
            presenter.hitungDibayarLunas(dana_disiapkan, rates[1], bulan_persiapan);
            Helper.trackThis(this, "user memilih moderat di " + type);
            break;
          case R.id.opsi_3:
            selected_type_asumsi = TYPE_AGGRESIVE;
            presenter.hitungCicilanBulanan(dana_disiapkan, rates[2], bulan_persiapan);
            presenter.hitungCicilanTahunan(dana_disiapkan, rates[2], bulan_persiapan);
            presenter.hitungDibayarLunas(dana_disiapkan, rates[2], bulan_persiapan);
            Helper.trackThis(this, "user memilih agresif di " + type);
            break;
          case 3000000:
            selected_type_asumsi = TYPE_AGGRESIVE;
            presenter.hitungCicilanBulanan(dana_disiapkan, rates[3], bulan_persiapan);
            presenter.hitungCicilanTahunan(dana_disiapkan, rates[3], bulan_persiapan);
            presenter.hitungDibayarLunas(dana_disiapkan, rates[3], bulan_persiapan);
            Helper.trackThis(this, "user memilih agresif di " + type);
          default:
            break;
        }
      });
    } else {
      Toast.makeText(getActivityComponent().context(),
          getString(R.string.error_source_unknown) + ", " + getString(
              R.string.please_try_again).toLowerCase(), Toast.LENGTH_LONG).show();
      finish();
    }
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.toggle) void toggleView(IconicsButton ib) {
    if (isExpanded) {
      showViews(false);
      ib.setText(DSFont.Icon.dsf_down_chevron_thin.getFormattedName());
      return;
    }

    showViews(true);
    ib.setText(DSFont.Icon.dsf_up_chevron_thin.getFormattedName());
  }

  private void showViews(boolean show) {
    int visible = show ? View.VISIBLE : View.GONE;
    this.isExpanded = show;
    for (View v : views) {
      v.setVisibility(visible);
      animate(v, show);
    }
    if (is_yearly_still_hidden) {
      views[1].setVisibility(View.GONE);
    }
  }

  private void animate(View v, boolean show) {
    if (show) {
      v.setVisibility(View.VISIBLE);
      v.setAlpha(0);
      ViewCompat.animate(v).withEndAction(new Runnable() {
        @Override public void run() {
          v.setAlpha(1);
        }
      }).alpha(1);
    } else {
      v.setVisibility(View.VISIBLE);
      v.setAlpha(1);
      ViewCompat.animate(v).withEndAction(new Runnable() {
        @Override public void run() {
          v.setVisibility(View.GONE);
        }
      }).alpha(0);
    }
  }

  @OnClick(R.id.btn_next) void onNext() {
    String type = getIntent().getExtras().getString(FinishingPlanActivity.TYPE);
    Intent intent = getIntent();
    intent.putExtra(CICILAN_BULANAN,
        Double.valueOf(RupiahCurrencyFormat.clearRp(tv_amount.getText().toString())));
    intent.putExtra(CICILAN_TAHUNAN,
        Double.valueOf(RupiahCurrencyFormat.clearRp(tv_amount_yearly.getText().toString())));
    intent.putExtra(DIBAYAR_LUNAS,
        Double.valueOf(RupiahCurrencyFormat.clearRp(tv_amount_lunas.getText().toString())));
    intent.putExtra(PERSENTASE_ASUMSI, procentase);
    intent.putExtra(TYPE_ASUMSI, selected_type_asumsi);
    intent.putExtra(FinishingPlanActivity.MODE, FinishingPlanActivity.MODE_NEW);
    intent.putExtra(FinishingPlanActivity.TYPE, type);
    Helper.goTo(this, FinishingPlanActivity.class, intent);
  }

  @OnClick(R.id.btn_info) void openInfo() {
    Helper.showDisclaimerDialog(getSupportFragmentManager(), getString(R.string.assumtion_cashback),
        "");
  }

  @OnClick(R.id.tv_disclaimer) void openDisclaimer() {
    Helper.showDisclaimerDialog(getSupportFragmentManager(),
        getString(R.string.ds_diclaimer_dialog_message), "(link: http://dompetsehat.com/terms)",
        "http://dompetsehat.com/terms");
  }

  @Override public void setUmur(int umur) {

  }

  @Override public void setDanaDisiapkan(double dana) {
    runOnUiThread(() -> tv_total.setText(rcf.toRupiahFormatSimple(Math.ceil(dana))));
  }

  @Override public void setProsentase(double prosentase) {
    runOnUiThread(() -> {
      this.procentase = prosentase;
      tv_persen.setText(String.format("%.2f", prosentase * 100) + " %p.a");
    });
  }

  @Override public void setCicilanPerbulan(double cicilanPerbulan) {
    runOnUiThread(() -> {
      tv_amount.setText(rcf.toRupiahFormatSimple(Math.ceil(cicilanPerbulan)));
    });
  }

  @Override public void setPendapatanBulanan(double pendapatanBulanan) {

  }

  @Override public void setDibayarLunas(double dibayarLunas) {
    runOnUiThread(() -> {
      tv_amount_lunas.setText(rcf.toRupiahFormatSimple(Math.ceil(dibayarLunas)));
    });
  }

  @Override public void setCicilanPertahun(double cicilanPertahun) {
    runOnUiThread(() -> {
      if (Double.compare(cicilanPertahun, 0) > 0) {
        Timber.e("setCicilanPertahun sini " + cicilanPertahun);
        tv_amount_yearly.setText(rcf.toRupiahFormatSimple(Math.ceil(cicilanPertahun)));
        is_yearly_still_hidden = false;
      } else {
        Timber.e("setCicilanPertahun sana " + cicilanPertahun);
        is_yearly_still_hidden = true;
      }
    });
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

  public static int getTypeByRisk(double risk) {
    //public double[] rates = {0.07,0.10,0.15,0.18};
    if (risk == 0.07) {
      return TYPE_CONSERVATIVE;
    } else if (risk == 0.1) {
      return TYPE_MODERATE;
    } else if (risk == 0.18) {
      return TYPE_AGGRESIVE;
    }
    return TYPE_CONSERVATIVE;
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
