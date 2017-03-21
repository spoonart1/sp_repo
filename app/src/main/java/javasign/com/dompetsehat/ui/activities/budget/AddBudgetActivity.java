package javasign.com.dompetsehat.ui.activities.budget;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Date;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.budget.BudgetPresenter;
import javasign.com.dompetsehat.presenter.category.AddCategoryPresenter;
import javasign.com.dompetsehat.ui.activities.budget.adapter.AdapterScheduler;
import javasign.com.dompetsehat.ui.activities.budget.base.BaseBudgetActivity;
import javasign.com.dompetsehat.ui.activities.category.CategoryActivity;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/15/16.
 */
public class AddBudgetActivity extends BaseBudgetActivity {

  @Bind(R.id.tv_categori_name) TextView tv_categori_name;
  @Bind(R.id.icr_category) IconCategoryRounded icon_category;
  @Bind(R.id.et_budget_goal) MaterialEditText et_budget_goal;
  @Bind(R.id.et_date_periode) TextView et_date_periode;
  @Bind(R.id.tv_periode_name) TextView tv_periode_name;
  @Bind(R.id.tv_repeat) TextView tv_repeat;
  @Bind(R.id.sw_repeat) SwitchCompat sw_repeat;
  @Bind(R.id.ll_repeat) LinearLayout ll_repeat;

  private String SELECTED_MODE = "mode";
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private Budget selectedBudget;
  private int accessFrom;
  public static int REQ_FIST_TIME = 1;
  public static int REQ_AFTER_THAT = 2;

  @Inject BudgetPresenter presenterBudget;
  @Inject AddCategoryPresenter presenterCategory;

