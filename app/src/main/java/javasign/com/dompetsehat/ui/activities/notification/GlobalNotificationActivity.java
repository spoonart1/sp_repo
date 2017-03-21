package javasign.com.dompetsehat.ui.activities.notification;

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
import javasign.com.dompetsehat.presenter.notification.NotificationInterface;
import javasign.com.dompetsehat.presenter.notification.NotificationPresenter;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.notification.adapter.AdapterGlobalNotification;
import javasign.com.dompetsehat.ui.activities.notification.pojo.GlobalNotifModel;
import javasign.com.dompetsehat.ui.activities.notification.pojo.TypeNotif;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by lafran on 3/9/17.
 */

public class GlobalNotificationActivity extends BaseActivity implements NotificationInterface {

  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Inject NotificationPresenter presenter;
  @Inject SyncPresenter presenterSync;
  private BlankView bv;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_global_notification);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    ButterKnife.bind(this);
    setTitle(getString(R.string.notification));

    View rootview = ButterKnife.findById(this, R.id.rootview);
    bv = new BlankView(rootview, DSFont.Icon.dsf_notification_2.getFormattedName(),
        "Tidak ada notifikasi");
    bv.beginLoading(null);

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
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recycleview.setLayoutManager(layoutManager);
    presenter.loadData();
  }

  @Override public void setAdapter(ArrayList<GlobalNotifModel> models) {
    AdapterGlobalNotification adapter = new AdapterGlobalNotification(models);
    adapter.setOnReadItem((v, model, pos) -> {
      model.starActivity(GlobalNotificationActivity.this,presenterSync);
      presenter.readNotif(model.key);
    });
    recycleview.setAdapter(adapter);
    Helper.checkIfBlank(bv, models.isEmpty());
  }
}
