package javasign.com.dompetsehat.ui.activities.setting.sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.sharing.SharingDataInterface;
import javasign.com.dompetsehat.presenter.sharing.SharingDataPresenter;
import javasign.com.dompetsehat.ui.activities.setting.sharing.adapters.AdapterMember;
import javasign.com.dompetsehat.ui.activities.setting.sharing.permission.PermissionSettingActivity;
import javasign.com.dompetsehat.ui.activities.setting.sharing.permission.model.PermissionModel;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by lafran on 1/3/17.
 */

public class InviteMemberSharingDataActivity extends BaseActivity implements SharingDataInterface {

  @Bind(R.id.et_groupname) MaterialEditText et_groupname;
  @Bind(R.id.et_member) EditText et_member;
  @Bind(R.id.ic_menu) View ic_menu;
  @Bind(R.id.recycleview) RecyclerView recyclerView;
  private BlankView bv;
  @Inject SharingDataPresenter presenter;

  private AdapterMember adapterMember;
  private ItemEventHelper<AdapterMember.Member> itemEventHelper;
  private int REQ_CODE = 5;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_member_sharing_data);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    setTitle(getString(R.string.group_sharing));
    itemEventHelper = ItemEventHelper.attachToActivity(this, "");
    bv = new BlankView(ButterKnife.findById(this, R.id.rootview),
        DSFont.Icon.dsf_comission_filled.getFormattedName(),
        getString(R.string.no_shared_data));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
    ic_menu.setVisibility(View.GONE);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.ic_menu) void onSave(View v) {
    Helper.showCustomSnackBar(v, getLayoutInflater(), "Berhasil disimpan");
  }

  @OnClick(R.id.btn_add) void addMember() {
    if (adapterMember != null) {
      String name = et_member.getText().toString();
      //AdapterMember.Member m = new AdapterMember.Member(name);
      //adapterMember.addMember(m);
      //recyclerView.scrollToPosition(0);
      presenter.addAgent(name);
      et_member.setText("");
      Helper.hideKeyboard(this);
    }
  }

  private void init() {
    bv.beginLoading(null);
    presenter.getData();
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    View ic_check = ButterKnife.findById(this, R.id.ic_menu);
    View btn_add = ButterKnife.findById(this, R.id.btn_add);
    Words.setButtonToListen(ic_check, et_groupname);
    Words.setButtonToListen(btn_add, et_member);
  }

  public void setAdapter(ArrayList<AdapterMember.Member> members) {
    adapterMember = new AdapterMember(members);
    adapterMember.setItemEventHelper(itemEventHelper);
    adapterMember.setOnClickItem(new ItemEventHelper.OnClickItem<AdapterMember.Member>() {
      @Override public void onClick(View v, AdapterMember.Member item, int section, int position) {
        seeDetail(item);
      }

      @Override
      public void onLongPressed(View v, AdapterMember.Member item, int section, int position) {
        itemEventHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {
          @Override public void onEdit() {
            seeDetail(item);
          }

          @Override public void onDelete() {
            adapterMember.remove(item);
            //final DeleteUtils deleteUtils = beginPendingRemoval();
          }
        });
      }
    });
    recyclerView.setAdapter(adapterMember);
  }

  private void seeDetail(AdapterMember.Member item) {
    startActivityForResult(new Intent(this, PermissionSettingActivity.class).putExtra(
        PermissionSettingActivity.KEY_USERNAME, item.name)
        .putExtra(PermissionSettingActivity.KEY_PERMISSION, item.permission)
        .putExtra(PermissionSettingActivity.KEY_ID_AGENT, item.id),REQ_CODE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == REQ_CODE){
      if(resultCode == RESULT_OK){
        presenter.getData();
      }
    }
  }

  @Override public void setData(ArrayList<AdapterMember.Member> members) {
    runOnUiThread(() -> {
      Helper.checkIfBlank(bv, members.isEmpty());
      setAdapter(members);
    });
  }

  @Override public void setAdapterModels(ArrayList<PermissionModel> models) {

  }

  @Override public void onError() {

  }

  @Override public void onNext() {

  }

  @Override public void onComplete() {

  }
}
