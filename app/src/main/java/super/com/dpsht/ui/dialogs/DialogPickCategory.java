package javasign.com.dompetsehat.ui.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.presenter.category.CategoryInterface;
import javasign.com.dompetsehat.presenter.category.CategoryPresenter;
import javasign.com.dompetsehat.ui.activities.category.CategoryActivity;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategory;
import javasign.com.dompetsehat.ui.activities.category.adapter.AdapterCategoryFragmentPager;
import javasign.com.dompetsehat.ui.activities.category.listfragment.ListCategoryFragment;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;

/**
 * Created by lafran on 1/31/17.
 */

public class DialogPickCategory extends DialogFragment implements CategoryInterface {
  @Bind(R.id.pager) ViewPager pager;
  @Bind(R.id.tablayout) TabLayout tablayout;
  @Bind(R.id.ic_back) IconicsTextView ic_back;
  @Inject CategoryPresenter presenter;
  private AdapterCategory.OnCategoryClick onCategoryClick;
  private AdapterCategoryFragmentPager adapterCategoryFragmentPager;
  final int REQUEST_CATEGORY = 1;
  String type = "All";
  String active_type = "DB";
  ProgressDialog dialog;

  public static DialogPickCategory newInstance(String typeCategory, String active_type) {
    DialogPickCategory dialogPickCategory = new DialogPickCategory();
    dialogPickCategory.type = typeCategory;
    dialogPickCategory.active_type = active_type;
    return dialogPickCategory;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_category, null);
    ButterKnife.bind(this, view);
    getDialog().setTitle(getString(R.string.select_category));
    ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    presenter.attachView(this);
    dialog = new ProgressDialog(getActivity());
    init();
    return view;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  public void init() {
    ic_back.setVisibility(View.GONE);
    if(type.equalsIgnoreCase("TF")){
      type = "All";
    }
    presenter.getCategegoryList(REQUEST_CATEGORY, active_type, type, false,false,onCategoryClick);
  }

  public void setOnCategoryClick(AdapterCategory.OnCategoryClick onCategoryClick) {
    this.onCategoryClick = onCategoryClick;
  }

  @Override public void setAdapter(ArrayList<FragmentCategoryModel> fragments) {
    getActivity().runOnUiThread(() -> {
      adapterCategoryFragmentPager = new AdapterCategoryFragmentPager(this.getChildFragmentManager(), getActivity(),fragments);
      pager.setAdapter(adapterCategoryFragmentPager);
      tablayout.setupWithViewPager(pager);
    });
  }

  @Override public void onAddCategory() {

  }

  @Override public void finishRenameCategory() {
    dialog.dismiss();

  }

  @Override public void errorRenameCategory() {
    dialog.dismiss();

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
