package javasign.com.dompetsehat.ui.activities.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.ThirdParty;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.presenter.plan.ChooseProductInterface;
import javasign.com.dompetsehat.presenter.plan.ChooseProductPresenter;
import javasign.com.dompetsehat.presenter.plan.FinisihingPlanInterface;
import javasign.com.dompetsehat.presenter.plan.FinisihingPlanPresenter;
import javasign.com.dompetsehat.ui.activities.account.ManageEachAccountActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.plan.adapter.AdapterNonInstitusi;
import javasign.com.dompetsehat.ui.activities.portofolio.PortofolioActivity;
import javasign.com.dompetsehat.ui.activities.referral.ReferralLoaderActivity;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/26/16.
 */
public class ProductListNonInstitusiActivity extends BundlePlanActivity
    implements FinisihingPlanInterface, ChooseProductInterface {

  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Bind(R.id.tv_header) TextView tv_header;
  @Bind(R.id.tv_subheader) TextView tv_subheader;
  @Bind(R.id.ll_bottom_info) LinearLayout ll_bottom_info;
  @Bind(R.id.icon) IconicsTextView icon;
  @Bind(R.id.btn_next) Button btn_next;
  @Bind(R.id.btn_computer) Button btn_computer;
  @Bind(R.id.btn_myreferral) Button btn_myreferral;
  @Bind(R.id.btn_portofolio_baru) Button btn_portofolio_baru;
  @Bind(R.id.ll_empty_view) LinearLayout ll_empty_view;

  public String selectedMode = "new";

  private RxBus rxBus = MyCustomApplication.getRxBus();
  private AdapterNonInstitusi adapter;
  public static final String KEY_ID_ACCOUNT = "id_account";
  @Inject FinisihingPlanPresenter presenter;
  @Inject ChooseProductPresenter presenterProduct;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product_list_institusi);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);
    presenterProduct.attachView(this);
    setTitle("");
    init();
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.btn_next) void onNext() {
    save();
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recycleview.setLayoutManager(layoutManager);
    if (getIntent().hasExtra(FinishingPlanActivity.MODE)) {
      selectedMode = getIntent().getExtras().getString(FinishingPlanActivity.MODE);
    } else {
      finish();
    }
    String vendor_type = getIntent().getExtras().getString(DbHelper.VENDOR_TYPE, Vendor.TYPE_INVESTMENT);
    tv_subheader.setText(getString(R.string.connected_account_is_easy_to_manage));
    if (vendor_type.equalsIgnoreCase(Vendor.TYPE_INVESTMENT)) {
      ll_bottom_info.setVisibility(View.VISIBLE);
      icon.setText(AccountView.iconVendor.get(AccountView.MNL));
      icon.setTextColor(AccountView.accountColor.get(AccountView.MNL));
      presenterProduct.initAdapter(getIntent().getExtras().getInt(KEY_ID_ACCOUNT));
    } else {
      ll_bottom_info.setVisibility(View.GONE);
      presenterProduct.initNonAdapter(getIntent().getExtras().getInt(KEY_ID_ACCOUNT));
    }
  }

  @Override public void setListNonIntitusi(Account account, List<Product> list, List<Integer> ids) {
    runOnUiThread(() -> {
      Timber.e("avelina " + list.size());
      adapter = new AdapterNonInstitusi(account, list).setDependentView(btn_next);
      recycleview.setAdapter(adapter.setChoosed_products(ids));
    });
  }

  @Override public void setListIntitusi(Account account, List<Product> list, List<Integer> ids,boolean is_show) {
    runOnUiThread(() -> {
      boolean isInvalid = false;
      if (list.size() == 0) {
        isInvalid = true;
      } else {
        isInvalid = true;
        for (Product p : list) {
          if (p.getBalance() > 0) {
            isInvalid = false;
          }
        }
      }
      if(isInvalid){
        ll_empty_view.setVisibility(View.VISIBLE);
        btn_computer.setVisibility(View.VISIBLE);
        btn_myreferral.setVisibility(View.VISIBLE);
        btn_portofolio_baru.setVisibility(View.GONE);
      }else if(is_show){
        btn_computer.setVisibility(View.GONE);
        btn_myreferral.setVisibility(View.GONE);
        btn_portofolio_baru.setVisibility(View.VISIBLE);
        ll_empty_view.setVisibility(View.VISIBLE);
      }else{
        adapter = new AdapterNonInstitusi(account, list).setDependentView(btn_next);
        recycleview.setAdapter(adapter.setMultiSelected(true).setChoosed_products(ids));
      }
    });
  }

  @OnClick(R.id.btn_computer) void keComputer() {
    String type = getIntent().getExtras().getString(FinishingPlanActivity.TYPE);
    saveWithoutConnect(type);
  }

  @OnClick(R.id.btn_myreferral) void keReferral() {
    //Helper.goTo(this, ReferralLoaderActivity.class);
    Helper.finishAllPreviousActivityWithNextTarget(this, ManageEachAccountActivity.class,
        new Intent()
            .putExtra(ManageEachAccountActivity.KEY_ID_ACCOUNT,getIntent().getExtras().getInt(KEY_ID_ACCOUNT))
            .putExtra("from", "login"));
  }

  @OnClick(R.id.btn_portofolio_baru) void kePortoBaru(){
    Helper.goTo(this, PortofolioActivity.class);
  }

  private void save() {
    Timber.e("Save products");
    Product product = adapter.getProductChecked();
    List<Product> products = adapter.getProductsChecked();
    if (product != null || products != null) {
      if (selectedMode.equalsIgnoreCase(FinishingPlanActivity.MODE_NEW)) {
        String vendor_type = getIntent().getExtras().getString(DbHelper.VENDOR_TYPE, Vendor.TYPE_INVESTMENT);
        int account_id = getIntent().getExtras().getInt(KEY_ID_ACCOUNT);
        if (vendor_type.equalsIgnoreCase(Vendor.TYPE_INVESTMENT) && products != null) {
          Integer[] datas = new Integer[products.size()];
          int i = 0;
          for (Product p : products) {
            datas[i] = p.getId_product();
            i++;
          }
          Timber.e("Aku di sini");
          SaveInvestasi(getIntent().getExtras().getString(FinishingPlanActivity.TYPE), account_id,datas);
        } else if(product != null) {
          SaveAndConnectProduct(account_id, product.getId_product());
        }else{
          Toast.makeText(this,getString(R.string.error_account_not_found),Toast.LENGTH_LONG).show();
        }
      } else if (selectedMode.equalsIgnoreCase(FinishingPlanActivity.MODE_EDIT)) {

      } else {
        if (getIntent().hasExtra(DetailPlanActivity.PLAN_ID_LOCAL)) {
          Timber.e("Heloo");
          int plan_id = getIntent().getExtras().getInt(DetailPlanActivity.PLAN_ID_LOCAL);
          UpdateConnectProduct(plan_id, product.getId_product());
        } else {
          finish();
        }
      }
      rxBus.send(new AddPlanEvent(getApplicationContext(), AddPlanEvent.ADD_PLANT_EVENT_SUCCESS));
    } else {
      finish();
    }
  }

  public void UpdateConnectProduct(int plan_id_local, int product_id) {
    presenter.updateConnectProduct(plan_id_local, product_id);
  }

  public void SaveInvestasi(String type, int account_id, Integer[] listproduct) {
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
            cicilan, cicilan_tahunan, lunas, asumsi, account_id, listproduct);

        Helper.trackThis(this, "user berhasil menyambungkan rencana Dana Pensiun dengan Manulife");
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
            cicilan, cicilan_tahunan, lunas, asumsi, account_id, listproduct);

        Helper.trackThis(this, "user berhasil menyambungkan rencana Dana Darurat dengan Manulife");
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
            lunas, asumsi, account_id, listproduct);
        Helper.trackThis(this, "user berhasil menyambungkan rencana lainnya dengan Manulife");
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
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        presenter.saveProductInvestasi(name, umur, lifetime, lama_kuliah, biaya_kuliah, uang_saku,
            dana_disiapakan, cicilan, cicilan_tahunan, lunas, asumsi, account_id, listproduct);
        Helper.trackThis(this, "user berhasil menyambungkan rencana Dana Kuliah dengan Manulife");
    }
  }

  public void SaveAndConnectProduct(int account_id, int product_id) {
    Intent intent = getIntent();

    String title = "";
    int umur = 0;
    int umur_pensiun = 0;
    double pendapatan = 0;

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
    double cicilan_tahunan = 0;
    double lunas = 0.0;
    double asumsi = 0;
    int lifetime = 0;

    String type = intent.getExtras().getString(FinishingPlanActivity.TYPE);
    switch (type) {
      case FinishingPlanActivity.TYPE_DANA_PENSIUN:
        Timber.e("JSON avesina product_id "+product_id);
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
            cicilan_tahunan, lunas, asumsi, account_id, product_id);
        break;

      case FinishingPlanActivity.TYPE_DANA_DARURAT:
        bulan = intent.getExtras().getInt(DanaDaruratActivity.BULAN_PENGGUNAAN);
        pengeluaran = intent.getExtras().getDouble(DanaDaruratActivity.PENGELUARAN_BULANAN);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(DanaDaruratActivity.TAHUN_PERSIAPAN) * 12;
        presenter.saveDraft(pengeluaran, lifetime, bulan, dana_disiapakan, cicilan, cicilan_tahunan,
            lunas, asumsi, account_id, product_id);
        break;

      case FinishingPlanActivity.TYPE_DANA_CUSTOM:
        title = intent.getExtras().getString(CustomPlanActivity.TITLE);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        cicilan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_BULANAN);
        cicilan_tahunan = intent.getExtras().getDouble(SetupPlanActivity.CICILAN_TAHUNAN);
        lunas = intent.getExtras().getDouble(SetupPlanActivity.DIBAYAR_LUNAS);
        asumsi = intent.getExtras().getDouble(SetupPlanActivity.PERSENTASE_ASUMSI);
        lifetime = intent.getExtras().getInt(SetupPlanActivity.BULAN_PERSIAPAN);
        presenter.saveDraft(title, lifetime, dana_disiapakan, cicilan, cicilan_tahunan, lunas,
            asumsi, account_id, product_id);
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
        lifetime = intent.getExtras().getInt(SetupPlanActivity.BULAN_PERSIAPAN);
        dana_disiapakan = intent.getExtras().getDouble(SetupPlanActivity.DANA_DISIAPKAN);
        presenter.saveDraft(name, lifetime, umur, lama_kuliah, biaya_kuliah, uang_saku,
            dana_disiapakan, cicilan, cicilan_tahunan, lunas, asumsi, account_id, product_id);
    }
    presenter.deleteSessionDraft(type);
  }

  @Override public void getHeaderTextById(int vendor_id) {
    runOnUiThread(() -> {
      String text = "Rekening " + AccountView.vendors.get(vendor_id);
      tv_header.setText(text);
    });
  }

  @Override
  public void setAccounts(List<ThirdParty> registerableIntitusi, List<Account> registeredAccount) {

  }

  @Override public void setInvests(List<Invest> invests) {

  }

  @Override protected void onDestroy() {
    if (presenterProduct != null) {
      presenterProduct.detachView();
    }
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
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
        presenter.saveDraft(umur, umur_pensiun, lifetime, pendapatan, dana_disiapakan, cicilan,
            cicilan_tahunan, lunas, asumsi, null, null);
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
        presenter.saveDraft(pengeluaran, lifetime, bulan, dana_disiapakan, cicilan, cicilan_tahunan,
            lunas, asumsi, null, null);
        break;
      case FinishingPlanActivity.TYPE_DANA_CUSTOM:
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
      case FinishingPlanActivity.TYPE_DANA_KULIAH:
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
    Helper.trackThis(this, "User telah berhasil menambahkan " + type);
    presenter.finishActivityWithPrevious(AddPlanEvent.ADD_PLANT_EVENT_SUCCESS);
  }
}