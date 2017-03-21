package javasign.com.dompetsehat.ui.activities.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.reminder.ReminderInterface;
import javasign.com.dompetsehat.presenter.reminder.ReminderPresenter;
import javasign.com.dompetsehat.ui.activities.reminder.adapter.AdapterReminder;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;
import javasign.com.dompetsehat.ui.activities.search.SearchActivity;
import javasign.com.dompetsehat.ui.event.DeleteAlarmEvent;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.view.BlankView;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class ReminderActivity extends BaseActivity
    implements SwipeRefreshLayout.OnRefreshListener, ReminderInterface {

  @Bind(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.tv_total_bill) TextView tv_total_bill;
  @Bind(R.id.tv_total_balance) TextView tv_total_balance;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private AdapterReminder adapterReminder;
  private BlankView bv;
  private ItemEventHelper<ReminderModel> itemEventHelper;
  private boolean onDelete = false;

  @Inject ReminderPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reminder);
    ButterKnife.bind(this);

    FloatingButtonHelper.init(this, ButterKnife.findById(this, R.id.rootview))
        .leaveOnlyPlusButton(
            v -> startActivity(new Intent(ReminderActivity.this, AddReminderActivity.class)))
        .attachToRecyclerview(recyclerView);

    getActivityComponent().inject(this);
    presenter.attachView(this);
    bv = new BlankView(ButterKnife.findById(this, R.id.rootview),
        DSFont.Icon.dsf_reminder_2.getFormattedName(), getString(R.string.you_dont_have_reminder));
    bv.beginLoading(null);
    itemEventHelper = ItemEventHelper.attachToActivity(this, "");
    setTitle(getString(R.string.reminder_title));
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);

    ButterKnife.findById(this, R.id.ic_menu).setVisibility(View.GONE);
    ButterKnife.findById(this, R.id.ic_search).setVisibility(View.GONE);
  }

  @OnClick(R.id.ic_back) void onBack(View v) {
    finish();
  }

  @OnClick(R.id.ic_menu) void addReminder(View v) {
    startActivity(new Intent(this, AddReminderActivity.class));
  }

  @OnClick(R.id.ic_search) void doSearch(View v) {
    startActivity(new Intent(this, SearchActivity.class).putExtra(SearchActivity.KEY_FLAG_FROM,
        SearchActivity.FLAG_SET_SEARCH_FOR_REMINDER));
  }

  private void init() {
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    refreshLayout.setOnRefreshListener(this);
    refreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.color_scheme_loading));
    Helper.trackThis(this, "User di halaman Pengingat/Reminder");
    setData();

    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(DeleteAlarmEvent.class)
        .subscribe(new SimpleObserver<DeleteAlarmEvent>() {
          @Override public void onNext(DeleteAlarmEvent deleteAlarmEvent) {
            setData();
          }
        });
  }

  private void setData() {
    refreshLayout.setRefreshing(true);
    presenter.loadData();
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override public void setHeaderData(int count, double totalBill, double totalBalance) {

    int colorAccent = Helper.accentColor(this, totalBill, totalBalance);

    tv_total_bill.setText(format.toRupiahFormatSimple(totalBill));
    tv_total_bill.setTextColor(colorAccent);
    tv_total_balance.setText(format.toRupiahFormatSimple(totalBalance));
    tv_total_balance.setTextColor(ContextCompat.getColor(this, R.color.blue_traveloka));
  }

  @Override public void setAdapter(List<ReminderModel> reminderModels) {
    runOnUiThread(() -> {
      adapterReminder = new AdapterReminder(reminderModels);
      adapterReminder.setPresenter(presenter);
      adapterReminder.setItemEventHelper(itemEventHelper);
      itemEventHelper.setRefreshLayoutDisableIfExist(refreshLayout);
      itemEventHelper.setAdapterOnClickItem(new ItemEventHelper.OnClickItem<ReminderModel>() {
        @Override public void onClick(View v, ReminderModel item,int section,int position) {
          seeDetail(item);
        }

        @Override public void onLongPressed(View v, ReminderModel item,int section,int position) {
          itemEventHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {

            @Override public void onEdit() {
              seeDetail(item);
            }

            @Override public void onDelete() {
              if(!onDelete) {
                onDelete = true;
                adapterReminder.remove(item);
                final DeleteUtils deleteUtils = beginPendingRemoval(item);
                Helper.deleteWithConfirmationMessage(v, ReminderActivity.this, getString(R.string.reminder_has_been_deleted), v1 -> {
                  deleteUtils.cancelRemoval();
                }, new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                  @Override public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    onDelete = false;
                  }
                });
              }
            }
          });
        }
      });

      recyclerView.setAdapter(adapterReminder);
      adapterReminder.notifyDataSetChanged();
      Helper.disposeSwiper(refreshLayout);
      Helper.checkIfBlank(bv, reminderModels.isEmpty());
    });
  }

  @Override public void setAlarm(Alarm alarm, Category category,UserCategory userCategorygit ) {

  }

  private DeleteUtils beginPendingRemoval(ReminderModel item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, presenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        setData();
      }

      @Override public void onDoneRemoving() {
        MyCustomApplication.getRxBus().send(new DeleteAlarmEvent(getApplicationContext()));
      }
    });
    return deleteUtils;
  }

  private void seeDetail(ReminderModel model) {
    startActivity(new Intent(ReminderActivity.this, AddReminderActivity.class).putExtra(
        AddReminderActivity.TAG_MODE, AddReminderActivity.TAG_MODE_EDIT)
        .putExtra(AddReminderActivity.TAG_ID_ALARM, model.identifier));
  }

  @Override public void onRefresh() {
    setData();
  }

  @Override protected void onStop() {
    Helper.disposeSwiper(refreshLayout);
    super.onStop();
  }

  @Override protected void onDestroy() {
    Helper.disposeSwiper(refreshLayout);
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }

  //@Override public void onItemClick(AdapterReminder.Holder viewHolder, ReminderModel model) {
  //  if (presenter.setActiveReminder(model)) {
  //    viewHolder.setReminderActive(!model.isActive);
  //  } else {
  //    Toast.makeText(getActivityComponent().context(), getString(R.string.error_source_unknown),
  //        Toast.LENGTH_LONG).show();
  //  }
  //}

  //@Override public void onDelete(ReminderModel item, int position) {
  //  ReminderModel reminderModel = (ReminderModel) item;
  //  presenter.deleteReminder(reminderModel.identifier);
  //}
  //
  //@Override public void onCancel(ReminderModel item, int position) {
  //
  //}
}

