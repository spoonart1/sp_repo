package javasign.com.dompetsehat.ui.activities.setting.export;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.setting.SettingInterface;
import javasign.com.dompetsehat.presenter.setting.SettingPresenter;
import javasign.com.dompetsehat.ui.activities.setting.adapter.AdapterSetting;
import javasign.com.dompetsehat.utils.Helper;
import javax.inject.Inject;

/**
 * Created by lafran on 12/30/16.
 */

public class ExportSettingActivity extends BaseActivity implements SettingInterface {

  @Bind(R.id.radio_group) RadioGroup radioGroup;
  @Bind(R.id.sp_account) AppCompatSpinner sp_account;
  @Bind(R.id.sp_type) AppCompatSpinner sp_type;
  @Bind(R.id.tv_start) TextView tv_start;
  @Bind(R.id.tv_end) TextView tv_end;
  @Bind(R.id.ll_parent) LinearLayout ll_parent;

  @Inject SettingPresenter presenter;
  private ProgressDialog dialog;
  private Account selectedAccount;
  String[] types = new String[] { "Semua", "Pengeluaran saja", "Pemasukan saja" };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting_export);
    getActivityComponent().inject(this);
    ButterKnife.bind(this);
    presenter.attachView(this);
    dialog = new ProgressDialog(this);
    setTitle(getString(R.string.export_data));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick({ R.id.ll_end, R.id.ll_start }) void openDateTime(View v) {
    switch (v.getId()) {
      case R.id.ll_start:
        initDateTimeDialog(null, new SlideDateTimeListener() {
          @Override public void onDateTimeSet(Date date) {
            String d = Helper.setSimpleDateFormat(date, GeneralHelper.FORMAT_YYYY_MM_DD);
            tv_start.setText(d);
          }
        });
        break;

      case R.id.ll_end:
        initDateTimeDialog(Calendar.getInstance().getTime(), new SlideDateTimeListener() {
          @Override public void onDateTimeSet(Date date) {
            String d = Helper.setSimpleDateFormat(date, GeneralHelper.FORMAT_YYYY_MM_DD);
            tv_end.setText(d);
          }
        });
        break;
    }
  }

  @OnClick(R.id.btn_export) void export() {
    String date_start = null;
    if (!tv_start.getText().toString().equalsIgnoreCase("---")) {
      date_start = tv_start.getText().toString();
    }
    String date_end = null;
    if (!tv_end.getText().toString().equalsIgnoreCase("---")) {
      date_end = tv_end.getText().toString();
    }
    String type = "All";
    if (sp_type.getSelectedItemPosition() == 1) {
      type = "DB";
    } else if (sp_type.getSelectedItemPosition() == 2) {
      type = "CR";
    }
    RadioButton radioButton = ButterKnife.findById(radioGroup, radioGroup.getCheckedRadioButtonId());
    if (radioButton == null) {
      Toast.makeText(this, "Pilih salah satu tipe file", Toast.LENGTH_LONG).show();
      return;
    }

    Helper.trackThis(this, "user klik "+radioButton.getText());

    String finalDate_start = date_start;
    String finalDate_end = date_end;
    String finalType = type;
    createDialog(getString(R.string.export_confirmation_dialog_title),
        getString(R.string.export_confirmation_dialog_message), "Backup", getString(R.string.save),
        getString(R.string.cancel), (dialogInterface, i) -> {
          presenter.exportData(radioButton.getText().toString(), selectedAccount, finalType,
              finalDate_start, finalDate_end,true);
        }, (dialogInterface, i) -> {
          presenter.exportData(radioButton.getText().toString(), selectedAccount, finalType,
              finalDate_start, finalDate_end,false);
        }, (dialogInterface, i) -> {
          dialogInterface.dismiss();
        });
  }

  public void shareBackup(String path) {
    String to = "";
    String subject = "Backup Dompet Sehat";
    String message = "Your backup is attached";
    Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
    email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
    email.putExtra(Intent.EXTRA_SUBJECT, subject);
    email.putExtra(Intent.EXTRA_TEXT, message);
    File f = new File(path);
    email.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
        new ArrayList<>(Arrays.asList(Uri.fromFile(f))));
    email.setType("text/*");
    startActivity(Intent.createChooser(email, "Send"));
  }

  private void initDateTimeDialog(Date initialDate, SlideDateTimeListener listener) {
    new SlideDateTimePicker.Builder(getSupportFragmentManager()).setInitialDate(initialDate)
        .setListener(listener)
        .setIsDateOnly(true)
        .setIs24HourTime(true)
        .setIndicatorColor(Helper.GREEN_DOMPET_COLOR)
        .build()
        .show();
  }

  private void init() {
    presenter.setSpinnerAccount();
    ArrayAdapter typeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, types);
    typeAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
    sp_type.setAdapter(typeAdapter);
  }

  public void createDialog(String title, String message, String neutralLabel, String positiveLabel,
      String negativeLabel, DialogInterface.OnClickListener dialogInterfaceNeutral,
      DialogInterface.OnClickListener dialogInterfacePositif,
      DialogInterface.OnClickListener dialogInterfaceNegatif) {
    AlertDialog.Builder builder = new AlertDialog.Builder(
        new ContextThemeWrapper(this, R.style.Theme_AppCompat_NoActionBar_FullScreen));
    builder.setTitle(title);
    builder.setMessage(message);
    if (neutralLabel != null) {
      builder.setNeutralButton(neutralLabel, dialogInterfaceNeutral);
    }
    if (positiveLabel != null) {
      builder.setPositiveButton(positiveLabel, dialogInterfacePositif);
    }
    if (negativeLabel != null) {
      builder.setNegativeButton(negativeLabel, dialogInterfaceNegatif);
    }
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  @Override public void startLoading(String message) {
    dialog.setMessage(message);
    dialog.show();
  }

  @Override public void error() {
    dialog.dismiss();
  }

  @Override public void complete(String message) {
    dialog.dismiss();
    Helper.showCustomSnackBar(ll_parent,getLayoutInflater(),"Data berhasil di export",false,
        ContextCompat.getColor(this,R.color.orange), Gravity.BOTTOM);
  }

  @Override public void onnext(String message) {
    runOnUiThread(() -> {
      dialog.setMessage(message);
    });
  }

  @Override public void setAdapter(List<AdapterSetting.SettingModel> settingModels) {

  }

  @Override public void setSpinner(ArrayList<Account> arrayLists, String[] labels) {
    runOnUiThread(() -> {
      ArrayAdapter accountAdapter =
          new ArrayAdapter(ExportSettingActivity.this, android.R.layout.simple_spinner_item,
              labels);
      accountAdapter.setDropDownViewResource(R.layout.spinner_item_drop_down);
      sp_account.setAdapter(accountAdapter);
      sp_account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
          selectedAccount = arrayLists.get(i);
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView) {

        }
      });
      sp_account.setSelection(0);
    });
  }

  @Override public void setPath(String path) {
    if(path != null) {
      shareBackup(path);
    }
  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
