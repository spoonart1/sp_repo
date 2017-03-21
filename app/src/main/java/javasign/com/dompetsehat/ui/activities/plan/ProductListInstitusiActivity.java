package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Invest;
import javasign.com.dompetsehat.models.ThirdParty;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.presenter.plan.FinisihingPlanInterface;
import javasign.com.dompetsehat.presenter.plan.FinisihingPlanPresenter;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.VerificationDialog;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.ManulifeHomePageActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.RegisterMamiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.TermsAndConditionManulife;
import javasign.com.dompetsehat.ui.activities.plan.adapter.AdapterProductInstitusi;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/26/16.
 */
public class ProductListInstitusiActivity extends BundlePlanActivity
    implements FinisihingPlanInterface {

  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Bind(R.id.tv_subheader) TextView tv_subheader;
  @Bind(R.id.tv_institusi_name) TextView tv_institusi_name;
  @Bind(R.id.icon) IconicsTextView icon;
  @Bind(R.id.btn_next) Button btn_next;
  @Bind(R.id.ll_empty_view) View ll_empty_view;

  public static final String ASUMSI = "asumsi";

  public String selectedMode = "new";

  private int typeAsumsi = -1;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private AdapterProductInstitusi adapterProductInstitusi;
  @Inject FinisihingPlanPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product_list_institusi);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);

    setTitle("");
    init();
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.btn_next) void onNext() {
    //save();
    //openExplainerDialog(false);
    VerificationDialog verificationDialog =
        VerificationDialog.newInstance((dialog2, phone, email) -> {
          save();
          dialog2.dismissAllowingStateLoss();
          Helper.goTo(this, TermsAndConditionManulife.class);
        });
    verificationDialog.show(getSupportFragmentManager(), "dialog-verify");
    RegisterMamiActivity.accessFrom = getIntent().getExtras().getString(FinishingPlanActivity.TYPE);
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
      save();
      startActivity(
          new Intent(this, ManulifeHomePageActivity.class).putExtra("action", isTryToLogin));
    });
    dialog.show(getSupportFragmentManager(), "explainer-dialog");
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.btn_computer) void continueAtComputer(){
    //Toast.makeText(this, "Lanjutkan di komputer, iki dibusak yo", Toast.LENGTH_SHORT).show();
  }

  @OnClick(R.id.btn_myreferral) void continueAtMyReferra(){
    //Toast.makeText(this, "Lanjutkan di my referral, iki dibusak yo", Toast.LENGTH_SHORT).show();
  }

  private void init() {

    int vendorId = AccountView.MNL; //seharus e nanti via intent getIntExtra, kalau investasi nya ada selain Manulife
    tv_subheader.setText(getSubheaderTextById(vendorId));
    icon.setText(AccountView.iconVendor.get(vendorId));
    icon.setTextColor(AccountView.accountColor.get(vendorId));
    tv_institusi_name.setText(AccountView.institusi.get(vendorId));

    if (getIntent().hasExtra(FinishingPlanActivity.MODE)) {
      selectedMode = getIntent().getExtras().getString(FinishingPlanActivity.MODE);
    } else {
      finish();
    }

    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recycleview.setLayoutManager(layoutManager);
    typeAsumsi = getIntent().getIntExtra(SetupPlanActivity.TYPE_ASUMSI, typeAsumsi);

    initAdapter();
  }

  private void initAdapter() {
    if (getIntent().hasExtra(SetupPlanActivity.TYPE_ASUMSI)) {
      int type = getIntent().getExtras().getInt(SetupPlanActivity.TYPE_ASUMSI);
      presenter.loadDataInvest(type);
    } else {
      finish();
    }
  }

  private void save() {
    List<Invest> invests = adapterProductInstitusi.getAllSelectedInvests();
    if (invests.size() > 0) {
      ll_empty_view.setVisibility(View.GONE);
      Integer[] ids = new Integer[invests.size()];
      int i = 0;
      for (Invest invest : invests) {
        ids[i] = invest.getId_ivest();
        i++;
      }
      String type = getIntent().getExtras().getString(FinishingPlanActivity.TYPE);
      if (selectedMode.equalsIgnoreCase(FinishingPlanActivity.MODE_NEW)) {
        SaveInvestasi(type, ids);
      } else if (selectedMode.equalsIgnoreCase(FinishingPlanActivity.MODE_EDIT)) {

      } else {
        if (getIntent().hasExtra(DetailPlanActivity.PLAN_ID_LOCAL)) {
          int plan_id = getIntent().getExtras().getInt(DetailPlanActivity.PLAN_ID_LOCAL);
          presenter.updatePlanProduct(plan_id, ids);
        } else {
          finish();
        }
      }
      rxBus.send(new AddPlanEvent(getApplicationContext(), AddPlanEvent.ADD_PLANT_EVENT_SUCCESS));
    } else{
      ll_empty_view.setVisibility(View.VISIBLE);
    }
  }

  public void SaveInvestasi(String type, Integer[] listproduct) {
    Intent intent = getIntent();
    // pensiun
    int umur = 0;
    int umur_pensiun = 0;
    double pendapatan = 0;

    // darurat
    int tahun = 0;
    int bulan = 0;
    double pengeluaran = 0.0;

    //custome
    String title = "";

    // kuliah
    String name = "";
    int lama_kuliah = 0;
    double uang_saku = 0.0;
    double biaya_kuliah = 0.0;

    double dana_disiapakan = 0;
    double cicilan = 0;
    double cicilan_tahunan = 0;
    double lunas = 0.0;
    double asumsi = 0;
    int lifetime = 0;
    switch (type) {
      case FinishingPlanActivity.TYPE_DANA_PENSIUN:
        umur = intent.getExtras().getInt(DanaPensiunActivity.UMUR);
        umur_pensiun = intent.getExtras().getInt(DanaPensiunActivity.UMUR_PENSIUN);
        pendapatan = intent.getExtras().getDouble(DanaPensiunActivity.PENDAPATAN_PERBULAN);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(SetupPlanActivity.BULAN_PERSIAPAN);
        presenter.saveProductInvestasi(umur, umur_pensiun, lifetime, pendapatan, dana_disiapakan,
            cicilan, cicilan_tahunan, lunas, asumsi, null, listproduct);
        break;
      case FinishingPlanActivity.TYPE_DANA_DARURAT:
        tahun = intent.getExtras().getInt(DanaDaruratActivity.TAHUN_PERSIAPAN);
        bulan = intent.getExtras().getInt(DanaDaruratActivity.BULAN_PENGGUNAAN);
        pengeluaran = intent.getExtras().getDouble(DanaDaruratActivity.PENGELUARAN_BULANAN);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(DanaDaruratActivity.TAHUN_PERSIAPAN) * 12;
        presenter.saveProductInvestasi(pengeluaran, lifetime, tahun, bulan, dana_disiapakan,
            cicilan, cicilan_tahunan, lunas, asumsi, null, listproduct);
        break;
      case FinishingPlanActivity.TYPE_DANA_CUSTOM:
        title = intent.getExtras().getString(CustomPlanActivity.TITLE);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(SetupPlanActivity.BULAN_PERSIAPAN);
        presenter.saveProductInvestasi(title, lifetime, dana_disiapakan, cicilan, cicilan_tahunan,
            lunas, asumsi, null, listproduct);
        break;
      case FinishingPlanActivity.TYPE_DANA_KULIAH:
        name = intent.getExtras().getString(DanaKuliahActivity.NAMA_ANAK);
        lama_kuliah = intent.getExtras().getInt(DanaKuliahActivity.LAMA_KULIAH);
        umur = intent.getExtras().getInt(DanaKuliahActivity.UMUR);
        uang_saku = intent.getExtras().getDouble(DanaKuliahActivity.UANG_SAKU);
        biaya_kuliah = intent.getExtras().getDouble(DanaKuliahActivity.BIAYA_KULIAH);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(SetupPlanActivity.BULAN_PERSIAPAN);
        presenter.saveProductInvestasi(name, umur, lifetime, lama_kuliah, biaya_kuliah, uang_saku,
            dana_disiapakan, cicilan, cicilan_tahunan, lunas, asumsi, null, listproduct);
    }
  }

  private String getSubheaderTextById(int vendorId) {
    return getString(R.string.choose_recommended_institusi_by) + " \n" + AccountView.institusi.get(
        vendorId) + ".";
  }

  @Override
  public void setAccounts(List<ThirdParty> registerableIntitusi, List<Account> registeredAccount) {

  }

  @Override public void setInvests(List<Invest> invests) {
    runOnUiThread(() -> {
      adapterProductInstitusi = new AdapterProductInstitusi(invests);
      adapterProductInstitusi.setDependentView(btn_next);
      recycleview.setAdapter(adapterProductInstitusi);
      adapterProductInstitusi.notifyDataSetChanged();
    });
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}