package javasign.com.dompetsehat.ui.activities.account;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.mikepenz.iconics.view.IconicsTextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.response.PortofolioResponse;
import javasign.com.dompetsehat.presenter.account.AccountPresenter;
import javasign.com.dompetsehat.presenter.account.EachAccountPresenter;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.account.base.BaseAccountActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.LoginKlikMamiActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.portofolio.DetailPortofolioActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.ui.event.DeleteAccountEvent;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.AccountView;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by lafran on 9/26/16.
 */

public class ManageEachAccountActivity extends BaseAccountActivity {

  public static String PRODUCT_NAME = "product_name";
  public static String PRODUCT_ID = "product_id";

  final String DEFAULT_ICON = DSFont.Icon.dsf_bank.getFormattedName();

  final public static String MODE_EDIT = "mode_edit";
  private FloatingButtonHelper buttonHelper;

  @Bind(R.id.cv_background) CircleShapeView cv_background;
  @Bind(R.id.icon) TextView icon;
  @Bind(R.id.tv_vendor_name) TextView tv_vendor_name;
  @Bind(R.id.tv_amount) TextView tv_amount;
  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.button_refresh) ImageView button_refresh;
  @Bind(R.id.footer_menu) View footer_menu;
  @Bind(R.id.splash_view) View splash_view;

  @Bind(R.id.viewflipper) ViewFlipper flipper;
  @Bind(R.id.btn_submit) Button submit;
  @Bind(R.id.btn_close) Button btn_close;
  @Bind(R.id.ll_tab_monthly) View ll_tab_monthly;
  @Bind(R.id.ll_tab_yearly) View ll_tab_yearly;
  @Bind(R.id.tv_month) TextView tv_month;
  @Bind(R.id.tv_year) TextView tv_year;
  @Bind(R.id.tv_status) TextView tv_status;
  @Bind(R.id.btn_sync_info) Button btn_sync_info;
  @Bind(R.id.ll_container) LinearLayout ll_container;
  @Bind(R.id.ll_content) LinearLayout ll_content;
  @Bind(R.id.footer_btn) TextView footer_btn;
  TextWatcher textWatcher;
  private Date selectedDate = null;

  int REQUEST = 123;

  public final static String KEY_ID_ACCOUNT = "account_id";

  @Inject EachAccountPresenter presenter;
  @Inject SyncPresenter presenterSync;
  private Account account;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private int mAccountID = -1;
  private int mVendorId = -1;

  private Animation enterFromBottom;
  private Animation exitToBottom;
  private boolean menuIsOpen = false;
  private int mCurrentPage = 0;
  private BlankView bv;
  private int selectedMonth = 0;
  private int selectedYear = 2015;
  private boolean hasSync = false;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acitivity_manage_each_account);
    ButterKnife.bind(this);

    setTitle(getString(R.string.detail_account));
    getActivityComponent().inject(this);
    presenter.attachView(this);
    button_refresh.setColorFilter(Color.GRAY);

    View rootview = ButterKnife.findById(this, R.id.rootview);
    bv = new BlankView(rootview, DSFont.Icon.dsf_transactions.getFormattedName(), "");

    enterFromBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);
    exitToBottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom);
    hideMenu(true);

    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    IconicsTextView ic_search = ButterKnife.findById(this, R.id.ic_search);
    ic_search.setText(DSFont.Icon.dsf_edit.getFormattedName());

    IconicsTextView ic_menu = ButterKnife.findById(this, R.id.ic_menu);
    ic_menu.setText(DSFont.Icon.dsf_delete_all.getFormattedName());
  }

  private void hideMenu(boolean hide) {
    int visibility = hide ? View.GONE : View.VISIBLE;
    footer_menu.setVisibility(visibility);
  }

  @OnClick(R.id.splash_view) void disable() {
    closeFooterMenu();
  }

  @OnClick(R.id.footer_menu) void click() {
    return;
  }

  private void openFooterMenu() {
    if (menuIsOpen) return;

    splash_view.setVisibility(View.VISIBLE);
    menuIsOpen = true;
    ViewCompat.animate(splash_view).alpha(0.5f).withEndAction(new Runnable() {
      @Override public void run() {
        splash_view.setAlpha(0.5f);
      }
    });

    enterFromBottom.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        hideMenu(false);
      }

      @Override public void onAnimationEnd(Animation animation) {
        hideMenu(false);
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });

    footer_menu.startAnimation(enterFromBottom);
  }

  private void closeFooterMenu() {
    if (!menuIsOpen) return;

    menuIsOpen = false;
    ViewCompat.animate(splash_view).alpha(0.0f).withEndAction(new Runnable() {
      @Override public void run() {
        splash_view.setAlpha(0.0f);
        splash_view.setVisibility(View.GONE);
      }
    });

    exitToBottom.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        hideMenu(false);
      }

      @Override public void onAnimationEnd(Animation animation) {
        hideMenu(true);
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    footer_menu.startAnimation(exitToBottom);
    flipper.setDisplayedChild(0);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @Override public void finish() {
    if (getIntent().hasExtra("from")) {
      if (getIntent().getExtras().getString("from").equalsIgnoreCase("login")) {
        super.finish();
        Helper.finishAllPreviousActivityWithNextTarget(this, NewMainActivity.class);
      }
    } else {
      super.finish();
    }
  }

  @OnClick(R.id.button_refresh) void clickRefresh() {
    final Handler handler = new Handler();
    ProgressDialog dialog = new ProgressDialog(this);
    dialog.setMessage(getString(R.string.synchronizing));
    dialog.show();
    presenterSync.syncAccount(account.getIdaccount());
    handler.postDelayed(() -> dialog.dismiss(), 5000);
  }

  @OnClick(R.id.ic_search) void onEdit() {
    if (this.account != null) {
      Intent i = new Intent(this, AddAccountBankActivity.class);
      if (mVendorId == AccountView.DP) {
        i = new Intent(this, AddAccountDompetActivity.class);
      }
      if (mVendorId == AccountView.MNL) {
        i = new Intent(this, LoginKlikMamiActivity.class);
      }
      i.putExtra(Words.ID_VENDOR, mVendorId);
      i.putExtra(Words.NAMA_VENDOR, tv_vendor_name.getText().toString());
      i.putExtra(MODE_EDIT, true);
      i.putExtra(Words.ACCOUNT_ID, this.account.getIdaccount());
      startActivityForResult(i, REQUEST);
    } else {
      Toast.makeText(this, getString(R.string.error_account_not_found), Toast.LENGTH_LONG).show();
      finish();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST) {
      init();
    }
  }

  @OnClick(R.id.ic_menu) void onDelete(View view) {
    if (account == null) {
      return;
    }
    AlertDialog dialog =
        new AlertDialog.Builder(this).setMessage(getString(R.string.warn_if_delete_account))
            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
              final DeleteUtils deleteUtils = beginPendingRemoval(account);
              Helper.deleteWithConfirmationMessage(view, this, getString(R.string.deleting_account),
                  v1 -> deleteUtils.cancelRemoval(), null);
            })
            .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
              dialogInterface.dismiss();
            })
            .create();
    dialog.show();
  }

  private DeleteUtils beginPendingRemoval(Account item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, presenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        Toast.makeText(ManageEachAccountActivity.this, getString(R.string.cancel),
            Toast.LENGTH_SHORT).show();
      }

      @Override public void onDoneRemoving() {
        MyCustomApplication.getRxBus().send(new DeleteAccountEvent());
        finish();
      }
    });
    return deleteUtils;
  }

  private void init() {
    Calendar calendar = Calendar.getInstance();
    selectedMonth = calendar.get(Calendar.MONTH);
    selectedYear = calendar.get(Calendar.YEAR);
    Bundle b = getIntent().getExtras();
    if (b == null) return;
    mAccountID = b.getInt(KEY_ID_ACCOUNT);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    loadData(false);
  }

  private void loadData(boolean with_sync) {
    bv.beginLoading(null);
    presenter.loadData(mAccountID, with_sync);
  }

  @Override public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      //if(BuildConfig.DEBUG || BuildConfig.DEBUG) {
      //  startActivityForResult(new Intent(this, FinalCreateAccountActivity.class).putExtra(
      //      FinalCreateAccountActivity.TAG_ID_ACCOUNT, mAccountID)
      //      .putExtra(FinalCreateAccountActivity.TAG_SALDO, 1000.0)
      //      .putExtra(FinalCreateAccountActivity.TAG_USERNAME, "test")
      //      .putExtra(FinalCreateAccountActivity.TAG_MESSAGE, "test"), State.FLAG_ACTIVITY_WILL_FINISH_AFTER);
      //}
      loadData(true);
      return true;
    }

    return super.onKeyLongPress(keyCode, event);
  }

  @OnClick(R.id.btn_sync_now) void doSync(View v) {
    if (btn_sync_info.isEnabled()) {
      hasSync = true;
      loadData(true);
    } else {
      Helper.showCustomSnackBar(v, getLayoutInflater(),
          "Fungsi ini tidak aktif sampai waktu hitung mundur selesai.",R.color.orange_400, Gravity.BOTTOM);
    }
  }

  @OnClick(R.id.footer_btn) void seeMenu() {
    openFooterMenu();

    submit.setOnClickListener(v -> {
      flipper.setDisplayedChild(1);
      presenter.getDataBank(account.getIdaccount(), selectedMonth, selectedYear);
    });
    btn_close.setOnClickListener(v -> closeFooterMenu());
    ll_tab_monthly.setOnClickListener(
        v -> showNumberPicker(Helper.MONTHS, tv_month, selectedMonth));

    ll_tab_yearly.setOnClickListener(v -> {
      Calendar calendar = Calendar.getInstance();
      int year = calendar.get(Calendar.YEAR);
      Integer[] range = new Integer[] {year-1,year};
      showNumberPicker(range, tv_year, selectedYear);
    });
  }

  private void showNumberPicker(Object[] data, TextView target, Integer value) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    NumberPicker picker = new NumberPicker(this);
    LinearLayout.LayoutParams param =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    picker.setLayoutParams(param);
    param.setMargins(16, 16, 16, 16);

    if (data[0] instanceof String) {
      picker.setMinValue(0);
      picker.setMaxValue(data.length - 1);
      picker.setDisplayedValues((String[]) data);
    } else {
      picker.setMinValue((Integer) data[0]);
      picker.setMaxValue((Integer) data[1]);
    }
    final int[] sMonth = { selectedMonth };
    final int[] sYear = { selectedYear };
    picker.setWrapSelectorWheel(true);
    picker.setOnValueChangedListener((picker1, oldVal, newVal) -> {
      if (data[0] instanceof String) {
        sMonth[0] = newVal;
      } else {
        sYear[0] = newVal;
      }
    });
    if (value != null) {
      picker.setValue(value);
    }
    builder.setView(picker);
    builder.setPositiveButton("OK", (dialog, which) -> {
      setTarget(target, data, sMonth[0], sYear[0]);
      dialog.dismiss();
    });

    AlertDialog dialog = builder.create();
    dialog.show();
  }

  private void setTarget(TextView target, Object[] data, int newValMonth, int newValYear) {
    if (data[0] instanceof String) {
      selectedMonth = newValMonth;
      target.setText(String.valueOf(data[newValMonth]));
    } else {
      selectedYear = newValYear;
      target.setText(String.valueOf(newValYear));
    }
  }

  private void setProperties(int bgColorIcon, String vendorName, double totalAmount,
      String userinfo, String icon_) {
    runOnUiThread(() -> {
      cv_background.setCircleColor(bgColorIcon);
      icon.setText(icon_);
      try {
        tv_vendor_name.setText(vendorName.toUpperCase());
        if (totalAmount <= 0) {
          tv_amount.setText(userinfo);
        } else {
          tv_amount.setText(format.toRupiahFormatSimple(totalAmount));
        }
      } catch (Exception e) {

      }
    });
  }

  private void setAdapter(Account account, List<Product> products) {
    Timber.e("carrisa setAdapter " + products.size());
    AdapterManageEachAccount adapterManageEachAccount =
        new AdapterManageEachAccount(account, products);
    recyclerView.setAdapter(adapterManageEachAccount);
    Helper.checkIfBlank(bv, products.isEmpty());

    if(hasSync) {
      new AlertDialog.Builder(this).setTitle("Sinkroninasi Berhasil!")
          .setMessage("Sinkronisasi telah berhasil. "
              + "Tunggu notifikasi dari kami, data akan di perbaharui secara otomatis. "
              + "\n\nps: Anda tidak dapat melakukan sinkronisasi lagi dalam 15 menit kedepan")
          .setPositiveButton("OK", (dialog, which) -> hasSync = false)
          .create()
          .show();
    }
  }

  private void showButtonFooter(boolean is_visible){
    if(is_visible) {
      footer_btn.setVisibility(View.VISIBLE);
    }else{
      footer_btn.setVisibility(View.GONE);
    }
  }

  @Override public void setAccount(Account account, double saldo) {
    this.account = account;
    Handler handler = new Handler();
    handler.post(() -> {
      mVendorId = account.getIdvendor();
      if (mVendorId == 6) {
        footer_btn.setVisibility(View.GONE);
        ll_content.setVisibility(View.GONE);
      } else {
        if(AccountView.getSection(mVendorId) == AccountPresenter.SECTION_BANK){
          footer_btn.setVisibility(View.VISIBLE);
          ll_content.setVisibility(View.VISIBLE);
        }else {
          footer_btn.setVisibility(View.GONE);
          ll_content.setVisibility(View.VISIBLE);
        }
      }
      int accountColor = AccountView.accountColor.get(mVendorId);
      String icon = AccountView.iconVendor.get(mVendorId);
      setProperties(accountColor, account.getName(), saldo, account.getLogin_info(), icon);
    });
  }

  @Override public void setListProduct(ArrayList<Product> products) {
    runOnUiThread(() -> setAdapter(this.account, products));
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    if (presenterSync != null) {
      presenterSync.detachView();
    }
    super.onDestroy();
  }

  @Override public void successGetData() {
    super.successGetData();
    closeFooterMenu();
  }

  @Override public void errorMessage(String message) {
    super.errorMessage(message);
    closeFooterMenu();
    Helper.showCustomSnackBar(flipper, getLayoutInflater(), "", true, R.color.red_400,
        Gravity.BOTTOM);
  }

  @Override public void setLastSync(Date date) {
    runOnUiThread(() -> {
      selectedDate = date;
      if (date.getTime() > 100) {
        String sub = getString(R.string.last_sync);
        tv_status.setText(sub + " " + Helper.setSimpleDateFormat(date, "dd MMM yyyy, HH:mm"));
        tv_status.setVisibility(View.VISIBLE);
      } else {
        tv_status.setVisibility(View.GONE);
      }
      Calendar calendar = Calendar.getInstance();
      long gap = calendar.getTime().getTime() - date.getTime();
      if (gap <= Words.fifteenminute) {
        btn_sync_info.setEnabled(false);
        btn_sync_info.setTextColor(ContextCompat.getColor(this, R.color.grey_600));
        disableButtonSync();
      } else {
        btn_sync_info.setEnabled(true);
        btn_sync_info.setTextColor(ContextCompat.getColor(this, R.color.white));
        enableButtonSync();
      }
    });
  }

  private void enableButtonSync() {
    try {
      btn_sync_info.setText("Sinkronisasi data baru");
      if (ll_container.getChildAt(2) instanceof TextClock) {
        TextClock t = (TextClock) ll_container.getChildAt(2);
        t.removeTextChangedListener(textWatcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          t.removeTextChangedListener(textWatcher);
          ll_container.removeAllViews();
          textWatcher = null;
          selectedDate = null;
        }
      } else if (ll_container.getChildAt(2) instanceof DigitalClock) {
        DigitalClock d = (DigitalClock) ll_container.getChildAt(2);
        d.removeTextChangedListener(textWatcher);
        ll_container.removeAllViews();
        textWatcher = null;
        selectedDate = null;
      }
      showButtonFooter(true);
    } catch (Exception e) {
      Timber.e("ERROR enableButtonSync() " + e);
    }
  }

  private void disableButtonSync() {
    if (selectedDate != null) {
      Calendar calendar = Calendar.getInstance();
      long gap = calendar.getTime().getTime() - selectedDate.getTime();
      String label_time =
          "<font color=red>" + Helper.populateStringToDate(ManageEachAccountActivity.this,
              Words.fifteenminute - gap) + "</font>";
      String label = getString(R.string.wait_sometime).replace("###", label_time).replace("#","<br/>");
      btn_sync_info.setText(Helper.fromHtml(label));
      if (this.textWatcher == null) {
        this.textWatcher = new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override public void afterTextChanged(Editable editable) {
            //if (!btn_sync_now.isEnabled()) {
            //  Calendar calendar = Calendar.getInstance();
            //  long gap = calendar.getTime().getTime()-selectedDate.getTime();
            //  String label_time = "<font color=red>" + Helper.populateStringToDate(ManageEachAccountActivity.this, Words.fifteenminute - gap) + "</font>";
            //  String label = getString(R.string.wait_sometime).replace("###", label_time);
            //  btn_sync_now.setText(Helper.fromHtml(label));
            //}
            setLastSync(selectedDate);
          }
        };
        if (android.os.Build.VERSION.SDK_INT >= 17) {
          TextClock t = new TextClock(this);
          t.setFormat24Hour("HH:mm:ss");
          t.addTextChangedListener(textWatcher);
          ll_container.addView(t);
        } else {
          DigitalClock d = new DigitalClock(this);
          d.addTextChangedListener(textWatcher);
          ll_container.addView(d);
        }
      }
      showButtonFooter(false);
    }
  }

  class AdapterManageEachAccount extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private Account account;
    private Gson gson;
    PortofolioResponse.Data data;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat format_date = new SimpleDateFormat("dd MMM yyyy", State.getLocale());

    public AdapterManageEachAccount(Account account, List<Product> productList) {
      this.productList = productList;
      this.account = account;
      this.gson = new Gson();
      this.data = gson.fromJson(account.getProperties(), PortofolioResponse.Data.class);
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.adapter_manage_each_account_product, parent, false);
      return new MH(view);
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      Product product = productList.get(position);
      MH mh = (MH) holder;
      mh.product_id = product.getId_product();
      mh.itemView.setOnClickListener(v -> {
        if (account.getIdvendor() != AccountView.MNL) {
          startActivity(
              new Intent(ManageEachAccountActivity.this, TransactionsActivity.class).putExtra(
                  PRODUCT_ID, mh.product_id)
                  .putExtra(PRODUCT_NAME, mh.tv_rekening.getText().toString())
                  .putExtra(TransactionsActivity.FROM, State.FROM_DETAIL_ACCOUNT));
        } else {
          Intent intent =
              new Intent(ManageEachAccountActivity.this, DetailPortofolioActivity.class);
          try {
            PortofolioResponse.Data.portfolio.investmentAccounts.investmentAccount ins =
                new Gson().fromJson(product.getProperties(),
                    PortofolioResponse.Data.portfolio.investmentAccounts.investmentAccount.class);
            String date = "";
            try {
              Date dt = df.parse(ins.FundLastValuationDate);
              date = format_date.format(dt);
            } catch (Exception e) {

            }
            Timber.e("saldo " + product.getBalance());
            intent.putExtra("cif", data.CifNum)
                .putExtra("name", data.CliFirstNm + " " + data.CliLastNm)
                .putExtra("title", product.getName())
                .putExtra("saldo", Double.valueOf(product.getBalance()))
                .putExtra("jumlah-unit", ins.UnitOnHand)
                .putExtra("cost-per-unit", ins.NavPerUnit)
                .putExtra("date", date);
            startActivity(intent);
          } catch (Exception e) {
            Timber.e("ERROR " + e);
          }
        }
      });
      mh.tv_rekening.setText(product.getName());
      if (product.getBalance() > 0 || product.getBalance() < 0) {
        mh.tv_value.setText(format.toRupiahFormatSimple(product.getBalance()));
      } else {
        mh.tv_value.setText("-");
      }
    }

    @Override public int getItemCount() {
      if (productList != null) {
        return productList.size();
      } else {
        return 0;
      }
    }

    class MH extends RecyclerView.ViewHolder {
      public int product_id;
      @Bind(R.id.action_menu) View action_menu;
      @Bind(R.id.tv_rekening) TextView tv_rekening;
      @Bind(R.id.tv_value) TextView tv_value;

      public MH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
      }
    }
  }
}
