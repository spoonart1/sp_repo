package javasign.com.dompetsehat.presenter.category;

import java.util.ArrayList;

import javasign.com.dompetsehat.ui.CommonInterface;
import javasign.com.dompetsehat.ui.activities.category.pojo.FragmentCategoryModel;

/**
 * Created by aves on 8/18/16.
 */

public interface CategoryInterface extends CommonInterface {
    void setAdapter(ArrayList<FragmentCategoryModel> fragments);
    void onAddCategory();
    void finishRenameCategory();
    void errorRenameCategory();
}
