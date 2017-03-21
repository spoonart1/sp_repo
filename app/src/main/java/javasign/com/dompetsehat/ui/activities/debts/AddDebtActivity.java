package javasign.com.dompetsehat.ui.activities.debts;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Debt;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.models.json.debt;
import javasign.com.dompetsehat.presenter.debts.DebtsInterface;
import javasign.com.dompetsehat.presenter.debts.DebtsPresenter;
import javasign.com.dompetsehat.ui.activities.account.AddAccountActivity;
import javasign.com.dompetsehat.ui.activities.account.AddAccountDompetActivity;
import javasign.com.dompetsehat.ui.activities.transaction.AddTransactionActivity;
import javasign.com.dompetsehat.ui.fragments.debts.ListDebtFragment;
import javasign.com.dompetsehat.ui.fragments.timeline.adapter.NewTimelineAdapter;
import javasign.com.dompetsehat.ui.fragments.timeline.pojo.TimelineView;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.Validate;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.CustomSpinnerAdapter;
import javax.inject.Inject;
import lecho.lib.hellocharts.model.Line;
import timber.log.Timber;

/**
 * Created by avesina on 2/17/17.
 */

public class AddDebtActivity extends BaseActivity implements DebtsInterface {
  private String MODE = "add";
  public static String TAG_MODE_ADD = "add";
  public static String TAG_MODE_EDIT = "edit";
  public static String TAG_MODE = "tag_mode";
  public static String TAG_ID_DEBT = "id_debt";
  public static String TAG_TYPE = "tag_type";
  public static Date tgl = Calendar.getInstance().getTime();
  public static Date tgl_payback = Calendar.getInstance().getTime();
  @Bind(R.id.tv_date) TextView tv_date;
  @Bind(R.id.tv_payback) TextView tv_payback;
  @Bind(R.id.et_name) EditText et_name;
  @Bind(R.id.floating_starting) TextView floating_starting;
  @Bind(R.id.et_email) EditText et_email;
  @Bind(R.id.sp_product) Spinner sp_product;
  @Bind(R.id.sp_mode) Spinner sp_mode;
  @Bind(R.id.et_amount) EditText et_amount;
  @Bind(R.id.ll_amount) LinearLayout ll_amount;
  @Bind(R.id.ll_cashflow) LinearLayout ll_cashflow;
  @Bind(R.id.rl_container) RelativeLayout rl_container;
  @Bind(R.id.radio_group) RadioGroup radio_group;
  @Inject DebtsPresenter presenter;
  RupiahCurrencyFormat format = new RupiahCurrencyFormat();
  String[] modes;
  private String selectedType = ListDebtFragment.TYPE_BORROW;
  private int selectedMode = 0;
  private Product selectedProduct;
  private Cash selectedCash;
  TimelineView timelineView = new TimelineView();
  AlertDialog alertDialog;
  @Bind(R.id.rootView) LinearLayout rootView;
  private int CODE_ADD_ACCOUNT = 23;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_debt);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);

    if (getIntent().getExtras() != null) {
      if (getIntent().hasExtra(TAG_MODE)) {
        MODE = getIntent().getExtras().getString(TAG_MODE, TAG_MODE_ADD);
      }
      if (getIntent().hasExtra(TAG_TYPE)) {
        selectedType = getIntent().getExtras().getString(TAG_TYPE);
        String name = getString(R.string.borrow);
        if (selectedType.equalsIgnoreCase(ListDebtFragment.TYPE_LEND)) {
          name = getString(R.string.lend);
        }
        setTitle(getString(R.string.add)+" " + name.substring(0, 1).toUpperCase() + name.substring(1));
        switch (selectedType) {
          case ListDebtFragment.TYPE_BORROW:
            radio_group.check(R.id.opsi_2);
            et_email.setVisibility(View.GONE);
            break;
          case ListDebtFragment.TYPE_LEND:
            radio_group.check(R.id.opsi_1);
            et_email.setVisibility(View.VISIBLE);
            break;
        }
      }
    }
    init();
  }

  private void init() {
    modes = new String[] { getString(R.string.debt_new), getString(R.string.debt_existing) };
    format.formatEditText(et_amount);
    View[] views = new View[] { ButterKnife.findById(this, R.id.ic_menu), floating_starting };
    Words.setButtonToListen(views, et_name, tv_date, tv_payback, et_amount);
    presenter.initSpinner();
    radio_group.setVisibility(View.GONE);
    radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
          case R.id.opsi_1:
            selectedType = ListDebtFragment.TYPE_LEND;
            et_email.setVisibility(View.VISIBLE);
            break;
          case R.id.opsi_2:
            selectedType = ListDebtFragment.TYPE_BORROW;
            et_email.setVisibility(View.GONE);
            break;
        }
        Timber.e("AVESINA " + selectedType);
        String tipe_mutasi = "CR";
        if (selectedType.equalsIgnoreCase(ListDebtFragment.TYPE_LEND)) {
          tipe_mutasi = "DB";
        }
        presenter.initSpinner(selectedProduct, tipe_mutasi);
      }
    });
    sp_mode.setAdapter(new CustomSpinnerAdapter(this, 0, modes).showIndicator(false));
    sp_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        et_amount.setText("");
        switch (i) {
          case 0:
            selectedMode = 0;
            rl_container.setVisibility(View.GONE);
            ll_amount.setVisibility(View.VISIBLE);
            break;
          case 1:
            selectedMode = 1;
            ll_cashflow.removeAllViews();
            rl_container.setVisibility(View.VISIBLE);
            ll_amount.setVisibility(View.GONE);
            break;
        }
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
        sp_mode.setSelection(0);
      }
    });
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_menu) void onSave() {
    if (selectedType.equalsIgnoreCase(ListDebtFragment.TYPE_LEND)) {
      if (TextUtils.isEmpty(et_email.getText().toString())) {
        et_email.setError("Email kosong");
        return;
      } else if (!Validate.isValidEmail(et_email.getText().toString())) {
        et_email.setError("Email tidak valid");
        return;
      }
    }
    if(selectedProduct == null){
      Helper.showCustomSnackBar(sp_product,getLayoutInflater(),getResources().getString(R.string.error_account_not_found),true,ContextCompat.getColor(this,R.color.red_400),Gravity.BOTTOM);
      return;
    }

    if (MODE.equalsIgnoreCase(TAG_MODE_EDIT)) {
      //presenter.editDebt();
    } else {
      presenter.addDebt(selectedMode, selectedType, selectedProduct, selectedCash,
          et_name.getText().toString(),
          Integer.valueOf(RupiahCurrencyFormat.clearRp(et_amount.getText().toString())),
          tv_date.getTag().toString(), tv_payback.getTag().toString(),
          et_email.getText().toString());
    }
  }

  public void createDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(
        new ContextThemeWrapper(this, R.style.Theme_AppCompat_NoActionBar_FullScreen));
    builder.setTitle(getString(R.string.no_account));
    builder.setMessage(getString(R.string.you_dont_have_account_add_now));
    builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
      Intent intent = new Intent(this,AddAccountActivity.class);
      startActivityForResult(intent,CODE_ADD_ACCOUNT);
    });
    builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
      dialogInterface.dismiss();
      finish();
    });
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    finish();
  }

  @OnClick({ R.id.tv_date, R.id.tv_payback }) void onOpenDate(View v) {
    SlideDateTimePicker.Builder builder =
        new SlideDateTimePicker.Builder(getSupportFragmentManager()).setListener(
            new SlideDateTimeListener() {
              @Override public void onDateTimeSet(Date date) {
                if (v.getId() == R.id.tv_date) {
                  tgl = date;
                  tv_date.setTag(Helper.setSimpleDateFormat(date, "yyyy-MM-dd"));
                  tv_date.setText(Helper.setSimpleDateFormat(date, "dd MMM yyyy"));
                } else if (v.getId() == R.id.tv_payback) {
                  tgl_payback = date;
                  tv_payback.setTag(Helper.setSimpleDateFormat(date, "yyyy-MM-dd"));
                  tv_payback.setText(Helper.setSimpleDateFormat(date, "dd MMM yyyy"));
                }
              }
            });
    if (v.getId() == R.id.tv_date) {
      if (tgl != null) {
        builder.setInitialDate(tgl);
      }
    } else if (v.getId() == R.id.tv_payback) {
      if (tgl_payback != null) {
        builder.setInitialDate(tgl_payback);
      }
    }
    builder.setIsDateOnly(true).setIs24HourTime(true).setIndicatorColor(Color.WHITE);
    SlideDateTimePicker slideDateTimePicker = builder.build();
    slideDateTimePicker.show();
  }

  @Override public void setAdapter(ArrayList<Debt> debts) {

  }

  @Override public void setTotal(double total) {

  }

  @Override public void snackBar(String message) {
    Helper.showCustomSnackBar(rootView, getLayoutInflater(), message, true,
        ContextCompat.getColor(this, R.color.orange_100), Gravity.BOTTOM);
  }

  @Override public void onRefresh() {

  }

  @Override public void setSpinnerProduct(ArrayList<Product> products, String[] labels) {
    runOnUiThread(() -> {
      if(products != null){
        if(products.size() <= 0){
          createDialog();
        }
      }else{
        createDialog();
      }
      sp_product.setAdapter(new CustomSpinnerAdapter(this, 0, labels).showIndicator(false));
      sp_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          String tipe_mutasi = "CR";
          if (selectedType.equalsIgnoreCase(ListDebtFragment.TYPE_LEND)) {
            tipe_mutasi = "DB";
          }
          selectedProduct = products.get(i);
          presenter.initSpinner(products.get(i), tipe_mutasi);
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView) {
          sp_product.setSelection(0);
        }
      });
    });
  }

  @Override public void finish() {
    super.finish();
  }

  @Override public void setSpinnerCashflow(ArrayList<Cash> cashes, String[] labels) {
    runOnUiThread(() -> {
      timelineView.cashs = cashes;
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == CODE_ADD_ACCOUNT){
      presenter.initSpinner();
    }
  }

  @OnClick(R.id.rl_container) void openCashflow() {
    if (timelineView.cashs.size() > 0) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View view = getLayoutInflater().inflate(R.layout.dialog_cashflow, null);
      LinearLayoutManager layoutManager = new LinearLayoutManager(this);
      layoutManager.setAutoMeasureEnabled(true);
      layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
      RecyclerView recycleview = ButterKnife.findById(view, R.id.recycleview);
      recycleview.setLayoutManager(layoutManager);
      recycleview.setAdapter(new NewTimelineAdapter(timelineView).setMultilines(true).setHideAccountName(true)
          .setOnClickItem(new NewTimelineAdapter.OnClickItem() {
            @Override public void onClick(View v, Cash cash, int section, int position) {
              if (v.getParent() != null) {
                ((ViewGroup) v.getParent()).removeView(v);
                selectedCash = cash;
                ll_cashflow.removeAllViews();
                v.setOnClickListener(view1 -> openCashflow());
                ll_cashflow.addView(v);
                ll_cashflow.setOnClickListener(null);
                et_amount.setText(format.toRupiahFormatSimple(cash.getAmount()));
                alertDialog.dismiss();
              }
            }

            @Override public void onLongClick(View v, Cash cash, int section, int position) {

            }

            @Override public void onCategoryClick(View v, Cash cash, int position) {

            }
          }));
      builder.setView(view);
      builder.setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss())
          .setCancelable(false);
      alertDialog = builder.create();
      alertDialog.show();
    } else {
      Helper.showCustomSnackBar(rl_container, getLayoutInflater(),
          "Anda tidak memiliki transaction", true, ContextCompat.getColor(this, R.color.orange_400),
          Gravity.BOTTOM);
    }
  }
}
