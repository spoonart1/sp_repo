package javasign.com.dompetsehat.presenter.plan;

import android.content.Context;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BasePresenter;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.injection.ApplicationContext;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.DanaDarurat;
import javasign.com.dompetsehat.models.DanaKuliah;
import javasign.com.dompetsehat.models.DanaPensiun;
import javasign.com.dompetsehat.models.Invest;
import javasign.com.dompetsehat.models.Plan;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.ProductPlan;
import javasign.com.dompetsehat.models.ThirdParty;
import javasign.com.dompetsehat.models.Vendor;
import javasign.com.dompetsehat.ui.activities.plan.SetupPlanActivity;
import javasign.com.dompetsehat.ui.activities.plan.adapter.AdapterFinishingPlan;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static javasign.com.dompetsehat.ui.activities.plan.FinishingPlanActivity.TYPE_DANA_CUSTOM;
import static javasign.com.dompetsehat.ui.activities.plan.FinishingPlanActivity.TYPE_DANA_DARURAT;
import static javasign.com.dompetsehat.ui.activities.plan.FinishingPlanActivity.TYPE_DANA_KULIAH;
import static javasign.com.dompetsehat.ui.activities.plan.FinishingPlanActivity.TYPE_DANA_PENSIUN;
import static javasign.com.dompetsehat.ui.activities.plan.SetupPlanActivity.TYPE_AGGRESIVE;
import static javasign.com.dompetsehat.ui.activities.plan.SetupPlanActivity.TYPE_CONSERVATIVE;
import static javasign.com.dompetsehat.ui.activities.plan.SetupPlanActivity.TYPE_MODERATE;

/**
 * Created by avesina on 9/9/16.
 */

public class FinisihingPlanPresenter extends BasePresenter<FinisihingPlanInterface> {
  private Context context;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  MCryptNew mcrypt = new MCryptNew();
  private final DataManager dataManager;
  private final SessionManager session;
  private GeneralHelper helper = GeneralHelper.getInstance();
  private DbHelper db;
  private RxBus rxBus = MyCustomApplication.getRxBus();

  Integer[] Conservative = { 1 };
  Integer[] Moderate = { 2, 3 };
  Integer[] Aggresive = { 4, 5 };

  @Inject public FinisihingPlanPresenter(@ApplicationContext Context context, DataManager dataManager, DbHelper db) {
    this.context = context;
    this.dataManager = dataManager;
    this.session = new SessionManager(context);
    this.db = db;
  }

  @Override public void attachView(FinisihingPlanInterface mvpView) {
    super.attachView(mvpView);
    compositeSubscription.add(
        rxBus.toObserverable().ofType(AddPlanEvent.class).subscribe(addPlanActivity -> {
        }, throwable -> {
        }, () -> {
          getMvpView().onComplete(AddPlanEvent.ADD_PLANT_EVENT_SUCCESS);
        }));
  }

  @Override public void detachView() {
    super.detachView();
    if (compositeSubscription != null) {
      compositeSubscription.unsubscribe();
    }
  }

  public void loadData(int type_asumsi) {
    compositeSubscription.add(getRegisteredAccount().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(list -> {
          getMvpView().setAccounts(getRegisterableAccountInstitusi(type_asumsi), list);
        }, throwable -> {
          Timber.e("ERROR " + throwable.getStackTrace());
        }, () -> {
        }));
  }

