package javasign.com.dompetsehat.ui.activities.reminder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.UserCategory;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.presenter.category.AddCategoryInterface;
import javasign.com.dompetsehat.presenter.category.AddCategoryPresenter;
import javasign.com.dompetsehat.presenter.reminder.ReminderInterface;
import javasign.com.dompetsehat.presenter.reminder.ReminderPresenter;
import javasign.com.dompetsehat.ui.activities.category.CategoryActivity;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.DateNumberPicker;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class AddReminderActivity extends BaseActivity
    implements AddCategoryInterface, ReminderInterface, DateNumberPicker.OnNumberPick {

  @Bind(R.id.et_title) MaterialEditText et_title;
  @Bind(R.id.et_amount) MaterialEditText et_amount;
  @Bind(R.id.tv_date) TextView tv_date;
  @Bind(R.id.floating_starting) TextView floating_starting;
  @Bind(R.id.tv_categori_name) TextView tv_categori_name;
  @Bind(R.id.icr_category) IconCategoryRounded icr_category;

  private String MODE = "add";
  public static String TAG_MODE_ADD = "add";
  public static String TAG_MODE_EDIT = "edit";
  public static String TAG_MODE = "tag_mode";
  public static String TAG_ID_ALARM = "id_alaram";
  private Alarm selectedAlarm;

  Category category;
  UserCategory userCategory;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();

  @Inject AddCategoryPresenter presenterCategory;
  @Inject ReminderPresenter presenterReminder;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_reminder);
    ButterKnife.bind(this);
    setTitle(getString(R.string.add_reminder_title));

    getActivityComponent().inject(this);
    presenterCategory.attachView(this);
    presenterReminder.attachView(this);

    if(getIntent().getExtras() != null) {
      MODE = getIntent().getExtras().getString(TAG_MODE, TAG_MODE_ADD);
    }

    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.ll_category) void onPickCategory() {
    Helper.goTo(this, CategoryActivity.class);
  }

  @OnClick(R.id.ic_menu) void onSave() {
    if (et_title.getText().toString().equalsIgnoreCase("")) {
      et_title.setError(getString(R.string.error_title_if_blank));
    } else if (et_amount.getText().toString().equalsIgnoreCase("")
        || Integer.valueOf(RupiahCurrencyFormat.clearRp(et_amount.getText().toString())) <= 0) {
      et_amount.setError(getString(R.string.error_amount_if_blank));
    } else if (tv_date.getText().toString().equalsIgnoreCase("")) {
      tv_date.setError(getString(R.string.error_date_if_blank));
    } else if (category == null) {
      tv_categori_name.setError(getString(R.string.error_category_if_blank));
    } else {
      Alarm alarm;
      if(MODE.equalsIgnoreCase(TAG_MODE_ADD)) {
        alarm = new Alarm();
        alarm.setId_alarm(-1);
      }else{
        if(selectedAlarm != null) {
          alarm = selectedAlarm;
        }else{
          Toast.makeText(this,"Terdapat kesalahan",Toast.LENGTH_LONG).show();
          finish();
          return;
        }
      }
      if(alarm!= null) {
        alarm.setId_category(category.getId_category());
        if(userCategory != null) {
          alarm.setUser_id_category(userCategory.getId_user_category());
        }
        alarm.setDate_alarm(tv_date.getText().toString());
        alarm.setDeskripsi_alarm(et_title.getText().toString());
        alarm.setJumlah_alarm(Float.valueOf(RupiahCurrencyFormat.clearRp(et_amount.getText().toString())));
        if(MODE.equalsIgnoreCase(TAG_MODE_ADD)){
          presenterReminder.saveReminder(alarm);
          Helper.trackThis(this, "User berhasil menambahkan daftar Pengingat");
        }else{
          presenterReminder.updateReminder(alarm);
          Helper.trackThis(this, "User berhasil mengubah daftar Pengingat");
        }
      }
      finish();
    }
  }

  @OnClick(R.id.tv_date) void getDate() {
    showDateNumberPickerDialog();
  }

  private void init() {
    format.formatEditText(et_amount);
    View[] views = new View[] { ButterKnife.findById(this, R.id.ic_menu), floating_starting };
    Words.setButtonToListen(views, et_title, et_amount, tv_categori_name, tv_date);
    if(MODE.equalsIgnoreCase(TAG_MODE_EDIT)){
      if(getIntent().hasExtra(TAG_ID_ALARM)){
        presenterReminder.setAlarm(getIntent().getExtras().getInt(TAG_ID_ALARM));
      }else{
        finish();
      }
    }
  }

  private void showDateNumberPickerDialog() {
    DateNumberPicker dlg = new DateNumberPicker();
    dlg.setOnNumberPick(this);
    dlg.show(getSupportFragmentManager(), "numberpicker");
  }

  @Override public void onPick(int number) {
    tv_date.setText(number + "");
  }

  @Override public void setCategory(Category category) {
    setIconCategory(category.getName(), category);
  }

  @Override public void setUserCategory(UserCategory userCategory) {
    this.userCategory = userCategory;
    setIconCategory(userCategory.getName(), userCategory.getParentCategory());
  }

  @Override public void setIconCategory(String textlabel, Category category) {
    this.category = category;
    icr_category.setIconCode(category.getIcon());
    icr_category.setIconTextColor(Color.WHITE);
    icr_category.setBackgroundColorIcon(Color.parseColor(category.getColor()));
    tv_categori_name.setText(textlabel);
  }

  @Override public void setAlarm(Alarm alarm,Category category,UserCategory userCategory) {
    runOnUiThread(() -> {
      this.selectedAlarm = alarm;
      et_title.setText(alarm.getDeskripsi_alarm());
      et_amount.setText(new RupiahCurrencyFormat().toRupiahFormatSimple(alarm.getJumlah_alarm()));
      tv_date.setText(String.valueOf(alarm.getDate_alarm()));
      setCategory(category);
      if(userCategory != null){
        setUserCategory(userCategory);
      }
    });
  }

  @Override public void setCashflow(Cash cashflow) {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }

  @Override public void setHeaderData(int count, double totalBill, double totalBalance) {

  }

  @Override public void setAdapter(List<ReminderModel> reminderModels) {

  }

  @Override protected void onDestroy() {
    if (presenterCategory != null) {
      presenterCategory.detachView();
    }
    if (presenterReminder != null) {
      presenterReminder.detachView();
    }
    super.onDestroy();
  }
}
