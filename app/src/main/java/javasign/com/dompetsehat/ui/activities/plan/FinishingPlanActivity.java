package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Invest;
import javasign.com.dompetsehat.models.ThirdParty;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.presenter.plan.FinisihingPlanInterface;
import javasign.com.dompetsehat.presenter.plan.FinisihingPlanPresenter;
import javasign.com.dompetsehat.ui.activities.institusi.dialog.VerificationDialog;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.TermsAndConditionManulife;
import javasign.com.dompetsehat.ui.activities.plan.adapter.AdapterFinishingPlan;
import javasign.com.dompetsehat.ui.activities.webview.WebLoaderActivity;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/26/16.
 */
public class FinishingPlanActivity extends BundlePlanActivity implements FinisihingPlanInterface {

  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Bind(R.id.tv_subheader) TextView tv_subheader;
  @Bind(R.id.tab_container) View tab_container;
  @Inject FinisihingPlanPresenter presenter;

  public static String TYPE = Words.TYPE;
  public static String MODE = "mode";
  public static String MODE_NEW = "new";
  public static String MODE_EDIT = "edit";
  public static String MODE_RECONNECT = "reconnect";
  public static String selectedMode = MODE;
  public static int selectedASUMSI = SetupPlanActivity.TYPE_CONSERVATIVE;

  public final static String TYPE_DANA_DARURAT = "Rencana Dana Darurat";
  public final static String TYPE_DANA_PENSIUN = "Rencana Dana Pensiun";
  public final static String TYPE_DANA_KULIAH = "Rencana Dana Kuliah";
  public final static String TYPE_DANA_CUSTOM = "Rencana Lainnya ";