  @Inject RxBus rxBus;
  Category selectedCategory = null;
  UserCategory selectedUserCategory = null;
  HashMap<String, String> selectedCalendar = null;
  String selectedEvery = null;
  Helper helper = new Helper();
  private boolean isSchedulerOpen = false;
  private boolean firstAdd = true;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_budget);
    ButterKnife.bind(this);

    getActivityComponent().inject(this);
    presenterBudget.attachView(this);
    presenterCategory.attachView(this);
    rxBus = MyCustomApplication.getRxBus();

    sw_repeat.setOnTouchListener(null);
    setTitle(getString(R.string.add_budget));
    if (getIntent().hasExtra(Budget.MODE)) {
      SELECTED_MODE = getIntent().getExtras().getString(Budget.MODE);
      if (SELECTED_MODE.equalsIgnoreCase(Budget.MODE_EDIT)) {
        if (getIntent().hasExtra(Budget.BUDGET_ID)) {
          presenterBudget.setBudget(getIntent().getExtras().getInt(Budget.BUDGET_ID));
          setTitle(getString(R.string.edit_budget));
        } else {
          finish();
        }
      } else {
        openDialogCategory(true);
      }
    } else {
      finish();
    }

    init();
  }

  public void emptyCtegory() {
    selectedCategory = null;
    icon_category.setIconCode(DSFont.Icon.dsf_general.getFormattedName());
    icon_category.setBackgroundColorIcon(Color.GRAY);
    tv_categori_name.setText(R.string.select_category);
  }

  @Override protected void onResume() {
    super.onResume();
    presenterBudget.catchAddPeriodic();
    presenterBudget.attachView(this);
    presenterCategory.attachView(this);
  }

  @Override public void setTitle(CharSequence title) {
    ((TextView) ButterKnife.findById(this, R.id.tv_title)).setText(title);
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    finish();
  }

  @OnClick(R.id.ic_menu) void doSave(View v) {
    save();
  }

  @OnClick(R.id.ll_category) void pickCategory() {
    openDialogCategory(false);
  }

  private void openDialogCategory(boolean is_first_time) {
    Intent i = new Intent(this, CategoryActivity.class);
    i.putExtra(CategoryActivity.KEY_CATEGORY, "DB");
    i.putExtra(CategoryActivity.FROM, "budget");
    startActivityForResult(i, is_first_time ? REQ_FIST_TIME : REQ_AFTER_THAT);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQ_FIST_TIME) {
      if (resultCode == RESULT_CANCELED) {
        finish();
      }
    }
  }

  @OnClick({ R.id.ll_content_periode, R.id.spacer }) void getScheduler() {
    showScheduler();
  }

  private void init() {

    if (getIntent().hasExtra(Words.FLAG)) {
      accessFrom = getIntent().getIntExtra(Words.FLAG, -1);
    }

    View checkList = ButterKnife.findById(this, R.id.ic_menu);
    View label = ButterKnife.findById(this, R.id.floating_text);
    View[] views = new View[] { checkList, label };
    Words.setButtonToListen(views, et_budget_goal, et_date_periode,
        tv_periode_name); // make checklist enable or disable

    et_budget_goal.setFloatingLabelAnimating(true);
    et_budget_goal.addTextChangedListener(new Words.SimpleTextWatcer() {
      @Override public void afterTextChanged(Editable editable) {
        try {
          if (Double.valueOf(RupiahCurrencyFormat.clearRp(editable.toString())) <= 0) {
            et_budget_goal.setError(getString(R.string.error_budget_zero));
          }
        } catch (Exception e) {
          et_budget_goal.setError(getString(R.string.error_budget_validate));
        }
      }
    });

    format.formatEditText(et_budget_goal);
    emptyCtegory();
  }

  private void showScheduler() {
    Helper.goTo(this, SchedulerMainActivity.class);
  }

  private void save() {
    if (selectedCategory == null) {
      //Toast.makeText(getActivityComponent().context(), "Pilih salah satu kategori", Toast.LENGTH_SHORT).show();
      tv_categori_name.setError(getString(R.string.select_at_least_one_category));
    } else if (TextUtils.isEmpty(et_budget_goal.getText())) {
      et_budget_goal.setError(getString(R.string.error_if_blank_field));
    } else if (TextUtils.isEmpty(et_date_periode.getText())
        || selectedCalendar == null
        || selectedEvery == null) {
      et_date_periode.setError(getString(R.string.error_if_blank_field));
    } else {
      Budget budget = new Budget();
      if (SELECTED_MODE.equalsIgnoreCase(Budget.MODE_EDIT)) {
        budget = selectedBudget;
      }
      budget.setCategory_budget(selectedCategory.getId_category());
      if (selectedUserCategory != null) {
        budget.setUser_category_budget(selectedUserCategory.getId_user_category());
      }
      budget.setAmount_budget(
          Double.valueOf(RupiahCurrencyFormat.clearRp(et_budget_goal.getText().toString())));
      budget.setDate_start(selectedCalendar.get(helper.DATE_START));
      budget.setDate_end(selectedCalendar.get(helper.DATE_END));
      budget.setEvery(selectedEvery);
      if (selectedEvery.equalsIgnoreCase(Budget.CUSTOM.toLowerCase())) {
        budget.setRepeat(
            sw_repeat.isChecked() ? Budget.BUDGET_REPEAT_TRUE : Budget.BUDGET_REPEAT_FALSE);
      } else {
        budget.setRepeat(budget.getRepeatOrNo(selectedEvery));
      }
      if (SELECTED_MODE.equalsIgnoreCase(Budget.MODE_ADD)) {
        budget.setCreated_at(Helper.getDateNow());
        budget.setUpdated_at(Helper.getDateNow());
        presenterBudget.saveBudget(budget);
      } else {
        budget.setUpdated_at(Helper.getDateNow());
        presenterBudget.updateBudget(budget);
      }

      if (accessFrom != -1) {
        setResult(State.BUDGET);
      }

      finish();
    }
  }

  @Override public void setCategory(Category category) {
    runOnUiThread(() -> {
      if (!isSchedulerOpen && SELECTED_MODE.equalsIgnoreCase(Budget.MODE_ADD) && firstAdd) {
        showScheduler();
        isSchedulerOpen = true;
        firstAdd = false;
      }
      setIconCategory(category.getName(), category);
    });
  }

  @Override public void setUserCategory(UserCategory userCategory) {
    selectedUserCategory = userCategory;
    setIconCategory(userCategory.getName(), userCategory.getParentCategory());
  }

  @Override public void setIconCategory(String textLabel, Category category) {
    this.selectedCategory = category;
    tv_categori_name.setText(textLabel);
    icon_category.setBackgroundColorIcon(Color.parseColor(category.getColor()));
    icon_category.setIconCode(category.getIcon());
    icon_category.setIconTextColor(Color.WHITE);
  }

  @Override public void setSHModel(AdapterScheduler.SHModel model) {
    runOnUiThread(() -> {
      isSchedulerOpen = false;
      View spacer = ButterKnife.findById(AddBudgetActivity.this, R.id.spacer);

      String datePeriode = "";
      try {
        Date date_start = Helper.setInputFormatter("yyyy-MM-dd", model.dateStart);
        String s_start = Helper.setSimpleDateFormat(date_start, "dd MMMM yyyy");
        Date date_end = Helper.setInputFormatter("yyyy-MM-dd", model.dateEnd);
        String s_end = Helper.setSimpleDateFormat(date_end, "dd MMMM yyyy");
        datePeriode = s_start + " - " + s_end;
      } catch (Exception e) {
        Timber.e("ERROR " + e);
      }
      tv_periode_name.setText(Words.toTitleCase(model.title));
      et_date_periode.setText(datePeriode);
      tv_periode_name.setVisibility(View.VISIBLE);
      et_date_periode.setVisibility(View.VISIBLE);
      ll_repeat.setVisibility(TextUtils.isEmpty(model.leftSwitchLabel) ? View.GONE : View.VISIBLE);
      spacer.setVisibility(View.GONE);
      tv_repeat.setText(model.leftSwitchLabel);
      sw_repeat.setChecked(model.isRepeatAble);
      Timber.e("avesina " + model.dateStart + " " + model.dateEnd + " " + model.identify);
      if (model.identify != null && !model.identify.equalsIgnoreCase(Budget.CUSTOM.toLowerCase())) {
        Timber.e("saya di sini ");
        selectedCalendar = helper.populatePeriodicString(model.identify);
      } else {
        selectedCalendar = new HashMap<String, String>();
        selectedCalendar.put(helper.DATE_START, model.dateStart);
        selectedCalendar.put(helper.DATE_END, model.dateEnd);
      }
      selectedEvery = model.identify;
    });
  }

  @Override public void setBudget(AdapterScheduler.SHModel model, Budget budget) {
    super.setBudget(model, budget);
    runOnUiThread(() -> {
      this.selectedBudget = budget;
      setCategory(budget.getCategory());
      if (budget.getUserCategory() != null) {
        budget.getUserCategory().setParentCategory(budget.getCategory());
        setUserCategory(budget.getUserCategory());
      }
      et_budget_goal.setText(format.toRupiahFormatSimple(budget.getAmount_budget()));
      setSHModel(model);
    });
  }

  @Override protected void onDestroy() {
    if (presenterBudget != null) {
      presenterBudget.detachView();
    }
    if (presenterCategory != null) {
      presenterCategory.detachView();
    }
    super.onDestroy();
  }
}
