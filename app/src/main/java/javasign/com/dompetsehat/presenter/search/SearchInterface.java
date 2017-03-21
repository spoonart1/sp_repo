package javasign.com.dompetsehat.presenter.search;

import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import javasign.com.dompetsehat.base.MvpView;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Category;

/**
 * Created by avesina on 9/27/16.
 */

public interface SearchInterface extends MvpView {
  void setAdapter(int flagFrom,RecyclerView.Adapter adapter);
  void setSeachMore(ArrayList<Account>accounts,String[] labelAccounts,String[] labelTypes,String[] labelTags);
  void setSeachCategory(ArrayList<Category>categories,String[] labelCategories);
  void setSeachTags(String[] labelTags);
}
