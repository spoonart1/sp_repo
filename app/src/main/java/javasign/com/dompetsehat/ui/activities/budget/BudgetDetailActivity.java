package javasign.com.dompetsehat.ui.activities.budget;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.budget.BudgetPresenter;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;
import javasign.com.dompetsehat.ui.activities.budget.base.BaseBudgetActivity;
import javasign.com.dompetsehat.ui.activities.transaction.TransactionsActivity;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.CustomProgress;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by lafran on 11/22/16.
 */

public class BudgetDetailActivity extends BaseBudgetActivity {

  @Bind(R.id.icr_category) IconCategoryRounded icr_category;
  @Bind(R.id.tv_budget_name) TextView tv_budget_name;
  @Bind(R.id.tv_periode_name) TextView tv_periode_name;
  @Bind(R.id.tv_date_periode) TextView tv_date_periode;
  @Bind(R.id.tv_repeat) TextView tv_repeat;
  @Bind(R.id.tv_right) TextView tv_right;
  @Bind(R.id.tv_setor) TextView tv_setor;
  @Bind(R.id.tv_value) TextView tv_value;
  @Bind(R.id.tv_left_time) TextView tv_left_time;
  @Bind(R.id.cp_bar) CustomProgress cp_bar;
  @Bind(R.id.ll_repeat) LinearLayout ll_repeat;
  @Bind(R.id.sw_repeat) SwitchCompat sw_repeat;
  @Bind(R.id.indicator) View indicator;
  @Bind(R.id.tv_keterangan) TextView tv_keterangan;
  @Bind(R.id.btn_detail) LinearLayout btn_detail;
  @Bind(R.id.tv_value_transaction) TextView tv_value_transaction;

  @Inject BudgetPresenter presenterBudget;
  GradientDrawable drawable;
  private final int strokeColor = Color.parseColor("#BDBDBD");
  private final int activeColor = Helper.GREEN_DOMPET_COLOR;
  private final int inactiveColor = Color.TRANSPARENT;