  private List<ThirdParty> getRegisterableAccountInstitusi(int type_asumsi) {
    ArrayList<ThirdParty> modes = new ArrayList<>();
    if (!session.ihaveAnInstutitionAccount()) {
      Vendor vendor = db.getVendorsByID(AccountView.MNL);
      ThirdParty thirdParty = new ThirdParty();
      thirdParty.id = vendor.getId();
      thirdParty.type = ThirdParty.TYPE_MANULIFE;
      thirdParty.name = vendor.getVendor_name();
      thirdParty.icon = AccountView.iconVendor.get(AccountView.MNL);
      thirdParty.color = AccountView.accountColor.get(AccountView.MNL);
      modes.add(thirdParty);
    }

    if(type_asumsi == SetupPlanActivity.TYPE_MODERATE || type_asumsi == SetupPlanActivity.TYPE_AGGRESIVE) {
      ThirdParty thirdParty2 = new ThirdParty();
      thirdParty2.id = 0;
      thirdParty2.type = ThirdParty.TYPE_KOINWORK;
      thirdParty2.name = "P2P Fintech Lending Koinwork ";
      thirdParty2.icon = DSFont.Icon.dsf_koinworks.getFormattedName();
      thirdParty2.color = AccountView.KW_COLOR;
      modes.add(thirdParty2);
    }

    return modes;
  }

  public Observable<ArrayList<Account>> getRegisteredAccount() {
    ArrayList<Account> accounts = db.getAllAccountByUser(session.getIdUser());
    ArrayList<Account> newAccounts = new ArrayList<>();
    for (Account a : accounts) {
      String real = mcrypt.decrypt(a.getName());
      a.setName(real);
      double saldo = 0;
      if(a.products != null){
        for(Product product:a.products){
          saldo += product.getBalance();
        }
      }
      a.saldo = saldo;
      newAccounts.add(a);
    }
    return Observable.just(newAccounts);
  }

  public boolean isProductUsed(List<Plan> plans,List<Product> products){
    ArrayList<Integer> ids_plan = new ArrayList<>();
    for (Plan p:plans){
      ids_plan.add(p.getAccount_id());
    }
    Timber.e("avesina ids "+ids_plan.get(ids_plan.size()-1));
    for(Product product:products){
      if(!ids_plan.contains(product.getId_product())){
        return false;
      }
    }
    return true;
  }

  public void loadDataInvest(int type_asumsi) {
    compositeSubscription.add(getList(type_asumsi).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(list -> {
          getMvpView().setInvests(list);
        }, throwable -> {
          Timber.e("ERROR " + throwable);
        }, () -> {
        }));
  }

  public List<Product> getProductsFromAccount() {
    List<Product> productList = new ArrayList<>();
    productList.add(getDompetAsProduct());
    return productList;
  }

  public Product getDompetAsProduct() {
    Product p = new Product().setAccount_id(AccountView.DP)
        .setColor(AccountView.DP_COLOR)
        .setName(AccountView.DP_S);
    return p;
  }

  public Observable<ArrayList<Invest>> getList(int type_asumsi) {
    ArrayList<Invest> invests = db.getListInvest();
    ArrayList<Invest> data = new ArrayList<>();
    Integer[] filters = {};
    switch (type_asumsi) {
      case TYPE_CONSERVATIVE:
        filters = Conservative;
        break;
      case TYPE_MODERATE:
        filters = Moderate;
        break;
      case TYPE_AGGRESIVE:
        filters = Aggresive;
        break;
    }
    for (Invest invest : invests) {
      if (Arrays.asList(filters).contains(invest.getId_ivest())) {
        data.add(invest);
      }
    }
    return Observable.just(data);
  }

  public Product generateProduct(String name, Invest invest) {
    Product p = new Product();
    p.setName(name);
    p.setInvest(invest);
    return p;
  }

