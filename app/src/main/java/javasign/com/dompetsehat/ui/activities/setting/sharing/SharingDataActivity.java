package javasign.com.dompetsehat.ui.activities.setting.sharing;

import android.content.Intent;
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
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.presenter.sharing.SharingDataPresenter;
import javasign.com.dompetsehat.ui.activities.setting.sharing.notif.NotificationActivity;
import javasign.com.dompetsehat.ui.activities.setting.sharing.adapters.AdapterSharingData;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;

/**
 * Created by lafran on 1/3/17.
 */

public class SharingDataActivity extends BaseActivity {

  @Bind(R.id.recycleview) RecyclerView recyclerview;
  @Bind(R.id.tv_notif) TextView tv_notif;
  @Inject SharingDataPresenter presenter;
  private AdapterSharingData adapter;
  private ItemEventHelper<AdapterSharingData.SharingModel> itemEventHelper;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sharing_data);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);

    FloatingButtonHelper.init(this, ButterKnife.findById(this, R.id.rootview))
        .leaveOnlyPlusButton(v -> {
          Helper.goTo(SharingDataActivity.this, InviteMemberSharingDataActivity.class);
        })
    .attachToRecyclerview(recyclerview);
    itemEventHelper = ItemEventHelper.attachToActivity(this, Words.NONE);
    setTitle(getString(R.string.group_sharing));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  @OnClick(R.id.ic_menu) void openNotif(){
    Helper.goTo(this, NotificationActivity.class);
  }

  private void init() {
    recyclerview.setLayoutManager(new LinearLayoutManager(this));

    ArrayList<AdapterSharingData.SharingModel> models =
        new ArrayList<AdapterSharingData.SharingModel>();

    models.add(new AdapterSharingData.SharingModel("DS-Go"));
    models.add(new AdapterSharingData.SharingModel("Dompetsehat"));
    models.add(new AdapterSharingData.SharingModel("KoinWorks"));

    setAdapter(models);
    //TODO : set notification count, call this
    setNotifCount(8);
  }

  private void setNotifCount(int count){
    String numberText = "";
    tv_notif.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    numberText = String.valueOf(count);

    if(count > 99) numberText = count + "+";

    tv_notif.setText(numberText);
  }

  private void setAdapter(ArrayList<AdapterSharingData.SharingModel> models){
    adapter = new AdapterSharingData(models);
    adapter.setItemEventHelper(itemEventHelper);
    adapter.setOnClickItem(new ItemEventHelper.OnClickItem<AdapterSharingData.SharingModel>() {
      @Override public void onClick(View v, AdapterSharingData.SharingModel item,int section,int position) {
        seeDetail(item);
      }

      @Override public void onLongPressed(View v, AdapterSharingData.SharingModel item,int section,int position) {
        itemEventHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick(){
          @Override public void onEdit() {
            seeDetail(item);
          }

          @Override public void onDelete() {
            adapter.remove(item);
            //final DeleteUtils deleteUtils = beginPendingRemoval(item);
            //Helper.deleteWithConfirmationMessage(v, SharingDataActivity.this, "yakin mau dihapus?",
            //    v1 -> {
            //
            //    }, null);
          }
        });
      }
    });
    recyclerview.setAdapter(adapter);
    adapter.notifyDataSetChanged();
  }

  private void seeDetail(AdapterSharingData.SharingModel model){
    startActivity(new Intent(this, InviteMemberSharingDataActivity.class));
  }

  /*private DeleteUtils beginPendingRemoval(AdapterSharingData.SharingModel item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, presenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        init();
      }

      @Override public void onDoneRemoving() {
        MyCustomApplication.getRxBus().send(new SharingDataEvent());
      }
    });
    return deleteUtils;
  }*/

}
