package javasign.com.dompetsehat.ui.activities.setting.sharing.permission;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.sharing.SharingDataInterface;
import javasign.com.dompetsehat.presenter.sharing.SharingDataPresenter;
import javasign.com.dompetsehat.ui.activities.setting.sharing.adapters.AdapterMember;
import javasign.com.dompetsehat.ui.activities.setting.sharing.adapters.AdapterPermission;
import javasign.com.dompetsehat.ui.activities.setting.sharing.permission.model.PermissionModel;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by lafran on 1/5/17.
 */

public class PermissionSettingActivity extends BaseActivity implements SharingDataInterface {

  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.subtitle) TextView subtitle;
  public static String KEY_USERNAME = "username";
  public static String KEY_ID_AGENT = "id_agent";
  public static String KEY_PERMISSION = "permission";

  private String username = "";
  private int id_agent;
  private HashMap<String,Boolean> permission;

  @Inject SharingDataPresenter presenter;
  ProgressDialog dialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting_permission);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    setTitle(getString(R.string.setting));
    presenter.attachView(this);
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  @OnClick(R.id.ic_menu) void onSave(){
    save();
  }

  public void setAbleToSave(boolean enable){
    View btn_check = ButterKnife.findById(this, R.id.ic_menu);
    btn_check.setEnabled(enable);
  }

  private void init() {
    dialog = new ProgressDialog(this);
    if(getIntent().hasExtra(KEY_USERNAME) && getIntent().hasExtra(KEY_ID_AGENT) && getIntent().hasExtra(KEY_PERMISSION)){
      username = getIntent().getExtras().getString(KEY_USERNAME);
      permission = (HashMap<String, Boolean>) getIntent().getSerializableExtra(KEY_PERMISSION);
      if(permission == null){
        permission = new HashMap<>();
      }
      Timber.e("avesina "+permission);
      id_agent = getIntent().getExtras().getInt(KEY_ID_AGENT);
    }else{
      finish();
    }
    subtitle.setText(getString(R.string.permission_data) +" "+ username);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    presenter.setListPermission(permission);
  }

  public void setAdapterModels(ArrayList<PermissionModel> models){
    AdapterPermission adapterPermission = new AdapterPermission(models);
    adapterPermission.setOnStateChange((model, status) -> {
      setAbleToSave(true);
      Timber.e("avesina before"+permission);
      permission.put(model.keyName,status);
      Timber.e("avesina after"+permission);
    });
    recyclerView.setAdapter(adapterPermission);
  }

  @Override public void onError() {
    dialog.dismiss();
  }

  @Override public void onNext() {
    dialog.dismiss();
  }

  @Override public void onComplete() {
    dialog.dismiss();
    setResult(Activity.RESULT_OK);
    finish();
  }

  private void save() {
    dialog.setMessage(getString(R.string.memuat_data));
    dialog.show();
    presenter.saveData(id_agent,permission);
  }

  @Override public void setData(ArrayList<AdapterMember.Member> members) {

  }
}