  // save dana pensiun
  public int saveDraft(int umur, int umur_pensiun, int tahun, double pendapatan,
      double dana_disiapakan, double cicilan_bulanan, double cicilan_tahun, double lunas,
      double asumsi, Integer account_id, Integer product_id) {
    Plan plan = new Plan();
    plan.setPlan_title("Dana Pensiun")
        .setType(db.PLAN_TYPE_PENSIUN)
        .setId_plan(-1)
        .setAccount_id(account_id)
        .setProduct_id(product_id)
        .setUser_id(Integer.valueOf(session.getIdUser()))
        .setPlan_total((float) dana_disiapakan)
        .setPlan_amount_monthly((float) cicilan_bulanan)
        .setPlan_amount_yearly((float) cicilan_tahun)
        .setPlan_amount_cash((float) lunas)
        .setLifetime(tahun)
        .setPlan_risk(asumsi);
    int id = (int) db.newPlan(plan);
    DanaPensiun danaPensiun = new DanaPensiun();
    danaPensiun.setId_plan_local(id)
        .setId_plan(-1)
        .setId_dana_pensiun(-1)
        .setPendapatan((float) pendapatan)
        .setUmur(umur)
        .setUmur_pensiun(umur_pensiun);
    db.newDanaPensiun(danaPensiun);
    Timber.e("JSON sebelum"+product_id);
    if(product_id != null) {
      Timber.e("JSON sesudah"+product_id);
      ProductPlan planProduct = new ProductPlan();
      planProduct.setId_product_plan(-1).setPlan_id(-1).setPlan_id_local(id).setProduct_id(product_id);
      db.newPlanProduct(planProduct);
    }
    return id;
  }

  // save dana darurat
  public int saveDraft(double pengeluaran, int lifetime, int bulan,
      double dana_disiapakan, double cicilan_bulanan, double cicilan_tahun, double lunas,
      double asumsi, Integer account_id, Integer product_id) {
    Plan plan = new Plan();
    plan.setPlan_title("Dana Darurat")
        .setType(db.PLAN_TYPE_DARURAT)
        .setId_plan(-1)
        .setAccount_id(account_id)
        .setProduct_id(product_id)
        .setUser_id(Integer.valueOf(session.getIdUser()))
        .setPlan_total((float) dana_disiapakan)
        .setPlan_amount_monthly((float) cicilan_bulanan)
        .setPlan_amount_yearly((float) cicilan_tahun)
        .setPlan_amount_cash((float) lunas)
        .setLifetime(lifetime)
        .setPlan_risk(asumsi);
    int id = (int) db.newPlan(plan);
    DanaDarurat danaDarurat = new DanaDarurat();
    danaDarurat.setId_plan(-1)
        .setId_plan_local(id)
        .setId_dana_darurat(-1)
        .setBulan_penggunaan(bulan)
        .setPengeluaran_bulanan(pengeluaran);
    db.newDanaDarurat(danaDarurat);
    if(product_id != null) {
      ProductPlan planProduct = new ProductPlan();
      planProduct.setId_product_plan(-1).setPlan_id(-1).setPlan_id_local(id).setProduct_id(product_id);
      db.newPlanProduct(planProduct);
    }
    return id;
  }

  public int saveDraft(String nama_anak, int usia_anak, int tahun, int lama_kuliah,
      double biaya_kuliah, double uang_saku, double dana_disiapakan, double cicilan_bulanan,
      double cicilan_tahun, double lunas, double asumsi, Integer account_id, Integer product_id) {
    Plan plan = new Plan();
    plan.setPlan_title("Dana Kuliah " + nama_anak)
        .setType(db.PLAN_TYPE_KULIAH)
        .setId_plan(-1)
        .setUser_id(Integer.valueOf(session.getIdUser()))
        .setAccount_id(account_id)
        .setProduct_id(product_id)
        .setPlan_total((float) dana_disiapakan)
        .setPlan_amount_monthly((float) cicilan_bulanan)
        .setPlan_amount_yearly((float) cicilan_tahun)
        .setPlan_amount_cash((float) lunas)
        .setLifetime(tahun)
        .setPlan_risk(asumsi);
    int id = (int) db.newPlan(plan);
    DanaKuliah danaKuliah = new DanaKuliah();
    danaKuliah.setId_plan_local(id)
        .setId_plan(-1)
        .setId_dana_kuliah(-1)
        .setNama_anak(nama_anak)
        .setUsia_anak(usia_anak)
        .setLama_kuliah(lama_kuliah)
        .setBiaya_kuliah(biaya_kuliah)
        .setUang_saku(uang_saku);
    db.newDanaKuliah(danaKuliah);
    if(product_id != null) {
      ProductPlan planProduct = new ProductPlan();
      planProduct.setId_product_plan(-1).setPlan_id(-1).setPlan_id_local(id).setProduct_id(product_id);
      db.newPlanProduct(planProduct);
    }
    return id;
  }

