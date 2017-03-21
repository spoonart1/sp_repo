package javasign.com.dompetsehat.ui.activities.category.listfragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsButton;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;
import javasign.com.dompetsehat.presenter.category.CategoryInterface;
import javasign.com.dompetsehat.presenter.category.CategoryPresenter;
import javasign.com.dompetsehat.ui.activities.category.AddCategory;
import javasign.com.dompetsehat.ui.activities.category.CategoryActivity;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategory;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;
import javasign.com.dompetsehat.ui.event.ChangeCategoryEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.Words;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/12/16.
 */
public class ListCategoryFragment extends Fragment
    implements AdapterCategory.OnCategoryClick, CategoryInterface {

  public static final int EXPENSE_CATEGORY = 0;
  public static final int INCOME_CATEGORY = 1;
  public static final int TRANSFER_CATEGORY = 2;

  @Bind(R.id.et_search_category) EditText et_search_category;
  @Bind(R.id.ic_search) IconicsTextView ic_search;
  @Bind(R.id.recycleview) RecyclerView recycleview;
  @Bind(R.id.add) IconicsButton add;

  private AdapterCategory adapter;
  private List<Category> mostUsedCategory;
  private List<Category> categories;
  private HashMap<Integer, List<UserCategory>> user_categories;
  private int categoryType = EXPENSE_CATEGORY;
  private boolean dontFinishActivity = false;
  private boolean hideAddButton = false;
  private AdapterCategory.OnCategoryClick onCategoryClick;

  @Inject RxBus rxBus;
  @Inject CategoryPresenter presenter;

  public static ListCategoryFragment newInstance(List<Category> mostUsedCategory,
      List<Category> categories, HashMap<Integer, List<UserCategory>> user_categories,
      int categoryType) {
    Bundle args = new Bundle();
    ListCategoryFragment fragment = new ListCategoryFragment();
    fragment.setArguments(args);
    fragment.setData(mostUsedCategory, categories, user_categories, categoryType);
    return fragment;
  }

  public ListCategoryFragment setDontFinishActivity() {
    this.dontFinishActivity = true;
    return this;
  }

  public ListCategoryFragment setOnCategoryClick(AdapterCategory.OnCategoryClick onCategoryClick) {
    this.onCategoryClick = onCategoryClick;
    return this;
  }

  private void setData(List<Category> mostUsedCategory, List<Category> categories,
      HashMap<Integer, List<UserCategory>> user_categories, int categoryType) {
    this.mostUsedCategory = mostUsedCategory;
    this.categories = categories;
    this.categoryType = categoryType;
    this.user_categories = user_categories;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_list_category, null);
    ButterKnife.bind(this, view);
    initData();
    fieldListener();

    ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    rxBus = MyCustomApplication.getRxBus();
    presenter.attachView(this);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (hideAddButton) {
      hideFloatingButton();
    }
  }

  @OnClick(R.id.add) void addCategory() {
    startActivity(
        new Intent(getActivity(), AddCategory.class).putExtra("category-type", categoryType));
  }

  private void initData() {
    if (categoryType == TRANSFER_CATEGORY) {
      add.setVisibility(View.GONE);
    } else {
      add.setVisibility(View.VISIBLE);
    }
    final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    recycleview.setLayoutManager(layoutManager);
    if (onCategoryClick == null) {
      onCategoryClick = this;
    }
    adapter = new AdapterCategory(getContext(), mostUsedCategory, categories, user_categories,
        onCategoryClick, categoryType);
    recycleview.setAdapter(adapter);
  }

  private void fieldListener() {
    Words.setButtonToListen(ic_search, et_search_category);
    et_search_category.addTextChangedListener(new Words.SimpleTextWatcer() {
      @Override public void afterTextChanged(Editable s) {
        ListCategoryFragment.this.adapter.getFilter().filter(s);
      }
    });
  }

  @Override public void onClickParent(Category category, int section) {
    rxBus.send(new ChangeCategoryEvent(category).setDontFinishActivity(dontFinishActivity));

    if (!dontFinishActivity) {
      getActivity().setResult(getActivity().RESULT_OK);
      getActivity().finish();
      return;
    }

    Helper.trackThis(getActivity(), "User memilih kategori yang pernah dibuat sendiri");
  }

  @Override public void onClickChild(UserCategory userCategory, int position) {
    rxBus.send(new ChangeCategoryEvent(userCategory).setDontFinishActivity(dontFinishActivity));

    if (!dontFinishActivity) {
      getActivity().finish();
      return;
    }

    Helper.trackThis(getActivity(), "User memilih kategori yang pernah dibuat sendiri");
  }

  @Override public void onLongClickParent(Category category, int section) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_category, null);
    final EditText et_cat_name = ButterKnife.findById(view, R.id.et_cat_name);
    final EditText et_cat_desc = ButterKnife.findById(view, R.id.et_cat_desc);
    et_cat_name.setText(category.getName());
    et_cat_desc.setText(category.getDescription());
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle("Edit description")
        .setView(view)
        .setPositiveButton(getActivity().getString(R.string.save), (dialogInterface, i) -> {
          categories.get(section).setName(et_cat_name.getText().toString());
          categories.get(section).setDescription(et_cat_desc.getText().toString());
          presenter.changeCategory(category.getId_category(), et_cat_name.getText().toString(),et_cat_desc.getText().toString());
        })
        .setNegativeButton(getActivity().getString(R.string.cancel),
            (dialogInterface, i) -> dialogInterface.dismiss())
        .create();
    alertDialog.show();
  }

  public void hideFloatingButton() {
    if (add != null) {
      hideAddButton = true;
      add.setVisibility(View.GONE);
    } else {
      hideAddButton = true;
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void setAdapter(ArrayList<FragmentCategoryModel> fragments) {

  }

  @Override public void onAddCategory() {

  }

  @Override public void finishRenameCategory() {
    initData();
  }

  @Override public void errorRenameCategory() {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }
}
