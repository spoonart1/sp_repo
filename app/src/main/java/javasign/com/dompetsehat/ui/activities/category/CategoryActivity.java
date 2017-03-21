package javasign.com.dompetsehat.ui.activities.category;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.category.CategoryInterface;
import javasign.com.dompetsehat.presenter.category.CategoryPresenter;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategory;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategoryFragmentPager;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/11/16.
 */
public class CategoryActivity extends BaseActivity implements CategoryInterface {

  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.pager) ViewPager pager;
  @Bind(R.id.spacer) View spacer;

  @Inject DbHelper db;
  @Inject CategoryPresenter presenter;
  private SessionManager session;
  private RxBus rxBus;
  private AdapterCategoryFragmentPager adapterCategoryFragmentPager;

  public static String KEY_CATEGORY = "key_category";
  public static String FROM = "from";

  public static String ACTIVE_CATEGORY = "active_category";

  final int REQUEST_CATEGORY = 1;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_category);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    presenter.attachView(this);
    session = new SessionManager(getActivityComponent().context());
    rxBus = MyCustomApplication.getRxBus();
    init();
    presenter.getEventAddCategory();
  }

  public void init() {
    String type = "All";
    String active_type = "DB";
    if (getIntent().hasExtra(ACTIVE_CATEGORY)) {
      active_type = getIntent().getExtras().getString(ACTIVE_CATEGORY);
    }

    if (getIntent().hasExtra(KEY_CATEGORY)) {
      type = getIntent().getExtras().getString(KEY_CATEGORY);
      spacer.setVisibility(View.VISIBLE);
    }
    if (getIntent().hasExtra(FROM)) {
      if (getIntent().getExtras().getString(FROM).contentEquals("budget")) {
        presenter.init(REQUEST_CATEGORY, active_type, type, true);
        tablayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
      } else if (getIntent().getExtras().getString(FROM).contentEquals("setting")) {
        presenter.getCategegoryList(REQUEST_CATEGORY, active_type, type, false, true,
            new AdapterCategory.OnCategoryClick() {
              @Override public void onClickParent(Category category, int section) {
                dialogEditCateogry(category);
              }

              @Override public void onClickChild(UserCategory userCategory, int position) {

              }

              @Override public void onLongClickParent(Category category, int section) {

              }
            });
      }
    } else {
      presenter.init(REQUEST_CATEGORY, active_type, type, false);
    }
  }

  private void dialogEditCateogry(Category category) {
    View view = getLayoutInflater().inflate(R.layout.dialog_edit_category, null);
    final EditText et_cat_name = ButterKnife.findById(view, R.id.et_cat_name);
    final EditText et_cat_desc = ButterKnife.findById(view, R.id.et_cat_desc);
    et_cat_name.setText(category.getName());
    et_cat_desc.setText(category.getDescription());
    AlertDialog alertDialog =
        new AlertDialog.Builder(CategoryActivity.this).setTitle("Edit description")
            .setView(view)
            .setPositiveButton(CategoryActivity.this.getString(R.string.save),
                (dialogInterface, i) -> {
                  presenter.changeCategory(category.getId_category(),
                      et_cat_name.getText().toString(), et_cat_desc.getText().toString());
                })
            .setNegativeButton(CategoryActivity.this.getString(R.string.cancel),
                (dialogInterface, i) -> dialogInterface.dismiss())
            .create();
    alertDialog.show();
  }

  @OnClick(R.id.ic_back) void doBack(View v) {
    setResult(RESULT_CANCELED);
    finish();
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    setResult(RESULT_CANCELED);
  }

  @Override public void onLoad(int requestid) {

  }

  @Override public void onComplete(int requestid) {
  }

  @Override public void onError(int requestid) {
  }

  @Override public void onNext(int requestid) {

  }

  @Override public void setAdapter(ArrayList<FragmentCategoryModel> fragments) {
    adapterCategoryFragmentPager =
        new AdapterCategoryFragmentPager(getSupportFragmentManager(), this, fragments);
    pager.setAdapter(adapterCategoryFragmentPager);
    tablayout.setupWithViewPager(pager);
  }

  @Override public void onAddCategory() {
    runOnUiThread(() -> {
      init();
    });
  }

  @Override public void finishRenameCategory() {
    init();
  }

  @Override public void errorRenameCategory() {

  }

  @Override protected void onDestroy() {
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