  private Budget budget;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private int REQUEST_EDIT_CODE = 1102;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_budget_detail);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenterBudget.attachView(this);
    setTitle("Detail Anggaran");
    init();
  }

  @Override protected void onResume() {
    super.onResume();
    presenterBudget.attachView(this);
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    IconicsTextView ic_menu = ButterKnife.findById(this, R.id.ic_menu);
    ic_menu.setText(DSFont.Icon.dsf_edit.getFormattedName());
    ic_menu.setEnabled(true);
    ic_menu.setTextColor(ContextCompat.getColor(this, R.color.black));
  }

  @OnClick(R.id.ic_menu) void onEdit() {
    Intent intent = new Intent(this, AddBudgetActivity.class);
    intent.putExtra(Budget.MODE, Budget.MODE_EDIT);
    intent.putExtra(Budget.BUDGET_ID, budget.getId());
    startActivityForResult(intent, REQUEST_EDIT_CODE);
  }

  private void init() {
    btn_detail.setVisibility(View.GONE);
    drawable = (GradientDrawable) indicator.getBackground();
    if (getIntent().hasExtra(Budget.BUDGET_ID)) {
      presenterBudget.setBudget(getIntent().getIntExtra(Budget.BUDGET_ID, -1));
      return;
    }else {
      Toast.makeText(this, getString(R.string.error_source_unknown), Toast.LENGTH_LONG).show();
      finish();
    }

    //setField();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_EDIT_CODE) {
      init();
    }
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    finish();
  }

  private void setField() {
    setCategory(budget.getCategory());
    if (budget.getUserCategory() != null) {
      budget.getUserCategory().setParentCategory(budget.getCategory());
      setUserCategory(budget.getUserCategory());
    }
    setAmount(budget.getCategory_cash_amount(), budget.getAmount_budget());
    setPeriode();
    setSwitch();
    setTimeLeft();
  }

  private void setTimeLeft() {
    long interval =
        GeneralHelper.getInstance().getInterval(budget.getDate_end(), budget.getDate_start());
    Calendar calendar = Calendar.getInstance();
    String difference = (interval-calendar.get(Calendar.DAY_OF_MONTH)) + " " + getString(R.string.day);
    tv_left_time.setText(difference);
  }

  private void setAmount(double deposit, double target) {
    tv_setor.setText(format.toRupiahFormatSimple(deposit));
    tv_value.setText(format.toRupiahFormatSimple(target));
    int persen = 100;
    if(deposit<target){
      persen = (int)((deposit/target)*100);
    }
    String text = getString(R.string.used)+" <font color='red' >("+persen+"%)</font>";
    tv_keterangan.setText(Html.fromHtml(text));
    cp_bar.setMaxValue(target);
    cp_bar.setProgressValue(deposit);
    cp_bar.invalidateDraw();
  }

  private void setPeriode() {
    tv_periode_name.setVisibility(View.VISIBLE);
    tv_date_periode.setVisibility(View.VISIBLE);
    tv_periode_name.setText(Words.toTitleCase(budget.getEvery()));
    try {
      Date date_start = Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_start());
      String s_start = Helper.setSimpleDateFormat(date_start, "dd MMMM yyyy");
      Date date_end = Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_end());
      String s_end = Helper.setSimpleDateFormat(date_end, "dd MMMM yyyy");
      tv_date_periode.setText(s_start + " s/d " + s_end);
    } catch (Exception e) {
      Timber.e("ERROR " + e);
    }
  }

  private void setSwitch() {
    ll_repeat.setVisibility(budget.getRepeat() == 1 ? View.VISIBLE : View.GONE);
    tv_repeat.setText(getString(R.string.repeat_every) + " " + budget.getEvery().replace("_", " "));
    sw_repeat.setChecked(budget.getRepeat() == 1);
    if (budget.getRepeat() == 1) {
      drawable.setColor(activeColor);
      drawable.setStroke(0, strokeColor);
    } else {
      drawable.setColor(inactiveColor);
      drawable.setStroke(1, strokeColor);
    }
    indicator.setBackground(drawable);
    sw_repeat.setClickable(false);
    sw_repeat.setTextColor(Helper.getAccentColor(this, 2));
  }

  @Override public void setCategory(Category category) {
    setIconCategory(category.getName(), category);
  }

  @Override public void setUserCategory(UserCategory userCategory) {
    super.setUserCategory(userCategory);
    setIconCategory(userCategory.getName(), userCategory.getParentCategory());
  }

  @Override public void setIconCategory(String textLabel, Category category) {
    tv_budget_name.setText(textLabel);
    icr_category.setBackgroundColorIcon(Color.parseColor(category.getColor()));
    icr_category.setIconCode(category.getIcon());
    icr_category.setIconTextColor(Color.WHITE);
  }

  @Override protected void onDestroy() {
    if (presenterBudget != null) {
      presenterBudget.detachView();
    }
    super.onDestroy();
  }

  @Override public void setBudget(AdapterScheduler.SHModel model, Budget budget) {
    super.setBudget(model, budget);
    runOnUiThread(() -> {
      Timber.e("Avesina "+budget.getId());
      this.budget = budget;
      setField();
    });
  }

  @OnClick(R.id.btn_detail) void detailTransaction(){
    if(tv_value_transaction.getTag() != null){
      if(tv_value_transaction.getTag() instanceof Integer[]){
        Integer[] ids = (Integer[]) tv_value_transaction.getTag();
        startActivity(new Intent(this, TransactionsActivity.class).putExtra(TransactionsActivity.FROM,
            State.FROM_TIMELINE).putExtra(State.IDS_TRANSACTION, new ArrayList<>(Arrays.asList(ids))));
      }
    }
  }

  @Override public void setDetailCash(Integer[] ids) {
    super.setDetailCash(ids);
    if(ids.length > 0) {
      tv_value_transaction.setText(ids.length + " Transaksi");
      tv_value_transaction.setTag(ids);
      btn_detail.setVisibility(View.VISIBLE);
    }else{
      btn_detail.setVisibility(View.GONE);
    }
  }
}
