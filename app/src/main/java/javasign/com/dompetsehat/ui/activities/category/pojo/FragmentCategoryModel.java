package javasign.com.dompetsehat.ui.activities.category.pojo;

import android.support.v4.app.Fragment;
import java.util.List;
import javasign.com.dompetsehat.models.Category;

/**
 * Created by bastianbentra on 8/12/16.
 */
public class FragmentCategoryModel {
  public static final int EXPENSE_CATEGORY = 0;
  public static final int INCOME_CATEGORY = 1;
  public String title;
  public int type;
  public Fragment fragment;
  public List<Category> categoryList;

  public FragmentCategoryModel(String title, Fragment fragment){
    this.title = title;
    this.fragment = fragment;
  }
}
