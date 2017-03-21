package javasign.com.dompetsehat.ui.activities.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.presenter.account.AccountPresenter;
import javasign.com.dompetsehat.presenter.sync.SyncInterface;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.account.base.BaseAccountActivity;
import javasign.com.dompetsehat.ui.activities.account.pojo.CellAccount;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.LoginKlikMamiActivity;
import javasign.com.dompetsehat.ui.event.DeleteAccountEvent;
import javasign.com.dompetsehat.ui.fragments.account.adapter.NewManageAccountAdapter;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.DeleteUtils;
import javasign.com.dompetsehat.utils.FloatingButtonHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.State;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.AccountView;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by lafran on 9/15/16.
 */

public class NewManageAccountActivity extends BaseAccountActivity implements SyncInterface {

  public static String accessFrom = "";

  @Bind(R.id.tv_total) TextView tv_total;
  @Bind(R.id.refresh_layout) SwipeRefreshLayout refresh_layout;
  @Bind(R.id.recycleview) RecyclerView recycleview;

  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private NewManageAccountAdapter adapter;
  private ItemEventHelper itemEventHelper;

  @Inject AccountPresenter presenter;
  @Inject SyncPresenter syncPresenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_manage_account);
    ButterKnife.bind(this);
    itemEventHelper = ItemEventHelper.attachToActivity(this, "");
    FloatingButtonHelper.init(this, ButterKnife.findById(this, R.id.rootview))
        .leaveOnlyPlusButton(v -> startActivity(
            new Intent(NewManageAccountActivity.this, AddAccountActivity.class)));
    getActivityComponent().inject(this);
    presenter.attachView(this);
    syncPresenter.attachView(this);

    setTitle(getString(R.string.account));
    init();

    //((MyCustomApplication)getApplication()).showMessage(this,"Asyk");
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    ButterKnife.findById(this, R.id.ic_menu).setVisibility(View.GONE);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_menu) void addAccount() {
    Helper.goTo(this, AddAccountActivity.class);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  private void init() {
    final LinearLayoutManager layoutManager =
        new LinearLayoutManager(NewManageAccountActivity.this, LinearLayoutManager.VERTICAL, false);
    recycleview.setLayoutManager(layoutManager);
    refresh_layout.setOnRefreshListener(this);
    setData();

    MyCustomApplication.getRxBus()
        .toObserverable()
        .ofType(DeleteAccountEvent.class)
        .subscribe(new SimpleObserver<DeleteAccountEvent>() {
          @Override public void onNext(DeleteAccountEvent deleteAccountEvent) {
            setData();
          }
        });
  }

  private void setData() {
    presenter.loadData();
  }

  @Override public void setTotalBalance(double total) {
    runOnUiThread(() -> {
      String totalBalances = format.toRupiahFormatSimple(new Double(total).intValue());
      tv_total.setText(totalBalances);
    });
  }

  @Override public void setAdapter(ArrayList<NewManageAccountAdapter.VendorParent> arrayList) {
    runOnUiThread(() -> {
      adapter = new NewManageAccountAdapter(NewManageAccountActivity.this, this,
          arrayList).setItemEventHelper(itemEventHelper);

      adapter.setOnClickItem(new ItemEventHelper.OnClickItem<CellAccount>() {
        @Override public void onClick(View v, CellAccount item,int section,int position) {
          seeDetail(item, v);
        }

        @Override public void onLongPressed(View v, CellAccount item,int section,int position) {
          itemEventHelper.showMenu(R.id.btn_rename);
          itemEventHelper.showEditorBar(new ItemEventHelper.SimpleMenuEditorClick() {
            @Override public void onEdit() {
              if (item != null) {
                Intent i = new Intent(NewManageAccountActivity.this, AddAccountBankActivity.class);
                if (item.getVendorId() == AccountView.DP) {
                  i = new Intent(NewManageAccountActivity.this, AddAccountDompetActivity.class);
                }
                if (item.getVendorId() == AccountView.MNL) {
                  i = new Intent(NewManageAccountActivity.this, LoginKlikMamiActivity.class);
                }
                if (item.getVendor() != null) {
                  i.putExtra(Words.ID_VENDOR, item.getVendor().getId());
                  i.putExtra(Words.NAMA_VENDOR, item.getVendor().getVendor_name());
                }
                i.putExtra(ManageEachAccountActivity.MODE_EDIT, true);
                i.putExtra(Words.ACCOUNT_ID, item.getAccount().getIdaccount());
                startActivity(i);
              } else {
                Toast.makeText(NewManageAccountActivity.this,getString(R.string.error_account_not_found), Toast.LENGTH_LONG).show();
                finish();
              }
            }

            @Override public void onRename() {
              final EditText input = new EditText(NewManageAccountActivity.this);
              String desc = item.getName();
              input.setText(desc);
              LinearLayout.LayoutParams lp =
                  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                      LinearLayout.LayoutParams.MATCH_PARENT);
              lp.setMargins(100,100,100,100);
              input.setLayoutParams(lp);
              AlertDialog alertDialog =
                  new AlertDialog.Builder(NewManageAccountActivity.this).setTitle("Edit Name Account")
                      .setView(input)
                      .setPositiveButton(NewManageAccountActivity.this.getString(R.string.save),
                          (dialogInterface, i) -> {
                            Timber.e("aveina");
                            presenter.renameAccount(item.getAccount(),input.getText().toString());
                            dialogInterface.dismiss();
                          }).setNegativeButton(NewManageAccountActivity.this.getString(R.string.cancel),(dialogInterface, i) -> dialogInterface.dismiss())
                      .create();
              alertDialog.show();
            }

            @Override public void onDelete() {
              AlertDialog dialog = new AlertDialog.Builder(NewManageAccountActivity.this).
                  setMessage(getString(R.string.warn_if_delete_account))
                  .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                    final DeleteUtils deleteUtils = beginPendingRemoval(item.getAccount());
                    Helper.deleteWithConfirmationMessage(v, NewManageAccountActivity.this,
                        getString(R.string.deleting_account), v1 -> deleteUtils.cancelRemoval(),
                        null);
                  })
                  .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                  })
                  .create();
              dialog.show();
            }
          });
        }
      });

      recycleview.setAdapter(adapter);
      adapter.notifyDataSetChanged();
      //adapter.setAddAccountClick((v, cell) -> {
      //  CircleShapeView circleShapeView = ButterKnife.findById(v, R.id.cv_background);
      //  adapter.editItem(cell, circleShapeView);
      //});
      //adapter.setDeleteAccountClick((view, position, account) -> {
      //  presenter.deleteAccount(view, account.getIdaccount());
      //});

      Helper.disposeSwiper(refresh_layout);
    });
  }

  private DeleteUtils beginPendingRemoval(Account item) {
    DeleteUtils deleteUtils = new DeleteUtils().execute(item, presenter);
    deleteUtils.setOnDeleteListener(new DeleteUtils.OnDeleteListener() {
      @Override public void onCancelRemoving() {
        setData();
      }

      @Override public void onDoneRemoving() {
        MyCustomApplication.getRxBus().send(new DeleteAccountEvent());
      }
    });
    return deleteUtils;
  }

  private void seeDetail(CellAccount item, View v) {
    CircleShapeView cv = ButterKnife.findById(v, R.id.cv_background);
    adapter.editItem(item, cv);
  }

  @Override protected void onResume() {
    super.onResume();
    //onRefresh();
  }

  @Override public void onRefresh() {
    setData();
  }

  @Override public void onDestroy() {
    Helper.disposeSwiper(refresh_layout);
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void onPause() {
    Helper.disposeSwiper(refresh_layout);
    super.onPause();
  }

  @Override public void onNext(int code, String message) {

  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == State.FLAG_ACTIVITY_WILL_FINISH_AFTER) {
      finish();
    }
  }
}
