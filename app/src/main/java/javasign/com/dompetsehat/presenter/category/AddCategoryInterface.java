package javasign.com.dompetsehat.presenter.category;

import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;

/**
 * Created by aves on 9/1/16.
 */

public interface AddCategoryInterface extends MvpView {
  void setCategory(Category category);

  void setUserCategory(UserCategory userCategory);

  void setCashflow(Cash cashflow);

  void setIconCategory(String textLabel, Category category);
}
