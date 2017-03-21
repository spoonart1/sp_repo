package javasign.com.dompetsehat.ui.activities.setting.sharing.notif;

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
import javasign.com.dompetsehat.ui.activities.setting.sharing.adapters.AdapterNotification;
import javasign.com.dompetsehat.utils.ItemEventHelper;

/**
 * Created by lafran on 1/3/17.
 */

public class NotificationActivity extends BaseActivity {

  @Bind(R.id.recycleview) RecyclerView recycleview;

  private AdapterNotification adapterNotification;
  private ItemEventHelper itemEventHelper;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    setTitle(getString(R.string.notification));
    itemEventHelper = ItemEventHelper.attachToActivity(this, "").deleteMenuOnly();
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
    recycleview.setLayoutManager(new LinearLayoutManager(this));
    ArrayList<AdapterNotification.NotifModel> models = new ArrayList<>();
    models.add(
        new AdapterNotification.NotifModel("Notif 1", "description 1", "Jumat, 23 Des 2017"));
    models.add(
        new AdapterNotification.NotifModel("Notif 2", "description 2", "Sabtu, 24 Des 2017"));
    models.add(
        new AdapterNotification.NotifModel("Notif 3", "description 3", "Minggu, 25 Des 2017"));
    models.add(
        new AdapterNotification.NotifModel("Notif 4", "description 4", "Senin, 26 Des 2017"));
    setAdapter(models);
  }

  private void setAdapter(ArrayList<AdapterNotification.NotifModel> models) {
    adapterNotification = new AdapterNotification(models);
    adapterNotification.setItemEventHelper(itemEventHelper);
    adapterNotification.setOnClickItem(
        new ItemEventHelper.OnClickItem<AdapterNotification.NotifModel>() {
          @Override public void onClick(View v, AdapterNotification.NotifModel item,int section,int position) {
            //TODO : on click item notification?
          }

          @Override public void onLongPressed(View v, AdapterNotification.NotifModel item,int section,int position) {
            itemEventHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {
              @Override public void onDelete() {
                adapterNotification.remove(item);
                //final DeleteUtils deleteUtils = beginPendingRemoval(item);
              }
            });
          }
        });
    recycleview.setAdapter(adapterNotification);
    adapterNotification.notifyDataSetChanged();
  }

  /*private DeleteUtils beginPendingRemoval(AdapterNotification.NotifModel item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, presenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        //setData();
      }

      @Override public void onDoneRemoving() {
        //MyCustomApplication.getRxBus().send(new DeleteBudgetEvent(getActivity().getApplicationContext()));
      }
    });
    return deleteUtils;
  }*/
}