  private AdapterFinishingPlan adapter;
  private List<AdapterFinishingPlan.ParentModel> parents = new ArrayList<>(2);
  private int typeAsumsi = -1;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_finishing_plan);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      tab_container.setElevation(0);
    }

    setTitle("");
    init();
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    IconicsTextView ic_menu = ButterKnife.findById(this, R.id.ic_menu);
    ic_menu.setText(getString(R.string.skip) + " ");
    ic_menu.setTextColor(Color.WHITE);
    ic_menu.setEnabled(true);
    ic_menu.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

    IconicsTextView ic_back = ButterKnife.findById(this, R.id.ic_back);
    ic_back.setTextColor(Color.WHITE);

    View simplebar = ButterKnife.findById(this, R.id.tab_container);
    simplebar.setBackgroundColor(getResources().getColor(R.color.blue_traveloka));
    GeneralHelper.statusBarColor(getWindow(), getResources().getColor(R.color.blue_traveloka));
  }

  @OnClick(R.id.ic_menu) void onSkip() {
    Intent intent = getIntent();
    if (selectedMode.equalsIgnoreCase(MODE_NEW)) {
      saveWithoutConnect(intent.getExtras().getString(TYPE));
    } else if (selectedMode.equalsIgnoreCase(MODE_EDIT)) {

    } else {
      finish();
    }
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recycleview.setLayoutManager(layoutManager);
    Intent intent = getIntent();
    if (intent.hasExtra(TYPE) && intent.hasExtra(MODE) && intent.hasExtra(SetupPlanActivity.TYPE_ASUMSI)) {
      selectedMode = intent.getExtras().getString(MODE);
      selectedASUMSI = intent.getExtras().getInt(SetupPlanActivity.TYPE_ASUMSI);
      switch (intent.getExtras().getString(TYPE)) {
        case TYPE_DANA_CUSTOM:
          String title = intent.getExtras().getString(CustomPlanActivity.TITLE);
          setTextSubheader(title);
          break;
        case TYPE_DANA_DARURAT:
          setTextSubheader(getString(R.string.emergency_fund));
          break;
        case TYPE_DANA_KULIAH:
          setTextSubheader(getString(R.string.college_fund));
          break;
        case TYPE_DANA_PENSIUN:
          setTextSubheader(getString(R.string.retired_fund));
          break;
      }
      presenter.loadData(selectedASUMSI);
    } else {
      finish();
    }
  }

  private void setTextSubheader(String from) {
    String result = getString(R.string.advice_to_open_account) + " " + from + " " + getString(
        R.string.this_account) + ".";
    tv_subheader.setText(result);
  }

  @Override public void setAccounts(List<ThirdParty> registerableAccountsInstitusi,
      List<Account> registeredAccount) {

    if (MyCustomApplication.showInvestasi()) {
      if (!Helper.isEmptyObject(registerableAccountsInstitusi)
          && registerableAccountsInstitusi.size() > 0) {
        AdapterFinishingPlan.ParentModel parentModelInvest = new AdapterFinishingPlan.ParentModel(getString(R.string.plan_finishing_title_open_new),registerableAccountsInstitusi);
        parents.add(parentModelInvest);
      }
    }

    if (!Helper.isEmptyObject(registeredAccount)) {
      AdapterFinishingPlan.ParentModel parentModelAccount = new AdapterFinishingPlan.ParentModel(
          getString(R.string.plan_finishing_title_connect_exist), registeredAccount);
      parents.add(parentModelAccount);
    }

    setAdapter();
  }

  @Override public void setInvests(List<Invest> invests) {

  }

  private void setAdapter() {
    if (Helper.isEmptyObject(adapter)) {
      adapter = new AdapterFinishingPlan(this, parents);
      recycleview.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }

    adapter.setOnItemClicked((v, object) -> {
      String type = "";
      if(getIntent().hasExtra(TYPE)){
        type = getIntent().getExtras().getString(TYPE);
      }

      if (object instanceof Account) {
        Account account = (Account) object;
        Vendor vendor = account.getVendor(FinishingPlanActivity.this);
        String vendorType = vendor.getVendor_type();
        Intent intent = getIntent();
        intent.putExtra(TYPE, type);
        intent.putExtra(DbHelper.VENDOR_TYPE, vendorType)
            .putExtra(ProductListNonInstitusiActivity.KEY_ID_ACCOUNT, account.getIdaccount());
        Helper.goTo(FinishingPlanActivity.this, ProductListNonInstitusiActivity.class, intent);
      } else if (object instanceof ThirdParty) {
        ThirdParty t = (ThirdParty) object;
        if(t.type == ThirdParty.TYPE_MANULIFE) {
          Intent intent = getIntent();
          intent.putExtra(DbHelper.VENDOR_TYPE, Vendor.TYPE_INVESTMENT);
          intent.putExtra(TYPE, type);
          intent.putExtra(MODE, MODE_NEW);
          Helper.trackThis(this,
              "user klik pilih buka rekening manulife  di halaman sambungkan rekening (dari flow semua rencana)");
          Helper.goTo(FinishingPlanActivity.this, ProductListInstitusiActivity.class, intent);
        }else{
          String utm_content = "AGR";
          if(selectedASUMSI == SetupPlanActivity.TYPE_MODERATE){
            utm_content = "MOD";
          }
          String url = "https://koinworks.com/lenders/new?utm_source=DSINV&utm_content="+utm_content;
          Helper.goTo(this,WebLoaderActivity.class,new Intent().putExtra("url",url));
          if(selectedASUMSI == SetupPlanActivity.TYPE_MODERATE) {
            Helper.trackThis(this, "User klik KoinWorks di menu rencana MODERATE ");
          }else {
            Helper.trackThis(this, "User klik KoinWorks di menu rencana AGGRESIVE");
          }
        }

      }
    });
  }

  private void saveWithoutConnect(String type) {
    Intent intent = getIntent();

    String title = "";
    int umur = 0;
    int umur_pensiun = 0;
    double pendapatan = 0;
    int lifetime = 0;

    // darurat
    int tahun = 0;
    int bulan = 0;
    double pengeluaran = 0.0;

    // kuliah
    String name = "";
    int lama_kuliah = 0;
    double uang_saku = 0.0;
    double biaya_kuliah = 0.0;

    double dana_disiapakan = 0;
    double cicilan = 0;
    double asumsi = 0;
    double cicilan_tahunan = 0;
    double lunas = 0.0;
    Timber.e("Type " + type);
    switch (type) {
      case TYPE_DANA_PENSIUN:
        umur = intent.getExtras().getInt(DanaPensiunActivity.UMUR);
        umur_pensiun = intent.getExtras().getInt(DanaPensiunActivity.UMUR_PENSIUN);
        pendapatan = intent.getExtras().getDouble(DanaPensiunActivity.PENDAPATAN_PERBULAN);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(SetupPlanActivity.BULAN_PERSIAPAN);
        presenter.saveDraft(umur, umur_pensiun, lifetime, pendapatan, dana_disiapakan, cicilan,
            cicilan_tahunan, lunas, asumsi, null, null);
        break;
      case TYPE_DANA_DARURAT:
        tahun = intent.getExtras().getInt(DanaDaruratActivity.TAHUN_PERSIAPAN);
        bulan = intent.getExtras().getInt(DanaDaruratActivity.BULAN_PENGGUNAAN);
        pengeluaran = intent.getExtras().getDouble(DanaDaruratActivity.PENGELUARAN_BULANAN);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(DanaDaruratActivity.TAHUN_PERSIAPAN) * 12;
        presenter.saveDraft(pengeluaran, lifetime, bulan, dana_disiapakan, cicilan, cicilan_tahunan,
            lunas, asumsi, null, null);
        break;
      case TYPE_DANA_CUSTOM:
        title = intent.getExtras().getString(CustomPlanActivity.TITLE);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(SetupPlanActivity.BULAN_PERSIAPAN);
        presenter.saveDraft(title, lifetime, dana_disiapakan, cicilan, cicilan_tahunan, lunas,
            asumsi, null, null);
        break;
      case TYPE_DANA_KULIAH:
        Timber.e("dana kuliah");
        name = intent.getExtras().getString(DanaKuliahActivity.NAMA_ANAK);
        lama_kuliah = intent.getExtras().getInt(DanaKuliahActivity.LAMA_KULIAH);
        umur = intent.getExtras().getInt(DanaKuliahActivity.UMUR);
        uang_saku = intent.getExtras().getDouble(DanaKuliahActivity.UANG_SAKU);
        biaya_kuliah = intent.getExtras().getDouble(DanaKuliahActivity.BIAYA_KULIAH);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        lifetime = intent.getExtras().getInt(SetupPlanActivity.BULAN_PERSIAPAN);
        presenter.saveDraft(name, umur, lifetime, lama_kuliah, biaya_kuliah, uang_saku,
            dana_disiapakan, cicilan, cicilan_tahunan, lunas, asumsi, null, null);
    }
    presenter.deleteSessionDraft(type);
    Helper.trackThis(this, "user telah berhasil membuat " + type);
    presenter.finishActivityWithPrevious(AddPlanEvent.ADD_PLANT_EVENT_SUCCESS);
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}