  // save dana custome
  public int saveDraft(String title, int tahun, double dana_disiapakan, double cicilan_bulanan,
      double cicilan_tahun, double lunas, double asumsi, Integer account_id, Integer product_id) {
    Plan plan = new Plan();
    plan.setPlan_title(title)
        .setType(db.PLAN_TYPE_CUSTOME)
        .setId_plan(-1)
        .setUser_id(Integer.valueOf(session.getIdUser()))
        .setAccount_id(account_id)
        .setProduct_id(product_id)
        .setPlan_total((float) dana_disiapakan)
        .setPlan_amount_monthly((float) cicilan_bulanan)
        .setPlan_amount_yearly((float) cicilan_tahun)
        .setPlan_amount_cash((float) lunas)
        .setLifetime(tahun)
        .setPlan_risk(asumsi);
    int id = (int) db.newPlan(plan);
    if(product_id != null) {
      ProductPlan planProduct = new ProductPlan();
      planProduct.setId_product_plan(-1).setPlan_id(-1).setPlan_id_local(id).setProduct_id(product_id);
      db.newPlanProduct(planProduct);
    }
    return id;
  }

  // save product dana pensiun
  public void saveProductInvestasi(int umur, int umur_pensiun, int tahun, double pendapatan,
      double dana_disiapakan, double cicilan_bulanan, double cicilan_tahun, double lunas,
      double asumsi, Integer account_id, Integer[] products) {
    int id = saveDraft(umur, umur_pensiun, tahun, pendapatan, dana_disiapakan, cicilan_bulanan,
        cicilan_tahun, lunas, asumsi, account_id, null);
    for (Integer product : products) {
      if (account_id == null) {
        ProductPlan planProduct = new ProductPlan();
        planProduct.setId_product_plan(-1)
            .setPlan_id(-1)
            .setPlan_id_local(id)
            .setInvest_id(product);
        db.newPlanProduct(planProduct);
      } else {
        ProductPlan planProduct = new ProductPlan();
        planProduct.setId_product_plan(-1)
            .setPlan_id(-1)
            .setPlan_id_local(id)
            .setProduct_id(product);
        db.newPlanProduct(planProduct);
      }
    }
  }

  // save product dana darurat
  public void saveProductInvestasi(double pengeluaran, int lifetime, int tahun, int bulan,
      double dana_disiapakan, double cicilan_bulanan, double cicilan_tahun, double lunas,
      double asumsi, Integer account_id, Integer[] products) {
    int id = saveDraft(pengeluaran, lifetime, bulan, dana_disiapakan, cicilan_bulanan,
        cicilan_tahun, lunas, asumsi, account_id, null);
    for (Integer product : products) {
      if (account_id == null) {
        ProductPlan planProduct = new ProductPlan();
        planProduct.setId_product_plan(-1)
            .setPlan_id(-1)
            .setPlan_id_local(id)
            .setInvest_id(product);
        db.newPlanProduct(planProduct);
      } else {
        ProductPlan planProduct = new ProductPlan();
        planProduct.setId_product_plan(-1)
            .setPlan_id(-1)
            .setPlan_id_local(id)
            .setProduct_id(product);
        db.newPlanProduct(planProduct);
      }
    }
  }

