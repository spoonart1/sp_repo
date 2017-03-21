package javasign.com.dompetsehat.ui.activities.portofolio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;

/**
 * Created by bastianbentra on 8/22/16.
 */
public class DetailPortofolioActivity extends BaseActivity {

  @Bind(R.id.tv_porto_name) TextView tv_porto_name;
  @Bind(R.id.tv_values_saldo) TextView tv_values_saldo;
  @Bind(R.id.tv_total_unit) TextView tv_total_unit;
  @Bind(R.id.tv_cost_per_unit) TextView tv_cost_per_unit;
  @Bind(R.id.tv_date) TextView tv_date;
  //@Bind(R.id.tv_total_portofolio) TextView tv_total;
  @Bind(R.id.tv_per_unit_dari_tanggal) TextView tv_per_unit_dari_tanggal;
  @Bind(R.id.tv_date_valuasi) TextView tv_date_valuasi;
  @Bind(R.id.tv_category) TextView tv_category;
  @Bind(R.id.tv_tgl_valuasi) TextView tv_tgl_valuasi;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_portofolio_detail);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    init();
  }

  private void init() {
    Bundle b = getIntent().getExtras();
    tv_tgl_valuasi.setText(b.getString("tanggal"));
    String portoName = b.getString("title", "");
    double totalSaldoAkhir = b.getDouble("saldo", 0.0);
    double jumlahUnit = b.getDouble("jumlah-unit", 0.0);
    double costPerUnit = b.getDouble("cost-per-unit");
    String date = b.getString("date", "not set");
    String category = b.getString("category", "");
    setData(portoName, totalSaldoAkhir, jumlahUnit, costPerUnit, date, category);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.btn_invest) void doInvest() {
    startActivity(new Intent(this, WebviewInstutisiActivity.class));
  }

  @OnClick(R.id.btn_info) void infoNAV() {
    Helper.showSimpleInformationDialog(getSupportFragmentManager(),
        getString(R.string.NAB_dialog_title), getString(R.string.NAB_dialog_explanation));
  }

  private void setData(String portoName, double saldo, double totalUnit, double costPerUnit,
      String date, String category) {

    //tv_total.setText(format.toRupiahFormatSimple(saldo));
    tv_porto_name.setText(portoName);
    tv_values_saldo.setText(format.toRupiahFormatSimple(saldo,true));
    //tv_cost_per_unit.setText(format.toRupiahFormatSimple(costPerUnit));
    tv_per_unit_dari_tanggal.setText(format.toRupiahFormatSimple(costPerUnit,true));
    //tv_date.setText(date);
    tv_date_valuasi.setText(date);
    tv_category.setText(category);
    tv_total_unit.setText(Helper.DecimalToString(totalUnit, 4));
    tv_cost_per_unit.setText(format.toRupiahFormatSimple(costPerUnit));
    tv_date.setText(date);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
