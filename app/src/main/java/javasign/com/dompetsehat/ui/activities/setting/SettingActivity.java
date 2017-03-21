package javasign.com.dompetsehat.ui.activities.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.setting.SettingInterface;
import javasign.com.dompetsehat.presenter.setting.SettingPresenter;
import javasign.com.dompetsehat.ui.activities.account.NewManageAccountActivity;
import javasign.com.dompetsehat.ui.activities.category.CategoryActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.profile.EditProfileActivity;
import javasign.com.dompetsehat.ui.activities.setting.about.AboutActivity;
import javasign.com.dompetsehat.ui.activities.setting.adapter.AdapterSetting;
import javasign.com.dompetsehat.ui.activities.setting.export.ExportSettingActivity;
import javasign.com.dompetsehat.ui.activities.setting.sharing.InviteMemberSharingDataActivity;
import javasign.com.dompetsehat.ui.activities.setting.sharing.SharingDataActivity;
import javasign.com.dompetsehat.ui.activities.setting.switchmode.SwitchModeActivity;
import javasign.com.dompetsehat.ui.activities.setting.syncs.SyncSettingActivity;
import javasign.com.dompetsehat.ui.activities.tag.TagActivity;
import javasign.com.dompetsehat.ui.activities.webview.WebLoaderActivity;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.LocaleHelper;
import javasign.com.dompetsehat.view.DateNumberPicker;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/23/16.
 */
public class SettingActivity extends BaseActivity
    implements AdapterSetting.OnSettingClick, SettingInterface, DateNumberPicker.OnNumberPick {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Inject SettingPresenter presenter;
  private int selected;
  public static final int REQ_PASS = 1;
  private int curPos = 0;
  private int curSec = 0;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting_new);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    setTitle(getString(R.string.setting));
    init();
    listen();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    presenter.populateData();
  }

  @Override public void setAdapter(List<AdapterSetting.SettingModel> settingModels) {
    runOnUiThread(() -> {
      AdapterSetting adapter = new AdapterSetting(settingModels);
      adapter.setOnSettingClick(this);
      recyclerView.setAdapter(adapter);
      int pos = curPos;
      if(curSec > 0){
        pos = settingModels.get(curSec-1).items.size() + curPos;
      }
      recyclerView.scrollToPosition(pos);

      //recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      //  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      //    super.onScrolled(recyclerView, dx, dy);
      //    mTotalScrolled = recyclerView.computeVerticalScrollOffset();
      //    Timber.e("mTotalScrolled "+mTotalScrolled);
      //  }
      //});
    });
  }

  @Override public void setSpinner(ArrayList<Account> arrayLists, String[] labels) {

  }

  @Override public void setPath(String path) {

  }

  private void changeLanguage() {
    final String[] items = getResources().getStringArray(R.array.bahasa);
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getResources().getString(R.string.preferred_language));
    final String savedLang = LocaleHelper.getLanguage(this).substring(0, 2).toLowerCase();
    int choosen = savedLang.equals("in") ? 0 : 1;
    builder.setSingleChoiceItems(items, choosen, (dialog1, which) -> {
      selected = which;
    }).setPositiveButton("OK", (dialog12, id) -> {
      final String choosenLang = items[selected].substring(0, 2).toLowerCase();
      if (!savedLang.equalsIgnoreCase(choosenLang)) {
        new AlertDialog.Builder(SettingActivity.this).setMessage(
            getResources().getString(R.string.you_need_to_restart))
            .setPositiveButton("OK", (dialog121, which) -> {
              LocaleHelper.setLocale(SettingActivity.this, choosenLang);
              Helper.finishAllPreviousActivityWithNextTarget(SettingActivity.this,
                  NewMainActivity.class);
              Helper.trackThis(SettingActivity.this, "User mengubah ke bahasa " + items[selected]);
            })
            .setNegativeButton(getResources().getString(R.string.text_later),
                (dialog1212, which) -> {
                  LocaleHelper.setLocale(SettingActivity.this, choosenLang);
                  dialog1212.dismiss();
                })
            .create()
            .show();
      }
    }).setNegativeButton(getString(R.string.cancel), (dialog13, id) -> {
      dialog13.dismiss();
    });
    builder.create();
    builder.show();
  }

  @Override public void onItemClick(View v, AdapterSetting.SettingModel.Item item, int section,
      int position) {
    curPos = position;
    curSec = section;
    switch (item.type) {
      case UserProfile:
        Helper.goTo(this, EditProfileActivity.class);
        break;

      case SwitchMode:
        if(!MyCustomApplication.showInvestasi()) {
          Helper.showCustomSnackBar(v, getLayoutInflater(), "Segera hadir");
        }else{
          Helper.goTo(this, SwitchModeActivity.class);
        }
        break;
      case Accounts:
        Helper.goTo(this, NewManageAccountActivity.class);
        break;

      case Categories:
        Helper.goTo(this, CategoryActivity.class,new Intent().putExtra(CategoryActivity.FROM,"setting"));
        break;

      case Synchronisation:
        Helper.goTo(this, SyncSettingActivity.class);
        break;

      case GroupSharing:
        //TODO : uncomment to enable
        Helper.goTo(this, InviteMemberSharingDataActivity.class);
        //Helper.showCustomSnackBar(v, getLayoutInflater(), "Segera hadir");
        break;

      case Languange:
        changeLanguage();
        break;

      case InitialDayOfMonth:
        DateNumberPicker dlg = DateNumberPicker.getInstance(presenter.getInitialDateofMonth(), getString(R.string.initial_day_of_the_month_wording));
        dlg.setOnNumberPick(this);
        dlg.show(getSupportFragmentManager(), "numberpicker");
        break;

      case SmartReminder:

        break;
      case PassCode:
        //Helper.showCustomSnackBar(v, getLayoutInflater(), "Segera hadir");
        //Intent i = new Intent(this, PassCodeActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(i);
        break;

      case ExportData:
        Helper.goTo(this, ExportSettingActivity.class);
        break;

      case ChangeLog:
        AdvancedDialog.ABuilder builder =
            new AdvancedDialog.ABuilder(this, getString(R.string.change_log), Color.BLACK).addText(
                Gravity.LEFT, getString(R.string.log_history))
                .withSingleFooterButton(getString(R.string.see_more));
        Helper.showAdvancedDialog(getSupportFragmentManager(), builder).setDelegate(identifier -> {
          startActivity(new Intent(this, WebLoaderActivity.class).putExtra("keephistory", true)
              .putExtra("url", "https://dompetsehat.com/android/log"));
        });
        break;
      case About:
        Helper.goTo(this, AboutActivity.class);
        break;
      case Tags:
        Helper.goTo(this, TagActivity.class);
        break;
    }
  }

  public void listen() {
    presenter.listenPasscode(this);
  }

  @Override protected void onDestroy() {
    presenter.detachView();
    super.onDestroy();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Timber.e("avesina itu orang hebat " + requestCode + " " + resultCode);
    if (requestCode == REQ_PASS) {
      if (RESULT_OK == resultCode) {
        Helper.showCustomSnackBar(recyclerView, getLayoutInflater(), "Passcode berhasil di hapus");
        presenter.clearPasscode();
      }
      presenter.populateData();
    }
  }

  @Override public void startLoading(String message) {

  }

  @Override public void error() {

  }

  @Override public void complete(String message) {

  }

  @Override public void onnext(String message) {

  }

  @Override public void onPick(int number) {
    presenter.saveInitialDateofMonth(number);
  }
}