  public void saveProductInvestasi(String title, int tahun, double dana_disiapakan,
      double cicilan_bulanan, double cicilan_tahun, double lunas, double asumsi, Integer account_id,
      Integer[] listproduct) {
    int id = saveDraft(title, tahun, dana_disiapakan, cicilan_bulanan, cicilan_tahun, lunas, asumsi,
        account_id, null);
    for (Integer product : listproduct) {
      if (account_id == null) {
        ProductPlan planProduct = new ProductPlan();
        planProduct.setId_product_plan(-1)
            .setPlan_id(-1)
            .setPlan_id_local(id)
            .setInvest_id(product);
        db.newPlanProduct(planProduct);
      } else {
        ProductPlan planProduct = new ProductPlan();
        planProduct.setId_product_plan(-1)
            .setPlan_id(-1)
            .setPlan_id_local(id)
            .setProduct_id(product);
        db.newPlanProduct(planProduct);
      }
    }
  }

  //public void saveProductInvestasi(String title, int tahun, double dana_disiapakan,
  //    double cicilan_bulanan, double cicilan_tahun, double lunas, double asumsi, int account_id,
  //    Integer[] listproduct) {
  //  int id = saveDraft(title, tahun, dana_disiapakan, cicilan_bulanan, cicilan_tahun, lunas, asumsi,
  //      account_id, null);
  //  for (Integer product : listproduct) {
  //    ProductPlan planProduct = new ProductPlan();
  //    planProduct.setId_product_plan(-1).setPlan_id(-1).setPlan_id_local(id).setInvest_id(product);
  //    db.newPlanProduct(planProduct);
  //  }
  //}

  public void saveProductInvestasi(String nama_anak, int usia_anak, int tahun, int lama_kuliah,
      double biaya_kuliah, double uang_saku, double dana_disiapakan, double cicilan_bulanan,
      double cicilan_tahun, double lunas, double asumsi, Integer account_id,
      Integer[] listproduct) {
    int id = saveDraft(nama_anak, usia_anak, tahun, lama_kuliah, biaya_kuliah, uang_saku,
        dana_disiapakan, cicilan_bulanan, cicilan_tahun, lunas, asumsi, account_id, null);
    for (Integer product : listproduct) {
      if (account_id == null) {
        ProductPlan planProduct = new ProductPlan();
        planProduct.setId_product_plan(-1)
            .setPlan_id(-1)
            .setPlan_id_local(id)
            .setInvest_id(product);
        db.newPlanProduct(planProduct);
      } else {
        ProductPlan planProduct = new ProductPlan();
        planProduct.setId_product_plan(-1)
            .setPlan_id(-1)
            .setPlan_id_local(id)
            .setProduct_id(product);
        db.newPlanProduct(planProduct);
      }
    }
  }

  public void deleteSessionDraft(String type) {
    switch (type) {
      case TYPE_DANA_PENSIUN:
        session.clearDanaPensiun();
        break;

      case TYPE_DANA_DARURAT:
        session.clearDanaDarurat();
        break;

      case TYPE_DANA_CUSTOM:
        session.clearDanaCustome();
        break;

      case TYPE_DANA_KULIAH:
        session.clearDanaKuliah();
        break;
    }
  }

  public void finishActivityWithPrevious(int eventCode) {
    rxBus.send(new AddPlanEvent(context,eventCode));
  }

  public void updateConnectProduct(int plan_id_local, int product_id) {
    Account account = db.getAccountById(product_id, 3, Integer.valueOf(session.getIdUser()));
    if (account != null) {
      Plan plan = db.getPlanBy(DbHelper.PLAN_ID_LOCAL, plan_id_local);
      plan.setAccount_id(account.getIdaccount());
      plan.setProduct_id(product_id);
      db.updatePlan(plan);
    }
  }

  public void updatePlanProduct(int id, Integer[] products) {
    db.deleteProductPlanByIdPlanLocal(id);
    for (Integer product : products) {
      ProductPlan planProduct = new ProductPlan();
      planProduct.setId_product_plan(-1).setPlan_id(-1).setPlan_id_local(id).setProduct_id(product);
      db.newPlanProduct(planProduct);
    }
  }
}
