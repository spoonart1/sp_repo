package javasign.com.dompetsehat.ui.activities.setting.syncs;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.sync.SyncInterface;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.setting.Setting;
import javasign.com.dompetsehat.ui.activities.setting.adapter.AdapterSetting;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.NetworkUtil;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;

/**
 * Created by lafran on 12/30/16.
 */

public class SyncSettingActivity extends BaseActivity implements AdapterSetting.OnSettingClick, SyncInterface {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Inject SyncPresenter presenter;
  private SessionManager session;
  private ProgressDialog dialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting_new);
    getActivityComponent().inject(this);
    ButterKnife.bind(this);
    presenter.attachView(this);
    session = presenter.session;
    dialog = new ProgressDialog(this);
    setTitle(getString(R.string.sync));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    List<AdapterSetting.SettingModel> settingModels = new ArrayList<>();

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(session.getLastSync() * 1000);
    String last_sync = getString(R.string.last_sync) + ": " + calendar.getTime().toString();
    calendar = Calendar.getInstance();
    calendar.setTimeInMillis(session.getLastSync(session.LAST_SYNC_ACCOUNT) * 1000);
    String last_sync_account = getString(R.string.last_sync) + ": " + calendar.getTime().toString();
    calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, session.getHoursSync());
    calendar.set(Calendar.MINUTE, session.getMinutesSync());

    String time_sync = Helper.setSimpleDateFormat(calendar.getTime(), "HH:mm");

    String[] syncDescrptions = new String[] {
        last_sync,
        last_sync_account,
        time_sync,
    };

    Setting[] syncEnums = new Setting[]{
        Setting.SyncAllData,
        Setting.SyncAllBankAccount,
        Setting.SyncEveryDayAt
    };

    settingModels.add(generateModel("",
        R.array.setting_child_item_sync_labels,
        R.array.setting_child_item_sync_icons,
        syncDescrptions, syncEnums, null));

    AdapterSetting adapter = new AdapterSetting(settingModels);
    adapter.setOnSettingClick(this);
    recyclerView.setAdapter(adapter);
  }

  private AdapterSetting.SettingModel generateModel(String title, @ArrayRes int labelArrayResId,
      @ArrayRes int iconArrayResId, String[] notes, Setting[] types,
      @Nullable AdapterSetting.SettingModel.CheckboxStyle checkboxStyle) {
    AdapterSetting.SettingModel model = new AdapterSetting.SettingModel();

    model.title = title;
    model.items = new ArrayList<>();

    String[] labels = getResources().getStringArray(labelArrayResId);
    String[] icons = getResources().getStringArray(iconArrayResId);

    for (int i = 0; i < labels.length; i++) {
      AdapterSetting.SettingModel.Item item = new AdapterSetting.SettingModel.Item();
      item.type = types[i];
      item.label = labels[i];
      item.icon = icons[i];
      item.checkboxStyle = checkboxStyle;
      if (notes != null) {
        item.note = notes[i];
      }

      model.items.add(item);
    }

    return model;
  }

  @Override public void onComplete(int code) {
    dialog.dismiss();
    init();
    if (presenter.TAG_SYNC_ALL == code) {
      presenter.SyncAllAccount(this);
    }
  }

  @Override public void onError(int kode) {
    dialog.dismiss();
  }

  @Override public void onNext(int code, String message) {

  }

  @Override public void onItemClick(View v, AdapterSetting.SettingModel.Item item, int section,
      int position) {
    switch (item.type){
      case SyncAllData:
        dialog.setMessage(getString(R.string.synchronizing));
        dialog.show();
        presenter.syncAll();
        break;
      case SyncAllBankAccount:
        if (NetworkUtil.getConnectivityStatus(this) > 0) {
          presenter.SyncAllAccount(this);
        } else {
          showAlertConnection();
        }
        break;
      case SyncEveryDayAt:

        int hour = session.getHoursSync();
        int minute = session.getMinutesSync();
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this,
            (timePicker, selectedHour, selectedMinute) -> {
              session.setTimeSync(selectedHour, selectedMinute);
              init();
            }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(getString(R.string.select_time));
        mTimePicker.show();
        break;
    }
  }

  private void showAlertConnection() {
    Helper.showCustomSnackBar(this.recyclerView, LayoutInflater.from(this),
        getString(R.string.app_need_connection), true,
        ContextCompat.getColor(this, R.color.colorPrimaryDark), Gravity.BOTTOM);
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
