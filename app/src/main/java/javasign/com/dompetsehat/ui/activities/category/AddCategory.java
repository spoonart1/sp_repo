package javasign.com.dompetsehat.ui.activities.category;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.callback.SimpleObserver;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.presenter.category.CategoryInterface;
import javasign.com.dompetsehat.presenter.category.CategoryPresenter;
import javasign.com.dompetsehat.ui.activities.category.listfragment.ListCategoryFragment;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;
import javasign.com.dompetsehat.ui.event.ChangeCategoryEvent;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;

/**
 * Created by bastianbentra on 8/12/16.
 */
public class AddCategory extends BaseActivity implements CategoryInterface {

  @Bind(R.id.ic_menu) IconicsTextView ic_menu;
  @Bind(R.id.et_category_name) EditText et_category_name;
  @Bind(R.id.et_parent) TextView et_parent;
  @Bind(R.id.fl_content) FrameLayout fl_content;
  @Bind(R.id.ic_arrow) ImageView ic_arrow;
  @Bind(R.id.splash_view) View splash_view;
  @Bind(R.id.icr_category) IconCategoryRounded icr_category;

  private int categoryType = ListCategoryFragment.EXPENSE_CATEGORY;
  private boolean isPerformPickCategory = false;

  private RxBus rxBus = MyCustomApplication.getRxBus();
  private DbHelper db;
  private SessionManager session;
  private Category selectedCategory;
  @Inject CategoryPresenter presenter;
  ProgressDialog dialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_category);
    ButterKnife.bind(this);

    db = DbHelper.getInstance(this);
    session = new SessionManager(this);

    getActivityComponent().inject(this);
    presenter.attachView(this);
    dialog = new ProgressDialog(this);
    init();
  }

  @OnClick(R.id.ic_menu) void doSave(View v){

    save();
    Helper.trackThis(this, "User telah berhasil menambahkan kategori baru");
  }

  @OnClick(R.id.ll_category) void pickParentCategory(View v){
    attachListCategoryFragment();
  }

  @OnClick(R.id.splash_view) void dispose(View v){
    detachListCategoryFragment();
  }

  @OnClick(R.id.ic_back) void onBack(View v){
    finish();
  }

  @Override public void finish() {
    if(!isPerformPickCategory)
      super.finish();

    detachListCategoryFragment();
  }

  private void init() {
    Words.setButtonToListen(ic_menu,et_category_name, et_parent);
    ic_arrow.setColorFilter(getResources().getColor(R.color.grey_400));
    categoryType = getIntent().getIntExtra("category-type", ListCategoryFragment.EXPENSE_CATEGORY);

    //TODO: jangan pakai AddCategoryPresenter ya, krn sudah ada FLAG 'dontFinishActivity'
    rxBus.toObserverable().ofType(ChangeCategoryEvent.class).subscribe(new SimpleObserver<ChangeCategoryEvent>() {
      @Override public void onNext(ChangeCategoryEvent changeCategoryEvent) {
        setIconCategory(changeCategoryEvent.category.getName(), changeCategoryEvent.category);
        selectedCategory = changeCategoryEvent.category;
        detachListCategoryFragment();
      }
    });
  }

  private void attachListCategoryFragment(){
    if(isPerformPickCategory)
      return;

    splash_view.setVisibility(View.VISIBLE);
    fl_content.setVisibility(View.VISIBLE);
    ic_arrow.setVisibility(View.VISIBLE);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    ListCategoryFragment fragment = categoryType == ListCategoryFragment.EXPENSE_CATEGORY ?
        getCategoryExpense() : getCategoryIncome();
    fragment.setDontFinishActivity();
    transaction.replace(R.id.fl_content, fragment, "list-category")
        .commit();
    isPerformPickCategory = true;
  }

  private void detachListCategoryFragment(){
    if (!isPerformPickCategory)
      return;

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    ListCategoryFragment fragment =
        (ListCategoryFragment) getSupportFragmentManager().findFragmentByTag("list-category");
    transaction.remove(fragment).commit();
    splash_view.setVisibility(View.GONE);
    fl_content.setVisibility(View.GONE);
    ic_arrow.setVisibility(View.GONE);
    isPerformPickCategory = false;
  }

  public ListCategoryFragment getCategoryIncome() {
    List<Category> mostUsedCategoryIncome = db.getMostCategory(session.getIdUser(), "CR");
    List<Category> categoriesIncome = db.getAllCategoryByType(session.getIdUser(), "CR");
    return ListCategoryFragment.newInstance(mostUsedCategoryIncome, categoriesIncome,new HashMap<>(),
        ListCategoryFragment.INCOME_CATEGORY);
  }

  public ListCategoryFragment getCategoryExpense() {
    List<Category> mostUsedCategoryExpense = db.getMostCategory(session.getIdUser(), "DB");
    List<Category> categoriesExpense = db.getAllCategoryByType(session.getIdUser(), "DB");
    return ListCategoryFragment.newInstance(mostUsedCategoryExpense, categoriesExpense,new HashMap<>(),
            ListCategoryFragment.EXPENSE_CATEGORY);
  }

  private void setIconCategory(String label, Category category){
    icr_category.setIconCode(category.getIcon());
    icr_category.setBackgroundColorIcon(Color.parseColor(category.getColor()));
    et_parent.setText(label);
  }

  private void save() {
    if(selectedCategory != null){
      presenter.saveSubCategory(
          selectedCategory.getId_category(),
          selectedCategory.getColor(),
          selectedCategory.getColor(),
          et_category_name.getText().toString()
      );

    }
  }

  @Override public void setAdapter(ArrayList<FragmentCategoryModel> fragments) {

  }

  @Override public void onAddCategory() {

  }

  @Override public void finishRenameCategory() {

  }

  @Override public void errorRenameCategory() {

  }

  @Override public void onLoad(int RequestID) {
    dialog.setMessage("Menyimpan.....");
    dialog.show();
  }

  @Override public void onComplete(int RequestID) {
    dialog.dismiss();
    finish();
  }

  @Override public void onError(int RequestID) {
    dialog.dismiss();

  }

  @Override public void onNext(int RequestID) {
    dialog.dismiss();
  }

  @Override protected void onDestroy() {
    if(presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }
}
